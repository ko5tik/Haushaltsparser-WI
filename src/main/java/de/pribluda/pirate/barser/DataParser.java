/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import de.pribluda.pirate.barser.beans.Dezernat;
import de.pribluda.pirate.barser.beans.Kostenstelle;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to parse various budget strings
 *
 * @author Konstantin Pribluda
 */
public class DataParser {
    public static final String ORGANISATIONSEINHEIT = "Organisation";
    public static final String KOSTENSSTELLENPREFIX = "kostens";

    public static Dezernat parseAmt(String line) {
        final String[] fragments = line.trim().split(":");

        if (fragments[0].startsWith(ORGANISATIONSEINHEIT)) {
            final String entry = fragments[1].trim();
            System.out.println("entity:" + entry);
            final Dezernat dezernat = new Dezernat();
            dezernat.setName(entry);
            return dezernat;

        }
        return null;
    }

    public static Kostenstelle parseKostenstelle(String line) {
        final String[] fragments = line.trim().split(":");

        if (fragments[0].toLowerCase().startsWith(KOSTENSSTELLENPREFIX)) {
            final String[] split = fragments[1].trim().split("\\s+");
            String name = split[0];
            String amt = split[1];

            final StringBuilder titleBuilder = new StringBuilder();
            for (int i = 2; i < split.length; i++) {
                titleBuilder.append(split[i]).append(" ");
            }

            Kostenstelle kostenstele = new Kostenstelle();

            kostenstele.setAmt(amt);
            kostenstele.setName(name);
            kostenstele.setTitle(titleBuilder.toString().trim());
            return kostenstele;
        }
        return null;
    }


    /**
     * process incoming number.   minus sign is affixed to the end
     *
     * @param number
     * @return
     */
    public static Integer processNumber(String number) {
        if (number == null) {
            return null;
        }
        final String preprocessed = number.replaceAll("\\.", "");
        try {
            if (preprocessed.endsWith("-")) {
                return Integer.parseInt(preprocessed.substring(0, preprocessed.length() - 1)) * -1;
            } else {
                return Integer.parseInt(preprocessed);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static List<String> splitPosition(String positionSpring) {
        List<String> parts = new ArrayList<String>();

        parts.add(positionSpring.substring(15, 59).trim());
        parts.add(positionSpring.substring(60, 73).trim());
        parts.add(positionSpring.substring(74, 87).trim());
        parts.add(positionSpring.substring(88, 101).trim());
        parts.add(positionSpring.substring(102, positionSpring.length()).trim());
        return parts;
    }
}
