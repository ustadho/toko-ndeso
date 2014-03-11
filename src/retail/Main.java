/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retail;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import retail.main.LoginUser;

/**
 *
 * @author ustadho
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //new systemSkin("coronaHthemepack.zip");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         //       UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            BorderUIResource borderUIResource= new BorderUIResource(BorderFactory.createLineBorder(Color.yellow, 2));
            UIManager.put("Table.focusCellHighlightBorder", borderUIResource);
        } catch (Exception e) {
            System.out.println("error setting l &f " + e);
        }

        new LoginUser().setVisible(true);
    }

}
