package forms;

import models.AccountingRow;
import models.ERowType;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountingRowFormData {
    public Long id;
    public ERowType rowType;
    public int year;
    public int month;
    @Constraints.Required
    public int day;
    @Constraints.Required
    public String label;
    @Constraints.Required
    public Long treasuryId;
    @Constraints.Required
    public Float totalAmount;
    public Float personalWithdrawal;
    public Long categoryId;

    public AccountingRowFormData() {

    }

    public AccountingRowFormData(AccountingRow accountingRow) {
        if (accountingRow != null) {
            this.id = accountingRow.id;
            this.rowType = accountingRow.rowType;
            this.year = accountingRow.getYear();
            this.month = accountingRow.getMonth();
            this.day = accountingRow.getDay();
            this.label = accountingRow.label;
            if (accountingRow.treasury != null) {
                this.treasuryId = accountingRow.treasury.id;
            }
            this.totalAmount = accountingRow.getTotalAmount();
            this.personalWithdrawal = accountingRow.getPersonalWithdrawal();
            if (this.personalWithdrawal == 0) {
                this.personalWithdrawal = null;
            }
            if (accountingRow.category != null) {
                this.categoryId = accountingRow.category.id;
            }

        }
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!DateUtils.checkDay(year, month, day)) {
            errors.add(new ValidationError("day", Messages.get("error.invalid")));
        }
        if (rowType == ERowType.EXPENSE) {
            if (personalWithdrawal != null && personalWithdrawal <= 0) {
                errors.add(new ValidationError("personalWithdrawal", Messages.get("error.accountingRow.personalWithdrawal.mustBeGreaterThanZero")));
            }
            if (personalWithdrawal != null && totalAmount != null && personalWithdrawal > totalAmount) {
                errors.add(new ValidationError("personalWithdrawal", Messages.get("error.accountingRow.personalWithdrawal.mustBeLowerThanAmount")));
            }
            if (personalWithdrawal == null || (totalAmount != null && personalWithdrawal < totalAmount)) {
                if (categoryId == null) {
                    errors.add(new ValidationError("categoryId", Messages.get("error.required")));
                }
            } else {
                if (categoryId != null) {
                    errors.add(new ValidationError("categoryId", Messages.get("error.accountingRow.category.mustBeEmpty")));
                }
            }
        } else {
            if (categoryId == null) {
                errors.add(new ValidationError("categoryId", Messages.get("error.required")));
            }
        }
        if (errors.size() > 0) {
            return errors;
        }
        return null;
    }

}
