package controllers;

import forms.AccountingRowFormData;
import models.AccountingRow;
import models.Category;
import models.ERowType;
import models.Treasury;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtils;
import views.html.AccountingController.byMonth;
import views.html.AccountingController.edit;
import views.html.AccountingController.summary;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class AccountingController extends Controller {

    public static Result january(int year) {
        return byMonth(ERowType.RECIPE.name().toLowerCase(), year, 1);
    }

    public static Result byMonth(String rowType, int year, int month) {
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
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

    public static Result summary(String rowType, int year) {
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }
        if (!DateUtils.checkYear(year)) {
            return notFound();
        }
        List<Category> parentCategories = Category.findParents(eRowType);
        List<Category> childCategories = Category.findChildren(eRowType);
        List<Category> leafCategories = Category.findLeafs(eRowType);
        List<Treasury> treasuries = Treasury.findByType(eRowType);

        return ok(summary.render(rowType, year, parentCategories, childCategories, leafCategories, treasuries));
    }

    public static Result add(String rowType, int year, int month) {
        if (ERowType.value(rowType) == null) {
            return notFound();
        }

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }
        Form<AccountingRowFormData> accountingRowForm = Form.form(AccountingRowFormData.class);
        return ok(edit.render(rowType, year, month, accountingRowForm));

    }



    public static Result save(String rowType, int year, int month) {

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }

        Form<AccountingRowFormData> accountingRowForm = Form.form(AccountingRowFormData.class).bindFromRequest();

        if (accountingRowForm.hasErrors()) {
            accountingRowForm.reject(Messages.get("alert.save.error"));
            return ok(edit.render(rowType, year, month, accountingRowForm));
        } else {
            AccountingRow accountingRow = new AccountingRow(accountingRowForm.get());
            if(accountingRow.id == null){
                accountingRow.save();
                flash("success", Messages.get("alert.accounting.save.success", new SimpleDateFormat("dd/MM/yyyy").format(accountingRow.date), accountingRow.label, accountingRow.getTotalAmount()));
                return redirect(routes.AccountingController.add(rowType, year, month));
            }else{
                accountingRow.update();
                flash("success", Messages.get("alert.accounting.update.success", new SimpleDateFormat("dd/MM/yyyy").format(accountingRow.date), accountingRow.label, accountingRow.getTotalAmount()));
                return redirect(routes.AccountingController.edit(accountingRow.id));
            }

        }
    }

    public static Result delete(String rowType, int year, int month) {
        DynamicForm form = Form.form().bindFromRequest();

        for (Map.Entry<String, String> entry : form.data().entrySet()){
            String key = entry.getKey();
            if(key != null && key.startsWith("accountingRowIds[")){
                AccountingRow accountingRow = AccountingRow.find.byId(Long.valueOf(entry.getValue()));
                accountingRow.delete();
            }
        }

        return redirect(routes.AccountingController.byMonth(rowType, year, month));
    }

    public static Result edit(Long accountingRowId) {
        if(accountingRowId == null){
            notFound();
        }
        AccountingRow accountingRow = AccountingRow.find.byId(accountingRowId);
        if(accountingRow == null){
            notFound();
        }

        AccountingRowFormData form = new AccountingRowFormData(accountingRow);
        Form<AccountingRowFormData> accountingRowForm = Form.form(AccountingRowFormData.class).fill(form);
        String rowType = accountingRow.rowType.lower();
        int year = accountingRow.getYear();
        int month = accountingRow.getMonth();
        return ok(edit.render(rowType, year, month, accountingRowForm));
    }


}
