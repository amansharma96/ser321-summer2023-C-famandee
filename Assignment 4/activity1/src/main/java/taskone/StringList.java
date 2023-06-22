package taskone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class StringList {

    private List<String> strings;

    public StringList() {
        strings = new ArrayList<>();
    }

    public void add(String str) {
        if (!strings.contains(str)) {
            strings.add(str);
        }
    }

    public boolean contains(String str) {
        return strings.contains(str);
    }

    public int size() {
        return strings.size();
    }

    public int find(String str) {
        return strings.indexOf(str);
    }

    public void clear() {
        strings.clear();
    }

    public void sort() {
        Collections.sort(strings);
    }

    public void prepend(int index, String str) {
        if (index >= 0 && index < strings.size()) {
            strings.add(index, str);
        }
    }

    public String toString() {
        return strings.toString();
    }
}

