package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            OSDataCenter.starter.bootStage();
            new Thread(()->{
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (OSDataCenter.starter.isLoaded()) {
                            Platform.runLater(()-> OSDataCenter.starter.secondStage(stage));
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
}