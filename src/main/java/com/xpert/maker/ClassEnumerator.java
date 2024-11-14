package com.xpert.maker;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author ayslan
 */
public class ClassEnumerator implements Serializable {

    private static final long serialVersionUID = 7195422249326118121L;

    private static void log(String msg) {
        //System.out.println("ClassDiscovery: " + msg);
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }

    private static void processDirectory(File directory, String pkgname, ArrayList<Class<?>> classes) {
        log("Reading Directory '" + directory + "'");
        // Get the list of the files contained in the package
        String[] files = directory.list();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            String className = null;
            // we are only interested in .class files
            if (fileName.endsWith(".class")) {
                // removes the .class extension
                className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);
            }
            log("FileName '" + fileName + "'  =>  class '" + className + "'");
            if (className != null) {
                classes.add(loadClass(className));
            }
            File subdir = new File(directory, fileName);
            if (subdir.isDirectory()) {
                processDirectory(subdir, pkgname + '.' + fileName, classes);
            }
        }
    }

    private static void processJarfile(URL resource, String pkgname, ArrayList<Class<?>> classes) {
        String relPath = pkgname.replace('.', '/');
        String resPath = resource.getPath();
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        log("Reading JAR file: '" + jarPath + "'");
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String className = null;
                if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                    className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                }
                log("JarEntry '" + entryName + "'  =>  class '" + className + "'");
                if (className != null) {
                    classes.add(loadClass(className));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }
    }

    public static ArrayList<Class<?>> getClassesForPackage(String pkgname) {
        ArrayList<Class<?>> classes = new ArrayList<>();

        String relPath = pkgname.replace('.', '/');

        // Get a File object for the package
        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
        if (resource == null) {
            resource = Thread.currentThread().getContextClassLoader().getResource(relPath);
            if (resource == null) {
                throw new RuntimeException("Unexpected problem: No resource for " + relPath);
            }
        }
        log("Package: '" + pkgname + "' becomes Resource: '" + resource.toString() + "'");
        try {
            String path = URLDecoder.decode(resource.getPath(), "UTF-8");
            if (resource.toString().startsWith("jar:")) {
                processJarfile(resource, pkgname, classes);
            } else {
                processDirectory(new File(path), pkgname, classes);
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        return classes;
    }
}
