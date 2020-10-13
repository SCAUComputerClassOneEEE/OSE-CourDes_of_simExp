package com.SCAUComputerClassOneEEE.OSEC.utils;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

/**
 * @Author: Sky
 * @Date: 2020/10/11 20:41
 */
@Getter
@Setter
public class MainUI {
    public static MainUI mainUI = new MainUI();
    private AnchorPane root = new AnchorPane();
    private Label systemTime = new Label("系统时间");
    private Label runningPCBID = new Label("正在运行的进程ID");
    private Label silenceOfTime = new Label("时间片");
    private Terminal userInterface = new Terminal(Main.fileTree);//用户接口
    private Label readyQueueID = new Label("就绪队列进程ID");
    private Label blockQueueIDAndTime = new Label("阻塞进程ID阻塞时间");
    private Label intermediateResults = new Label("进程执行中间结果");
    private Label runningInstruction = new Label("正在执行的指令");
    private Label finalResult = new Label("程序运行最终结果");
    private Label memory = new Label("内存使用情况");
    private Label equipment = new Label("设备使用情况");
    private VBox diskFolder = Main.fileTree.getVBox();//磁盘目录结构
    private Label diskUse = new Label("磁盘使用情况");
    private Stage stage = new Stage();
    private JFrame frame=new JFrame();  // 创建设备主面板
    public MainUI(){
        initLayout();
        root.setPrefSize(1000,700);
        //root.getChildren().addAll(systemTime,runningPCBID,silenceOfTime,userInterface,readyQueueID,blockQueueIDAndTime,intermediateResults,runningInstruction,finalResult,memory,equipment,diskFolder,diskUse);
        root.getChildren().add(systemTime);
        root.getChildren().add(runningPCBID);
        root.getChildren().add(silenceOfTime);
        root.getChildren().add(Terminal.textArea);
        root.getChildren().add(runningInstruction);
        root.getChildren().add(intermediateResults);
        root.getChildren().add(finalResult);
        root.getChildren().add(diskFolder);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void initLayout(){
        systemTime.setLayoutX(20);
        systemTime.setLayoutY(20);

        runningPCBID.setLayoutX(150);
        runningPCBID.setLayoutY(20);

        silenceOfTime.setLayoutX(350);
        silenceOfTime.setLayoutY(20);

        Terminal.textArea.setLayoutX(450);
        Terminal.textArea.setLayoutY(20);

        runningInstruction.setLayoutX(50);
        runningInstruction.setLayoutY(300);

        intermediateResults.setLayoutX(300);
        intermediateResults.setLayoutY(200);

        finalResult.setLayoutX(300);
        finalResult.setLayoutY(300);

        diskFolder.setLayoutX(650);
        diskFolder.setLayoutY(250);
    }

    public void show(){
        stage.show();
    }

}
