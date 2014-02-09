package forms;

import models.Accounting;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class AccountingFormData {

    @Constraints.Required
    public int year;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        if(Accounting.findByYear(year) != null){
            errors.add(new ValidationError("year", Messages.get("error.accounting.year.alreadyExist")));
        }

        if(!DateUtils.checkYear(year)){
            errors.add(new ValidationError("year", Messages.get("error.accounting.year.invalid")));
        }

        if (errors.size() > 0) {
            return errors;
        }

        return null;
    }


}
