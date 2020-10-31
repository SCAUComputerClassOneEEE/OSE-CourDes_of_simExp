package com.SCAUComputerClassOneEEE.OSEC.controller;


import com.SCAUComputerClassOneEEE.OSEC.utils.OS;
import com.SCAUComputerClassOneEEE.OSEC.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.EAT;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.Device;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import com.SCAUComputerClassOneEEE.OSEC.pane.DiskPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author: Sky
 * @Date: 2020/10/14 21:30
 */

public class MySceneController implements Initializable {
    public static SimpleObjectProperty<Integer> diskChange = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> cpuTimeSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> timeSliceSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningPCBIDSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningIRSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> intermediateResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> finalResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> memoryChange = new SimpleObjectProperty<>();


    public static double width = 1300;
    public static double height = 830;

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
    private Button reset;

    @FXML
    private Tab fileSystem;

    @FXML
    private TableView<PCB> readyTable;
    @FXML
    private TableColumn<PCB,Integer> readyID;
    @FXML
    private TableColumn<PCB,Integer> readyArriveTime;
    @FXML
    private TableColumn<PCB,Integer> readyTotalTime;
    @FXML
    private TableColumn<PCB,Integer> readyAX;

    @FXML
    private TableColumn<PCB,String> readyEXFileName;
    @FXML
    private TableColumn<PCB,Integer> readyRemainInstructions;
    @FXML
    private TableColumn<PCB,Double> readyProgressRate;

    @FXML
    private TableView<PCB> blockTable;
    @FXML
    private TableColumn<PCB,Integer> blockID;
    @FXML
    private TableColumn<PCB,String> waitEq;
    @FXML
    private TableColumn<PCB,Integer> blockArriveTime;
    @FXML
    private TableColumn<PCB,Integer> blockTotalTime;
    @FXML
    private TableColumn<PCB,String> blockEXFileName;
    @FXML
    private TableColumn<PCB,Integer> blockAX;
    @FXML
    private TableColumn<PCB,Double> blockProgressRate;

    @FXML
    private Pane memoryPane;


    @FXML
    private BorderPane bp1;
    @FXML
    private BorderPane bp2;

    @FXML
    private TableView<EAT> equipmentTable;
    @FXML
    private TableColumn<EAT,Character> equipmentID;
    @FXML
    private TableColumn<EAT, Number> useEquipmentPCBID;
    @FXML
    private TableColumn<EAT, Number> time;

    private boolean isFirstStart = true;
    private Thread coreThread;

    @FXML
    private void reset(){
        //把cpu进程关掉
        coreThread.interrupt();

        runningIR.setText("");
        runningPCBID.setText("");
        cpuTime.setText("");
        timeSlice.setText("");
        intermediateResult.setText("");
        finalResult.setText("");

        isFirstStart = true;
        beginORStop.setText("开始");
        beginORStop.setGraphic(new ImageView(new Image("file:" +"src/main/resources/"+"开始"+".png",
                30, 30,
                true, true)));
        OS.cpu.reset();
        OS.clock.reset();
        OS.device.reset();
        //OS.memory.reset();
        updateMemoryPane();
    }

