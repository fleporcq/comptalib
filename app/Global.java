import com.avaje.ebean.Ebean;
import models.Category;
import play.*;
import play.libs.Yaml;

import java.util.*;

public class Global extends GlobalSettings{
    public void onStart(Application app) {
        InitialData.insert(app);
    }

    static class InitialData {

        public static void insert(Application app) {
            if(Ebean.find(Category.class).findRowCount() == 0) {

                Map<String,List<Object>> all = (Map<String,List<Object>>) Yaml.load("initial-data.yml");
                Ebean.save(all.get("categories"));
                Ebean.save(all.get("treasuries"));

            }
        }

    }
}
