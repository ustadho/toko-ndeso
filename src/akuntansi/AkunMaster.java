/*
 * AkunMaster.java
 *
 * Created on September 6, 2008, 9:46 PM
 */

package akuntansi;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import retail.main.GeneralFunction;
import retail.main.ListRsbm;

/**
 *
 * @author  oestadho
 */
public class AkunMaster extends javax.swing.JInternalFrame {
    private ArrayList lstTipeAkun=new ArrayList();
    private Connection conn;
    private String sAccNo="";
    private String sType="";
    private boolean sNew=false;
    private ListRsbm lst1; Integer rowPos=0;
    private DefaultTableModel srcTable;
    /** Creates new form AkunMaster */
    public AkunMaster() {
        initComponents();
        jTabbedPane1.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
    }

    public void  setAccNo(String s){
        sAccNo=s;
    }

    public void setConn(Connection con){
        conn=con;
    }
    
    public void setNew(boolean flg){
        sNew = flg;
    }
    
    public void setModelTable(DefaultTableModel flg, int row){
        srcTable = flg;
        rowPos= row;
    }

    private void udfInitForm(){
        cmbTipeAkun.removeAllItems();
        try {
            ResultSet rs = conn.createStatement().executeQuery("select * from acc_group");
            while(rs.next()){
                cmbTipeAkun.addItem(rs.getString("type_name"));
                lstTipeAkun.add(rs.getString("type_id"));
            }
            cmbTipeAkun.setSelectedIndex(-1);
            rs.close();

            if(sAccNo.length()>0){
                String s="select a.acc_no, coalesce(a.acc_name,'') as acc_name, coalesce(type_name,'') as type_name," +
                        "coalesce(a.active, true) as active, coalesce(a.curr_id,'') as curr_id, coalesce(curr_name,'') as curr_name, " +
                        "trim(coalesce(a.sub_acc_of,''))<>'' as is_sub,  " +
                        "coalesce(a.sub_acc_of,'') as sub_acc_of, coalesce(a2.acc_name,'') as sub_acc_of_name," +
                        "coalesce(a.saldo_awal,0) as saldo_awal, a.per_tgl, coalesce(a.catatan,'') as catatan," +
                        "coalesce(a.acc_group,'') as acc_group, coalesce(a3.acc_name,'') as acc_group_name " +
                        "from acc_coa a " +
                        "left join acc_group g on g.type_id=a.acc_type " +
                        "left join acc_currency curr on curr.curr_id=a.curr_id " +
                        "left join acc_coa a2 on a2.acc_no=a.sub_acc_of " +
                        "left join acc_coa a3 on a3.acc_no=a.acc_group " +
                        "where a.acc_no='"+sAccNo+"'";

                rs=conn.createStatement().executeQuery(s);
                if(rs.next()){
                    cmbTipeAkun.setSelectedItem(rs.getString("type_name"));
                    chkAktif.setSelected(!rs.getBoolean("active"));
                    txtNoAkun.setText(rs.getString("acc_no"));
                    txtNamaAkun.setText(rs.getString("acc_name"));
                    chkSubAkun.setSelected(rs.getBoolean("is_sub"));
                    txtSubAkun.setText(rs.getString("sub_acc_of"));
                    lblSubAkun.setText(rs.getString("sub_acc_of_name"));
                    txtSaldoAwal.setText(GeneralFunction.dFmt.format(rs.getDouble("saldo_awal")));
                    txtMataUang.setText(rs.getString("curr_id"));
                    lblMataUang.setText(rs.getString("curr_name"));
                    jDateTgl.setDate(null);
                    txtCatatan.setText(rs.getString("catatan"));
                    txtGroupAkun.setText(rs.getString("acc_group"));
                    lblGroupAkun.setText(rs.getString("acc_group_name"));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AkunMaster.class.getName()).log(Level.SEVERE, null, ex);
        }

        txtSubAkun.setVisible(chkSubAkun.isSelected());
        lblSubAkun.setVisible(chkSubAkun.isSelected());
        
    }

    private void udfKeyList(java.awt.event.KeyEvent evt, JTextField txt, JLabel lbl,
            String Qry, int x, int y, int n, int m){
        try {
        String sCari = txt.getText();
        switch (evt.getKeyCode()) {

            case java.awt.event.KeyEvent.VK_ENTER: {

                if (lst1.isVisible()) {
//                    System.out.println("Masuk ");
                    Object[] obj = lst1.getOResult();
                    if (obj.length > 0) {
                        txt.setText(obj[0].toString());
                        lbl.setText(obj[1].toString());
                        lst1.setVisible(false);
                    }
                }
                break;
            }
            case java.awt.event.KeyEvent.VK_DELETE: {
                lst1.setFocusable(true);
                lst1.requestFocus();

                break;
            }
            case java.awt.event.KeyEvent.VK_ESCAPE: {
                lst1.setVisible(false);
                txt.setText("");
                lbl.setText("");

                break;
            }
            case java.awt.event.KeyEvent.VK_DOWN: {
                if (lst1.isVisible()) {
//                    System.out.println("Down ");
                    lst1.setFocusableWindowState(true);
                    lst1.setVisible(true);
                    lst1.requestFocus();
                }
                break;
            }
            default: {
                if (!evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("Up") || !evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("F2")) {
//                    String sQry = "select acc_no, acc_name from acc_coa " +
//                            "where (acc_no||acc_name) " +
//                            "iLike '%" + txtSubAkun.getText() + "%' order by acc_name ";
                    String sQry = Qry;

//                    System.out.println(sQry);
                    lst1.setSQuery(sQry);

                    lst1.setBounds(x , y , n, m+200);


                    lst1.setFocusableWindowState(false);
                    lst1.setTxtCari(txt);
                    lst1.setCompDes(new javax.swing.JLabel[]{lbl});
                    lst1.setColWidth(0, txt.getWidth());
                    lst1.setColWidth(1, lbl.getWidth()-10);

                    if (lst1.getIRowCount() > 0) {
                        lst1.setVisible(true);
                        requestFocusInWindow();
                        txt.requestFocus();
                    } else {
                        lst1.setVisible(false);
                        txt.setText("");
                        lbl.setText("");
                        txt.requestFocus();
                    }
                }
                break;
            }
        }
    } catch (SQLException se) {
        System.out.println(se.getMessage());
    }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbTipeAkun = new javax.swing.JComboBox();
        txtNoAkun = new javax.swing.JTextField();
        chkAktif = new javax.swing.JCheckBox();
        txtNamaAkun = new javax.swing.JTextField();
        chkSubAkun = new javax.swing.JCheckBox();
        txtMataUang = new javax.swing.JTextField();
        txtSubAkun = new javax.swing.JTextField();
        lblMataUang = new javax.swing.JLabel();
        lblSubAkun = new javax.swing.JLabel();
        txtSaldoAwal = new javax.swing.JTextField();
        jDateTgl = new org.jdesktop.swingx.JXDatePicker();
        txtGroupAkun = new javax.swing.JTextField();
        lblGroupAkun = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtCatatan = new javax.swing.JTextPane();
        btnClear = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setName("Form"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(AkunMaster.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        cmbTipeAkun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTipeAkun.setName("cmbTipeAkun"); // NOI18N
        cmbTipeAkun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipeAkunActionPerformed(evt);
            }
        });

        txtNoAkun.setText(resourceMap.getString("txtNoAkun.text")); // NOI18N
        txtNoAkun.setName("txtNoAkun"); // NOI18N

        chkAktif.setText(resourceMap.getString("chkAktif.text")); // NOI18N
        chkAktif.setName("chkAktif"); // NOI18N

        txtNamaAkun.setText(resourceMap.getString("txtNamaAkun.text")); // NOI18N
        txtNamaAkun.setName("txtNamaAkun"); // NOI18N

        chkSubAkun.setText(resourceMap.getString("chkSubAkun.text")); // NOI18N
        chkSubAkun.setName("chkSubAkun"); // NOI18N
        chkSubAkun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSubAkunActionPerformed(evt);
            }
        });

        txtMataUang.setText(resourceMap.getString("txtMataUang.text")); // NOI18N
        txtMataUang.setName("txtMataUang"); // NOI18N
        txtMataUang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMataUangKeyReleased(evt);
            }
        });

        txtSubAkun.setName("txtSubAkun"); // NOI18N
        txtSubAkun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSubAkunKeyReleased(evt);
            }
        });

        lblMataUang.setText(resourceMap.getString("lblMataUang.text")); // NOI18N
        lblMataUang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblMataUang.setName("lblMataUang"); // NOI18N

        lblSubAkun.setForeground(resourceMap.getColor("lblSubAkun.foreground")); // NOI18N
        lblSubAkun.setText(resourceMap.getString("lblSubAkun.text")); // NOI18N
        lblSubAkun.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblSubAkun.setName("lblSubAkun"); // NOI18N

        txtSaldoAwal.setName("txtSaldoAwal"); // NOI18N

        jDateTgl.setName("jDateTgl"); // NOI18N

        txtGroupAkun.setName("txtGroupAkun"); // NOI18N
        txtGroupAkun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGroupAkunKeyReleased(evt);
            }
        });

        lblGroupAkun.setForeground(resourceMap.getColor("lblGroupAkun.foreground")); // NOI18N
        lblGroupAkun.setText(resourceMap.getString("lblGroupAkun.text")); // NOI18N
        lblGroupAkun.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblGroupAkun.setName("lblGroupAkun"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(chkSubAkun)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNoAkun, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(chkAktif)
                                .addGap(82, 82, 82))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbTipeAkun, 0, 249, Short.MAX_VALUE)
                                .addGap(64, 64, 64))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtSubAkun, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                                    .addComponent(txtMataUang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblMataUang, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                                    .addComponent(lblSubAkun, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSaldoAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateTgl, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtNamaAkun, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtGroupAkun, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblGroupAkun, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbTipeAkun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNoAkun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAktif))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNamaAkun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtMataUang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMataUang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSubAkun)
                    .addComponent(txtSubAkun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubAkun))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSaldoAwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jDateTgl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGroupAkun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGroupAkun, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblMataUang, txtMataUang});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblSubAkun, txtSubAkun});

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtCatatan.setName("txtCatatan"); // NOI18N
        jScrollPane1.setViewportView(txtCatatan);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        btnClear.setText(resourceMap.getString("btnClear.text")); // NOI18N
        btnClear.setName("btnClear"); // NOI18N

        btnSimpan.setText(resourceMap.getString("btnSimpan.text")); // NOI18N
        btnSimpan.setName("btnSimpan"); // NOI18N
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnBatal.setText(resourceMap.getString("btnBatal.text")); // NOI18N
        btnBatal.setName("btnBatal"); // NOI18N
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBatal)
                    .addComponent(btnSimpan)
                    .addComponent(btnClear))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
    lst1 = new ListRsbm();
    lst1.setVisible(false);
    lst1.setSize(500, 200);
    lst1.setConn(conn) ;
}//GEN-LAST:event_formInternalFrameOpened

