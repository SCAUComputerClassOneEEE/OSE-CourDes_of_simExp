package com.SCAUComputerClassOneEEE.OSEC.starter;

import com.SCAUComputerClassOneEEE.OSEC.controller.MainSceneController;
import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import com.SCAUComputerClassOneEEE.OSEC.data_model.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.data_model.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.data_model.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.data_service.ProcessSimService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * @author best lu & hlf
 * @date 31/10/2020
 */
@Data
public class Starter {

    private static Starter starter = new Starter();

    private final Stage stage = new Stage();
    private boolean allReady = false;
    private static Label infoLb;
    private static final int LENGTH_OF_OS_CODE = 30;

    public static Starter getStarter() {
        return starter;
    }

    public boolean isLoaded() {
        return allReady;
    }
    /**
     * 主界面
     * @param stage
     */
    public void secondStage(Stage stage){
        Parent root;
        try {
            URL fxml = Starter.class.getClassLoader().getResource("MainScene.fxml").toURI().toURL();
            //root = FXMLLoader.load(Starter.class.getResource("MainScene.fxml"));
            root = FXMLLoader.load(fxml);
            Scene scene = new Scene(root);

            scene.getStylesheets().add(Starter.class.getClassLoader().getResource("background.css").toExternalForm());

            stage.setScene(scene);
            stage.setWidth(1350);
            stage.setHeight(950);
            URL icon = Starter.class.getClassLoader().getResource("操作系统.png").toURI().toURL();
            stage.getIcons().add(new Image(icon.toString(),20, 20,
                    true, true));
            stage.setTitle("模拟操作系统实现");
            stage.show();
            bootOS();

            askForLoadExistDat();

            stage.setOnCloseRequest(e -> {
                askForSaveDat();
                if (MainSceneController.getCoreThread() != null) {
                    MainSceneController.getCoreThread().stop();
                }
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void askForLoadExistDat() throws URISyntaxException, MalformedURLException {
        URL dataFile = Starter.class.getClassLoader().getResource("diskDat.dat").toURI().toURL();
        if (new File(dataFile.toURI()).exists()){
            tipPane("检测到已存在的磁盘数据，是否加载？",true);
        }
    }

    private void askForSaveDat() {
        tipPane("是否保存磁盘数据",false);
    }
    /**
     * 启动界面
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public void bootStage() throws URISyntaxException, MalformedURLException {
        URL url = Starter.class.getClassLoader().getResource("加载界面.png").toURI().toURL();
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
        URL icon = Starter.class.getClassLoader().getResource("操作系统.png").toURI().toURL();
        stage.getIcons().add(new Image(icon.toString(),20, 20,
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

    private void tipPane(String tips,boolean type) {
        Stage stage = new Stage();

        //锁定当前提示框
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("提示");
        stage.setMinWidth(300);
        stage.setMaxHeight(300);
        Label tipLabel = new Label(tips);
        Button yes = new Button(("是"));
        Button no = new Button("否");

        HBox hBox = new HBox();
        hBox.getChildren().addAll(yes, no);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(30);
        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: White");
        vBox.getChildren().addAll(tipLabel, hBox);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene (vBox);
        stage.setScene(scene);
        stage.show();
        if (type) {
            yes.setOnAction (e -> {
                try {
                    OSDataCenter.disk.readDiskFromFile();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
                stage.close();
            });
        }else {
            yes.setOnAction (e -> {
                try {
                    OSDataCenter.disk.writeDiskToFile();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
                stage.close();
            });
        }

        no.setOnAction(e ->stage.close());
    }
}
