/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmRevCostBudget2.java
 *
 * Created on 06 Mei 10, 6:04:23
 */

package akuntansi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import retail.MainForm;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmRevCostBudget2 extends javax.swing.JInternalFrame {
    private Connection conn;
    private String sID="";
    
    final JTextField ustTextField = new JTextField() {
        protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
            if (hasFocus()) {
                return super.processKeyBinding(ks, e, condition, pressed);
            } else {
                this.requestFocus();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        processKeyBinding(ks, e, condition, pressed);
                    }
              });
                return true;
            }
        }
    };

    /** Creates new form FrmRevCostBudget2 */
    public FrmRevCostBudget2() {
        initComponents();
         MyTableCellEditor cEditor=new MyTableCellEditor();
        ((DefaultTableModel)tblPendapatan.getModel()).setNumRows(0);
        tblPendapatan.getColumn("Pencapaian").setCellRenderer(new MyRowRenderer());
        tblPendapatan.getColumnModel().getColumn(tblPendapatan.getColumnModel().getColumnIndex("Target")).setCellEditor(cEditor);
        tblPendapatan.getColumn("Keterangan").setPreferredWidth(303);
        tblPendapatan.getColumn("Target").setPreferredWidth(100);
        tblPendapatan.getColumn("Realisasi").setPreferredWidth(100);
        tblPendapatan.getColumn("Pencapaian").setPreferredWidth(90);
        
        tblPendapatan.getModel().addTableModelListener(new TableModelListener(){
          public void tableChanged(TableModelEvent e){
              double target=0, real=0;
             if(e.getType() == TableModelEvent.UPDATE){
                int column = e.getColumn();

                if(column != TableModelEvent.ALL_COLUMNS){
                   int firstRow = e.getFirstRow();
                   if(firstRow != TableModelEvent.HEADER_ROW){
                      if(tblPendapatan.getColumnName(column).equals("Target")||tblPendapatan.getColumnName(column).equals("Realisasi")){
                         //for(int i = firstRow; i <= e.getLastRow(); i++){
                          real=GeneralFunction.udfGetDouble(tblPendapatan.getValueAt(e.getLastRow(), tblPendapatan.getColumnModel().getColumnIndex("Realisasi")));
                          target=GeneralFunction.udfGetDouble(tblPendapatan.getValueAt(e.getLastRow(), tblPendapatan.getColumnModel().getColumnIndex("Target")));
                          tblPendapatan.setValueAt(
                                  (real==0 && target==0? 0:  real/target) ,
                                  tblPendapatan.getSelectedRow(),
                                  tblPendapatan.getColumnModel().getColumnIndex("Pencapaian"));

                            udfSetTotal();
                      }
                   }else{
                      //columns got moved around or renamed or something else
                   }
                }else{
                   //Entire table got changed...

                    
                }
             }else{
                //rows or columns were added or deleted, usually not a user action.
             }
          }
        });
    }

    private void udfSetTotal(){
        double target, real;
        double totTarget=0, totReal=0, persen=0;
        double jmlBudget=0;

        for(int i = 0; i < tblPendapatan.getRowCount(); i++){
            target=tblPendapatan.getValueAt(i, 2)==null? 0: GeneralFunction.udfGetDouble(tblPendapatan.getValueAt(i, 2).toString());
            real=tblPendapatan.getValueAt(i, 3)==null? 0: GeneralFunction.udfGetDouble(tblPendapatan.getValueAt(i, 3).toString());
            totTarget+= target;
            totReal+= real;
            persen+= tblPendapatan.getValueAt(i, 4)==null? 0: GeneralFunction.udfGetDouble(tblPendapatan.getValueAt(i, 4).toString());

            if(target>0 || real>0) jmlBudget++;
        }
        lblTarget.setText(GeneralFunction.dFmt.format(totTarget));
        lblReal.setText(GeneralFunction.dFmt.format(totReal));
        System.out.println("Total persen "+persen);
        System.out.println("Total 100 % "+jmlBudget);
        lblPencapaian.setText(new DecimalFormat("#,##0.00 %").format(persen/jmlBudget) );
    }

    public void setID(String s){
        sID=s;
    }
    
    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(Component source){
        

        try{
            if(conn==null ||jComboBox1.getSelectedIndex()<0) return;
            ResultSet rs=null;
            rs=conn.createStatement().executeQuery("select id, coalesce(tipe,'') as tipe, tahun::int, " +
                    "coalesce(keterangan,'') as ket " +
                    "from acc_account_budget where tahun='"+jYearChooser1.getYear()+"' " +
                    "and tipe='"+(jComboBox1.getSelectedIndex()==0? "4": "5")+"' order by id desc limit 1;");

            sID="";
            if(rs.next()){
                sID=rs.getString(1);
                txtKeterangan.setText(rs.getString("ket"));
                if(source!=null && !source.equals(jYearChooser1)) jYearChooser1.setValue(rs.getInt("tahun"));
                if(source!=null && !source.equals(jComboBox1)) jComboBox1.setSelectedIndex(rs.getString("tipe").equalsIgnoreCase("4")? 0 : 1);
                jButton1.setText("Update");
            }else{
                txtKeterangan.setText("");
                jButton1.setText("Simpan");
            }
            rs.close();

            String sQry="select * from fn_acc_list_budget('"+jYearChooser1.getYear()+"', '"+(jComboBox1.getSelectedIndex()==0? "4": "5")+"', '') as " +
                    "(acc_no varchar, acc_name varchar, budget double precision, realisasi double precision)";

            rs=conn.createStatement().executeQuery(sQry);
            ((DefaultTableModel)tblPendapatan.getModel()).setNumRows(0);

            while(rs.next()){
                ((DefaultTableModel)tblPendapatan.getModel()).addRow(new Object[]{
                    rs.getString("acc_no"),
                    rs.getString("acc_name"),
                    rs.getDouble("budget"),
                    rs.getDouble("realisasi"),
                   (rs.getDouble("realisasi")==0 && rs.getDouble("budget")==0? 0:  rs.getDouble("realisasi")/rs.getDouble("budget"))
                });
            }// (rs.getDouble("realisasi")==0 && rs.getDouble("budget")==0? 0:  rs.getDouble("realisasi")/rs.getDouble("budget"))
            rs.close();
            udfSetTotal();
            
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

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPendapatan = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        lblPencapaian = new javax.swing.JLabel();
        lblTarget = new javax.swing.JLabel();
        lblReal = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(FrmRevCostBudget2.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Kategori :"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 60, 20));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 60, 20));

        txtKeterangan.setName("txtKeterangan"); // NOI18N
        txtKeterangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKeteranganActionPerformed(evt);
            }
        });
        jPanel2.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 55, 420, -1));

        jButton1.setText("Simpan"); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 50, 80, 30));

        jYearChooser1.setName("jYearChooser1"); // NOI18N
        jYearChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jYearChooser1PropertyChange(evt);
            }
        });
        jPanel2.add(jYearChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 70, -1));

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 55, 90, 20));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pendapatan", "Biaya" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 220, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblPendapatan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "No. Akun", "Keterangan", "Target", "Realisasi", "Pencapaian"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPendapatan.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblPendapatan.setName("tblPendapatan"); // NOI18N
        jScrollPane1.setViewportView(tblPendapatan);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Jumlah :"); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lblPencapaian.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblPencapaian.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPencapaian.setText(resourceMap.getString("lblPencapaian.text")); // NOI18N
        lblPencapaian.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPencapaian.setName("lblPencapaian"); // NOI18N

        lblTarget.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblTarget.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTarget.setText(resourceMap.getString("lblTarget.text")); // NOI18N
        lblTarget.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTarget.setName("lblTarget"); // NOI18N

        lblReal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblReal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblReal.setText(resourceMap.getString("lblReal.text")); // NOI18N
        lblReal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblReal.setName("lblReal"); // NOI18N

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(139, 139, 139)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(150, 150, 150)
                                .addComponent(lblTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(lblReal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPencapaian, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
                        .addGap(4, 4, 4))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2))
                    .addComponent(lblTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblReal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPencapaian, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtKeteranganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKeteranganActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txtKeteranganActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        udfInitForm(jComboBox1);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfSave();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jYearChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jYearChooser1PropertyChange
        //System.out.println(evt.getPropertyName());
        udfInitForm(jYearChooser1);
    }//GEN-LAST:event_jYearChooser1PropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm(this);
    }//GEN-LAST:event_formInternalFrameOpened

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        udfPrint();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmRevCostBudget2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private javax.swing.JLabel lblPencapaian;
    private javax.swing.JLabel lblReal;
    private javax.swing.JLabel lblTarget;
    private javax.swing.JTable tblPendapatan;
    private javax.swing.JTextField txtKeterangan;
    // End of variables declaration//GEN-END:variables

    private void udfSave() {
        try{
            conn.setAutoCommit(false);
            String  sDet="",
                    sHead="select fn_acc_update_budget_h('"+sID+"', '"+jYearChooser1.getYear()+"', " +
                    "'"+(jComboBox1.getSelectedIndex()==0? "4": "5")+"', '"+txtKeterangan.getText()+"', " +
                    "'"+MainForm.sUserName+"')";
            ResultSet rs=conn.createStatement().executeQuery(sHead);

            if(rs.next()){
                sID=rs.getString(1);
                sDet="Delete from acc_account_budget_detail where id='"+sID+"'; ";
                for(int i=0; i< tblPendapatan.getRowCount(); i++){
                    sDet+="insert into acc_account_budget_detail(id, acc_no, budget) values(" +
                            "'"+sID+"', '"+tblPendapatan.getValueAt(i, 0).toString()+"'," +
                            ""+GeneralFunction.udfGetDouble(tblPendapatan.getValueAt(i, 2).toString())+");";
                }

                rs.close();
                int i=conn.createStatement().executeUpdate(sDet);
            }

            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, jButton1.getText()+" data sukses!");
        }catch(SQLException se){
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmRevCostBudget2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void udfPrint() {
        try{
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            HashMap reportParam = new HashMap();
            JasperReport jasperReport = null;
            reportParam.put("tahun", String.valueOf(jYearChooser1.getYear()));
            reportParam.put("jenis", (jComboBox1.getSelectedIndex()==0? "4": "5"));
            reportParam.put("sJenis", jComboBox1.getSelectedItem().toString());
            reportParam.put("totPersen", lblPencapaian.getText());
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/AccBudget.jasper"));

            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            if(jasperReport==null) return;
            JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
            print.setOrientation(jasperReport.getOrientation());

            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                return;
            }
            JasperViewer.viewReport(print, false);

        } catch (JRException ex) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            System.out.println(ex.getMessage());
        }
    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
