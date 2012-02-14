package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * abstract base file parser class providing PDF file traversal.  instance shall be created by subclasses
 *
 * @author Konstantin Pribluda
 */
public abstract class AbstractFileParser {


    public static final String VALUE_TAG = "value";
    public static final String YEAR = "year";
    public static final String QUALIFIER = "qualifier";
    public static final String ANSATZ = "Ansatz";
    public static final String ERGEBNINS = "Ergebnins";
    public static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9\\,\\.-]+");

    public static DBCollection connectMongo() throws UnknownHostException {
        Mongo m = new Mongo();
        DB db = m.getDB("mydb");

        return db.getCollection("budget");
    }

    public abstract Map<String, Object>[] valueTemplates();

    /**
     * process incoming page
     *
     * @param parser PDF parser
     * @param i      page number
     * @param coll
     * @throws java.io.IOException
     */

    public void processPage(PdfReaderContentParser parser, int i, DBCollection coll) throws IOException {

        System.out.println("processing page:" + i);
        final SimpleTextExtractionStrategy renderListener = new SimpleTextExtractionStrategy();
        final String resultantText = parser.processContent(i, renderListener).getResultantText();
        if (resultantText == null)
            return;

        final String[] lines = resultantText.split("\n");


        final Map<String, String> entityMap = DataParser.extractEntityData(lines[0]);
        if (entityMap == null)
            return;

        System.out.println(" ... accepted");

        final Map<String, String> subentityMap = DataParser.extractSubentity(lines[1]);


        // skip till field declaration , and extract field values
        int[][] valueLocations = null;
        int lineNum = 1;
        for (; lineNum < lines.length; lineNum++) {
            if (lines[lineNum].trim().startsWith("Position")) {
                valueLocations = extractValueLocations(lines[lineNum]);
                break;
            }
        }


        // process single lines
        lineNum++;
        for (; lineNum < lines.length; lineNum++) {

            final List<Map<String, Object>> positions = extractPositions(lines[lineNum], valueLocations);
            if (positions != null) {
                // save to database
                for (Map<String, Object> position : positions) {
                    position.putAll(entityMap);
                    if (subentityMap != null) {
                        position.putAll(subentityMap);
                    }
                    BasicDBObject bdo = new BasicDBObject();
                    bdo.putAll(position);
                    coll.insert(bdo);
                }
            } else {
                //  System.err.println("unable to process:" + lines[lineNum]);
            }
        }


    }


    /**
     * extract individual positions.
     *
     * @param positionString
     * @param valueLocations
     * @return
     */
    abstract List<Map<String, Object>> extractPositions(String positionString, int[][] valueLocations);


    protected void processFile(PdfReader pdfReader, DBCollection coll) throws IOException {
        final int numberOfPages = pdfReader.getNumberOfPages();
        System.err.println("number of pages:" + numberOfPages);

        PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);

        // walk through pages and process them
        for (int i = 1; i <= numberOfPages; i++) {
            processPage(parser, i, coll);
        }

    }

    protected Integer extractValue(String positionString, int beginIndex, int endIndex) {
        //System.err.println("extracting from " + beginIndex + " to" + endIndex + " of " + positionString.length());
        if (positionString.length() < endIndex || beginIndex >= endIndex)
            return null;

        final String substring = positionString.substring(beginIndex, endIndex);
     //   System.err.println("value: |" + substring + "|");
        //System.err.println("extracted value:" + substring);
        final Integer value = DataParser.processNumber(substring);
     //   if (value == null) {
     //       System.err.println("failed value: " + substring);
     //   }
        return value;
    }

    public int[][] extractValueLocations(String header) {
        System.err.println(header);
        final Pattern[] titles = valueTitles();
        final int[][] positions = new int[titles.length][2];
        for (int i = 0; i < titles.length; i++) {

            final Matcher matcher = titles[i].matcher(header);
            if (matcher.find()) {
                positions[i][0] = matcher.start();
                positions[i][1] = matcher.end();
            //    System.err.println("from: " + positions[i][0] + " to: " + positions[i][1] + "|" + header.substring(positions[i][0], positions[i][1]) + "|");
            }
        }

        return positions;
    }

    public abstract Pattern[] valueTitles();
}
