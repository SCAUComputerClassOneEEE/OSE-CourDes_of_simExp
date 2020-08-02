package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
//右键菜单
class MenuPane {
    private ContextMenu addMenu = new ContextMenu();
    private MenuItem openMenu = new MenuItem("打开");
    private MenuItem createFileMenu = new MenuItem("创建文件");
    private MenuItem createDirectoryMenu = new MenuItem("创建目录");
    private MenuItem deleteMenu = new MenuItem("删除");

    public MenuPane(TreeItem<AFile> treeItem, Disk disk){
        addMenu.getItems().addAll(openMenu, createFileMenu, createDirectoryMenu, deleteMenu);
        this.openMenu.setOnAction(actionEvent -> {});
        this.createDirectoryMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(disk, treeItem, 0);
        });
        this.createFileMenu.setOnAction(actionEvent -> {
            TextInputBox textInputBox = new TextInputBox(disk, treeItem, 1);
        });
        this.deleteMenu.setOnAction(actionEvent -> {
            delete(disk, treeItem);
        });
    }


    //删除
    /**
     *
     * @param disk          固定的磁盘类（新建会出错）
     * @param myTreeItem    操作的节点
     */
    public void delete(Disk disk, TreeItem<AFile> myTreeItem){
        AFile root = myTreeItem.getValue();
        char[] chars = new char[64];
        Arrays.fill(chars, '*');
        String str = String.valueOf(chars);
        //清理自己的
        disk.recovery( (int)root.getDiskNum() );
        try {
            //清理自己的
            disk.writeFile( (int)root.getDiskNum(), str );
            //清理孩子
            for(AFile aFile : root.getAFiles()){
                if(aFile.isDirectory()){
                    recoveryDisk(disk, (int)aFile.getDiskNum(), str);
                }
                recoveryDisk(disk, (int)aFile.getDiskNum(), str);
            }
            //清理父亲的
            AFile aFile = (AFile) myTreeItem.getParent().getValue();
            disk.writeFile( (int)aFile.getDiskNum(), resetChip(disk, aFile.getAFiles().indexOf(root), aFile.getAFiles().size(), (int)aFile.getDiskNum()));
            aFile.getAFiles().remove(root);
            myTreeItem.getParent().getChildren().remove(myTreeItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将创建文件的信息替换读出的磁盘块内容
     * @param position 删除碎片位置
     * @param length 磁盘块内容长度
     * @param diskNum 磁盘块号
     * @param disk    固定的磁盘类（新建会出错）
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String resetChip(Disk disk, int position, int length, int diskNum){
        int i;
        char[] block_cont = String.valueOf(disk.readFile(diskNum)).toCharArray();
        for(i = position * 8; i < (length-1) * 8; i++) block_cont[i] = block_cont[i + 8];
        for(; i < length * 8; i++) block_cont[i] = '*';
        System.out.print("block_cont:");
        System.out.println(block_cont);
        return String.valueOf(block_cont);
    }

    private void recoveryDisk(Disk disk, int diskNum, String str) throws Exception{
        disk.recovery(diskNum);
        disk.writeFile(diskNum, str);
    }
}
