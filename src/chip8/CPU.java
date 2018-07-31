/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Semaphore;

/**
 *
 * @author gabriel
 */
public class CPU implements Runnable {
    private final int SCR_WIDTH = 64;
    private final int SCR_HEIGHT = 32;
    private final int SPRT_WIDTH = 8;
    private final int SPRT_HEIGHT = 15;
    private final int EXP_SCR_WIDTH = SCR_WIDTH + SPRT_WIDTH;
    private final int EXP_SCR_HEIGHT = SCR_HEIGHT + SPRT_HEIGHT;
    private final boolean shift_quirk = false;
    
    private int opcode;
    private int PC;
    private int memory[];
    private int V[];
    private int I;
    private Deque<Integer> stack;
    private CountDownTimer delay_timer;
    private CountDownTimer sound_timer;
    private int[] gfx;
    private Map<String,Integer> key_map; //real keys to chip8 keys
    private Map<Integer,Boolean> pressed_key;   //chip8 keys to boolean
    private int update_key; //used in set_Vx_key
    private Screen screen;
    private int key_tolerance;
    private int key_ticks;
    
    private boolean is_pressed_key;
    private boolean sound;
    private Thread buzzer;
    
    private Semaphore semph;
    
    private char X, Y;
    
    public CPU() {
        memory = new int[4096];
        V = new int[16];
        stack = new ArrayDeque();
        gfx = new int[SCR_WIDTH * SCR_HEIGHT];
        key_map = new HashMap<String,Integer>();
        pressed_key = new HashMap<Integer,Boolean>();
        delay_timer = new CountDownTimer();
        sound_timer = new CountDownTimer();
        key_ticks = 0;
        key_tolerance = 100;
        sound = false;
        
        semph = new Semaphore(1);
        try {
            semph.acquire();
        }
        catch(Exception e) {}
        
        buzzer = new Thread(new ConcurrentBuzzer(14400,Buzzer.SQUARE,0.5f,200));
        buzzer.start();
        
        key_map.put("1",1);
        key_map.put("2",2);
        key_map.put("3",3);
        key_map.put("4",0xC);
        key_map.put("q",4);
        key_map.put("w",5);
        key_map.put("e",6);
        key_map.put("r",0xD);
        key_map.put("a",7);
        key_map.put("s",8);
        key_map.put("d",9);
        key_map.put("f",0xE);
        key_map.put("z",0xA);
        key_map.put("x",0);
        key_map.put("c",0xB);
        key_map.put("v",0xF);
        
        pressed_key.put(1,false);
        pressed_key.put(2,false);
        pressed_key.put(3,false);
        pressed_key.put(0xC,false);
        pressed_key.put(4,false);
        pressed_key.put(5,false);
        pressed_key.put(6,false);
        pressed_key.put(0xD,false);
        pressed_key.put(7,false);
        pressed_key.put(8,false);
        pressed_key.put(9,false);
        pressed_key.put(0xE,false);
        pressed_key.put(0xA,false);
        pressed_key.put(0,false);
        pressed_key.put(0xB,false);
        pressed_key.put(0xF,false);
        
        int sprites[] = {
            0xF0,0x90,0x90,0x90,0xF0,
            0x20,0x60,0x20,0x20,0x70,
            0xF0,0x10,0xF0,0x80,0xF0,
            0xF0,0x10,0xF0,0x10,0xF0,
            0x90,0x90,0xF0,0x10,0x10,
            0xF0,0x80,0xF0,0x10,0xF0,
            0xF0,0x80,0xF0,0x90,0xF0,
            0xF0,0x10,0x20,0x40,0x40,
            0xF0,0x90,0xF0,0x90,0xF0,
            0xF0,0x90,0xF0,0x10,0xF0,
            0xF0,0x90,0xF0,0x90,0x90,
            0xE0,0x90,0xE0,0x90,0xE0,
            0xF0,0x80,0x80,0x80,0xF0,
            0xE0,0x90,0x90,0x90,0xE0,
            0xF0,0x80,0xF0,0x80,0xF0,
            0xF0,0x80,0xF0,0x80,0x80            
        };
        
        for(int i = 0x50; i < 0x50+80; i++)
            memory[i] = sprites[i-80];
    }
    
    public void call(int addr) {
        stack.push(PC);
        jump(addr);
    }
    
    public void disp_clear() {
        for(int pixel : gfx) {
            pixel = 0;
        }
    }
    
    public void ret() {
        PC = stack.pop();
    }
    
    public void jump(int addr) {
        PC = addr;
    }
    
    public void skip_next_if(char instr) {
        switch(instr) {
            case 3:
                if(V[X] == (opcode & 0xFF))
                    PC += 2;               
                break;
            case 4:
                if(V[X] != (opcode & 0xFF))
                    PC += 2;
                break;
            case 5:
                if(V[X] == V[Y])
                    PC += 2;
                break;
            case 9:
                if(V[X] != V[Y])
                    PC += 2;
                break;
            case 0x9E:
                if(pressed_key.get(V[X]))
                    PC += 2;
                break;
            case 0xA1:
                if(!pressed_key.get(V[X]))
                    PC += 2;
                break;
        }
    }
    
