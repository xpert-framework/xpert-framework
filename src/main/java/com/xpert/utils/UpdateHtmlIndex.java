package com.xpert.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Generic class to generate xpert-framework artifacts
 *
 * @author ayslan
 */
public class UpdateHtmlIndex {

    private static final Logger logger = Logger.getLogger(UpdateHtmlIndex.class.getName());
    
    

    public static void main(String[] args) throws FileNotFoundException {
        updateIndex("./../xpert-framework.github.io/maven");
    }

//    public static void main2(String[] args) throws FileNotFoundException {
//        File file = new File("D:\\Projetos\\xpert-framework\\git\\xpert-framework.github.io\\maven\\.index\\");
//        file.mkdirs();
//    }
    public static void updateIndex(String file) throws FileNotFoundException {
        updateIndex(new File(file), "");
    }

    /**
     * Create a HTML file (index.html) in folder and subfolders
     *
     * @param file
     * @param root
     * @throws FileNotFoundException
     */
    public static void updateIndex(File file, String root) throws FileNotFoundException {
        if (file == null) {
            return;
        }
        
        logger.log(Level.INFO, "Updating Index in {0}", file.getAbsolutePath());
        

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH);
        StringBuilder html = new StringBuilder();
        File[] files = file.listFiles();

        String currentDir = root + "/" + file.getName() + "/";

        html.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n");
        html.append("<html>\n");
        html.append("<head><title>Index of ").append(currentDir).append("</title></head>\n");
        html.append("<body bgcolor=\"white\">");
        html.append("<h1>Index of ").append(currentDir).append("</h1>\n");
        html.append("<hr>");
        html.append("<pre>");
        html.append("<a href=\"../\">../</a>\n");
        if (files != null && files.length > 0) {
            for (File child : files) {
                if (!child.getName().equals("index.html") && !child.getName().equals(".index")) {

                    String fileName = child.getName();
                    if (child.isDirectory()) {
                        fileName = fileName + "/";
                    }

                    String link = "<a href=\"" + fileName + "\">" + fileName + "</a>";
                    html.append(link);
                    html.append(org.apache.commons.lang3.StringUtils.rightPad("", 51 - fileName.length()));
                    html.append(dateFormat.format(file.lastModified()));
                    html.append(org.apache.commons.lang3.StringUtils.rightPad("", 19));
                    if (child.isDirectory()) {
                        html.append("-");
                    } else {
                        html.append(child.length());
                    }
                    html.append("\n");

                }
            }

        }
        html.append("</pre>");
        html.append("<hr>");
        html.append("</body>\n</html>");

        if (files != null && files.length > 0) {
            for (File child : files) {
                if (child.isDirectory()) {
                    logger.log(Level.INFO, "Updating File {0}", file.getName());
                    updateIndex(child, root + "/" + file.getName());
                }
            }
        }

        PrintStream printStream = new PrintStream(file + "/index.html");
        printStream.print(html.toString());
        printStream.flush();
        printStream.close();
    }

}
