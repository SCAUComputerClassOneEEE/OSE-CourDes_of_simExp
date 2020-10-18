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
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    public static SimpleObjectProperty<String> runningPCBIDSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningIRSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> intermediateResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> finalResultSim = new SimpleObjectProperty<>();

    private static final Color[] colors = new Color[]{Color.DEEPSKYBLUE, Color.ALICEBLUE, Color.YELLOW, Color.TOMATO, Color.SILVER, Color.TURQUOISE, Color.TAN,
            Color.CORAL, Color.SKYBLUE, Color.SNOW};

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
        FileTree fileTree = Main.fileTree;
        BorderPane root = new BorderPane();
        Terminal terminal = new Terminal(fileTree);
        root.setRight(new FilePane());
        root.setLeft(fileTree.getVBox());
        fileSystem.setContent(root);
        root.setCenter(Terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);

        beginORStop.setText("开始");

        initTime();
        initEquipmentTable();
        initReadyTable();
        initBlockTable();
        initMemoryPane();


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
    }

    private void initMemoryPane(){

        Rectangle rect = new Rectangle(50, 147);
        memoryPane.getChildren().add(rect);



//        System.out.println("Pane高度：" + 147 + ",Pane宽度" + 727);



    }
    public  void updateMemoryPane(){
        //        memory.getMat().getMAT_OccupyCont().get(0).getLength();
        memoryPane.getChildren().clear();
        Rectangle rect = new Rectangle(50, 147);
        memoryPane.getChildren().add(rect);

        Memory memory = Memory.getMemory();
        int length = memory.getMat().getMAT_OccupyCont().size();
        for (int i = 0; i < length; i++){
            double width = memory.getMat().getMAT_OccupyCont().get(i).getLength() / 512 * 727;
            double x = memory.getMat().getMAT_OccupyCont().get(i).getPointer() / 512 * 727;
            Rectangle rectangle = new Rectangle(width, 147, colors[i]);
            memoryPane.getChildren().add(rectangle);
        }
    }


}
