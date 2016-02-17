package com.xpert.persistence.query;

/**
 *
 * @author Ayslan
 */
public enum LikeType {

    BEGIN, END, BOTH;

    public static String getNameList() {
        StringBuilder builder = new StringBuilder();
        for (LikeType type : LikeType.values()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(type.name().toLowerCase());
        }
        return builder.toString();
    }

}
