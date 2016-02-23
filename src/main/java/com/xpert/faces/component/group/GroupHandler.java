package com.xpert.faces.component.group;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.facelets.MethodRule;

/**
 *
 * @author ayslan
 */
public class GroupHandler extends ComponentHandler {

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
