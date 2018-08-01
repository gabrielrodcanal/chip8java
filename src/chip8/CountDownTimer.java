/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author gabriel
 */
public class CountDownTimer {
    private long time;
    private Timer timer;
    private boolean cancel;
    
    public CountDownTimer() {
        time = 0;
        timer = new Timer();
        cancel = false;
    }
    
    public void start() {
        long delay = 0;
        long period = 16;
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(time > 0)
                    time--;
                else {
                    cancel();
                    timer.purge();
                }  
            }
        }, delay, period);
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
        start();
    }
}