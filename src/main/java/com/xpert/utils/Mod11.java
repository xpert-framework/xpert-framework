package com.xpert.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ayslan
 */
public class Mod11 {

    /**
     * retorna o dv de um numero
     * @param number
     * @return 
     */
    public static String getDV(String number) {

        List<Integer> list = new ArrayList<Integer>();
        for (int i = number.length() - 1, j = 2; i >= 0; i--) {
            list.add(j * Integer.parseInt(number.substring(i, i + 1)));
            if (j == 9) {
                j = 2;
            } else {
                j++;
            }
        }
        Integer somatorio = 0;
        for (Integer i : list) {
            somatorio += i;
        }
        Integer resto = somatorio % 11;
        Integer dv = 11 - resto;
        if (dv == 10 || dv == 11) {
            dv = 0;
        }
        
        return dv.toString();
    }

    /**
     * retorno o dv do CPF
     * @param string
     * @return 
     */
    public static String getMod11(String string) {
        int d1, d2;
        int digito1, digito2, resto;
        int digitoVerificador;
        String nDigResult;

        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;

        for (int nCount = 1; nCount <= string.length(); nCount++) {
            digitoVerificador = Integer.valueOf(string.substring(nCount - 1, nCount)).intValue();

            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
            d1 = d1 + (11 - nCount) * digitoVerificador;

            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
            d2 = d2 + (12 - nCount) * digitoVerificador;
        }

        //Primeiro resto da divisão por 11.
        resto = (d1 % 11);

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2) {
            digito1 = 0;

        } else {
            digito1 = 11 - resto;


        }
        d2 += 2 * digito1;

        //Segundo resto da divisão por 11.
        resto = (d2 % 11);

        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2) {
            digito2 = 0;

        } else {
            digito2 = 11 - resto;

            //Digito verificador do CPF que está sendo validado.

        }

        //Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
        return nDigResult;
    }

}
