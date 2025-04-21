import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.net.URI;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Utils {
	public static void openWebsite(Component parent, String url){
		try{
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new java.net.URI(url));
		} catch (Exception e){
			JOptionPane.showMessageDialog(parent, "Nie udało się otworzyć przeglądarki.\nOtwórz: " + url);
		}
	}

	public static File fileDialog(Component parent, String... extensions){
		JFileChooser fileChooser = new JFileChooser();
		String description = String.join(", ", extensions);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);

		fileChooser.setFileFilter(filter);
		int result = fileChooser.showOpenDialog(parent);

		if(result == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		else {
			return null;
		}
	}

	public static void saveJpanel(Component parent, JPanel panel){
		JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save As");
    fileChooser.setSelectedFile(new File("clusters.png"));

    int userSelection = fileChooser.showSaveDialog(parent);

  	if (userSelection == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
            
      if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
        selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
      }

			BufferedImage image = new BufferedImage(
        panel.getWidth(), panel.getHeight(),
        BufferedImage.TYPE_INT_ARGB
      );

      Graphics2D g2 = image.createGraphics();
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      panel.paint(g2);
      g2.dispose();

      try {
        ImageIO.write(image, "png", selectedFile);
        JOptionPane.showMessageDialog(parent, "Zapisano pomyślnie!");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(parent, "Błąd podczas zapisywanie pliku.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
	}

	public static String getFileExtension(File file){
		String name = file.getName();
		int dotIndex = name.lastIndexOf(".");
		return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
	}
}
