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
        parser.processContent(PAGE_NUM, renderListener);

        System.out.println(renderListener.getResultantText());
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
