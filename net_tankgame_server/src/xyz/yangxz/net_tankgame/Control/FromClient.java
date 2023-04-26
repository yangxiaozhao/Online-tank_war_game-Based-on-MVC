package xyz.yangxz.net_tankgame.Control;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

import com.google.gson.Gson;

import xyz.yangxz.net_tankgame.Model.*;

// 处理玩家信息
public class FromClient implements Runnable {
	// 当前线程是否存活（对应的客户端连接是否正常）
	//public boolean isAlive;
	
	// 连接的玩家地址、对应的坦克
	private Socket client;
	private Tank tank;
	// 客户端传来的输入信息
	private BufferedInputStream input;
	
	public FromClient(Socket socket, Tank player){
		this.client = socket;
		this.tank = player;
		try {
			input=new BufferedInputStream(client.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("-----------从客户端建立输入流失败！-----------");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(tank.team+" is ready");
		while(true) {
			try {
				char c=(char)input.read();
				System.out.println(tank.team+" : "+(input!=null));
				switch(c) {
				case 'U':
					tank.direction = 0;
					tank.moving = true;
					break;
				case 'D':
					tank.direction = 180;
					tank.moving = true;
					break;
				case 'L':
					tank.direction = 270;
					tank.moving = true;
					break;
				case 'R':
					tank.direction = 90;
					tank.moving = true;
					break;
				case 'S':
					Bullet bullet = new Bullet(tank.team, 8,tank.x,tank.y,tank.direction);
					System.out.println(tank.team+ " shuting");
					WarData.addBullet(bullet);
					break;
				case 'u':
				case 'd':
				case 'l':
				case 'r':
					tank.moving = false;
					break;
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("test 1:"+e.getMessage());
				Thread.currentThread().interrupt();
				break;
			} 
		}
		try {
			if(input!=null) {
				input.close();
			}
			if(client!=null) {
				client.close();
				System.out.println("client is not null");
			}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("test 2:"+e.getMessage());
		}
	}
}
