package com.SCAUComputerClassOneEEE.OSEC.dataOjb.processSim;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 *
 *
 * pcb模拟
 * @author best lu
 * @since 2020/08/15
 */
@Getter
@Setter
public class PCB {

    private int processId;//进程id
    private ProcessState processState;//进程状态
    private int pointerToMemory;//内存指针 0 ~ 512
    private Date timeWhenProcessStart;//启动时间
    //资源（占用设备、内存大小）

    public enum ProcessState{

        NEW(0,"新建"),
        READY(1,"就绪"),
        RUNNING(2,"运行"),
        BLOCKED(3,"阻塞"),
        EXIT(4,"退出");

        private int code;
        private String state;

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        ProcessState(int code, String state) {
            this.code = code;
            this.state = state;
        }

        public ProcessState getValue(int code){
            for (ProcessState processState : values()){
                if (processState.getCode() == code)
                    return processState;
            }
            return null;
        }
    }
}