private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
    this.dispose();
}//GEN-LAST:event_btnBatalActionPerformed

private void cmbTipeAkunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipeAkunActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_cmbTipeAkunActionPerformed

private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
    udfSave();
}//GEN-LAST:event_btnSimpanActionPerformed

private void txtSubAkunKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSubAkunKeyReleased
    String sQry = "select acc_no, acc_name from acc_coa " +
            "where (acc_no||acc_name) " +
            "iLike '%" + txtSubAkun.getText() + "%' and acc_type ='"+ lstTipeAkun.get(cmbTipeAkun.getSelectedIndex()).toString() +"' order by acc_name ";

         udfKeyList(evt, txtSubAkun, lblSubAkun, sQry, this.txtSubAkun.getLocationOnScreen().x,
                 this.txtSubAkun.getLocationOnScreen().y + txtSubAkun.getHeight() ,
                 txtSubAkun.getWidth() + lblSubAkun.getWidth()+20,
                 (lst1.getIRowCount()>10? 12*lst1.getRowHeight(): (lst1.getIRowCount()+3)*lst1.getRowHeight()));
}//GEN-LAST:event_txtSubAkunKeyReleased

private void chkSubAkunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSubAkunActionPerformed
    if (chkSubAkun.isSelected()){
        txtSubAkun.setVisible(true);
        lblSubAkun.setVisible(true);
    }else{
        txtSubAkun.setVisible(false);
        lblSubAkun.setVisible(false);
    }
}//GEN-LAST:event_chkSubAkunActionPerformed

