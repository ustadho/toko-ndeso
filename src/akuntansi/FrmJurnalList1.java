/*
 * ListAkun.java
 *
 * Created on August 23, 2008, 10:21 AM
 */

package akuntansi;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.border.DropShadowBorder;


/**
 *
 * @author  oestadho
 */
public class FrmJurnalList1 extends javax.swing.JInternalFrame {
    //private DropShadowBorder dsb = new DropShadowBorder(UIManager.getColor("Control"), 0, 8, .5f, 12, false, true, true, true);
    DefaultTableModel myModel;
    private DropShadowBorder dsb=new DropShadowBorder();
    //private DropShadowBorder dsb = new DropShadowBorder(new Color(255,240,240), 0, 8, .5f, 12, false, true, true, true);
    
    private Connection conn;
    private JDesktopPane jDesktop;
    private ArrayList lstResort=new ArrayList();
    private String sUserName;
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    
    /** Creates new form ListAkun */
    public FrmJurnalList1() {
        initComponents();
        
        
        panelHeader.setBorder(dsb);
//        panelFilter.setBorder(dsb);
        jScrollPane1.setBorder(dsb);
//        masterTable.getColumn(1).setPreferredWidth(230);
    }

    public void setDesktopPane(JDesktopPane jDesktopPane1) {
        jDesktop =jDesktopPane1;
    }

    void setUserName(String sUserName) {
        this.sUserName=sUserName;
    }

    private void udfInitForm(){
        myModel=(DefaultTableModel)masterTable.getModel();
        masterTable.setModel(myModel);
        myModel.setNumRows(0);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select current_date");
            rs.next();
            jDateChooser1.setDate(rs.getDate(1));
            jDateChooser2.setDate(rs.getDate(1));
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        chkTanggal.setSelected(true);
        
//        txtKode.setText("");
        txtNama.setText("");
        
        udfFilter();

//        masterTable.getColumn("Nama Item").setPreferredWidth(200);
       
    }
    
    private void udfFilter(){
        if(myModel==null) return;

        myModel.setNumRows(0);
        String sTipe="";
        sTipe+=(chkTanggal.isSelected()? " or ( tanggal >= '"+ new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser1.getDate()) +"' and tanggal <= '"+ new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser2.getDate()) +"' ) ":" ");
        sTipe+=(udfCekNumeric(txtNama.getText())? (txtNama.getText().length()>0?" or (debit+credit) = "+ txtNama.getText().toString().replace(",", "") : " "):"");
//        sTipe+=(chkService.isSelected()? (sTipe.length()>0? ",": "")+"'S'":"");
//        sTipe+=(chkGroup.isSelected()? (sTipe.length()>0? ",": "")+"'G'":"");
//        sTipe+=(sTipe.length()>0? ",": "")+"''";

        String s="select journal_no, to_char(tanggal,'dd-MM-yyyy') as tanggal, description, d.acc_no, (debit+credit) as amount " +
                 " from acc_journal_detail d left join acc_journal a using(journal_no) " +
                 " where upper(tipe)='JRE' and  (upper(journal_no) like '%"+ txtNama.getText().toUpperCase() +"%' or upper(description) like '%"+ txtNama.getText().toUpperCase() +"%' or upper(d.acc_no) like '%"+ txtNama.getText().toUpperCase() +"%' " + sTipe +")";

//        System.out.println(s);

        try {
//            ResultSet rs = conn.createStatement().executeQuery("select * from agama where " +
//                    "kdagama Ilike '%"+txtKode.getText()+"%' and desagama iLike '%"+txtNama.getText()+"%'");

            ResultSet rs = conn.createStatement().executeQuery(s);
            while(rs.next()){
                myModel.addRow(new Object[]{
                    rs.getString("journal_no"),
                    rs.getString("tanggal"),
                    rs.getString("description"),
                    rs.getString("acc_no"),
                    rs.getDouble("amount")
                });
            }
            if(myModel.getRowCount()>0) masterTable.setRowSelectionInterval(0, 0);
            //cmbTipeAkun.setSelectedIndex(-1);
            rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error from udfInitForm", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }

    private boolean udfCekNumeric(String str){
        boolean flag = true;
        char[] stringArray;
        stringArray = str.toCharArray();
        for(int index=0; index < stringArray.length; index++){
//            System.out.print(stringArray[index]);
            if (!((stringArray[index] >= '0' && stringArray[index] <= '9')) &&
                  stringArray[index] != '-' && stringArray[index] != ' '){
                flag = false;
          }
        }
        return flag;
    }

    

    private void udfLoadJournalEntry(){
        FrmJournalEntry fJournal=new FrmJournalEntry();
        fJournal.setConn(conn);
        fJournal.setKoreksi(true);
        fJournal.setKode(masterTable.getValueAt(masterTable.getSelectedRow(), 0).toString());
        fJournal.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fJournal.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
    }

    private void udfFilterKategori(){
        if(myModel==null) return;

        myModel.setNumRows(0);
        String sTipe="";
        sTipe+=(chkTanggal.isSelected()? " or ( tanggal >= '"+ new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser1.getDate()) +"' and tanggal <= '"+ new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser2.getDate()) +"' ) ":" ");
        sTipe+=(udfCekNumeric(txtNama.getText())? (txtNama.getText().length()>0?" or d.amount = "+ txtNama.getText().toString().replace(",", "") : " "):"");
