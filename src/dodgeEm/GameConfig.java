package dodgeEm;

public interface GameConfig {
	String APP_NAME = "Dodge 'Em 0.01";
    String HOST = "localhost";
    int playerNum=3;
	int PORT = 3000;

	final int GAME_START=0;
	static final int IN_PROGRESS=1;
	final int GAME_END=2;
	final int WAITING_FOR_PLAYERS=3;
}