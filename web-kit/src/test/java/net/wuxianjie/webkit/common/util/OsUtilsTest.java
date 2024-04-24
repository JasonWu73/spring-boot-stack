package net.wuxianjie.webkit.common.util;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

class OsUtilsTest {

    @Test
    void isWindows_returnsTrue_whenOSNameContainsWin() {
        System.setProperty("os.name", "Windows 10");
        Assertions.assertThat(OsUtils.isWindows()).isTrue();
    }

    @Test
    void isWindows_returnsFalse_whenOSNameDoesNotContainWin() {
        System.setProperty("os.name", "macOS");
        Assertions.assertThat(OsUtils.isWindows()).isFalse();
    }

    @Test
    void isMac_returnsTrue_whenOSNameContainsMac() {
        System.setProperty("os.name", "macOS");
        Assertions.assertThat(OsUtils.isMac()).isTrue();
    }

    @Test
    void isMac_returnsFalse_whenOSNameDoesNotContainMac() {
        System.setProperty("os.name", "Windows 10");
        Assertions.assertThat(OsUtils.isMac()).isFalse();
    }

    @Test
    void isLinux_returnsTrue_whenOSNameContainsLinux() {
        System.setProperty("os.name", "Linux");
        Assertions.assertThat(OsUtils.isLinux()).isTrue();
    }

    @Test
    void isLinux_returnsFalse_whenOSNameDoesNotContainLinux() {
        System.setProperty("os.name", "Windows 10");
        Assertions.assertThat(OsUtils.isLinux()).isFalse();
    }

    @Test
    void getOsName_returnsCorrectOSName() {
        System.setProperty("os.name", "Windows 10");
        Assertions.assertThat(OsUtils.getOsName()).isEqualTo("Windows");

        System.setProperty("os.name", "macOS");
        Assertions.assertThat(OsUtils.getOsName()).isEqualTo("macOS");

        System.setProperty("os.name", "Linux");
        Assertions.assertThat(OsUtils.getOsName()).isEqualTo("Linux");

        System.setProperty("os.name", "Other");
        Assertions.assertThat(OsUtils.getOsName()).isEqualTo("Other");
    }

}