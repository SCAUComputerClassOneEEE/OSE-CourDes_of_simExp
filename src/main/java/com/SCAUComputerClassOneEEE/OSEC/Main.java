package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    public static Disk disk = new Disk();
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();

        FileTree fileTree = new FileTree(new VBox(), disk);
        Terminal terminal = new Terminal(fileTree);

        root.setLeft(fileTree.getVBox());
        root.setCenter(terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        //test();
    }

    void test() throws Exception {
        Memory memoryTest = new Memory();
        System.out.println("return process pointer " + memoryTest.malloc(10, "1234567890"));
        System.out.println("return process pointer " + memoryTest.malloc(10, "1234567890"));
        System.out.println("return process pointer " + memoryTest.malloc(10, "1234567890"));
        memoryTest.recovery(10);
        System.out.println("return process pointer " + memoryTest.malloc(20, "12345678901234567890"));
        memoryTest.recovery(30);
        memoryTest.MAT_display();
    }
}