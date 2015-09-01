package com.xpert.maker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Ayslan
 */
public class ReadClass {

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages. Adapted from
     * http://snippets.dzone.com/posts/show/4831 and extended to support use of
     * JAR files
     *
     * @param path
     * @return The classes
     */
    public static Class[] getClasses(String path) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
          //  String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<String> dirs = new ArrayList<String>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(resource.getFile());
            }
            TreeSet<String> classes = new TreeSet<String>();
            for (String directory : dirs) {
                classes.addAll(findClasses(directory, path));
            }
            ArrayList<Class> classList = new ArrayList<Class>();
            for (String clazz : classes) {
                classList.add(Class.forName(clazz));
            }
            return classList.toArray(new Class[classes.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs. Adapted from http://snippets.dzone.com/posts/show/4831 and
     * extended to support use of JAR files
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static TreeSet<String> findClasses(String directory, String packageName) throws Exception {
        TreeSet<String> classes = new TreeSet<String>();
        if (directory.startsWith("file:") && directory.contains("!")) {
            String[] split = directory.split("!");
            URL jar = new URL(split[0]);
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
                    classes.add(className);
                }
            }
        }
        File dir = new File(directory);
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
            }
        }
        return classes;
    }

}
