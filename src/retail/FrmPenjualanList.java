/*
 * ListAkun.java
 *
 * Created on August 23, 2008, 10:21 AM
 */

package retail;

import java.awt.BorderLayout;
import java.awt.Container;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 *
 * @author  oestadho
 */
public class FrmPenjualanList extends javax.swing.JInternalFrame {
    //private DropShadowBorder dsb = new DropShadowBorder(UIManager.getColor("Control"), 0, 8, .5f, 12, false, true, true, true);
    DefaultTableModel myModel;
    private DropShadowBorder dsb=new DropShadowBorder();
    //private DropShadowBorder dsb = new DropShadowBorder(new Color(255,240,240), 0, 8, .5f, 12, false, true, true, true);
    private NumberFormat dFmt=NumberFormat.getInstance();
    private Connection conn;
    private JDesktopPane jDesktop;
    private String sUserName;
    private List lstDivisi=new  ArrayList();
    private boolean isJatuhTempo, isMacet;

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setIsMacet(boolean b){
        isMacet=b;
    }

    void setIsJatuhTempo(boolean b){
        isJatuhTempo=b;

        jPanel1.setVisible(!b);
        hiperLinkNew.setVisible(!b);
        hiperLinkDel.setVisible(!b);
        hiperLinkUpd.setVisible(!b);
        
    }
    /** Creates new form ListAkun */
    public FrmPenjualanList() {
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
        
        masterTable.getColumnExt("Tgl. Jt Tempo").setVisible(false);
        masterTable.getColumnExt("Divisi").setVisible(false);
        masterTable.getColumn("NIP").setPreferredWidth(40);
        masterTable.getColumn("Batal").setPreferredWidth(10);
        
    }

    public void setDesktopPane(JDesktopPane jDesktopPane1) {
        jDesktop =jDesktopPane1;
    }

    void setUserName(String sUserName) {
        this.sUserName=sUserName;
    }