//        JTextField text=new JTextField("");
//        ustTextField
        int col, row;

        //private NumberFormat  nf=NumberFormat.getNumberInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
           //ustTextField.setEditable(isCellCanEdit);
           //ustTextField.setEnabled(btnGenerate.getText().equalsIgnoreCase("Generate"));
           col=vColIndex;
           row=rowIndex;
           ustTextField.setBackground(new Color(0,255,204));
           ustTextField.addFocusListener(txtFoculListener);
           //if(col==jTable1.getColumnModel().getColumnIndex("Minus")||col==jTable1.getColumnModel().getColumnIndex("Ditagihkan")){
                ustTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                      if (col!=0) {
                          char c = evt.getKeyChar();
                          if (!((c >= '0' && c <= '9')) &&
                                (c != KeyEvent.VK_BACK_SPACE) &&
                                (c != KeyEvent.VK_DELETE) &&
                                (c != KeyEvent.VK_ENTER)) {
                                getToolkit().beep();
                                evt.consume();
                                return;
                          }
                       }
                    }
                });
                ustTextField.addFocusListener(txtFoculListener);
        //}
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            //text.setText(value==null? "0": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
//                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
//                    Number dVal = nf.parse(value.toString());
//                    text.setText(nf.format(dVal));
                      ustTextField.setText(GeneralFunction.dFmt.format(GeneralFunction.udfGetDouble(value.toString())));

