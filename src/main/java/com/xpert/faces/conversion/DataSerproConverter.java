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
public class DataSerproConverter implements Converter, Serializable {

    private static final long serialVersionUID = 9019795991481631749L;

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date dataHora = sdf.parse(dataPadraoSerpro);
            SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
            return dt.format(dataHora);
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date dataHora = sdf.parse(dataPadraoSerpro);
                SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
                return dt.format(dataHora);
            } catch (ParseException ex) {
                return null;
            }
        }
    }
}
