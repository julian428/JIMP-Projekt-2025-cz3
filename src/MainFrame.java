import javax.swing.*;
import java.io.File;

public class MainFrame extends JFrame {
	private DrawGraph graph;
	public MainFrame(){
		setTitle("Wizualizacja grafu");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		this.graph = new DrawGraph();
		JScrollPane scrollPane = new JScrollPane(this.graph);
		add(scrollPane, SwingConstants.CENTER);

		createMenuBar();
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("Plik");
		JMenuItem loadGraph = new JMenuItem("otwórz");
		JMenuItem saveGraph = new JMenuItem("zapisz");

		fileMenu.add(loadGraph);
		fileMenu.add(saveGraph);

		JMenu optionsMenu = new JMenu("Opcje");
		JCheckBoxMenuItem insideConnections = new JCheckBoxMenuItem("połączenia w klastrze");
		JCheckBoxMenuItem outsideConnections = new JCheckBoxMenuItem("połączenia między klastrami");
		JCheckBoxMenuItem clusterBorders = new JCheckBoxMenuItem("granice klastrów");
		JCheckBoxMenuItem insideConnectionsCount = new JCheckBoxMenuItem("ilość wewnętrznych połączeń");
		JCheckBoxMenuItem outsideConnectionsCount = new JCheckBoxMenuItem("ilość wychodzących połączeń");

		insideConnections.setSelected(true);
		outsideConnections.setSelected(true);

		optionsMenu.add(insideConnections);
		optionsMenu.add(outsideConnections);
		optionsMenu.add(insideConnectionsCount);
		optionsMenu.add(outsideConnectionsCount);
		optionsMenu.add(clusterBorders);

		JMenuItem help = new JMenuItem("Pomoc?");

		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
		menuBar.add(help);
		setJMenuBar(menuBar);

		loadGraph.addActionListener(e -> {
			File selectedFile = Utils.fileDialog(this, "csrrg", "clusters");
			if(selectedFile == null) return;
			graph.setGraph(this, selectedFile);
		});
		saveGraph.addActionListener(e -> JOptionPane.showMessageDialog(this, "Wybór miejsca zapisu i nazwy zdjęcia grafu."));

		insideConnections.addActionListener(e -> graph.changeInsideConnectionsVisibility());
		outsideConnections.addActionListener(e -> graph.changeOutsideConnectionsVisibility());
		insideConnectionsCount.addActionListener(e -> graph.changeInConnectionsCount());
		outsideConnectionsCount.addActionListener(e -> graph.changeOutConnectionsCount());

		help.addActionListener(e -> Utils.openWebsite(this,"https://github.com/julian428/JIMP-Projekt-2025-cz3"));
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
		});
	}
}
