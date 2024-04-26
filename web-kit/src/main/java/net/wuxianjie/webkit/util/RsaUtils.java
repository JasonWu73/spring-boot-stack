package net.wuxianjie.webkit.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加解密工具类。
 */
public class RsaUtils {

    /**
     * 虽然 1024 位的密钥长度被认为是相对安全的，但更推荐 2048 或 4096 位。
     */
    private static final int RSA_KEY_LENGTH = 2048;

    private static final String RSA_CRYPTO_ALGORITHM = "RSA";

    /**
     * 生成新的 RSA 2048 位密钥对，密钥对使用 Base64 编码。
     *
     * @return Base64 编码的密钥对
     */
    public static KeyPair generateKeyPair() {
        var gen = getKeyPairGenerator();
        gen.initialize(RSA_KEY_LENGTH);
        var keys = gen.generateKeyPair();
        return new KeyPair(
                Base64.getEncoder().encodeToString(keys.getPublic().getEncoded()),
                Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded())
        );
    }

    /**
     * 使用公钥加密字符串。
     *
     * @param raw 需要加密的原始字符串
     * @param publicKey Base64 公钥字符串
     * @return Base64 编码的密文
     */
    public static String encrypt(String raw, String publicKey) {
        var encryptCipher = getEncryptCipher(publicKey);
        try {
            var encrypted = encryptCipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("公钥加密失败", e);
        }
    }

    /**
     * 使用私钥解密密文。
     *
     * @param encrypted 需要解密的密文
     * @param privateKey Base64 私钥字符串
     * @return 原始字符串
     */
    public static String decrypt(String encrypted, String privateKey) {
        var decryptCipher = getDecryptCipher(privateKey);
        try {
            var decrypted = decryptCipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("私钥解密失败", e);
        }
    }

    /**
     * 从 Base64 公钥字符串中解析出公钥。
     *
     * @param publicKey Base64 公钥字符串
     * @return 公钥
     */
    public static PublicKey getPublicKey(String publicKey) {
        var keyBytes = Base64.getDecoder().decode(publicKey);
        var keySpec = new X509EncodedKeySpec(keyBytes);
        var factory = getKeyFactory();
        try {
            return factory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("公钥错误", e);
        }
    }

    /**
     * 从 Base64 私钥字符串中解析出私钥。
     *
     * @param privateKey Base64 私钥字符串
     * @return 私钥
     */
    public static PrivateKey getPrivateKey(String privateKey) {
        var keyBytes = Base64.getDecoder().decode(privateKey);
        var keySpec = new PKCS8EncodedKeySpec(keyBytes);
        var factory = getKeyFactory();
        try {
            return factory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("私钥错误", e);
        }
    }

    private static KeyPairGenerator getKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance(RSA_CRYPTO_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码算法不支持", e);
        }
    }

    private static Cipher getCipher() {
        try {
            return Cipher.getInstance(RSA_CRYPTO_ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("密码算法不支持", e);
        }
    }

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance(RSA_CRYPTO_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码算法不支持", e);
        }
    }

    private static Cipher getEncryptCipher(String publicKey) {
        var cipher = getCipher();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return cipher;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("公钥错误", e);
        }
    }

    private static Cipher getDecryptCipher(String privateKey) {
        var cipher = getCipher();
        try {
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
            return cipher;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("私钥错误", e);
        }
    }

    /**
     * RSA 密钥对。
     *
     * @param publicKey Base64 公钥字符串
     * @param privateKey Base64 私钥字符串
     */
    public record KeyPair(String publicKey, String privateKey) {
    }

}
