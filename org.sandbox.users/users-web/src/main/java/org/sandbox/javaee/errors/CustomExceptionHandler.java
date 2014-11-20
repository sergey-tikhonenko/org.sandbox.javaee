package org.sandbox.javaee.errors;

import java.util.Iterator;
import java.util.Map;

import javax.ejb.EJBAccessException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private ExceptionHandler wrappedHandler;
    private static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    public CustomExceptionHandler(ExceptionHandler exceptionHandler) {
        logger.debug("CustomExceptionHandler.init");
        this.wrappedHandler = exceptionHandler;
    }

    @Override
    public ExceptionHandler getWrapped() {
        logger.debug("CustomExceptionHandler.getWrapped" + " " + this.wrappedHandler);
        return this.wrappedHandler;
    }

    @Override
    public void handle() throws FacesException {
        logger.debug("CustomExceptionHandler.handle");

        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
            ExceptionQueuedEvent event = i.next();
            logger.debug("Iterating over ExceptionQueuedEvents. Current: {}", event);

            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            Throwable throwable = context.getException();

            try {
                if (isCauseFor(throwable, EJBAccessException.class)) {
                    handleException(throwable, "/error403");
                }
                else if (isCauseFor(throwable, Throwable.class)) {
                    handleException(throwable, "/error");
                }
            } finally {
                i.remove();
            }

        }
        getWrapped().handle();
    }

    private boolean isCauseFor(Throwable throwable, Class<?> testClass) {
        if (throwable != null && testClass.isInstance(throwable))
            return true;
        if (throwable != null && throwable.getCause() != null)
            return isCauseFor(throwable.getCause(), testClass);
        return false;
    }

    private void handleException(Throwable throwable, String view) {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
        NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();

            requestMap.put("exceptionClass", throwable.getClass().getName());
            requestMap.put("exceptionMessage", throwable.getMessage());
            facesContext.getExternalContext().getFlash().put("exceptionInformation", throwable.getCause());
            navigationHandler.handleNavigation(facesContext, null, view);
            facesContext.renderResponse();

            // remove the comment below if you want to report the error in a jsf error message
            //JsfUtil.addErrorMessage(t.getMessage());
    }

}