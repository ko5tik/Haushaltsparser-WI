/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reseerved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import de.pribluda.pirate.barser.beans.Dezernat;
import de.pribluda.pirate.barser.beans.Kostenstelle;
import de.pribluda.pirate.barser.beans.Position;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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


        processPage(pdfReader);
    }

    private static Kostenstelle processPage(PdfReader pdfReader) throws IOException {

        Kostenstelle result = new Kostenstelle();
        PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);

        final SimpleTextExtractionStrategy renderListener = new SimpleTextExtractionStrategy();


        final String[] lines = parser.processContent(PAGE_NUM, renderListener).getResultantText().split("\n");


        int lineIndex = 0;
        Dezernat dezernat = null;

        // extract organisational entity
        for (; lineIndex < lines.length; lineIndex++) {
            dezernat = DataParser.parseAmt(lines[lineIndex]);
            if (dezernat != null)
                break;
        }


        System.out.println("dezernat:" + dezernat);

        // parse until we get proper kostenstelle
        // extract organisational entity
        for (; lineIndex < lines.length; lineIndex++) {
            result = DataParser.parseKostenstelle(lines[lineIndex]);
            if (result != null)
                break;
        }

        System.out.println("Kostenstelle:" + result);

        // now we got kostenstelle,  skip  to real positions

        for (; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].trim().startsWith("Position"))
                break;
        }

        // process indicivudal positions
        for (; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].length() < 15)
                continue;

            final List<String> parts = DataParser.splitPosition(lines[lineIndex]);

            Position position = new Position();
            position.setName(parts.get(0));

            position.setData(new HashMap<String, Integer>());

            Integer value = DataParser.processNumber(parts.get(1));
            if (value != null) {
                position.getData().put("2011", value);

            }

            value = DataParser.processNumber(parts.get(2));
            if (value != null) {
                position.getData().put("2010", value);
            }

            value = DataParser.processNumber(parts.get(3));
            if (value != null) {
                position.getData().put("2009", value);
            }

            value = DataParser.processNumber(parts.get(4));
            if (value != null) {
                position.getData().put("2008", value);
            }

            System.out.println("position:" + position);
        }

        return result;
    }


}
