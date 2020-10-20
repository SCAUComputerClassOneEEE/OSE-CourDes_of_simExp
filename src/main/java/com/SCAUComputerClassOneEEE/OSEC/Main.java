package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.op.DiskPane;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            Parent root = FXMLLoader.load(getClass().getResource("MyScene.fxml"));
            Scene scene = new Scene(root);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}