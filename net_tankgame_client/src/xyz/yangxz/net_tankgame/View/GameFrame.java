package xyz.yangxz.net_tankgame.View;

import java.awt.*;
import java.text.AttributedCharacterIterator;

import javax.swing.*;

import xyz.yangxz.net_tankgame.Model.*;


public class GameFrame extends JFrame {

	WarData wardata;
	/**
	 * Create the frame.
	 */
	public GameFrame(WarData wardata) {
		this.wardata = wardata;
		init();
	}

	public void init() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		setLocationRelativeTo(null);
		setTitle("坦克大战低配版");
		JPanel contentPane = new JPanel() {
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				try {
					for (Tank tank : wardata.getTanks()) {
						tank.draw(g2);
						//System.out.println(tank.team+","+tank.x+","+tank.y+","+tank.moving);
					}
						
					for (Bullet bullet : wardata.getBullets())
						bullet.draw(g2);
					for (Wall wall : wardata.getWalls()) {
						wall.draw(g2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		contentPane.setBackground(new Color(0.1f, 0.1f, 0.1f));
		setContentPane(contentPane);
		setVisible(true);
	}
}
