package forms;

import play.data.validation.Constraints;

public class AccountingFormData {

    @Constraints.Required
    public int year;

}
