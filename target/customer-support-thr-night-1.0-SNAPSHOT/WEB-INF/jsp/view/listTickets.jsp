<%@ page import="java.util.Map" %>
<%
    @SuppressWarnings("unchecked")
    Map<Integer, Ticket> ticketDatabase = (Map<Integer, Ticket>)request.getAttribute("ticketDatabase");
    int numTickets = ticketDatabase.size();
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Customer Support</title>
    </head>
    <body>
        <a href="<c:url value="/login?"><c:param name="logout" value="true" /></c:url>">Logout</a>
        <h2>Tickets</h2>
        <p><a href="<c:url value="/tickets">
                  <c:param name="action" value="create" />
              </c:url>">Create Ticket</a></p>
        
        <p style="font-style: italic">There  <%= numTickets == 1 ? "is " : "are "%><%= Integer.toString(numTickets)%><%= numTickets == 1 ? " ticket " : " tickets "%> in the system.</p>
        
        <%
            if(ticketDatabase.size() > 0) {
        %>
                <ul>
        <%
                for(int id : ticketDatabase.keySet()) {
                    String idString = Integer.toString(id);
                    Ticket ticket = ticketDatabase.get(id);
        %>
                    <li><a href="
                        <c:url value="/tickets">
                            <c:param name="action" value="view" />
                            <c:param name="ticketId" value="<%= idString %>" />
                        </c:url>
                    ">Ticket #<%= idString %> - From: <%= ticket.getCustomerName() %></a></li>
        <%
                }
        %>
                </ul>
        <%
            }
        %></body>
</html>