/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gabriel
 */
public class CPUTest {
    
    public CPUTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add_Vx_Vy method, of class CPU.
     */
    @Test
    public void testAdd_Vx_Vy() {
        System.out.println("add_Vx_Vy");
        CPU instance = new CPU();
        instance.set_X((char)0);
        instance.set_Y((char)1);
        instance.set_V((char)0,0xFF);
        instance.set_V((char)1,1);
        instance.add_Vx_Vy();
        
        assertEquals("1+0xFF",instance.get_flag(),1);
        
        instance.set_V((char)0,0xFE);
        instance.set_V((char)1,1);
        instance.set_V((char)0xF, 0);
        instance.add_Vx_Vy();
        assertEquals("1+0xFE",instance.get_flag(),0);
    }

    /**
     * Test of sub_Vx_Vy method, of class CPU.
     */
    @Test
    public void testSub_Vx_Vy() {
        System.out.println("sub_Vx_Vy");
        CPU instance = new CPU();
        instance.set_X((char)0);
        instance.set_Y((char)1);
        instance.set_V((char)0,0);
        instance.set_V((char)1,1);
        instance.sub(0);
        
        assertEquals("0-1",instance.get_flag(),0);
        
        instance.set_V((char)0,1);
        instance.set_V((char)1,0);
        
        instance.sub(0);
        
        assertEquals("1-0",instance.get_flag(),1);
    }

    /**
     * Test of set_bcd_Vx method, of class CPU.
     */
    @Test
    public void testSet_bcd_Vx() {
        System.out.println("set_bcd_Vx");
        CPU instance = new CPU();
        
        int I = instance.get_I();
        
        instance.set_X((char)0);
        instance.set_V((char)0, 123);
        instance.set_bcd_Vx();
        
        assertEquals(instance.getMem(I),1);
        assertEquals(instance.getMem(I+1),2);
        assertEquals(instance.getMem(I+2),3);
    }

    /**
     * Test of reg_dump method, of class CPU.
     */
    @Test
    public void testReg_dump() {
        System.out.println("reg_dump");
        CPU instance = new CPU();
        
        instance.set_X((char)0xF);
        for(int i = 0; i < 0xF; i++)
            instance.set_V((char)i, i+1);
        instance.reg_dump();
        
        for(int i = 0; i < 0xF; i++) {
            assertEquals(instance.getMem(i),i+1);
        }
        
        instance.set_V((char)0,234);
        instance.set_X((char)0);
        instance.reg_dump();
        assertEquals(instance.getMem(0),234);
    }

    /**
     * Test of reg_load method, of class CPU.
     */
    @Test
    public void testReg_load() {
        System.out.println("reg_load");
        CPU instance = new CPU();
        
        instance.set_X((char)0xF);
        for(int i = 0; i < 0xF; i++)
            instance.setMem((char)i, i+1);
        instance.reg_load();
        
        for(int i = 0; i < 0xF; i++) {
            assertEquals(instance.get_V(i),i+1);
        }
    }
}
