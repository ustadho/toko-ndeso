/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmReport.java
 *
 * Created on Mar 18, 2009, 8:39:10 PM
 */

package retail;

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
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import retail.main.ListRsbm;

/**
 *
 * @author ustadho
 */
public class FrmRptInventory extends javax.swing.JInternalFrame {
    private retail.main.ListRsbm lst;
    private Connection conn;
    ArrayList lstGudang=new ArrayList();
    ArrayList lstKategori=new ArrayList();
    //ArrayList lstGudang=new ArrayList();
    String sReport="";

    /** Creates new form FrmReport */
    public FrmRptInventory() {
        initComponents();
        jListMenuPersediaan.setSelectedIndex(0);
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
        try{
            lstGudang.clear(); cmbGudang.removeAllItems();
            ResultSet rs;
            rs=conn.createStatement().executeQuery("select kode_gudang, nama_gudang from r_gudang order by kode_gudang");
            lstGudang.add(""); cmbGudang.addItem("<Semua>");
            while(rs.next()){
                lstGudang.add(rs.getString(1));
                cmbGudang.addItem(rs.getString(2));
            }
            rs.close();

            lstKategori.clear(); cmbKategori.removeAllItems();
            rs=conn.createStatement().executeQuery("select kategori from r_item_kategori order by 1");
            lstKategori.add(""); cmbKategori.addItem("<Semua>");
            while(rs.next()){
                lstKategori.add(rs.getString(1));
                cmbKategori.addItem(rs.getString(1));
            }
            cmbKategori.setSelectedIndex(0);

            rs.close();

             Calendar cal = Calendar.getInstance();

            int currentMonth= cal.get(Calendar.MONTH);
            int currentYear = ( cal.get(Calendar.YEAR));
            //currentYear=currentMonth==1? currentYear-1: currentYear;

            SpinnerModel yearModel = new SpinnerNumberModel(currentYear, //initial value
                                           currentYear - 100, //min
                                           currentYear + 100, //max
                                           1);                //step
            spnTahun.setModel(yearModel);
            spnTahun.setEditor(new JSpinner.NumberEditor(spnTahun, "#"));
            cmbBulan.setSelectedIndex(currentMonth-1); //==1? 11: currentMonth-1);
        }catch(SQLException se){

        }
        panelPersediaan.setVisible(false);
        udfSetMenuReport();
    }

    private void udfSetMenuReport(){
        if(sReport.equalsIgnoreCase("persediaan")){
            panelPersediaan.setVisible(true);
            jListMenuPersediaan.setSelectedIndex(0);

        }
    }

    private boolean udfCekBeforePrint(){
        boolean b=true;
        if(jListMenuPersediaan.getSelectedIndex()==7 &&
                cmbGudang.getSelectedIndex()==0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih gudang terelbih dulu!");
            cmbGudang.requestFocusInWindow();
            return false;
        }
        return b;
    }
    private void udfPreviewPersediaan(){
        if(!udfCekBeforePrint()) return;

        HashMap reportParam = new HashMap();
        JasperReport jasperReport = null;
        try {
            reportParam.put("corporate", MainForm.sNamaUsaha);
            reportParam.put("alamat", MainForm.sAlamat);
            reportParam.put("telp", MainForm.sTelp);
            reportParam.put("tanggal1", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()));
            reportParam.put("tanggal2", new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate()));
            reportParam.put("kode_item", txtKode.getText());
            reportParam.put("kategori", lstKategori.get(cmbKategori.getSelectedIndex()).toString());
            reportParam.put("gudang", lstGudang.get(cmbGudang.getSelectedIndex()).toString());
            reportParam.put("bulan", spnTahun.getValue().toString()+"-"+new DecimalFormat("00").format(cmbBulan.getSelectedIndex()+1));
            reportParam.put("nama_bulan", cmbBulan.getSelectedItem().toString()+" "+spnTahun.getValue().toString());