private void txtMataUangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMataUangKeyReleased
    String sQry = "select curr_id, curr_name from acc_currency " +
            "where (curr_id||curr_name) " +
            "iLike '%" + txtMataUang.getText() + "%'  ";

         udfKeyList(evt, txtMataUang, lblMataUang, sQry, this.txtMataUang.getLocationOnScreen().x,
                 this.txtMataUang.getLocationOnScreen().y + txtMataUang.getHeight() ,
                 txtMataUang.getWidth() + lblMataUang.getWidth()+20,
                 (lst1.getIRowCount()>10? 12*lst1.getRowHeight(): (lst1.getIRowCount()+3)*lst1.getRowHeight()));
}//GEN-LAST:event_txtMataUangKeyReleased

private void txtGroupAkunKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGroupAkunKeyReleased
    String sQry = "select acc_no, acc_name from acc_coa " +
            "where (acc_no||acc_name) " +
            "iLike '%" + txtGroupAkun.getText() + "%' and acc_type ='"+ lstTipeAkun.get(cmbTipeAkun.getSelectedIndex()).toString() +"' order by acc_name ";

         udfKeyList(evt, txtGroupAkun, lblGroupAkun, sQry, this.txtGroupAkun.getLocationOnScreen().x,
                 this.txtGroupAkun.getLocationOnScreen().y + txtGroupAkun.getHeight() ,
                 txtGroupAkun.getWidth() + lblGroupAkun.getWidth()+20,
                 (lst1.getIRowCount()>10? 12*lst1.getRowHeight(): (lst1.getIRowCount()+3)*lst1.getRowHeight()));
}//GEN-LAST:event_txtGroupAkunKeyReleased

