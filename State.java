import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//ref: circlewars
//author: JACHermocilla

public class State{
	// This is a map(key-value pair) of <player name,NetPlayer>
	private Map<String, Player> players=new HashMap<String, Player>();
	
	public State(){}

	public void update(String name, Player player){
		players.put(name,player);
	}
	
	public Map getPlayers(){
		return players;
	}
}
