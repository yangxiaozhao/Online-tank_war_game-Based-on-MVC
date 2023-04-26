package xyz.yangxz.net_tankgame.Control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import xyz.yangxz.net_tankgame.Model.*;

// 游戏控制区
public class GameControl {
	
	private static int clientCount = 0;
	private static Socket client1;
	private static Socket client2;
	public static boolean isAlive=true;
	
	public GameControl() {
		try {
			// 启动服务器
			WarData.serverSocket = new ServerSocket(6666);
			System.out.println("服务器已启动，等待客户端连接...");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("-----------服务器启动失败！-----------");
		}

		clientCount=0;
		while (clientCount < 2) {
			// 客户端接入
			Socket clientSocket;
			try {
				clientSocket = WarData.serverSocket.accept();
				System.out.println("客户端已连接，IP地址为：" + clientSocket.getInetAddress().getHostAddress());
				switch (clientCount) {
				// 第一台客户端接入
				case 0:
					this.client1 = clientSocket;
					clientCount++;
					System.out.println("当前客户端数量为：" + clientCount);
					WarData.player1 = new Tank(344, 640, Team.PLAYER1, 0, WarData.player_speed, WarData.player_hp);
					break;
				// 第二台客户端接入
				case 1:
					this.client2 = clientSocket;
					clientCount++;
					System.out.println("当前客户端数量为：" + clientCount);
					WarData.player2 = new Tank(542, 640, Team.PLAYER2, 0, WarData.player_speed, WarData.player_hp);
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
		ToClients toClients=new ToClients(client1, client2);
		//Thread toClientsThread=new Thread(toClients);
		
		try {
			toClients.sentToClient(DataType.STRING, Message.begin());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		Thread fromClient1Thread=new Thread(new FromClient(client1, WarData.player1));
		Thread fromClient2Thread=new Thread(new FromClient(client2, WarData.player2));
		toClients.start();
		fromClient1Thread.start();
		fromClient2Thread.start();
		
	}

//	@Override
//	public void run() {
//		while(isAlive) {
//			// TODO Auto-generated method stub
//			clientCount=0;
//			while (clientCount < 2) {
//				// 客户端接入
//				Socket clientSocket;
//				try {
//					clientSocket = WarData.serverSocket.accept();
//					System.out.println("客户端已连接，IP地址为：" + clientSocket.getInetAddress().getHostAddress());
//					switch (clientCount) {
//					// 第一台客户端接入
//					case 0:
//						this.client1 = clientSocket;
//						clientCount++;
//						System.out.println("当前客户端数量为：" + clientCount);
//						WarData.player1 = new Tank(344, 640, Team.PLAYER1, 0, WarData.player_speed, WarData.player_hp);
//						break;
//					// 第二台客户端接入
//					case 1:
//						this.client2 = clientSocket;
//						clientCount++;
//						System.out.println("当前客户端数量为：" + clientCount);
//						WarData.player2 = new Tank(542, 640, Team.PLAYER2, 0, WarData.player_speed, WarData.player_hp);
//						break;
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			
//		}
//			ToClients toClients=new ToClients(client1, client2);
//			//Thread toClientsThread=new Thread(toClients);
//			toClients.start();
//			try {
//				toClients.sentToClient(DataType.STRING, Message.begin());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				System.out.println(e.getMessage());
//			}
//			Thread fromClient1Thread=new Thread(new FromClient(client1, WarData.player1));
//			Thread fromClient2Thread=new Thread(new FromClient(client2, WarData.player2));
//			fromClient1Thread.start();
//			fromClient2Thread.start();
//			try {
//				System.out.println("已挂起");
//				wait();
//				System.out.println("已挂起");
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
}


