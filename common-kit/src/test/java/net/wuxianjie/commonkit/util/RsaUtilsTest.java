package net.wuxianjie.commonkit.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RsaUtilsTest {

    @Test
    void testGenerateKeyPair() {
        RsaUtils.KeyPair keyPair = RsaUtils.generateKeyPair();
        System.out.printf(
            """
                ------------------------------------------
                公钥: %s
                \s
                私钥: %s
                ------------------------------------------
                """,
            keyPair.publicKey(), keyPair.privateKey()
        );
        assertThat(keyPair.publicKey()).isBase64();
        assertThat(keyPair.privateKey()).isBase64();
    }

    @Test
    void testEncryptAndDecrypt() {
        RsaUtils.KeyPair keyPair = RsaUtils.generateKeyPair();
        String rawText = "你好，RSA 密码算法";
        String encryptedText = RsaUtils.encrypt(
            rawText, keyPair.publicKey()
        );
        String decryptedText = RsaUtils.decrypt(
            encryptedText, keyPair.privateKey()
        );
        assertThat(decryptedText).isEqualTo(rawText);
    }

}
