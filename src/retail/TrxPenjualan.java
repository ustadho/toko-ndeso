/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TrxPenjualan.java
 *
 * Created on Mar 8, 2009, 8:05:56 AM
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.GeneralFunction;
import retail.main.JDesktopImage;

/**
 *
 * @author ustadho
 */
public class TrxPenjualan extends javax.swing.JFrame {
    Connection conn;
    private DefaultTableModel myModel;
    private NumberFormat dFmt=NumberFormat.getInstance();
    retail.main.ListRsbm lst=new retail.main.ListRsbm();
    private MyKeyListener kListener=new MyKeyListener();
    private NumberFormat numFormat=NumberFormat.getInstance();
    List lstSatuan=new ArrayList();
    List lstHarga=new ArrayList();
    List lstKonversi=new ArrayList();
    List lstGudang=new ArrayList();

    /** Creates new form TrxPenjualan */
    public TrxPenjualan() {
        initComponents();
        jXTable1.addKeyListener(new MyKeyListener());
        
        jXTable1.setRowHeight(22);
        jXTable1.setGridColor(new Color(1));
        jXTable1.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));

        jXTable1.changeSelection(0, 0, true, true);
        jXTable1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jXTable1.setSelectionForeground(new Color(255, 255, 255));
        jXTable1.getColumnModel().getColumn(0).setCellEditor(new MyTableCellEditor());
        jXTable1.getColumnModel().getColumn(2).setCellEditor(new MyTableCellEditor());

        jXTable1.setSurrendersFocusOnKeystroke(true);
        udfClear();

        jXTable1.getModel().addTableModelListener(new MyTableModelListener(jXTable1));
        txtNoAnggota.addKeyListener(kListener);
        txtNoAnggota.addFocusListener(txtFoculListener);
        jXTable1.addKeyListener(kListener);

        for(int i=0;i<panelCash.getComponentCount();i++){
            Component c = panelCash.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        for(int i=0;i<panelKredit.getComponentCount();i++){
            Component c = panelKredit.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }
        
        for(int i=0;i<panelAddItem.getComponentCount();i++){
            Component c = panelAddItem.getComponent(i);
            if(c instanceof JTextField || c instanceof  JFormattedTextField || c instanceof  JTextArea || c instanceof  JComboBox
                || c instanceof  JCheckBox  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }

        jXTable1.getColumn("Konv").setMinWidth(0);jXTable1.getColumn("Konv").setMaxWidth(0);jXTable1.getColumn("Konv").setPreferredWidth(0);
    }

    void setConn(Connection con){
        this.conn=con;
    }

    private void udfAddItem() {
        if(txtKode.getText().trim().equalsIgnoreCase("")||txtNamaBarang.getText().trim().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this, "Silakan isi item transaksi terlebih dulu!", "Information", JOptionPane.INFORMATION_MESSAGE);
            txtKode.requestFocus();
            return;
        }
        if(GeneralFunction.udfGetDouble(txtQty.getText())==0){
            JOptionPane.showMessageDialog(this, "Silakan isi Qty Item terlebih dulu!", "Information", JOptionPane.INFORMATION_MESSAGE);
            txtKode.requestFocus();
            return;
        }
        TableColumnModel col=jXTable1.getColumnModel();
        if(btnAdd.getText().equalsIgnoreCase("ADD")){
            for(int i=0; i<jXTable1.getRowCount(); i++){
                if(((DefaultTableModel)jXTable1.getModel()).getValueAt(i, 0).toString().equalsIgnoreCase(txtKode.getText()) &&
                   ((DefaultTableModel)jXTable1.getModel()).getValueAt(i, col.getColumnIndex("Satuan")).toString().equalsIgnoreCase(cmbUnit.getSelectedItem().toString())     ){
                    int curQty=GeneralFunction.udfGetInt(((DefaultTableModel)jXTable1.getModel()).getValueAt(i, jXTable1.getColumnModel().getColumnIndex("QTY")).toString());
                    double currAmount=GeneralFunction.udfGetDouble(((DefaultTableModel)jXTable1.getModel()).getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Sub Total")));

                    ((DefaultTableModel)jXTable1.getModel()).setValueAt(curQty+GeneralFunction.udfGetInt(txtQty.getText()), i, jXTable1.getColumnModel().getColumnIndex("QTY"));
                    ((DefaultTableModel)jXTable1.getModel()).setValueAt(currAmount+GeneralFunction.udfGetDouble(txtSubTotal.getText()), i, jXTable1.getColumnModel().getColumnIndex("Sub Total"));

                    udfStartNewItem();
                    return;
                }
            }
            ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                txtKode.getText(),
                txtNamaBarang.getText(),
                dFmt.format(GeneralFunction.udfGetDouble(txtQty.getText())),
                cmbUnit.getSelectedIndex()>=0? cmbUnit.getSelectedItem().toString():"",
                dFmt.format(GeneralFunction.udfGetDouble(txtHarga.getText())),
                dFmt.format(GeneralFunction.udfGetDouble(txtDisc.getText())),
                dFmt.format(GeneralFunction.udfGetDouble(txtTax.getText())),
                dFmt.format(GeneralFunction.udfGetDouble(txtSubTotal.getText())),
                txtKonv.getText()
            });
            jXTable1.setRowSelectionInterval(myModel.getRowCount()-1, myModel.getRowCount()-1);
        }else{
            int iRow=jXTable1.getSelectedRow();
            TableColumnModel column=jXTable1.getColumnModel();
            txtKode.setText(myModel.getValueAt(iRow, column.getColumnIndex("Kode")).toString());
            udfSetSatuanBarang();
            txtNamaBarang.setText(myModel.getValueAt(iRow, column.getColumnIndex("Barang")).toString());
            txtQty.setText(dFmt.format(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("QTY")))));
            cmbUnit.setSelectedItem(myModel.getValueAt(iRow, column.getColumnIndex("Satuan")));
            txtHarga.setText(dFmt.format(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Harga")))));
            txtQty.setText(dFmt.format(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("QTY")))));
            txtDisc.setText(dFmt.format(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Disc(%)")))));
            txtTax.setText(dFmt.format(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Tax(%)")))));
            txtSubTotal.setText(dFmt.format(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Sub Total")))));
            txtKonv.setText(dFmt.format(GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("Konv")))));
            btnAdd.setText("Add");
        }
        udfStartNewItem();
    }

    private void udfClear() {
        txtNoAnggota.setText("");
        lblAnggota.setText("");
        lblNip.setText("");
        lblDivisi.setText("");
        txtTunai.setText("0");
        txtKembali.setText("0");
        txtJmlCicilan.setText("0");
        txtAngsuran.setText("0");
        cmbPeriode.setSelectedIndex(0);

        myModel=(DefaultTableModel)jXTable1.getModel();
        myModel.setNumRows(0);
        jXTable1.setModel(myModel);

        txtNoAnggota.requestFocus();
        
    }

    private void udfLookupBarang(JTextField text) {
        jXTable1.requestFocus(true);
        jXTable1.requestFocus();
        String sKodeBarang="";

        DlgLookupBarang dlgLookupItem=new DlgLookupBarang(this, true);
        dlgLookupItem.setConn(conn);
        //dlgLookupItem.setVisible(false);
        dlgLookupItem.udfClear();
        //dlgLookupItem.tampilkan(sKodeBarang);
        dlgLookupItem.setVisible(true);
        
        sKodeBarang=dlgLookupItem.getKodeBarang();
        dlgLookupItem.dispose();
        
        if(sKodeBarang.trim().length()>0){
            for (int i=0; i<jXTable1.getRowCount(); i++){
                if(jXTable1.getValueAt(i, 0)==null || jXTable1.getValueAt(i, 0).toString().length()==0 ||
                   jXTable1.getValueAt(i, 1)==null || jXTable1.getValueAt(i, 1).toString().length()==0 ){
                    if(jXTable1.getSelectedRow()!=i) jXTable1.setValueAt(dlgLookupItem.getKodeBarang(), i, 0);

                    udfSetBarang(sKodeBarang, jXTable1.getSelectedRow());
                    if(text!=null) {
                        text.setText(sKodeBarang);
                        text.setFocusable(false);
                    }
                    
//                    try{
//                        String sQry="select kode_item, coalesce(nama_item,'') as nama_item, " +
//                                "coalesce(unit_jual,'') as sat_jual, coalesce(harga_jual,0)*coalesce(konv_jual,0) as harga_jual " +
//                                "from r_item where kode_item='"+dlgLookupItem.getKodeBarang()+"'";
//
//                        System.out.println(sQry);
//                        ResultSet rs=conn.createStatement().executeQuery(sQry);
//                        if(rs.next()){
//                            myModel.setValueAt(rs.getString("nama_item"), i, 1);
//                            myModel.setValueAt(1, i, 2);
//                            myModel.setValueAt(rs.getString("sat_jual"), i, 3);
//                            myModel.setValueAt(rs.getString("harga_jual"), i, 4);
//                            myModel.setValueAt(rs.getString("harga_jual"), i, 5);
//
//                        }
//
//                    }catch(SQLException se){
//                        JOptionPane.showMessageDialog(this, se.getMessage());
//                    }
                    //dlgLookupItem.setVisible(false);

                    return;
                }
            }
        }

        //if(new DlgLookupBarang(this, true).u)
    }

    private void udfSetSubTotal(int iRow){
        myModel.setValueAt(GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("QTY")).toString()) *
            GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Harga")).toString()),
            iRow, jXTable1.getColumnModel().getColumnIndex("Sub Total"));

    }

    private String udfSetBarang(String sKode, int row){
        String sReturn="";

        try{
            String sQry="Select kode_item,  coalesce(nama_item, '') as nama_item, " +
                    "coalesce(unit_jual,'') as sat_jual, coalesce(harga_jual,0)*coalesce(konv_jual,0) as harga_jual " +
                    "from r_item where kode_item = '"+sKode+"'";

            ResultSet rs=conn.createStatement().executeQuery(sQry);

            if(rs.next()){
                for(int i=0; i< jXTable1.getRowCount(); i++){
                    if(i!=row ) {
                        if(myModel.getValueAt(i, 0).toString().length()>0 && myModel.getValueAt(i, 1).toString().length()>0 &&  myModel.getValueAt(i, 0).toString().equalsIgnoreCase(sKode)){
                            myModel.setValueAt(GeneralFunction.udfGetDouble(myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("QTY")))+1 ,
                                    i, jXTable1.getColumnModel().getColumnIndex("QTY"));

                            myModel.setValueAt("", row, 0); myModel.setValueAt("", row, 1); myModel.setValueAt(null, row, 2);
                            myModel.setValueAt("", row, 3); myModel.setValueAt(null, row, 1); myModel.setValueAt(null, row, 2);
                            udfSetSubTotal(i);
                            jXTable1.changeSelection(myModel.getRowCount()-1, 0, false, false);
                            sReturn="";
                            return sReturn;
                        }
                    }else{
                        if(myModel.getValueAt(i, 0).toString().length()>0 && myModel.getValueAt(i, 1).toString().length()>0 &&  myModel.getValueAt(i, 0).toString().equalsIgnoreCase(sKode)){
                            myModel.setValueAt(GeneralFunction.udfGetDouble(myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("QTY")))+1 ,
                                    i, jXTable1.getColumnModel().getColumnIndex("QTY"));
                            myModel.setValueAt(GeneralFunction.udfGetDouble(myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("QTY")))*
                                    GeneralFunction.udfGetDouble(myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Harga"))) ,
                                    i, jXTable1.getColumnModel().getColumnIndex("Sub Total"));
                            jXTable1.changeSelection(myModel.getRowCount()-1, 0, false, false);
                            sReturn="";
                            return sReturn;
                        }
                    }
                }

                myModel.setValueAt(rs.getString("kode_item"), row, 0);  //Kode Item
                myModel.setValueAt(rs.getString("nama_item"), row, 1);  //Nama Item
                myModel.setValueAt(1, row, 2);                          //QTY
                myModel.setValueAt(rs.getString("sat_jual"), row, 3);
                myModel.setValueAt(rs.getDouble("harga_jual"), row, 4);
                myModel.setValueAt(rs.getDouble("harga_jual"), row, 5);

                myModel.addRow(new Object[]{
                    "", "", 0, "", 0, 0
                });
                jXTable1.setRowSelectionInterval(myModel.getRowCount()-1, myModel.getRowCount()-1);
                jXTable1.changeSelection(myModel.getRowCount()-1, 0, false, false);
                sReturn=myModel.getValueAt(row, 0).toString();
                return sReturn;
            }else{
                JOptionPane.showMessageDialog(this, "Kode barang tidak ditemukan!");
                myModel.setValueAt("", row, 0); myModel.setValueAt("", row, 1); myModel.setValueAt(null, row, 2);
                myModel.setValueAt("", row, 3); myModel.setValueAt(null, row, 1); myModel.setValueAt(null, row, 2);

                jXTable1.requestFocusInWindow();
                jXTable1.requestFocus();
                jXTable1.changeSelection(row, 0, false, false);
                sReturn="";
                rs.close();
                return sReturn;
            }
            //rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(TrxPenjualan.this, se.getMessage());
        }

        return sReturn;
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
        jXCollapsiblePane1 = new org.jdesktop.swingx.JXCollapsiblePane();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtNoAnggota = new javax.swing.JTextField();
        lblAnggota = new javax.swing.JLabel();
        lblNip = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblDivisi = new javax.swing.JLabel();
        lblGrandTotal = new javax.swing.JLabel();
        lblGrandTotal1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cmbJenisBayar = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        panelCash = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTunai = new javax.swing.JTextField();
        txtKembali = new javax.swing.JTextField();
        panelKredit = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtJmlCicilan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtAngsuran = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cmbPeriode = new javax.swing.JComboBox();
        chkPotongGaji = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        panelAddItem = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtQty = new javax.swing.JTextField();
        cmbUnit = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtDisc = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtTax = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtKonv = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jXCollapsiblePane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jXCollapsiblePane1.setCollapsed(true);
        jXCollapsiblePane1.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("NIP"); // NOI18N
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 100, 21));

        txtNoAnggota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoAnggota.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoAnggotaFocusLost(evt);
            }
        });
        txtNoAnggota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoAnggotaKeyReleased(evt);
            }
        });
        jPanel2.add(txtNoAnggota, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 80, 21));

        lblAnggota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblAnggota, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 300, 21));

        lblNip.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblNip, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 35, 50, 21));

        jLabel9.setText("No. Anggota"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 21));

        jLabel10.setText("Divisi : "); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 35, 50, 21));

        lblDivisi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblDivisi, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 35, 130, 21));

        jXCollapsiblePane1.getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 510, 70));

        lblGrandTotal.setBackground(new java.awt.Color(0, 0, 102));
        lblGrandTotal.setFont(new java.awt.Font("Arial Unicode MS", 1, 60));
        lblGrandTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblGrandTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrandTotal.setText("0");
        lblGrandTotal.setOpaque(true);
        jXCollapsiblePane1.getContentPane().add(lblGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(625, 10, 360, 60));

        lblGrandTotal1.setBackground(new java.awt.Color(153, 0, 0));
        lblGrandTotal1.setFont(new java.awt.Font("Arial Unicode MS", 1, 60));
        lblGrandTotal1.setForeground(new java.awt.Color(255, 255, 255));
        lblGrandTotal1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblGrandTotal1.setOpaque(true);
        jXCollapsiblePane1.getContentPane().add(lblGrandTotal1, new org.netbeans.lib.awtextra.AbsoluteConstraints(631, 5, 360, 60));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Barang", "QTY", "Satuan", "Harga", "Disc", "Tax", "Sub Total", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
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
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jXTable1);
        jXTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jXTable1.getColumnModel().getColumn(0).setPreferredWidth(120);
        jXTable1.getColumnModel().getColumn(1).setPreferredWidth(400);

        jPanel3.add(jScrollPane1);

        jScrollPane2.setViewportView(jPanel3);

        jXCollapsiblePane1.getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 131, 980, 360));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setText("F5 - Bayar");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 190, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel2.setForeground(new java.awt.Color(0, 51, 204));
        jLabel2.setText("F9 - List (Lookup) Barang");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 190, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel3.setForeground(new java.awt.Color(0, 51, 204));
        jLabel3.setText("Ins - Insert Barang");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 20));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbJenisBayar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TUNAI", "KREDIT" }));
        cmbJenisBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbJenisBayarActionPerformed(evt);
            }
        });
        jPanel4.add(cmbJenisBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(156, 10, 230, -1));

        jPanel5.setLayout(new java.awt.CardLayout());

        panelCash.setBorder(javax.swing.BorderFactory.createTitledBorder("Cash"));
        panelCash.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("TUNAI");
        panelCash.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 100, 20));

        jLabel5.setText("KEMBALI");
        panelCash.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, 20));

        txtTunai.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtTunai.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTunai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTunaiKeyReleased(evt);
            }
        });
        panelCash.add(txtTunai, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 150, -1));

        txtKembali.setEditable(false);
        txtKembali.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtKembali.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelCash.add(txtKembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 150, -1));

        jPanel5.add(panelCash, "card2");

        panelKredit.setBorder(javax.swing.BorderFactory.createTitledBorder("Kredit"));

        jLabel6.setText("Jml. Cicilan");

        txtJmlCicilan.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtJmlCicilan.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setText("Angsuran");

        txtAngsuran.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtAngsuran.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("X");

        cmbPeriode.setFont(new java.awt.Font("Tahoma", 1, 12));
        cmbPeriode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Per Bulan", "Per Minggu", "Per Hari" }));

        chkPotongGaji.setSelected(true);
        chkPotongGaji.setText("Potong Gaji");

        javax.swing.GroupLayout panelKreditLayout = new javax.swing.GroupLayout(panelKredit);
        panelKredit.setLayout(panelKreditLayout);
        panelKreditLayout.setHorizontalGroup(
            panelKreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKreditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelKreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelKreditLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJmlCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPeriode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelKreditLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkPotongGaji, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelKreditLayout.setVerticalGroup(
            panelKreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelKreditLayout.createSequentialGroup()
                .addGroup(panelKreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtJmlCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPeriode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelKreditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPotongGaji))
                .addGap(29, 29, 29))
        );

        jPanel5.add(panelKredit, "card3");

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 370, 90));

        jLabel15.setText("Jenis Pembayaran");
        jPanel4.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 140, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 390, 140));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel13.setForeground(new java.awt.Color(0, 51, 204));
        jLabel13.setText("F3 - Bersihkan layar");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 190, 20));

        jButton1.setText("Simpan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 165, 110, 30));

        jLabel14.setText("Catatan :");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, 240, 20));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 240, 100));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("26-06-2009 12:36");
        jLabel16.setOpaque(true);
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 160, 20));

        jLabel17.setText("Tanggal");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 70, 20));

        jButton2.setText("Close");
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 165, 120, 30));

        jXCollapsiblePane1.getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 980, 200));

        panelAddItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAddItem.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setBackground(new java.awt.Color(255, 255, 204));
        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Kode");
        jLabel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel12.setOpaque(true);
        panelAddItem.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 120, -1));

        txtKode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKeyReleased(evt);
            }
        });
        panelAddItem.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 120, -1));

        jLabel18.setBackground(new java.awt.Color(255, 255, 204));
        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setText("Barang");
        jLabel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel18.setOpaque(true);
        panelAddItem.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 2, 340, -1));

        txtNamaBarang.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtNamaBarang.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaBarang.setEnabled(false);
        panelAddItem.add(txtNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 340, -1));

        jLabel19.setBackground(new java.awt.Color(255, 255, 204));
        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel19.setText("Satuan");
        jLabel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel19.setOpaque(true);
        panelAddItem.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 2, 90, -1));

        txtQty.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtQtyKeyTyped(evt);
            }
        });
        panelAddItem.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 50, -1));

        cmbUnit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbUnit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbUnitItemStateChanged(evt);
            }
        });
        panelAddItem.add(cmbUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 90, -1));

        jLabel21.setBackground(new java.awt.Color(255, 255, 204));
        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setText("Harga");
        jLabel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel21.setOpaque(true);
        panelAddItem.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 2, 80, -1));

        txtHarga.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga.setEnabled(false);
        panelAddItem.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 20, 80, -1));

        jLabel22.setBackground(new java.awt.Color(255, 255, 204));
        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel22.setText("Disc (%)");
        jLabel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel22.setOpaque(true);
        panelAddItem.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 2, 50, -1));

        txtDisc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtDisc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 20, 50, -1));

        jLabel23.setBackground(new java.awt.Color(255, 255, 204));
        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setText("Tax (%)");
        jLabel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel23.setOpaque(true);
        panelAddItem.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 2, 50, -1));

        txtTax.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTax.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtTax, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 20, 50, -1));

        jLabel24.setBackground(new java.awt.Color(255, 255, 204));
        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setText("Sub Total");
        jLabel24.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel24.setOpaque(true);
        panelAddItem.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 2, 80, -1));

        txtSubTotal.setEditable(false);
        txtSubTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSubTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubTotal.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSubTotal.setEnabled(false);
        panelAddItem.add(txtSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 20, 80, -1));

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel25.setText("Konv");
        jLabel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel25.setOpaque(true);
        panelAddItem.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 2, 40, -1));

        txtKonv.setEditable(false);
        txtKonv.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtKonv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtKonv.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKonv.setEnabled(false);
        panelAddItem.add(txtKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 20, 40, -1));

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        panelAddItem.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 19, 79, -1));

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setText("QTY");
        jLabel27.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel27.setOpaque(true);
        panelAddItem.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 0, 50, -1));

        jXCollapsiblePane1.getContentPane().add(panelAddItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 980, 43));

        getContentPane().add(jXCollapsiblePane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1000, 710));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1024)/2, (screenSize.height-768)/2, 1024, 768);
    }// </editor-fold>//GEN-END:initComponents

    private void txtNoAnggotaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoAnggotaFocusLost
