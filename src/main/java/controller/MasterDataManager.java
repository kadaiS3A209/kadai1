package controller; // または utils など、適切なパッケージに作成

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

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

        labTestMaster = loadLabTestsFromXlsx("131jlac10_1.xlsx");
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
                Cell codeCell = row.getCell(0);
                Cell nameCell = row.getCell(1);

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
     * 臨床検査マスタのExcelファイルを読み込む
     * @param fileName resourcesフォルダ内のファイル名
     * @return 検査Beanのリスト
     */
    private List<LabTestBean> loadLabTestsFromXlsx(String fileName) {
        List<LabTestBean> list = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = WorkbookFactory.create(is)) {
            
            // ★"識別コード " という名前のシートを取得 (末尾にスペースがある可能性に注意)
            Sheet sheet = workbook.getSheet("識別コード ");
            
            int skipLines = 2; // ヘッダーが2行目からなので、最初の1行をスキップ
            int rowNum = 0;

            for (Row row : sheet) {
                if (rowNum++ < skipLines) {
                    continue; // ヘッダー行をスキップ
                }
                
                // セルを取得 (A列=0, C列=2)
                Cell codeCell = row.getCell(0);
                Cell nameCell = row.getCell(2);

                if (codeCell != null && nameCell != null) {
                    String code = formatter.formatCellValue(codeCell).trim();
                    String name = formatter.formatCellValue(nameCell).trim();
                    
                    if (!code.isEmpty() && !name.isEmpty()) {
                        list.add(new LabTestBean(code, name));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Excelファイル '" + fileName + "' の読み込みに失敗しました。");
            e.printStackTrace();
        }
        return list;
    }
}
