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

    public static void center(JFrame jFrame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        jFrame.setLocation((screenSize.width - jFrame.getSize().width) / 2, (screenSize.height - jFrame.getSize().height) / 2);
    }

    public static void copyToClipboard(JTextField jTextField) {
        copyToClipboard(jTextField.getText());
    }

    public static void copyToClipboard(JTextArea jTextArea) {
        copyToClipboard(jTextArea.getText());
    }

    public static void copyToClipboard(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    public static void createHiperLink(JLabel label, final String url, String text) {
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
