/*
 * KasirCon.java
 *
 * Created on July 6, 2005, 7:42 PM
 */

/**
 *
 * @author  Administrator
 */
package retail.main;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
public class KasirCon{    
    /** Creates a new instance of KasirCon */

    private Connection con;
    boolean bError;
    public KasirCon(String url,String user,String pass, JFrame fThis) {
       try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url,user,pass);
            bError=true;
        } catch(java.sql.SQLException se) {
            JOptionPane jfo = new JOptionPane(se.getMessage(),JOptionPane.INFORMATION_MESSAGE);
	    JDialog dialog = jfo.createDialog(fThis, "Message");
	    dialog.setModal(true);
	    dialog.setVisible(true);
            
            
            
            bError=false;
            //System.exit(1);
        }  catch(ClassNotFoundException ce) {
            JOptionPane jfo = new JOptionPane(ce.getMessage(),JOptionPane.INFORMATION_MESSAGE);
	    JDialog dialog = jfo.createDialog(fThis, "Message");
	    dialog.setModal(true);
	    dialog.setVisible(true);
            
                        
            bError=false;
            //System.exit(1);
        } 
   
    }
    
    public Connection getCon(){
        return con;
    }      
     
     public boolean gettErrLog(){
        return bError;
    }
}
