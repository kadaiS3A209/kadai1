package controller; // パッケージは適宜変更してください

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AreaCodeDictionary {

    // 市外局番(先頭の0は除く)と、電話番号全体の桁数(先頭の0を含む)をマッピングする
    private static final Map<String, Integer> AREA_CODE_MAP;

    // static初期化ブロック: このクラスが最初に読み込まれる時に一度だけ実行される
    static {
        Map<String, Integer> map = new HashMap<>();
        String csvFileName = "shigaikyokuban_list.csv";

        // クラスパスからリソースとしてCSVファイルを読み込む
        try (InputStream is = AreaCodeDictionary.class.getClassLoader().getResourceAsStream(csvFileName);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 空行やコメント行（もしあれば）をスキップ
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] columns = line.split(","); // CSVファイルをカンマで分割
                if (columns.length >= 2) {
                    String areaCode = columns[0].trim();
                    try {
                        int totalDigits = Integer.parseInt(columns[1].trim());
                        map.put(areaCode, totalDigits);
                    } catch (NumberFormatException e) {
                        System.err.println("市外局番辞書CSVの読み込みエラー: 数値への変換に失敗しました。行: " + line);
                    }
                }
            }
            System.out.println("市外局番辞書が正常に読み込まれました。件数: " + map.size());

        } catch (IOException | NullPointerException e) {
            e.printStackTrace(); // ファイル読み込みエラーのログを出力
            // ファイルが読み込めない場合は、アプリケーションの起動を停止させることも検討
            throw new RuntimeException("市外局番辞書ファイル (" + csvFileName + ") の読み込みに失敗しました。", e);
        }

        // 変更不可能なMapとして初期化
        AREA_CODE_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * 市外局番(先頭の0を除く)をキーとして、電話番号全体の桁数を取得します。
     * @param areaCode 先頭の0を除いた市外局番 (例: "3", "90", "422")
     * @return 電話番号全体の桁数。辞書にない場合はnull。
     */
    public static Integer getTotalDigitsFor(String areaCode) {
        return AREA_CODE_MAP.get(areaCode);
    }
}
