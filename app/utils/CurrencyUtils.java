package utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    public static String format(Float amount){
        if(amount != null){
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(amount);
        }
        return null;
    }

    public static float centsToEuros(int cents){
        return cents/100F;
    }

    public static int eurosToCents(float euros){
        return Math.round(euros * 100);
    }

}
