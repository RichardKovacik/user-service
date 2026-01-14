package sk.mvp.user_service.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, Character> {

    @Override
    public Character convertToDatabaseColumn(Gender gender) {
        if (gender == null) return null;
        return gender.getCode(); // vráti 'M' alebo 'F'
    }

    @Override
    public Gender convertToEntityAttribute(Character dbData) {
        if (dbData == null) return null;
        return Gender.getValidGenderFromCode(dbData); // throwws excpetion if it is invalid
    }
}
