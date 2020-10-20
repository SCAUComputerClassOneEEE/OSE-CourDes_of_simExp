package com.SCAUComputerClassOneEEE.OSEC.controller;


import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.EAT;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim.Equipment;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.op.DiskPane;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.print.attribute.standard.NumberUp;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @Author: Sky
 * @Date: 2020/10/14 21:30
 */

public class MySceneController implements Initializable {
    public static SimpleObjectProperty<Integer> diskChange = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Long> cpuTimeSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> timeSliceSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningPCBIDSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningIRSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> intermediateResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> finalResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> memoryChange = new SimpleObjectProperty<>();



    @FXML
    private TextField cpuTime;//系统时间
    @FXML
    private TextField timeSlice;//剩余时间片
    @FXML
    private TextField runningPCBID;//正在运行的进程ID
    @FXML
    private TextField runningIR;//正在运行的指令
    @FXML
    private TextField intermediateResult;//程序执行中间结果
    @FXML
    private TextField finalResult;//程序运行最终结果，即X的值



    @FXML
    private Button beginORStop;

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
    private TableColumn<PCB,String> waitEq;

    @FXML
    private Pane memoryPane;




    @FXML
    private TableView<EAT> equipmentTable;
    @FXML
    private TableColumn<EAT,Character> equipmentID;
    @FXML
    private TableColumn<EAT, Number> useEquipmentPCBID;
    @FXML
    private TableColumn<EAT, Number> time;


    @FXML
    public void beginORStop(){
        if (beginORStop.getText().equals("开始")){
            TaskThreadPools.execute(Main.cpu);
            beginORStop.setText("暂停");
        }
        else {
            beginORStop.setText("开始");
        }
    }


    private void setCPUTime(long time){
        cpuTime.setText(String.valueOf(time));
    }

    private void setTimeSlice(int time){
        timeSlice.setText(String.valueOf(time));
    }

    private void setRunningPCBID(String ID){
        runningPCBID.setText(ID);
    }

    private void setRunningIR(String IR){
        runningIR.setText(IR);
    }

    private void setIntermediateResult(String result){
        intermediateResult.setText(result);
    }

    private void setFinalResult(String result){
        finalResult.setText(result);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        beginORStop.setText("开始");

        initTime();
        initEquipmentTable();
        initReadyTable();
        initBlockTable();
        //initMemoryPane();
        initFileSystem();

        memoryChange.setValue(0);
        memoryChange.addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(this::updateMemoryPane);

        }));
        diskChange.setValue(0);
        diskChange.addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(() -> updateDiakPane(newValue));
        }));
    }

    private void updateDiakPane(int index) {
        DiskPane.BlockPane[] blockPane = Main.diskPane.getBlockPanes();
        System.out.println(blockPane[index]);
        Main.diskPane.updateType(index);
    }

    private BorderPane root = new BorderPane();
    private BorderPane bottom = new BorderPane();
    private FilePane rightPane = new FilePane();
    private VBox leftPane = Main.fileTree.getVBox();
    private Terminal centerPane = new Terminal(Main.fileTree);//初始化命令行

    private void initFileSystem(){
        root.setLeft(Main.fileTree.getVBox());
        root.setRight(rightPane);
        root.setCenter(centerPane.textArea);
        root.setBottom(bottom);
        bottom.setLeft(OpenFileManager.openFileTableView);
        bottom.setCenter(Main.diskPane.getRoot());
        fileSystem.setContent(root);
        Platform.runLater(this::initFileSystem);
    }

    private void initTime(){
        cpuTimeSim.addListener((observable, oldValue, newValue)-> setCPUTime(newValue.longValue()));
        cpuTime.setText("0");
        timeSliceSim.addListener((observable, oldValue, newValue)-> setTimeSlice(newValue.intValue()));
        timeSlice.setText("6");
        runningPCBIDSim.addListener((observable, oldValue, newValue)-> setRunningPCBID(newValue));

        runningIRSim.addListener((observable, oldValue, newValue)-> setRunningIR(newValue));

        intermediateResultSim.addListener((observable, oldValue, newValue)-> setIntermediateResult(newValue));

        finalResultSim.addListener((observable, oldValue, newValue)-> setFinalResult(newValue));
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
        waitEq.setCellValueFactory(new PropertyValueFactory<>("waitEq"));
    }

    private void initMemoryPane(){

        Rectangle rect = new Rectangle(50, 147);
        memoryPane.getChildren().add(rect);


    }


    private void updateMemoryPane(){
        memoryPane.getChildren().clear();
        for (int i = 0; i < CPU.allPCB.size() ; i++){
            PCB drawingPCB = CPU.allPCB.get(i);
            double width = ((double)drawingPCB.getTotalSize() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());
            double layoutX = ((double)drawingPCB.getPointerToMemory() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());

            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(width,memoryPane.getHeight());
            stackPane.setLayoutX(layoutX);

            Rectangle rectangle = new Rectangle(width, memoryPane.getHeight(), drawingPCB.getColor());
            Label label = new Label(String.valueOf(drawingPCB.getProcessId()));
            if (i==0){
                label.setText("OS");
            }
            stackPane.getChildren().add(rectangle);
            stackPane.getChildren().add(label);
            memoryPane.getChildren().add(stackPane);
        }
    }



}
