package com.ldnhat.controller;

import com.ldnhat.remote.ClientRemoteImpl;
import com.ldnhat.utils.Constant;
import com.ldnhat.utils.DataRecord;
import com.ldnhat.view.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;

public class PlayRecordController implements Initializable {
    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;

    private Media media;
    private MediaPlayer mediaPlayer;

    private ArrayList<File> songs;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private TimerTask task;

    private boolean running;

    private List<File> fileWav;

    public PlayRecordController() {
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ClientRemoteImpl currentClient = Main.getClient();

        try {
            fileWav = currentClient.getServer()
                    .findAllFileByUserId(currentClient.getClient().getId(), Constant.WAV);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        songs = new ArrayList<>();

        if(fileWav != null) {

            songs.addAll(fileWav);
        }else {
            System.out.println("file is null");
        }
        String findCurrentSong = Objects.requireNonNull(songs.stream()
                .filter(s -> DataRecord.recordName.equals(s.getName()))
                .findAny().orElse(null)).toString();
        media = new Media(Constant.serverFile+"/"+findCurrentSong);
        mediaPlayer = new MediaPlayer(media);

        songLabel.setText(findCurrentSong);

        for (int speed : speeds) {

            speedBox.getItems().add(speed + "%");
        }

        speedBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener((arg01, arg11, arg2) -> mediaPlayer.setVolume(volumeSlider.getValue() * 0.01));

        songProgressBar.setStyle("-fx-accent: #00FF00;");
    }

    public void playMedia() {

        beginTimer();
        changeSpeed(null);
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void pauseMedia() {

        cancelTimer();
        mediaPlayer.pause();
    }

    public void resetMedia() {

        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void previousMedia() {

        if(songNumber > 0) {

            songNumber--;

            mediaPlayer.stop();

            if(running) {

                cancelTimer();
            }

            media = new Media(Constant.serverFile+"/"+songs.get(songNumber));
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
        else {

            songNumber = songs.size() - 1;

            mediaPlayer.stop();

            if(running) {

                cancelTimer();
            }

            media = new Media(Constant.serverFile+"/"+songs.get(songNumber));
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
    }

    public void nextMedia() {

        if(songNumber < songs.size() - 1) {

            songNumber++;

            mediaPlayer.stop();

            if(running) {

                cancelTimer();
            }

            media = new Media(Constant.serverFile+"/"+songs.get(songNumber));
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
        else {

            songNumber = 0;

            mediaPlayer.stop();

            media = new Media(Constant.serverFile+"/"+songs.get(songNumber));
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
    }

    public void changeSpeed(ActionEvent event) {

        if(speedBox.getValue() == null) {

            mediaPlayer.setRate(1);
        }
        else {

            //mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current/end);

                if(current/end == 1) {

                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

}