//        if(!lst.isVisible() && !txtNoAnggota.getText().equalsIgnoreCase("") && isNew)
//            txtPinjamanKe.setText(dFmt.format(getPinjamanKe()));
    }//GEN-LAST:event_txtNoAnggotaFocusLost

    private void txtNoAnggotaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoAnggotaKeyReleased
        try {
            String sCari = txtNoAnggota.getText();
            switch (evt.getKeyCode()) {
                case java.awt.event.KeyEvent.VK_ENTER: {
                    if (lst.isVisible()) {
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtNoAnggota.setText(obj[0].toString());
                            lblAnggota.setText(obj[1].toString());
                            lblNip.setText(obj[2].toString());
                            lblDivisi.setText(obj[3].toString());
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
                    txtNoAnggota.setText("");
                    lblAnggota.setText("");
                    lblDivisi.setText("");
                    lblNip.setText("");
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
                        String sQry = "select a.no_anggota, a.nama, coalesce(a.nip) as nip, " +
                                "coalesce(nama_divisi,'') " +
                                "from m_anggota a " +
                                "left join m_divisi dv on dv.kode_divisi=a.kode_divisi " +
                                "where no_anggota<>'00000' and (no_anggota||nama||coalesce(nip,'')||coalesce(dv.nama_divisi,'')) " +
                                "iLike '%" + txtNoAnggota.getText() + "%' order by nama ";

                        //System.out.println(sQry);
                        lst.setSQuery(sQry);

                        //                    lst.setBounds(this.getX()+MainForm.iLeft + this.jPanel1.getX() + this.txtNoAnggota.getX() + 4,
                        //                            this.getY() + MainForm.iTop + this.jPanel1.getY() + this.txtNoAnggota.getY() + txtNoAnggota.getHeight() + 50,
                        //                            txtNoAnggota.getWidth() + lblAnggota.getWidth()+10,
                        //                            (lst.getIRowCount()>10? 12*lst.getRowHeight(): (lst.getIRowCount()+2)*lst.getRowHeight()));

                        lst.setBounds(txtNoAnggota.getLocationOnScreen().x ,
                                txtNoAnggota.getLocationOnScreen().y + txtNoAnggota.getHeight() ,
                                txtNoAnggota.getWidth() + lblAnggota.getWidth(),
                                (lst.getIRowCount()>10? 12*lst.getRowHeight(): (lst.getIRowCount()+3)*lst.getRowHeight()));


                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtNoAnggota);
                        lst.setCompDes(new javax.swing.JLabel[]{lblAnggota, lblNip, lblDivisi});
                        lst.setColWidth(0, txtNoAnggota.getWidth());
                        lst.setColWidth(1, lblAnggota.getWidth()-100);

                        if (lst.getIRowCount() > 0) {
                            lst.setVisible(true);
                            requestFocusInWindow();
                            txtNoAnggota.requestFocus();
                        } else {
                            lst.setVisible(false);
                            txtNoAnggota.setText("");
                            lblAnggota.setText("");
                            lblDivisi.setText("");
                            lblNip.setText("");
                            txtNoAnggota.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
}//GEN-LAST:event_txtNoAnggotaKeyReleased

    private void txtTunaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTunaiKeyReleased
        GeneralFunction.keyTyped(evt);
    }//GEN-LAST:event_txtTunaiKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        lst.setConn(this.conn);
        lst.setVisible(false);

        jXTable1.getColumn("Kode").setPreferredWidth(txtKode.getWidth());
        jXTable1.getColumn("Barang").setPreferredWidth(txtNamaBarang.getWidth());
        jXTable1.getColumn("QTY").setPreferredWidth(txtQty.getWidth());
        jXTable1.getColumn("Satuan").setPreferredWidth(cmbUnit.getWidth());
        jXTable1.getColumn("Harga").setPreferredWidth(txtHarga.getWidth());
        jXTable1.getColumn("Disc").setPreferredWidth(txtDisc.getWidth());
        jXTable1.getColumn("Tax").setPreferredWidth(txtTax.getWidth());
        jXTable1.getColumn("Sub Total").setPreferredWidth(txtSubTotal.getWidth());
    }//GEN-LAST:event_formWindowOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        udfSimpanBayar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cmbJenisBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbJenisBayarActionPerformed
        panelCash.setVisible(cmbJenisBayar.getSelectedIndex()==0);
        panelKredit.setVisible(cmbJenisBayar.getSelectedIndex()==1);
//        String namaPanel="";
//        switch(cmbJenisBayar.getSelectedIndex()){
//            case 0:{
//                namaPanel="panelCash";
//                break;
//            }
//            case 1:{
//                namaPanel="panelKredit";
//                break;
//            }
//        }
//
//        ((java.awt.CardLayout)jPanel5.getLayout()).show(jPanel5, namaPanel);
}//GEN-LAST:event_cmbJenisBayarActionPerformed

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
                    if(txtNamaBarang.getText().length()>0){
                        udfSetSatuanBarang();
                        cmbUnit.setModel(cmbUnit.getModel());
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
                    udfStartNewItem();
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
                        String sQry="Select kode_item,  coalesce(nama_item, '') as nama_item, " +
                                "coalesce(unit_jual,'') as sat_jual, coalesce(harga_jual,0)*coalesce(konv_jual,0) as harga_jual, 1 as qty, 1 as konv,coalesce(harga_jual,0)*coalesce(konv_jual,0) as subtotal " +
                                "from vw_r_item_trx where (kode_item||coalesce(nama_item,'')) ilike  '%"+txtKode.getText()+"%'";

                        //System.out.println(sQry);
                        lst.setSQuery(sQry);

                        lst.setBounds(this.getX()+jXCollapsiblePane1.getX()+ this.panelAddItem.getX() + this.txtKode.getX() + 4,
                                this.getY() + jXCollapsiblePane1.getY()+ this.panelAddItem.getY() + this.txtKode.getY() + txtKode.getHeight() + 33,
                                txtKode.getWidth() + txtNamaBarang.getWidth()+10,
                                (lst.getIRowCount()>10? 12*lst.getRowHeight(): (lst.getIRowCount()+3)*lst.getRowHeight()));


                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtKode);
                        //lst.setLblDes(new javax.swing.JLabel[]{lblAnggota, lblNip});
                        lst.setCompDes(new JComponent[]{txtNamaBarang, cmbUnit, txtHarga, txtQty, txtKonv, txtSubTotal});

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

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        udfAddItem();
}//GEN-LAST:event_btnAddActionPerformed

    private void txtQtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQtyKeyTyped
        GeneralFunction.keyTyped(evt);
    }//GEN-LAST:event_txtQtyKeyTyped

    private void cmbUnitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbUnitItemStateChanged
        if(cmbUnit.getSelectedIndex()<0) return;
        txtKonv.setText(lstKonversi.get(cmbUnit.getSelectedIndex()).toString());
        txtHarga.setText(GeneralFunction.dFmt.format(lstHarga.get(cmbUnit.getSelectedIndex())));
        udfSubTotalItem();
    }//GEN-LAST:event_cmbUnitItemStateChanged

    private boolean udfCekBeforeSave(){
        boolean b=true, adaBarang=false;
        if(GeneralFunction.udfGetDouble(lblGrandTotal.getText())==0){
            JOptionPane.showMessageDialog(this, "Total penjualan masih Nol");
            jXTable1.requestFocus();
            return false;
        }

        for(int i=0; i<myModel.getRowCount(); i++){
            if(myModel.getValueAt(i, 1)!=null && myModel.getValueAt(i, 1).toString().length()>0){
               adaBarang=true;
            }
        }
        if(!adaBarang){
            JOptionPane.showMessageDialog(this, "Barang masih belum dimasukkan");
            jXTable1.requestFocus();
            return false;
        }
        if(cmbJenisBayar.getSelectedItem().toString().equalsIgnoreCase("tunai")){
            if(GeneralFunction.udfGetDouble(txtTunai.getText())==0){
                JOptionPane.showMessageDialog(this, "Silakan lakukan pembayaran tunai terlebih dulu!");
                txtTunai.requestFocus();
                return false;
            }
        }else{
            if((GeneralFunction.udfGetDouble(txtJmlCicilan.getText())* GeneralFunction.udfGetDouble(txtAngsuran.getText())) < GeneralFunction.udfGetDouble(lblGrandTotal.getText()) ){
                JOptionPane.showMessageDialog(this, "Jumlah pembayaran masih kurang dari total penjualan!");
                txtJmlCicilan.requestFocus();
                return false;
            }
        }
        
        return b;
    }

    private void udfSimpanBayar() {
        if(udfCekBeforeSave()){
//            DlgBayarPenjualan dlgLookupItem=new DlgBayarPenjualan(this, true);
//            dlgLookupItem.setConn(this.conn);
//            dlgLookupItem.udfSetTotalTrx(GeneralFunction.udfGetDouble(lblGrandTotal.getText()));
//            dlgLookupItem.setVisible(true);
            
            //if(dlgLookupItem.isBayar()){
                try{
                    conn.setAutoCommit(false);
                    ResultSet rs=conn.createStatement().executeQuery("select fn_r_ins_sales_h('"+txtNoAnggota.getText()+"', 0, 0, 'Test', " +
                            ""+GeneralFunction.udfGetDouble(txtTunai.getText())+", "+GeneralFunction.udfGetDouble(txtKembali.getText())+", " +
                            ""+panelKredit.isVisible()+", "+GeneralFunction.udfGetInt(txtJmlCicilan.getText())+"::smallint, "+GeneralFunction.udfGetDouble(txtAngsuran.getText())+", " +
                            "current_date, "+cmbPeriode.getSelectedIndex()+"::smallint, '"+MainForm.sUserName+"' ,'00', '"+cmbJenisBayar.getSelectedItem().toString().substring(0, 1
                            )+"')");
                    //String sQry="Select * from r_sales_detail limit 0";
                    String sQry="";
                    
                    if(rs.next()){
                        for(int iRow=0; iRow<jXTable1.getRowCount(); iRow++){
                            if(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Barang")).toString().length()>0){
                                sQry+=(sQry.length()>0? " union all ": "")+ "select fn_r_ins_sales_d('"+rs.getString(1)+"', " +
                                    "'"+jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Kode"))+"', " +
                                    "'', '', "+GeneralFunction.udfGetDouble(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("QTY")).toString())+", " +
                                    ""+GeneralFunction.udfGetDouble(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Harga")).toString())+", " +
                                    ""+GeneralFunction.udfGetDouble(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Disc")).toString())+", " +
                                    ""+GeneralFunction.udfGetDouble(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Tax")).toString())+", " +
                                    "'"+jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Satuan")).toString()+"', " +
                                    GeneralFunction.udfGetInt(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Konv")).toString())+")  ";
//                                rsD.moveToInsertRow();
//                                rsD.updateString("sales_no", rs.getString(1));
//                                rsD.updateString("kode_item", rs.getString(1));
//                                rsD.updateString("exp_date", rs.getString(1));
//                                rsD.updateString("batch_no", rs.getString(1));
//                                rsD.updateString("qty", rs.getString(1));
//                                rsD.updateString("unit_price", rs.getString(1));
//                                rsD.updateString("sales_no", rs.getString(1));

                            }
                        }
                        System.out.println(sQry);
                        
                        ResultSet rsD=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery(sQry);

                        conn.setAutoCommit(true);
                        if(rsD.next()){
                            JOptionPane.showMessageDialog(this, "Input data sukses");
                            udfClear();
                        }
                    }

                    rs.close();
                }catch(SQLException se){
                    try {
                        conn.setAutoCommit(true);
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, se.getMessage());

                    } catch (SQLException ex) {
//                        conn.setAutoCommit(true);
//                        JOptionPane.showMessageDialog(this, "transaksi di Rollback")
                        Logger.getLogger(TrxPenjualan.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            //}
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TrxPenjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkPotongGaji;
    private javax.swing.JComboBox cmbJenisBayar;
    private javax.swing.JComboBox cmbPeriode;
    private javax.swing.JComboBox cmbUnit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblAnggota;
    private javax.swing.JLabel lblDivisi;
    private javax.swing.JLabel lblGrandTotal;
    private javax.swing.JLabel lblGrandTotal1;
    private javax.swing.JLabel lblNip;
    private javax.swing.JPanel panelAddItem;
    private javax.swing.JPanel panelCash;
    private javax.swing.JPanel panelKredit;
    private javax.swing.JTextField txtAngsuran;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtJmlCicilan;
    private javax.swing.JTextField txtKembali;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKonv;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtNoAnggota;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTunai;
    // End of variables declaration//GEN-END:variables

    public class MyTableModelListener implements TableModelListener {
        JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        MyTableModelListener(JTable table) {
            this.table = table;
        }

        public void tableChanged(TableModelEvent e) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();

            int mColIndex = e.getColumn();

            double dTotal=0;
            for (int i=0; i<myModel.getRowCount(); i++){
                dTotal+=myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Sub Total"))==null? 0: GeneralFunction.udfGetDouble(myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Sub Total")).toString());
            }
            lblGrandTotal.setText(dFmt.format(dTotal));

        }
    }

    private void udfStartNewItem(){
        txtKode.setText("");
        txtNamaBarang.setText("");
        txtQty.setText("");
        txtHarga.setText("");
        cmbUnit.removeAllItems();
        txtDisc.setText("");
        txtTax.setText("");
        txtSubTotal.setText("");
        txtKonv.setText("");
        txtKode.requestFocus();
        btnAdd.setText("Add");
    }

    private void udfSetSatuanBarang(){
        lstSatuan.clear();
        lstHarga.clear();
        lstKonversi.clear();
        cmbUnit.removeAllItems();

        try{
            int iRow=jXTable1.getSelectedRow();
            String s="select * from fn_r_list_satuan_item('"+txtKode.getText()+"') as (harga double precision, satuan varchar, konversi double precision)";
            ResultSet rs=conn.createStatement().executeQuery(s);
            while(rs.next()){
                lstHarga.add(rs.getDouble("harga"));
                lstSatuan.add(rs.getString("satuan"));
                lstKonversi.add(rs.getString("konversi"));
                cmbUnit.addItem(rs.getString("satuan"));
            }
            //myModel.setValueAt(cmbSatuan.getModel(), iRow, jXTable1.getColumnModel().getColumnIndex("lstSat"));
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField textEditor=new JTextField("");
        JLabel label;// =new JLabel("");

        int col, row;

        private NumberFormat  nf=NumberFormat.getInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           col=vColIndex;
           row=rowIndex;
           textEditor.setBackground(new Color(0,255,204));
           textEditor.setFont(new Font("Tahoma",Font.PLAIN,15));
           textEditor.setSelectionStart(0);
           textEditor.setSelectionEnd(textEditor.getText().length());
           
           if(col==jXTable1.getColumnModel().getColumnIndex("QTY")){
                textEditor.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                       Component c=(Component) e.getSource();
                       c.setBackground(g1);

                       ((JTextField)e.getSource()).setSelectionStart(0);
                       ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

                    }
                    public void focusLost(FocusEvent e) {
                        Component c=(Component) e.getSource();
                        c.setBackground(g2);
                        //c.setForeground(fHitam);
                    }
               });
           }

           textEditor.addKeyListener(new java.awt.event.KeyAdapter() {
           public void keyTyped(java.awt.event.KeyEvent evt) {
              if (col==2) {
                  GeneralFunction.keyTyped(evt);
               }
            }
           public void keyPressed(java.awt.event.KeyEvent evt) {
               if (evt.getKeyCode()==KeyEvent.VK_F12) {
                    txtTunai.requestFocus();
               }else if (evt.getKeyCode()==KeyEvent.VK_F5) {
                    udfSimpanBayar();
               }else if (evt.getKeyCode()==KeyEvent.VK_F9) {
                    udfLookupBarang(textEditor);
               }else if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                    //evt.consume();
               }
            }
        });
           //textEditor.selectAll();
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            textEditor.setText(value==null? "": value.toString());
            //textEditor.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    Number dVal = nf.parse(value.toString());
                    textEditor.setText(nf.format(dVal));
                } catch (java.text.ParseException ex) {
                    Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else
                textEditor.setText(value==null? "":value.toString());
           return textEditor;
        }

        public Object getCellEditorValue() {
            Object o="";//=textEditor.getText();
            Object retVal = 0;
            if(col==jXTable1.getColumnModel().getColumnIndex("QTY")){
                try {
                    //retVal = Integer.parseInt(((JTextField)textEditor).getText().replace(",",""));
                    retVal = GeneralFunction.udfGetDouble(((JTextField)textEditor).getText());
                    o=nf.format(retVal);

                    //udfSetSubTotal(row);
                    myModel.setValueAt( GeneralFunction.udfGetDouble(((JTextField)textEditor).getText()) *
                        GeneralFunction.udfGetDouble(myModel.getValueAt(row, jXTable1.getColumnModel().getColumnIndex("Harga")).toString()),
                        row, jXTable1.getColumnModel().getColumnIndex("Sub Total"));

                    return o;
                } catch (Exception e) {
                    toolkit.beep();
                    retVal="";
                }
            }else{
                retVal=(Object)textEditor.getText();
                //JOptionPane.showMessageDialog(null, "Kode Barang : "+retVal.toString());

                if(retVal.toString().trim().length()>0){
                    retVal=udfSetBarang(retVal.toString(), row);
                    if(retVal.toString().length()==0){
                        //toolkit.beep();
                        getToolkit().beep();
                        //getToolkit().
                        //(textEditor.getKeyListeners()).consume();

                    }
                }
            }
            return retVal;
        }

    }

    public class MyKeyListener extends KeyAdapter {
        double sisa=0;

        public void keyTyped(KeyEvent e) {
            if(e.getSource().equals(txtQty)||e.getSource().equals(txtHarga)||e.getSource().equals(txtDisc)||e.getSource().equals(txtTax)){
                keyNumeric(e);
                //udfItemSubTotal();
            }
        }
        public void keyReleased(KeyEvent e) {
          //if(e.getSource().equals(txtAngsuran))
             if(e.getSource().equals(txtTunai)){
                txtKembali.setText(numFormat.format(-(GeneralFunction.udfGetDouble(lblGrandTotal.getText())-GeneralFunction.udfGetDouble(txtTunai.getText()))));
                panelKredit.setVisible(!(GeneralFunction.udfGetDouble(lblGrandTotal.getText())<=GeneralFunction.udfGetDouble(txtTunai.getText())));
             }
             if(e.getSource().equals(txtJmlCicilan)){
                sisa=GeneralFunction.udfGetDouble(lblGrandTotal.getText())-GeneralFunction.udfGetDouble(txtTunai.getText());
                txtAngsuran.setText(numFormat.format(sisa/GeneralFunction.udfGetDouble(txtJmlCicilan.getText())));
                
             }
             if(e.getSource().equals(txtDisc)||e.getSource().equals(txtTax)||e.getSource().equals(txtQty)){
                    udfSubTotalItem();

//                txtTotal.setText(numFormat.format(udfGetDouble(lblSubTotal.getText())-udfGetDouble(txtDiscRp.getText())
//                     +udfGetDouble(txtBiayaKirim.getText())+udfGetDouble(txtBiayaMaterai.getText())));
//             txtAmount.setText(txtTotal.getText());
             }
        }
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_ENTER : {
                    if(evt.getSource().getClass().getName().equals("JTable")){
                        return;
                    }
                    if (!lst.isVisible()){
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                    }else{
                    lst.requestFocus();
                    }
                    break;
                    
                }

            case KeyEvent.VK_F12: {  //Bayar
                txtTunai.requestFocus();
                break;
            }
            case KeyEvent.VK_F5: {  //Bayar
                udfSimpanBayar();
                break;
            }
            case KeyEvent.VK_INSERT: {  //Bayar
                jXTable1.requestFocusInWindow();
                jXTable1.requestFocus();
                jXTable1.changeSelection(0, 0, false, false);
                break;
            }
            case KeyEvent.VK_F9: {  //Delete
                udfLookupBarang(null);
                break;
            }

            case KeyEvent.VK_ESCAPE: {
                dispose();
            }

            case KeyEvent.VK_UP : {
                if(evt.getSource().getClass().getName().equals("JTable")){
                    return;
                }
                if (!lst.isVisible()){
                    Component c = findPrevFocus();
                    if (c!=null) c.requestFocus();
                }else{
                    lst.requestFocus();
                }
                break;
            }
            case KeyEvent.VK_DOWN : {
                if(evt.getSource().getClass().getName().equals("JTable")){
                    return;
                }
                if (!lst.isVisible()){
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                }else{
                    lst.requestFocus();
                }
                break;
            }
          }
    }



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

    private void udfSubTotalItem(){
        txtSubTotal.setText(numFormat.format((
            (GeneralFunction.udfGetDouble(txtHarga.getText())*GeneralFunction.udfGetInt(txtQty.getText()) )*
            (1-GeneralFunction.udfGetFloat(txtDisc.getText())/100))*
            (1+GeneralFunction.udfGetFloat(txtTax.getText())/100)
            ));

    }
    private void keyNumeric(KeyEvent evt){
        char c = evt.getKeyChar();
        if (!((c >= '0') && (c <= '9') ||
            (c == KeyEvent.VK_BACK_SPACE) ||
            (c == KeyEvent.VK_ENTER) ||
            (c == KeyEvent.VK_DELETE))) {
            getToolkit().beep();
            evt.consume();
        }
    }

    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);

           if(c.equals(txtTunai)||c.equals(txtJmlCicilan)||c.equals(txtAngsuran)||c.equals(txtQty)||c.equals(txtDisc)||c.equals(txtTax)){
                ((JTextField)e.getSource()).setSelectionStart(0);
               ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

           }
           
           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            //c.setForeground(fHitam);
        }
   };

    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
