package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;


import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class TextInputBox {
    private GridPane root;
    private Label tipLable;
    private Label fileName;
    private Label warmLable;
    private Button button;
    private TextField fieldFileName;
    private MenuPane menuPane;

    TextInputBox(Disk disk, TreeItem<AFile> myTreeItem, int type){
        root = new GridPane();

        root.setPadding(new Insets(20));
        root.setHgap(25);
        root.setVgap(15);

        tipLable = new Label("请输入文件\\目录名:");
        root.add(tipLable, 0, 0, 2, 1);

        fileName = new Label("文件名");
        fieldFileName = new TextField();
        warmLable = new Label();//警告
        button = new Button("确认");

        GridPane.setHalignment(fileName, HPos.RIGHT);
        root.add(fileName, 0, 1);

        GridPane.setHalignment(warmLable, HPos.LEFT);
        root.add(warmLable, 0, 2, 2,1);

        // 文本的水平对齐
        GridPane.setHalignment(fieldFileName, HPos.LEFT);
        root.add(fieldFileName, 1, 1);

        // 按钮的水平对齐
        GridPane.setHalignment(button, HPos.RIGHT);
        root.add(button, 1, 3);

        Scene scene = new Scene(root, 350, 200);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.show();

        fieldFileName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() > 3){
                    warmLable.setText("文件名长度要求小于等于3");
                    button.setDisable(true);
                }else if(judge(newValue)){
                    warmLable.setText("文件不能包含“$”、 “.”、 “/”字符");
                    button.setDisable(true);
                }else{
                    warmLable.setText("");
                    button.setDisable(false);
                }
            }
        });
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                //1为文件，0为目录
                if(type == 1) createFile(disk, myTreeItem, fieldFileName.getText());
                else if(type == 0) createDirectory(disk, myTreeItem, fieldFileName.getText());
            }
        });
    }

    //用于命令行创建的
    public TextInputBox(){}

    //判断有无“$”、 “.”、 “/”字符
    private boolean judge(String string){
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if(c == '$' || c == '.' || c == '/')
                return true;
        }
        return false;
    }

    //创建目录
    /**
     *
     * @param disk          固定的磁盘类（新建会出错）
     * @param myTreeItem    操作的父节点（想要在之下创建子节点）
     * @param fileName      文件目录名（想判断是否重命名）
     */
    public String createDirectory(Disk disk, TreeItem<AFile> myTreeItem, String fileName){
        AFile root = myTreeItem.getValue();
        if (foundFile(root, fileName)){
            return "文件名重复，创建失败！";
        }else if(root.getAFiles().size() >= 8){
            return "该目录已满，创建失败！";
        }
        int header = disk.malloc_F_Header();
        if(header == -1){
            return "磁盘已满，创建失败！";
        }else {
            System.out.println("新磁盘号："+header);
            char diskNum = (char) header;
            char property = 8;
            char length = 0;
            AFile newFile = new AFile(fileName, "  ", property, diskNum, length, root.getLocation() + "/" + root.getFileName());
            String str = replaceBlock_cont(disk, (int)root.getDiskNum(), root.getAFiles().size(), newFile.getALLData());
            try {
                disk.writeFile((int)root.getDiskNum(), str);
                MyTreeItem treeItem = new MyTreeItem(newFile);
                myTreeItem.getChildren().add(treeItem);
                root.getAFiles().add(newFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            myTreeItem.setExpanded(true);
            return "创建成功";
        }
    }

    //创建文件
    /**
     *
     * @param disk          固定的磁盘类（新建会出错）
     * @param myTreeItem    操作的父节点（想要在之下创建子节点）
     * @param fileName      文件目录名（想判断是否重命名）
     */
    public String createFile(Disk disk, TreeItem<AFile> myTreeItem, String fileName){
        AFile root = myTreeItem.getValue();
        if (foundFile(root, fileName)){
            return "文件名重复，创建失败！";
        }else if(root.getAFiles().size() >= 8){
            return "该目录已满，创建失败！";
        }
        int header = disk.malloc_F_Header();
        if(header == -1){
            return "磁盘已满，创建失败！";
        }else{
            System.out.println("新磁盘号："+header);
            char diskNum = (char)header;
            char property = 4;
            char length = 1;
            AFile newFile = new AFile(fileName, "tx", property, diskNum, length, root.getLocation()+"/"+root.getFileName());
            String str = replaceBlock_cont(disk, (int)root.getDiskNum(), root.getAFiles().size(), newFile.getALLData());
            try{
                disk.writeFile((int)root.getDiskNum(), str);
                MyTreeItem treeItem = new MyTreeItem(newFile);
                myTreeItem.getChildren().add(treeItem);
                root.getAFiles().add(newFile);
            }catch (Exception e){
                e.printStackTrace();
            }
            myTreeItem.setExpanded(true);
            return "创建成功";
        }
    }

    //删除
    /**
     *
     * @param disk          固定的磁盘类（新建会出错）
     * @param myTreeItem    操作的节点
     * @return 删除成功为true
     */
    public boolean delete(Disk disk, TreeItem<AFile> myTreeItem){
        AFile root = myTreeItem.getValue();
        char[] chars = new char[64];
        Arrays.fill(chars, '*');
        String str = String.valueOf(chars);
        //清理自己的
        disk.recovery( (int)root.getDiskNum() );
        try {
            //清理自己的
            disk.writeFile( (int)root.getDiskNum(), str );
            //清理孩子
            recoveryDisk(disk, root, str);
            //清理父亲的
            AFile aFile = (AFile) myTreeItem.getParent().getValue();
            disk.writeFile( (int)aFile.getDiskNum(), resetChip(disk, aFile.getAFiles().indexOf(root), aFile.getAFiles().size(), (int)aFile.getDiskNum()));
            aFile.getAFiles().remove(root);
            myTreeItem.getParent().getChildren().remove(myTreeItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //判断磁盘块是否已满
    private boolean directory_full(String block_cont){
        int num = 0;
        char[] block_conts = block_cont.toCharArray();
        for(char b : block_conts){
            if(b != '*') num++;
        }
        System.out.println("num:"+num);
        return num >= 64;
    }

    //判断是否已经创建文件(文件名不重复)
    private boolean foundFile(AFile root, String fileName){
        for(AFile aFile : root.getAFiles()){
            if(fileName.equals(aFile.getFileName()))
                return true;
        }
        return false;
    }

    /**
     * 将创建文件的信息替换读出的磁盘块内容
     * @param num 父目录中孩子数量
     * @param block_cont 孩子的信息
     * @param disk 固定的磁盘类（新建会出错）
     * @param diskNum 磁盘号
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String replaceBlock_cont(Disk disk, int diskNum, int num, char[] block_cont){
        String str = disk.readFile(diskNum);
        char[] chars = str.toCharArray();
        int i = num * 8;
        for(char c : block_cont) {
            chars[i++] = c;
        }
        return String.valueOf(chars);
    }

    /**
     * 删除文件的信息替换读出的磁盘块内容
     * @param position 删除碎片位置
     * @param length 磁盘块内容长度
     * @param diskNum 磁盘块号
     * @param disk    固定的磁盘类（新建会出错）
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String resetChip(Disk disk, int position, int length, int diskNum){
        int i;
        char[] block_cont = String.valueOf(disk.readFile(diskNum)).toCharArray();
        for(i = position * 8; i < (length-1) * 8; i++) block_cont[i] = block_cont[i + 8];
        for(; i < length * 8; i++) block_cont[i] = '*';
        System.out.print("block_cont:");
        System.out.println(block_cont);
        return String.valueOf(block_cont);
    }

    /**
     *
     * @param disk      固定的磁盘类（新建会出错）
     * @param root      删除的文件\目录
     * @param str       将磁盘复原的字符串
     * @throws Exception 删除失败抛出异常
     */
    private void recoveryDisk(Disk disk, AFile root, String str) throws Exception{
        for(AFile aFile : root.getAFiles()){
            if(aFile.isDirectory())
                recoveryDisk(disk, aFile, str);
            disk.recovery((int)aFile.getDiskNum());
            disk.writeFile((int)aFile.getDiskNum(), str);
        }
    }
}
