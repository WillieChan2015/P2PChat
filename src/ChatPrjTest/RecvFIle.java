package ChatPrjTest;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.*;

public class RecvFIle extends Thread {
	String fileStyle;
	JProgressBar progressbar;
	long fileSize;

	public RecvFIle(String fn, long size) {
		fileStyle = fn;
		fileSize = size;
		System.out.println("Receive-File size: " + fileSize + "KB");
	}

	void receiveFile(Socket socket) throws IOException {
		byte[] inputByte = null;
		int length = 0;
		DataInputStream dis = null;
		FileOutputStream fos = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(null);
		String path = chooser.getSelectedFile().getPath();
		if (!path.endsWith("\\"))
			path = path + "\\";
		String filePath = path + getDate() + "SJ" + new Random().nextInt(10000) + "." + fileStyle;
		try {
			try {
				dis = new DataInputStream(socket.getInputStream());
				/*
				 * 文件存储位置
				 */
				fos = new FileOutputStream(new File(filePath));
				inputByte = new byte[1024];
				System.out.println("Starting receiving file...");
				double sum = 0;
				int i = 0, j = -1;
				InitPB();
				while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
					sum += length;
					i = (int) (((sum / 1024) / fileSize) * 100);
					if (i != j) {
						System.out.print(i + "% ");
						j = i;
					}
					progressbar.setValue(i);
					fos.write(inputByte, 0, length);
					fos.flush();
				}
				
				System.out.println("Receieving file succeed: " + filePath);
				JOptionPane.showMessageDialog(null, "Received file succeed!");
			} finally {
				if (fos != null)
					fos.close();
				if (dis != null)
					dis.close();
				if (socket != null)
					socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void InitPB() {
		JFrame frame = new JFrame("Receiving");
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

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return df.format(new Date());
	}

	public void run() {
		try {
			ServerSocket server = new ServerSocket(3334);
			// ServerSocket server = new ServerSocket(3334, 0,
			// InetAddress.getLocalHost());
			System.out.println("Waiting for receiving...");
			server.setSoTimeout(60000);
			Socket socket = server.accept();
			System.out.println("Starting receiving file...");
			receiveFile(socket);
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
