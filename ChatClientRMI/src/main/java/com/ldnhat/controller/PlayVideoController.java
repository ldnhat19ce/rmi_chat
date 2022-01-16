package com.ldnhat.controller;

import com.ldnhat.utils.DataVideo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayVideoController implements Initializable {
    private String dir = "http://192.168.1.4:8080/ServerSaveFile_war_exploded/file/";

    @FXML
    private MediaView mediaView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("data video: "+DataVideo.videoName);
        //Converts media to string URL
        Media media = new Media(dir+DataVideo.videoName);
        MediaPlayer player = new MediaPlayer(media);
        player.setAutoPlay(true);
        mediaView.setMediaPlayer(player);
        mediaView.setPreserveRatio(true);

    }
}
