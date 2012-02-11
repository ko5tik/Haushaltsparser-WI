package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


        fileParser.processFile(pdfReader);
        System.err.println("reader:" + pdfReader);


    }

    /**
     * process incoming page
     *
     * @param parser PDF parser
     * @param i      page number
     * @throws IOException
     */
    @Override
    public void processPage(PdfReaderContentParser parser, int i) throws IOException {

        final SimpleTextExtractionStrategy renderListener = new SimpleTextExtractionStrategy();
        final String resultantText = parser.processContent(i, renderListener).getResultantText();
        if (resultantText == null)
            return;

        final String[] lines = resultantText.split("\n");


        final Map<String, String> entityMap = DataParser.extractEntityData(lines[0]);
        if (entityMap == null)
            return;

        final Map<String, String> subentityMap = DataParser.extractSubentity(lines[1]);


        // skip till field declaration , and extract field values
        int[][] valueLocations = null;
        int lineNum = 1;
        for (; lineNum < lines.length; lineNum++) {
            if (lines[lineNum].trim().startsWith("Position")) {
                System.err.println(lines[lineNum]);
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
            } else {
                System.err.println("unable to process:" + lines[lineNum]);
            }
        }


    }

    static Pattern[] titles = {
            Pattern.compile("HH Ansatz\\s+2013"),
            Pattern.compile("HH Ansatz\\s+2012"),
            Pattern.compile("HH Ansatz\\s+2011"),
            Pattern.compile("Ergebnis\\s+2010")};

    @Override
    public Pattern[] valueTitles() {
        return titles;
    }

    @Override
    List<Map<String, Object>> extractPositions(String positionString, int[][] valueLocations) {

        if (positionString.length() < 56)
            return null;

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> template = new HashMap<String, Object>();
        template.put("posNumber", positionString.substring(0, 7).trim());
        template.put("posDescription", positionString.substring(7, 56).trim());


        HashMap<String, Object> position = extractValue(positionString, 2013, "Ansatz", valueLocations[0][0], valueLocations[0][1]);
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }

        position = extractValue(positionString, 2012, "Ansatz", valueLocations[1][0], valueLocations[1][1]);
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }

        position = extractValue(positionString, 2011, "Ansatz", valueLocations[2][0], valueLocations[2][1]);
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }
        // lst segment maz be shorter
        position = extractValue(positionString, 2010, "Ergebnis", valueLocations[3][0], positionString.length());
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }


        return result;
    }


    public static AbstractFileParser createInstance() {
        return new FileParser();
    }

}
