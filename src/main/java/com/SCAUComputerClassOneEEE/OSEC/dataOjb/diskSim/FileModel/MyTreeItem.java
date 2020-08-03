package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class MyTreeItem extends TreeItem<AFile> {

    public MyTreeItem(AFile aFile){
        this.setValue(aFile);
    }

    @Override
    public boolean isLeaf(){
        return !getValue().isDirectory();
    }
}
