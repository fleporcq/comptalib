package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import org.joda.time.DateTime;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class AccountingRow extends Model {

    public static Finder<Long, AccountingRow> find = new Finder<Long, AccountingRow>(Long.class, AccountingRow.class);
    @Id
    public Long id;
    @Constraints.Required
    public String label;
    @Constraints.Required
    public Float amount;
    public Float personalWithdrawal;
    @Enumerated(EnumType.STRING)
    public ERowType rowType;
    @ManyToOne
    public Category category;
    @ManyToOne
    public Treasury treasury;
    public Date date;

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

    public Float getTotalAmount() {
        if (personalWithdrawal != null && amount != null) {
            return amount + personalWithdrawal;
        } else if(amount != null){
            return amount;
        } else if(personalWithdrawal != null){
            return personalWithdrawal;
        }
        return null;
    }

    @Override
    public void save() {
        if (personalWithdrawal != null) {
            amount = amount - personalWithdrawal;
            if(amount == 0){
                amount = null;
            }
        }
        super.save();
    }
}
