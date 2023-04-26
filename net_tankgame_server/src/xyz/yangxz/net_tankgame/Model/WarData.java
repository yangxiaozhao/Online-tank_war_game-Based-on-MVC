package xyz.yangxz.net_tankgame.Model;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.Gson;

import xyz.yangxz.net_tankgame.Control.GameControl;

// 服务器端:游戏数据区
public class WarData {

	public static ServerSocket serverSocket=null;
	
	// 游戏中的常量
	public static double player_hp = 300;
	public static double enemy_hp = 100;
	public static int player_speed = 5;
	public static int enemy_speed = 2;
	public static int enemyNum_Max = 5;
	public static int enemyNum = 3;
	public static volatile int dead_enemy = 0;

	// 游戏元素列表
	public static CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();
	public static ExecutorService es = Executors.newFixedThreadPool(enemyNum);
	public static CopyOnWriteArrayList<Tank> tanks = new CopyOnWriteArrayList();
	public static CopyOnWriteArrayList<Wall> walls = new CopyOnWriteArrayList<>();
	public static List<Tank> tempanklist = new ArrayList<>();

	// 玩家1、2
	public static Tank player1=new Tank();
	public static Tank player2=new Tank();

	// 初始化敌方坦克与地图
	static{
		addEnemy(enemyNum_Max);
		addWall();
		tanks.add(player1);
		tanks.add(player1);
	}

	// 更新游戏数据，返回true表示游戏继续，返回false表示游戏结束
	public static boolean update() {
		// 判断玩家是否死亡
		if (player1.destroyed && player2.destroyed)
			return false;
		// TODO Auto-generated method stub
		for (Tank tank : tanks) {
			// 玩家坦克
			if (tank.team == Team.PLAYER1 || tank.team == Team.PLAYER2 ) {
				/***
				 * 玩家与敌方子弹的碰撞检测
				 ***/
				for (Bullet bullet : bullets) {
					if (tank.getRect().intersects(bullet.getRect()) && (bullet.team!= Team.PLAYER1 && bullet.team != Team.PLAYER2)) {
						tank.damage(bullet.damage);
						bullets.remove(bullet);
					}
				}
				tank.update(player_speed);
				/***
				 * 坦克与墙的碰撞检测
				 ***/
				for (Wall wall : walls) {
					if (tank.getRect().intersects(wall.getRect()))
						tank.update(-player_speed);
				}
				/***
				 * 坦克与坦克的碰撞检测
				 ***/
				for (Tank t : tanks) {
					if (t != tank && tank.getRect().intersects(t.getRect()))
						tank.update(-player_speed);
				}
			}

			// 敌方坦克
			if (tank.team != Team.PLAYER1 && tank.team != Team.PLAYER2) {
				/***
				 * 敌方坦克与玩家子弹的碰撞检测
				 ***/
				for (Bullet bullet : bullets) {
					if (tank.getRect().intersects(bullet.getRect()) && (bullet.team == Team.PLAYER1 || bullet.team == Team.PLAYER2)) {
						tank.damage(bullet.damage);
						bullets.remove(bullet);
					}
				}
				// 防止敌方坦克跑出地图
				if (tank.x <= 0) {
					tank.direction = 90;
				}
				if (tank.y <= 0) {
					tank.direction = 180;
				}
				if (tank.x >= 900) {
					tank.direction = 270;
				}
				if (tank.y >= 700) {
					tank.direction = 0;
				}
				tank.move(tank.direction, tank.speed);
				// 运动几步随机开炮
				if (tank.steps > 100) {
					// 方向随机
					double random = (new Random().nextInt(4)) * 90;
					tank.direction = random;
					tank.moving = true;
					tank.steps = 0;
				}
				tank.update(enemy_speed);
				/***
				 * 坦克与墙的碰撞检测
				 ***/
				for (Wall wall : walls) {
					if (tank.getRect().intersects(wall.getRect()))
						tank.update(-player_speed);
				}
				/***
				 * 坦克与坦克的碰撞检测
				 ***/
				for (Tank t : tanks) {
					if (t != tank && tank.getRect().intersects(t.getRect()))
						tank.update(-player_speed);
				}
			}
		}

		// 更新子弹
		for (Bullet bullet : bullets) {
			if(bullet.destroyed)
				bullets.remove(bullet);
			else {
				bullet.update();
			}
		}

		// 更新墙体
		for (Wall wall : walls) {
			for (Bullet bullet : bullets) {
				if (bullet.getRect().intersects(wall.getRect())) {
					bullet.death();
					wall.damage(bullet.damage);
					bullets.remove(bullet);
					if (wall.style != WallStyle.HOME && wall.destroyed)
						walls.remove(wall);
					if (wall.style == WallStyle.HOME && wall.destroyed) {
						wall.update();
						return false;
					}
					break;
				}
			}

		}

		// 判断敌方坦克是否被全部击灭
		if (dead_enemy == enemyNum_Max) {
			return false;
		}

		return true;
	}

