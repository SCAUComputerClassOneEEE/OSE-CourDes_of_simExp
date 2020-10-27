package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.pane;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DiskSimService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

//右键菜单
public class MenuPane {
    private ContextMenu addMenu = new ContextMenu();
    private MenuItem openMenu = new MenuItem("打开");
    private MenuItem createFileMenu = new MenuItem("创建文本文件");
    private MenuItem createExeFileMenu = new MenuItem("创建可执行文件");
    private MenuItem createDirectoryMenu = new MenuItem("创建目录");
    private MenuItem deleteMenu = new MenuItem("删除");
    private MenuItem createProcessMenu = new MenuItem("创建进程");

    private DiskSimService diskSimService = new DiskSimService();

    public MenuPane(TreeItem<AFile> treeItem){
        addMenu.getItems().addAll(openMenu, createExeFileMenu, createFileMenu, createDirectoryMenu, createProcessMenu, deleteMenu);

        this.openMenu.setOnAction(actionEvent -> diskSimService.showFile(treeItem));

        this.createDirectoryMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(treeItem, 0);
            textInputBox.show();
        });

        this.createFileMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(treeItem, 1);
            textInputBox.show();
        });

        this.createExeFileMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(treeItem, 2);
            textInputBox.show();
        });

        this.deleteMenu.setOnAction(actionEvent -> diskSimService.deleteFile(treeItem));

        this.createProcessMenu.setOnAction(actionEvent -> FilePane.tipBox(treeItem));
    }
}
