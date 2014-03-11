/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Trx2.java
 *
 * Created on Dec 28, 2010, 10:36:17 AM
 */

package retail.sales;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import retail.DLgLookup;
import retail.DlgLookupItemJual;
import retail.MainForm;
import retail.PrintPenjualan;
import retail.main.GeneralFunction;
import retail.main.SysConfig;

/**
 *
 * @author ustadho
 */
public class TrxReturPenjualan extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private Connection conn;
    private JComboBox cmbSatuanTbl=new JComboBox();
    private Component aThis;
    private MyKeyListener kListener=new MyKeyListener();
    private GeneralFunction fn=new GeneralFunction();
    private DlgLookupItemJual lookupItem =new DlgLookupItemJual(this, true);
    ArrayList lstGudang=new ArrayList();
    MyTableCellEditor cEditor=new MyTableCellEditor();
    private boolean isKoreksi=false;
    private String sNoRetur;
    private String tglSkg;
    private Object ObjForm;
    private boolean stItemUpd=false;

    /** Creates new form Trx2 */
    public TrxReturPenjualan() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        //initConn();
        tblDetail.getTableHeader().setFont(tblDetail.getFont());
        tblDetail.setRowHeight(22);
        tblDetail.addKeyListener(kListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel2, kListener, txtFocusListener);
        AutoCompleteDecorator.decorate(cmbSatuanTbl);
        tblDetail.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"selectNextColumnCell");

//        tableModel=((DefaultTableModel)table.getModel());
//        tableModel.addTableModelListener(new InteractiveTableModelListener());

//        table.setSurrendersFocusOnKeystroke(true);
//        if (!hasEmptyRow()) {
//             addEmptyRow();
//        }
        
        tblDetail.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                TableColumnModel col=tblDetail.getColumnModel();
                udfSetTotal();
            }
        });
        
        tblDetail.getColumn("ProductID").setPreferredWidth(130);
        tblDetail.getColumn("Nama Barang").setPreferredWidth(300);
        tblDetail.getColumn("Sub Total").setPreferredWidth(120);
//        table.getColumn("Sub Total").setCellRenderer(new InteractiveRenderer(5));
//        table.getColumn("Konv").setCellRenderer(new InteractiveRenderer(6));

//        table.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuanTbl));
        
