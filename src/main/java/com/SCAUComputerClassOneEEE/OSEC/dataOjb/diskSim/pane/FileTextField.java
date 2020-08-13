package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 文本编辑域
 */
public class FileTextField {
    private TextArea textArea = new TextArea();
    private BorderPane borderPane = new BorderPane();
    private MenuBar menuBar = new MenuBar();
    private Menu menu = new Menu();
    private MenuItem save = new MenuItem();
    private Stage stage = new Stage();
    private Disk disk = Main.disk;

    public FileTextField(TreeItem<AFile> myTreeItem){
        init(myTreeItem);
    }

    private void init(TreeItem<AFile> myTreeItem){
        AFile aFile = myTreeItem.getValue();
        int diskNum = (int)aFile.getDiskNum();
        //读取块中内容并进行转化
        String str = disk.readFile(diskNum);
        String string = deleteCharString0(str, '*');

        textArea.setText(string);

        save.setText("保存");

        menu.setText("文件菜单");
        menu.getItems().add(save);

        menuBar.getMenus().add(menu);

        save.setOnAction(event -> {
            try {
                disk.writeFile(diskNum, textArea.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            OpenFileManager.closeAFile(aFile);
        });
    }

    public void show(){
        stage.show();
    }

    public void close(){
        stage.close();
    }

    //删除磁盘块中的'*'
    String deleteCharString0(String sourceString, char chElemData) {
        String deleteString = "";
        for (int i = 0; i < sourceString.length(); i++) {
            if (sourceString.charAt(i) != chElemData) {
                deleteString += sourceString.charAt(i);
            }
        }
        return deleteString;
    }
}
