<%
    String ticketId = (String)request.getAttribute("ticketId");
    Ticket ticket = (Ticket)request.getAttribute("ticket");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Customer Support</title>
    </head>
    <body>
        <h2>Ticket #<%= ticketId %></h2>
        <p><strong>Customer Name</strong><br>
            <%= ticket.getCustomerName() %></p>
        <p><strong>Subject</strong><br>
            <%= ticket.getSubject() %></p>
        <p><strong>Message</strong><br>
            <%= ticket.getBody() %></p>
        <%
            if(ticket.getNumberOfAttachments() > 0) {
        %>
            <p><strong>Attachments</strong> 
        <%
                for(Attachment attachment : ticket.getAttachments()) {
        %>
                    <br><%= attachment.getName() %>&nbsp;
                    <a href="
                        <c:url value="/tickets">
                            <c:param name="action" value="download" />
                            <c:param name="ticketId" value="<%= ticketId %>" />
                            <c:param name="attachment" value="<%= attachment.getName() %>" />
                        </c:url>
                    ">Download</a>
        <%
                }
        %>
            </p>
        <%
            }
        %>
        <a href="<c:url value="/tickets" />">Return to list tickets</a>
    </body>
</html>