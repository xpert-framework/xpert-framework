package com.xpert.utils;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Utility class to java swing
 *
 * @author ayslan
 */
public class SwingUtils {

    /**
     * Position in center a JFrame
     *
     * @param jFrame
     */
    public static void center(JFrame jFrame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        jFrame.setLocation((screenSize.width - jFrame.getSize().width) / 2, (screenSize.height - jFrame.getSize().height) / 2);
    }

    /**
     * Copy a JTextField text (jTextField.getText()) to clipborad
     *
     * @param jTextField
     */
    public static void copyToClipboard(JTextField jTextField) {
        copyToClipboard(jTextField.getText());
    }

    /**
     * Copy a JTextArea text (jTextArea.getText()) to clipborad
     *
     * @param jTextArea
     */
    public static void copyToClipboard(JTextArea jTextArea) {
        copyToClipboard(jTextArea.getText());
    }

    /**
     * Copy a String to clipborad
     *
     * @param string
     */
    public static void copyToClipboard(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    /**
     * Create a HTML hyperlink in JLabel component
     *
     * @param label
     * @param url
     * @param text
     */
    public static void createHyperLink(JLabel label, final String url, String text) {
        label.setToolTipText(url);
        label.setText("<html><a href=\"\">" + text + "</a></html>");
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

}
