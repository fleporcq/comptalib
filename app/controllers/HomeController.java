package controllers;

import models.Accounting;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.HomeController.index;
import java.util.List;

@Security.Authenticated(Secured.class)
public class HomeController extends Controller {

    public static Result index() {
        List<Accounting> accountings = Accounting.all();
        return ok(index.render(accountings));
    }

}
