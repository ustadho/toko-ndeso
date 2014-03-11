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
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.GeneralFunction;

/**
 *
 * @author ustadho
 */
public class TrxPembelian extends javax.swing.JFrame {
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
    JComboBox cmbSatuan =new JComboBox();
    private boolean isKoreksi=false;

    /** Creates new form TrxPenjualan */
    public TrxPembelian() {
        initComponents();
        jXTable1.addKeyListener(new MyKeyListener());
        
        //jXTable1.setRowHeight(25);
        jXTable1.setGridColor(new Color(1));
        jXTable1.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));

        //jXTable1.changeSelection(0, 0, true, true);
        jXTable1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        jXTable1.setSelectionForeground(new Color(255, 255, 255));
//        jXTable1.getColumnModel().getColumn(0).setCellEditor(new MyTableCellEditor());
//        jXTable1.getColumnModel().getColumn(2).setCellEditor(new MyTableCellEditor());
//        jXTable1.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbSatuan));
        
        //jXTable1.setSurrendersFocusOnKeystroke(true);
        udfClear();

        jXTable1.getModel().addTableModelListener(new MyTableModelListener(jXTable1));
        txtKodeSupp.addKeyListener(kListener);
        txtKodeSupp.addFocusListener(txtFoculListener);
        txtCatatan.addKeyListener(kListener);
        txtCatatan.addFocusListener(txtFoculListener);
        jXTable1.addKeyListener(kListener);

        for(int i=0;i<panelAddItem.getComponentCount();i++){
            Component c = panelAddItem.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  || c.getClass().getSimpleName().equalsIgnoreCase("JButton")) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);

            }
        }
        for(int i=0;i<jPanel5.getComponentCount();i++){
            Component c = jPanel5.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  || c.getClass().getSimpleName().equalsIgnoreCase("JButton")) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);

            }
        }
        btnAdd.addKeyListener(kListener);
        
        for(int i=0;i<panelCash.getComponentCount();i++){
            Component c = panelCash.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")  ) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFoculListener);
            }
        }

        cmbUnit.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(((JComboBox)e.getSource()).getSelectedIndex()>=0 && lstKonversi.size()>0){
                    //myModel.setValueAt(lstKonversi.get(((JComboBox)e.getSource()).getSelectedIndex()).toString(), jXTable1.getSelectedRow(), jXTable1.getColumnModel().getColumnIndex("Konversi"));
                    txtKonv.setText(lstKonversi.get(((JComboBox)e.getSource()).getSelectedIndex()).toString());
                    txtHarga.setText(dFmt.format(udfGetDouble(lstHarga.get(((JComboBox)e.getSource()).getSelectedIndex()).toString())));
                    txtSubTotal.setText(dFmt.format(udfGetDouble(txtHarga.getText())*udfGetDouble(txtQty.getText())));
                }
            }
        });
    }

    void setConn(Connection con){
        this.conn=con;
    }

    void setKoreksi(boolean b) {
        isKoreksi=b;
        txtNoInvoice.setEnabled(isKoreksi);
    }

    private void udfItemSubTotal(){
        double  subTotal= (GeneralFunction.udfGetDouble(txtQty.getText())* GeneralFunction.udfGetDouble(txtHarga.getText()));
        double diskon   =(subTotal/100)*GeneralFunction.udfGetDouble(txtDisc.getText());
        double tax =(subTotal/100)*GeneralFunction.udfGetDouble(txtTax.getText());

        subTotal=subTotal+tax-diskon;
        txtSubTotal.setText(new DecimalFormat("#,##0").format(subTotal));
    }

    private void udfClear() {
        txtKodeSupp.setText("");
        lblNamaSupp.setText("");
        lblAlamatSupp.setText("");
        txtDiscRp.setText("0");
        txtBiayaKirim.setText("0");
        
        myModel=(DefaultTableModel)jXTable1.getModel();
        myModel.setNumRows(0);
        jXTable1.setModel(myModel);

        txtKodeSupp.requestFocus(); lblNamaSupp.setText(""); lblAlamatSupp.setText("");
        txtCatatan.setText(""); txtNoInvoice.setText("");
        
    }

    private void udfInitForm(){
        lst.setConn(this.conn);
        lst.setVisible(false);

        SelectionListener listener = new SelectionListener(jXTable1);
        jXTable1.getSelectionModel().addListSelectionListener(listener);
        jXTable1.getColumnModel().getSelectionModel().addListSelectionListener(listener);

        try{
            ResultSet rs=conn.createStatement().executeQuery("select kode_gudang, coalesce(nama_gudang,'') as nama_gudang " +
                    "from r_gudang order by kode_gudang");

            lstGudang.clear();
            cmbGudang.removeAllItems();

            while(rs.next()){
                lstGudang.add(rs.getString("kode_gudang"));
                cmbGudang.addItem(rs.getString("nama_gudang"));
            }
            cmbGudang.setSelectedIndex(0);
            rs.close();

            jXDatePicker1.setFormats("dd/MM/yyyy");
            jXDatePicker2.setFormats("dd/MM/yyyy");

        }catch(SQLException se){

        }
    }

    private void udfLookupBarang() {
        DlgLookupBarang d1=new DlgLookupBarang(this, true);
        d1.setConn(conn);
        d1.setVisible(true);
        if(d1.getKodeBarang().trim().length()>0){
            for (int i=0; i<jXTable1.getRowCount(); i++){
                if(jXTable1.getValueAt(i, 0)==null || jXTable1.getValueAt(i, 0).toString().length()==0 ||
                   jXTable1.getValueAt(i, 1)==null || jXTable1.getValueAt(i, 1).toString().length()==0 ){
                    jXTable1.setValueAt(d1.getKodeBarang(), i, 0);

                    //udfSetBarang(d1.getKodeBarang(), jXTable1.getSelectedRow());
//                    try{
//                        String sQry="select kode_item, coalesce(nama_item,'') as nama_item, " +
//                                "coalesce(unit_jual,'') as sat_jual, coalesce(harga_jual,0)*coalesce(konv_jual,0) as harga_jual " +
//                                "from r_item where kode_item='"+d1.getKodeBarang()+"'";
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
                    return;
                }
            }
        }

        //if(new DlgLookupBarang(this, true).u)
    }

    private double udfGetDouble(String sNum){
        double hsl=0;
        if(!sNum.trim().equalsIgnoreCase("")){
            try{
                hsl=dFmt.parse(sNum).doubleValue();
            } catch (java.text.ParseException ex) {
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
        txtKodeSupp = new javax.swing.JTextField();
        lblNamaSupp = new javax.swing.JLabel();
        lblAlamatSupp = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtCatatan = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        panelCash = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtDiscRp = new javax.swing.JTextField();
        txtBiayaKirim = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtDiscPersen = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBiayaMaterai = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPembayaran = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lblSubTotal = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtNoInvoice = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel15 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jLabel16 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtNoReceipt = new javax.swing.JTextField();
        cmbGudang = new javax.swing.JComboBox();
        jLabel31 = new javax.swing.JLabel();
        panelAddItem = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtQty = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
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
        txtBatchNo = new javax.swing.JTextField();
        txtExpDate = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();

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

        txtKodeSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
        jPanel2.add(lblNamaSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 300, 21));

        lblAlamatSupp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(lblAlamatSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 35, 380, 21));

        jLabel9.setText("Catatan"); // NOI18N
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 100, 21));

        jLabel17.setText("Supplier"); // NOI18N
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 21));

        txtCatatan.setColumns(20);
        txtCatatan.setRows(5);
        jScrollPane3.setViewportView(txtCatatan);

        jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 60, 380, 40));

        jXCollapsiblePane1.getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 110));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Barang", "Batch No.", "Exp. Date", "QTY", "Satuan", "Harga", "Disc(%)", "Tax(%)", "Sub Total", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class, java.lang.Integer.class
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
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jXTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jXTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jXTable1);
        jXTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jXTable1.getColumnModel().getColumn(0).setPreferredWidth(120);
        jXTable1.getColumnModel().getColumn(1).setPreferredWidth(280);
        jXTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        jXTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        jXTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jXTable1.getColumnModel().getColumn(5).setPreferredWidth(60);
        jXTable1.getColumnModel().getColumn(7).setPreferredWidth(55);
        jXTable1.getColumnModel().getColumn(8).setPreferredWidth(55);
        jXTable1.getColumnModel().getColumn(10).setPreferredWidth(50);

        jPanel3.add(jScrollPane1);

        jScrollPane2.setViewportView(jPanel3);

        jXCollapsiblePane1.getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 171, 980, 320));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setText("F3 - Update Item / Barang");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 250, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setForeground(new java.awt.Color(0, 0, 153));
        jLabel3.setText("Ins - Insert Item / Barang");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 20));

        jPanel4.setBackground(new java.awt.Color(0, 51, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelCash.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelCash.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setText("%=");
        panelCash.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 30, 20));

        jLabel5.setText("Biaya kirim");
        panelCash.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 100, 20));

        txtDiscRp.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtDiscRp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscRp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiscRpKeyReleased(evt);
            }
        });
        panelCash.add(txtDiscRp, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 130, -1));

        txtBiayaKirim.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtBiayaKirim.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelCash.add(txtBiayaKirim, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 190, -1));

        jLabel26.setText("Discount");
        panelCash.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 20));

        txtDiscPersen.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtDiscPersen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscPersen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiscPersenKeyReleased(evt);
            }
        });
        panelCash.add(txtDiscPersen, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 30, -1));

        jLabel6.setText("Biaya Materai");
        panelCash.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 100, 20));

        txtBiayaMaterai.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtBiayaMaterai.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelCash.add(txtBiayaMaterai, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 190, -1));

        jLabel7.setText("TOTAL :");
        panelCash.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelCash.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 190, -1));

        jLabel11.setText("Pembayaran :");
        panelCash.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 115, 100, 20));

        txtPembayaran.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtPembayaran.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelCash.add(txtPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 115, 190, -1));

        jPanel4.add(panelCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 380, 140));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 400, 160));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel13.setForeground(new java.awt.Color(0, 0, 153));
        jLabel13.setText("F2 - Clear Item");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 250, 20));

        lblSubTotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubTotal.setText("0");
        jPanel1.add(lblSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 190, 20));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel28.setText("Sub Total :");
        jPanel1.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 10, 100, 20));

        jButton2.setText("Simpan");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 163, 100, 30));

        jXCollapsiblePane1.getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 500, 980, 200));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNoInvoice.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
        jPanel5.add(txtNoInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 120, 21));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("Tgl. Invoice"); // NOI18N
        jPanel5.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 130, 21));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Invoice No."); // NOI18N
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, 21));
        jPanel5.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 130, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Amount"); // NOI18N
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 120, 21));

        txtAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, 120, 21));
        jPanel5.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 80, 130, -1));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Tgl. Jatuh Tempo"); // NOI18N
        jPanel5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, 130, 21));

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel30.setText("Gudang"); // NOI18N
        jPanel5.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 120, 21));

        txtNoReceipt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoReceipt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNoReceiptFocusLost(evt);
            }
        });
        txtNoReceipt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNoReceiptKeyReleased(evt);
            }
        });
        jPanel5.add(txtNoReceipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 120, 21));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel5.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 130, -1));

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel31.setText("Receipt No."); // NOI18N
        jPanel5.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 120, 21));

        jXCollapsiblePane1.getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, 430, 110));

        panelAddItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAddItem.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setBackground(new java.awt.Color(255, 255, 204));
        jLabel8.setText("Kode");
        jLabel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel8.setOpaque(true);
        panelAddItem.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 2, 120, -1));

        txtKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKeyReleased(evt);
            }
        });
        panelAddItem.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 120, -1));

        jLabel18.setBackground(new java.awt.Color(255, 255, 204));
        jLabel18.setText("Barang");
        jLabel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel18.setOpaque(true);
        panelAddItem.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 2, 230, -1));

        txtNamaBarang.setEditable(false);
        panelAddItem.add(txtNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 230, -1));

        jLabel19.setBackground(new java.awt.Color(255, 255, 204));
        jLabel19.setText("Satuan");
        jLabel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel19.setOpaque(true);
        panelAddItem.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 2, 60, -1));

        txtQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 20, 40, -1));

        jLabel20.setBackground(new java.awt.Color(255, 255, 204));
        jLabel20.setText("Exp. Date");
        jLabel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel20.setOpaque(true);
        panelAddItem.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(437, 2, 63, -1));

        panelAddItem.add(cmbUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 20, 60, -1));

        jLabel21.setBackground(new java.awt.Color(255, 255, 204));
        jLabel21.setText("Harga");
        jLabel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel21.setOpaque(true);
        panelAddItem.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 2, 80, -1));

        txtHarga.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 20, 80, -1));

        jLabel22.setBackground(new java.awt.Color(255, 255, 204));
        jLabel22.setText("Disc (%)");
        jLabel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel22.setOpaque(true);
        panelAddItem.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 2, 50, -1));

        txtDisc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 20, 50, -1));

        jLabel23.setBackground(new java.awt.Color(255, 255, 204));
        jLabel23.setText("Tax (%)");
        jLabel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel23.setOpaque(true);
        panelAddItem.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 2, 50, -1));

        txtTax.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtTax, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 20, 50, -1));

        jLabel24.setBackground(new java.awt.Color(255, 255, 204));
        jLabel24.setText("Sub Total");
        jLabel24.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel24.setOpaque(true);
        panelAddItem.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 2, 80, -1));

        txtSubTotal.setEditable(false);
        txtSubTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 20, 80, -1));

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setText("Konv");
        jLabel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel25.setOpaque(true);
        panelAddItem.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 2, 40, -1));

        txtKonv.setEditable(false);
        txtKonv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 20, 40, -1));

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        panelAddItem.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 19, 79, -1));

        txtBatchNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtBatchNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 77, -1));

        txtExpDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtExpDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(437, 20, 63, -1));

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setText("QTY");
        jLabel27.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel27.setOpaque(true);
        panelAddItem.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 2, 40, -1));

        jLabel29.setBackground(new java.awt.Color(255, 255, 204));
        jLabel29.setText("Batch No.");
        jLabel29.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel29.setOpaque(true);
        panelAddItem.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 2, 77, -1));

        jXCollapsiblePane1.getContentPane().add(panelAddItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 980, 43));

        getContentPane().add(jXCollapsiblePane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1000, 710));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1024)/2, (screenSize.height-768)/2, 1024, 768);
    }// </editor-fold>//GEN-END:initComponents

    private void txtKodeSuppFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtKodeSuppFocusLost
