package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import com.SCAUComputerClassOneEEE.OSEC.Main;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.pane.OpenFileManager;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AFile{
    private String fileName;
    /*
     *名仅可以使用字母、数字和除“$”、 “.”、 “/”、“#”以外的字符
     * 第一个字节的值为“$”时表示该目录为空目录项
     *，文件名和类型名之间用“.”分割，用“/”作为路径名中目录间分隔符
     * 3 个字节
     */
    private String type;           //2个字节   类型

    /*
    第 7 位   第 6 位   第 5 位   第 4 位   第 3 位   第 2 位     第 1 位     第 0 位
    未使用     未使用     未使用    执行文件   目录属性   普通文件    系统文件     只读文件
     */
    private char property;       //1个字节  属性

    private char diskNum;        //1个字节 起始盘块号
    private char length;         //1个字节,盘数

    private String location;        //位置,存放父路径的，好按照名称来找
    private ArrayList<AFile> aFiles = new ArrayList<>();

    //绝对路径
    private String absoluteLocation;

    //创建文件、目录
    public AFile(String fileName, String type, char property, char diskNum, char length, String location){
        this.fileName = fileName;
        this.type = type;
        this.property = property;
        this.diskNum = diskNum;
        this.length = length;
        this.location = location;
        this.absoluteLocation = location+"/"+fileName+"."+type;
    }

    @Override
    public String toString(){
        return this.fileName;
    }

    //是文本文件为true
    public boolean isFile(){
        return "tx".equals(this.type);
    }
    //是目录为true
    public boolean isDirectory(){
        return "  ".equals(this.type);
    }
    //是可执行文件
    public boolean isExeFile() {return "ex".equals(this.type); }
    //得到文件信息
    public char[] getALLData(){
        String string = "";
        if(this.fileName.length() < 3){
            int i = 3 - this.fileName.length();
            while (i > 0){
                string += " ";
                i--;
            }
        }
        return (this.fileName + string + this.type + this.property + this.diskNum + this.length).toCharArray();
    }
    //得到文件内容
    public String getDiskContent(){
        String str = Main.disk.readFile(diskNum);
        StringBuilder deleteString = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) != '#')
                deleteString.append(str.charAt(i));
        return deleteString.toString();
    }

    public boolean isOpen(){
        return OpenFileManager.contain(this);
    }
}
