package br.com.atai.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte {@code boolean} para as colunas Oracle {@code CHAR(1)} com valores 'S'/'N'.
 */
@Converter
public class BooleanSimNaoConverter implements AttributeConverter<Boolean, Character> {

    private static final char SIM = 'S';
    private static final char NAO = 'N';

    @Override
    public Character convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute ? SIM : NAO;
    }

    @Override
    public Boolean convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case SIM -> Boolean.TRUE;
            case NAO -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("Valor inválido para flag S/N: " + dbData);
        };
    }
}
