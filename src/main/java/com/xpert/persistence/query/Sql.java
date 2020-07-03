package com.xpert.persistence.query;

/**
 *
 * @author ayslanms
 */
public class Sql {

    /**
     * Create a field with alias. Example field: u.name, alias: userName,
     * returns "u.name AS userName"
     *
     * @param field
     * @param alias
     * @return
     */
    public static String select(String field, String alias) {
        return field + as(alias);
    }

    /**
     * Create "AS" in Query. Example AS TOTAL
     *
     * @param alias
     * @return
     */
    public static String as(String alias) {
        return alias != null ? " AS " + alias : "";
    }

    /**
     * Create SUM Clause
     *
     * @param field
     * @return
     */
    public static String sum(String field) {
        return "SUM(" + field + ")";
    }

    /**
     * Create SUM Clause
     *
     * @param field
     * @param alias
     * @return
     */
    public static String sum(String field, String alias) {
        return sum(field) + as(alias);
    }

    /**
     * Create COUNT Clause
     *
     * @param field
     * @return
     */
    public static String count(String field) {
        return "COUNT(" + field + ")";
    }

    /**
     * Create COUNT DISTINCT Clause
     *
     * @param field
     * @param alias
     * @return
     */
    public static String count(String field, String alias) {
        return count(field) + as(alias);
    }

    /**
     * Create COUNT DISTINCT Clause
     *
     * @param field
     * @return
     */
    public static String countDistinct(String field) {
        return "COUNT(DISTINCT " + field + ")";
    }

    /**
     * Create COUNT DISTINCT Clause
     *
     * @param field
     * @param alias
     * @return
     */
    public static String countDistinct(String field, String alias) {
        return countDistinct(field) + as(alias);
    }

    /**
     * Create MAX Clause
     *
     * @param field
     * @return
     */
    public static String max(String field) {
        return "MAX(" + field + ")";
    }

    /**
     * Create MAX Clause
     *
     * @param field
     * @param alias
     * @return
     */
    public static String max(String field, String alias) {
        return max(field) + as(alias);
    }

    /**
     * Create MIN Clause
     *
     * @param field
     * @return
     */
    public static String min(String field) {
        return "MIN(" + field + ")";
    }

    /**
     * Create MIN Clause
     *
     * @param field
     * @param alias
     * @return
     */
    public static String min(String field, String alias) {
        return min(field) + as(alias);
    }

    /**
     * Create AVG Clause
     *
     * @param field
     * @return
     */
    public static String avg(String field) {
        return "AVG(" + field + ")";
    }

    /**
     * Create AVG Clause
     *
     * @param field
     * @param alias
     * @return
     */
    public static String avg(String field, String alias) {
        return avg(field) + as(alias);
    }

}
