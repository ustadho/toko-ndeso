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
import java.awt.event.ActionEvent;
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
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import retail.DlgLookupItemJual;
import retail.DlgPembayaran;
import retail.MainForm;
import retail.PrintPenjualan;
import retail.main.GeneralFunction;
import retail.main.SysConfig;

/**
 *
 * @author ustadho
 */
public class TrxPenjualan extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private Connection conn;
    private JComboBox cmbSatuan=new JComboBox();
    private Component aThis;
    private MyKeyListener kListener=new MyKeyListener();
    private GeneralFunction fn=new GeneralFunction();
    private DlgLookupItemJual lookupItem =new DlgLookupItemJual(this, true);
    ArrayList lstGudang=new ArrayList();
    MyTableCellEditor cEditor=new MyTableCellEditor();
    private boolean isKoreksi=false;

    /** Creates new form Trx2 */
    public TrxPenjualan() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        //initConn();
        table.getTableHeader().setFont(table.getFont());
        table.setRowHeight(22);
        table.addKeyListener(kListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        AutoCompleteDecorator.decorate(cmbSatuan);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"selectNextColumnCell");

        tableModel=((DefaultTableModel)table.getModel());
        tableModel.addTableModelListener(new InteractiveTableModelListener());

        table.setSurrendersFocusOnKeystroke(true);
        if (!hasEmptyRow()) {
             addEmptyRow();
        }
        table.getColumn("ProductID").setPreferredWidth(130);
        table.getColumn("Nama Barang").setPreferredWidth(300);
        table.getColumn("Sub Total").setPreferredWidth(120);
        table.getColumn("Sub Total").setCellRenderer(new InteractiveRenderer(5));
        //table.getColumn("Konv").setCellRenderer(new InteractiveRenderer(6));

        table.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
        
        table.getColumn("ProductID").setCellEditor(cEditor);
        table.getColumn("Qty").setCellEditor(cEditor);
        table.getColumn("Harga").setCellEditor(cEditor);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=table.getSelectedRow();
                udfLoadComboKonv(iRow);
            }
        });
        //cmbSatuan.addKeyListener(kListener);
        aThis=this;
        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>=0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });
