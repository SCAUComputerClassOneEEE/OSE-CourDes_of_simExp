package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FileTextField {
    private TextArea textArea = new TextArea();
    private BorderPane borderPane = new BorderPane();
    private MenuBar menuBar = new MenuBar();
    private Menu menu = new Menu();
    private MenuItem menuItem = new MenuItem();

    public FileTextField(Disk disk, TreeItem<AFile> myTreeItem){
        AFile aFile = myTreeItem.getValue();
        int diskNum = (int)aFile.getDiskNum();
        String str = disk.readFile(diskNum);
        String string = deleteCharString0(str, '*');

        textArea.setText(string);

        menuItem.setText("保存");

        menu.setText("文件");
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        menuItem.setOnAction(event -> {
            try {
                disk.writeFile(diskNum, textArea.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);

        Scene scene = new Scene(borderPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            OpenedFile.closeFile(aFile);
        });
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
