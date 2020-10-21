package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.Compile;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;
import com.SCAUComputerClassOneEEE.OSEC.op.DiskPane;
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
    private final DiskSimService diskSimService = new DiskSimService();

    public FileTextField(TreeItem<AFile> myTreeItem){
        init(myTreeItem);
    }

    private void init(TreeItem<AFile> myTreeItem){
        AFile aFile = myTreeItem.getValue();
        int diskNum = aFile.getDiskNum();
        //读取块中内容并进行转化
        if(aFile.isFile())
            textArea.setText(aFile.getDiskContent());
        else if(aFile.isExeFile()) {
            StringBuilder contents = new StringBuilder();
            char[] chars = aFile.getDiskContent().toCharArray();
            for (char c:chars)
               contents.append(Compile.decompile(c) + (char)10);
            textArea.setText(contents.toString());
        }
        else
            textArea.setText(null);

        if(myTreeItem.getValue().getProperty() == 3)
            textArea.setEditable(false);

        save.setText("保存");

        menu.setText("文件菜单");
        menu.getItems().add(save);

        menuBar.getMenus().add(menu);

        save.setOnAction(event -> {
            try {
                if(aFile.isFile()){
                    disk.writeFile(diskNum, textArea.getText());
                    aFile.setLength((char)disk.getFileSize(diskNum));
                }else if(aFile.isExeFile()){
                    diskSimService.write_exeFile(aFile, textArea.getText());
                }
                AFile fatherFile = myTreeItem.getParent().getValue();
                disk.writeFile(fatherFile.getDiskNum(), modify(fatherFile, aFile));
                FilePane.update(myTreeItem);
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
