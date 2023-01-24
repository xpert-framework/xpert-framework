package com.xpert.faces.conversion;

import com.xpert.i18n.XpertResourceBundle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * Converter to show Yes when true, No for false
 *
 * @author ayslan
 */
public class DataSerproConverter implements Converter {

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
