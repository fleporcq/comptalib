package controllers;

import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }


    public static User getConnectedUser(){
        String username = Context.current().session().get("username");
        User user = null;
        if(username != null){
            user = User.findByUsername(username);
        }
        return user;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.ApplicationController.login());
    }
    
}