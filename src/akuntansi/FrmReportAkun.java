/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmReportAkun.java
 *
 * Created on Mar 18, 2009, 8:39:10 PM
 */

package akuntansi;

import java.awt.Cursor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import retail.MainForm;
import retail.main.GeneralFunction;
import retail.main.ListRsbm;

/**
 *
 * @author ustadho
 */
public class FrmReportAkun extends javax.swing.JInternalFrame {
    private ListRsbm lst;
    private Connection conn;
    ArrayList lstGudang=new ArrayList();
    ArrayList lstKategori=new ArrayList();
    //ArrayList lstGudang=new ArrayList();
    String sReport="";
    String sUnit="";

    /** Creates new form FrmReportAkun */
    public FrmReportAkun() {
        initComponents();

    }

    public void setConn(Connection con){
        this.conn=con;
    }

    public void udfSetFlagReport(String s){
        sReport=s;
    }

    private void udfInitForm(){
        lst=new ListRsbm();
        lst.setConn(this.conn);
        jXDatePicker1.setFormats("dd/MM/yyyy");
        jXDatePicker2.setFormats("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();

        int currentMonth= cal.get(Calendar.MONTH);
        int currentYear = ( cal.get(Calendar.YEAR));
        //currentYear=currentMonth==1? currentYear-1: currentYear;

        SpinnerModel yearModel = new SpinnerNumberModel(currentYear, //initial value
                                       currentYear - 100, //min
                                       currentYear + 100, //max
                                       1);                //step
        spinnerTahun.setModel(yearModel);
        spinnerTahun.setEditor(new JSpinner.NumberEditor(spinnerTahun, "#"));
        cmbBulan.setSelectedIndex(currentMonth-1); //==1? 11: currentMonth-1);

        jListMenu.setSelectedIndex(0);
        
    }


    private JTable getSkontroTable(){
        JTable jTable1=new JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nomor", "AccNo_A", "AccName_A", "lYear_A", "nYear_A", "AccNo_P", "AccName_P", "lYear_P", "nYear_P"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });

        return jTable1;
    }

    private void udfPreviewNeraca(String sTahun, String sBulan,String sUnit){
        JTable tabel=getSkontroTable();
        String s="select * from fn_acc_rpt_neraca('"+sBulan+"', '"+sTahun+"', '"+sUnit+"') as (groups text, tipe text, " +
                "acc_no varchar, acc_name varchar, lyear double precision, nyear double precision)";
        ((DefaultTableModel)tabel.getModel()).setNumRows(0);
        try{
            ResultSet rs=conn.createStatement().executeQuery(s);
            int i=0, jmlBaris=0, jmlAktiva=0;
            while(rs.next()){
                jmlBaris++;
                if(rs.getString("groups").equalsIgnoreCase("Aktiva")){
                    ((DefaultTableModel)tabel.getModel()).addRow(new Object[]{
                        jmlBaris, rs.getString("acc_no"), rs.getString("acc_name"),
                        rs.getDouble("lYear"),rs.getDouble("nYear")
                    });
                }else{
                    if(jmlAktiva==0) {
                        jmlAktiva=tabel.getRowCount();
                        i=0;
                        jmlBaris=0;
                    }
                    if(jmlAktiva<=i)
                        ((DefaultTableModel)tabel.getModel()).addRow(new Object[]{
                            jmlBaris, null, null, null, null, rs.getString("acc_no"), rs.getString("acc_name"),
                            rs.getDouble("lYear"), rs.getDouble("nYear")
                        });
                    else{
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getString("acc_no"), i, 5);
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getString("acc_name"), i, 6);
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getDouble("lYear"), i, 7);
                        ((DefaultTableModel)tabel.getModel()).setValueAt(rs.getDouble("nYear"), i, 8);
                    }
                    i++;
                }
            }
            HashMap reportParam=new HashMap();
            JasperReport jasperReport = null;
            reportParam.put("nama_koperasi", MainForm.sNamaUsaha);
            reportParam.put("alamat", MainForm.sAlamat);
            reportParam.put("telp", MainForm.sTelp);
            reportParam.put("tahun", spinnerTahun.getValue().toString());
            reportParam.put("unit", cmbUnit.getSelectedItem().toString());

            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/Skontro/NeracaWithJTable.jasper"));
            JasperPrint print = JasperFillManager.fillReport(
                    jasperReport,
                    reportParam, new JRTableModelDataSource((DefaultTableModel)tabel.getModel()));

