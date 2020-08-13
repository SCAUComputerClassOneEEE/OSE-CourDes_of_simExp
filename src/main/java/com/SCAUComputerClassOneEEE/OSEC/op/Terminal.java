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
    private FileTree fileTree;
    private MyTreeItem rootTree;

    public static TextArea textArea = new TextArea();

    //数据服务层
    private DiskSimService diskSimService = new DiskSimService();

    //模式串
    public Pattern pattern1 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)");
    public Pattern pattern2 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)");


    public Terminal(FileTree fileTree) {
        this.fileTree = fileTree;
        this.rootTree = fileTree.getRootTree();


        String command = "";

        textArea.appendText("可使用指令: create mkdir delete open");
        textArea.appendText("(请按回车开始) ");
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
            List<String> fileNameList = getFileNameList(filePath);

            switch (action){
                case "create":
                    textArea.appendText(diskSimService.createFile(getFatherTreeItem(fileNameList, rootTree, 0),
                            fileNameList.get(fileNameList.size() - 1)));
                    break;
                case "mkdir":
                    textArea.appendText(diskSimService.createDirectory(getFatherTreeItem(fileNameList, rootTree, 0),
                            fileNameList.get(fileNameList.size() - 1)));
                    break;
                case "delete":
                    textArea.appendText(diskSimService.delete(getLastTreeItem(filePath))?"删除成功":"删除失败或文件不存在");
                    break;
                case "open":
                    textArea.appendText(diskSimService.open(getLastTreeItem(filePath))?"打开成功":"已打开文件数达最大或文件已打开");
                    break;
            }

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

    List<String> getFileNameList(String filePath){
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
//        textArea.appendText(fatherTreeItem.toString());
        // i从0开始,务必让 i 初值为0
        //假如只有长度1，则直接返回根目录作为子目录
        if(fileNameList.size() == 1){
            return rootTree;
        }else{

            //长度不为1，是多级目录
            ObservableList<TreeItem<AFile>> myTreeItems = fatherTreeItem.getChildren();

//            textArea.appendText("的myTreeItems大小:" + myTreeItems.size());
            //如果函数传入的父节点没有孩子，传入的父节点即为所需父节点，片面理解，防止不了用户错误操作
            // mkdir /a/b(有父目录） 和 mkdir /a/b(无父目录）判断条件重复,后续再修复,判断核心是有无a
            if(myTreeItems.size()  == 0){
                return fatherTreeItem;
            }
            //如果传入父节点有孩子，就用for匹配是否有相同名字，有就继续递归，没有 传入的父节点即为所需父节点
            for (int j = 0; j < myTreeItems.size(); j++){
                if(fileNameList.get(i).equals(myTreeItems.get(j).getValue().getFileName())){
                    i++;
                    fatherTreeItem = getFatherTreeItem(fileNameList, myTreeItems.get(j), i);
                }
     //           没找到,循环结束后 j = myTreeItems.size()  退出条件
                if(j == (myTreeItems.size() - 1)){
//                    textArea.appendText("father not found");
                    return fatherTreeItem;
                }
            }
        }
        return null;
    }

    TreeItem<AFile> getLastTreeItem(String name){
        int 起始 = 0;
        int num = 2;
        int second = getCharacterPosition(name, num++);
        System.out.println("second:"+second);
        boolean flag = true;
        String str = null;
        ObservableList<TreeItem<AFile>> treeItems = rootTree.getChildren();
        while (second != -1){
            int i = 0;
            str = name.substring(起始 + 1,second);
            System.out.println("截取的:"+str);
            for (i = 0; i < treeItems.size(); i++) {
                if(str.equals(treeItems.get(i).getValue().getFileName()))
                    break;
            }
            System.out.println("i:"+i);
            if(i < treeItems.size()){
                起始 = second;
                second = getCharacterPosition(name,num++);
                treeItems = treeItems.get(i).getChildren();
            }else {
                flag = false;
                break;
            }
        }
        if(flag){
            str = name.substring(起始+1);
            System.out.println("截取的:"+str);
            for (int i = 0; i < treeItems.size(); i++)
                if(str.equals(treeItems.get(i).getValue().getFileName()))
                    return  treeItems.get(i);
        }
        return  null;
    }

    int getCharacterPosition(String string,int num){
        Matcher slashMatcher = Pattern.compile("/").matcher(string);
        int mIdx = 0;
        while(slashMatcher.find()) {
            mIdx++;
            if(mIdx == num){
                break;
            }
        }
        if(mIdx < num) return  -1;
        return slashMatcher.start();
    }

}