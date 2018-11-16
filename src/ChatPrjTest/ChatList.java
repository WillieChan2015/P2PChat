package ChatPrjTest;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.*;
import java.net.*;

/*
 * 	Show the online users 	
 *
 */

public class ChatList extends JFrame {
	
	MainLanChat mChat;
	JList onlinechatList;
	DefaultListModel onlineListModel;
	//Map<String, SocketAddress> userMap = new HashMap<>();
	Map<String, String> userMap = new HashMap<>();
	JScrollPane listPanel;
	JButton startButton;
	JButton stopButton;
	JTextField nameFile;
	ArrayList<Thread> threadList;
	//ArrayList<String> winList = new ArrayList<>();
	//Map<String, ChatWindow> winList = new HashMap<>();
	Map<String, Boolean> winList = new HashMap<>();
	//ArrayList<String> chatWinList;
	private Thread cServer;
	private Thread sHandler;
	
	
	public ChatList(MainLanChat mFrame) throws Exception {
		mChat = mFrame;
		Container infoPanel = this.getContentPane();
		//JPanel infoPanel = new JPanel();
		infoPanel.setLayout(null);
		
		// Show the infos about Address
		JLabel ipLabel = new JLabel("Your IP      :");
		JLabel ipLabelInfo = new JLabel(InetAddress.getLocalHost().getHostAddress());
		infoPanel.add(ipLabel);
		infoPanel.add(ipLabelInfo);
		ipLabel.setBounds(new Rectangle(15, 15, 80, 20));
		ipLabelInfo.setBounds(new Rectangle(90, 15, 100, 20));
		JLabel portLabel = new JLabel("Your Port  :");
		JLabel portLabelInfo = new JLabel("3333");
		infoPanel.add(portLabel);
		infoPanel.add(portLabelInfo);
		portLabel.setBounds(new Rectangle(15, 35, 80, 20));
		portLabelInfo.setBounds(new Rectangle(90, 35, 40, 20));
		JLabel nameLabel = new JLabel("Your name:");
		infoPanel.add(nameLabel);
		nameLabel.setBounds(new Rectangle(15, 55, 80, 20));
		nameFile = new JTextField(80);
		infoPanel.add(nameFile);
		nameFile.setBounds(new Rectangle(90, 55, 90, 20));
		startButton = new JButton("Start");
		infoPanel.add(startButton);
		startButton.setBounds(new Rectangle(200, 25, 80, 20));
		startButton.setForeground(Color.RED);
		stopButton = new JButton("Stop");
		infoPanel.add(stopButton);
		stopButton.setBounds(new Rectangle(285, 25, 80, 20));
		stopButton.setForeground(Color.RED);
		stopButton.setEnabled(false);
		
		threadList = new ArrayList();
		// chatWinList = new ArrayList<>();
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (nameFile.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "Please input the user name!", "ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (nameFile.getText().trim().length() > 10) {
					JOptionPane.showMessageDialog(null, "User name is too long!", "NOTICE", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (nameFile.getText().trim().contains(".") || nameFile.getText().trim().contains(":")) {
					JOptionPane.showMessageDialog(null, "User name is  illegal!(Contains '.' or ':')", "NOTICE", JOptionPane.ERROR_MESSAGE);
					return;
				}
				System.out.println("Start");
				
				cServer = new ChatServer(mChat);
				cServer.start();
				threadList.add(cServer);
				sHandler = new ServerHandler(mChat);
				sHandler.start();
				threadList.add(sHandler);
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				nameFile.setEditable(false);
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				while (!threadList.isEmpty()) {
					Thread t = (Thread) threadList.get(0);
					t.stop();
					threadList.remove(0);
				}
				stopServer();
				System.out.println("Stop");
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				winList.clear();
				//chatWinList.clear();
				nameFile.setEditable(true);
			}
		});
		
		JButton reButton = new JButton("Refresh");
		infoPanel.add(reButton);
		reButton.setBounds(new Rectangle(285, 55, 70, 20));
		reButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
		//reButton.setEnabled(false);
		reButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userMap.clear();
				onlineListModel.clear();
				System.out.println("Refresh");
			}
		});
		
		// Show the online users
		listPanel = new JScrollPane();
		
		onlineListModel = new DefaultListModel();
		onlinechatList = new JList<>();
		onlinechatList.setModel(onlineListModel);
		listPanel.setViewportView(onlinechatList);
		infoPanel.add(listPanel);
		listPanel.setBounds(new Rectangle(15, 80, 350, 400));
		listPanel.setBorder(new TitledBorder("Online Pals"));
		
		onlinechatList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2 && onlinechatList.getSelectedValue() != null){
                	try {
	                	System.out.println("Double Clicked...");
	                	String slct = onlinechatList.getSelectedValue().toString();
	                	System.out.println("Select " + slct);
	                	String[] status = slct.split("\\|");
	                	Runnable r = new ChatWindow(mChat, status);
	                	new Thread(r).start();
	                	winList.put(status[0], true);
	                	//chatWinList.add(status[0]);
                	} catch (Exception e2) {}
                }
			}
		});
		
		
		// Menu
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		//menu.setIcon(new ImageIcon("./icon.png"));
		menuBar.add(menu);
		infoPanel.add(menuBar);
		menuBar.setBackground(new Color(198, 226, 255));
		menuBar.setBounds(new Rectangle(15, 485, 350/*48*/, 25));
		JMenuItem aboutItem = new JMenuItem("About");
		menu.add(aboutItem);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//AboutChat();
				AboutDialog dialog = new AboutDialog(mChat.chatList);
				dialog.setVisible(true);
			}
		});
		
		
		setSize(400, 560);
		infoPanel.setBackground(new Color(198, 226, 255));
		setTitle("Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 3 * 2, screenSize.height / 6);
		//Image icon = Toolkit.getDefaultToolkit().getImage("");
		//this.setIconImage(icon);
		setResizable(false);
		setVisible(true);
	}
	
	private void AboutChat() {
		JFrame aFrame = new JFrame("About");
		
	}
	
	private void stopServer() {
		String localIP;
		try {
			localIP = InetAddress.getLocalHost().getHostAddress();
			localIP = "OFFIP" + ":" + nameFile.getText() + ":" + localIP + "";
			System.out.println("Local IP: " + localIP + " off");
			DatagramPacket packet = new DatagramPacket(localIP.getBytes(), localIP.getBytes().length, InetAddress.getByName(mChat.IPs), 3333);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();
			userMap.clear();
			onlineListModel.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Send stop cmd...");
	}
	
}

class AboutDialog extends JDialog {
	public AboutDialog(JFrame owner) {
		super(owner, "About", true);
		add(new JLabel("<html><h1><i>Lan Chat</i></h1><hr>By Mr. C</html>"), BorderLayout.CENTER);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		setSize(300, 200);
	}
}