    @FXML
    public void beginORStop() {
        if (beginORStop.getText().equals("开始")){
            if (isFirstStart) {
                coreThread = new Thread(OS.cpu);
                coreThread.start();
                isFirstStart = false;
            }else {
                OS.cpu.WAITING = false;
                OS.cpu.notifyCpu();
            }
            beginORStop.setText("暂停");
            beginORStop.setGraphic(new ImageView(new Image("file:" +"src/main/resources/"+"暂停"+".png",
                    30, 30,
                    true, true)));
        }
        else {
            beginORStop.setText("开始");
            beginORStop.setGraphic(new ImageView(new Image("file:" +"src/main/resources/"+"开始"+".png",
                    30, 30,
                    true, true)));
            OS.cpu.WAITING = true;
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        beginORStop.setText("开始");

        initTime();
        initEquipmentTable();
        initReadyTable();
        initBlockTable();
        initFileSystem();

        memoryChange.setValue(0);
        memoryChange.addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(this::updateMemoryPane);
        }));
        diskChange.setValue(0);
        diskChange.addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(() -> updateDiskPane(newValue));
        }));
    }

    private void updateDiskPane(int index) {
        OS.diskPane.updateType(index);
    }

    private void initFileSystem(){

        FilePane centerPane = new FilePane();
        VBox leftPane = OS.fileTree.getFileTreePane();
        TextArea rightPane = OS.terminal.getTextArea();//初始化命令行

        bp1.setLeft(leftPane);
        bp1.setCenter(centerPane);
        bp1.setRight(rightPane);

        bp2.setLeft(OpenFileManager.openFileTableView);
        bp2.setCenter(OS.diskPane.getDiskBlockSet());

        width = bp1.getPrefWidth();
        height = bp1.getPrefHeight();

        //leftPane.setPrefSize(width/5,3*height/5);
        OS.fileTree.getTreeView().setPrefSize(width/5,3*height/5);
        //leftPane.setSize(width/5,3*height/5);
        centerPane.setPrefSize(2*width/5,3*height/5);
        rightPane.setPrefSize(2*width/5,3*height/5);
        OpenFileManager.openFileTableView.setPrefSize(4*width/7,2*height/5);
        OS.diskPane.getDiskBlockSet().setPrefSize(3*width/7,2*height/5);

    }

    private void initTime(){
        cpuTimeSim.addListener((observable, oldValue, newValue)-> setCPUTime(newValue.intValue()));

        timeSliceSim.addListener((observable, oldValue, newValue)-> setTimeSlice(newValue.intValue()));

        runningPCBIDSim.addListener((observable, oldValue, newValue)-> setRunningPCBID(newValue));

        runningIRSim.addListener((observable, oldValue, newValue)-> setRunningIR(newValue));

        intermediateResultSim.addListener((observable, oldValue, newValue)-> setIntermediateResult(newValue));

        finalResultSim.addListener((observable, oldValue, newValue)-> setFinalResult(newValue));
    }

    private void initEquipmentTable(){
        //给表添加数据源
        equipmentTable.setItems(Device.getRunningLists());
        //设置列属性
        equipmentID.setCellValueFactory(new PropertyValueFactory<>("deviceID"));
        useEquipmentPCBID.setCellValueFactory(new PropertyValueFactory<>("pcbID"));
        time.setCellValueFactory(new PropertyValueFactory<>("remainingTime"));
    }

    private void initReadyTable(){
        readyTable.setItems(CPU.readyQueue);
        readyID.setCellValueFactory(new PropertyValueFactory<>("processId"));
        readyArriveTime.setCellValueFactory(new PropertyValueFactory<>("arriveTime"));
        readyTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        readyAX.setCellValueFactory(new PropertyValueFactory<>("AX"));
        //readyNextIR.setCellValueFactory(new PropertyValueFactory<>("nextInstruction"));
        readyEXFileName.setCellValueFactory(new PropertyValueFactory<>("exFileName"));
        readyRemainInstructions.setCellValueFactory(new PropertyValueFactory<>("remainInstructions"));
        readyProgressRate.setCellValueFactory(new PropertyValueFactory<>("progressRate"));
        readyProgressRate.setCellFactory(ProgressBarTableCell.forTableColumn());

        readyID.setSortable(false);
        readyArriveTime.setSortable(false);
        readyTotalTime.setSortable(false);
        readyAX.setSortable(false);
        //readyNextIR.setSortable(false);
        readyEXFileName.setSortable(false);
        readyRemainInstructions.setSortable(false);
        readyProgressRate.setSortable(false);
    }

    private void initBlockTable(){
        blockProgressRate.setCellValueFactory(new PropertyValueFactory<>("progressRate"));
        blockProgressRate.setCellFactory(ProgressBarTableCell.forTableColumn());
        blockTable.setItems(CPU.blockedQueue);
        blockID.setCellValueFactory(new PropertyValueFactory<>("processId"));
        waitEq.setCellValueFactory(new PropertyValueFactory<>("waitingForDevice"));
        blockArriveTime.setCellValueFactory(new PropertyValueFactory<>("arriveTime"));
        blockTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        blockEXFileName.setCellValueFactory(new PropertyValueFactory<>("exFileName"));
        blockAX.setCellValueFactory(new PropertyValueFactory<>("AX"));


        blockID.setSortable(false);
        waitEq.setSortable(false);
        blockArriveTime.setSortable(false);
        blockTotalTime.setSortable(false);
        blockEXFileName.setSortable(false);
        blockAX.setSortable(false);
        blockProgressRate.setSortable(false);
    }


    private void updateMemoryPane(){
        memoryPane.getChildren().clear();
        for (int i = 0; i < CPU.allPCB.size() ; i++){
            PCB drawingPCB = CPU.allPCB.get(i);
            double width = ((double)drawingPCB.getTotalSize() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());
            double layoutX = ((double)drawingPCB.getPointerToMemory() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());

            Label label = new Label(String.valueOf(drawingPCB.getProcessId()));
            if (i==0){
                label.setText("OS");
            }
            else {
                width*=2;
                layoutX*=2;
                layoutX-=((double)CPU.allPCB.get(0).getTotalSize() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());
            }


            StackPane stackPane = new StackPane();
            stackPane.setPrefSize(width,memoryPane.getHeight());
            stackPane.setLayoutX(layoutX);

            Rectangle rectangle = new Rectangle(width, memoryPane.getHeight(), drawingPCB.getColor());

            stackPane.getChildren().add(rectangle);
            stackPane.getChildren().add(label);
            memoryPane.getChildren().add(stackPane);
        }
    }

    private void setCPUTime(int time){
        cpuTime.setText(String.valueOf(time));
    }

    private void setTimeSlice(int time){
        if (CPU.curPCB==null){
            timeSlice.setText("");
            return;
        }
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


}
