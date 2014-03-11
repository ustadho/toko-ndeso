/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmPO.java
 *
 * Created on Jul 12, 2010, 11:06:18 AM
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmGRReturn extends javax.swing.JInternalFrame {
    GeneralFunction fn;
    private Connection conn;
    MyKeyListener kListener=new MyKeyListener();
    TableColumnModel col=null;
    private boolean isKoreksi=false;
    private JComboBox cmbSatuan=new JComboBox();
    private Component aThis;
    private String sOldDeliveryNo="";

    /** Creates new form FrmPO */
    public FrmGRReturn() {
        initComponents();
        AutoCompleteDecorator.decorate(cmbSatuan);
        col=tblItem.getColumnModel();
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Qty")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Expired")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Harga Sat")).setCellEditor(cEditor);
        //tblItem.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
//        tblItem.getColumn("Urut").setMinWidth(0);       tblItem.getColumn("Urut").setMaxWidth(0);       tblItem.getColumn("Urut").setPreferredWidth(0);
        tblItem.getColumn("Sisa").setMinWidth(0);  tblItem.getColumn("Sisa").setMaxWidth(0);  tblItem.getColumn("Sisa").setPreferredWidth(0);
        tblItem.getColumn("Keterangan").setPreferredWidth(250);
        tblItem.getColumn("Konv").setPreferredWidth(60);
        tblItem.getColumn("Satuan").setPreferredWidth(70);

        tblItem.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        tblItem.setRowHeight(22);
        tblItem.setSurrendersFocusOnKeystroke(true);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(conn==null) return;
                int iRow=tblItem.getSelectedRow();
                if(iRow<0){
                    txtQtyOnHand.setText("0");
                    
                }else{
                    udfLoadKetBawah();
                }
                udfLoadComboKonv(iRow);
            }
        });

