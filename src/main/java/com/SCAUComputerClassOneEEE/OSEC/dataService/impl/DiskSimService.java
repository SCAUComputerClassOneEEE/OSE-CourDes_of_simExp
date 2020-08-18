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
    public String createFile(TreeItem<AFile> myTreeItem, String fileName, int attribute){
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
            char property = (char)attribute;
            char length = 1;
            AFile newFile = new AFile(fileName, "tx", property, diskNum, length, root.getLocation()+"/"+root.getFileName());
            return getString(myTreeItem, root, newFile);
        }
    }

    @Override
    public boolean deleteFile(TreeItem<AFile> myTreeItem) {
        AFile root = myTreeItem.getValue();
        //如果文件已经打开，关闭失败
        if (root.isOpen())return false;
        
        boolean result;
        char[] chars = new char[64];
        Arrays.fill(chars, '#');
        String str = String.valueOf(chars);
        //清理自己的
        disk.recovery(root.getDiskNum());
        try {
            //清理自己的
            disk.writeFile(root.getDiskNum(), str);
            //清理孩子
            recoveryDisk(root, str);
            //清理父亲的
            AFile aFile = myTreeItem.getParent().getValue();
            disk.writeFile(aFile.getDiskNum(), resetChip(aFile.getAFiles().indexOf(root), aFile.getAFiles().size(), aFile.getDiskNum()));
            aFile.getAFiles().remove(root);
            myTreeItem.getParent().getChildren().remove(myTreeItem);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    @Override
    public boolean showFile(TreeItem<AFile> myTreeItem){
        if(myTreeItem == null)
            return false;
        else if (myTreeItem.isLeaf() && OpenFileManager.openAFile(myTreeItem.getValue(),"read&write")) {
            FileTextField fileTextField = new FileTextField(myTreeItem);
            fileTextField.show();
            return true;
        }
        else {
            System.out.println("已打开文件数达最大或文件已打开");
            return false;
        }
    }

    //不是直接界面打开，而是逻辑上打开了这个文件，并通过指针进行读取
    public boolean open_file(TreeItem<AFile> myTreeItem, String operation_type){
        //不存在的情况
        if(myTreeItem == null)
            return false;
        int property= myTreeItem.getValue().getProperty();
        //不能以写方式打开只读文件
        if(("write".equals(operation_type) && property == 4) ||
                ("read".equals(operation_type) && (property == 3 || property == 4)))
            return OpenFileManager.openAFile(myTreeItem.getValue(),operation_type);
        return false;
    }

    public String read_file(TreeItem<AFile> myTreeItem, int read_length){
        if(myTreeItem == null)
            return "文件不存在";
        if(OpenFileManager.contain(myTreeItem.getValue())||open_file(myTreeItem, "read")){
            String string = disk.readFile(myTreeItem.getValue().getDiskNum());
            return string.substring(0, Math.max(read_length, string.length())-1);
        }
        return "方式错误";
    }

    public boolean write_file(TreeItem<AFile> myTreeItem, String buffer,int write_length){
        if(myTreeItem == null)
            return false;
        if(OpenFileManager.contain(myTreeItem.getValue())||open_file(myTreeItem, "write")){
            try {
                AFile root = myTreeItem.getValue();
                String file_cont = disk.readFile(root.getDiskNum());
                String buffer_cont = buffer.substring(0, Math.max(write_length, buffer.length()));
                disk.writeFile(root.getDiskNum(), file_cont+buffer_cont);
                root.setDiskNum((char)disk.getFileSize(root.getDiskNum()));
                AFile fatherFile = myTreeItem.getParent().getValue();
                disk.writeFile(fatherFile.getDiskNum(), modify(fatherFile, root));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }else return false;
    }

    public boolean close_file(TreeItem<AFile> myTreeItem){
        if(myTreeItem == null)
            return false;
        else return OpenFileManager.closeAFile(myTreeItem.getValue());
    }

    public boolean typeFile(TreeItem<AFile> myTreeItem){
        return showFile(myTreeItem);
    }

    public boolean change(TreeItem<AFile> myTreeItem, int property){
        if(myTreeItem == null)
            return false;

        //如果文件已打开，则不能修改
        if (OpenFileManager.contain(myTreeItem.getValue()))return false;

        /*else if(OpenFileManager.openAFile(myTreeItem.getValue(),"write")){
            OpenFileManager.closeAFile(myTreeItem.getValue());
            return false;
        }*/
        try {
            //修改自己的
            AFile root = myTreeItem.getValue();
            root.setProperty((char)property);
            //修改父亲的
            AFile fatherFile = myTreeItem.getParent().getValue();
            disk.writeFile(fatherFile.getDiskNum(), modify(fatherFile, root));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
     * @param diskNum 磁盘号
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String replaceBlock_cont(int diskNum, int num, char[] block_cont){
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
            if(b != '#') num++;
        }
        System.out.println("num:"+num);
        return num >= 64;
    }

    /**
     * 删除文件的信息替换读出的磁盘块内容
     * @param position 删除碎片位置
     * @param length 磁盘块内容长度
     * @param diskNum 磁盘块号
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String resetChip(int position, int length, int diskNum){
        int i;
        char[] block_cont = String.valueOf(disk.readFile(diskNum)).toCharArray();
        for(i = position * 8; i < (length-1) * 8; i++) block_cont[i] = block_cont[i + 8];
        for(; i < length * 8; i++) block_cont[i] = '*';
        System.out.print("block_cont:");
        System.out.println(block_cont);
        return String.valueOf(block_cont);
    }

    /**
     * @param root      删除的文件\目录
     * @param str       将磁盘复原的字符串
     * @throws Exception 删除失败抛出异常
     */
    private void recoveryDisk(AFile root, String str) throws Exception{
        for(AFile aFile : root.getAFiles()){
            if(aFile.isDirectory())
                recoveryDisk(aFile, str);
            disk.recovery((int)aFile.getDiskNum());
            disk.writeFile((int)aFile.getDiskNum(), str);
        }
    }

    private String getString(TreeItem<AFile> myTreeItem, AFile root, AFile newFile) {
        String str = replaceBlock_cont(root.getDiskNum(), root.getAFiles().size(), newFile.getALLData());
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

    private String modify(AFile fatherFile, AFile rootFile){
        char[] block_cont = String.valueOf(disk.readFile(fatherFile.getDiskNum())).toCharArray();
        char[] root_cont = rootFile.getALLData();
        int in = fatherFile.getAFiles().indexOf(rootFile);
        for (int i = 0; i < 8; i++)
            block_cont[in * 8 + i] = root_cont[i];
        return String.valueOf(block_cont);
    }
}
