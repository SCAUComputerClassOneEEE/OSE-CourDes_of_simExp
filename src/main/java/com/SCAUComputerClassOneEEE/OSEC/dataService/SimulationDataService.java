package com.SCAUComputerClassOneEEE.OSEC.dataService;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import javafx.scene.control.TreeItem;

public interface SimulationDataService {
    String createFile(TreeItem<AFile> myTreeItem, String fileName);
    String createDirectory(TreeItem<AFile> myTreeItem, String fileName);
    String delete(TreeItem<AFile> myTreeItem);
}
