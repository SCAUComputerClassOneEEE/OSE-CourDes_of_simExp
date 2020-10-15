package com.SCAUComputerClassOneEEE.OSEC.controller;


import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author: Sky
 * @Date: 2020/10/14 21:30
 */

public class MySceneController implements Initializable {

    public static SimpleObjectProperty<Long> cpuTimeSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> timeSliceSim = new SimpleObjectProperty<>();

    @FXML
    private TextField timeSlice;

    @FXML
    private TextField cpuTime;

    @FXML
    private Button button;

    @FXML
    private Tab fileSystem;

    @FXML
    private TableView<PCB> readyTable;

    @FXML
    private TableColumn<PCB,Integer> readyID;

    @FXML
    public void test(){
        setCPUTime(1);

    }


    private void setCPUTime(long time){
        cpuTime.setText(String.valueOf(time));
    }

    private void setTimeSlice(int time){
        timeSlice.setText(String.valueOf(time));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileTree fileTree = FileTree.getFileTree();
        BorderPane root = new BorderPane();

        Terminal terminal = new Terminal(fileTree);

        root.setRight(new FilePane());
        root.setLeft(fileTree.getVBox());
        //fileSystem.setContent(root);
        root.setCenter(terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);

        cpuTimeSim.addListener((observable, oldValue, newValue)->{
            setCPUTime(newValue.longValue());
        });

        timeSliceSim.addListener((observable, oldValue, newValue)->{
            setTimeSlice(newValue.intValue());
        });

        readyTable.setItems(CPU.readyQueue);

        readyID.setCellValueFactory(new PropertyValueFactory<>("processId"));

    }
}
