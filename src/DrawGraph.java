import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DrawGraph extends JPanel {
	private class Node {
		public int id;
		public int clusterId;
		public Node(int id, int clusterId){
			this.id = id;
			this.clusterId = clusterId;
		}

		@Override
		public String toString(){
			return this.id + "@" + this.clusterId;
		}
	}

	private class Edge {
		public int originId;
		public int destinationId;
		public Edge(int originId, int destinationId){
			this.originId = originId;
			this.destinationId = destinationId;
		}
	}

	private List<Node> nodes = new ArrayList<>();
	private int nodeCount = 0;
	private int clusterCount = 0;
	private int clusterSize = 0;
	private double sizeDeltaPercentage = 0.0;
	private Component parent;

	public void setGraph(Component parent, File file){
		this.parent = parent;
		String fileExtension = Utils.getFileExtension(file);
		this.nodes.clear();

		switch(fileExtension){
			case "clusters":
				parseClustersFile(file);
				break;
			default:
				JOptionPane.showMessageDialog(parent, "Jeszcze nie zaimplementowano rysowania grafu z pliku " + fileExtension);
		}

		repaint();
	}

	private void parseClustersFile(File file){
		try(BufferedReader reader = new BufferedReader(new FileReader(file))){
			String header = reader.readLine();
			String[] tokens = header.trim().split(" ");
			this.nodeCount = Integer.parseInt(tokens[0].trim().split(":")[1]);
			this.clusterCount = Integer.parseInt(tokens[1].trim().split(":")[1]);
			this.clusterSize = Integer.parseInt(tokens[3].trim().split(":")[1]);
			this.sizeDeltaPercentage = Double.parseDouble(tokens[2].trim().split(":")[1]);

			String line;
			for(int i = 0; i < clusterCount && (line = reader.readLine()) != null; i++){
				String[] nodeTokens = line.trim().split(" ");
				for(String n : nodeTokens){
					int nodeId = Integer.parseInt(n);
					this.nodes.add(new Node(nodeId, i));
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void parseDotFile(File file){}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		int width = getWidth();
		int height = getHeight();

		Map<Integer, List<Node>> clusterMap = new HashMap<>();
    for (Node node : nodes) {
      clusterMap.computeIfAbsent(node.clusterId, k -> new ArrayList<>()).add(node);
    }

		if(clusterMap.size() < 1) return;

		int clusterCount = clusterMap.size();
    int regionWidth = width / clusterCount;
  	int radius = 30;
    int padding = 20;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color[] colors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};
		int i = 0;
    for (int clusterId : clusterMap.keySet()) {
			List<Node> clusterNodes = clusterMap.get(clusterId);
      Color color = colors[clusterId % colors.length];

			int cols = (int)Math.ceil(Math.sqrt(clusterNodes.size()));
			int spacing = radius * 2 + padding;

			for(int j = 0; j < clusterNodes.size(); j++){
				int row = j / cols;
        int col = j % cols;

        int x = i * regionWidth + col * spacing + padding;
        int y = row * spacing + padding;

     		drawNode(g2, clusterNodes.get(j), color, x, y, radius);
			}
			i++;
    }
	}

	private void drawNode(Graphics2D g, Node n, Color color, int x, int y, int radius){
		g.setColor(color);
		g.fillOval(x, y, radius, radius);

		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(n.id), x + radius / 3, y + radius / 2);
	}
}
