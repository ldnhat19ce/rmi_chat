package com.ldnhat.dao.impl;

import java.util.Objects;

public class Test {

    private String name;
    private int id;

    public Test(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return test.id == this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static void main(String[] args) {
        Test test1 = new Test("aab", 3);
        Test test2 = new Test("aa", 1);

        if(test1.equals(test2))
            System.out.println("Both Objects are equal. ");
        else
            System.out.println("Both Objects are not equal. ");
    }
}
