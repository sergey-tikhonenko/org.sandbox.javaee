package org.sandbox.javaee.data;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.sandbox.javaee.model.User;
import org.sandbox.javaee.model.User_;
import org.sandbox.javaee.util.Primary;

//The @Stateful annotation eliminates the need for manual transaction demarcation
@Stateless
@Primary
@RolesAllowed({"UserRole", "UserViewer", "UserEditor"})
public class UserRepository {

    @Inject
    private EntityManager em;

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public User findByEmail(String email, Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = cb.createQuery(User.class);
        Root<User> user = criteria.from(User.class);

        if (userId == null) {
            criteria.select(user).where(cb.equal(user.get(User_.email), email));
        } else {
            criteria.select(user)
                .where(
                    cb.and(
                        cb.equal(user.get(User_.email), email),
                        cb.notEqual(user.get(User_.id), userId)
                    ));
        }
        return em.createQuery(criteria).getSingleResult();
    }

    public List<User> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = cb.createQuery(User.class);
        Root<User> user = criteria.from(User.class);

        criteria.select(user).orderBy(cb.asc(user.get(User_.lastName)), cb.asc(user.get(User_.firstName)));

        return em.createQuery(criteria).getResultList();
        //final List<User> results = em.createNamedQuery("User.findAllOrderByLastName", User.class).getResultList();
    }
}
