package org.sandbox.javaee.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sandbox.javaee.service.CheckSecurityService;
import org.sandbox.javaee.util.Primary;

@WebServlet("/SecurityCheckError")
@ServletSecurity(@HttpConstraint(rolesAllowed = "UserEditor"))
public class CheckSecurityErrorServlet extends HttpServlet {

    private static final long serialVersionUID = -8327303128695573844L;

    private static String PAGE_HEADER = "<html><head><title>SecurityCheck</title></head><body>";
    private static String PAGE_FOOTER = "</body></html>";

    @Inject @Primary
    private CheckSecurityService service;

    /**
     * Servlet entry point method which calls securedEJB.getSecurityInfo()
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        // Get security principal
        String principal = service.getSecurityInfo();
        // Get user name from login principal
        String remoteUser = req.getRemoteUser();
        // Get authentication type
        String authType = req.getAuthType();

        writer.println(PAGE_HEADER);
        writer.println("<h1>" + "Successfully called CheckSecurityService " + "</h1>");
        writer.println("<p>" + "Principal : " + principal + "</p>");
        writer.println("<p>" + "Remote User : " + remoteUser + "</p>");
        writer.println("<p>" + "Authentication Type : " + authType + "</p>");
        writer.println("<p>" + "isUserInRole 'UserViewer' : " + req.isUserInRole("UserViewer") + "</p>");
        writer.println("<p>" + "isUserInRole 'UserEditor' : " + req.isUserInRole("UserEditor") + "</p>");
        writer.println(PAGE_FOOTER);
        writer.close();
    }
}