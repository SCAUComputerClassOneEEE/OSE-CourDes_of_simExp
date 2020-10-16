package com.SCAUComputerClassOneEEE.OSEC.controller;


import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.EAT;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.Equipment;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import javax.print.attribute.standard.NumberUp;
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
    private TableView<PCB> blockTable;
    @FXML
    private TableColumn<PCB,Integer> blockID;


    @FXML
    private TableView<EAT> equipmentTable;
    @FXML
    private TableColumn<EAT,Character> equipmentID;
    @FXML
    private TableColumn<EAT, Number> useEquipmentPCBID;
    @FXML
    private TableColumn<EAT, Number> time;


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
        FileTree fileTree = Main.fileTree;
        BorderPane root = new BorderPane();
        Terminal terminal = new Terminal(fileTree);
        root.setRight(new FilePane());
        root.setLeft(fileTree.getVBox());
        fileSystem.setContent(root);
        root.setCenter(Terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);


        initTime();
        initEquipmentTable();
        initReadyTable();
        initBlockTable();
    }

    private void initTime(){
        cpuTimeSim.addListener((observable, oldValue, newValue)->{
            setCPUTime(newValue.longValue());
        });

        timeSliceSim.addListener((observable, oldValue, newValue)->{
            setTimeSlice(newValue.intValue());
        });

    }

    private void initEquipmentTable(){
        //给表添加数据源
        equipmentTable.setItems(Equipment.runningLists);
        //设置列属性
        equipmentID.setCellValueFactory(new PropertyValueFactory<>("eqID"));
        useEquipmentPCBID.setCellValueFactory(new PropertyValueFactory<>("pcbID"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    private void initReadyTable(){
        readyTable.setItems(CPU.readyQueue);
        readyID.setCellValueFactory(new PropertyValueFactory<>("processId"));
    }

    private void initBlockTable(){
        blockTable.setItems(CPU.blockedQueue);
        blockID.setCellValueFactory(new PropertyValueFactory<>("processId"));
    }


}
