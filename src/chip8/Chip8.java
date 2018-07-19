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
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Screen screen = new Screen(cpu);
                screen.setVisible(true);
            }
        });
        
        cpu.powerup("MAZE");
    }
    
}