//        table.getColumn("ProductID").setCellEditor(cEditor);
//        table.getColumn("Qty").setCellEditor(cEditor);
//        table.getColumn("Harga").setCellEditor(cEditor);

        tblDetail.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblDetail.getSelectedRow();
                udfLoadComboKonv(iRow);
            }
        });
        aThis=this;
        cmbSatuanTbl.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuanTbl.getSelectedIndex()>0 && conn!=null)
                    udfLoadKonversi(cmbSatuanTbl.getSelectedItem().toString());

            }
        });

        cmbSatuanTbl.setFont(tblDetail.getFont());
        tblDetail.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            public void columnAdded(TableColumnModelEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void columnRemoved(TableColumnModelEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void columnMoved(TableColumnModelEvent e) {
                
            }

            public void columnMarginChanged(ChangeEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
                
                if(tblDetail.getSelectedColumn()==1)
                    tblDetail.setColumnSelectionInterval(2, 2);
            }
        });

    }

    private void udfSetTotal(){
        lblTotal.setText("0");
                
        if(tblDetail.getRowCount()>0){
            double total=0;
            for(int i=0; i< tblDetail.getRowCount(); i++){
                total+=fn.udfGetDouble(tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Sub Total")));
            }

            total=chkPersen.isSelected()? total*(1-fn.udfGetDouble(txtPot.getText())/100): total-fn.udfGetDouble(txtPot.getText());
            lblTotal.setText(fn.dFmt.format(Math.floor(total)));
        }
    }
    
    public void setObjForm(Object b){
        this.ObjForm=b;
    }

    private void udfLoadComboKonv(int iRow){
        
        if(iRow<0) return;
        if(tblDetail.getValueAt(iRow, 0)==null ||tblDetail.getValueAt(iRow, 0).toString().equalsIgnoreCase(""))
            return;

        try{
            
            cmbSatuan.removeAllItems();
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual " +
                         "from r_item where kode_item='"+tblDetail.getValueAt(iRow, 0).toString()+"'");
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

    public void setFlagKoreksi(boolean b){
        this.isKoreksi=b;
    }

    private void initConn(){
        String url = "jdbc:postgresql://localhost/KopSiloam";
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url,"tadho","ustasoft");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch(ClassNotFoundException ce) {
            System.out.println(ce.getMessage());
        }
    }

    public void setConn(Connection conn) {
        this.conn=conn;
    }

    private void udfInitForm() {
        lookupItem.setConn(conn);
        fn.setConn(conn);
        cmbGudang.setSelectedItem(MainForm.sNamaGudang);
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        JFormattedTextField jFDate1 = new JFormattedTextField(fmttgl);
//        jFJtTempo.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs=conn.createStatement().executeQuery("select kode_gudang, coalesce(nama_gudang,'') as nama_gudang, " +
                    "to_char(current_date, 'dd/MM/yyyy') as tgl " +
                    "from r_gudang order by 1");
            lstGudang.clear();
            cmbGudang.removeAllItems();
            
            while(rs.next()){
                lstGudang.add(rs.getString(1));
                cmbGudang.addItem(rs.getString(2));
//                jFJtTempo.setText(rs.getString("tgl"));
//                jFJtTempo.setValue(rs.getString("tgl"));
                txtDate.setText(rs.getString("tgl"));
                this.tglSkg=rs.getString("tgl");
            }
            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        if(isKoreksi)
            udfLoadKoreksiRetur();
        else
            udfNew();
        
        txtReturnNo.setEnabled(isKoreksi);
        txtNoTrx.setEnabled(!isKoreksi);
        txtCustomer.setEnabled(!isKoreksi);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(isKoreksi)
                    txtReturnNo.requestFocusInWindow();
                else
                    txtNoTrx.requestFocusInWindow();
            }
        });
    }

    private void udfNew() {
        cmbJenisRetur.setSelectedIndex(0);
        cmbTunaiKredit.setSelectedIndex(0);
        txtPot.setText("0"); chkPersen.setSelected(false);
        txtDate.setText(tglSkg);
        txtNoTrx.setText(""); txtReturnNo.setText("");
        txtReturnNo.setEnabled(isKoreksi);
        txtCustomer.setText(""); txtCustomer.setText("");
        ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
        lblTotal.setText("0");
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);   btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        
    }

    private void udfLoadKoreksiRetur(){
        String sQry="select r.no_retur, to_Char(r.tanggal, 'dd/MM/yyyy') as tanggal, coalesce(r.sales_no,'') as sales_no, to_char(s.sales_date,'dd/MM/yyyy') as sales_date," +
                "coalesce(r.kode_cust,'') as kode_cust, coalesce(c.nama,'') as nama_cust, coalesce(c.alamat_1,'') as alamat ,coalesce(g.nama_gudang,'') as nama_gudang," +
                "coalesce(r.keterangan,'') as keterangan, case when r.tunai_kredit='T' then 'TUNAI' else 'KREDIT' end as tunai_kredit, " +
                "case when r.jenis_retur='P' then 'Potong Tagihan' else 'Tukar Barang' end as jenis_retur, " +
                "coalesce(r.potongan,0) as potongan " +
                "from r_retur_jual r " +
                "inner join r_sales s on s.sales_no=r.sales_no " +
                "left join r_customer c on c.kode_cust=r.kode_cust " +
                "left join r_gudang g on g.kode_gudang=r.kode_gudang " +
                "where upper(r.no_retur)='"+txtReturnNo.getText().toUpperCase()+"'";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);
            if(rs.next()){
                txtReturnNo.setText(rs.getString("no_retur"));
                txtDate.setText(rs.getString("tanggal"));
                txtNoTrx.setText(rs.getString("sales_no"));
                txtCustomer.setText(rs.getString("kode_cust"));
                txtNamaCustomer.setText(rs.getString("nama_cust"));
                cmbJenisRetur.setSelectedItem(rs.getString("jenis_retur"));
                lblTgl.setText(rs.getString("sales_date"));
                cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtCatatan.setText(rs.getString("keterangan"));
                cmbTunaiKredit.setSelectedItem(rs.getString("tunai_kredit"));
                txtPot.setText(fn.dFmt.format(rs.getDouble("potongan")));
                chkPersen.setSelected(false);
                rs.close();
                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                
                sQry="select d.kode_item, coalesce(i.nama_item,'') as nama_item, coalesce(d.qty,0) as qty, " +
                        "coalesce(d.unit,'') as unit, coalesce(d.unit_price,0) as unit_price, " +
                        "coalesce(d.qty,0) * coalesce(d.unit_price,0) as sub_Total, " +
                        "coalesce(konv,0) as konv, coalesce(x.sisa,0) as sisa " +
                        "from r_retur_jual_detail d " +
                        "inner join r_item i on i.kode_item=d.kode_item  " +
                        "left join(select * from fn_r_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as " +
                        "   (kode_item varchar, nama_item varchar, " +
                        "   unit_price double precision, sisa double precision, unit varchar)" +
                        ")x on x.kode_item=d.kode_item " +
                        "where d.no_retur='"+txtReturnNo.getText()+"'";

                ((DefaultTableModel)tblDetail.getModel()).setNumRows(0);
                rs=conn.createStatement().executeQuery(sQry);
                while(rs.next()){
                    ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                        rs.getString("kode_item"),
                        rs.getString("nama_item"),
                        rs.getString("unit"),
                        rs.getDouble("qty"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("sub_Total"),
                        rs.getDouble("konv"),
                        rs.getDouble("sisa")+rs.getDouble("qty"),
                    });
                }
                udfSetTotal();
                //addEmptyRow();
                if(tblDetail.getRowCount()>0)
                    tblDetail.setRowSelectionInterval(0, 0);

            }else{
                JOptionPane.showMessageDialog(this, "No. Retur tidak ditemukan!");
                udfNew();
                if(!txtReturnNo.isFocusOwner())
                    txtReturnNo.requestFocus();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

    }

    public void setNoTrx(String toString) {
        txtNoTrx.setText(toString);
    }

    public void setNoRetur(String sNoRetur) {
        this.sNoRetur=sNoRetur;
        this.txtReturnNo.setText(sNoRetur);
    }

    public class InteractiveTableModelListener implements TableModelListener {
         public void tableChanged(TableModelEvent evt) {
             if (evt.getType() == TableModelEvent.UPDATE || evt.getType() == TableModelEvent.INSERT) {
                 int column = evt.getColumn();
                 int row = evt.getFirstRow();
                 System.out.println("row: " + row + " column: " + column);
                 //table.setColumnSelectionInterval(column + (column==0? 2: 1), column + (column==0? 2: 1));

                 if(column<tblDetail.getColumnCount()-1)
                    tblDetail.setColumnSelectionInterval(column + 1, column + 1);

                 try{
                    if(column==tblDetail.getColumnModel().getColumnIndex("ProductID") ){
                        if(tblDetail.getValueAt(row, column).toString().length()>0){
                            String sQry="select x.*, coalesce(unit2,'') as unit2, coalesce(unit3,'') as unit3 " +
                                    "from(select * from fn_r_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as " +
                                    "(kode_item varchar, nama_item varchar, " + 
                                    "unit_price double precision, sisa double precision, unit varchar) " +
                                    "where kode_item='"+tblDetail.getValueAt(row, column).toString()+"')x " +
                                    "inner join  r_item i on i.kode_item=x.kode_item ";
                             //System.out.println(sQry);
                             ResultSet rs=conn.createStatement().executeQuery(sQry);
                             cmbSatuan.removeAllItems();
                             if(rs.next()){
                                 tblDetail.setValueAt(rs.getString("nama_item"), row, tblDetail.getColumnModel().getColumnIndex("Nama Barang"));
                                 if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                                 if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                                 if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                                 tblDetail.setValueAt(rs.getString("unit"), row, tblDetail.getColumnModel().getColumnIndex("Satuan"));
                                 tblDetail.setValueAt(1, row, tblDetail.getColumnModel().getColumnIndex("Qty"));
                                 tblDetail.setValueAt(1, row, tblDetail.getColumnModel().getColumnIndex("Konv"));
                                 tblDetail.setValueAt(rs.getDouble("unit_price"), row, tblDetail.getColumnModel().getColumnIndex("Harga"));
                                 tblDetail.setValueAt(rs.getDouble("sisa"), row, tblDetail.getColumnModel().getColumnIndex("Sisa"));
                                 tblDetail.setColumnSelectionInterval(2, 2);
                                 
                                 //udfLoadComboKonv(row);

                             }else{
                                 JOptionPane.showMessageDialog(aThis, "Item tidak ditemukan!");
                                 
                             }
                             rs.close();
                        }else{
                            //tableModel.removeRow(row);
                            udfClearRow(row);
                        }
                    }else if(column==tblDetail.getColumnModel().getColumnIndex("Satuan")){
                        udfLoadKonversi(tblDetail.getValueAt(row, column).toString());
                    }else if(column==tblDetail.getColumnModel().getColumnIndex("Qty")||column==tblDetail.getColumnModel().getColumnIndex("Harga")){
                        tblDetail.setValueAt(
                                fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Qty")))*
                                fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Harga"))),
                                row, tblDetail.getColumnModel().getColumnIndex("Sub Total"));
                    }
                    double dTotal=0;
                    for(int i=0; i<tblDetail.getRowCount(); i++){
                        if(tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Sub Total"))!=null)
                            dTotal+=fn.udfGetDouble(tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Sub Total")));
                    }
                    lblTotal.setText(fn.dFmt.format(dTotal));

                 }catch(SQLException se){
                        JOptionPane.showMessageDialog(null, se.getMessage());
                     }
                 tblDetail.setRowSelectionInterval(row, row);
             }
         }


     }

    private void udfClearRow(int row){
        tblDetail.setValueAt("", row, 0);
        tblDetail.setValueAt("", row, 1);
        tblDetail.setValueAt("1", row, 2);
        tblDetail.setValueAt("", row, 3);
        tblDetail.setValueAt(0, row, 4);
        tblDetail.setValueAt(0, row, 5);
        tblDetail.setValueAt(1, row, 6);

    }

    private void udfChangePriceAll(){
        if(tblDetail.getRowCount()<=0) return;
        try{
            ResultSet rs=null;
            TableColumnModel col=tblDetail.getColumnModel();
            String sCustType=cmbJenisRetur.getSelectedIndex()==0? "G": "R";
            String sQry="", sUnit;
            for (int i=0; i<tblDetail.getRowCount(); i++){
                sUnit=tblDetail.getValueAt(i, col.getColumnIndex("Satuan")).toString();
                sQry="select coalesce(nama_item,'') as nama_item, " +
                     "coalesce(unit,'') as unit, " +
                     "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                     "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                     "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                     "case  when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit,'') then harga_g_1 " +
                     "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit2,'') then harga_g_2 " +
                     "      when '"+sCustType+"'='G' and '"+sUnit+"'=coalesce(unit3,'') then harga_g_3 " +
                     "      when '"+sCustType+"'='R' and '"+sUnit+"'=coalesce(unit,'') then harga_r_1 " +
                     "      when '"+sCustType+"'='R' and '"+sUnit+"'=coalesce(unit2,'') then harga_r_2 " +
                     "      when '"+sCustType+"'='R' and '"+sUnit+"'=coalesce(unit3,'') then harga_r_3 " +
                     "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                     "end as harga " +
                     "from r_item i " +
                     "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                     "where i.kode_item='"+tblDetail.getValueAt(i, 0).toString()+"'";
                rs=conn.createStatement().executeQuery(sQry);
                if(rs.next()){
                    tblDetail.setValueAt(rs.getDouble("harga"), i, col.getColumnIndex("Harga"));
                }
                rs.close();
            }


        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadKonversi(String sUnit) {
        int row=tblDetail.getSelectedRow();
        if(row<0) return;
        try {
            String sCustType=cmbJenisRetur.getSelectedItem().toString().substring(0, 1);
            String sQry = "select case  when '" + sUnit + "'=unit then 1 " +
                          "             when '" + sUnit + "'=unit2 then coalesce(konv2,1) " +
                          "             when '" + sUnit + "'=unit3 then coalesce(konv3,1) " +
                          "             else 1 end as konv, " +
                          "case  when '"+sCustType+"'='G' and '" + sUnit + "'=coalesce(unit,'') then harga_g_1 " +
                          "      when '"+sCustType+"'='G' and '" + sUnit + "'=coalesce(unit2,'') then harga_g_2 " +
                          "      when '"+sCustType+"'='G' and '" + sUnit + "'=coalesce(unit3,'') then harga_g_3 " +
                          "      when '"+sCustType+"'='R' and '" + sUnit + "'=coalesce(unit,'') then harga_r_1 " +
                          "      when '"+sCustType+"'='R' and '" + sUnit + "'=coalesce(unit2,'') then harga_r_2 " +
                          "      when '"+sCustType+"'='R' and '" + sUnit + "'=coalesce(unit3,'') then harga_r_3  " +
                          "end as harga " +
                          "from r_item i " +
                          "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                          "where i.kode_item='" + tblDetail.getValueAt(row, 0).toString() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                tblDetail.setValueAt(rs.getDouble("harga"), row, tblDetail.getColumnModel().getColumnIndex("Harga"));
                tblDetail.setValueAt(rs.getInt("konv"), row, tblDetail.getColumnModel().getColumnIndex("Konv"));
            } else {
                tblDetail.setValueAt(0, row, tblDetail.getColumnModel().getColumnIndex("Harga"));
                tblDetail.setValueAt(1, row, tblDetail.getColumnModel().getColumnIndex("Konv"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrxReturPenjualan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class InteractiveRenderer extends DefaultTableCellRenderer {
         protected int interactiveColumn;

         public InteractiveRenderer(int interactiveColumn) {
             this.interactiveColumn = interactiveColumn;
         }

         public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column){
             Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
             //if (column == interactiveColumn && hasFocus ) {
             //if (column == interactiveColumn && hasFocus ) {
             if (column >=interactiveColumn && hasFocus ) {
                 if ((tableModel.getRowCount() - 1) == row &&
                    !hasEmptyRow()){
                     addEmptyRow();
                 }

                 highlightLastRow(row);
             }
             setHorizontalAlignment(JLabel.RIGHT);
             if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }

            setValue(value);
             return c;
         }
     }

    public void highlightLastRow(int row) {
         int lastrow = tableModel.getRowCount();
         if (row == lastrow - 1) {
             tblDetail.setRowSelectionInterval(lastrow - 1, lastrow - 1);
         } else {
             tblDetail.setRowSelectionInterval(row + 1, row + 1);
         }

         tblDetail.setColumnSelectionInterval(0, 0);
     }

    public boolean hasEmptyRow() {
         if (tableModel.getRowCount() == 0) return false;
         int row=tableModel.getRowCount()-1;

         if((tableModel.getValueAt(row, 0)==null || tableModel.getValueAt(row, 0).toString().trim().equals("")) &&
            (tableModel.getValueAt(row, 1)==null || tableModel.getValueAt(row, 1).toString().trim().equals(""))
            //(tableModel.getValueAt(row, 3)==null || tableModel.getValueAt(row, 3).toString().trim().equals(""))
            ){
            return true;
         }
         else return false;
     }


    public void addEmptyRow() {
         tableModel.addRow(new Object[]{"", "", 1, "",0, 0, 1});
         tableModel.fireTableRowsInserted(tableModel.getRowCount() - 1,tableModel.getRowCount() - 1);
         tblDetail.requestFocusInWindow();
         tblDetail.setRowSelectionInterval(tblDetail.getRowCount()-1, tblDetail.getRowCount()-1);
         tblDetail.changeSelection(tableModel.getRowCount()-1, 0, false, false);

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
                if(tblDetail.getSelectedColumn()==0){
                    retVal = ((JTextField)text).getText();
                }else if(tblDetail.getSelectedColumn()==tblDetail.getColumnModel().getColumnIndex("Qty")){
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                    if(fn.udfGetDouble(((JTextField)text).getText())>fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Sisa")))){
                        JOptionPane.showMessageDialog(aThis, "Jumlah yang diretur melebihi Sisa transaksi!\n" +
                                "Sisa transaksi adalah "+fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Sisa"))));
                        retVal = fn.udfGetDouble(tblDetail.getValueAt(row, tblDetail.getColumnModel().getColumnIndex("Sisa")));
                    }
                }else
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

        private void setValue(String toString) {
            text.setText(toString);
        }
    }

     private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField )
                        ){
//                if(e.getSource().equals(txtQty)){
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
                if(e.getSource().equals(txtNoTrx) && aThis.isShowing() && aThis.isFocusable() && txtNoTrx.getText().trim().length()>0)
                    udfLoadSales();
                else if(e.getSource().equals(txtReturnNo) && aThis.isShowing() && aThis.isFocusable() && txtReturnNo.getText().trim().length()>0)
                    udfLoadKoreksiRetur();
           }
        }
    } ;

     private void udfLoadSales(){
         try{
             String sQry="select s.sales_no, to_char(s.sales_date,'dd/MM/yyyy') as tgl_penjualan, coalesce(s.kode_cust,'') as kode_cust, "
                     + "coalesce(c.nama,'') as nama_cust, coalesce(s.kode_gudang,'') as kode_gudang, coalesce(g.nama_gudang,'') as nama_gudang "
                     + "from r_sales s "
                     + "left join r_customer c on c.kode_cust=s.kode_cust "
                     + "left join r_gudang g on g.kode_gudang=s.kode_gudang "
                     + "where upper(s.sales_no)='"+txtNoTrx.getText().toUpperCase()+"'";
             
             ResultSet rs=conn.createStatement().executeQuery(sQry);
             if(rs.next()){
                 udfNew();
                 
                 txtNoTrx.setText(rs.getString("sales_no"));
                 lblTgl.setText(rs.getString("tgl_penjualan"));
                 txtCustomer.setText(rs.getString("kode_cust"));
                 txtNamaCustomer.setText(rs.getString("nama_cust"));
                 cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                 
             }else{
                 JOptionPane.showMessageDialog(this, "No. Transaksi penjualan tidak ditemukan!");
                 udfNew();
                 if(!txtNoTrx.isFocusOwner())
                     txtNoTrx.requestFocus();
             }
             rs.close();
             
         }catch(SQLException se){
             JOptionPane.showMessageDialog(this, se.getMessage());
         }
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
                    
                    if(cmbJenisRetur.getSelectedIndex()<0){
                        JOptionPane.showMessageDialog(aThis, "Pilih harga penjualan terlebih dulu!");
                        cmbJenisRetur.requestFocus();
                        return;
                    }
                    if(tblDetail.getCellEditor()!=null && evt.getSource().equals(tblDetail))
                        tblDetail.getCellEditor().stopCellEditing();

                    udfLookupItemRetur2(evt);
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
                        if((txtQty.isFocusOwner()) || txtHarga.isFocusOwner()){
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
                    if(tblDetail.getSelectedColumn()==2)
                        tblDetail.setColumnSelectionInterval(0, 0);
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblDetail) && tblDetail.getSelectedRow()>=0){
                        if(tblDetail.getCellEditor()!=null)
                            tblDetail.getCellEditor().stopCellEditing();
                        
                        int iRow[]= tblDetail.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        TableModel tm= tblDetail.getModel();

                        while(iRow.length>0) {
                            //JOptionPane.showMessageDialog(null, iRow[0]);
                            ((DefaultTableModel)tm).removeRow(tblDetail.convertRowIndexToModel(iRow[0]));
                            iRow = tblDetail.getSelectedRows();
                        }
                        tblDetail.clearSelection();

                        if(tblDetail.getRowCount()>0 && rowPalingAtas<tblDetail.getRowCount()){
                            tblDetail.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }else{
                            if(tblDetail.getRowCount()>0)
                                tblDetail.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                tblDetail.requestFocus();
                        }
                        if(tblDetail.getSelectedRow()>=0){
                            tblDetail.changeSelection(tblDetail.getSelectedRow(), 0, false, false);
                            cEditor.setValue(tblDetail.getValueAt(tblDetail.getSelectedRow(), 0).toString());
                        }
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
        if(fn.udfGetDouble(txtQty.getText())>fn.udfGetDouble(lblSisa.getText())){
            if(!txtQty.isFocusOwner())
                txtQty.requestFocus();
            JOptionPane.showMessageDialog(this, "Qty retur melebihi sisa penjualan!\n"
                    + "Sisa penjualan adalah: "+lblSisa.getText());
            
            return;
        }
        TableColumnModel col=tblDetail.getColumnModel();
        
        if(stItemUpd){
            int iRow=tblDetail.getSelectedRow();
            if(iRow<0) return;
            tblDetail.setValueAt(txtKode.getText(), iRow, col.getColumnIndex("ProductID"));
            tblDetail.setValueAt(lblItem.getText(), iRow, col.getColumnIndex("Nama Barang"));
            tblDetail.setValueAt(cmbSatuan.getSelectedItem().toString(), iRow, col.getColumnIndex("Satuan"));
            tblDetail.setValueAt(fn.udfGetInt(txtQty.getText()), iRow, col.getColumnIndex("Qty"));
            tblDetail.setValueAt(fn.udfGetDouble(txtHarga.getText()), iRow, col.getColumnIndex("Harga"));
            tblDetail.setValueAt(fn.udfGetDouble(lblSubTotal.getText()), iRow, col.getColumnIndex("Sub Total"));
            tblDetail.setValueAt(fn.udfGetInt(lblKonv.getText()), iRow, col.getColumnIndex("Konv"));
            tblDetail.setValueAt(fn.udfGetInt(lblSisa.getText()), iRow, col.getColumnIndex("Sisa"));
            tblDetail.changeSelection(iRow, iRow, false, false);
        }else{
            String sUnit="";
            for(int i=0; i<tblDetail.getRowCount(); i++){
                sUnit=cmbSatuan.getSelectedIndex()<0? "": cmbSatuan.getSelectedItem().toString();
                if(tblDetail.getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) && tblDetail.getValueAt(i, tblDetail.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(sUnit)){
                    if(JOptionPane.showConfirmDialog(this, ("Item tersebut sudah dimasukkan pada baris ke "+(i+1)+".\nQty Item akan ditambahkan?"), "Item exists", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
                        return;
                    else{
                        double total=fn.udfGetInt(txtQty.getText())+fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty")));
                        tblDetail.setValueAt(total, i, col.getColumnIndex("Qty"));
                        tblDetail.setValueAt(total*fn.udfGetDouble(lblSubTotal.getText()), i, col.getColumnIndex("Sub Total"));
                        udfClearItem();
                        txtKode.requestFocusInWindow();
                        return;
                    }
                }
            }
            ((DefaultTableModel)tblDetail.getModel()).addRow(new Object[]{
                txtKode.getText(),
                lblItem.getText(),
                (cmbSatuan.getSelectedItem()==null? "": cmbSatuan.getSelectedItem().toString()) ,
                fn.udfGetDouble(txtQty.getText()),
                fn.udfGetDouble(txtHarga.getText()),
                fn.udfGetDouble(lblSubTotal.getText()),
                fn.udfGetInt(lblKonv.getText()),
                fn.udfGetDouble(lblSisa.getText()),
            });
        tblDetail.setRowSelectionInterval(((DefaultTableModel)tblDetail.getModel()).getRowCount()-1, ((DefaultTableModel)tblDetail.getModel()).getRowCount()-1);
        tblDetail.changeSelection(((DefaultTableModel)tblDetail.getModel()).getRowCount()-1, ((DefaultTableModel)tblDetail.getModel()).getRowCount()-1, false, false);
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
        lblSisa.setText("0");
        stItemUpd=false;
    }
    
    private void udfLookupItemRetur2(KeyEvent evt){
        if(tblDetail.getCellEditor()!=null && evt.getSource().equals(tblDetail))
                tblDetail.getCellEditor().stopCellEditing();
        
        String sItem="";
        for(int i=0; i< tblDetail.getRowCount(); i++){
            sItem+=(sItem.length()==0? "" : ",") +"'"+tblDetail.getValueAt(i, 0).toString()+"'";
        }
        String sQry="select * from(select * from fn_r_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as (kode_item varchar, nama_item varchar, " +
                "unit_price double precision, sisa double precision, unit varchar) " +
                (sItem.length()>0? " where  kode_item not in("+sItem+") " : "")+
                ")x ";
        DLgLookup d1=new DLgLookup(this, true);
        d1.udfLoad(conn, sQry, "(kode_item||nama_item)", txtKode);
        d1.setTitle("Lookup item retur");
        d1.setVisible(true);
        if(d1.getKode().length()>0) {
            txtKode.setText(d1.getKode());
                String sMsg=udfLoadItem();
                if(sMsg.length()>0){
                    JOptionPane.showMessageDialog(aThis, sMsg);
                    //if(!txtKode.isFocusOwner())
                        txtKode.requestFocus();
                    return;
                }
                txtQty.requestFocus();
        }
    }

    private String udfLoadItem(){
        String sMsg="";
        try{
            if(txtKode.getText().length()>0){
                String sQry="select x.*, coalesce(unit2,'') as unit2, coalesce(unit3,'') as unit3 " +
                                    "from(select * from fn_r_retur_jual_item_sisa('"+txtNoTrx.getText()+"') as " +
                                    "(kode_item varchar, nama_item varchar, " + 
                                    "unit_price double precision, sisa double precision, unit varchar) " +
                                    "where kode_item='"+txtKode.getText()+"')x " +
                                    "inner join  r_item i on i.kode_item=x.kode_item or coalesce(i.barcode,'')='"+txtKode.getText()+"' ";
                System.out.println(sQry);
                 ResultSet rs=conn.createStatement().executeQuery(sQry);
                 cmbSatuan.removeAllItems();
                 if(rs.next()){
                     lblItem.setText(rs.getString("nama_item"));
                     if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                     if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                     if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                     //cmbSatuan.setSelectedItem(rs.getString("unit_jual"));
                     lblKonv.setText("1");
                     txtHarga.setText(fn.dFmt.format(rs.getDouble("unit_price")));
                     txtQty.setText("1");
                     lblSisa.setText(fn.dFmt.format(rs.getDouble("sisa")));
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
    
    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(tblDetail.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada item yang ditransaksikan!\nTekan insert untuk menambahkan item penjualan");
            tblDetail.requestFocus();
            return false;
        }
        if(cmbTunaiKredit.getSelectedItem().toString().equalsIgnoreCase("KREDIT") && txtCustomer.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Isikan nama customer untuk penjualan kredit");
            txtCustomer.requestFocus();
            return false;
        }
        if(isKoreksi && (tblDetail.getRowCount()==0 || (tblDetail.getRowCount()==1 && (tblDetail.getValueAt(0, 0)==null||tblDetail.getValueAt(0, 0).toString().equalsIgnoreCase(""))))){
            b=JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk membatalkan transaksi retur ini?", "Batal Retur", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
        }
        return b;
    }

    private void udfSave(){
        if(!udfCekBeforeSave()) return;
         String sSql="", sMsg="";
         ResultSet rs=null;
         try{
             conn.setAutoCommit(false);
             if(isKoreksi){
                 rs=conn.createStatement().executeQuery("select fn_r_retur_jual_koreksi('"+txtReturnNo.getText()+"', '"+MainForm.sUserName+"')");
                 if(rs.next()){
                     sMsg="Retur dikoreksi dengan nomor '"+rs.getString(1)+"'";
                 }
             }
             if(tblDetail.getRowCount()==0) {
                 conn.setAutoCommit(true);
                 JOptionPane.showMessageDialog(aThis, sMsg);
                 return;
             }

             double dPotongan=chkPersen.isSelected()? fn.udfGetDouble(lblTotal.getText())/100*fn.udfGetDouble(txtPot.getText()): fn.udfGetDouble(txtPot.getText());
             
             rs=conn.createStatement().executeQuery("select fn_r_get_retur_jual_no('"+fn.yyyymmdd_format.format(new SimpleDateFormat("dd/MM/yyyy").parse(lblTgl.getText()))+"')");
             if(rs.next())
                 txtReturnNo.setText(rs.getString(1));

             sSql="INSERT INTO r_retur_jual(" +
                  "no_retur, tanggal, kode_cust, sales_no, kode_gudang, " +
                  "date_ins, user_ins, date_upd, user_upd, potongan, tunai_kredit, " +
                  "jenis_retur, keterangan, flag_trx) " +
                  "VALUES ('"+txtReturnNo.getText()+"', '"+new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText())+"', '"+txtCustomer.getText()+"', '"+txtNoTrx.getText()+"', '"+lstGudang.get(cmbGudang.getSelectedIndex()).toString()+"', " +
                  "now(), '"+MainForm.sUserName+"', "+(isKoreksi? "now()": "null")+", "+(isKoreksi? "'"+MainForm.sUserName+"'": "null")+", "+dPotongan+", " +
                  "'"+cmbTunaiKredit.getSelectedItem().toString().substring(0, 1)+"', '"+cmbJenisRetur.getSelectedItem().toString().substring(0, 1)+"', " +
                  "'"+txtCatatan.getText()+"', 'T'); ";

             TableColumnModel col=tblDetail.getColumnModel();
             for(int i=0; i< tblDetail.getRowCount(); i++){
                 if(tblDetail.getValueAt(i, col.getColumnIndex("ProductID"))!=null &&
                   tblDetail.getValueAt(i, col.getColumnIndex("ProductID")).toString().length()>0){
                    sSql+="INSERT INTO r_retur_jual_detail(no_retur, kode_item, qty, unit_price, " +
                            "date_ins, user_ins, unit, konv) values(" +
                            "'"+txtReturnNo.getText()+"', " +
                            "'"+tblDetail.getValueAt(i, col.getColumnIndex("ProductID"))+"', " +
                            ""+fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Qty")))+", " +
                            ""+fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Harga")))+", " +
                            "now(), '"+MainForm.sUserName+"', " +
                            "'"+tblDetail.getValueAt(i, col.getColumnIndex("Satuan")).toString()+ "'," +
                            ""+fn.udfGetDouble(tblDetail.getValueAt(i, col.getColumnIndex("Konv")))+");";
                 }
             }

             //System.out.println(sSql);

             
             int i=conn.createStatement().executeUpdate(sSql);
             conn.setAutoCommit(true);
             sMsg+=sMsg.length()>0? "\n":""+"Simpan retur penjualan sukses!";
              JOptionPane.showMessageDialog(this, sMsg);
             //udfPreviewRetur();
             if(ObjForm!=null && ObjForm instanceof FrmReturPenjualanHistory)
                 ((FrmReturPenjualanHistory)ObjForm).udfLoadRetur();
             if(isKoreksi)
                 this.dispose();

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
                JOptionPane.showMessageDialog(this, se1.getMessage());
            }
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
            PrintPenjualanRetur pn = new PrintPenjualanRetur(conn, txtNoTrx.getText(), MainForm.sUserName,services[i]);
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
        tblDetail = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cmbJenisRetur = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNamaCustomer = new javax.swing.JTextField();
        txtCustomer = new javax.swing.JTextField();
        lblTgl = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbTunaiKredit = new javax.swing.JComboBox();
        cmbGudang = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtReturnNo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtPot = new javax.swing.JTextField();
        chkPersen = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtQty = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        cmbSatuan = new javax.swing.JComboBox();
        txtKode = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        lblKonv = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        lblSisa = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Retur Penjualan");
        setBackground(new java.awt.Color(204, 204, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblDetail.setFont(new java.awt.Font("Tahoma", 0, 12));
        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Satuan", "Qty", "Harga", "Sub Total", "Konv", "Sisa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblDetail);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 203, 830, 170));

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbJenisRetur.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbJenisRetur.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Potong Tagihan", "Tukar Barang" }));
        cmbJenisRetur.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbJenisReturItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbJenisRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 150, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Pelanggan");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText("Jenis Retur");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 90, 20));

        txtNamaCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaCustomer.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaCustomer.setEnabled(false);
        jPanel1.add(txtNamaCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 410, 20));

        txtCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.setEnabled(false);
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel1.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 130, 20));

        lblTgl.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTgl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 35, 90, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel5.setText("No. Penjualan");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tgl. Penjualan ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 35, 90, 20));

        cmbTunaiKredit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbTunaiKredit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbTunaiKredit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTunaiKreditItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbTunaiKredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 35, 150, -1));

        cmbGudang.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Center" }));
        cmbGudang.setEnabled(false);
        jPanel1.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 60, 150, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel7.setText("Potongan");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 85, 90, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel9.setText("Catatan");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtCatatan.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCatatanKeyReleased(evt);
            }
        });
        jPanel1.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 410, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel10.setText("Pembayaran");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 35, 90, 20));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel11.setText("Retur #");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 70, 20));

        txtReturnNo.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtReturnNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtReturnNo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtReturnNo.setEnabled(false);
        jPanel1.add(txtReturnNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 130, 20));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel12.setText("Site");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, 90, 20));

        txtPot.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtPot.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPot.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPotKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPotKeyTyped(evt);
            }
        });
        jPanel1.add(txtPot, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 85, 80, 20));

        chkPersen.setFont(new java.awt.Font("Tahoma", 0, 12));
        chkPersen.setText("Persen");
        chkPersen.setOpaque(false);
        chkPersen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkPersenItemStateChanged(evt);
            }
        });
        jPanel1.add(chkPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 85, 70, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 830, 112));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel8.setText("<html>\n<b>F4</b> &nbsp&nbsp : Membuat transaksi baru  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Insert</b> &nbsp : Menambah item barang  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Del</b> &nbsp&nbsp &nbsp &nbsp : Menghapus item barang <br>\n<b>F5</b> &nbsp&nbsp : Menyimpan Transaksi <br>\n</html>"); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setOpaque(true);
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 830, 40));

        lblTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 34));
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0,00");
        lblTotal.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        lblTotal.setOpaque(true);
        getContentPane().add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 320, 40));

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

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/Exit.png"))); // NOI18N
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

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 173, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Tgl. Retur");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(316, 30, 70, 20));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText(":");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(386, 30, 10, 20));

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
        getContentPane().add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(396, 30, 120, 20));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtQty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtQty.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 60, 20));

        lblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 0, 290, 20));

        cmbSatuan.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbSatuan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 110, 20));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 120, 20));

        txtHarga.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 100, 20));

        lblKonv.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblKonv.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 0, 40, 20));

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 0, 110, 20));

        lblSisa.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblSisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblSisa, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 0, 0, 20));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 182, 830, 20));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-861)/2, (screenSize.height-465)/2, 861, 465);
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
        //printKwitansi(txtNoPO.getText(), false);
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

    private void txtCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerKeyReleased
        fn.lookup(evt, new Object[]{txtNamaCustomer}, "select kode_cust, coalesce(nama,'') as nama_customer from r_customer " +
                "where kode_cust||coalesce(nama,'') ilike '%"+txtCustomer.getText()+"%'", 500, 200);
    }//GEN-LAST:event_txtCustomerKeyReleased

    private void txtCatatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCatatanKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCatatanKeyReleased

    private void cmbTunaiKreditItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTunaiKreditItemStateChanged
