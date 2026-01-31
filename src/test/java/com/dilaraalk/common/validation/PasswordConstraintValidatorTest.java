package com.dilaraalk.common.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordConstraintValidatorTest {

    private PasswordConstraintValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new PasswordConstraintValidator();
    }

    @Test
    void isValid_ValidPassword_ReturnsTrue() {
        // En az 8 karakter, 1 büyük, 1 küçük, 1 rakam, 1 özel
        assertTrue(validator.isValid("GucluSifre1!", context));
    }

    @Test
    void isValid_TooShort_ReturnsFalse() {
        setupContextMocks();
        assertFalse(validator.isValid("Kisa1!", context));
    }

    @Test
    void isValid_NoUpperCase_ReturnsFalse() {
        setupContextMocks();
        assertFalse(validator.isValid("kucukharf1!", context));
    }

    @Test
    void isValid_NoLowerCase_ReturnsFalse() {
        setupContextMocks();
        assertFalse(validator.isValid("BUYUKHARF1!", context));
    }

    @Test
    void isValid_NoDigit_ReturnsFalse() {
        setupContextMocks();
        assertFalse(validator.isValid("RakamYok!", context));
    }

    @Test
    void isValid_NoSpecialChar_ReturnsFalse() {
        setupContextMocks();
        assertFalse(validator.isValid("OzelKarakterYok1", context));
    }

    @Test
    void isValid_NullPassword_ReturnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    private void setupContextMocks() {
        // Lenient kullanıyoruz çünkü bazı testlerde çağrılmayabilir veya farklı sırada
        // olabilir
        // Ancak validator implementasyonuna göre hata durumunda kesin çağrılıyor.
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        lenient().when(builder.addConstraintViolation()).thenReturn(context);
    }
}
