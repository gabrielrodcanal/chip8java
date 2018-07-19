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

/**
 *
 * @author gabriel
 */
public class CPU {
    private int opcode;
    private int PC;
    private int SP;
    private int memory[];
    private int V[];
    private int I;
    private Deque<Integer> stack;
    private int delay_timer;
    private int sound_timer;
    private int screen[];
    private Map<String,String> key_map;
    
    private char X, Y;
    
    public CPU() {
        memory = new int[4096];
        V = new int[16];
        stack = new ArrayDeque();
        screen = new int[64*32];
        key_map = new HashMap<String,String>();
        
        key_map.put("1","1");
        key_map.put("2","2");
        key_map.put("3","3");
        key_map.put("C","4");
        key_map.put("4","Q");
        key_map.put("5","W");
        key_map.put("6","E");
        key_map.put("D","R");
        key_map.put("7","A");
        key_map.put("8","S");
        key_map.put("9","D");
        key_map.put("E","F");
        key_map.put("A","Z");
        key_map.put("0","X");
        key_map.put("B","C");
        key_map.put("F","V");
        
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
        
        for(int i = 0; i < 80; i++)
            memory[i] = sprites[i];
    }
    
    public void call(int addr) {
        stack.push(PC);
        jump(addr);
    }
    
    public void disp_clear() {
        for(int pixel : screen) {
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
                break;
            case 0xA1:
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
        V[0xF] = V[Y] & 1;
        V[X] = V[Y] >>> 1;
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
        
        if(s1 > s2)
            V[0xF] = 1;
        else
            V[0xF] = 0;
        
        if(mode == 0)
            V[X] = (V[X] - V[Y]) & 0xFF;
        else
            V[Y] = (V[Y] - V[X]) & 0xFF;
    }
    
    public void shift_left() {
        V[0xF] = V[X] & 0x80;
        V[X] = V[Y] << 1;
    }
    
    public void set_I() {
        I = opcode & 0xFFF;
    }
    
    public void rand() {
        Random rand = new Random();
        V[X] = rand.nextInt(256) & (opcode & 0xFF);
    }
    
    public void draw() {
        
    }
    
    public void set_Vx_delay() {
        V[X] = delay_timer;
    }
    
    public void set_Vx_key() {
        
    }
    
    public void set_delay_Vx() {
        delay_timer = V[X];
    }
    
    public void set_sound_Vx() {
        sound_timer = V[X];
    }
    
    public void add_I_Vx() {
        I += V[X];
    }
    
    public void set_I_sprite_addr() {
        I = V[X] * 5;
    }
    
    public void set_bcd_Vx() {
        memory[I] = V[X] / 100;
        memory[I+1] = (V[X] / 10) % 10;
        memory[I+2] = V[X] % 10;
    }
    
    public void reg_dump() {
        for(int i = I; i < I+X; i++) {
            memory[i] = V[i];
        }
    }
    
    public void reg_load() {
        for(int i = I; i < I+X; i++) {
            V[i] = memory[i];
        }
    }
    
    public void powerup(String gamepath) {
        try {
            BufferedInputStream game = new BufferedInputStream(new FileInputStream(gamepath));
            
            int addr = 0;
            
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
        opcode = (memory[PC] << 8) & memory[PC+1];
        PC += 2;
    }
    
    public void decode() {
        switch((opcode & 0x80) >> 7) {
            case 0:
                switch(opcode & 7) {
                    case(0x0E0):
                        disp_clear();
                        break;
                    case(0x0EE):
                        ret();
                        break;
                    default:
                        call(opcode & 7);
                }
                break;
            case 1:
                jump(opcode & 7);
                break;
            case 2:
                call(opcode & 7);
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
                switch(opcode & 1) {
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
                //draw
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
                        this.add_I_Vx();
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
        
        PC += 2;
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
}
