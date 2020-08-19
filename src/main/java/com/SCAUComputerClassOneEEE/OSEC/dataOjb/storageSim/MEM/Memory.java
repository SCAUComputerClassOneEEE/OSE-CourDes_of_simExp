package com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM;

import com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim.PCB;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * 内存模拟
 * @author best lu
 * @since 2020/08/15
 */
@Getter
@Setter
public class Memory {

    public static final int PCB_SIZE = 10;
    public static final int USER_MEMORY_AREA_SIZE = 512;
    private static final List<PCB> pcbList = new ArrayList<>(PCB_SIZE);
    private static final MAT mat = new MAT();
    private char[] userMemoryArea = new char[USER_MEMORY_AREA_SIZE];

    /**
     * 分配
     * @param size 大小
     */
    public void malloc(int size){

    }

    /**
     * 回收
     */
    public void recovery(){

    }

    /**
     * 维护
     */
    public void maintain(){

    }

    /**
     * 内存分配表
     */
    @Getter
    @Setter
    private static class MAT{
        /*
          Free List <pointer,length>
          pointer为起始地址，length为空闲块长度
         */
        final List<FreeBlock> MAT_cont = new ArrayList<>();
        @Getter
        @Setter
        private static class FreeBlock{
            int pointer;
            int length;
            FreeBlock(int pointer,int length){
                this.length = length;
                this.pointer = pointer;
            }
        }
        MAT(){
            MAT_cont.add(new FreeBlock(0,USER_MEMORY_AREA_SIZE));
        }

        int malloc_MAT(int length){
            //
            return 0;
        }
        int recovery_MAT(int pointer){
            //
            return 0;
        }

        /**
         * 首次适配
         * 空闲区链表结构：空闲区按 起始地址递增顺序排列。
         * 分配时，从链首开始查找，从第一个满足要求的空闲区中划分出进程需要的大小并分配，其余部分作为一个新空闲区。
         * @param size 请求空间大小
         * @return 返回起始地址 -1 表示没有足够大的空闲块。
         */
        int distribution_MAT_FF(int size){
            int retPointer = -1;
            for (FreeBlock iFreeBlock : MAT_cont) {
                if (iFreeBlock.getLength() >= size) {
                    retPointer = iFreeBlock.pointer;
                    if (iFreeBlock.getLength() > size){
                        FreeBlock newFreeBlock = new FreeBlock(retPointer + size,
                                iFreeBlock.getLength() - size);
                        MAT_cont.add(MAT_cont.indexOf(iFreeBlock),newFreeBlock);
                    }
                    MAT_cont.remove(iFreeBlock);
                }
            }
            return retPointer;
        }
        void distribution_MAT_NF(){
            //下次适配
        }
        void distribution_MAT_BF(){
            //最佳适配
        }
        void distribution_MAT_WF(){
            //最差适配
        }
    }
}
