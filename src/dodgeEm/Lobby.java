package dodgeEm;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.net.DatagramPacket;

public class Lobby extends BasicGameState {
    int playerState = 0;
    int currentPlayer = 0;
    int maxPlayer = 0;

    public Lobby(int state){

    }
    @Override
    public int getID() {
        return 2;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {

    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        graphics.drawString("Waiting for " + currentPlayer + "/" + maxPlayer + " players", Game.CENTER_X, Game.CENTER_Y);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        try{
            Thread.sleep(1);
        }catch(Exception e){
            e.printStackTrace();
        }

        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try{
            MainMenu.clientSocket.receive(packet);
        }catch (Exception e){
//            e.printStackTrace();
        }

        String serverData = new String(buf);
        serverData = serverData.trim();

        String tokens[] = serverData.split(",");
        if(tokens[0].length() > 0){
            playerState = Integer.parseInt(tokens[0]);
        }else{
            playerState = -1;
        }

        if(playerState == GameServer.CONNECTED_PLAYER && tokens.length > 1){
            String tokens2[] = tokens[1].split("-");
            if(tokens2.length > 0){
                System.out.println(tokens2[0]);
                System.out.println(tokens2[1]);
                this.currentPlayer = Integer.parseInt(tokens2[0]);
                this.maxPlayer = Integer.parseInt(tokens2[1]);
            }
        }


    }
}
