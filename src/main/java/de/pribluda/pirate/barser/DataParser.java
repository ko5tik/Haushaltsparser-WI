/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import de.pribluda.pirate.barser.beans.Entity;
import de.pribluda.pirate.barser.beans.Field;
import de.pribluda.pirate.barser.beans.Kostenstelle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities to parse various budget strings
 *
 * @author Konstantin Pribluda
 */
public class DataParser {
    public static final String ORGANISATIONSEINHEIT = "Organisation";
    public static final String KOSTENSSTELLENPREFIX = "kostens";
    public static final String PRODUCTPREFIX = "produkt";
    public static final String ENTITY = "Entity";
    public static final String PRODUCT_ID = "productId";
    public static final String AMT = "amt";
    public static final String DESCRIPTION = "description";
    public static final String ACCOUNT_ID = "accountId";

    public static Entity parseAmt(String line) {
        final String[] fragments = line.trim().split(":");

        if (fragments[0].startsWith(ORGANISATIONSEINHEIT)) {
            final String entry = fragments[1].trim();
            System.out.println("entity:" + entry);
            final Entity entity = new Entity();
            entity.setName(entry);
            return entity;

        }
        return null;
    }


    /**
     * extract entity data out of string - if any
     *
     * @param entityDesc
     * @return
     */
    public static Map<String, String> extractEntityData(String entityDesc) {
        Map<String, String> result = new HashMap<String, String>();

        final String[] fragments = entityDesc.trim().split(":");
        if (fragments[0].startsWith(ORGANISATIONSEINHEIT)) {
            result.put(ENTITY, fragments[1].trim());

            return result;

        }
        return null;

    }


    /**
     * extract subentity data if available. typically on second string.
     *
     * @param subentiyDesc
     * @return
     */
    public static Map<String, String> extractSubentity(String subentiyDesc) {
        Map<String, String> result = new HashMap<String, String>();

        final String[] fragments = subentiyDesc.trim().split(":");
        final String fragmentDesignator = fragments[0].toLowerCase();
        if (fragments.length < 2)
            return null;


        final String[] split = fragments[1].trim().split("\\s+");

        if (fragmentDesignator.startsWith(KOSTENSSTELLENPREFIX)) {
            result.put(ACCOUNT_ID, split[0].trim());
            processSubentityDescription(result, split);
            return result;
        } else if (fragmentDesignator.startsWith(PRODUCTPREFIX)) {
            result.put(PRODUCT_ID, split[0].trim());
            processSubentityDescription(result, split);
            return result;
        }

        return null;
    }

    /**
     * @param result
     * @param split
     */
    private static void processSubentityDescription(Map<String, String> result, String[] split) {
        result.put(AMT, split[1].trim());

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < split.length; i++) {
            sb.append(split[i]).append(" ");
        }
        result.put(ENTITY, sb.toString().trim());
    }

    public static Kostenstelle parseKostenstelle(String line) {
        final String[] fragments = line.trim().split(":");

        if (fragments[0].toLowerCase().startsWith(KOSTENSSTELLENPREFIX)) {
            final String[] split = fragments[1].trim().split("\\s+");
            String name = split[0].trim();
            String amt = split[1].trim();

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
        final String preprocessed = number.trim().replaceAll("\\.", "").replaceAll(",\\d\\d", "");
        try {
            if (preprocessed.endsWith("-")) {
                return Integer.parseInt(preprocessed.substring(0, preprocessed.length() - 1)) * -1;
            } else {
                return Integer.parseInt(preprocessed);
            }
        } catch (NumberFormatException e) {
            if (preprocessed.length() > 0) {
                System.err.println("parse failure:" + preprocessed);
            }
            return null;
        }
    }

    public static List<String> splitPosition(String positionString) {
        List<String> parts = new ArrayList<String>();

        parts.add(positionString.substring(15, 59).trim());
        parts.add(positionString.substring(60, 73).trim());
        parts.add(positionString.substring(74, 87).trim());
        parts.add(positionString.substring(88, 101).trim());
        parts.add(positionString.substring(102, positionString.length()).trim());
        return parts;
    }

    /**
     * extract designator and year parts
     *
     * @param positionString
     * @return
     * @deprecated handle via fields
     */

    public static List<String[]> splitDesignators(String positionString) {

        List<String[]> parts = new ArrayList<String[]>();
        parts.add(splitDesignatorAndYear(positionString.substring(58, 73).trim()));
        parts.add(splitDesignatorAndYear(positionString.substring(74, 88).trim()));
        parts.add(splitDesignatorAndYear(positionString.substring(89, 101).trim()));
        parts.add(splitDesignatorAndYear(positionString.substring(102, positionString.length()).trim()));

        return parts;
    }

    private static String[] splitDesignatorAndYear(String positionString) {
        final int lastSpace = positionString.lastIndexOf(" ");
        return new String[]{positionString.substring(0, lastSpace), positionString.substring(lastSpace + 1)};
    }


    /**
     * extract field objects from header string
     *
     * @return
     */
    private static Field[] extractFields() {
        return null;
    }
}
