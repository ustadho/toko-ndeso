/*
 * ListAkun.java
 *
 * Created on August 23, 2008, 10:21 AM
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 *
 * @author  oestadho
 */
public class FrmSupplierList extends javax.swing.JInternalFrame {
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
    public FrmSupplierList() {
        initComponents();
        
        
        panelHeader.setBorder(dsb);
        panelFilter.setBorder(dsb);
        jScrollPane2.setBorder(dsb);
        
//        for (Component child : this.getComponents()) {
//            if (child instanceof JComponent) {
//                child.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
//            }
//        }
        
//        btnPrint.setVisible(false);
//        panelFilter.setVisible(false);
//        chkFilter.setVisible(false);
        
        
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
        
        txtNama.setText("");
        txtAlamat.setText("");
        
        udfFilter();

        //Setting untuk SHSB
//        masterTable.getColumnModel().removeColumn(masterTable.getColumn("Alamat"));
//        masterTable.getColumnModel().removeColumn(masterTable.getColumn("Kecamatan"));
//        masterTable.getColumnModel().removeColumn(masterTable.getColumn("Kabupaten"));
//        masterTable.getColumnModel().removeColumn(masterTable.getColumn("Resort"));

        masterTable.getColumn("Nama Supplier").setPreferredWidth(150);
        masterTable.getColumn("Alamat").setPreferredWidth(150);
        masterTable.setHighlighters(HighlighterFactory.createSimpleStriping());
    }
    
    private void udfFilter(){
        if(myModel==null) return;
        
        myModel.setNumRows(0);
        
        String s="select kode_supp, nama_supp, coalesce(alamat_1, '') as alamat, coalesce(telepon,'') as telepon, coalesce(kontak,'') as kontak " +
                "from r_supplier " +
                "where coalesce(nama_supp,'') iLike '%"+txtNama.getText()+"%' and coalesce(alamat_1,'') iLike '%"+txtAlamat.getText()+"%' " +
                (cmbAktif.getSelectedIndex()==0? "":"and active="+(cmbAktif.getSelectedIndex()==1)) +" "+
                "order by kode_supp" ;
        
        System.out.println(s);
        
        try {
//            ResultSet rs = conn.createStatement().executeQuery("select * from agama where " +
//                    "kdagama Ilike '%"+txtKode.getText()+"%' and desagama iLike '%"+txtNama.getText()+"%'");
            
            ResultSet rs = conn.createStatement().executeQuery(s);
            while(rs.next()){
                myModel.addRow(new Object[]{
                    rs.getString("kode_supp"),
                    rs.getString("nama_supp"),
                    rs.getString("alamat"),
                    rs.getString("telepon"),
                    rs.getString("kontak")
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
        hiperLinkNew = new org.jdesktop.swingx.JXHyperlink();
        hiperLinkUpd = new org.jdesktop.swingx.JXHyperlink();
        hiperLinkDel = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jPanel2 = new javax.swing.JPanel();
        txtNama = new javax.swing.JTextField();
        txtAlamat = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        chkFilter = new javax.swing.JCheckBox();
        btnPrint = new javax.swing.JButton();
        panelFilter = new org.jdesktop.swingx.JXTitledPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbAktif = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Supplier"); // NOI18N
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

        hiperLinkNew.setMnemonic('B');
        hiperLinkNew.setText("Baru"); // NOI18N
        hiperLinkNew.setFont(new java.awt.Font("Tahoma 11", 1, 12));
        hiperLinkNew.setName("hiperLinkNew"); // NOI18N
        hiperLinkNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkNewActionPerformed(evt);
            }
        });

        hiperLinkUpd.setMnemonic('U');
        hiperLinkUpd.setText("Ubah"); // NOI18N
        hiperLinkUpd.setFont(new java.awt.Font("Tahoma 11", 1, 12));
        hiperLinkUpd.setName("hiperLinkUpd"); // NOI18N
        hiperLinkUpd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkUpdActionPerformed(evt);
            }
        });

        hiperLinkDel.setMnemonic('H');
        hiperLinkDel.setText("Hapus"); // NOI18N
        hiperLinkDel.setFont(new java.awt.Font("Tahoma", 1, 11));
        hiperLinkDel.setName("hiperLinkDel"); // NOI18N
        hiperLinkDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkDelActionPerformed(evt);
            }
        });

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
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 89, -1));

        txtAlamat.setName("txtAlamat"); // NOI18N
        txtAlamat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAlamatKeyReleased(evt);
            }
        });
        jPanel2.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 262, -1));

        jLabel3.setText("Alamat"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 260, 20));

        jLabel4.setText("Nama"); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 20));

        chkFilter.setSelected(true);
        chkFilter.setText("Filter"); // NOI18N
        chkFilter.setName("chkFilter"); // NOI18N
        chkFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/print_kecil.png"))); // NOI18N
        btnPrint.setName("btnPrint"); // NOI18N

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(hiperLinkUpd, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hiperLinkDel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(hiperLinkNew, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addComponent(chkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {hiperLinkDel, hiperLinkNew, hiperLinkUpd});

        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hiperLinkDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hiperLinkUpd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hiperLinkNew, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelHeaderLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnPrint)
                .addGap(37, 37, 37))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(chkFilter)
                .addGap(38, 38, 38))
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPrint, hiperLinkDel, hiperLinkNew, hiperLinkUpd, jXHyperlink5});

        panelFilter.setTitle("Filter"); // NOI18N
        panelFilter.setName("panelFilter"); // NOI18N

        jLabel2.setText("Status Aktif"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cmbAktif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Ya", "Tidak" }));
        cmbAktif.setName("cmbAktif"); // NOI18N
        cmbAktif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAktifActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter.getContentContainer());
        panelFilter.getContentContainer().setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(cmbAktif, javax.swing.GroupLayout.Alignment.LEADING, 0, 130, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbAktif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(379, Short.MAX_VALUE))
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Supplier", "Nama Supplier", "Alamat", "Telepon", "Kontak"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
        jScrollPane2.setViewportView(masterTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHeader, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
   
}//GEN-LAST:event_formInternalFrameClosed

