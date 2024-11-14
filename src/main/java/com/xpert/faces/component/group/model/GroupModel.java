package com.xpert.faces.component.group.model;

import com.xpert.faces.component.api.SortOrder;
import com.xpert.utils.CollectionsUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * A java bean to group values from listsed on a property
 *
 * @author ayslan
 * @param <K> Key Type
 * @param <V> Value Type
 */
public class GroupModel<K, V> implements Serializable {

    private static final long serialVersionUID = 9009437646145537151L;

    private String groupBy;
    private String itemSortField;
    private SortOrder itemSortOrder;
    private Comparator<V> itemComparator;
    private String sortField;
    private SortOrder sortOrder;
    private Comparator<V> comparator;
    /**
     * the original value
     */
    private List<V> value;
    private List<GroupModelItem<K, V>> itens;

    public GroupModel(String field, List value) {
        this.groupBy = field;
        this.value = value;
        this.itens = new ArrayList<>();
    }

    /**
     * Returns the itens size
     *
     * @return itens size (list.size())
     */
    public int getItensSize() {
        if (itens != null) {
            return itens.size();
        }
        return 0;
    }

    /**
     * Returns the size of model
     *
     * @return size of model
     */
    public int getSize() {
        if (value != null) {
            return value.size();
        }
        return 0;
    }

    /**
     * Group the itens. The grouping is done with java Map, so its important to
     * have a good "equals" defined ind the bean
     */
    public void groupItens() {
        if (value != null) {
            //   System.out.println("Grouping by: " + groupBy + " size: " + value.size() + " list: " + value);
            Map<Object, GroupModelItem> map = new LinkedHashMap<>();
            itens = new ArrayList<>();
            if (comparator != null) {
                Collections.sort(value, comparator);
            }
            for (Object item : value) {
                try {
                    Object keyForData;
                    try {
                        keyForData = PropertyUtils.getProperty(item, groupBy);
                    } catch (NestedNullException ex) {
                        //null key
                        keyForData = null;
                    }
                    GroupModelItem groupModelItem = map.get(keyForData);
                    if (groupModelItem == null) {
                        try {
                            groupModelItem = new GroupModelItem();
                            groupModelItem.setKey(keyForData);
                            groupModelItem.setValue(new ArrayList());
                            itens.add(groupModelItem);
                        } catch (Exception ex) {
                            throw new RuntimeException("Error in instance type " + value.getClass().getName(), ex);
                        }
                        map.put(keyForData, groupModelItem);
                    }
                    groupModelItem.getValue().add(item);
                } catch (Exception ex) {
                    throw new RuntimeException("Error getting property " + groupBy + " in collection ", ex);
                }
            }

        } else {
            itens = new ArrayList<>();
        }

        //order main list
        if (comparator == null) {
            String sortKey = "key";
            if (sortField != null && !sortField.isEmpty()) {
                sortKey = sortKey + "." + sortField;
            }

            if (itens != null && !itens.isEmpty()) {

                Object firstItem = itens.get(0).getKey();
                boolean isSort = false;
                if (firstItem instanceof Comparable || (sortField != null && !sortField.isEmpty())) {
                    isSort = true;
                }
                if (isSort == true) {
                    if (sortOrder == null || sortOrder.equals(SortOrder.ASCENDING)) {
                        CollectionsUtils.orderAsc(itens, sortKey);
                    } else {
                        CollectionsUtils.orderDesc(itens, sortKey);
                    }
                }

            }
        }

        // System.out.println("quantidade de itens: " + itens.size());
        //normalize
        if (itens != null) {
            for (int i = 0; i < itens.size(); i++) {
                GroupModelItem groupModelItem = itens.get(i);
                groupModelItem.setIndex(i);
                if (i == 0) {
                    groupModelItem.setFirst(true);
                } else if (i == itens.size() - 1) {
                    groupModelItem.setLast(true);
                }
                //order itens
                if (itemComparator != null) {
                    Collections.sort(groupModelItem.getValue(), itemComparator);
                } else {
                    if (itemSortField != null && !itemSortField.isEmpty()) {
                        if (itemSortOrder == null || itemSortOrder.equals(SortOrder.ASCENDING)) {
                            CollectionsUtils.orderAsc(groupModelItem.getValue(), itemSortField);
                        } else {
                            CollectionsUtils.orderDesc(groupModelItem.getValue(), itemSortField);
                        }
                    }
                }
            }
        }

    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getItemSortField() {
        return itemSortField;
    }

    public void setItemSortField(String itemSortField) {
        this.itemSortField = itemSortField;
    }

    public SortOrder getItemSortOrder() {
        return itemSortOrder;
    }

    public void setItemSortOrder(SortOrder itemSortOrder) {
        this.itemSortOrder = itemSortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<V> getValue() {
        return value;
    }

    public void setValue(List<V> value) {
        this.value = value;
    }

    public List<GroupModelItem<K, V>> getItens() {
        return itens;
    }

    public void setItens(List<GroupModelItem<K, V>> itens) {
        this.itens = itens;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Comparator<V> getItemComparator() {
        return itemComparator;
    }

    public void setItemComparator(Comparator<V> itemComparator) {
        this.itemComparator = itemComparator;
    }

    public Comparator<V> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<V> comparator) {
        this.comparator = comparator;
    }

}
