package com.SCAUComputerClassOneEEE.OSEC.dataService.impl;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.MyTreeItem;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FileTextField;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.dataService.SimulationDataService;
import javafx.scene.control.TreeItem;
import lombok.Setter;

import java.util.Arrays;

@Setter
public class DiskSimService implements SimulationDataService {
    private Disk disk = Main.disk;

    @Override
    public String createFile(TreeItem<AFile> myTreeItem, String fileName){
        AFile root = myTreeItem.getValue();
        if (foundFile(root, fileName)){
            return "文件名重复，创建失败！";
        }else if(root.getAFiles().size() >= 8){
            return "该目录已满，创建失败！";
        }
        int header = disk.malloc_F_Header();
        if(header == -1){
            return "磁盘已满，创建失败！";
        }else{
            System.out.println("新磁盘号："+header);
            char diskNum = (char)header;
            char property = 4;
            char length = 1;
            AFile newFile = new AFile(fileName, "tx", property, diskNum, length, root.getLocation()+"/"+root.getFileName());
            return getString(myTreeItem, root, newFile);
        }
    }

    /**
     *
     * @param myTreeItem    操作的父节点（想要在之下创建子节点）
     * @param fileName      文件目录名（想判断是否重命名）
     */
    @Override
    public String createDirectory(TreeItem<AFile> myTreeItem, String fileName){
        AFile root = myTreeItem.getValue();
        if (foundFile(root, fileName)){
            return "文件名重复，创建失败！";
        }else if(root.getAFiles().size() >= 8){
            return "该目录已满，创建失败！";
        }
        int header = disk.malloc_F_Header();
        if(header == -1){
            return "磁盘已满，创建失败！";
        }else {
            System.out.println("新磁盘号："+header);
            char diskNum = (char) header;
            char property = 8;
            char length = 0;
            AFile newFile = new AFile(fileName, "  ", property, diskNum, length, root.getLocation() + "/" + root.getFileName());
            return getString(myTreeItem, root, newFile);
        }
    }

    @Override
    //删除
    public boolean delete(TreeItem<AFile> myTreeItem){
        AFile root = myTreeItem.getValue();
        char[] chars = new char[64];
        Arrays.fill(chars, '*');
        String str = String.valueOf(chars);
        //清理自己的
        disk.recovery(root.getDiskNum());
        try {
            //清理自己的
            disk.writeFile(root.getDiskNum(), str );
            //清理孩子
            recoveryDisk(disk, root, str);
            //清理父亲的
            AFile aFile = (AFile) myTreeItem.getParent().getValue();
            disk.writeFile( (int)aFile.getDiskNum(), resetChip(disk, aFile.getAFiles().indexOf(root), aFile.getAFiles().size(), (int)aFile.getDiskNum()));
            aFile.getAFiles().remove(root);
            myTreeItem.getParent().getChildren().remove(myTreeItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean open(TreeItem<AFile> myTreeItem){
        if (myTreeItem.isLeaf() && OpenFileManager.openAFile(myTreeItem.getValue())) {
            FileTextField fileTextField = new FileTextField(myTreeItem);
            fileTextField.show();
            return true;
        }
        else {
            System.out.println("已打开文件数达最大或文件已打开");
            return false;
        }
    }


    //判断是否已经创建文件(文件名不重复)
    private boolean foundFile(AFile root, String fileName){
        for(AFile aFile : root.getAFiles()){
            if(fileName.equals(aFile.getFileName()))
                return true;
        }
        return false;
    }

    /**
     * 将创建文件的信息替换读出的磁盘块内容
     * @param num 父目录中孩子数量
     * @param block_cont 孩子的信息
     * @param disk 固定的磁盘类（新建会出错）
     * @param diskNum 磁盘号
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String replaceBlock_cont(Disk disk, int diskNum, int num, char[] block_cont){
        String str = disk.readFile(diskNum);
        char[] chars = str.toCharArray();
        int i = num * 8;
        for(char c : block_cont) {
            chars[i++] = c;
        }
        return String.valueOf(chars);
    }

    //判断磁盘块是否已满
    private boolean directory_full(String block_cont){
        int num = 0;
        char[] block_conts = block_cont.toCharArray();
        for(char b : block_conts){
            if(b != '*') num++;
        }
        System.out.println("num:"+num);
        return num >= 64;
    }


    /**
     * 删除文件的信息替换读出的磁盘块内容
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

    /**
     *
     * @param disk      固定的磁盘类（新建会出错）
     * @param root      删除的文件\目录
     * @param str       将磁盘复原的字符串
     * @throws Exception 删除失败抛出异常
     */
    private void recoveryDisk(Disk disk, AFile root, String str) throws Exception{
        for(AFile aFile : root.getAFiles()){
            if(aFile.isDirectory())
                recoveryDisk(disk, aFile, str);
            disk.recovery((int)aFile.getDiskNum());
            disk.writeFile((int)aFile.getDiskNum(), str);
        }
    }

    private String getString(TreeItem<AFile> myTreeItem, AFile root, AFile newFile) {
        String str = replaceBlock_cont(disk, (int)root.getDiskNum(), root.getAFiles().size(), newFile.getALLData());
        try{
            disk.writeFile((int)root.getDiskNum(), str);
            MyTreeItem treeItem = new MyTreeItem(newFile);
            myTreeItem.getChildren().add(treeItem);
            myTreeItem.setExpanded(true);
            root.getAFiles().add(newFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        myTreeItem.setExpanded(true);
        return "创建成功";
    }

}
