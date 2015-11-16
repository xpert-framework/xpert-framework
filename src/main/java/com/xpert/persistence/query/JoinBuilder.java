package com.xpert.persistence.query;

import java.util.ArrayList;

/**
 *
 * @author Ayslan
 */
public class JoinBuilder extends ArrayList<Join> {

    private String rootAlias;
    private boolean distinct;
    
    public String getJoinString() {
        return getJoinString(QueryType.SELECT);
    }
    public String getJoinString(QueryType queryType) {
        StringBuilder builder = new StringBuilder();
        for (Join join : this) {
            builder.append(" ");
            builder.append(join.getType().getClausule());
            if (join.isFetch() && queryType != null && queryType.equals(QueryType.SELECT)) {
                builder.append(" FETCH");
            }
            builder.append(" ");
            builder.append(join.getProperty());
            if (join.getAlias() != null && !join.getAlias().isEmpty()) {
                builder.append(" ");
                builder.append(join.getAlias());
            }
        }
        return builder.toString();
    }
    

    public JoinBuilder() {
    }

    public JoinBuilder(String rootAlias) {
        this.rootAlias = rootAlias;
    }

    public JoinBuilder(String rootAlias, boolean distinct) {
        this.rootAlias = rootAlias;
        this.distinct = distinct;
    }
    
    public JoinBuilder leftJoin(String join) {
        add(new Join(join, JoinType.LEFT_JOIN));
        return this;
    }

    public JoinBuilder leftJoin(String join, String alias) {
        add(new Join(join, alias, JoinType.LEFT_JOIN));
        return this;
    }

    public JoinBuilder leftJoinFetch(String join) {
        add(new Join(join, JoinType.LEFT_JOIN, true));
        return this;
    }

    public JoinBuilder leftJoinFetch(String join, String alias) {
        add(new Join(join, alias, JoinType.LEFT_JOIN, true));
        return this;
    }

    public JoinBuilder innerJoin(String join) {
        add(new Join(join, JoinType.INNER_JOIN));
        return this;
    }

    public JoinBuilder innerJoin(String join, String alias) {
        add(new Join(join, alias, JoinType.INNER_JOIN));
        return this;
    }

    public JoinBuilder innerJoinFetch(String join) {
        add(new Join(join, JoinType.INNER_JOIN, true));
        return this;
    }

    public JoinBuilder innerJoinFetch(String join, String alias) {
        add(new Join(join, alias, JoinType.INNER_JOIN, true));
        return this;
    }

    public JoinBuilder join(String join) {
        add(new Join(join, JoinType.JOIN));
        return this;
    }

    public JoinBuilder join(String join, String alias) {
        add(new Join(join, alias, JoinType.JOIN));
        return this;
    }

    public JoinBuilder joinFetch(String join) {
        add(new Join(join, JoinType.JOIN, true));
        return this;
    }

    public JoinBuilder joinFetch(String join, String alias) {
        add(new Join(join, alias, JoinType.JOIN, true));
        return this;
    }

    public JoinBuilder rightJoin(String join) {
        add(new Join(join, JoinType.RIGHT_JOIN));
        return this;
    }

    public JoinBuilder rightJoin(String join, String alias) {
        add(new Join(join, alias, JoinType.RIGHT_JOIN));
        return this;
    }

    public JoinBuilder rightJoinFetch(String join) {
        add(new Join(join, JoinType.RIGHT_JOIN, true));
        return this;
    }

    public JoinBuilder rightJoinFetch(String join, String alias) {
        add(new Join(join, alias, JoinType.RIGHT_JOIN, true));
        return this;
    }

    public String getRootAlias() {
        return rootAlias;
    }

    public boolean isDistinct() {
        return distinct;
    }
    
    
}
