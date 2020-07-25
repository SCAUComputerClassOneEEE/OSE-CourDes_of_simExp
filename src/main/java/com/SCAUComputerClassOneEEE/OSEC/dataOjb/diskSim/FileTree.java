package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

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
            treeView.setCellFactory((TreeView<AFile> p) -> new TextFieldTreeCellImpl());
            vBox.getChildren().add(treeView);
        }
    }

    private void setRootFileTreeItems(AFile aFile, TreeItem<AFile> rootTreeItem){
        //加载根目录
        AFile[] aFiles = aFile.getAFiles();
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
    private AFile[] aFiles;

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

    public boolean isFile(){
        return !"  ".equals(this.type);
    }

    public boolean isDirectory(){
        return "  ".equals(this.type);
    }

    public String getALLData(){return this.fileName + this.type + this.property + this.diskNum + this.length;}
}

@Getter
@Setter
//右键菜单
class MenuPane {
    private AFile root;
    private TreeItem myTreeItem;
    private Disk disk = new Disk();
    private ContextMenu addMenu = new ContextMenu();
    private MenuItem openMenu = new MenuItem("打开");
    private MenuItem createFileMenu = new MenuItem("创建文件");
    private MenuItem createDirectoryMenu = new MenuItem("创建目录");
    private MenuItem deleteMenu = new MenuItem("删除");

    public MenuPane(TreeItem<AFile> treeItem){
        this.myTreeItem = treeItem;
        this.root = treeItem.getValue();
        addMenu.getItems().addAll(openMenu, createFileMenu, createDirectoryMenu, deleteMenu);
        addFunction();
    }


    private void addFunction(){
        this.openMenu.setOnAction(actionEvent -> {});
        this.createDirectoryMenu.setOnAction(actionEvent -> {
            int header1 = disk.malloc_F_Header();
            //新增
            System.out.println("新建文件对应的磁盘号：" + header1);
            System.out.println("root对应的磁盘号：" + (int)this.root.getDiskNum());
            if(header1 == -1){
                System.out.println("磁盘已满，创建失败！");
            }else if(disk.readFile(this.root.getDiskNum()).length() >= 64){
                System.out.println("该目录已满，创建失败！");
            }else {
                char diskNum = (char) header1;
                char property = 8;
                char length = 0;
                AFile newFile = new AFile("roo", "  ", property, diskNum, length, root.getLocation() + "/" + root.getFileName());
                MyTreeItem treeItem = new MyTreeItem(newFile);
                String str = disk.readFile(root.getDiskNum()) + newFile.getALLData();
                try {
                    disk.writeFile(root.getDiskNum(), str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.myTreeItem.getChildren().add(treeItem);
                //新增
                System.out.println("str:"+str+","+(int)property+","+(int)diskNum+","+(int)length);
                System.out.println("长度："+str.length());
                System.out.println("root目录对应磁盘内容:"+disk.readFile((int)this.root.getDiskNum()));
            }
        });
        this.createFileMenu.setOnAction(actionEvent -> {
            int header2 = disk.malloc_F_Header();
            System.out.println("新建文件对应的磁盘号：" + header2);
            System.out.println("root对应的磁盘号：" + (int)this.root.getDiskNum());
            if(header2 == -1){
                System.out.println("磁盘已满，创建失败！");
            }else{
                char diskNum = (char)header2;
                char property = 4;
                char length = 1;
                AFile newFile = new AFile("roo", "tx", property, diskNum, length, root.getLocation()+"/"+root.getFileName());
                MyTreeItem treeItem = new MyTreeItem(newFile);
                String str = disk.readFile((int)root.getDiskNum()) + newFile.getALLData();
                System.out.println("写入前root目录对应磁盘内容:"+disk.readFile((int)this.root.getDiskNum()));
                try{
                    disk.writeFile(root.getDiskNum(), str);
                }catch (Exception e){
                    e.printStackTrace();
                }
                this.myTreeItem.getChildren().add(treeItem);
                //新增
            }
        });
        this.deleteMenu.setOnAction(event -> {});
    }
}

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
            // 如果有儿子  并且 有父亲，则拥有添加顾客菜单栏
            if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
                menuPane = new MenuPane(getTreeItem());
                setContextMenu(menuPane.getAddMenu());
            }else if(!getTreeItem().isLeaf()){
                //根节点
                menuPane = new MenuPane(getTreeItem());
                setContextMenu(menuPane.getAddMenu());
            } else {
                // 其他情况没有菜单栏
                setContextMenu(null);
            }
        }
    }

    // 获取该Item的名字
    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}