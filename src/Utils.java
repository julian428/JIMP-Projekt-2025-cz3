import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.net.URI;
import java.io.File;

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

	public static String getFileExtension(File file){
		String name = file.getName();
		int dotIndex = name.lastIndexOf(".");
		return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
	}
}
