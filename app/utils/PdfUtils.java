package utils;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import models.*;
import play.i18n.Messages;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class PdfUtils {

    public static byte[] generateSummary(ERowType rowType, int year, ParentCategoryList parentCategories, List<Treasury> treasuries) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A3.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            List<Category> leafCategories = parentCategories.getLeafs();

            List<ParentCategoryList> pages = parentCategories.paginate(10);
            int cellCount = (ERowType.EXPENSE.equals(rowType) ? 2 : 1) + treasuries.size() + leafCategories.size();
            PdfPTable table = new PdfPTable(cellCount);

            addTableHeader(table, rowType, parentCategories, treasuries);
            addSummaryTableBody(table, year, rowType, leafCategories, treasuries);
            addSummaryTableFooter(table, year, rowType, leafCategories, treasuries);
            table.setWidthPercentage(100);
            document.add(table);

            document.close();



            return baos.toByteArray();
        } catch (DocumentException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addSummaryTableFooter(PdfPTable table, int year, ERowType rowType, List<Category> leafCategories, List<Treasury> treasuries) {
        table.addCell(Messages.get("accounting.fromJanuarySum"));
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        for(Treasury treasury : treasuries){
            table.addCell(CurrencyUtils.format(treasury.fromJanuarySum(year, 12)));
        }
        if (ERowType.EXPENSE.equals(rowType)) {
            table.addCell(CurrencyUtils.format(Accounting.personalWithdrawalFromJanuarySum(year, 12)));
        }
        for(Category category : leafCategories){
            table.addCell(CurrencyUtils.format(category.fromJanuarySum(year, 12)));
        }
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    }

    private static void addSummaryTableBody(PdfPTable table, int year, ERowType rowType, List<Category> leafCategories, List<Treasury> treasuries) {
        for (int month = 1; month <= 12; month++) {
            table.addCell(DateUtils.month(month));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            for (Treasury treasury : treasuries) {
                BigDecimal monthSum = treasury.monthSum(year, month);
                table.addCell((BigDecimal.ZERO.compareTo(monthSum) != 0) ? CurrencyUtils.format(monthSum) : "");
            }
            if (ERowType.EXPENSE.equals(rowType)) {
                BigDecimal monthSum = Accounting.personalWithdrawalMonthSum(year, month);
                table.addCell((BigDecimal.ZERO.compareTo(monthSum) != 0) ? CurrencyUtils.format(monthSum) : "");
            }
            for (Category leafCategory : leafCategories) {
                BigDecimal monthSum = leafCategory.monthSum(year, month);
                table.addCell((BigDecimal.ZERO.compareTo(monthSum) != 0) ? CurrencyUtils.format(monthSum) : "");
            }
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        }
    }

    private static void addTableHeader(PdfPTable table, ERowType rowType, ParentCategoryList parentCategories,  List<Treasury> treasuries) {
        PdfPCell monthCell = new PdfPCell(new Phrase(Messages.get("accountingSummary.month")));
        monthCell.setRowspan(2);
        table.addCell(monthCell);

        PdfPCell treasuriesCell = new PdfPCell(new Phrase(Messages.get("accountingRow.treasury")));
        treasuriesCell.setColspan(treasuries.size());
        table.addCell(treasuriesCell);

        if (ERowType.EXPENSE.equals(rowType)) {
            PdfPCell personalWithdrawalsCell = new PdfPCell(new Phrase(Messages.get("accountingRow.personalWithdrawals")));
            personalWithdrawalsCell.setRowspan(2);
            table.addCell(personalWithdrawalsCell);
        }

        for (Category parentCategory : parentCategories) {
            PdfPCell catCell;
            catCell = new PdfPCell(new Phrase(parentCategory.name));
            if (parentCategory.hasChildren()) {
                catCell.setColspan(parentCategory.children.size());
            } else {
                catCell.setRowspan(2);
            }
            table.addCell(catCell);
        }

        for (Treasury treasury : treasuries) {
            table.addCell(treasury.name);
        }

        for (Category childCategory : parentCategories.getChildren()) {
            table.addCell(childCategory.name);
        }
    }


}
