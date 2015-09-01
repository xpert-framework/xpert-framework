package com.xpert.utils;

import java.util.Collections;
import java.util.List;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;

public class CollectionsUtils {

    @SuppressWarnings("unchecked")
    public static void orderAsc(List list, String ordenacao) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ComparatorChain multiSort = new ComparatorChain();
        String array[] = ordenacao.split(",");
        for (int i = 0; i < array.length; i++) {
            multiSort.addComparator(new BeanComparator(array[i].trim(), new NullComparator()), false);
        }
        Collections.sort(list, multiSort);
    }

    @SuppressWarnings("unchecked")
    public static void orderDesc(List list, String ordenacao) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ComparatorChain multiSort = new ComparatorChain();
        String array[] = ordenacao.split(",");
        for (int i = 0; i < array.length; i++) {
            multiSort.addComparator(new BeanComparator(array[i].trim(), new NullComparator()), true);
        }
        Collections.sort(list, multiSort);
    }

    /**
     *
     * @param list
     * @param ordenacao
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static void order(List list, String ordenacao) throws IllegalArgumentException {
        if (list == null || list.isEmpty()) {
            return;
        }
        ComparatorChain multiSort = new ComparatorChain();
        String array[] = ordenacao.split(",");
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
