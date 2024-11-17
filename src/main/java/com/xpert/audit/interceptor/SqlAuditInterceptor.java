package com.xpert.audit.interceptor;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 *
 * @author Arnaldo Jr.
 */
public class SqlAuditInterceptor implements StatementInspector {

    private static final long serialVersionUID = 1L;

    // ThreadLocal para armazenar o SQL atual
    private static final ThreadLocal<String> currentSql = ThreadLocal.withInitial(() -> null);

    // ThreadLocal para controlar se o SQL deve ser capturado
    private static final ThreadLocal<Boolean> captureSql = ThreadLocal.withInitial(() -> false);

    @Override
    public String inspect(String sql) {
        // Só captura o SQL se a flag estiver habilitada
        if (Boolean.TRUE.equals(captureSql.get())) {
            currentSql.set(sql);
        }
        return sql;
    }

    /**
     * Habilita a captura de SQL.
     */
    public static void startCapturing() {
        captureSql.set(true);
        currentSql.set(null); // Limpa qualquer SQL anterior
    }

    /**
     * Desabilita a captura de SQL e retorna o SQL capturado.
     *
     * @return o SQL capturado ou null, se nenhum foi capturado
     */
    public static String stopCapturing() {
        String sql = currentSql.get();
        captureSql.remove();
        currentSql.remove();
        return sql;
    }

}