    private void udfDeletePinjaman(boolean isDel) {
        int iRow = masterTable.getSelectedRow();
    
    if(iRow>=0){

        if(udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Terbayar")).toString())>0||udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Sisa")).toString())==0){
            JOptionPane.showMessageDialog(this, "Pinjaman tidak bisa dihapus karena "+
                    (udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Sisa")).toString())==0?"sudah Lunas atau sudah":"") +
                    " dilakukan angsuran");
            return;
        }

        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk "+(isDel?"menghapus":"membatalkan")+ "data tersebut?", "Confirm", JOptionPane.YES_NO_OPTION)==
                JOptionPane.YES_OPTION){
                try {
                    ResultSet rs=conn.createStatement().executeQuery("select * from t_angsuran_detail d inner join t_angsuran a on a.kode_trx=d.kode_trx " +
                            "where no_pinjaman='"+masterTable.getValueAt(iRow, 0).toString()+"' and a.batal=false ");
                    if(rs.next()){
                        JOptionPane.showMessageDialog(this, (isDel?"Penghapusan ":"Pembatalan")+" data pinjaman tidak bisa dilakukan karena sudah dilakukan angsuran atas no pinjaman tersebut!!!");
                        return;
                    }
                    
                    //int iDel = conn.createStatement().executeUpdate("Delete from t_pinjam where no_pinjaman='" + masterTable.getValueAt(iRow, 0).toString() + "'");
                    int iDel = conn.createStatement().executeUpdate("Update t_pinjam set batal=true, "+
                            (isDel==true? "is_del=true,user_del='"+MainForm.sUserName+"', tgl_Del=now(), ": "")+ 
                            "user_batal='"+MainForm.sUserName+"', tgl_batal=now() " +
                            "where no_pinjaman='" + masterTable.getValueAt(iRow, 0).toString() + "'");
                    
                    if(iDel>0){
                        JOptionPane.showMessageDialog(this, (isDel?"Penghapusan ":"Pembatalan")+"Pinjaman sukses!!!");
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
                    JOptionPane.showMessageDialog(this, "Hapus data gagal!\n"+ex.getMessage());
                }
                
        }
    }
    }

    private void udfInitForm(){
        myModel=(DefaultTableModel)masterTable.getModel();
        masterTable.setModel(myModel);
        myModel.setNumRows(0);
        masterTable.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
        
        
        try {
            ResultSet rs=conn.createStatement().executeQuery("select kode_divisi, coalesce(nama_divisi,'') as divisi from m_divisi order by 2");
            
            cmbDivisi.removeAllItems();
            cmbDivisi.addItem("<Semua>");
            lstDivisi.add("");
            while(rs.next()){
                cmbDivisi.addItem(rs.getString("divisi"));
                lstDivisi.add(rs.getString("kode_divisi"));
            }
            
            cmbDivisi.setSelectedIndex(0);
            rs.close();
            
        } catch (SQLException ex) {
            
        }
        txtNama.setText("");
        
        //masterTable.getColumnModel().getColumn(WIDTH)
        
        udfFilter();

        jDate1.setFormats("dd/MM/yyyy");
        jDate2.setFormats("dd/MM/yyyy");
    }

    private void udfPreviewAngsuran(){
        int iRow=masterTable.getSelectedRow();

        if(iRow>=0){
            HashMap reportParam = new HashMap();
            JasperReport jasperReport = null;

            try {
                reportParam.put("nama_koperasi", MainForm.sNamaUsaha);
                reportParam.put("alamat", MainForm.sAlamat);
                reportParam.put("telp", MainForm.sTelp);
                reportParam.put("no_pinjaman", masterTable.getValueAt(iRow, 0).toString());
                reportParam.put("tgl_pinjam", masterTable.getValueAt(iRow, 1).toString());
                reportParam.put("nama_anggota", masterTable.getValueAt(iRow, 4).toString());
                reportParam.put("alamat_anggota", masterTable.getValueAt(iRow, 5).toString());

                reportParam.put("pokok", udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Jml. Pinjam")).toString()));
                reportParam.put("plus_bunga", udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Pokok+Jasa")).toString()));
                reportParam.put("divisi", masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Divisi")).toString());
                reportParam.put("resort", masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Resort")).toString());

                jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/AngsuranDetailPerPinjaman.jasper"));
                JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
                print.setOrientation(jasperReport.getOrientation());
                if (print.getPages().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                    return;
                }
                JasperViewer.viewReport(print, false);

            } catch (JRException ex) {
                    Logger.getLogger(FrmRptInventory.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }

    private void udfPrint() {
        int iRow=masterTable.getSelectedRow();

        if(iRow>=0){
            HashMap reportParam = new HashMap();
            JasperReport jasperReport = null;

            try {
                reportParam.put("nama_koperasi", MainForm.sNamaUsaha);
                reportParam.put("alamat", MainForm.sAlamat);
                reportParam.put("telp", MainForm.sTelp);

                reportParam.put("tgl1", new SimpleDateFormat("yyyy-MM-dd").format(jDate1.getDate()));
                reportParam.put("tgl2", new SimpleDateFormat("yyyy-MM-dd").format(jDate2.getDate()));
//                reportParam.put("resort", lstResort.get(cmbResort.getSelectedIndex()).toString());
                reportParam.put("nama", "");
                reportParam.put("alamat_anggota", "");
                reportParam.put("divisi", lstDivisi.get(cmbDivisi.getSelectedIndex()).toString());

                jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/PinjamanListSum.jasper"));
                JasperPrint print = JasperFillManager.fillReport(jasperReport, reportParam, conn);
                print.setOrientation(jasperReport.getOrientation());
                if (print.getPages().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                    return;
                }
                JasperViewer.viewReport(print, false);

            } catch (JRException ex) {
                    Logger.getLogger(FrmRptInventory.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }

    private double udfGetDouble(String sNum){
        double hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=dFmt.parse(sNum).doubleValue();
            } catch (ParseException ex) {
                hsl=0;
                Logger.getLogger(FrmPenjualanList.class.getName()).log(Level.SEVERE, null, ex);
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }
    
    private void udfFilter(){
        lblTotJual.setText("0");
        lblTotTerbayar.setText("0");
        lblTotSisa.setText("0");
        double totJual=0, totBayar=0, totSisa=0;
        
        if(conn!=null  && myModel!=null && cmbDivisi.getSelectedIndex()>=0 && lstDivisi.size()>0){
            String s="";
            myModel.setNumRows(0);
            if(!isJatuhTempo)
                s="select * from fn_r_list_penjualan('"+ new SimpleDateFormat("yyyy-MM-dd").format(jDate1.getDate()) +"', '"+new SimpleDateFormat("yyyy-MM-dd").format(jDate2.getDate())+"', " +
                        "'"+txtNama.getText()+"', '"+lstDivisi.get(cmbDivisi.getSelectedIndex()).toString()+"') as (sales_no varchar, sales_date date, jt_tempo date, kode_cust varchar, nip varchar, " +
                        "nama varchar, divisi varchar, jumlah double precision, bayar double precision, is_koreksi boolean)";
            else
                s="select * from fn_r_list_penjualan('"+ new SimpleDateFormat("yyyy-MM-dd").format(jDate1.getDate()) +"', '"+new SimpleDateFormat("yyyy-MM-dd").format(jDate2.getDate())+"', " +
                        "'"+txtNama.getText()+"', '"+lstDivisi.get(cmbDivisi.getSelectedIndex()).toString()+"') " +
                        "as (sales_no varchar, sales_date date, jt_tempo date, kode_cust varchar, nip varchar, " +
                        "nama varchar, divisi varchar, divisi varchar, jumlah double precision, bayar double precision, is_koreksi boolean)";

            System.out.println(s);

            try {
    //            ResultSet rs = conn.createStatement().executeQuery("select * from agama where " +
    //                    "kdagama Ilike '%"+txtKode.getText()+"%' and desagama iLike '%"+txtNama.getText()+"%'");

                ResultSet rs = conn.createStatement().executeQuery(s);
                while(rs.next()){
                    myModel.addRow(new Object[]{
                        rs.getString("sales_no"),
                        rs.getDate("sales_date"),
                        rs.getDate("jt_tempo"),
                        rs.getString("kode_cust"),
                        rs.getString("nip"),
                        rs.getString("nama"),
                        rs.getString("divisi"),
                        rs.getDouble("jumlah"),
                        rs.getDouble("bayar"),
                        rs.getDouble("jumlah")-rs.getDouble("bayar"),
                        rs.getBoolean("is_koreksi")
                    });
                    
                    totJual+=rs.getDouble("jumlah");
                    totBayar+=rs.getDouble("bayar");
                    totSisa+=rs.getDouble("jumlah")-rs.getDouble("bayar");
                }
                
                lblTotJual.setText(dFmt.format(totJual));
                lblTotTerbayar.setText(dFmt.format(totBayar));
                lblTotSisa.setText(dFmt.format(totSisa));


                if(myModel.getRowCount()>0) masterTable.setRowSelectionInterval(0, 0);
                rs.close();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error from udfInitForm", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void udfNewPinjam(){
        
    }
    

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
        jPanel2 = new javax.swing.JPanel();
        txtNama = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        chkFilter = new javax.swing.JCheckBox();
        btnPrint = new javax.swing.JButton();
        panelFilter = new org.jdesktop.swingx.JXTitledPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbDivisi = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jDate1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDate2 = new org.jdesktop.swingx.JXDatePicker();
        jPanel3 = new javax.swing.JPanel();
        lblTotSisa = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblTotJual = new javax.swing.JLabel();
        lblTotTerbayar = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Penjualan"); // NOI18N
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
        hiperLinkNew.setFont(new java.awt.Font("Tahoma", 1, 11));
        hiperLinkNew.setName("hiperLinkNew"); // NOI18N
        hiperLinkNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkNewActionPerformed(evt);
            }
        });

        hiperLinkUpd.setMnemonic('U');
        hiperLinkUpd.setText("Ubah"); // NOI18N
        hiperLinkUpd.setFont(new java.awt.Font("Tahoma", 1, 11));
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

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNama.setName("txtNama"); // NOI18N
        txtNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNamaKeyReleased(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 89, -1));

        jLabel4.setText("Nama"); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 20));

        jButton1.setText("Refresh"); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 90, -1));

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
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {hiperLinkDel, hiperLinkNew, hiperLinkUpd});

        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelHeaderLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(hiperLinkDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(hiperLinkUpd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(hiperLinkNew, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelHeaderLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addComponent(btnPrint))
                        .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelHeaderLayout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(chkFilter)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPrint, hiperLinkDel, hiperLinkNew, hiperLinkUpd});

        panelFilter.setTitle("Filter"); // NOI18N
        panelFilter.setName("panelFilter"); // NOI18N

        jLabel2.setText("Divisi"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cmbDivisi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Semua>", "Diproses", "Disetujui", "Ditolak" }));
        cmbDivisi.setName("cmbDivisi"); // NOI18N
        cmbDivisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDivisiActionPerformed(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jDate1.setName("jDate1"); // NOI18N
        jDate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDate1ActionPerformed(evt);
            }
        });
        jPanel1.add(jDate1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 41, 124, -1));

        jLabel5.setText("Dari"); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 21, 54, 18));

        jLabel6.setText("Sampai"); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(9, 74, 54, 18));

        jDate2.setName("jDate2"); // NOI18N
        jDate2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDate2ActionPerformed(evt);
            }
        });
        jPanel1.add(jDate2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 94, 124, -1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Total"));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotSisa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotSisa.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotSisa.setText("0"); // NOI18N
        lblTotSisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotSisa.setName("lblTotSisa"); // NOI18N
        jPanel3.add(lblTotSisa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 110, 18));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Penjualan"); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 110, 18));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Terbayar"); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 110, 18));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Sisa"); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 110, 18));

        lblTotJual.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotJual.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotJual.setText("0"); // NOI18N
        lblTotJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotJual.setName("lblTotJual"); // NOI18N
        jPanel3.add(lblTotJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 110, 18));

        lblTotTerbayar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTotTerbayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotTerbayar.setText("0"); // NOI18N
        lblTotTerbayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotTerbayar.setName("lblTotTerbayar"); // NOI18N
        jPanel3.add(lblTotTerbayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 110, 18));

        jButton2.setText("Preview Angsuran"); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFilterLayout = new javax.swing.GroupLayout(panelFilter.getContentContainer());
        panelFilter.getContentContainer().setLayout(panelFilterLayout);
        panelFilterLayout.setHorizontalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbDivisi, javax.swing.GroupLayout.Alignment.LEADING, 0, 131, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFilterLayout.setVerticalGroup(
            panelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDivisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Tgl. Penjualan", "Tgl. Jt Tempo", "No. Anggota", "NIP", "Nama", "Divisi", "Jumlah", "Terbayar", "Sisa", "Batal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        masterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        masterTable.setColumnControlVisible(true);
        masterTable.setName("masterTable"); // NOI18N
        masterTable.setShowGrid(true);
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                    .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
   
}//GEN-LAST:event_formInternalFrameClosed

