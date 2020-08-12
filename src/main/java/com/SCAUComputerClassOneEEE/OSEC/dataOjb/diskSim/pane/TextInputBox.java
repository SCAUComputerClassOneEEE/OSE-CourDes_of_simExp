package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane;


import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataService.impl.DiskSimService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private Button button;
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
            if(c == '$' || c == '.' || c == '/')
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
        button = new Button("确认");

        GridPane.setHalignment(fileName, HPos.RIGHT);
        root.add(fileName, 0, 1);

        GridPane.setHalignment(warmLabel, HPos.LEFT);
        root.add(warmLabel, 0, 2, 2,1);

        // 文本的水平对齐
        GridPane.setHalignment(fieldFileName, HPos.LEFT);
        root.add(fieldFileName, 1, 1);

        // 按钮的水平对齐
        GridPane.setHalignment(button, HPos.RIGHT);
        root.add(button, 1, 3);

        Scene scene = new Scene(root, 350, 200);

        primaryStage.setScene(scene);


        fieldFileName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() > 3){
                    warmLabel.setText("文件名长度要求小于等于3");
                    button.setDisable(true);
                }else if(judge(newValue)){
                    warmLabel.setText("文件不能包含“$”、 “.”、 “/”字符");
                    button.setDisable(true);
                }else{
                    warmLabel.setText("");
                    button.setDisable(false);
                }
            }
        });
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                //1为文件，0为目录
                if(type == 1) diskSimService.createFile(myTreeItem, fieldFileName.getText());
                else if(type == 0) diskSimService.createDirectory(myTreeItem, fieldFileName.getText());
            }
        });
    }
}
