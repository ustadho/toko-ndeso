/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmAkunList.java
 *
 * Created on Mar 6, 2011, 3:36:26 PM
 */

package akuntansi;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.JDesktopImage;

/**
 *
 * @author cak-ust
 */
public class FrmAkunList extends javax.swing.JInternalFrame {
    private Connection conn;
    private List lstAccType=new ArrayList();
    private JDesktopImage jDesktop;

    /** Creates new form FrmAkunList */
    public FrmAkunList() {
        initComponents();
        tblCoa.getTableHeader().setReorderingAllowed(false);
        tblCoa.setHighlighters(HighlighterFactory.createSimpleStriping());
        tblCoa.getColumn("Nama").setPreferredWidth(250);
        tblCoa.getColumn("Tipe").setPreferredWidth(150);
        tblCoa.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                hypDelete.setEnabled(tblCoa.getSelectedRow()>=0);
                hypEdit.setEnabled(tblCoa.getSelectedRow()>=0);
            }
        });
    }

    public void setDesktop(JDesktopImage desktop){
        this.jDesktop=desktop;
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        lstAccType.clear();     cmbAccType.removeAllItems();
        lstAccType.add("");     cmbAccType.addItem("<Semua>");
        
        try{
            ResultSet rs=conn.createStatement().executeQuery("select type_id, coalesce(type_name,'') as type_name " +
                    "from acc_group order by type_id");
            while(rs.next()){
                lstAccType.add(rs.getString("type_id"));
                cmbAccType.addItem(rs.getString("type_name"));
            }
            cmbAccType.setSelectedIndex(0);
            rs.close();

            udfFilter("");
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void udfFilter(String sKode){
        if(cmbAccType.getSelectedIndex()<0) return;
        int iRow=0;
        try{
            ((DefaultTableModel)tblCoa.getModel()).setNumRows(0);
            String sQry="select coa.acc_no, coalesce(coa.acc_name,'') as acc_name, coalesce(g.type_name,'') as type_name, " +
                    "sum(coalesce(d.debit,0)-coalesce(d.credit,0)) as saldo " +
                    "from acc_coa coa  " +
                    "inner join acc_group g on g.type_id=coa.acc_type " +
                    "left join acc_journal_Detail d on d.acc_no=coa.acc_no " +
                    "where coa.acc_no||coalesce(coa.acc_name,'') ilike '%"+txtCari.getText()+"%' "+
                    (cmbAccType.getSelectedIndex()==0? "": " and coa.acc_type='"+lstAccType.get(cmbAccType.getSelectedIndex()).toString()+"' ") +
                    (cmbAktif.getSelectedIndex()==0? "": " and coa.active="+(cmbAktif.getSelectedIndex()==1)+" ") +
                    "group by coa.acc_no, coalesce(coa.acc_name,'') , coalesce(g.type_name,''),coa.acc_type " +
                    "order by coa.acc_type, coa.acc_no";
            //System.out.println(sQry);

            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblCoa.getModel()).addRow(new Object[]{
                    rs.getString("acc_no"),
                    rs.getString("acc_name"),
                    rs.getString("type_name"),
                    rs.getDouble("saldo")
                });
                if(rs.getString("acc_no").equalsIgnoreCase(sKode))
                    iRow=tblCoa.getRowCount()-1;
            }
            rs.close();
            if(tblCoa.getRowCount()>0)
                tblCoa.setRowSelectionInterval(iRow, iRow);


        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfDelete() {
        int iRow = tblCoa.getSelectedRow();

        if(iRow>=0){
            if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus Akun '"+tblCoa.getValueAt(iRow, 2).toString()+" ("+tblCoa.getValueAt(iRow, 0).toString()+")'?", "Confirm", JOptionPane.YES_NO_OPTION)==
                    JOptionPane.YES_OPTION){
                    try {
                        int iDel = conn.createStatement().executeUpdate("Delete from acc_coa where acc_no='" + tblCoa.getValueAt(iRow, 0).toString() + "'");

                        if(iDel>0){
                            JOptionPane.showMessageDialog(this, "Hapus data sukses!!!");
                            ((DefaultTableModel)tblCoa.getModel()).removeRow(iRow);

                            if(iRow>=tblCoa.getRowCount() && ((DefaultTableModel)tblCoa.getModel()).getRowCount()>0){
                                tblCoa.setRowSelectionInterval(iRow-1, iRow-1);
    //                        }else if(iRow>myModel.getRowCount()){
    //                            masterTable.setRowSelectionInterval(iRow-1, iRow-1);
                            }else if(((DefaultTableModel)tblCoa.getModel()).getRowCount()>0){
                                tblCoa.setRowSelectionInterval(iRow, iRow);
                            }
                        }else{
                            JOptionPane.showMessageDialog(this, "Hapus data gagal!!!");
                        }
                    } catch (SQLException ex) {
                        //Logger.getLogger(AgamaList.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, "Hapus data gagal");
                    }

            }
        }
    }

    public void udfNew(){
        AkunMaster fMaster=new AkunMaster();
        fMaster.setConn(conn);
        fMaster.setAccNo("");
        fMaster.setNew(true);
        fMaster.setMainForm(this);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
        } catch(PropertyVetoException PO){

        }
    }


    private void udfEdit(){
        int iRow=tblCoa.getSelectedRow();
        if(iRow<0) return;

        AkunMaster fMaster=new AkunMaster();
        fMaster.setConn(conn);
        fMaster.setAccNo("");
        fMaster.setAccNo(tblCoa.getValueAt(iRow, 0).toString());
        fMaster.setNew(false);
        fMaster.setMainForm(this);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
        } catch(PropertyVetoException PO){

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

        jPanel1 = new javax.swing.JPanel();
        hypDelete = new org.jdesktop.swingx.JXHyperlink();
        hypNew = new org.jdesktop.swingx.JXHyperlink();
        hypEdit = new org.jdesktop.swingx.JXHyperlink();
        jLabel3 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCoa = new org.jdesktop.swingx.JXTable();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbAccType = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbAktif = new javax.swing.JComboBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Akun");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        hypDelete.setText("Hapus");
        hypDelete.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        hypDelete.setName("hypDelete"); // NOI18N
        hypDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(hypDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 50, 20));

        hypNew.setText("Baru");
        hypNew.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        hypNew.setName("hypNew"); // NOI18N
        hypNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypNewActionPerformed(evt);
            }
        });
        jPanel1.add(hypNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 20));

        hypEdit.setText("Ubah");
        hypEdit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        hypEdit.setName("hypEdit"); // NOI18N
        hypEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypEditActionPerformed(evt);
            }
        });
        jPanel1.add(hypEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 40, 20));

        jLabel3.setText("Pencarian"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 80, 20));

        txtCari.setName("txtCari"); // NOI18N
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });
        jPanel1.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 262, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblCoa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Akun", "Nama", "Tipe", "Saldo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCoa.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblCoa.setName("tblCoa"); // NOI18N
        jScrollPane1.setViewportView(tblCoa);

        jXTitledPanel1.setTitle("Filter");
        jXTitledPanel1.setName("jXTitledPanel1"); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Tipe");
        jLabel1.setName("jLabel1"); // NOI18N
        jXTitledPanel1.getContentContainer().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 20));

        cmbAccType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAccType.setName("cmbAccType"); // NOI18N
        cmbAccType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAccTypeItemStateChanged(evt);
            }
        });
        jXTitledPanel1.getContentContainer().add(cmbAccType, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 150, -1));

        jLabel2.setText("Aktif");
        jLabel2.setName("jLabel2"); // NOI18N
        jXTitledPanel1.getContentContainer().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 50, 20));

        cmbAktif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Semua>", "Aktif", "Non Aktif" }));
        cmbAktif.setName("cmbAktif"); // NOI18N
        cmbAktif.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbAktifItemStateChanged(evt);
            }
        });
        jXTitledPanel1.getContentContainer().add(cmbAktif, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 150, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jXTitledPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTitledPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(3, 3, 3))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-761)/2, (screenSize.height-457)/2, 761, 457);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        udfFilter("");
}//GEN-LAST:event_txtCariKeyReleased

    private void cmbAccTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAccTypeItemStateChanged
        udfFilter("");
    }//GEN-LAST:event_cmbAccTypeItemStateChanged

    private void cmbAktifItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbAktifItemStateChanged
        udfFilter("");
    }//GEN-LAST:event_cmbAktifItemStateChanged

    private void hypNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypNewActionPerformed
        udfNew();
    }//GEN-LAST:event_hypNewActionPerformed

    private void hypEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_hypEditActionPerformed

    private void hypDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypDeleteActionPerformed
        udfDelete();
    }//GEN-LAST:event_hypDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbAccType;
    private javax.swing.JComboBox cmbAktif;
    private org.jdesktop.swingx.JXHyperlink hypDelete;
    private org.jdesktop.swingx.JXHyperlink hypEdit;
    private org.jdesktop.swingx.JXHyperlink hypNew;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private javax.swing.JPanel panelHeader;
    private org.jdesktop.swingx.JXTable tblCoa;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables

}