//                } catch (java.text.ParseException ex) {
//                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }else
                ustTextField.setText(value==null? "0":value.toString());
           return ustTextField;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
////                //retVal = Integer.parseInt(((JTextField)component).getText().replace(",",""));
////                retVal = GeneralFunction.udfGetDouble(((JTextField)text).getText());
////                o=numFormat.format(retVal);
////
////                //if((col==2||col==3) && (Double)retVal>0) tblTagihan.setValueAt(0, row, (col==2? 3:2));
////
//////                    //udfSetSubTotal(row);
//////                    myModel.setValueAt( GeneralFunction.udfGetDouble(((JTextField)component).getText()) *
//////                        GeneralFunction.udfGetDouble(myModel.getValueAt(row, tblAccount.getColumnModel().getColumnIndex("Harga")).toString()),
//////                        row, tblAccount.getColumnModel().getColumnIndex("Sub Total"));
////
                return GeneralFunction.udfGetDouble(ustTextField.getText());
                //return o;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g1);
            ((JTextField)c).setSelectionStart(0);
               ((JTextField)c).setSelectionEnd(((JTextField)c).getText().length());

           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            ((JTextField)c).setText(GeneralFunction.dFmt.format(GeneralFunction.udfGetDouble(((JTextField)c).getText())));

        }
   };

   public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat numFmt=NumberFormat.getIntegerInstance();

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(column==4){
                setHorizontalAlignment(jLabel7.RIGHT);
                value=new DecimalFormat("#,##0.00 %").format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }

    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

}
