package com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim;

import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import com.SCAUComputerClassOneEEE.OSEC.pane.MenuPane;
import com.SCAUComputerClassOneEEE.OSEC.pane.FilePane;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

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
    //disk磁盘块内容
    private Disk.DiskBlock[] diskBlocks;

    public static FileTree getFileTree() { return fileTree; }
    public FileTree() {
        init();
        addListener();
    }

    //初始化
    private void init(){
        this.fileTreePane = new VBox();
        int header = OSDataCenter.disk.malloc_F_Header();
        if(header == -1){
            System.out.println("错误，磁盘已满");
        }else {
            char diskNum = (char) header;
            char property = 8;
            char length = 0;
            AFile rootFile = new AFile("root", "  ", property, diskNum, length, "");
            this.rootTree = new MyTreeItem(rootFile);
            this.treeView = new TreeView<>(rootTree);
            this.treeView.setShowRoot(true);
            // 单元设置，TreeView下的每个子控件都支持,包扩子子控件,所以添加菜单栏那里只对有儿子有父亲的进行设置
            treeView.setCellFactory((TreeView<AFile> p) -> new TextFieldTreeCellImpl());
            fileTreePane.getChildren().add(treeView);
        }
    }

    public void readingDisk(Disk disk){
        treeView.getRoot().getChildren().removeAll();
        if(disk != null)
            diskBlocks = disk.getDiskBlocks();
        if (diskBlocks != null)
            setRootFileTreeItems(2, rootTree);
    }

    private void setRootFileTreeItems(int diskNum, TreeItem<AFile> fatherItem) {
        char[] chars = diskBlocks[diskNum].getBlock_cont();
        AFile fatherFile = fatherItem.getValue();
        fatherItem.setExpanded(true);
        for (int i = 0; i < 8; i++) {
            int j = i * 8;
            if(chars[j] == '#') break;
            StringBuilder stringBuilder = new StringBuilder();
            for (int k = j; k < j + 3; k++) {
                if (chars[k] == ' ')
                    break;
                stringBuilder.append(chars[k]);
            }
            String fileName = stringBuilder.toString();
            String type = new String(chars, j + 3, 2);
            String location = fatherFile.getAbsoluteLocation();
            AFile aFile = new AFile(fileName, type, chars[j + 5], chars[j + 6], chars[j + 7], location);
            OSDataCenter.diskPane.updateType(chars[j + 6]);
            fatherFile.getAFiles().add(aFile);
            TreeItem<AFile> treeItem = new TreeItem<>(aFile);
            fatherItem.getChildren().add(treeItem);
            if(aFile.isDirectory())
                setRootFileTreeItems(aFile.getDiskNum(), treeItem);
        }
    }

    private void addListener(){
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) return;
            else FilePane.setTreeNode(newValue);
        });
    }

    /**
     * 内部类，表示一个文件树节点
     */
    @Getter
    @Setter
    public static class MyTreeItem extends TreeItem<AFile> {

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

    @Getter
    @Setter
    // 对TreeView下的每个单元格进行处理
    static final class TextFieldTreeCellImpl extends TreeCell<AFile> {
        private MenuPane menuPane;

        public TextFieldTreeCellImpl() {
            super();
        }

        @Override
        public void updateItem(AFile item, boolean empty) {
            super.updateItem(item, empty);
            // 为空不处理
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
                // 如果有儿子  并且 有父亲，则拥有添加、删除功能，不能打开
                if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
                    menuPane = new MenuPane(getTreeItem());
                    menuPane.getOpenMenu().setDisable(true);
                    menuPane.getCreateProcessMenu().setDisable(true);
                    setContextMenu(menuPane.getAddMenu());
                } else if (!getTreeItem().isLeaf() && getTreeItem().getParent() == null) {
                    //根目录,不能删除，打开
                    menuPane = new MenuPane(getTreeItem());
                    menuPane.getOpenMenu().setDisable(true);
                    menuPane.getDeleteMenu().setDisable(true);
                    menuPane.getCreateProcessMenu().setDisable(true);
                    setContextMenu(menuPane.getAddMenu());
                } else if (getTreeItem().isLeaf() && getTreeItem().getValue().isFile()) {
                    //叶子（文本文件），不能创建孩子
                    menuPane = new MenuPane(getTreeItem());
                    menuPane.getCreateDirectoryMenu().setDisable(true);
                    menuPane.getCreateFileMenu().setDisable(true);
                    menuPane.getCreateExeFileMenu().setDisable(true);
                    menuPane.getCreateProcessMenu().setDisable(true);
                    setContextMenu(menuPane.getAddMenu());
                } else if (getTreeItem().isLeaf() && getTreeItem().getValue().isExeFile()) {
                    //叶子（可知悉文件），不能创建孩子，可以创建进程
                    menuPane = new MenuPane(getTreeItem());
                    menuPane.getCreateDirectoryMenu().setDisable(true);
                    menuPane.getCreateFileMenu().setDisable(true);
                    menuPane.getCreateExeFileMenu().setDisable(true);
                    setContextMenu(menuPane.getAddMenu());
                } else {
                    //空白处
                    setMenuPane(null);
                }
            }
        }

        // 获取该Item的名字
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}

