package controllers;

import forms.AccountingFormData;
import forms.AccountingRowFormData;
import models.Accounting;
import models.AccountingRow;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.CurrencyUtils;
import utils.DateUtils;
import views.html.AccountingController.index;
import views.html.AccountingController.edit;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Security.Authenticated(Secured.class)
public class AccountingController extends Controller {

    public static Result index() {
        List<Accounting> accountings = Accounting.all();
        return ok(index.render(accountings));
    }

    public static Result delete() {
        DynamicForm form = Form.form().bindFromRequest();

        for (Map.Entry<String, String> entry : form.data().entrySet()){
            String key = entry.getKey();
            if(key != null && key.startsWith("accountingYears[")){
                Accounting accounting = Accounting.findByYear(Integer.valueOf(entry.getValue()));
                accounting.delete();
            }
        }

        return redirect(routes.AccountingController.index());
    }

    public static Result add() {
        Form<AccountingFormData> accountingForm = Form.form(AccountingFormData.class);
        return ok(edit.render(accountingForm));
    }

    public static Result save() {
        Form<AccountingFormData> accountingForm = Form.form(AccountingFormData.class).bindFromRequest();

        if (accountingForm.hasErrors()) {
            accountingForm.reject(Messages.get("alert.save.error"));
            return ok(views.html.AccountingController.edit.render(accountingForm));
        } else {
            Accounting accounting = new Accounting(accountingForm.get());
            accounting.save();
            flash("success", Messages.get("alert.accounting.save.success", accounting.year));
            return redirect(routes.AccountingController.add());
        }
    }
}
