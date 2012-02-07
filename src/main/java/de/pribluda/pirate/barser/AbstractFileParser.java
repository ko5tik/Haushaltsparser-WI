package de.pribluda.pirate.barser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * @param position position string
     * @return
     */
   abstract List<Map<String,Object>> extractPositions(String position);

    /**
     * extract
     * @param position position string
     * @return
     */
    abstract List<Map<String,String>> extractDesignators(String position);


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
}
