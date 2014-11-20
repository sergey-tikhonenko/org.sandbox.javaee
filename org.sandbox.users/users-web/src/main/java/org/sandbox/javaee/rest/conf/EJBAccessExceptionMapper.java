package org.sandbox.javaee.rest.conf;

import java.util.Collections;

import javax.ejb.EJBAccessException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *  Implementation of {@link ExceptionMapper} to send down a "403 Forbidden" response
 *  in case if a caller has no permission to invoke EJB method.
 *
 */
@Provider
public class EJBAccessExceptionMapper implements ExceptionMapper<EJBAccessException> {
    @Override
    public Response toResponse(EJBAccessException exception) {
        return Response.status(Response.Status.FORBIDDEN)
            .entity(Collections.singletonMap("error", exception.getMessage()))
            //.type(MediaType.APPLICATION_JSON) //"text/plain"
            .build();
    }
}
