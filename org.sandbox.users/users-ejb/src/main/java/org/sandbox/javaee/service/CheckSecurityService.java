package org.sandbox.javaee.service;

import java.security.Principal;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.sandbox.javaee.util.Primary;

@Stateless
@Primary
public class CheckSecurityService {

    @Resource
    private SessionContext ctx;

    @RolesAllowed({ "UserViewer" })
    public String getSecurityInfo() {
        // Session context injected using the resource annotation
        Principal principal = ctx.getCallerPrincipal();
        return principal.toString();
    }

    @RolesAllowed({ "UserEditor" })
    public String alowedForUserEditor() {
        // Session context injected using the resource annotation
        Principal principal = ctx.getCallerPrincipal();
        return principal.toString();
    }
}