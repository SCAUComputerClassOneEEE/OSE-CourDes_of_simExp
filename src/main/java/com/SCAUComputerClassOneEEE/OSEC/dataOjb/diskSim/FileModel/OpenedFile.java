package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Sky
 * @Date: 2020/8/11 20:02
 */
public class OpenedFile {
    public static ArrayList<OFTLE> openFiles = new ArrayList<>();

    public static boolean openFile(AFile file){
        OFTLE oftle = new OFTLE(file);
        if (openFiles.size()<5&&!openFiles.contains(oftle)){
            openFiles.add(oftle);
            printOpenFiles();
            return true;
        }
        else return false;
    }

    public static boolean closeFile(AFile file){
       for (OFTLE each:openFiles){
           if(each.getName().equals(file.getFileName())){
               openFiles.remove(each);
               printOpenFiles();
               return true;
           }
       }
       return false;
    }

    public static void printOpenFiles(){
        System.out.println("当前打开文件");
        for(OFTLE each:openFiles){
            System.out.println(each.toString());
        }
    }
}
@Getter
class OFTLE{

    String name;
    char attribute;//文件的属性，用 1 个字节表示，所以此用 char 类型
    int number;//文件起始盘块号
    int length; //文件长度，文件占用的字节数
    //int flag; //操作类型，用“0”表示以读操作方式打开文件，用“1”表示以写操作方式打开文件
    Pointer readPointer;
    Pointer writPointer;

    public OFTLE(AFile file){
        this.name = file.getFileName();
        this.attribute = file.getProperty();
        this.number = file.getDiskNum();
        this.length = file.getLength();

    }

    @Override
    public String toString() {
        return "name:"+this.name+"attribute:"+this.attribute+"起始盘块"+this.number+"总块数"+this.length;
    }
}
@Setter
@Getter
class Pointer{
    int blockNum;//盘块号
    int pointerLocation;//指针位置
}