package xyz.yangxz.net_tankgame.Model;

import java.util.ArrayList;
import java.util.List;

public class WarData {
	private List<Tank> tanks=new ArrayList<>();
	private List<Bullet> bullets=new ArrayList<>();
	private List<Wall> walls=new ArrayList<>();
	public List<Tank> getTanks() {
		return tanks;
	}
	public void setTanks(List<Tank> tanks) {
		this.tanks = tanks;
	}
	public List<Bullet> getBullets() {
		return bullets;
	}
	public void setBullets(List<Bullet> bullets) {
		this.bullets = bullets;
	}
	public List<Wall> getWalls() {
		return walls;
	}
	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}
}
