package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.pane;


import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataService.DiskSimService;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TextInputBox {
    private GridPane root;
    private Label tipLabel;
    private Label fileName;
    private Label warmLabel;
    private Button confirmButton;
    private Button cancelButton;
    private TextField fieldFileName;
    private MenuPane menuPane;
    private Stage primaryStage = new Stage();
    //服务层
    private DiskSimService diskSimService = new DiskSimService();

    public TextInputBox(TreeItem<AFile> myTreeItem, int type){
        init(myTreeItem,type);
    }

    //判断有无“$”、 “.”、 “/”字符
    private boolean judge(String string){
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if(c == '$' || c == '.' || c == '/' || c == '#')
                return true;
        }
        return false;
    }

    public void show(){
        primaryStage.show();
    }

    private void init(TreeItem<AFile> myTreeItem, int type){
        root = new GridPane();

        root.setPadding(new Insets(20));
        root.setHgap(25);
        root.setVgap(15);

        tipLabel = new Label("请输入文件\\目录名:");
        root.add(tipLabel, 0, 0, 2, 1);

        fileName = new Label("文件名");
        fieldFileName = new TextField();
        warmLabel = new Label();//警告
        confirmButton = new Button("确认");
        cancelButton = new Button("取消");

        GridPane.setHalignment(fileName, HPos.RIGHT);
        root.add(fileName, 0, 1);

        GridPane.setHalignment(warmLabel, HPos.LEFT);
        root.add(warmLabel, 0, 2, 2,1);

        // 文本的水平对齐
        GridPane.setHalignment(fieldFileName, HPos.LEFT);
        root.add(fieldFileName, 1, 1);

        // 按钮的水平对齐
        GridPane.setHalignment(confirmButton, HPos.LEFT);
        root.add(confirmButton, 1, 3);
        GridPane.setHalignment(cancelButton, HPos.RIGHT);
        root.add(cancelButton, 1, 3);

        Scene scene = new Scene(root, 350, 200);

        primaryStage.setScene(scene);


        fieldFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length() > 3){
                warmLabel.setText("文件名长度要求小于等于3");
                confirmButton.setDisable(true);
            }else if(judge(newValue)){
                warmLabel.setText("文件不能包含“$”、 “.”、 “/”、“#”字符");
                confirmButton.setDisable(true);
            }else{
                warmLabel.setText("");
                confirmButton.setDisable(false);
            }
        });
        confirmButton.setOnAction(event -> {
            primaryStage.close();
            //2为可执行文件，1为文本文件，0为目录
            if(type == 2) diskSimService.createFile(myTreeItem, fieldFileName.getText(), 16);
            else if(type == 1) diskSimService.createFile(myTreeItem, fieldFileName.getText(), 4);
            else if(type == 0) diskSimService.createFile(myTreeItem, fieldFileName.getText(), 8);
        });
        cancelButton.setOnAction(event -> primaryStage.close());
    }
}
