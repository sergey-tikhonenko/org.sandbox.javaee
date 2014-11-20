package org.sandbox.javaee.rest;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBAccessException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.sandbox.javaee.data.UserRepository;
import org.sandbox.javaee.model.User;
import org.sandbox.javaee.service.UserService;
import org.sandbox.javaee.util.Primary;
import org.slf4j.Logger;

/**
 * JAX-RS Example
 *
 * This class produces a RESTful service to provide read/write operation for the persisted user list.
 */
@Path("/users")
@RequestScoped
public class UserRESTService {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private UserRepository repository;

    @Inject @Primary
    private UserService userService;

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserRESTService.class);

    @GET
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    public List<User> listAllUsers() {
        log.debug("get users");
        logger.debug("get users");
        final List<User> results = repository.findAllOrderedByName();
        return results;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON })
    public /*User*/Response lookupUserById(@PathParam("id") long id) {

        log.debug("get User for Id:{}", id);
        if (logger.isDebugEnabled()) logger.debug(String.format("get User for Id:%s", id));
        User user = repository.findById(id);
        return Response.ok().entity(user).build();
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Context UriInfo uriInfo, User user) {

        log.debug("createUser {}", user);
        if (logger.isDebugEnabled()) logger.debug(String.format("createUser %s", user));

        Response.ResponseBuilder builder = null;

        try {
            // Validates user using bean validation
            validateUser(user);

            userService.register(user);

            UriBuilder ub = uriInfo.getBaseUriBuilder();
            URI userUri = ub.path(user.getId().toString()).build();
            // Create an "201 Created" response
            builder = Response.created(userUri).entity(user);
        }
        catch (ConstraintViolationException ce) {
            throw ce; // Bean validation issues are converted to a response by means of ExceptionMapper
        }
        catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        }
        catch (EJBAccessException e) {
            throw e; // Security exceptions are handled by means of ExceptionMapper
        }
        catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = Collections.singletonMap("error", e.getMessage());
            builder = Response.serverError().entity(responseObj);
        }

        return builder.build();
    }

    @PUT
    @Path("{id:[0-9][0-9]*}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @Context SecurityContext sc,
            @PathParam("id") Long id,
            User entity
            ) {
        log.info("updateUser {}", id);
        if (logger.isDebugEnabled()) logger.debug(String.format("updateUser %s", id));

        Response.ResponseBuilder builder = null;

        try {
            // Validates user using bean validation
            validateUser(entity);

            if (!sc.isUserInRole("UserEditor")) {
                Principal userPrincipal = sc.getUserPrincipal();
                String userName = userPrincipal == null ? "anonimous" : userPrincipal.getName();

                Map<String, String> responseObj = Collections.singletonMap(
                    "error",
                    String.format("User %s has no permission to edit users!", userName)
                );
                builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
            }
            else {
                entity = userService.update(entity);

                // Create an "ok" response
                builder = Response.ok().entity(entity);
            }
        }
        catch (ConstraintViolationException ce) {
            throw ce; // Bean validation issues are converted to a response by means of ExceptionMapper
        }
        catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = Collections.singletonMap("email", "Email is already taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        }
        catch (EJBAccessException e) {
            throw e; // Security exceptions are handled by means of ExceptionMapper
        }
        catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = Collections.singletonMap("error", e.getMessage());
            builder = Response.serverError().entity(responseObj);
        }

        return builder.build();
    }

    @DELETE
    @Path("{id:[0-9][0-9]*}")
    public void removeUser(@PathParam("id") Long id) {

            log.info("removeUser {}", id);
            if (logger.isDebugEnabled()) logger.debug(String.format("removeUser %s", id));

            userService.delete(id); // Security exceptions are handled by means of ExceptionMapper
    }

    /**
     * <p>Validates the given User variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param user User to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateUser(User user) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(user.getEmail(), user.getId())) {
            throw new ValidationException("Unique Email Violation");
        }
    }


    /**
     * Checks if a user with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the User class.
     *
     * @param email The email to check
     * @param email The updating user ID, to exclude from search. Can be null for a new user.
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email, Long userId) {
        User user = null;
        try {
            user = repository.findByEmail(email, userId);
        } catch (NoResultException e) {
            // ignore
        }
        return user != null;
    }
}
