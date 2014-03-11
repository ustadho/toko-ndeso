/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemGroupList.java
 *
 * Created on Feb 18, 2011, 7:46:09 PM
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.postgresql.jdbc2.optional.SimpleDataSource;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmTransfer extends javax.swing.JInternalFrame {
    private Connection conn;
    private ArrayList lstSiteFrom=new ArrayList();
    private ArrayList lstSiteTo=new ArrayList();
    private GeneralFunction fn=new GeneralFunction();
    private DlgLookupItemBeli lookupItem =new DlgLookupItemBeli(JOptionPane.getFrameForComponent(this), true);
    private MyKeyListener kListener=new MyKeyListener();
    private boolean isKoreksi=false;

    private Component aThis;
    private boolean stItemUpd=false;
    /** Creates new form FrmItemGroupList */
    public FrmTransfer() {
        initComponents();
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.removeComboUpDown(cmbSatuan);
        fn.removeComboUpDown(cmbSiteFrom);
        fn.removeComboUpDown(cmbSiteTo);
        table.addKeyListener(kListener);

        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());
            }
        });
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

        txtTransferNo.setEnabled(isKoreksi);
        jXDatePicker1.setEnabled(false);
        aThis=this;
    }

    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }

    public void setIsKoreksi(boolean b){
        this.isKoreksi=b;
    }

    private void udfInitForm(){
        table.getTableHeader().setReorderingAllowed(false);
        //table.getTableHeader().setResizingAllowed(false);
        table.getColumn("ProductID").setPreferredWidth(120);     //table.getColumn("ProductID").setMinWidth(txtKode.getWidth());   table.getColumn("ProductID").setMaxWidth(txtKode.getWidth());
        table.getColumn("Nama Barang").setPreferredWidth(360);   //table.getColumn("Nama Barang").setMinWidth(txtKode.getWidth()); table.getColumn("Nama Barang").setMaxWidth(txtKode.getWidth());
        table.getColumn("Satuan").setPreferredWidth(110);        //table.getColumn("Satuan").setMinWidth(txtKode.getWidth());      table.getColumn("Satuan").setMaxWidth(txtKode.getWidth());
        table.getColumn("Qty").setPreferredWidth(60);           //table.getColumn("Qty").setMinWidth(txtKode.getWidth());         table.getColumn("Qty").setMaxWidth(txtKode.getWidth());
        table.getColumn("Konv").setPreferredWidth(40);          //table.getColumn("Konv").setMinWidth(txtKode.getWidth());        table.getColumn("Konv").setMaxWidth(txtKode.getWidth());
        table.setRowHeight(20);
        try{
            cmbSiteFrom.removeAllItems();   lstSiteFrom.clear();
            cmbSiteTo.removeAllItems();     lstSiteTo.clear();
            ResultSet rs=conn.createStatement().executeQuery("select kode_gudang, coalesce(nama_gudang,'') as nama_gudang, current_date as skg " +
                    "from r_gudang order by 1");
            while(rs.next()){
                lstSiteFrom.add(rs.getString("kode_gudang"));
                cmbSiteFrom.addItem(rs.getString("nama_gudang"));
                lstSiteTo.add(rs.getString("kode_gudang"));
                cmbSiteTo.addItem(rs.getString("nama_gudang"));
                jXDatePicker1.setDate(rs.getDate("skg"));
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());

        }
        
        lookupItem.setConn(conn);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtKeterangan.requestFocus();
            }
        });
        
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;

        if(cmbSiteFrom.getSelectedIndex()<0||cmbSiteTo.getSelectedIndex()<0){
            JOptionPane.showMessageDialog(this, "Silakan pilih gudang asal atau gudang tujuan terlebih dulu!");
            cmbSiteFrom.requestFocus();
            return false;
        }
        if(cmbSiteFrom.getSelectedItem().toString().equalsIgnoreCase(cmbSiteTo.getSelectedItem().toString())){
            JOptionPane.showMessageDialog(this, "Gudang asal dan tujuan tidak boleh sama!");
            cmbSiteFrom.requestFocus();
            return false;
        }
        if(table.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang akan ditransfer masih kosong!");
            if(txtKode.isFocusOwner())
                txtKode.requestFocus();
            return false;
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        try{
            String sNewCode="";
            conn.setAutoCommit(false);
            ResultSet rs=conn.createStatement().executeQuery("select fn_r_get_transfer_no('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"')");
            if(rs.next())
                sNewCode=rs.getString(1);
            rs.close();
            txtTransferNo.setText(sNewCode);

            String sUpd="insert into r_transfer(no_transfer, tanggal, description, from_gudang, to_gudang, user_ins, time_ins) " +
                    "values('"+sNewCode+"', now(), '"+txtKeterangan.getText()+"', '"+lstSiteFrom.get(cmbSiteFrom.getSelectedIndex()).toString()+"', " +
                    "'"+lstSiteTo.get(cmbSiteTo.getSelectedIndex()).toString()+"', " +
                    "'"+MainForm.sUserName+"', now());";

            TableColumnModel col=table.getColumnModel();
            for(int i=0; i<table.getRowCount(); i++){
                sUpd+="insert into r_transfer_detail(no_transfer, kode_item, qty, unit, konv) values(" +
                        "'"+sNewCode+"','"+table.getValueAt(i, col.getColumnIndex("ProductID")).toString()+"', " +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")))+ ", " +
                        "'"+table.getValueAt(i, col.getColumnIndex("Satuan")).toString()+"', " +
                        fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Konv")))+")";
            }
            int i=conn.createStatement().executeUpdate(sUpd);
            conn.commit();
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan data sukses!");
            udfNew();
            txtKeterangan.requestFocus();
            
        }catch(SQLException se){
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                System.err.println(se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew(){
        txtTransferNo.setText("");
        txtKeterangan.setText("");
        ((DefaultTableModel)table.getModel()).setNumRows(0);
        udfClearItem();

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
        jLabel2 = new javax.swing.JLabel();
        txtTransferNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel4 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbSiteFrom = new javax.swing.JComboBox();
        cmbSiteTo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtQty = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        cmbSatuan = new javax.swing.JComboBox();
        txtKode = new javax.swing.JTextField();
        lblKonv = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Transfer antar Gudang"); // NOI18N
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

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText("Keterangan");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 90, 20));

        txtTransferNo.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtTransferNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTransferNo.setName("txtTransferNo"); // NOI18N
        jPanel1.add(txtTransferNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 130, 22));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText("Transfer Dari:");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jXDatePicker1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 120, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel4.setText("Tanggal");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 100, 20));

        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, 410, 22));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel5.setText("Transfer No.");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 20));

        cmbSiteFrom.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbSiteFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSiteFrom.setName("cmbSiteFrom"); // NOI18N
        jPanel1.add(cmbSiteFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 260, -1));

        cmbSiteTo.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbSiteTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSiteTo.setName("cmbSiteTo"); // NOI18N
        jPanel1.add(cmbSiteTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 80, 260, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setText("Transfer Ke :");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 260, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 710, 110));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Item Transfer  ");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 300, 30));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Satuan", "Qty", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
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
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setName("table"); // NOI18N
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 710, 250));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtQty.setName("txtQty"); // NOI18N
        jPanel2.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 0, 60, 20));

        lblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblItem.setName("lblItem"); // NOI18N
        jPanel2.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 0, 360, 20));

        cmbSatuan.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cmbSatuan.setName("cmbSatuan"); // NOI18N
        jPanel2.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 0, 110, 20));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setName("txtKode"); // NOI18N
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 20));

        lblKonv.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblKonv.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv.setName("lblKonv"); // NOI18N
        jPanel2.add(lblKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 0, 40, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 710, 22));

        btnSimpan.setText("Simpan");
        btnSimpan.setName("btnSimpan"); // NOI18N
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        getContentPane().add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 433, 80, 30));

        btnCancel.setText("Batal");
        btnCancel.setName("btnCancel"); // NOI18N
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 433, 80, 30));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-748)/2, (screenSize.height-501)/2, 748, 501);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSimpanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JComboBox cmbSiteFrom;
    private javax.swing.JComboBox cmbSiteTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblKonv;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtTransferNo;
    // End of variables declaration//GEN-END:variables

    private void udfClearItem(){
        txtKode.setText("");
        lblItem.setText("");
        cmbSatuan.removeAllItems();
        lblKonv.setText("1");
        txtQty.setText("");
     }

   public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(txtKode) && txtKode.getText().trim().length()==0)
                udfClearItem();
