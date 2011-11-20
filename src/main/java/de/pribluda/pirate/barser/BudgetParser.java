/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reseerved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.IOException;

/**
 *
 */
public class BudgetParser {

    public static final int PAGE_NUM = 696;
    public static final String ORGANISATIONSEINHEIT = "Organisation";
    public static final String KOSTENSSTELLENPREFIX = "Kostens";

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

        final PdfDictionary pageDictionary = pdfReader.getPageN(PAGE_NUM);

        System.out.println("page dictionary:\t" + pageDictionary.toString());

        //printRecursive("\t", pageDictionary);


        PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);

        final SimpleTextExtractionStrategy renderListener = new SimpleTextExtractionStrategy();


        final String[] lines = parser.processContent(PAGE_NUM, renderListener).getResultantText().split("\n");

        // page processed,  gather  usefull information
        for (String line : lines) {
            System.out.println("line:" + line);
        }


        int lineIndex = 0;

        // extract organisational entity
        String entity = null;
        String amt = null;
        String kostenstelle = null;
        String title;
        for (; lineIndex < lines.length; lineIndex++) {
            final String line = lines[lineIndex];
            System.out.println(line);
            final String[] fragments = line.trim().split(":");

            if (fragments[0].startsWith(ORGANISATIONSEINHEIT)) {
                entity = fragments[1].trim();
                System.out.println("entity:" + entity);
            } else if (fragments[0].startsWith(KOSTENSSTELLENPREFIX)) {
                final String[] split = fragments[1].trim().split("\\s+");
                kostenstelle = split[0];
                amt = split[1];

                final StringBuilder titleBuilder = new StringBuilder();
                for (int i = 2; i < split.length; i++) {
                    titleBuilder.append(split[i]).append(" ");
                }
                title = titleBuilder.toString().trim();

                System.out.println("amt:" + amt);
                System.out.println("kostenstelle:" + kostenstelle);
                System.out.println("title:" + title);

                break;
            }
        }
        lineIndex++;

        // bypass all the lines until "Position
        for (; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].trim().startsWith("Position")) {

                break;
            }
        }

        lineIndex++;

        // from here we have content lines
        for (; lineIndex < lines.length; lineIndex++) {

        }
    }


    static void printRecursive(String prefix, PdfDictionary dictionary) {
        for (PdfName key : dictionary.getKeys()) {
            final PdfObject pdfObject = dictionary.get(key);
            System.out.println(prefix + key + " / " + pdfObject + " / " + pdfObject.getClass());
            if (pdfObject instanceof PdfDictionary) {
                printRecursive("\t" + prefix, (PdfDictionary) pdfObject);
            }
        }

    }
}
