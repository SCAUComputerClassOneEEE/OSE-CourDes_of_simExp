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

    /**
     *
     * @param str 写入的字符串
     * @return -1表示没有足够空间
     * @throws Exception dangerous operation
     */
    public int malloc(String str) throws Exception {
        int strLength = str.length();

        int preBlockSize = (strLength % 64) > 0 ? strLength/64 + 1 : strLength/64;
        int header = fat.malloc_FAT(preBlockSize);
        if (header == -1) return -1;
        write(header,str);
        return header;
    }

    public void write(int position,String str) throws Exception {
        if (position == 0) throw new Exception("dangerous operation");
        if (fat.getFileSize(position) > 1)
        while (str.length() > 0){
            String element = str.substring(0,64);
            str = str.substring(64);

            synchronized (diskBlocks[position]){
                if (fat.getFAT_cont()[position] == -1){
                    diskBlocks[position].w(element);
                    int newBlock = fat.malloc_FAT(1);



                }else{
                    diskBlocks[position].w(element);
                    position = fat.getFAT_cont()[position];
                }
            }
        }
        if (fat.getFAT_cont()[position] != -1) {
            fat.recovery_FAT(fat.getFAT_cont()[position]);
            fat.getFAT_cont()[position] = -1;
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

    public void markDamage(int position){ fat.mark_FAT(position); }

    @Setter
    @Getter
    private static class FAT{

        int freeBlocks = 126;
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
                /* 计算剩余空闲 */
                freeBlocks ++;
                recovery_FAT(FAT_cont[FAT_cont[header]]);
            }
        }

        /**
         * 获得文件分配表中首个空闲块
         * -1表示已满
         * @return 首个空闲块
         */
        private int getFreeBlockOrder(){
            int ret = frsFreePosition;
            /* 计算剩余空闲 */
            freeBlocks --;
            int i;
            for (i = frsFreePosition; i < 128; i++)
                if (FAT_cont[i] == 0) {
                    frsFreePosition = i;
                    break;
                }
            return ret;
        }

        /**
         * 从文件分配表第一个空闲块开始分配 preBlockSize 份内存
         * @param preBlockSize 请求划分的块数
         * @return 文件块头
         */
        int malloc_FAT(int preBlockSize){
            if (preBlockSize <= 0) return -1;
            if (preBlockSize > freeBlocks) return -1;
            int header = 0;
            for (int i = 1,prePos = 0; i <= preBlockSize; i ++){
                int freeOrder = getFreeBlockOrder();

                if (i ==1) header = freeOrder;
                else FAT_cont[prePos] = freeOrder;

                if(i == preBlockSize) FAT_cont[freeOrder] = -1;
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
            if (FAT_cont[header] == 0) return 0;
            if (FAT_cont[header] == -1) return 1;
            else return getFileSize(FAT_cont[FAT_cont[header]]) + 1;
        }

        /**
         * 获取文件流串的索引链表
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
        void w(String newStr){
            if (block_cont == null){
                block_cont = new char[64];
            }
            block_cont = newStr.toCharArray();
        }
        String r() {
            if (block_cont == null) return "";
            return String.copyValueOf(block_cont);
        }
    }
}
