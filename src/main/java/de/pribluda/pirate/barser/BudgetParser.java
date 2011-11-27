/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reseerved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import de.pribluda.pirate.barser.beans.Dezernat;
import de.pribluda.pirate.barser.beans.Kostenstelle;
import de.pribluda.pirate.barser.beans.Position;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BudgetParser {

    public static final int PAGE_NUM = 64;


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


        Mongo m = new Mongo();
        DB db = m.getDB("mydb");
        DBCollection coll = db.getCollection("positions");

        // dezernat 1
        processRange(pdfReader, 64, 234, coll);
        // dezernat 2
        processRange(pdfReader, 246, 274, coll);
        // dezernat 4
        processRange(pdfReader, 285, 327, coll);
        //dezernat 5
         processRange(pdfReader, 340, 452, coll);
        //dezernat 6
         processRange(pdfReader, 461, 601, coll);
        // dezernat 7
         processRange(pdfReader, 612, 636, coll);
        // dezernat 8
         processRange(pdfReader, 645, 816, coll);

    }

    private static void processRange(PdfReader pdfReader, int from, int to, DBCollection coll) throws IOException {
        for (int i = from; i <= to; i++) {
            System.err.println("processing page: " + i);
            processPage(pdfReader, coll, i);
        }
    }

    private static Kostenstelle processPage(PdfReader pdfReader, DBCollection dbCollection, int pageNum) throws IOException {

        Kostenstelle kostenstelle = new Kostenstelle();
        PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);

        final SimpleTextExtractionStrategy renderListener = new SimpleTextExtractionStrategy();


        final String[] lines = parser.processContent(pageNum, renderListener).getResultantText().split("\n");


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
            kostenstelle = DataParser.parseKostenstelle(lines[lineIndex]);
            if (kostenstelle != null)
                break;
        }

        System.out.println("Kostenstelle:" + kostenstelle);


        // now we got kostenstelle,  skip  to real positions

        for (; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].trim().startsWith("Position"))
                break;
        }

        Map basicDBObject = new HashMap();
        basicDBObject.put("dezernat", dezernat.getName());
        basicDBObject.put("amt", kostenstelle.getAmt());
        basicDBObject.put("kostenstelle", kostenstelle.getName());
        basicDBObject.put("title", kostenstelle.getTitle());


        // process individual positions
        for (; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].length() < 15)
                continue;

            final List<String> parts = DataParser.splitPosition(lines[lineIndex]);

            Position position = new Position();
            position.setName(parts.get(0));

            basicDBObject.put("position", position.getName());


            position.setData(new HashMap<String, Integer>());


            Integer value = DataParser.processNumber(parts.get(1));
            if (value != null) {
                position.getData().put("2011", value);

                BasicDBObject bdo = new BasicDBObject();
                bdo.putAll(basicDBObject);

                bdo.append("year", 2011);
                bdo.append("value", value);
                dbCollection.insert(bdo);
            }

            value = DataParser.processNumber(parts.get(2));
            if (value != null) {
                position.getData().put("2010", value);

                BasicDBObject bdo = new BasicDBObject();
                bdo.putAll(basicDBObject);

                bdo.append("year", 2010);
                bdo.append("value", value);

                dbCollection.insert(bdo);
            }

            value = DataParser.processNumber(parts.get(3));
            if (value != null) {
                position.getData().put("2009", value);

                BasicDBObject bdo = new BasicDBObject();
                bdo.putAll(basicDBObject);

                bdo.append("year", 2009);
                bdo.append("value", value);

                dbCollection.insert(bdo);
            }

            value = DataParser.processNumber(parts.get(4));
            if (value != null) {
                position.getData().put("2008", value);

                BasicDBObject bdo = new BasicDBObject();
                bdo.putAll(basicDBObject);

                bdo.append("year", 2008);
                bdo.append("value", value);

                dbCollection.insert(bdo);
            }

            System.out.println("position:" + position);
        }

        return kostenstelle;
    }


}
