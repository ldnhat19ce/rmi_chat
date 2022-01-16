package com.ldnhat.utils;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActiveCamera extends Thread {

    private Webcam webcam;
    private ImageView imageView;
    private Image image;
    private int width;
    private int height;
    private GridPane gridPane;
    private boolean active;
    private int port;
    private int receivePort;
    private InetAddress receiveAddress;
    private boolean receiveActive;
    private DatagramSocket datagramSocket;

    public ActiveCamera(GridPane gridPane, int value) throws UnknownHostException, SocketException {
        this.gridPane = gridPane;

        width = (int) (gridPane.getPrefWidth() / gridPane.getColumnConstraints().size());
        height = (int) (gridPane.getPrefHeight() / gridPane.getRowConstraints().size());

        System.out.println(Webcam.getWebcams().get(value).getName());
        webcam = Webcam.getWebcamByName(Webcam.getWebcams().get(value).getName());

        active = true;
        receiveActive = false;

        //user là người gọn
        if (value == 0){
            port = 2333;
            receivePort = 2355;
            receiveAddress = InetAddress.getByName("127.0.0.2");
        }else{
            //user là người nhận được cuộc gọi
            port = 2355;
            receivePort = 2333;
            receiveAddress = InetAddress.getByName("127.0.0.1");
        }
        datagramSocket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        super.run();
        webcam.open();
        while (active){
            try{
                Platform.runLater(() -> {
                    if(webcam.isOpen()){
                        gridPane.getChildren().clear();

                        BufferedImage bufferedImage = webcam.getImage();

                        image = SwingFXUtils.toFXImage(bufferedImage, null);
                        imageView = new ImageView(image);
                        imageView.setFitWidth(width);
                        imageView.setFitHeight(height);

                        gridPane.add(imageView, 0, 1);
                        bufferedImage.flush();

                        if (receiveActive){
                            try {
                                sendUDPMessage(bufferedImage);

                                BufferedImage received = receiveUdpMessage();

                                if (received != null){
                                    ImageView imageView1;
                                    Image image1;

                                    image1 = SwingFXUtils.toFXImage(received, null);
                                    imageView1 = new ImageView(image1);
                                    imageView1.setFitWidth(width);
                                    imageView1.setFitHeight(height);

                                    gridPane.add(imageView1, 0, 0);
                                }
                            } catch (IOException e) {
                                Logger.getLogger(ActiveCamera.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    }
                });
            Thread.sleep(150);
            }catch (Exception e){
                Logger.getLogger(ActiveCamera.class
                        .getName()).log(Level.SEVERE, null, e);
            }
        }
    }


    public BufferedImage receiveUdpMessage() throws IOException {
        if (!datagramSocket.isClosed()){
            try{
                DatagramPacket datagramPacket;

                byte[] buf = new byte[50000];

                datagramPacket = new DatagramPacket(buf, buf.length);

                if (!datagramSocket.isClosed()) {

                    datagramSocket.receive(datagramPacket);

                    ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData());

                    /*converts Byte array to buffered image*/
                    return ImageIO.read(bais);
                }
            }catch (Exception e){
                Logger.getLogger(ActiveCamera.class
                        .getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    public void sendUDPMessage(BufferedImage message) throws IOException {
        try {
            DatagramPacket datagramPacket;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(message, "jpg", baos);
            baos.flush();

            byte[] buffer = baos.toByteArray();

            datagramPacket = new DatagramPacket(buffer, buffer.length, receiveAddress, receivePort);

            if (!datagramSocket.isClosed()){
                datagramSocket.send(datagramPacket);
            }
        }catch (Exception e){
            Logger.getLogger(ActiveCamera.class
                    .getName()).log(Level.SEVERE, null, e);
        }

    }

    public Webcam getWebcam() {
        return webcam;
    }

    public void setWebcam(Webcam webcam) {
        this.webcam = webcam;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public InetAddress getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(InetAddress receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public boolean isReceiveActive() {
        return receiveActive;
    }

    public void setReceiveActive(boolean receiveActive) {
        this.receiveActive = receiveActive;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void close(){
        datagramSocket.close();
        webcam.close();
    }

    public static void main(String[] args) {
        System.out.println(Webcam.getWebcamByName(Webcam.getWebcams().get(2).getName()));

    }
}
