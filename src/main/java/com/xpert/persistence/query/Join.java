package com.xpert.persistence.query;

/**
 *
 * @author ayslan
 */
public class Join {

    private String property;
    private String alias;
    private JoinType type;
    private boolean fetch;

    public Join(String property, JoinType type) {
        this.property = property;
        this.type = type;
        this.fetch = false;
    }

    public Join(String property, JoinType type, boolean fetch) {
        this.property = property;
        this.type = type;
        this.fetch = fetch;
    }

    public Join(String property, String alias, JoinType type) {
        this.property = property;
        this.alias = alias;
        this.type = type;
        this.fetch = false;
    }

    public Join(String property, String alias, JoinType type, boolean fetch) {
        this.property = property;
        this.alias = alias;
        this.type = type;
        this.fetch = fetch;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public boolean isFetch() {
        return fetch;
    }

    public void setFetch(boolean fetch) {
        this.fetch = fetch;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (property != null) {
            builder.append("(Property: ").append(property).append(") ");
        }
        if (alias != null) {
            builder.append("(Alias: ").append(alias).append(") ");
        }
        if (type != null) {
            builder.append("(Type: ").append(type.getClausule()).append(") ");
        }
        builder.append("(Fetch: ").append(fetch).append(") ");

        builder.append("}");
        return builder.toString();
    }

}
