package de.pribluda.pirate.barser;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * test proper working of file parser
 *
 * @author Konstantin Pribluda
 */
public class BudgetParser20122013Test {


    @Test
    public void testParsingOfEntries() {
        final String header = "     Position / Bezeichung                                HH Ansatz  2013 HH Ansatz  2012 HH Ansatz  2011 Ergebnis 2010 ";

        final BudgetParser20122013 fileParser = new BudgetParser20122013();

        final int[][] locations = fileParser.extractValueLocations(header);

        String values = "     1 privatrechtliche Leistungsentgelte                     12.368.143-    12.460.103-    12.404.380- 12.982.519,74-";

        List<Map<String, Object>> positions = fileParser.extractPositions(values, locations);

        assertEquals(new Integer(-12368143), positions.get(0).get(AbstractFileParser.VALUE_TAG));
        assertEquals(new Integer(-12460103), positions.get(1).get(AbstractFileParser.VALUE_TAG));
        assertEquals(new Integer(-12404380), positions.get(2).get(AbstractFileParser.VALUE_TAG));
        assertEquals(new Integer(-12982519), positions.get(3).get(AbstractFileParser.VALUE_TAG));

        values = "     1 privatrechtliche Leistungsentgelte                     12.368.143-                   12.404.380- 12.982.519,74-";

        positions = fileParser.extractPositions(values, locations);

        assertEquals(new Integer(-12368143), positions.get(0).get(AbstractFileParser.VALUE_TAG));
        assertEquals(new Integer(-12404380), positions.get(1).get(AbstractFileParser.VALUE_TAG));
        assertEquals(2011, positions.get(1).get(AbstractFileParser.YEAR));
        assertEquals(new Integer(-12982519), positions.get(2).get(AbstractFileParser.VALUE_TAG));
        assertEquals(2010, positions.get(2).get(AbstractFileParser.YEAR));
    }


    @Test
    public void testTitleExtraction() {
        String values = "     1 privatrechtliche Leistungsentgelte                     12.368.143-    12.460.103-    12.404.380- 12.982.519,74-";
        final String title = AbstractFileParser.extractPositionName(values);

        assertEquals("privatrechtliche Leistungsentgelte", title);
    }
}
