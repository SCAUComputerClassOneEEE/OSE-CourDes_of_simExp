package com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * 内存模拟
 * @author best lu
 * @since 2020/08/15
 */
@Data
public class Memory {

    public static final int PCB_SIZE = 10;
    public static final int USER_MEMORY_AREA_SIZE = 512;

    private static final List<PCB> pcbList = new ArrayList<>(PCB_SIZE);
    private static final MAT mat = new MAT();

    private char[] userMemoryArea = new char[USER_MEMORY_AREA_SIZE];

    /**
     *
     * @param size 分配大小
     * @param exeChars 写入数据
     * @throws Exception 内存已满
     */
    public void malloc(int size,char[] exeChars) throws Exception {
        int pointer = mat.malloc_MAT(size);
        if (pointer == -1) {
            maintain();
            pointer = mat.malloc_MAT(size);
        }
        if (pointer == -1) throw new Exception("The memory is full");
        if (size - pointer >= 0)
            System.arraycopy(exeChars, 0, userMemoryArea, pointer, size - pointer);
    }

    /**
     *
     * @param pointer 被回收的进程指针
     * @throws Exception 进程不存在
     */
    public void recovery(int pointer) throws Exception {
        MAT.ProcessBlock thisProcessBlock = MAT.ProcessBlock.screen(mat.getMAT_OccupyCont(),pointer);
        if (thisProcessBlock == null) throw new Exception("PROCESS NOT EXIST");
        mat.recovery_MAT(pointer,thisProcessBlock.getLength());
    }

    /**
     * 维护
     */
    public void maintain(){

    }

    /**
     * 内存分配表
     */
    @Data
    private static class MAT{
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
        private static class FreeBlock{
            int pointer;
            int length;
            FreeBlock(int pointer,int length){
                this.length = length;
                this.pointer = pointer;
            }
        }
        @Data
        private static class ProcessBlock{
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

        void recovery_MAT(int pointer,int length) throws Exception {
            if (MAT_OccupyCont.size() == 0) throw new Exception("NO PROCESS");
            if (MAT_OccupyCont.size() == 1){
                MAT_FreeCont.clear();
                MAT_OccupyCont.clear();
                MAT_FreeCont.add(new FreeBlock(0,USER_MEMORY_AREA_SIZE));
                return;
            }
            MAT_OccupyCont.removeIf(processBlock -> processBlock.getPointer() == pointer);
            int lastIndex = MAT_FreeCont.size() - 1;
            FreeBlock firstFree = MAT_FreeCont.get(0);
            FreeBlock lastFree = MAT_FreeCont.get(lastIndex);
            if (firstFree.getPointer() > pointer){
                //回收块在空闲队列的前面
                if (pointer + length < firstFree.getPointer())
                    //RPF
                    MAT_FreeCont.add(0,new FreeBlock(pointer,length));
                else {
                    //RF
                    MAT_FreeCont.remove(0);
                    firstFree.setPointer(pointer);
                    firstFree.setLength(firstFree.getLength() + length);
                    MAT_FreeCont.add(0,firstFree);
                }
            }else if (lastFree.getPointer() < pointer){
                //回收块在空闲队列的后面
                if (lastFree.getPointer() + lastFree.getLength() == pointer){
                    //PFR
                    MAT_FreeCont.remove(lastIndex);
                    lastFree.setLength(lastFree.getLength() + length);
                    MAT_FreeCont.add(lastFree);
                }else
                    //PR
                    MAT_FreeCont.add(new FreeBlock(pointer,length));
            }else{
                //回收块在空闲块的中间
                for (FreeBlock freeBlock : MAT_FreeCont){
                    FreeBlock nextFreeBlock = MAT_FreeCont.get(MAT_FreeCont.indexOf(freeBlock) + 1);
                    if (freeBlock.getPointer() < pointer && pointer < nextFreeBlock.getPointer()){
                        //PRP
                        //FRP
                        //FRF
                        //PRF
                    }
                }
            }
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
                    retPointer = iFreeBlock.pointer;
                    if (iFreeBlock.getLength() > size){
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
