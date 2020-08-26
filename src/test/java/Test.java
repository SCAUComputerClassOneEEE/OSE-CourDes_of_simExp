import com.SCAUComputerClassOneEEE.OSEC.dataOjb.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;

public class Test {
    public static void main(String[] args) throws Exception {

    }
    public void diskTest() throws Exception {
        Disk testDisk = new Disk();
        int header1 = testDisk.malloc_F_Header();
        testDisk.writeFile(header1,"header1 wrote.");
        int header2 = testDisk.malloc_F_Header();
        testDisk.writeFile(header2,"header2 wrote.qwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwer");
        int header3 = testDisk.malloc_F_Header();
        testDisk.writeFile(header3,"header3 wrote.");
        testDisk.writeFile(header2,"header2 wrote.qwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwer");

    }
    public void memoryTest() throws Exception {
        Memory memoryTest = new Memory();
        System.out.println("return process pointer " + memoryTest.malloc(10, "1234567890"));
        System.out.println("return process pointer " + memoryTest.malloc(10, "1234567890"));
        System.out.println("return process pointer " + memoryTest.malloc(10, "1234567890"));
        memoryTest.recovery(10);
        System.out.println("return process pointer " + memoryTest.malloc(20, "12345678901234567890"));
        memoryTest.recovery(30);
        memoryTest.MAT_display();
    }
}
