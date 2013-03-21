package utils;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import models.*;
import play.i18n.Messages;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class PdfUtils {

    public static final int COLS_PER_PAGE = 10;

    public static byte[] generateSummary(ERowType rowType, int year, ParentCategoryList parentCategories, List<Treasury> treasuries) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();


            List<ParentCategoryList> pages = parentCategories.paginate(COLS_PER_PAGE, COLS_PER_PAGE - 3);

            boolean firstPage = true;
            for (ParentCategoryList page : pages) {
                if (!firstPage) {
                    document.newPage();
                }
                List<Category> leafCategories = page.getLeafs();

                //label + categories
                int cellCount = 1 + leafCategories.size();
                if (firstPage) {
                    //label + [ personalWithdrawal + ] treasuries + categories
                    cellCount = (ERowType.EXPENSE.equals(rowType) ? 2 : 1) + treasuries.size() + leafCategories.size();
                }

                PdfPTable table = new PdfPTable(cellCount);
                table.setWidthPercentage(100);

                addTableHeader(table, rowType, page, treasuries, firstPage);
                addSummaryTableBody(table, year, rowType, leafCategories, treasuries, firstPage);
                addSummaryTableFooter(table, year, rowType, leafCategories, treasuries, firstPage);

                document.add(table);

                firstPage = false;
            }

            document.close();


            return baos.toByteArray();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addTableHeader(PdfPTable table, ERowType rowType, ParentCategoryList parentCategories, List<Treasury> treasuries, boolean firstPage) {
        PdfPCell monthCell = new PdfPCell(new Phrase(Messages.get("accountingSummary.month")));
        monthCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        monthCell.setRowspan(2);
        table.addCell(monthCell);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
        if (firstPage) {
            PdfPCell treasuriesCell = new PdfPCell(new Phrase(Messages.get("accountingRow.treasury")));
            treasuriesCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            treasuriesCell.setColspan(treasuries.size());
            table.addCell(treasuriesCell);
            if (ERowType.EXPENSE.equals(rowType)) {
                PdfPCell personalWithdrawalsCell = new PdfPCell(new Phrase(Messages.get("accountingRow.personalWithdrawals")));
                personalWithdrawalsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                personalWithdrawalsCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                personalWithdrawalsCell.setRowspan(2);
                table.addCell(personalWithdrawalsCell);
            }
        }
        for (Category parentCategory : parentCategories) {
            PdfPCell catCell;
            catCell = new PdfPCell(new Phrase(parentCategory.name));
            catCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            catCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            if (parentCategory.hasChildren()) {
                catCell.setColspan(parentCategory.children.size());
            } else {
                catCell.setRowspan(2);
            }
            table.addCell(catCell);
        }

        if (firstPage) {
            for (Treasury treasury : treasuries) {
                table.addCell(treasury.name);
            }
        }

        for (Category childCategory : parentCategories.getChildren()) {
            table.addCell(childCategory.name);
        }
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    }

    private static void addSummaryTableBody(PdfPTable table, int year, ERowType rowType, List<Category> leafCategories, List<Treasury> treasuries, boolean firstPage) {
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        for (int month = 1; month <= 12; month++) {
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(DateUtils.month(month));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            if (firstPage) {
                for (Treasury treasury : treasuries) {
                    BigDecimal monthSum = treasury.monthSum(year, month);
                    table.addCell((BigDecimal.ZERO.compareTo(monthSum) != 0) ? CurrencyUtils.format(monthSum) : "");
                }
                if (ERowType.EXPENSE.equals(rowType)) {
                    BigDecimal monthSum = Accounting.personalWithdrawalMonthSum(year, month);
                    table.addCell((BigDecimal.ZERO.compareTo(monthSum) != 0) ? CurrencyUtils.format(monthSum) : "");
                }
            }
            for (Category leafCategory : leafCategories) {
                BigDecimal monthSum = leafCategory.monthSum(year, month);
                table.addCell((BigDecimal.ZERO.compareTo(monthSum) != 0) ? CurrencyUtils.format(monthSum) : "");
            }
        }
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    }

    private static void addSummaryTableFooter(PdfPTable table, int year, ERowType rowType, List<Category> leafCategories, List<Treasury> treasuries, boolean firstPage) {
        table.addCell(Messages.get("accounting.fromJanuarySum"));
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (firstPage) {
            for (Treasury treasury : treasuries) {
                table.addCell(CurrencyUtils.format(treasury.fromJanuarySum(year, 12)));
            }
            if (ERowType.EXPENSE.equals(rowType)) {
                table.addCell(CurrencyUtils.format(Accounting.personalWithdrawalFromJanuarySum(year, 12)));
            }
        }
        for (Category category : leafCategories) {
            table.addCell(CurrencyUtils.format(category.fromJanuarySum(year, 12)));
        }
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    }


}
