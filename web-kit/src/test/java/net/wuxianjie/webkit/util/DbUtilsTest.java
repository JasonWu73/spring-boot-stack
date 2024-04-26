package net.wuxianjie.webkit.util;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

class DbUtilsTest {

    @Test
    void toLike_replaceSpacesWithPercentage() {
        var value = "  KeyOne    KeyTwo  ";
        var expected = "%KeyOne%KeyTwo%";
        var actual = DbUtils.toLike(value);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toLike_addsPercentageAtTheBeginningAndEnd() {
        var value = "KeyOne";
        var expected = "%KeyOne%";
        var actual = DbUtils.toLike(value);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toLike_returnsNull_whenValueIsEmpty() {
        var value = "";
        var actual = DbUtils.toLike(value);
        Assertions.assertThat(actual).isNull();
    }

    @Test
    void toLike_returnsNull_whenValueIsBlank() {
        var value = " ";
        var actual = DbUtils.toLike(value);
        Assertions.assertThat(actual).isNull();
    }

    @Test
    void toLike_returnsNull_whenValueIsNull() {
        var actual = DbUtils.toLike(null);
        Assertions.assertThat(actual).isNull();
    }

}