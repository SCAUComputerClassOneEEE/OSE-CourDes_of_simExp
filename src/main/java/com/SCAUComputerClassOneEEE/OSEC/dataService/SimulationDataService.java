package com.SCAUComputerClassOneEEE.OSEC.dataService;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import javafx.scene.control.TreeItem;

public interface SimulationDataService {
    String createFile(TreeItem<AFile> myTreeItem, String fileName);
    String createDirectory(TreeItem<AFile> myTreeItem, String fileName);
    boolean delete(TreeItem<AFile> myTreeItem);
    boolean open(TreeItem<AFile> myTreeItem);
}
