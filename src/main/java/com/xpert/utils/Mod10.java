package com.xpert.utils;

/**
 *
 * @author arnaldo
 */
public class Mod10 {

    /**
     * Calcular um dígito verificador a partir de uma sequência de números
     * enviada.
     *
     * @param number - Sequência de números para cálculo do DV
     * @return DV gerado.
     */
    public static String getDV(String number) {

        if (!org.apache.commons.lang.StringUtils.isNumeric(number)) {
            throw new IllegalArgumentException("Value is not number");
        }

        int pesoAlternado = 2;
        int dv = 0;
        String parcela;
        for (int i = number.length() - 1; i >= 0; i--) {
            parcela = String.valueOf(Integer.parseInt(number.substring(i, i + 1)) * pesoAlternado);
            for (int j = 0; j < parcela.length(); j++) {
                dv += Integer.parseInt(parcela.substring(j, j + 1));
            }
            if (pesoAlternado == 2) {
                pesoAlternado--;
            } else {
                pesoAlternado++;
            }
        }
        dv = dv % 10;

        if (dv != 0) {
            dv = 10 - dv;
        }
        return String.valueOf(dv);
    }

    /**
     * Calcular um dígito verificador com a quantidade de casas indicadas a
     * partir de uma sequência de números enviada.
     *
     * @param number - Sequência de números para cálculo do DV
     * @param quantidadeDigitos Quantidade de dígitos a serem retornados
     * @return DV gerado.
     */
    public static String getDV(String number, int quantidadeDigitos) {
        if (quantidadeDigitos > 1) {
            String parcial = getDV(number);
            return parcial + getDV(number + parcial, --quantidadeDigitos);
        } else {
            return getDV(number);
        }
    }
}
