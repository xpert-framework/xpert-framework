package com.xpert.faces.component.group;

import com.xpert.faces.component.renderkit.CoreRenderer;
import java.io.IOException;
import java.util.Map;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author ayslan
 */
public class GroupRenderer extends CoreRenderer {

    private static final long serialVersionUID = -3437482438193749497L;

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

        Group group = (Group) component;

        int rowCount = group.getRowCount();

        String rowIndexVar = group.getRowIndexVar();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

        for (int i = 0; i < rowCount; i++) {
            if (rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }
            group.setRowIndex(i);
            if (rowIndexVar != null) {
                requestMap.put(rowIndexVar, i);
            }
            if (group.isRowAvailable()) {
                renderChildren(context, group);
            }
        }

        //cleanup
        group.setRowIndex(-1);

        if (rowIndexVar != null) {
            requestMap.remove(rowIndexVar);
        }

    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}
