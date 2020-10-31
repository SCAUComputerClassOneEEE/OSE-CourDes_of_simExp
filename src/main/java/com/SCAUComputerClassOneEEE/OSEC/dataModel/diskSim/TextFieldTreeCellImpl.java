package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim;

import com.SCAUComputerClassOneEEE.OSEC.pane.MenuPane;
import javafx.scene.control.TreeCell;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
// 对TreeView下的每个单元格进行处理
final class TextFieldTreeCellImpl extends TreeCell<AFile> {
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
