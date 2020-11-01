package com.SCAUComputerClassOneEEE.OSEC.dataModel.diskSim;

import com.SCAUComputerClassOneEEE.OSEC.utils.OS;
import com.SCAUComputerClassOneEEE.OSEC.pane.OpenFileManager;
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
        this.absoluteLocation = location+"/"+fileName;
    }

    public AFile(String filePath, int property) throws Exception{
        int header = OS.disk.malloc_F_Header();
        if(header == -1){
            throw new Exception("磁盘空间不足");
        }else{
            //System.out.println("新磁盘号："+header);
            this.diskNum = (char)header;
            if(property == 8){
                this.length = 0;
                this.type = "  ";
            }else if(property == 4){
                length = 1;
                this.type = "tx";
            }else if(property == 16){
                length = 1;
                this.type = "ex";
            }
            this.location = "/root" + filePath.substring(0,filePath.lastIndexOf("/"));
            this.fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            this.property = (char) property;
            this.diskNum = (char) header;
            this.absoluteLocation = this.location + this.fileName;
        }
    }

    public AFile(String fatherPath, String fileName, int property) throws Exception{
        int header = OS.disk.malloc_F_Header();
        if(header == -1){
            throw new Exception("磁盘空间不足");
        }else{
            //System.out.println("新磁盘号："+header);
            this.diskNum = (char)header;
            if(property == 8){
                this.length = 0;
                this.type = "  ";
            }else if(property == 4){
                length = 1;
                this.type = "tx";
            }else if(property == 16){
                length = 1;
                this.type = "ex";
            }
            this.location = "/root" + fatherPath;
            this.fileName = fileName;
            this.property = (char) property;
            this.diskNum = (char) header;
            this.absoluteLocation = this.location +"/"+ this.fileName;
        }
    }

    @Override
    public String toString(){
        if(this.isDirectory()) return this.fileName;
        else return this.fileName + "." + this.type;
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
        StringBuilder string = new StringBuilder();
        if(this.fileName.length() < 3){
            int i = 3 - this.fileName.length();
            while (i > 0){
                string.append(" ");
                i--;
            }
        }
        return (this.fileName + string + this.type + this.property + this.diskNum + this.length).toCharArray();
    }
    //得到文件内容
    public String getDiskContent(){
        String str = OS.disk.readFile(diskNum);
        StringBuilder deleteString = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) != '#')
                deleteString.append(str.charAt(i));
        return deleteString.toString();
    }

    public boolean isOpen(){
        return OpenFileManager.contain(this);
    }

    /**
     * @Author: Sky
     * @Date: 2020/8/13 22:54
     */
    @Getter
    @Setter
    public static class AOpenFile {

        private String absoluteLocation;    //文件完整路径(包含后缀)
        private int property;           //文件属性
        private int diskNum;            //起始盘块
        private int length;             //文件长度(文件占据的盘块数)
        private String openType;//读写类型(操作类型)

        //读指针
        private int rPointerBlockNum;
        private int rPointerLocation;

        //写指针
        private int wPointerBlockNum;
        private int wPointerLocation;

        public AOpenFile(AFile aFile) {
            this.absoluteLocation = aFile.getAbsoluteLocation();
            this.property = aFile.getProperty();
            this.diskNum = aFile.getDiskNum();
            this.length = aFile.getLength();
            this.openType = "关闭";
            this.rPointerBlockNum = aFile.getDiskNum();
            this.rPointerLocation = 0;
            this.wPointerBlockNum = aFile.getDiskNum();
            this.wPointerLocation = 0;
        }

        @Override
        public String toString() {
            return String.format("文件路径:%s 文件属性:%d 起始盘块号:%d 文件长度:%d 操作类型:%s 读指针块号与块内地址:%d,%d 写指针块号与块内地址:%d,%d", absoluteLocation, property, diskNum, length, openType, rPointerBlockNum, rPointerLocation, wPointerBlockNum, wPointerLocation);
        }

    }
}
