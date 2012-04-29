/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reseerved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.mongodb.DBCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parser für Haushaltsplan 2010-2011
 */
public class BudgetParser extends AbstractFileParser {

    /**
     * parse and process document
     *
     * @param args argv[0] desigfnates pdf file to process
     */
    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.err.println("usage: budgetParser <pdf file name>");
            System.exit(-1);
        }


        // open PDF file
        final PdfReader pdfReader = new PdfReader(args[0]);
        final BudgetParser budgetParser = new BudgetParser();

        DBCollection coll = connectMongo();

        budgetParser.processFile(pdfReader, coll);
    }


    static Pattern[] titles = {
            Pattern.compile("HH Ansatz\\s+2011"),
            Pattern.compile("HH Ansatz\\s+2010"),
            Pattern.compile("HH Ansatz\\s+(20)?09"),
            Pattern.compile("Ergebnis\\s+2008")};

    static final Map<String, Object>[] designators = new Map[4];


    static {
        designators[0] = new HashMap<String, Object>();
        designators[0].put(YEAR, 2011);
        designators[0].put(QUALIFIER, ANSATZ);
        designators[0].put(SOURCE, "2010/2011");

        designators[1] = new HashMap<String, Object>();
        designators[1].put(YEAR, 2010);
        designators[1].put(QUALIFIER, ANSATZ);
        designators[1].put(SOURCE, "2010/2011");

        designators[2] = new HashMap<String, Object>();
        designators[2].put(YEAR, 2009);
        designators[2].put(QUALIFIER, ANSATZ);
        designators[2].put(SOURCE, "2010/2011");

        designators[3] = new HashMap<String, Object>();
        designators[3].put(YEAR, 2008);
        designators[3].put(QUALIFIER, ERGEBNINS);
        designators[3].put(SOURCE, "2010/2011");
    }


    /**
     * regexes to extract header positions
     *
     * @return
     */
    @Override
    public Pattern[] valueTitles() {
        return titles;
    }

    @Override
    public Map<String, Object>[] valueTemplates() {
        return designators;
    }

}
