package xyz.yangxz.net_tankgame.Control;

import xyz.yangxz.net_tankgame.Model.Bullet;
import xyz.yangxz.net_tankgame.Model.Team;
import xyz.yangxz.net_tankgame.Model.WarData;
import java.util.logging.Logger;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(FromClient.class.getName());

	public static void main(String[] args) {

		WarData wardata=new WarData();
		GameControl control=new GameControl();
		//new Thread(control).start();
	}
}

