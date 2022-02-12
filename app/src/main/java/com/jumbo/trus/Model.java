package com.jumbo.trus;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

public abstract class Model implements Serializable {

    protected String id;
    protected String name;

    public Model() {

    }

    public Model(String name) {
        this.name = name;
        //id = System.currentTimeMillis() + returnRandomChars(5);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String returnRandomChars(int size) {
        StringBuilder returnChars = new StringBuilder();
        for (int i = 0; i < size; i++) {
            returnChars.append(rndChar());
        }
        return returnChars.toString();
    }

    private char rndChar () {
        int rnd = (int) (Math.random() * 52);
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(id, model.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                '}';
    }
}
