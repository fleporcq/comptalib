package utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    public static String format(BigDecimal amount){
        if(amount != null){
            return NumberFormat.getCurrencyInstance(Locale.FRANCE).format(amount);
        }
        return null;
    }

    public static BigDecimal centsToEuros(BigInteger cents){
        if(cents != null){
            return new BigDecimal(cents).divide(new BigDecimal(100));
        }
        return null;
    }

    public static BigInteger eurosToCents(BigDecimal euros){
        if(euros != null){
           return euros.setScale(2,BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100)).toBigInteger();
        }
        return null;
    }

}