//        jLabel3.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
//        jFJtTempo.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
    }//GEN-LAST:event_cmbTunaiKreditItemStateChanged

    private void cmbJenisReturItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbJenisReturItemStateChanged
        udfChangePriceAll();
    }//GEN-LAST:event_cmbJenisReturItemStateChanged

    private void txtPotKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPotKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtPotKeyTyped

    private void txtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDateFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateFocusLost

    private void txtDateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDateKeyReleased

    private void chkPersenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkPersenItemStateChanged
        udfSetTotal();
    }//GEN-LAST:event_chkPersenItemStateChanged

    private void txtPotKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPotKeyReleased
        udfSetTotal();
    }//GEN-LAST:event_txtPotKeyReleased

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    BorderUIResource borderUIResource= new BorderUIResource(BorderFactory.createLineBorder(Color.yellow, 2));
                    UIManager.put("Table.focusCellHighlightBorder", borderUIResource);
                } catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Couldn't load Windows look and feel " + e);
                }
                new TrxReturPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkPersen;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbJenisRetur;
    private javax.swing.JComboBox cmbSatuan;
    private javax.swing.JComboBox cmbTunaiKredit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JLabel lblSisa;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JLabel lblTgl;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNamaCustomer;
    private javax.swing.JTextField txtNoTrx;
    private javax.swing.JTextField txtPot;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtReturnNo;
    // End of variables declaration//GEN-END:variables

}
