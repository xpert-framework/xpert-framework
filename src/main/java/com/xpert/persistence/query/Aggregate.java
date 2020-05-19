package com.xpert.persistence.query;

/**
 *
 * @author ayslanms
 */
public class Aggregate {

    public static String sum(String field) {
        return "SUM(" + field + ")";
    }

    public static String count(String field) {
        return "COUNT(" + field + ")";
    }

    public static String max(String field) {
        return "MAX(" + field + ")";
    }

    public static String min(String field) {
        return "MIN(" + field + ")";
    }

    public static String avg(String field) {
        return "AVG(" + field + ")";
    }
}
