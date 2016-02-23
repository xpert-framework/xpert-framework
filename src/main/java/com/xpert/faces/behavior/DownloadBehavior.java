package com.xpert.faces.behavior;

import com.xpert.i18n.XpertResourceBundle;
import com.xpert.utils.StringEscapeUtils;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;

/**
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "scripts/core.js"),
    @ResourceDependency(library = "xpert", name = "css/style.css")
})
public class DownloadBehavior extends ClientBehaviorBase {

    private String oncomplete;
    private String onstart;
    private String message;
    private Boolean showModal = Boolean.TRUE;

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {

        StringBuilder script = new StringBuilder();
        String clientId = behaviorContext.getComponent().getClientId();

        script.append("Xpert.behavior.download('").append(clientId).append("', {");
        script.append("showModal:");
        if (showModal != null && showModal) {
            script.append("true");
        } else {
            script.append("false");
        }
        if (oncomplete != null && !oncomplete.isEmpty()) {
            script.append(",oncomplete:function(){").append(oncomplete).append(";}");
        }
        if (onstart != null && !onstart.isEmpty()) {
            script.append(",onstart:function(){").append(onstart).append(";}");
        }
        script.append(",message:'");
        if (message != null && !message.isEmpty()) {
            script.append(StringEscapeUtils.escapeJavaScript(message));
        } else {
            script.append(XpertResourceBundle.get("loading"));

        }
        script.append("'});");

        return script.toString();
    }

    public Boolean getShowModal() {
        return showModal;
    }

    public void setShowModal(Boolean showModal) {
        this.showModal = showModal;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getOnstart() {
        return onstart;
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
