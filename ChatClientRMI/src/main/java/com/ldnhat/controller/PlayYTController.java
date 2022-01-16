package com.ldnhat.controller;

import com.ldnhat.utils.DataYT;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayYTController implements Initializable {

    @FXML
    private WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String linkYT = DataYT.linkYT;

        webView.getEngine().load(linkYT);
        webView.setPrefSize(640, 390);
    }
}
