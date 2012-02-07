package de.pribluda.pirate.barser.beans;

/**
 * represents field object, can extract value from supplied string
 *
 * @author Konstantin Pribluda
 */
public class Field {

    String designator;
    int start;
    int end;

    /**
     *
     * @param start inclusive
     * @param end   exclusive
     */
    public Field(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public String getDesignator() {
        return designator;
    }

    public void setDesignator(String designator) {
        this.designator = designator;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String extract(String source) {
        return source.substring(start,end);
    }
}