//        cmbSatuan.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if ((table.getRowCount() - 1) == table.getSelectedRow() &&
//                    !hasEmptyRow()){
//                     addEmptyRow();
//                 }
//            }
////        });
        cmbSatuan.setFont(table.getFont());
        table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
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
                
                if(table.getSelectedColumn()==1)
                    table.setColumnSelectionInterval(2, 2);
            }
        });

    }

    private void udfLoadComboKonv(int iRow){
        
        if(iRow<0) return;
        if(table.getValueAt(iRow, 0)==null ||table.getValueAt(iRow, 0).toString().equalsIgnoreCase(""))
            return;

        try{
            cmbSatuan.removeAllItems();
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual " +
                         "from r_item where kode_item='"+table.getValueAt(iRow, 0).toString()+"'");
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
                    table.requestFocusInWindow();
            }
        });
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
        addEmptyRow();

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
                        rs.getDouble("qty"),
                        rs.getString("unit_jual"),
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

    public void setNoTrx(String toString) {
        txtNoTrx.setText(toString);
    }

    public class InteractiveTableModelListener implements TableModelListener {
         public void tableChanged(TableModelEvent evt) {
             if (evt.getType() == TableModelEvent.UPDATE || evt.getType() == TableModelEvent.INSERT) {
                 int column = evt.getColumn();
                 int row = evt.getFirstRow();
                 System.out.println("row: " + row + " column: " + column);
                 //table.setColumnSelectionInterval(column + (column==0? 2: 1), column + (column==0? 2: 1));

                 if(column<table.getColumnCount()-1)
                    table.setColumnSelectionInterval(column + 1, column + 1);

                 try{
                    if(column==table.getColumnModel().getColumnIndex("ProductID") ){
                        String sCustType=cmbCustType.getSelectedIndex()==0? "G": "R";
                        if(table.getValueAt(row, column).toString().length()>0){
                            String sQry="select coalesce(nama_item,'') as nama_item, " +
                                     "coalesce(unit,'') as unit, " +
                                     "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                                     "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                                     "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual, " +
                                     "case  when '"+sCustType+"'='G' and coalesce(unit_jual,'')=coalesce(unit,'') then harga_g_1 " +
                                     "      when '"+sCustType+"'='G' and coalesce(unit_jual,'')=coalesce(unit2,'') then harga_g_2 " +
                                     "      when '"+sCustType+"'='G' and coalesce(unit_jual,'')=coalesce(unit3,'') then harga_g_3 " +
                                     "      when '"+sCustType+"'='R' and coalesce(unit_jual,'')=coalesce(unit,'') then harga_r_1 " +
                                     "      when '"+sCustType+"'='R' and coalesce(unit_jual,'')=coalesce(unit2,'') then harga_r_2 " +
                                     "      when '"+sCustType+"'='R' and coalesce(unit_jual,'')=coalesce(unit3,'') then harga_r_3 " +
                                     "else (case when '"+sCustType+"'='G' then harga_g_1 else harga_r_1 end) " +
                                     "end as harga " +
                                     "from r_item i " +
                                     "left join r_item_harga_jual h on h.kode_item=i.kode_item  " +
                                     "where i.kode_item='"+table.getValueAt(row, column).toString()+"'";

                            System.out.println(sQry);
                             ResultSet rs=conn.createStatement().executeQuery(sQry);
                             cmbSatuan.removeAllItems();
                             if(rs.next()){
                                 table.setValueAt(rs.getString("nama_item"), row, table.getColumnModel().getColumnIndex("Nama Barang"));
                                 if(rs.getString("unit").length()>0) cmbSatuan.addItem(rs.getString("unit"));
                                 if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                                 if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
                                 table.setValueAt(rs.getString("unit_jual"), row, table.getColumnModel().getColumnIndex("Satuan"));
                                 table.setValueAt(rs.getInt("konv_jual"), row, table.getColumnModel().getColumnIndex("Konv"));
                                 table.setValueAt(rs.getDouble("harga"), row, table.getColumnModel().getColumnIndex("Harga"));
                                 table.setColumnSelectionInterval(2, 2);
                                 
                                 //udfLoadComboKonv(row);

                             }else{
                                 JOptionPane.showMessageDialog(aThis, "Item tidak ditemukan!");
                                 
                             }
                             rs.close();
                        }else{
                            //tableModel.removeRow(row);
                            udfClearRow(row);
                        }
                    }else if(column==table.getColumnModel().getColumnIndex("Satuan")){
                        udfLoadKonversi(table.getValueAt(row, column).toString());
                    }else if(column==table.getColumnModel().getColumnIndex("Qty")||column==table.getColumnModel().getColumnIndex("Harga")){
                        table.setValueAt(
                                fn.udfGetDouble(table.getValueAt(row, table.getColumnModel().getColumnIndex("Qty")))*
                                fn.udfGetDouble(table.getValueAt(row, table.getColumnModel().getColumnIndex("Harga"))),
                                row, table.getColumnModel().getColumnIndex("Sub Total"));
                    }
                    double dTotal=0;
                    for(int i=0; i<table.getRowCount(); i++){
                        if(table.getValueAt(i, table.getColumnModel().getColumnIndex("Sub Total"))!=null)
                            dTotal+=fn.udfGetDouble(table.getValueAt(i, table.getColumnModel().getColumnIndex("Sub Total")));
                    }
                    lblTotal.setText(fn.dFmt.format(dTotal));

                 }catch(SQLException se){
                        JOptionPane.showMessageDialog(null, se.getMessage());
                     }
                 table.setRowSelectionInterval(row, row);
             }
         }


     }

    private void udfClearRow(int row){
        table.setValueAt("", row, 0);
        table.setValueAt("", row, 1);
        table.setValueAt("1", row, 2);
        table.setValueAt("", row, 3);
        table.setValueAt(0, row, 4);
        table.setValueAt(0, row, 5);
        table.setValueAt(1, row, 6);

    }

    private void udfChangePriceAll(){
        if(table.getRowCount()<=0) return;
        try{
            ResultSet rs=null;
            TableColumnModel col=table.getColumnModel();
            String sCustType=cmbCustType.getSelectedIndex()==0? "G": "R";
            String sQry="", sUnit;
            for (int i=0; i<table.getRowCount(); i++){
                sUnit=table.getValueAt(i, col.getColumnIndex("Satuan")).toString();
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
                     "where i.kode_item='"+table.getValueAt(i, 0).toString()+"'";
                rs=conn.createStatement().executeQuery(sQry);
                if(rs.next()){
                    table.setValueAt(rs.getDouble("harga"), i, col.getColumnIndex("Harga"));
                }
                rs.close();
            }


        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadKonversi(String sUnit) {
        int row=table.getSelectedRow();
        if(row<0) return;
        try {
            String sCustType=cmbCustType.getSelectedItem().toString().substring(0, 1);
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
                          "where i.kode_item='" + table.getValueAt(row, 0).toString() + "'";

            //System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                table.setValueAt(rs.getDouble("harga"), row, table.getColumnModel().getColumnIndex("Harga"));
                table.setValueAt(rs.getInt("konv"), row, table.getColumnModel().getColumnIndex("Konv"));
            } else {
                table.setValueAt(0, row, table.getColumnModel().getColumnIndex("Harga"));
                table.setValueAt(1, row, table.getColumnModel().getColumnIndex("Konv"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(TrxPenjualan.class.getName()).log(Level.SEVERE, null, ex);
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
             table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
         } else {
             table.setRowSelectionInterval(row + 1, row + 1);
         }

         table.setColumnSelectionInterval(0, 0);
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
         table.requestFocusInWindow();
         table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
         table.changeSelection(tableModel.getRowCount()-1, 0, false, false);

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
                if(table.getSelectedColumn()==0){
                    retVal = ((JTextField)text).getText();
                }else if(table.getSelectedColumn()==table.getColumnModel().getColumnIndex("Qty")||table.getSelectedColumn()==table.getColumnModel().getColumnIndex("Harga"))
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
            if(toString!=null)
                text.setText(toString);
        }
    }

     private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))
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
                if(e.getSource().equals(txtCustomer))
                    setTitle("Penjualan "+txtNamaCustomer.getText());
                else if(e.getSource().equals(txtNoTrx) && aThis.isShowing() && aThis.isFocusable() && txtNoTrx.getText().trim().length()>0)
                    udfLoadKoreksiJual();
           }
        }
    } ;

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){

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
                    
                    if(cmbCustType.getSelectedIndex()<0){
                        JOptionPane.showMessageDialog(aThis, "Pilih harga penjualan terlebih dulu!");
                        cmbCustType.requestFocus();
                        return;
                    }
                    if(table.getCellEditor()!=null && evt.getSource().equals(table))
                        table.getCellEditor().stopCellEditing();
                    
//                    if(table.getSelectedRow()>=0 && table.getValueAt(table.getSelectedRow(), 0)==null){
//
//                    }else{
//                            ((DefaultTableModel)table.getModel()).addRow(new Object[]{
//                                "", //Kode
//                                "", //Nama Barang
//                                1,  //Qty
//                                "", //Satuan
//                                0,  //Harga
//                                0,  //Sub Total
//                                1,  //Konv
//
//                            });
//                            if(table.getRowCount()>0){
//                                table.setRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
//                                table.changeSelection(table.getSelectedRow(), 0, false, false);
//                            }
//                        }
                        lookupItem.setAlwaysOnTop(true);
                        lookupItem.setSrcTable(table, table.getColumnModel().getColumnIndex("Qty"));
                        lookupItem.setKeyEvent(evt);
                        lookupItem.setObjForm(this);
                        lookupItem.setVisible(true);
                        lookupItem.clearText();
                        lookupItem.requestFocusInWindow();
                        if(lookupItem.getKodeBarang().length()>0){
                            if(table.getRowCount()==0 || (table.getValueAt(table.getRowCount()-1, 0)!=null && !table.getValueAt(table.getRowCount()-1, 0).toString().trim().equalsIgnoreCase("")) )
                                addEmptyRow();
                            table.requestFocusInWindow();
                            table.setValueAt(lookupItem.getKodeBarang(), table.getRowCount()-1, 0);
                            table.changeSelection(table.getRowCount()-1, table.getColumnModel().getColumnIndex("Qty"), false, false);
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
                        if (!fn.isListVisible()){
//                            if(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getText()!=null
//                               && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")){
//                                if(table.getSelectedColumn()==0){
//                                    //table.setValueAt(((JTextField)evt.getSource()).getText(), table.getSelectedRow(), 0);
//                                    //table.changeSelection(table.getSelectedRow(), 2, false, false);
//                                    //table.setColumnSelectionInterval(2, 2);
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
                        if(table.getSelectedColumn()==table.getColumnModel().getColumnIndex("Konv") && !hasEmptyRow()){
                            addEmptyRow();
                            table.requestFocusInWindow();

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
                            cEditor.setValue(table.getValueAt(table.getSelectedRow(), 0).toString());
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

    private boolean udfCekBeforeSave(){
        boolean b=true;
        if(table.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada item yang ditransaksikan!\nTekan insert untuk menambahkan item penjualan");
            table.requestFocus();
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

        try{
            conn.setAutoCommit(false);
            ResultSet rs=null;

            if(isKoreksi){
                if(table.getRowCount()==0 && JOptionPane.showConfirmDialog(this, "Anda yakin untuk membatalkan penjualan ini?")!=JOptionPane.YES_OPTION){
                    return;
                }
                rs=conn.createStatement().executeQuery("select fn_r_koreksi_jual('"+txtNoTrx.getText()+"')");
                rs.close();
                if(table.getRowCount()==0){
                    udfNew();
                    return;
                }
            }

            rs=conn.createStatement().executeQuery("select fn_r_get_sales_no("+(cmbCustPembayaran.getSelectedIndex()==1)+", " +
                    "current_date::varchar)");
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

            String sQryH="INSERT INTO r_sales(" +
                        "sales_no, sales_date, kode_cust, " +
                        "catatan, " +
                        "date_ins, user_ins, is_kredit, " +
                        "kode_gudang, " +
                        "jenis, tgl_jt_tempo, bayar)" +
                        "VALUES ('"+txtNoTrx.getText()+"', current_date, '"+txtCustomer.getText()+"', " +
                        "'"+txtCatatan.getText()+"', " +
                        "now(), '"+MainForm.sUserName+"', "+(cmbCustPembayaran.getSelectedIndex()==1)+", " +
                        "'"+lstGudang.get(cmbGudang.getSelectedIndex()).toString()+"', " +
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
            if(JOptionPane.showConfirmDialog(this, "Input data sukses, Klik ok untuk cetak invoice", "Message", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                printKwitansi();

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
        lblTotal = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Penjualan");
        setBackground(new java.awt.Color(204, 204, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        table.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Qty", "Satuan", "Harga", "Sub Total", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbCustType.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbCustType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GROSIR", "ECERAN" }));
        cmbCustType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCustTypeItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbCustType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 150, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Pelanggan");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText("Cust. Type");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNamaCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaCustomer.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaCustomer.setEnabled(false);
        jPanel1.add(txtNamaCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 410, 20));

        txtCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel1.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 130, 20));

        lblTgl.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTgl.setText("20/12/2010");
        lblTgl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lblTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 35, 90, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoTrx.setEnabled(false);
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 140, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel5.setText("No.");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 30, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setText("Tgl.");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 35, 30, 20));

        jButton1.setText("....");
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 35, 30, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText("Jatuh Tempo");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 35, 90, 20));

        cmbCustPembayaran.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbCustPembayaran.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbCustPembayaran.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCustPembayaranItemStateChanged(evt);
            }
        });
        jPanel1.add(cmbCustPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, 150, -1));

        cmbGudang.setFont(new java.awt.Font("Tahoma", 0, 12));
        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Center" }));
        cmbGudang.setEnabled(false);
        jPanel1.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 60, 150, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel7.setText("Site");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, 50, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel9.setText("Catatan");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 90, 20));

        txtCatatan.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCatatan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCatatanKeyReleased(evt);
            }
        });
        jPanel1.add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 700, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel10.setText("Pembayaran");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, 90, 20));

        jFJtTempo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFJtTempo.setFont(new java.awt.Font("Tahoma", 0, 12));
        jPanel1.add(jFJtTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 35, 110, 20));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel8.setText("<html>\n<b>F4</b> &nbsp&nbsp : Membuat transaksi baru  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Insert</b> &nbsp : Menambah item barang  &nbsp  &nbsp |  &nbsp  &nbsp \n<b>Del</b> &nbsp&nbsp &nbsp &nbsp : Menghapus item barang <br>\n<b>F5</b> &nbsp&nbsp : Menyimpan Transaksi <br>\n</html>"); // NOI18N
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setOpaque(true);

        lblTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 34));
        lblTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0,00");
        lblTotal.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        lblTotal.setOpaque(true);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(349, 349, 349)
                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-844)/2, (screenSize.height-465)/2, 844, 465);
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

    private void cmbCustPembayaranItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCustPembayaranItemStateChanged
        jLabel3.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
        jFJtTempo.setVisible(cmbCustPembayaran.getSelectedIndex()==1);
    }//GEN-LAST:event_cmbCustPembayaranItemStateChanged

    private void cmbCustTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCustTypeItemStateChanged
        udfChangePriceAll();
    }//GEN-LAST:event_cmbCustTypeItemStateChanged

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
                new TrxPenjualan().setVisible(true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblTgl;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtNamaCustomer;
    private javax.swing.JTextField txtNoTrx;
    // End of variables declaration//GEN-END:variables

}
