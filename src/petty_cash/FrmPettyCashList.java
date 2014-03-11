/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPettyCashList.java
 *
 * Created on 13 Mei 11, 21:11:41
 */
package petty_cash;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.GeneralFunction;
import retail.main.JDesktopImage;

/**
 *
 * @author cak-ust
 */
public class FrmPettyCashList extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private JDesktopImage desktopPane;
            
    
    /** Creates new form FrmPettyCashList */
    public FrmPettyCashList() {
        initComponents();
        jDateAwal.setFormats("dd/MM/yyyy");
        jDateAkhir.setFormats("dd/MM/yyyy");
        jXTable1.setHighlighters(HighlighterFactory.createAlternateStriping());
        
        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int iRow=jXTable1.getSelectedRow();
                btnDelete.setEnabled(iRow>=0);
                btnEdit.setEnabled(iRow>=0);
                
            }
        });
        jXTable1.getTableHeader().setReorderingAllowed(false);
    }
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void udfLoadPettyCash(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select no_trx, tanggal, coalesce(bayar_ke_dari,'') as bayar_ke_dari, "
                    + "coalesce(keterangan,'') as keterangan, coalesce(keluar,0) as keluar, coalesce(masuk,0) as masuk "
                    + "from petty_cash where to_Char(tanggal, 'yyyy-MM-dd')>='"+fn.yyyymmdd_format.format(jDateAwal.getDate()) +"' "
                    + "and to_Char(tanggal, 'yyyy-MM-dd')<='"+fn.yyyymmdd_format.format(jDateAkhir.getDate()) +"' order by time_ins "); 
            ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getString("no_trx"),
                    rs.getDate("tanggal"),
                    rs.getString("bayar_ke_dari"),
                    rs.getString("keterangan"),
                    rs.getDouble("masuk"),
                    rs.getDouble("keluar")
                });
            }
            if(jXTable1.getRowCount()>0){
                jXTable1.setRowSelectionInterval(0, 0);
                jXTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jXTable1, (DefaultTableModel)jXTable1.getModel()).getModel());
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jDateAkhir = new org.jdesktop.swingx.JXDatePicker();
        jDateAwal = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnTampil = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Petty Cash");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Trx", "Tanggal", "Terima Dari/ Dibayar Kepada", "Keterangan", "Masuk", "Keluar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.setName("jXTable1"); // NOI18N
        jScrollPane1.setViewportView(jXTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 43, 747, 250));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jDateAkhir.setName("jDateAkhir"); // NOI18N
        jPanel1.add(jDateAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 130, -1));

        jDateAwal.setName("jDateAwal"); // NOI18N
        jPanel1.add(jDateAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 120, -1));

        jLabel1.setText("Sampai");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 40, 20));

        jLabel2.setText("Dari");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 20));

        btnTampil.setText("Tampilkan");
        btnTampil.setName("btnTampil"); // NOI18N
        btnTampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilActionPerformed(evt);
            }
        });
        jPanel1.add(btnTampil, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 80, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, 40));

        btnDelete.setText("Hapus");
        btnDelete.setEnabled(false);
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 300, 70, -1));

        btnNew.setText("Baru");
        btnNew.setName("btnNew"); // NOI18N
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        getContentPane().add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 70, -1));

        btnEdit.setText("Ubah");
        btnEdit.setEnabled(false);
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        getContentPane().add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 300, 70, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilActionPerformed
        udfLoadPettyCash();
    }//GEN-LAST:event_btnTampilActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfLoadPettyCash();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus petty cash ini?", "Hapus Petty Cash", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                String sNoTrx=jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString();
                int i=conn.createStatement().executeUpdate("delete from petty_cash where no_trx='"+sNoTrx+"'");
                if(i>0){
                    JOptionPane.showMessageDialog(this, "Petty Cash telah terhapus!");
                    udfLoadPettyCash();
                }
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        String sNoTrx=jXTable1.getValueAt(jXTable1.getSelectedRow(), 0).toString();
        FrmPettyCash f1=new FrmPettyCash();
        f1.setConn(conn);
        f1.udfLoadPettyCash(sNoTrx);
        f1.setVisible(true);
        f1.setSrcForm(this);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        desktopPane.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try{
            f1.setSelected(true);
        } catch(PropertyVetoException PO){}
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        FrmPettyCash f1=new FrmPettyCash();
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setSrcForm(this);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        desktopPane.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        try{
            f1.setSelected(true);
        } catch(PropertyVetoException PO){}
    }//GEN-LAST:event_btnNewActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnTampil;
    private org.jdesktop.swingx.JXDatePicker jDateAkhir;
    private org.jdesktop.swingx.JXDatePicker jDateAwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    // End of variables declaration//GEN-END:variables

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktopPane=jDesktopPane1;
    }
}
