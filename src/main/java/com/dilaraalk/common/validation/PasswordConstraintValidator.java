package com.dilaraalk.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Kural setleri
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\|,.<>\\/?].*");
        boolean isLengthValid = password.length() >= 8 && password.length() <= 32;

        // Tüm kuralları kontrol et
        if (hasUpperCase && hasLowerCase && hasDigit && hasSpecial && isLengthValid) {
            return true;
        }

        // Hata mesajlarını topla
        context.disableDefaultConstraintViolation();

        StringBuilder message = new StringBuilder("Şifre şunları içermelidir:");
        if (!hasUpperCase) {
            message.append("Büyük harf içermeli. ");
        }
        if (!hasLowerCase) {
            message.append("Küçük harf içermeli. ");
        }
        if (!hasDigit) {
            message.append("Rakam içermeli. ");
        }
        if (!hasSpecial) {
            message.append("Özel karakter içermeli. ");
        }
        if (!isLengthValid) {
            message.append("En az 8, en fazla 32 karakter uzunluğunda olmalı.");
        }
        if (message.toString().endsWith(",")) {
            message.setLength(message.length() - 1);
        }

        // Hata mesajını context'e ekle
        context.buildConstraintViolationWithTemplate(message.toString())
                .addConstraintViolation();

        return false;

    }

}