	// 添加敌方坦克
	public static void addEnemy(int n) {
		// 创建指定个敌方坦克
		for (int i = 0; i < n; i++) {
			Random rand = new Random();
			Tank enemy = new Tank(rand.nextInt(10) * 80 + 80.0, 30, rand.nextInt(2) == 0 ? Team.ENEMY1 : Team.ENEMY2,
					rand.nextInt(4) * 90, enemy_speed, enemy_hp);

			Iterator<Tank> it = tempanklist.iterator();
			while (it.hasNext()) {
				Tank tmp = it.next();
				// 避免敌方坦克位置重叠
				if (enemy.getRect().intersects(tmp.getRect())) {
					it.remove();
					i--;
				}
			}
			tempanklist.add(enemy);
		}
		// 利用线程池管理敌方坦克
		for(Tank tank:tempanklist) {
			TankThread thread=new TankThread(tank);
			es.submit(thread);
		}
	}

	// 添加子弹，该方法由坦克调用
	public static void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	// 添加墙体
	private static void addWall() {
		// TODO Auto-generated method stub
		walls.add(new Wall(442, 640, WallStyle.HOME)); // 基地

		walls.add(new Wall(394, 640, WallStyle.BRICK)); // 基地围墙
		walls.add(new Wall(394, 592, WallStyle.BRICK));
		walls.add(new Wall(442, 592, WallStyle.BRICK));
		walls.add(new Wall(490, 592, WallStyle.BRICK));
		walls.add(new Wall(490, 640, WallStyle.BRICK));

		walls.add(new Wall(346, 302, WallStyle.BRICK)); // H型墙
		walls.add(new Wall(346, 350, WallStyle.BRICK));
		walls.add(new Wall(346, 398, WallStyle.BRICK));
		walls.add(new Wall(346, 446, WallStyle.BRICK));
		walls.add(new Wall(394, 350, WallStyle.BRICK));
		walls.add(new Wall(442, 350, WallStyle.BRICK));
		walls.add(new Wall(490, 350, WallStyle.BRICK));
		walls.add(new Wall(538, 302, WallStyle.BRICK));
		walls.add(new Wall(538, 350, WallStyle.BRICK));
		walls.add(new Wall(538, 398, WallStyle.BRICK));
		walls.add(new Wall(538, 446, WallStyle.BRICK));

		walls.add(new Wall(188, 130, WallStyle.BRICK)); // 左上方砖
		walls.add(new Wall(236, 130, WallStyle.BRICK));
		walls.add(new Wall(188, 178, WallStyle.BRICK));
		walls.add(new Wall(236, 178, WallStyle.BRICK));

		walls.add(new Wall(418, 130, WallStyle.BRICK)); // 中间矩形砖
		walls.add(new Wall(466, 130, WallStyle.BRICK));

		walls.add(new Wall(648, 130, WallStyle.BRICK)); // 右上方砖
		walls.add(new Wall(696, 130, WallStyle.BRICK));
		walls.add(new Wall(648, 178, WallStyle.BRICK));
		walls.add(new Wall(696, 178, WallStyle.BRICK));

		walls.add(new Wall(24, 350, WallStyle.IRON)); // 铁墙
		walls.add(new Wall(72, 350, WallStyle.IRON));
		walls.add(new Wall(860, 350, WallStyle.IRON));
		walls.add(new Wall(812, 350, WallStyle.IRON));

	}

}
