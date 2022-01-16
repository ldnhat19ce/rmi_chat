package com.ldnhat.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class CustomFile implements Serializable {

    private HBox hbox;
    private VBox vbox;
    private Label label;
    private String fileName;
    private ImageView imageView;

    public CustomFile(String fileName, String name) {
        hbox = new HBox();
        vbox = new VBox();
        label = new Label();

        this.fileName = fileName;
        create(name);
    }

    private void create(String name){
        label.setText(name);

        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        vbox.setMinWidth(60);
        vbox.setMaxWidth(60);

        Image image = new Image("/assets/file.png", false);
        imageView = new ImageView(image);

        vbox.getChildren().addAll(imageView, label);
        hbox.getChildren().add(vbox);

        vbox.setCursor(Cursor.HAND);
        hbox.setAlignment(Pos.TOP_RIGHT);
        hbox.setSpacing(10);
    }

    public HBox getHbox() {
        return hbox;
    }

    public String getFileName() {
        return fileName;
    }

    public VBox getVbox() {
        return vbox;
    }

    public void setVbox(VBox vbox) {
        this.vbox = vbox;
        hbox.getChildren().removeAll();
        hbox.getChildren().add(vbox);

    }

    public void setTextColor(String color) {

        label.setTextFill(Paint.valueOf(color));

    }
}




