package net.wuxianjie.myspringbootstarter.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RsaUtilsTest {

    @Test
    void testGenerateKeyPair() {
        RsaUtils.KeyPair keyPair = RsaUtils.generateKeyPair();
        Assertions.assertThat(keyPair.publicKey()).isBase64();
        Assertions.assertThat(keyPair.privateKey()).isBase64();
    }

    @Test
    void testEncryptAndDecrypt() {
        RsaUtils.KeyPair keyPair = RsaUtils.generateKeyPair();
        String rawText = "你好，RSA 密码算法";
        String encryptedText = RsaUtils.encrypt(rawText, keyPair.publicKey());
        String decryptedText = RsaUtils.decrypt(encryptedText, keyPair.privateKey());
        Assertions.assertThat(decryptedText).isEqualTo(rawText);
    }
}
