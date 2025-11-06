package com.zts.delivery.global.persistence.converter;

import com.zts.delivery.global.persistence.Price;
import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.menu.domain.ItemOption;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OptionConverter implements AttributeConverter<ItemOption, String> {

    private static final String SPLIT_CHAR = "||";

    private static final String REGEX_SPLIT_CHAR = "\\|\\|";

    @Override
    public String convertToDatabaseColumn(ItemOption attribute) {
        if (attribute == null || attribute.getName() == null || attribute.getPrice() == null) {
            return null;
        }

        return String.format("%s%s%d",
            attribute.getName(),
            SPLIT_CHAR,
            attribute.getPrice().getValue()
        );
    }

    @Override
    public ItemOption convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        final String[] parts = dbData.split(REGEX_SPLIT_CHAR);

        if (parts.length != 2) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST);
        }

        String name = parts[0];

        int priceValue;
        try {
            priceValue = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST);
        }

        return new ItemOption(name, new Price(priceValue));
    }
}
