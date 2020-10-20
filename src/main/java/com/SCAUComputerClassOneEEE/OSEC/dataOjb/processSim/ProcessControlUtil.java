package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * 进程控制工具类
 * @Author: Sky
 * @Date: 2020/10/20 11:29
 */
public class ProcessControlUtil {

    public static ArrayList<Color> colors = new ArrayList<Color>(){{add(Color.DEEPSKYBLUE); add(Color.BROWN);
        add(Color.YELLOW);add(Color.TOMATO);add(Color.SILVER);add(Color.TURQUOISE);add(Color.TAN);add(Color.CORAL);
        add(Color.SKYBLUE);add(Color.SNOW);add(Color.GREEN);}};
    /**进程控制原语
     * 进程申请
     * 参数为一个可执行文件对象
     */
    public static void create(AFile aFile){
        int pointer;//内存首空间
        int totalSize = aFile.getDiskContent().toCharArray().length;
        //申请内存
        try {
            pointer = Memory.getMemory().malloc(aFile.getDiskContent().toCharArray());
        }catch (Exception e){
            return;
        }
        //空白进程控制块
        PCB newProcess = new PCB(pointer,totalSize,colors.get(0));
        colors.remove(0);
        //添加进就绪队列并显示结果
        CPU.readyQueue.add(newProcess);
        CPU.allPCB.add(newProcess);
    }

    /**进程控制原语
     * 进程销毁
     */
    public static void destroy(PCB destroyProcess){
        //回收内存空间
        try{
            Memory.getMemory().recovery(destroyProcess.getPointerToMemory());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //回收PCB,运行时会把pcb从就绪队列中拿出来
        colors.add(destroyProcess.getColor());
        CPU.readyQueue.remove(destroyProcess);
        CPU.allPCB.remove(destroyProcess);
    }

    /**进程控制原语
     * 进程阻塞
     */
    public static void block(PCB blockPCB){
        //保存现场
        blockPCB.setNextInstruction(CPU.PC-blockPCB.getPointerToMemory());
        blockPCB.setAX(CPU.getAX());
        //将进程链入对应的阻塞队列
        CPU.blockedQueue.add(blockPCB);
    }


    /**进程控制原语
     * 进程唤醒
     */
    public static void awake(PCB awakePCB){
        //进程唤醒的主要工作是将进程由阻塞队列中摘下，修改进程状态为就绪，然后链入就绪队列
        CPU.blockedQueue.remove(awakePCB);
        CPU.readyQueue.add(awakePCB);
    }


}
