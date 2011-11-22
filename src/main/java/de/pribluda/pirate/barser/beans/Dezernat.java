/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser.beans;

/**
 * Represents certain dezernat
 *
 * @author Konstantin Pribluda
 */
public class Dezernat {


    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Dezernat{" +
                "name='" + name + '\'' +
                '}';
    }
}
