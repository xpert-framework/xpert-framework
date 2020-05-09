package com.xpert.core.validation;

import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.Restrictions;
import com.xpert.utils.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author ayslan
 */
public class UniqueFields extends ArrayList<UniqueField> {

    public static void main(String[] args) {
        System.out.println(ReflectionUtils.getDeclaredFields(UniqueFields.class));
    }

    private static final Map<Class, Set<String[]>> UNIQUE_CONSTRAINT_CACHE = new HashMap<>();

    public static UniqueFields from(Class entity) {

        Set<String[]> cache = UNIQUE_CONSTRAINT_CACHE.get(entity);
        if (cache != null) {
            return from(cache);
        }

        Set<String[]> setUniques = new HashSet<>();

        //1 - Search for @Table "uniqueConstraints"
        Table table = (Table) entity.getAnnotation(Table.class);
        if (table != null) {
            UniqueConstraint[] uniqueConstraints = table.uniqueConstraints();
            if (uniqueConstraints != null) {
                for (UniqueConstraint uniqueConstraint : uniqueConstraints) {
                    setUniques.add(uniqueConstraint.columnNames());
                }
            }
        }

        //2 - search for fields
        List<Field> fields = ReflectionUtils.getDeclaredFields(entity);
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.unique()) {
                setUniques.add(new String[]{field.getName()});
            }
        }

        //3 - search for methods
        List<Method> methods = ReflectionUtils.getDeclaredMethods(entity);
        for (Method method : methods) {
            Column column = method.getAnnotation(Column.class);
            if (column != null && column.unique()) {
                setUniques.add(new String[]{ReflectionUtils.getMethodName(method)});
            }
        }
        //update cache
        UNIQUE_CONSTRAINT_CACHE.put(entity, setUniques);
        
        return from(setUniques);
    }

    public static UniqueFields from(Set<String[]> stringSet) {
        if (stringSet == null || stringSet.isEmpty()) {
            return null;
        }

        UniqueFields uniqueFields = new UniqueFields();
        for (String[] stringArray : stringSet) {
            uniqueFields.add(stringArray);
        }
        return uniqueFields;
    }

    public UniqueFields() {
    }

    /**
     *
     * @param fields
     */
    public UniqueFields(String... fields) {
        add(new UniqueField(fields));
    }

    /**
     *
     * @param restrictions
     * @param fields
     */
    public UniqueFields(Restrictions restrictions, String... fields) {
        add(new UniqueField(restrictions, fields));
    }

    /**
     *
     * @param restriction
     * @param fields
     */
    public UniqueFields(Restriction restriction, String... fields) {
        add(new UniqueField(restriction, fields));
    }

    public UniqueFields add(Restriction restriction, String... fields) {
        add(new UniqueField(restriction, fields));
        return this;
    }

    public UniqueFields add(String... fields) {
        add(new UniqueField(fields));
        return this;
    }

    public UniqueFields add(UniqueField uniqueField, String message) {
        add(uniqueField.setMessage(message));
        return this;
    }

}
