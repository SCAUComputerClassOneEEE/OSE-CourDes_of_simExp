package com.SCAUComputerClassOneEEE.OSEC.op;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.*;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hlf
 * @date 28/7/2020
 */
public class Terminal {
    public Pattern pattern1 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)");
    public Pattern pattern2 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)");

    public static TextArea textArea = new TextArea();
    TextInputBox tib = new TextInputBox();
    private Disk disk;


    public Terminal(Disk disk) {
        this.disk = disk;

        String command = "";

        textArea.appendText(">> ");
        final int[] currentCaretPos = {textArea.getCaretPosition()};
        textArea.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.BACK_SPACE && currentCaretPos[0] == textArea.getCaretPosition()) {
                textArea.setEditable(false);
            } else {
                textArea.setEditable(true);
            }
        });
        textArea.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.ENTER) {
                    String command = textArea.getText(currentCaretPos[0], textArea.getCaretPosition() - 1);
                    currentCaretPos[0] = textArea.getCaretPosition() + 3;
                    textArea.appendText(">> ");
                    textArea.positionCaret(textArea.getCaretPosition());
                    formatStr(command);
                }
            }
        });

    }


    public void formatStr(String command) {
        String arr = command.replaceAll("\\s+", " ");
        //匹配模式
        Matcher matcher1 = pattern1.matcher(arr);
        Matcher matcher2 = pattern2.matcher(arr);

        if (matcher1.matches()) {
            System.out.println(matcher1.group(0));
            String action = matcher1.group(1);
            String filePath = matcher1.group(2);

            switch (action){
                case "create":
                    if(tib.createFile(disk, Null, filePath)){
                        textArea.appendText("文件创建成功！");
                    } else {
                        textArea.appendText("错误！文件已经存在");
                    }break;
                case "delete":
                    if (tib.delete(disk, Null, filePath)){
                        textArea.appendText("文件删除成功！");
                    } else {
                        textArea.appendText("错误！文件不存在");
                    }
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
            String srcFilepath = matcher2.group(2);
            String desFilePath = matcher2.group(3);
//            if(fileOperations.copy(srcFilepath,desFilePath)){
//                textArea.appendText("复制成功！");
//            }else{
//                textArea.appendText("错误！");
//            }
//        }

        }
    }
}