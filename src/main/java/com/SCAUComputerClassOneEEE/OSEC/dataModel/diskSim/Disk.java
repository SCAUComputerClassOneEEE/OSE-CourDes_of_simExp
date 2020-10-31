package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim;

import com.SCAUComputerClassOneEEE.OSEC.controller.MySceneController;
import javafx.application.Platform;
import lombok.Data;
import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author best lu
 * @since 2020/7/18 14:00
 */
public class Disk implements Serializable{

    private volatile static Disk disk;

    public static final int DISK_MAX_SIZE = 128;
    public static final int BLOCK_MAX_SIZE = 64;

    //文件分配表，占两字节，磁盘的 0、1 号
    @Getter
    private final FAT fat;
    //模拟的磁盘数据区，（ 2 ~ 127 号）
    private final DiskBlock[] diskBlocks;

    public static Disk getDisk(){
        if (disk == null){
            synchronized (Disk.class){
                if (disk == null){
                    disk = new Disk();
                }
            }
        }
        return disk;
    }

    private Disk() {
        fat = new FAT();
        diskBlocks = new DiskBlock[DISK_MAX_SIZE - 2];
        for (int i = 0; i < diskBlocks.length; i++) {
            diskBlocks[i] = new DiskBlock(i + 2);
        }
    }
    
    public void recovery(int header){
        if(MySceneController.diskChange.getValue() == header)
            Platform.runLater(()->MySceneController.diskChange.setValue(-1));
        Platform.runLater(()->MySceneController.diskChange.setValue(header));
        fat.recovery_FAT(header);
    }

    /**
     * 为目录或文件申请一个头盘块
     * @return -1表示没有足够空间
     *         others 文件头盘块号
     */
    public synchronized int malloc_F_Header() {
        int header;
        try{
            header = fat.getFreeBlockOrder();
            if(header != 2 && MySceneController.diskChange.getValue() == header){
                MySceneController.diskChange.setValue(-1);
            }
            MySceneController.diskChange.setValue(header);
            return  header;
        }catch (Exception e){
            e.printStackTrace();
            return  -1;
        }
    }

    /**
     * 写入文件到磁盘
     * @param header 目录或文件头
     * @param str 内容
     * @throws Exception 容量不足
     */
    public synchronized void writeFile(int header,String str) throws Exception {
        int preBlockSize = str.length()/(BLOCK_MAX_SIZE + 1);
        fat.mallocForFile_FAT(header,preBlockSize);//exception
        for (int i = 0,position = header; i < preBlockSize + 1; i ++){
            String wStr;
            if (i != preBlockSize) wStr = str.substring(i * BLOCK_MAX_SIZE, i * BLOCK_MAX_SIZE + BLOCK_MAX_SIZE);
            else wStr = str.substring(i * BLOCK_MAX_SIZE);
            diskBlocks[position].write(wStr);
            position = fat.getFAT_cont()[position];
        }
    }

    /**
     * 读出文件
     * @param header 目录或文件头
     * @return 内容
     */
    public String readFile(int header){
        List<Integer> fileList = fat.getFileList(header);
        StringBuilder stringBuffer = new StringBuilder();
        for (Integer integer : fileList) {
            stringBuffer.append(diskBlocks[integer].read());
        }
        return stringBuffer.toString();
    }

    /**
     * 计算文件长度
     * @param header 目录或文件头
     * @return 文件长度
     */
    public int getFileSize(int header){ return fat.getFileSize(header); }

    /**
     * 标记损坏盘块
     * @param position 位置
     */
    public void markDamage(int position){ fat.mark_FAT(position); }

    @Data
    private static class FAT implements Serializable {

        static final int EOF = 255;
        int freeBlocks;
        int frsFreePosition;//记录第一个空闲块索引
        final int[] FAT_cont = new int[DISK_MAX_SIZE];

        FAT(){
            FAT_cont[0] = EOF;
            FAT_cont[1] = EOF;
            freeBlocks = DISK_MAX_SIZE - 2;
            frsFreePosition = 2;
        }

