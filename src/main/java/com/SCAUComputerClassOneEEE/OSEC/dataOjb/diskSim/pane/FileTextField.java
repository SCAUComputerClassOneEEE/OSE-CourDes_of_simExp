package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 文本编辑域
 */
public class FileTextField {
    private final TextArea textArea = new TextArea();
    private final BorderPane borderPane = new BorderPane();
    private final MenuBar menuBar = new MenuBar();
    private final Menu menu = new Menu();
    private final MenuItem save = new MenuItem();
    private final Stage stage = new Stage();
    private final Disk disk = Main.disk;

    public FileTextField(TreeItem<AFile> myTreeItem){
        init(myTreeItem);
    }

    private void init(TreeItem<AFile> myTreeItem){
        AFile aFile = myTreeItem.getValue();
        int diskNum = aFile.getDiskNum();
        //读取块中内容并进行转化
        String str = disk.readFile(diskNum);
        String string = deleteCharString0(str, '#');

        textArea.setText(string);
        if(myTreeItem.getValue().getProperty() == 3)
            textArea.setEditable(false);

        save.setText("保存");

        menu.setText("文件菜单");
        menu.getItems().add(save);

        menuBar.getMenus().add(menu);

        save.setOnAction(event -> {
            try {
                disk.writeFile(diskNum, textArea.getText());
                aFile.setDiskNum((char)disk.getFileSize(diskNum));
                AFile fatherFile = myTreeItem.getParent().getValue();
                disk.writeFile(fatherFile.getDiskNum(), modify(fatherFile, aFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        borderPane.setTop(menuBar);
        borderPane.setCenter(textArea);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> OpenFileManager.closeAFile(aFile));
    }

    public void show(){
        stage.show();
    }

    String modify(AFile fatherFile, AFile rootFile){
        char[] block_cont = String.valueOf(disk.readFile(fatherFile.getDiskNum())).toCharArray();
        char[] root_cont = rootFile.getALLData();
        int in = fatherFile.getAFiles().indexOf(rootFile);
        System.arraycopy(root_cont, 0, block_cont, in * 8, 8);
        return String.valueOf(block_cont);
    }

    //删除磁盘块中的'#'
    public static String deleteCharString0(String sourceString, char chElemData) {
        StringBuilder deleteString = new StringBuilder();
        for (int i = 0; i < sourceString.length(); i++) {
            if (sourceString.charAt(i) != chElemData) {
                deleteString.append(sourceString.charAt(i));
            }
        }
        return deleteString.toString();
    }
}
