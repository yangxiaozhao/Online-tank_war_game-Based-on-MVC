package xyz.yangxz.net_tankgame.Control;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import xyz.yangxz.net_tankgame.Model.Message;
import xyz.yangxz.net_tankgame.Model.Tank;
import xyz.yangxz.net_tankgame.Model.WarData;

public class ToClients extends Thread{

	// 连接的玩家地址
	private Socket client1;
	private Socket client2;
	// 传给客户端数据
	private BufferedWriter output1;
	private BufferedWriter output2;
	private Gson gson=new Gson();

	public ToClients(Socket client1, Socket client2) {
		super();
		this.client1 = client1;
		this.client2 = client2;
		try {
			output1 = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
			output2 = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("-----------向客户端建立输出流失败！-----------");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if (WarData.update()) {
				updateClient();
			} else {
				// 如果游戏已结束，则发送终止信号
				updateClient();
				try {
					if (WarData.enemyNum_Max == WarData.dead_enemy)
						sentToClient(DataType.STRING, Message.victory());
					else
						sentToClient(DataType.STRING, Message.gameOver());
				} catch (Exception e1) {
					// TODO: handle exception
					System.out.println(e1.getMessage());
				} finally {
					try {
						if (output1 != null)
							output1.close();
						if (output2 != null)
							output2.close();
						if (client1 != null)
							client1.close();
						if (client2 != null)
							client2.close();
					} catch (IOException e1) {
						// TODO: handle exception
						System.out.println("error: " + e1.getMessage());
					}
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 向客户端发送游戏数据
	protected void updateClient() {
		// 封装列表信息，序列号为json串发送给客户端
		String tanks = gson.toJson(WarData.tanks);
		String bullets = gson.toJson(WarData.bullets);
		String walls = gson.toJson(WarData.walls);
		try {
			sentToClient(DataType.TANK, tanks);
			sentToClient(DataType.BULLET, bullets);
			sentToClient(DataType.WALL, walls);
			sentToClient(DataType.STRING, "SentOver");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			try {
				if (output1 != null)
					output1.close();
				if (output2 != null)
					output2.close();
				if (client1 != null)
					client1.close();
				if (client2 != null)
					client2.close();
				Thread.currentThread().interrupt();
				notify();
			} catch (IOException e1) {
				// TODO: handle exception
				System.out.println("test 3:" + e1.getMessage());
			}

		}
	}

	public void sentToClient(DataType type, String str) throws IOException {
		JsonObject json = new JsonObject();
		json.addProperty("type", type.ordinal());
		json.addProperty("data", str);
		output1.write(json.toString() + "\n");
		output2.write(json.toString() + "\n");
		output1.flush();
		output2.flush();
		// TODO Auto-generated catch block
	}

}

enum DataType {
	TANK, BULLET, WALL, STRING
}