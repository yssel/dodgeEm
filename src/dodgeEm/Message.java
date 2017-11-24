package dodgeEm;

import java.util.Timer;
import java.util.TimerTask;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Message {
    private String sender;
    private String message;
    private Timer timer;
    private float duration;
    private int alpha;

    public Message(String line){
        String[] tokens = line.split(":");

        this.sender = tokens[0] + ": ";
        this.message = tokens[1];
        this.timer = new Timer();
        this.duration = 5;
        this.alpha = 255;
    }

    public void startTimer(){
        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if(Message.this.duration <= 3){
                    Message.this.alpha *= (Message.this.duration/5);
                }
                if(Message.this.duration == 0){
                    timer.cancel();
                }
                System.out.println(Message.this.duration);
                Message.this.duration -= 0.50;
            }
        }, 0, 500);
    }

    public void render(int x, int y, Graphics graphics){
        graphics.setColor(new Color(255, 0, 0, this.alpha));
        graphics.drawString(this.sender, x, y);

        graphics.setColor(new Color(255, 255, 255, this.alpha));
        graphics.drawString(this.message, x + graphics.getFont().getWidth(sender), y);
    }
}
