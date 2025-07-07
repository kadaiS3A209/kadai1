package controller; // または utils など、適切なパッケージに作成

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import model.DiseaseBean;
import model.LabTestBean;

@WebListener
public class MasterDataManager implements ServletContextListener {

    private static List<DiseaseBean> diseaseMaster = new ArrayList<>();
    private static List<LabTestBean> labTestMaster = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("マスタデータの読み込みを開始します...");

        // ★修正: Excelファイルから直接読み込むように変更
        diseaseMaster = loadDiseasesFromXlsx("kihon2013.xlsx");
        System.out.println("疾病マスタの読み込み完了。件数: " + diseaseMaster.size());

        labTestMaster = loadLabTestsFromXlsx("17jlac11_3.xlsx");
        System.out.println("臨床検査項目マスタの読み込み完了。件数: " + labTestMaster.size());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        diseaseMaster.clear();
        labTestMaster.clear();
        System.out.println("マスタデータを解放しました。");
    }

    // --- 公開メソッド (変更なし) ---
    public static List<DiseaseBean> getAllDiseases() { return diseaseMaster; }
    public static List<LabTestBean> getAllLabTests() { return labTestMaster; }

    // --- Excel読み込み用のヘルパーメソッド ---
    
    /**
     * 疾病マスタのExcelファイルを読み込む
     * @param fileName resourcesフォルダ内のファイル名
     * @return 疾病Beanのリスト
     */
    private List<DiseaseBean> loadDiseasesFromXlsx(String fileName) {
        List<DiseaseBean> list = new ArrayList<>();
        DataFormatter formatter = new DataFormatter(); // セルの値を文字列として取得するフォーマッター

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = WorkbookFactory.create(is)) {
            
            // ★"基本分類表データ" という名前のシートを取得
            Sheet sheet = workbook.getSheet("基本分類表データ");
            
            int skipLines = 5; // ヘッダーが5行目からなので、最初の4行をスキップ
            int rowNum = 0;

            for (Row row : sheet) {
                if (rowNum++ < skipLines) {
                    continue; // ヘッダー行をスキップ
                }

                // セルを取得 (A列=0, B列=1)
                Cell codeCell = row.getCell(7);
                Cell nameCell = row.getCell(9);

                if (codeCell != null && nameCell != null) {
                    String code = formatter.formatCellValue(codeCell).trim();
                    String name = formatter.formatCellValue(nameCell).trim();

                    if (!code.isEmpty() && !name.isEmpty()) {
                        list.add(new DiseaseBean(code, name));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Excelファイル '" + fileName + "' の読み込みに失敗しました。");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * ★修正: 臨床検査マスタのExcelファイルから指定の列を読み込む
     * @param fileName resourcesフォルダ内のファイル名
     * @return 検査Beanのリスト
     */
    private List<LabTestBean> loadLabTestsFromXlsx(String fileName) {
        List<LabTestBean> list = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheet("検査試薬とJLAC10_ 11");
            
            int skipLines = 17;
            int rowNum = 0;

            for (Row row : sheet) {
                if (rowNum++ < skipLines) {
                    continue; // ヘッダー行をスキップ
                }
                
                // ★修正(1): 読み込む列のインデックスに U列 と V列 を追加
                // A列  -> 0
                // U列  -> 20
                // V列  -> 21
                // BC列 -> 54
                // BD列 -> 55
                Cell salesNameCell    = row.getCell(0);
                Cell measurementCodeCell     = row.getCell(20); // U列: 基準値
                Cell measurementCell         = row.getCell(21); // V列: 単位
                Cell jlacTestNameCell = row.getCell(54);
                Cell jlac11CodeCell   = row.getCell(55);

                // 主要なコードと名称が存在する場合のみ処理
                if (jlac11CodeCell != null && jlacTestNameCell != null) {
                    String code = formatter.formatCellValue(jlac11CodeCell).trim();
                    String testName = formatter.formatCellValue(jlacTestNameCell).trim();
                    
                    if (!code.isEmpty() && !testName.isEmpty()) {
                        // 他の列はnullの可能性があるため、nullチェックを行う
                        String salesName = (salesNameCell != null) ? formatter.formatCellValue(salesNameCell).trim() : "";
                        String measurementCode  = (measurementCodeCell != null) ? formatter.formatCellValue(measurementCodeCell).trim() : "";
                        String measurement      = (measurementCell != null) ? formatter.formatCellValue(measurementCell).trim() : "";

                        // ★修正(2): 新しいLabTestBeanのコンストラクタに合わせて5つの値を渡す
                        list.add(new LabTestBean(code, testName, salesName, measurementCode, measurement));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Excelファイル '" + fileName + "' の読み込みに失敗しました。");
            e.printStackTrace();
        }
        return list;
    }
    
    
    /**
     * ★★★ このメソッドを追加します ★★★
     * 検査コードを指定して、対応する検査マスタ情報を取得します。
     * @param code 検索するJLAC11コード
     * @return 条件に一致するLabTestBean。見つからない場合はnull。
     */
    public static LabTestBean findLabTestByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        // メモリ上のリストをループして、コードが一致するものを探す
        for (LabTestBean test : labTestMaster) {
            if (code.equals(test.getJlac11Code())) {
                return test;
            }
        }
        return null; // 見つからなかった場合
    }

}


