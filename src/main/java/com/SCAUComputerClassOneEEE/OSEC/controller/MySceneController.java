package com.SCAUComputerClassOneEEE.OSEC.controller;


import com.SCAUComputerClassOneEEE.OSEC.Main;
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
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
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
/*    @FXML
    private TableColumn<PCB,Integer> readyNextIR;*/
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


    @FXML
    public void beginORStop(){
        if (beginORStop.getText().equals("开始运行")){
            TaskThreadPools.execute(Main.cpu);
            beginORStop.setText("暂停");
        }
        else {
            beginORStop.setText("开始运行");
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        beginORStop.setText("开始运行");

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
        DiskPane.BlockPane[] blockPane = Main.diskPane.getBlockPanes();
        Main.diskPane.updateType(index);
    }

    private void initFileSystem(){

        FilePane centerPane = new FilePane();
        VBox leftPane = Main.fileTree.getVBox();
        Terminal rightPane = new Terminal(Main.fileTree);//初始化命令行

        bp1.setLeft(leftPane);
        bp1.setCenter(centerPane);
        bp1.setRight(rightPane.textArea);

        bp2.setLeft(OpenFileManager.openFileTableView);
        bp2.setCenter(Main.diskPane.getRoot());

        width = bp1.getPrefWidth();
        height = bp1.getPrefHeight();
        System.out.println(width);
        System.out.println(height);

        //leftPane.setPrefSize(width/5,3*height/5);
        Main.fileTree.getTreeView().setPrefSize(width/5,3*height/5);
        //leftPane.setSize(width/5,3*height/5);
        centerPane.setPrefSize(2*width/5,3*height/5);
        rightPane.textArea.setPrefSize(2*width/5,3*height/5);
        OpenFileManager.openFileTableView.setPrefSize(4*width/7,2*height/5);
        Main.diskPane.getRoot().setPrefSize(3*width/7,2*height/5);

    }

    private void initTime(){
        cpuTimeSim.addListener((observable, oldValue, newValue)-> setCPUTime(newValue.longValue()));

        timeSliceSim.addListener((observable, oldValue, newValue)-> setTimeSlice(newValue.intValue()));

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
        waitEq.setCellValueFactory(new PropertyValueFactory<>("waitEq"));
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

    private void setCPUTime(long time){
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
