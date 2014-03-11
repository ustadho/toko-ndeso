/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*udf
 * FrmGoodReceipt.java
 *
 * Created on Jul 15, 2010, 4:49:38 PM
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.AbstractCellEditor;
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
import retail.main.GeneralFunction;
import retail.main.JDesktopImage;
import retail.main.SysConfig;

/**
 *
 * @author ustadho
 */
public class FrmGR extends javax.swing.JInternalFrame {
    GeneralFunction fn;
    private Connection conn;
    TableColumnModel col=null;
    private boolean stMinus=false;
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private boolean withPO=true;
    private DlgLookupItemBeli lookupItem =new DlgLookupItemBeli(JOptionPane.getFrameForComponent(this), true);
    private Component aThis;

    /** Creates new form FrmGoodReceipt */
    public FrmGR() {
        initComponents();
        aThis=this;
        tblItem.getTableHeader().setFont(new Font("Tahoma", 0, 12));
        tblItem.getColumn("Keterangan").setPreferredWidth(200);
        tblItem.getColumn("On Order").setPreferredWidth(70);
        tblItem.getColumn("Sisa Order").setPreferredWidth(70);
        tblItem.getColumn("On Receipt").setPreferredWidth(70);
        tblItem.getColumn("Satuan").setPreferredWidth(50);
        tblItem.getColumn("Expired").setPreferredWidth(100);
        tblItem.setRowHeight(22);

//        tblItem.getColumn("Konv").setMinWidth(0); tblItem.getColumn("Konv").setMaxWidth(0); tblItem.getColumn("Konv").setPreferredWidth(0);
        tblItem.getColumn("UomKecil").setMinWidth(0); tblItem.getColumn("UomKecil").setMaxWidth(0); tblItem.getColumn("UomKecil").setPreferredWidth(0);
        tblItem.getColumn("JmlKecil").setMinWidth(0); tblItem.getColumn("JmlKecil").setMaxWidth(0); tblItem.getColumn("JmlKecil").setPreferredWidth(0);
        tblItem.getColumn("OnHand").setMinWidth(0); tblItem.getColumn("OnHand").setMaxWidth(0); tblItem.getColumn("OnHand").setPreferredWidth(0);
//        tblPR.getColumn("Harga").setMinWidth(0); tblPR.getColumn("Harga").setMaxWidth(0); tblPR.getColumn("Harga").setPreferredWidth(0);
//        tblPR.getColumn("Disc").setMinWidth(0); tblPR.getColumn("Disc").setMaxWidth(0); tblPR.getColumn("Disc").setPreferredWidth(0);
//        tblPR.getColumn("PPN").setMinWidth(0); tblPR.getColumn("PPN").setMaxWidth(0); tblPR.getColumn("PPN").setPreferredWidth(0);

        col=tblItem.getColumnModel();
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblItem.getColumnModel().getColumn(col.getColumnIndex("On Receipt")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Expired")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Harga")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("Disc")).setCellEditor(cEditor);
        tblItem.getColumnModel().getColumn(col.getColumnIndex("PPN")).setCellEditor(cEditor);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblItem.getSelectedRow();
                if(iRow>=0){
                    txtConv.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Konv")))));
                    lblUomKecil.setText(tblItem.getValueAt(iRow, col.getColumnIndex("UomKecil")).toString());
                    txtStockOnHand.setText(fn.dFmt.format(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("OnHand")))));
                }
            }
        });

        tblItem.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int iRow=tblItem.getSelectedRow();
                int iCol=e.getColumn();
                TableColumnModel col=tblItem.getColumnModel();

                if(iCol==col.getColumnIndex("On Receipt") && e.getType()==TableModelEvent.UPDATE ){
                    tblItem.setValueAt(fn.udfGetFloat(tblItem.getValueAt(iRow, col.getColumnIndex("On Receipt")))*fn.udfGetFloat(tblItem.getValueAt(iRow, col.getColumnIndex("Konv"))),
                            iRow, col.getColumnIndex("JmlKecil"));
                }else if(iRow >=0 && !withPO && iCol==col.getColumnIndex("Product ID") && e.getType()==TableModelEvent.UPDATE ){
                    try{
                        ResultSet rs=conn.createStatement().executeQuery("select coalesce(nama_item,'') as nama_item, " +
                                     "coalesce(unit,'') as unit, " +
                                     "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                                     "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                                     "coalesce(s.uom_alt,'') as uom_supp, " +
                                     "coalesce(s.price,0) as unit_price, " +
                                     "coalesce(s.disc,0) as disc, " +
                                     "coalesce(s.vat, 0) as vat, " +
                                     "coalesce(convertion,1) as konv, " +
                                     "sum(coalesce(st.saldo,0)) as on_hand " +
                                     "from r_item i " +
                                     "left join r_item_stok st on st.kode_item=i.kode_item and st.kode_gudang='"+txtSite.getText()+"' " +
                                     "left join r_item_supplier s on s.kode_item=i.kode_item and s.kode_supp='"+txtSupplier.getText()+"' " +
                                     "where i.kode_item='"+tblItem.getValueAt(iRow, 0).toString()+"' " +
                                     "group by coalesce(nama_item,'') , coalesce(unit,''), coalesce(unit2,'') , " +
                                     "coalesce(konv2,1), coalesce(unit3,''), coalesce(konv3,1) ,coalesce(s.uom_alt,'') , " +
                                     "coalesce(s.price,0) , coalesce(s.disc,0) , coalesce(s.vat, 0) , coalesce(convertion,1)");
                         if(rs.next()){
                             tblItem.setValueAt(rs.getString("nama_item"), iRow, tblItem.getColumnModel().getColumnIndex("Keterangan"));
                             tblItem.setValueAt(rs.getString("uom_supp"), iRow, tblItem.getColumnModel().getColumnIndex("Satuan"));
                             tblItem.setValueAt(rs.getInt("konv"), iRow, tblItem.getColumnModel().getColumnIndex("Konv"));
                             tblItem.setValueAt(rs.getDouble("unit_price"), iRow, tblItem.getColumnModel().getColumnIndex("Harga"));
                             tblItem.setValueAt(rs.getDouble("disc"), iRow, tblItem.getColumnModel().getColumnIndex("Disc"));
                             tblItem.setValueAt(rs.getDouble("vat"), iRow, tblItem.getColumnModel().getColumnIndex("PPN"));
                             tblItem.setValueAt(rs.getDouble("on_hand"), iRow, tblItem.getColumnModel().getColumnIndex("OnHand"));

                         }else{
                             tblItem.setValueAt("", iRow, tblItem.getColumnModel().getColumnIndex("Keterangan"));
                             tblItem.setValueAt("", iRow, tblItem.getColumnModel().getColumnIndex("Satuan"));
                             tblItem.setValueAt(1, iRow, tblItem.getColumnModel().getColumnIndex("Konv"));
                             tblItem.setValueAt(0, iRow, tblItem.getColumnModel().getColumnIndex("Harga"));
                             tblItem.setValueAt(0, iRow, tblItem.getColumnModel().getColumnIndex("Disc"));
                             tblItem.setValueAt(0, iRow, tblItem.getColumnModel().getColumnIndex("PPN"));
                             tblItem.setValueAt(0, iRow, tblItem.getColumnModel().getColumnIndex("OnHand"));
                         }

                        rs.close();

                    }catch(SQLException se){
                        JOptionPane.showMessageDialog(aThis, se.getMessage());
                    }
                }else if(iCol==col.getColumnIndex("Konv") && e.getType()==TableModelEvent.UPDATE){
                    txtConv.setText(fn.intFmt.format(tblItem.getValueAt(iRow, iCol)));
                }else if(iCol==col.getColumnIndex("OnHand") && e.getType()==TableModelEvent.UPDATE){
                    txtStockOnHand.setText(fn.intFmt.format(tblItem.getValueAt(iRow, iCol)));
                }
                if(iCol==col.getColumnIndex("On Receipt")||iCol==col.getColumnIndex("Harga")||iCol==col.getColumnIndex("Disc")||iCol==col.getColumnIndex("PPN")||iCol==col.getColumnIndex("Disc(Rp)")||iCol==col.getColumnIndex("PPn(Rp)"))
                    udfSetSubTotalItem();
                 if(tblItem.getRowCount()>0){
                    double totLine=0, totVat=0;
                    double extPrice=0;
                    for(int i=0; i< tblItem.getRowCount(); i++){
                        //if(e.getType()==TableModelEvent.DELETE) ((DefaultTableModel)tblItem.getModel()).setValueAt(i+1, i, 0);
//                        extPrice=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("On Receipt")))*
//                                fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")));
//                        extPrice=extPrice-(extPrice/100)* fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc")));
//
//                        totLine+=extPrice;
                        extPrice=fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Sub Total")));
                        totLine+=extPrice;
                        totVat+=extPrice/100*fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN")));
                    }

                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totVat)+Math.floor(totLine)));
                }else{
                    txtTotalLine.setText("0");
                    txtTotVat.setText("0");
                    txtNetto.setText("0");
                }
            }
        });

        
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
    
        txtSite.setEnabled(false);
    }

    private void udfSetSubTotalItem(){
        int iRow=tblItem.getSelectedRow();
        if(iRow<0) return;
        TableColumnModel col=tblItem.getColumnModel();
        boolean stDiscRp=tblItem.getValueAt(iRow, col.getColumnIndex("Disc(Rp)"))!=null && (Boolean)tblItem.getValueAt(iRow, col.getColumnIndex("Disc(Rp)"))==true;
        double subTotal=fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("On Receipt")))*fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Harga")));
        subTotal=stDiscRp? subTotal-fn.udfGetDouble(fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Disc")))): subTotal*(1-fn.udfGetDouble(tblItem.getValueAt(iRow, col.getColumnIndex("Disc")))/100);
        //subTotal=chkPPnRp.isSelected()? subTotal*(1+fn.udfGetDouble(txtPPn.getText())/100): subTotal+fn.udfGetDouble(txtPPn.getText());
        tblItem.setValueAt(subTotal, iRow, col.getColumnIndex("Sub Total"));
    }
    
    public void setStatusMinus(boolean b){
        this.stMinus=b;
    }

    public void setFlagPO(boolean b){
        this.withPO=b;
    }
    
    private void setStatusMinus(){
        txtNoGR.setEnabled(stMinus);
        txtNoPO.setEnabled(!stMinus);
        txtNoInvoice.setEnabled(!stMinus);
        //txtSite.setEnabled(!stMinus);
        txtSupplier.setEnabled(!stMinus);
        jLabel17.setText(stMinus? "Last TTB#": "Receipt#");
        jLabel16.setText(stMinus? "Pembelian (Minus)": "Pembelian");
    }

    public void setConn(Connection con){
        this.conn=con;
        lookupItem.setConn(con);
    }

    private Date getDueDate(Date d, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, i);

        return c.getTime();
    }

    private void setDueDate(){
        try {
            jFJtTempo.setText(new SimpleDateFormat("dd/MM/yyyy").format(
                    getDueDate(new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText()),
                    fn.udfGetInt(txtTop.getText()))));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
        
        try{
            String sSql="select fn_r_get_gr_no('"+ymd.format(dmy.parse(txtDate.getText()))+"') as no_gr";
            String sNoGr="";
            ResultSet rs=conn.createStatement().executeQuery(sSql);
            if(rs.next()){
                txtNoGR.setText(rs.getString(1));
                sNoGr=rs.getString(1);
            }
            rs.close();
            sSql="INSERT INTO r_gr(no_gr, no_receipt, tanggal, kode_gudang, kode_supp, freight, bayar, koreksi, " +
                    "date_ins, user_ins, remark, no_po) values('"+sNoGr+"', '"+txtNoInvoice.getText()+"', now(), " + //
                    "'"+txtSite.getText()+"', '"+txtSupplier.getText()+"', "+fn.udfGetDouble(txtBiayaLain.getText())+", " +
                    fn.udfGetDouble(txtBayar.getText())+ ", false, now(), '"+MainForm.sUserName+"', '"+txtRemark.getText()+"', " +
                    "'"+txtNoPO.getText()+"'); ";

            for(int i=0; i<tblItem.getRowCount(); i++){
                sSql+=  "INSERT INTO r_gr_detail(no_gr, kode_item, qty, unit_price, disc, " +
                        "tax, exp_date, unit, konv, no_po, is_disc_rp, is_tax_rp) values('"+sNoGr+"'," +
                        "'"+tblItem.getValueAt(i, col.getColumnIndex("Product ID")).toString()+"', " +
                        fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("On Receipt")))+ ", " +
                        fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Harga")))+ ", " +
                        fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Disc")))+ ", " +
                        fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("PPN")))+ ", " +
                        (tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().length()==0? "null" : "'"+new SimpleDateFormat("dd/MM/yy").parse(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString())+"'")+"," +
                        "'"+tblItem.getValueAt(i, col.getColumnIndex("Satuan")).toString()+"'," +
                        ""+fn.udfGetDouble(tblItem.getValueAt(i, col.getColumnIndex("Konv")))+", " +
                        "'"+txtNoPO.getText()+"', " +
                        (Boolean)tblItem.getValueAt(i, col.getColumnIndex("Disc(Rp)"))+"," +
                        (Boolean)tblItem.getValueAt(i, col.getColumnIndex("PPn(Rp)"))+");";
            }

            System.out.println(sSql);
            conn.setAutoCommit(false);
            int i=conn.createStatement().executeUpdate(sSql);
            conn.setAutoCommit(true);
            //JOptionPane.showMessageDialog(this, "Simpan Sukses ");
            //printKwitansi(txtNoPO.getText(), false);
            udfNew();
            
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }catch(SQLException ex){

            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } catch (SQLException ex1) {
                Logger.getLogger(FrmGR.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }catch(java.lang.ArrayIndexOutOfBoundsException ex){

        }
    }

    private void printKwitansi(String sNo_PR, Boolean okCpy){
        try{
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
            if (JOptionPane.showConfirmDialog(null,"Simpan Good Receipt Sukses. Selanjutnya akan di Print!","SGHS Go Open Source",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//                PrintGood_receipt pn = new PrintGood_receipt(conn, txtNoGR.getText(), false,PHARMainMenu.sUserName ,services[i]);
            }
        }catch(java.lang.NullPointerException nu){
            JOptionPane.showMessageDialog(this, nu.getMessage());
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

        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtSite = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        lblSite = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtNoInvoice = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtReceiptBy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNoGR = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtTglPO = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtNoPO = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtTop = new javax.swing.JTextField();
        jFJtTempo = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        txtStockOnHand = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtConv = new javax.swing.JLabel();
        lblUomKecil = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtTotVat = new javax.swing.JLabel();
        txtNetto = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtBiayaLain = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Penerimaan/ Pembelian Barang (Plus)"); // NOI18N
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

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/print.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
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
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 36));
        jLabel16.setForeground(new java.awt.Color(0, 0, 153));
        jLabel16.setText(" Penerimaan Barang");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Gudang");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtSite.setFont(new java.awt.Font("Dialog", 0, 12));
        txtSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSite.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtSite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSiteFocusLost(evt);
            }
        });
        txtSite.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSiteKeyReleased(evt);
            }
        });
        jPanel1.add(txtSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 60, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Catatan");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtRemark.setFont(new java.awt.Font("Dialog", 0, 12));
        txtRemark.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtRemark.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRemarkFocusLost(evt);
            }
        });
        txtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRemarkKeyReleased(evt);
            }
        });
        jPanel1.add(txtRemark, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 390, 20));

        lblSite.setFont(new java.awt.Font("Dialog", 0, 12));
        lblSite.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSite.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSitePropertyChange(evt);
            }
        });
        jPanel1.add(lblSite, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 85, 260, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        lblSupplier.setFont(new java.awt.Font("Dialog", 0, 12));
        lblSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblSupplier.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblSupplierPropertyChange(evt);
            }
        });
        jPanel1.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 260, 20));

        txtSupplier.setFont(new java.awt.Font("Dialog", 0, 12));
        txtSupplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSupplier.setDisabledTextColor(new java.awt.Color(153, 153, 153));
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
        jPanel1.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 60, 20));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Supplier");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText(":");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 10, 20));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(":");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 10, 20));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("No. Invoice");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(":");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 10, 20));

        txtNoInvoice.setFont(new java.awt.Font("Dialog", 0, 12));
        txtNoInvoice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoInvoice.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoInvoice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoInvoiceFocusLost(evt);
            }
        });
        txtNoInvoice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoInvoiceKeyReleased(evt);
            }
        });
        jPanel1.add(txtNoInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 220, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Tgl. Terima");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 30, 80, 20));

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
        jPanel1.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 90, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Transaction #");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 90, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Diterima Oleh");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 50, 80, 20));

        txtReceiptBy.setFont(new java.awt.Font("Dialog", 0, 12));
        txtReceiptBy.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtReceiptBy.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReceiptBy.setEnabled(false);
        txtReceiptBy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtReceiptByFocusLost(evt);
            }
        });
        txtReceiptBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtReceiptByKeyReleased(evt);
            }
        });
        jPanel1.add(txtReceiptBy, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 120, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 30, 10, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText(":");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 50, 10, 20));

        txtNoGR.setFont(new java.awt.Font("Dialog", 0, 14));
        txtNoGR.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoGR.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoGR.setEnabled(false);
        txtNoGR.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoGRFocusLost(evt);
            }
        });
        txtNoGR.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNoGRPropertyChange(evt);
            }
        });
        txtNoGR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoGRKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNoGRKeyTyped(evt);
            }
        });
        txtNoGR.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                txtNoGRVetoableChange(evt);
            }
        });
        jPanel1.add(txtNoGR, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 120, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 20));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Tgl.");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 70, 30, 20));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText(":");
        jPanel1.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 10, 20));

        txtTglPO.setFont(new java.awt.Font("Dialog", 0, 12));
        txtTglPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTglPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTglPO.setEnabled(false);
        txtTglPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTglPOFocusLost(evt);
            }
        });
        txtTglPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTglPOKeyReleased(evt);
            }
        });
        jPanel1.add(txtTglPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 70, 80, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("No. PO");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 70, 80, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText(":");
        jPanel1.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 70, 10, 20));

        txtNoPO.setFont(new java.awt.Font("Dialog", 0, 12));
        txtNoPO.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNoPO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoPO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoPOFocusLost(evt);
            }
        });
        txtNoPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoPOKeyReleased(evt);
            }
        });
        jPanel1.add(txtNoPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 70, 120, 20));

        jLabel11.setText("Jth. Tempo  :"); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 90, 80, 21));

        txtTop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txtTop, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 90, 40, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jFJtTempo.setEnabled(false);
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12));
        jPanel1.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 90, 80, 20));

        jLabel12.setText("T.O.P"); // NOI18N
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 90, 80, 21));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Stock On Hand :");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 120, 20));

        txtStockOnHand.setFont(new java.awt.Font("Dialog", 0, 12));
        txtStockOnHand.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtStockOnHand.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtStockOnHand.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtStockOnHandPropertyChange(evt);
            }
        });
        jPanel3.add(txtStockOnHand, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 5, 80, 20));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Conv = ");
        jPanel3.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 5, 50, 20));

        txtConv.setFont(new java.awt.Font("Dialog", 0, 12));
        txtConv.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtConv.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtConv.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtConvPropertyChange(evt);
            }
        });
        jPanel3.add(txtConv, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 5, 70, 20));

        lblUomKecil.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblUomKecil.setForeground(new java.awt.Color(0, 0, 153));
        lblUomKecil.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUomKecil.setText("Uom");
        jPanel3.add(lblUomKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 5, 60, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 10, 30));

        jLabel1.setBackground(new java.awt.Color(204, 255, 255));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("<html>\n &nbsp <b>F4 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Membuat Good Receipt baru <br> \n &nbsp <b>F5 &nbsp &nbsp &nbsp &nbsp : </b> &nbsp Menyimpan Good Receipt <br>\n &nbsp <b>Ctrl+C : </b> &nbsp Copy Baris Item  &nbsp  &nbsp\n &nbsp <b>Ctrl+V : </b> &nbsp Paste Baris Item <br>\n &nbsp <b>Insert : </b> &nbsp Menambah Item Good Receipt dari PO yang sama<br>\n<hr>\n &nbsp <b>Catatan : </b> &nbsp Format Expired Date adalah 'dd/MM/yy' contoh '31/05/11'<br>\n</html>"); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Keterangan", "On Order", "Sisa Order", "On Receipt", "Satuan", "Expired", "Harga", "Disc", "PPN", "Sub Total", "Konv", "UomKecil", "JmlKecil", "OnHand", "Disc(Rp)", "PPn(Rp)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, true, true, true, true, false, false, false, false, false, true, true
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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Netto");
        jPanel2.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 90, 20));

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel2.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 120, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Line Total :");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 90, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel2.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 120, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12));
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel2.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 70, 120, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("V.A.T");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 90, 20));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Biaya Lain");
        jPanel2.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 80, 20));

        txtBiayaLain.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBiayaLain.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBiayaLain.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBiayaLain.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBiayaLain.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBiayaLainFocusLost(evt);
            }
        });
        txtBiayaLain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBiayaLainKeyReleased(evt);
            }
        });
        jPanel2.add(txtBiayaLain, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 120, 20));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("BAYAR");

        txtBayar.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBayar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBayar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBayar.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBayar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBayarFocusLost(evt);
            }
        });
        txtBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBayarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addGap(170, 170, 170)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addGap(540, 540, 540)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-892)/2, (screenSize.height-592)/2, 892, 592);
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
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png")));
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

    private void txtSiteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSiteFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtSiteFocusLost

    private void txtSiteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSiteKeyReleased
        fn.lookup(evt, new Object[]{lblSite},
        "select kode_gudang, nama_gudang from r_gudang where kode_gudang||coalesce(nama_gudang,'') ilike '%"+txtSite.getText()+"%' order by 1",
                txtSite.getWidth()+lblSite.getWidth(), 300);
}//GEN-LAST:event_txtSiteKeyReleased

    private void txtRemarkFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkFocusLost

    private void txtRemarkKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRemarkKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtRemarkKeyReleased

    private void txtReceiptByFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReceiptByFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByFocusLost

    private void txtReceiptByKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReceiptByKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtReceiptByKeyReleased

    private void lblSitePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSitePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblSitePropertyChange

    private void txtNoGRFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoGRFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRFocusLost

    private void txtNoGRPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNoGRPropertyChange
        btnPrint.setEnabled(txtNoGR.getText().length()>0);
}//GEN-LAST:event_txtNoGRPropertyChange

    private void txtNoGRKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoGRKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRKeyReleased

    private void txtNoGRKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoGRKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRKeyTyped

    private void txtNoGRVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_txtNoGRVetoableChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoGRVetoableChange

    private void lblSupplierPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblSupplierPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lblSupplierPropertyChange

    private void txtSupplierFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSupplierFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSupplierFocusLost

    private void txtSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupplierKeyReleased
        String sQry="select kode_supp as Kode, coalesce(nama_supp,'') as Supplier, " +
                    "coalesce(alamat_1,'')||' '||coalesce(nama_kota,'') as alamat  " +
                    "from r_supplier s " +
                    "left join m_kota k on s.kota=k.kode_kota " +
                    "where (kode_supp||coalesce(    nama_supp,'')) " +
                    "iLike '%" + txtSupplier.getText() + "%' order by coalesce(nama_supp,'') ";

        fn.lookup(evt, new Object[]{lblSupplier}, sQry, txtSupplier.getWidth()+lblSupplier.getWidth()+18, 200);
    }//GEN-LAST:event_txtSupplierKeyReleased

    private void txtNoInvoiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoInvoiceFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoInvoiceFocusLost

    private void txtNoInvoiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoInvoiceKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoInvoiceKeyReleased

    private void txtStockOnHandPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtStockOnHandPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtStockOnHandPropertyChange

    private void txtConvPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtConvPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtConvPropertyChange

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void txtTglPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTglPOFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTglPOFocusLost

    private void txtTglPOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTglPOKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTglPOKeyReleased

    private void txtNoPOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoPOFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoPOFocusLost

    private void txtNoPOKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoPOKeyReleased
        fn.lookup(evt, new Object[]{null},
                "select * from fn_r_lookup_no_po_supplier('"+txtSupplier.getText()+"','%"+txtNoPO.getText()+"%') " +
                "as (\"No PO\" varchar)",
                txtNoPO.getWidth(), 150);
    }//GEN-LAST:event_txtNoPOKeyReleased

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        fn.setVisibleList(false);
    }//GEN-LAST:event_formInternalFrameClosed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printKwitansi(txtNoGR.getText(), false);
}//GEN-LAST:event_btnPrintActionPerformed

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNettoPropertyChange

    private void txtBayarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBayarFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBayarFocusLost

    private void txtBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBayarKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBayarKeyReleased

    private void txtBiayaLainFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaLainFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBiayaLainFocusLost

    private void txtBiayaLainKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBiayaLainKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBiayaLainKeyReleased

    
    private void udfNew(){
        txtSupplier.setText(""); lblSupplier.setText("");
        txtNoPO.setText("");
        txtNoInvoice.setText("");
        txtRemark.setText("");
        txtTglPO.setText("");
        txtStockOnHand.setText("");
        txtConv.setText("");
        lblUomKecil.setText("");
        txtTop.setText(""); jFJtTempo.setText("");
        
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);

        btnNew.setEnabled(false);
        btnSave.setEnabled(true);
        btnCancel.setText("Cancel");
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Cancel.png")));
        //txtSite.setText(""); lblSite.setText("");
        //txtSupplier.requestFocus();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JFormattedTextField jFJtTempo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSite;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblUomKecil;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtBiayaLain;
    private javax.swing.JLabel txtConv;
    private javax.swing.JTextField txtDate;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JTextField txtNoGR;
    private javax.swing.JTextField txtNoInvoice;
    private javax.swing.JTextField txtNoPO;
    private javax.swing.JTextField txtReceiptBy;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtSite;
    private javax.swing.JLabel txtStockOnHand;
    private javax.swing.JTextField txtSupplier;
    private javax.swing.JTextField txtTglPO;
    private javax.swing.JTextField txtTop;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables
    MyKeyListener kListener=new MyKeyListener();

    private void udfClearPO(){
        txtTglPO.setText("");
        ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
        
    }

    private void udfLoadItemFromPO() {
        if(txtNoPO.getText().trim().length()==0){
            udfClearPO();
            return;
        }

        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(tanggal, 'dd/MM/yyyy') as tgl_po, coalesce(top,0) as top, " +
                    "to_char(tanggal+coalesce(top,0), 'dd/MM/yyyy') as tgl_jt_tempo from r_po  " +
                    "where no_po='"+txtNoPO.getText()+"'");
            if(rs.next()){
                txtTglPO.setText(rs.getString("tgl_po"));
                txtTop.setText(rs.getString("top"));
                jFJtTempo.setText(rs.getString("tgl_jt_tempo"));
            }
            rs.close();

            String s="select * from fn_r_gr_item_sisa_po('"+txtNoPO.getText()+"') as (no_po varchar, kode_item varchar," +
                    "nama_item varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, " +
                    "harga double precision, disc double precision, ppn double precision, konv int, uom_kecil varchar, " +
                    "jml_kecil numeric, on_hand numeric, is_disc_rp boolean , is_tax_rp boolean)";

            rs=conn.createStatement().executeQuery(s);
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
            double subTotal=0;
            while(rs.next()){
                subTotal=rs.getDouble("on_receipt")*rs.getDouble("harga");
                subTotal=rs.getBoolean("is_disc_rp")? subTotal-rs.getDouble("disc"): subTotal*(1-rs.getDouble("disc")/100);

                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getDouble("qty_order"),
                    rs.getDouble("sisa"),
                    rs.getDouble("on_receipt"),
                    rs.getString("uom_po"),
                    "",
                    rs.getDouble("harga"),
                    rs.getDouble("disc"),
                    rs.getDouble("ppn"),
                    subTotal,
                    rs.getDouble("konv"),
                    rs.getString("uom_kecil"),
                    rs.getDouble("jml_kecil"),
                    rs.getDouble("on_hand"),
                    rs.getBoolean("is_disc_rp"),
                    rs.getBoolean("is_tax_rp"),
                });
            }
            tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
            if(tblItem.getRowCount()>0)
                tblItem.changeSelection(0, 4, false, false);

            else{
                JOptionPane.showMessageDialog(this, "Item Sisa PO tidak ditemukan! Silakan cek No. PO anda!");
                //txtNoPO.requestFocus();
                return;
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }


    private boolean udfCekBeforeSave(){
        if(txtSupplier.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan supplier terlebih dulu~");
            txtSupplier.requestFocus();
            return false;
        }
        if(txtSite.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan masukkan Site terlebih dulu~");
            txtSite.requestFocus();
            return false;
        }
        if(tblItem.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Item yang diterima masih kosong~");
            tblItem.requestFocus();
            return false;
        }
        if(txtNoInvoice.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Masukkan nomor Invoice terlebih dulu");
            txtNoInvoice.requestFocus();
            return false;
        }
        if(!stMinus){
            try{
                ResultSet rs=conn.createStatement().executeQuery(
                        "select no_receipt from r_gr where no_receipt='"+txtNoInvoice.getText()+"'; ");
                if(rs.next()){
                    JOptionPane.showMessageDialog(this, "No. Invoice sudah pernah dimasukkan!");
                    txtNoInvoice.requestFocus();
                    rs.close();
                    return false;
                }
                rs.close();

            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
//        for(int i=0; i<tblItem.getRowCount(); i++){
//            if(tblItem.getValueAt(i, col.getColumnIndex("Expired")).toString().equalsIgnoreCase("")){
//                if(JOptionPane.showConfirmDialog(this, "Expired Date pada baris ke : "+(i+1)+" masih kosong.\nAkan dilanjutkan", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION){
//                    tblItem.grabFocus();
//                    tblItem.changeSelection(i, col.getColumnIndex("Expired"), false, false);
//                    return false;
//                }
//            }
//        }
        return true;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        txtBayar.addKeyListener(kListener); txtBayar.addFocusListener(txtFocusListener);

        tblItem.addKeyListener(kListener);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy')");
            rs.next();
            txtDate.setText(rs.getString(1));
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        udfNew();
        txtSite.setText(MainForm.sKodeGudang);
        lblSite.setText(MainForm.sNamaGudang);

        setStatusMinus();
        Runnable doRun = new Runnable() {
            public void run() {
                if(!stMinus)
                    txtSupplier.requestFocusInWindow();
                else
                    txtNoGR.requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(doRun);
        txtNoPO.setVisible(withPO);     jLabel10.setVisible(withPO);    jLabel28.setVisible(withPO);
        txtTglPO.setVisible(withPO);    jLabel8.setVisible(withPO);     jLabel27.setVisible(withPO);

    }

    private void udfLoadGR(){
        if(txtNoGR.getText().trim().isEmpty()){
            udfNew();
            return;
        }

        String s="select coalesce(no_inv_do_sj,'') as no_sj, gr.kode_supp, coalesce(s.nama_supplier,'') as nama_supplier," +
                "coalesce(gr.site_id,'') as site_id, coalesce(st.site_name,'') as site_name," +
                "coalesce(gr.no_po,'') as no_po, to_char(po.tanggal, 'dd/MM/yyyy') as tgl_po " +
                "from phar_good_receipt gr " +
                "left join phar_good_receipt_detail grd on grd.good_receipt_id=gr.good_receipt_id " +
                "left join phar_supplier s on s.kode_supplier=gr.kode_supp " +
                "left join phar_site st on st.site_id=gr.site_id " +
                "left join phar_po po on po.no_po=gr.no_po where " +
                "gr.good_receipt_id='"+txtNoGR.getText()+"' ";

        try{
            ResultSet rs=conn.createStatement().executeQuery(s);
            if(rs.next()){
                txtSupplier.setText(rs.getString("kode_supp"));
                lblSupplier.setText(rs.getString("nama_supplier"));
                txtSite.setText(rs.getString("site_id"));
                lblSite.setText(rs.getString("site_name"));
                txtNoPO.setText(rs.getString("no_po"));
                txtTglPO.setText(rs.getString("tgl_po"));
                txtNoInvoice.setText(rs.getString("no_sj"));

                rs.close();
                s="select * from fn_phar_gr_item_detail('"+txtNoPO.getText()+"') as (no_po varchar, kode_barang varchar," +
                        "nama_barang varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, " +
                        "harga numeric, disc double precision, ppn real, konv real, uom_kecil varchar, " +
                        "jml_kecil double precision, on_hand numeric, exp_date text, urut smallint, no_pr varchar)";

                rs=conn.createStatement().executeQuery(s);
                ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                while(rs.next()){
                    ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("qty_order"),
                        rs.getDouble("sisa"),
                        rs.getDouble("on_receipt"),
                        rs.getString("uom_po"),
                        rs.getString("exp_date"),
                        rs.getDouble("harga"),
                        rs.getDouble("disc"),
                        rs.getDouble("ppn"),
                        rs.getDouble("konv"),
                        rs.getString("uom_kecil"),
                        rs.getDouble("on_receipt")*rs.getDouble("konv"), //rs.getDouble("jml_kecil"),
                        rs.getDouble("on_hand"),
                        rs.getInt("urut"),
                        rs.getString("no_pr")
                    });
                }


                tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
                if(tblItem.getRowCount()>0)
                    tblItem.changeSelection(0, 4, false, false);
            }else{
                JOptionPane.showMessageDialog(this, "No. Good Receipt tidak ditemukan!");
                udfNew();
                //txtNoGR.grabFocus();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

                if(e.getSource().equals(txtNoPO) && !fn.isListVisible())
                    udfLoadItemFromPO();
                else if(e.getSource().equals(txtNoGR))
                    udfLoadGR();
                else if(e.getSource().equals(txtTop))
                    setDueDate();

           }
        }


    } ;



    private void copyTable(){
        // Get all the table data
        Vector data = ((DefaultTableModel)tblItem.getModel()).getDataVector();
        // Copy the second row
        tableCopyRow = (Vector) data.elementAt(tblItem.getSelectedRow());
        //row = (Vector) row.clone();
    }

    private void pasteTable(){
        ((DefaultTableModel)tblItem.getModel()).insertRow(tblItem.getSelectedRow(), tableCopyRow);
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
//          if(evt.getSource().equals(tblPR)){
//              if(tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("On Receipt") && stMinus){
//                  evt.consume();
//                  return;
//              }
//          }
            if(evt.getSource() instanceof JTextField &&
                    ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor") && stMinus)
                ((JTextField)evt.getSource()).setEditable(tblItem.getSelectedColumn()==tblItem.getColumnModel().getColumnIndex("On Receipt"));

          if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor") &&
              !(tblItem.getSelectedColumn()==tblItem.getColumnModel().getColumnIndex("Expired"))) {

//              if((tblPR.getSelectedColumn()!=tblPR.getColumnModel().getColumnIndex("On Receipt"))){
//                  if(stMinus){
//                      evt.consume();
//                      return;
//                  }
//              }
              char c = evt.getKeyChar();
              if (!((c >= '0' && c <= '9')) &&
                    (c != KeyEvent.VK_BACK_SPACE) &&
                    (c != KeyEvent.VK_DELETE) &&
                    (c != KeyEvent.VK_ENTER) &&
                    (c != '-')) {
                    getToolkit().beep();
                    evt.consume();
                    return;
              }
           }
          
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
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
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "SHS Pharmacy",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F5:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                        int iRow[]= tblItem.getSelectedRows();
                        int rowPalingAtas=iRow[0];

//                        if(JOptionPane.showConfirmDialog(FrmPO.this,
//                                "Item '"+tblPR.getValueAt(iRow, 3).toString()+"' dihapus dari PO?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
//                            return;

                        for (int a=0; a<iRow.length; a++){
                            ((DefaultTableModel)tblItem.getModel()).removeRow(tblItem.getSelectedRow());
                        }

                        if(tblItem.getRowCount()>0 && rowPalingAtas<tblItem.getRowCount()){
                            //if(tblPR.getSelectedRow()>0)
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
                case KeyEvent.VK_INSERT:{
                    if(withPO)
                        udfLookupItemPO();
                    else
                        udfLookupItemNonPO(evt);

                    break;
                }

            }
        }

        @Override
        public void keyReleased(KeyEvent evt){
            if(evt.getSource().equals(tblItem) && tblItem.getSelectedRow()>=0){
                if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_C && evt.getModifiersEx()==java.awt.event.KeyEvent.CTRL_DOWN_MASK){
                    if(fn.udfGetInt(tblItem.getValueAt(tblItem.getSelectedRow(), tblItem.getColumnModel().getColumnIndex("On Receipt")))<=1){
                        JOptionPane.showMessageDialog(FrmGR.this, "Qty on Receipt <= 1 baris tidak bisa dicopy!");
                        tableCopyRow=null;
                        return;
                    }
                    copyTable();
                }
                if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_V && evt.getModifiersEx()==java.awt.event.KeyEvent.CTRL_DOWN_MASK){
                    if(tableCopyRow==null){
                        JOptionPane.showMessageDialog(FrmGR.this, "Tabel item belum di-copy! Klik pada item kemudian tekan Ctrl+C terlebih dulu!");
                        return;
                    }
                    pasteTable();
                }
            }else if(evt.getSource().equals(txtTop))
                setDueDate();

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

    private void udfLookupItemPO(){
        DLgLookup d1=new DLgLookup(JOptionPane.getFrameForComponent(FrmGR.this), true);
        String sItem="";
        for(int i=0; i< tblItem.getRowCount(); i++){
            if(tblItem.getValueAt(i, 0)!=null)
            sItem+=(sItem.length()==0? "" : ",") +"'"+tblItem.getValueAt(i, 0).toString()+"'";
        }

        String s="select * from (" +
                "select * from fn_r_gr_item_sisa_po('"+txtNoPO.getText()+"') as (no_po varchar, kode_item varchar," +
                "nama_item varchar, qty_order numeric, sisa numeric, on_receipt numeric, uom_po varchar, " +
                "harga double precision, disc double precision, ppn double precision, konv int, uom_kecil varchar, jml_kecil numeric, on_hand numeric)" +
                (sItem.length()>0? " where  kode_item not in("+sItem+") " : "")+
                "order by nama_item )x ";

        //System.out.println(s);

        d1.setTitle("Lookup Item from PO");
        d1.udfLoad(conn, s, "(kode_item||nama_item)", null);

        d1.setVisible(true);

        //System.out.println("Kode yang dipilih" +d1.getKode());
        if(d1.getKode().length()>0){
            TableColumnModel col=d1.getTable().getColumnModel();
            JTable tbl=d1.getTable();
            int iRow = tbl.getSelectedRow();

            ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                tbl.getValueAt(iRow, col.getColumnIndex("kode_item")).toString(),
                tbl.getValueAt(iRow, col.getColumnIndex("nama_item")).toString(),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("qty_order"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("sisa"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("on_receipt"))),
                tbl.getValueAt(iRow, col.getColumnIndex("uom_po")).toString(),
                "",
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("harga"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("disc"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("ppn"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("konv"))),
                tbl.getValueAt(iRow, col.getColumnIndex("uom_kecil")).toString(),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("jml_kecil"))),
                fn.udfGetDouble(tbl.getValueAt(iRow, col.getColumnIndex("on_hand")))
            });

            tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
            tblItem.requestFocusInWindow();
            tblItem.changeSelection(tblItem.getRowCount()-1, tblItem.getColumnModel().getColumnIndex("On Receipt"), false, false);
        }
    }

    

    private void udfLookupItemNonPO(KeyEvent evt){
        if(txtSupplier.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isi Supplier terlebih dulu!");
            if(!txtSupplier.isFocusOwner())
                txtSupplier.requestFocus();
            return;
        }
        lookupItem.setAlwaysOnTop(true);
        lookupItem.setSrcTable(tblItem, tblItem.getColumnModel().getColumnIndex("On Receipt"));
        lookupItem.setKeyEvent(evt);
        lookupItem.setObjForm(this);
        lookupItem.setVisible(true);
        lookupItem.clearText();
        lookupItem.requestFocusInWindow();
        if(lookupItem.getKodeBarang().length()>0){
            if(tblItem.getRowCount()==0 || (tblItem.getValueAt(tblItem.getRowCount()-1, 0)!=null && !tblItem.getValueAt(tblItem.getRowCount()-1, 0).toString().trim().equalsIgnoreCase("")) )
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    "", "", 0, 0, 1, "", "", 0, 0, 0, 1, "", 1, 0
                });
            tblItem.requestFocusInWindow();
            tblItem.setRowSelectionInterval(tblItem.getRowCount()-1, tblItem.getRowCount()-1);
            tblItem.setValueAt(lookupItem.getKodeBarang(), tblItem.getRowCount()-1, 0);
            tblItem.changeSelection(tblItem.getRowCount()-1, tblItem.getColumnModel().getColumnIndex("On Receipt"), false, false);

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
            if(vColIndex==tblItem.getColumnModel().getColumnIndex("Expired"))
                text=fText;
            else
                text=ustTextField;
            
            text.setName("textEditor");

            if(stMinus)
                text.setEditable(vColIndex==tblItem.getColumnModel().getColumnIndex("On Receipt"));

            if(vColIndex==tblItem.getColumnModel().getColumnIndex("On Receipt")||
                    vColIndex==tblItem.getColumnModel().getColumnIndex("Harga")||
                    vColIndex==tblItem.getColumnModel().getColumnIndex("Disc")||
                    vColIndex==tblItem.getColumnModel().getColumnIndex("PPN")){
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
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(FrmGR.this),
                                "Silakan isikan format tanggal dengan 'dd/MM/yy'\n" +
                                "Contoh: 31/12/19");
                                
                        retVal=tblItem.getValueAt(row, col).toString();
                        tblItem.requestFocusInWindow();
                        tblItem.changeSelection(row, tblItem.getColumnModel().getColumnIndex("Expired"), false, false);
                    }else
                        retVal = ((JTextField)text).getText();

                }else if(withPO && col==tblItem.getColumnModel().getColumnIndex("On Receipt")){
                    double sisaPR=fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("Sisa Order")));

//                    if(withPO && fn.udfGetDouble(((JTextField)text).getText())> sisaPR ){
//                        JOptionPane.showMessageDialog(FrmGR.this, "Jumlah On Receipt melebihi Qty Sisa PO \nQuantity PO adalah : "+
//                                fn.dFmt.format(sisaPR));
//                        o=fn.udfGetDouble(tblItem.getValueAt(row, tblItem.getColumnModel().getColumnIndex("On Receipt")));
//                        return o;
//                    }
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                }else
                    retVal = fn.udfGetDouble(((JTextField)text).getText());

                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    Vector tableCopyRow;
}
