package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class FileTree {
    private TreeView<AFile> treeView;
    private MyTreeItem rootTree;
    private VBox vBox;
    private Disk disk = new Disk();

    public FileTree(VBox vBox) {
        this.vBox = vBox;
        int header = disk.malloc_F_Header();
        if(header == -1){
            System.out.println("错误，磁盘已满");
        }else{
            System.out.println("根目录对应的磁盘号：" + header);
            char diskNum = (char)header;
            char property = 8;
            char length = 0;
            AFile rootFile = new AFile("root", "  ", property, diskNum, length, "");
            rootTree = new MyTreeItem(rootFile);
            this.setRootFileTreeItems(rootFile, rootTree);
            this.treeView = new TreeView<>(rootTree);
            this.treeView.setShowRoot(true);
            // 单元设置，TreeView下的每个子控件都支持,包扩子子控件,所以添加菜单栏那里只对有儿子有父亲的进行设置
            treeView.setCellFactory((TreeView<AFile> p) -> new TextFieldTreeCellImpl(this.disk));
            vBox.getChildren().add(treeView);
        }
    }

    private void setRootFileTreeItems(AFile aFile, TreeItem<AFile> rootTreeItem){
        //加载根目录
        ArrayList<AFile> aFiles = aFile.getAFiles();
        if(aFiles == null){
            return;
        }
        for(AFile file:aFiles){
//            System.out.println(file.getFileName());
            TreeItem<AFile> treeItem = new TreeItem<>(file);
            rootTreeItem.getChildren().add(treeItem);
            if(!treeItem.isLeaf()){
                setRootFileTreeItems(file, treeItem);
            }
        }
    }

}

