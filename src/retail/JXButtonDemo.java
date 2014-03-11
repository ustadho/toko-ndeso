/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retail;

/**
 *
 * @author ustadho
 */
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.image.StackBlurFilter;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImageOp;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
/** * Simple demo * */
public class JXButtonDemo extends JPanel {
    public JXButtonDemo() {
        //simple demo that blurs the button's text
        final JXButton b = new JXButton("Execute");
        final AbstractPainter fgPainter = (AbstractPainter)b.getForegroundPainter();
        final StackBlurFilter filter = new StackBlurFilter();
        fgPainter.setFilters(filter);
        b.addMouseListener(new MouseAdapter() {
            boolean entered = false;
            public void mouseEntered(MouseEvent mouseEvent) {
                if (!entered) {
                    fgPainter.setFilters(new BufferedImageOp[0]);
                    b.repaint();
                    entered = true;
                }
            }
            public void mouseExited(MouseEvent mouseEvent) {
                if (entered) {
                    fgPainter.setFilters(filter);
                    b.repaint();
                    entered = false;
                }
            }
        });
        add(b);
    }
    public static void main(String[] args) {
        JXFrame f = new JXFrame("JXButton Demo", true);
        f.add(new JXButtonDemo());
        f.setSize(400, 300);
        f.setStartPosition(JXFrame.StartPosition.CenterInScreen);
        f.setVisible(true);
    }
}


