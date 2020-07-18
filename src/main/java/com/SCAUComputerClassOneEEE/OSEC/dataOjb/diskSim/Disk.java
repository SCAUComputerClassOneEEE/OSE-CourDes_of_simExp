package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author best lu
 * @since 2020/7/18 14:00
 */
@Getter
@Setter
public class Disk {

    private static final FAT fat = new FAT();//文件分配表，占两字节，磁盘的 0、1 号
    private static final DiskBlock[] diskBlocks = new DiskBlock[126];//模拟的磁盘数据区，（ 2 ~ 127 号）

    public void recovery(int header){
        fat.recovery_FAT(header);
    }

    public int malloc(String str) throws Exception {
        int strLength = str.length();
        int preBlockSize = (strLength % 64) > 0 ? strLength/64 + 1 : strLength/64;
        int header = fat.malloc_FAT(preBlockSize);
        write(header,str);
        return header;
    }

    public void write(int position,String str) throws Exception {
        /*
         两种情况，新创建或修改。
         1.新创建时，只能通过malloc。
         2.修改时长度变化需要改分配表（检查最后一次写的块在表中是否为-1）
         */
        if (position == 0) throw new Exception("dangerous operation");
        synchronized (diskBlocks[position]){

        }
    }

    public String read(int header){
        List<Integer> fileList = fat.getFileList(header);
        StringBuilder stringBuffer = new StringBuilder();
        for (Integer integer : fileList) {
            stringBuffer.append(diskBlocks[integer].r());
        }
        return stringBuffer.toString();
    }

    public void markDamage(int position){
        fat.mark_FAT(position);
    }

    @Setter
    @Getter
    private static class FAT{

        int frsFreePosition = 2;//记录第一个空闲块索引
        final int[] FAT_cont = new int[128];

        FAT(){
            FAT_cont[0] = -1;
            FAT_cont[1] = -1;
        }

        /**
         * 文件分配表的回收修改
         * @param header 回收的首块
         */
        void recovery_FAT(int header){
            if (header == 0) return;
            if (header == 1) return;
            synchronized (FAT_cont){
                if (header < frsFreePosition) frsFreePosition = header;
                FAT_cont[header] = 0;
                recovery_FAT(FAT_cont[FAT_cont[header]]);
            }
        }

        /**
         * 获得文件分配表中首个空闲块
         * @return 首个空闲块
         */
        int getFreeBlockOrder(){
            int ret = frsFreePosition;
            for (int i = frsFreePosition; i < 128; i++)
                if (FAT_cont[i] == 0) {
                    frsFreePosition = i;
                    break;
                }
            return ret;
        }

        /**
         * 文件分配表的分配内存
         * @param preBlockSize 请求划分的块数
         * @return 文件块头
         */
        int malloc_FAT(int preBlockSize){
            int header = 0;
            for (int i = 1,prePos = 0; i <= preBlockSize; i ++){
                int freeOrder = fat.getFreeBlockOrder();
                if (i ==1) {
                    header = freeOrder;
                }else if(i == preBlockSize){
                    fat.getFAT_cont()[freeOrder] = -1;
                }else{
                    fat.getFAT_cont()[prePos] = freeOrder;
                }
                prePos = freeOrder;
            }
            return header;
        }
        /**
         * 损坏的磁盘块标记
         * @param position 块位置
         */
        void mark_FAT(int position){
            if (position == 0) return;
            if (position == 1) return;
            synchronized (FAT_cont){
                FAT_cont[position] = 254;
            }
        }

        /**
         * 计算文件块数
         * @param header 文件块头
         * @return 文件块数
         */
        int getFileSize(int header){
            if (FAT_cont[header] == -1) return 1;
            else return getFileSize(FAT_cont[FAT_cont[header]]) + 1;
        }

        /**
         * 获取文件流串的索引
         * @param header 文件块头
         * @return 文件流串的索引
         */
        List<Integer> getFileList(int header){
            List<Integer> list = new ArrayList<>();
            list.add(header);
            while(true){
                if (FAT_cont[header] != -1) list.add(FAT_cont[header]);
                else break;
                header = FAT_cont[header];
            }
            return list;
        }
    }

    @Getter
    @Setter
    private static class DiskBlock{

        int order;//块号
        char[] block_cont;//内容
        DiskBlock(int order){
            this.order = order;
        }
        void  w(int cur,char newChar) throws Exception {
            if (cur > 64) throw new Exception("full out of block" + order);
            if (block_cont == null){
                block_cont = new char[64];
            }
            block_cont[cur] = newChar;
        }

        String r() {
            if (block_cont == null) return "";
            return String.copyValueOf(block_cont);
        }
    }
}
