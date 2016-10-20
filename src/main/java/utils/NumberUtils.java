package utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    /**
     * Returns the NumberFormat of number according to its currency or the separatos (commas or dots) that are in the
     * number.
     *
     * @param number
     * @param currency
     * @return
     */
    public static NumberFormat determineNumberFormat(String number, String currency) {
        if (!number.contains(".") && number.contains(",")) {
            if (currency == "$" || currency == "USD") {
                return NumberFormat.getInstance(java.util.Locale.US);
            }
            else {
                return NumberFormat.getInstance(Locale.FRENCH);
            }
        } else if (number.contains(".") && !number.contains(",")) {
            if (currency == "€" || currency == "EUR" || currency == "PLN") {
                return NumberFormat.getInstance(Locale.FRENCH);
            } else {
                return NumberFormat.getInstance(java.util.Locale.US);
            }
        }
        else if (!number.contains(",") && !number.contains(".")) {
            return NumberFormat.getInstance(Locale.FRENCH);
        }
        else if (number.contains(".") && number.contains(",")) {
            return NumberFormat.getInstance(java.util.Locale.US);
        }
        return null; // Unknown format.
    }
}
