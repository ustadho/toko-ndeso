/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemMaster.java
 *
 * Created on Mar 7, 2009,udf 2:24:15 PM
 */

package retail;

import retail.main.GeneralFunction;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import retail.main.ListRsbm;
import retail.main.MyKeyListener;

/**
 *
 * @author ustadho
 */
public class FrmItemMaster extends javax.swing.JInternalFrame {
    private Connection conn;
    private boolean isNew=false;
    private DefaultTableModel srcModel;
    private JTable srcTable;
    private String sUnit2="", sUnit3="";
    private float fKonv2=0, fKonv3=0;
    private ListRsbm lst;
    private String sKodeBarang="";
    private ArrayList lstGudang=new ArrayList();
    GeneralFunction fn=new GeneralFunction();
    private Object objForm;
    private NumberFormat dFmt=NumberFormat.getInstance();

    /** Creates new form FrmItemMaster */
    public FrmItemMaster() {
        initComponents();
        txtSubItem.setVisible(false);
        lblSubItem.setVisible(false);

        lst = new ListRsbm();
        lst.setVisible(false);
        kListener.setListRsbm(lst);

        fn.addKeyListenerInContainer(panelGeneral, kListener, txtFoculListener);
//        for(int i=0;i<panelGeneral.getComponentCount();i++){
//            Component c = panelGeneral.getComponent(i);
//            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
//            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
//            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
//                c.addKeyListener(kListener);
//                c.addFocusListener(txtFoculListener);
//            }
//        }
        for(int i=0;i<panelSaldoAwal.getComponentCount();i++){
            Component c = panelSaldoAwal.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        for(int i=0;i<panelAkun.getComponentCount();i++){
            Component c = panelAkun.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
    }

    void setConn(Connection conn) {
        this.conn=conn;
    }

    public void setObjForm(Object obj){
        this.objForm=obj;
    }

    void setKodeBarang(String s){
        this.sKodeBarang=s;
    }

    void setIsNew(boolean b) {
        this.isNew=b;
    }

    void setSrcModel(DefaultTableModel myModel) {
        this.srcModel=myModel;
    }

    void setSrcTable(JTable masterTable) {
        this.srcTable=masterTable;
    }

    void setUnitTambahan(String sUnit2, float fKonv2, String sUnit3, float fKonv3 ){
        this.sUnit2=sUnit2; this.fKonv2=fKonv2;
        this.sUnit3=sUnit3; this.fKonv3=fKonv3;
    }

    private double udfGetDouble(String sNum){
        double hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=dFmt.parse(sNum).doubleValue();
            } catch (ParseException ex) {
                hsl=0;
                //Logger.getLogger(FrmTrxPinjam.class.getName()).log(Level.SEVERE, null, ex);
            }catch(NumberFormatException ne){
                hsl=0;
            }catch(IllegalArgumentException i){
                hsl=0;
            }
        }
        return hsl;
  }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        //kListener.setListRsbm(fn.getListRsbm());
        try{
            ResultSet rs=conn.createStatement().executeQuery("select kode_gudang, coalesce(nama_gudang,'') from r_gudang order by kode_gudang");
            lstGudang.add("");
            cmbGudang.removeAllItems();
            cmbGudang.addItem("");
            while(rs.next()){
                lstGudang.add(rs.getString(1));
                cmbGudang.addItem(rs.getString(2));
            }
            rs.close();

            rs=conn.createStatement().executeQuery("select kategori from r_item_kategori order by 1");
            cmbKategori.removeAllItems();
            //cmbKategori.addItem("");
            while(rs.next()){
                cmbKategori.addItem(rs.getString(1));
            }
            rs.close();

            if(sKodeBarang.length()>0)  udfLoadItem();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        txtSaldoQty.setEnabled(isNew);
        txtUnitPrice.setEnabled(isNew);
        txtExpDate.setEnabled(isNew);
        cmbGudang.setEnabled(isNew);
        jDateSaldoAwal.setEnabled(isNew);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtKodeItem.requestFocus();
            }
      });
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(txtKodeItem.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi kode barang terlebih dulu!");
            txtKodeItem.requestFocus();
            b=false;
            return b;
        }
        if(txtNamaItem.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi kode barang terlebih dulu!");
            txtNamaItem.requestFocus();
            b=false;
            return b;
        }
        if(isNew){
            try{
                ResultSet rs=conn.createStatement().executeQuery("select * from r_item where kode_item='"+txtKodeItem.getText()+"'");
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "Barang dengan kode tersebut sudah dimasukkan!");
                    txtKodeItem.requestFocus();
                    b=false;

                }
                rs.close();
                return b;
            }catch(SQLException se){}
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;

        try{
            conn.setAutoCommit(false);
            ResultSet rs=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from r_item where kode_item='"+sKodeBarang+"'");

            if(!rs.next()) {
                isNew=true;
                rs.moveToInsertRow();
            }
            rs.updateString("kode_item", txtKodeItem.getText());
            rs.updateString("nama_item", txtNamaItem.getText());
            rs.updateString("tipe", cmbTipe.getSelectedItem().toString().substring(0, 1));
            rs.updateString("sub_item_of", txtSubItem.getText());
            rs.updateBoolean("active", chkAktif.isSelected());
            if (isNew) {
                rs.updateDouble("stock", udfGetDouble(txtSaldoQty.getText()));
                String sTime=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(jDateSaldoAwal.getDate());
                rs.updateTimestamp("per_tgl", java.sql.Timestamp.valueOf(sTime));
                rs.updateDouble("harga_unit", udfGetDouble(txtUnitPrice.getText()));
                rs.updateDouble("hpp", udfGetDouble(txtUnitPrice.getText()));
            }
            rs.updateDouble("harga_jual", udfGetDouble(txtHargaJual.getText()));
            rs.updateDouble("diskon", udfGetDouble(txtDiskon.getText()));
            rs.updateString("kode_pajak_jual", txtKodePajakJual.getText());
            rs.updateString("kode_pajak_beli", txtKodePajakBeli.getText());
            rs.updateString("supp_utama", txtSupplier.getText());

            rs.updateString("akun_persediaan", txtAccPersediaan.getText());
            rs.updateString("akun_sales", txtAccJual.getText());
            rs.updateString("akun_ret_jual", txtAccRetJual.getText());
            rs.updateString("akun_disk_jual", txtAccDiskJual.getText());
            rs.updateString("akun_hpp", txtAccHPP.getText());
            rs.updateString("akun_ret_beli", txtAccRetBeli.getText());
            rs.updateString("akun_belum_tertagih", txtAccBlmTertagih.getText());

            rs.updateDouble("min_reorder", udfGetDouble(txtMinReorder.getText()));
            rs.updateString("unit", txtUnit.getText());
            rs.updateString("unit2", sUnit2);
            rs.updateDouble("konv2", fKonv2);
            rs.updateString("unit3", sUnit3);
            rs.updateDouble("konv3", fKonv3);
            rs.updateString("kategori", cmbKategori.getSelectedIndex()==0? "": cmbKategori.getSelectedItem().toString());
            rs.updateString("barcode", txtBarcode.getText());

            if(isNew){
                rs.insertRow();
                if(srcTable!=null){
                    ((DefaultTableModel)srcTable.getModel()).addRow(new Object[]{
                        txtKodeItem.getText(),
                        txtNamaItem.getText(),
                        udfGetDouble(txtSaldoQty.getText()),
                        udfGetDouble(txtHargaJual.getText()),
                        cmbTipe.getSelectedItem().toString()
                    });
                    srcTable.setRowSelectionInterval(srcTable.getRowCount()-1, srcTable.getRowCount()-1);
                    srcTable.changeSelection(srcTable.getRowCount()-1, 0, false, false);
                }

            }
            else{
                rs.updateRow();
                if(srcTable!=null){
                    int iRow=srcTable.getSelectedRow();
                    srcTable.setValueAt(txtKodeItem.getText(), iRow, 0);
                    srcTable.setValueAt(txtNamaItem.getText(), iRow, 1);
                    srcTable.setValueAt(udfGetDouble(txtSaldoQty.getText()), iRow, 2);
                    srcTable.setValueAt(udfGetDouble(txtHargaJual.getText()), iRow, 3);
                    srcTable.setValueAt(cmbTipe.getSelectedItem().toString(), iRow, 4);
                }

            }

            conn.setAutoCommit(true);
            if(objForm instanceof FrmSettingHargaJual)
                ((FrmSettingHargaJual)objForm).udfFilter(txtKodeItem.getText());

            JOptionPane.showMessageDialog(this, "Simpan data barang sukses");
            this.dispose();

        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);

                JOptionPane.showMessageDialog(this, "Gagal Simpan\n" + se.getMessage());
            } catch (SQLException ex) {
                //Logger.getLogger(FrmItemMaster.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex.getMessage());
            }
        }
    }
    
    private void udfLoadItem(){
        try{
            String sQry="select case i.tipe when 'I' then 0 when 'N' then 1 else 2 end as tipe, coalesce(i.active, true) as active, coalesce(i.kategori,'') as kategori,  " +
                    "i.kode_item, trim(coalesce(i.sub_item_of,''))<>'' as is_sub_item, coalesce(i.sub_item_of,'') as kode_sub, coalesce(sub.nama_item,'') as nama_sub," +
                    "i.nama_item, coalesce(i.stock,0) as cur_qty, coalesce(i.hpp,0) as cur_harga , coalesce(i.stock,0) * coalesce(i.harga_unit,0) as hpp," +
                    "coalesce(i.unit,'') as unit, coalesce(i.unit2, '') as unit2, coalesce(i.konv2,0) as konv2, coalesce(i.unit3,'') as unit3, coalesce(i.konv3,0) as konv3," +
                    "coalesce(i.harga_jual,0) as harga_jual, coalesce(i.diskon,0) as diskon, coalesce(i.kode_pajak_jual,'') as kode_pajak_jual, " +
                    "i.per_tgl, coalesce(i.barcode,'') as barcode, " +
                    "coalesce(i.supp_utama,'') as kode_supp_utama, coalesce(sup.nama_supp,'') as nama_supp_utama," +
                    "coalesce(i.min_reorder,0) as min_reorder, coalesce(i.kode_pajak_beli,'') as kode_pajak_beli, " +
                    "coalesce(i.unit_jual,'') as unit_jual, coalesce(i.konv_jual,1) as konv_jual, coalesce(i.catatan,'') as catatan," +
                    "coalesce(i.akun_persediaan,'') as kode_akun_persediaan, coalesce(coa1.acc_name,'') as akun_persediaan," +
                    "coalesce(i.akun_sales,'') as kode_akun_sales,coalesce(coa2.acc_name,'') as akun_sales," +
                    "coalesce(i.akun_ret_jual,'') as kode_akun_ret_jual,coalesce(coa3.acc_name,'') as akun_ret_jual," +
                    "coalesce(i.akun_disk_jual,'') as kode_akun_disk_jual, coalesce(coa4.acc_name,'') as akun_disk_jual," +
                    "coalesce(i.akun_hpp,'') as kode_akun_hpp, coalesce(coa5.acc_name,'') as akun_hpp," +
                    "coalesce(i.akun_ret_beli,'') as kode_akun_ret_beli ,coalesce(coa6.acc_name,'') as akun_ret_beli, " +
                    "coalesce(i.akun_belum_tertagih,'') as kode_akun_blm_tertagih, coalesce(coa7.acc_name,'') as akun_blm_tertagih " +
                    "from r_item i " +
                    "left join r_item sub on sub.kode_item=i.sub_item_of " +
                    "left join r_supplier sup on sup.kode_supp=i.supp_utama " +
                    "left join acc_coa coa1 on coa1.acc_no=i.akun_persediaan " +
                    "left join acc_coa coa2 on coa2.acc_no=i.akun_sales " +
                    "left join acc_coa coa3 on coa3.acc_no=i.akun_ret_jual " +
                    "left join acc_coa coa4 on coa4.acc_no=i.akun_disk_jual " +
                    "left join acc_coa coa5 on coa5.acc_no=i.akun_hpp " +
                    "left join acc_coa coa6 on coa6.acc_no=i.akun_ret_beli " +
                    "left join acc_coa coa7 on coa7.acc_no=i.akun_belum_tertagih " +
                    "where i.kode_item='"+sKodeBarang+"'";

            System.out.println(sQry);
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                isNew=false;
                cmbTipe.setSelectedIndex(rs.getInt("tipe"));
                cmbKategori.setSelectedItem(rs.getString("kategori"));
                chkAktif.setSelected(rs.getBoolean("active"));
                txtKodeItem.setText(rs.getString("kode_item"));
                chkSubItem.setSelected(rs.getBoolean("is_sub_Item"));
                txtSubItem.setText(rs.getString("kode_sub"));
                lblSubItem.setText(rs.getString("nama_sub"));
                txtNamaItem.setText(rs.getString("nama_item"));
                lblCurrentQty.setText(dFmt.format(rs.getDouble("cur_qty")));
                lblCurrentUnitPrice.setText(dFmt.format(rs.getDouble("cur_harga")));
                lblCurrentHPP.setText(dFmt.format(rs.getDouble("hpp")));
                txtUnit.setText(rs.getString("unit"));
                sUnit2=rs.getString("unit2");
                fKonv2=rs.getFloat("konv2");
                sUnit3=rs.getString("unit3");
                fKonv3=rs.getFloat("konv3");
                txtHargaJual.setText(dFmt.format(rs.getDouble("harga_jual")));
                txtDiskon.setText(dFmt.format(rs.getDouble("diskon")));
                txtKodePajakJual.setText(rs.getString("kode_pajak_jual"));
                txtSupplier.setText(rs.getString("kode_supp_utama"));
                lblSupplier.setText(rs.getString("nama_supp_utama"));
                txtMinReorder.setText(dFmt.format(rs.getDouble("min_reorder")));
                txtKodePajakBeli.setText(rs.getString("kode_pajak_beli"));
                txtUnitJual.setText(rs.getString("unit_jual"));
                txtKonvJual.setText(rs.getString("konv_jual"));
                txtHargaSatJual.setText(dFmt.format(rs.getDouble("harga_jual")*rs.getDouble("konv_jual")));
                txtCatatan.setText(rs.getString("catatan"));
                jDateSaldoAwal.setDate(rs.getDate("per_tgl"));

                txtAccPersediaan.setText(rs.getString("kode_akun_persediaan")); lblAccPersediaan.setText(rs.getString("akun_persediaan"));
                txtAccJual.setText(rs.getString("kode_akun_sales")); lblAccJual.setText(rs.getString("akun_sales"));
                txtAccRetJual.setText(rs.getString("kode_akun_ret_jual")); lblAccRetJual.setText(rs.getString("akun_ret_jual"));
                txtAccDiskJual.setText(rs.getString("kode_akun_disk_jual")); lblAccDiskJual.setText(rs.getString("akun_disk_jual"));
                txtAccHPP.setText(rs.getString("kode_akun_hpp")); lblAccHPP.setText(rs.getString("akun_hpp"));
                txtAccRetBeli.setText(rs.getString("kode_akun_ret_beli")); lblAccRetBeli.setText(rs.getString("akun_persediaan"));
                txtAccBlmTertagih.setText(rs.getString("kode_akun_blm_tertagih")); lblAccBelumTertagih.setText(rs.getString("akun_blm_tertagih"));

                txtSubItem.setVisible(chkSubItem.isSelected());
                lblSubItem.setVisible(chkSubItem.isSelected());

                txtBarcode.setText(rs.getString("barcode"));

            }else{
                JOptionPane.showMessageDialog(this, "Item tidak ditemukan!");
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtSubItem = new javax.swing.JTextField();
        chkAktif = new javax.swing.JCheckBox();
        chkSubItem = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        txtKodeItem = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbTipe = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblCurrentQty = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblCurrentHPP = new javax.swing.JLabel();
        lblCurrentUnitPrice = new javax.swing.JLabel();
        txtUnit = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelSaldoAwal = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtSaldoQty = new javax.swing.JTextField();
        lblSaldoHPP = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        cmbGudang = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jDateSaldoAwal = new org.jdesktop.swingx.JXDatePicker();
        btnLookupSaldo = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtExpDate = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtHargaJual = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtDiskon = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtKodePajakJual = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtMinReorder = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtKodePajakBeli = new javax.swing.JTextField();
        lblSubItem = new javax.swing.JLabel();
        txtNamaItem = new javax.swing.JTextField();
        panelAkun = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        txtAccPersediaan = new javax.swing.JTextField();
        lblAccPersediaan = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        txtAccJual = new javax.swing.JTextField();
        lblAccJual = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtAccRetJual = new javax.swing.JTextField();
        lblAccRetJual = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txtAccDiskJual = new javax.swing.JTextField();
        lblAccDiskJual = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtAccHPP = new javax.swing.JTextField();
        lblAccHPP = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txtAccRetBeli = new javax.swing.JTextField();
        lblAccRetBeli = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        txtAccBlmTertagih = new javax.swing.JTextField();
        lblAccBelumTertagih = new javax.swing.JLabel();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCatatan = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtKonvJual = new javax.swing.JTextField();
        txtUnitJual = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtHargaSatJual = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Master Item");
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

        panelGeneral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGeneral.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 590, -1));

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inventory", "Non Inventory", "Service (Jasa)" }));
        panelGeneral.add(cmbKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 160, -1));

        jLabel2.setText("Tipe Barang");
        panelGeneral.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 100, 20));
        panelGeneral.add(txtSubItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 45, 110, -1));

        chkAktif.setSelected(true);
        chkAktif.setText("Aktif");
        chkAktif.setOpaque(false);
        panelGeneral.add(chkAktif, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 20, 70, -1));

        chkSubItem.setText("Sub Item");
        chkSubItem.setOpaque(false);
        chkSubItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSubItemActionPerformed(evt);
            }
        });
        panelGeneral.add(chkSubItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 45, 90, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Informasi Penjualan");
        panelGeneral.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 250, 20));
        panelGeneral.add(txtKodeItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 120, -1));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Kategori : ");
        panelGeneral.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 110, 20));

        lblSupplier.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGeneral.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 440, 130, 20));

        jLabel6.setText("Kode Barang");
        panelGeneral.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 100, 20));

        cmbTipe.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inventory", "Non Inventory", "Service (Jasa)" }));
        panelGeneral.add(cmbTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 150, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Saldo Saat Ini"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Kuantitas");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 100, 20));

        lblCurrentQty.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCurrentQty.setText("0");
        jPanel1.add(lblCurrentQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 120, 20));

        jLabel10.setText("Harga Per Unit");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 100, 20));

        jLabel11.setText("Harga Pokok");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 100, 20));

        jButton1.setText("...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 21, 20, 20));

        lblCurrentHPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCurrentHPP.setText("0");
        jPanel1.add(lblCurrentHPP, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 120, 20));

        lblCurrentUnitPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCurrentUnitPrice.setText("0");
        jPanel1.add(lblCurrentUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 45, 120, 20));

        txtUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUnitKeyReleased(evt);
            }
        });
        jPanel1.add(txtUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 70, -1));

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Satuan Kecil : ");
        jPanel1.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 20, 120, 20));

        panelGeneral.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 590, 100));

        jLabel7.setText("Nama Barang");
        panelGeneral.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        panelSaldoAwal.setBorder(javax.swing.BorderFactory.createTitledBorder("Saldo Awal"));
        panelSaldoAwal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setText("Kuantitas");
        panelSaldoAwal.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 100, 20));

        txtSaldoQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelSaldoAwal.add(txtSaldoQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 130, -1));

        lblSaldoHPP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSaldoHPP.setText("0");
        panelSaldoAwal.add(lblSaldoHPP, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 130, 20));

        txtUnitPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelSaldoAwal.add(txtUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 45, 130, -1));

        jLabel16.setText("Harga Per Unit");
        panelSaldoAwal.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 100, 20));

        jLabel17.setText("Harga Pokok");
        panelSaldoAwal.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 100, 20));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Per tgl. ");
        panelSaldoAwal.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 110, 20));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Inventory", "Non Inventory", "Service (Jasa)" }));
        panelSaldoAwal.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 45, 160, -1));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Exp. Date : ");
        panelSaldoAwal.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 110, 20));
        panelSaldoAwal.add(jDateSaldoAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, 120, -1));

        btnLookupSaldo.setText("...");
        panelSaldoAwal.add(btnLookupSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 45, 20, 20));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Gudang : ");
        panelSaldoAwal.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 45, 110, 20));
        panelSaldoAwal.add(txtExpDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 100, -1));

        panelGeneral.add(panelSaldoAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 590, 100));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel22.setText("Informasi Persediaan");
        panelGeneral.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 250, 20));

        jLabel23.setText("Harga Jual");
        panelGeneral.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 100, 20));

        txtHargaJual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHargaJualKeyTyped(evt);
            }
        });
        panelGeneral.add(txtHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 390, 130, -1));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("%");
        panelGeneral.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 390, 20, 20));
        panelGeneral.add(txtDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 390, 30, -1));

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Diskon : ");
        panelGeneral.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 390, 70, 20));

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Kode Pajak Penjualan ");
        panelGeneral.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 390, 140, 20));
        panelGeneral.add(txtKodePajakJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 390, 80, -1));

        jLabel27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGeneral.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 371, 590, -1));

        jLabel28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGeneral.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 590, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel29.setText("Informasi Pembelian");
        panelGeneral.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 420, 250, 20));

        jLabel30.setText("Supplier Utama");
        panelGeneral.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 440, 100, 20));

        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        panelGeneral.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 440, 30, 20));

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Min. Reorder : ");
        panelGeneral.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 440, 90, 20));
        panelGeneral.add(txtMinReorder, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 440, 40, -1));

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Kode Pajak Pemb. ");
        panelGeneral.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 440, 110, 20));
        panelGeneral.add(txtKodePajakBeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 440, 80, -1));

        lblSubItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGeneral.add(lblSubItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 66, 270, 20));
        panelGeneral.add(txtNamaItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 490, -1));

        jTabbedPane1.addTab("General", panelGeneral);

        panelAkun.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setText("Akun Persediaan");
        panelAkun.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 120, 20));

        txtAccPersediaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccPersediaanKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccPersediaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 80, 20));

        lblAccPersediaan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccPersediaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 360, 20));

        jLabel36.setText("Akun Penjualan");
        panelAkun.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 65, 120, 20));

        txtAccJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccJualKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 65, 80, 20));

        lblAccJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 65, 360, 20));

        jLabel38.setText("Akun Ret. Penjualan");
        panelAkun.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 120, 20));

        txtAccRetJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccRetJualKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccRetJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 80, 20));

        lblAccRetJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccRetJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 360, 20));

        jLabel40.setText("Akun Disk. Penjualan");
        panelAkun.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 115, 120, 20));

        txtAccDiskJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccDiskJualKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccDiskJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 115, 80, 20));

        lblAccDiskJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccDiskJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 115, 360, 20));

        jLabel42.setText("Akun HPP");
        panelAkun.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 120, 20));

        txtAccHPP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccHPPKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccHPP, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 80, 20));

        lblAccHPP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccHPP, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, 360, 20));

        jLabel44.setText("Akun Ret. Pembelian");
        panelAkun.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 165, 120, 20));

        txtAccRetBeli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccRetBeliKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccRetBeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 165, 80, 20));

        lblAccRetBeli.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccRetBeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 165, 360, 20));

        jLabel46.setText("Akun Belum Tertagih");
        panelAkun.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 120, 20));

        txtAccBlmTertagih.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAccBlmTertagihKeyReleased(evt);
            }
        });
        panelAkun.add(txtAccBlmTertagih, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 80, 20));

        lblAccBelumTertagih.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAkun.add(lblAccBelumTertagih, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 190, 360, 20));

        jTabbedPane1.addTab("Akun - Akun", panelAkun);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Catatan"));

        txtCatatan.setColumns(20);
        txtCatatan.setRows(5);
        jScrollPane2.setViewportView(txtCatatan);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Setting penjualan"));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Konversi");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 30, 70, 20));

        jLabel9.setText("Satuan");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 20));

        txtKonvJual.setEditable(false);
        jPanel4.add(txtKonvJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, 40, 20));

        txtUnitJual.setEditable(false);
        jPanel4.add(txtUnitJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 60, 20));

        jLabel12.setText("Harga per Satuan Jual");
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 130, 20));

        txtHargaSatJual.setEditable(false);
        txtHargaSatJual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel4.add(txtHargaSatJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 30, 110, 20));

        jLabel13.setText("Barcode");

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                        .addContainerGap(11, Short.MAX_VALUE))
                    .addGroup(jXPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Lain lain", jXPanel1);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 610, 500));

        btnSave.setMnemonic('S');
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 520, 80, -1));

        jButton3.setMnemonic('C');
        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(533, 520, 80, -1));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-631)/2, (screenSize.height-585)/2, 631, 585);
    }// </editor-fold>//GEN-END:initComponents

    private void chkSubItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSubItemActionPerformed
        txtSubItem.setVisible(chkSubItem.isSelected());
        lblSubItem.setVisible(chkSubItem.isSelected());
    }//GEN-LAST:event_chkSubItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(txtUnit.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi unit terkecil terlebih dulu");
            txtUnit.requestFocus();
            return;
        }
        DlgSatuanTambahan d1=new DlgSatuanTambahan(JOptionPane.getFrameForComponent(this), true);
        d1.setFormItemMaster(this);
        d1.setUnitKecil(txtUnit.getText());
        d1.setUnit2(sUnit2); d1.setKonv2(fKonv2);
        d1.setUnit3(sUnit3); d1.setKonv3(fKonv3);
        d1.setVisible(true);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtHargaJualKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHargaJualKeyTyped
        GeneralFunction.keyTyped(evt);
    }//GEN-LAST:event_txtHargaJualKeyTyped

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        fn.lookup(evt, new Object[]{lblSupplier}, "select kode_supp as kode, coalesce(nama_supp,'') as nama_supplier " +
                "from r_supplier where kode_supp ||coalesce(nama_supp,'')||coalesce(kontak,'') ilike '%"+txtSupplier.getText()+"%' order by 2",
                txtSupplier.getWidth()+lblSupplier.getWidth(), 200);
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtAccPersediaanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccPersediaanKeyReleased
         fn.lookup(evt, new Object[]{lblAccPersediaan},
                 "select acc_no, acc_name from acc_coa where acc_type='03' and acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccPersediaan.getWidth()+lblAccPersediaan.getWidth()+17, 200);
    }//GEN-LAST:event_txtAccPersediaanKeyReleased

    private void txtAccJualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccJualKeyReleased
        fn.lookup(evt, new Object[]{lblAccJual}, "select acc_no, acc_name from acc_coa where acc_type in('11', '14') and acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccJual.getWidth()+lblAccJual.getWidth()+17, 200);
    }//GEN-LAST:event_txtAccJualKeyReleased

    private void txtAccRetJualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccRetJualKeyReleased
        fn.lookup(evt, new Object[]{lblAccRetJual}, "select acc_no, acc_name from acc_coa where  acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccRetJual.getWidth()+lblAccRetJual.getWidth()+17, 200);

    }//GEN-LAST:event_txtAccRetJualKeyReleased

    private void txtAccDiskJualKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccDiskJualKeyReleased
        fn.lookup(evt, new Object[]{lblAccDiskJual}, "select acc_no, acc_name from acc_coa where acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccDiskJual.getWidth()+lblAccDiskJual.getWidth()+17, 200);

    }//GEN-LAST:event_txtAccDiskJualKeyReleased

    private void txtAccHPPKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccHPPKeyReleased
        fn.lookup(evt, new Object[]{lblAccHPP}, "select acc_no, acc_name from acc_coa where acc_type='12' and acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccHPP.getWidth()+lblAccHPP.getWidth()+17, 200);

    }//GEN-LAST:event_txtAccHPPKeyReleased

    private void txtAccRetBeliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccRetBeliKeyReleased
        fn.lookup(evt, new Object[]{lblAccRetBeli}, "select acc_no, acc_name from acc_coa where acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccRetBeli.getWidth()+lblAccRetBeli.getWidth()+17, 200);
    }//GEN-LAST:event_txtAccRetBeliKeyReleased

    private void txtAccBlmTertagihKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAccBlmTertagihKeyReleased
        fn.lookup(evt, new Object[]{lblAccBelumTertagih}, "select acc_no, acc_name from acc_coa where acc_no||coalesce(acc_name,'') ilike '%"+((JTextField)evt.getSource()).getText()+"%'",
                 txtAccBlmTertagih.getWidth()+lblAccBelumTertagih.getWidth()+17, 200);
    }//GEN-LAST:event_txtAccBlmTertagihKeyReleased

    private void txtUnitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitKeyReleased
        txtUnitJual.setText(txtUnit.getText());
    }//GEN-LAST:event_txtUnitKeyReleased

    /**
    * @param args the command line arguments
    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new FrmItemMaster().setVisible(true);
//            }
//        });
//    }

    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);
           //c.setSelectionStart(0);
           //c.setSelectionEnd(c.getText().length());

           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            //c.setForeground(fHitam);
        }
   };

   private void udfLookup(KeyEvent evt, String sQry, Object srcObj[]){
           // TODO add your handling code here:
    try {
        switch (evt.getKeyCode()) {
            case java.awt.event.KeyEvent.VK_ENTER: {
                if (lst.isVisible()) {
                    Object[] obj = lst.getOResult();
                    if (obj.length > 0) {
                        for(int i=0; i< srcObj.length; i++){
                           if(srcObj[i] instanceof JLabel)
                               ((JLabel)srcObj[i]).setText(obj[0].toString());
                           else if (srcObj[i] instanceof JTextField)
                               ((JTextField)srcObj[i]).setText(obj[0].toString());
                        }
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
                for(int i=0; i< srcObj.length; i++){
                   if(srcObj[i] instanceof JLabel)
                       ((JLabel)srcObj[i]).setText("");
                   else if (srcObj[i] instanceof JTextField)
                       ((JTextField)srcObj[i]).setText("");
                }
                break;
            }
            case java.awt.event.KeyEvent.VK_DOWN: {
                lst.getTable().requestFocus();
                break;
            }
            default: {
                if (!evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("Up") || !evt.getKeyText(evt.getKeyCode()).equalsIgnoreCase("F2")) {
                    System.out.println(sQry);
                    lst.setSQuery(sQry);

                    lst.setBounds(((JTextField)evt.getSource()).getLocationOnScreen().x,
                            ((JTextField)evt.getSource()).getLocationOnScreen().y+((JTextField)evt.getSource()).getHeight() ,
                            ((JTextField)evt.getSource()).getWidth() + ((JTextField)srcObj[0]).getWidth()+10,
                            (lst.getIRowCount()>10? 12*lst.getRowHeight(): (lst.getIRowCount()+2)*lst.getRowHeight()));
                    
                    lst.setFocusableWindowState(false);
                    lst.setTxtCari((JTextField)evt.getSource());
                    lst.setCompDes((JComponent[])srcObj);
                    lst.setColWidth(0, ((JTextField)evt.getSource()).getWidth());
                    if(srcObj[0] instanceof JLabel ) lst.setColWidth(1, ((JLabel)evt.getSource()).getWidth()-10);

                    if (lst.getIRowCount() > 0) {
                        lst.setVisible(true);
                        requestFocusInWindow();
                        ((JTextField)evt.getSource()).requestFocus();
                    } else {
                        lst.setVisible(false);
                        for(int i=0; i< srcObj.length; i++){
                           if(srcObj[i] instanceof JLabel)
                               ((JLabel)srcObj[i]).setText("");
                           else if (srcObj[i] instanceof JTextField)
                               ((JTextField)srcObj[i]).setText("");
                        }
                    }
                }
                break;
            }
        }
    } catch (SQLException se) {
        System.out.println(se.getMessage());
    }
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLookupSaldo;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkAktif;
    private javax.swing.JCheckBox chkSubItem;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbKategori;
    private javax.swing.JComboBox cmbTipe;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private org.jdesktop.swingx.JXDatePicker jDateSaldoAwal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private javax.swing.JLabel lblAccBelumTertagih;
    private javax.swing.JLabel lblAccDiskJual;
    private javax.swing.JLabel lblAccHPP;
    private javax.swing.JLabel lblAccJual;
    private javax.swing.JLabel lblAccPersediaan;
    private javax.swing.JLabel lblAccRetBeli;
    private javax.swing.JLabel lblAccRetJual;
    private javax.swing.JLabel lblCurrentHPP;
    private javax.swing.JLabel lblCurrentQty;
    private javax.swing.JLabel lblCurrentUnitPrice;
    private javax.swing.JLabel lblSaldoHPP;
    private javax.swing.JLabel lblSubItem;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JPanel panelAkun;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelSaldoAwal;
    private javax.swing.JTextField txtAccBlmTertagih;
    private javax.swing.JTextField txtAccDiskJual;
    private javax.swing.JTextField txtAccHPP;
    private javax.swing.JTextField txtAccJual;
    private javax.swing.JTextField txtAccPersediaan;
    private javax.swing.JTextField txtAccRetBeli;
    private javax.swing.JTextField txtAccRetJual;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextArea txtCatatan;
    private javax.swing.JTextField txtDiskon;
    private javax.swing.JTextField txtExpDate;
    private javax.swing.JTextField txtHargaJual;
    private javax.swing.JTextField txtHargaSatJual;
    private javax.swing.JTextField txtKodeItem;
    private javax.swing.JTextField txtKodePajakBeli;
    private javax.swing.JTextField txtKodePajakJual;
    private javax.swing.JTextField txtKonvJual;
    private javax.swing.JTextField txtMinReorder;
    private javax.swing.JTextField txtNamaItem;
    private javax.swing.JTextField txtSaldoQty;
    private javax.swing.JTextField txtSubItem;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtUnit;
    private javax.swing.JTextField txtUnitJual;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables

    private MyKeyListener kListener =new MyKeyListener();
    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);

}