private void udfSave(){
//        if(udfCekbeforeSave()){
            try {
                conn.setAutoCommit(false);
                ResultSet rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from acc_coa where acc_no='"+sAccNo+"'");
                if(!rs.next()|| sNew){
                    rs.moveToInsertRow();
//                   txtKode.setText(getStockID());
                }
                // Set values for the new row.
                //sKode
                rs.updateString("acc_no", txtNoAkun.getText());
                rs.updateString("acc_name", txtNamaAkun.getText());
                rs.updateString("sub_acc_of", txtSubAkun.getText());
                rs.updateString("acc_type", lstTipeAkun.get(cmbTipeAkun.getSelectedIndex()).toString());
                rs.updateBoolean("active", chkAktif.isSelected());
                rs.updateString("catatan", txtCatatan.getText());
                rs.updateString("curr_id", txtMataUang.getText());
                rs.updateString("acc_group", txtGroupAkun.getText());
                // Insert the new row
                if(sNew)
                    rs.insertRow();
                else
                    rs.updateRow();
                    
//                if(txtNoAkun.getText().equals("") && txtNamaAkun==null){
                    if(sNew){
                        if(srcTable!=null)
                            srcTable.addRow(new Object[]{
                                txtNoAkun.getText(),
                                txtNamaAkun.getText(),
                                txtCatatan.getText(),
                                cmbTipeAkun.getSelectedItem().toString()
    //                            txtPenanggung.getText()
                            });
//                        srcTable.setRowSelectionInterval(srcModel.getRowCount()-1, srcModel.getRowCount()-1);

                    }else{
                        if(srcTable!=null){
                            srcTable.setValueAt(txtNoAkun.getText(), rowPos, 0);
                            srcTable.setValueAt(txtNamaAkun.getText(), rowPos, 1);
                            srcTable.setValueAt(cmbTipeAkun.getSelectedItem().toString(),rowPos, 2);
                            srcTable.setValueAt("0",rowPos, 3);
                        }
                    }
                if(objForm instanceof FrmAkunList){
                    ((FrmAkunList)objForm).udfFilter(txtNoAkun.getText());
                    ((FrmAkunList)objForm).requestFocusInWindow();
                    
                }
//                }else{
////                    txtDivisi.setText(txtKode.getText());
////                    lblDivisi.setText(txtNama.getText());
//                }
                dispose();
                conn.setAutoCommit(true);
            } catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);

                JOptionPane.showMessageDialog(this, "Gagal Simpan\n" + se.getMessage());
            } catch (SQLException ex) {
                //Logger.getLogger(FrmItemMaster.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.getMessage());
            }
        }
            
//        }
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JCheckBox chkAktif;
    private javax.swing.JCheckBox chkSubAkun;
    private javax.swing.JComboBox cmbTipeAkun;
    private org.jdesktop.swingx.JXDatePicker jDateTgl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblGroupAkun;
    private javax.swing.JLabel lblMataUang;
    private javax.swing.JLabel lblSubAkun;
    private javax.swing.JTextPane txtCatatan;
    private javax.swing.JTextField txtGroupAkun;
    private javax.swing.JTextField txtMataUang;
    private javax.swing.JTextField txtNamaAkun;
    private javax.swing.JTextField txtNoAkun;
    private javax.swing.JTextField txtSaldoAwal;
    private javax.swing.JTextField txtSubAkun;
    // End of variables declaration//GEN-END:variables
    private Object objForm;

    void setMainForm(Object aThis) {
        this.objForm=aThis;
    }

}
