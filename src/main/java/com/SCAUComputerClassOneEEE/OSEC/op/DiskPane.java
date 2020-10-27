package com.SCAUComputerClassOneEEE.OSEC.op;

import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.Disk;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Data;

@Data
public class DiskPane {

    private volatile static DiskPane diskPane;

    private BlockPane[] blockPanes;
    private GridPane root;
    private static int rowNum = 8;
    private static int colNum = 16;

    public DiskPane(){
        blockPanes = new BlockPane[128];
        root = new GridPane();
        root.setPadding(new Insets(5));
        root.setHgap(rowNum);
        root.setVgap(colNum);

        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                int index = i * colNum + j;
                blockPanes[index] = new BlockPane(index);
                if(index == 0 || index == 1 || index == 2) {
                    blockPanes[index].getRectangle().setFill(Color.BLUE);
                    blockPanes[index].setType(1);
                }
                root.add(blockPanes[index], j, i+3, 1, 1);
            }
        }
        root.setStyle("-fx-background-color: WHITE");
    }

    public static DiskPane getDiskPane(){
        if (diskPane == null){
            synchronized (Disk.class){
                if (diskPane == null){
                    diskPane = new DiskPane();
                }
            }
        }
        return diskPane;
    }

    public void updateType(int index){
        if(index <= 1 || index > 127)
            return;
        if(blockPanes[index].getType() == 0)
            blockPanes[index].setUse();
        else if(blockPanes[index].getType() == 1)
            blockPanes[index].setFree();
    }

    @Data
    public class BlockPane extends StackPane {
        private Rectangle rectangle;
        private Label label;
        private int index;
        private int type;

        BlockPane(int index){
            this.rectangle = new Rectangle(25, 20, Color.ALICEBLUE);
            this.rectangle.setStroke(Color.BLACK);
            this.index = index;
            this.type = 0;
            this.label = new Label(index + "");
            this.getChildren().addAll(this.rectangle, this.label);
        }

        public void setUse(){
            rectangle.setFill(Color.BLUE);
            type = 1;
        }
        public void setFree(){
            rectangle.setFill(Color.WHITE);
            type = 0;
        }
        public void Damage(){
            rectangle.setFill(Color.RED);
            type = -1;
        }
    }
}
