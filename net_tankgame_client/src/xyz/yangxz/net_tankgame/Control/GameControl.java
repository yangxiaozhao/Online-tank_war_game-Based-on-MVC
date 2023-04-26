package xyz.yangxz.net_tankgame.Control;

import java.awt.Dialog;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import xyz.yangxz.net_tankgame.Model.*;
import xyz.yangxz.net_tankgame.View.GameFrame;

public class GameControl implements Runnable{
	
	private static Socket socket=null;
	private static BufferedOutputStream out = null;
	private static BufferedReader in=null;
	
	private static WarData wardata;
	private static GameFrame frame=null;
	private static ShutControl shutControl;
	private Timer timer;
	private KeyListener listen;
	

	public GameControl(GameFrame frame,WarData wardata) {
		this.frame=frame;
		this.wardata=wardata;
		new Thread(new ShutControl()).start();
		try{
			//InetAddress serverIP = InetAddress.getByName("82.156.245.74");
			//InetAddress serverIP = InetAddress.getByName("39.106.89.66");
			InetAddress serverIP = InetAddress.getByName("localhost");
			int port = 6666;
			// 创建应该socket连接
			socket = new Socket(serverIP, port);
			// IO流
			out = new BufferedOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        System.out.println("已向服务器发送连接...");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			closeSocket();
			System.out.println("error: "+e1.getMessage());
		}
		
		// 监听按键，并发送数据
		listen=new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
		        int keyCode = e.getKeyCode();
		        char c = 0;
		        switch (keyCode) {
		        case KeyEvent.VK_UP:
		        	c='U';
					break;
				case KeyEvent.VK_DOWN:
					c='D';
					break;
				case KeyEvent.VK_LEFT:
					c='L';
					break;
				case KeyEvent.VK_RIGHT:
					c='R';
					break;
				case KeyEvent.VK_SPACE:
					if(shutControl.shuting) {
						c='S';
						playMusic("src/music/fire.wav");
						shutControl.shuting=false;
					}
					break;
		        }
		        try {
					out.write((int)c);
					out.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println(e1.getMessage());
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
		        int keyCode = e.getKeyCode();
		        char c = 0;
		        switch (keyCode) {
		        case KeyEvent.VK_UP:
		        	c='u';
					break;
				case KeyEvent.VK_DOWN:
					c='d';
					break;
				case KeyEvent.VK_LEFT:
					c='l';
					break;
				case KeyEvent.VK_RIGHT:
					c='r';
					break;
		        }
		        try {
					out.write((int)c);
					out.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		
		frame.addKeyListener(listen);
		
	}

	@Override
	// 接收服务器传来的游戏数据
	public void run() {
		// TODO Auto-generated method stub
		Gson gson=new Gson();
		try {
			while (true) {
				String str=in.readLine();
				JsonObject json = new Gson().fromJson(str, JsonObject.class);
		        int type = json.get("type").getAsInt();
		        String data = json.get("data").getAsString();
		        System.out.println(str+","+data);
		        switch (DataType.values()[type]) {
		            case TANK:
		                // 处理tank对象
		                wardata.setTanks(gson.fromJson(data, new TypeToken<List<Tank>>() {}.getType()));
		                break;
		            case BULLET:
		                // 处理bullet对象
		            	wardata.setBullets(gson.fromJson(data, new TypeToken<List<Bullet>>() {}.getType()));
		                break;
		            case WALL:
		            	// 处理Wall对象
		            	wardata.setWalls(gson.fromJson(data, new TypeToken<List<Wall>>() {}.getType()));
		            	break;
		            case STRING:
		                // 处理普通字符串
		            	switch(data) {
		            	case "Begin":
		            		playMusic("src/music/start.wav");
		            		break;
		            	case "Over":
		            		System.out.println("you lose");
		            		frame.removeKeyListener(listen);
		            		closeSocket();
		            		System.out.println("---------游戏结束！您输了！---------");
		            		break;
		            	case "Victory":
		            		System.out.println("you win");
		            		frame.removeKeyListener(listen);
		            		closeSocket();
		            		System.out.println("---------游戏结束！您胜利了！---------");
		            		break;
		            	case "Defeat":
		            		playMusic("src/music/bang.wav");
		            		break;
		            	case "SentOver":
		            		frame.repaint();
		            		break;
		            	}
		                break;
		        }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			closeSocket();
			System.out.println("-----------服务器已关闭！-----------");
		}
	}

	public static void playMusic(String filepath) {
		// TODO Auto-generated method stub
		Clip music = null;
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
			music = AudioSystem.getClip();
			music.open(audioInputStream);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		music.start();
	}
	
	// 关闭与服务器的连接
	public static void closeSocket() {
		try {
			if(in!=null)
				in.close();
			if(out!=null)
				out.close();
			if(socket!=null)
				socket.close();
		}catch (IOException e) {
			// TODO: handle exception
			System.out.println("error: "+e.getMessage());
		}
	}
}

enum DataType {
    TANK, BULLET, WALL, STRING
}
