package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
        put("^\\d{1,2}\\-\\d{1,2}\\-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
        put("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$", "dd/MM/yyyy");
        put("^\\d{1,2}\\s\\d{1,2}\\s\\d{4}$", "dd MM yyyy");
        put("^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{4}\\/\\d{1,2}\\/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "yyyy.MM.dd");
        put("^\\d{4}\\s\\d{1,2}\\s\\d{1,2}$", "yyyy MM dd");
    }};

    /**
     * Determines SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString
     * @return
     */
    public static String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null; // Unknown format.
    }

    /**
     * Formats the date dateString adding zeros if necessary.
     *
     * @param dateString
     * @return
     */
    public static String formatDate(String dateString, String dateFormat) {String separator = null;
        if (dateFormat.contains(".")) {
            separator = ".";
        }
        else if (dateFormat.contains("-")) {
            separator = "-";
        }
        else if (dateFormat.contains("/")) {
            separator = "/";
        }
        else if (dateFormat.contains(" ")) {
            separator = " ";
        }
        else {
            return dateString;
        }

        String[] dateArray = dateString.split("\\" + separator);
        String[] dateFormatArray = dateFormat.split("\\" + separator);

        for (int i = 0; i < dateArray.length; ++i) {
            // CASE: Add zeros in the case they don't have
            dateArray[i] = (dateArray[i].length() < 2) ? "0" + dateArray[i] : dateArray[i];

            // CASE: Switching positions of dd and MM as they are not correct
            if(dateFormatArray[i].equals("MM") && Integer.parseInt(dateArray[i]) > 12) {
                int index = Arrays.asList(dateFormatArray).indexOf("dd");
                String temp = dateArray[index];
                dateArray[index] = dateArray[i];
                dateArray[i] = temp;
            }
        }

        return String.join(separator, dateArray);
    }
}
