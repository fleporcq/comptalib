package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;
import utils.CurrencyUtils;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Sql
public class Accounting {

    public int year;

    public static List<Accounting> all() {
        String sql = "SELECT YEAR(date) AS year FROM accounting_row GROUP BY year ORDER BY year ASC";
        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        Query<Accounting> query = Ebean.find(Accounting.class);
        return query.setRawSql(rawSql).findList();
    }


    public static BigDecimal personalWithdrawalMonthSum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.month(ERowType.EXPENSE, year, month);
        int sum = 0;
        for (AccountingRow accountingRow : accountingRows) {
            sum += accountingRow.personalWithdrawal;
        }
        return CurrencyUtils.centsToEuros(sum);
    }

    public static BigDecimal personalWithdrawalFromJanuarySum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.fromJanuary(ERowType.EXPENSE, year, month);
        int sum = 0;
        for (AccountingRow accountingRow : accountingRows) {
            sum += accountingRow.personalWithdrawal;
        }
        return CurrencyUtils.centsToEuros(sum);
    }
}
