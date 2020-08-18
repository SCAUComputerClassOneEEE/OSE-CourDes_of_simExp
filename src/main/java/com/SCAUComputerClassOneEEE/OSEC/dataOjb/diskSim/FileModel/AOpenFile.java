package com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.FileModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Sky
 * @Date: 2020/8/13 22:54
 */
@Getter
@Setter
public class AOpenFile {

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

    public AOpenFile(AFile aFile){
        this.absoluteLocation = aFile.getAbsoluteLocation();
        this.property = aFile.getProperty();
        this.diskNum = aFile.getDiskNum();
        this.length = aFile.getLength();
        this.openType = "关闭";
        this.rPointerBlockNum = 0;
        this.rPointerLocation = 0;
        this.wPointerBlockNum = 0;
        this.wPointerLocation = 0;
    }

    @Override
    public String toString(){
        return String.format("文件路径:%s 文件属性:%d 起始盘块号:%d 文件长度:%d 操作类型:%s 读指针块号与块内地址:%d,%d 写指针块号与块内地址:%d,%d",absoluteLocation,property,diskNum,length,openType,rPointerBlockNum,rPointerLocation,wPointerBlockNum,wPointerLocation);
    }

}
