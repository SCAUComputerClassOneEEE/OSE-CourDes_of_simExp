package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Disk {
    /**
     * 假设模拟磁盘有 128 个物理块，每个物理块大小为 64 字节。盘块的块号从 0 编起。
     */
    private static final FAT fat = new FAT();//文件分配表，占两字节，磁盘的 0、1 号
    private static final DiskBlock[] diskBlocks = new DiskBlock[126];//模拟的磁盘数据区，（ 2 ~ 127 号）

    public void recovery(int header){
        fat.recovery_FAT(header);
    }

    public void malloc(int size){
        int freeOrder = fat.getFreeBlockOrder();

    }

    public void write(int position){

    }

    public int[] read(int header){
        return null;
    }

    public void markDamage(int position){
        fat.mark_FAT(position);
    }

    @Setter
    @Getter
    private static class FAT{
        /**
         * 因为盘块有 128 块，所以文件分配表有 128 项。
         * 文件分配表占用了磁盘的 0 块和 1 块，这两块就不能作其它用处。
         * 若一个盘块是某个文件的最后一块，填写“ -1 ”表示文件结束。
         * 用“ 0 ”表示磁盘盘块空闲。
         * 用“ 254 ”表示该盘块损坏不能使用。
         */
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
         *
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
    }

    @Getter
    @Setter
    private static class DiskBlock{
        /**
         * 物理块大小为 64 字节
         */
        int order;
        int[] block_cont;
        void w(){

        }
        void r(){

        }
    }
}