//            else if(evt.getSource().equals(txtNoPO)){
//                fn.lookup(evt, new Object[]{null},
//                "select * from fn_r_lookup_no_po_supplier('"+txtSupplier.getText()+"','%"+txtNoPO.getText()+"%') " +
//                "as (\"No PO\" varchar)",
//                txtNoPO.getWidth(), 150);
//            }
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
                case KeyEvent.VK_INSERT:{
                    if(cmbSiteFrom.getSelectedIndex()<0){
                        JOptionPane.showMessageDialog(aThis, "Silakan pilih gudang asal terlebih dulu!", "Information", JOptionPane.INFORMATION_MESSAGE);
                        if(!cmbSiteFrom.isFocusOwner())
                            cmbSiteFrom.requestFocusInWindow();
                    }
                    if(table.getCellEditor()!=null && evt.getSource().equals(table))
                        table.getCellEditor().stopCellEditing();

                        lookupItem.setAlwaysOnTop(true);
                        lookupItem.setKeyEvent(evt);
                        lookupItem.setObjForm(this);
                        lookupItem.setVisible(true);
                        lookupItem.clearText();
                        lookupItem.requestFocusInWindow();
                        if(lookupItem.getKodeBarang().length()>0){
                            txtKode.setText(lookupItem.getKodeBarang());
                            udfLoadItem();
                            cmbSatuan.requestFocus();
                        }
                    break;
                }
                case KeyEvent.VK_F4:{
                    udfNew();
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
                    txtKode.setText(table.getValueAt(iRow, col.getColumnIndex("Product ID")).toString());
                    udfLoadComboKonversi();
                    lblItem.setText(table.getValueAt(iRow, col.getColumnIndex("Keterangan")).toString());
                    cmbSatuan.setSelectedItem(table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString());
                    txtQty.setText(fn.intFmt.format(fn.udfGetInt(table.getValueAt(iRow, col.getColumnIndex("Qty")))));
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
                        if(txtQty.isFocusOwner()){
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
                    }
                    else{
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

    private void udfAddItemToTable(){
        if(lblItem.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan item terlebih dulu!");
            txtKode.requestFocusInWindow();
            return;
        }
        if(fn.udfGetDouble(txtQty.getText())==0){
            JOptionPane.showMessageDialog(this, "Masukkan Qty lebih dari 0!");
            if(!txtQty.isFocusOwner())
                txtQty.requestFocusInWindow();
            return;
        }
        TableColumnModel col=table.getColumnModel();
        if(stItemUpd){
            int iRow=table.getSelectedRow();
            if(iRow<0) return;
            table.setValueAt(txtKode.getText(), iRow, col.getColumnIndex("Product ID"));
            table.setValueAt(lblItem.getText(), iRow, col.getColumnIndex("Keterangan"));
            table.setValueAt(fn.udfGetDouble(txtQty.getText()), iRow, col.getColumnIndex("Qty"));
            table.setValueAt(cmbSatuan.getSelectedItem().toString(), iRow, col.getColumnIndex("Satuan"));
            table.setValueAt(fn.udfGetInt(lblKonv.getText()), iRow, col.getColumnIndex("Konv"));
            
        }else{
            String sUnit="";
            for(int i=0; i<table.getRowCount(); i++){
                sUnit=cmbSatuan.getSelectedIndex()<0? "": cmbSatuan.getSelectedItem().toString();
                if(table.getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) &&
                        table.getValueAt(i, table.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(sUnit) ){
                    if(JOptionPane.showConfirmDialog(this, ("Item tersebut sudah dimasukkan pada baris ke "+(i+1)+".\nQty Item akan diupdate?"), "Item exists", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                        return;

                    table.setValueAt(fn.udfGetDouble(txtQty.getText()), i, table.getColumnModel().getColumnIndex("Qty"));
                }
            }
            ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                txtKode.getText(),
                lblItem.getText(),
                cmbSatuan.getSelectedItem().toString(),
                fn.udfGetDouble(txtQty.getText()),
                fn.udfGetInt(lblKonv.getText()),

            });
        }
        table.setRowSelectionInterval(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1);
        table.changeSelection(((DefaultTableModel)table.getModel()).getRowCount()-1, 0, false, false);
        udfClearItem();
        txtKode.requestFocus();
        txtKode.requestFocusInWindow();
    }

    private String udfLoadItem(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3 " +
                         "from r_item i " +
                         "where i.kode_item ='"+txtKode.getText()+"' or coalesce(i.barcode,'')='"+txtKode.getText()+"' " +
                         "";

                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     lblItem.setText(rs.getString("nama_item"));
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                     cmbSatuan.setSelectedItem(rs.getString("unit"));
                     lblKonv.setText("1");

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

    private void udfLoadKonversi(String sUnit) {
        //int row=table.getSelectedRow();
        if(cmbSatuan.getSelectedIndex()<0) return;
        try {
            String sQry = "select case  when '" + sUnit + "'=unit then 1 " +
                          "             when '" + sUnit + "'=unit2 then coalesce(konv2,1) " +
                          "             when '" + sUnit + "'=unit3 then coalesce(konv3,1) " +
                          "             else 1 end as konv  " +
                          "from r_item i " +
                          "where i.kode_item='" + txtKode.getText() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                lblKonv.setText(rs.getString("konv"));

            } else {
                lblKonv.setText("1");
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private String udfLoadComboKonversi(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3  " +
                         "from r_item i " +
                         "where i.kode_item||coalesce(i.barcode,'')='"+txtKode.getText()+"'";

                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                 }
                 rs.close();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return sMsg;
    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if(e.getSource() instanceof JTextField){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }else if(e.getSource() instanceof JFormattedTextField){
                    ((JFormattedTextField)e.getSource()).setSelectionStart(0);
                    ((JFormattedTextField)e.getSource()).setSelectionEnd(((JFormattedTextField)e.getSource()).getText().length());
                }

            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

                if(e.getSource().equals(txtKode))
                    udfLoadItem();

           }
        }


    } ;
}