private void hiperLinkNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkNewActionPerformed
    udfNewPinjam();
}//GEN-LAST:event_hiperLinkNewActionPerformed

private void chkFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFilterActionPerformed
    panelFilter.setVisible(chkFilter.isSelected());
}//GEN-LAST:event_chkFilterActionPerformed

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void hiperLinkDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkDelActionPerformed
    udfDeletePinjaman(true);
}//GEN-LAST:event_hiperLinkDelActionPerformed

private void hiperLinkUpdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkUpdActionPerformed
    udfUpdate();
            
}//GEN-LAST:event_hiperLinkUpdActionPerformed

private void txtNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyReleased
    udfFilter();
}//GEN-LAST:event_txtNamaKeyReleased

private void cmbDivisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDivisiActionPerformed
    if(conn!=null  && myModel!=null && cmbDivisi.getSelectedIndex()>=0 && lstDivisi.size()>0) udfFilter();
}//GEN-LAST:event_cmbDivisiActionPerformed

private void jDate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDate1ActionPerformed
    if(conn!=null  && myModel!=null && cmbDivisi.getSelectedIndex()>=0 && lstDivisi.size()>0 ) udfFilter();
}//GEN-LAST:event_jDate1ActionPerformed

private void jDate2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDate2ActionPerformed
    if(conn!=null  && myModel!=null && cmbDivisi.getSelectedIndex()>=0 && lstDivisi.size()>0 ) udfFilter();
}//GEN-LAST:event_jDate2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    txtNama.setText(""); 
    udfFilter();
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    udfPreviewAngsuran();
}//GEN-LAST:event_jButton2ActionPerformed

