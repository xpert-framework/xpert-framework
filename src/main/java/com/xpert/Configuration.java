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
        return auditingImplClass != null && metadataImplClass != null;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactoryClass == null) {
            return null;
        }
        try {
            return (EntityManagerFactory) entityManagerFactoryClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static EntityManagerFactory getAuditEntityManagerFactory() {
        if (auditEntityManagerFactoryClass == null) {
            return null;
        }
        try {
            return (EntityManagerFactory) auditEntityManagerFactoryClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AbstractAuditing getAbstractAuditing() {
        if (auditingImplClass == null) {
            return null;
        }
        try {
            return (AbstractAuditing) auditingImplClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AbstractMetadata getAbstractMetadata() {
        if (metadataImplClass == null) {
            return null;
        }
        try {
            return (AbstractMetadata) metadataImplClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static AbstractAuditingListener getAuditingListener() {
        if (auditingListenerClass == null) {
            return null;
        }
        try {
            return (AbstractAuditingListener) auditingListenerClass.newInstance();
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
                    if (children.item(temp).getNodeName().equals("auditing")) {
                        Element element = (Element) node;
                        try {
                            String tagValue = getTagValue("auditing-impl", element);
                            if (tagValue != null && !tagValue.isEmpty()) {
                                auditingImplClass = Class.forName(tagValue, true, Thread.currentThread().getContextClassLoader());
                                logger.log(Level.INFO, "Found AuditingImpl: {0}", auditingImplClass.getName());
                            }
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        try {
                            String tagValue = getTagValue("metadata-impl", element);
                            if (tagValue != null && !tagValue.isEmpty()) {
                                metadataImplClass = Class.forName(tagValue, true, Thread.currentThread().getContextClassLoader());
                                logger.log(Level.INFO, "Found MetadataImpl: {0}", metadataImplClass.getName());
                            }
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        try {
                            String tagValue = getTagValue("auditing-listener", element);
                            if (tagValue != null && !tagValue.isEmpty()) {
                                auditingListenerClass = Class.forName(tagValue, true, Thread.currentThread().getContextClassLoader());
                                logger.log(Level.INFO, "Found AuditingListener: {0}", auditingListenerClass.getName());
                            }
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (children.item(temp).getNodeName().equals("entity-manager-factory")) {
                        try {
                            entityManagerFactoryClass = Class.forName(children.item(temp).getTextContent(), true, Thread.currentThread().getContextClassLoader());
                            logger.log(Level.INFO, "Found EntityManagerFactory: {0}", entityManagerFactoryClass.getName());
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (children.item(temp).getNodeName().equals("audit-entity-manager-factory")) {
                        try {
                            auditEntityManagerFactoryClass = Class.forName(children.item(temp).getTextContent(), true, Thread.currentThread().getContextClassLoader());
                            logger.log(Level.INFO, "Found AuditEntityManagerFactory: {0}", auditEntityManagerFactoryClass.getName());
                        } catch (ClassNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (children.item(temp).getNodeName().equals("resource-bundle")) {
                        bundleName = children.item(temp).getTextContent();
                        logger.log(Level.INFO, "Found ResourceBundle: {0}", bundleName);
                    }
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

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

    public static Class getAuditingImplClass() {
        return auditingImplClass;
    }

    public static Class getMetadataImplClass() {
        return metadataImplClass;
    }

    public static Class getAuditingListenerClass() {
        return auditingListenerClass;
    }

    public static Class getEntityManagerFactoryClass() {
        return entityManagerFactoryClass;
    }

    public static Class getAuditEntityManagerFactoryClass() {
        return auditEntityManagerFactoryClass;
    }

    public static String getBundleName() {
        return bundleName;
    }

}
