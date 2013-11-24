package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.mongodb.DBCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * parse budget information from  30.8.2013
 */
public class BudgetParser2013 extends  AbstractFileParser {


    private static final Map<String,Object>[] designators = new Map[4];

    static {
        designators[0] = new HashMap<String, Object>();
        designators[0].put(YEAR, 2015);
        designators[0].put(QUALIFIER, ANSATZ);
        designators[0].put(SOURCE, "2013");

        designators[1] = new HashMap<String, Object>();
        designators[1].put(YEAR, 2014);
        designators[1].put(QUALIFIER, ANSATZ);
        designators[1].put(SOURCE, "2013");

        designators[2] = new HashMap<String, Object>();
        designators[2].put(YEAR, 2013);
        designators[2].put(QUALIFIER, ANSATZ);
        designators[2].put(SOURCE, "2013");

        designators[3] = new HashMap<String, Object>();
        designators[3].put(YEAR, 2012);
        designators[3].put(QUALIFIER, ERGEBNINS);
        designators[3].put(SOURCE, "2013");
    }

    // patterns to determine proper header values
    private static final Pattern[] patterns = {
            Pattern.compile("HH Ansatz\\s+2015"),
            Pattern.compile("HH Ansatz\\s+2014"),
            Pattern.compile("HH Ansatz\\s+2013"),
            Pattern.compile("Ergebnis\\s+2012")
    };

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
        final BudgetParser20102011 budgetParser = new BudgetParser20102011();

        DBCollection coll = connectMongo();

        budgetParser.processFile(pdfReader, coll);
    }

    @Override
    public Map<String, Object>[] valueTemplates() {
        return designators;
    }

    @Override
    public Pattern[] valueTitles() {
        return patterns;
    }
}
