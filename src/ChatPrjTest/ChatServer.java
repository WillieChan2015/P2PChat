package ChatPrjTest;

import java.io.*;
import java.net.*;
import java.util.Map.Entry;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * 
 *  
 * 
 */
public class ChatServer extends Thread {
	MainLanChat mlc;
	String localIP;

	public ChatServer(MainLanChat mf) {
		super();
		mlc = mf;

	}

	public void run() {
		try {
			localIP = InetAddress.getLocalHost().getHostAddress();
			localIP = "OLIP" + ":" + mlc.chatList.nameFile.getText() + ":" + localIP + ":";
			System.out.println("Local IP: " + localIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(localIP.getBytes(), localIP.getBytes().length,
						InetAddress.getByName(mlc.IPs), 3333);
				DatagramSocket socket = new DatagramSocket();
				socket.send(packet);
				socket.close();
				sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

class ServerHandler extends Thread {
	MainLanChat mlc;
	String msg;

	public ServerHandler(MainLanChat m) {
		mlc = m;
	}

	public void run() {
		byte[] inbuf = new byte[50];

		DatagramSocket socket;
		try {
			socket = new DatagramSocket(3333);
			while (true) {
				DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length);
				synchronized (socket) {
					try {
						socket.receive(packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				msg = new String(packet.getData());
				for (int i = 0, j = inbuf.length; i < j; i++)
					inbuf[i] = (byte) 0;

				String[] status = msg.split(":");
				// User online
				if (status[0].trim().equals("OLIP") && !status[1].trim().equals(mlc.chatList.nameFile.getText())) {
					if (mlc.chatList.userMap.get(status[1].trim()) == null) {
						System.out.println(msg + "- connected!");

						mlc.chatList.userMap.put(status[1].trim(), status[2].trim());
						mlc.chatList.winList.put(status[1].trim(), false);
						mlc.chatList.onlineListModel.addElement(status[1].trim() + "|" + status[2].trim());
					}
				}
				// User offline
				if (status[0].trim().equals("OFFIP") && !status[1].trim().equals(mlc.chatList.nameFile.getText())) {
					System.out.println(msg + "- disconnected!");
					if (mlc.chatList.userMap.get(status[1].trim()) != null) {
						mlc.chatList.userMap.remove(status[1].trim());
						mlc.chatList.onlineListModel.removeElement(status[1].trim() + "|" + status[2].trim());
						mlc.chatList.winList.remove(status[1]);
					}
				}

				if (status[0].trim().equals("MSG")) {
					if (mlc.chatList.userMap.containsKey(status[1]))
						if (!mlc.chatList.winList.get(status[1])) {
							String m = status[1] + "|" + status[2] + " sent a msg to you";
							System.out.println("Get: " + m);
					
							JOptionPane.showMessageDialog(null, m);
							while (!mlc.chatList.winList.get(status[1])) {
								sleep(1000);
							}
							System.out.println("============\n" + msg + "\n" + "============");

							String lip = InetAddress.getLocalHost().getHostAddress();
							DatagramPacket packet2 = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
									InetAddress.getByName(lip), 6666);
							DatagramSocket socket2 = new DatagramSocket();
							socket2.send(packet2);
							socket2.close();
						}
				}

				if (status[0].trim().equals("SF") && status[4].trim().equals(mlc.chatList.nameFile.getText().trim())) {
					String msg = status[1] + "|" + status[2] + " is sending a file to you";
					int cf = JOptionPane.showConfirmDialog(null, msg, "Confirm", JOptionPane.YES_NO_OPTION);
					if (cf == 0) { // Selected YES
						long l = Long.parseLong(status[5]);
						System.out.println("File size: " + l + "KB");
						new RecvFIle(status[3], l).start();
					}
				}

				sleep(200);
			}
		} catch (Exception e) {
		}
	}

	public String getTime() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		return year + ":" + month + ":" + date + ", " + hour + ":" + minute + ":" + second + "\n";
	}

}
