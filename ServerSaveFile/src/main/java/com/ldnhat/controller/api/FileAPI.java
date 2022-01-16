package com.ldnhat.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldnhat.model.FileModel;
import com.ldnhat.service.FileService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@WebServlet(urlPatterns = {"/api/files"})
public class FileAPI extends HttpServlet {

    private FileService fileService;

    public FileAPI() {
        fileService = new FileService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String filePath = "/file";
        int number = Objects.requireNonNull(new File(getServletContext().getRealPath(filePath)).list()).length + 1;
        System.out.println(number);
        String fileName = fileService.saveImage(request, response, number);
        System.out.println("file name: "+fileName);

        objectMapper.writeValue(response.getOutputStream(), fileName);
    }
}
