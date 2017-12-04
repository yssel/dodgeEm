package dodgeEm;

public interface GameConfig {
	String APP_NAME = "Dodge 'Em 0.01";
    String HOST = "192.168.0.20";
    int playerNum=2;
	int PORT = 3000;

	final int GAME_START=0;
	static final int IN_PROGRESS=1;
	final int GAME_END=2;
	final int WAITING_FOR_PLAYERS=3;
}