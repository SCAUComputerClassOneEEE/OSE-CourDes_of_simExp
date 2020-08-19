package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;
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
    private MenuItem createFileMenu = new MenuItem("创建文件");
    private MenuItem createDirectoryMenu = new MenuItem("创建目录");
    private MenuItem deleteMenu = new MenuItem("删除");

    private DiskSimService diskSimService = new DiskSimService();

    public MenuPane(TreeItem<AFile> treeItem){
        addMenu.getItems().addAll(openMenu, createFileMenu, createDirectoryMenu, deleteMenu);

        this.openMenu.setOnAction(actionEvent -> diskSimService.showFile(treeItem));

        this.createDirectoryMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(treeItem, 0);
            textInputBox.show();
        });

        this.createFileMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(treeItem, 1);
            textInputBox.show();
        });

        this.deleteMenu.setOnAction(actionEvent -> diskSimService.deleteFile(treeItem));
    }
}
