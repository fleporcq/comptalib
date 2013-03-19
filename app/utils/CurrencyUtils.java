package utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    public static String format(BigDecimal amount){
        if(amount != null){
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(amount);
        }
        return null;
    }

    public static BigDecimal centsToEuros(long cents){
        return new BigDecimal(cents).divide(new BigDecimal(100));
    }

    public static int eurosToCents(BigDecimal euros){
        if(euros != null){
           return euros.setScale(2,BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100)).intValue();
        }
        return 0;
    }

}
