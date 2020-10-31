package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * 内部类，表示一个文件树节点
 */
@Getter
@Setter
public class MyTreeItem extends TreeItem<AFile> {

    public MyTreeItem(AFile aFile) {
        this.setValue(aFile);

        //设置图标
        File file = new File("src/main/resources/folder.png");
        if (isLeaf()) {
            file = new File("src/main/resources/文件.png");
        }

        this.setGraphic(new ImageView(new Image("file:" + file, 20, 20,
                true, true)));
    }

    @Override
    public boolean isLeaf() {
        return !getValue().isDirectory();
    }
}
