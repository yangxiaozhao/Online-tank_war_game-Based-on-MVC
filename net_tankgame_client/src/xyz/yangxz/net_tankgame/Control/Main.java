package xyz.yangxz.net_tankgame.Control;

import java.io.*;
import java.net.*;

import xyz.yangxz.net_tankgame.Model.WarData;
import xyz.yangxz.net_tankgame.View.GameFrame;

public class Main {
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		WarData data=new WarData();
		GameFrame frame=new GameFrame(data);
		new Thread(new GameControl(frame, data)).start();
		
	}
}
