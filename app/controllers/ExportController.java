package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import util.pdf.PDF;
import utils.DateUtils;
import views.html.ExportController.export;
import views.html.ExportController.pdf;

import java.util.List;

public class ExportController extends Controller {
    public static Result pdf(int year) {
        if (!DateUtils.checkYear(year)) {
            return notFound();
        }
        if(Accounting.findByYear(year) == null){
            return notFound();
        }
        List<Treasury> expenseTreasuries = Treasury.findByType(ERowType.EXPENSE);
        List<ParentCategoryList> expensePages = new ParentCategoryList(Category.findParents(ERowType.EXPENSE)).paginate(12, 9);
        List<Treasury> recipeTreasuries = Treasury.findByType(ERowType.RECIPE);
        List<ParentCategoryList> recipePages = new ParentCategoryList(Category.findParents(ERowType.RECIPE)).paginate(12, 9);
        return PDF.ok(pdf.render(year, recipePages, recipeTreasuries, expensePages, expenseTreasuries));
    }

    public static Result export(int year) {
        return ok(export.render(year));
    }
}
