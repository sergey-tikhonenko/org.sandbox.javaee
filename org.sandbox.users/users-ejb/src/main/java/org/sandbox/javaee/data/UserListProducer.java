package org.sandbox.javaee.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.sandbox.javaee.model.User;
import org.sandbox.javaee.util.Primary;

@RequestScoped
public class UserListProducer {

    @Inject @Primary
    private UserRepository repository;

    private List<User> users;

    // @Named provides access the return value via the EL variable name "users" in the UI (e.g., Facelets or JSP view)
    @Produces
    @Named
    public List<User> getUsers() {
        return users;
    }

    public void onUsersListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final User user) {
        retrieveAllUsersOrderedByName();
    }

    @PostConstruct
    public void retrieveAllUsersOrderedByName() {
        users = repository.findAllOrderedByName();
    }
}
