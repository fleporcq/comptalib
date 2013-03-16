package forms;

import models.ERowType;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountingRowForm {
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

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
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
        }else{
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
