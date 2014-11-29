package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.util.UUID;

public class ExampleSelfBlobstore extends HttpServlet {
    private static final String ENCODING = "UTF-8";
    private static final String BUCKET_NAME = "mark_nguyen_foo";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GcsService gcsService = GcsServiceFactory.createGcsService();
        String objectName = UUID.randomUUID().toString();
        GcsFilename filename = new GcsFilename(BUCKET_NAME, objectName);
        GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
        OutputStream outputStream = Channels.newOutputStream(outputChannel);
        byte[] data = objectName.getBytes(Charset.forName(ENCODING));

        outputStream.write(data);
        outputStream.close();

        resp.getWriter().println(objectName);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String objectName = req.getParameter("object_name");
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobKey blobKey = blobstoreService.createGsBlobKey(String.format("/gs/%s/%s", BUCKET_NAME, objectName));
        blobstoreService.serve(blobKey, resp);
    }
}
