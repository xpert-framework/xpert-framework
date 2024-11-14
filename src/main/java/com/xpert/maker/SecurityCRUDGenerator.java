package com.xpert.maker;

import com.xpert.utils.HumaniseCamelCase;
import com.xpert.utils.StringUtils;
import java.io.File;
import java.util.List;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 * Class to create acess control generation. The xpert-framework base project
 * has a class to generate Acess Control
 *
 * @author ayslan
 */
public class SecurityCRUDGenerator implements Serializable {

    private static final long serialVersionUID = -6252444417173326626L;

    public static String create(List<Class> classes, String view) {
        StringBuilder builder = new StringBuilder();
        if (classes != null) {
            for (Class clazz : classes) {
                if (!clazz.isAnnotationPresent(Embeddable.class)) {
                    builder.append(create(clazz, view));
                }
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        String teste = "D:\\Desenvolvimento\\xpert-framework\\xpert-framework-base\\base-web\\src\\main\\webapp\\view\\exemplo";
        System.out.println(teste.substring(teste.lastIndexOf("webapp") + 6, teste.length()).replace(File.separator, "/"));
    }

    public static String create(Class clazz, String view) {

        if (view != null && !view.endsWith("/")) {
            view = view + "/";
        }

        String className = clazz.getSimpleName();
        String classLower = StringUtils.getLowerFirstLetter(clazz.getSimpleName());
        String classHuman = new HumaniseCamelCase().humanise(className);

        StringBuilder builder = new StringBuilder();
        builder.append("//").append(classHuman).append("\n");
        builder.append("create(new Permissao(\"").append(classLower).append("\", \"").append(classHuman).append("\", true), null);").append("\n");
        builder.append("create(new Permissao(\"").append(classLower).append(".create\", \"Cadastro de ").append(classHuman).append("\", \"").append(view).append(classLower).append("/create").append(className).append(".jsf\", true), \"").append(classLower).append("\");").append("\n");
        builder.append("create(new Permissao(\"").append(classLower).append(".list\", \"Consulta de ").append(classHuman).append("\", \"").append(view).append(classLower).append("/list").append(className).append(".jsf\", true), \"").append(classLower).append("\");").append("\n");
        builder.append("create(new Permissao(\"").append(classLower).append(".audit\", \"Auditoria de ").append(classHuman).append("\"").append("), \"").append(classLower).append("\");").append("\n");
        builder.append("create(new Permissao(\"").append(classLower).append(".delete\", \"Exclusão de ").append(classHuman).append("\"").append("), \"").append(classLower).append("\");").append("\n");
        builder.append("\n");

        return builder.toString();

    }
}
