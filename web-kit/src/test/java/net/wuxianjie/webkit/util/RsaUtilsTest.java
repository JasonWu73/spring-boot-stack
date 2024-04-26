package net.wuxianjie.webkit.util;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

class RsaUtilsTest {

    @Test
    void generateKeyPair_returnsValidKeyPair() {
        var keyPair = RsaUtils.generateKeyPair();
        System.out.printf("""
                =========================
                公钥: %s
                
                私钥: %s
                =========================
                """, keyPair.publicKey(), keyPair.privateKey());
        Assertions.assertThat(keyPair.publicKey()).isBase64();
        Assertions.assertThat(keyPair.privateKey()).isBase64();
    }

    @Test
    void encrypt_decrypt_returnsOriginalString() {
        var keyPair = RsaUtils.generateKeyPair();
        var raw = "你好，RSA 密码算法";
        var encrypted = RsaUtils.encrypt(raw, keyPair.publicKey());
        var decrypted = RsaUtils.decrypt(encrypted, keyPair.privateKey());
        Assertions.assertThat(decrypted).isEqualTo(raw);
    }

}