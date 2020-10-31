package com.SCAUComputerClassOneEEE.OSEC.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * @author hlf
 * @date 31/10/2020
 */
public class MainUI {
    private static Label infoLb;

    public static void mainUI() throws URISyntaxException, MalformedURLException {
        System.out.println("1");
        URL url = MainUI.class.getClassLoader().getResource("1.PNG").toURI().toURL();
        Image image = new Image(url.toString());
        ImageView view = new ImageView(image);

        infoLb = new Label();
        infoLb.setTextFill(Color.WHITE);
        AnchorPane.setRightAnchor(infoLb, 10.0);
        AnchorPane.setBottomAnchor(infoLb, 10.0);

        AnchorPane page = new AnchorPane();
        page.getChildren().addAll(view, infoLb);
        Stage stage = new Stage();
        stage.setScene(new Scene(page));
        stage.setWidth(image.getWidth());
        stage.setHeight(image.getHeight());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        initSystem();
        System.out.println("3");
//        stage.close();
    }
    public static void showInfo(String info) {
        Platform.runLater(() -> infoLb.setText(info));
    }

    private static void initSystem() {
        try {
            showInfo("初始化目录...");
            Thread.sleep(1500);
            showInfo("初始化系统配置...");
            Thread.sleep(1500);
            showInfo("版本检测...");
            Thread.sleep(1500);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
