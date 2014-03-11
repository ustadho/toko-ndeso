/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmBaserPriceByItem.java
 *
 * Created on Aug 8, 2010, 2:11:18 PM
 */

package retail;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import retail.main.DlgLookup;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmSupplierPriceByItem extends javax.swing.JFrame {
    private Connection conn;
    private GeneralFunction fn;
    MyKeyListener kListener=new MyKeyListener();
    protected DecimalFormat dFmt=new DecimalFormat("#,##0.00");
    private JComboBox cmbSatuan=new JComboBox();

    /** Creates new form FrmBaserPriceByItem */
    public FrmSupplierPriceByItem() {
        initComponents();
        tblSupplier.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Rk")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Price List1")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Disc1%")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("PPN1%")).setCellEditor(cEditor);
        tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("Konversi")).setCellEditor(cEditor);
        tblSupplier.getColumn("Satuan2").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
        //tblSupplier.getColumnModel().getColumn(tblSupplier.getColumnModel().getColumnIndex("UOM Alt")).setCellEditor(cEditor);

        tblSupplier.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=tblSupplier.getSelectedRow();
                if(iRow>=0){
                    udfLoadKetBawah(iRow);
                    
                }else{
                    lblBasePrice.setText("0");
                    lblHargaRetur.setText("0");
                }
            }
        });

        tblSupplier.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if(e.getType()==TableModelEvent.UPDATE){
                    TableColumnModel col=tblSupplier.getColumnModel();
                    int iRow=tblSupplier.getSelectedRow();
                    if(iRow>=0 && (e.getColumn()==col.getColumnIndex("Konversi")||
                            e.getColumn()==col.getColumnIndex("Price List1")||
                            e.getColumn()==col.getColumnIndex("Disc1%")||
                            e.getColumn()==col.getColumnIndex("PPN1%"))){
                        double hargaRetur=fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List1")))-
                                     (fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List1")))/100 * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc1%"))));
                        hargaRetur=hargaRetur+(hargaRetur/100 * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN1%"))));
                        hargaRetur=hargaRetur/fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Konversi")));
                        tblSupplier.setValueAt(hargaRetur, iRow, col.getColumnIndex("Harga Beli"));
                        udfLoadKetBawah(iRow);
                    }else if(iRow>=0 && e.getColumn()==col.getColumnIndex("Satuan2")){
                        tblSupplier.setValueAt(tblSupplier.getValueAt(iRow, col.getColumnIndex("Satuan2")),
                                iRow, col.getColumnIndex("Satuan Kcl"));
                    }
                }
            }
        });
        cmbSatuan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if(cmbSatuan.getSelectedIndex()>0 && conn!=null)
                    udfLoadKonversi(cmbSatuan.getSelectedItem().toString());

            }
        });
        
    }

    private void udfLoadKonversi(String sUnit) {
        int row=tblSupplier.getSelectedRow();
        if(row<0) return;
        try {
            String sQry = "select case  when '" + sUnit + "'=unit then 1 " +
                          "             when '" + sUnit + "'=unit2 then coalesce(konv2,1) " +
                          "             when '" + sUnit + "'=unit3 then coalesce(konv3,1) " +
                          "             else 1 end as konv, " +
                          "case when '"+sUnit+"'=uom_alt then coalesce(s.price,0) else 0 end as price, " +
                          "case when '"+sUnit+"'=uom_alt then coalesce(s.disc,0) else 0 end as disc," +
                          "case when '"+sUnit+"'=uom_alt then coalesce(s.vat,0) else 0 end as vat " +
                          "from r_item i " +
                          "left join r_item_supplier s on s.kode_item=i.kode_item and s.kode_supp='"+tblSupplier.getValueAt(row, 0).toString()+"' " +
                          "where i.kode_item='" + txtItem.getText() + "'";

            System.out.println(sQry);
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            if (rs.next()) {
                tblSupplier.setValueAt(rs.getInt("konv"), row, tblSupplier.getColumnModel().getColumnIndex("Konversi"));
            } else {
                tblSupplier.setValueAt(1, row, tblSupplier.getColumnModel().getColumnIndex("Konversi"));
            }
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfLoadKetBawah(int iRow){
        TableColumnModel col=tblSupplier.getColumnModel();
        if(tblSupplier.getValueAt(iRow, 0)==null||tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List1"))==null)
            return;

        double basePrice=fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List1")))+
                        (fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List1")))/100 * fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN1%"))));
        basePrice=basePrice/fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Konversi")));

        double hargaRetur=fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Price List1")))*
                         (1 - fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Disc1%")))/100);
        hargaRetur=hargaRetur *(1 + fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("PPN1%")))/100);
        hargaRetur=hargaRetur/fn.udfGetDouble(tblSupplier.getValueAt(iRow, col.getColumnIndex("Konversi")));

        lblBasePrice.setText(dFmt.format(basePrice));
        lblHargaRetur.setText(dFmt.format(hargaRetur));
    }
    public void setConn(Connection con){
        this.conn=con;

    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        tblSupplier.addKeyListener(kListener);
        jScrollPane1.addKeyListener(kListener);
        tblSupplier.setRowHeight(22);
        tblSupplier.getColumn("Harga Beli").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("Price List1").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("Disc1%").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("PPN1%").setCellRenderer(new MyRowRenderer());
        tblSupplier.getColumn("Konversi").setCellRenderer(new MyRowRenderer());
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

                if(e.getSource().equals(txtItem) && !fn.isListVisible())
                    udfLoadSupplier();

           }
        }


    } ;

    private void udfLoadSupplier(){
        String s="select sb.kode_supp, coalesce(s.nama_supp,'') as nama, coalesce(sb.priority,0) as rk," +
                "coalesce(sb.price,0)-(coalesce(sb.price,0)/100*coalesce(sb.disc,0))+" +
                "(coalesce(sb.price,0)-(coalesce(sb.price,0)/100*coalesce(sb.disc,0)))/100*coalesce(sb.vat,0) as harga_beli, " +
                "coalesce(sb.price,0) as price_list1, coalesce(sb.disc,0) as disc, coalesce(sb.vat,0) as ppn, " +
                "coalesce(sb.uom_alt,'') as uom_alt, coalesce(convertion,1) as konv, " +
                "coalesce(i.unit,'') as unit_kecil " +
                "from r_item_supplier sb " +
                "inner join r_item i on i.kode_item=sb.kode_item " +
                "left join r_supplier s on s.kode_supp=sb.kode_supp " +
                "where sb.kode_item='"+txtItem.getText()+"' order by priority ";
        try{
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(unit,'') as unit, coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                    "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3 " +
                    "from r_item where kode_item='"+txtItem.getText()+"'");
            cmbSatuan.removeAllItems();
            if(rs.next()){
                cmbSatuan.addItem(rs.getString("unit"));
                if(rs.getString("unit2").length()>0) cmbSatuan.addItem(rs.getString("unit2"));
                if(rs.getString("unit3").length()>0) cmbSatuan.addItem(rs.getString("unit3"));
            }
            rs.close();
            rs=conn.createStatement().executeQuery(s);
            ((DefaultTableModel)tblSupplier.getModel()).setNumRows(0);
            while(rs.next())
                ((DefaultTableModel)tblSupplier.getModel()).addRow(new Object[]{
                    rs.getString("kode_supp"),
                    rs.getString("nama"),
                    rs.getInt("rk"),
                    rs.getDouble("harga_beli")/ rs.getDouble("konv"),
                    rs.getString("unit_kecil"),
                    rs.getDouble("price_list1"),
                    rs.getDouble("disc"),
                    rs.getDouble("ppn"),
                    rs.getString("uom_alt"),
                    rs.getDouble("konv")
                });
            tblSupplier.setModel((DefaultTableModel)fn.autoResizeColWidth(tblSupplier, (DefaultTableModel)tblSupplier.getModel()).getModel());
            if(tblSupplier.getRowCount()>0)
                tblSupplier.setRowSelectionInterval(0, 0);

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

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        lblItemName = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtSatKecil = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSupplier = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblBasePrice = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblHargaRetur = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Price By Product");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel1.setText("Master Price By Product");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 630, 60));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Product ID");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(":");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 10, 20));

        txtItem.setFont(new java.awt.Font("Dialog", 0, 12));
        txtItem.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtItem.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtItem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtItemFocusLost(evt);
            }
        });
        txtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtItemKeyReleased(evt);
            }
        });
        jPanel1.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 20));

        lblItemName.setFont(new java.awt.Font("Dialog", 0, 12));
        lblItemName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblItemName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblItemNamePropertyChange(evt);
            }
        });
        jPanel1.add(lblItemName, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 430, 20));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Satuan Kecil");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 90, 20));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(":");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 10, 20));

        txtSatKecil.setFont(new java.awt.Font("Dialog", 0, 14));
        txtSatKecil.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtSatKecil.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSatKecil.setEnabled(false);
        txtSatKecil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSatKecilKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSatKecilKeyTyped(evt);
            }
        });
        jPanel1.add(txtSatKecil, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 120, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 780, 60));

        tblSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SupplierID", "Nama Supplier", "Rk", "Harga Beli", "Satuan Kcl", "Price List1", "Disc1%", "PPN1%", "Satuan2", "Konversi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSupplier.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblSupplier.setSurrendersFocusOnKeystroke(true);
        tblSupplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblSupplierKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblSupplierKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblSupplierKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(tblSupplier);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 780, 160));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBasePrice.setFont(new java.awt.Font("Dialog", 1, 12));
        lblBasePrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBasePrice.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblBasePrice.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblBasePricePropertyChange(evt);
            }
        });
        jPanel2.add(lblBasePrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 100, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Base Price :");
        jPanel2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        lblHargaRetur.setFont(new java.awt.Font("Dialog", 1, 12));
        lblHargaRetur.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHargaRetur.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lblHargaRetur.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblHargaReturPropertyChange(evt);
            }
        });
        jPanel2.add(lblHargaRetur, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, 110, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Harga Retur : ");
        jPanel2.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 90, 20));

        jLabel2.setBackground(new java.awt.Color(204, 255, 255));
        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setText("<html>\n &nbsp <b>F4  &nbsp &nbsp    : </b> Clear <br> \n &nbsp <b>F5 &nbsp &nbsp : </b>  Simpan Master Price <br>\n &nbsp <b>Insert : </b> Menambah Supplier\n</html>"); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setOpaque(true);
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 290, 50));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 780, 70));

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
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

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, 150, 60));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-818)/2, (screenSize.height-415)/2, 818, 415);
    }// </editor-fold>//GEN-END:initComponents

    private void txtItemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtItemFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtItemFocusLost

    private void txtItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtItemKeyReleased
        fn.lookup(evt, new Object[]{lblItemName, txtSatKecil}, "select kode_item, coalesce(nama_item,'') as nama_barang, " +
                "coalesce(unit,'') as satuan " +
                "from r_Item where active=true " +
                "and kode_item||coalesce(nama_item,'') ilike '%"+txtItem.getText()+"%' " +
                "order by 2",
                txtItem.getWidth()+lblItemName.getWidth(), 200);
