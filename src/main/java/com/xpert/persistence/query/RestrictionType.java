package com.xpert.persistence.query;

/**
 *
 * @author ayslan
 */
public enum RestrictionType {

    EQUALS("=", "eq"),
    NOT_EQUALS("!=", "ne"),
    GREATER_THAN(">", "gt"),
    LESS_THAN("<", "lt"),
    GREATER_EQUALS_THAN(">=", "gte"),
    LESS_EQUALS_THAN("<=", "lte"),
    LIKE("LIKE", "like"),
    NOT_LIKE("NOT LIKE", "notlike"),
    IN("IN", "in"),
    NOT_IN("NOT IN", "notin"),
    MEMBER_OF("MEMBER OF", "memberof"),
    NOT_MEMBER_OF("NOT MEMBER OF", "notmemberof"),
    NULL("IS NULL", "null", true),
    NOT_NULL("IS NOT NULL", "notnull", true),
    DATA_TABLE_FILTER("LIKE"),
    OR("OR", true),
    START_GROUP("(", true),
    QUERY_STRING("", true),
    END_GROUP(")", true);

    private final String symbol;
    private String acronym;
    private boolean ignoreParameter;

    private RestrictionType(String symbol) {
        this.symbol = symbol;
    }

    private RestrictionType(String symbol, String acronym) {
        this.symbol = symbol;
        this.acronym = acronym;
    }

    private RestrictionType(String symbol, boolean ignoreParameter) {
        this.symbol = symbol;
        this.ignoreParameter = ignoreParameter;
    }

    private RestrictionType(String symbol, String acronym, boolean ignoreParameter) {
        this.symbol = symbol;
        this.acronym = acronym;
        this.ignoreParameter = ignoreParameter;
    }

    public static RestrictionType getByAcronym(String acronym) {
        for (RestrictionType restrictionType : RestrictionType.values()) {
            if (restrictionType.getAcronym() != null && restrictionType.getAcronym().equals(acronym.trim())) {
                return restrictionType;
            }
        }
        return null;
    }

    public static String getAcronymList() {
        StringBuilder builder = new StringBuilder();
        for (RestrictionType restrictionType : RestrictionType.values()) {
            if (restrictionType.getAcronym() != null && !restrictionType.getAcronym().isEmpty()) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(restrictionType.getAcronym());
            }
        }
        return builder.toString();
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isIgnoreParameter() {
        return ignoreParameter;
    }

    public String getAcronym() {
        return acronym;
    }

}
