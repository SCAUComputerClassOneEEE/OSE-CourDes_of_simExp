package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;


/**
 * 管路已打开的文件
 * @Author: Sky
 * @Date: 2020/8/12 17:02
 */
@Getter
@Setter
public class OpenFileManager {

    private static ObservableList<AFile> openFileList = FXCollections.observableArrayList();
    public static TableView<AFile> openFileTableView = new TableView<>();

    static {
        intiTableView();
    }

    /**
     * 打开一个文件
     * @param aFile
     * @return
     */
    public static boolean openAFile(AFile aFile){
        if (openFileList.size()<5&&!openFileList.contains(aFile)){
            openFileList.add(aFile);
            printOpenFileMassage();
            return true;
        }
        return false;
    }

    /**
     * 关闭一个文件
     * @param aFile
     * @return
     */
    public static boolean closeAFile(AFile aFile){
        if(openFileList.contains(aFile)){
            boolean result = openFileList.remove(aFile);
            printOpenFileMassage();
            return result;
        }
        return false;
    }

    /**
     * 打印当前已打开的文件信息
     */
    public static void printOpenFileMassage(){
        System.out.println("massage:");
        for (AFile each:openFileList){
            System.out.println(each.getALLData());
        }
    }

    /**
     * 初始化"已打开的文件"信息管理表格
     */
    private static void intiTableView(){
        openFileTableView.setItems(openFileList);

        TableColumn<AFile,String> fileLocation = new TableColumn<>("文件路径");
        fileLocation.setCellValueFactory(new PropertyValueFactory<>("fileLocation"));

        TableColumn<AFile,Character> property = new TableColumn<>("文件属性");
        property.setCellValueFactory(new PropertyValueFactory<>("property"));

        TableColumn<AFile,Character> diskNum = new TableColumn<>("起始盘块号");
        diskNum.setCellValueFactory(new PropertyValueFactory<>("diskNum"));

        TableColumn<AFile,Character> length = new TableColumn<>("文件长度");
        length.setCellValueFactory(new PropertyValueFactory<>("length"));

        TableColumn<AFile,Character> openType = new TableColumn<>("操作类型");
        openType.setCellValueFactory(new PropertyValueFactory<>("openType"));

        TableColumn<AFile,String> readPointer = new TableColumn<>("读指针");
        TableColumn<AFile,Integer> rDiskBlockNum = new TableColumn<>("块号");
        rDiskBlockNum.setCellValueFactory(new PropertyValueFactory<>("rPointerBlockNum"));
        TableColumn<AFile,Integer> rPointerLocation = new TableColumn<>("块内地址");
        rPointerLocation.setCellValueFactory(new PropertyValueFactory<>("rPointerLocation"));
        readPointer.getColumns().addAll(rDiskBlockNum,rPointerLocation);

        TableColumn<AFile,String> writPointer = new TableColumn<>("写指针");
        TableColumn<AFile,Integer> wDiskBlockNum = new TableColumn<>("块号");
        wDiskBlockNum.setCellValueFactory(new PropertyValueFactory<>("wPointerBlockNum"));
        TableColumn<AFile,Integer> wPointerLocation = new TableColumn<>("块内地址");
        wPointerLocation.setCellValueFactory(new PropertyValueFactory<>("wPointerLocation"));
        writPointer.getColumns().addAll(wDiskBlockNum,wPointerLocation);

        openFileTableView.getColumns().addAll(fileLocation,property,diskNum,length,openType,readPointer,writPointer);

    }


}
