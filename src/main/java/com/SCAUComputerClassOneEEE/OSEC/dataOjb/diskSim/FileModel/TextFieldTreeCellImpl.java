package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.MenuPane;
import javafx.scene.control.TreeCell;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 对TreeView下的每个单元格进行处理
final class TextFieldTreeCellImpl extends TreeCell<AFile> {
    private MenuPane menuPane;
    private Disk disk;

    public TextFieldTreeCellImpl(Disk disk) {
        super();
        this.disk = disk;
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
            if(!getTreeItem().isLeaf() && getTreeItem().getParent() != null){
                menuPane = new MenuPane(getTreeItem(), disk);
                menuPane.getOpenMenu().setDisable(true);
                setContextMenu(menuPane.getAddMenu());
            }else if(!getTreeItem().isLeaf() && getTreeItem().getParent() == null){
                //根目录,不能删除，打开
                menuPane = new MenuPane(getTreeItem(), disk);
                menuPane.getOpenMenu().setDisable(true);
                menuPane.getDeleteMenu().setDisable(true);
                setContextMenu(menuPane.getAddMenu());
            }else if(getTreeItem().isLeaf()){
                //叶子（文本文件），不能创建孩子
                menuPane = new MenuPane(getTreeItem(), disk);
                menuPane.getCreateDirectoryMenu().setDisable(true);
                menuPane.getCreateFileMenu().setDisable(true);
                setContextMenu(menuPane.getAddMenu());
            }else{
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
