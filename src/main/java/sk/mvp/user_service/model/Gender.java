package sk.mvp.user_service.model;

public enum Gender {
    MALE('M'),
    FEMALE('F');

    private char code;

    Gender(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }
    public static Gender getValidGenderFromCode(char code) {
        for (Gender gender : Gender.values()) {
            if (gender.getCode() == Character.toUpperCase(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid gender string %c. Are supported only: M or F characters", code));

    }
}
