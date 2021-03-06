package com.hajder.travelagency.bean;

import com.hajder.travelagency.action.ChangePasswordAction;
import com.hajder.travelagency.action.GetUserAction;
import com.hajder.travelagency.action.RegisterUserAction;
import com.hajder.travelagency.entity.User;
import com.hajder.travelagency.resource.NavigationTags;
import org.apache.http.client.utils.URIBuilder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User managed bean used for user authorization, including admins and super-user
 * Created by Piotr on 13.12.2016.
 * @author Piotr Hajder
 */
@ManagedBean
@RequestScoped
public class UserBean {
    private String username;
    private String oldPassword;
    private String password;
    private String email;

    public String login() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        GetUserAction action = new GetUserAction();
        action.setUsername(username);
        action.setPassword(password);
        action.execute();

        User user = action.getUser();
        if(user != null) {
            session.setAttribute("user", user);
            password = null;
            return NavigationTags.SUCCESS;
        }
        //TODO: Zmienic wiadomosc. Byc moze na lokalizowana.
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Nieprawidlowa nazwa uzytkownika lub haslo"));
        return NavigationTags.DENY;
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        session.invalidate();

        return NavigationTags.SUCCESS;
    }

    public String register() {
        //TODO: Wykonac wiadomosc jako lokalizowana
        RegisterUserAction action = new RegisterUserAction();
        User user = new User(username, password);
        user.setEmail(email);
        action.setUser(user);
        action.execute();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Wykonano poprawnie rejestracje. Musisz sie zalogowac."));
        return NavigationTags.SUCCESS;
    }

    public void changePassword() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");

        FacesMessage msg;

        if(user != null) {
            ChangePasswordAction action = new ChangePasswordAction();
            action.setUsername(user.getUsername());
            action.setOldPassword(oldPassword);
            action.setNewPassword(password);
            action.execute();

            if (action.isResult()) {
                msg = new FacesMessage("Success!");
            } else {
                msg = new FacesMessage("Invalid passwords");
            }
        } else {
            msg = new FacesMessage("You have to be logged in!");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean isAdminLogged() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        return user != null && user.isAdmin();
    }

    public boolean isUserLogged() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        return user != null && !user.isAdmin();
    }

    public boolean isSuperAdmin() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        return user != null && user.isSuperAdmin();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
