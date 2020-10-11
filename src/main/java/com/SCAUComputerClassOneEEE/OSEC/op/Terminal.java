package com.SCAUComputerClassOneEEE.OSEC.op;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.*;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hlf
 * @date 28/7/2020
 */
public class Terminal {
    private MyTreeItem rootTree;
    private static  int[] currentCaretPos;

    public static TextArea textArea = new TextArea();

    //数据服务层
    private DiskSimService diskSimService = new DiskSimService();

    //模式串
    public Pattern pattern1 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)");
    public Pattern pattern2 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)");
    public Pattern pattern3 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)\\s([/,\\w]+)");


    public Terminal(FileTree fileTree) {
        this.rootTree = fileTree.getRootTree();
        init();
    }


    //匹配串并执行对应指令
    public void formatStr(String command) {
        String arr = command.replaceAll("\\s+", " ");
        //匹配模式
        Matcher matcher1 = pattern1.matcher(arr);//一个参数
        Matcher matcher2 = pattern2.matcher(arr);//两个参数
        Matcher matcher3 = pattern3.matcher(arr);//三个参数

        if (matcher1.matches()) {//一个参数
            //输出指令
            System.out.println(matcher1.group(0));
            //指令动作
            String action = matcher1.group(1);
            //文件路径
            String filePath = matcher1.group(2);

            List<String> fileNameList = diskSimService.getFileNameList(filePath);
            String info;
            switch (action){
                case "close"://ok
                    info = diskSimService.close_file(diskSimService.getLastTreeItem(filePath))?"关闭成功":"关闭失败";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "delete"://ok
                    info = diskSimService.deleteFile(diskSimService.getLastTreeItem(filePath))?"删除成功":"删除失败";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "typeFile":
                    info = diskSimService.typeFile(diskSimService.getLastTreeItem(filePath))?"显示成功":"显示失败";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;

                    //下面的是目录功能
                case "md":
                    info = diskSimService.createFile(diskSimService.getFatherTreeItem(fileNameList, rootTree, 0),
                            fileNameList.get(fileNameList.size() - 1), 8);
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "dir":
                    info = diskSimService.dirDirectory(diskSimService.getLastTreeItem(filePath))?"true":"false";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "rd":
                    info = diskSimService.rdDirectory(diskSimService.getLastTreeItem(filePath))?"true":"false";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
            }

        } else if (matcher2.matches()) {//两个参数
            System.out.println(matcher2.group(0));
            String action = matcher2.group(1);
            String filePath = matcher2.group(2);
            //操作值
            String value = matcher2.group(3);

            List<String> fileNameList = diskSimService.getFileNameList(filePath);
            String info;
            switch (action){
                case "create"://ok
                    info = diskSimService.createFile(diskSimService.getFatherTreeItem(fileNameList, rootTree, 0),
                            fileNameList.get(fileNameList.size() - 1), Integer.parseInt(value));
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "open"://ok
                    info = diskSimService.open_file(diskSimService.getLastTreeItem(filePath),value)?"打开成功":"打开失败";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "read"://ok
                    info = diskSimService.read_file(diskSimService.getLastTreeItem(filePath),Integer.parseInt(value));
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "change"://ok
                    info = diskSimService.change(diskSimService.getLastTreeItem(filePath),Integer.parseInt(value))?"属性修改成功":"属性修改失败";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
                    break;
                case "inputBuffer":
                    info = diskSimService.inputBuffer(Integer.parseInt(filePath),value)?"写缓冲成功":"写缓冲失败";
                    textArea.appendText(info);
                    textArea.appendText("\n>> ");
                    currentCaretPos[0]+=info.length()+4;
            }

        }else if (matcher3.matches()){//三个参数
            System.out.println(matcher3.group(0));
            String action = matcher3.group(1);
            String path = matcher3.group(2);
            int bufferNum = Integer.parseInt(matcher3.group(3));
            int length = Integer.parseInt(matcher3.group(4));

            String info = diskSimService.write_file(diskSimService.getLastTreeItem(path), bufferNum, length)?"成功":"失败";
            textArea.appendText(info);
            textArea.appendText("\n>> ");
            currentCaretPos[0]+=info.length()+4;
        }
    }

    private void init(){

        textArea.appendText("指令表:\n");
        textArea.appendText("创建文件 create /path fileType\n");//2参
        textArea.appendText("打开文件 open /path openType(read/write)\n");//2参
        textArea.appendText("读文件  read /path readLength\n");//2参
        textArea.appendText("写文件  write /path bufferNum writLength\n");//3参
        textArea.appendText("关闭文件 close /path\n");//1参
        textArea.appendText("删除文件 delete /path\n");//1参
        textArea.appendText("显示文件内容 typeFile /path\n");//1参
        textArea.appendText("改变文件属性 change /path newValue\n");//2参
        textArea.appendText("建立目录 md /path\n");//1参
        textArea.appendText("显示目录内容 dir /path\n");//1参
        textArea.appendText("删除空目录 rd /path\n");//1参
        textArea.appendText("往缓冲区写入数据 inputBuffer bufferNum str\n");
        textArea.appendText("(请按回车开始) ");
        currentCaretPos = new int[]{textArea.getCaretPosition()};
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
                    //System.out.println(currentCaretPos[0]+":"+textArea.getCaretPosition());
                }
            }
        });

    }

}