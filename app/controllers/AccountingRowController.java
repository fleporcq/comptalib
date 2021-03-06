package controllers;

import forms.AccountingRowFormData;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CurrencyUtils;
import utils.DateUtils;
import views.html.AccountingRowController.byMonth;
import views.html.AccountingRowController.edit;
import views.txt.AccountingRowController.select;
import views.txt.AccountingRowController.autocomplete;
import views.html.AccountingRowController.summary;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
@Security.Authenticated(Secured.class)
public class AccountingRowController extends Controller {

    public static Result january(int year) {
        return byMonth(ERowType.RECIPE.lower(), year, 1);
    }

    public static Result byMonth(String rowType, int year, int month) {
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }
        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }
        if(Accounting.findByYear(year) == null){
            return notFound();
        }
        List<AccountingRow> accountingRows = AccountingRow.month(eRowType, year, month);
        ParentCategoryList parentCategories = new ParentCategoryList(Category.findParents(eRowType));
        List<Treasury> treasuries = Treasury.findByType(eRowType);
        return ok(byMonth.render(rowType, year, month, accountingRows, parentCategories, treasuries));
    }

    public static Result recipeSummary(int year) {
        return summary(ERowType.RECIPE.lower(), year);
    }

    public static Result summary(String rowType, int year) {
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }
        if (!DateUtils.checkYear(year)) {
            return notFound();
        }
        if(Accounting.findByYear(year) == null){
            return notFound();
        }
        ParentCategoryList parentCategories = new ParentCategoryList(Category.findParents(eRowType));
        List<Treasury> treasuries = Treasury.findByType(eRowType);

        return ok(summary.render(rowType, year, parentCategories, treasuries));
    }

    public static Result add(String rowType, int year, int month) {
        if (ERowType.value(rowType) == null) {
            return notFound();
        }

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }
        if(Accounting.findByYear(year) == null){
            return notFound();
        }
        Form<AccountingRowFormData> accountingRowForm = Form.form(AccountingRowFormData.class);
        return ok(edit.render(rowType, year, month, accountingRowForm));

    }



    public static Result save(String rowType, int year, int month) {

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }
        if(Accounting.findByYear(year) == null){
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
                flash("success", Messages.get("alert.accountingRow.save.success", new SimpleDateFormat("dd/MM/yyyy").format(accountingRow.date), accountingRow.label, CurrencyUtils.format(accountingRow.getTotalAmount())));
                return redirect(routes.AccountingRowController.add(rowType, year, month));
            }else{
                accountingRow.update();
                flash("success", Messages.get("alert.accountingRow.update.success", new SimpleDateFormat("dd/MM/yyyy").format(accountingRow.date), accountingRow.label, CurrencyUtils.format(accountingRow.getTotalAmount())));
                return redirect(routes.AccountingRowController.edit(accountingRow.id));
            }

        }
    }

    public static Result delete(String rowType, int year, int month) {
        DynamicForm form = Form.form().bindFromRequest();

        for (Map.Entry<String, String> entry : form.data().entrySet()){
            String key = entry.getKey();
            if(key != null && key.startsWith("accountingRowIds[")){
                AccountingRow accountingRow = AccountingRow.findById(Long.valueOf(entry.getValue()));
                accountingRow.delete();
            }
        }

        return redirect(routes.AccountingRowController.byMonth(rowType, year, month));
    }

    public static Result edit(Long accountingRowId) {
        if(accountingRowId == null){
            notFound();
        }
        AccountingRow accountingRow = AccountingRow.findById(accountingRowId);
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

    public static Result autocomplete(String rowType, String startWith){
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }
        List <String> labels = AccountingRow.findDistinctLabelByRowTypeAndStartWith(eRowType, startWith);
        response().setContentType("application/json");
        return ok(autocomplete.render(labels));
    }

    public static Result select(String rowType, String label){
        ERowType eRowType = ERowType.value(rowType);
        if (eRowType == null) {
            return notFound();
        }
        AccountingRow accountingRow = AccountingRow.findByRowTypeAndLabel(eRowType, label);
        response().setContentType("application/json");
        return ok(select.render(accountingRow));
    }



}