        /**
         * 文件分配表的回收修改
         * @param header 回收的首块
         */
        void recovery_FAT(int header){
            //System.out.println("--------recovery_FAT--------");
            if (header == 0) {
                //System.out.println("return because header == 0 is true\n--------end--------");
                return;
            }
            if (header == 1) {
                //System.out.println("return because header == 1 is true\n--------end--------");
                return;
            }
            if (header == EOF) {
                //System.out.println("return because header == EOF is true\n--------end--------");
                return;
            }
            //System.out.println("    recovery the " + header + " now.");
            if (header < frsFreePosition) {
                //System.out.println("    header < frsFreePosition is true");
                frsFreePosition = header;
            }
            recovery_FAT(FAT_cont[header]);
            FAT_cont[header] = 0;
            //System.out.println("    make the FAT_cont[header] = 0 yet");
            /* 计算剩余空闲 */
            freeBlocks ++;
            //System.out.println("--------end--------");
            //System.out.println();
        }

        /**
         * 获得文件分配表中首个空闲块
         * @return 首个空闲块号
         */
        int getFreeBlockOrder() throws Exception {
            FAT_cont[frsFreePosition] = EOF;
            int ret = frsFreePosition;
            /* 计算剩余空闲 */
            freeBlocks --;
            int i;
            for (i = 2; i < DISK_MAX_SIZE; i++)
                if (FAT_cont[i] == 0) {
                    frsFreePosition = i;
                    break;
                }
            if (i == DISK_MAX_SIZE) throw new Exception("Not enough memory to be allocated.");
            return ret;
        }

        /**
         * 从指定的文件盘块开始尾添 preBlockSize 块盘
         * @param preBlockSize 请求划分的块数
         */
        void mallocForFile_FAT(int header,int preBlockSize) throws Exception {
            //System.out.println("---------mallocForFile_FAT--------");
            if (preBlockSize < 0) return;
            if (preBlockSize > 0){
                recovery_FAT(FAT_cont[header]);
            }
            //System.out.println("   preBlockSize = " + preBlockSize);
            if (preBlockSize > freeBlocks)
                throw new Exception("Not enough memory to be allocated.");
            for (int i = 0; i < preBlockSize; i ++){
                int freeOrder = getFreeBlockOrder();
                //System.out.println("   get next free block order, " + freeOrder);
                FAT_cont[header] = freeOrder;
                header = freeOrder;
            }
            FAT_cont[header] = EOF;
            //System.out.println("   FAT_cont[ " + header + " ] = EOF now");
            //System.out.println("-------end--------");
            //System.out.println();
        }
        /**
         * 损坏的磁盘块标记
         * @param position 块位置
         */
        void mark_FAT(int position){
            if (position == 0) return;
            if (position == 1) return;
                FAT_cont[position] = 254;
        }

        /**
         * 计算文件块数
         * @param header 文件块头
         * @return 文件块数
         */
        int getFileSize(int header){
            if (header == 0) return 0;
            if (header == EOF) return 0;
            else return getFileSize(FAT_cont[header]) + 1;
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
                if (FAT_cont[header] != EOF) list.add(FAT_cont[header]);
                else break;
                header = FAT_cont[header];
            }
            return list;
        }
    }

    @Data
    private static class DiskBlock implements Serializable{

        int order;//块号
        char[] block_cont = new char[BLOCK_MAX_SIZE];//内容

        DiskBlock(int order){
            this.order = order;
            Arrays.fill(block_cont, '#');
        }

        void write(String newStr){
            System.arraycopy(newStr.toCharArray(), 0, block_cont, 0, newStr.length());
        }

        String read() {
            if (block_cont == null) return "";
            return String.copyValueOf(block_cont);
        }
    }

    public void readDiskFromFile() {
        try(ObjectInputStream ooi = new ObjectInputStream(new FileInputStream("src/main/resources/diskDat.dat"))) {
            disk = (Disk) ooi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void writeDiskToFile() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/diskDat.dat"))) {
            oos.writeObject(disk);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
