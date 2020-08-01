package com.SCAUComputerClassOneEEE.OSEC.op;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileTree;

/**
 * @author hlf
 * @date 28/7/2020
 */
public class Terminal {
    //格式串
    public Pattern pattern1 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)");
    public Pattern pattern2 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)");
    //输入区
    public static TextArea textArea = new TextArea();


    public void start(Stage primaryStage) throws Exception {
        String command = "";

        textArea.appendText(">> ");
        final int[] currentCaretPos = {textArea.getCaretPosition()};
        //设置到不可编辑与可编辑区域
        textArea.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.BACK_SPACE && currentCaretPos[0] == textArea.getCaretPosition()) {
                //当按下删除并且到此时距离等于最小距离时，不可编辑
                textArea.setEditable(false);
            } else {
                textArea.setEditable(true);
            }
        });
        //当按键松开后触发
        textArea.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //当按下回车时
                if (event.getCode() == KeyCode.ENTER) {
                    //获取执行的语句
                    String command = textArea.getText(currentCaretPos[0], textArea.getCaretPosition() - 1);
                    //光标的位置，用于之前是否可编辑操作
                    currentCaretPos[0] = textArea.getCaretPosition() + 3;
                    textArea.appendText(">> ");
                    textArea.positionCaret(textArea.getCaretPosition());
                    formatStr(command);
                }
            }
        });
        Scene scene = new Scene(textArea, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void formatStr(String command) {
        //去掉多余空格
        String arr = command.replaceAll("\\s+", " ");
        //匹配模式
        Matcher matcher1 = pattern1.matcher(arr);
        Matcher matcher2 = pattern2.matcher(arr);

        if (matcher1.matches()) {
            System.out.println(matcher1.group(0));
            String action = matcher1.group(1); //获取动作
            String filePath = matcher1.group(2);//获取路径

            switch(action){
                case "create":

            }
            //下面是文件方法调用

//            switch (action) {
//                case "create":
//                    if (fileOperations.create(filePath)) {
//                        textArea.appendText("文件创建成功！");
//                    } else {
//                        textArea.appendText("错误！文件已经存在");
//                    }break;
//                case "delete":
//                    if(fileOperations.delete(filePath)){
//                        textArea.appendText("文件删除成功！");
//                    }else{
//                        textArea.appendText("错误！文件不存在");
//                    }
//                    break;
//                case "type":
//                    if(!fileOperations.type(filePath,textArea)){
//                        textArea.appendText("错误！文件不存在");
//                    }
//                    break;
//                case "mkdir":
//                    if(fileOperations.mkdir(filePath)){
//                        textArea.appendText("目录创建成功！");
//                    }else{
//                        textArea.appendText("错误！目录已存在");
//                    }
//                    break;
//                case "rmdir":
//                    if(fileOperations.rmdir(filePath)){
//                        textArea.appendText("目录删除成功！");
//                    }else{
//                        textArea.appendText("错误！该目录包含子目录");
//                    }
//                    break;
//            }
//
        } else if (matcher2.matches()) {
            String srcFilepath = matcher2.group(2); //源文件路径
            String desFilePath = matcher2.group(3);//目标文件路径
//            if(fileOperations.copy(srcFilepath,desFilePath)){
//                textArea.appendText("复制成功！");
//            }else{
//                textArea.appendText("错误！");
//            }
//        }

        }
    }
}