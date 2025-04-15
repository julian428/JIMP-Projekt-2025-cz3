import javax.swing.*;

public class MainFrame extends JFrame {
	public MainFrame(){
		setTitle("Wizualizacja grafu");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JLabel label = new JLabel("Hello, world!");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label);

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

		loadGraph.addActionListener(e -> JOptionPane.showMessageDialog(this, "Wybór pliku .txt lub .dot"));
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
