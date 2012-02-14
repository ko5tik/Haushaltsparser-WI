package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.mongodb.DBCollection;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * process supplied budget file and save data into mongo
 *
 * @author Konstantin Pribluda
 */
public class FileParser extends AbstractFileParser {


    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("usage: fileParser <pdf file name>");
            System.exit(-1);
        }


        final FileParser fileParser = new FileParser();

        // open PDF file
        final PdfReader pdfReader = new PdfReader(args[0]);

        DBCollection coll = connectMongo();

        fileParser.processFile(pdfReader, coll);
        System.err.println("reader:" + pdfReader);


    }

    static Pattern[] titles = {
            Pattern.compile("HH Ansatz\\s+2013"),
            Pattern.compile("HH Ansatz\\s+2012"),
            Pattern.compile("HH Ansatz\\s+2011"),
            Pattern.compile("Ergebnis\\s+2010")};

    static final Map<String, Object>[] designators = new Map[4];


    static {
        designators[0] = new HashMap<String, Object>();
        designators[0].put(YEAR, 2013);
        designators[0].put(QUALIFIER, ANSATZ);

        designators[1] = new HashMap<String, Object>();
        designators[1].put(YEAR, 2012);
        designators[1].put(QUALIFIER, ANSATZ);

        designators[2] = new HashMap<String, Object>();
        designators[2].put(YEAR, 2011);
        designators[2].put(QUALIFIER, ANSATZ);

        designators[3] = new HashMap<String, Object>();
        designators[3].put(YEAR, 2010);
        designators[3].put(QUALIFIER, ERGEBNINS);
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

    /**
     * extract positions out of supplied string
     *
     * @param positionString
     * @param valueLocations
     * @return
     */
    @Override
    List<Map<String, Object>> extractPositions(String positionString, int[][] valueLocations) {
      //  System.err.println(positionString);
        if (positionString.length() < 56)
            return null;


        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        final Matcher matcher = NUMBER_PATTERN.matcher(positionString);

        matcher.region(56, positionString.length());


        // iterate over value locations
        boolean found = matcher.find();

        for (int i = 0; i < valueLocations.length; i++) {
            int[] location = valueLocations[i];
            // do we have  match?
            if (found) {
                final int massCenter = (matcher.start() + matcher.end()) / 2;
                if (massCenter >= location[0] && massCenter <= location[1]) {
                    // mass center inside region, take it

                    final Integer value = extractValue(positionString, matcher.start(), matcher.end());
                    if (value != null) {
                        Map<String, Object> position = new HashMap<String, Object>();
                        position.putAll(valueTemplates()[i]);
                        position.put(AbstractFileParser.VALUE_TAG, value);
                        result.add(position);
                    }
                    // find next match if available
                    found = matcher.find();
                }
            } else {
                // nothing found, bail out
                break;
            }
        }

        return result;
    }


}