//        sTipe+=(chkService.isSelected()? (sTipe.length()>0? ",": "")+"'S'":"");
//        sTipe+=(chkGroup.isSelected()? (sTipe.length()>0? ",": "")+"'G'":"");
//        sTipe+=(sTipe.length()>0? ",": "")+"''";

        String s="select no_voucher, to_char(tanggal,'dd-MM-yyyy') as tangal, a.memo, d.acc_no, d.amount " + 
                 " from acc_bukti_kas_detail d left join acc_bukti_kas a using(no_bukti) " +
                 " where no_voucher like '%"+ txtNama.getText() +"%' or a.memo like '%"+ txtNama.getText() +"%' " + sTipe;

//        System.out.println(s);

        try {
//            ResultSet rs = conn.createStatement().executeQuery("select * from agama where " +
//                    "kdagama Ilike '%"+txtKode.getText()+"%' and desagama iLike '%"+txtNama.getText()+"%'");

            ResultSet rs = conn.createStatement().executeQuery(s);
            while(rs.next()){
                myModel.addRow(new Object[]{
                    rs.getString("no_voucher"),
                    rs.getString("tangal"),
                    rs.getString("memo"),
                    rs.getString("acc_no"),
                    rs.getDouble("amount")
                    
                    
                });
            }
            if(myModel.getRowCount()>0) masterTable.setRowSelectionInterval(0, 0);
            //cmbTipeAkun.setSelectedIndex(-1);
            rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error from udfInitForm", JOptionPane.INFORMATION_MESSAGE);
        }

    }
//    private void redraw() {
//        DropShadowBorder old = dsb;
//        dsb = new DropShadowBorder(dsb.getLineColor(),
//                dsb.getLineWidth(), 8,
//                dsb.getShadowOpacity(), dsb.getCornerSize(),true, false, false, true);
////                topShadowCB.isSelected(), leftShadowCB.isSelected(),
////                bottomShadowCB.isSelected(), rightShadowCB.isSelected());
//        
//        //iterate down the containment heirarchy, replacing any old dsb's with
//        //the new one
//        replaceBorder(old, this);
//        repaint();
//    }
//    
//    private void replaceBorder(DropShadowBorder old, JComponent c) {
//        if (c.getBorder() == old) {
//            c.setBorder(dsb);
//        }
//        
//        for (Component child : c.getComponents()) {
//            if (child instanceof JComponent) {
//                replaceBorder(old, (JComponent)child);
//            }
//        }
//    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jPanel2 = new javax.swing.JPanel();
        txtNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblUbah = new org.jdesktop.swingx.JXHyperlink();
        jScrollPane1 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        chkTanggal = new javax.swing.JCheckBox();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        btnLoad = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Bukti Jurnal Umum"); // NOI18N
        setName("Form"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        panelHeader.setName("panelHeader"); // NOI18N

        jXHyperlink5.setMnemonic('R');
        jXHyperlink5.setName("jXHyperlink5"); // NOI18N
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink5ActionPerformed(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNama.setName("txtNama"); // NOI18N
        txtNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNamaKeyReleased(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 280, -1));

        jLabel3.setText("Cari "); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 20));

        lblUbah.setMnemonic('U');
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(FrmJurnalList1.class);
        lblUbah.setText(resourceMap.getString("lblUbah.text")); // NOI18N
        lblUbah.setFont(resourceMap.getFont("lblUbah.font")); // NOI18N
        lblUbah.setName("lblUbah"); // NOI18N
        lblUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblUbahActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUbah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        masterTable.setAutoCreateRowSorter(true);
        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Sumber", "No. Sumber", "Keterangan", "No. Akun", "Nama Akun", "Amount", "Flag"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        masterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        masterTable.setName("masterTable"); // NOI18N
        masterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masterTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(masterTable);
        masterTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        masterTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("masterTable.columnModel.title1")); // NOI18N
        masterTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("masterTable.columnModel.title7")); // NOI18N
        masterTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        masterTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("masterTable.columnModel.title0")); // NOI18N
        masterTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        masterTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("masterTable.columnModel.title2")); // NOI18N
        masterTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        masterTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("masterTable.columnModel.title3")); // NOI18N
        masterTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("masterTable.columnModel.title6")); // NOI18N
        masterTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        masterTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("masterTable.columnModel.title4")); // NOI18N
        masterTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        masterTable.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("masterTable.columnModel.title5")); // NOI18N

        jXTitledPanel1.setTitle(resourceMap.getString("jXTitledPanel1.title")); // NOI18N
        jXTitledPanel1.setName("jXTitledPanel1"); // NOI18N

        chkTanggal.setText(resourceMap.getString("chkTanggal.text")); // NOI18N
        chkTanggal.setName("chkTanggal"); // NOI18N

        jDateChooser1.setDateFormatString(resourceMap.getString("jDateChooser1.dateFormatString")); // NOI18N
        jDateChooser1.setName("jDateChooser1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jDateChooser2.setDateFormatString(resourceMap.getString("jDateChooser2.dateFormatString")); // NOI18N
        jDateChooser2.setName("jDateChooser2"); // NOI18N

        btnLoad.setText("Load"); // NOI18N
        btnLoad.setName("btnLoad"); // NOI18N
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXTitledPanel1Layout = new javax.swing.GroupLayout(jXTitledPanel1.getContentContainer());
        jXTitledPanel1.getContentContainer().setLayout(jXTitledPanel1Layout);
        jXTitledPanel1Layout.setHorizontalGroup(
            jXTitledPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXTitledPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkTanggal)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jXTitledPanel1Layout.setVerticalGroup(
            jXTitledPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXTitledPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkTanggal)
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLoad)
                .addContainerGap(285, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    
}//GEN-LAST:event_formInternalFrameClosed

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
    udfInitForm();
}//GEN-LAST:event_jXHyperlink5ActionPerformed

