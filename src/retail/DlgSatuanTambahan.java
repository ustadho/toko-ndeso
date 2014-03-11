/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgSatuanTambahan.java
 *
 * Created on Mar 7, 2009, 3:39:36 PM
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import retail.main.GeneralFunction;

/**
 *
 * @author ustadho
 */
public class DlgSatuanTambahan extends javax.swing.JDialog {
    String sUnitKecil="", sUnit2="", sUnit3="";
    float fKonv2=0, fKonv3=0;
    private NumberFormat dFmt=NumberFormat.getInstance();
    private GeneralFunction fn=new GeneralFunction();

    private Connection conn;
    private FrmItemMaster fMaster;

    /** Creates new form DlgSatuanTambahan */
    public DlgSatuanTambahan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
    }

    void setConn(Connection con){
        this.conn=con;
    }

    void setFormItemMaster(FrmItemMaster aThis) {
        fMaster=aThis;
    }

    void setUnitKecil(String s){
        this.sUnitKecil=s;
        lblUnitKecil2.setText(s);
        lblUnitKecil3.setText(s);
    }

    void setUnit2(String s){
        this.sUnit2=s;
        txtUnit2.setText(sUnit2);
    }
    
    void setUnit3(String s){
        this.sUnit3=s;
        txtUnit3.setText(s);
    }

    void setKonv2(float k){
        this.fKonv2=k;
        txtKonv2.setText(k>0? String.valueOf(k): "");
    }

    void setKonv3(float k){
        this.fKonv3=k;
        txtKonv3.setText(k>0? String.valueOf(k): "");
    }

    private float udfGetFloat(String sNum){
        float hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=dFmt.parse(sNum).floatValue();
            } catch (ParseException ex) {
                hsl=0;
                //Logger.getLogger(FrmTrxPinjam.class.getName()).log(Level.SEVERE, null, ex);
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
          if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")) {

              char c = evt.getKeyChar();
              if (!((c >= '0' && c <= '9')) &&
                    (c != KeyEvent.VK_BACK_SPACE) &&
                    (c != KeyEvent.VK_DELETE) &&
                    (c != KeyEvent.VK_ENTER) &&
                    (c != '-') &&
                    (c != '.')) {
                    getToolkit().beep();
                    evt.consume();
                    return;
              }
           }

        }
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    Component c = findNextFocus();
                    if (c==null) return;
                    c.requestFocus();

                    break;
                }
                case KeyEvent.VK_DOWN: {
                    Component c = findNextFocus();
                    if (c==null) return;
                    c.requestFocus();
                    break;

                }

                case KeyEvent.VK_UP: {
                    if(!(evt.getSource() instanceof JTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    dispose();
                    break;
                }
                case KeyEvent.VK_DELETE:{

                    break;

                }
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

        public Component findNextFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component nextFocus = policy.getComponentAfter(root, c);
                if (nextFocus == null) {
                    nextFocus = policy.getDefaultComponent(root);
                }
                return nextFocus;
            }
            return null;
        }

        public Component findPrevFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component prevFocus = policy.getComponentBefore(root, c);
                if (prevFocus == null) {
                    prevFocus = policy.getDefaultComponent(root);
                }
                return prevFocus;
            }
            return null;
        }
    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField ||(((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor")))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

           }
        }


    } ;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblUnitKecil3 = new javax.swing.JLabel();
        lblUnitKecil2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        txtUnit2 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txtUnit3 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtKonv2 = new javax.swing.JTextField();
        txtKonv3 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        btnOK = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Unit tambahan");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblUnitKecil3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        getContentPane().add(lblUnitKecil3, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 50, 20));

        lblUnitKecil2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        getContentPane().add(lblUnitKecil2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 50, 20));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("=");
        jPanel1.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 20, 20));
        jPanel1.add(txtUnit2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 60, -1));

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Satuan 3 : ");
        jPanel1.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 70, 20));
        jPanel1.add(txtUnit3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 60, -1));

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Satuan 2 : ");
        jPanel1.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 70, 20));
        jPanel1.add(txtKonv2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 50, -1));
        jPanel1.add(txtKonv3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, 50, -1));

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("=");
        jPanel1.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 20, 20));

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        jPanel1.add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 80, -1));

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("X");
        jPanel1.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 20, 20));

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("X");
        jPanel1.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 20, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 290, 100));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-313)/2, (screenSize.height-155)/2, 313, 155);
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        udfOK();
    }//GEN-LAST:event_btnOKActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgSatuanTambahan dialog = new DlgSatuanTambahan(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOK;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblUnitKecil2;
    private javax.swing.JLabel lblUnitKecil3;
    private javax.swing.JTextField txtKonv2;
    private javax.swing.JTextField txtKonv3;
    private javax.swing.JTextField txtUnit2;
    private javax.swing.JTextField txtUnit3;
    // End of variables declaration//GEN-END:variables

    private void udfOK() {
        if(txtUnit2.getText().length()>0 && GeneralFunction.udfGetInt(txtKonv2.getText())<=1){
            JOptionPane.showMessageDialog(this, "Konversi 2 diisi dengan nilai diatas 1!");
            txtKonv2.requestFocus();
            return;
        }
        if(txtUnit3.getText().length()>0 && txtUnit2.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi unit 2 terlebih dulu!");
            txtUnit2.requestFocus();
            return;
        }
        if(txtUnit3.getText().length()>0 && GeneralFunction.udfGetInt(txtKonv3.getText())<=1){
            JOptionPane.showMessageDialog(this, "Konversi 3 diisi dengan nilai diatas 1!");
            txtKonv3.requestFocus();
            return;
        }

        sUnit2=txtUnit2.getText();
        sUnit3=txtUnit3.getText();
        fKonv2=udfGetFloat(txtKonv2.getText());
        fKonv3=udfGetFloat(txtKonv3.getText());

        fMaster.setUnitTambahan(sUnit2, fKonv2, sUnit3, fKonv3);
        this.dispose();
    }

}
