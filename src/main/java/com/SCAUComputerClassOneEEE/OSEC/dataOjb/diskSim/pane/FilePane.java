package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class FilePane extends BorderPane {
    public static SimpleObjectProperty<TreeItem<AFile>> treeNode = new SimpleObjectProperty<>();
    public static FlowPane flowPane = new FlowPane();
    private int count = 0;
    public FilePane(){
        init();
        addListener();
    }

    private void addListener() {
        treeNode.addListener((observable, oldValue, newValue) ->
            FilePane.update(newValue)
        );
    }

    private void init(){
        flowPane.setHgap(5);
        flowPane.setVgap(5);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true); scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        scrollPane.setStyle("-fx-background-color: White;");
        this.setCenter(scrollPane);
    }

    private static Label initPicture(TreeItem<AFile> ti){
        AFile aFile = ti.getValue();
        ImageView iv1 = new ImageView(new Image("file:" + new File("src/main/resources/picture/file.jpg"), 100, 100, true, true));
        ImageView iv2 = new ImageView(new Image("file:" + new File("src/main/resources/picture/folder.jpg"), 100, 100, true, true));
        ImageView iv3 = new ImageView(new Image("file:" + new File("src/main/resources/picture/file1.jpg"), 100, 100, true, true));
        ImageView iv4 = new ImageView(new Image("file:" + new File("src/main/resources/picture/folder1.jpg"), 100, 100, true, true));
        ImageView iv5 = new ImageView(new Image("file:" + new File("src/main/resources/picture/exe.jpg"), 100, 100, true, true));
        ImageView iv6 = new ImageView(new Image("file:" + new File("src/main/resources/picture/exe1.jpg"), 100, 100, true, true));

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
            } else if(e.getButton() == MouseButton.SECONDARY && ti.getValue().isFile()){
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

    public static void update(TreeItem<AFile> newValue){
        try {
            Platform.runLater(()-> flowPane.getChildren().remove(0, flowPane.getChildren().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(newValue.getValue().isDirectory()){
            for(TreeItem<AFile> ti : newValue.getChildren()){
                Platform.runLater(()-> flowPane.getChildren().add(initPicture(ti)));
                //flowPane.getChildren().add(initPicture(ti));
            }
        }
    }

    public static void setTreeNode(TreeItem<AFile> newTreeItem) {
        treeNode.set(newTreeItem);
    }
}
