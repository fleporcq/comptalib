package models;

import controllers.Secured;
import forms.AccountingFormData;
import play.db.ebean.Model;
import utils.CurrencyUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Entity
public class Accounting extends Model {

    @Id
    public Long id;

    @Column(length = 4)
    public int year;

    @ManyToOne
    public User user;

    @OneToMany(mappedBy = "accounting", cascade = CascadeType.ALL)
    public List<AccountingRow> accountingRows;

    public static Finder<Long, Accounting> find = new Finder<Long, Accounting>(Long.class, Accounting.class);

    public Accounting(AccountingFormData form) {
        this.year = form.year;
        this.user = Secured.getConnectedUser();
    }

    public static List<Accounting> all() {
        return find.where().eq("user", Secured.getConnectedUser()).orderBy("year DESC").findList();
    }

    public static Accounting findByYear(int year) {
        return find.where().eq("year", year).eq("user", Secured.getConnectedUser()).findUnique();
    }

    public static BigDecimal personalWithdrawalMonthSum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.month(ERowType.EXPENSE, year, month);
        BigInteger sum = BigInteger.ZERO;
        for (AccountingRow accountingRow : accountingRows) {
            if(accountingRow.personalWithdrawal != null){
                sum = sum.add(accountingRow.personalWithdrawal);
            }
        }
        return CurrencyUtils.centsToEuros(sum);
    }

    public static BigDecimal personalWithdrawalFromJanuarySum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.fromJanuary(ERowType.EXPENSE, year, month);
        BigInteger sum = BigInteger.ZERO;
        for (AccountingRow accountingRow : accountingRows) {
            if(accountingRow.personalWithdrawal != null){
                sum = sum.add(accountingRow.personalWithdrawal);
            }
        }
        return CurrencyUtils.centsToEuros(sum);
    }
}
