/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPO.java
 *
 * Created on 11 Jan 11, 6:40:44
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import javax.swing.text.MaskFormatter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmPO extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    private boolean isKoreksi=false;
    MyTableCellEditor cEditor=new MyTableCellEditor();
    MyKeyListener kListener=new MyKeyListener();
    private DlgLookupItemBeli lookupItem =new DlgLookupItemBeli(JOptionPane.getFrameForComponent(this), true);
    private Component aThis;
    TableColumnModel colModel;
    private boolean stItemUpd=false;

    /** Creates new form FrmPO */
    public FrmPO() {
        initComponents();
        colModel=tblItem.getColumnModel();

        tblItem.setRowHeight(20);
        tblItem.getTableHeader().setFont(tblItem.getFont());
        tblItem.getTableHeader().setReorderingAllowed(false);
        tblItem.getTableHeader().setResizingAllowed(false);
        tblItem.getTableHeader().setBackground(Color.cyan);

//        MyTableCellEditor cEditor=new MyTableCellEditor();
//        tblItem.getColumnModel().getColumn(colModel.getColumnIndex("Qty")).setCellEditor(cEditor);
//        tblItem.getColumnModel().getColumn(colModel.getColumnIndex("Disc")).setCellEditor(cEditor);
//        tblItem.getColumnModel().getColumn(colModel.getColumnIndex("PPn")).setCellEditor(cEditor);
//        tblItem.getColumnModel().getColumn(colModel.getColumnIndex("Harga")).setCellEditor(cEditor);
//        tblItem.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuan));

        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
        fn.removeComboUpDown(cmbSatuan);
        aThis=this;
        tblItem.addKeyListener(kListener);
        tblItem.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int iRow=tblItem.getSelectedRow();
//                if((e.getType()==TableModelEvent.DELETE||e.getType()==TableModelEvent.UPDATE) && tblItem.getRowCount()>0 )
//                    tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
                if(conn==null) return;

                if(e.getColumn()==0){
//                    try{
//                        ResultSet rs=conn.createStatement().executeQuery("select coalesce(nama_item,'') as nama_item, " +
//                                     "coalesce(unit,'') as unit, " +
//                                     "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
//                                     "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
//                                     "coalesce(s.uom_alt,'') as uom_supp " +
//                                     "from r_item i " +
//                                     "left join r_item_supplier s on s.kode_item=i.kode_item and s.kode_supp='"+txtKodeSupp.getText()+"' " +
//                                     "where i.kode_item='"+tblItem.getValueAt(iRow, 0).toString()+"' ");
//                         if(rs.next()){
//                             tblItem.setValueAt(rs.getString("nama_item"), iRow, tblItem.getColumnModel().getColumnIndex("Nama Barang"));
//                             tblItem.setValueAt(rs.getString("uom_supp"), iRow, tblItem.getColumnModel().getColumnIndex("Satuan"));
//                             udfLoadKonversi(rs.getString("uom_supp"));
//
//                             if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
//                             if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
//                             if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
//
//                         }else{
//                             tblItem.setValueAt("", iRow, tblItem.getColumnModel().getColumnIndex("Nama Barang"));
//                             tblItem.setValueAt("", iRow, tblItem.getColumnModel().getColumnIndex("Satuan"));
//                             cmbSatuan.removeAllItems();
//                         }
//
//                        rs.close();
//                    }catch(SQLException se){
//                        JOptionPane.showMessageDialog(aThis, se.getMessage());
//                    }
                }else if(e.getColumn()==colModel.getColumnIndex("Qty")||e.getColumn()==colModel.getColumnIndex("Harga")||e.getColumn()==colModel.getColumnIndex("Disc")||e.getColumn()==colModel.getColumnIndex("PPN")){
//                    double extPrice=fn.udfGetDouble(tblItem.getValueAt(iRow, colModel.getColumnIndex("Qty")))*
//                            fn.udfGetDouble(tblItem.getValueAt(iRow, colModel.getColumnIndex("Harga")));
//                    extPrice=extPrice-(extPrice/100)* fn.udfGetDouble(tblItem.getValueAt(iRow, colModel.getColumnIndex("Disc %")));
//                    tblItem.setValueAt(extPrice, iRow, colModel.getColumnIndex("Sub Total"));

                }

                if(tblItem.getRowCount()>0){
                    double totLine=0, totVat=0, extPrice=0;
                    TableColumnModel col=tblItem.getColumnModel();

                    for(int i=0; i< tblItem.getRowCount(); i++){
//                        if(e.getType()==TableModelEvent.DELETE && !sudahGR)
//                            ((DefaultTableModel)tblPO.getModel()).setValueAt(i+1, i, col.getColumnIndex("No."));
                        extPrice=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Sub Total")));
                        totLine+=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Sub Total")));
                        //totVat+=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Sub Total")))/100*fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPn %")));
                        totVat+=(Boolean)tblItem.getValueAt(i, col.getColumnIndex("PPn(Rp)"))==true ? fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN"))): extPrice/100*fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN")));
                    }

                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totLine+totVat-fn.udfGetDouble(txtDiscRp.getText()))));

                }else{
                    txtTotalLine.setText("0");
                    txtTotVat.setText("0");
                    txtDiscRp.setText("0");
                    txtDiscPersen.setText("0");
                    txtNetto.setText("0");
                }
            }
        });
        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
    }

    private void udfLoadKonversi(String sUnit) {
        //int row=table.getSelectedRow();
        if(cmbSatuan.getSelectedIndex()<0) return;
        try {
            String sQry = "select case  when '" + sUnit + "'=unit then 1 " +
                          "             when '" + sUnit + "'=unit2 then coalesce(konv2,1) " +
                          "             when '" + sUnit + "'=unit3 then coalesce(konv3,1) " +
                          "             else 1 end as konv," +
                          "coalesce(s.price,0) as unit_price, " +
                          "coalesce(s.disc,0) as disc, " +
                          "coalesce(s.vat,0) as vat, " +
                          "coalesce(s.is_disc_rp, false) as is_disc_rp, " +
                          "coalesce(s.is_tax_rp, false) as is_tax_rp  " +
                          "from r_item i " +
                          "left join r_item_supplier s on s.kode_item=i.kode_item and coalesce(s.uom_alt,'')='"+sUnit+"' " +
                          "where i.kode_item='" + txtKode.getText() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                lblKonv.setText(rs.getString("konv"));
                txtHarga.setText(fn.dFmt.format(rs.getDouble("unit_price")));
                txtDisc.setText(fn.dFmt.format(rs.getDouble("disc")));
                txtPPn.setText(fn.dFmt.format(rs.getDouble("vat")));
                chkDiscRp.setSelected(rs.getBoolean("is_disc_rp"));
                chkPPnRp.setSelected(rs.getBoolean("is_tax_rp"));
            } else {
                lblKonv.setText("1");
                txtHarga.setText("0");
                txtDisc.setText("0");
                txtPPn.setText("0");
                chkDiscRp.setSelected(false);
                chkPPnRp.setSelected(false);
                lblSubTotal.setText("0");
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

   public void setConn(Connection con){
       this.conn=con;
       fn.setConn(conn);
   }

    private void udfNew() {
        txtNoPO.setText("");
        txtKodeSupp.setText(""); lblNamaSupp.setText("");
        txtKeterangan.setText("");
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);   btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        txtKodeSupp.requestFocus();
    }

    private void udfInitForm(){
        lookupItem.setConn(conn);

        tblItem.getColumn("ProductID").setPreferredWidth(txtKode.getWidth());
        tblItem.getColumn("Nama Barang").setPreferredWidth(lblItem.getWidth());
        tblItem.getColumn("Satuan").setPreferredWidth(cmbSatuan.getWidth());
        tblItem.getColumn("Qty").setPreferredWidth(txtQty.getWidth());
        tblItem.getColumn("Harga").setPreferredWidth(txtHarga.getWidth());
        tblItem.getColumn("Disc").setPreferredWidth(txtDisc.getWidth());
        tblItem.getColumn("PPN").setPreferredWidth(txtPPn.getWidth());
        tblItem.getColumn("Sub Total").setPreferredWidth(lblSubTotal.getWidth());
        tblItem.getColumn("Konv").setPreferredWidth(lblKonv.getWidth());
        tblItem.setRowHeight(22);

        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
        jFTanggal.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs = conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy') as tgl2 ");
            if(rs.next()){
                jFTanggal.setText(rs.getString(1));
                jFTanggal.setValue(rs.getString(1));
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        
        txtNoPO.setEnabled(isKoreksi);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                if(isKoreksi){
                    if(txtNoPO.getText().length()>0)
                        udfLoadKoreksiPO();
                    txtNoPO.requestFocusInWindow();
                }else{
                    udfNew();
                    txtKodeSupp.requestFocusInWindow();
                }
            }
        });



    }

    public void setFlagKoreksi(boolean b) {
        this.isKoreksi=b;
    }

    private void udfLoadKoreksiPO(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select po.no_po, po.kode_supp, coalesce(s.nama_supp,'') as nama_supp, coalesce(s.alamat_1,'')||' - '||coalesce(s.telepon,'')  as alamat_telp, " +
                    "coalesce(po.catatan,'') as catatan,  to_char(po.tanggal, 'dd/MM/yyyy') as tgl_po, coalesce(po.top,0) as top, " +
                    "to_char(po.tanggal+coalesce(po.top,0), 'dd/MM/yyyy') as tgl_jt_tempo, coalesce(buyer,'') as buyer " +
                    "from r_po po " +
                    "inner join r_supplier s on s.kode_supp=po.kode_supp " +
                    "where upper(po.no_po)='"+txtNoPO.getText().trim().toUpperCase()+"'");
            if(rs.next()){
                txtNoPO.setText(rs.getString("no_po"));
                txtKodeSupp.setText(rs.getString("kode_supp"));
                lblNamaSupp.setText(rs.getString("nama_supp"));
                txtKeterangan.setText(rs.getString("catatan"));
                jFTanggal.setText(rs.getString("tgl_po"));
                txtTop.setText(rs.getString("top"));
                jFJtTempo.setText(rs.getString("tgl_jt_tempo"));
                txtBuyer.setText(rs.getString("buyer"));
            }else{
                JOptionPane.showMessageDialog(this, "PO tidak ditemukan! Silakan masukkan nomor PO yang benar!");
                udfNew();
                if(!txtNoPO.isFocusOwner())
                    txtNoPO.requestFocus();
                rs.close();
                return;
            }
            
            rs.close();
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);

            rs=conn.createStatement().executeQuery("select d.kode_item, coalesce(i.nama_item,'') as nama_item, coalesce(d.qty,0) as qty, coalesce(d.unit,'') as sat_po, " +
                    "coalesce(d.unit_price, 0) as harga_sat, coalesce(d.disc,0) as disc, coalesce(d.tax,0) as tax, " +
                    "(coalesce(d.qty,0)*coalesce(d.unit_price, 0)) *(1- coalesce(d.disc,0)/100) as sub_total, coalesce(d.konv,1) as konv," +
                    "coalesce(d.is_disc_rp, false) as is_disc_rp, coalesce(d.is_tax_rp, false) as is_tax_rp " +
                    "from r_po_detail d " +
                    "inner join r_item i on i.kode_item=d.kode_item " +
                    "where d.no_po='"+txtNoPO.getText().trim().toUpperCase()+"' " +
                    "order by d.serial_no");
            while(rs.next())
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("sat_po"),
                    rs.getDouble("qty"),
                    rs.getDouble("harga_sat"),
                    rs.getDouble("disc"),
                    rs.getDouble("tax"),
                    rs.getDouble("sub_total"),
                    rs.getDouble("konv"),
                    rs.getBoolean("is_disc_rp"),
                    rs.getBoolean("is_tax_rp")
                });

            rs.close();

            if(tblItem.getRowCount()>0 ){
                //tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
                tblItem.setRowSelectionInterval(0, 0);
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public void setNoPO(String sNoPo) {
        txtNoPO.setText(sNoPo);
    }

    private String udfLoadItem(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "case  when s.uom_alt=i.unit2 then i.unit2 " +
                         "      when s.uom_alt=i.unit3 then i.unit2 else coalesce(i.unit,'') end as uom_beli, " +
                         "case  when s.uom_alt=i.unit2 then coalesce(i.konv2,1) " +
                         "      when s.uom_alt=i.unit3 then coalesce(i.konv3,1) else 1 end as konv_beli, " +
                         "coalesce(s.price,0) as unit_price, coalesce(s.disc,0) as disc, coalesce(s.vat,0) as vat, " +
                         "coalesce(s.is_disc_rp, false) as is_disc_rp, coalesce(s.is_tax_rp, false) as is_tax_rp " +
                         "from r_item i " +
                         "left join r_item_supplier s on s.kode_item=i.kode_item and s.kode_supp='"+txtKodeSupp.getText()+"' " +
                         "where i.kode_item||coalesce(i.barcode,'')='"+txtKode.getText()+"' " +
                         "order by coalesce(s.convertion,0)  desc limit 1";

                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     lblItem.setText(rs.getString("nama_item"));
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                     cmbSatuan.setSelectedItem(rs.getString("uom_beli"));
                     lblKonv.setText(rs.getString("konv_beli"));
                     txtHarga.setText(fn.dFmt.format(rs.getDouble("unit_price")));
                     chkDiscRp.setSelected(rs.getBoolean("is_disc_rp"));
                     txtDisc.setText(fn.dFmt.format(rs.getDouble("disc")));
                     chkDiscRp.setSelected(rs.getBoolean("is_tax_rp"));
                     txtPPn.setText(fn.dFmt.format(rs.getDouble("vat")));
                     txtQty.setText("1");
                     udfLoadKonversi(cmbSatuan.getSelectedItem().toString());
                     udfSetSubTotalItem();

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

    private void udfSetSubTotalItem(){
        double subTotal=fn.udfGetDouble(txtQty.getText())*fn.udfGetDouble(txtHarga.getText());
        subTotal=chkDiscRp.isSelected()? subTotal-fn.udfGetDouble(txtDisc.getText()): subTotal*(1-fn.udfGetDouble(txtDisc.getText())/100);
        //subTotal=chkPPnRp.isSelected()? subTotal*(1+fn.udfGetDouble(txtPPn.getText())/100): subTotal+fn.udfGetDouble(txtPPn.getText());
        lblSubTotal.setText(fn.dFmt.format(subTotal));
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(txtHarga)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtDisc)||evt.getSource().equals(txtPPn))
                udfSetSubTotalItem();
        }

        @Override
        public void keyTyped(KeyEvent evt){
//            if(evt.getSource().equals(txtNamaPasien) && txtNoReg.getText().length()>0)
//                evt.consume();
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_INSERT:{
                    if(txtKodeSupp.getText().trim().length()==0){
                        JOptionPane.showMessageDialog(aThis, "Masukkan supplier terlebih dulu!");
                        if(!txtKodeSupp.isFocusOwner()){
                            txtKodeSupp.requestFocus();
                            return;
                        }
                    }
                    if(tblItem.getCellEditor()!=null && evt.getSource().equals(tblItem))
                        tblItem.getCellEditor().stopCellEditing();

                        lookupItem.setAlwaysOnTop(true);
                        lookupItem.setSrcTable(tblItem, tblItem.getColumnModel().getColumnIndex("Qty"));
                        lookupItem.setKeyEvent(evt);
                        lookupItem.setObjForm(this);
                        lookupItem.clearText();
                        lookupItem.setVisible(true);

                        lookupItem.requestFocusInWindow();
                        if(lookupItem.getKodeBarang()!=null && lookupItem.getKodeBarang().length()>0){
                            txtKode.setText(lookupItem.getKodeBarang());
//                            String sMsg=udfLoadItem();
                            //txtQty.requestFocus();
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
                    int iRow=tblItem.getSelectedRow();
                    if(iRow < 0) return;
                    stItemUpd=true;
                    TableColumnModel col=tblItem.getColumnModel();
                    txtKode.setText(tblItem.getValueAt(iRow, col.getColumnIndex("ProductID")).toString());
                    udfLoadComboKonversi();
                    lblItem.setText(tblItem.getValueAt(iRow, col.getColumnIndex("Nama Barang")).toString());
                    cmbSatuan.setSelectedItem(tblItem.getValueAt(iRow, col.getColumnIndex("Satuan")).toString());
                    txtQty.setText(fn.intFmt.format(fn.udfGetInt(tblItem.getValueAt(iRow, col.getColumnIndex("Qty")))));
                    txtHarga.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Harga")))));
                    txtDisc.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Disc")))));
                    txtPPn.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("PPN")))));
                    lblSubTotal.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Sub Total")))));
                    chkDiscRp.setSelected((Boolean)tblItem.getValueAt(iRow, col.getColumnIndex("Disc(Rp)")));
                    chkPPnRp.setSelected((Boolean)tblItem.getValueAt(iRow, col.getColumnIndex("PPn(Rp)")));
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
                        if(txtPPn.isFocusOwner()){
                            udfAddItemToTable();
                            return;
                        }
                        if (!fn.isListVisible()){
//                            if(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getText()!=null
//                               && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")){
//                                if(tblItem.getSelectedColumn()==0){
//                                    //tblItem.setValueAt(((JTextField)evt.getSource()).getText(), tblItem.getSelectedRow(), 0);
//                                    //tblItem.changeSelection(tblItem.getSelectedRow(), 2, false, false);
//                                    //tblItem.setColumnSelectionInterval(2, 2);
//                                }
//                            }

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
                    }else{

                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(ct instanceof JTable){
//                        if(((JTable)ct).getSelectedRow()==0){
//                            Component c = findNextFocus();
//                            if (c==null) return;
//                            if(c.isEnabled())
//                                c.requestFocus();
//                            else{
//                                c = findNextFocus();
//                                if (c!=null) c.requestFocus();;
//                            }
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
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
                            c = findNextFocus();
                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }
                case KeyEvent.VK_LEFT:{
                    if(tblItem.getSelectedColumn()==2)
                        tblItem.setColumnSelectionInterval(0, 0);
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                        int iRow[]= tblItem.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= tblItem.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(tblItem.convertRowIndexToModel(iRow[0]));
                            iRow = tblItem.getSelectedRows();
                        }
                        tblItem.clearSelection();

                        if(tblItem.getRowCount()>0 && rowPalingAtas<tblItem.getRowCount()){
                            tblItem.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(tblItem.getRowCount()>0)
                                tblItem.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                tblItem.requestFocus();
                        }
                        if(tblItem.getSelectedRow()>=0)
                            tblItem.changeSelection(tblItem.getSelectedRow(), 0, false, false);

                        if(tblItem.getCellEditor()!=null)
                            tblItem.getCellEditor().stopCellEditing();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(!cEditor.isVisible() && JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
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

    public void addEmptyRow() {
         ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{"", "", 1, "", 1});
         ((DefaultTableModel)tblItem.getModel()).fireTableRowsInserted(tblItem.getRowCount() - 1,tblItem.getRowCount() - 1);
         tblItem.requestFocus();
         tblItem.changeSelection(tblItem.getRowCount()-1, 0, false, false);
     }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= ustTextField;

        int col, row;
        public Component getTableCellEditorComponent(JTable tblDetail, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;
            text=ustTextField;
            text.setName("textEditor");

            text.addKeyListener(kListener);

           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.setFont(tblDetail.getFont());
           text.setVisible(!lookupItem.isVisible());
            if(lookupItem.isVisible()){
                return null;
            }
            text.setText(value==null? "": value.toString());

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
               text.setText(fn.dFmt.format(value));
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                if(tblItem.getSelectedColumn()==0){
                    retVal = ((JTextField)text).getText();

                }
                else
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

        public boolean isVisible(){
            return text.isVisible();
        }
    }

    JTextField ustTextField = new JTextField() {
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

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null)
                        ){
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

                if(e.getSource().equals(jFTanggal)||e.getSource().equals(txtTop)){
                    setDueDate();
                }else if(e.getSource().equals(txtKodeSupp)){
                    udfLoadSupplier();
                }else if(e.getSource().equals(txtNoPO)){
                    udfLoadKoreksiPO();
                }
            } 

        }
    } ;

    private void udfLoadSupplier(){
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select coalesce(s.nama_supp,'') as nama, coalesce(alamat_1,'') as alamat, coalesce(k.nama_kota,'') as kota, " +
                    "coalesce(t.jatuh_tempo,0) as jt_tempo " +
                    "from r_supplier  s " +
                    "left join m_termin t on s.termin=t.kode " +
                    "left join m_kota k on k.kode_kota=s.kota " +
                    "where s.kode_supp='"+txtKodeSupp.getText()+"'");
            if(rs.next()){
                lblNamaSupp.setText(rs.getString("nama"));
                lblAlamatSupp.setText(rs.getString("alamat")+" "+rs.getString("kota"));
                txtTop.setText(rs.getString("jt_tempo"));
                setDueDate();
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private Date getDueDate(Date d, int i){
        Date dueDate = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void setDueDate(){
        try {
            jFJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                    getDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(jFTanggal.getText()),
                    fn.udfGetInt(txtTop.getText()))));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private boolean udfCekBeforeSave(){
        if(txtKodeSupp.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi supplier terlebih dulu!");
            txtKodeSupp.requestFocus();
            return false;
        }
        if(fn.udfGetInt(txtTop.getText())==0){
            if(JOptionPane.showConfirmDialog(this, "TOP Masih Nol anda tetap melanjutkan?!", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                txtTop.requestFocus();
                return false;
            }
        }
        if(!isKoreksi && tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item PO masih kosong!");
            tblItem.requestFocus();
            return false;
        }

        return true;
    }
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    private void udfSave(){
//'"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText())+"'
        if(!udfCekBeforeSave()) return;
         String sSql="", sMessage="";
         ResultSet rs=null;
         try{

             if(isKoreksi){
                 if(tblItem.getRowCount()>0){
                     rs=conn.createStatement().executeQuery("select fn_r_po_koreksi('"+txtNoPO.getText()+"', '"+MainForm.sUserName+"', " +
                             "'"+ymd.format(dmy.parse(jFTanggal.getText()))+"')");
                     rs.next();
                     rs.close();

                     sSql=  "update r_po set " +
                            "kode_supp='"+txtKodeSupp.getText()+"'," +
                            "disc_persen   ="+fn.udfGetDouble(txtDiscPersen.getText())+", " +
                            "disc_rp    ="+fn.udfGetDouble(txtDiscRp.getText())+", " +
                            "ppn        ="+fn.udfGetDouble(txtTotVat.getText())+"," +
                            "top        ="+txtTop.getText()+"," +
                            "catatan     ='"+txtKeterangan.getText()+"'," +
                            "buyer      ='"+txtBuyer.getText()+"', " +
                            "user_upd   ='"+MainForm.sUserName+"', " +
                            "time_upd   =now() " +
                            "where no_po='"+txtNoPO.getText()+"';";

                     sSql+="delete from r_po_detail where no_po='"+txtNoPO.getText()+"';";

                     TableColumnModel col=tblItem.getColumnModel();
                     for(int i=0; i< tblItem.getRowCount(); i++){
                       sSql+=  "INSERT INTO r_po_detail(no_po, kode_item, qty, unit, unit_price, disc, tax, " +
                                "konv) values('"+txtNoPO.getText()+"', " +
                                "'"+tblItem.getValueAt(i, col.getColumnIndex("ProductID"))+"', " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                                "'"+tblItem.getValueAt(i, col.getColumnIndex("Satuan"))+"', " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")))+", " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc")))+", " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPn")))+", " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Konv")))+"  " +
                                " " +
                                ");";
                     }

                     //System.out.println(sSql);

                     conn.setAutoCommit(false);
                     int i=conn.createStatement().executeUpdate(sSql);
                     conn.setAutoCommit(true);
                     if(JOptionPane.showConfirmDialog(this, "Koreksi PO sukses! Selanjutnya akan Cetak PO?", "Cetak PO?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        previewPO(txtNoPO.getText());
                     }
//                     if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
//                         ((FrmPRMaintenance)srcFrom).udfLoadPR(0);

                     dispose();
                     return;
                 }else{
                     if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan PO '"+txtNoPO.getText()+"' ini?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
                        return;
                     }

                     conn.setAutoCommit(false);
                     int i=conn.createStatement().executeUpdate("Update r_po set flag_trx='K', " +
                             "user_upd   ='"+MainForm.sUserName+"', " +
                             "date_upd   =now() " +
                             "where no_po='"+txtNoPO.getText()+"'; ");
                     conn.setAutoCommit(true);
                     JOptionPane.showMessageDialog(this, "Pembatalan PO sukses!");

