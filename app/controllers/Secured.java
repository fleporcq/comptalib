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

    public User getConnectedUser(){
        String username = getUsername(Context.current());
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