package controllers;

import forms.AccountingRowForm;
import models.AccountingRow;
import models.Category;
import models.ERowType;
import models.Treasury;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtils;
import views.html.AccountingController.byMonth;
import views.html.AccountingController.edit;

import java.text.SimpleDateFormat;
import java.util.List;

public class AccountingController extends Controller {

    public static Result january(int year) {
        return byMonth(ERowType.RECIPE.name().toLowerCase(), year, 1);
    }

    public static Result byMonth(String rowType, int year, int month) {
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }
        try {
            eRowType = ERowType.valueOf(StringUtils.upperCase(rowType));
        } catch (IllegalArgumentException ex) {
            return notFound();
        }
        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }
        List<AccountingRow> accountingRows = AccountingRow.month(eRowType, year, month);

        List<Category> parentCategories = Category.findParents(eRowType);
        List<Category> childCategories = Category.findChildren(eRowType);
        List<Category> leafCategories = Category.findLeafs(eRowType);
        List<Treasury> treasuries = Treasury.findByType(eRowType);

        return ok(byMonth.render(rowType, year, month, accountingRows, parentCategories, childCategories, leafCategories, treasuries));
    }

    public static Result add(String rowType, int year, int month) {
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }
        Form<AccountingRowForm> accountingRowForm = Form.form(AccountingRowForm.class);
        return ok(edit.render(rowType, year, month, accountingRowForm));

    }

    public static Result save(String rowType, int year, int month) {

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }

        Form<AccountingRowForm> accountingRowForm = Form.form(AccountingRowForm.class).bindFromRequest();

        if (accountingRowForm.hasErrors()) {
            accountingRowForm.reject(Messages.get("alert.save.error"));
            return ok(edit.render(rowType, year, month, accountingRowForm));
        } else {
            AccountingRow accountingRow = new AccountingRow(accountingRowForm.get());
            accountingRow.save();
            flash("success", Messages.get("alert.accounting.save.success", new SimpleDateFormat("dd/MM/yyyy").format(accountingRow.date), accountingRow.label, accountingRow.getTotalAmount()));
            return redirect(routes.AccountingController.add(rowType, year, month));
        }
    }


}
