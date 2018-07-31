/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.Color;

/**
 *
 * @author gabriel
 */
public class Screen extends javax.swing.JFrame {
    private CPU cpu;
    private BufferedImage screen_buffer;
    
    private int black;
    private int white;
    
    private int[] screen_size;
    private int[] pixels;
    private int[] gfx;
    
    private int[] trans_colour;
    private int magnify_factor;
    private int big_width, big_height;

    /**
     * Creates new form ScreenFrame
     */
    public Screen(CPU cpu) {   
        initComponents();
        
        this.cpu = cpu;
        
        black = Color.BLACK.getRGB();
        white = Color.WHITE.getRGB();
        
        trans_colour = new int[] {black,white};
        screen_size = cpu.get_scr_size();
        magnify_factor = 5;
        big_width = magnify_factor * screen_size[0];
        big_height = magnify_factor * screen_size[1];
        pixels = new int[big_width * big_height];
        screen_buffer = new BufferedImage(big_width,big_height,BufferedImage.TYPE_BYTE_BINARY);
        screenLabel.setIcon(new ImageIcon(screen_buffer));
        getContentPane().setPreferredSize(new java.awt.Dimension(big_width, big_height));
        pack();
        this.setResizable(false);
    }
    
    public void set_gfx(int[] gfx) {
        this.gfx  = gfx;
    }
    
    public void paint_screen() {       
        magnify_pixels();
        screen_buffer.setRGB(0, 0, big_width, big_height, pixels, 0, big_width);
        screenLabel.repaint();
    }
    
    public void magnify_pixels() {
        int starting_pixel, row;
        int big_row = 0;
        int big_skip = magnify_factor * big_width;
        
        for(int gfx_pixel = 0; gfx_pixel < screen_size[0] * screen_size[1]; gfx_pixel++) {
            starting_pixel = big_row * big_skip + magnify_factor * (gfx_pixel % screen_size[0]);
            
            for(row = 0; row < magnify_factor; row++) {
                for(int i = 0; i < magnify_factor; i++) {
                    pixels[starting_pixel + i] = trans_colour[gfx[gfx_pixel]];
                }

                starting_pixel += big_width;
            }
            
            if(gfx_pixel % screen_size[0] == 0 && gfx_pixel != 0)
                big_row += 1;
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        screenLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(screenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(screenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        cpu.set_pressed();
        cpu.update_pressed_key(Character.toString(evt.getKeyChar()));
    }//GEN-LAST:event_formKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel screenLabel;
    // End of variables declaration//GEN-END:variables
}