//        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
//                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());
//
//            }
//        });

        tblItem.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int mColIndex=e.getColumn();
                if(mColIndex==0){
                    String sKodeBarang=tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString();

                    try{
                        String sQry="select i.kode_item, nama_item, coalesce(i.unit,'') as uom_kecil," +
                                "round( (coalesce(s.price,0)-(coalesce(s.price,0)/100*coalesce(s.disc,0))+(coalesce(s.price,0)/100*coalesce(s.vat,0)))/coalesce(s.convertion,1)) as harga_retur ," +
                                "coalesce(s.convertion,1) as konv " +
                                "from r_item i " +
                                "left join r_item_supplier s on s.kode_item=i.kode_item and s.kode_supp='"+txtSupplier.getText()+"'" +
                                "where i.kode_item='"+sKodeBarang+"' ";

                        //System.out.println(sQry);
                        
                        ResultSet rs=conn.createStatement().executeQuery(sQry);
                        if(rs.next()){
                            TableColumnModel col=tblItem.getColumnModel();
                            int iRow=tblItem.getSelectedRow();

                            ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("nama_item"), iRow, col.getColumnIndex("Keterangan"));
                            ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getString("uom_kecil"), iRow, col.getColumnIndex("Satuan"));
                            ((DefaultTableModel)tblItem.getModel()).setValueAt(rs.getDouble("harga_retur"), iRow, col.getColumnIndex("Harga Sat"));
                            ((DefaultTableModel)tblItem.getModel()).setValueAt(1, iRow, col.getColumnIndex("Konv"));
                            ((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Qty"));
                            //((DefaultTableModel)tblItem.getModel()).setValueAt(new Double(0), iRow, col.getColumnIndex("Ext. Price"));
                            ((DefaultTableModel)tblItem.getModel()).setValueAt("", iRow, col.getColumnIndex("Expired"));
                            udfLoadKetBawah();
                            udfLoadComboKonv(tblItem.getSelectedRow());
                        }
                        rs.close();
                    }catch(SQLException se){
                        System.err.println(se.getMessage());
                    }
                }else if(mColIndex==tblItem.getColumnModel().getColumnIndex("Qty")||mColIndex==tblItem.getColumnModel().getColumnIndex("Harga Sat"))
                    udfSubTotal();

                double dTotal=0;
                for(int i=0; i< tblItem.getRowCount(); i++){
                    dTotal+=fn.udfGetDouble(tblItem.getValueAt(i, tblItem.getColumnModel().getColumnIndex("Sub Total")));
                }
                lblTotal.setText(fn.dFmt.format(dTotal));
            }
        });

        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
            
        aThis=this;
    }

    private void udfSubTotal(){
        int i=tblItem.getSelectedRow();
        if(i>=0){
            TableColumnModel col=tblItem.getColumnModel();
            tblItem.setValueAt(fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))*fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga Sat"))),
                    i, col.getColumnIndex("Sub Total"));
        }
    }

    private void udfLoadComboKonv(int iRow){

        if(iRow<0) return;
        if(tblItem.getValueAt(iRow, 0)==null ||tblItem.getValueAt(iRow, 0).toString().equalsIgnoreCase(""))
            return;

        try{
            cmbSatuan.removeAllItems();
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual " +
                         "from r_item where kode_item='"+tblItem.getValueAt(iRow, 0).toString()+"'");
             if(rs.next()){
                 if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                 if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                 if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
             }else{

             }
             rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(aThis, se.getMessage());
        }
    }

    private void udfLoadKonversi(String sUnit) {
        int row=tblItem.getSelectedRow();
        if(row<0) return;
        try {
            String sQry = "select case  when '" + sUnit + "'=unit then 1 " +
                          "             when '" + sUnit + "'=unit2 then coalesce(konv2,1) " +
                          "             when '" + sUnit + "'=unit3 then coalesce(konv3,1) " +
                          "             else 1 end as konv " +
                          "from r_item i " +
                          "where i.kode_item='" + tblItem.getValueAt(row, 0).toString() + "'";

            System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                tblItem.setValueAt(rs.getInt("konv"), row, tblItem.getColumnModel().getColumnIndex("Konv"));
            } else {
                tblItem.setValueAt(1, row, tblItem.getColumnModel().getColumnIndex("Konv"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrxPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void udfLoadKetBawah(){
        TableColumnModel col=tblItem.getColumnModel();
        int iRow=tblItem.getSelectedRow();

        try{
            if(conn==null || tblItem.getValueAt(iRow, col.getColumnIndex("Product ID"))==null ) return;
            
            //txtDeliveryNo.setText(tblItem.getValueAt(iRow, col.getColumnIndex("Delivery#")).toString());
            String sKodeBarang=tblItem.getValueAt(iRow, col.getColumnIndex("Product ID")).toString();
            ResultSet rs=null;
            rs=conn.createStatement().executeQuery("select coalesce(saldo,0) as on_hand " +
                    "from r_kartu_stok " +
                    "where kode_item= '"+sKodeBarang+"' " +
                    "and kode_gudang='"+txtSiteFrom.getText()+"'" +
                    "order by serial_no desc limit 1 ");
            if(rs.next()){
                txtQtyOnHand.setText(fn.dFmt.format(rs.getDouble("on_hand")));
            }else{
                txtQtyOnHand.setText("0");
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(FrmGRReturn.this, se.getMessage());
        }
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        if(isKoreksi) jLabel16.setText("Revisi Retur Pembelian");
        txtReqBy.setText(MainForm.sUserName);
        txtSiteFrom.setText(MainForm.sKodeGudang);
        lblSiteFrom.setText(MainForm.sNamaGudang);

        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        lookupItem.setConn(conn);
        tblItem.addKeyListener(kListener);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        if(!isKoreksi)
            udfNew();

            Runnable doRun = new Runnable() {
                public void run() {
                    if(!isKoreksi)
                      txtSupplier.requestFocusInWindow();
                    else{
                      txtReturnNo.requestFocusInWindow();
                      btnNew.setEnabled(false);
                      btnSave.setEnabled(true);
                    }
                }
            };
            SwingUtilities.invokeLater(doRun);

    }

    private void udfLoadReturnOrder(){
        String s="select h.no_return_order, coalesce(h.kode_supplier,'') as kode_supp, coalesce(s.nama_supplier,'') as nama_supplier," +
                "coalesce(s.telp,'') as telp, coalesce(h.site_id,'') as site_id, coalesce(st.site_name,'') as site_name, " +
                "coalesce(h.shiping,'') as shiping, coalesce(h.cara_bayar,'0')::int as cara_bayar, to_char(h.tanggal, 'dd/MM/yyyy') as tanggal, " +
                "coalesce(h.top,0) as top, coalesce(h.closed, false) as closed, " +
                "to_char(due_date,'dd/MM/yyyy') as due_date, coalesce(currency,'') as curr, coalesce(h.kurs,1) as kurs, " +
                "trim(coalesce(acc_level_1 ,'')) as acc1, trim(coalesce(acc_level_2 ,'')) as acc2, trim(coalesce(acc_level_3 ,'')) as acc3," +
                "coalesce(delivery_no,'') as delivery_no " +
                "from phar_return_order h " +
                "left join phar_supplier s on s.kode_supplier=h.kode_supplier " +
                "left join phar_site st on st.site_id=h.site_id " +
                "where flag_trx='T' and h.no_return_order='"+txtReturnNo.getText()+"'";

        try{
            ResultSet rs=conn.createStatement().executeQuery(s);

            if(rs.next()){
                txtSupplier.setText(rs.getString("kode_supp"));
                lblSupplier.setText(rs.getString("nama_supplier"));
                lblTelepon.setText(rs.getString("telp"));
                txtSiteFrom.setText(rs.getString("site_id"));
                lblSiteFrom.setText(rs.getString("site_name"));
                txtShipping.setText(rs.getString("shiping"));
                cmbCaraBayar.setSelectedIndex(rs.getInt("cara_bayar"));
                txtDeliveryNo.setText(rs.getString("delivery_no"));

                rs.close();
                s="select * from fn_phar_return_order_sisa_detail('"+txtReturnNo.getText()+"') as (kode_barang varchar, " +
                        "nama_barang varchar, qty numeric, price numeric, expired text, uom varchar, konv numeric, " +
                        "urut integer, delivery_no varchar)";

                //System.out.println(s);
                
                ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(s);
                while(rs.next()){
                    ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("qty"),
                        rs.getDouble("price"),
                        rs.getString("expired"),
                        rs.getString("uom"),
                        rs.getDouble("konv"),
                        rs.getDouble("qty"),
                        rs.getInt("urut"),
                        rs.getString("delivery_no"),
                    });
                }

                if(tblItem.getRowCount()>0){
                    tblItem.setRowSelectionInterval(0, 0);
                    udfLoadKetBawah();
                }
                rs.close();
            }
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

    private void udfNew(){
        btnNew.setEnabled(false);
        btnSave.setEnabled(true);
        btnCancel.setText("Cancel");
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("image/Icon/Cancel.png")));
        if(!isKoreksi) txtReturnNo.setText("");
        txtReturnNo.setEnabled(!isKoreksi);
        txtSupplier.setText(""); lblSupplier.setText(""); lblTelepon.setText("");
        //txtSiteFrom.setText(""); lblSiteFrom.setText("");
        txtShipping.setText("");
        txtDeliveryNo.setText("");
        sOldDeliveryNo="";
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
        txtSupplier.requestFocus();
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
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        lblTelepon = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtShipping = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtReqBy = new javax.swing.JTextField();
        lblSupplier = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtSiteFrom = new javax.swing.JTextField();
        lblSiteFrom = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        cmbCaraBayar = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtReturnNo = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        cmbJenisRetur = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        txtDeliveryNo = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        txtQtyOnHand = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        txtRemarkGood = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Return Pembelian ke Supplier"); // NOI18N
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

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
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
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        btnCancel.setText("Exit");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail Return Order to Supplier"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Date");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 30, 70, 20));

        txtDate.setFont(new java.awt.Font("Dialog", 0, 12));
        txtDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDate.setEnabled(false);
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDateFocusLost(evt);
            }
        });
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDateKeyReleased(evt);
            }
        });
        jPanel1.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 120, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Supplier ID");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 80, 20));

        txtSupplier.setFont(new java.awt.Font("Dialog", 0, 12));
        txtSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupplier.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSupplier.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSupplierFocusLost(evt);
            }
        });
        txtSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSupplierKeyReleased(evt);
            }
        });
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 70, 20));

        lblTelepon.setFont(new java.awt.Font("Dialog", 0, 12));
        lblTelepon.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblTelepon.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblTeleponPropertyChange(evt);
            }
        });
        jPanel1.add(lblTelepon, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 270, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Shiping");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 80, 20));

        txtShipping.setFont(new java.awt.Font("Dialog", 0, 12));
        txtShipping.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtShipping.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtShipping.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtShippingFocusLost(evt);
            }
        });
        txtShipping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtShippingKeyReleased(evt);
            }
        });
        jPanel1.add(txtShipping, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, 320, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Return By");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 55, 70, 20));

        txtReqBy.setFont(new java.awt.Font("Dialog", 0, 12));
        txtReqBy.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReqBy.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReqBy.setEnabled(false);
        txtReqBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReqByFocusLost(evt);
            }
        });
        txtReqBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReqByKeyReleased(evt);
            }
        });
        jPanel1.add(txtReqBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 55, 120, 20));

        lblSupplier.setFont(new java.awt.Font("Dialog", 0, 12));
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupplier.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSupplierPropertyChange(evt);
            }
        });
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 330, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, 10, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 55, 10, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Site ID");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 80, 20));

        txtSiteFrom.setFont(new java.awt.Font("Dialog", 0, 12));
        txtSiteFrom.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSiteFrom.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSiteFrom.setEnabled(false);
        txtSiteFrom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSiteFromFocusLost(evt);
            }
        });
        txtSiteFrom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteFromKeyReleased(evt);
            }
        });
        jPanel1.add(txtSiteFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, 60, 20));

        lblSiteFrom.setFont(new java.awt.Font("Dialog", 0, 12));
        lblSiteFrom.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSiteFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSiteFromPropertyChange(evt);
            }
        });
        jPanel1.add(lblSiteFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 120, 260, 20));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText(":");
        jPanel1.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 10, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText(":");
        jPanel1.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 10, 20));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText(":");
        jPanel1.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 95, 10, 20));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Cara Bayar");
        jPanel1.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 105, 80, 20));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText(":");
        jPanel1.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 80, 10, 20));

        cmbCaraBayar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "KREDIT", "TUNAI" }));
        cmbCaraBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(cmbCaraBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 105, 110, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Return #");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 25, 90, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(":");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 25, 10, 20));

        txtReturnNo.setFont(new java.awt.Font("Dialog", 0, 12));
        txtReturnNo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReturnNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReturnNo.setEnabled(false);
        txtReturnNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReturnNoKeyReleased(evt);
            }
        });
        jPanel1.add(txtReturnNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 25, 130, 20));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Jenis Retur");
        jPanel1.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 80, 90, 20));

        cmbJenisRetur.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbJenisRetur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Potong Tagihan", "Tukar Barang" }));
        cmbJenisRetur.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(cmbJenisRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 80, 120, -1));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Receipt #");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 95, 90, 20));

        txtDeliveryNo.setFont(new java.awt.Font("Dialog", 0, 12));
        txtDeliveryNo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtDeliveryNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(txtDeliveryNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 95, 180, 20));

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText(":");
        jPanel1.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 10, 20));

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText(":");
        jPanel1.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 105, 10, 20));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 30));
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText("Return Pembelian");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 10, 90));

        txtQtyOnHand.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        txtQtyOnHand.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtQtyOnHand.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtQtyOnHand.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtQtyOnHandPropertyChange(evt);
            }
        });
        jPanel3.add(txtQtyOnHand, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 5, 90, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Source Quantity :");
        jPanel3.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 5, 110, 20));
        jPanel3.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 28, 450, -1));

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("<html> &nbsp \n<b>F4 : </b> &nbsp Membuat <u>Retur Pembelian ke Supplier</u> baru <br>   &nbsp \n<b>F5 : </b> &nbsp <u>Retur Pembelian</u>\n</html>"); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 290, 70));

        txtRemarkGood.setFont(new java.awt.Font("Dialog", 0, 12));
        txtRemarkGood.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtRemarkGood.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRemarkGood.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRemarkGoodFocusLost(evt);
            }
        });
        txtRemarkGood.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRemarkGoodKeyReleased(evt);
            }
        });
        jPanel3.add(txtRemarkGood, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 40, 310, 20));

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText(":");
        jPanel3.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 10, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Catatan");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 90, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("Sub Total :");
        jPanel3.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 5, 80, 20));

        lblTotal.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblTotal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblTotalPropertyChange(evt);
            }
        });
        jPanel3.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 5, 110, 20));

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Keterangan", "Expired", "Satuan", "Qty", "Harga Sat", "Sub Total", "Konv", "Sisa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tblItem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                        .addGap(200, 200, 200)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-784)/2, (screenSize.height-532)/2, 784, 532);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharpurchase/image/Icon/Exit.png")));
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        fn.lookup(evt, new Object[]{lblSupplier, lblTelepon}, 
                "select s.kode_supp, coalesce(nama_supp,'') as nama, coalesce(telepon,'')as telp, coalesce(t.jatuh_Tempo,0) as top " +
                "from r_supplier s " +
                "left join m_termin t on t.kode=s.termin " +
                "where s.kode_supp||coalesce(nama_supp,'') ilike '%"+txtSupplier.getText()+"%' order by 2",
                txtSupplier.getWidth()+lblSupplier.getWidth()+18, 200);
}//GEN-LAST:event_txtSupplierKeyReleased

    private void lblTeleponPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblTeleponPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblTeleponPropertyChange

    private void txtShippingFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtShippingFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtShippingFocusLost

    private void txtShippingKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtShippingKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtShippingKeyReleased

    private void txtReqByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReqByFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReqByFocusLost

    private void txtReqByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReqByKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReqByKeyReleased

    private void lblSupplierPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSupplierPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSupplierPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtReturnNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReturnNoKeyReleased
        
    }//GEN-LAST:event_txtReturnNoKeyReleased

    private void txtSiteFromFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFromFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFromFocusLost

    private void txtSiteFromKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteFromKeyReleased
        fn.lookup(evt, new Object[]{lblSiteFrom},
                "select kode_gudang, coalesce(nama_gudang,'') as nama_gudang from r_gudang " +
                "where upper(kode_gudang||coalesce(nama_gudang,'')) iLike upper('%" + txtSiteFrom.getText() +"%') order by 2",
                txtSiteFrom.getWidth()+lblSiteFrom.getWidth()+18, 100);
}//GEN-LAST:event_txtSiteFromKeyReleased

    private void lblSiteFromPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSiteFromPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSiteFromPropertyChange

    private void txtQtyOnHandPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtQtyOnHandPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyOnHandPropertyChange

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        try {
            fn.setVisibleList(false);
            finalize();
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_formInternalFrameClosed

    private void txtRemarkGoodFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkGoodFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkGoodFocusLost

    private void txtRemarkGoodKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkGoodKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarkGoodKeyReleased

    private void lblTotalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblTotalPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTotalPropertyChange

    private boolean udfCekBeforeSave(){
        if(txtSupplier.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi supplier terlebih dulu!");
            txtSupplier.requestFocus();
            return false;
        }
        if(tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Itey PO masih kosong!");
            txtSupplier.requestFocus();
            return false;
        }
        return true;
    }

    private void udfSave(){
         if(!udfCekBeforeSave()) return;
         String sSql="";
         ResultSet rs=null;
         try{
             if(tblItem.getRowCount()==0) return;
             rs=conn.createStatement().executeQuery("select fn_r_get_retur_beli_no('"+fn.yyyymmdd_format.format(new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText()))+"')");
             if(rs.next())
                 txtReturnNo.setText(rs.getString(1));

             sSql="INSERT INTO r_retur_beli(" +
                  "no_retur, tanggal, kode_supp, receipt_no, kode_gudang, " +
                  "date_ins, user_ins, date_upd, user_upd, delivery_no, tunai_kredit, jenis_retur, keterangan) " +
                  "VALUES ('"+txtReturnNo.getText()+"', '"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText())+"', '"+txtSupplier.getText()+"', '', '"+txtSiteFrom.getText()+"', " +
                  "now(), '"+MainForm.sUserName+"', "+(isKoreksi? "now()": "null")+", "+(isKoreksi? "'"+MainForm.sUserName+"'": "null")+", '"+txtDeliveryNo.getText()+"', " +
                  "'"+cmbCaraBayar.getSelectedItem().toString().substring(0, 1)+"', '"+cmbJenisRetur.getSelectedItem().toString().substring(0, 1)+"', " +
                  "'"+txtRemarkGood.getText()+"'); ";

             TableColumnModel col=tblItem.getColumnModel();
             for(int i=0; i< tblItem.getRowCount(); i++){
                 if(tblItem.getValueAt(i, col.getColumnIndex("Product ID"))!=null &&
                   tblItem.getValueAt(i, col.getColumnIndex("Product ID")).toString().length()>0){
                    sSql+="INSERT INTO r_retur_beli_detail(no_retur, kode_item, qty, unit_price, user_ins, exp_date) values(" +
                            "'"+txtReturnNo.getText()+"', " +
                            "'"+tblItem.getValueAt(i, col.getColumnIndex("Product ID"))+"', " +
                            ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                            ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga Sat")))+", " +
                            "'"+MainForm.sUserName+"', " +
                            (tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().length() == 0 ? "null" : "'" + new SimpleDateFormat("dd/MM/yy").parse(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString()) + "'") + ");";
                 }
             }

             //System.out.println(sSql);

             conn.setAutoCommit(false);
             int i=conn.createStatement().executeUpdate(sSql);
             conn.setAutoCommit(true);
              JOptionPane.showMessageDialog(this, "Retur Good to Supplier sukses!");
             udfPreviewRetur();
             udfNew();
             //printKwitansi(txtTrxNo.getText(), false);
         } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException se1) {
                Logger.getLogger(FrmGRReturn.class.getName()).log(Level.SEVERE, null, se1);
            }
         }

    }

    private void udfPreviewRetur(){
        String sNo_Retur=txtReturnNo.getText();
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            JasperReport jasperReportmkel = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/ReturBeli.jasper"));
            HashMap parameter = new HashMap();
            parameter.put("corporate", MainForm.sNamaUsaha);
            parameter.put("alamat", MainForm.sAlamat);
            parameter.put("telp", MainForm.sTelp);
            parameter.put("no_retur",sNo_Retur);
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

//    private void printKwitansi(String sNo_PR, Boolean okCpy){
//        PrinterJob job = PrinterJob.getPrinterJob();
//        SysConfig sy=new SysConfig();
//
//        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
//        int i=0;
//        for(i=0;i<services.length;i++){
//            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
//                break;
//            }
//        }
//        if (JOptionPane.showConfirmDialog(null,"Siapkan Printer!","SGHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//            PrintReturnGood pn = new PrintReturnGood(conn, sNo_PR,okCpy, PHARMainMenu.sUserName, services[i]);
//        }
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbCaraBayar;
    private javax.swing.JComboBox cmbJenisRetur;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSiteFrom;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTelepon;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtDeliveryNo;
    private javax.swing.JLabel txtQtyOnHand;
    private javax.swing.JTextField txtRemarkGood;
    private javax.swing.JTextField txtReqBy;
    private javax.swing.JTextField txtReturnNo;
    private javax.swing.JTextField txtShipping;
    private javax.swing.JTextField txtSiteFrom;
    private javax.swing.JTextField txtSupplier;
    // End of variables declaration//GEN-END:variables

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
//                if(e.getSource().equals(txtKurs)||
//                        (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                //}else
                if(e.getSource().equals(txtDeliveryNo)){
                    sOldDeliveryNo=txtDeliveryNo.getText();
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

                if(e.getSource().equals(txtReturnNo) && txtReturnNo.getText().length()>0)
                    udfLoadReturnOrder();
                else if(e.getSource().equals(txtDeliveryNo) && !sOldDeliveryNo.equalsIgnoreCase(txtDeliveryNo.getText()) ){
                    udfLoadGRDelivery();
                    sOldDeliveryNo=txtDeliveryNo.getText();
                }

           }
        }
    } ;

    private void udfLoadGRDelivery(){
        if(txtDeliveryNo.getText().length()==0)
            udfNew();
        
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select gr.no_gr, gr.kode_supp, coalesce(s.nama_supp,'') as nama_supp, to_char(gr.tanggal, 'dd/MM/yyy') as tanggal, coalesce(gr.top,0) as top, " +
                    "to_char(gr.tanggal+coalesce(gr.top,0) , 'dd/MM/yyy') as jt_tempo, coalesce(gr.ship_via,'') as ship_via, " +
                    "coalesce(gr.no_receipt,'') as no_receipt " +
                    "from r_gr gr " +
                    "inner join r_gr_detail grd on grd.no_gr=gr.no_gr " +
                    "inner join r_supplier s on s.kode_supp=gr.kode_supp " +
                    "where upper(gr.no_receipt)='"+txtDeliveryNo.getText().toUpperCase()+"'");

            if(rs.next()){
                txtSupplier.setText(rs.getString("kode_supp"));
                lblSupplier.setText(rs.getString("nama_supp"));
                txtDeliveryNo.setText(rs.getString("no_receipt"));
                txtShipping.setText(rs.getString("ship_via"));
                ((DefaultTableModel)tblItem.getModel()).setNumRows(0);

            }else{
                udfNew();
                txtDeliveryNo.requestFocus();
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    
    public void setKoreksi(boolean b) {
        this.isKoreksi=b;
        txtReturnNo.setEnabled(b);
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_INSERT:{
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0 && tblItem.getValueAt(tblItem.getSelectedRow(), 0)==null){

                    }else if(evt.getSource().equals(tblItem) && tblItem.getSelectedColumn()==tblItem.getColumnModel().getColumnIndex("Product ID")){
                        
                    } else{
                        ((DefaultTableModel)tblItem.getModel()).setNumRows(tblItem.getRowCount()+1);
                        tblItem.requestFocusInWindow();
                        tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
                        tblItem.changeSelection(tblItem.getRowCount()-1, 0, false, false);
                    }
                    //if(evt.getSource().equals(tblPR) && tblPR.getSelectedRow()>=0 && tblPR.getSelectedColumn()==0){
//                        lookupItem.setSrcTable(tblItem, tblItem.getColumnModel().getColumnIndex("Qty"));
//                        lookupItem.setVisible(true);
                    DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(FrmGRReturn.this), true);
                    String sItem="";
                    for(int i=0; i< tblItem.getRowCount(); i++){
                        if(tblItem.getValueAt(i, 0)!=null)
                            sItem+=(sItem.length()==0? "" : ",") +"'"+tblItem.getValueAt(i, 0).toString()+"'";
                    }
                    String sQry="";
                    if(txtDeliveryNo.getText().trim().length()==0){
                        sQry=   "select * from(select i.kode_item, nama_item  from r_item i " +
                                (sItem.length()>0? " where  kode_item not in("+sItem+") " : "")+
                                " order by 2)x ";
                        d1.setTitle("Lookup Item");
                    }else{
                        sQry="select * from(select * from fn_r_retur_beli_item_sisa('"+txtDeliveryNo.getText()+"') as " +
                                "(kode_item varchar, nama_item varchar, sisa numeric, unit varchar) " +
                                (sItem.length()>0? " where  kode_item not in("+sItem+") " : "")+
                                ")x";

                        d1.setTitle("Lookup Barang from Delivery '"+txtDeliveryNo.getText()+"'");
                    }
                    d1.udfLoad(conn, sQry, "(kode_item||nama_item)", tblItem);

                    d1.setVisible(true);
                    if(d1.getKode().length()>0) {
                        tblItem.changeSelection(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Qty"), false, false);
                        if(txtDeliveryNo.getText().trim().length()>0)
                            tblItem.setValueAt(d1.getTable().getValueAt(d1.getTable().getSelectedRow(), 2), tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("Sisa"));
                    }
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable))                    {
                        if (!fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            c.requestFocus();
                        }else{
                            fn.lstRequestFocus();
                        }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                            if (!fn.isListVisible()){
                                Component c = findNextFocus();
                                if (c==null) return;
                                c.requestFocus();
                            }else{
                                fn.lstRequestFocus();
                            }
                            break;
                    }
                }

                case KeyEvent.VK_UP: {
                    if(!(evt.getSource() instanceof JTable))
                    {
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    btnCancelActionPerformed(null);
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F4:{
                    udfNew();
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                        int iRow[]= tblItem.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        for (int a=0; a<iRow.length; a++){
                            ((DefaultTableModel)tblItem.getModel()).removeRow(tblItem.getSelectedRow());
                        }

                        if(tblItem.getRowCount()>0 && rowPalingAtas<tblItem.getRowCount()){
                            tblItem.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }
                        else{
                            if(tblItem.getRowCount()>0)
                                tblItem.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                txtSupplier.requestFocus();

                        }
                        if(tblItem.getSelectedRow()>=0)
                            tblItem.changeSelection(tblItem.getSelectedRow(), 0, false, false);
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

    public JFormattedTextField getFormattedText(){
        JFormattedTextField fText=null;
        try {
            fText = new JFormattedTextField(new MaskFormatter("##/##/##")){
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
        } catch (ParseException ex) {
            Logger.getLogger(FrmGR.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fText;
    }
    
    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= ustTextField;
        JFormattedTextField fText=getFormattedText();
        int col, row;

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;

            text.setName("textEditor");
            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Expired"))
                text=fText;
            else
                text=ustTextField;

            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Qty")){
               text.addKeyListener(kListener);
            }else{
               text.removeKeyListener(kListener);
            }

           //col=vColIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           //text.addKeyListener(kListener);
           text.setFont(table.getFont());
           //text.setName("textEditor");


            text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

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
                if(col==tblItem.getColumnModel().getColumnIndex("Expired")){
                    if(!fn.validateDate(((JTextField)text).getText(), true, "dd/MM/yy")){
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmGRReturn.this),
                                "Silakan isikan format tanggal dengan 'dd/MM/yyyy'\n" +
                                "Contoh: 31/12/19");

                    }else{
                        retVal = ((JTextField)text).getText();
                    }
                }else if(col==tblItem.getColumnModel().getColumnIndex("Qty")){
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
//                    if(fn.udfGetDouble(((JTextField)text).getText())>fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa")))){
//                        JOptionPane.showMessageDialog(aThis, "Jumlah yang diretur melebihi Sisa transaksi!\n" +
//                                "Sisa transaksi adalah "+fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa"))));
//                        retVal = fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa")));
//                    }
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

    }

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
    DlgLookupItemBeli lookupItem=new DlgLookupItemBeli(JOptionPane.getFrameForComponent(this), true);
}
