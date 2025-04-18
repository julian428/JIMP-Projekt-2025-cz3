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
		JMenuItem loadGraph = new JMenuItem("otwórz graf");
		JMenuItem saveGraph = new JMenuItem("zapisz graf");

		JMenuItem help = new JMenuItem("Pomoc?");

		fileMenu.add(loadGraph);
		fileMenu.add(saveGraph);

		menuBar.add(fileMenu);
		menuBar.add(help);
		setJMenuBar(menuBar);

		loadGraph.addActionListener(e -> {
			File selectedFile = Utils.fileDialog(this, "txt", "dot", "csrrg", "clusters");
			if(selectedFile == null) return;
			graph.setGraph(this, selectedFile);
		});
		saveGraph.addActionListener(e -> JOptionPane.showMessageDialog(this, "Wybór miejsca zapisu i nazwy zdjęcia grafu."));
		help.addActionListener(e -> Utils.openWebsite(this,"https://github.com/julian428/JIMP-Projekt-2025-cz3"));
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
		});
	}
}
