/*
 * Copyright (c) 2011. By Konstantin Pribluda, all rights reserved. See accompanying
 * license file for exact licensing terms
 */

package de.pribluda.pirate.barser

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfReaderContentParser
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy

if (args.length < 1) {
    println(" please specify pdf document: parseCityGroovy <file name>")
    System.exit(-1);
}

pdfReader = new PdfReader(args[0]);
parser = new PdfReaderContentParser(pdfReader);
renderListener = new SimpleTextExtractionStrategy();

lines = parser.processContent(696, renderListener).getResultantText().split("\n");


lineNumber = 1;
//lines.each { print   (lineNumber++) ; print  it ; print  "\n"  }

// parse individual lines
lines.each {
    fragments = it.split "\\s+";
    fragments.each { print it}
    print "\n"

}



