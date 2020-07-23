package com.SCAUComputerClassOneEEE.OSEC;

import com.SCAUComputerClassOneEEE.OSEC.utils.TaskThreadPools;

public class simExpApp {
    public static void main(String[] args) {
        String str = "TaskThreadPools_TaskThreadPools_TaskThreadPools_TaskThreadPools_TaskThreadPools_TaskThreadPools_1234";
        int preBlockSize = str.length()/32;
        for (int i = 0; i < preBlockSize + 1; i ++){
            System.out.println(i);
            String wStr;
            if (i != preBlockSize) wStr = str.substring(i*32, i*32+32);
            else {
                wStr = str.substring(i * 32);
            }
            System.out.println(wStr);
        }
    }
}
