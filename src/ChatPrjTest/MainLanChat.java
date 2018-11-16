package ChatPrjTest;



import javax.swing.*;

import java.net.*;

public class MainLanChat extends JFrame {
	ChatList chatList;
	ChatWindow chatWindow;
	String IPs = null;
	//String IPs = "192.168.11.255";
	String localName;
	
	
	public MainLanChat() throws Exception {
		String ip = InetAddress.getLocalHost().getHostAddress();
		String[] stats = ip.split("\\.");
		ip = stats[0] + "." + stats[1] + "." + stats[2] + "." + "255";
		IPs = ip;
		chatList = new ChatList(this);
		//IP = InetAddress.getLocalHost().getHostAddress();
		
		
	}
	
	public static void main(String[] args) throws Exception {
		new MainLanChat();
	}
}