private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
    udfPrint();
}//GEN-LAST:event_btnPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JComboBox cmbDivisi;
    private org.jdesktop.swingx.JXHyperlink hiperLinkDel;
    private org.jdesktop.swingx.JXHyperlink hiperLinkNew;
    private org.jdesktop.swingx.JXHyperlink hiperLinkUpd;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private org.jdesktop.swingx.JXDatePicker jDate1;
    private org.jdesktop.swingx.JXDatePicker jDate2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotJual;
    private javax.swing.JLabel lblTotSisa;
    private javax.swing.JLabel lblTotTerbayar;
    private org.jdesktop.swingx.JXTable masterTable;
    private org.jdesktop.swingx.JXTitledPanel panelFilter;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JSlider shadowSizeSlider;

    private int getColumnIndex(JTable tbl, String columnName){
        int iCol=0;
        
        for (iCol=0; iCol<tbl.getColumnCount(); iCol++){
            if(tbl.getColumnName(iCol).equalsIgnoreCase(columnName)){
                return iCol;
            }
        }
        
        return iCol;
    }

    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){

            if(udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Sisa")).toString())==0||udfGetDouble(masterTable.getValueAt(iRow, getColumnIndex(masterTable, "Terbayar")).toString())>0){
                JOptionPane.showMessageDialog(this, "Pinjaman tidak bisa diubah karena sudah Lunas atau sudah dilakukan angsuran");
                return;
            }

            
        }
    }
}
