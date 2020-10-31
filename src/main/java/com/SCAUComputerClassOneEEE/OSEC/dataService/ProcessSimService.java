package com.SCAUComputerClassOneEEE.OSEC.dataService;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.devicesSim.Device;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim.FileModel.AFile;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.CPU;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.Clock;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM.Memory;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * 进程控制服务类
 * @Author: Sky
 * @Date: 2020/10/20 11:29
 */
public class ProcessSimService {
    private static final ProcessSimService processSimService = new ProcessSimService();

    //颜色库，给PCB赋颜色值
    private static final ArrayList<Color> colors = new ArrayList<Color>(){{add(Color.DEEPSKYBLUE); add(Color.YELLOWGREEN);
        add(Color.YELLOW);add(Color.TOMATO);add(Color.SILVER);add(Color.TURQUOISE);add(Color.TAN);add(Color.CORAL);
        add(Color.SKYBLUE);add(Color.PINK);add(Color.LIGHTSTEELBLUE);add(Color.CYAN);}};

    public static ProcessSimService getProcessSimService(){
        return ProcessSimService.processSimService;
    }

    public static ArrayList<Color> getColors(){
        return ProcessSimService.colors;
    }


    /**进程控制原语
     * 进程申请
     * 参数为一个可执行文件对象
     */
    public void create(AFile aFile){
        if (CPU.readyQueue.size() + CPU.blockedQueue.size() + (CPU.curPCB == null ? 0 : 1) >= 10){
            //System.out.println("系统最多存在10个进程");
            return;
        }
        int pointer;//内存首空间
        int totalSize;
        try {
            totalSize = aFile.getDiskContent().toCharArray().length;
        }catch (Exception e){
            //System.out.println("空指针");
            return;
        }
        //申请内存
        try {
            pointer = Memory.getMemory().malloc(aFile.getDiskContent().toCharArray());
        }catch (Exception e){
            return;
        }
        Color newProcessColor = randomColor();
        //空白进程控制块
        PCB newProcess = new PCB(pointer,totalSize,newProcessColor,(int) Clock.getClock().getCpuRanTime(),aFile.getAbsoluteLocation().substring(5)+".ex");
        colors.remove(newProcessColor);
        //添加进就绪队列并显示结果
        CPU.readyQueue.add(newProcess);
        CPU.allPCB.add(newProcess);
    }

    /**进程控制原语
     * 进程销毁
     */
    public void destroy(PCB destroyProcess){
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
    public void block(PCB blockPCB){
        //保存现场
        blockPCB.setNextInstruction(CPU.PC-blockPCB.getPointerToMemory());
        blockPCB.setAX(CPU.AX);
        blockPCB.setRemainInstructions(blockPCB.getTotalSize()-blockPCB.getNextInstruction());
        blockPCB.setProgressRate(((double)blockPCB.getTotalSize()-blockPCB.getRemainInstructions())/blockPCB.getTotalSize());
        //将进程链入对应的阻塞队列
        CPU.blockedQueue.add(blockPCB);
    }

    /**进程控制原语
     * 进程唤醒
     */
    public void awake(PCB awakePCB){
        //进程唤醒的主要工作是将进程由阻塞队列中摘下，修改进程状态为就绪，然后链入就绪队列
        CPU.blockedQueue.remove(awakePCB);
        CPU.readyQueue.add(awakePCB);
    }

    /**
     * 随机返回还可用的颜色
     * @return
     */
    private Color randomColor(){
            int index = (int)(Math.random()*colors.size());
            return colors.get(index);
    }

}
