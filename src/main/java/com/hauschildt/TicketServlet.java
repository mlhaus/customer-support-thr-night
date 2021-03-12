/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hauschildt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author k0519415
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/tickets"})
@MultipartConfig(
        fileSizeThreshold = 5_242_880, //5MB
        maxFileSize = 20_971_520L //20MB
)
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
        HttpSession session = request.getSession();
        if(session.getAttribute("username") == null) {
            response.sendRedirect("login");
            return;
        }
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "create":
                showTicketForm(request, response);
                break;
            case "view":
                viewTicket(request, response);
                break;
            case "download":
                downloadAttachment(request, response);
                break;
            case "list":
            default:
                listTickets(request, response);
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
        HttpSession session = request.getSession();
        if(session.getAttribute("username") == null) {
            response.sendRedirect("login");
            return;
        }
        
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
                listTickets(request, response);
                break;
        }
    }
    
    private void downloadAttachment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idString = request.getParameter("ticketId");
        Ticket ticket = getTicket(idString);
        String name = request.getParameter("attachment");
        if (ticket == null || name == null) {
            response.sendRedirect("tickets");
            return;
        }

        Attachment attachment = ticket.getAttachment(name);
        if (attachment == null) {
            response.sendRedirect("tickets?action=view&ticketId=" + idString);
            return;
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getName());
        response.setContentType("application/octet-stream");

        try (ServletOutputStream stream = response.getOutputStream()) {
            stream.write(attachment.getContents());
        }
    }
    
    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idString = request.getParameter("ticketId");
        Ticket ticket = getTicket(idString);
        if (ticket == null) {
            return;
        }

        request.setAttribute("ticketId", idString);
        request.setAttribute("ticket", ticket);

        request.getRequestDispatcher("/WEB-INF/jsp/view/viewTicket.jsp").forward(request, response);
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
        ticket.setCustomerName((String)request.getSession().getAttribute("username"));
        ticket.setSubject(request.getParameter("subject"));
        ticket.setBody(request.getParameter("body"));
        
        Part filePart = request.getPart("file1");
        if (filePart != null && filePart.getSize() > 0) {
            Attachment attachment = processAttachment(filePart);
            if (attachment != null) {
                ticket.addAttachment(attachment);
            }
        }

        int id;
        synchronized (this) {
            id = this.TICKET_ID_SEQUENCE++;
            ticketDatabase.put(id, ticket);
        }

        response.sendRedirect("tickets?action=view&ticketId=" + id);
    }
    
    private Attachment processAttachment(Part filePart) throws IOException {
        Attachment attachment = new Attachment();
        try (InputStream inputStream = filePart.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            attachment.setName(filePart.getSubmittedFileName());
            attachment.setContents(outputStream.toByteArray());
        }
        return attachment;
    }
    
    private void showTicketForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/view/ticketForm.jsp").forward(request, response);
    }

    private void listTickets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("ticketDatabase", this.ticketDatabase);
        request.getRequestDispatcher("/WEB-INF/jsp/view/listTickets.jsp").forward(request, response);
    }

}
