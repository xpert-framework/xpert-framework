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
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 *
 * @author ayslan
 */
public class UniqueFields extends ArrayList<UniqueField> {

    private static final Logger LOG = Logger.getLogger(UniqueFields.class.getName());

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

        Map<String, String> fieldColumn = new HashMap<>();

        //2 - search for fields
        List<Field> fields = ReflectionUtils.getDeclaredFields(entity);
        for (Field field : fields) {
            //@Column
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                fieldColumn.put(column.name(), field.getName());
                if (column.unique()) {
                    setUniques.add(new String[]{field.getName()});
                }
            }
            //@JoinColumn
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if (joinColumn != null) {
                fieldColumn.put(joinColumn.name(), field.getName());
                if (joinColumn.unique()) {
                    setUniques.add(new String[]{field.getName()});
                }
            }
            //When @ManyToOne and @OneToOne and @JoinColumn is not found, assume "field_id"
            if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                if (joinColumn == null) {
                    fieldColumn.put(field.getName().toLowerCase() + "_id", field.getName());
                }
            }
        }

        //3 - search for methods
        List<Method> methods = ReflectionUtils.getDeclaredMethods(entity);
        for (Method method : methods) {
            //@Column
            Column column = method.getAnnotation(Column.class);
            String fieldName = ReflectionUtils.getMethodName(method);
            if (column != null) {
                fieldColumn.put(column.name(), fieldName);
                if (column.unique()) {
                    setUniques.add(new String[]{fieldName});
                }
            }
            //@JoinColumn
            JoinColumn joinColumn = method.getAnnotation(JoinColumn.class);
            if (joinColumn != null) {
                fieldColumn.put(joinColumn.name(), fieldName);
                if (joinColumn.unique()) {
                    setUniques.add(new String[]{fieldName});
                }
            }
            //When @ManyToOne and @OneToOne and @JoinColumn is not found, assume "field_id"
            if (method.isAnnotationPresent(ManyToOne.class) || method.isAnnotationPresent(OneToOne.class)) {
                if (joinColumn == null) {
                    fieldColumn.put(fieldName.toLowerCase() + "_id", fieldName);
                }
            }
        }

        //verify if all fields exists in entity
        //if field is not found then get by @Colunm
        for (String[] fieldsName : setUniques) {
            for (int i = 0; i < fieldsName.length; i++) {
                Field field = ReflectionUtils.getDeclaredField(entity, fieldsName[i]);
                if (field == null) {
                    String fieldByColumnName = fieldColumn.get(fieldsName[i]);
                    if (fieldByColumnName != null) {
                        fieldsName[i] = fieldByColumnName;
                    } else {
                        LOG.log(Level.WARNING, "Field {0} is not found on class {1} and its used in @UniqueConstraint/@Colunm(unique=true)/@JoinColunm(unique=true). This field will be not used to validade data",
                                new Object[]{fieldsName[i], entity.getName()});
                        fieldsName[i] = null;
                    }
                }
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
