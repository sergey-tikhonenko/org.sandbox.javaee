package org.sandbox.javaee.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

//import org.richfaces.cdi.push.Push;
import org.sandbox.javaee.model.User;
import org.sandbox.javaee.service.UserService;
import org.sandbox.javaee.util.Primary;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an EL name
@Model
public class UserController {

    @Inject @Primary
    private UserService userService;

    @Inject
    private FacesContext facesContext;

    @Inject
//    @Push(topic = "pushCdi")
    private Event<String> pushEvent;

    private User newUser;

    @Produces
    @Named
    public User getNewUser() {
        return newUser;
    }

    @PostConstruct
    public void initNewUser() {
        newUser = new User();
    }

    // User Details view support
    private User user;

    @Produces
    @Named
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public void register() throws Exception {
        try {
            userService.register(newUser);

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, msg);
            pushEvent.fire(String.format("New user added: %s (id: %d)", newUser.getName(), newUser.getId()));
            initNewUser();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
            facesContext.addMessage(null, m);
        }
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }

    public void delete(Long id) throws Exception {
        userService.delete(id);
     }
}
