/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
/**
 *
 * @author gabriel
 */
public class Buzzer {
    private int sample_rate;
    private SourceDataLine line;
    private AudioFormat af;
    private byte[] data;
    final static char SQUARE = 0;
    final static char SAWTOOTH = 1;
    final static char TRIANGLE = 2;
    final static char SINE = 3;
    
    /**
     * Creates a Buzzer object. This class allows the production of different sound waves given their frequency: square, 
     * sawtooth, triangle and sine. The method close_line() should be executed before the program using this class 
     * terminates.
     * @param sample_rate number of samples per wave period.
     * @param wave_type SQUARE, SAWTOOTH, TRIANGLE or SINE class constants.
     * @param seconds number of seconds the sound will last.
     * @param frequency desired frequency of the wave.
     */
    public Buzzer(int sample_rate, char wave_type, float seconds, double frequency) {
        af = new AudioFormat(sample_rate, 8, 1, true, true);
        this.sample_rate = sample_rate;
        
        try {
            line = AudioSystem.getSourceDataLine(af);
            line.open(af);
            line.start();
            data = get_wave(wave_type,seconds,frequency);
            
        }
        catch(Exception e) {}
    }
    
    /**
     * Produce the sound associated to the wave.
     */
    public void play() {
        line.write(data, 0, data.length);
        line.drain();
    }
    
    /**
     * Close the line associated to the output hardware.
     */
    public void close_line() {
        line.close();
    }
    
    private byte[] get_wave(char type, float seconds, double frequency) {
        byte[] data = new byte[Math.round(seconds * sample_rate)];
        double sampling_interval = (double) (sample_rate / frequency);  //samples per period
        double tau = 1/frequency;   //wave period
        double time = 0;    //real elapsed time
        double mod_time;
        
        switch(type) {
            case 0: //square
                for(int i = 0; i < data.length; i++) {
                    if(time % tau < tau/2)
                        data[i] = (byte) 127;
                    else
                        data[i] = (byte) -127;
                    time += tau / sampling_interval;
                }
                break;
                
            case 1: //sawtooth
                for(int i = 0; i < data.length; i++) {
                    mod_time = time % tau;
                    if(mod_time < tau/2)
                        data[i] = (byte) (254 * mod_time / tau);
                    else
                        data[i] = (byte) (127 * (2 * mod_time / tau - 2));
                    time += tau / sampling_interval;
                }
                break;
                
            case 2: //triangle
                for(int i = 0; i < data.length; i++) {
                    mod_time = time % tau;
                    if(mod_time < tau/2)
                        data[i] = (byte) (127 * ((4 * mod_time) / tau - 1));
                    else
                        data[i] = (byte) (127 * (3 - 4 * mod_time / tau));
                    time += tau / sampling_interval;
                }
                break;
                
            case 3:
                for(int i = 0; i < data.length; i++) {
                    time = 2 * Math.PI * i / sampling_interval;
                    data[i] = (byte) (127 * Math.sin(time));
                }
                break;
        }
        
        return data;
    }
}