    public void assign_Vx_const() {
        V[X] = opcode & 0xFF;
    }
    
    public void add_Vx_const() {
        V[X] = (V[X] + (opcode & 0xFF)) & 0xFF;
    }
    
    public void assign_Vx_Vy() {
        V[X] = V[Y];
    }
    
    public void or() {
        V[X] |= V[Y];
    }
    
    public void and() {
        V[X] &= V[Y];
    }
    
    public void xor() {
        V[X] ^= V[Y];
    }
    
    public void add_Vx_Vy() {
        if(V[X] > (0xFF - V[Y]))
            V[0xF] = 1;
        
        V[X] = (V[X] + V[Y]) & 0xFF;
    }
    
    public void shift_right() {
        if(!shift_quirk) {
            V[0xF] = V[Y] & 1;
            V[X] = V[Y] >>> 1;
        }
        else {
            V[0xF] = V[X] & 1;
            V[X] >>>= 1;
        }
    }
    
    public void sub(int mode) {
        int s1, s2;
        switch(mode) {
            case(0):
                s1 = V[X];
                s2 = V[Y];
                break;
            case(1):
                s1 = V[Y];
                s2 = V[X];
                break;
            default:
                s1 = s2 = 0;
        }
        
        if(s1 < s2)
            V[0xF] = 0;
        else
            V[0xF] = 1;
        
        if(mode == 0)
            V[X] = (V[X] - V[Y]) & 0xFF;
        else
            V[Y] = (V[Y] - V[X]) & 0xFF;
    }
    
    public void shift_left() {
        if(!shift_quirk) {
            V[0xF] = (V[Y] & 0x80) >>> 7;
            V[X] = (V[Y] << 1) & 0xFF;
        }
        else {
            V[0xF] = (V[X] & 0x80) >>> 7;
            V[X] = (V[X] << 1) & 0xFF;
        }
    }
    
    public void set_I() {
        I = opcode & 0xFFF;
    }
    
    public void rand() {
        Random rand = new Random();
        V[X] = rand.nextInt(256) & (opcode & 0xFF);
    }
    
    public void draw() {
        V[0xF] = 0;
        int n = opcode & 0xF;
        int row = 0;
        int mask;
        int mem_val,gfx_pos;
        int x_cord, y_cord;
        
        for(int i = I; i < I + n; i++) {
            mask = 0x80;
            
            for(int j = 0; j < 8; j++) {
                x_cord = (V[X] + j) % SCR_WIDTH;
                y_cord = (V[Y] + row)% SCR_HEIGHT;
                gfx_pos = x_cord + SCR_WIDTH * y_cord;
                
                mem_val = (memory[i] & mask) >>> 7-j;
                
                if(mem_val == 1) {
                    //collision detection
                    if(gfx[gfx_pos] == 1 && mem_val == 1)
                        V[0xF] = 1;

                    gfx[gfx_pos] ^= mem_val;
                }
                mask >>>= 1;
            }
            
            row++;
        }
        
        screen.paint_screen();
    }
    
    public void set_Vx_delay() {
        V[X] = (int)delay_timer.getTime();
    }
    
    public void set_Vx_key() {
        is_pressed_key = false;
        while(!is_pressed_key) {}
                
        if(pressed_key.keySet().contains(update_key))    //does update_key belong to the chip-8 keyboard?
            V[X] = update_key;
    }
    
    public void set_pressed() {
        is_pressed_key = true;
    }
    
    public void set_delay_Vx() {
        delay_timer.setTime((long)V[X]);
    }
    
    public void set_sound_Vx() {
        if(V[X] > 0) {
            sound = true;
            sound_timer.setTime((long)V[X]);
        }
    }
    
    public void add_I_Vx() {
        if(I + V[X] > 0xFFF)
            V[0xF] = 1;
        else
            V[0xF] = 0;
        I += V[X];
    }
    
    public void set_I_sprite_addr() {
        I = V[X] * 5 + 80;
    }
    
    public void set_bcd_Vx() {
        memory[I] = V[X] / 100;
        memory[I+1] = (V[X] / 10) % 10;
        memory[I+2] = V[X] % 10;
    }
    
    public void reg_dump() {
        int reg = 0;
        for(int i = I; i <= I+X; i++) {
            memory[i] = V[reg];
            reg++;
        }
    }
    
    public void reg_load() {
        int reg = 0;
        for(int i = I; i <= I+X; i++) {
            V[reg] = memory[i];
            reg++;
        }
    }
    
    public void powerup(String gamepath) {
        try {
            BufferedInputStream game = new BufferedInputStream(new FileInputStream(gamepath));
            
            int addr = 0x200;
            PC = 0x200;
            
            try {
                while((memory[addr] = game.read()) != -1) {
                    addr++;
                }
                memory[addr+1] = 0;
            }
            catch(IOException e) {}
        }
        catch (FileNotFoundException e) {
            System.exit(-1);
        }
    }
    
