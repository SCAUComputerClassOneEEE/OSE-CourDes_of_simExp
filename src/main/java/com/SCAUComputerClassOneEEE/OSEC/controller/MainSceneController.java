package com.SCAUComputerClassOneEEE.OSEC.controller;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.AOpenFile;
import com.SCAUComputerClassOneEEE.OSEC.utils.OS;
import com.SCAUComputerClassOneEEE.OSEC.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.EAT;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.Device;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author: Sky
 * @Date: 2020/10/14 21:30
 */
public class MainSceneController implements Initializable {
    //初始化
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        界面的初始化
         */
        // 初始化表格中的信息列
        initMassage();
        // 初始化设备表格
        initDeviceTable();
        // 初始化就绪表格
        initReadyTable();
        // 初始化阻塞表格
        initBlockTable();
        // 初始化内存界面
        initMemoryPane();
        // 初始化文件系统
        initFileSystem();
    }

    /**
     * 用于按钮绑定的事件
     */
    // 开始或暂停
    @FXML
    public void startORStop() {
        // 准备开始
        if ("开始".equals(startORStop.getText())){
            // 首次开始
            if (isFirstStart) {
                coreThread = new Thread(OS.cpu);
                coreThread.start();
                isFirstStart = false;
            }else { // 暂停后启动
                OS.cpu.WAITING = false;
                OS.cpu.notifyCpu();
            }
            startORStop.setText("暂停");
            startORStop.setGraphic(new ImageView(new Image("file:" +"src/main/resources/暂停.png", 30, 30, true, true)));
        }
        else { // 准备暂停
            OS.cpu.WAITING = true;
            startORStop.setText("开始");
            startORStop.setGraphic(new ImageView(new Image("file:" +"src/main/resources/开始.png", 30, 30, true, true)));
        }
    }

    // 重置
    @FXML
    private void reset(){
        if (coreThread == null) return;
        // 把cpu进程关掉
        coreThread.stop();
        coreThread = null;
        // 重置
        OS.cpu.reset();
        OS.clock.reset();
        OS.device.reset();
        OS.processSimService.reset();
        PCB.nextProcessID = 0;
        OS.memory.reset();

        startORStop.setText("开始");
        startORStop.setGraphic(new ImageView(new Image("file:" +"src/main/resources/开始.png", 30, 30, true, true)));

        cpuTime.setText("");
        timeSlice.setText("");
        runningPCBID.setText("");
        runningIR.setText("");
        intermediateResult.setText("");
        finalResult.setText("");

        isFirstStart = true;
    }

    private void initMassage(){
        //加监听器
        cpuTimeSim.addListener((observable, oldValue, newValue)-> setCPUTime(newValue));
        timeSliceSim.addListener((observable, oldValue, newValue)-> setTimeSlice(newValue));
        runningPCBIDSim.addListener((observable, oldValue, newValue)-> setRunningPCBID(newValue));
        runningIRSim.addListener((observable, oldValue, newValue)-> setRunningIR(newValue));
        intermediateResultSim.addListener((observable, oldValue, newValue)-> setIntermediateResult(newValue));
        finalResultSim.addListener((observable, oldValue, newValue)-> setFinalResult(newValue));
    }
    private void initDeviceTable(){
        //给表添加数据源
        equipmentTable.setItems(Device.getRunningLists());
        //设置列属性
        equipmentID.setCellValueFactory(new PropertyValueFactory<>("deviceID"));
        useEquipmentPCBID.setCellValueFactory(new PropertyValueFactory<>("pcbID"));
        remainingTime.setCellValueFactory(new PropertyValueFactory<>("remainingTime"));
    }
    private void initReadyTable(){
        readyTable.setItems(CPU.readyQueue);
        readyID.setCellValueFactory(new PropertyValueFactory<>("processId"));
        readyEXFileName.setCellValueFactory(new PropertyValueFactory<>("exFileName"));
        readyArriveTime.setCellValueFactory(new PropertyValueFactory<>("arriveTime"));
        readyTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        readyAX.setCellValueFactory(new PropertyValueFactory<>("AX"));
        readyRemainInstructions.setCellValueFactory(new PropertyValueFactory<>("remainInstructions"));
        readyProgressRate.setCellValueFactory(new PropertyValueFactory<>("progressRate"));
        readyProgressRate.setCellFactory(ProgressBarTableCell.forTableColumn());
    }
    private void initBlockTable(){
        blockProgressRate.setCellValueFactory(new PropertyValueFactory<>("progressRate"));
        blockProgressRate.setCellFactory(ProgressBarTableCell.forTableColumn());
        blockTable.setItems(CPU.blockedQueue);
        blockID.setCellValueFactory(new PropertyValueFactory<>("processId"));
        waitingForDevice.setCellValueFactory(new PropertyValueFactory<>("waitingForDevice"));
        blockArriveTime.setCellValueFactory(new PropertyValueFactory<>("arriveTime"));
        blockTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        blockEXFileName.setCellValueFactory(new PropertyValueFactory<>("exFileName"));
        blockAX.setCellValueFactory(new PropertyValueFactory<>("AX"));
    }
    private void initMemoryPane() {
        memoryChange.setValue(0);
        memoryChange.addListener(((observable, oldValue, newValue) -> {
            Platform.runLater(this::updateMemoryPane);
        }));
    }
    private void initFileSystem(){
        // 组件
        borderPane1OfFileSystemTab.setLeft(leftPane);
        borderPane1OfFileSystemTab.setCenter(centerPane);
        borderPane1OfFileSystemTab.setRight(rightPane);
        borderPaneOfBorderPane1.setLeft(leftOfBottom);
        borderPaneOfBorderPane1.setCenter(rightOfBottom);

        // 大小尺寸
        OS.width = borderPane1OfFileSystemTab.getPrefWidth();
        OS.height = borderPane1OfFileSystemTab.getPrefHeight();
        OS.fileTree.getTreeView().setPrefSize(OS.width/5,3*OS.height/5);
        OS.diskPane.getDiskBlockSet().setPrefSize(3*OS.width/7,2*OS.height/5);
        centerPane.setPrefSize(2*OS.width/5,3*OS.height/5);
        rightPane.setPrefSize(2*OS.width/5,3*OS.height/5);
        OpenFileManager.openFileTableView.setPrefSize(4*OS.width/7,2*OS.height/5);

        //监听器
        diskChange.setValue(0);
        diskChange.addListener(((observable, oldValue, newValue) -> Platform.runLater(() -> updateDiskPane(newValue))));
    }

    // 监听器调用的方法
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
    private void updateMemoryPane(){
        // 清空
        memoryPane.getChildren().clear();
        for (int i = 0; i < CPU.allPCB.size() ; i++){
            PCB drawingPCB = CPU.allPCB.get(i);
            // 宽
            double width = 2 * ((double)drawingPCB.getTotalSize() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());
            // 位置
            double layoutX = 2 * ((double)drawingPCB.getPointerToMemory() / Memory.getMemory().getUserMemoryArea().length * memoryPane.getWidth());

            // 一个进程在内存界面中的表示
            Rectangle shapeOfProcessBlock = new Rectangle(width, memoryPane.getHeight(), drawingPCB.getColor());
            Label label = new Label(String.valueOf(drawingPCB.getProcessId()));
            if (i == 0) label.setText("OS");
            StackPane aProcessBlock = new StackPane();
            aProcessBlock.setPrefSize(width,memoryPane.getHeight());
            aProcessBlock.setLayoutX(layoutX);
            aProcessBlock.getChildren().addAll(shapeOfProcessBlock,label);

            memoryPane.getChildren().add(aProcessBlock);
        }
    }
    private void updateDiskPane(int index) {
        OS.diskPane.updateType(index);
    }

    /**
     * 以下为进程执行界面的控件
     */
    // 按钮
    @FXML
    private Button startORStop;
    @FXML
    private Button reset;

    // 系统运行中的一些信息
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

    // 设备分配表
    @FXML
    private TableView<EAT> equipmentTable;// 表格
    @FXML
    private TableColumn<EAT,Character> equipmentID;// 设备名称
    @FXML
    private TableColumn<EAT, Number> useEquipmentPCBID;// 使用该设备的进程id
    @FXML
    private TableColumn<EAT, Number> remainingTime;// 剩余使用时间

    // 就绪进程表格
    @FXML
    private TableView<PCB> readyTable;// 表格
    @FXML
    private TableColumn<PCB,Integer> readyID;// 进程id
    @FXML
    private TableColumn<PCB,String> readyEXFileName;// 生成该进程的可执行文件名
    @FXML
    private TableColumn<PCB,Integer> readyArriveTime;// 进程到达时间
    @FXML
    private TableColumn<PCB,Integer> readyTotalTime;// 进程存在总时间
    @FXML
    private TableColumn<PCB,Integer> readyAX;// 进程中X的值
    @FXML
    private TableColumn<PCB,Integer> readyRemainInstructions;// 进程的剩余指令数
    @FXML
    private TableColumn<PCB,Double> readyProgressRate;// 进程的完成度——进度条

    // 阻塞队列表格
    @FXML
    private TableView<PCB> blockTable;// 表格
    @FXML
    private TableColumn<PCB,Integer> blockID;// id
    @FXML
    private TableColumn<PCB,String> blockEXFileName;// 文件名
    @FXML
    private TableColumn<PCB,String> waitingForDevice;// 阻塞原因
    @FXML
    private TableColumn<PCB,Integer> blockArriveTime;// 到达时间
    @FXML
    private TableColumn<PCB,Integer> blockTotalTime;// 存在总时间
    @FXML
    private TableColumn<PCB,Integer> blockAX;// X
    @FXML
    private TableColumn<PCB,Double> blockProgressRate;// 进度

    //内存界面
    @FXML
    private Pane memoryPane;

    /**
     * 以下为文件系统界面的两个主面板
     */
    @FXML
    private BorderPane borderPane1OfFileSystemTab;// 主体框架 左中右下
    @FXML
    private BorderPane borderPaneOfBorderPane1;// 嵌套在主体框架中的底部位置


    /**
     * 成员变量
     */
    // 记录是否首次开启cpu线程
    private boolean isFirstStart = true;
    // 存放cpu线程
    private Thread coreThread = null;

    // 文件系统的组件变量
    private final FilePane centerPane = new FilePane();
    private final VBox leftPane = OS.fileTree.getFileTreePane();
    private final TextArea rightPane = OS.terminal.getTextArea();
    private final TableView<AOpenFile> leftOfBottom = OpenFileManager.openFileTableView;
    private final GridPane rightOfBottom = OS.diskPane.getDiskBlockSet();

    //维护数据属性，监听器装在的对象
    public static SimpleObjectProperty<Integer> diskChange = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> cpuTimeSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> timeSliceSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningPCBIDSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> runningIRSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> intermediateResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<String> finalResultSim = new SimpleObjectProperty<>();
    public static SimpleObjectProperty<Integer> memoryChange = new SimpleObjectProperty<>();
}
