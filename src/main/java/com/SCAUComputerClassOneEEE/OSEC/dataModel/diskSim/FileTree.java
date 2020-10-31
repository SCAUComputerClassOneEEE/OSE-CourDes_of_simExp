package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim;

import com.SCAUComputerClassOneEEE.OSEC.utils.OS;
import com.SCAUComputerClassOneEEE.OSEC.pane.FilePane;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class FileTree {

    private static FileTree fileTree = new FileTree();

    //文件树界面
    private VBox fileTreePane;
    //文件树
    private TreeView<AFile> treeView;
    //根项目
    private MyTreeItem rootTree;

    public static FileTree getFileTree() {
        return fileTree;
    }

    public FileTree() {
        init();
        addListener();
    }

    //初始化
    private void init(){
        this.fileTreePane = new VBox();
        int header = OS.disk.malloc_F_Header();
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
            treeView.setCellFactory((TreeView<AFile> p) -> new TextFieldTreeCellImpl());
            fileTreePane.getChildren().add(treeView);
        }
    }

    private void setRootFileTreeItems(AFile aFile, TreeItem<AFile> rootTreeItem){
        //加载根目录
        ArrayList<AFile> aFiles = aFile.getAFiles();
        if(aFiles == null){
            return;
        }
        for(AFile file:aFiles){
            TreeItem<AFile> treeItem = new TreeItem<>(file);
            rootTreeItem.getChildren().add(treeItem);
            if(!treeItem.isLeaf()){
                setRootFileTreeItems(file, treeItem);
            }
        }
    }

    private void addListener(){
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) return;
            else FilePane.setTreeNode(newValue);
        });
    }

}

