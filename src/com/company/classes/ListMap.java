package com.company.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** HASH MAP WHERE PUT ADDS TO A LIST INSTEAD OF OVERWRITING THE VALUE **/
public class ListMap<K, V> {

    private final Map<K, ArrayList<V>> m = new HashMap<>();
    private int size = 0;

    public void put(K k, V v) {
        if (m.containsKey(k)) {
            m.get(k).add(v);
        } else {
            ArrayList<V> arr = new ArrayList<>();
            arr.add(v);
            m.put(k, arr);
            size++;
        }
    }

    public ArrayList<V> get(K k) {
        return m.get(k);
    }

    public V get(K k, int index) {
        return m.get(k).size()-1 < index ? null : m.get(k).get(index);
    }

    public int size() {
        return size;
    }

}