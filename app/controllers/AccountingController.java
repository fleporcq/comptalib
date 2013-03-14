package controllers;

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
        Form<AccountingRow> accountingRowForm = Form.form(AccountingRow.class);
        return ok(edit.render(rowType, year, month, accountingRowForm));

    }

    public static Result save(String rowType, int year, int month) {

        ERowType eRowType = ERowType.value(rowType);

        if (eRowType == null) {
            return notFound();
        }

        if (!DateUtils.checkYear(year) || !DateUtils.checkMonth(month)) {
            return notFound();
        }

        Form<AccountingRow> accountingRowForm = Form.form(AccountingRow.class).bindFromRequest();


        if (accountingRowForm.field("treasury.id").value().isEmpty()) {
            accountingRowForm.reject("treasury.id", "error.required");
        }

        Form.Field dayField = accountingRowForm.field("day");
        int day = 1;
        if (dayField.value().isEmpty()) {
            accountingRowForm.reject("day", "error.required");
        } else {
            try {
                day = Integer.valueOf(dayField.value());
            } catch (NumberFormatException e) {
                accountingRowForm.reject("day", "error.invalid");
            }
            if (!DateUtils.checkDay(year, month, day)) {
                accountingRowForm.reject("day", "error.invalid");
            }
        }

        if (eRowType == ERowType.EXPENSE) {
            Form.Field personalWithdrawalField = accountingRowForm.field("personalWithdrawal");
            Form.Field amountField = accountingRowForm.field("amount");
            Float personalWithdrawal = null;
            Float amount = null;
            if (!personalWithdrawalField.value().isEmpty()) {
                try {
                    personalWithdrawal = Float.valueOf(personalWithdrawalField.value());
                } catch (NumberFormatException e) {

                }
                if (personalWithdrawal != null && personalWithdrawal <= 0) {
                    accountingRowForm.reject("personalWithdrawal", "error.accountingRow.personalWithdrawal.mustBeGreaterThanZero");
                }
                if (!amountField.value().isEmpty()) {
                    try {
                        amount = Float.valueOf(amountField.value());
                    } catch (NumberFormatException e) {

                    }
                    if (personalWithdrawal != null && amount != null && personalWithdrawal > amount) {
                        accountingRowForm.reject("personalWithdrawal", "error.accountingRow.personalWithdrawal.mustBeLowerThanAmount");
                    }
                }
            }
            if (personalWithdrawal == null || (amount != null && personalWithdrawal < amount)) {
                if (accountingRowForm.field("category.id").value().isEmpty()) {
                    accountingRowForm.reject("category.id", "error.required");
                }
            } else {
                String categoryFieldValue =  accountingRowForm.field("category.id").value();
                if (categoryFieldValue != null && !categoryFieldValue.isEmpty()) {
                    accountingRowForm.reject("category.id", "error.accountingRow.category.mustBeEmpty");
                }
            }
        } else {
            if (accountingRowForm.field("category.id").value().isEmpty()) {
                accountingRowForm.reject("category.id", "error.required");
            }
        }


        if (accountingRowForm.hasErrors()) {
            accountingRowForm.reject(Messages.get("alert.save.error"));
            return ok(edit.render(rowType, year, month, accountingRowForm));
        } else {
            AccountingRow accountingRow = accountingRowForm.get();
            accountingRow.date = DateUtils.createDate(year, month, day).getTime();
            accountingRow.rowType = eRowType;
            accountingRow.save();
            flash("success", Messages.get("alert.accounting.save.success", new SimpleDateFormat("dd/MM/yyyy").format(accountingRow.date), accountingRow.label, accountingRow.getTotalAmount()));
            return redirect(routes.AccountingController.add(rowType, year, month));
        }
    }


}
