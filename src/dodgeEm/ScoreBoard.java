package dodgeEm;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

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
    ArrayList<String> scorers = new ArrayList<>();

    /** FOR ENCRYPTION/DECRYPTION USING AES**/
    private String game = "Dodge'Em";
    private String SALT2 = "GSST";
    private byte[] key;
    private MessageDigest sha;
    private SecretKeySpec secretKeySpec;
    private static Cipher cipher;

    public ScoreBoard() throws Exception {
        this.font = loadFont("res/zig.ttf", 25f);
        this.font1 = loadFont("res/zig.ttf", 20f);
        /** Temporary top scorers **/
        scorers.add("rain 999");
        scorers.add("rain1 709");
        scorers.add("rain2 601");
        scorers.add("rain3 599");
        scorers.add("rain4 409");
        scorers.add("rain5 301");
        scorers.add("rain6 299");
        scorers.add("rain7 109");
        scorers.add("rain8 51");
        scorers.add("rain9 21");
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
        String [] str;
        graphics.setFont(this.font);

        graphics.setColor(new Color(192f,192f,192f,100f));
        graphics.drawRect(200,30,400,500);

        String string = "SCOREBOARD";
        graphics.drawString(string, Game.CENTER_X-(this.font.getWidth(string)/2), Game.CENTER_Y-250);
        float centerX= Game.CENTER_X-(this.font.getWidth(string)/2);

        graphics.setFont(this.font1);
        String string1 = "PLAYER";
        String string2 = "KILLS";

        graphics.drawString(string1, centerX+((this.font.getWidth(string1)/2)-90), Game.CENTER_Y-(250-40));
        graphics.drawString(string2, centerX+((this.font.getWidth(string2)/2)+90), Game.CENTER_Y-(250-40));
        float lcenterX= centerX+((this.font.getWidth(string1)/2)-90);
        float rcenterX= centerX+((this.font.getWidth(string2)/2)+90);
        str = scorers.get(0).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-120));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-120));
        str = scorers.get(1).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-160));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-160));
        str = scorers.get(2).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-200));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-200));
        str = scorers.get(3).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-240));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-240));
        str = scorers.get(4).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-280));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-280));
        str = scorers.get(5).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-320));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-320));
        str = scorers.get(6).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-360));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-360));
        str = scorers.get(7).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-400));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-400));
        str = scorers.get(8).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-440));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-440));
        str = scorers.get(9).split(" ");
        graphics.drawString(str[0], lcenterX+((this.font.getWidth(str[0])/2)/2), Game.CENTER_Y-(290-480));
        graphics.drawString(str[1], rcenterX+((this.font.getWidth(str[1])/2)/2), Game.CENTER_Y-(290-480));




        //centering
        // graphics.drawString(string, Game.CENTER_X-(this.font.getWidth(string)/2), Game.CENTER_Y-(this.font.getHeight(string)/2));
        //graphics.drawRect(10, 10, 10, 10);

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

    public ArrayList<String> readScorersArray() throws Exception{
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
            scorers.add(str);
        }
        return scorers;
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
