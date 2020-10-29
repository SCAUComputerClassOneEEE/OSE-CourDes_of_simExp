package com.SCAUComputerClassOneEEE.OSEC.dataModel.storageSim.MEM;

import com.SCAUComputerClassOneEEE.OSEC.controller.MySceneController;
import com.SCAUComputerClassOneEEE.OSEC.dataModel.processSim.PCB;
import javafx.application.Platform;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * 内存模拟
 * @author best lu
 * @since 2020/08/15
 */
public class Memory {

    private volatile static Memory memory;

    public static final int PCB_SIZE = 10;
    public static final int USER_MEMORY_AREA_SIZE = 512;
    public static final int ERROR_RETURN_POINTER = -1;
    @Getter
    private final List<PCB> PCB_LIST;
    @Getter
    private final MAT mat;
    @Getter
    private char[] userMemoryArea;

    private Memory(){
        userMemoryArea = new char[USER_MEMORY_AREA_SIZE];
        Arrays.fill(userMemoryArea,'#');
        mat = new MAT();
        PCB_LIST = new ArrayList<>(PCB_SIZE);

    }

    public static Memory getMemory(){
        if (memory == null){
            synchronized (Memory.class){
                if (memory == null){
                    memory = new Memory();
                }
            }
        }
        return memory;
    }
    /**
     *
     * @param exeChars 写入数据
     * @throws Exception 内存已满
     * @return pointer
     */
    public int malloc(char[] exeChars) throws Exception {
        synchronized (this) {
            int pointer = mat.malloc_MAT(exeChars.length);
            if (pointer == ERROR_RETURN_POINTER) {
                //System.out.println("## compress auto");
                compress();
                pointer = mat.malloc_MAT(exeChars.length);
                if (pointer == ERROR_RETURN_POINTER) throw new Exception("The memory is full");
            }
            if (exeChars.length > 0){
                //System.out.println("## copying into " + pointer + " length: " + (exeChars.length));
                System.arraycopy(exeChars, 0, userMemoryArea, pointer, exeChars.length);
            }

            MySceneController.memoryChange.setValue(MySceneController.memoryChange.getValue()+1);
            //
            return pointer;
        }
    }

    /**
     *
     * @param pointer 被回收的进程指针
     * @throws Exception 进程不存在
     */
    public void recovery(int pointer) throws Exception {
        synchronized (this) {
            MAT.ProcessBlock thisProcessBlock = MAT.ProcessBlock.screen(mat.getMAT_OccupyCont(),pointer);
            if (thisProcessBlock == null) throw new Exception("PROCESS NOT EXIST");
            mat.recovery_MAT(pointer,thisProcessBlock.getLength());
            Platform.runLater(()-> MySceneController.memoryChange.setValue(MySceneController.memoryChange.getValue()+1));
        }
    }

    /**
     * 维护
     */
    public synchronized void compress(){
        synchronized (this) {
            //System.out.println("-------------compression-----------");
            Iterator<MAT.ProcessBlock> processBlockIterator = mat.MAT_OccupyCont.iterator();
            int iProcessLength = 0;
            while(processBlockIterator.hasNext()){
                MAT.ProcessBlock processBlock = processBlockIterator.next();
                if (processBlock.getLength() >= 0)
                    System.arraycopy(userMemoryArea, processBlock.getPointer(), userMemoryArea, iProcessLength, processBlock.getLength());
                iProcessLength += processBlock.getLength();
            }
            mat.getMAT_FreeCont().clear();
            mat.getMAT_FreeCont().add(new MAT.FreeBlock(iProcessLength,mat.totalFreeLength()));
            mat.compressionProcessBlocks();
        }
    }

    public char[] readPChars(int pId){
        return userMemoryArea;
    }

    public void MAT_display(){
        System.out.println();
        System.out.println(mat.getMAT_FreeCont().size());
        for (MAT.FreeBlock f: mat.getMAT_FreeCont()) {
            //System.out.println("-Free p: " + f.getPointer() + ", l: " + f.getLength());
        }
        System.out.println();
        System.out.println(mat.getMAT_OccupyCont().size());
        for (MAT.ProcessBlock p:mat.getMAT_OccupyCont()){
            //System.out.println("-Process p: " + p.getPointer() + ", l: " + p.getLength());
        }
    }

    /**
     * 内存分配表
     */
    @Data
    public static class MAT{
        /*
          Free List <pointer,length>
          pointer为起始地址，length为空闲块长度
         */
        final List<FreeBlock> MAT_FreeCont = new ArrayList<>();
        /*
          Process List <pointer,length>
          pointer为起始地址，length为进程块长度
         */
        final List<ProcessBlock> MAT_OccupyCont = new ArrayList<>();
        @Data
        public static class FreeBlock{
            int pointer;
            int length;
            FreeBlock(int pointer,int length){
                this.length = length;
                this.pointer = pointer;
            }
            int endOfFreePointer(){
                return pointer + length;
            }
        }
        @Data
        public static class ProcessBlock{
            int pointer;
            int length;
            ProcessBlock(int pointer,int length){
                this.length = length;
                this.pointer = pointer;
            }
            static ProcessBlock screen(List<ProcessBlock> MAT_OccupyCont,int pointer){
                for (ProcessBlock processBlock : MAT_OccupyCont){
                    if (processBlock.getPointer() == pointer)
                        return processBlock;
                }
                return null;
            }
        }

