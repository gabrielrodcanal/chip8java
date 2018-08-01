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
    private static final int GAME_ARG = 0;
    private static final int SHIFT_QUIRK_ARG = 1;
    private static final int LOAD_STORE_QUIRK_ARG = 2;
    private static final int MAGNIFY_FACTOR_ARG = 3;
    private static final int WAVE_TYPE_ARG = 4;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CPU cpu = new CPU(Boolean.valueOf(args[SHIFT_QUIRK_ARG]),Boolean.valueOf(args[LOAD_STORE_QUIRK_ARG]),
        Integer.parseInt(args[WAVE_TYPE_ARG]));
        
        Screen screen = new Screen(cpu,Integer.parseInt(args[MAGNIFY_FACTOR_ARG]));
        
        cpu.powerup(args[GAME_ARG]);
        cpu.set_screen(screen);
        cpu.link_screen_gfx();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                screen.setVisible(true);
            }
        });
        
        Thread cpu_thread = new Thread(cpu);
        cpu_thread.start();
    }    
}
