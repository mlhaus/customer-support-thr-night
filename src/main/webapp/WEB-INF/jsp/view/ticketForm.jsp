<!DOCTYPE html>
<html>
    <head>
        <title>Customer Support</title>
    </head>
    <body>
        <h2>Create a Ticket</h2>
        <form method="POST" action="tickets" enctype="multipart/form-data">
            <input type="hidden" name="action" value="create" />
            <label for="customerName">Your Name</label><br>
            <input type="text" name="customerName" id="customerName" required/><br><br>
            <label for="subject">Subject</label><br>
            <input type="text" name="subject" id="subject" required/><br><br>
            <label for="body">Body</label><br>
            <textarea name="body" id="body" rows="5" cols="30" required></textarea><br><br>
            <label for="file1">Attachment</label><br>
            <input type="file" name="file1" id="file1"/><br><br>
            <input type="submit" value="Submit"/>
        </form>
    </body>
</html>