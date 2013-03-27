package controllers;

import jsmessages.JsMessages;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.ApplicationController.login;

public class ApplicationController extends Controller {

    public static Result jsMessages() {
        return ok(JsMessages.generate("Messages")).as("application/javascript");
    }

    public static Result login() {
        return ok(login.render(Form.form(forms.LoginFormData.class)));
    }

    public static Result authenticate() {
        Form<forms.LoginFormData> loginForm = Form.form(forms.LoginFormData.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session("username", loginForm.get().username);
            return redirect(routes.AccountingController.index());
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", Messages.get("alert.logout.success"));
        return redirect(
                routes.ApplicationController.login()
        );
    }
}
