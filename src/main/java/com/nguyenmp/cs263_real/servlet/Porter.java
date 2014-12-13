package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.tools.cloudstorage.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Porter extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GcsService gcsService = GcsServiceFactory.createGcsService();
        ListOptions options = new ListOptions.Builder().setRecursive(true).build();
        ListResult files = gcsService.list("mark_nguyen_foo", options);
        while (files.hasNext()) {
            ListItem file = files.next();
            if (file.getName().startsWith("by_computer")) continue;
            GcsFilename filename = new GcsFilename("mark_nguyen_foo", file.getName());
            GcsFilename newfilename = new GcsFilename("mark_nguyen_foo", "by_computer/by_day/" + file.getName());
            System.out.println(newfilename.getObjectName());
            gcsService.copy(filename, newfilename);
        }
    }
}
