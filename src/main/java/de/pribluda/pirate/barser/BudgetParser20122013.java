package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.mongodb.DBCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parser für Haushaltsplan 2012-2013.
 * since pdf documents are slightly different,   there are different parser programs.
 * database is the same
 *
 * @author Konstantin Pribluda
 */
public class BudgetParser20122013 extends AbstractFileParser {


    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("usage: fileParser <pdf file name>");
            System.exit(-1);
        }


        final BudgetParser20122013 fileParser = new BudgetParser20122013();

        // open PDF file
        final PdfReader pdfReader = new PdfReader(args[0]);

        DBCollection coll = connectMongo();

        fileParser.processFile(pdfReader, coll);

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
        designators[0].put(SOURCE, "2012/2013");

        designators[1] = new HashMap<String, Object>();
        designators[1].put(YEAR, 2012);
        designators[1].put(QUALIFIER, ANSATZ);
        designators[1].put(SOURCE, "2012/2013");

        designators[2] = new HashMap<String, Object>();
        designators[2].put(YEAR, 2011);
        designators[2].put(QUALIFIER, ANSATZ);
        designators[2].put(SOURCE, "2012/2013");

        designators[3] = new HashMap<String, Object>();
        designators[3].put(YEAR, 2010);
        designators[3].put(QUALIFIER, ERGEBNINS);
        designators[3].put(SOURCE, "2012/2013");
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


}
