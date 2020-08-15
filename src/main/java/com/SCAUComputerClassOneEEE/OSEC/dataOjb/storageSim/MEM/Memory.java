package com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        final List<Map<Integer,Integer>> MAT_cont = new ArrayList<>();//Free Queue <pointer,length>

        int malloc_MAT(int length){
            //
            return 0;
        }
        int recovery_MAT(int pointer){
            //
            return 0;
        }

        void maintain_MAT_FF(){
            //首次适配
        }
        void maintain_MAT_NF(){
            //下次适配
        }
        void maintain_MAT_BF(){
            //最佳适配
        }
        void maintain_MAT_WF(){
            //最差适配
        }
    }
}
