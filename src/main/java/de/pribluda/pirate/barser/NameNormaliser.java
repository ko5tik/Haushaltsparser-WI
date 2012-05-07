package de.pribluda.pirate.barser;

/**
 * normalise supplied name
 *
 * @author Konstantin Pribluda
 */
public class NameNormaliser {

    public String normalize(String source) {
        return source.replaceAll("[/\\s]","_");
    }
}
