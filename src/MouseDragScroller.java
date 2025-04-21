import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class MouseDragScroller {
    private final Point startPoint = new Point();

    public MouseDragScroller(JPanel panel, JScrollPane scrollPane) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint.setLocation(e.getPoint());
                SwingUtilities.convertPointToScreen(startPoint, panel);
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point dragPoint = e.getPoint();
                SwingUtilities.convertPointToScreen(dragPoint, panel);

                int dx = startPoint.x - dragPoint.x;
                int dy = startPoint.y - dragPoint.y;

                JViewport viewport = scrollPane.getViewport();
                Point viewPosition = viewport.getViewPosition();
                viewPosition.translate(dx, dy);

                panel.scrollRectToVisible(new Rectangle(viewPosition, viewport.getSize()));

                startPoint.setLocation(dragPoint);
            }
        });
    }
}
