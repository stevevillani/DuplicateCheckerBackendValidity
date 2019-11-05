package com.villani.checkDuplicates.domain;

import java.util.List;

public class PersonWrapper {
    List<Person> duplicates;
    List<Person> uniques;

    public List<Person> getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(List<Person> duplicates) {
        this.duplicates = duplicates;
    }

    public List<Person> getUniques() {
        return uniques;
    }

    public void setUniques(List<Person> uniques) {
        this.uniques = uniques;
    }
}
