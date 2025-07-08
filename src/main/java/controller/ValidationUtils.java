package controller; // パッケージは適宜変更してください

import java.util.regex.Pattern;

public class ValidationUtils {

    // 検証用の正規表現パターン (変更なし)
    private static final Pattern HYPHEN_GENERAL =
            Pattern.compile("^0[1-9]\\d{0,3}-\\d{1,4}-\\d{4}$");

    private static final Pattern HYPHEN_SPECIAL =
            Pattern.compile("^(0120|0800|0570|0990)-\\d{3}-\\d{3}$");

    /**
     * 市外局番辞書を用いて電話番号を検証し、統一された形式にフォーマットします。
     * (これが外部から呼び出されるメインのメソッドになります)
     * @param input ユーザーが入力した電話番号文字列
     * @return 検証・フォーマット済みの電話番号文字列 (ハイフン区切り)
     * @throws IllegalArgumentException バリデーションに失敗した場合
     */
    public static String validateAndFormatWithDictionary(String input) throws IllegalArgumentException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("電話番号が入力されていません。");
        }

        // --- 1. 前処理：入力の正規化 ---
        String processed = input.trim();
        processed = processed.replace("(", "").replace(")", "").replace("-", ""); // 全てのセパレータを除去

        if (processed.matches(".*[^\\d].*")) { // 不正な文字（数字以外）がないかチェック
            throw new IllegalArgumentException("電話番号には数字以外の文字は使用できません。");
        }

        if (!processed.startsWith("0")) { // 先頭が '0' かどうかのチェック
            throw new IllegalArgumentException("電話番号は0から始まる必要があります。");
        }
        
        // --- 2. 辞書と連携したフォーマット処理 ---
        String finalNumber = formatPhoneNumberWithDictionary(processed);

        // --- 3. 最終フォーマット検証 ---
        boolean isValid = HYPHEN_GENERAL.matcher(finalNumber).matches() ||
                          HYPHEN_SPECIAL.matcher(finalNumber).matches();

        if (isValid) {
            return finalNumber; // 全ての検証とフォーマットをクリアした電話番号を返す
        } else {
            // フォーマット後もパターンに一致しない場合 (例: 桁数が辞書と合わない)
            throw new IllegalArgumentException("電話番号の形式（市外局番または桁数）が正しくありません。");
        }
    }

    /**
     * ★★★ ここが新しい精緻化されたフォーマットメソッド ★★★
     * 数字のみの電話番号文字列を、市外局番辞書に基づいてハイフン区切り形式にフォーマットします。
     * @param digitsOnly 数字のみの電話番号文字列
     * @return フォーマットされた電話番号文字列。適切な形式が見つからなければ元の数字列。
     */
    private static String formatPhoneNumberWithDictionary(String digitsOnly) {
        // 市外局番を特定する
        String areaCodeKey = null;
        // 5桁の市外局番(例: 01234)から2桁(例: 03)まで、長いものから順に試す
        for (int i = 5; i >= 2; i--) {
            if (digitsOnly.length() >= i) {
                String potentialPrefix = digitsOnly.substring(1, i); // 先頭の0を除いた部分
                Integer expectedTotalDigits = AreaCodeDictionary.getTotalDigitsFor(potentialPrefix);

                if (expectedTotalDigits != null && expectedTotalDigits == digitsOnly.length()) {
                    // 辞書に市外局番が見つかり、かつ全体の桁数も一致した場合
                    areaCodeKey = potentialPrefix;
                    break; // 最も長い一致が見つかったのでループを抜ける
                }
            }
        }

        if (areaCodeKey == null) {
            // 辞書に有効な市外局番が見つからなかった場合
            // この番号は後の最終検証でエラーになる可能性が高いが、ここでは一旦そのまま返す
            return digitsOnly;
        }

        // 辞書で見つかった市外局番に基づいてフォーマットを決定
        if (areaCodeKey.equals("120") || areaCodeKey.equals("800") || areaCodeKey.equals("570") || areaCodeKey.equals("990")) {
            // フリーダイヤル等の特殊なフォーマット (XXXX-XXX-XXX)
            return String.format("%s-%s-%s",
                digitsOnly.substring(0, 4), digitsOnly.substring(4, 7), digitsOnly.substring(7, 10));
        } else {
            // 一般的な固定電話・携帯電話のフォーマット
            int areaCodeLength = areaCodeKey.length() + 1; // 先頭の0を含む市外局番の長さ
            int lastPartLength = 4; // 加入者番号は通常4桁

            String part1 = digitsOnly.substring(0, areaCodeLength);
            String part2 = digitsOnly.substring(areaCodeLength, digitsOnly.length() - lastPartLength);
            String part3 = digitsOnly.substring(digitsOnly.length() - lastPartLength);

            return String.format("%s-%s-%s", part1, part2, part3);
        }
    }
}





