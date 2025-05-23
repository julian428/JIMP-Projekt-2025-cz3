import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

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

	private Map<Integer, Node> nodes = new HashMap<>();
	private List<Edge> edges = new ArrayList<>();

	private int nodeCount = 0;
	private int edgeCount = 0;
	private int clusterCount = 0;
	private int clusterSize = 0;
	private int maxClusterSize = 0;
	private int clustersPerRow = 0;
	private double sizeDeltaPercentage = 0.0;

	private Component parent;

	private boolean showInsideConnections = true;
	private boolean showOutsideConnections = true;
	private boolean showInConnectionsCount = false;
	private boolean showOutConnectionsCount = false;
	private boolean showClusterBorders = false;
	private boolean showClusterBilans = false;
	private boolean showLostNodesCount = false;

	public DrawGraph(){
    setPreferredSize(new Dimension(1000, 1000));
	}

	public void changeInsideConnectionsVisibility(){
		this.showInsideConnections = !this.showInsideConnections;
		repaint();
	}
	
	public void changeOutsideConnectionsVisibility(){
		this.showOutsideConnections = !this.showOutsideConnections;
		repaint();
	}

	public void changeInConnectionsCount(){
		this.showInConnectionsCount = !this.showInConnectionsCount;
		repaint();
	}

	public void changeOutConnectionsCount(){
		this.showOutConnectionsCount = !this.showOutConnectionsCount;
		repaint();
	}

	public void changeBorderVisibility(){
		this.showClusterBorders = !this.showClusterBorders;
		repaint();
	}

	public void changeClusterBilansVisibility(){
		this.showClusterBilans = !this.showClusterBilans;
		repaint();
	}

	public void changeLostNodesCountVisibility(){
		this.showLostNodesCount = !this.showLostNodesCount;
		repaint();
	}

	public void setGraph(Component parent, File file){
		this.parent = parent;
		String fileExtension = Utils.getFileExtension(file);
		this.nodes.clear();
		this.edges.clear();

		switch(fileExtension){
			case "clusters":
				parseClustersFile(file);
				break;
			case "csrrg":
				generateGraph(file);
				break;
			default:
				JOptionPane.showMessageDialog(parent, "Jeszcze nie zaimplementowano rysowania grafu z pliku " + fileExtension);
				return;
		}

		maxClusterSize = (int)Math.ceil(this.clusterSize * (1 + this.sizeDeltaPercentage / 100));
		clustersPerRow = (int)Math.ceil(Math.sqrt(this.clusterCount));
		int sideLength = (int)(50 * maxClusterSize * clustersPerRow) + clustersPerRow * 50;
    setPreferredSize(new Dimension(sideLength, sideLength));

		repaint();
	}

	private void generateGraph(File file){
		JTextField clusterCountField = new JTextField();
    JTextField marginField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Ilość klastrów:"));
    panel.add(clusterCountField);
    panel.add(new JLabel("Margines róznicy rozmiaru klastrów (%):"));
    panel.add(marginField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Ustaw parametry podziału",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        try {
            int clusterCount = Integer.parseInt(clusterCountField.getText().trim());
            double margin = Double.parseDouble(marginField.getText().trim());

            runGraphGenerator(file, clusterCount, margin);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Podane liczby nie są poprawne.");
						generateGraph(file);
        }
    }
	}
	
	private void runGraphGenerator(File file, int clusterCount, double margin) {
    String path = file.getAbsolutePath();

    JDialog loadingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Generating Graph", true);
    loadingDialog.setLayout(new BorderLayout());
    loadingDialog.setSize(300, 150);
    loadingDialog.setLocationRelativeTo(null);

    JLabel messageLabel = new JLabel("Generowanie grafu...", SwingConstants.CENTER);
    JLabel timerLabel = new JLabel("Czas generacji: 0s", SwingConstants.CENTER);
    JButton cancelButton = new JButton("Cancel");

    loadingDialog.add(messageLabel, BorderLayout.NORTH);
    loadingDialog.add(timerLabel, BorderLayout.CENTER);
    loadingDialog.add(cancelButton, BorderLayout.SOUTH);

    final boolean[] cancelled = {false};

    final int[] elapsedSeconds = {0};
    Timer timer = new Timer(1000, e -> {
        elapsedSeconds[0]++;
        timerLabel.setText("Czas generacji: " + elapsedSeconds[0] + "s");
    });

    timer.start();

    cancelButton.addActionListener(e -> {
        cancelled[0] = true;
        loadingDialog.dispose();
    });

    new Thread(() -> {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "./JIMP-Projekt-2025-cz2/bin/divide_graph",
                "-i", path,
                "-c", String.valueOf(clusterCount),
                "-p", String.valueOf(margin)
            );
            Process process = pb.start();

            while (true) {
                try {
                    if (process.waitFor(1, java.util.concurrent.TimeUnit.SECONDS)) {
                        break;
                    }
                    if (cancelled[0]) {
                        process.destroy();
                        return;
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            int exitCode = process.exitValue();
            if (exitCode == 0) {
                File output = new File("clusters.clusters");
                parseClustersFile(output);
            } else {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(null, "Nie udało się wygenerować grafu.")
                );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, "Błąd przy uruchamianiu programu.")
            );
        } finally {
            SwingUtilities.invokeLater(() -> {
                timer.stop();
                loadingDialog.dispose();
            });
        }
    }).start();

    loadingDialog.setVisible(true);
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
					double nodeX = (Double.parseDouble(nodePosition.split(";")[0]) + 1)/2;
					double nodeY = (Double.parseDouble(nodePosition.split(";")[1]) + 1)/2;
					this.nodes.put(nodeId, new Node(nodeId, nodeCluster, nodeX, nodeY));
				}
			}

			for(int i = 0; i < edgeCount && (line = reader.readLine()) != null; i++){
				String[] connection = line.trim().split(" -> ");
				int from = Integer.parseInt(connection[0]);
				int to = Integer.parseInt(connection[1]);
				edges.add(new Edge(from, to));
			}
		}catch(IOException e){
			e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Błąd przy czytaniu pliku.");
		}catch(Exception e){
			e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Niepoprawny format pliku.");
		}
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		int clusterWidth = maxClusterSize * 50;

		if(this.nodes.size() < 1) return;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//normalizuje klastry
		double[] minX = new double[this.clusterCount];
		Arrays.fill(minX, 1);
		double[] maxX = new double[this.clusterCount];
		double[] minY = new double[this.clusterCount];
		Arrays.fill(minY, 1);
		double[] maxY = new double[this.clusterCount];
		for(Node node : this.nodes.values()){
			int c = node.clusterId;
			if (node.x < minX[c]) minX[c] = node.x;
    	if (node.x > maxX[c]) maxX[c] = node.x;
    	if (node.y < minY[c]) minY[c] = node.y;
    	if (node.y > maxY[c]) maxY[c] = node.y;
		}

		// rysowanie wierzchołków
    for(Node node : this.nodes.values()) {
			node.clearConnectionsCount();
			int c = node.clusterId;
			if(clustersPerRow < 1){
      	JOptionPane.showMessageDialog(null, "Błąd podczas rysowania grafu.");
				return;
			}
			int row = c % clustersPerRow;
			int col = c / clustersPerRow;

			int originX = row * clusterWidth + row * 50;
			int originY = col * clusterWidth + col * 50;
			
			double normX = (node.x - minX[c]) / (maxX[c] - minX[c]);
    	double normY = (node.y - minY[c]) / (maxY[c] - minY[c]);

			int localX = (int)Math.round(clusterWidth * normX);
			int localY = (int)Math.round(clusterWidth * normY);

			node.absoluteX = originX + localX;
			node.absoluteY = originY + localY;
    }

		int[] clusterOutConnections = new int[this.clusterCount];
		int[] clusterInConnections = new int[this.clusterCount];
		for(Edge edge : edges){
			Node from = this.nodes.get(edge.originId);
			Node to = this.nodes.get(edge.destinationId);
			boolean fromSameCluster = from.clusterId == to.clusterId;
			
			// update ilosć połączeń
			if(fromSameCluster){
				from.inConnections++;
				to.inConnections++;
				clusterInConnections[from.clusterId] += 2;
			}else{
				from.outConnections++;
				to.outConnections++;
				clusterOutConnections[from.clusterId]++;
				clusterOutConnections[to.clusterId]++;
			}

			if(fromSameCluster && this.showInsideConnections){
				g2.setColor(Color.GRAY);
				g2.drawLine(from.absoluteX + 15, from.absoluteY + 15, to.absoluteX + 15, to.absoluteY + 15);
			}
			else if(!fromSameCluster && this.showOutsideConnections){
				g2.setColor(Color.RED);
				g2.drawLine(from.absoluteX + 15, from.absoluteY + 15, to.absoluteX + 15, to.absoluteY + 15);
			}
		}

		

		// rysowanie wierzchołków
		int[] lostNodesCount = new int[this.clusterCount]; // ilość wierzchołków które mają więcej połączeń poza swój własny klaster.
		for(Node node : this.nodes.values()){
			node.drawNode(g2);
			if(node.inConnections == 0) lostNodesCount[node.clusterId]++;
			if(this.showInConnectionsCount) node.drawInConnectionsCount(g2);
			if(this.showOutConnectionsCount) node.drawOutConnectionsCount(g2);
		}

		// rysowanie granic klastrów is bilansu połączeń
		for(int i = 0; i < this.clusterCount; i++){
			int row = i % clustersPerRow;
			int col = i / clustersPerRow;
				
			int startX = row * clusterWidth + row * 50;
			int startY = col * clusterWidth + col * 50;

			int endX = startX + clusterWidth + 50;
			int endY = startY + clusterWidth + 50;

			g2.setColor(Color.BLACK);
			if(this.showClusterBorders){
				g2.drawLine(startX, startY, endX, startY);
				g2.drawLine(startX, startY, startX, endY);
				g2.drawLine(endX, startY, endX, endY);
				g2.drawLine(startX, endY, endX, endY);
			}
			if(this.showClusterBilans){
				g2.drawString("wewnętrzne połączenia: " + clusterInConnections[i], startX + (endX - startX) / 2 - 100, startY + 15);
				g2.setColor(Color.RED);
				g2.drawString("zewnętrzne połączenia: " + clusterOutConnections[i], startX + (endX - startX) / 2 - 100, startY + 30);
			}
			if(this.showLostNodesCount){
				g2.drawString("błędne wierzchołki: " + lostNodesCount[i], startX + (endX - startX) / 2 - 100, startY + 45);
			}
		}
	}
}