private void hiperLinkNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkNewActionPerformed
    udfNew();
    
    
}//GEN-LAST:event_hiperLinkNewActionPerformed

private void chkFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFilterActionPerformed
    panelFilter.setVisible(chkFilter.isSelected());
}//GEN-LAST:event_chkFilterActionPerformed

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void hiperLinkDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkDelActionPerformed
    int iRow = masterTable.getSelectedRow();
    
    if(iRow>=0){
        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus data tersebut?", "Confirm", JOptionPane.YES_NO_OPTION)==
                JOptionPane.YES_OPTION){
                try {
                    int iDel = conn.createStatement().executeUpdate("Delete from r_supplier where kode_supp='" + masterTable.getValueAt(iRow, 0).toString() + "'");
                
                    if(iDel>0){
                        JOptionPane.showMessageDialog(this, "Hapus data sukses!!!");
                        myModel.removeRow(iRow);
                        
                        if(iRow>=myModel.getRowCount() && myModel.getRowCount()>0){
                            masterTable.setRowSelectionInterval(iRow-1, iRow-1);
//                        }else if(iRow>myModel.getRowCount()){
//                            masterTable.setRowSelectionInterval(iRow-1, iRow-1);
                        }else if(myModel.getRowCount()>0){
                            masterTable.setRowSelectionInterval(iRow, iRow);
                        }
                    }
                } catch (SQLException ex) {
                    //Logger.getLogger(AgamaList.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this, "Hapus data gagal\n"+ex.getMessage());
                }
                
        }
    }
}//GEN-LAST:event_hiperLinkDelActionPerformed

private void hiperLinkUpdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkUpdActionPerformed
    udfUpdate();
            
}//GEN-LAST:event_hiperLinkUpdActionPerformed

private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
    udfInitForm();
}//GEN-LAST:event_jXHyperlink5ActionPerformed

private void txtNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyReleased
    udfFilter();
}//GEN-LAST:event_txtNamaKeyReleased

private void txtAlamatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAlamatKeyReleased
    udfFilter();
}//GEN-LAST:event_txtAlamatKeyReleased

private void cmbAktifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAktifActionPerformed
    if(conn!=null  && myModel!=null) udfFilter();
}//GEN-LAST:event_cmbAktifActionPerformed

private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
    if(evt.getClickCount()==2)
        udfUpdate();
}//GEN-LAST:event_masterTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JComboBox cmbAktif;
    private org.jdesktop.swingx.JXHyperlink hiperLinkDel;
    private org.jdesktop.swingx.JXHyperlink hiperLinkNew;
    private org.jdesktop.swingx.JXHyperlink hiperLinkUpd;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXTable masterTable;
    private org.jdesktop.swingx.JXTitledPanel panelFilter;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JSlider shadowSizeSlider;

    private void udfNew() {
        FrmSupplierMaster fMaster=new FrmSupplierMaster();
        fMaster.setTitle("Supplier baru");
        fMaster.setConn(conn);
        fMaster.setIsNew(true);
        fMaster.setSrcTable(masterTable);
        fMaster.setSrcModel(myModel);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
        } catch(PropertyVetoException PO){

        }
        }

    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
            FrmSupplierMaster fMaster=new FrmSupplierMaster();
            fMaster.setTitle("Update Supplier");
            //fMaster.settDesktopPane(jDesktop);
            fMaster.setConn(conn);
            fMaster.setSrcModel(myModel);
            fMaster.setSrcTable(masterTable);
            fMaster.setIsNew(false);
            fMaster.setKodeSupp(masterTable.getValueAt(iRow, 0).toString());
            fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
            jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
            fMaster.setVisible(true);
            try{
                //fMaster.setMaximum(true);
                fMaster.setSelected(true);
            } catch(PropertyVetoException PO){

            }
        }
    }
}
