package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Crypto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    public String username;

    @Constraints.Required
    public String password;


    public static Finder<String, User> find = new Finder(String.class, User.class);

    public static User findByUsername(String username) {
        return find.where().eq("username", username).findUnique();
    }

    public static User authenticate(String username, String password) {
        return find.where()
                .eq("username", username)
                .eq("password", Crypto.sign(password))
                .findUnique();
    }



}

