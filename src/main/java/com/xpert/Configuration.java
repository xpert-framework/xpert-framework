package com.xpert;

import com.xpert.audit.AbstractAuditingListener;
import com.xpert.audit.QueryAuditPersisterFactory;
import com.xpert.audit.model.AbstractAuditing;
import com.xpert.audit.model.AbstractMetadata;
import com.xpert.audit.model.AbstractQueryAuditing;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import javax.xml.XMLConstants;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * xpert-framework Configuration class. This class acess xpert-config.xml and
 * its possible to retrieve the value with static methods.
 *
 * @author ayslan
 */
public class Configuration {

    private Configuration() {
    }

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    public static final String CONFIG_LOCATION = "/WEB-INF/xpert-config.xml";
    private static Class auditingImplClass;
    private static Class metadataImplClass;
    private static Class auditingListenerClass;
    private static Class entityManagerFactoryClass;
    private static Class auditEntityManagerFactoryClass;
    private static String bundleName;
    private static Class queryAuditingImplClass;
    private static Class queryAuditPersisterFactoryImplClass;

    public static EntityManager getEntityManager() {
        try {
            EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
            if (entityManagerFactory == null) {
                throw new RuntimeException("[xpert-framework] No EntityManagerFactory defined in xpert-config.xml");
            }
            return entityManagerFactory.getEntityManager();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static EntityManager getAuditEntityManager() {
        try {
            EntityManagerFactory entityManagerFactory = getAuditEntityManagerFactory();
            if (entityManagerFactory == null) {
                throw new RuntimeException("[xpert-framework] No AuditEntityManagerFactory defined in xpert-config.xml");
            }
            return entityManagerFactory.getEntityManager();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static boolean isAudit() {
        return auditingImplClass != null && metadataImplClass != null;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return (EntityManagerFactory) newInstance(entityManagerFactoryClass);
    }

    public static EntityManagerFactory getAuditEntityManagerFactory() {
        return (EntityManagerFactory) newInstance(auditEntityManagerFactoryClass);
    }

    public static AbstractAuditing getAbstractAuditing() {
        return (AbstractAuditing) newInstance(auditingImplClass);
    }

    public static AbstractMetadata getAbstractMetadata() {
        return (AbstractMetadata) newInstance(metadataImplClass);
    }

    public static AbstractQueryAuditing getAbstractQueryAuditing() {
        if (queryAuditingImplClass == null) {
            throw new RuntimeException("[xpert-framework] No 'query-auditing-impl' defined in xpert-config.xml");
        }
        return (AbstractQueryAuditing) newInstance(queryAuditingImplClass);
    }

    public static AbstractAuditingListener getAuditingListener() {
        return (AbstractAuditingListener) newInstance(auditingListenerClass);
    }

    public static QueryAuditPersisterFactory getQueryAuditPersisterFactory() {
        if (queryAuditPersisterFactoryImplClass == null) {
            throw new RuntimeException("[xpert-framework] No 'query-audit-persister-factory' defined in xpert-config.xml");
        }
        return (QueryAuditPersisterFactory) newInstance(queryAuditPersisterFactoryImplClass);
    }

    public static Object newInstance(Class clazz) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void init(ServletContext servletContext) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            builder = dbFactory.newDocumentBuilder();
            InputStream inputStream = servletContext.getResourceAsStream(CONFIG_LOCATION);

            if (inputStream == null) {
                logger.log(Level.INFO, "xpert-config.xml not found in WEB-INF");
                return;
            }

            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();
            NodeList children = root.getChildNodes();

            for (int temp = 0; temp < children.getLength(); temp++) {
                Node node = children.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nomeName = children.item(temp).getNodeName();
                    if (nomeName.equals("auditing")) {
                        Element element = (Element) node;
                        auditingImplClass = getClass("auditing-impl", element);
                        metadataImplClass = getClass("metadata-impl", element);
                        auditingListenerClass = getClass("auditing-listener", element);
                        queryAuditingImplClass = getClass("query-auditing-impl", element);
                        queryAuditPersisterFactoryImplClass = getClass("query-audit-persister-factory", element);
                    }
                    if (nomeName.equals("entity-manager-factory")) {
                        try {
                            entityManagerFactoryClass = getClass(node.getTextContent());
                            logger.log(Level.INFO, "[xpert-config.xml] Found entity-manager-factory: {0}", entityManagerFactoryClass.getName());
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (nomeName.equals("audit-entity-manager-factory")) {
                        try {
                            auditEntityManagerFactoryClass = getClass(node.getTextContent());
                            logger.log(Level.INFO, "[xpert-config.xml] Found audit-entity-manager-factory: {0}", auditEntityManagerFactoryClass.getName());
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (nomeName.equals("resource-bundle")) {
                        bundleName = children.item(temp).getTextContent();
                        logger.log(Level.INFO, "[xpert-config.xml] Found resource-bundle: {0}", bundleName);
                    }
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    private static Class getClass(String className) throws ClassNotFoundException {
        return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
    }

    private static Class getClass(String tag, Element element) {
        try {
            String tagValue = getTagValue(tag, element);
            if (tagValue != null && !tagValue.isEmpty()) {
                Class clazz = getClass(tagValue);
                logger.log(Level.INFO, "[xpert-config.xml] Found {0}: {1}", new Object[]{tag, clazz.getName()});
                return clazz;
            }
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            nodeList = nodeList.item(0).getChildNodes();
            if (nodeList != null && nodeList.getLength() > 0) {
                Node value = nodeList.item(0);
                return value.getNodeValue();
            }
        }
        return null;
    }

    /**
     * Return a class instance of AuditingImpl
     *
     * @return
     */
    public static Class getAuditingImplClass() {
        return auditingImplClass;
    }

    /**
     * Return a class instance of AuditingImpl
     *
     * @return
     */
    public static Class getMetadataImplClass() {
        return metadataImplClass;
    }

    /**
     * Return a class instance of AuditingListener
     *
     * @return
     */
    public static Class getAuditingListenerClass() {
        return auditingListenerClass;
    }

    /**
     * Return a class instance of EntityManagerFactory
     *
     * @return
     */
    public static Class getEntityManagerFactoryClass() {
        return entityManagerFactoryClass;
    }

    /**
     * Return a class instance of AuditEntityManagerFactory
     *
     * @return
     */
    public static Class getAuditEntityManagerFactoryClass() {
        return auditEntityManagerFactoryClass;
    }

    /**
     * Return a class instance of QueryAuditingImpl
     *
     * @return
     */
    public static Class getQueryAuditingImplClass() {
        return queryAuditingImplClass;
    }

    /**
     * Return a class instance of QueryAuditPersisterFactoryImpl
     *
     * @return
     */
    public static Class getQueryAuditPersisterFactoryImplClass() {
        return queryAuditPersisterFactoryImplClass;
    }

    /**
     * Return the bundle name
     *
     * @return
     */
    public static String getBundleName() {
        return bundleName;
    }

}