            print.setOrientation(jasperReport.getOrientation());
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                return;
            }
            JasperViewer.viewReport(print, false);

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }

    private void udfPreviewReport(){
        HashMap reportParam = new HashMap();
        JasperReport jasperReport = null;
        try {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            reportParam.put("nama_koperasi", MainForm.sNamaUsaha);
            reportParam.put("alamat", MainForm.sAlamat);
            reportParam.put("telp", MainForm.sTelp);
            sUnit=(cmbUnit.getSelectedIndex()==0? "":(cmbUnit.getSelectedIndex()==1? "SP" : "RT"));
            switch(jListMenu.getSelectedIndex()){
                case 0:{
                    reportParam.put("tanggal1", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()));
                    reportParam.put("tanggal2", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate()));
                    reportParam.put("unit", sUnit);
                    reportParam.put("sUnit", cmbUnit.getSelectedItem().toString());
                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/AccJournalDet.jasper"));

                    break;
                }
                case 1:{
                    reportParam.put("tanggal1", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()));
                    reportParam.put("tanggal2", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate()));
                    reportParam.put("AccNo", txtAccNo.getText());
                    reportParam.put("sUnit", cmbUnit.getSelectedItem().toString());
                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/GLDet.jasper"));

                    break;
                }
                case 2:{
                    reportParam.put("tanggal1", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()));
                    reportParam.put("tanggal2", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate()));
                    reportParam.put("unit", sUnit);
                    reportParam.put("sUnit", cmbUnit.getSelectedItem().toString());
                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/NeracaSaldo.jasper"));

                    break;
                }
                
                case 3:{
                    reportParam.put("tanggal1", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()));
                    reportParam.put("tanggal2", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate()));
                    reportParam.put("unit", sUnit);
                    reportParam.put("sUnit", cmbUnit.getSelectedItem().toString());
                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/NeracaLajur_v1.jasper"));
                    break;
                }
                case 4:{
                    reportParam.put("bulan", new DecimalFormat("00").format(cmbBulan.getSelectedIndex()+1));
                    reportParam.put("tahun", spinnerTahun.getValue().toString());
                    reportParam.put("unit", sUnit);
                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/LabaRugi.jasper"));
                    break;
                }
                case 5:{
//                    reportParam.put("bulan", new DecimalFormat("00").format(cmbBulan.getSelectedIndex()+1));
//                    reportParam.put("tahun", spinnerTahun.getValue().toString());
//                    reportParam.put("unit", sUnit);
//                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/Neraca2.jasper"));
                    udfPreviewNeraca(
                            spinnerTahun.getValue().toString(),
                            new DecimalFormat("00").format(cmbBulan.getSelectedIndex()+1),
                            sUnit);

                    break;
                }
                case 6:{
                    reportParam.put("tanggal1", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()));
                    reportParam.put("tanggal2", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate()));
                    reportParam.put("AccNo", txtAccNo.getText());
                    reportParam.put("sHeader", "Jurnal Kas Harian");
                    reportParam.put("sUnit", cmbUnit.getSelectedItem().toString());
                    jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/JurnalKasHarian.jasper"));

                    break;
                }
                
            }

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
            System.out.println(ex.getMessage());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panelParameter = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtAccNo = new javax.swing.JTextField();
        txtAccName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel13 = new javax.swing.JLabel();
        panelBulan = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbBulan = new javax.swing.JComboBox();
        spinnerTahun = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListMenu = new javax.swing.JList();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        cmbUnit = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Laporan Buku Besar");
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

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelParameter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelParameter.setLayout(new java.awt.CardLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtAccNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccNoKeyReleased(evt);
            }
        });
        jPanel2.add(txtAccNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 70, -1));

        txtAccName.setEditable(false);
        jPanel2.add(txtAccName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 220, -1));

        jLabel9.setBackground(new java.awt.Color(255, 255, 204));
        jLabel9.setText("Akun :");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 70, 20));

        jLabel12.setBackground(new java.awt.Color(255, 255, 204));
        jLabel12.setText("Dari :");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 20));
        jPanel2.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 120, -1));
        jPanel2.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 120, -1));

        jLabel13.setBackground(new java.awt.Color(255, 255, 204));
        jLabel13.setText("Sampai :");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 70, 20));

        panelParameter.add(jPanel2, "card2");

        jLabel2.setText("Bulan");

        cmbBulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));

        jLabel3.setText("Tahun");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(cmbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spinnerTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(1, 1, 1)
                        .addComponent(spinnerTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(1, 1, 1)
                        .addComponent(cmbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelBulanLayout = new javax.swing.GroupLayout(panelBulan);
        panelBulan.setLayout(panelBulanLayout);
        panelBulanLayout.setHorizontalGroup(
            panelBulanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBulanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );
        panelBulanLayout.setVerticalGroup(
            panelBulanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBulanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        panelParameter.add(panelBulan, "card3");

        jPanel1.add(panelParameter, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 430, 180));

        jListMenu.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Bukti Jurnal Umum", "Buku Besar (Detail)", "Neraca Saldo", "Neraca Lajur", "Laporan Rugi Laba", "Neraca", "Jurnal Kas Harian" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListMenu.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListMenuValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListMenu);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 210, 180));

        jButton2.setText("Preview");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cmbUnit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Simpan Pinjam", "Retail" }));

        jLabel1.setText("Unit");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(233, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(cmbUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jListMenuValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListMenuValueChanged
        int iList=jListMenu.getSelectedIndex();
        jPanel2.setVisible(iList==0);
        txtAccNo.setVisible(iList==1||iList==6);
        jLabel9.setVisible(txtAccNo.isVisible());
        txtAccName.setVisible(jLabel9.isVisible());
        panelBulan.setVisible(iList==4||iList==5);
}//GEN-LAST:event_jListMenuValueChanged

    private GeneralFunction fn=new GeneralFunction();

    private void txtAccNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccNoKeyReleased
        String sQry = "select a.acc_no, a.acc_name " +
                    "from acc_coa a " +
                    "where (a.acc_no||coalesce(a.acc_name,'')) iLike '%" + txtAccNo.getText() + "%' " +
                    "and a.acc_no not in(select distinct sub_acc_of from acc_coa where acc_type='01' and sub_acc_of is not null) " +
                    "order by acc_no ";
        fn.lookup(evt, new Object[]{txtAccName}, sQry, txtAccNo.getWidth()+txtAccName.getWidth()+18, 130);

}//GEN-LAST:event_txtAccNoKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        udfPreviewReport();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
}//GEN-LAST:event_jButton1ActionPerformed

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbBulan;
    private javax.swing.JComboBox cmbUnit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jListMenu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JPanel panelBulan;
    private javax.swing.JPanel panelParameter;
    private javax.swing.JSpinner spinnerTahun;
    private javax.swing.JTextField txtAccName;
    private javax.swing.JTextField txtAccNo;
    // End of variables declaration//GEN-END:variables

}
