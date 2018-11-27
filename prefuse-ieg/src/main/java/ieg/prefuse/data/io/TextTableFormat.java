package ieg.prefuse.data.io;

import javax.xml.bind.annotation.*;

import prefuse.data.io.*;
import prefuse.data.parser.*;

/**
 * Description of data saved in a tabular text file. This description can be
 * loaded from XML via JAXB and be used to initialize a prefuse
 * {@link TableReader}.
 * 
 * @author Rind
 * 
 */
public class TextTableFormat {
    // TODO configure FixedWidthTextTableReader

    /**
     * either CSV or RegEx delimited
     */
    @XmlElement(required = true)
    private Method method = Method.CSV;

    /**
     * first line of the file has column headers or data
     */
    @XmlElement(name = "has-header", required = false)
    private boolean hasHeader = true;

    /**
     * parse temporal data to {@link Date} or keep it as a {@link String}
     */
    @XmlElement(name = "parse-dates", required = false)
    private boolean parseDates = true;

    /**
     * delimiter for RegEx method. This is ignored for other methods.
     */
    @XmlElement(name = "delimiter-regex", required = false)
    private String delimiterRegex = "\t";

    /**
     * Constructor with default values: CSV with header and parsing dates.
     */
    public TextTableFormat() {
        super();
    }

    /**
     * Constructor with specified settings
     * 
     * @param method
     *            either CSV or RegEx delimited
     * @param hasHeader
     *            first line is header or data
     * @param parseDates
     *            parse temporal data or not
     * @param delimiterRegex
     *            delimiter for RegEx method
     */
    public TextTableFormat(Method method, boolean hasHeader,
            boolean parseDates, String delimiterRegex) {
        this.method = method;
        this.hasHeader = hasHeader;
        this.parseDates = parseDates;
        this.delimiterRegex = delimiterRegex;
    }

    /**
     * initialize a prefuse {@link TableReader} for the data described here.
     * 
     * @return a table reader
     */
    public TableReader getTableReader() {
        AbstractTextTableReader reader;
        if (this.method == Method.REGEX)
            reader = new DelimitedTextTableReader(this.delimiterRegex,
                    getParserFactory());
        else
            reader = new CSVTableReader(getParserFactory());
        reader.setHasHeader(this.hasHeader);

        return reader;
    }

    /**
     * get a parser factory that either understands temporal data or not
     */
    private ParserFactory getParserFactory() {
        if (this.parseDates) {
            return ParserFactory.getDefaultFactory();
        } else {
            // skipped parsers: DateParser, TimeParser, DateTimeParser
            final DataParser[] nonTemporalParsers = new DataParser[] {
                    //new IntParser(), new LongParser(), 
                    new DoubleParser(),
                    new FloatParser(), new BooleanParser(),
                    new ColorIntParser(), new IntArrayParser(),
                    new LongArrayParser(), new FloatArrayParser(),
                    new DoubleArrayParser(), new StringParser() };
            return new ParserFactory(nonTemporalParsers);
        }
    }

    @XmlEnum
    public enum Method {
        @XmlEnumValue("csv")
        CSV, @XmlEnumValue("regex-delimited")
        REGEX
    }
}