package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.controller.MainSceneController;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.dataService.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.ui.MainUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class Main extends Application {

    private final Stage stage = new Stage();
    private boolean allReady = false;
    private static Label infoLb;
    private static final int LENGTH_OF_OS_CODE = 30;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            bootStage();
            new Thread(()->{
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (allReady) {
                            Platform.runLater(()-> secondStage(stage));
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主界面
     * @param stage 是
     */
    public void secondStage(Stage stage){
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("background.css").toExternalForm());
            stage.setScene(scene);
            stage.setWidth(1350);
            stage.setHeight(950);
            //stage.initStyle(StageStyle.DECORATED);
            stage.getIcons().add(new Image("file:" +"src/main/resources/操作系统.png",20, 20,
                    true, true));
            stage.setTitle("模拟操作系统实现");
            stage.show();
            bootOS();
            stage.setOnCloseRequest(e -> {
                Disk.getDisk().writeDiskToFile();
                if (MainSceneController.getCoreThread() != null) {
                    MainSceneController.getCoreThread().stop();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动界面
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public void bootStage() throws URISyntaxException, MalformedURLException {
        URL url = MainUI.class.getClassLoader().getResource("加载界面.png").toURI().toURL();
        Image image = new Image(url.toString());
        ImageView view = new ImageView(image);

        infoLb = new Label();
        infoLb.setTextFill(Color.WHITE);
        AnchorPane.setRightAnchor(infoLb, 10.0);
        AnchorPane.setBottomAnchor(infoLb, 10.0);

        AnchorPane page = new AnchorPane();
        page.getChildren().addAll(view, infoLb);

        stage.setScene(new Scene(page));
        stage.setWidth(image.getWidth());
        stage.setHeight(image.getHeight());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("file:" +"src/main/resources/操作系统.png",20, 20,
                true, true));
        Thread t = new Thread(this::initSystem);
        t.start();
        stage.show();
    }

    public void showInfo(String info) {
        Platform.runLater(() -> infoLb.setText(info));
    }

    private void initSystem() {
        try {
            showInfo("初始化目录...");
            Thread.sleep(1500);
            showInfo("初始化系统配置...");
            Thread.sleep(1500);
            allReady = true;
            showInfo("版本检测...");
            Thread.sleep(1500);
            Platform.runLater(stage::close);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟往内存中装在操作系统代码
     */
    public static void bootOS() {
        char[] osCode = new char[LENGTH_OF_OS_CODE];
        try {
            PCB os = new PCB(0,osCode.length, ProcessSimService.getColors().get(0),0,"os");
            ProcessSimService.getColors().remove(0);
            CPU.allPCB.add(os);
            Memory.getMemory().malloc(osCode);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}