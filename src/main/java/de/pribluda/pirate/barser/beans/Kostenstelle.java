/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser.beans;

/**
 * represents certain budget account
 *
 * @author Konstantin Pribluda
 */
public class Kostenstelle {

    String name;
    String title;
    String amt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }


    @Override
    public String toString() {
        return "Kostenstelle{" +
                "amt='" + amt + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
