package org.sandbox.javaee.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * This annotation was created as a workaround against Weblogic issue when it generates extension of EJBs while deployment.
 * This prevents to inject them just by a type. Something like below is appeared in the log.
 * <pre>
 *  org.jboss.weld.exceptions.DeploymentException: WELD-001409 Ambiguous dependencies for type [UserRepository] with qualifiers [@Default]
 *    at injection point [[field] @Inject private org.sandbox.javaee.data.UserListProducer.repository].
 *    Possible dependencies [[Session bean [class org.sandbox.javaee.data.UserRepository with qualifiers [@Any @Default]; local interfaces are [UserRepository],
 *    Managed Bean [class org.sandbox.javaee.data.UserRepository_r1uxka_Impl] with qualifiers [@Any @Default]]]
 * </pre>
 * Just put this qualifier on EJB on a class level and use it with &#064;Inject annotation.
 * <pre>
 * &#064;Stateless
 * &#064;Primary
 * public class UserRepository {
 * ...
 * </pre>
 * <pre>
 * &#064;Inject &#064;Primary
 * private UserRepository repository;
 * </pre>
 *
 * @author tserge
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface Primary {

}
