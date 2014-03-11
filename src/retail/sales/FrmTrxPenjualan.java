/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmTrxPenjualan.java
 *
 * Created on 04 Feb 11, 19:39:06
 */

package retail.sales;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import retail.DlgLookupItemJual;
import retail.DlgPembayaran;
import retail.MainForm;
import retail.PrintPenjualan;
import retail.main.GeneralFunction;
import retail.main.JDesktopImage;
import retail.main.SysConfig;



/**
 *
 * @author cak-ust
 */
public class FrmTrxPenjualan extends javax.swing.JFrame {
    private Connection conn;
    private GeneralFunction fn;
    private Component aThis;
    private DlgLookupItemJual lookupItem =new DlgLookupItemJual(this, true);
    private MyKeyListener kListener=new MyKeyListener();
    private boolean lockHarga=true;
    private boolean isKoreksi=false;
    ArrayList lstGudang=new ArrayList();
    private boolean stItemUpd=false;
    private String sNoTrx;
    private JDesktopImage desktop;
    private Object srcForm;

    /** Creates new form FrmTrxPenjualan */
    public FrmTrxPenjualan() {
        initComponents();

       
        //initConn();
       table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");

        //AutoCompleteDecorator.decorate(cmbSatuan);

        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                udfSetTotal();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                udfClearItem();
            }
        });
        aThis=this;
        udfNew();
    }

    public void setSrcForm(Object frm){
        srcForm=frm;
    }

    private void udfSetTotal(){
        lblTotal.setText("0.00");
        double dTotal=0;
        for(int i=0; i<table.getRowCount(); i++){
            dTotal+=fn.udfGetDouble(table.getValueAt(i, table.getColumnModel().getColumnIndex("Sub Total")));
        }
        lblTotal.setText(fn.dFmt.format(dTotal));
    }

    public void setLockHarga(boolean b){
        this.lockHarga=b;
    }
    
    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        table.getColumn("ProductID").setPreferredWidth(txtKode.getWidth());
        table.getColumn("Nama Barang").setPreferredWidth(lblItem.getWidth());
        table.getColumn("Satuan").setPreferredWidth(cmbSatuan.getWidth());
        table.getColumn("Qty").setPreferredWidth(txtQty.getWidth());
        table.getColumn("Harga").setPreferredWidth(txtHarga.getWidth());
        table.getColumn("Sub Total").setPreferredWidth(lblSubTotal.getWidth());
        table.getColumn("Konv").setPreferredWidth(lblKonv.getWidth());
        table.getTableHeader().setResizingAllowed(false);

        table.setRowHeight(22);
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.removeComboUpDown(cmbGudang);
        fn.removeComboUpDown(cmbCustPembayaran);
        fn.removeComboUpDown(cmbCustType);
        fn.removeComboUpDown(cmbSatuan);
        table.addKeyListener(kListener);
        txtHarga.setEnabled(!lockHarga);

        lookupItem.setConn(conn);
        fn.setConn(conn);
        cmbGudang.setSelectedItem(MainForm.sNamaGudang);
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
        jFJtTempo.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs=conn.createStatement().executeQuery("select kode_gudang, coalesce(nama_gudang,'') as nama_gudang, " +
                    "to_char(current_date, 'dd/MM/yyyy') as tgl " +
                    "from r_gudang order by 1");
            lstGudang.clear();
            cmbGudang.removeAllItems();

            while(rs.next()){
                lstGudang.add(rs.getString(1));
                cmbGudang.addItem(rs.getString(2));
                jFJtTempo.setText(rs.getString("tgl"));
                jFJtTempo.setValue(rs.getString("tgl"));
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        if(isKoreksi)
            udfLoadKoreksiJual();
        else
            udfNew();

        txtNoTrx.setEnabled(isKoreksi);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(isKoreksi)
                    txtNoTrx.requestFocusInWindow();
                else
                    txtKode.requestFocusInWindow();
            }
        });

    }

    private void udfLoadKoreksiJual(){
        String sQry="select h.sales_no, to_char(h.sales_date, 'dd/MM/yyyy') as tgl_trx, case when h.cust_type='G' then 'GROSIR' else 'ECERAN' end as cust_type," +
                "coalesce(h.kode_cust,'') as kode_cust, coalesce(c.nama,'') as nama_cust, coalesce(h.kode_gudang,'') as kode_gudang, " +
                "coalesce(g.nama_gudang,'') as nama_gudang, coalesce(h.catatan,'') as catatan, " +
                "case when h.jenis='T' then 'TUNAI' else 'KREDIT' end as jenis " +
                "from r_sales h " +
                "left join r_customer c on c.kode_cust=h.kode_cust " +
                "left join r_gudang g on g.kode_gudang=h.kode_gudang " +
                "where h.sales_no='"+txtNoTrx.getText()+"'";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                txtNoTrx.setText(rs.getString("sales_no"));
                txtCustomer.setText(rs.getString("kode_cust"));
                txtNamaCustomer.setText(rs.getString("nama_cust"));
                cmbCustType.setSelectedItem(rs.getString("cust_type"));
                lblTgl.setText(rs.getString("tgl_trx"));
                cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtCatatan.setText(rs.getString("catatan"));
                cmbCustPembayaran.setSelectedItem(rs.getString("jenis"));

                rs.close();
                sQry="select d.kode_item, coalesce(i.nama_item,'') as nama_item, coalesce(d.qty,0) as qty, " +
                        "coalesce(d.unit_jual,'') as unit_jual, coalesce(d.unit_price,0) as unit_price, " +
                        "coalesce(d.qty,0) * coalesce(d.unit_price,0) as sub_Total, " +
                        "case when d.unit_jual=i.unit2 then coalesce(konv2,0) when d.unit_jual=i.unit2 then coalesce(konv3,0) else 1 end as konv " +
                        "from r_sales_detail d " +
                        "inner join r_item i on i.kode_item=d.kode_item  " +
                        "where d.sales_no='"+txtNoTrx.getText()+"'";

                ((DefaultTableModel)table.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                        rs.getString("kode_item"),
                        rs.getString("nama_item"),
                        rs.getString("unit_jual"),
                        rs.getDouble("qty"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("sub_Total"),
                        rs.getDouble("konv")
                    });
                }
                if(table.getRowCount()>0)
                    table.setRowSelectionInterval(0, 0);

            }else{
                JOptionPane.showMessageDialog(this, "No. Penjualan tidak ditemukan!");
                udfNew();
                txtNoTrx.requestFocus();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }

    private void initConn(){
        String url = "jdbc:postgresql://localhost/NABILA";
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url,"tadho","ustasoft");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch(ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }
    }

    private void udfLoadKonversi(String sUnit) {
        //int row=table.getSelectedRow();
        if(cmbSatuan.getSelectedIndex()<0) return;
        try {
            String sCustType=cmbCustType.getSelectedItem().toString().substring(0, 1);
            //String sCustType=cmbCustType.getSelectedIndex()==0? "G": "R";
            String sQry = "select case  when '" + sUnit + "'=unit then 1 " +
                          "             when '" + sUnit + "'=unit2 then coalesce(konv2,1) " +
                          "             when '" + sUnit + "'=unit3 then coalesce(konv3,1) " +
                          "             else 1 end as konv, " +
                          "case  when '"+sCustType+"'='G' and '" + sUnit + "'=coalesce(unit,'') then harga_g_1 " +
                          "      when '"+sCustType+"'='G' and '" + sUnit + "'=coalesce(unit2,'') then harga_g_2 " +
                          "      when '"+sCustType+"'='G' and '" + sUnit + "'=coalesce(unit3,'') then harga_g_3 " +
                          "      when '"+sCustType+"'='E' and '" + sUnit + "'=coalesce(unit,'') then harga_r_1 " +
                          "      when '"+sCustType+"'='E' and '" + sUnit + "'=coalesce(unit2,'') then harga_r_2 " +
                          "      when '"+sCustType+"'='E' and '" + sUnit + "'=coalesce(unit3,'') then harga_r_3  " +
                          "end as harga " +
                          "from r_item i " +
                          "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                          "where i.kode_item='" + txtKode.getText() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                txtHarga.setText(fn.dFmt.format(rs.getDouble("harga")));
                lblSubTotal.setText(fn.dFmt.format(rs.getDouble("harga")*fn.udfGetDouble(txtQty.getText())));
                lblKonv.setText(rs.getString("konv"));
            } else {
                txtHarga.setText("0");
                lblSubTotal.setText("0");
                lblKonv.setText("1");
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfChangePriceAll(){
        if(table.getRowCount()<=0) return;
        try{
            ResultSet rs=null;
            TableColumnModel col=table.getColumnModel();
            //String sCustType=cmbCustType.getSelectedIndex()==0? "G": "R";
            String sCustType=cmbCustType.getSelectedItem().toString().substring(0, 1);
            String sQry="", sUnit;
            for (int i=0; i<=table.getRowCount(); i++){
                sUnit=table.getValueAt(i, col.getColumnIndex("Satuan")).toString();
                if(i==table.getRowCount() && txtKode.getText().length()>0){
                    sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                         "case  when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit,'') then harga_g_1 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit2,'') then harga_g_2 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit3,'') then harga_g_3 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit,'') then harga_r_1 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit2,'') then harga_r_2 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit3,'') then harga_r_3 " +
                         "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                         "end as harga " +
                         "from r_item i " +
                         "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                         "where i.kode_item='"+txtKode.getText()+"'";
                    rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        txtHarga.setText(fn.dFmt.format(rs.getDouble("harga")));
                        lblSubTotal.setText(fn.dFmt.format(rs.getDouble("harga")*fn.udfGetDouble(txtQty.getText())));
                    }
                    rs.close();
                }else{
                    sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                         "case  when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit,'') then harga_g_1 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit2,'') then harga_g_2 " +
                         "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit3,'') then harga_g_3 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit,'') then harga_r_1 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit2,'') then harga_r_2 " +
                         "      when '"+sCustType+"'='E' and '"+sUnit+"'=coalesce(unit3,'') then harga_r_3 " +
                         "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                         "end as harga " +
                         "from r_item i " +
                         "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                         "where i.kode_item='"+table.getValueAt(i, 0).toString()+"'";
                    rs=conn.createStatement().executeQuery(sQry);
                    if(rs.next()){
                        table.setValueAt(rs.getDouble("harga"), i, col.getColumnIndex("Harga"));
                        table.setValueAt(rs.getDouble("harga")*fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty"))),
                                i, col.getColumnIndex("Sub Total"));
                    }
                    rs.close();
                }
            }


        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private String udfLoadItem(){
        String sMsg="";
        try{
            String sCustType=cmbCustType.getSelectedIndex()==0? "G": "E";
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                         "case  when '"+sCustType+"'='G' and coalesce(unit_jual,'')=coalesce(unit,'') then harga_g_1 " +
                         "      when '"+sCustType+"'='G' and coalesce(unit_jual,'')=coalesce(unit2,'') then harga_g_2 " +
                         "      when '"+sCustType+"'='G' and coalesce(unit_jual,'')=coalesce(unit3,'') then harga_g_3 " +
                         "      when '"+sCustType+"'='E' and coalesce(unit_jual,'')=coalesce(unit,'') then harga_r_1 " +
                         "      when '"+sCustType+"'='E' and coalesce(unit_jual,'')=coalesce(unit2,'') then harga_r_2 " +
                         "      when '"+sCustType+"'='E' and coalesce(unit_jual,'')=coalesce(unit3,'') then harga_r_3 " +
                         "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                         "end as harga " +
                         "from r_item i " +
                         "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                         "where i.kode_item='"+txtKode.getText()+"' or coalesce(i.barcode,'')='"+txtKode.getText()+"' ";

                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     lblItem.setText(rs.getString("nama_item"));
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                     cmbSatuan.setSelectedItem(rs.getString("unit_jual"));
                     lblKonv.setText(rs.getString("konv_jual"));
                     txtHarga.setText(fn.dFmt.format(rs.getDouble("harga")));
                     txtQty.setText("1");
                     lblSubTotal.setText(txtHarga.getText());

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

    public void setFlagKoreksi(boolean b) {
        this.isKoreksi=b;
    }

    public void setNoTrx(String s) {
        this.sNoTrx = s;
        txtNoTrx.setText(s);
    }

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktop=jDesktopPane1;
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(txtQty))
                lblSubTotal.setText(fn.dFmt.format(fn.udfGetDouble(txtQty.getText())*fn.udfGetDouble(txtHarga.getText())));
            else if(evt.getSource().equals(txtKode) && txtKode.getText().trim().length()==0)
                udfClearItem();
        }

        @Override
        public void keyTyped(KeyEvent evt){
            if(evt.getSource().equals(txtHarga) || evt.getSource().equals(txtQty))
                fn.keyTyped(evt);
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{

                    if(cmbCustType.getSelectedIndex()<0){
                        JOptionPane.showMessageDialog(aThis, "Pilih harga penjualan terlebih dulu!");
                        cmbCustType.requestFocus();
                        return;
                    }
                    //if(table.getCellEditor()!=null && evt.getSource().equals(table))
                    if(!txtKode.isFocusOwner())    
                        txtKode.requestFocus();
                    
                    if(table.getCellEditor()!=null && evt.getSource().equals(table))
                        table.getCellEditor().stopCellEditing();
                    lookupItem.setAlwaysOnTop(true);
                    lookupItem.setSrcTable(table, table.getColumnModel().getColumnIndex("Qty"));
                    lookupItem.setKeyEvent(evt);
                    lookupItem.setObjForm(this);
                    lookupItem.setVisible(true);
                    lookupItem.clearText();
                    lookupItem.requestFocusInWindow();
                    if(lookupItem.getKodeBarang().length()>0){
                        txtKode.setText(lookupItem.getKodeBarang());
                        String sMsg=udfLoadItem();
                        if(sMsg.length()>0){
                            JOptionPane.showMessageDialog(aThis, sMsg);
                            //if(!txtKode.isFocusOwner())
                                txtKode.requestFocus();
                            return;
                        }
                        txtQty.requestFocus();
                        //cmbSatuan.requestFocus();
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
                    txtKode.setText(table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString());
                    udfLoadComboKonversi();
                    lblItem.setText(table.getValueAt(iRow, col.getColumnIndex("Nama Barang")).toString());
                    cmbSatuan.setSelectedItem(table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString());
                    txtQty.setText(fn.intFmt.format(fn.udfGetInt(table.getValueAt(iRow, col.getColumnIndex("Qty")))));
                    txtHarga.setText(fn.dFmt.format(fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga")))));
                    lblSubTotal.setText(fn.dFmt.format(fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Sub Total")))));
                    txtQty.requestFocusInWindow();
                    break;
                }
                case KeyEvent.VK_F9:{
//                    if(tblDetail.getRowCount()==0) return;
//                    ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
//                        tblHeader.getRowCount()+1, "T", 0
//                    });
//                    tblHeader.requestFocusInWindow();
//                    tblHeader.requestFocus();
//                    tblHeader.setRowSelectionInterval(tblHeader.getRowCount()-1, tblHeader.getRowCount()-1);
//                    tblHeader.changeSelection(tblHeader.getRowCount()-1, 1, false, false);
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
//                        if(txtKode.isFocusOwner()){
//                            txtQty.requestFocusInWindow();
//                            return;
                        //}else
                        if((lockHarga && txtQty.isFocusOwner()) || txtHarga.isFocusOwner()){
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
                    if(fn.isListVisible()){
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
        if(fn.udfGetDouble(txtHarga.getText())==0){
            txtKode.requestFocus();
            JOptionPane.showMessageDialog(this, "Harga jual belum diset!");
            
            return;
        }
        TableColumnModel col=table.getColumnModel();
        if(stItemUpd){
            int iRow=table.getSelectedRow();
            if(iRow<0) return;
            table.setValueAt(txtKode.getText(), iRow, col.getColumnIndex("ProductID"));
            table.setValueAt(lblItem.getText(), iRow, col.getColumnIndex("Nama Barang"));
            table.setValueAt(cmbSatuan.getSelectedItem().toString(), iRow, col.getColumnIndex("Satuan"));
            table.setValueAt(fn.udfGetInt(txtQty.getText()), iRow, col.getColumnIndex("Qty"));
            table.setValueAt(fn.udfGetDouble(txtHarga.getText()), iRow, col.getColumnIndex("Harga"));
            table.setValueAt(fn.udfGetDouble(lblSubTotal.getText()), iRow, col.getColumnIndex("Sub Total"));
            table.setValueAt(fn.udfGetInt(lblKonv.getText()), iRow, col.getColumnIndex("Konv"));
            table.changeSelection(iRow, iRow, false, false);
        }else{
            String sUnit="";
            for(int i=0; i<table.getRowCount(); i++){
                sUnit=cmbSatuan.getSelectedIndex()<0? "": cmbSatuan.getSelectedItem().toString();
                if(table.getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) && table.getValueAt(i, table.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(sUnit)){
                    if(JOptionPane.showConfirmDialog(this, ("Item tersebut sudah dimasukkan pada baris ke "+(i+1)+".\nQty Item akan ditambahkan?"), "Item exists", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                        return;
                    else{
                        double total=fn.udfGetInt(txtQty.getText())+fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")));
                        table.setValueAt(total, i, col.getColumnIndex("Qty"));
                        table.setValueAt(total*fn.udfGetDouble(lblSubTotal.getText()), i, col.getColumnIndex("Sub Total"));
                        udfClearItem();
                        txtKode.requestFocusInWindow();
                        return;
                    }
                }
            }
            ((DefaultTableModel)table.getModel()).addRow(new Object[]{
                txtKode.getText(),
                lblItem.getText(),
                (cmbSatuan.getSelectedItem()==null? "": cmbSatuan.getSelectedItem().toString()) ,
                fn.udfGetDouble(txtQty.getText()),
                fn.udfGetDouble(txtHarga.getText()),
                fn.udfGetDouble(lblSubTotal.getText()),
                fn.udfGetInt(lblKonv.getText()),
            });
        table.setRowSelectionInterval(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1);
        table.changeSelection(((DefaultTableModel)table.getModel()).getRowCount()-1, ((DefaultTableModel)table.getModel()).getRowCount()-1, false, false);
        }

        udfClearItem();
        txtKode.requestFocus();
        txtKode.requestFocusInWindow();
    }

    private void udfClearItem(){
        txtKode.setText("");
        lblItem.setText("");
        cmbSatuan.removeAllItems();
        txtQty.setText("1");
        txtHarga.setText("0");
        lblSubTotal.setText("0");
        lblKonv.setText("1");
        stItemUpd=false;
    }

    private void udfNew() {
        cmbCustType.setSelectedIndex(0);
        cmbCustPembayaran.setSelectedIndex(0);
        jLabel3.setVisible(false); jFJtTempo.setVisible(false);
        txtNoTrx.setText("");
        txtNoTrx.setEnabled(isKoreksi);
        txtCustomer.setText(""); txtCustomer.setText("");
        ((DefaultTableModel)table.getModel()).setNumRows(0);
        lblTotal.setText("0");
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);   btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        udfClearItem();
        txtKode.requestFocusInWindow();
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(table.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada item yang ditransaksikan!\nTekan 'Insert' untuk menambahkan item penjualan");
            txtKode.requestFocus();
            return false;
        }
        if(cmbCustPembayaran.getSelectedItem().toString().equalsIgnoreCase("KREDIT") && txtCustomer.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Isikan nama customer untuk penjualan kredit");
            txtCustomer.requestFocus();
            return false;
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        String sNoKoreksi="";
        try{
            conn.setAutoCommit(false);
            ResultSet rs=null;

            if(isKoreksi){
                if(table.getRowCount()==0 && JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan penjualan ini?")!=JOptionPane.YES_OPTION){
                    return;
                }
                rs=conn.createStatement().executeQuery("select fn_r_koreksi_jual('"+txtNoTrx.getText()+"')");
                if(rs.next())
                    sNoKoreksi=rs.getString(1);
                
                rs.close();
                if(table.getRowCount()==0){
                    this.dispose();
//                    udfNew();
//                    return;
                }
            }

            rs=conn.createStatement().executeQuery("select fn_r_get_sales_no("+(cmbCustPembayaran.getSelectedIndex()==1)+", " +
                    (isKoreksi?"(select to_char(sales_date, 'yyyy-MM-dd') from r_sales where sales_no='"+txtNoTrx.getText()+"')": "current_date::varchar")+" )");
            //String sQry="Select * from r_sales_detail limit 0";
            if(rs.next()){
                txtNoTrx.setText(rs.getString(1));
            }
            rs.close();
            DlgPembayaran d1=new DlgPembayaran(this, true);
            d1.setNoTrx(txtNoTrx.getText());
            d1.setTotal(fn.udfGetDouble(lblTotal.getText()));
            d1.setVisible(true);
            if(!d1.isSelected()) return;

            if(d1.getBayar()<fn.udfGetDouble(lblTotal.getText()) &&
                    (txtCustomer.getText().trim().equalsIgnoreCase("")||txtCustomer.getText().trim().equalsIgnoreCase("CASH"))){
                JOptionPane.showMessageDialog(this, "Untuk transaksi kredit silakan masukkan nama pelanggan terlebih dulu!");
                txtCustomer.requestFocusInWindow();
                return;
            }
            String sQryH="INSERT INTO r_sales(" +
                        "sales_no, sales_date, kode_cust, " +
                        "catatan, " +
                        "date_ins, user_ins, is_kredit, " +
                        "kode_gudang, cust_type, " +
                        "jenis, tgl_jt_tempo, bayar)" +
                        "VALUES ('"+txtNoTrx.getText()+"', "+
                        (isKoreksi?"(select sales_date from r_sales where sales_no='"+sNoKoreksi+"')": "current_date")+", " +
                        "'"+txtCustomer.getText()+"', " +
                        "'"+txtCatatan.getText()+"', " +
                        "now(), '"+MainForm.sUserName+"', "+(cmbCustPembayaran.getSelectedIndex()==1)+", " +
                        "'"+lstGudang.get(cmbGudang.getSelectedIndex()).toString()+"', '"+cmbCustType.getSelectedItem().toString().substring(0, 1)+"', " +
                        "'"+cmbCustPembayaran.getSelectedItem().toString().substring(0, 1).toUpperCase()+"', " +
                         (cmbCustPembayaran.getSelectedIndex()==1?"'"+new SimpleDateFormat("dd/MM/yy").parse(jFJtTempo.getText())+"'" :"null")+", " +
                        d1.getBayar()+ ");";

            String sQry="";
            TableColumnModel col=table.getColumnModel();
            for(int iRow=0; iRow<table.getRowCount(); iRow++){
                if(table.getValueAt(iRow, col.getColumnIndex("ProductID"))!=null &&
                   table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString().length()>0){
                    sQry+= //(sQry.length()>0? " union all ": "")+
                           "INSERT INTO r_sales_detail(sales_no, kode_item, qty, unit_price, disc, " +
                           "unit_jual, konv_jual) VALUES (" +
                           "'"+txtNoTrx.getText()+"', '"+table.getValueAt(iRow, col.getColumnIndex("ProductID")).toString()+"', " +
                           fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Qty")))+", " +
                           fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Harga")))+", 0, " +
                           "'"+table.getValueAt(iRow, col.getColumnIndex("Satuan")).toString()+"', " +
                           fn.udfGetDouble(table.getValueAt(iRow, col.getColumnIndex("Konv")))+");";
                }
            }
            System.out.println(sQryH+sQry);

            int i=conn.createStatement().executeUpdate(sQryH+sQry);

            conn.setAutoCommit(true);
            if(JOptionPane.showConfirmDialog(this, "Input data sukses, Klik ok untuk cetak invoice", "Message", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                printKwitansi();
                if(fn.udfGetDouble(lblTotal.getText())>d1.getBayar())
                    printKwitansi();
                
            }
            if(isKoreksi && srcForm!=null){
                if(srcForm instanceof FrmSalesHistory){
                    ((FrmSalesHistory)srcForm).udfFilter();
                    this.dispose();
                    return;
                }
            }
            udfNew();
            rs.close();
        }catch(SQLException se){
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }catch(ParseException pe){
            JOptionPane.showMessageDialog(this, pe.getMessage());
        }

    }

    private void printKwitansi(){
        PrinterJob job = PrinterJob.getPrinterJob();
        SysConfig sy=new SysConfig();

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        int i=0;
        for(i=0;i<services.length;i++){
            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
                break;
            }
        }
        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
        try{
            PrintPenjualan pn = new PrintPenjualan(conn, txtNoTrx.getText(), MainForm.sUserName,services[i], false);

        }catch(java.lang.ArrayIndexOutOfBoundsException ie){
            JOptionPane.showMessageDialog(this, "Printer tidak ditemukan!");
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
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtQty = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        cmbSatuan = new javax.swing.JComboBox();
        txtKode = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        lblKonv = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cmbCustType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNamaCustomer = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        lblTgl = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cmbCustPembayaran = new javax.swing.JComboBox();
        cmbGudang = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jFJtTempo = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        table.setFont(new java.awt.Font("Tahoma", 0, 12));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Satuan", "Qty", "Harga", "Sub Total", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
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
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.setName("table"); // NOI18N
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtQty.setName("txtQty"); // NOI18N
        jPanel1.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 60, 20));

        lblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblItem.setName("lblItem"); // NOI18N
        jPanel1.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 0, 290, 20));

        cmbSatuan.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cmbSatuan.setName("cmbSatuan"); // NOI18N
        jPanel1.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 110, 20));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setName("txtKode"); // NOI18N
        jPanel1.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 20));

        txtHarga.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga.setName("txtHarga"); // NOI18N
        jPanel1.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 100, 20));

        lblKonv.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblKonv.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv.setName("lblKonv"); // NOI18N
        jPanel1.add(lblKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 0, 50, 20));

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSubTotal.setName("lblSubTotal"); // NOI18N
        jPanel1.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 0, 110, 20));

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setName("btnNew"); // NOI18N
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/print.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Exit.png"))); // NOI18N
        btnCancel.setText("Exit");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        lblTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 34));
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0,00");
        lblTotal.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        lblTotal.setName("lblTotal"); // NOI18N
        lblTotal.setOpaque(true);

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbCustType.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbCustType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GROSIR", "ECERAN" }));
        cmbCustType.setName("cmbCustType"); // NOI18N
        cmbCustType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCustTypeItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbCustType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 150, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Pelanggan");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText("Cust. Type");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNamaCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaCustomer.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaCustomer.setEnabled(false);
        txtNamaCustomer.setName("txtNamaCustomer"); // NOI18N
        jPanel2.add(txtNamaCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 410, 20));

        txtCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.setName("txtCustomer"); // NOI18N
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel2.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 130, 20));

        lblTgl.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTgl.setText("20/12/2010");
        lblTgl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTgl.setName("lblTgl"); // NOI18N
        jPanel2.add(lblTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 35, 90, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoTrx.setEnabled(false);
        txtNoTrx.setName("txtNoTrx"); // NOI18N
        jPanel2.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 140, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel5.setText("No.");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 30, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setText("Tgl.");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 35, 30, 20));

        jButton1.setText("+");
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 35, 30, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText("Jatuh Tempo");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 35, 90, 20));

        cmbCustPembayaran.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbCustPembayaran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbCustPembayaran.setName("cmbCustPembayaran"); // NOI18N
        cmbCustPembayaran.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCustPembayaranItemStateChanged(evt);
            }
        });
        jPanel2.add(cmbCustPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, 150, -1));

        cmbGudang.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Center" }));
        cmbGudang.setEnabled(false);
        cmbGudang.setName("cmbGudang"); // NOI18N
        jPanel2.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 60, 160, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel7.setText("Site");
        jLabel7.setName("jLabel7"); // NOI18N
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 60, 80, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel9.setText("Catatan");
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 90, 20));

        txtCatatan.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.setName("txtCatatan"); // NOI18N
        txtCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCatatanKeyReleased(evt);
            }
        });
        jPanel2.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 700, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel10.setText("Pembayaran");
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, 90, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFJtTempo.setName("jFJtTempo"); // NOI18N
        jPanel2.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 35, 110, 20));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel8.setText("<html>\n<b>F4</b> &nbsp&nbsp : Membuat transaksi baru  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Insert</b> &nbsp : Menambah item barang  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Del</b> &nbsp&nbsp &nbsp &nbsp : Menghapus item barang |  &nbsp  &nbsp\n<b>F3</b> &nbsp : Mengubah item transaksi <br>\n<b>F5</b> &nbsp&nbsp : Menyimpan Transaksi <br>\n</html>"); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(429, 429, 429)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 840, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblTotal)))
                .addGap(9, 9, 9)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-880)/2, (screenSize.height-557)/2, 880, 557);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printKwitansi();
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Revision")>0) dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Exit.png"))); // NOI18N
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void cmbCustTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCustTypeItemStateChanged
        if(table.getRowCount()>0)
            udfChangePriceAll();
}//GEN-LAST:event_cmbCustTypeItemStateChanged

    private void txtCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerKeyReleased
        fn.lookup(evt, new Object[]{txtNamaCustomer}, "select kode_cust, coalesce(nama,'') as nama_customer from r_customer " +
                "where kode_cust||coalesce(nama,'') ilike '%"+txtCustomer.getText()+"%'", 500, 200);
}//GEN-LAST:event_txtCustomerKeyReleased

    private void cmbCustPembayaranItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCustPembayaranItemStateChanged
        jLabel3.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
        jFJtTempo.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
}//GEN-LAST:event_cmbCustPembayaranItemStateChanged

    private void txtCatatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCatatanKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtCatatanKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfNewCustomer();
    }//GEN-LAST:event_jButton1ActionPerformed

    public void udfSetNewCustomer(String sKode, String sNama){
        txtCustomer.setText(sKode);
        txtNamaCustomer.setText(sNama);
        if(!txtCustomer.isFocusOwner())
            txtKode.requestFocusInWindow();
    }

    private void udfNewCustomer() {
        retail.FrmCustomerMaster fMaster=new retail.FrmCustomerMaster();
        fMaster.setTitle("Customer baru");
        fMaster.setConn(conn);
        fMaster.setIsNew(true);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        desktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setObjForm(this);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
            fMaster.requestFocusInWindow();
        } catch(PropertyVetoException PO){

        }
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmTrxPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbCustPembayaran;
    private javax.swing.JComboBox cmbCustType;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JButton jButton1;
    private javax.swing.JFormattedTextField jFJtTempo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblKonv;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTgl;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNamaCustomer;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtQty;
    // End of variables declaration//GEN-END:variables

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
                if(e.getSource().equals(txtCustomer))
                    setTitle("Penjualan "+txtNamaCustomer.getText());
                else if(e.getSource().equals(txtNoTrx) && aThis.isShowing() && aThis.isFocusable() && txtNoTrx.getText().trim().length()>0)
                    udfLoadKoreksiJual();
                else if(e.getSource().equals(txtKode) && !e.isTemporary()){
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

}
