package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;

import javax.persistence.Entity;
import java.util.List;

@Entity
@Sql
public class Accounting {

    public int year;

    public static List<Accounting> all(){
        String sql = "SELECT YEAR(date) AS year FROM accounting_row GROUP BY year ORDER BY year ASC";
        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        Query<Accounting> query = Ebean.find(Accounting.class);
        return query.setRawSql(rawSql).findList();
    }



    public static float personalWithdrawalMonthSum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.month(ERowType.EXPENSE, year, month);
        int sum = 0;
        for (AccountingRow accountingRow : accountingRows) {
            if (accountingRow.personalWithdrawal != null) {
                sum += accountingRow.personalWithdrawal;
            }
        }
        return sum / 100F;
    }

    public static float personalWithdrawalFromJanuarySum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.fromJanuary(ERowType.EXPENSE, year, month);
        int sum = 0;
        for (AccountingRow accountingRow : accountingRows) {
            if (accountingRow.personalWithdrawal != null) {
                sum += accountingRow.personalWithdrawal;
            }
        }
        return sum / 100F;
    }
}
