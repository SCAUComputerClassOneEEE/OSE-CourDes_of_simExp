package com.SCAUComputerClassOneEEE.OSEC.utils;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @Author: Sky
 * @Date: 2020/10/25 21:05
 */
public class ButtonUtil {
    //创建一个Button，button的文字为函数参数
    public static void decorateButton(Button button,String buttonName) {
        //Button button = new Button();
        //button.setId(buttonName);
        button.setPadding(new Insets(10, 10, 10, 10));
        //button.setText(buttonName);
        button.setStyle("-fx-background-color:White;");
        button.setGraphic(new ImageView(new Image("file:" +"src/main/resources/"+buttonName+".png",
                30, 30,
                true, true)));
        //button.setTooltip(new Tooltip(buttonName));
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color:#e6e6e6;");
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color:White;");
        });
        //return button;
    }
}