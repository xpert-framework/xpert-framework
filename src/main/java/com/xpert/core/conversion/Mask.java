package com.xpert.core.conversion;

public class Mask {

    public static final int CPF_LENGTH = 11;
    public static final int CNPJ_LENGTH = 14;

    public static String mask(double valor, String mask) {
        return mask(Double.toString(valor), mask);
    }

    public static String mask(int valor, String mask) {
        return mask(Integer.toString(valor), mask);
    }

    public static String maskInscricao(String cnpj) {
        return mask(cnpj, "##.###.##-#");
    }

    public static String maskCep(String cep) {
        return mask(cep, "##.###-###");
    }

    public static String maskTelefone(String phone) {
        return mask(phone, "(##)####-####");
    }
    
    public static String maskPlacaCarro(String placa) {
        return mask(placa, "###-####");
    }

    public static String maskCpf(String value) {
        if (value != null && !value.isEmpty()) {
            value = value.replaceAll("[^\\d]", "");
            if (value.length() < CPF_LENGTH) {
                value = fillZeros(value, CPF_LENGTH);
            }
            return mask(value, "###.###.###-##");
        }
        return null;
    }

    public static String maskCnpj(String value) {
        if (value != null && !value.isEmpty()) {
            value = value.replaceAll("[^\\d]", "");
            if (value.length() < CNPJ_LENGTH) {
                value = fillZeros(value, CNPJ_LENGTH);
            }
            return mask(value, "##.###.###/####-##");
        }
        return null;
    }

    public static String maskCpfCnpj(String value) {
        if (value == null) {
            return null;
        }
        if (value.toString().length() > 11) {
            return maskCnpj(value.toString());
        } else {
            return maskCpf(value.toString());
        }
    }

    public static String fillZeros(String string, int tamanho) {
        String value = "";
        if (string != null && !string.trim().isEmpty()) {
            value = string;
            for (int x = value.length(); x < tamanho; x++) {
                value = "0" + value;
            }
        }
        return value;
    }

    public static String mask(String value, String mask) {
        for (int i = 0; i < value.length(); i++) {
            mask = mask.replaceFirst("#", value.substring(i, i + 1));
        }
        return mask;
    }
}
