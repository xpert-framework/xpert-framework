package com.xpert;

import com.xpert.audit.AbstractAuditingListener;
import com.xpert.audit.model.AbstractAuditing;
import com.xpert.audit.model.AbstractMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ayslan
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    public static final String CONFIG_LOCATION = "/WEB-INF/xpert-config.xml";
    public static Class AUDITING_IMPL;
    public static Class METADATA_IMPL;
    public static Class AUDITING_LISTENER;
    public static Class ENTITY_MANAGER_FACTORY;
    public static Class AUDIT_ENTITY_MANAGER_FACTORY;
    public static String BUNDLE;

    public static EntityManager getEntityManager() {
        try {
            EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
            if (entityManagerFactory == null) {
                throw new RuntimeException("No EntityManagerFactory defined in xpert-config.xml");
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
                throw new RuntimeException("No AuditEntityManagerFactory defined in xpert-config.xml");
            }
            return entityManagerFactory.getEntityManager();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static boolean isAudit() {
        if (AUDITING_IMPL != null && METADATA_IMPL != null) {
            return true;
        }
        return false;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY == null) {
            return null;
        }
        try {
            return (EntityManagerFactory) ENTITY_MANAGER_FACTORY.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static EntityManagerFactory getAuditEntityManagerFactory() {
        if (AUDIT_ENTITY_MANAGER_FACTORY == null) {
            return null;
        }
        try {
            return (EntityManagerFactory) AUDIT_ENTITY_MANAGER_FACTORY.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AbstractAuditing getAbstractAuditing() {
        if (AUDITING_IMPL == null) {
            return null;
        }
        try {
            return (AbstractAuditing) AUDITING_IMPL.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AbstractMetadata getAbstractMetadata() {
        if (METADATA_IMPL == null) {
            return null;
        }
        try {
            return (AbstractMetadata) METADATA_IMPL.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AbstractAuditingListener getAuditingListener() {
        if (AUDITING_LISTENER == null) {
            return null;
        }
        try {
            return (AbstractAuditingListener) AUDITING_LISTENER.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void init(ServletContext servletContext) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
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
                    if (children.item(temp).getNodeName().equals("auditing")) {
                        Element element = (Element) node;
                        try {
                            String tagValue = getTagValue("auditing-impl", element);
                            if (tagValue != null && !tagValue.isEmpty()) {
                                AUDITING_IMPL = Class.forName(tagValue, true, Thread.currentThread().getContextClassLoader());
                                logger.log(Level.INFO, "Found AuditingImpl: {0}", AUDITING_IMPL.getName());
                            }
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        try {
                            String tagValue = getTagValue("metadata-impl", element);
                            if (tagValue != null && !tagValue.isEmpty()) {
                                METADATA_IMPL = Class.forName(tagValue, true, Thread.currentThread().getContextClassLoader());
                                logger.log(Level.INFO, "Found MetadataImpl: {0}", METADATA_IMPL.getName());
                            }
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        try {
                            String tagValue = getTagValue("auditing-listener", element);
                            if (tagValue != null && !tagValue.isEmpty()) {
                                AUDITING_LISTENER = Class.forName(tagValue, true, Thread.currentThread().getContextClassLoader());
                                logger.log(Level.INFO, "Found AuditingListener: {0}", AUDITING_LISTENER.getName());
                            }
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (children.item(temp).getNodeName().equals("entity-manager-factory")) {
                        try {
                            ENTITY_MANAGER_FACTORY = Class.forName(children.item(temp).getTextContent(), true, Thread.currentThread().getContextClassLoader());
                            logger.log(Level.INFO, "Found EntityManagerFactory: {0}", ENTITY_MANAGER_FACTORY.getName());
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (children.item(temp).getNodeName().equals("audit-entity-manager-factory")) {
                        try {
                            AUDIT_ENTITY_MANAGER_FACTORY = Class.forName(children.item(temp).getTextContent(), true, Thread.currentThread().getContextClassLoader());
                            logger.log(Level.INFO, "Found AuditEntityManagerFactory: {0}", AUDIT_ENTITY_MANAGER_FACTORY.getName());
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (children.item(temp).getNodeName().equals("resource-bundle")) {
                        BUNDLE = children.item(temp).getTextContent();
                        logger.log(Level.INFO, "Found ResourceBundle: {0}", BUNDLE);
                    }
                }
            }

        } catch (SAXException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            nodeList = nodeList.item(0).getChildNodes();
            if (nodeList != null && nodeList.getLength() > 0) {
                Node value = (Node) nodeList.item(0);
                return value.getNodeValue();
            }
        }
        return null;
    }
}
