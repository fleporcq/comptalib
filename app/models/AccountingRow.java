package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import forms.AccountingRowFormData;
import org.joda.time.DateTime;
import play.db.ebean.Model;
import utils.CurrencyUtils;
import utils.DateUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class AccountingRow extends Model {


    public static Finder<Long, AccountingRow> find = new Finder<Long, AccountingRow>(Long.class, AccountingRow.class);
    @Id
    public Long id;
    public String label;
    public int amount;
    public int personalWithdrawal;
    @Enumerated(EnumType.STRING)
    public ERowType rowType;
    @ManyToOne
    public Category category;
    @ManyToOne
    public Treasury treasury;
    public Date date;

    public AccountingRow(AccountingRowFormData form) {
        this.id = form.id;
        this.label = form.label;

        if (form.personalWithdrawal != null) {
            this.personalWithdrawal = CurrencyUtils.eurosToCents(form.personalWithdrawal);
        }

        if (form.totalAmount != null && form.personalWithdrawal != null) {
            this.amount = CurrencyUtils.eurosToCents(form.totalAmount.subtract(form.personalWithdrawal));
        } else if (form.totalAmount != null) {
            this.amount = CurrencyUtils.eurosToCents(form.totalAmount);
        }
        this.rowType = form.rowType;
        this.date = DateUtils.createDate(form.year, form.month, form.day).getTime();
        Category category = new Category();
        category.id = form.categoryId;
        this.category = category;
        Treasury treasury = new Treasury();
        treasury.id = form.treasuryId;
        this.treasury = treasury;
        System.out.println("Amount-----"+this.amount);
    }

    public static Page<AccountingRow> page(int page, int pageSize, String sortBy, String order, String filter) {
        return find.where()
                .ilike("label", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
    }

    public static List<AccountingRow> month(ERowType rowType, int year, int month) {
        return fromTo(rowType, year, month, 0, null, null);
    }

    public static List<AccountingRow> month(ERowType rowType, int year, int month, Category category) {
        return fromTo(rowType, year, month, 0, category, null);
    }

    public static List<AccountingRow> month(ERowType rowType, int year, int month, Treasury treasury) {
        return fromTo(rowType, year, month, 0, null, treasury);
    }

    public static List<AccountingRow> fromJanuary(ERowType rowType, int year, int month) {
        return fromTo(rowType, year, 1, month, null, null);
    }

    public static List<AccountingRow> fromJanuary(ERowType rowType, int year, int month, Category category) {
        return fromTo(rowType, year, 1, month, category, null);
    }

    public static List<AccountingRow> fromJanuary(ERowType rowType, int year, int month, Treasury treasury) {
        return fromTo(rowType, year, 1, month, null, treasury);
    }

    public static List<AccountingRow> fromTo(ERowType rowType, int year, int startMonth, int endMonth, Category category, Treasury treasury) {
        if (startMonth == 0) {
            return null;
        }
        if (endMonth == 0) {
            endMonth = startMonth;
        }
        DateTime firstDayOfMonth = new DateTime(year, startMonth, 1, 0, 0);
        DateTime lastDayOfMonth = new DateTime((endMonth < 12) ? year : year + 1, (endMonth < 12) ? endMonth + 1 : 1, 1, 0, 0);

        ExpressionList expressionList = find.where().eq("rowType", rowType).between("date", firstDayOfMonth, lastDayOfMonth);

        if (category != null) {
            expressionList = expressionList.eq("category", category);
        }

        if (treasury != null) {
            expressionList = expressionList.eq("treasury", treasury);
        }

        List<AccountingRow> accountingRows = expressionList.orderBy("date ASC").findList();

        return accountingRows;
    }



    public BigDecimal getTotalAmount() {
        return CurrencyUtils.centsToEuros(amount + personalWithdrawal);
    }
	
    public int getTotalAmountIntValue() {
            return amount + personalWithdrawal;
    }
	
    public BigDecimal getAmount() {
        return CurrencyUtils.centsToEuros(amount);
    }

    public BigDecimal getPersonalWithdrawal() {
        return CurrencyUtils.centsToEuros(personalWithdrawal);
    }

    public Integer getYear(){
        if(date != null){
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
            return Integer.valueOf(formatYear.format(date));
        }
        return null;
    }

    public Integer getMonth(){
        if(date != null){
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
            return Integer.valueOf(formatMonth.format(date));
        }
        return null;
    }

    public Integer getDay(){
        if(date != null){
            SimpleDateFormat formatDay = new SimpleDateFormat("dd");
            return Integer.valueOf(formatDay.format(date));
        }
        return null;
    }
}
