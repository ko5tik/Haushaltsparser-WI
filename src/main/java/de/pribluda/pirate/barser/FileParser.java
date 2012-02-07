package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        // skip till field declaration
        int lineNum = 1;
        for (; lineNum < lines.length; lineNum++) {
            System.out.println(lines[lineNum]);
            if (lines[lineNum].trim().startsWith("Position")) {
                break;
            }
        }


        // process single lines
        lineNum++;
        for (; lineNum < lines.length; lineNum++) {
            System.err.println(lines[lineNum]);
            final List<Map<String, Object>> positions = extractPositions(lines[lineNum]);
            System.err.println("processed:" + positions);
        }


    }

    @Override
    List<Map<String, Object>> extractPositions(String positionString) {

        if (positionString.length() < 56)
            return null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> template = new HashMap<String, Object>();
        template.put("posNumber", positionString.substring(0, 7).trim());
        template.put("posDescription", positionString.substring(7, 56).trim());


        HashMap<String, Object> position = extractValue(positionString, 2013, "Ansatz", 56, 73);
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }

        position = extractValue(positionString, 2012, "Ansatz", 73, 87);
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }

        position = extractValue(positionString, 2011, "Ansatz", 87, 102);
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }

        position = extractValue(positionString, 2010, "Ergebnis", 102, positionString.length());
        if (position != null) {
            position.putAll(template);
            result.add(position);
        }


        return result;
    }

    private HashMap<String, Object> extractValue(String positionString, int year, String qualifier, int beginIndex, int endIndex) {
        if ( positionString.length() < endIndex)
            return null;

        final HashMap<String, Object> position = new HashMap<String, Object>();

        final String substring = positionString.substring(beginIndex, endIndex);
        System.err.println("extracted value:" + substring);
        final Integer value = DataParser.processNumber(substring);
        System.err.println("int value:" + value);
        if (value == null)
            return null;

        position.put("year", year);
        position.put("qualifier", qualifier);
        position.put("value", value);
        return position;
    }

    @Override
    List<Map<String, String>> extractDesignators(String position) {
        return null;
    }


    void processEntity(int page, String[] lines) {

        final Map<String, String> stringStringMap = DataParser.extractEntityData(lines[0]);

        // skip till field declaration
        int lineNum = 1;
        for (; lineNum < lines.length; lineNum++) {
            System.out.println(lines[lineNum]);
            if (lines[lineNum].trim().startsWith("Position")) {
                break;
            }
        }


        // process single lines
        lineNum++;
        for (; lineNum < lines.length; lineNum++) {
            final List<Map<String, String>> designators = extractDesignators(lines[lineNum]);
            final List<Map<String, Object>> positions = extractPositions(lines[lineNum]);
        }
    }

    public static AbstractFileParser createInstance() {
        return new FileParser();
    }

}
