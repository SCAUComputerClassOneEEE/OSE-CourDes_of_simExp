import com.SCAUComputerClassOneEEE.OSEC.dataOjb.storageSim.MEM.Memory;

public class Test {
    public static void main(String[] args) throws Exception {
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
