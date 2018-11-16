package ChatPrjTest;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Calendar;
import java.io.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/*
 * 	Show the window that 
 * 	Chat with other
 * 
 * 
 */


public class ChatWindow extends JFrame implements Runnable{
	MainLanChat mChat;
	String[] name;
	JTextArea messageArea;
	JTextField sendField;
	JButton sendButton;
	JButton sendFileButton;
	String nameWin;
	ChatWindow cW = this;
	boolean isOn = true;
	
	public ChatWindow(MainLanChat m, String[] n) {
		mChat = m;
		name = n;
		JPanel msgPanel = new JPanel();
		messageArea = new JTextArea(12, 30);
		messageArea.setEditable(false);
		Font defaultFont = new Font("SansSerif", Font.PLAIN, 16);
		messageArea.setFont(defaultFont);
		messageArea.setWrapStyleWord(true);
		messageArea.setLineWrap(true);
		JScrollPane scroller = new JScrollPane(messageArea);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		msgPanel.add(scroller);
		scroller.setBorder(new TitledBorder("Message Area"));
		sendField = new JTextField(30);
		sendField.setFont(defaultFont);
		JPanel sendPanel = new JPanel();
		sendPanel.add(sendField);
		sendPanel.setBorder(new TitledBorder("Input Field"));
		sendButton = new JButton("Send");
		sendFileButton = new JButton("Send File");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendButton);
		buttonPanel.add(sendFileButton);
		add(msgPanel, BorderLayout.NORTH);
		add(sendPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
		
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sendMessage();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		sendField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER)
					sendButton.doClick();
			}
		});
		
		sendFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new SendFile(cW, name[1]).start();
				} catch (Exception e1) {
					e1.printStackTrace();
				};
			}
		});
		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		setTitle("Chat with " + name[0] + "/" + name[1]);
		nameWin = name[0];
		// name[0] is user's name, name[1] is user's IP
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				e.getWindow().dispose();
			}
		});
		*/
		addWindowListener(new MyWindowListener());
		
		
		setVisible(true);
	}
	
	private void sendMessage() {
		if (sendField.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "Message cannot be EMPTY!", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String msg = sendField.getText().trim();
		String mString = msg;
		//msg = "MSG" + ":" + name[0] + ":" + name[1] + ":" + msg;
		
		try {
			msg = "MSG" + ":" + mChat.chatList.nameFile.getText() + ":" + InetAddress.getLocalHost().getHostAddress() + ":" + msg;
			//SocketAddress target = (SocketAddress) mChat.chatList.userMap.get(name[0]);
			
			/*
			InetAddress ia = InetAddress.getByName(name[1]);
			
			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, ia, 6666);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
			*/
			
			//if (!mChat.chatList.winList.get(nameWin)) {
			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(mChat.IPs), 3333);
			DatagramSocket socket1 = new DatagramSocket();
			socket1.send(packet);
			socket1.close();
			//mChat.chatList.winList.put(nameWin, true);
			//}
		
		
			/*
			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(mChat.IPs), 3333);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
			*/
			
			//else {
			DatagramPacket packet2 = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(mChat.IPs), 6666);
			DatagramSocket socket2 = new DatagramSocket();
			socket2.send(packet2);
			socket2.close();
			//}
			/*
			DatagramPacket packet3 = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(name[1]), 6666);
			DatagramSocket socket3 = new DatagramSocket();
			socket3.send(packet3);
			socket3.close();
			*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		messageArea.append("I - " + getTime() + " :\n" + mString + "\n");
		sendField.setText("");
		sendField.requestFocus();
		messageArea.setCaretPosition(messageArea.getText().length());
		System.out.println("Sent: " + msg);
		
	}
	
	DatagramSocket socket = null;
	public void run() {
		
		String msg;
	
		byte[] inbuf = new byte[50];
		
		
		try {
			socket = new DatagramSocket(6666);
			while (isOn) {
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
				
				if (status[0].trim().equals("MSG")) {
					if (!mChat.chatList.nameFile.getText().equals(status[1])) {
						if (status[1].equals(nameWin)) {
							String m = status[1] + "|" + status[2] + " sent a msg to you";
							System.out.println("Get: " + m);
						
							messageArea.append(status[1] + " - " + getTime() + " :\n" + status[3] + "\n");
							messageArea.setCaretPosition(messageArea.getText().length());
						}
					}
				}
				
				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	class MyWindowListener extends WindowAdapter{  
	    @Override  
	    public void windowClosing(WindowEvent e) {  
	    	//mChat.chatList.chatWinList.remove(nameWin);
	        mChat.chatList.winList.put(nameWin, false);
	        isOn = false;
	        socket.close();
	        super.windowClosing(e);  
	        
	    }  
	}
	
	
	public String getTime() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR); 
		int month = c.get(Calendar.MONTH); 
		int date = c.get(Calendar.DATE); 
		int hour = c.get(Calendar.HOUR_OF_DAY); 
		String minute = Integer.toString(c.get(Calendar.MINUTE)); 
		String second = Integer.toString(c.get(Calendar.SECOND)); 
		
		if (Integer.parseInt(minute) < 10)
			minute = "0" + minute + "";
		if (Integer.parseInt(second) < 10)
			second = "0" + second + "";
		return year + ":" + month + ":" + date + ", " + hour + ":" + minute + ":" + second;
	}
	
}
