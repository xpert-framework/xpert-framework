package com.xpert.maker;

import com.xpert.faces.bootstrap.BootstrapVersion;
import com.xpert.faces.primefaces.PrimeFacesVersion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import com.xpert.Constants;
import com.xpert.utils.SwingUtils;
import java.awt.event.MouseListener;
import javax.swing.JTextArea;

/**
 *
 * @author ayslan
 */
public class MakerSwingFrame extends javax.swing.JFrame {

    private static final String JAVA_PROJECT_PREFFIX = File.separator + "java";
    private static final Logger logger = Logger.getLogger(MakerSwingFrame.class.getName());
    private static final Color BLUE = new Color(66, 139, 202);
    private BeanConfiguration beanConfiguration;
    private ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    private File lastFile;

    public Class getClassManagedBean() {
        return null;
    }

    public String getResourceBundle() {
        return null;
    }

    public String getDefaultPackage() {
        return null;
    }

    public String getDefaultTemplatePath() {
        return null;
    }

    public String getDefaultResourceBundle() {
        return null;
    }

    public String getDefaultBaseDAOImpl() {
        return null;
    }

    public String getManagedBeanSuffix() {
        return null;
    }

    public String getDatePattern() {
        return BeanCreator.DEFAULT_DATE_PATTERN;
    }

    public String getTimePattern() {
        return BeanCreator.DEFAULT_TIME_PATTERN;
    }

    public String getBusinessObjectSuffix() {
        return null;
    }

    public PrimeFacesVersion getPrimeFacesVersion() {
        return PrimeFacesVersion.VERSION_3;
    }

    public BootstrapVersion getBootstrapVersion() {
        return null;
    }

    public boolean isUseCDIBeans() {
        return false;
    }

    public boolean isGeneratesSecurityArea() {
        return true;
    }

    public boolean isMaskCalendar() {
        return true;
    }
    
    public boolean isHideIdInRequest() {
        return false;
    }

    /**
     * Creates new form MakerMainFrame
     */
    public MakerSwingFrame() {
        initComponents();
        initCustomLayout();
        initFromConfiguration();
    }

    private void addTootipTextWithLocations(final JTextField textField, final String description) {
        textField.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                StringBuilder text = new StringBuilder();
                text.append("<html>").append(description).append("<br/><br/>");
                text.append("<b>Location: </b>");
                text.append("'").append(textField.getText()).append("'");
                if(textField.getText() != null && !textField.getText().trim().isEmpty() && !new File(textField.getText()).exists()){
                    text.append("<span style=\"color: red\"> (Not found)</span>");
                }
                text.append("</html>");
                textField.setToolTipText(text.toString());
            }

            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public final void initCustomLayout() {

        buttonCreateClasses.setOpaque(true);
        buttonSearchClasses.setOpaque(true);
        buttonSelectAll.setOpaque(true);
        buttonSelectBO.setOpaque(true);
        buttonSelectDAO.setOpaque(true);
        buttonSelectDAOImpl.setOpaque(true);
        buttonSelectMB.setOpaque(true);
        buttonSelectNone.setOpaque(true);
        buttonSelectView.setOpaque(true);
        buttonSelectMessageBundle.setOpaque(true);
        buttonSelectClassMB.setOpaque(true);
        
        buttonTab1Next.setOpaque(true);
        buttonTab2Back.setOpaque(true);
        buttonTab2Next.setOpaque(true);
        buttonTab3Back.setOpaque(true);
        //copy buttons
        buttonCopyClassBean.setOpaque(true);
        buttonCopyI18n.setOpaque(true);
        buttonCopyLog.setOpaque(true);
        buttonCopySecurity.setOpaque(true);
        //open buttons
        buttonOpenClassBean.setOpaque(true);
        buttonOpenI18n.setOpaque(true);
        buttonOpenLog.setOpaque(true);
        buttonOpenSecurity.setOpaque(true);

        createLabelTabbedPanel("Select Classes", 0);
        createLabelTabbedPanel("Project Configuration", 1);
        createLabelTabbedPanel("Create Classes", 2);

        //create links
        SwingUtils.createHiperLink(labelLinkDocs, "https://code.google.com/p/xpert-framework/wiki/Download?tm=2", "https://code.google.com/p/xpert-framework/wiki/Download?tm=2");
        SwingUtils.createHiperLink(labelLinkShowcase, "http://showcase.xpertsistemas.com.br/", "http://showcase.xpertsistemas.com.br/");
        SwingUtils.createHiperLink(labelLinkXpertSistemas, "http://www.xpertsistemas.com.br/", "http://www.xpertsistemas.com.br/");

        addTootipTextWithLocations(textClassMB, "Directory of Class Managed Bean (ClassMB)");
        addTootipTextWithLocations(textResourceBundleLocation, "Directory of default messages resource bundles");
        addTootipTextWithLocations(textBusinessObject, "Directory of Business Object");
        addTootipTextWithLocations(textDAO, "Directory of DAO interface (Data Acess Object)");
        addTootipTextWithLocations(textDAOImpl, "Directory of DAO implmentation (Data Acess Object)");
        addTootipTextWithLocations(textManagedBean, "XHTML directory");
        addTootipTextWithLocations(textView, "Directory of Business Object");
    }

    private void loadClassManagedBeanText() {
        Class classMB = getClassManagedBean();
        if (classMB != null) {
            checkBoxUpdateClassMB.setSelected(true);
            String packageLocation = classMB.getPackage().getName().replace(".", File.separator);
            String absolutePath = new java.io.File(".").getAbsolutePath();
            if (absolutePath.endsWith(".")) {
                absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
            }
            //absolutepath + src/main/java/ + package + className +.java
            String classMBLocation = absolutePath
                    + "src" + File.separator + "main" + File.separator + "java" + File.separator
                    + packageLocation + File.separator + classMB.getSimpleName() + ".java";
            textClassMB.setText(classMBLocation);
        }
        checkCheckBoxStates();

    }

    private void loadResourceBundleText() {
        String resourceBundle = getResourceBundle();
        if (resourceBundle != null && !resourceBundle.isEmpty()) {
            checkBoxUpdateMessageBundle.setSelected(true);
            String absolutePath = new java.io.File(".").getAbsolutePath();
            if (absolutePath.endsWith(".")) {
                absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
            }
            if (resourceBundle.startsWith("/")) {
                resourceBundle = resourceBundle.substring(1, resourceBundle.length());
            }
            String resourceLocation = absolutePath + resourceBundle.replace("/", File.separator);
            textResourceBundleLocation.setText(resourceLocation);
        }
        checkCheckBoxStates();

    }

    private void createLabelTabbedPanel(String title, int index) {

        JLabel labelTab = new JLabel(title);
        labelTab.setPreferredSize(new Dimension(130, 30));
        labelTab.setForeground(Color.WHITE);
        labelTab.setHorizontalAlignment(SwingConstants.CENTER);
        tabbedPanelMain.setOpaque(true);
        tabbedPanelMain.setTabComponentAt(index, labelTab);
    }

