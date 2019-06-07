package com.example.datatables.db;

import lombok.Getter;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class NameDict {

    protected Set<String> firstNameSet;
    protected Set<String> lastNameSet;
    protected Set<String> departmentNameSet;

    protected NameDict() {
        this.firstNameSet = new LinkedHashSet<>();
        this.lastNameSet = new LinkedHashSet<>();
        this.departmentNameSet = new LinkedHashSet<>();
    }

    void add(String value, boolean first) {
        if (first) {
            this.firstNameSet.add(value);
        } else {
            this.lastNameSet.add(value);
        }
    }

    void addDep(String value) {
        this.departmentNameSet.add(value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNameSet.size());
        sb.append(" first, ");
        sb.append(lastNameSet.size());
        sb.append(" last names");
        return sb.toString();
    }

    String getRandomFirstName() {
        return getRandomSetValue(firstNameSet);
    }

    String getRandomLastName() {
        return getRandomSetValue(lastNameSet);
    }

    private String getRandomSetValue(Set<String> set) {
        int index = (int) Math.round(Math.random() * set.size());
        Iterator<String> iterator = set.iterator();
        String res = "";
        for (int i = 0; i < index; i++) {
            res = iterator.next();
        }
        return res;
    }
}
