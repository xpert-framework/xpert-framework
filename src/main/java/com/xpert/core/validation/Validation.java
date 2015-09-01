package com.xpert.core.validation;

import com.xpert.utils.Mod11;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static boolean validateCPF(String cpf) {

        cpf = cpf.replace(".", "").replace("-", "");
        boolean retorno = false;
        if (cpf.length() < 11) {
            return retorno;
        }
        if (cpf.equals("11111111111")) {
            return retorno;
        }
        if (cpf.equals("22222222222")) {
            return retorno;
        }
        if (cpf.equals("33333333333")) {
            return retorno;
        }
        if (cpf.equals("44444444444")) {
            return retorno;
        }
        if (cpf.equals("55555555555")) {
            return retorno;
        }
        if (cpf.equals("66666666666")) {
            return retorno;
        }
        if (cpf.equals("77777777777")) {
            return retorno;
        }
        if (cpf.equals("88888888888")) {
            return retorno;
        }
        if (cpf.equals("99999999999")) {
            return retorno;
        }
        if (cpf.equals("00000000000")) {
            return retorno;
        }

        int d1, d2;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;

        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;

        for (int nCount = 1; nCount < cpf.length() - 1; nCount++) {
            digitoCPF = Integer.valueOf(cpf.substring(nCount - 1, nCount)).intValue();

            // multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
            d1 = d1 + (11 - nCount) * digitoCPF;

            // para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
            d2 = d2 + (12 - nCount) * digitoCPF;
        }

        // Primeiro resto da divis�o por 11.
        resto = (d1 % 11);

        // Se o resultado for 0 ou 1 o digito � 0 caso contr�rio o digito � 11 menos o resultado anterior.
        if (resto < 2) {
            digito1 = 0;
        } else {
            digito1 = 11 - resto;
        }

        d2 += 2 * digito1;

        // Segundo resto da divis�o por 11.
        resto = (d2 % 11);

        // Se o resultado for 0 ou 1 o digito � 0 caso contr�rio o digito � 11 menos o resultado anterior.
        if (resto < 2) {
            digito2 = 0;
        } else {
            digito2 = 11 - resto;
        }

        // Digito verificador do CPF que est� sendo validado.
        String nDigVerific = cpf.substring(cpf.length() - 2, cpf.length());

        // Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

        // comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
        return nDigVerific.equals(nDigResult);
    }

    public static boolean validateCNPJ(String cnpj) {
        @SuppressWarnings("unused")
        int soma = 0, aux, dig;

        cnpj = cnpj.replace(".", "").replace("-", "").replace("/", "");

        if (cnpj.length() != 14) {
            return false;
        }

        String cnpj_calc = cnpj.substring(0, 12);

        char[] chr_cnpj = cnpj.toCharArray();

        // Primeira parte
        for (int i = 0; i < 4; i++) {
            if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
            }
        }
        dig = 11 - (soma % 11);

        cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

        // Segunda parte
        soma = 0;
        for (int i = 0; i < 5; i++) {
            if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
            }
        }
        for (int i = 0; i < 8; i++) {
            if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
            }
        }
        dig = 11 - (soma % 11);
        cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

        return cnpj.equals(cnpj_calc);
    }

    public static boolean validateEmail(String strEmail) {
        boolean retorno = true;

        Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(strEmail.toLowerCase());

        if (!m.find()) {
            retorno = false;
        }

        return retorno;
    }


    public static boolean validateCEP(String cep) {
        boolean retorno = true;

        cep = cep.replace(".", "").replace("-", "");

        Pattern p = Pattern.compile("^[0-9]{8}$+");
        Matcher m = p.matcher(cep);

        if (!m.find()) {
            retorno = false;
        }

        return retorno;
    }

  
    
    public static boolean validateLongitude(String coordenada) {
        coordenada = coordenada.toUpperCase();
        try {
            if (coordenada.charAt(3) != 'º'
                    || coordenada.charAt(6) != '\''
                    || !coordenada.substring(9, 11).equals("\'\'")
                    ) {
                return false;
            }
            int graus = Integer.parseInt(coordenada.substring(0, 3));
            int minutos = Integer.parseInt(coordenada.substring(4, 6));
            int segundos = Integer.parseInt(coordenada.substring(7, 9));
            if (graus > 180
                    || (graus == 180 && (minutos != 0 || segundos != 0))
                    || (graus < 180 && (minutos > 59 || segundos > 59))) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    public static boolean validateLatitude(String coordenada) {
        coordenada = coordenada.toUpperCase();
        try {
            if (coordenada.charAt(2) != 'º'
                    || coordenada.charAt(5) != '\''
                    || !coordenada.substring(8, 10).equals("\'\'")
                    ) {
                return false;
            }
            int graus = Integer.parseInt(coordenada.substring(0, 2));
            int minutos = Integer.parseInt(coordenada.substring(3, 5));
            int segundos = Integer.parseInt(coordenada.substring(6, 8));
            if (graus > 90
                    || (graus == 90 && (minutos != 0 || segundos != 0))
                    || (graus < 90 && (minutos > 59 || segundos > 59))) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
     /**
     * TRUE if String is blank
     *
     * @param string
     * @return
     */
    public static boolean isBlank(String string) {

        if (string == null || string.trim().isEmpty()) {
            return true;
        }
        return false;

    }
    
    /**
     * TRUE if RENAVAM is valid
     *
     * @param string
     * @return
     */
    public static boolean validateRENAVAM(String string) {

        if(isBlank(string)){
            return false;
        }
        
        if (string.length() < 9 || string.length() > 11) {
            return false;
        }
        
        String dv = string.substring(string.length()-1);
        
        String dvGenerate = Mod11.getDV(string.substring(0, string.length()-1));
        
        if(dv.equals(dvGenerate)){
             return true;
        }
        
        return false;

    }
}
