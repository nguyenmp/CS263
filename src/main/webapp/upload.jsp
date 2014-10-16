<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<% BlobstoreService blobStore = BlobstoreServiceFactory.getBlobstoreService(); %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Task Setup</title>
</head>
<body>
<p>Enqueue a value, to be processed by a worker.</p>
<form action="<%= blobStore.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
    <p>Please enter a value and a name:</p>
    <input type="file" name="file" />
    <input type="submit" />
</form>
</body>

</html>