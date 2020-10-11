package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    public static Disk disk = Disk.getDisk();
    public static CPU cpu = CPU.getCPU();

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();

        FileTree fileTree = new FileTree(new VBox());
        Terminal terminal = new Terminal(fileTree);

        root.setRight(new FilePane());
        root.setLeft(fileTree.getVBox());
        root.setCenter(terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);



        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void boot(){
        TaskThreadPools.execute(cpu);
    }

}