            String sReport="";

            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            switch(jListMenuPersediaan.getSelectedIndex()){
                case 0:{sReport = "Inv_ItemListSummary"; break;} //Rngkasan daftar barang
                case 1:{JOptionPane.showMessageDialog(this, "Maaf, sementara masih belum tersedia"); break;} //Rincian daftar barang
                case 2:{sReport = "mutasi_item";    break;} //Mutasi barang
                case 3:{sReport = "Inv_RingkasanValuasi"; break;} //Ringkasan valuasi persediaan
                case 4:{JOptionPane.showMessageDialog(this, "Maaf, sementara masih belum tersedia"); break;} //Stok Opname
                case 5:{sReport ="Inv_KartuStok";   break;}
                case 6:{sReport ="Inv_OpSheetKategoriSat";   break;}
                case 7:{sReport ="Inv_OpSheetKategoriSatBlmOpname"; break;}
                case 8:{sReport="Sales_ByDatePerMonthSum"; break;}
            }

            if(sReport.length()==0) {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/"+sReport+".jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
            print.setOrientation(jasperReport.getOrientation());
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        panelPersediaan = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListMenuPersediaan = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        txtNamaBarang = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        cmbGudang = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cmbLokasi = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        panelTanggal = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        panelBulan = new javax.swing.JPanel();
        spnTahun = new javax.swing.JSpinner();
        cmbBulan = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setTitle("Laporan Persediaan");
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

        jPanel1.setLayout(new java.awt.CardLayout());
        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 164, -1, -1));

        jListMenuPersediaan.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "1.  Ringkasan Daftar Barang", "2.  Rincian Daftar Barang", "3.  Mutasi Persediaan", "4.  Ringkasan Valuasi Persediaan", "5.  Stock Opname", "6.  Kartu Stok", "7.  Form Entrian Stok Opname per Kategori", "8.  Form Entrian Item BELUM Stok Opname", "9. Rekap Qty Penjualan Barang per Hari" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListMenuPersediaan.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListMenuPersediaanValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListMenuPersediaan);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setBackground(new java.awt.Color(255, 255, 204));
        jLabel8.setText("Kategori :");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 70, 20));

        txtKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKeyReleased(evt);
            }
        });
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 70, -1));

        txtNamaBarang.setEditable(false);
        jPanel2.add(txtNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 210, -1));

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 280, -1));

        jLabel9.setBackground(new java.awt.Color(255, 255, 204));
        jLabel9.setText("Barang :");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 70, 20));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 170, -1));

        jLabel11.setBackground(new java.awt.Color(255, 255, 204));
        jLabel11.setText("Gudang :");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 70, 20));

        jLabel14.setBackground(new java.awt.Color(255, 255, 204));
        jLabel14.setText("Lokasi :");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 70, 20));

        cmbLokasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbLokasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, 170, -1));

        jPanel3.setLayout(new java.awt.CardLayout());

        panelTanggal.setBorder(javax.swing.BorderFactory.createTitledBorder("Tanggal"));

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Semua");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Terpilih");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jXDatePicker1.setEnabled(false);

        jXDatePicker2.setEnabled(false);

        jLabel12.setBackground(new java.awt.Color(255, 255, 204));
        jLabel12.setText("Dari :");

        jLabel13.setBackground(new java.awt.Color(255, 255, 204));
        jLabel13.setText("Sampai :");

        javax.swing.GroupLayout panelTanggalLayout = new javax.swing.GroupLayout(panelTanggal);
        panelTanggal.setLayout(panelTanggalLayout);
        panelTanggalLayout.setHorizontalGroup(
            panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTanggalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelTanggalLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jXDatePicker2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jXDatePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelTanggalLayout.setVerticalGroup(
            panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTanggalLayout.createSequentialGroup()
                .addGroup(panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jRadioButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTanggalLayout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(panelTanggalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.add(panelTanggal, "card2");

        panelBulan.setBorder(javax.swing.BorderFactory.createTitledBorder("Periode"));

        spnTahun.setModel(new javax.swing.SpinnerListModel(new String[] {"2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040", "2041", "2041", "2042", "2043", "2044", "2045", "2046", "2047", "2048", "2049", "2050"}));

        cmbBulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));

        jLabel1.setText("Bulan");

        javax.swing.GroupLayout panelBulanLayout = new javax.swing.GroupLayout(panelBulan);
        panelBulan.setLayout(panelBulanLayout);
        panelBulanLayout.setHorizontalGroup(
            panelBulanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBulanLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelBulanLayout.setVerticalGroup(
            panelBulanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBulanLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(panelBulanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnTahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(panelBulan, "card3");

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 350, 80));

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

        javax.swing.GroupLayout panelPersediaanLayout = new javax.swing.GroupLayout(panelPersediaan);
        panelPersediaan.setLayout(panelPersediaanLayout);
        panelPersediaanLayout.setHorizontalGroup(
            panelPersediaanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPersediaanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPersediaanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPersediaanLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPersediaanLayout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelPersediaanLayout.setVerticalGroup(
            panelPersediaanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPersediaanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPersediaanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPersediaanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelPersediaanLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton2});

        getContentPane().add(panelPersediaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 360));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtKodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKeyReleased
        try {
            switch (evt.getKeyCode()) {
                case java.awt.event.KeyEvent.VK_ENTER: {
                    if (lst.isVisible()) {
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtKode.setText(obj[0].toString());
                            //txtNamaBarang.setText(obj[1].toString());
                            lst.udfSelected();
                            //txtSubTotal.setText(txtHarga.getText());
                            lst.setVisible(false);
                        }
                    }
                    
                    break;
                }
                case java.awt.event.KeyEvent.VK_DELETE: {
                    lst.setFocusable(true);
                    lst.requestFocus();

                    break;
                }
                case java.awt.event.KeyEvent.VK_ESCAPE: {
                    lst.setVisible(false);
                    break;
                }
                case java.awt.event.KeyEvent.VK_DOWN: {
                    if (lst.isVisible()) {
                        lst.setFocusableWindowState(true);
                        lst.setVisible(true);
                        lst.requestFocus();
                    }
                    break;
                }
                default: {
                    if (!evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("Up") || !evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("F2")) {
                        String sQry="select distinct i.kode_item, coalesce(nama_item,'') as nama_barang " +
                                "from r_item i " +
                                "where i.active=true and (i.kode_item||coalesce(nama_item,'')) ilike  '%"+txtKode.getText()+"%'  order by coalesce(nama_item,'') ";

                        //System.out.println(sQry);
                        lst.setSQuery(sQry);

                        lst.setBounds(this.txtKode.getLocationOnScreen().x ,
                                this.txtKode.getLocationOnScreen().y + txtKode.getHeight(),
                                txtKode.getWidth() + txtNamaBarang.getWidth(),
                                (lst.getIRowCount()>10? 12*lst.getRowHeight(): (lst.getIRowCount()+3)*lst.getRowHeight()));


                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtKode);
                        //lst.setLblDes(new javax.swing.JLabel[]{lblAnggota, lblNip});
                        lst.setCompDes(new JComponent[]{txtNamaBarang});

                        lst.setColWidth(0, txtKode.getWidth());
                        lst.setColWidth(1, txtNamaBarang.getWidth()-10);

                        if (lst.getIRowCount() > 0) {
                            lst.setVisible(true);
                            requestFocusInWindow();
                            txtKode.requestFocus();
                        } else {
                            lst.setVisible(false);
                            //                            txtNoAnggota.setText("");
                            //                            lblAnggota.setText("");
                            //                            lblNip.setText("");
                            txtKode.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
}//GEN-LAST:event_txtKodeKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        jXDatePicker1.setEnabled(jRadioButton2.isSelected());
        jXDatePicker2.setEnabled(jRadioButton2.isSelected());
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        jXDatePicker1.setEnabled(jRadioButton2.isSelected());
        jXDatePicker2.setEnabled(jRadioButton2.isSelected());
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jListMenuPersediaanValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListMenuPersediaanValueChanged
        int iList=jListMenuPersediaan.getSelectedIndex();
        panelBulan.setVisible(iList==8);
        panelTanggal.setVisible(!panelBulan.isVisible());
        cmbKategori.setEnabled(iList==1||iList==6||iList==8);
        panelTanggal.setEnabled(iList==2);
        cmbGudang.setEnabled(!(iList==2));
        jLabel14.setVisible(iList==6); cmbLokasi.setVisible(iList==6);
    }//GEN-LAST:event_jListMenuPersediaanValueChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(sReport.equalsIgnoreCase("persediaan")) udfPreviewPersediaan();

    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbBulan;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbKategori;
    private javax.swing.JComboBox cmbLokasi;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jListMenuPersediaan;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JPanel panelBulan;
    private javax.swing.JPanel panelPersediaan;
    private javax.swing.JPanel panelTanggal;
    private javax.swing.JSpinner spnTahun;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNamaBarang;
    // End of variables declaration//GEN-END:variables

}
