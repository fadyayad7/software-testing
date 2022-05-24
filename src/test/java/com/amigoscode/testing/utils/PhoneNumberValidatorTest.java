package com.amigoscode.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator phoneNumberValidator;

    @BeforeEach
    void setUp() {
        phoneNumberValidator = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+447000000000,true",
            "+4470000000033,false",
            "+447000000003,true",
            "0047000000003,false"
    })
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        assertThat(phoneNumberValidator.test(phoneNumber)).isEqualTo(expected);
    }
}