//                     if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
//                         ((FrmPRMaintenance)srcFrom).udfLoadPR(0);

                     dispose();
                     return;
                 }
             }

             if(tblItem.getRowCount()>0){
                 sSql="select fn_r_get_no_po('"+ymd.format(dmy.parse(jFTanggal.getText()))+"') as no_po";
                 rs=conn.createStatement().executeQuery(sSql);
                 if(rs.next())
                     txtNoPO.setText(rs.getString(1));

                 sSql="INSERT INTO r_po(no_po, kode_supp, tanggal, catatan, disc_persen, disc_rp, " +
                     "date_ins, user_ins, flag_trx, buyer, top) " +
                     "values('"+txtNoPO.getText()+"', '"+txtKodeSupp.getText()+"', " +
                     "now(), '"+txtKeterangan.getText()+"', " +
                     ""+fn.udfGetDouble(txtDiscRp.getText())+", "+fn.udfGetDouble(txtTotVat.getText())+"," +
                     "now(), '"+MainForm.sUserName+"', 'T', '"+txtBuyer.getText()+"', "+fn.udfGetInt(txtTop.getText())+"); ";

                 TableColumnModel col=tblItem.getColumnModel();
                     for(int i=0; i< tblItem.getRowCount(); i++){
                        sSql+=  "INSERT INTO r_po_detail(no_po, kode_item, qty, unit, unit_price, disc, tax, " +
                                "konv, is_disc_rp, is_tax_rp) values('"+txtNoPO.getText()+"', " +
                                "'"+tblItem.getValueAt(i, col.getColumnIndex("ProductID"))+"', " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                                "'"+tblItem.getValueAt(i, col.getColumnIndex("Satuan"))+"', " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")))+", " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc")))+", " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN")))+", " +
                                ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Konv")))+",  " +
                                (Boolean)tblItem.getValueAt(i, col.getColumnIndex("Disc(Rp)"))+ ", " +
                                (Boolean)tblItem.getValueAt(i, col.getColumnIndex("PPn(Rp)"))+ " " +
                                ");";
                     }

                 //System.out.println(sSql);
                 conn.setAutoCommit(false);

                 int i=conn.createStatement().executeUpdate(sSql);
                 conn.setAutoCommit(true);
                 if(JOptionPane.showConfirmDialog(this, "Simpan PO sukses! Selanjutnya akan Cetak PO?", "Cetak PO?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    previewPO(txtNoPO.getText());
                 }
                 udfNew();

                 //printKwitansi(txtNoPO.getText(), false);

//                 if(srcFrom!=null && srcFrom instanceof FrmPRMaintenance)
//                     ((FrmPRMaintenance)srcFrom).udfLoadPR(0);

                 if(isKoreksi) dispose();
             }
             

         } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }catch(SQLException se){

            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(this, ex1.getMessage());
            }
         }

    }

    private void previewPO(String sNo_PO){
      try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/PO.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("corporate", MainForm.sNamaUsaha);
            parameter.put("alamat", MainForm.sAlamat);
            parameter.put("telp", MainForm.sTelp);
            parameter.put("no_po",sNo_PO);
            JasperPrint jasperPrintmkel = JasperFillManager.fillReport(jasperReportmkel, parameter, conn);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JasperViewer.viewReport(jasperPrintmkel, false);
//            if(!jasperPrintmkel.getPages().isEmpty()){
//                ResultSet rs=conn.createStatement().executeQuery(
//                        "select * from fn_phar_po_update_status_print('"+sNo_PO+"', '"+MainForm.sUserName+"') as " +
//                        "(time_print timestamp without time zone, print_ke int)");
//                if(rs.next()){
//                    tblHeader.setValueAt(rs.getTimestamp(1), tblHeader.getSelectedRow(), tblHeader.getColumnModel().getColumnIndex("Last Print"));
//                }
//                rs.close();
//            }

      }catch(JRException je){
            System.out.println(je.getMessage());
      }
  }

    private void udfAddItemToTable(){
        if(lblItem.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan item terlebih dulu!");
            txtKode.requestFocusInWindow();
            return;
        }
        if(fn.udfGetDouble(txtQty.getText())==0){
            JOptionPane.showMessageDialog(this, "Masukkan Qty lebih dari 0!");
            txtQty.requestFocusInWindow();
            return;
        }
        TableColumnModel col=tblItem.getColumnModel();
        if(stItemUpd){
            int iRow=tblItem.getSelectedRow();
            if(iRow<0) return;
            tblItem.setValueAt(txtKode.getText(), iRow, col.getColumnIndex("ProductID"));
            tblItem.setValueAt(lblItem.getText(), iRow, col.getColumnIndex("Nama Barang"));
            tblItem.setValueAt(cmbSatuan.getSelectedItem().toString(), iRow, col.getColumnIndex("Satuan"));
            tblItem.setValueAt(fn.udfGetInt(txtQty.getText()), iRow, col.getColumnIndex("Qty"));
            tblItem.setValueAt(fn.udfGetDouble(txtHarga.getText()), iRow, col.getColumnIndex("Harga"));
            tblItem.setValueAt(fn.udfGetDouble(txtDisc.getText()), iRow, col.getColumnIndex("Disc"));
            tblItem.setValueAt(fn.udfGetDouble(txtPPn.getText()), iRow, col.getColumnIndex("PPN"));
            tblItem.setValueAt(chkDiscRp.isSelected(), iRow, col.getColumnIndex("Disc(Rp)"));
            tblItem.setValueAt(chkPPnRp.isSelected(), iRow, col.getColumnIndex("PPn(Rp)"));
            tblItem.setValueAt(fn.udfGetDouble(lblSubTotal.getText()), iRow, col.getColumnIndex("Sub Total"));
            tblItem.setValueAt(fn.udfGetInt(lblKonv.getText()), iRow, col.getColumnIndex("Konv"));
        }else{
            String sUnit="";
            for(int i=0; i<tblItem.getRowCount(); i++){
                sUnit=cmbSatuan.getSelectedIndex()<0? "": cmbSatuan.getSelectedItem().toString();
                if(tblItem.getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) &&
                        tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(sUnit) ){
                    JOptionPane.showMessageDialog(this, ("Item tersebut sudah dimasukkan pada baris ke "+(i+1)+".\nQty Item akan ditambahkan?"), "Item exists", JOptionPane.ERROR_MESSAGE);
                    return;
//                    else{
//                        double total=fn.udfGetInt(txtOnReceipt.getText())+fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("Qty")));
//                        table.setValueAt(total, i, col.getColumnIndex("Qty"));
//                        table.setValueAt(total*fn.udfGetDouble(lblSubTotal.getText()), i, col.getColumnIndex("Sub Total"));
//                        udfClearItem();
//                        txtKode.requestFocusInWindow();
//                        return;
//                    }
                }
            }
            ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                txtKode.getText(),
                lblItem.getText(),
                cmbSatuan.getSelectedItem().toString(),
                fn.udfGetDouble(txtQty.getText()),
                fn.udfGetDouble(txtHarga.getText()),
                fn.udfGetDouble(txtDisc.getText()),
                fn.udfGetDouble(txtPPn.getText()),
                fn.udfGetDouble(lblSubTotal.getText()),
                fn.udfGetInt(lblKonv.getText()),
                chkDiscRp.isSelected(),
                chkPPnRp.isSelected()
            });
        }
        tblItem.setRowSelectionInterval(((DefaultTableModel)tblItem.getModel()).getRowCount()-1, ((DefaultTableModel)tblItem.getModel()).getRowCount()-1);
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
        txtDisc.setText("0");
        txtPPn.setText("0");
        lblSubTotal.setText("0");
        lblKonv.setText("1");
        stItemUpd=false;

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtNoPO = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtKodeSupp = new javax.swing.JTextField();
        lblNamaSupp = new javax.swing.JLabel();
        lblAlamatSupp = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jFTanggal = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtTop = new javax.swing.JTextField();
        jFJtTempo = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtBuyer = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtTotVat = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNetto = new javax.swing.JLabel();
        txtDiscRp = new javax.swing.JTextField();
        txtDiscPersen = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtQty = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        lblKonv = new javax.swing.JLabel();
        cmbSatuan = new javax.swing.JComboBox();
        txtKode = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        lblSubTotal = new javax.swing.JLabel();
        txtDisc = new javax.swing.JTextField();
        txtPPn = new javax.swing.JTextField();
        chkDiscRp = new javax.swing.JCheckBox();
        chkPPnRp = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pesanan Pembelian");
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

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
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

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setText("No. PO"); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 60, 21));

        txtNoPO.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoPO.setName("txtNoPO"); // NOI18N
        txtNoPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoPOFocusLost(evt);
            }
        });
        jPanel1.add(txtNoPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 130, 21));

        jToolBar1.add(jPanel1);

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKodeSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKodeSupp.setName("txtKodeSupp"); // NOI18N
        txtKodeSupp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtKodeSuppFocusLost(evt);
            }
        });
        txtKodeSupp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeSuppKeyReleased(evt);
            }
        });
        jPanel2.add(txtKodeSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 80, 21));

        lblNamaSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblNamaSupp.setName("lblNamaSupp"); // NOI18N
        jPanel2.add(lblNamaSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 300, 21));

        lblAlamatSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblAlamatSupp.setName("lblAlamatSupp"); // NOI18N
        jPanel2.add(lblAlamatSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 35, 380, 21));

        jLabel9.setText("Keterangan"); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 100, 21));

        jLabel17.setText("Supplier"); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 21));

        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jPanel2.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 380, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText("Tanggal");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 70, 20));

        jFTanggal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFTanggal.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFTanggal.setName("jFTanggal"); // NOI18N
        jPanel2.add(jFTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 100, 20));

        jLabel10.setText(" Hari"); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, 60, 21));

        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTop.setName("txtTop"); // NOI18N
        jPanel2.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 30, 40, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFJtTempo.setName("jFJtTempo"); // NOI18N
        jPanel2.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 50, 100, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel4.setText("Jt. Tempo");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, 70, 20));

        jLabel11.setText("T.O.P"); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 70, 21));

        jLabel12.setText("Pembeli"); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 70, 70, 21));

        txtBuyer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBuyer.setName("txtBuyer"); // NOI18N
        jPanel2.add(txtBuyer, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 70, 120, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 842, 100));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Satuan", "Qty", "Harga", "Disc", "PPN", "Sub Total", "Konv", "Disc(Rp)", "PPn(Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class
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
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.setName("tblItem"); // NOI18N
        jScrollPane1.setViewportView(tblItem);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 840, 150));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.setName("txtTotalLine"); // NOI18N
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel3.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 5, 120, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Line Total :");
        jLabel23.setName("jLabel23"); // NOI18N
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 5, 90, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Discount");
        jLabel26.setName("jLabel26"); // NOI18N
        jPanel3.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 30, 50, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("PPn");
        jLabel27.setName("jLabel27"); // NOI18N
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 55, 90, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.setName("txtTotVat"); // NOI18N
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel3.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 55, 120, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Netto");
        jLabel28.setName("jLabel28"); // NOI18N
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, 90, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12));
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.setName("txtNetto"); // NOI18N
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel3.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 80, 120, 20));

        txtDiscRp.setFont(new java.awt.Font("Dialog", 1, 12));
        txtDiscRp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscRp.setText("0");
        txtDiscRp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDiscRp.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiscRp.setName("txtDiscRp"); // NOI18N
        txtDiscRp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscRpKeyTyped(evt);
            }
        });
        jPanel3.add(txtDiscRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 120, 20));

        txtDiscPersen.setFont(new java.awt.Font("Dialog", 1, 12));
        txtDiscPersen.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDiscPersen.setText("0");
        txtDiscPersen.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDiscPersen.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiscPersen.setName("txtDiscPersen"); // NOI18N
        txtDiscPersen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDiscPersenKeyTyped(evt);
            }
        });
        jPanel3.add(txtDiscPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 30, 40, 20));

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("<html>\n &nbsp <b>F4  &nbsp &nbsp    : </b> Membuat PO baru <br> \n &nbsp <b>F5 &nbsp &nbsp : </b>  Menyimpan PO <br>\n &nbsp <b>Insert : </b> Menambah Item PR\n</html>"); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setName("jLabel1"); // NOI18N
        jLabel1.setOpaque(true);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 290, 90));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 840, 110));

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtQty.setName("txtQty"); // NOI18N
        jPanel4.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 50, 20));

        lblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblItem.setName("lblItem"); // NOI18N
        jPanel4.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 220, 20));

        lblKonv.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblKonv.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv.setName("lblKonv"); // NOI18N
        jPanel4.add(lblKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 40, 20));

        cmbSatuan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        cmbSatuan.setName("cmbSatuan"); // NOI18N
        jPanel4.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 70, 20));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setName("txtKode"); // NOI18N
        jPanel4.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 120, 20));

        txtHarga.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga.setName("txtHarga"); // NOI18N
        jPanel4.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 70, 20));

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSubTotal.setName("lblSubTotal"); // NOI18N
        jPanel4.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 20, 110, 20));

        txtDisc.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtDisc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtDisc.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDisc.setName("txtDisc"); // NOI18N
        jPanel4.add(txtDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 20, 80, 20));

        txtPPn.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtPPn.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPPn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPPn.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPPn.setName("txtPPn"); // NOI18N
        jPanel4.add(txtPPn, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, 80, 20));

        chkDiscRp.setText("(Rp.)");
        chkDiscRp.setName("chkDiscRp"); // NOI18N
        chkDiscRp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkDiscRpItemStateChanged(evt);
            }
        });
        jPanel4.add(chkDiscRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 2, 60, 18));

        chkPPnRp.setText(" (Rp.)");
        chkPPnRp.setName("chkPPnRp"); // NOI18N
        chkPPnRp.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPPnRpItemStateChanged(evt);
            }
        });
        jPanel4.add(chkPPnRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 2, 60, 18));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 842, 40));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-872)/2, (screenSize.height-506)/2, 872, 506);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        //printKwitansi(txtNoPO.getText(), false);
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Revision")>0) dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtKodeSuppFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKodeSuppFocusLost
        //        if(!lst.isVisible() && !txtNoAnggota.getText().equalsIgnoreCase("") && isNew)
        //            txtPinjamanKe.setText(dFmt.format(getPinjamanKe()));
}//GEN-LAST:event_txtKodeSuppFocusLost

    private void txtKodeSuppKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeSuppKeyReleased
        String sQry="select kode_supp as Kode, coalesce(nama_supp,'') as Supplier, " +
                    "coalesce(alamat_1,'')||' '||coalesce(nama_kota,'') as alamat  " +
                    "from r_supplier s " +
                    "left join m_kota k on s.kota=k.kode_kota " +
                    "where (kode_supp||coalesce(nama_supp,'')||coalesce(nama_kota,'')) " +
                    "iLike '%" + txtKodeSupp.getText() + "%' order by coalesce(nama_supp,'') ";
       fn.lookup(evt, new Object[]{lblNamaSupp, lblAlamatSupp}, sQry, txtKodeSupp.getWidth()+lblNamaSupp.getWidth()+20, 140);
                        
}//GEN-LAST:event_txtKodeSuppKeyReleased

    private void txtNoPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoPOFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoPOFocusLost

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNettoPropertyChange

    private void txtDiscRpKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscRpKeyTyped
        fn.keyTyped(evt);
}//GEN-LAST:event_txtDiscRpKeyTyped

    private void txtDiscPersenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscPersenKeyTyped
        fn.keyTyped(evt);
}//GEN-LAST:event_txtDiscPersenKeyTyped

    private void chkDiscRpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkDiscRpItemStateChanged
        udfSetSubTotalItem();
    }//GEN-LAST:event_chkDiscRpItemStateChanged

    private void chkPPnRpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPPnRpItemStateChanged
        udfSetSubTotalItem();
    }//GEN-LAST:event_chkPPnRpItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkDiscRp;
    private javax.swing.JCheckBox chkPPnRp;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JFormattedTextField jFJtTempo;
    private javax.swing.JFormattedTextField jFTanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblAlamatSupp;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblKonv;
    private javax.swing.JLabel lblNamaSupp;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtBuyer;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtDiscPersen;
    private javax.swing.JTextField txtDiscRp;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKodeSupp;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtNoPO;
    private javax.swing.JTextField txtPPn;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtTop;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables

}
