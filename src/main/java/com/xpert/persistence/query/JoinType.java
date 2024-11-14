package com.xpert.persistence.query;

/**
 *
 * @author ayslan
 */
public enum JoinType {
    
    JOIN("JOIN"), 
    INNER_JOIN("INNER JOIN"), 
    LEFT_JOIN("LEFT JOIN"), 
    RIGHT_JOIN("RIGHT JOIN");
    
    private final String clausule;

    private JoinType(String clausule) {
        this.clausule = clausule;
    }
    
    public String getClausule() {
        return clausule;
    }
    
    
    
}
