/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemGroup.java
 *
 * Created on 14 Feb 11, 9:03:56
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmItemGroup extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private boolean stItemUpd=false;
    private Component aThis;
    MyKeyListener kListener=new MyKeyListener();
    private String sOldKode="xxx";
    TableColumnModel col;
    private JXTable srcTable;
    private Object objForm;

    /** Creates new form FrmItemGroup */
    public FrmItemGroup() {
        initComponents();
        aThis=this;
        col=table.getColumnModel();
    }

    public void setObjForm(Object obj){
        this.objForm=obj;
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    public void setKode(String s){
        this.sOldKode=s;
    }

    private void udfLoadITemGroup(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select i.kode_item, coalesce(i.nama_item,'') as nama_item, " +
                    "coalesce(i.akun_sales,'') as akun_jual, coalesce(a1.acc_name,'') as acc_jual_name," +
                    "coalesce(i.akun_disk_jual,'') as akun_disc, coalesce(a2.acc_name,'') as acc_disc_name," +
                    "coalesce(i.akun_ret_jual,'') as akun_retur, coalesce(a3.acc_name,'') as acc_ret_name," +
                    "coalesce(i.unit,'') as unit " +
                    "from r_item i " +
                    "left join acc_coa a1 on a1.acc_no=akun_sales " +
                    "left join acc_coa a2 on a1.acc_no=akun_disk_jual " +
                    "left join acc_coa a3 on a1.acc_no=akun_ret_jual " +
                    "where kode_item='"+sOldKode+"'");
            if(rs.next()){
                txtGroupID.setText(rs.getString("kode_item"));
                txtNamaGroup.setText(rs.getString("nama_item"));
                txtSatuan.setText(rs.getString("unit"));
                txtAccPenjualan.setText(rs.getString("akun_jual")); lblAccPenjualan.setText(rs.getString("acc_jual_name"));
                txtAccDiscJual.setText(rs.getString("akun_disc")); lblAccDiscJual.setText(rs.getString("acc_disc_name"));
                txtAccReturJual.setText(rs.getString("akun_retur")); lblAccReturJual.setText(rs.getString("acc_ret_name"));

                rs.close();
                ((DefaultTableModel)table.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery("select d.kode_item, coalesce(i.nama_Item,'') as nama_item, coalesce(i.unit,'') as unit, coalesce(d.qty,0) as qty " +
                        "from r_item_group_detail d " +
                        "inner join r_item i on i.kode_item=d.kode_item " +
                        "where d.group_id='"+sOldKode+"'");
                while(rs.next()){
                    ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                        rs.getString("kode_item"),
                        rs.getString("nama_item"),
                        rs.getString("unit"),
                        rs.getDouble("qty")
                    });
                }
                if(table.getRowCount()>0)
                    table.setRowSelectionInterval(0, 0);
                
                rs.close();
            }

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(txtGroupID.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan kode group terlebih dulu!");
            if(!txtGroupID.isFocusOwner())
                txtGroupID.requestFocus();
            return false;
        }
        if(txtNamaGroup.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan nama group terlebih dulu!");
            if(!txtNamaGroup.isFocusOwner())
                txtNamaGroup.requestFocus();
            return false;
        }
        if(((DefaultTableModel)table.getModel()).getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan komponen group terlebih dulu!");
            if(!txtKode.isFocusOwner())
                txtKode.requestFocus();
            return false;
        }
        if(sOldKode.equalsIgnoreCase("") || (stItemUpd && !sOldKode.equalsIgnoreCase(txtGroupID.getText()) ) ){
            try{
                ResultSet rs=conn.createStatement().executeQuery("select * from r_item where kode_item='"+txtGroupID.getText()+"'");
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "Kode Group tersebut sudah pernah dimasukkan.\nSilakan masukkan kode lain!");
                }
                rs.close();
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        boolean isNew=false;
        try{
            conn.setAutoCommit(false);
            ResultSet rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery("select * from r_item where kode_item='"+sOldKode+"'");
            
            if(!rs.next()){
                isNew=true;
                rs.moveToInsertRow();
            }
            rs.updateString("kode_item", txtGroupID.getText());
            rs.updateString("nama_item", txtNamaGroup.getText());
            rs.updateString("tipe", "G");
            rs.updateBoolean("active", chkAktif.isSelected());
            rs.updateBoolean("cetak_item_group", chkCetakItemFaktur.isSelected());
            rs.updateString("akun_sales", txtAccPenjualan.getText());
            rs.updateString("akun_ret_jual", txtAccReturJual.getText());
            rs.updateString("akun_disk_jual", txtAccDiscJual.getText());
            rs.updateString("unit", txtSatuan.getText());
            rs.updateString("unit_jual", txtSatuan.getText());

            if(isNew){
                rs.insertRow();
                if(srcTable!=null){
                    ((DefaultTableModel)srcTable.getModel()).addRow(new Object[]{
                        txtGroupID.getText(),
                        txtNamaGroup.getText(),
                        0,
                        0,
                        "Group"
                    });
                    srcTable.setRowSelectionInterval(srcTable.getRowCount()-1, srcTable.getRowCount()-1);
                    srcTable.changeSelection(srcTable.getRowCount()-1, 0, false, false);
                }
            }else{
                rs.updateRow();
                if(srcTable!=null){
                    int iRow=srcTable.getSelectedRow();
                    srcTable.setValueAt(txtGroupID.getText(), srcTable.getSelectedRow(), 0);
                    srcTable.setValueAt(txtNamaGroup.getText(), srcTable.getSelectedRow(), 1);
                    srcTable.setValueAt(0, srcTable.getSelectedRow(), 2);
                    srcTable.setValueAt(0, srcTable.getSelectedRow(), 3);
                    srcTable.setValueAt("Group", srcTable.getSelectedRow(), 4);
                }
            }
            String sUpd="";
//            if(isNew)
//                sUpd="insert into r_item(" +
//                        "kode_item, nama_item, tipe, active, cetak_item_group, akun_sales, akun_ret_jual, akun_disk_jual) values(" +
//                        "'"+txtGroupID.getText()+"', '"+txtNamaGroup.getText()+"','G', "+chkAktif.isSelected()+","+
//                        chkCetakItemFaktur.isSelected()+",'"+txtAccPenjualan.getText()+"','"+txtAccReturJual.getText()+"', " +
//                        "'"+txtAccDiscJual.getText()+"');";
//            else
//                sUpd="update r_item set " +
//                        "kode_item='"+txtKode.getText()+"', " +
//                        "nama_item='"+txtNamaGroup.getText()+"'," +
//                        "tipe='G', " +
//                        "active="+chkAktif.isSelected()+", " +
//                        "cetak_item_group="+chkCetakItemFaktur.isSelected()+", " +
//                        "akun_sales='"+txtAccPenjualan.getText()+"', " +
//                        "akun_ret_jual='"+txtAccReturJual.getText()+"', " +
//                        "akun_disk_jual='"+txtAccDiscJual.getText()+"' " +
//                        "where kode_item='"+sOldKode+"'; ";

            sUpd+="delete from r_item_group_detail where group_id='"+txtGroupID.getText()+"';";
            for(int i=0; i<table.getRowCount(); i++){
                sUpd+="insert into r_item_Group_detail(group_id, kode_item, qty) values('"+txtGroupID.getText()+"', " +
                        "'"+table.getValueAt(i, col.getColumnIndex("ProductID")).toString()+"', " +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")))+ ")";
            }
            int i=conn.createStatement().executeUpdate(sUpd);
            conn.setAutoCommit(true);
            
            if(objForm instanceof FrmSettingHargaJual)
                ((FrmSettingHargaJual)objForm).udfFilter(txtGroupID.getText());

            JOptionPane.showMessageDialog(this, "Simpan Item Group sukses!");
            if(srcTable!=null) dispose();
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmItemGroup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void udfInitForm(){
        fn.setConn(conn);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        table.addKeyListener(kListener);
        table.getColumn("ProductID").setPreferredWidth(txtKode.getWidth());
        table.getColumn("Nama Barang").setPreferredWidth(lblItem.getWidth());
        table.getColumn("Satuan").setPreferredWidth(lblSatKecil.getWidth());
        table.getColumn("Qty").setPreferredWidth(txtQty.getWidth());
        table.setRowHeight(20);

        if(sOldKode.length()>0)
            udfLoadITemGroup();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(stItemUpd)
                    txtNamaGroup.requestFocusInWindow();
                else
                    txtGroupID.requestFocusInWindow();
            }
        });
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
        txtNamaGroup = new javax.swing.JTextField();
        txtGroupID = new javax.swing.JTextField();
        chkCetakItemFaktur = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        txtAccPenjualan = new javax.swing.JTextField();
        lblAccPenjualan = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        txtAccReturJual = new javax.swing.JTextField();
        lblAccReturJual = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtAccDiscJual = new javax.swing.JTextField();
        lblAccDiscJual = new javax.swing.JLabel();
        chkAktif = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        txtSatuan = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtQty = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        lblSatKecil = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Grup Item");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Nama Group");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 60, 20));

        jLabel2.setText("No. Group");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 60, 20));

        txtNamaGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaGroup.setName("txtNamaGroup"); // NOI18N
        jPanel1.add(txtNamaGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 45, 460, 20));

        txtGroupID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtGroupID.setName("txtGroupID"); // NOI18N
        jPanel1.add(txtGroupID, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 100, 20));

        chkCetakItemFaktur.setText("Cetak Barang di Faktur");
        chkCetakItemFaktur.setName("chkCetakItemFaktur"); // NOI18N
        jPanel1.add(chkCetakItemFaktur, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 150, -1));

        jLabel34.setText("Akun Penjualan");
        jLabel34.setName("jLabel34"); // NOI18N
        jPanel1.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 95, 120, 20));

        txtAccPenjualan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAccPenjualan.setName("txtAccPenjualan"); // NOI18N
        jPanel1.add(txtAccPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 95, 80, 20));

        lblAccPenjualan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAccPenjualan.setName("lblAccPenjualan"); // NOI18N
        jPanel1.add(lblAccPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 95, 380, 20));

        jLabel36.setText("Akun Retur Jual");
        jLabel36.setName("jLabel36"); // NOI18N
        jPanel1.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 120, 20));

        txtAccReturJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAccReturJual.setName("txtAccReturJual"); // NOI18N
        jPanel1.add(txtAccReturJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, 80, 20));

        lblAccReturJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAccReturJual.setName("lblAccReturJual"); // NOI18N
        jPanel1.add(lblAccReturJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, 380, 20));

        jLabel38.setText("Akun Diskon Jual");
        jLabel38.setName("jLabel38"); // NOI18N
        jPanel1.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 145, 120, 20));

        txtAccDiscJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtAccDiscJual.setName("txtAccDiscJual"); // NOI18N
        jPanel1.add(txtAccDiscJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 145, 80, 20));

        lblAccDiscJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAccDiscJual.setName("lblAccDiscJual"); // NOI18N
        jPanel1.add(lblAccDiscJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 145, 380, 20));

        chkAktif.setSelected(true);
        chkAktif.setText("Aktif");
        chkAktif.setName("chkAktif"); // NOI18N
        jPanel1.add(chkAktif, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 70, -1));

        jLabel4.setText("Satuan");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 80, 20));

        txtSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSatuan.setName("txtSatuan"); // NOI18N
        jPanel1.add(txtSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 110, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 600, 180));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        table.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Satuan", "Qty"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
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
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setName("table"); // NOI18N
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 600, 120));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtQty.setName("txtQty"); // NOI18N
        jPanel2.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 60, 20));

        lblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblItem.setName("lblItem"); // NOI18N
        jPanel2.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 0, 290, 20));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setName("txtKode"); // NOI18N
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 20));

        lblSatKecil.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblSatKecil.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSatKecil.setName("lblSatKecil"); // NOI18N
        jPanel2.add(lblSatKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 110, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 600, -1));

        jLabel3.setBackground(new java.awt.Color(0, 0, 102));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Detail Barang");
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setOpaque(true);
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 600, 20));

        jButton1.setMnemonic('S');
        jButton1.setText("Simpan");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 380, 80, 30));

        jButton2.setMnemonic('B');
        jButton2.setText("Batal");
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 380, 70, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfSave();
    }//GEN-LAST:event_jButton1ActionPerformed

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField)){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
//                else if(e.getSource().equals(txtKelas) && !fn.isListVisible()){
//                    sOldKelas=txtKelas.getText();
//                }
            }
        }

        public void focusLost(FocusEvent e) {
            if(e.getSource() instanceof  JTextField  || e.getSource() instanceof  JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtKode) && !e.isTemporary()){
                    if(txtKode.getText().isEmpty()){
                        udfClearItem();
                    }else{
                        String sMessage=udfLoadItem();
                        if(sMessage.length()>0){
                            if(!e.isTemporary())
                                txtKode.requestFocusInWindow();

                            JOptionPane.showMessageDialog(aThis, sMessage);
                            return;
                        }
                    }

                    txtQty.requestFocusInWindow();
                }

           }
        }
    } ;

    private String udfLoadItem(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit " +
                         "from r_item i " +
                         "where i.kode_item='"+txtKode.getText()+"' or coalesce(i.barcode,'')='"+txtKode.getText()+"' ";

                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 if(rs.next()){
                     lblItem.setText(rs.getString("nama_item"));
                     lblSatKecil.setText(rs.getString("unit"));
                     txtQty.setText("1");
                     
                 }else{
                     //JOptionPane.showMessageDialog(aThis, "Item tidak ditemukan!");
                     sMsg="Item tidak ditemukan!";
                 }
                 rs.close();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return sMsg;
    }
    
    private void udfClearItem(){
        txtKode.setText("");
        lblItem.setText("");
        lblSatKecil.setText("");
        txtQty.setText("1");
        stItemUpd=false;
    }

    public void setSrcTable(JXTable masterTable) {
        this.srcTable=masterTable;
    }

    void setIsNew(boolean b) {
        this.stItemUpd=!b;
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(txtKode))
                fn.lookup(evt, new Object[]{lblItem}, "select kode_item, coalesce(nama_item,'') as nama_item from " +
                        "r_item where tipe<>'G' and coalesce(nama_item,'')||kode_item||coalesce(barcode,'') ilike '%"+txtKode.getText()+"%'  order by 2",
                        txtKode.getWidth()+lblItem.getWidth()+18, 200);
            else if(evt.getSource().equals(txtAccPenjualan))
                fn.lookup(evt, new Object[]{lblAccPenjualan},
                "select acc_no, acc_name from acc_coa where acc_type='03' and acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                txtAccPenjualan.getWidth()+lblAccPenjualan.getWidth()+17, 200);
            else if(evt.getSource().equals(txtAccReturJual))
                fn.lookup(evt, new Object[]{lblAccReturJual}, "select acc_no, acc_name from acc_coa where acc_type in('11', '14') and acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                    txtAccReturJual.getWidth()+lblAccReturJual.getWidth()+17, 200);
            else if(evt.getSource().equals(txtAccDiscJual))
                fn.lookup(evt, new Object[]{lblAccDiscJual}, "select acc_no, acc_name from acc_coa where  acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                    txtAccDiscJual.getWidth()+lblAccDiscJual.getWidth()+17, 200);

        }

        @Override
        public void keyTyped(KeyEvent evt){
            if(evt.getSource().equals(txtQty))
                fn.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F4:{
                    //udfNew();
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F3:{
                    int iRow=table.getSelectedRow();
                    if(iRow < 0) return;
                    stItemUpd=true;
                    TableColumnModel col=table.getColumnModel();
                    txtKode.setText(table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString());
                    lblItem.setText(table.getValueAt(iRow, col.getColumnIndex("Nama Barang")).toString());
                    lblSatKecil.setText(table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString());
                    txtQty.setText(fn.intFmt.format(fn.udfGetInt(table.getValueAt(iRow, col.getColumnIndex("Qty")))));
                    txtQty.requestFocusInWindow();
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
//                        if(txtKode.isFocusOwner()){
//                            txtQty.requestFocusInWindow();
//                            return;
                        //}else
                        if((txtQty.isFocusOwner())){
                            udfAddItemToTable();
                            return;
                        }
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(ct instanceof JTable){
//                        if(((JTable)ct).getSelectedRow()==0){
////                            Component c = findNextFocus();
////                            if (c==null) return;
////                            if(c.isEnabled())
////                                c.requestFocus();
////                            else{
////                                c = findNextFocus();
////                                if (c!=null) c.requestFocus();;
////                            }
//                        }
                    }else{
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                        break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findPrevFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findPrevFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
//                            c = findPreFocus();
//                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }
                case KeyEvent.VK_LEFT:{
                    if(table.getSelectedColumn()==2)
                        table.setColumnSelectionInterval(0, 0);
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(table) && table.getSelectedRow()>=0){
                        if(table.getCellEditor()!=null)
                            table.getCellEditor().stopCellEditing();

                        int iRow[]= table.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= table.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(table.convertRowIndexToModel(iRow[0]));
                            iRow = table.getSelectedRows();
                        }
                        table.clearSelection();

                        if(table.getRowCount()>0 && rowPalingAtas<table.getRowCount()){
                            table.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(table.getRowCount()>0)
                                table.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                table.requestFocus();
                        }
                        if(table.getSelectedRow()>=0){
                            table.changeSelection(table.getSelectedRow(), 0, false, false);
                            //cEditor.setValue(table.getValueAt(table.getSelectedRow(), 0).toString());
                        }
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(evt.getSource().equals(txtKode)){
                        udfClearItem();
                        return;
                    }
                    if(table.getRowCount()>0 && JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "Ustasoft",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
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

    private void udfAddItemToTable(){
        if(lblItem.getText().trim().length()==0){
            txtKode.requestFocus();
            JOptionPane.showMessageDialog(this, "Silakan masukkan item terlebih dulu!");
            return;
        }
        if(fn.udfGetDouble(txtQty.getText())==0){
            txtQty.requestFocus();
            JOptionPane.showMessageDialog(this, "Masukkan Qty lebih dari 0!");
            return;
        }
        TableColumnModel col=table.getColumnModel();
        if(stItemUpd){
            int iRow=table.getSelectedRow();
            if(iRow<0) return;
            table.setValueAt(txtKode.getText(), iRow, col.getColumnIndex("ProductID"));
            table.setValueAt(lblItem.getText(), iRow, col.getColumnIndex("Nama Barang"));
            table.setValueAt(lblSatKecil.getText(), iRow, col.getColumnIndex("Satuan"));
            table.setValueAt(fn.udfGetDouble(txtQty.getText()), iRow, col.getColumnIndex("Qty"));
            table.changeSelection(iRow, iRow, false, false);
        }else{
            String sUnit="";
            for(int i=0; i<table.getRowCount(); i++){
                sUnit=lblSatKecil.getText();
                if(table.getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) && table.getValueAt(i, table.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(sUnit)){
                    if(JOptionPane.showConfirmDialog(this, ("Item tersebut sudah dimasukkan pada baris ke "+(i+1)+".\nQty Item akan ditambahkan?"), "Item exists", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                        return;
                    else{
                        double total=fn.udfGetDouble(txtQty.getText())+fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")));
                        table.setValueAt(total, i, col.getColumnIndex("Qty"));
                        udfClearItem();
                        txtKode.requestFocusInWindow();
                        return;
                    }
                }
            }
            ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                txtKode.getText(),
                lblItem.getText(),
                lblSatKecil.getText(),
                fn.udfGetDouble(txtQty.getText())
            });
        table.setRowSelectionInterval(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1);
        table.changeSelection(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1, false, false);
        }

        udfClearItem();
        txtKode.requestFocus();
        txtKode.requestFocusInWindow();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAktif;
    private javax.swing.JCheckBox chkCetakItemFaktur;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAccDiscJual;
    private javax.swing.JLabel lblAccPenjualan;
    private javax.swing.JLabel lblAccReturJual;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblSatKecil;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtAccDiscJual;
    private javax.swing.JTextField txtAccPenjualan;
    private javax.swing.JTextField txtAccReturJual;
    private javax.swing.JTextField txtGroupID;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNamaGroup;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtSatuan;
    // End of variables declaration//GEN-END:variables

}
