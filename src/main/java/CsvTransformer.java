import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import utils.DateUtils;
import utils.NumberUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvTransformer {

    private CSVReader csvReader;
    private static char CSV_READER_SEPARATOR = ';';
    private static char CSV_READER_QUOTE = '\"';

    private CSVWriter csvWriter;
    private static char CSV_WRITER_SEPARATOR = '|';
    private static char CSV_WRITER_QUOTE = '\'';

    private static String NUMBER_REGEX = "[0-9.,]+";
    private static String CURRENCY_REGEX = "[A-Za-z\\$\\€]+";
    private static String DATE_REGEX = "(\\d{1,2}(\\.|\\-|\\/|\\s)\\d{1,2}(\\.|\\-|\\/|\\s)\\d{4})|" +
            "(\\d{4}(\\.|\\-|\\/|\\s)\\d{1,2}(\\.|\\-|\\/|\\s)\\d{1,2})";
    private static DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    CsvTransformer(String pathIn, String pathOut) {
        csvReader = getCSVReader(pathIn);
        csvWriter = getCSVWriter(pathOut);
        convert();
    }

    /**
     * Reads the file located in the path and configures the CSVReader with the separators and quote characters
     * selected.
     *
     * @param path
     * @return
     */
    private CSVReader getCSVReader(String path) {
        try {
            Charset utf8Charset = Charset.forName("UTF-8");
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, utf8Charset);
            return new CSVReader(inputStreamReader, CSV_READER_SEPARATOR, CSV_READER_QUOTE);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Sets the output file located in path and configures the CSVWriter with the separators and quote characters
     * selected.
     *
     * @param path
     * @return
     */
    private CSVWriter getCSVWriter(String path) {
        try {
            PrintWriter printWriter = new PrintWriter(path, "ISO-8859-1");
            return new CSVWriter(printWriter, CSV_WRITER_SEPARATOR, CSV_WRITER_QUOTE);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Writes in csvWriter
     *
     */
    private void convert() {
        try {
            csvWriter.writeNext(new String[]{"name", "offerurl", "price", "published", "description"});

            List<String[]> csv = csvReader.readAll();
            // Remove headers
            csv.remove(0);
            for ( String[] input : csv ) {
                String[] output = new String[5];
                for (int i = 0; i < output.length; ++i) {
                    if (i == 0) {
                        output[i] = input[0];
                    }
                    else if (i == 1) {
                        output[i] = input[1] + "?id=" + input[2];
                    }
                    // CASE: COLUMN PRICE
                    else if (i == 2) {
                        Matcher numberMatcher = checkRegex(NUMBER_REGEX, input[3]);
                        Matcher currencyMatcher = checkRegex(CURRENCY_REGEX, input[3]);
                        if (numberMatcher.find()) {
                            String capturedNumber = numberMatcher.group();
                            String capturedCurrency = "";
                            if (currencyMatcher.find()) {
                                capturedCurrency = currencyMatcher.group();
                            }

                            NumberFormat capturedFormat = NumberUtils.determineNumberFormat(capturedNumber,
                                    capturedCurrency);

                            try {
                                output[i] = capturedFormat.parse(capturedNumber).toString();
                            }
                            catch (ParseException e) {
                                //DO nothing
                            }
                        }
                    }
                    //CASE: COLUMN DATE
                    else if (i == 3) {
                        Matcher matcher = checkRegex(DATE_REGEX, input[4]);
                        while (matcher.find()) {
                            String capturedDate = matcher.group();
                            try {
                                String dateFormat = DateUtils.determineDateFormat(capturedDate);
                                if (dateFormat != null && !dateFormat.isEmpty()) {
                                    DateTimeFormatter dateTimeFormatterInput = DateTimeFormatter.ofPattern(dateFormat);
                                    String formattedDate = DateUtils.formatDate(capturedDate, dateFormat);
                                    LocalDate localDate = LocalDate.parse(formattedDate, dateTimeFormatterInput);
                                    output[i] = localDate.format(OUTPUT_DATE_FORMATTER);
                                    break;
                                }
                            }
                            catch(DateTimeParseException e) {
                                // DO nothing
                            }
                        }
                    }
                    else if (i == 4) {
                        output[i] = input[4];
                    }
                }
                csvWriter.writeNext(output);
            }
            csvWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Searches for the specified regex pattern in the string.
     *
     * @param regex
     * @param string
     * @return
     */
    private Matcher checkRegex(String regex, String string){
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(string);

        return matcher;
    }
}
