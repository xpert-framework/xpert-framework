package com.xpert.faces.conversion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import java.io.Serializable;

/**
 *
 * Converter to show Yes when true, No for false
 *
 * @author ayslan
 */
public class DataHoraSerproConverter implements Converter, Serializable {

    private static final long serialVersionUID = -814703145979968127L;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String dataPadraoSerpro = (String) value;

        try {
            if (dataPadraoSerpro == null) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date dataHora = sdf.parse(dataPadraoSerpro);
            SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return dt.format(dataHora);
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date dataHora = sdf.parse(dataPadraoSerpro);
                SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                return dt.format(dataHora);
            } catch (ParseException ex) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date dataHora = sdf.parse(dataPadraoSerpro);
                    SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    return dt.format(dataHora);
                } catch (ParseException ex2) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
                        Date dataHora = sdf.parse(dataPadraoSerpro);
                        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        return dt.format(dataHora);
                    } catch (ParseException ex3) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date dataHora = sdf.parse(dataPadraoSerpro);
                            SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
                            return dt.format(dataHora);
                        } catch (ParseException ex4) {
                            return null;
                        }
                    }
                }
            }
        }
    }
}
