package dodgeEm;

import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import static dodgeEm.Game.loadFont;

public class ScoreBoard {
    public TrueTypeFont font;
    public TrueTypeFont font1;
    public TrueTypeFont font2;
    ArrayList<String> scorers = new ArrayList<>();

    /** FOR ENCRYPTION/DECRYPTION USING AES**/
    private String game = "Dodge'Em";
    private String SALT2 = "GSST";
    private byte[] key;
    private MessageDigest sha;
    private SecretKeySpec secretKeySpec;
    private static Cipher cipher;
    private Boolean visible = false;

    public ScoreBoard() throws Exception {
        this.font = loadFont("res/DJB.ttf", 40f);
        this.font1 = loadFont("res/trench100free.ttf", 30f);
        this.font2 = loadFont("res/trench100free.ttf", 45f);
//        /** Temporary top scorers **/
//        scorers.add("rain 999");
//        scorers.add("rain1 709");
//        scorers.add("rain2 601");
//        scorers.add("rain3 599");
//        scorers.add("rain4 409");
//        scorers.add("rain5 301");
//        scorers.add("rain6 299");
//        scorers.add("rain7 109");
//        scorers.add("rain8 51");
//        scorers.add("rain9 21");
        try{
            key = (SALT2+game).getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            cipher = Cipher.getInstance("AES");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKeySpec = new SecretKeySpec(key, "AES");
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public void render(){
        Graphics graphics = new Graphics();
        String [] tokens = new String[2];
        graphics.setFont(this.font);

        if(this.visible) {
            graphics.setColor(new Color(255,255,255, 150));
            graphics.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

            String title = "SCOREBOARD";
            graphics.setColor(Color.black);
            graphics.drawString(title, Game.CENTER_X - (this.font.getWidth(title) / 2), (Game.CENTER_Y - 275));
            float centerX = Game.CENTER_X - (this.font.getWidth(title)  / 2);

            graphics.setFont(this.font1);
            String player = "PLAYER";
            String score = "KILLS";

            float playerCenterX = centerX - ((this.font.getWidth(player) / 2));
            float scoreCenterX = centerX + (this.font.getWidth(title)/2) + (this.font.getWidth(score)/2);


            graphics.drawString(player, playerCenterX, (Game.CENTER_Y - 270) + 40);
            graphics.drawString(score, scoreCenterX, (Game.CENTER_Y - 270) + 40);

            float lineEnd = 0;

            for(int i = 0; i < scorers.size(); i++) {

                tokens = scorers.get(i).split(" ");

                int lineSpacing = 45*i;
                lineEnd = scoreCenterX + this.font.getWidth(score)-15 - (this.font.getWidth(tokens[1]));
                if(i==0){
                    graphics.setFont(this.font2);
                    graphics.setColor(new Color(139,0,0, 180));
                    graphics.fillRect(0, (Game.CENTER_Y - 190) + lineSpacing, Game.SCREEN_WIDTH, ((Game.CENTER_Y - 200) + lineSpacing+30)/4);
                    //graphics.setColor(Color.white);
                }
                else{
                    graphics.setFont(this.font1);
                    graphics.setColor(new Color(0,0,0, 180-(i*5)));
                    graphics.fillRect(0, (Game.CENTER_Y - 200) + lineSpacing, Game.SCREEN_WIDTH, ((Game.CENTER_Y - 200) + lineSpacing)/(4+i));
                }

                graphics.setColor(Color.white);
                graphics.drawString(tokens[0], playerCenterX, (Game.CENTER_Y - 200) + lineSpacing);
                graphics.drawString(tokens[1], lineEnd, (Game.CENTER_Y - 200)  + lineSpacing);
            }

            lineEnd = scoreCenterX + this.font.getWidth(score)-15 - (this.font.getWidth(Integer.toString(MainMenu.kills)));

            graphics.setColor(Color.red);
            graphics.drawString(MainMenu.name, playerCenterX, (Game.CENTER_Y - 200) + 45*10);
            graphics.drawString(Integer.toString(MainMenu.kills), lineEnd, (Game.CENTER_Y - 200) + 45*10);
        }
    }

    public void listen(Input input){
        if(input.isKeyDown(Input.KEY_TAB)){
            this.visible = true;
        } else{
            this.visible = false;
        }
    }

    public void writeScorers() throws Exception {
        String plainText="";
        for(String str: scorers){

            plainText+=(str+"\n");
        }
        FileWriter fw = new FileWriter("res/data/scores.txt");
        String encryptedText = encrypt(plainText, secretKeySpec);
        fw.write(encryptedText);
        fw.close();
    }

    public void readScorersArray() throws Exception{
        ArrayList<String> scorers = new ArrayList<>();
        String plainText="";
        try{
            BufferedReader br = new BufferedReader(new FileReader("res/data/scores.txt"));
            String line= null;
            while((line= br.readLine())!=null){

                plainText+=(line+"\n");
            }
        }catch(IOException e){}

        String decryptedText = decrypt(plainText, secretKeySpec);

        String [] topscorers = decryptedText.split("\n");

        for(String str : topscorers){
            this.scorers.add(str);
        }
    }

    public String encrypt(String plainText, SecretKey secretKey) throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}