    public void fetch() {
        opcode = (memory[PC] << 8) | memory[PC+1];
        PC += 2;
        X = (char)((opcode & 0xF00) >>> 8);
        Y = (char)((opcode & 0xF0) >>> 4);
    }
    
    public void decode() {
        switch((opcode & 0xF000) >> 12) {
            case 0:
                switch(opcode & 0xFF) {
                    case(0x0E0):
                        disp_clear();
                        break;
                    case(0x0EE):
                        ret();
                        break;
                    default:
                        call(opcode & 0xFFF);
                }
                break;
            case 1:
                jump(opcode & 0xFFF);
                break;
            case 2:
                call(opcode & 0xFFF);
                break;
            case 3:
                skip_next_if((char)3);
                break;
            case 4:
                skip_next_if((char)4);
                break;
            case 5:
                skip_next_if((char)5);
                break;
            case 6:
                assign_Vx_const();
                break;
            case 7:
                add_Vx_const();
                break;
            case 8:
                switch(opcode & 0xF) {
                    case 0:
                        assign_Vx_Vy();
                        break;
                    case 1:
                        or();
                        break;
                    case 2:
                        and();
                        break;
                    case 3:
                        xor();
                        break;
                    case 4:
                        add_Vx_Vy();
                        break;
                    case 5:
                        sub(0);
                        break;
                    case 6:
                        shift_right();
                        break;
                    case 7:
                        sub(1);
                        break;
                    case 0xE:
                        shift_left();
                        break;
                }
                break;
            case 9:
                skip_next_if((char)9);
                break;
            case 0xA:
                set_I();
                break;
            case 0xB:
                jump((opcode & 0xFFF) + V[0]);
                break;
            case 0xC:
                rand();
                break;
            case 0xD:
                draw();
                break;
            case 0xE:
                switch(opcode & 0xFF) {
                    case 0x9E:
                        skip_next_if((char)0x9E);
                        break;
                    case 0xA1:
                        skip_next_if((char)0xA1);
                        break;
                }
                break;
            case 0xF:
                switch(opcode & 0xFF) {
                    case 0x07:
                        set_Vx_delay();
                        break;
                    case 0x0A:
                        set_Vx_key();
                        break;
                    case 0x15:
                        set_delay_Vx();
                        break;
                    case 0x18:
                        set_sound_Vx();
                        break;
                    case 0x1E:
                        add_I_Vx();
                        break;
                    case 0x29:
                        set_I_sprite_addr();
                        break;
                    case 0x33:
                        set_bcd_Vx();
                        break;
                    case 0x55:
                        reg_dump();
                        break;
                    case 0x65:
                        reg_load();
                        break;
                }
        }
        
        key_ticks = ++key_ticks % key_tolerance;
        if(key_ticks == 0)
            pressed_key.replaceAll((k,v) -> v = false);
    }
    
    public void update_pressed_key(String key) {
        try {
            int chip8_key = key_map.get(key);
            if(pressed_key.keySet().contains(chip8_key)) {  //does the key belong to the chip 8 keyboard?
                pressed_key.put(key_map.get(key),true);
                update_key = chip8_key;
            }
        }
        catch(java.lang.NullPointerException e) {}
    }
    
    public void emulate_cycle() {
        fetch();
        decode();
        
        if(sound && sound_timer.getTime() == 0) {
            semph.release();
            sound = false;
        }
    }
    
    public int[] get_exp_scr_size() {
        return new int[] {EXP_SCR_WIDTH,EXP_SCR_HEIGHT};
    }
    
    public int[] get_scr_size() {
        return new int[] {SCR_WIDTH,SCR_HEIGHT};
    }
    
    public void set_gfx(int[] gfx) {
        this.gfx = gfx;
    }
    
    public void set_screen(Screen screen) {
        this.screen = screen;
    }
    
    public void run() {
        while(true) {
            emulate_cycle();
            try {
                Thread.sleep(2);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void link_screen_gfx() {
        screen.set_gfx(gfx);
    }
    
    //Methods for testing
    public int get_opcode() {
        return opcode;
    }
    
    public void add_to_stack(Integer i) {
        stack.add(i);
    }
    
    public Integer pop_from_stack() {
        return stack.pop();
    }
    
    public void setMem(int addr, int val) {
        memory[addr] = val;
    }
    
    public int getMem(int addr) {
        return memory[addr];
    }
    
    public void set_V(char ind, int val) {
        V[ind] = val;
    }
    
    public void set_X(char val) {
        X = val;
    }
    
    public void set_Y(char val) {
        Y = val;
    }
    
    public int get_V(int ind) {
        return V[ind];
    }
    
    public int get_flag() {
        return V[0xF];
    }
    
    public int get_I() {
        return I;
    }
    
    private class ConcurrentBuzzer extends Buzzer implements Runnable {        
        public ConcurrentBuzzer(int sample_rate, char wave_type, float seconds, double frequency) {
            super(sample_rate, wave_type, seconds, frequency);
        }
        
        @Override
        public void run() {
            while(true) {
                try {
                    semph.acquire();
                    play();
                }
                catch(Exception e) {}
            }
        }
    }
}