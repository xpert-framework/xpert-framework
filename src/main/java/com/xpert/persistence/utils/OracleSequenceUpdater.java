package com.xpert.persistence.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;
import javax.sql.DataSource;

/**
 *
 * Sequence updater to Oracle database
 *
 * @author ayslan, arnaldo
 */
public class OracleSequenceUpdater extends SequenceUpdater implements Serializable {

    private static final long serialVersionUID = -5329620832665889911L;

    private final EntityManager entityManager;
    private DataSource dataSource;

    public OracleSequenceUpdater(DataSource dataSource, EntityManager entityManager) {
        this.dataSource = dataSource;
        this.entityManager = entityManager;
    }

    public OracleSequenceUpdater(EntityManager entityManager) {
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

        if (maxId == null) {
            maxId = 1L;
        }

        String nextVal = getSelectNextVal(sequenceName);

        Query queryNextVal = entityManager.createNativeQuery(nextVal);
        Number nextValResult = (Number) queryNextVal.getSingleResult();

        String alterDefault = getAlterSequenceIncrementBy(sequenceName, 1);
        String alterMax = getAlterSequenceIncrementBy(sequenceName, (maxId - nextValResult.longValue()));

        if ((maxId - nextValResult.longValue()) != 0) {
            Query queryAlterMax = entityManager.createNativeQuery(alterMax);
            queryAlterMax.executeUpdate();
        }

        Query queryNext = entityManager.createNativeQuery(nextVal);
        queryNext.executeUpdate();

        Query queryAlterDefault = entityManager.createNativeQuery(alterDefault);
        queryAlterDefault.executeUpdate();
    }

    public void changeCurrentValueJdbc(Connection connection, String sequenceName, Long maxId) {

        if (maxId == null) {
            maxId = 1L;
        }

        String nextVal = getSelectNextVal(sequenceName);
        PreparedStatement statementNextVal = null;
        PreparedStatement statementAlterMax = null;
        PreparedStatement statementNext = null;
        PreparedStatement statementAlterDefault = null;
        ResultSet resultSet = null;
        
        try {

            statementNextVal = connection.prepareStatement(nextVal);
            statementNextVal.execute();
            resultSet = statementNextVal.getResultSet();
            resultSet.next();
            long nextValResult = resultSet.getLong(1);

            String alterDefault = getAlterSequenceIncrementBy(sequenceName, 1);
            String alterMax = getAlterSequenceIncrementBy(sequenceName, (maxId - nextValResult));

            if ((maxId - nextValResult) != 0) {
                statementAlterMax = connection.prepareStatement(alterMax);
                statementAlterMax.execute();
                statementAlterMax.close();
            }

            statementNext = connection.prepareStatement(nextVal);
            statementNext.execute();

            statementAlterDefault = connection.prepareStatement(alterDefault);
            statementAlterDefault.execute();

            //close
            resultSet.close();
            statementNextVal.close();
            statementAlterDefault.close();
            statementNext.close();

        } catch (SQLException ex) {
            throw new RuntimeException("Error updating sequence " + sequenceName, ex);
        } finally {
            //close result set and statements
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statementNextVal != null) {
                    statementNextVal.close();
                }
                if (statementAlterMax != null) {
                    statementAlterMax.close();
                }
                if (statementNext != null) {
                    statementNext.close();
                }
                if (statementAlterDefault != null) {
                    statementAlterDefault.close();
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
    public void createSequence(final Connection connection, final String schema, final String sequenceName, final int initialValue, final int allocationSize) {
        if (connection != null) {
            createSequenceJdbc(connection, schema, sequenceName, initialValue, allocationSize);
        } else {
            createSequenceJpa(schema, sequenceName, initialValue, allocationSize);
        }
    }

    public void createSequenceJdbc(final Connection connection, final String schema, final String sequenceName, final int initialValue, final int allocationSize) {

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(getQuerySelectSequence());

            statement.setString(1, sequenceName.trim().toUpperCase());
            statement.execute();
            resultSet = statement.getResultSet();
            resultSet.next();
            Long result = resultSet.getLong(1);

            if (result == null || result.intValue() <= 0) {
                String nameWithSchema = sequenceName;
                if (schema != null && !schema.isEmpty()) {
                    nameWithSchema = nameWithSchema + "." + sequenceName;
                }
                String queryCreate = getQueryCreateSequence(nameWithSchema, initialValue, allocationSize);
                PreparedStatement statementCreate = connection.prepareStatement(queryCreate);
                statementCreate.execute();

            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error creating sequence " + sequenceName, ex);
        } finally {
            //close result set and statements
            try {
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public void createSequenceJpa(final String schema, final String sequenceName, final int initialValue, final int allocationSize) {
        Query querySelect = entityManager.createNativeQuery(getQuerySelectSequence());
        querySelect.setParameter(1, sequenceName.trim().toUpperCase());
        Number result = (Number) querySelect.getSingleResult();

        if (result == null || result.intValue() <= 0) {
            String nameWithSchema = sequenceName;
            if (schema != null && !schema.isEmpty()) {
                nameWithSchema = nameWithSchema + "." + sequenceName;
            }
            String queryCreate = getQueryCreateSequence(nameWithSchema, initialValue, allocationSize);
            Query queryNext = entityManager.createNativeQuery(queryCreate);
            queryNext.executeUpdate();
        }
    }

    public String getAlterSequenceIncrementBy(String sequenceName, long incrementBy) {
        return "ALTER SEQUENCE " + sequenceName + " INCREMENT by " + incrementBy;
    }

    public String getSelectNextVal(String sequenceName) {
        return "SELECT " + sequenceName + ".NEXTVAL FROM dual";
    }

    public String getQuerySelectSequence() {
        return "SELECT COUNT(*) FROM user_sequences WHERE upper(sequence_name) = ? ";
    }

    public String getQueryCreateSequence(String sequenceName, int initialValue, int allocationSize) {
        return "CREATE SEQUENCE " + sequenceName + " INCREMENT BY " + allocationSize + " START WITH " + initialValue;
    }

}
