import java.net.InetAddress;

//ref: circlewars
//author: JACHermocilla

public class Player {
	//netAdress of player
	private InetAddress address;
	//port number of player
	private int port;
	//name of player
	private String name;

	public Player(String name,InetAddress address, int port){
		this.address = address;
		this.port = port;
		this.name = name;
	}

	public InetAddress getAddress(){
		return address;
	}

	public int getPort(){
		return port;
	}

	public String getName(){
		return name;
	}
}
