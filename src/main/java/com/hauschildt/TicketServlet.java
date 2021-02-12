/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hauschildt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author k0519415
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/tickets"})
public class TicketServlet extends HttpServlet {
    
    private Map<Integer, Ticket> ticketDatabase = new LinkedHashMap<>();
    private volatile int TICKET_ID_SEQUENCE = 1;

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "create":
                showTicketForm(response);
                break;
            case "view":
                viewTicket(request, response);
                break;
            case "download":

                break;
            case "list":
            default:
                listTickets(response);
                break;
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "create":
                createTicket(request, response);
                break;
            case "list":
            default:
                listTickets(response);
                break;
        }
    }
    
    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idString = request.getParameter("ticketId");
        Ticket ticket = getTicket(idString);
        PrintWriter writer = writeHeader(response);
        if (ticket == null) {
            writer.append("<h2>Ticket not found</h2>\r\n");
        } else {
            writer.append("<h2>Ticket #" + idString + "</h2>\r\n")
                    .append("<p><strong>Customer Name</strong><br>")
                    .append(ticket.getCustomerName() + "</p>\r\n")
                    .append("<p><strong>Subject</strong><br>")
                    .append(ticket.getSubject() + "</p>\r\n")
                    .append("<p><strong>Message</strong><br>")
                    .append(ticket.getBody() + "</p>\r\n");
        }
        writer.append("<a href=\"tickets\">Return to list tickets</a>\r\n");
        writeFooter(writer);
    }
    
    private Ticket getTicket(String idString) throws ServletException, IOException {
        if (idString == null || idString.length() == 0) {
            return null;
        }

        try {
            Ticket ticket = ticketDatabase.get(Integer.parseInt(idString));
            if (ticket == null) {
                return null;
            }
            return ticket;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Ticket ticket = new Ticket();
        ticket.setCustomerName(request.getParameter("customerName"));
        ticket.setSubject(request.getParameter("subject"));
        ticket.setBody(request.getParameter("body"));

        int id;
        synchronized (this) {
            id = this.TICKET_ID_SEQUENCE++;
            ticketDatabase.put(id, ticket);
        }

        response.sendRedirect("tickets?action=view&ticketId=" + id);
    }
    
    private void showTicketForm(HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = writeHeader(response);

        writer.append("  <h2>Create a Ticket</h2>\r\n")
                .append("  <form method=\"POST\" action=\"tickets\">\r\n")
                .append("    <input type=\"hidden\" name=\"action\" value=\"create\" />\r\n")
                .append("    <label for=\"customerName\">Your Name</label><br>\r\n")
                .append("    <input type=\"text\" name=\"customerName\" id=\"customerName\" required/><br><br>\r\n")
                .append("    <label for=\"subject\">Subject</label><br>\r\n")
                .append("    <input type=\"text\" name=\"subject\" id=\"subject\" required/><br><br>\r\n")
                .append("    <label for=\"body\">Body</label><br>\r\n")
                .append("    <textarea name=\"body\" id=\"body\" rows=\"5\" cols=\"30\" required></textarea><br><br>\r\n")
                .append("    <input type=\"submit\" value=\"Submit\"/>\r\n")
                .append("  </form>\r\n");

        writeFooter(writer);
    }

    private void listTickets(HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = writeHeader(response);
        int numTickets = ticketDatabase.size();
        writer.append("    <h2>Tickets</h2>\r\n")
                .append("    <p><a href=\"tickets?action=create\">Create Ticket</a></p>\r\n")
                .append("    <p style=\"font-style: italic\">There ")
                .append(numTickets == 1 ? "is " : "are ")
                .append(Integer.toString(numTickets))
                .append(numTickets == 1 ? " ticket " : " tickets ")
                .append(" in the system.</p>\r\n");
        
        if (ticketDatabase.size() > 0) {
            writer.append("    <ul>\r\n");
            for (int id : ticketDatabase.keySet()) {
                String idString = Integer.toString(id);
                Ticket ticket = ticketDatabase.get(id);
                writer.append("      <li><a href=\"tickets?action=view&ticketId=" + idString + "\">")
                        .append("Ticket #" + idString + " - ")
                        .append("From: " + ticket.getCustomerName())
                        .append("</a></li>\r\n");
            }
            writer.append("    </ul>\r\n");
        }

        writeFooter(writer);
    }

    private PrintWriter writeHeader(HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("  <head>\r\n")
                .append("    <title>Customer Support</title>\r\n")
                .append("  </head>\r\n")
                .append("  <body>\r\n");

        return writer;
    }

    private void writeFooter(PrintWriter writer) {
        writer.append("  </body>\r\n")
                .append("</html>\r\n");
        writer.close();
    }

}
