package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.op.DiskPane;
import com.SCAUComputerClassOneEEE.OSEC.ui.MainUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class Main extends Application {
    public static DiskPane diskPane = DiskPane.getDiskPane();
    public static Disk disk = Disk.getDisk();
    public static FileTree fileTree = FileTree.getFileTree();
    public static CPU cpu = CPU.getCpu();
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            MainUI.mainUI();
            System.out.println("2");
            Parent root = FXMLLoader.load(getClass().getResource("MyScene.fxml"));
            Scene scene = new Scene(root);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            stage.getIcons().add(new Image("file:" +"src/main/resources/操作系统.png",20, 20,
                    true, true));
            stage.setTitle("myOS");
            stage.resizableProperty().setValue(Boolean.FALSE);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}