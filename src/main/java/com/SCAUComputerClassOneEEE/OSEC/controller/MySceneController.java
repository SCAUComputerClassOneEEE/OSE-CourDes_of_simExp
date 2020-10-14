package com.SCAUComputerClassOneEEE.OSEC.controller;


import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.FileTree;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.FilePane;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import com.SCAUComputerClassOneEEE.OSEC.op.Terminal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author: Sky
 * @Date: 2020/10/14 21:30
 */

public class MySceneController implements Initializable {

    @FXML
    private Button button;

    @FXML
    private Tab file;

    @FXML
    public void test(){


    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileTree fileTree = FileTree.getFileTree();
        BorderPane root = new BorderPane();

        Terminal terminal = new Terminal(fileTree);

        root.setRight(new FilePane());
        root.setLeft(fileTree.getVBox());
        file.setContent(root);
        root.setCenter(terminal.textArea);
        root.setBottom(OpenFileManager.openFileTableView);
    }
}
