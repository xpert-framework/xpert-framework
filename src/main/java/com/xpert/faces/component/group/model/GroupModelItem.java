package com.xpert.faces.component.group.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * The itens of a GroupModel
 *
 * @author ayslan
 * @param <K> Key Type
 * @param <V> Value Type
 */
public class GroupModelItem<K, V> implements Serializable {

    private static final long serialVersionUID = 5941918850830301563L;

    private K key;
    private List<V> value;
    private boolean first;
    private boolean last;
    private int index;

    public GroupModelItem() {
        this.value = new ArrayList();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return value.size();
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public List<V> getValue() {
        return value;
    }

    public void setValue(List<V> value) {
        this.value = value;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

}