//        if(evt.getKeyCode()==KeyEvent.VK_ENTER && !fn.isListVisible())
//            udfLoadSupplier();
}//GEN-LAST:event_txtItemKeyReleased

    private void lblItemNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblItemNamePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblItemNamePropertyChange

    private void txtSatKecilKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSatKecilKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtSatKecilKeyReleased

    private void txtSatKecilKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSatKecilKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_txtSatKecilKeyTyped

    private void lblBasePricePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblBasePricePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblBasePricePropertyChange

    private void lblHargaReturPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblHargaReturPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_lblHargaReturPropertyChange

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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void tblSupplierKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyReleased
        
    }//GEN-LAST:event_tblSupplierKeyReleased

    private void tblSupplierKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyTyped
        
    }//GEN-LAST:event_tblSupplierKeyTyped

    private void tblSupplierKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSupplierKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP && tblSupplier.getSelectedRow()==0){
           txtItem.requestFocus();
        }
    }//GEN-LAST:event_tblSupplierKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmSupplierPriceByItem().setVisible(true);
            }
        });
    }
    SimpleDateFormat dmyFmt_hhmm=new SimpleDateFormat("dd/MM/yyyy hh:mm");
    NumberFormat numFmt=new DecimalFormat("#,##0.00");
    NumberFormat nFmt=new DecimalFormat("#,##0");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {


            if(value instanceof Date ){
                value=fn.ddMMyy_format.format(value);
            if(value instanceof Timestamp ){
                value=dmyFmt_hhmm.format(value);
            }}else if(value instanceof Double ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=numFmt.format(value);
            }else if(value instanceof Integer ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=nFmt.format(value);

            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (hasFocus) {
                setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }


            setValue(value);
            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblBasePrice;
    private javax.swing.JLabel lblHargaRetur;
    private javax.swing.JLabel lblItemName;
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtItem;
    private javax.swing.JTextField txtSatKecil;
    // End of variables declaration//GEN-END:variables
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private void udfNew() {
        txtItem.setText(""); lblItemName.setText("");
        txtSatKecil.setText("");
        ((DefaultTableModel)tblSupplier.getModel()).setNumRows(0);
        lblBasePrice.setText("0"); lblHargaRetur.setText("0");
        txtItem.requestFocus();
    }

    private boolean udfCekBeforeSave(){
        boolean b=true;
        TableColumnModel col=tblSupplier.getColumnModel();
        if(txtItem.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Silakan isi ProductID terlebih dulu!");
            txtItem.requestFocus();
            return false;
        }
        if(tblSupplier.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Tidak ada supplier yang akan disimpan!");
            tblSupplier.requestFocus();
            return false;
        }
        double rkI, rkJ;
        for(int i=0; i<tblSupplier.getRowCount(); i++){
            rkI=fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Rk")));
            for(int j=0; j< tblSupplier.getRowCount(); j++){
                rkJ=fn.udfGetDouble(tblSupplier.getValueAt(j, col.getColumnIndex("Rk")));
                if(i!=j && rkJ==rkI ){
                    JOptionPane.showMessageDialog(this, "Ranking \""+rkJ+"\" Sama untuk Supplier '"+tblSupplier.getValueAt(j, 2).toString()+"' " +
                            "dengan supplier '"+tblSupplier.getValueAt(i, 2).toString()+"' ");
                    tblSupplier.requestFocusInWindow();
                    tblSupplier.changeSelection(j, col.getColumnIndex("Rk"), false, false);
                    return false;
                }
            }
        }

        return b;
    }
    private void udfSave() {
        try{
            if(!udfCekBeforeSave()) return;
            String sItem="", sIns="";
            TableColumnModel col=tblSupplier.getColumnModel();

            for(int i=0; i< tblSupplier.getRowCount(); i++){
                sItem+=(sItem.length()==0? "" : ",") +"'"+tblSupplier.getValueAt(i, 0).toString()+"'";
                sIns+=(sIns.length()==0? "" : " union all ") +
                        "select fn_r_save_item_supplier('"+txtItem.getText()+"'," +
                        "'"+tblSupplier.getValueAt(i, col.getColumnIndex("SupplierID")).toString()+"', " +
                        "'"+tblSupplier.getValueAt(i, col.getColumnIndex("Satuan2")).toString()+"', " +
                        fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Konversi")))+", " +
                        fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Price List1")))+"," +
                        fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("Disc1%")))+"," +
                        "0::double precision," +
                        fn.udfGetDouble(tblSupplier.getValueAt(i, col.getColumnIndex("PPN1%")))+"," +
                        fn.udfGetInt(tblSupplier.getValueAt(i, col.getColumnIndex("Rk")))+"," +
                        "0::double precision,0::double precision, '"+MainForm.sUserName+"') ";
            }
            String sDelSupp="delete from r_item_supplier where kode_supp not in("+sItem+") " +
                    "and kode_item='"+txtItem.getText()+"'; ";

            //System.out.println(sIns+sDelSupp);
            conn.setAutoCommit(false);
            int iUpd=conn.createStatement().executeUpdate(sDelSupp);

            ResultSet rs=conn.createStatement().executeQuery(sIns);
            rs.next();
            rs.close();

//            rs=conn.createStatement().executeQuery("select fn_phar_pr_update_supplier_pertama()");
//            rs.next();
//            rs.close();

            conn.setAutoCommit(true);

            JOptionPane.showMessageDialog(this, "Simpan Master Price Sukses!");

        }catch(SQLException se){
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmSupplierPriceByItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
          if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor") &&
              tblSupplier.getSelectedColumn()!=tblSupplier.getColumnModel().getColumnIndex("Satuan2")) {

              char c = evt.getKeyChar();
              if (!((c >= '0' && c <= '9')) &&
                    (c != KeyEvent.VK_BACK_SPACE) &&
                    (c != KeyEvent.VK_DELETE) &&
                    (c != KeyEvent.VK_ENTER) &&
                    (c != '-') &&
                    (c != '.')) {
                    getToolkit().beep();
                    evt.consume();
                    return;
              }
           }

        }
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
                    if(!(evt.getSource() instanceof JTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }else if(evt.getSource().equals(tblSupplier) && tblSupplier.getSelectedRow()==0){
                        //txtItem.requestFocus();
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
                case KeyEvent.VK_F4:{
                    udfNew();
                    break;
                }
                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblSupplier) && tblSupplier.getSelectedRow()>=0){
                        int iRow[]= tblSupplier.getSelectedRows();
                        int rowPalingAtas=iRow[0];

//                        if(JOptionPane.showConfirmDialog(FrmPO.this,
//                                "Item '"+tblPR.getValueAt(iRow, 3).toString()+"' dihapus dari PO?", "Confirm", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
//                            return;

                        for (int a=0; a<iRow.length; a++){
                            ((DefaultTableModel)tblSupplier.getModel()).removeRow(tblSupplier.getSelectedRow());
                        }

                        if(tblSupplier.getRowCount()>0 && rowPalingAtas<tblSupplier.getRowCount()){
                            //if(tblPR.getSelectedRow()>0)
                                tblSupplier.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }
                        else{
                            if(tblSupplier.getRowCount()>0)
                                tblSupplier.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
                            else
                                txtItem.requestFocus();

                        }
                        if(tblSupplier.getSelectedRow()>=0)
                            tblSupplier.changeSelection(tblSupplier.getSelectedRow(), 0, false, false);
                    }
                    break;

                }
                case KeyEvent.VK_INSERT:{
                    DlgLookup d1=new DlgLookup(JOptionPane.getFrameForComponent(FrmSupplierPriceByItem.this), true);
                    String sSupplier="";
                    for(int i=0; i< tblSupplier.getRowCount(); i++){
                        sSupplier+=(sSupplier.length()==0? "" : ",") +"'"+tblSupplier.getValueAt(i, 0).toString()+"'";
                    }

                    String s="select * from (" +
                            "select kode_supp as kode_supplier, coalesce(nama_supp,'') as nama_supplier from " +
                            "r_supplier sp "+
                            (sSupplier.length()>0? "where kode_supp not in("+sSupplier+")":"")+" order by 2) x ";

                    //System.out.println(s);
//                    ((DefaultTableModel)tblSupplier.getModel()).setNumRows(tblSupplier.getRowCount()+1);
//                    tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount()-1, tblSupplier.getRowCount()-1);
                    d1.setTitle("Lookup Supplier");
                    d1.udfLoad(conn, s, "(kode_supplier||nama_supplier)", null);

                    d1.setVisible(true);

                    //System.out.println("Kode yang dipilih" +d1.getKode());
                    if(d1.getKode().length()>0){
                        TableColumnModel col=d1.getTable().getColumnModel();
                        JTable tbl=d1.getTable();
                        int iRow = tbl.getSelectedRow();

                        ((DefaultTableModel)tblSupplier.getModel()).addRow(new Object[]{
                            tbl.getValueAt(iRow, col.getColumnIndex("kode_supplier")).toString(),
                            tbl.getValueAt(iRow, col.getColumnIndex("nama_supplier")).toString(),
                            tblSupplier.getRowCount()+1,
                            0,
                            "",
                            0,
                            0,
                            10,
                            "",
                            1
                        });

                        tblSupplier.setRowSelectionInterval(tblSupplier.getRowCount()-1, tblSupplier.getRowCount()-1);
                        tblSupplier.requestFocusInWindow();
                        tblSupplier.changeSelection(tblSupplier.getRowCount()-1, tblSupplier.getColumnModel().getColumnIndex("Rk"), false, false);
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

     public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= new JTextField() {
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

        int col, row;

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;

            if(vColIndex!=tblSupplier.getColumnModel().getColumnIndex("Satuan2")){
               text.addKeyListener(kListener);
            }else{
               text.removeKeyListener(kListener);
            }

            text.setName("textEditor");

           //col=vColIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           //text.addKeyListener(kListener);
           text.setFont(table.getFont());
           //text.setName("textEditor");


            //text.setText(value==null? "": value.toString());
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
//                if(col==tblSupplier.getColumnModel().getColumnIndex("Satuan2")){
//                    retVal = ((JTextField)text).getText();
//                }else
                if(col==tblSupplier.getColumnModel().getColumnIndex("Rk")){
                    retVal = fn.udfGetInt(((JTextField)text).getText());
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
}
