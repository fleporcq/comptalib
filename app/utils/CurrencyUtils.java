package utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    public static String format(Float amount){
        if(amount != null){
            return NumberFormat.getCurrencyInstance().format(amount);
        }
        return null;
    }
}