package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

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

@Getter
@Setter
class MyTreeItem extends TreeItem<AFile> {

    public MyTreeItem(AFile aFile){
        this.setValue(aFile);
    }

    @Override
    public boolean isLeaf(){
        return !getValue().isDirectory();
    }
}

@Getter
@Setter
class AFile{
    private String fileName;
    /*
     *名仅可以使用字母、数字和除“$”、 “.”、 “/”以外的字符
     * 第一个字节的值为“$”时表示该目录为空目录项
     *，文件名和类型名之间用“.”分割，用“/”作为路径名中目录间分隔符
     * 3 个字节
     */
    private String type;           //2个字节   类型
    private char property;       //1个字节  属性
    private char diskNum;        //1个字节 起始盘块号
    private char length;         //1个字节,盘数

    private String location;        //位置,存放父路径的，好按照名称来找
    private ArrayList<AFile> aFiles = new ArrayList<>();

    //创建文件、目录
    public AFile(String fileName, String type, char property, char diskNum, char length, String location){
        this.fileName = fileName;
        this.type = type;
        this.property = property;
        this.diskNum = diskNum;
        this.length = length;
        this.location = location;
    }

    @Override
    public String toString(){
        return this.fileName;
    }

    //是文本文件为true
    public boolean isFile(){
        return !"  ".equals(this.type);
    }
    //是目录为true
    public boolean isDirectory(){
        return "  ".equals(this.type);
    }
    //得到文件信息
    public char[] getALLData(){ return (this.fileName + this.type + this.property + this.diskNum + this.length).toCharArray(); }
}

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