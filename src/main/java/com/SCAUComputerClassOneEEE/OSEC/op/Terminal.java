package com.SCAUComputerClassOneEEE.OSEC.op;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

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
    private Disk disk;
    private FileTree fileTree;
    private MyTreeItem rootTree;

    public static TextArea textArea = new TextArea();
    TextInputBox tib = new TextInputBox();  //实例化文件操作输入盒子对象
    //模式串
    public Pattern pattern1 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)");
    public Pattern pattern2 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)");


    public Terminal(Disk disk, FileTree fileTree) {
        this.disk = disk;
        this.fileTree = fileTree;
        this.rootTree = fileTree.getRootTree();


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
            List<String> fileNameList = getfileNameList(filePath);

            switch (action){
                case "create":
                    for (int i = 0; i < fileNameList.size(); i++){
                        if (i == 0 && fileNameList.size() == 1){
                            textArea.appendText(tib.createFile(disk, rootTree, fileNameList.get(i)));
                        }else if(i == 1){
                            ObservableList<TreeItem<AFile>> myTreeItems = rootTree.getChildren();
                            for (int j = 0; j < myTreeItems.size(); j++){
                                if(fileNameList.get(i - 1).equals(myTreeItems.get(j).getValue().getFileName())){
                                    textArea.appendText(tib.createFile(disk, myTreeItems.get(j), fileNameList.get(i)));
                                }
                            }
                        }
                    }
                    break;
                case "mkdir":
                    //注释的方法正常
//                    for (int i = 0; i < fileNameList.size(); i++){
//                        if (i == 0 && fileNameList.size() == 1){
//                            textArea.appendText(tib.createDirectory(disk, rootTree, fileNameList.get(i)));
//                        }else if(i == 1){
//                            ObservableList<TreeItem<AFile>> myTreeItems = rootTree.getChildren();
//                            for (int j = 0; j < myTreeItems.size(); j++){
//                                if(fileNameList.get(i - 1).equals(myTreeItems.get(j).getValue().getFileName())){
//                                    textArea.appendText(tib.createDirectory(disk, myTreeItems.get(j), fileNameList.get(i)));
//                                }
//                            }
//                        }
//                    }
                    textArea.appendText(tib.createDirectory(disk, getFatherTreeItem(fileNameList, rootTree, 0),
                            fileNameList.get(fileNameList.size() - 1)));

                    break;
//                case "delete":
//                    if (tib.delete(disk, rootTree)){
//                        textArea.appendText("文件删除成功！");
//                    } else {
//                        textArea.appendText("错误！文件不存在");
//                    }break;
//                case "rmdir":
//                    if(tib.delete(disk, Null, filePath)){
//                        textArea.appendText("目录删除成功！");
//                    }else{
//                        textArea.appendText("错误！该目录包含子目录");
//                    }
//                    break;
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

    /**
     *
     * @param filePath
     * @return 文件名数组，每个元素都为上一个元素所属TreeItem的子的文件名
     */

    List<String> getfileNameList(String filePath){
        //将字符串以 / 拆分成几个字符串，其中fileNames[0]是第一个 / 前面的字符，如果第一个字符是 / ，则fileNames[0]为空(可打印
        String[] fileNames = filePath.split("/");
        //将数组第一个空内容去掉,并转化成List类型
        List<String> fileNameList = new ArrayList<String>(Arrays.asList(fileNames));
        fileNameList.remove(0);
//解开注释证明从下标 0 开始
//            for(int i = 0; i < fileNameList.size(); i++){
//                System.out.println(fileNameList.get(i));
//
//            }
        return fileNameList;

    }

    /**
     * 递归找到fileNameList对应的父节点
     * @param fileNameList
     * @param fatherTreeItem
     * @return
     */
    TreeItem<AFile> getFatherTreeItem(List<String> fileNameList, TreeItem<AFile> fatherTreeItem, int i){
        textArea.appendText(fatherTreeItem.toString());
        // i从0开始,务必让 i 初值为0
        if(fileNameList.size() == 1){
            return rootTree;
        }else{
//            if(i == (fileNameList.size() - 1)){
//                return fatherTreeItem;
//            }
            ObservableList<TreeItem<AFile>> myTreeItems = fatherTreeItem.getChildren();
            if(myTreeItems.isEmpty()) { return fatherTreeItem; }
            for (int j = 0; j < myTreeItems.size(); j++){
                if(fileNameList.get(i).equals(myTreeItems.get(j).getValue().getFileName())){
                    i++;
                    getFatherTreeItem(fileNameList, myTreeItems.get(j), i);
                }
                //没找到,循环结束后 j = myTreeItems.size()  退出条件
//                if(j == (myTreeItems.size() - 1)){
//                    textArea.appendText("father not found");
//                    return fatherTreeItem;
//                }
            }
        }
        return null;
    }

}