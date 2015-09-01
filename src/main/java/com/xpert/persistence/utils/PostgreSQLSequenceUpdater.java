package com.xpert.persistence.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;

/**
 * Sequence update to PostgreSQL
 *
 * @author ayslan, arnaldo
 */
public class PostgreSQLSequenceUpdater extends SequenceUpdater {

    private final EntityManager entityManager;
    private DataSource dataSource;

    public PostgreSQLSequenceUpdater(DataSource dataSource, EntityManager entityManager) {
        this.dataSource = dataSource;
        this.entityManager = entityManager;
    }

    public PostgreSQLSequenceUpdater(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void changeCurrentValue(Connection connection, String sequenceName, Long maxId) {
        if (connection != null) {
            changeCurrentValueJdbc(connection, sequenceName, maxId);
        } else {
            changeCurrentValueJpa(sequenceName, maxId);
        }
    }

    public void changeCurrentValueJpa(String sequenceName, Long maxId) {
        //select setval('sequencename' ,1);
        String setValQueryString = getStringSetVal(sequenceName.trim(), maxId);
        Query querySetVal = entityManager.createNativeQuery(setValQueryString);
        querySetVal.getSingleResult();
    }

    public void changeCurrentValueJdbc(Connection connection, String sequenceName, Long maxId) {
        //select setval('sequencename' ,1);
        String setValQueryString = getStringSetVal(sequenceName.trim(), maxId);
        PreparedStatement statementSetVal = null;
        try {
            statementSetVal = connection.prepareStatement(setValQueryString);
            statementSetVal.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (statementSetVal != null) {
                    statementSetVal.close();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void createSequence(Connection connection, String schema, String sequenceName, int initialValue, int allocationSize) {
        throw new UnsupportedOperationException("PostgreSQL is not supported yet.");
    }

    public String getStringSetVal(String sequenceName, Long maxId) {
        if (maxId == null) {
            //false indicate that sequence is not called (maxId == null the table is empty)
            return "SELECT SETVAL('" + sequenceName + "', 1, false)";
        } else {
            return "SELECT SETVAL('" + sequenceName + "', " + maxId + ")";
        }
    }

}
