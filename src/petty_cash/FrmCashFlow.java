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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmCashFlow extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
            
    
    /** Creates new form FrmPettyCashList */
    public FrmCashFlow() {
        initComponents();
        jDateAwal.setFormats("dd/MM/yyyy");
        jDateAkhir.setFormats("dd/MM/yyyy");
        jXTable1.setHighlighters(HighlighterFactory.createAlternateStriping());
        jXTable1.getTableHeader().setReorderingAllowed(false);
    }
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void udfLoadCashFlow(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select * from fn_rpt_arus_kas("
                    + "'"+fn.yyyymmdd_format.format(jDateAwal.getDate()) +"', "
                    + "'"+fn.yyyymmdd_format.format(jDateAkhir.getDate()) +"') as ("
                    + "tanggal date, transaksi varchar, dari_Ke varchar, ket varchar,  "
                    + "masuk double precision, keluar double precision)"); 
            ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getDate("tanggal"),
                    rs.getString("transaksi"),
                    rs.getString("dari_ke"),
                    rs.getString("ket"),
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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Arus Kas");
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Transaksi", "Terima Dari/ Dibayar Kepada", "Keterangan", "Masuk", "Keluar"
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilActionPerformed
        udfLoadCashFlow();
    }//GEN-LAST:event_btnTampilActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfLoadCashFlow();
    }//GEN-LAST:event_formInternalFrameOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTampil;
    private org.jdesktop.swingx.JXDatePicker jDateAkhir;
    private org.jdesktop.swingx.JXDatePicker jDateAwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    // End of variables declaration//GEN-END:variables
}