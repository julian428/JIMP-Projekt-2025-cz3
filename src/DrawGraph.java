import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DrawGraph extends JPanel {
	private class Edge {
		public int originId;
		public int destinationId;

		public Edge(int originId, int destinationId){
			this.originId = originId;
			this.destinationId = destinationId;
		}

		@Override
		public String toString() {
			return this.originId + " -> " + this.destinationId;
		}
	}

	private List<Node> nodes = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();

	private int nodeCount = 0;
	private int edgeCount = 0;
	private int clusterCount = 0;
	private int clusterSize = 0;
	private double sizeDeltaPercentage = 0.0;
	private Component parent;

	public DrawGraph() {
    setPreferredSize(new Dimension(20000, 20000));
  }

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
			this.edgeCount = Integer.parseInt(tokens[1].trim().split(":")[1]);
			this.clusterCount = Integer.parseInt(tokens[2].trim().split(":")[1]);
			this.clusterSize = Integer.parseInt(tokens[4].trim().split(":")[1]);
			this.sizeDeltaPercentage = Double.parseDouble(tokens[3].trim().split(":")[1]);

			String line;
			for(int i = 0; i < clusterCount && (line = reader.readLine()) != null; i++){
				String[] nodeTokens = line.trim().split(" ");
				for(String n : nodeTokens){
					String nodeInfo = n.trim().split("@")[0];
					String nodePosition = n.trim().split("@")[1];

					int nodeId = Integer.parseInt(nodeInfo.split(";")[0]);
					int nodeCluster = Integer.parseInt(nodeInfo.split(";")[1]);
					double nodeX = Math.round((Double.parseDouble(nodePosition.split(";")[0]) + 1) *10000);
					double nodeY = Math.round((Double.parseDouble(nodePosition.split(";")[1]) + 1) *10000);
					System.out.println(nodeId + "@" + nodeX + ";" + nodeY);
					this.nodes.add(new Node(nodeId, nodeCluster, (int)nodeX, (int)nodeY));
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

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

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int i = 0;
    for (int clusterId : clusterMap.keySet()) {
			List<Node> clusterNodes = clusterMap.get(clusterId);
			for(int j = 0; j < clusterNodes.size(); j++){
				clusterNodes.get(j).drawNode(g2);
			}
			i++;
    }
	}
}
