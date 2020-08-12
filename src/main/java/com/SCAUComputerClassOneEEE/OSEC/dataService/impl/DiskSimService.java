package com.SCAUComputerClassOneEEE.OSEC.dataService.impl;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.TextInputBox;
import com.SCAUComputerClassOneEEE.OSEC.dataService.SimulationDataService;
import javafx.scene.control.TreeItem;
import lombok.Setter;

@Setter
public class DiskSimService implements SimulationDataService {
    private TextInputBox textInputBox = new TextInputBox();
    private Disk disk = Main.disk;
    @Override
    public String createFile(TreeItem<AFile> myTreeItem, String fileName) {
        return textInputBox.createFile(disk,myTreeItem,fileName);
    }

    @Override
    public String createDirectory(TreeItem<AFile> myTreeItem, String fileName) {
        return textInputBox.createDirectory(disk,myTreeItem,fileName);
    }

    @Override
    public String delete(TreeItem<AFile> myTreeItem) {
        if (textInputBox.delete(disk,myTreeItem)){
            return "删除成功";
        }
        return "删除文件失败或文件不存在";
    }
}
