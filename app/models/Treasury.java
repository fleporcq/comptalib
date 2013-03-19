package models;

import com.avaje.ebean.ExpressionList;
import play.db.ebean.Model;
import utils.CurrencyUtils;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Treasury extends Model {

    public static Finder<Long, Treasury> find = new Finder<Long, Treasury>(Long.class, Treasury.class);
    @Id
    public Long id;
    public String name;
    public Long ordering;
    @Enumerated(EnumType.STRING)
    public ERowType rowType;

    public static List<Treasury> findByType(ERowType type) {
        ExpressionList expressionList = find.where();
        if (type != null) {
            expressionList = expressionList.eq("rowType", type);
        }
        return expressionList.orderBy("ordering").findList();
    }

    public static Map<String, String> options(ERowType type) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        List<Treasury> categories = Treasury.findByType(type);
        for (Treasury category : categories) {
            options.put(category.id.toString(), category.name);
        }
        return options;
    }

    public float monthSum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.month(this.rowType, year, month, this);
        int sum = 0;
        for (AccountingRow accountingRow : accountingRows) {
            sum += accountingRow.getTotalAmountIntValue();
        }
        return CurrencyUtils.centsToEuros(sum);
    }

    public float fromJanuarySum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.fromJanuary(this.rowType, year, month, this);
        int sum = 0;
        for (AccountingRow accountingRow : accountingRows) {
            sum += accountingRow.getTotalAmountIntValue();
        }
        return CurrencyUtils.centsToEuros(sum);
    }
}