//        if(!lst.isVisible() && !txtNoAnggota.getText().equalsIgnoreCase("") && isNew)
//            txtPinjamanKe.setText(dFmt.format(getPinjamanKe()));
}//GEN-LAST:event_txtKodeSuppFocusLost

    private void txtKodeSuppKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeSuppKeyReleased
        try {
            String sCari = txtKodeSupp.getText();
            switch (evt.getKeyCode()) {
                case java.awt.event.KeyEvent.VK_ENTER: {
                    if (lst.isVisible()) {
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtKodeSupp.setText(obj[0].toString());
                            lst.udfSelected();
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
                    txtKodeSupp.setText("");
                    lblNamaSupp.setText("");
                    lblAlamatSupp.setText("");
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
                        String sQry = "select kode_supp as Kode, coalesce(nama_supp,'') as Supplier, " +
                                "coalesce(alamat_1,'')||' '||coalesce(nama_kota,'') as alamat  " +
                                "from r_supplier s " +
                                "left join m_kota k on s.kota=k.kode_kota " +
                                "where (kode_supp||coalesce(nama_supp,'')) " +
                                "iLike '%" + txtKodeSupp.getText() + "%' order by coalesce(nama_supp,'') ";

                        //System.out.println(sQry);
                        lst.setSQuery(sQry);

                        lst.setBounds(this.getX()+jXCollapsiblePane1.getX()+ this.jPanel2.getX() + this.txtKodeSupp.getX() + 4,
                                this.getY() + jXCollapsiblePane1.getY()+ this.jPanel2.getY() + this.txtKodeSupp.getY() + txtKodeSupp.getHeight() + 33,
                                txtKodeSupp.getWidth() + lblNamaSupp.getWidth()+120,
                                (lst.getIRowCount()>10? 12*lst.getRowHeight(): (lst.getIRowCount()+3)*lst.getRowHeight()));


                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtKodeSupp);
                        lst.setCompDes(new javax.swing.JLabel[]{lblNamaSupp, lblAlamatSupp});
                        lst.setColWidth(0, txtKodeSupp.getWidth());
                        lst.setColWidth(1, lblNamaSupp.getWidth()-10);

                        if (lst.getIRowCount() > 0) {
                            lst.setVisible(true);
                            requestFocusInWindow();
                            txtKodeSupp.requestFocus();
                        } else {
                            lst.setVisible(false);
                            txtKodeSupp.setText("");
                            lblNamaSupp.setText("");
                            lblAlamatSupp.setText("");
                            txtKodeSupp.requestFocus();
                        }
                    }
                    break;
                }
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
}//GEN-LAST:event_txtKodeSuppKeyReleased

    private void txtDiscRpKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscRpKeyReleased
        //        txtKembali.setText(numFormat.format(dTotalTrx-GeneralFunction.udfGetDouble(txtTunai.getText())));
        //        jPanel2.setVisible(!(dTotalTrx<=GeneralFunction.udfGetDouble(txtTunai.getText())));
}//GEN-LAST:event_txtDiscRpKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        udfInitForm();

    }//GEN-LAST:event_formWindowOpened

    private void txtNoInvoiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoInvoiceFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoInvoiceFocusLost

    private void txtNoInvoiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoInvoiceKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoInvoiceKeyReleased

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
                        cmbUnit.setModel(cmbSatuan.getModel());
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

    private void txtDiscPersenKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscPersenKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtDiscPersenKeyReleased

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if(btnAdd.getText().equalsIgnoreCase("ADD")){
            myModel.addRow(new Object[]{
                txtKode.getText(),
                txtNamaBarang.getText(),
                txtBatchNo.getText(),
                txtExpDate.getText(),
                dFmt.format(udfGetDouble(txtQty.getText())),
                cmbSatuan.getSelectedIndex()>=0? cmbSatuan.getSelectedItem().toString():"",
                dFmt.format(udfGetDouble(txtHarga.getText())),
                dFmt.format(udfGetDouble(txtDisc.getText())),
                dFmt.format(udfGetDouble(txtTax.getText())),
                dFmt.format(udfGetDouble(txtSubTotal.getText())),
                txtKonv.getText()
            });
            jXTable1.setRowSelectionInterval(myModel.getRowCount()-1, myModel.getRowCount()-1);
        }else{
            int iRow=jXTable1.getSelectedRow();
            TableColumnModel column=jXTable1.getColumnModel();
            txtKode.setText(myModel.getValueAt(iRow, column.getColumnIndex("Kode")).toString());
            udfSetSatuanBarang();
            txtNamaBarang.setText(myModel.getValueAt(iRow, column.getColumnIndex("Barang")).toString());
            txtBatchNo.setText(myModel.getValueAt(iRow, column.getColumnIndex("Batch No.")).toString());
            txtExpDate.setText(myModel.getValueAt(iRow, column.getColumnIndex("Exp. Date")).toString());
            txtQty.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("QTY")).toString())));
            cmbSatuan.setSelectedItem(myModel.getValueAt(iRow, column.getColumnIndex("Satuan")));
            txtHarga.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Harga")).toString())));
            txtQty.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("QTY")).toString())));
            txtDisc.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Disc(%)")).toString())));
            txtTax.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Tax(%)")).toString())));
            txtSubTotal.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Sub Total")).toString())));
            txtKonv.setText(dFmt.format(GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("Konv")).toString())));
            btnAdd.setText("Add");
        }
        udfStartNewItem();
}//GEN-LAST:event_btnAddActionPerformed

    private void jXTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jXTable1KeyPressed
        int iRow=jXTable1.getSelectedRow();
        if(iRow>=0){
            if(evt.getKeyCode()==KeyEvent.VK_DELETE){
                if(JOptionPane.showConfirmDialog(this, "Apakah benar item akan dihapus?", "Konfirmasi delete item", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    myModel.removeRow(jXTable1.getSelectedRow());
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_F3){
                TableColumnModel column=jXTable1.getColumnModel();

                txtKode.setText(myModel.getValueAt(iRow, column.getColumnIndex("Kode")).toString());
                udfSetSatuanBarang();
                txtNamaBarang.setText(myModel.getValueAt(iRow, column.getColumnIndex("Barang")).toString());
                txtBatchNo.setText(myModel.getValueAt(iRow, column.getColumnIndex("Batch No.")).toString());
                txtExpDate.setText(myModel.getValueAt(iRow, column.getColumnIndex("Exp. Date")).toString());
                txtQty.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("QTY")).toString())));
                cmbSatuan.setSelectedItem(myModel.getValueAt(iRow, column.getColumnIndex("Satuan")));
                txtHarga.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Harga")).toString())));
                txtQty.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("QTY")).toString())));
                txtDisc.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Disc(%)")).toString())));
                txtTax.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Tax(%)")).toString())));
                txtSubTotal.setText(dFmt.format(udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Sub Total")).toString())));
                txtKonv.setText(dFmt.format(GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("Konv")).toString())));
                btnAdd.setText("Update");
            }
        }

    }//GEN-LAST:event_jXTable1KeyPressed

    private void txtNoReceiptFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNoReceiptFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoReceiptFocusLost

    private void txtNoReceiptKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoReceiptKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtNoReceiptKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        udfSimpanTrx();
    }//GEN-LAST:event_jButton2ActionPerformed

    private boolean udfCekBeforeSave(){
        boolean b=true, adaBarang=false;
        if(txtKodeSupp.getText().trim().length()==0){
            JOptionPane.showMessageDialog(this, "Silakan isikan Supplier terlebih dulu!");
            txtKodeSupp.requestFocus();
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


//        if(GeneralFunction.udfGetDouble(txtTunai.getText())+ (udfGetDouble(txtJmlCicilan.getText())* udfGetDouble(txtAngsuran.getText())) < udfGetDouble(lblGrandTotal.getText()) ){
//            JOptionPane.showMessageDialog(this, "Jumlah pembayaran masih kurang dari total penjualan!");
//            txtTunai.requestFocus();
//            return false;
//        }

        return b;
    }

    
    
    private void udfSimpanTrx() {
        if(udfCekBeforeSave()){
//            DlgBayarPenjualan d1=new DlgBayarPenjualan(this, true);
//            d1.setConn(this.conn);
//            d1.udfSetTotalTrx(GeneralFunction.udfGetDouble(lblGrandTotal.getText()));
//            d1.setVisible(true);
            
            //if(d1.isBayar()){
                try{
                    conn.setAutoCommit(false);
                    String sQry="select fn_r_ins_purchase_header('"+txtNoReceipt.getText()+"', " +
                            "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', '', " +
                            "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', '', " +
                            "'"+lstGudang.get(cmbGudang.getSelectedIndex()).toString()+"', '"+txtKodeSupp.getText()+"', " +
                            udfGetDouble(txtDiscPersen.getText())+ ","+udfGetDouble(txtDiscRp.getText())+", "+
                            udfGetDouble(txtBiayaKirim.getText())+ ","+udfGetDouble(txtBiayaMaterai.getText())+", " +
                            udfGetDouble(txtPembayaran.getText())+ ",'', true, true, '', '"+txtCatatan.getText()+"', '"+MainForm.sUserName+"', false) ";

                    System.out.println(sQry);

                    ResultSet rs=conn.createStatement().executeQuery(sQry);
                    
                    if(rs.next()){
                        txtNoInvoice.setText(rs.getString(1));
                        ResultSet rsDet =conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from r_purchase_detail limit 0");
                        TableColumnModel column=jXTable1.getColumnModel();
                        SimpleDateFormat yMd=new SimpleDateFormat("yyyy/MM/dd");
                        SimpleDateFormat dMy=new SimpleDateFormat("dd/MM/yyyy");

                        for(int iRow=0; iRow<jXTable1.getRowCount(); iRow++){
                            if(jXTable1.getValueAt(iRow, jXTable1.getColumnModel().getColumnIndex("Barang")).toString().length()>0){
                                rsDet.moveToInsertRow();
                                rsDet.updateString("purchase_no", rs.getString(1));
                                rsDet.updateString("kode_item", myModel.getValueAt(iRow, column.getColumnIndex("Kode")).toString());
                                rsDet.updateDate("exp_date", myModel.getValueAt(iRow, column.getColumnIndex("Exp. Date")).toString().equalsIgnoreCase("")? null: java.sql.Date.valueOf(yMd.format(dMy.parse(myModel.getValueAt(iRow, column.getColumnIndex("Exp. Date")).toString()))) );
                                rsDet.updateString("batch_no", myModel.getValueAt(iRow, column.getColumnIndex("Batch No.")).toString());
                                rsDet.updateInt("qty", GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("QTY")).toString())* GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("Konv")).toString()));
                                rsDet.updateDouble("unit_price", GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Harga")).toString())/GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("Konv")).toString()));
                                rsDet.updateDouble("disc", GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Disc(%)")).toString()));
                                rsDet.updateDouble("tax", GeneralFunction.udfGetDouble(myModel.getValueAt(iRow, column.getColumnIndex("Tax(%)")).toString()));
                                rsDet.updateString("kode_dep", "");
                                rsDet.updateString("project", "");
                                rsDet.updateString("no_po", "");
                                rsDet.updateString("unit", myModel.getValueAt(iRow, column.getColumnIndex("Satuan")).toString());
                                rsDet.updateInt("konv", GeneralFunction.udfGetInt(myModel.getValueAt(iRow, column.getColumnIndex("Konv")).toString()));
                                rsDet.insertRow();
                            }
                        }
//                        System.out.println(sQry);
//                        ResultSet rsD=conn.createStatement().executeQuery(sQry);

                        conn.setAutoCommit(true);
//                        if(rsD.next()){
                            JOptionPane.showMessageDialog(this, "Input data sukses");
                            udfClear();
//                        }
                    }

                    rs.close();
                } catch (java.text.ParseException ex) {
                Logger.getLogger(TrxPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }catch(SQLException se){
                    try {
                        conn.setAutoCommit(true);
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, se.getMessage());

                    } catch (SQLException ex) {
//                        conn.setAutoCommit(true);
//                        JOptionPane.showMessageDialog(this, "transaksi di Rollback")
                        Logger.getLogger(TrxPembelian.class.getName()).log(Level.SEVERE, null, ex);
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
                new TrxPembelian().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbUnit;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblAlamatSupp;
    private javax.swing.JLabel lblNamaSupp;
    private javax.swing.JLabel lblSubTotal;
    private javax.swing.JPanel panelAddItem;
    private javax.swing.JPanel panelCash;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtBiayaKirim;
    private javax.swing.JTextField txtBiayaMaterai;
    private javax.swing.JTextArea txtCatatan;
    private javax.swing.JTextField txtDisc;
    private javax.swing.JTextField txtDiscPersen;
    private javax.swing.JTextField txtDiscRp;
    private javax.swing.JTextField txtExpDate;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKodeSupp;
    private javax.swing.JTextField txtKonv;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtNoInvoice;
    private javax.swing.JTextField txtNoReceipt;
    private javax.swing.JTextField txtPembayaran;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtTax;
    private javax.swing.JTextField txtTotal;
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
            dTotal+=udfGetDouble(myModel.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Sub Total")).toString());
        }
        lblSubTotal.setText(dFmt.format(dTotal));
        }
    }

    public class MyComboboxCellEditor extends AbstractCellEditor implements TableCellEditor{
        JComboBox combo=new JComboBox(cmbSatuan.getModel());
        

        public Object getCellEditorValue() {
            return (Object)combo.getSelectedItem().toString();

        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(row>=0) udfSetSatuanBarang();
            combo.removeAllItems();
            combo.setModel(cmbSatuan.getModel());
            int i=cmbSatuan.getModel().getSize();

            combo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    myModel.setValueAt(lstKonversi.get(combo.getSelectedIndex()).toString(), jXTable1.getSelectedRow(), jXTable1.getColumnModel().getColumnIndex("Konversi"));
                }
            });
            return combo;
        }

    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField component=new JTextField("");
        JLabel label;// =new JLabel("");

        int col, row;

        private NumberFormat  nf=NumberFormat.getInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           col=vColIndex;
           row=rowIndex;
           component.setBackground(new Color(0,255,204));
           component.setFont(new Font("Tahoma",Font.PLAIN,15));
           component.setSelectionStart(0);
           component.setSelectionEnd(component.getText().length());
           
           if(col==jXTable1.getColumnModel().getColumnIndex("QTY")){
                component.addFocusListener(new FocusListener() {
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

           component.addKeyListener(new java.awt.event.KeyAdapter() {
           public void keyTyped(java.awt.event.KeyEvent evt) {
              if (col==2) {
                  char c = evt.getKeyChar();
                  if (!((c >= '0') && (c <= '9') ||
                     (c != KeyEvent.VK_BACK_SPACE) ||
                     (c != KeyEvent.VK_DELETE) ||
                     (c != KeyEvent.VK_ENTER))) {
                        getToolkit().beep();
                        evt.consume();
                        return;
                  }
               }
            }
           public void keyPressed(java.awt.event.KeyEvent evt) {
               if (evt.getKeyCode()==KeyEvent.VK_F12) {
                  txtDiscRp.requestFocus();
               }
               if (evt.getKeyCode()==KeyEvent.VK_F5) {
                  udfSimpanTrx();
               }
            }
        });
           //component.selectAll();
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            component.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    Number dVal = nf.parse(value.toString());
                    component.setText(nf.format(dVal));
                } catch (java.text.ParseException ex) {
                    Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else
                component.setText(value==null? "":value.toString());
           return component;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            if(col==jXTable1.getColumnModel().getColumnIndex("QTY")){
                try {
                    //retVal = Integer.parseInt(((JTextField)component).getText().replace(",",""));
                    retVal = udfGetDouble(((JTextField)component).getText());
                    o=nf.format(retVal);

                    //udfSetSubTotal(row);
                    myModel.setValueAt( udfGetDouble(((JTextField)component).getText()) *
                        udfGetDouble(myModel.getValueAt(row, jXTable1.getColumnModel().getColumnIndex("Harga")).toString()),
                        row, jXTable1.getColumnModel().getColumnIndex("Sub Total"));

                    return o;
                } catch (Exception e) {
                    toolkit.beep();
                    retVal=0;
                }
            }else{
                retVal=(Object)component.getText();
                //JOptionPane.showMessageDialog(null, "Kode Barang : "+retVal.toString());

                if(retVal.toString().trim().length()>0){
                    //retVal=udfSetBarang(retVal.toString(), row);
                }
            }
            return retVal;
        }

    }

    private void udfSetSatuanBarang(){
        lstSatuan.clear();
        lstHarga.clear();
        lstKonversi.clear();
        cmbSatuan.removeAllItems();

        try{
            int iRow=jXTable1.getSelectedRow();
            ResultSet rs=conn.createStatement().executeQuery("select * from fn_r_list_satuan_item('"+txtKode.getText()+"') as (harga double precision, satuan varchar, konversi double precision)");
            while(rs.next()){
                lstHarga.add(rs.getDouble("harga"));
                lstSatuan.add(rs.getString("satuan"));
                cmbSatuan.addItem(rs.getString("satuan"));
                lstKonversi.add(rs.getString("konversi"));
            }
            //myModel.setValueAt(cmbSatuan.getModel(), iRow, jXTable1.getColumnModel().getColumnIndex("lstSat"));
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(TrxPembelian.this, se.getMessage());
        }
    }

    public class SelectionListener implements ListSelectionListener {
         JTable table;
         int rowPos;
         int colPos;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }
        public void valueChanged(ListSelectionEvent e) {
            if(table.getSelectedRow()>=0 && table.getSelectedColumn()!=jXTable1.getColumnModel().getColumnIndex("Satuan")){
                rowPos = table.getSelectedRow();
                if (rowPos >=0 && rowPos < table.getRowCount() && table.getValueAt(rowPos,0)!=null && table.getValueAt(rowPos,0).toString().length()>0) {
                    //udfSetSatuanBarang();
                }
            }
        }
    }

    public class MyKeyListener extends KeyAdapter {
        double sisa=0;

        public void keyTyped(KeyEvent e) {
            if(e.getSource().equals(txtQty)||e.getSource().equals(txtHarga)||e.getSource().equals(txtDisc)||e.getSource().equals(txtTax)||
               e.getSource().equals(txtDiscRp)||e.getSource().equals(txtDiscPersen)||e.getSource().equals(txtBiayaKirim)||
               e.getSource().equals(txtPembayaran)||e.getSource().equals(txtBiayaMaterai)){
                keyNumeric(e);
                //udfItemSubTotal();
            }
        }
        public void keyReleased(KeyEvent e) {
          //if(e.getSource().equals(txtAngsuran))
             if(e.getSource().equals(txtDiscPersen)){
                txtDiscRp.setText(numFormat.format(((GeneralFunction.udfGetDouble(lblSubTotal.getText())/100)*GeneralFunction.udfGetFloat(txtDiscPersen.getText()))));
                //panelKredit.setVisible(!(GeneralFunction.udfGetDouble(lblGrandTotal.getText())<=GeneralFunction.udfGetDouble(txtTunai.getText())));
             }
//             if(e.getSource().equals(txtJmlCicilan)){
//                sisa=udfGetDouble(lblGrandTotal.getText())-udfGetDouble(txtTunai.getText());
//                txtAngsuran.setText(numFormat.format(sisa/GeneralFunction.udfGetDouble(txtJmlCicilan.getText())));
//
//             }

             txtTotal.setText(numFormat.format(udfGetDouble(lblSubTotal.getText())-udfGetDouble(txtDiscRp.getText())
                     +udfGetDouble(txtBiayaKirim.getText())+udfGetDouble(txtBiayaMaterai.getText())));
             txtAmount.setText(txtTotal.getText());

        }
    public void keyPressed(KeyEvent evt) {
        int keyKode = evt.getKeyCode();
        switch(keyKode){
            case KeyEvent.VK_ENTER : {
                if (!lst.isVisible()){
                    Component c = findNextFocus();
                    if (c!=null) c.requestFocus();
                }else{
                    lst.requestFocus();
                }
                break;
            }

            case KeyEvent.VK_UP : {
                if (!lst.isVisible()){
                Component c = findPrevFocus();
                if (c!=null) c.requestFocus();
                }else{
                lst.requestFocus();
                }
                break;
            }
            case KeyEvent.VK_DOWN : {
                if (!lst.isVisible()){
                Component c = findNextFocus();
                if (c!=null) c.requestFocus();
                }else{
                lst.requestFocus();
                }
                break;
            }
            case KeyEvent.VK_ESCAPE : {
                if(evt.getSource().equals(txtKode)||evt.getSource().equals(txtNamaBarang)||evt.getSource().equals(txtBatchNo)||
                        evt.getSource().equals(txtKode)||evt.getSource().equals(txtExpDate)||evt.getSource().equals(txtQty)||
                        evt.getSource().equals(cmbUnit)||evt.getSource().equals(txtDisc)||evt.getSource().equals(txtTax)||
                        evt.getSource().equals(txtSubTotal)||evt.getSource().equals(txtKonv)){

                    udfStartNewItem();
                    lst.setVisible(false);
                    btnAdd.setText("Add");
                }

                break;
            }
            case KeyEvent.VK_F2: {  //Bayar
                udfStartNewItem();
                break;
            }
            case KeyEvent.VK_F3: {  //Bayar
                jXTable1KeyPressed(evt);
                break;
            }
            case KeyEvent.VK_INSERT: {  //insert item
                btnAddActionPerformed(new ActionEvent(btnAdd, ActionEvent.ACTION_PERFORMED, "Add"));
                break;
            }
            case KeyEvent.VK_F9: {  //Delete
                udfLookupBarang();
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

    private void udfStartNewItem(){
        txtKode.setText("");
        txtNamaBarang.setText("");
        txtBatchNo.setText("");
        txtExpDate.setText("");
        txtQty.setText("");
        txtHarga.setText("");
        cmbSatuan.removeAllItems();
        txtDisc.setText("");
        txtTax.setText("");
        txtSubTotal.setText("");
        txtKonv.setText("");
        txtKode.requestFocus();
        btnAdd.setText("Add");
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

           if(c.equals(txtDiscRp)||c.equals(txtDiscPersen)||c.equals(txtDiscRp)||c.equals(txtQty)||c.equals(txtDisc)||c.equals(txtHarga)||c.equals(txtTax)){
                ((JTextField)e.getSource()).setSelectionStart(0);
               ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());

           }
           
           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);

            if(e.getSource().equals(txtQty)||e.getSource().equals(txtHarga)||e.getSource().equals(txtDisc)||e.getSource().equals(txtTax)){
                ((JTextField)e.getSource()).setText(dFmt.format(udfGetDouble(((JTextField)e.getSource()).getText())));
                udfItemSubTotal();
            }else if(c.equals(txtExpDate)){
                if(txtExpDate.getText().trim().equalsIgnoreCase("")) return;
                if(!txtExpDate.getText().trim().equalsIgnoreCase("/  /")||!txtExpDate.getText().trim().equalsIgnoreCase("")) {
                    if(txtExpDate.getText().length()==7) txtExpDate.setText("01/"+txtExpDate.getText());
                    if(!validateDate(txtExpDate.getText(),true,"dd/MM/yyyy")){
                        JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                        //txtExpDate.setText("");
                        txtExpDate.requestFocus();

                        return;

                    }
                }else{
                    txtExpDate.setText("");
                }
           }else if(e.getSource().equals(txtDiscRp)||e.getSource().equals(txtBiayaKirim)||e.getSource().equals(txtBiayaMaterai)||e.getSource().equals(txtPembayaran)){
                ((JTextField)e.getSource()).setText(dFmt.format(udfGetDouble(((JTextField)e.getSource()).getText())));
           }
            //c.setForeground(fHitam);
        }
   };

   public static boolean validateDate( String dateStr, boolean allowPast, String formatStr){
     if (formatStr == null) return false; // or throw some kinda exception, possibly a InvalidArgumentException
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		Date testDate = null;
		try
		{
			testDate = df.parse(dateStr);
		}
		catch (java.text.ParseException e)
		{
			// invalid date format
			return false;
		}
		if (!allowPast)
		{
			// initialise the calendar to midnight to prevent
			// the current day from being rejected
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTime().after(testDate)) return false;
		}
		// now test for legal values of parameters
		if (!df.format(testDate).equals(dateStr)) return false;
		return true;
	}

    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
