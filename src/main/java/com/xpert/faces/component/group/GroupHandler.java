package com.xpert.faces.component.group;

import jakarta.faces.view.facelets.ComponentConfig;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.MetaRule;
import jakarta.faces.view.facelets.MetaRuleset;
import java.io.Serializable;
import org.primefaces.facelets.MethodRule;

/**
 *
 * @author ayslan
 */
public class GroupHandler extends ComponentHandler implements Serializable {

    private static final long serialVersionUID = 9028724307168014386L;
    
    private static final MetaRule SORT_FUNCTION = new MethodRule("sortFunction", Integer.class, new Class[]{Object.class, Object.class});
    private static final MetaRule ITEM_SORT_FUNCTION = new MethodRule("itemSortFunction", Integer.class, new Class[]{Object.class, Object.class});

    public GroupHandler(ComponentConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);

        metaRuleset.addRule(SORT_FUNCTION);
        metaRuleset.addRule(ITEM_SORT_FUNCTION);

        return metaRuleset;
    }
}
