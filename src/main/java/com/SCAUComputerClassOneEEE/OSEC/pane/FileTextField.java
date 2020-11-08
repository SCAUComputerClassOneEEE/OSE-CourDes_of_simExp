package com.SCAUComputerClassOneEEE.OSEC.pane;

import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.AFile;
import com.SCAUComputerClassOneEEE.OSEC.utils.CompileUtil;
import com.SCAUComputerClassOneEEE.OSEC.data_service.DiskSimService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 文本编辑域
 */
public class FileTextField {
    private final TextArea textArea = new TextArea();
    private final BorderPane borderPane = new BorderPane();
    private final MenuBar menuBar = new MenuBar();
    private final Menu menu = new Menu();
    private final MenuItem save = new MenuItem();
    private final MenuItem selectAll = new MenuItem();
    private final Stage stage = new Stage();
    private final DiskSimService diskSimService = new DiskSimService();

    private String text;

    public FileTextField(TreeItem<AFile> myTreeItem) {
        init(myTreeItem);
    }

    private void init(TreeItem<AFile> myTreeItem) {
        AFile aFile = myTreeItem.getValue();

        save.setText("保存");
        selectAll.setText("全选");
        menu.setText("菜单");
        menu.getItems().addAll(save, selectAll);
        menuBar.getMenus().addAll(menu);

        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle(aFile.getFileName() + "." + aFile.getType());

        fileContentDisplay(aFile);
        setWindowProperty(myTreeItem);
        addListener(myTreeItem);
    }

    public void show(){
        stage.show();
    }

    public void save(TreeItem<AFile> myTreeItem) {
        try {
            AFile aFile = myTreeItem.getValue();
            int diskNum = aFile.getDiskNum();
            if(aFile.isFile()){
                int len = 64 - (textArea.getText().length() % 64);
                String rest = "";
                for (int i=0;i<len;i++){
                    rest += "#";
                }
                System.out.println("rest="+rest);
                String newContent = textArea.getText() + rest;
                OSDataCenter.disk.writeFile(diskNum, newContent);
                aFile.setLength((char) OSDataCenter.disk.getFileSize(diskNum));
            }else if(aFile.isExeFile()){
                diskSimService.write_exeFile(aFile, textArea.getText());
            }
            AFile fatherFile = myTreeItem.getParent().getValue();
            OSDataCenter.disk.writeFile(fatherFile.getDiskNum(), modify(fatherFile, aFile));
            FilePane.update(myTreeItem);
            text = textArea.getText();
            stage.setTitle(aFile.getFileName() + "." + aFile.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取块中内容并进行转化
     * @param aFile 文件
     */
    void fileContentDisplay(AFile aFile) {
        String textFile;
        if(aFile.isFile())
            textFile = aFile.getDiskContent();
        else if(aFile.isExeFile()) {
            StringBuilder contents = new StringBuilder();
            char[] chars = aFile.getDiskContent().toCharArray();
            for (char c:chars)
                contents.append(CompileUtil.decompile(c) + ";" + (char)10);
            textFile = contents.toString();
        }
        else
            textFile = "";
        textArea.setText(textFile);
        text = textFile;
    }

    /**
     * 系统功能设置
     * @param myTreeItem 相应文件项
     */
    void setWindowProperty(TreeItem<AFile> myTreeItem) {
        AFile aFile = myTreeItem.getValue();
        //只读文件设置不可修改
        if(myTreeItem.getValue().getProperty() == 3)
            textArea.setEditable(false);
        save.setOnAction(event -> {
            save(myTreeItem);
            stage.setTitle(aFile.getFileName() + "." + aFile.getType());
        });
        selectAll.setOnAction(event -> textArea.selectAll());
        borderPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S)
                save(myTreeItem);
            else if (event.getCode() == KeyCode.A)
                textArea.selectAll();
        });
        stage.setOnCloseRequest(event -> OpenFileManager.closeAFile(myTreeItem.getValue()));
    }

    String modify(AFile fatherFile, AFile rootFile) {
        char[] block_cont = String.valueOf(OSDataCenter.disk.readFile(fatherFile.getDiskNum())).toCharArray();
        char[] root_cont = rootFile.getALLData();
        int in = fatherFile.getAFiles().indexOf(rootFile);
        System.arraycopy(root_cont, 0, block_cont, in * 8, 8);
        return String.valueOf(block_cont);
    }

    void addListener(TreeItem<AFile> myTreeItem) {
        AFile aFile = myTreeItem.getValue();
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(text)) {
                stage.setTitle("*" + aFile.getFileName() + "." + aFile.getType());
            } else stage.setTitle(aFile.getFileName() + "." + aFile.getType());
        });
    }
}