private void txtNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyReleased
    udfFilter();
}//GEN-LAST:event_txtNamaKeyReleased

private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
    if(evt.getClickCount()==2){
        udfUpdate();
    }
}//GEN-LAST:event_masterTableMouseClicked

private void lblUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblUbahActionPerformed
    if (masterTable.getSelectedRow()>=0)   udfLoadJournalEntry();
}//GEN-LAST:event_lblUbahActionPerformed

private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
    udfFilter();
}//GEN-LAST:event_btnLoadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoad;
    private javax.swing.JCheckBox chkTanggal;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private org.jdesktop.swingx.JXHyperlink lblUbah;
    private org.jdesktop.swingx.JXTable masterTable;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JSlider shadowSizeSlider;

    private void udfNew() {
//        FrmItemMaster fMaster=new FrmItemMaster();
//        fMaster.setTitle("Item baru");
//        fMaster.setConn(conn);
//        fMaster.setKodeBarang("XXXXXXXXXXXXXXXXXXXXXXXXXX");
//        fMaster.setIsNew(true);
//        fMaster.setSrcTable(masterTable);
//        fMaster.setSrcModel(myModel);
//        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
//        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        fMaster.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fMaster.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
        }

    private void udfSetSaldoAwal() {
        int i=masterTable.getSelectedRow();
        if(i>=0){
//            DlgSaldoAwal d1=new DlgSaldoAwal(JOptionPane.getFrameForComponent(this), false);
//            d1.setConn(conn);
//            d1.setNoAnggota(masterTable.getValueAt(i, 0).toString());
//            d1.setTitle("Saldo awal anggota : "+masterTable.getValueAt(i, 0).toString()+" - "+masterTable.getValueAt(i, 1).toString());
//            d1.setVisible(true);
        }
    }

    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
//            FrmItemMaster fMaster=new FrmItemMaster();
//            fMaster.setTitle("Update Item / Barang");
//            //fMaster.settDesktopPane(jDesktop);
//            fMaster.setConn(conn);
//            fMaster.setSrcModel(myModel);
//            fMaster.setSrcTable(masterTable);
//            fMaster.setKodeBarang(masterTable.getValueAt(iRow, 0).toString());
//            fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
//            jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
//            fMaster.setVisible(true);
//            try{
//                //fMaster.setMaximum(true);
//                fMaster.setSelected(true);
//            } catch(PropertyVetoException PO){
//
//            }
            
        }
    }
}
