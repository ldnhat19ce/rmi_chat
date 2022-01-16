package com.ldnhat.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class FileService {

    public String saveImage(HttpServletRequest request, HttpServletResponse response, int number){
        String test = "";
        String nameOfImage = "";
        ServletContext context = request.getSession().getServletContext();
        final String address = context.getRealPath("/file/");

        final int MaxMemorySize = 1024 * 1024 * 3;

        final int MaxRequestSize = 1024 * 1024 * 50;

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (!isMultipart) {
            test = "thiếu enctype: multipart/form-data";

        }
        System.out.println(test);
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(MaxMemorySize);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Set overall request size constraint
        upload.setSizeMax(MaxRequestSize);

        // Parse the request
        try {
            List<FileItem> items = upload.parseRequest(request);


            for (FileItem item : items){
                System.out.println(item.getName());
            }

            // Process the uploaded items
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();

                if (!item.isFormField()) {

                    String name = item.getName().substring(0, item.getName().lastIndexOf("."));
                    String extension = item.getName().substring(item.getName().lastIndexOf("."), item.getName().length());
                    String fileName = name+"_"+number+extension;
                    nameOfImage = fileName;
                    // pathfile: vị trí mà chúng ta muốn upload file vào
                    // gửi cho server

                    String pathFile = address + File.separator + fileName;

                    File uploadedFile = new File(pathFile);
                    boolean kt = uploadedFile.exists();

                    try {
                        item.write(uploadedFile);
                        test = "success";

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        test = e.getMessage();
                        System.out.println(test);
                    }

                } else {
                    test = "failed";
                }
            }

        } catch (FileUploadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nameOfImage;
    }
}
