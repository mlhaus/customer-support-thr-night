<!DOCTYPE html>
<html>
    <head>
        <title>Customer Support</title>
    </head>
    <body>
        <h2>Login</h2>
        <%
            if((Boolean)request.getAttribute("loginFailed")) {
        %>
                <p style="font-weight: bold;">The username or password you entered are not correct. Please try again.</p>
        <%
            } else {
        %>
                <p>You must log in to access the customer support site.</p>
        <%
            }
        %>
        <form method="POST" action="<c:url value="/login" />">
            <label for="username">Username</label>
            <input type="text" name="username" id="username" /><br><br>
            <label for="password">Password</label>
            <input type="password" name="password" id="password" /><br><br>
            <input type="submit" value="Log In" />
        </form>
    </body>
</html>