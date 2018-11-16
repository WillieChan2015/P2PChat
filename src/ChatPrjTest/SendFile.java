package ChatPrjTest;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class SendFile extends Thread {
	ChatWindow cWin;
	int length = 0;
	double sumL = 0;
	byte[] sendBytes = null;
	Socket socket = null;
	DataOutputStream dos = null;
	FileInputStream fis = null;
	boolean bool = false;
	String sendHostIP;
	JProgressBar progressbar;

	public SendFile(ChatWindow w, String hIP) {
		cWin = w;
		sendHostIP = hIP;
	}

	void Notice(String fn, long size) throws Exception {
		String[] s = fn.split("\\.");
		String fileStyle = s[s.length - 1];

		String msg = "SF:" + cWin.mChat.chatList.nameFile.getText() + ":" + InetAddress.getLocalHost().getHostAddress()
				+ ":" + fileStyle + ":" + cWin.nameWin + ":" + size + ":";
		System.out.println("Send File:" + msg);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
				InetAddress.getByName(cWin.mChat.IPs), 3333);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}

	private void InitPB() {
		JFrame frame = new JFrame("Sending");
		JPanel panel = new JPanel();
		progressbar = new JProgressBar(0, 100);

		// 显示当前进度值信息
		progressbar.setStringPainted(true);
		// 设置进度条边框不显示
		progressbar.setBorderPainted(false);
		// 设置进度条的前景色
		progressbar.setForeground(Color.BLUE);
		// 设置进度条的背景色
		progressbar.setBackground(new Color(188, 190, 194));
		// progressbar.setSize(100, 20);
		panel.add(progressbar);

		progressbar.setValue(0);

		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	public void run() {
		try {
			File file;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.showOpenDialog(null);
			if ((file = fileChooser.getSelectedFile()) == null) {
				JOptionPane.showMessageDialog(null, "Cancel!", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			double bytes = file.length();
			long fileSize = (long) (bytes / 1024);
			System.out.println("File size: " + fileSize + "KB");
			Notice(file.getAbsolutePath(), fileSize);
			// if (ConfirmRecv()) {
			int t = 1;
			while (t <= 70) {
				try {
					long l = file.length();
					socket = new Socket();
					// socket.connect(new InetSocketAddress(sendHostIP, 3334));
					SocketAddress socketAddress = new InetSocketAddress(sendHostIP, 3334);
					socket.connect(socketAddress, 800);
					if (socket.isConnected())
						System.out.println("Connected succeed");
					else
						System.out.println("Connected failed");

					dos = new DataOutputStream(socket.getOutputStream());
					fis = new FileInputStream(file);
					sendBytes = new byte[1024];
					int i, j = -1;
					InitPB();
					System.out.println("Sent: ");
					while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
						sumL += length;
						i = (int) ((sumL / l) * 100);
						if (i != j) {
							System.out.print(i + "% ");
							j = i;
						}
						progressbar.setValue(i);
						// System.out.println("已传输：" + ((sumL / l) * 100) +
						// "%");
						dos.write(sendBytes, 0, length);
						dos.flush();
					}
					System.out.println();
					if (sumL == l) {
						bool = true;
					}
					socket.shutdownOutput();
				} catch (Exception e) {
					// System.out.println("Sending file failed.");
					bool = false;
					// e.printStackTrace();
					System.out.println("Waiting..." + t);
					t++;
					sleep(1000);
				} finally {
					if (dos != null)
						dos.close();
					if (fis != null)
						fis.close();
					if (socket != null)
						socket.close();
					if (bool)
						break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(bool ? "Succeed" : "Failed");
		JOptionPane.showMessageDialog(null, bool ? "Succeed" : "Failed, timed out!");
	}
}
