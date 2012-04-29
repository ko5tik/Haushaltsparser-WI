package de.pribluda.pirate.barser.beans;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test proper working of field beans
 *
 * @author Konstantin Pribluda
 */
public class FieldTest {

    public static final String string = "     5 Steuern und ähnliche Abgaben                          459.425.000-   437.685.000-   414.165.500- 423.357.356,60-";

    /**
     * shall extract field out of string
     */
    @Test
    public void testFieldExtraction() {
        final String toExtract = "      13       Aufwendungen f. Sach- und Dienstleistg.             9.676         9.673        10.021         8.917 \n";

        Field posNo = new Field(0, 10);
        Field description = new Field(15, 55);

        assertEquals("13", posNo.extract(toExtract));
        assertEquals("Aufwendungen f. Sach- und Dienstleistg.", description.extract(toExtract));


    }
}