        MAT(){
            MAT_FreeCont.add(new FreeBlock(0,USER_MEMORY_AREA_SIZE));
        }

        int malloc_MAT(int length){
            int pPointer = allocate_MAT_FF(length);
            if (pPointer == -1) return pPointer;
            MAT_OccupyCont.add(new ProcessBlock(pPointer,length));
            return pPointer;
        }

        int totalProcessLength(){
            int total = 0;
            for (ProcessBlock processBlock:MAT_OccupyCont) total += processBlock.getLength();
            return total;
        }

        void compressionProcessBlocks(){
            MAT_OccupyCont.get(0).setPointer(0);
            int processLength = 0;
            for (ProcessBlock p:MAT_OccupyCont) {
                processLength += p.length;
                int i = MAT_OccupyCont.indexOf(p) + 1;
                if (i >= MAT_OccupyCont.size()) break;
                MAT_OccupyCont.get(i).setPointer(processLength);
            }
        }

        int totalFreeLength(){
            return USER_MEMORY_AREA_SIZE - totalProcessLength();
        }

        void recovery_MAT(int pointer,int length) throws Exception {
            //System.out.println("-------------recovery---------");
            if (MAT_OccupyCont.size() == 0) throw new Exception("NO PROCESS");
            if (MAT_OccupyCont.size() == 1){
                //System.out.println("    MAT_OccupyCont.size() == 1 is true");
                MAT_FreeCont.clear();
                MAT_OccupyCont.clear();
                MAT_FreeCont.add(new FreeBlock(0,USER_MEMORY_AREA_SIZE));
                return;
            }
            //System.out.println("    MAT_OccupyCont.size() == 1 is false");
            MAT_OccupyCont.removeIf(processBlock -> processBlock.getPointer() == pointer);
            //System.out.println("    remove " + pointer + " successfully");
            //加入新空闲区
            if (pointer > MAT_FreeCont.get(MAT_FreeCont.size() - 1).getPointer()){
                MAT_FreeCont.add(new FreeBlock(pointer,length));
                //System.out.println("    add to end of freeList");
            }
            else for (int i = 0; i < MAT_FreeCont.size(); i++)
                if (MAT_FreeCont.get(i).getPointer() > pointer){
                    MAT_FreeCont.add(i,new FreeBlock(pointer,length));
                    //System.out.println("    add to " + i + " of freeList");
                    break;
                }

            //MAT_FreeCont.size() > 1 时，合并所有相邻区
            for (int i = 0; i < MAT_FreeCont.size() - 1; i++) {
                FreeBlock currFreeBlock = MAT_FreeCont.get(i);
                FreeBlock nextFreeBlock = MAT_FreeCont.get(i + 1);
                //判断是否相邻
                if (currFreeBlock.endOfFreePointer() == nextFreeBlock.getPointer()){
                    //System.out.println("    currFreeBlock.endOfFreePointer() == nextFreeBlock.getPointer() is true");
                    //把下一个相邻的空闲区合到当前空闲区
                    currFreeBlock.setLength(currFreeBlock.getLength() + nextFreeBlock.getLength());
                    //System.out.println("    Merger of " + i + " and " + (i + 1));
                    MAT_FreeCont.remove(i + 1);
                    i--;//防止三个及以上的相邻空闲区
                }
            }
            //System.out.println("-------------end--------------");
        }

        /**
         * 首次适配
         * 空闲区链表结构：空闲区按 起始地址递增顺序排列。
         * 分配时，从链首开始查找，从第一个满足要求的空闲区中划分出进程需要的大小并分配，其余部分作为一个新空闲区。
         * @param size 请求空间大小
         * @return 返回起始地址 -1 表示没有足够大的空闲块。
         */
        int allocate_MAT_FF(int size){
            int retPointer = -1;
            for (FreeBlock iFreeBlock : MAT_FreeCont) {
                if (iFreeBlock.getLength() >= size) {
                    //找到第一个满足要求的空闲区
                    retPointer = iFreeBlock.getPointer();
                    if (iFreeBlock.getLength() > size){
                        //其余部分作为一个新空闲区
                        FreeBlock newFreeBlock = new FreeBlock(retPointer + size,
                                iFreeBlock.getLength() - size);
                        MAT_FreeCont.add(MAT_FreeCont.indexOf(iFreeBlock),newFreeBlock);
                    }
                    MAT_FreeCont.remove(iFreeBlock);
                }
            }
            return retPointer;
        }
    }
}
