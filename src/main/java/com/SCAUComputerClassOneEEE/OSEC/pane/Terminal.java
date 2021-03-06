package com.SCAUComputerClassOneEEE.OSEC.pane;

import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Getter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hlf
 * @date 28/7/2020
 */
public class Terminal {
    private static Terminal terminal = new Terminal();
    private static  int[] currentCaretPos;

    @Getter
    public TextArea textArea = new TextArea();

    //模式串
    public Pattern pattern0 = Pattern.compile("([a-zA-Z]+)");
    public Pattern pattern1 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w,.]+)");
    public Pattern pattern2 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)");
    public Pattern pattern3 = Pattern.compile("([a-zA-Z]+)\\s([/,\\w]+)\\s([/,\\w]+)\\s([/,\\w]+)");


    public static Terminal getTerminal() {
        return terminal;
    }

    public Terminal() {
        textArea.setStyle("-fx-background-color: transparent;");
        init();
    }


    //匹配串并执行对应指令
    public void formatStr(String command) {
        String arr = command.replaceAll("\\s+", " ");
        //匹配模式
        Matcher matcher0 = pattern0.matcher(arr);//无参数
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

            List<String> fileNameList = OSDataCenter.diskSimService.getFileNameList(filePath);
            String info;
            switch (action){
                case "create"://新，ok
                    info = OSDataCenter.diskSimService.createFile(filePath);
                    formatOut(info);
                    break;
                case "close"://ok
                    info = OSDataCenter.diskSimService.close_file(OSDataCenter.diskSimService.getLastTreeItem(filePath))?"关闭成功":"关闭失败";
                    formatOut(info);
                    break;
                case "delete"://ok
                    info = OSDataCenter.diskSimService.deleteFile(OSDataCenter.diskSimService.getLastTreeItem(filePath))?"删除成功":"删除失败";
                    formatOut(info);
                    break;
                case "type":
                    info = OSDataCenter.diskSimService.typeFile(OSDataCenter.diskSimService.getLastTreeItem(filePath))?"显示成功":"显示失败";
                    formatOut(info);
                    break;

                    //下面的是目录功能
                case "mkdir"://新,ok
                    info = OSDataCenter.diskSimService.createFile(filePath);
                    formatOut(info);
                    break;
                case "dir":
                    info = OSDataCenter.diskSimService.dirDirectory(OSDataCenter.diskSimService.getLastTreeItem(filePath))?"true":"false";
                    formatOut(info);
                    break;
                case "deldir":
                    info = OSDataCenter.diskSimService.rdDirectory(OSDataCenter.diskSimService.getLastTreeItem(filePath))?"true":"false";
                    formatOut(info);
                    break;

                //-------------新增功能-----------------------
                    //删除空目录
                case "rmdir"://ok
                    info = OSDataCenter.diskSimService.rmdir(filePath);//info填要输出的内容
                    formatOut(info);
                    break;

                //-----------------------------------------
            }

        } else if (matcher2.matches()) {//两个参数
            System.out.println(matcher2.group(0));
            String action = matcher2.group(1);
            String filePath = matcher2.group(2);
            //操作值
            String value = matcher2.group(3);

            List<String> fileNameList = OSDataCenter.diskSimService.getFileNameList(filePath);
            String info;
            switch (action){
                case "open"://ok
                    info = OSDataCenter.diskSimService.open_file(OSDataCenter.diskSimService.getLastTreeItem(filePath),value)?"打开成功":"打开失败";
                    formatOut(info);
                    break;
                case "read"://ok
                    info = OSDataCenter.diskSimService.read_file(OSDataCenter.diskSimService.getLastTreeItem(filePath),Integer.parseInt(value));
                    formatOut(info);
                    break;
                case "change"://ok
                    info = OSDataCenter.diskSimService.change(OSDataCenter.diskSimService.getLastTreeItem(filePath),Integer.parseInt(value))?"属性修改成功":"属性修改失败";
                    formatOut(info);
                    break;

                //-------------新增功能-----------------------

                //拷贝文件
                case "copy":
                    info = OSDataCenter.diskSimService.copyFile(filePath, value);//info填要输出的内容
                    formatOut(info);
                    break;
                 //移动文件
                case "move"://ok
                    info = OSDataCenter.diskSimService.move(filePath, value);//info填要输出的内容
                    formatOut(info);
                    break;
                //改变目录路径
                case "chdir"://ok
                    info = OSDataCenter.diskSimService.chdir(filePath, value);//info填要输出的内容
                    formatOut(info);
                    break;
                //----------------------------------------
                case "inputBuffer":
                    info = OSDataCenter.diskSimService.inputBuffer(Integer.parseInt(filePath),value)?"写缓冲成功":"写缓冲失败";
                    formatOut(info);

            }

        }else if (matcher3.matches()){//三个参数
            System.out.println(matcher3.group(0));
            String path = matcher3.group(2);
            int bufferNum = Integer.parseInt(matcher3.group(3));
            int length = Integer.parseInt(matcher3.group(4));

            String info = OSDataCenter.diskSimService.write_file(OSDataCenter.diskSimService.getLastTreeItem(path), bufferNum, length)?"成功":"失败";
            formatOut(info);
        }else if(matcher0.matches()){//无参数,ok
            //格式化操作
            String action = matcher0.group(1);
            if ("format".equals(action)) {
                String info = OSDataCenter.diskSimService.format();
                formatOut(info);
                FilePane.update(OSDataCenter.fileTree.getRootTree());
            }
        }
    }
    private void formatOut(String info){
        textArea.appendText(info);
        textArea.appendText("\n$ ");
        currentCaretPos[0]+=info.length()+3;
    }
    private void init(){

        textArea.appendText("指令表:\n");

        textArea.appendText("创建文件 create /path（需要后缀名.ex或者.tx\n");//1参
        textArea.appendText("打开文件 open /path openType(read/write)\n");//2参
        textArea.appendText("读文件  read /path readLength\n");//2参
        textArea.appendText("写文件  write /path bufferNum writLength\n");//3参
        textArea.appendText("关闭文件 close /path\n");//1参
        textArea.appendText("删除文件 delete /path\n");//1参
        textArea.appendText("显示文件内容 type /path\n");//1参
        textArea.appendText("改变文件属性 change /path newValue\n");//2参
        textArea.appendText("建立目录 mkdir /path\n");//1参
        textArea.appendText("显示目录内容 dir /path\n");//1参
        textArea.appendText("删除目录 deldir /path\n");//1参
        //新增
        textArea.appendText("删除空目录 rmdir /path\n");//1参
        textArea.appendText("拷贝文件 copy /path /path\n");//2参
        textArea.appendText("改变目录路径 chdir /path /path\n");//1参
        textArea.appendText("移动文件 move /path /path\n");//2参
        textArea.appendText("磁盘格式化 format\n");//无参

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
                    currentCaretPos[0] = textArea.getCaretPosition() + 2;
                    textArea.appendText("$ ");
                    textArea.positionCaret(textArea.getCaretPosition());
                    formatStr(command);
                    //System.out.println(currentCaretPos[0]+":"+textArea.getCaretPosition());
                }
            }
        });

    }

}