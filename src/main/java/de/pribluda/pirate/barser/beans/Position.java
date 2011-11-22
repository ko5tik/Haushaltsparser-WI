/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser.beans;

import java.util.Map;

/**
 * represents individual position
 *
 * @author Konstantin Pribluda
 */
public class Position {
    String name;
    Map<String, Integer> data;

    public Map<String, Integer> getData() {
        return data;
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Position{" +
                "data=" + data +
                ", name='" + name + '\'' +
                '}';
    }
}
