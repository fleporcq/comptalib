package controllers;

import models.Accounting;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.ApplicationController.index;

import java.util.List;

public class ApplicationController extends Controller {
  
    public static Result index() {
        List<Accounting> accountings = Accounting.all();
        return ok(index.render(accountings));
    }
  
}
