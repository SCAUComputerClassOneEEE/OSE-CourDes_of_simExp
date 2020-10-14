package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import com.SCAUComputerClassOneEEE.OSEC.utils.MainUI;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class Main extends Application {

    public static Disk disk = Disk.getDisk();
    public static FileTree fileTree = FileTree.getFileTree();
    public static CPU cpu = CPU.getCpu();
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        /*BorderPane root = new BorderPane();

        Terminal terminal = new Terminal(fileTree);

        root.setRight(new FilePane());
        root.setLeft(fileTree.getVBox());
        root.setCenter(terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        CPU cpu = new CPU();
        cpu.init();*/
        //MainUI.mainUI.show();
        TaskThreadPools.execute(cpu);
    }


}