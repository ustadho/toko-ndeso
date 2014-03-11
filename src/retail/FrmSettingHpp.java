/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSettingHpp.java
 *
 * Created on Mar 2, 2011, 5:41:56 AM
 */

package retail;

import java.awt.Component;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmSettingHpp extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();

    /** Creates new form FrmSettingHpp */
    public FrmSettingHpp() {
        initComponents();
        aThis=this;
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(conn==null || tblItem.getSelectedRow()<0) return;
                txtHpp.setText("");
                udfLoadItemHistory();
            }
        });

        for(int i=0; i<tblItemHistory.getColumnCount(); i++){
            tblItemHistory.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
        }
        tblItem.setHighlighters(HighlighterFactory.createSimpleStriping());
        tblItemHistory.setHighlighters(HighlighterFactory.createSimpleStriping());
        tblItemHistory.setAutoCreateRowSorter(false);
    }

    private void udfLoadItemHistory(){
        try{
            ((DefaultTableModel)tblItemHistory.getModel()).setNumRows(0);

            ResultSet rs=conn.createStatement().executeQuery("select tanggal, coalesce(hpp,0) as hpp, coalesce(harga_sat,0) as harga_sat, " +
                    "coalesce(k.saldo,0) as saldo, coalesce(k.keterangan,'') as ket, tipe, serial_no " +
                    "from r_kartu_stok k where kode_item='"+tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString()+"' " +
                    "order by serial_no");
            while(rs.next()){
                 ((DefaultTableModel)tblItemHistory.getModel()).addRow(new Object[]{
                    rs.getDate("tanggal"),
                    rs.getDouble("hpp"),
                    rs.getDouble("harga_sat"),
                    rs.getDouble("saldo"),
                    rs.getString("ket"),
                    rs.getString("tipe"),
                    rs.getInt("serial_no")
                 });
            }
            if(tblItemHistory.getRowCount()>0)
                tblItemHistory.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItemHistory, (DefaultTableModel)tblItemHistory.getModel()).getModel());
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfLoadItem(){
        String sQry="";
        if(cmbOpsiTampilan.getSelectedIndex()==0)
            sQry=   "select i.kode_item, coalesce(i.nama_item,'') as nama_item, coalesce(i.kategori,'') as kategori " +
                    "from r_item i " +
                    "order by coalesce(i.nama_item,'')";
        else
            sQry=   "select distinct k.kode_item, coalesce(i.nama_item,'') as nama_item, coalesce(i.kategori,'') as kategori " +
                    "from r_kartu_stok k " +
                    "inner join r_item i on i.kode_item=k.kode_item " +
                    "where k.hpp>k.harga_sat and k.tipe='JL' " +
                    "order by coalesce(i.nama_item,'')";
        try{
            ((DefaultTableModel)tblItemHistory.getModel()).setNumRows(0);
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);

            ResultSet rs=conn.createStatement().executeQuery(sQry);
            while(rs.next()){
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("kategori")
                });
            }
            tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
            if(tblItem.getRowCount()>0)
                tblItem.setRowSelectionInterval(0, 0);

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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtHpp = new javax.swing.JTextField();
        btnProses = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItemHistory = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        btnRefresh = new javax.swing.JButton();
        cmbOpsiTampilan = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItem = new org.jdesktop.swingx.JXTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Setting Harga Pokok");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Harga Pokok Awal");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 110, 20));

        txtHpp.setName("txtHpp"); // NOI18N
        txtHpp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtHppKeyPressed(evt);
            }
        });
        jPanel1.add(txtHpp, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 5, 140, 20));

        btnProses.setText("Proses");
        btnProses.setName("btnProses"); // NOI18N
        btnProses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProsesActionPerformed(evt);
            }
        });
        jPanel1.add(btnProses, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 5, 120, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblItemHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Hpp", "Harga Trans", "Saldo", "Keterangan", "Tipe", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItemHistory.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItemHistory.setName("tblItemHistory"); // NOI18N
        jScrollPane1.setViewportView(tblItemHistory);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnRefresh.setText("Refresh");
        btnRefresh.setName("btnRefresh"); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 5, -1, -1));

        cmbOpsiTampilan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua Item", "Item dg Hpp lebih kecil dr Harga Transaksi" }));
        cmbOpsiTampilan.setName("cmbOpsiTampilan"); // NOI18N
        jPanel2.add(cmbOpsiTampilan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 240, -1));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Kategori"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setName("tblItem"); // NOI18N
        jScrollPane2.setViewportView(tblItem);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Histori Transaksi");
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Daftar Item");
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        udfLoadItem();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnProsesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesActionPerformed
        udfProsesHpp();
    }//GEN-LAST:event_btnProsesActionPerformed

    private void txtHppKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHppKeyPressed
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHppKeyPressed

    private void udfProsesHpp(){
        if(tblItem.getSelectedRow()<0){
            JOptionPane.showMessageDialog(this, "Silakan pilih item yang akan di-recalculate!");
            tblItem.requestFocus();
            return;
        }
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select fn_r_recalculate_hpp_item('"+tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString()+"', "+fn.udfGetDouble(txtHpp.getText())+") ");
            if(rs.next()){
                JOptionPane.showMessageDialog(this, "Proses re-Calculate hpp sukses!");
                udfLoadItemHistory();
            }
            rs.close();
            
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProses;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox cmbOpsiTampilan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTable tblItem;
    private org.jdesktop.swingx.JXTable tblItemHistory;
    private javax.swing.JTextField txtHpp;
    // End of variables declaration//GEN-END:variables

    private SimpleDateFormat dmy=new SimpleDateFormat("dd/MM/yyyy");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());

            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmy.format(value);
            }if(value instanceof Timestamp ){
                value=dmy.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }
}