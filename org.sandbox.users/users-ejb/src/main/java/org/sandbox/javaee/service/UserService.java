package org.sandbox.javaee.service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.sandbox.javaee.model.User;
import org.sandbox.javaee.util.Primary;
import org.slf4j.Logger;

// The @Stateful annotation eliminates the need for manual transaction demarcation
@Stateless
@Primary
@RolesAllowed({"UserEditor"})
public class UserService {

   @Inject
   private Logger log;

   @Inject
   private EntityManager em;

   @Inject
   private Event<User> userEventSrc;

   public void register(User newUser) throws Exception {
       log.info("Registering {}", newUser.getName());
       em.persist(newUser);
       userEventSrc.fire(newUser);
   }

   public User update(User changedUser) throws Exception {
       log.info("Changing {}", changedUser.getName());
       User merged = em.merge(changedUser);
       userEventSrc.fire(changedUser);
       return merged;
   }

   public void delete(Long id) {
       User user = em.find(User.class, id);
       log.info("Deleting {}", user.getName());
       em.remove(user);
       userEventSrc.fire(user);
   }
}
