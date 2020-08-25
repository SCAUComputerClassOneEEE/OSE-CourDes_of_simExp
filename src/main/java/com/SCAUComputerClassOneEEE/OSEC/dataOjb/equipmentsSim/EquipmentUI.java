package com.SCAUComputerClassOneEEE.OSEC.dataOjb.equipmentsSim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;

public class EquipmentUI extends JPanel {

    private static Equipment A1, A2, B1, B2, B3, C1, C2, C3;

    private static Timer[] equipTime = new Timer[8];
    private static Timer[] clockTime = new Timer[8];

    public EquipmentUI(){

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "设备管理")); //设置边框
        setLayout(new BorderLayout());    // 设置布局

        this.A1 = new Equipment('A');
        this.A2 = new Equipment('A');
        this.B1 = new Equipment('B');
        this.B2 = new Equipment('B');
        this.B3 = new Equipment('B');
        this.C1 = new Equipment('C');
        this.C2 = new Equipment('C');
        this.C3 = new Equipment('C');

        JLabel name=new JLabel("设备使用");

    }
    /**
     *  应用设备函数
     * @param pName  进程名字
     * @param equipmentName  所需设备名字
     * @param time       所需时间
     * @throws EquipmentBusyException
     */
    public static void applyEquipment(String pName, char equipmentName, int time) throws EquipmentBusyException{
        if(equipmentName == 'A'){
            if(A1.isFree()){
                A1.applyEquipment(time);
                //
                //
            } else if (A2.isFree()){
                A2.applyEquipment(time);
                //
                //
            } else {
                throw new EquipmentBusyException();
            }
        } else if (equipmentName == 'B'){
            if (B1.isFree()){
                B1.applyEquipment(time);
                //
                //
            } else if(B2.isFree()){
                B2.applyEquipment(time);
                //
                //
            } else if(B3.isFree()){
                B3.applyEquipment(time);
                //
                //
            }
        } else if (equipmentName == 'C') {
            if (C1.isFree()){
                C1.applyEquipment(time);
                //
                //
            } else if(C2.isFree()){
                C2.applyEquipment(time);
                //
                //
            } else if (C3.isFree()){
                C3.applyEquipment(time);
            } else {
                throw new EquipmentBusyException();
            }
        }

    }

    /**
     * 检查所需设备是否空闲
     * @param equipmentName 所需设备名字
     * @return
     */
    public static boolean checkEquipment(char equipmentName){
        if(equipmentName == 'A'){
            if(A1.isFree() || A2.isFree()){
                return true;
            }
        } else if (equipmentName == 'B'){
            if (B2.isFree() || B1.isFree() || B3.isFree()){
                return true;
            }
        } else if (equipmentName == 'C'){
            if (C1.isFree() || C2.isFree() || C3.isFree()){
                return true;
            }
        }
        return false;
    }

    public static void setTimerState(String pName, Equipment equipment, int time, /*JRadioButton radio, */ int index ){
        //
        equipTime[index] = new Timer(1000 * time, e -> {
            //
            equipment.setIsFree(true);
            //
            // 内存
            equipTime[index].stop();
            clockTime[index].stop();
        });

        clockTime[index] = new Timer(1000, e -> {
            //
            //
        });

        equipTime[index].start();    //启动 Timer，使它开始向其监听器发送动作事件
        clockTime[index].start();
    }

    public static void main(String[] args){
        JFrame frame=new JFrame();  // 创建设备主面板
        frame.setBounds(new Rectangle(100,100,400,400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new EquipmentUI());
        frame.setVisible(true);
    }



}
