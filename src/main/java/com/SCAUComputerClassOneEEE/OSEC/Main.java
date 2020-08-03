package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Disk disk = new Disk();
        BorderPane root = new BorderPane();
        Terminal terminal = new Terminal(disk);
        FileTree fileTree = new FileTree(new VBox(), disk);

        root.setLeft(fileTree.getVBox());
        root.setCenter(terminal.textArea);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}