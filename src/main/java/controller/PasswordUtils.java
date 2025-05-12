package controller;
///import java.util.Base64; // Base64も選択肢ですが、ここでは16進数文字列を主に使います
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    // PBKDF2の推奨パラメータ (実際の環境に合わせて調整してください)
    private static final int ITERATIONS = 65536; // 反復回数 (例: 2^16)
    private static final int KEY_LENGTH = 256;   // 生成されるハッシュのビット長 (SHA-256なので256)
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTE_SIZE = 16; // ソルトのバイト長 (例: 16バイト = 128ビット)

    /**
     * ランダムなソルトを生成します。
     * @return 生成されたソルト (バイト配列)
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 指定されたパスワードとソルトを使用してパスワードハッシュを生成します。
     * @param password パスワード (char配列)
     * @param salt ソルト (バイト配列)
     * @return ハッシュ化されたパスワード (バイト配列)
     * @throws NoSuchAlgorithmException サポートされていないアルゴリズムが指定された場合
     * @throws InvalidKeySpecException 指定されたキースペックが無効な場合
     */
    public static byte[] hashPassword(final char[] password, final byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * バイト配列を16進数文字列に変換します。
     * @param bytes バイト配列
     * @return 16進数文字列
     */
    public static String toHexString(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    /**
     * 16進数文字列をバイト配列に変換します。
     * @param hexString 16進数文字列
     * @return バイト配列
     */
    public static byte[] fromHexString(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    // --- (オプション) Base64エンコード/デコードの例 ---
    // public static String toBase64String(byte[] bytes) {
    //     return Base64.getEncoder().encodeToString(bytes);
    // }
    //
    // public static byte[] fromBase64String(String base64String) {
    //     return Base64.getDecoder().decode(base64String);
    // }
}
