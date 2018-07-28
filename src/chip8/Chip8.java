/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8;

/**
 *
 * @author gabriel
 */
public class Chip8 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CPU cpu = new CPU();
        
        Screen screen = new Screen(cpu);
        
        cpu.powerup(args[0]);
        cpu.set_screen(screen);
        cpu.link_screen_gfx();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                screen.setVisible(true);
                
            }
        });  
        
        cpu.run();
    }    
}