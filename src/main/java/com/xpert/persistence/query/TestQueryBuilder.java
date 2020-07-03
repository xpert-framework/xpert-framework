package com.xpert.persistence.query;

import static com.xpert.persistence.query.Sql.*;
import static com.xpert.persistence.query.Restriction.*;

/**
 *
 * @author ayslan
 */
public class TestQueryBuilder {

    public static void printQueryString(Restrictions restrictions) {

        QueryBuilder builder = new QueryBuilder(null);
        builder.from(Class.class);
        builder.add(restrictions);
        System.out.println(restrictions.getQueryString());
        System.out.println(builder.getQueryString());
        System.out.println(builder.getQueryParameters());
    }

    public static void mainff(String[] args) {
        QueryBuilder queryBuilder = new QueryBuilder(null);
        queryBuilder
                .by("horario")
                .aggregate(avg("total"))
                .from(
                        new QueryBuilder()
                                .by(
                                        select("cast(data as date)", "data"),
                                        select("HOUR(data)", "horario")
                                )
                                .aggregate(
                                        count("*", "total")
                                )
                                .from(Object.class, "o")
                );

        System.out.println(queryBuilder.getQueryString());
        System.out.println(queryBuilder.getQueryParameters());
    }

    public static void mainYYY(String[] args) {
        QueryBuilder queryBuilder = new QueryBuilder(null);
        queryBuilder.nativeQuery(true)
                .by("horario")
                .aggregate(avg("total"))
                .from(
                        new QueryBuilder()
                                .by(select("CAST(dataHora as date)", "data"), select("EXTRACT(HOUR FROM dataHora)", "horario"))
                                .aggregate(count("*", "total"))
                                .from(Object.class),
                        "tab"
                );

        System.out.println(queryBuilder.getQueryString());
        System.out.println(queryBuilder.getQueryParameters());
    }

    public static void mainXX(String[] args) {
        QueryBuilder queryBuilder = new QueryBuilder(null);
        queryBuilder
                .by("data", "usuario.nome")
                .aggregate(sum("total", "total"), count("*", "count"))
                .from(Object.class, "o")
                .having(greaterThan(count("*"), 0))
                .having(greaterThan(sum("total"), 100));

        System.out.println(queryBuilder.getQueryString());
        System.out.println(queryBuilder.getQueryParameters());
    }

    public static void main(String[] args) {
        QueryBuilder queryBuilder = new QueryBuilder(null);
        queryBuilder.select("data", "usuario.nome", "COUNT(*)")
                .from(Object.class, "o1")
                .leftJoin("object2", "o2", "o1.id = o2.id")
                .groupBy("data", "usuario.nome")
                .having(Restriction.greaterThan(count("*"), 0))
                .orderBy("data", "usuario.nome");
        System.out.println(queryBuilder.getQueryString());
        System.out.println(queryBuilder.getQueryParameters());
    }

    public static void main33(String[] args) {
        QueryBuilder queryBuilder = new QueryBuilder(null)
                .from(Object.class, "o")
                .like("nome1", "TESTE")
                .like("nome2", "TESTE", false)
                .notLike("nome3", "TESTE")
                .notLike("nome4", "TESTE", false);

        System.out.println(queryBuilder.getQueryString());
    }

    public static void main4(String[] args) {

//        BaseDAO baseDAO = new BaseDAOImpl() {
//
//            @Override
//            public EntityManager getEntityManager() {
//                return null;
//            }
//        };
//
//        baseDAO.setEntityClass(String.class);
//
//        baseDAO.listAttributes("campo", "valor", "teste, outro, mais");
    }

    public static void main5(String[] args) {

        QueryBuilder queryBuilder = new QueryBuilder(null).selectDistinct("b")
                .from(Object.class, "o")
                .innerJoin("outro a")
                .leftJoinFetch("o.atributos b")
                .startGroup()
                .equals("o.teste", "1234")
                .like("a.teste", "1234")
                .endGroup()
                .or()
                .startGroup()
                .isNull("o.teste")
                .endGroup()
                .orderBy("a.teste");

        System.out.println(queryBuilder.getQueryString());

    }

    public static void main6(String[] args) {

        Restriction r = new Restriction("code", 1L);
        r.setCastAs("string");

//        QueryBuilder queryBuilder = new QueryBuilder(null)
//                .from(Object.class, "o")
//                .memberOf("Joe", "names")
//                .notMemberOf("Joe", "names")
//                .add(r)
//                .innerJoin("outro a")
//                .debug();
        QueryBuilder queryBuilder = new QueryBuilder(null)
                .from(Object.class, "o")
                .isNotNull("name")
                .isNull("name2")
                .isNotEmpty("name3")
                .isEmpty("name4")
                .add("name5", RestrictionType.NULL, false)
                .add("name6", RestrictionType.EMPTY, false)
                .add("name7", RestrictionType.NOT_NULL, false)
                .add("name8", RestrictionType.NOT_EMPTY, false)
                .debug();

        queryBuilder.getResultList();

        System.out.println(queryBuilder.getQueryString());

    }

    public static void main2(String[] args) {

        //Caso 1
        //FROM class WHERE  nome = 'MARIA' OR nome = 'JOSE' OR status = true
        Restrictions restrictions = new Restrictions();

        //solução 1
        restrictions.equals("nome", "MARIA")
                .or()
                .equals("nome", "JOSE")
                .or()
                .equals("status", true)
                .or()
                .addQueryString("dataNascimento IS NULL OR dataNascimento > dataCadastro");

        printQueryString(restrictions);

        //Caso 2
        //FROM class WHERE  (nome = 'MARIA' AND status = true) OR (code = '123') 
        restrictions = new Restrictions();

        //em cadeia
        restrictions.startGroup()
                .notEquals("nome", "MARIA").equals("status", true)
                .endGroup()
                .or()
                .equals("code", "123");

        printQueryString(restrictions);

        //Caso 3
        //FROM class WHERE  (nome = 'MARIA' OR nome = 'JOSE') AND (code = '123' OR code = '321') AND status IS NOT NULL
        //em cadeia
        restrictions = new Restrictions();
        restrictions
                .startGroup()
                .equals("nome", "MARIA").or().equals("nome", "JOSE")
                .endGroup()
                .startGroup()
                .equals("code", "123").or().equals("code", "321")
                .endGroup()
                .isNotNull("status");

        printQueryString(restrictions);

        //Caso 4
        //FROM class WHERE  ((nome = 'MARIA' OR nome = 'JOSE') AND (cidade = 'TERESINA' OR cidade = 'BRASILIA')) AND (code = '123' OR code = '321')
        //em cadeia
        restrictions = new Restrictions();

        restrictions.startGroup()
                .startGroup()
                .equals("nome", "MARIA").or().equals("nome", "JOSE")
                .endGroup()
                .startGroup()
                .equals("cidade", "TERESINA").or().equals("cidade", "BRASILIA")
                .endGroup()
                .endGroup()
                .startGroup()
                .equals("code", "123").or().equals("code", "321")
                .endGroup();

        printQueryString(restrictions);

        //Caso 5
        //FROM class WHERE  nome = 'MARIA' AND ativo = true AND status IN(?)
        restrictions = new Restrictions();

        //em cadeia
        restrictions
                .equals("nome", "MARIA")
                .equals("nome", "MARIA")
                .in("status", true);

        printQueryString(restrictions);

    }
}
