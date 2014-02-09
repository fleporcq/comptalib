package controllers;

import models.*;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import util.pdf.PDF;
import utils.DateUtils;
import views.html.ExportController.export;
import views.html.ExportController.pdf;

import java.util.List;

public class ExportController extends Controller {

    public static Result pdf(int year, int month, boolean onlySummary) {
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
        return PDF.ok(pdf.render(year, month, onlySummary, recipePages, recipeTreasuries, expensePages, expenseTreasuries));
    }

    public static Result saveAsPdf(int year, int month, boolean onlySummary) {
        User user = Secured.getConnectedUser();

        String partialName = "";
        if(month > 0 && !onlySummary){
            partialName = "-" + month;
        }else if(onlySummary){
            partialName = "-" + Messages.get("pdf.filename.summary");
        }

        response().setContentType("application/x-download");
        response().setHeader("Content-disposition","attachment; filename=" + Messages.get("pdf.filename.prefix") +"-" + user.username + "-" + year + partialName + ".pdf");
        return pdf(year, month, onlySummary);
    }

    public static Result exportYear(int year) {
        if (!DateUtils.checkYear(year)) {
            return notFound();
        }
        return ok(export.render(year, 0, false));
    }

    public static Result exportMonth(int year, int month) {
        if (!DateUtils.checkYear(year)) {
            return notFound();
        }
        if (!DateUtils.checkMonth(month)) {
            return notFound();
        }
        return ok(export.render(year, month, false));
    }

    public static Result exportSummary(int year) {
        if (!DateUtils.checkYear(year)) {
            return notFound();
        }
        return ok(export.render(year, 0, true));
    }
}
