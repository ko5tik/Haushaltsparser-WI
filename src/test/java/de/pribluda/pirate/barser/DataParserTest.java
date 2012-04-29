/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying license file for exact licensing terms
 */

package de.pribluda.pirate.barser;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * test data parsing
 *
 * @author e078262 ( Konstantin Pribluda )
 */
public class DataParserTest {
    static final String toParse =
            " Organisationseinheit:                        Entity VIII\n" +
                    "      Kostenstelle/Kostenstellengruppe:            1300112          40 Budget Riederbergschule\n" +
                    "                                                   Teilergebnishaushalt\n" +
                    "                                                        - EURO -\n" +
                    "      Position    Bezeichung                               HH Ansatz 2011 HH Ansatz 2010 HH Ansatz 09 Ergebnis 2008 \n" +
                    "      \n" +
                    "      1        privatrechtliche Leistungsentgelte                                                                  \n" +
                    "      2        �ffentlich-rechtliche Leistungsentgelte                                                             \n" +
                    "      3        Kostenerstattungen und Kostenumlagen                                                                \n" +
                    "      4        Bestandsver�nder./andere akt.Eigenleist.                                                            \n" +
                    "      5        Steuern und �hnliche Abgaben                                                                        \n" +
                    "      6        Ertr�ge aus Transferleistungen                                                                      \n" +
                    "      7        Zuwendungen und allgemeine Umlagen                                                                  \n" +
                    "      8        Ertr�ge Aufl�sg.v.Sopos Inv.zuweisungen                                                             \n" +
                    "      9        sonstige ordentliche Ertr�ge                                                                        \n" +
                    "      \n" +
                    "      10        ordentliche Ertr�ge                                                                                \n" +
                    "      \n" +
                    "      11       Personalaufwendungen                                                                                \n" +
                    "      12       Versorgungsaufwendungen                                                                             \n" +
                    "      13       Aufwendungen f. Sach- und Dienstleistg.             9.676         9.673        10.021         8.917 \n" +
                    "      14       Abschreibungen                                      2.540         2.543                       5.165 \n" +
                    "      15       Aufwend.f�r Zuweisg./Zusch.+ Finanzausg.                                                            \n" +
                    "      16       Steueraufw.u.Aufw.aus gesetzl.Uml.verpfl                                                            \n" +
                    "      17       Transferaufwendungen                                                                                \n" +
                    "      18       sonstige ordentliche Aufwendungen                                                                   \n" +
                    "      \n" +
                    "      19        ordentlicher Aufwand                              12.216        12.216        10.021        14.082 \n" +
                    "      \n" +
                    "      20        Verwaltungsergebnis                               12.216        12.216        10.021        14.082 \n" +
                    "      \n" +
                    "      21       Finanzertr�ge                                                                                       \n" +
                    "      22       Zinsen und sonstige Finanzaufwendung.                                                               \n" +
                    "      \n" +
                    "      23        Finanzergebnis                                                                                     \n" +
                    "      \n" +
                    "      24        ordentliches Ergebnis                             12.216        12.216        10.021        14.082 \n" +
                    "      \n" +
                    "      25       au�erordentliche Ertr�ge                                                                            \n" +
                    "      26       au�erordentliche Aufwendungen                                                                       \n" +
                    "      \n" +
                    "      27        au�erordentliches Ergebnis                                                                         \n" +
                    "      \n" +
                    "      28        Jahresergebnis                                    12.216        12.216        10.021        14.082 \n" +
                    "      \n" +
                    "      29        Belastung durch dLV                              135.949       135.408        99.866        99.670 \n" +
                    "      30        Entlastung durch dLV                                                                               \n" +
                    "      \n" +
                    "      31        Ergebnis aus dLV                                 135.949       135.408        99.866        99.670 \n" +
                    "      \n" +
                    "      32        Belastung durch Umlagen                           68.823        67.634        70.025       349.033 \n" +
                    "      33        Entlastung durch Umlagen                         216.988-      215.258-      179.912-      462.785-\n" +
                    "      \n" +
                    "      34        Ergebnis aus Umlagen                             148.165-      147.624-      109.887-      113.752-\n" +
                    "      \n" +
                    "      35        Ergebnis nach ILV                                                                                  \n" +
                    "\n" +
                    "";

    /**
     * shall give back null in case number is not parseable
     */
    @Test
    public void testUnparsableNumberProducesNull() {
        assertNull(DataParser.processNumber("232ccdfrefdc"));
        assertNull(DataParser.processNumber("-"));
        assertNull(DataParser.processNumber(""));
        assertNull(DataParser.processNumber(null));
    }


    /**
     * in case '-' sign is found on the end, shall result in negative number
     */
    @Test
    public void testMinusOnTheEndIsNegative() {
        assertEquals(new Integer(-239), DataParser.processNumber("239-"));
    }

    /**
     * shall parse positive integer
     */
    @Test
    public void testPositiveParsing() {
        assertEquals(new Integer(239), DataParser.processNumber("239"));
    }


    /**
     * decimal dots shall be ignored
     */
    @Test
    public void testParsingWithDots() {
        assertEquals(new Integer(123239), DataParser.processNumber("123.239"));
    }

    /**
     * shall split  and deliver position parts based on  defined field borders
     */
    @Test
    public void testPositionSplit() {

        final List<String> strings = DataParser.splitPosition(toParse.split("\n")[20]);
        assertEquals(5, strings.size());

        assertEquals("Aufwendungen f. Sach- und Dienstleistg.", strings.get(0));
        assertEquals("9.676", strings.get(1));
        assertEquals("9.673", strings.get(2));
        assertEquals("10.021", strings.get(3));
        assertEquals("8.917", strings.get(4));
    }


    /**
     * shall produce list of pairs of year and designator from first  table string
     */
    @Test
    public void testTagAndYearParsing() {
        final List<String[]> strings = DataParser.splitDesignators(toParse.split("\n")[4]);

        assertEquals(4, strings.size());

        assertEquals("HH Ansatz", strings.get(0)[0]);
        assertEquals("2011", strings.get(0)[1]);


        assertEquals("HH Ansatz", strings.get(1)[0]);
        assertEquals("2010", strings.get(1)[1]);

        assertEquals("HH Ansatz", strings.get(2)[0]);
        assertEquals("09", strings.get(2)[1]);

        assertEquals("Ergebnis", strings.get(3)[0]);
        assertEquals("2008", strings.get(3)[1]);

    }


    @Test
    public void testFractionIsSupressedAlsoForNegatives() {

        assertEquals(new Integer(-161575), DataParser.processNumber("161.575,03-"));
        assertEquals(new Integer(161575), DataParser.processNumber("161.575,03"));
    }

}
