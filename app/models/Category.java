package models;

import com.avaje.ebean.ExpressionList;
import play.db.ebean.Model;
import utils.CurrencyUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends Model {

    public static Model.Finder<Long, Category> find = new Model.Finder<Long, Category>(Long.class, Category.class);
    @Id
    public Long id;
    public String name;
    public ERowType rowType;
    public Long ordering;
    @ManyToOne
    public Category parent;
    @OneToMany(mappedBy = "parent")
    public List<Category> children;

    public static List<Category> find(ERowType type) {
        ExpressionList expressionList = find.where();
        if (type != null) {
            expressionList = expressionList.eq("rowType", type);
        }
        return expressionList.orderBy("ordering").findList();
    }

    public static List<Category> findParents(ERowType type) {
        ExpressionList expressionList = find.where().isNull("parent");
        if (type != null) {
            expressionList = expressionList.eq("rowType", type);
        }
        return expressionList.orderBy("ordering").findList();
    }

    public static List<Category> findChildren(ERowType type) {
        ExpressionList expressionList = find.where().isNotNull("parent");
        if (type != null) {
            expressionList = expressionList.eq("rowType", type);
        }
        return expressionList.orderBy("ordering").findList();
    }

    public static List<Category> findLeafs(ERowType type) {
        List<Category> categories = find(type);
        List<Category> leafs = new ArrayList<Category>();
        for (Category category : categories) {
            if (!category.hasChildren()) {
                leafs.add(category);
            }
        }
        return leafs;
    }

    public boolean hasChildren() {
        return this.children.size() > 0;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public BigDecimal monthSum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.month(this.rowType, year, month, this);
        BigInteger sum = BigInteger.ZERO;
        for (AccountingRow accountingRow : accountingRows) {
            if(accountingRow.amount != null){
                sum = sum.add(accountingRow.amount);
            }
        }
        return CurrencyUtils.centsToEuros(sum);
    }

    public BigDecimal fromJanuarySum(int year, int month) {
        List<AccountingRow> accountingRows = AccountingRow.fromJanuary(this.rowType, year, month, this);
        BigInteger sum = BigInteger.ZERO;
        for (AccountingRow accountingRow : accountingRows) {
            if(accountingRow.amount != null){
                sum = sum.add(accountingRow.amount);
            }
        }
        return CurrencyUtils.centsToEuros(sum);
    }
}
