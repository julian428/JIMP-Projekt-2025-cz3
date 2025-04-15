import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class Utils {
	public static void openWebsite(Component parent, String url){
		try{
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
			desktop.browse(new java.net.URI(url));
		} catch (Exception e){
			JOptionPane.showMessageDialog(parent, "Nie udało się otworzyć przeglądarki.\nOtwórz: " + url);
		}
	}
}
