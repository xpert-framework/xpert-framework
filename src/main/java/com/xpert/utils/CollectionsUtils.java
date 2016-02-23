package com.xpert.utils;

import java.util.Collections;
import java.util.List;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;

/**
 * Utility class to use with java.util.Collection and java.util.List (and his
 * instances).
 *
 * This class uses "commons-beanutils" and "common-collections" to do the
 * sorting
 *
 * @author ayslan
 */
public class CollectionsUtils {

    /**
     * Order "asc" the list
     *
     * @param list a collection o java.util.List interfaces
     * @param order property name to be used in sorting
     */
    @SuppressWarnings("unchecked")
    public static void orderAsc(List list, String order) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ComparatorChain multiSort = new ComparatorChain();
        String array[] = order.split(",");
        for (int i = 0; i < array.length; i++) {
            multiSort.addComparator(new BeanComparator(array[i].trim(), new NullComparator()), false);
        }
        Collections.sort(list, multiSort);
    }

    /**
     * Order "desc" the list
     *
     * @param list a collection o java.util.List interfaces
     * @param order property name to be used in sorting
     */
    @SuppressWarnings("unchecked")
    public static void orderDesc(List list, String order) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ComparatorChain multiSort = new ComparatorChain();
        String array[] = order.split(",");
        for (int i = 0; i < array.length; i++) {
            multiSort.addComparator(new BeanComparator(array[i].trim(), new NullComparator()), true);
        }
        Collections.sort(list, multiSort);
    }

    /**
     * Order list with sinlge or multiple sorting. Examples:
     *
     * CollectionsUtils.order(list, "name desc, code asc")
     * CollectionsUtils.order(list, "birthday desc, name")
     *
     *
     * @param list
     * @param order
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static void order(List list, String order) throws IllegalArgumentException {
        if (list == null || list.isEmpty()) {
            return;
        }
        ComparatorChain multiSort = new ComparatorChain();
        String array[] = order.split(",");
        for (int i = 0; i < array.length; i++) {
            String field[] = array[i].trim().split(" ");
            if (field.length == 1 || (field.length == 2 && field[1].equalsIgnoreCase("asc"))) {
                multiSort.addComparator(new BeanComparator(field[0].trim(), new NullComparator()), false);
            } else {
                if (field[1].equalsIgnoreCase("desc")) {
                    multiSort.addComparator(new BeanComparator(field[0].trim(), new NullComparator()), true);
                } else {
                    throw new IllegalArgumentException("Value of the second argument must be asc or desc");
                }
            }
        }
        Collections.sort(list, multiSort);
    }
}
