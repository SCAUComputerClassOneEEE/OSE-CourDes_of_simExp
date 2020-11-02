package com.SCAUComputerClassOneEEE.OSEC.pane;

import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.AFile;
import com.SCAUComputerClassOneEEE.OSEC.starter.Starter;
import com.SCAUComputerClassOneEEE.OSEC.utils.CompileUtil;
import com.SCAUComputerClassOneEEE.OSEC.data_service.ProcessSimService;
import com.SCAUComputerClassOneEEE.OSEC.data_center.OSDataCenter;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Getter
@Setter
public class FilePane extends BorderPane {
    public static SimpleObjectProperty<TreeItem<AFile>> treeNode = new SimpleObjectProperty<>();
    public static FlowPane flowPane = new FlowPane();
    private int count = 0;
    public FilePane(){
        init();
        addListener();
        flowPane.setStyle("-fx-background-color:WHITE");
    }

    private void addListener() {
        treeNode.addListener((observable, oldValue, newValue) ->
            FilePane.update(newValue)
        );
    }

    //初始化面板
    private void init(){
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true); scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        scrollPane.setStyle("-fx-background-color: White;");
        this.setCenter(scrollPane);
    }

    //初始化图片
    private static Label initPicture(TreeItem<AFile> ti) throws URISyntaxException, MalformedURLException {
        AFile aFile = ti.getValue();
        ImageView iv1 = new ImageView(new Image(Starter.class.getClassLoader().getResource("file.jpg").toURI().toURL().toString(), 100, 100, true, true));
        ImageView iv2 = new ImageView(new Image(Starter.class.getClassLoader().getResource("folder.jpg").toURI().toURL().toString(), 100, 100, true, true));
        ImageView iv3 = new ImageView(new Image(Starter.class.getClassLoader().getResource("file1.jpg").toURI().toURL().toString(), 100, 100, true, true));
        ImageView iv4 = new ImageView(new Image(Starter.class.getClassLoader().getResource("folder1.jpg").toURI().toURL().toString(), 100, 100, true, true));
        ImageView iv5 = new ImageView(new Image(Starter.class.getClassLoader().getResource("exe.jpg").toURI().toURL().toString(), 100, 100, true, true));
        ImageView iv6 = new ImageView(new Image(Starter.class.getClassLoader().getResource("exe1.jpg").toURI().toURL().toString(), 100, 100, true, true));

        Label label = new Label();
        label.setPickOnBounds(true);
        label.setGraphicTextGap(10);
        label.setPadding(new Insets(10, 10, 10, 10));
        label.setContentDisplay(ContentDisplay.TOP);
        label.setMaxSize(120,150);
        label.setMinSize(120,150);

        if(aFile.isDirectory())
            label.setGraphic(iv2);
        else if(aFile.isFile())
            label.setGraphic(iv1);
        else label.setGraphic(iv5);

        label.setText(aFile.getFileName() +"." +aFile.getType());
        label.setOnMouseEntered(e -> {
            if(aFile.isDirectory())
                label.setGraphic(iv4);
            else if(aFile.isFile())
                label.setGraphic(iv3);
            else label.setGraphic(iv6);
        });
        label.setOnMouseExited(e -> {
            if(aFile.isDirectory())
                label.setGraphic(iv2);
            else if(aFile.isFile())
                label.setGraphic(iv1);
            else label.setGraphic(iv5);
        });
        label.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && aFile.isFile()){
                FileTextField fileTextField = new FileTextField(ti);
                fileTextField.show();
            } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && aFile.isDirectory()){
                update(ti);
            } else if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && aFile.isExeFile()){
                tipBox(ti);
            }
            if(e.getButton() == MouseButton.SECONDARY && ti.getValue().isFile()){
                MenuPane menuPane = new MenuPane(ti);
                menuPane.getCreateExeFileMenu().setDisable(true);
                menuPane.getCreateDirectoryMenu().setDisable(true);
                menuPane.getCreateFileMenu().setDisable(true);
                menuPane.getCreateProcessMenu().setDisable(true);
                label.setContextMenu(menuPane.getAddMenu());
            } else if(e.getButton() == MouseButton.SECONDARY && ti.getValue().isDirectory()){
                MenuPane menuPane = new MenuPane(ti);
                menuPane.getOpenMenu().setDisable(true);
                menuPane.getCreateProcessMenu().setDisable(true);
                label.setContextMenu(menuPane.getAddMenu());
            } else if(e.getButton() == MouseButton.SECONDARY && ti.getValue().isExeFile()){
                MenuPane menuPane = new MenuPane(ti);
                menuPane.getCreateExeFileMenu().setDisable(true);
                menuPane.getCreateDirectoryMenu().setDisable(true);
                menuPane.getCreateFileMenu().setDisable(true);
                label.setContextMenu(menuPane.getAddMenu());
            }
        });
        return label;
    }

    //更新节点
    public static void update(TreeItem<AFile> newValue){
        try {
            Platform.runLater(()-> flowPane.getChildren().remove(0, flowPane.getChildren().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(newValue.getValue().isDirectory()){
            for(TreeItem<AFile> ti : newValue.getChildren()){
                Platform.runLater(()-> {
                    try {
                        flowPane.getChildren().add(initPicture(ti));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                });
                //flowPane.getChildren().add(initPicture(ti));
            }
        } else {
            TextArea textArea = new TextArea();
            textArea.setPrefSize(2* OSDataCenter.width/5,3* OSDataCenter.height/5);
            textArea.setEditable(false);
            if(newValue.getValue().isFile())
                textArea.setText(newValue.getValue().getDiskContent());
            else if(newValue.getValue().isExeFile()) {
                StringBuilder contents = new StringBuilder();
                char[] chars = newValue.getValue().getDiskContent().toCharArray();
                for (char c:chars)
                    contents.append(CompileUtil.decompile(c) + ";" + (char)10);
                textArea.setText(contents.toString());
            }
            Platform.runLater(()-> flowPane.getChildren().add(textArea));
        }
    }

    public static void setTreeNode(TreeItem<AFile> newTreeItem) {
        treeNode.set(newTreeItem);
    }

    public static void tipBox(TreeItem<AFile> ti){
        Stage stage = new Stage();

        //锁定当前提示框
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("提示");
        stage.setMinWidth(300);
        stage.setMaxHeight(300);
        Label tipLabel = new Label("是否创建进程");
        Button closeButton = new Button("取消");
        Button createProcess = new Button(("确定"));
        closeButton.setOnAction(e ->stage.close());
        createProcess.setOnAction(e -> {
            ProcessSimService.getProcessSimService().create(ti.getValue());
            stage.close();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(createProcess, closeButton);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: White");
        vBox.getChildren().addAll(tipLabel, hBox);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene (vBox);
        stage.setScene(scene);
        stage.show();
    }
}