    public static void run(final MakerSwingFrame maker) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                maker.center();
                maker.setVisible(true);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        run(new MakerSwingFrame());
    }

    public void searchClasses() {
        try {
            ArrayList<Class<?>> allClasses = ClassEnumerator.getClassesForPackage(textPackageName.getText());
            classes = new ArrayList<Class<?>>();
            for (Class entity : allClasses) {
                if (!entity.isEnum() && !entity.isInterface()) {
                    classes.add(entity);
                }
            }
            listClasses.setListData(classes.toArray(new Class[classes.size()]));
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Message: " + ex.getMessage() + ". See java log for details", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void selectAll() {
        if (classes != null && !classes.isEmpty()) {
            int[] indices = new int[classes.size()];
            for (int i = 0; i < classes.size(); i++) {
                indices[i] = i;
            }
            listClasses.setSelectedIndices(indices);
        }
    }

    public void selectNone() {
        listClasses.setSelectedIndices(new int[0]);
    }

    public boolean validateField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, fieldName + " is required", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean validateConfiguration() {

        if (!validateField(beanConfiguration.getManagedBeanLocation(), "ManagedBean location")) {
            return false;
        }
        if (!validateField(beanConfiguration.getManagedBean(), "ManagedBean package")) {
            return false;
        }
        if (!validateField(beanConfiguration.getBusinessObjectLocation(), "Business Object (BO) location")) {
            return false;
        }
        if (!validateField(beanConfiguration.getBusinessObject(), "Business Object (BO) package")) {
            return false;
        }
        if (!validateField(beanConfiguration.getDaoLocation(), "DAO location")) {
            return false;
        }
        if (!validateField(beanConfiguration.getDao(), "DAO package")) {
            return false;
        }
        if (!validateField(beanConfiguration.getDaoImplLocation(), "DAO Implementation location")) {
            return false;
        }
        if (!validateField(beanConfiguration.getDaoImpl(), "DAO Implementation package")) {
            return false;
        }
        if (!validateField(beanConfiguration.getViewLocation(), "View location")) {
            return false;
        }
        if (!validateField(beanConfiguration.getTemplate(), "Facelets Template")) {
            return false;
        }

        return true;
    }

    public void generate() {

        Object[] selectedClasses = listClasses.getSelectedValues();

        if (selectedClasses == null || selectedClasses.length == 0) {
            JOptionPane.showMessageDialog(this, "No Classes Selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Class> classesList = new ArrayList<Class>();
        for (Object object : selectedClasses) {
            classesList.add((Class) object);
        }

        beanConfiguration = new BeanConfiguration();
        beanConfiguration.setPrimeFacesVersion((PrimeFacesVersion) comboPrimeFacesVersion.getSelectedItem());
        if (comboBootstrapVersion.getSelectedItem() != null && comboBootstrapVersion.getSelectedItem() instanceof BootstrapVersion) {
            beanConfiguration.setBootstrapVersion((BootstrapVersion) comboBootstrapVersion.getSelectedItem());
        }
        beanConfiguration.setTemplate(textTemplatePath.getText());
        beanConfiguration.setAuthor(textAuthor.getText());
        beanConfiguration.setBaseDAO(textBaseDAOImpl.getText());
        beanConfiguration.setBusinessObject(textPackageBO.getText());
        beanConfiguration.setManagedBean(textPackageMB.getText());
        beanConfiguration.setDao(textPackageDAO.getText());
        beanConfiguration.setDaoImpl(textPackageDAOImpl.getText());
        beanConfiguration.setResourceBundle(textResourceBundle.getText());
        //location
        beanConfiguration.setManagedBeanLocation(textManagedBean.getText());
        beanConfiguration.setBusinessObjectLocation(textBusinessObject.getText());
        beanConfiguration.setDaoLocation(textDAO.getText());
        beanConfiguration.setDaoImplLocation(textDAOImpl.getText());
        beanConfiguration.setViewLocation(textView.getText());
        //suffix/preffix
        beanConfiguration.setManagedBeanSuffix(textManagedBeanSuffix.getText());
        beanConfiguration.setBusinessObjectSuffix(textBusinessObjectSuffix.getText());
        beanConfiguration.setUseCDIBeans(checkUseCDIBeans.isSelected());
        beanConfiguration.setGeneratesSecurityArea(checkGeneratesSecurityArea.isSelected());
        beanConfiguration.setMaskCalendar(checkMaskCalendar.isSelected());
        beanConfiguration.setDatePattern(textDatePattern.getText());
        beanConfiguration.setTimePattern(textTimePattern.getText());
        beanConfiguration.setHideIdInRequest(checkHideIdInRequest.isSelected());
        //class MB
        beanConfiguration.setClassManagedBeanLocation(textClassMB.getText());
        beanConfiguration.setResourceBundleLocation(textResourceBundleLocation.getText());
        try {
            PersistenceMappedBean persistenceMappedBean = new PersistenceMappedBean(null);
            List<MappedBean> mappedBeans = persistenceMappedBean.getMappedBeans(classesList, beanConfiguration);
            textAreaSecurityGeneration.setText(SecurityCRUDGenerator.create(classesList, getViewForSecurity(beanConfiguration)));
            StringBuilder logBuilder = new StringBuilder();
            BeanCreator.writeBean(mappedBeans, beanConfiguration, logBuilder);
            //classMB
            if (checkBoxUpdateClassMB.isSelected()) {
                BeanCreator.writeClassManagedBean(classesList, beanConfiguration);
            }
            //message bundles
            if (checkBoxUpdateMessageBundle.isSelected()) {
                BeanCreator.writeResourceBundle(mappedBeans, beanConfiguration);
            }
            if (beanConfiguration.isClassManagedBeanGenerated()) {
                textAreaClassBean.setEnabled(false);
                textAreaClassBean.setText("File " + beanConfiguration.getClassManagedBeanLocation() + " updated!");
            } else {
                textAreaClassBean.setEnabled(true);
                textAreaClassBean.setText(BeanCreator.getClassBean(classesList, beanConfiguration));
            }
            if (beanConfiguration.isResourceBundleGenerated()) {
                textAreaI18n.setEnabled(false);
                textAreaI18n.setText("File " + beanConfiguration.getResourceBundleLocation() + " updated!");
            } else {
                textAreaI18n.setEnabled(true);
                textAreaI18n.setText(BeanCreator.getI18N(mappedBeans));
            }
            textAreaLog.setText(logBuilder.toString());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Message: " + ex.getMessage() + ". See java log for details", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String getViewForSecurity(BeanConfiguration beanConfiguration) {
        String viewPath = "";
        if (beanConfiguration.getViewLocation() != null && !beanConfiguration.getViewLocation().isEmpty()) {
            if (beanConfiguration.getViewLocation().contains("webapp")) {
                viewPath = beanConfiguration.getViewLocation().substring(beanConfiguration.getViewLocation().lastIndexOf("webapp") + 6, beanConfiguration.getViewLocation().length());
                viewPath = viewPath.replace(File.separator, "/");
            }
        }
        return viewPath;
    }

    public void showFileChooser(JTextField textSelection, JTextField textPackage) {
        JFileChooser chooser = new JFileChooser();
        if (textSelection != null && textSelection.getText() != null && !textSelection.getText().isEmpty() && new File(textSelection.getText()).exists()) {
            chooser.setCurrentDirectory(new File(textSelection.getText()));
        } else if (lastFile != null) {
            chooser.setCurrentDirectory(lastFile);
        } else {
            chooser.setCurrentDirectory(new java.io.File("."));
        }
        chooser.setDialogTitle("Select a Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (textSelection != null) {
                textSelection.setText(path);
            }
            if (textPackage != null) {
                int javaPathIndex = path.lastIndexOf(JAVA_PROJECT_PREFFIX);
                if (javaPathIndex > 0) {
                    int fullIndex = javaPathIndex + JAVA_PROJECT_PREFFIX.length() + 1;
                    if (path.length() > fullIndex) {
                        String javaPath = path.substring(fullIndex, path.length());
                        textPackage.setText(javaPath.replace(File.separator, "."));
                    }
                }
            }
            lastFile = chooser.getSelectedFile();
        }
    }

    public final void initFromConfiguration() {
        if (getDefaultPackage() != null) {
            textPackageName.setText(getDefaultPackage());
        }
        if (getDefaultTemplatePath() != null) {
            textTemplatePath.setText(getDefaultTemplatePath());
        } else {
            textTemplatePath.setText(BeanCreator.DEFAULT_TEMPLATE);
        }
        if (getDefaultResourceBundle() != null) {
            textResourceBundle.setText(getDefaultResourceBundle());
        } else {
            textResourceBundle.setText(BeanCreator.DEFAULT_RESOURCE_BUNDLE);
        }
        if (getDefaultBaseDAOImpl() != null) {
            textBaseDAOImpl.setText(getDefaultBaseDAOImpl());
        }
        if (getManagedBeanSuffix() != null) {
            textManagedBeanSuffix.setText(getManagedBeanSuffix());
        } else {
            textManagedBeanSuffix.setText(BeanCreator.SUFFIX_MANAGED_BEAN);
        }
        if (getBusinessObjectSuffix() != null) {
            textBusinessObjectSuffix.setText(getBusinessObjectSuffix());
        } else {
            textBusinessObjectSuffix.setText(BeanCreator.SUFFIX_BUSINESS_OBJECT);
        }
        if (getDatePattern() != null) {
            textDatePattern.setText(getDatePattern());
        }
        if (getTimePattern() != null) {
            textTimePattern.setText(getTimePattern());
        }
        checkUseCDIBeans.setSelected(isUseCDIBeans());
        checkGeneratesSecurityArea.setSelected(isGeneratesSecurityArea());
        checkMaskCalendar.setSelected(isMaskCalendar());
        checkHideIdInRequest.setSelected(isHideIdInRequest());
        textAuthor.setText(System.getProperty("user.name"));
        loadPrimefacesCombo();
        loadBootstrapCombo();
        loadClassManagedBeanText();
        loadResourceBundleText();

    }

    private void loadPrimefacesCombo() {
        PrimeFacesVersion[] versions = PrimeFacesVersion.values();
        comboPrimeFacesVersion.removeAllItems();
        for (PrimeFacesVersion version : versions) {
            comboPrimeFacesVersion.addItem(version);
        }
        comboPrimeFacesVersion.setSelectedItem(getPrimeFacesVersion());
        comboPrimeFacesVersion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(((PrimeFacesVersion) value).getDescription());
                }
                return this;
            }
        });
    }

    private void loadBootstrapCombo() {
        BootstrapVersion[] versions = BootstrapVersion.values();
        comboBootstrapVersion.removeAllItems();
        comboBootstrapVersion.addItem(null);
        for (BootstrapVersion version : versions) {
            comboBootstrapVersion.addItem(version);
        }
        comboBootstrapVersion.setSelectedItem(getBootstrapVersion());
        comboBootstrapVersion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null && value instanceof BootstrapVersion) {
                    setText(((BootstrapVersion) value).getDescription());
                } else {
                    setText("Don't use");
                }
                return this;
            }
        });
    }

    public void center() {
        SwingUtils.center(this);
    }

    public void checkCheckBoxStates() {
        //class MB
        textClassMB.setEnabled(checkBoxUpdateClassMB.isSelected());
        buttonSelectClassMB.setEnabled(checkBoxUpdateClassMB.isSelected());
        //bundles
        textResourceBundleLocation.setEnabled(checkBoxUpdateMessageBundle.isSelected());
        buttonSelectMessageBundle.setEnabled(checkBoxUpdateMessageBundle.isSelected());
    }

    private void openContentNewWindow(JTextArea textArea) {
        DialogShowText dialogShowText = new DialogShowText(this, true);
        dialogShowText.getTextAreaCopy().setText(textArea.getText());
        dialogShowText.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMain = new javax.swing.JPanel();
        tabbedPanelMain = new javax.swing.JTabbedPane();
        panelSelectClasses = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        labelPackageName = new javax.swing.JLabel();
        textPackageName = new javax.swing.JTextField();
        buttonSearchClasses = new javax.swing.JButton();
        scrollPaneSelectClasses = new javax.swing.JScrollPane();
        listClasses = new javax.swing.JList();
        labelSelectClasses = new javax.swing.JLabel();
        buttonSelectAll = new javax.swing.JButton();
        buttonSelectNone = new javax.swing.JButton();
        labelSelectClasses1 = new javax.swing.JLabel();
        buttonTab1Next = new javax.swing.JButton();
        panelClassesConfiguration = new javax.swing.JPanel();
        labelXpertMaker = new javax.swing.JLabel();
        labelWatermakerXpertSistemas = new javax.swing.JLabel();
        labelDocs = new javax.swing.JLabel();
        labelLinkDocs = new javax.swing.JLabel();
        labelLinkShowcase = new javax.swing.JLabel();
        labelShowcase = new javax.swing.JLabel();
        labelIntroPtBr = new javax.swing.JLabel();
        labelIntroEn = new javax.swing.JLabel();
        labelXpertSistemas = new javax.swing.JLabel();
        labelLinkXpertSistemas = new javax.swing.JLabel();
        panelStep1 = new javax.swing.JPanel();
        labelStep1 = new javax.swing.JLabel();
        labelStep1Detail = new javax.swing.JLabel();
        panelConfiguration = new javax.swing.JPanel();
        panelOthers = new javax.swing.JPanel();
        labelResourceBundle = new javax.swing.JLabel();
        textResourceBundle = new javax.swing.JTextField();
        labelAuthor = new javax.swing.JLabel();
        textAuthor = new javax.swing.JTextField();
        labelBaseDAOImpl = new javax.swing.JLabel();
        textBaseDAOImpl = new javax.swing.JTextField();
        labelMBSuffix = new javax.swing.JLabel();
        textManagedBeanSuffix = new javax.swing.JTextField();
        textBusinessObjectSuffix = new javax.swing.JTextField();
        labelBOSuffix = new javax.swing.JLabel();
        checkGeneratesSecurityArea = new javax.swing.JCheckBox();
        checkUseCDIBeans = new javax.swing.JCheckBox();
        checkMaskCalendar = new javax.swing.JCheckBox();
        labelDatePattern = new javax.swing.JLabel();
        textDatePattern = new javax.swing.JTextField();
        labelTimePattern = new javax.swing.JLabel();
        textTimePattern = new javax.swing.JTextField();
        checkHideIdInRequest = new javax.swing.JCheckBox();
        panelJavaClassConfiguration = new javax.swing.JPanel();
        labelBOPackage = new javax.swing.JLabel();
        textPackageBO = new javax.swing.JTextField();
        textBusinessObject = new javax.swing.JTextField();
        buttonSelectBO = new javax.swing.JButton();
        labelBOLocation = new javax.swing.JLabel();
        labelDAOLocation = new javax.swing.JLabel();
        textDAO = new javax.swing.JTextField();
        buttonSelectDAO = new javax.swing.JButton();
        labelDAOPackage = new javax.swing.JLabel();
        textPackageDAO = new javax.swing.JTextField();
        labelDAOImplLocation = new javax.swing.JLabel();
        labelDAOImplPackage = new javax.swing.JLabel();
        buttonSelectDAOImpl = new javax.swing.JButton();
        textDAOImpl = new javax.swing.JTextField();
        textPackageDAOImpl = new javax.swing.JTextField();
        labelMBLocation = new javax.swing.JLabel();
        textManagedBean = new javax.swing.JTextField();
        buttonSelectMB = new javax.swing.JButton();
        labelMBPackage = new javax.swing.JLabel();
        textPackageMB = new javax.swing.JTextField();
        textClassMB = new javax.swing.JTextField();
        buttonSelectClassMB = new javax.swing.JButton();
        checkBoxUpdateClassMB = new javax.swing.JCheckBox();
        checkBoxUpdateMessageBundle = new javax.swing.JCheckBox();
        textResourceBundleLocation = new javax.swing.JTextField();
        buttonSelectMessageBundle = new javax.swing.JButton();
        panelViewConfiguration = new javax.swing.JPanel();
        labelXHTMLLocation = new javax.swing.JLabel();
        textView = new javax.swing.JTextField();
        buttonSelectView = new javax.swing.JButton();
        labelFaceletsTemplate = new javax.swing.JLabel();
        textTemplatePath = new javax.swing.JTextField();
        labelPrimeFacesVersion = new javax.swing.JLabel();
        comboPrimeFacesVersion = new javax.swing.JComboBox();
        labelBootstrapVersion = new javax.swing.JLabel();
        comboBootstrapVersion = new javax.swing.JComboBox();
        buttonTab2Back = new javax.swing.JButton();
        buttonTab2Next = new javax.swing.JButton();
        panelStep4 = new javax.swing.JPanel();
        labelStep4 = new javax.swing.JLabel();
        labelStep4Detail = new javax.swing.JLabel();
        panelStep2 = new javax.swing.JPanel();
        labelStep2 = new javax.swing.JLabel();
        labelStep2Detail = new javax.swing.JLabel();
        panelStep3 = new javax.swing.JPanel();
        labelStep3 = new javax.swing.JLabel();
        labelStep3Detail = new javax.swing.JLabel();
        panelCreateClasses = new javax.swing.JPanel();
        scrollPaneLog = new javax.swing.JScrollPane();
        textAreaLog = new javax.swing.JTextArea();
        scrollPaneClassBean = new javax.swing.JScrollPane();
        textAreaClassBean = new javax.swing.JTextArea();
        labelI18N = new javax.swing.JLabel();
        labelClassBean = new javax.swing.JLabel();
        scrollPaneI18N = new javax.swing.JScrollPane();
        textAreaSecurityGeneration = new javax.swing.JTextArea();
        panelStep5 = new javax.swing.JPanel();
        labelStep5 = new javax.swing.JLabel();
        labelStep5Detail = new javax.swing.JLabel();
        panelStep6 = new javax.swing.JPanel();
        labelStep6 = new javax.swing.JLabel();
        labelStep6Detail = new javax.swing.JLabel();
        panelStep7 = new javax.swing.JPanel();
        labelStep7 = new javax.swing.JLabel();
        labelStep7Detail = new javax.swing.JLabel();
        buttonTab3Back = new javax.swing.JButton();
        buttonCreateClasses = new javax.swing.JButton();
        panelStep8 = new javax.swing.JPanel();
        labelStep8 = new javax.swing.JLabel();
        labelStep8Detail = new javax.swing.JLabel();
        scrollPaneI18N1 = new javax.swing.JScrollPane();
        textAreaI18n = new javax.swing.JTextArea();
        buttonCopyI18n = new javax.swing.JButton();
        buttonCopyClassBean = new javax.swing.JButton();
        buttonCopySecurity = new javax.swing.JButton();
        buttonOpenI18n = new javax.swing.JButton();
        buttonOpenClassBean = new javax.swing.JButton();
        buttonOpenSecurity = new javax.swing.JButton();
        buttonCopyLog = new javax.swing.JButton();
        buttonOpenLog = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Xpert-framework. Version " + Constants.VERSION
        );
        setBackground(new java.awt.Color(255, 255, 255));

        jPanelMain.setBackground(new java.awt.Color(239, 239, 239));

        tabbedPanelMain.setBackground(BLUE);
        tabbedPanelMain.setForeground(new java.awt.Color(255, 255, 255));
        tabbedPanelMain.setFocusable(false);
        tabbedPanelMain.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N

        panelSelectClasses.setBackground(new java.awt.Color(237, 242, 253));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 0, 153))); // NOI18N

        labelPackageName.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelPackageName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelPackageName.setText("Package Name:");

        textPackageName.setToolTipText("Java package of your classes");

        buttonSearchClasses.setBackground(BLUE);
        buttonSearchClasses.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonSearchClasses.setForeground(new java.awt.Color(255, 255, 255));
        buttonSearchClasses.setBorderPainted(false);
        buttonSearchClasses.setLabel("Search Classes");
        buttonSearchClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchClassesActionPerformed(evt);
            }
        });

        listClasses.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        listClasses.setToolTipText("Select the classes to generate CRUD (Ctrl+click to select multiple)");
        scrollPaneSelectClasses.setViewportView(listClasses);

        labelSelectClasses.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelSelectClasses.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelSelectClasses.setText("Classes found in package:");

        buttonSelectAll.setBackground(BLUE);
        buttonSelectAll.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonSelectAll.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectAll.setToolTipText("Select all classes");
        buttonSelectAll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonSelectAll.setBorderPainted(false);
        buttonSelectAll.setLabel("All");
        buttonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectAllActionPerformed(evt);
            }
        });

        buttonSelectNone.setBackground(BLUE);
        buttonSelectNone.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonSelectNone.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectNone.setToolTipText("Remove selection");
        buttonSelectNone.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonSelectNone.setBorderPainted(false);
        buttonSelectNone.setLabel("None");
        buttonSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectNoneActionPerformed(evt);
            }
        });

        labelSelectClasses1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelSelectClasses1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelSelectClasses1.setText("Select:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(scrollPaneSelectClasses)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(labelSelectClasses1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonSelectAll, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonSelectNone, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(392, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(textPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSearchClasses))
                            .addComponent(labelPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 53, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(labelSelectClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPackageName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSearchClasses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSelectClasses)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneSelectClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSelectClasses1)
                    .addComponent(buttonSelectAll)
                    .addComponent(buttonSelectNone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27))
        );

        buttonTab1Next.setBackground(BLUE);
        buttonTab1Next.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonTab1Next.setForeground(new java.awt.Color(255, 255, 255));
        buttonTab1Next.setText("Next");
        buttonTab1Next.setToolTipText("");
        buttonTab1Next.setBorderPainted(false);
        buttonTab1Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTab1NextActionPerformed(evt);
            }
        });

        panelClassesConfiguration.setBackground(new java.awt.Color(255, 255, 255));
        panelClassesConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        labelXpertMaker.setBackground(new java.awt.Color(102, 204, 255));
        labelXpertMaker.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        labelXpertMaker.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelXpertMaker.setText("Xpert-framework Maker");
        labelXpertMaker.setOpaque(true);

        labelWatermakerXpertSistemas.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelWatermakerXpertSistemas.setForeground(new java.awt.Color(204, 204, 204));
        labelWatermakerXpertSistemas.setText("Xpert Sistemas");

        labelDocs.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelDocs.setText("Xpert-framework documentation:");

        labelLinkDocs.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelLinkDocs.setForeground(new java.awt.Color(51, 51, 255));
        labelLinkDocs.setText("https://code.google.com/p/xpert-framework/wiki/Download?tm=2");

        labelLinkShowcase.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelLinkShowcase.setForeground(new java.awt.Color(51, 51, 255));
        labelLinkShowcase.setText("http://showcase.xpertsistemas.com.br/");

        labelShowcase.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelShowcase.setText("Xpert-framework showcase:");

        labelIntroPtBr.setBackground(new java.awt.Color(241, 241, 241));
        labelIntroPtBr.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelIntroPtBr.setText("<html> O xpert-maker é um facilitador na geração<br/>  de CRUDs.<br/> A geração de código é baseada nas  <br/> classes modelos,  construídas a partir das <br/> especificações JPA e Bean Validation. </html>");
        labelIntroPtBr.setOpaque(true);

        labelIntroEn.setBackground(new java.awt.Color(241, 241, 241));
        labelIntroEn.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelIntroEn.setText("<html>\nThe xpert-maker is a facilitator in generating <br/> CRUD.<br/>\nCode generation is based on class models,<br/> built from the JPA\nand  <br/>Bean Validation specification.\n</html>");
        labelIntroEn.setOpaque(true);

        labelXpertSistemas.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelXpertSistemas.setText("Xpert Sistemas:");

        labelLinkXpertSistemas.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelLinkXpertSistemas.setForeground(new java.awt.Color(51, 51, 255));
        labelLinkXpertSistemas.setText("http://www.xpertsistemas.com.br/");

        javax.swing.GroupLayout panelClassesConfigurationLayout = new javax.swing.GroupLayout(panelClassesConfiguration);
        panelClassesConfiguration.setLayout(panelClassesConfigurationLayout);
        panelClassesConfigurationLayout.setHorizontalGroup(
            panelClassesConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClassesConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelClassesConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelLinkDocs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(labelXpertMaker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClassesConfigurationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(labelWatermakerXpertSistemas))
                    .addComponent(labelIntroPtBr)
                    .addComponent(labelIntroEn)
                    .addComponent(labelLinkXpertSistemas, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelClassesConfigurationLayout.createSequentialGroup()
                        .addGroup(panelClassesConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDocs)
                            .addComponent(labelShowcase)
                            .addComponent(labelLinkShowcase, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelXpertSistemas))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelClassesConfigurationLayout.setVerticalGroup(
            panelClassesConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClassesConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelXpertMaker, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelIntroEn, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelIntroPtBr, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelShowcase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLinkShowcase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelDocs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLinkDocs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelXpertSistemas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLinkXpertSistemas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(labelWatermakerXpertSistemas)
                .addContainerGap())
        );

        panelStep1.setBackground(new java.awt.Color(153, 204, 255));
        panelStep1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep1.setBackground(new java.awt.Color(51, 0, 204));
        labelStep1.setForeground(new java.awt.Color(255, 255, 255));
        labelStep1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep1.setText("1");
        labelStep1.setOpaque(true);

        labelStep1Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep1Detail.setText("Choose your java model classes, these classes must be JPA entities");

        javax.swing.GroupLayout panelStep1Layout = new javax.swing.GroupLayout(panelStep1);
        panelStep1.setLayout(panelStep1Layout);
        panelStep1Layout.setHorizontalGroup(
            panelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep1Layout.createSequentialGroup()
                .addComponent(labelStep1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelStep1Detail)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelStep1Layout.setVerticalGroup(
            panelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep1Detail))
        );

        javax.swing.GroupLayout panelSelectClassesLayout = new javax.swing.GroupLayout(panelSelectClasses);
        panelSelectClasses.setLayout(panelSelectClassesLayout);
        panelSelectClassesLayout.setHorizontalGroup(
            panelSelectClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSelectClassesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSelectClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelSelectClassesLayout.createSequentialGroup()
                        .addComponent(panelClassesConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelSelectClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelStep1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSelectClassesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonTab1Next, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelSelectClassesLayout.setVerticalGroup(
            panelSelectClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSelectClassesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSelectClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelSelectClassesLayout.createSequentialGroup()
                        .addComponent(panelStep1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelClassesConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonTab1Next)
                .addContainerGap(177, Short.MAX_VALUE))
        );

        tabbedPanelMain.addTab("Select Classes", panelSelectClasses);

        panelConfiguration.setBackground(new java.awt.Color(237, 242, 253));

        panelOthers.setBackground(new java.awt.Color(255, 255, 255));
        panelOthers.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 51, 102))); // NOI18N
        panelOthers.setPreferredSize(new java.awt.Dimension(723, 158));

        labelResourceBundle.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelResourceBundle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelResourceBundle.setLabelFor(textResourceBundle);
        labelResourceBundle.setText("Resource Bundle:");

        textResourceBundle.setToolTipText("I18N name of your resources in pages");

        labelAuthor.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelAuthor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelAuthor.setLabelFor(textAuthor);
        labelAuthor.setText("Author:");

        textAuthor.setToolTipText("Author to be write in java classes");

        labelBaseDAOImpl.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelBaseDAOImpl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelBaseDAOImpl.setLabelFor(textAuthor);
        labelBaseDAOImpl.setText("BaseDAOImpl:");

        textBaseDAOImpl.setToolTipText("The BaseDAOImpl class of your project");

        labelMBSuffix.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelMBSuffix.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMBSuffix.setLabelFor(textManagedBeanSuffix);
        labelMBSuffix.setText("Managed Bean Suffix:");

        textManagedBeanSuffix.setToolTipText("Managed Bean suffix. Example suffix \"MB\" for class Person generates \"PersonMB\"");

        textBusinessObjectSuffix.setToolTipText("Business Object suffix. Example suffix \"BO\" for class Person generates \"PersonBO\"");

        labelBOSuffix.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelBOSuffix.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelBOSuffix.setLabelFor(textBusinessObjectSuffix);
        labelBOSuffix.setText("Business Object Suffix:");

        checkGeneratesSecurityArea.setBackground(new java.awt.Color(255, 255, 255));
        checkGeneratesSecurityArea.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        checkGeneratesSecurityArea.setText("Generate securityArea (use acess control in forms)");

        checkUseCDIBeans.setBackground(new java.awt.Color(255, 255, 255));
        checkUseCDIBeans.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        checkUseCDIBeans.setText("Use CDI Beans (@Named instead of @ManagedBean)");
        checkUseCDIBeans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUseCDIBeansActionPerformed(evt);
            }
        });

        checkMaskCalendar.setBackground(new java.awt.Color(255, 255, 255));
        checkMaskCalendar.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        checkMaskCalendar.setLabel("Mask calendar (Primefaces < 5.x, don't put mask on p:calendar)");
        checkMaskCalendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkMaskCalendarActionPerformed(evt);
            }
        });

        labelDatePattern.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelDatePattern.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelDatePattern.setLabelFor(textDatePattern);
        labelDatePattern.setText("Date pattern:");

        textDatePattern.setToolTipText("Date pattern to be used in p:calendar");

        labelTimePattern.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelTimePattern.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTimePattern.setLabelFor(textTimePattern);
        labelTimePattern.setText("Time pattern:");

        textTimePattern.setToolTipText("Time pattern to be used in p:calendar");

        checkHideIdInRequest.setBackground(new java.awt.Color(255, 255, 255));
        checkHideIdInRequest.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        checkHideIdInRequest.setText("Hide the field \"id\" in request URL when editing a object");
        checkHideIdInRequest.setToolTipText("Hide the field \"id\" in request URL when editing a object");
        checkHideIdInRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkHideIdInRequestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelOthersLayout = new javax.swing.GroupLayout(panelOthers);
        panelOthers.setLayout(panelOthersLayout);
        panelOthersLayout.setHorizontalGroup(
            panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOthersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelOthersLayout.createSequentialGroup()
                        .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelOthersLayout.createSequentialGroup()
                                .addComponent(labelBaseDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(183, 183, 183)
                                .addComponent(labelResourceBundle, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelOthersLayout.createSequentialGroup()
                                .addComponent(textBaseDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textResourceBundle, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelOthersLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(textManagedBeanSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textBusinessObjectSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelOthersLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(labelMBSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelBOSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelOthersLayout.createSequentialGroup()
                        .addComponent(checkUseCDIBeans, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(checkHideIdInRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelOthersLayout.createSequentialGroup()
                        .addComponent(labelDatePattern, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelTimePattern, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOthersLayout.createSequentialGroup()
                        .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelOthersLayout.createSequentialGroup()
                                .addComponent(textDatePattern, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(textTimePattern, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(checkGeneratesSecurityArea, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(checkMaskCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(115, 115, 115)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        panelOthersLayout.setVerticalGroup(
            panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOthersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBaseDAOImpl)
                    .addComponent(labelResourceBundle)
                    .addComponent(labelAuthor)
                    .addComponent(labelMBSuffix)
                    .addComponent(labelBOSuffix))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textBaseDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textResourceBundle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textManagedBeanSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textBusinessObjectSuffix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOthersLayout.createSequentialGroup()
                        .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelDatePattern)
                            .addComponent(labelTimePattern))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textDatePattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textTimePattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(checkUseCDIBeans)
                            .addComponent(checkHideIdInRequest)))
                    .addGroup(panelOthersLayout.createSequentialGroup()
                        .addComponent(checkMaskCalendar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkGeneratesSecurityArea)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        panelJavaClassConfiguration.setBackground(new java.awt.Color(255, 255, 255));
        panelJavaClassConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 51, 102))); // NOI18N
        panelJavaClassConfiguration.setPreferredSize(new java.awt.Dimension(723, 85));

        labelBOPackage.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelBOPackage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelBOPackage.setLabelFor(textPackageBO);
        labelBOPackage.setText("Package:");

        textPackageBO.setToolTipText("Package name of Business Object");
        textPackageBO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textPackageBOActionPerformed(evt);
            }
        });

        textBusinessObject.setToolTipText("Your directory of Business Object");

        buttonSelectBO.setBackground(BLUE);
        buttonSelectBO.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectBO.setText("...");
        buttonSelectBO.setBorderPainted(false);
        buttonSelectBO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectBOActionPerformed(evt);
            }
        });

        labelBOLocation.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelBOLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelBOLocation.setLabelFor(textBusinessObject);
        labelBOLocation.setText("Business Object Location:");

        labelDAOLocation.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelDAOLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelDAOLocation.setLabelFor(textDAO);
        labelDAOLocation.setText("DAO (Interface) Location:");

        textDAO.setToolTipText("Your directory of DAO interface (Data Acess Object)");

        buttonSelectDAO.setBackground(BLUE);
        buttonSelectDAO.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectDAO.setText("...");
        buttonSelectDAO.setBorderPainted(false);
        buttonSelectDAO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectDAOActionPerformed(evt);
            }
        });

        labelDAOPackage.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelDAOPackage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelDAOPackage.setLabelFor(textPackageDAO);
        labelDAOPackage.setText("Package:");

        textPackageDAO.setToolTipText("Package name of DAO interface (Data Acess Object)");
        textPackageDAO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textPackageDAOActionPerformed(evt);
            }
        });

        labelDAOImplLocation.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelDAOImplLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelDAOImplLocation.setLabelFor(textDAOImpl);
        labelDAOImplLocation.setText("DAO (Implementation) Location:");

        labelDAOImplPackage.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelDAOImplPackage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelDAOImplPackage.setLabelFor(textPackageDAOImpl);
        labelDAOImplPackage.setText("Package:");

        buttonSelectDAOImpl.setBackground(BLUE);
        buttonSelectDAOImpl.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectDAOImpl.setText("...");
        buttonSelectDAOImpl.setBorderPainted(false);
        buttonSelectDAOImpl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectDAOImplActionPerformed(evt);
            }
        });

        textDAOImpl.setToolTipText("Your directory of DAO implmentation (Data Acess Object)");

        textPackageDAOImpl.setToolTipText("Package name of DAO implementation (Data Acess Object)");
        textPackageDAOImpl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textPackageDAOImplActionPerformed(evt);
            }
        });

        labelMBLocation.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelMBLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMBLocation.setLabelFor(textManagedBean);
        labelMBLocation.setText("Managed Bean Location:");

        textManagedBean.setToolTipText("Your directory of Managed Bean (Controller)");

        buttonSelectMB.setBackground(BLUE);
        buttonSelectMB.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectMB.setText("...");
        buttonSelectMB.setBorderPainted(false);
        buttonSelectMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectMBActionPerformed(evt);
            }
        });

        labelMBPackage.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelMBPackage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMBPackage.setLabelFor(textPackageMB);
        labelMBPackage.setText("Package:");

        textPackageMB.setToolTipText("Package name of Managed Bean");
        textPackageMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textPackageMBActionPerformed(evt);
            }
        });

        textClassMB.setToolTipText("Directory of Class Managed Bean (ClassMB)");

        buttonSelectClassMB.setBackground(BLUE);
        buttonSelectClassMB.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectClassMB.setText("...");
        buttonSelectClassMB.setBorderPainted(false);
        buttonSelectClassMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectClassMBActionPerformed(evt);
            }
        });

        checkBoxUpdateClassMB.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxUpdateClassMB.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        checkBoxUpdateClassMB.setText("Update Class Bean");
        checkBoxUpdateClassMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUpdateClassMBActionPerformed(evt);
            }
        });

        checkBoxUpdateMessageBundle.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxUpdateMessageBundle.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        checkBoxUpdateMessageBundle.setText("Update i18n bundle");
        checkBoxUpdateMessageBundle.setActionCommand("");
        checkBoxUpdateMessageBundle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUpdateMessageBundleActionPerformed(evt);
            }
        });

        textResourceBundleLocation.setToolTipText("Directory of default messages resource bundles");

        buttonSelectMessageBundle.setBackground(BLUE);
        buttonSelectMessageBundle.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectMessageBundle.setText("...");
        buttonSelectMessageBundle.setBorderPainted(false);
        buttonSelectMessageBundle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectMessageBundleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelJavaClassConfigurationLayout = new javax.swing.GroupLayout(panelJavaClassConfiguration);
        panelJavaClassConfiguration.setLayout(panelJavaClassConfigurationLayout);
        panelJavaClassConfigurationLayout.setHorizontalGroup(
            panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                        .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelBOLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                .addComponent(checkBoxUpdateMessageBundle, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textResourceBundleLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonSelectMessageBundle, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                        .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                            .addComponent(textManagedBean, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonSelectMB, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                            .addComponent(textDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonSelectDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(labelMBLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelJavaClassConfigurationLayout.createSequentialGroup()
                                        .addComponent(checkBoxUpdateClassMB, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(textClassMB, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(buttonSelectClassMB, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(textPackageMB, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                    .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(textBusinessObject, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelDAOLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(buttonSelectBO, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelBOPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textPackageBO, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelDAOPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                    .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                            .addComponent(textDAO, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonSelectDAO, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(labelDAOImplLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(textPackageDAO)
                                        .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                                            .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(labelMBPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(textPackageDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(labelDAOImplPackage, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(0, 0, Short.MAX_VALUE))))))
                        .addContainerGap(34, Short.MAX_VALUE))))
        );
        panelJavaClassConfigurationLayout.setVerticalGroup(
            panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelJavaClassConfigurationLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelJavaClassConfigurationLayout.createSequentialGroup()
                        .addComponent(labelBOLocation)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelJavaClassConfigurationLayout.createSequentialGroup()
                        .addComponent(labelBOPackage)
                        .addGap(6, 6, 6)
                        .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textBusinessObject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(textPackageBO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonSelectBO, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(2, 2, 2)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDAOLocation)
                    .addComponent(labelDAOPackage))
                .addGap(4, 4, 4)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textDAO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectDAO, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textPackageDAO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDAOImplLocation)
                    .addComponent(labelDAOImplPackage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textPackageDAOImpl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMBLocation)
                    .addComponent(labelMBPackage))
                .addGap(3, 3, 3)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textManagedBean, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectMB, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textPackageMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxUpdateClassMB)
                    .addComponent(textClassMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectClassMB, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelJavaClassConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxUpdateMessageBundle)
                    .addComponent(textResourceBundleLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectMessageBundle, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        labelMBLocation.getAccessibleContext().setAccessibleParent(panelConfiguration);
        textManagedBean.getAccessibleContext().setAccessibleParent(panelConfiguration);
        buttonSelectMB.getAccessibleContext().setAccessibleParent(panelConfiguration);

        panelViewConfiguration.setBackground(new java.awt.Color(255, 255, 255));
        panelViewConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(0, 51, 102))); // NOI18N

        labelXHTMLLocation.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelXHTMLLocation.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelXHTMLLocation.setLabelFor(textView);
        labelXHTMLLocation.setText("XHTML Location:");

        textView.setToolTipText("XHTML directory");

        buttonSelectView.setBackground(BLUE);
        buttonSelectView.setForeground(new java.awt.Color(255, 255, 255));
        buttonSelectView.setText("...");
        buttonSelectView.setBorderPainted(false);
        buttonSelectView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectViewActionPerformed(evt);
            }
        });

        labelFaceletsTemplate.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelFaceletsTemplate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelFaceletsTemplate.setLabelFor(textTemplatePath);
        labelFaceletsTemplate.setText("Facelets Template:");

        textTemplatePath.setToolTipText("Facelets template to be used in generated pages");

        labelPrimeFacesVersion.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelPrimeFacesVersion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelPrimeFacesVersion.setLabelFor(comboPrimeFacesVersion);
        labelPrimeFacesVersion.setText("PrimeFaces:");

        comboPrimeFacesVersion.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        comboPrimeFacesVersion.setToolTipText("Version of primefaces");

        labelBootstrapVersion.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelBootstrapVersion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelBootstrapVersion.setLabelFor(comboBootstrapVersion);
        labelBootstrapVersion.setText("Bootstrap:");
        labelBootstrapVersion.setToolTipText("");

        comboBootstrapVersion.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        comboBootstrapVersion.setToolTipText("Version of bootstrap");

        javax.swing.GroupLayout panelViewConfigurationLayout = new javax.swing.GroupLayout(panelViewConfiguration);
        panelViewConfiguration.setLayout(panelViewConfigurationLayout);
        panelViewConfigurationLayout.setHorizontalGroup(
            panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelViewConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelViewConfigurationLayout.createSequentialGroup()
                        .addComponent(textView)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelectView, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(panelViewConfigurationLayout.createSequentialGroup()
                        .addComponent(labelXHTMLLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelViewConfigurationLayout.createSequentialGroup()
                        .addComponent(labelFaceletsTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(labelPrimeFacesVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelViewConfigurationLayout.createSequentialGroup()
                        .addComponent(textTemplatePath, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboPrimeFacesVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboBootstrapVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelBootstrapVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelViewConfigurationLayout.setVerticalGroup(
            panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelViewConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelXHTMLLocation)
                    .addComponent(labelFaceletsTemplate)
                    .addComponent(labelPrimeFacesVersion)
                    .addComponent(labelBootstrapVersion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelViewConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSelectView, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textTemplatePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboPrimeFacesVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBootstrapVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonTab2Back.setBackground(BLUE);
        buttonTab2Back.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonTab2Back.setForeground(new java.awt.Color(255, 255, 255));
        buttonTab2Back.setText("Back");
        buttonTab2Back.setBorderPainted(false);
        buttonTab2Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTab2BackActionPerformed(evt);
            }
        });

        buttonTab2Next.setBackground(BLUE);
        buttonTab2Next.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonTab2Next.setForeground(new java.awt.Color(255, 255, 255));
        buttonTab2Next.setText("Next");
        buttonTab2Next.setBorderPainted(false);
        buttonTab2Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTab2NextActionPerformed(evt);
            }
        });

        panelStep4.setBackground(new java.awt.Color(153, 204, 255));
        panelStep4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep4.setBackground(new java.awt.Color(51, 0, 204));
        labelStep4.setForeground(new java.awt.Color(255, 255, 255));
        labelStep4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep4.setText("4");
        labelStep4.setOpaque(true);

        labelStep4Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep4Detail.setText("Other configurations. Do you use CDI? Need acess control?");

        javax.swing.GroupLayout panelStep4Layout = new javax.swing.GroupLayout(panelStep4);
        panelStep4.setLayout(panelStep4Layout);
        panelStep4Layout.setHorizontalGroup(
            panelStep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep4Layout.createSequentialGroup()
                .addComponent(labelStep4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelStep4Detail)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelStep4Layout.setVerticalGroup(
            panelStep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep4Detail))
        );

        panelStep2.setBackground(new java.awt.Color(153, 204, 255));
        panelStep2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep2.setBackground(new java.awt.Color(51, 0, 204));
        labelStep2.setForeground(new java.awt.Color(255, 255, 255));
        labelStep2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep2.setText("2");
        labelStep2.setOpaque(true);

        labelStep2Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep2Detail.setText("Configure the path where you to generate the CRUD java classes");

        javax.swing.GroupLayout panelStep2Layout = new javax.swing.GroupLayout(panelStep2);
        panelStep2.setLayout(panelStep2Layout);
        panelStep2Layout.setHorizontalGroup(
            panelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep2Layout.createSequentialGroup()
                .addComponent(labelStep2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelStep2Detail)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelStep2Layout.setVerticalGroup(
            panelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep2Detail))
        );

        panelStep3.setBackground(new java.awt.Color(153, 204, 255));
        panelStep3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep3.setBackground(new java.awt.Color(51, 0, 204));
        labelStep3.setForeground(new java.awt.Color(255, 255, 255));
        labelStep3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep3.setText("3");
        labelStep3.setOpaque(true);

        labelStep3Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep3Detail.setText("Configure the path to generate the JSF pages (XHTML).  Choose the primefaces version and boostrap (if you want a responsive layout)");

        javax.swing.GroupLayout panelStep3Layout = new javax.swing.GroupLayout(panelStep3);
        panelStep3.setLayout(panelStep3Layout);
        panelStep3Layout.setHorizontalGroup(
            panelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep3Layout.createSequentialGroup()
                .addComponent(labelStep3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelStep3Detail)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelStep3Layout.setVerticalGroup(
            panelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep3Detail))
        );

        javax.swing.GroupLayout panelConfigurationLayout = new javax.swing.GroupLayout(panelConfiguration);
        panelConfiguration.setLayout(panelConfigurationLayout);
        panelConfigurationLayout.setHorizontalGroup(
            panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelConfigurationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelOthers, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
                    .addComponent(panelViewConfiguration, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelStep4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelStep2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelStep3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelConfigurationLayout.createSequentialGroup()
                        .addComponent(buttonTab2Back, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonTab2Next, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelJavaClassConfiguration, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelConfigurationLayout.setVerticalGroup(
            panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfigurationLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(panelStep2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(panelJavaClassConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(panelStep3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelViewConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelStep4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOthers, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(panelConfigurationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonTab2Back)
                    .addComponent(buttonTab2Next))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        tabbedPanelMain.addTab("Project Configuration", panelConfiguration);

        panelCreateClasses.setBackground(new java.awt.Color(237, 242, 253));

        textAreaLog.setColumns(20);
        textAreaLog.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaLog.setRows(5);
        scrollPaneLog.setViewportView(textAreaLog);

        textAreaClassBean.setColumns(20);
        textAreaClassBean.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaClassBean.setRows(5);
        scrollPaneClassBean.setViewportView(textAreaClassBean);

        labelI18N.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelI18N.setText("I18N Resource Bundle:");

        labelClassBean.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        labelClassBean.setText("Class Bean:");

        textAreaSecurityGeneration.setColumns(20);
        textAreaSecurityGeneration.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaSecurityGeneration.setRows(5);
        scrollPaneI18N.setViewportView(textAreaSecurityGeneration);

        panelStep5.setBackground(new java.awt.Color(153, 204, 255));
        panelStep5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep5.setBackground(new java.awt.Color(51, 0, 204));
        labelStep5.setForeground(new java.awt.Color(255, 255, 255));
        labelStep5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep5.setText("5");
        labelStep5.setOpaque(true);

        labelStep5Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep5Detail.setText("Is everything ok? Just click above and see the log");

        javax.swing.GroupLayout panelStep5Layout = new javax.swing.GroupLayout(panelStep5);
        panelStep5.setLayout(panelStep5Layout);
        panelStep5Layout.setHorizontalGroup(
            panelStep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep5Layout.createSequentialGroup()
                .addComponent(labelStep5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelStep5Detail)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelStep5Layout.setVerticalGroup(
            panelStep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep5Detail))
        );

        panelStep6.setBackground(new java.awt.Color(153, 204, 255));
        panelStep6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep6.setBackground(new java.awt.Color(51, 0, 204));
        labelStep6.setForeground(new java.awt.Color(255, 255, 255));
        labelStep6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep6.setText("6");
        labelStep6.setOpaque(true);

        labelStep6Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep6Detail.setText("Copy the generated resource bundle tou your i18n file");

        javax.swing.GroupLayout panelStep6Layout = new javax.swing.GroupLayout(panelStep6);
        panelStep6.setLayout(panelStep6Layout);
        panelStep6Layout.setHorizontalGroup(
            panelStep6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep6Layout.createSequentialGroup()
                .addComponent(labelStep6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelStep6Detail)
                .addGap(0, 97, Short.MAX_VALUE))
        );
        panelStep6Layout.setVerticalGroup(
            panelStep6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep6Detail))
        );

        panelStep7.setBackground(new java.awt.Color(153, 204, 255));
        panelStep7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep7.setBackground(new java.awt.Color(51, 0, 204));
        labelStep7.setForeground(new java.awt.Color(255, 255, 255));
        labelStep7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep7.setText("7");
        labelStep7.setOpaque(true);

        labelStep7Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep7Detail.setText("Copy the methods to your Class Managed Bean");

        javax.swing.GroupLayout panelStep7Layout = new javax.swing.GroupLayout(panelStep7);
        panelStep7.setLayout(panelStep7Layout);
        panelStep7Layout.setHorizontalGroup(
            panelStep7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep7Layout.createSequentialGroup()
                .addComponent(labelStep7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStep7Detail)
                .addContainerGap(154, Short.MAX_VALUE))
        );
        panelStep7Layout.setVerticalGroup(
            panelStep7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep7Detail))
        );

        buttonTab3Back.setBackground(BLUE);
        buttonTab3Back.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        buttonTab3Back.setForeground(new java.awt.Color(255, 255, 255));
        buttonTab3Back.setText("Back");
        buttonTab3Back.setBorderPainted(false);
        buttonTab3Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTab3BackActionPerformed(evt);
            }
        });

        buttonCreateClasses.setBackground(BLUE);
        buttonCreateClasses.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        buttonCreateClasses.setForeground(new java.awt.Color(255, 255, 255));
        buttonCreateClasses.setText("Create Classes");
        buttonCreateClasses.setBorderPainted(false);
        buttonCreateClasses.setFocusPainted(false);
        buttonCreateClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateClassesActionPerformed(evt);
            }
        });

        panelStep8.setBackground(new java.awt.Color(153, 204, 255));
        panelStep8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));

        labelStep8.setBackground(new java.awt.Color(51, 0, 204));
        labelStep8.setForeground(new java.awt.Color(255, 255, 255));
        labelStep8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelStep8.setText("8");
        labelStep8.setOpaque(true);

        labelStep8Detail.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        labelStep8Detail.setText("Copy this security acess generation (if you use xpert-framework base project)");

        javax.swing.GroupLayout panelStep8Layout = new javax.swing.GroupLayout(panelStep8);
        panelStep8.setLayout(panelStep8Layout);
        panelStep8Layout.setHorizontalGroup(
            panelStep8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep8Layout.createSequentialGroup()
                .addComponent(labelStep8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelStep8Detail)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStep8Layout.setVerticalGroup(
            panelStep8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStep8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelStep8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelStep8Detail))
        );

        textAreaI18n.setColumns(20);
        textAreaI18n.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textAreaI18n.setRows(5);
        scrollPaneI18N1.setViewportView(textAreaI18n);

        buttonCopyI18n.setBackground(BLUE);
        buttonCopyI18n.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonCopyI18n.setForeground(new java.awt.Color(255, 255, 255));
        buttonCopyI18n.setText("Copy");
        buttonCopyI18n.setToolTipText("Copy to clipboard");
        buttonCopyI18n.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonCopyI18n.setBorderPainted(false);
        buttonCopyI18n.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyI18nActionPerformed(evt);
            }
        });

        buttonCopyClassBean.setBackground(BLUE);
        buttonCopyClassBean.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonCopyClassBean.setForeground(new java.awt.Color(255, 255, 255));
        buttonCopyClassBean.setText("Copy");
        buttonCopyClassBean.setToolTipText("Copy to clipboard");
        buttonCopyClassBean.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonCopyClassBean.setBorderPainted(false);
        buttonCopyClassBean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyClassBeanActionPerformed(evt);
            }
        });

        buttonCopySecurity.setBackground(BLUE);
        buttonCopySecurity.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonCopySecurity.setForeground(new java.awt.Color(255, 255, 255));
        buttonCopySecurity.setText("Copy");
        buttonCopySecurity.setToolTipText("Copy to clipboard");
        buttonCopySecurity.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonCopySecurity.setBorderPainted(false);
        buttonCopySecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopySecurityActionPerformed(evt);
            }
        });

        buttonOpenI18n.setBackground(BLUE);
        buttonOpenI18n.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonOpenI18n.setForeground(new java.awt.Color(255, 255, 255));
        buttonOpenI18n.setText("Open");
        buttonOpenI18n.setToolTipText("Open in a new window");
        buttonOpenI18n.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonOpenI18n.setBorderPainted(false);
        buttonOpenI18n.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenI18nActionPerformed(evt);
            }
        });

        buttonOpenClassBean.setBackground(BLUE);
        buttonOpenClassBean.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonOpenClassBean.setForeground(new java.awt.Color(255, 255, 255));
        buttonOpenClassBean.setText("Open");
        buttonOpenClassBean.setToolTipText("Open in a new window");
        buttonOpenClassBean.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonOpenClassBean.setBorderPainted(false);
        buttonOpenClassBean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenClassBeanActionPerformed(evt);
            }
        });

        buttonOpenSecurity.setBackground(BLUE);
        buttonOpenSecurity.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonOpenSecurity.setForeground(new java.awt.Color(255, 255, 255));
        buttonOpenSecurity.setText("Open");
        buttonOpenSecurity.setToolTipText("Open in a new window");
        buttonOpenSecurity.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonOpenSecurity.setBorderPainted(false);
        buttonOpenSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenSecurityActionPerformed(evt);
            }
        });

        buttonCopyLog.setBackground(BLUE);
        buttonCopyLog.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonCopyLog.setForeground(new java.awt.Color(255, 255, 255));
        buttonCopyLog.setText("Copy");
        buttonCopyLog.setToolTipText("Copy to clipboard");
        buttonCopyLog.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonCopyLog.setBorderPainted(false);
        buttonCopyLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyLogActionPerformed(evt);
            }
        });

        buttonOpenLog.setBackground(BLUE);
        buttonOpenLog.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        buttonOpenLog.setForeground(new java.awt.Color(255, 255, 255));
        buttonOpenLog.setText("Open");
        buttonOpenLog.setToolTipText("Open in a new window");
        buttonOpenLog.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        buttonOpenLog.setBorderPainted(false);
        buttonOpenLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCreateClassesLayout = new javax.swing.GroupLayout(panelCreateClasses);
        panelCreateClasses.setLayout(panelCreateClassesLayout);
        panelCreateClassesLayout.setHorizontalGroup(
            panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCreateClassesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panelCreateClassesLayout.createSequentialGroup()
                            .addComponent(buttonOpenSecurity, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonCopySecurity, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelStep5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelCreateClassesLayout.createSequentialGroup()
                                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(buttonCreateClasses)
                                    .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelCreateClassesLayout.createSequentialGroup()
                                            .addComponent(labelI18N)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(buttonOpenI18n, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonCopyI18n, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(panelStep6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(scrollPaneI18N1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panelStep7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panelCreateClassesLayout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addComponent(labelClassBean)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(buttonOpenClassBean, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(buttonCopyClassBean, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(scrollPaneClassBean))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCreateClassesLayout.createSequentialGroup()
                                        .addComponent(buttonOpenLog, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(buttonCopyLog, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(scrollPaneLog)
                            .addComponent(panelStep8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPaneI18N)))
                    .addComponent(buttonTab3Back, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        panelCreateClassesLayout.setVerticalGroup(
            panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCreateClassesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelStep5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonCreateClasses)
                    .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonCopyLog)
                        .addComponent(buttonOpenLog)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelStep6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelStep7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelI18N)
                    .addComponent(labelClassBean)
                    .addComponent(buttonCopyI18n)
                    .addComponent(buttonCopyClassBean)
                    .addComponent(buttonOpenI18n)
                    .addComponent(buttonOpenClassBean))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollPaneI18N1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                    .addComponent(scrollPaneClassBean))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelStep8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCreateClassesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCopySecurity)
                    .addComponent(buttonOpenSecurity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneI18N, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonTab3Back)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        tabbedPanelMain.addTab("Create Classes", panelCreateClasses);

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPanelMain)
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addComponent(tabbedPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, 681, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSearchClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchClassesActionPerformed
        searchClasses();
    }//GEN-LAST:event_buttonSearchClassesActionPerformed

    private void buttonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectAllActionPerformed
        selectAll();
    }//GEN-LAST:event_buttonSelectAllActionPerformed

    private void buttonSelectNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectNoneActionPerformed
        selectNone();
    }//GEN-LAST:event_buttonSelectNoneActionPerformed

    private void buttonSelectViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectViewActionPerformed
        showFileChooser(textView, null);
    }//GEN-LAST:event_buttonSelectViewActionPerformed

    private void buttonSelectDAOImplActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectDAOImplActionPerformed
        showFileChooser(textDAOImpl, textPackageDAOImpl);
    }//GEN-LAST:event_buttonSelectDAOImplActionPerformed

    private void buttonSelectMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectMBActionPerformed
        showFileChooser(textManagedBean, textPackageMB);
    }//GEN-LAST:event_buttonSelectMBActionPerformed

    private void textPackageMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textPackageMBActionPerformed
    }//GEN-LAST:event_textPackageMBActionPerformed

    private void textPackageBOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textPackageBOActionPerformed
    }//GEN-LAST:event_textPackageBOActionPerformed

    private void buttonSelectBOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectBOActionPerformed
        showFileChooser(textBusinessObject, textPackageBO);
    }//GEN-LAST:event_buttonSelectBOActionPerformed

    private void textPackageDAOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textPackageDAOActionPerformed
    }//GEN-LAST:event_textPackageDAOActionPerformed

    private void buttonSelectDAOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectDAOActionPerformed
        showFileChooser(textDAO, textPackageDAO);
    }//GEN-LAST:event_buttonSelectDAOActionPerformed

    private void textPackageDAOImplActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textPackageDAOImplActionPerformed
    }//GEN-LAST:event_textPackageDAOImplActionPerformed

    private void buttonCreateClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateClassesActionPerformed
        generate();
    }//GEN-LAST:event_buttonCreateClassesActionPerformed

    private void buttonTab1NextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTab1NextActionPerformed
        tabbedPanelMain.setSelectedIndex(1);
    }//GEN-LAST:event_buttonTab1NextActionPerformed

    private void buttonTab2BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTab2BackActionPerformed
        tabbedPanelMain.setSelectedIndex(0);
    }//GEN-LAST:event_buttonTab2BackActionPerformed

    private void buttonTab2NextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTab2NextActionPerformed
        tabbedPanelMain.setSelectedIndex(2);
    }//GEN-LAST:event_buttonTab2NextActionPerformed

    private void buttonTab3BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTab3BackActionPerformed
        tabbedPanelMain.setSelectedIndex(1);
    }//GEN-LAST:event_buttonTab3BackActionPerformed

    private void checkUseCDIBeansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUseCDIBeansActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkUseCDIBeansActionPerformed

    private void checkMaskCalendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkMaskCalendarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkMaskCalendarActionPerformed

    private void buttonSelectClassMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectClassMBActionPerformed
        JFileChooser chooser = new JFileChooser();
        if (textClassMB != null && textClassMB.getText() != null && !textClassMB.getText().isEmpty() && new File(textClassMB.getText()).exists()) {
            chooser.setCurrentDirectory(new File(textClassMB.getText()));
        } else {
            chooser.setCurrentDirectory(new java.io.File("."));
        }
        chooser.setDialogTitle("Select a Directory");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            textClassMB.setText(path);
        }
    }//GEN-LAST:event_buttonSelectClassMBActionPerformed

    private void checkBoxUpdateClassMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxUpdateClassMBActionPerformed
        checkCheckBoxStates();
    }//GEN-LAST:event_checkBoxUpdateClassMBActionPerformed

    private void checkBoxUpdateMessageBundleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxUpdateMessageBundleActionPerformed
        checkCheckBoxStates();
    }//GEN-LAST:event_checkBoxUpdateMessageBundleActionPerformed

    private void buttonSelectMessageBundleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectMessageBundleActionPerformed
        JFileChooser chooser = new JFileChooser();
        if (textResourceBundleLocation != null && textResourceBundleLocation.getText() != null
                && !textResourceBundleLocation.getText().isEmpty() && new File(textResourceBundleLocation.getText()).exists()) {
            chooser.setCurrentDirectory(new File(textResourceBundleLocation.getText()));
        } else {
            chooser.setCurrentDirectory(new java.io.File("."));
        }
        chooser.setDialogTitle("Select a Directory");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            textResourceBundleLocation.setText(path);
        }
    }//GEN-LAST:event_buttonSelectMessageBundleActionPerformed

    private void buttonCopyI18nActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyI18nActionPerformed
        SwingUtils.copyToClipboard(textAreaI18n);
    }//GEN-LAST:event_buttonCopyI18nActionPerformed

    private void buttonCopyClassBeanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyClassBeanActionPerformed
        SwingUtils.copyToClipboard(textAreaClassBean);
    }//GEN-LAST:event_buttonCopyClassBeanActionPerformed

    private void buttonCopySecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopySecurityActionPerformed
        SwingUtils.copyToClipboard(textAreaSecurityGeneration);
    }//GEN-LAST:event_buttonCopySecurityActionPerformed

    private void buttonOpenI18nActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenI18nActionPerformed
        openContentNewWindow(textAreaI18n);
    }//GEN-LAST:event_buttonOpenI18nActionPerformed

    private void buttonOpenClassBeanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenClassBeanActionPerformed
        openContentNewWindow(textAreaClassBean);
    }//GEN-LAST:event_buttonOpenClassBeanActionPerformed

    private void buttonOpenSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenSecurityActionPerformed
        openContentNewWindow(textAreaSecurityGeneration);
    }//GEN-LAST:event_buttonOpenSecurityActionPerformed

    private void buttonCopyLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyLogActionPerformed
        SwingUtils.copyToClipboard(textAreaLog);
    }//GEN-LAST:event_buttonCopyLogActionPerformed

    private void buttonOpenLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenLogActionPerformed
        openContentNewWindow(textAreaLog);
    }//GEN-LAST:event_buttonOpenLogActionPerformed

    private void checkHideIdInRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkHideIdInRequestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkHideIdInRequestActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCopyClassBean;
    private javax.swing.JButton buttonCopyI18n;
    private javax.swing.JButton buttonCopyLog;
    private javax.swing.JButton buttonCopySecurity;
    private javax.swing.JButton buttonCreateClasses;
    private javax.swing.JButton buttonOpenClassBean;
    private javax.swing.JButton buttonOpenI18n;
    private javax.swing.JButton buttonOpenLog;
    private javax.swing.JButton buttonOpenSecurity;
    private javax.swing.JButton buttonSearchClasses;
    private javax.swing.JButton buttonSelectAll;
    private javax.swing.JButton buttonSelectBO;
    private javax.swing.JButton buttonSelectClassMB;
    private javax.swing.JButton buttonSelectDAO;
    private javax.swing.JButton buttonSelectDAOImpl;
    private javax.swing.JButton buttonSelectMB;
    private javax.swing.JButton buttonSelectMessageBundle;
    private javax.swing.JButton buttonSelectNone;
    private javax.swing.JButton buttonSelectView;
    private javax.swing.JButton buttonTab1Next;
    private javax.swing.JButton buttonTab2Back;
    private javax.swing.JButton buttonTab2Next;
    private javax.swing.JButton buttonTab3Back;
    private javax.swing.JCheckBox checkBoxUpdateClassMB;
    private javax.swing.JCheckBox checkBoxUpdateMessageBundle;
    private javax.swing.JCheckBox checkGeneratesSecurityArea;
    private javax.swing.JCheckBox checkHideIdInRequest;
    private javax.swing.JCheckBox checkMaskCalendar;
    private javax.swing.JCheckBox checkUseCDIBeans;
    private javax.swing.JComboBox comboBootstrapVersion;
    private javax.swing.JComboBox comboPrimeFacesVersion;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JLabel labelAuthor;
    private javax.swing.JLabel labelBOLocation;
    private javax.swing.JLabel labelBOPackage;
    private javax.swing.JLabel labelBOSuffix;
    private javax.swing.JLabel labelBaseDAOImpl;
    private javax.swing.JLabel labelBootstrapVersion;
    private javax.swing.JLabel labelClassBean;
    private javax.swing.JLabel labelDAOImplLocation;
    private javax.swing.JLabel labelDAOImplPackage;
    private javax.swing.JLabel labelDAOLocation;
    private javax.swing.JLabel labelDAOPackage;
    private javax.swing.JLabel labelDatePattern;
    private javax.swing.JLabel labelDocs;
    private javax.swing.JLabel labelFaceletsTemplate;
    private javax.swing.JLabel labelI18N;
    private javax.swing.JLabel labelIntroEn;
    private javax.swing.JLabel labelIntroPtBr;
    private javax.swing.JLabel labelLinkDocs;
    private javax.swing.JLabel labelLinkShowcase;
    private javax.swing.JLabel labelLinkXpertSistemas;
    private javax.swing.JLabel labelMBLocation;
    private javax.swing.JLabel labelMBPackage;
    private javax.swing.JLabel labelMBSuffix;
    private javax.swing.JLabel labelPackageName;
    private javax.swing.JLabel labelPrimeFacesVersion;
    private javax.swing.JLabel labelResourceBundle;
    private javax.swing.JLabel labelSelectClasses;
    private javax.swing.JLabel labelSelectClasses1;
    private javax.swing.JLabel labelShowcase;
    private javax.swing.JLabel labelStep1;
    private javax.swing.JLabel labelStep1Detail;
    private javax.swing.JLabel labelStep2;
    private javax.swing.JLabel labelStep2Detail;
    private javax.swing.JLabel labelStep3;
    private javax.swing.JLabel labelStep3Detail;
    private javax.swing.JLabel labelStep4;
    private javax.swing.JLabel labelStep4Detail;
    private javax.swing.JLabel labelStep5;
    private javax.swing.JLabel labelStep5Detail;
    private javax.swing.JLabel labelStep6;
    private javax.swing.JLabel labelStep6Detail;
    private javax.swing.JLabel labelStep7;
    private javax.swing.JLabel labelStep7Detail;
    private javax.swing.JLabel labelStep8;
    private javax.swing.JLabel labelStep8Detail;
    private javax.swing.JLabel labelTimePattern;
    private javax.swing.JLabel labelWatermakerXpertSistemas;
    private javax.swing.JLabel labelXHTMLLocation;
    private javax.swing.JLabel labelXpertMaker;
    private javax.swing.JLabel labelXpertSistemas;
    private javax.swing.JList listClasses;
    private javax.swing.JPanel panelClassesConfiguration;
    private javax.swing.JPanel panelConfiguration;
    private javax.swing.JPanel panelCreateClasses;
    private javax.swing.JPanel panelJavaClassConfiguration;
    private javax.swing.JPanel panelOthers;
    private javax.swing.JPanel panelSelectClasses;
    private javax.swing.JPanel panelStep1;
    private javax.swing.JPanel panelStep2;
    private javax.swing.JPanel panelStep3;
    private javax.swing.JPanel panelStep4;
    private javax.swing.JPanel panelStep5;
    private javax.swing.JPanel panelStep6;
    private javax.swing.JPanel panelStep7;
    private javax.swing.JPanel panelStep8;
    private javax.swing.JPanel panelViewConfiguration;
    private javax.swing.JScrollPane scrollPaneClassBean;
    private javax.swing.JScrollPane scrollPaneI18N;
    private javax.swing.JScrollPane scrollPaneI18N1;
    private javax.swing.JScrollPane scrollPaneLog;
    private javax.swing.JScrollPane scrollPaneSelectClasses;
    private javax.swing.JTabbedPane tabbedPanelMain;
    private javax.swing.JTextArea textAreaClassBean;
    private javax.swing.JTextArea textAreaI18n;
    private javax.swing.JTextArea textAreaLog;
    private javax.swing.JTextArea textAreaSecurityGeneration;
    private javax.swing.JTextField textAuthor;
    private javax.swing.JTextField textBaseDAOImpl;
    private javax.swing.JTextField textBusinessObject;
    private javax.swing.JTextField textBusinessObjectSuffix;
    private javax.swing.JTextField textClassMB;
    private javax.swing.JTextField textDAO;
    private javax.swing.JTextField textDAOImpl;
    private javax.swing.JTextField textDatePattern;
    private javax.swing.JTextField textManagedBean;
    private javax.swing.JTextField textManagedBeanSuffix;
    private javax.swing.JTextField textPackageBO;
    private javax.swing.JTextField textPackageDAO;
    private javax.swing.JTextField textPackageDAOImpl;
    private javax.swing.JTextField textPackageMB;
    private javax.swing.JTextField textPackageName;
    private javax.swing.JTextField textResourceBundle;
    private javax.swing.JTextField textResourceBundleLocation;
    private javax.swing.JTextField textTemplatePath;
    private javax.swing.JTextField textTimePattern;
    private javax.swing.JTextField textView;
    // End of variables declaration//GEN-END:variables
}
