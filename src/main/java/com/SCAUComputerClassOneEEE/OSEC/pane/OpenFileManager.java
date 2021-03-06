package com.SCAUComputerClassOneEEE.OSEC.pane;

import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.AFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;


/**
 * 管理已打开的文件
 * @Author: Sky
 * @Date: 2020/8/11 17:02
 */
@Getter
@Setter
public class OpenFileManager {

    private static ObservableList<AFile.AOpenFile> openFileList = FXCollections.observableArrayList();
    public static TableView<AFile.AOpenFile> openFileTableView = new TableView<>();

    static {
        intiTableView();
    }

    /**
     * 打开一个文件
     * @param aFile
     * @return
     */
    public static boolean openAFile(AFile aFile,String operation_type){
        //可能已打开
        for(AFile.AOpenFile each:openFileList){
            if (each.getAbsoluteLocation().equals(aFile.getAbsoluteLocation())){
                return false;
            }
        }
        //最多打开5个
        if (openFileList.size()>=5)
            return false;
        //直接打开
        AFile.AOpenFile aOpenFile = new AFile.AOpenFile(aFile);
        //设置打开方式(读/写)
        aOpenFile.setOpenType(operation_type);
        openFileList.add(aOpenFile);
        printOpenFileMassage();
        return true;

    }

    /**
     * 关闭一个文件
     * @param aFile
     * @return
     */
    public static boolean closeAFile(AFile aFile){
        for (AFile.AOpenFile each:openFileList){
            if(each.getAbsoluteLocation().equals(aFile.getAbsoluteLocation())){
                boolean result = openFileList.remove(each);
                each.setOpenType("关闭");
                printOpenFileMassage();
                return result;
            }
        }
        return false;
    }

    public static boolean contain(AFile aFile){
        for (AFile.AOpenFile each:openFileList){
            if(each.getAbsoluteLocation().equals(aFile.getAbsoluteLocation())){
                return true;
            }
        }
        return false;
    }
    /**
     * 打印当前已打开的文件信息
     */
    public static void printOpenFileMassage(){
        System.out.printf("当前已打开文件%d个\n",openFileList.size());
        for (AFile.AOpenFile each:openFileList){
            System.out.println(each.toString());
        }
    }

    public static AFile.AOpenFile getOpenFile(AFile aFile){
        for (AFile.AOpenFile each:openFileList){
            if (each.getAbsoluteLocation().equals(aFile.getAbsoluteLocation()))
                return each;
        }
        return null;
    }
    /**
     * 初始化"已打开的文件"信息管理表格
     */
    private static void intiTableView(){
        openFileTableView.setStyle("-fx-background-color: WHITE");
        openFileTableView.setItems(openFileList);

        TableColumn<AFile.AOpenFile,String> absoluteLocation = new TableColumn<>("文件路径");
        absoluteLocation.setPrefWidth(100);
        absoluteLocation.setCellValueFactory(new PropertyValueFactory<>("absoluteLocation"));

        TableColumn<AFile.AOpenFile,Integer> property = new TableColumn<>("文件属性");
        property.setCellValueFactory(new PropertyValueFactory<>("property"));

        TableColumn<AFile.AOpenFile,Integer> diskNum = new TableColumn<>("起始盘块号");
        diskNum.setPrefWidth(100);
        diskNum.setCellValueFactory(new PropertyValueFactory<>("diskNum"));

        TableColumn<AFile.AOpenFile,Integer> length = new TableColumn<>("文件长度");
        length.setCellValueFactory(new PropertyValueFactory<>("length"));

        TableColumn<AFile.AOpenFile,Character> openType = new TableColumn<>("操作类型");
        openType.setCellValueFactory(new PropertyValueFactory<>("openType"));

        TableColumn<AFile.AOpenFile,String> readPointer = new TableColumn<>("读指针");
        TableColumn<AFile.AOpenFile,Integer> rDiskBlockNum = new TableColumn<>("块号");
        rDiskBlockNum.setCellValueFactory(new PropertyValueFactory<>("rPointerBlockNum"));
        TableColumn<AFile.AOpenFile,Integer> rPointerLocation = new TableColumn<>("块内地址");
        rPointerLocation.setCellValueFactory(new PropertyValueFactory<>("rPointerLocation"));
        readPointer.getColumns().addAll(rDiskBlockNum,rPointerLocation);

        TableColumn<AFile.AOpenFile,String> writPointer = new TableColumn<>("写指针");
        TableColumn<AFile.AOpenFile,Integer> wDiskBlockNum = new TableColumn<>("块号");
        wDiskBlockNum.setCellValueFactory(new PropertyValueFactory<>("wPointerBlockNum"));
        TableColumn<AFile.AOpenFile,Integer> wPointerLocation = new TableColumn<>("块内地址");
        wPointerLocation.setCellValueFactory(new PropertyValueFactory<>("wPointerLocation"));
        writPointer.getColumns().addAll(wDiskBlockNum,wPointerLocation);

        openFileTableView.getColumns().addAll(absoluteLocation,property,diskNum,length,openType,readPointer,writPointer);

    }


}
