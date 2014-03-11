/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgTermin.java
 *
 * Created on 20 Des 10, 22:24:06
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
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class DlgTermin extends javax.swing.JDialog {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    private String sOldKode="";

    /** Creates new form DlgTermin */
    public DlgTermin(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        MyKeyListener kListener=new MyKeyListener();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);

    }

    public void setConn(Connection con){
        this.conn=con;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtJtTempo = new javax.swing.JTextField();
        txtHariDisc = new javax.swing.JTextField();
        txtDiscPersen = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextPane();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        chkCOD = new javax.swing.JCheckBox();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Termin Pembayaran");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Jatuh Tempo");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 120, 20));

        jLabel2.setText("Akan mendapat diskon :");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 55, 140, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Informasi Diskon");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, 20));

        jLabel4.setText("%");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 55, 60, 20));

        txtJtTempo.setName("txtJtTempo"); // NOI18N
        txtJtTempo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtJtTempoKeyTyped(evt);
            }
        });
        jPanel1.add(txtJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 110, -1));

        txtHariDisc.setName("txtHariDisc"); // NOI18N
        txtHariDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHariDiscKeyTyped(evt);
            }
        });
        jPanel1.add(txtHariDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 110, -1));

        txtDiscPersen.setName("txtDiscPersen"); // NOI18N
        txtDiscPersen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscPersenKeyTyped(evt);
            }
        });
        jPanel1.add(txtDiscPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 55, 110, -1));

        jLabel5.setText("Keterangan :");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 115, 140, 20));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jScrollPane1.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, 170, 50));

        jLabel6.setText("Jika membayar antara :");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 140, 20));

        jLabel7.setText("hari");
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, 60, 20));

        jLabel8.setText("hari");
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, 60, 20));

        chkCOD.setText("Tunai Saat Pengantaran (C.O.D)");
        chkCOD.setName("chkCOD"); // NOI18N
        chkCOD.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkCODItemStateChanged(evt);
            }
        });
        jPanel1.add(chkCOD, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 210, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 350, 200));

        btnOK.setText("OK");
        btnOK.setName("btnOK"); // NOI18N
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        getContentPane().add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 80, -1));

        btnCancel.setText("Cancel");
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 220, 70, -1));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-385)/2, (screenSize.height-296)/2, 385, 296);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtHariDiscKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHariDiscKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHariDiscKeyTyped

    private void txtDiscPersenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscPersenKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtDiscPersenKeyTyped

    private void txtJtTempoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJtTempoKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtJtTempoKeyTyped

    private void chkCODItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkCODItemStateChanged
        txtDiscPersen.setText("0"); txtDiscPersen.setEditable(!chkCOD.isSelected());
        txtHariDisc.setText("0");   txtHariDisc.setEditable(!chkCOD.isSelected());
        txtJtTempo.setText("0");    txtJtTempo.setEditable(!chkCOD.isSelected());

    }//GEN-LAST:event_chkCODItemStateChanged

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        udfSave();
    }//GEN-LAST:event_btnOKActionPerformed

    private boolean udfCekBeforeSave(String sKode){
        boolean b=true;
        try{
            ResultSet rs=conn.createStatement().executeQuery("select * from m_termin where kode='"+sKode+"'");
            b=!rs.next();
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return b;
    }
    private void udfSave(){
        String sKode=txtDiscPersen.getText()+"/"+ txtHariDisc.getText()+" n/"+txtJtTempo.getText();
        sKode=chkCOD.isSelected()? "C.O.D": sKode;
        if(!udfCekBeforeSave(sKode)) return;

        boolean isNew=false;
        try{
            ResultSet rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("select * from m_termin where kode='"+sOldKode+"'");
            if(!rs.next()){
                isNew=true;
                rs.moveToInsertRow();
            }
            rs.updateString("kode", sKode);
            rs.updateInt("hari_diskon", fn.udfGetInt(txtHariDisc.getText()));
            rs.updateDouble("diskon", fn.udfGetDouble(txtDiscPersen.getText()));
            rs.updateInt("jatuh_tempo", fn.udfGetInt(txtJtTempo.getText()));
            rs.updateString("keterangan", txtKeterangan.getText());
            if(isNew){
                rs.updateString("user_ins", MainForm.sUserName);
                rs.insertRow();
            }
            else{
                rs.updateString("user_upd", MainForm.sUserName);
                rs.updateRow();
            }
            JOptionPane.showMessageDialog(this, "Simpan termin pembayaran sukses!");
            this.dispose();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgTermin dialog = new DlgTermin(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JCheckBox chkCOD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtDiscPersen;
    private javax.swing.JTextField txtHariDisc;
    private javax.swing.JTextField txtJtTempo;
    private javax.swing.JTextPane txtKeterangan;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable ||ct instanceof JXTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
                        {
                            if (!fn.isListVisible()){
                                Component c = findNextFocus();
                                if (c==null) return;
                                c.requestFocus();
                            }else{
                                fn.lstRequestFocus();
                            }
                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_INSERT:{


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
//                if(e.getSource().equals(txtTOP)||e.getSource().equals(txtKurs)||e.getSource().equals(txtDiscPersen)||e.getSource().equals(txtDiscRp)||
//                        (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                //}
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

//                if(!e.isTemporary() && e.getSource().equals(txtSupplier) && !isKoreksi && !fn.isListVisible() && suppLevel<=1)
//                    udfLoadItemFromPR();
//

           }
        }
    } ;

}
