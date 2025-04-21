import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Node {
	public int id;
	public int clusterId;
	public double x = 0;
	public double y = 0;
	public int absoluteX = 0;
	public int absoluteY = 0;

	public int outConnections = 0;
	public int inConnections = 0;

	private int radius = 30;
	private Color color = Color.BLACK;

	public Node(int id, int clusterId, double x, double y){
		this.id = id;
		this.clusterId = clusterId;
		this.x = x;
		this.y = y;
		Color[] colors = {Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA};
		this.color = colors[clusterId % colors.length];
	}

	@Override
	public String toString(){
		return this.id + "@" + this.clusterId;
	}

	public void drawNode(Graphics2D g){
		g.setColor(color);
		g.fillOval(this.absoluteX, this.absoluteY, radius, radius);

		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(this.id), this.absoluteX + radius / 3, this.absoluteY + radius / 2);
	}

	public void drawOutConnectionsCount(Graphics2D g){
		g.setColor(Color.RED);
		g.drawString(String.valueOf(this.outConnections), this.absoluteX + this.radius, this.absoluteY + this.radius + 10);
	}

	public void drawInConnectionsCount(Graphics2D g){
		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(this.inConnections), this.absoluteX, this.absoluteY + this.radius + 10);
	}

	public void clearConnectionsCount(){
		this.inConnections = 0;
		this.outConnections = 0;
	}
}


