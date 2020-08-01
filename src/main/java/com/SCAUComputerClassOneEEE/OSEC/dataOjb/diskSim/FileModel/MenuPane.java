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
    private AFile root;             //事件节点
    private TreeItem myTreeItem;    //事件节点
    private Disk disk;
    private ContextMenu addMenu = new ContextMenu();
    private MenuItem openMenu = new MenuItem("打开");
    private MenuItem createFileMenu = new MenuItem("创建文件");
    private MenuItem createDirectoryMenu = new MenuItem("创建目录");
    private MenuItem deleteMenu = new MenuItem("删除");

    public MenuPane(TreeItem<AFile> treeItem, Disk disk){
        this.disk = disk;
        this.myTreeItem = treeItem;
        this.root = treeItem.getValue();
        addMenu.getItems().addAll(openMenu, createFileMenu, createDirectoryMenu, deleteMenu);
        addFunction();
    }

    private void addFunction(){
        this.openMenu.setOnAction(actionEvent -> {});
        this.createDirectoryMenu.setOnAction(actionEvent -> {
            createDirectory();
        });
        this.createFileMenu.setOnAction(actionEvent -> {
            createFile();
        });
        this.deleteMenu.setOnAction(actionEvent -> {
            delete();
        });
    }

    //创建目录
    public void createDirectory(){
        int header = disk.malloc_F_Header();
        if(header == -1){
            System.out.println("磁盘已满，创建失败！");
        }else if(this.root.getAFiles().size() >= 8){
            System.out.println("该目录已满，创建失败！");
        }else {
            System.out.println("新磁盘号："+header);
            char diskNum = (char) header;
            char property = 8;
            char length = 0;
            AFile newFile = new AFile("roo", "  ", property, diskNum, length, root.getLocation() + "/" + root.getFileName());
            System.out.println("新文件信息："+String.valueOf(newFile.getALLData()));
            System.out.println(newFile.getFileName() +","+ newFile.getType() +","+ (int)newFile.getProperty() +","+ (int)newFile.getDiskNum() +","+ (int)newFile.getLength());
            String str = replaceBlock_cont(this.root.getAFiles().size(), newFile.getALLData());
            System.out.println("要写入的父目录的磁盘号:"+ (int)this.root.getDiskNum());
            System.out.println("写入前磁盘的内容:"+disk.readFile( (int)this.root.getDiskNum()));
            System.out.println("要写入的磁盘的字符串:"+str);
            try {
                disk.writeFile(root.getDiskNum(), str);
                MyTreeItem treeItem = new MyTreeItem(newFile);
                this.myTreeItem.getChildren().add(treeItem);
                this.root.getAFiles().add(newFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //创建文件
    public void createFile(){
        int header = disk.malloc_F_Header();
        if(header == -1){
            System.out.println("磁盘已满，创建失败！");
        }else if(this.root.getAFiles().size() >= 8){
            System.out.println("该目录已满，创建失败！");
        }else{
            System.out.println("新磁盘号："+header);
            char diskNum = (char)header;
            char property = 4;
            char length = 1;
            AFile newFile = new AFile("roo", "tx", property, diskNum, length, root.getLocation()+"/"+root.getFileName());
            System.out.println("新文件信息："+String.valueOf(newFile.getALLData()));
            System.out.println(newFile.getFileName() +","+ newFile.getType() +","+ (int)newFile.getProperty() +","+ (int)newFile.getDiskNum() +","+ (int)newFile.getLength());
            String str = replaceBlock_cont(this.root.getAFiles().size(), newFile.getALLData());
            System.out.println("要写入的父目录的磁盘号:"+ (int)this.root.getDiskNum());
            System.out.println("写入前磁盘的内容:"+disk.readFile( (int)this.root.getDiskNum()));
            System.out.println("要写入的磁盘的字符串:"+str);
            try{
                disk.writeFile(root.getDiskNum(), str);
                MyTreeItem treeItem = new MyTreeItem(newFile);
                this.myTreeItem.getChildren().add(treeItem);
                this.root.getAFiles().add(newFile);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //删除
    public void delete(){
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
                disk.recovery((int)aFile.getDiskNum());
                disk.writeFile((int)aFile.getDiskNum(), str);
            }
            //清理父亲的
            AFile aFile = (AFile) this.myTreeItem.getParent().getValue();
            disk.writeFile( (int)aFile.getDiskNum(), resetChip(aFile.getAFiles().indexOf(root), aFile.getAFiles().size(), (int)aFile.getDiskNum()));
            aFile.getAFiles().remove(this.root);
            this.myTreeItem.getParent().getChildren().remove(myTreeItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //判断是否已经创建文件(文件名不重复)
    private boolean foundFile(String fileName){
        for(AFile aFile : this.root.getAFiles()){
            if(fileName.equals(aFile.getFileName()))
                return true;
        }
        return false;
    }

    /**
     * 将创建文件的信息替换读出的磁盘块内容
     * @param num 父目录中孩子数量
     * @param block_cont 孩子的信息
     * @return 将要写入父目录对应的磁盘块的字符串
     */
    private String replaceBlock_cont(int num, char[] block_cont){
        String str = disk.readFile((int) this.root.getDiskNum());
        char[] chars = str.toCharArray();
        int i = num * 8;
        for(char c : block_cont) {
            chars[i++] = c;
        }
        return String.valueOf(chars);
    }

    /**
     * 将创建文件的信息替换读出的磁盘块内容
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
}
