/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgBayarPenjualan.java
 *
 * Created on Mar 12, 2009, 7:42:24 AM
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
import retail.main.GeneralFunction;

/**
 *
 * @author ustadho
 */
public class DlgBayarPenjualan extends javax.swing.JDialog {
    private double dTotalTrx=0;
    private NumberFormat numFormat=NumberFormat.getInstance();
    private Connection conn;
    private MyKeyListener kListener =new MyKeyListener();
    private boolean stBayar=false;
    private double dTunaiBayar=0;
    private double dTunaiKembali=0;
    private boolean bKredit=false;
    private int iJmlCicilan=0;
    private double dAngsuran=0;
    private String dateJtTempo;

    /** Creates new form DlgBayarPenjualan */
    public DlgBayarPenjualan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        jButton1.addKeyListener(kListener);
        jButton2.addKeyListener(kListener);
    }

    void setConn(Connection conn) {
        this.conn=conn;
    }

    private void keyNumeric(KeyEvent evt){
        char c = evt.getKeyChar();
        if (!((c >= '0') && (c <= '9') ||
            (c == KeyEvent.VK_BACK_SPACE) ||
            (c == KeyEvent.VK_ENTER) ||
            (c == KeyEvent.VK_DELETE))) {
            getToolkit().beep();
            evt.consume();
        }
    }

    void udfSetTotalTrx(double  d){
        dTotalTrx=d;
    }

    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);
           //c.setSelectionStart(0);
           //c.setSelectionEnd(c.getText().length());

           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            //c.setForeground(fHitam);
        }
   };

    private void setBKredit(boolean b) {
        bKredit=b;
    }

    private void setDAngsuran(double d) {
        dAngsuran=d;
    }

    private void setDTunaiBayar(double d) {
        dTunaiBayar=d;
    }

    private void setDTunaiKembali(double d) {
        dTunaiKembali=d;
    }

    private void setDateJtTempo(String text) {
        dateJtTempo=text;
    }

    private void setIJmlCicilan(int i) {
        iJmlCicilan=i;
    }

    private void setStBayar(boolean b) {
        stBayar=b;
    }

    public boolean isBayar(){
        return stBayar;
    }

    public double getTunaiBayar(){
        return dTunaiBayar;
    }

    public double getTunaiKembali(){
        return dTunaiKembali;
    }

    public boolean isKredit(){
        return bKredit;
    }

    public int getJmlCicilan(){
        return iJmlCicilan;
    }

    public double getAngsuran(){
        return dAngsuran;
    }

    public String getTglJtTempo(){
        return dateJtTempo;
    }

    public int getPeriodeAngsuran(){
        return cmbPeriode.getSelectedIndex();
    }

    private void udfBayarDech() {
        setStBayar(true);
        setDTunaiBayar(GeneralFunction.udfGetDouble(txtTunai.getText()));
        setDTunaiKembali(GeneralFunction.udfGetDouble(txtKembali.getText()));
        setBKredit(jPanel2.isVisible());
        setIJmlCicilan(GeneralFunction.udfGetInt(txtJmlCicilan.getText()));
        setDAngsuran(GeneralFunction.udfGetDouble(txtAngsuran.getText()));
        setDateJtTempo(txtJtTempo.getText());

        this.dispose();
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
        txtTunai = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtKembali = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtJmlCicilan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAngsuran = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbPeriode = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        txtJtTempo = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cash"));

        jLabel1.setText("TUNAI");

        txtTunai.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtTunai.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTunai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTunaiKeyReleased(evt);
            }
        });

        jLabel2.setText("KEMBALI");

        txtKembali.setEditable(false);
        txtKembali.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtKembali.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTunai, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(113, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTunai, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Kredit"));

        jLabel3.setText("Jml. Cicilan");

        txtJmlCicilan.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtJmlCicilan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("Angsuran");

        txtAngsuran.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtAngsuran.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("X");

        cmbPeriode.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cmbPeriode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Per Bulan", "Per Minggu", "Per Hari" }));

        jLabel6.setText("Jatuh Tempo");

        txtJtTempo.setEditable(false);
        txtJtTempo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtJtTempo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJmlCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJtTempo, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJmlCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJtTempo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-408)/2, (screenSize.height-296)/2, 408, 296);
    }// </editor-fold>//GEN-END:initComponents

    private void txtTunaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTunaiKeyReleased
//        txtKembali.setText(numFormat.format(dTotalTrx-GeneralFunction.udfGetDouble(txtTunai.getText())));
//        jPanel2.setVisible(!(dTotalTrx<=GeneralFunction.udfGetDouble(txtTunai.getText())));

    }//GEN-LAST:event_txtTunaiKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        udfBayarDech();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgBayarPenjualan dialog = new DlgBayarPenjualan(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox cmbPeriode;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtAngsuran;
    private javax.swing.JTextField txtJmlCicilan;
    private javax.swing.JTextField txtJtTempo;
    private javax.swing.JTextField txtKembali;
    private javax.swing.JTextField txtTunai;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
          //if(e.getSource().equals(txtAngsuran))
            keyNumeric(e);
            txtKembali.setText(numFormat.format(dTotalTrx-GeneralFunction.udfGetDouble(txtTunai.getText())));
            jPanel2.setVisible(!(dTotalTrx<=GeneralFunction.udfGetDouble(txtTunai.getText())));

        }
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_ENTER : {
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                    break;
                }
                case KeyEvent.VK_DOWN : {
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                    break;
                }
                case KeyEvent.VK_UP : {
                    Component c = findPrevFocus();
                    if (c!=null) c.requestFocus();
                    break;
                }
                case KeyEvent.VK_F5 : {
                    udfBayarDech();
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    dispose();
                    break;
                }
                default :{
                    //private void keyTyped(KeyEvent e) {
                      //keyNumeric(evt);
                    //}
                    break;
                }
          }
    }
    
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

    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);


}
