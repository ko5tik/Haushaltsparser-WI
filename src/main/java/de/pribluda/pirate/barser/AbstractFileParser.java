package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

import java.io.IOException;
import java.util.HashMap;
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


    /**
     * perform necessary page processing
     *
     * @param parser PDF parser
     * @param i      page number
     * @throws IOException
     */
    public abstract void processPage(PdfReaderContentParser parser, int i) throws IOException;


    /**
     * extract individual positions.
     *
     * @param positionString
     * @param valueLocations
     * @return
     */
   abstract List<Map<String,Object>> extractPositions(String positionString, int[][] valueLocations);


    protected void processFile(PdfReader pdfReader) throws IOException {
        final int numberOfPages = pdfReader.getNumberOfPages();
        System.err.println("number of pages:" + numberOfPages);

        PdfReaderContentParser parser = new PdfReaderContentParser(pdfReader);

        final AbstractFileParser instance = FileParser.createInstance();

        // walk through pages and process them
        for (int i = 1; i <= numberOfPages; i++) {
            processPage(parser, i);
        }

    }

    protected HashMap<String, Object> extractValue(String positionString, int year, String qualifier, int beginIndex, int endIndex) {
        //System.err.println("extracting from " + beginIndex + " to" + endIndex + " of " + positionString.length());
        if (positionString.length() < endIndex || beginIndex >= endIndex)
            return null;

        final HashMap<String, Object> position = new HashMap<String, Object>();

        final String substring = positionString.substring(beginIndex, endIndex);
        //System.err.println("extracted value:" + substring);
        final Integer value = DataParser.processNumber(substring);
        //System.err.println("int value:" + value);
        if (value == null)
            return null;

        position.put("year", year);
        position.put("qualifier", qualifier);
        position.put("value", value);
        return position;
    }

    public int[][] extractValueLocations(String header) {
        final Pattern[] titles = valueTitles();
        final int[][] positions = new int[titles.length][2];
        for (int i = 0; i < titles.length; i++) {

            final Matcher matcher = titles[i].matcher(header);
            if (matcher.find()) {
                positions[i][0] = matcher.start();
                positions[i][1] = matcher.end();
               // System.err.println(titles[i] + ":" + positions[i][0] + " / " + positions[i][1]);
            }
        }

        return positions;
    }

    public abstract Pattern[] valueTitles();
}
