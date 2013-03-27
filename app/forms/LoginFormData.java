package forms;

import models.User;
import play.i18n.Messages;

public class LoginFormData {

    public String username;
    public String password;

    public String validate() {
        if(User.authenticate(username, password) == null) {
            return Messages.get("alert.login.error");
        }
        return null;
    }
}
