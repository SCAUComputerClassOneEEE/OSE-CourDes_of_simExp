import com.SCAUComputerClassOneEEE.OSEC.data_model.diskSim.Disk;
import com.SCAUComputerClassOneEEE.OSEC.data_model.storageSim.MEM.Memory;
import org.junit.Test;

import java.util.Arrays;

public class Testtt {

    @Test
    public void diskTest() throws Exception {
        Disk testDisk = Disk.getDisk();

        int header1 = testDisk.malloc_F_Header();
        System.out.println("get header: " + header1);
        testDisk.writeFile(header1,"header1 wrote.");

        int header2 = testDisk.malloc_F_Header();
        System.out.println("get header: " + header2);
        testDisk.writeFile(header2,"header2 wrote.qwerqwerqwerqwerqwerqwerqwerqwerqwerqwer" +
                "qwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwer" +
                "qwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwer" +
                "qwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwerqwer");

        int header3 = testDisk.malloc_F_Header();
        System.out.println("get header: " + header3);
        testDisk.writeFile(header3,"header3 wrote.");

        testDisk.writeFile(header2,"header2 wrote.q");
        int header4 = testDisk.malloc_F_Header();
        System.out.println(header4);
    }

   @Test
    public void memoryTest() throws Exception {
        Memory memoryTest = Memory.getMemory();
        System.out.println("return process pointer " + memoryTest.malloc("1234567890".toCharArray()));
        System.out.println("return process pointer " + memoryTest.malloc("1234567890".toCharArray()));
        System.out.println("return process pointer " + memoryTest.malloc("1234567890".toCharArray()));
        memoryTest.recovery(0);
        memoryTest.MAT_display();
        memoryTest.recovery(10);
        memoryTest.MAT_display();
        System.out.println("return process pointer " + memoryTest.malloc("1234567890".toCharArray()));
//        memoryTest.recovery(20);
//        memoryTest.MAT_display();
//        memoryTest.compress();
//        System.out.println("return process pointer " + memoryTest.malloc( "abcuytegfd".toCharArray()));
        memoryTest.MAT_display();
    }
    @Test
    public void tt(){
        char[] chars = new char[10];
        Arrays.fill(chars,'c');
        chars[0] = '0';
        chars[1] = '1';
        chars[5] = '5';
        chars[6] = '6';
        System.out.println(chars);
        System.arraycopy(chars,5,chars,0,2);
        System.out.println(chars);
    }
    @Test
    public void tttt(){

    }
}
