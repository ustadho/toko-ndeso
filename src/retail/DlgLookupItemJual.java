/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgLookupItem2.java
 *
 * Created on 28 Des 10, 6:36:14
 */

package retail;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class DlgLookupItemJual extends javax.swing.JDialog {
    private Connection conn;
    GeneralFunction fn=new GeneralFunction();
    private String sCustType="R";
    private int columnIndex;
    private JTable srcTable;
    private KeyEvent keyEvent;
    private Object objForm;
    private String sKodeBarang="";

    /** Creates new form DlgLookupItem2 */
    public DlgLookupItemJual(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tblItem.getTableHeader().setFont(tblItem.getFont());
        tblStok.getTableHeader().setFont(tblItem.getFont());
        tblStok.setFont(tblItem.getFont());

        txtCari.addFocusListener(txtFocusListener);
        tblItem.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"selectNextColumnCell");
        tblItem.getColumn("Kode").setPreferredWidth(120);
        tblItem.getColumn("Nama Barang").setPreferredWidth(250);
        tblItem.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(tblItem.getSelectedRow()>=0){
                    udfLoadStok();
                    udfLoadHarga();
                }
            }
        });
    }

    public void setCustType(String s){
        this.sCustType=s;
    }

    public void setObjForm(Object obj){
        objForm=obj;
    }

    private void setSelected(){
        int iRow=tblItem.getSelectedRow();
        if(iRow>=0)
            sKodeBarang=tblItem.getValueAt(iRow, 0).toString();
        if(objForm !=null)
            this.dispose();
    }

    private void udfLoadStok(){
        String sItem=tblItem.getValueAt(tblItem.getSelectedRow(), 0).toString();
        try {
            ((DefaultTableModel)tblStok.getModel()).setNumRows(0);

            ResultSet rs = conn.createStatement().executeQuery(
                    "select s.kode_gudang, coalesce(g.nama_gudang,'') as gudang, sum(coalesce(saldo,0)) as saldo " +
                    "from r_item_stok s " +
                    "left join r_gudang g on g.kode_gudang=s.kode_gudang " +
                    "where kode_item='"+sItem+"' " +
                    "group by s.kode_gudang, coalesce(g.nama_gudang,'') order by s.kode_gudang");
            while(rs.next())
                ((DefaultTableModel)tblStok.getModel()).addRow(new Object[]{
                    rs.getString("kode_gudang"),
                    rs.getString("gudang"),
                    rs.getDouble("saldo")
                });
            rs.close();
            if(tblStok.getRowCount()>0)
                tblStok.setRowSelectionInterval(0, 0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void udfLoadHarga(){
        int iRow=tblItem.getSelectedRow();
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select coalesce(unit,'') as unit1," +
                    "coalesce(unit2,'') as unit2," +
                    "coalesce(konv2,1) as konv2," +
                    "coalesce(unit3,'') as unit3," +
                    "coalesce(konv3,1) as konv3," +
                    "coalesce(harga_r_1,0) as harga_r_1," +
                    "coalesce(harga_r_2,0) as harga_r_2, " +
                    "coalesce(harga_r_3,0) as harga_r_3," +
                    "coalesce(harga_g_1,0) as harga_g_1," +
                    "coalesce(harga_g_2,0) as harga_g_2," +
                    "coalesce(harga_g_3,0) as harga_g_3 " +
                    "from r_item i " +
                    "left join r_item_harga_jual h on h.kode_item=i.kode_item " +
                    "where i.kode_item='"+tblItem.getValueAt(iRow, tblItem.getColumnModel().getColumnIndex("Kode")).toString()+"' ");

            if(rs.next()){
                lblSat1G.setText(rs.getString("unit1"));    lblSat1E.setText(rs.getString("unit1"));
                lblSat2G.setText(rs.getString("unit2"));    lblSat2E.setText(rs.getString("unit2"));
                lblSat3G.setText(rs.getString("unit3"));    lblSat3E.setText(rs.getString("unit3"));
                lblKonv2G.setText(rs.getString("konv2")+ " " +rs.getString("unit1"));   lblKonv2E.setText(rs.getString("konv2")+" " +rs.getString("unit1"));
                lblKonv3G.setText(rs.getString("konv3")+ " " +rs.getString("unit1"));   lblKonv3E.setText(rs.getString("konv3")+" " +rs.getString("unit1"));
                txtHarga1G.setText(fn.intFmt.format(rs.getDouble("harga_g_1")));
                txtHarga2G.setText(fn.intFmt.format(rs.getDouble("harga_g_2")));
                txtHarga3G.setText(fn.intFmt.format(rs.getDouble("harga_g_3")));
                txtHarga1E.setText(fn.intFmt.format(rs.getDouble("harga_r_1")));
                txtHarga2E.setText(fn.intFmt.format(rs.getDouble("harga_r_2")));
                txtHarga3E.setText(fn.intFmt.format(rs.getDouble("harga_r_3")));

                txtHarga2G.setEnabled(rs.getString("unit2").length()>0);   txtHarga3G.setEnabled(rs.getString("unit3").length()>0);
                txtHarga2E.setEnabled(rs.getString("unit2").length()>0);   txtHarga3E.setEnabled(rs.getString("unit3").length()>0);

            }else{
                udfClear();
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfClear() {
        lblSat1G.setText("");    lblSat1E.setText("");
        lblSat2G.setText("");    lblSat2E.setText("");
        lblSat3G.setText("");    lblSat3E.setText("");
        lblKonv2G.setText("");   lblKonv2G.setText("");
        lblKonv3G.setText("");   lblKonv3G.setText("");
        txtHarga1G.setText("");
        txtHarga2G.setText("");
        txtHarga3G.setText("");
        txtHarga1E.setText("");
        txtHarga2E.setText("");
        txtHarga3E.setText("");

    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfFilter(){
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery("select kode_item, coalesce(nama_item,'') as nama_item, " +
                    "coalesce(kategori,'') as kategori from r_item where active<>false and " +
                    "kode_item||coalesce(nama_item,'')||coalesce(kategori,'')||coalesce(barcode,'') ilike '%"+txtCari.getText()+"%' " +
                    "order by nama_item");
            while(rs.next()){
                ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("kategori")
                });
            }
            rs.close();
            if(tblItem.getRowCount()>0)
                tblItem.setRowSelectionInterval(0, 0);

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }catch(SQLException se){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        txtCari = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new org.jdesktop.swingx.JXTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblKonv1E = new javax.swing.JLabel();
        lblSat1E = new javax.swing.JLabel();
        lblKonv2E = new javax.swing.JLabel();
        lblSat2E = new javax.swing.JLabel();
        lblSat3E = new javax.swing.JLabel();
        lblKonv3E = new javax.swing.JLabel();
        txtHarga3E = new javax.swing.JTextField();
        txtHarga1E = new javax.swing.JTextField();
        txtHarga2E = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblKonv1G = new javax.swing.JLabel();
        lblSat1G = new javax.swing.JLabel();
        lblKonv2G = new javax.swing.JLabel();
        lblSat2G = new javax.swing.JLabel();
        lblSat3G = new javax.swing.JLabel();
        lblKonv3G = new javax.swing.JLabel();
        txtHarga3G = new javax.swing.JTextField();
        txtHarga1G = new javax.swing.JTextField();
        txtHarga2G = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStok = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lookup Item by Harga Jual");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel1.setText("Pencarian");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 20));

        txtCari.setFont(new java.awt.Font("Tahoma", 0, 14));
        txtCari.setName("txtCari"); // NOI18N
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });
        getContentPane().add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 430, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Kategori"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        tblItem.setName("tblItem"); // NOI18N
        tblItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblItemMouseClicked(evt);
            }
        });
        tblItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblItemKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblItem);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 540, 440));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Harga Grosir");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jSeparator1.setName("jSeparator1"); // NOI18N
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 170, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Harga Eceran");
        jLabel9.setName("jLabel9"); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 90, 20));

        jSeparator3.setName("jSeparator3"); // NOI18N
        jPanel1.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 170, -1));

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setBackground(new java.awt.Color(153, 153, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Harga");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setOpaque(true);
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 90, 20));

        jLabel11.setBackground(new java.awt.Color(153, 153, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Konv");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setOpaque(true);
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 80, 20));

        jLabel12.setBackground(new java.awt.Color(153, 153, 255));
        jLabel12.setText("Satuan");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setOpaque(true);
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        lblKonv1E.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv1E.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv1E.setText("1");
        lblKonv1E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv1E.setName("lblKonv1E"); // NOI18N
        lblKonv1E.setOpaque(true);
        jPanel3.add(lblKonv1E, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 80, 20));

        lblSat1E.setBackground(new java.awt.Color(153, 255, 255));
        lblSat1E.setText("PCS");
        lblSat1E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat1E.setName("lblSat1E"); // NOI18N
        lblSat1E.setOpaque(true);
        jPanel3.add(lblSat1E, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 80, 20));

        lblKonv2E.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv2E.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv2E.setText("10");
        lblKonv2E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv2E.setName("lblKonv2E"); // NOI18N
        lblKonv2E.setOpaque(true);
        jPanel3.add(lblKonv2E, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 80, 20));

        lblSat2E.setBackground(new java.awt.Color(153, 255, 255));
        lblSat2E.setText("BOX");
        lblSat2E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat2E.setName("lblSat2E"); // NOI18N
        lblSat2E.setOpaque(true);
        jPanel3.add(lblSat2E, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 80, 20));

        lblSat3E.setBackground(new java.awt.Color(153, 255, 255));
        lblSat3E.setText("KARTON");
        lblSat3E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat3E.setName("lblSat3E"); // NOI18N
        lblSat3E.setOpaque(true);
        jPanel3.add(lblSat3E, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 80, 20));

        lblKonv3E.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv3E.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv3E.setText("100");
        lblKonv3E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv3E.setName("lblKonv3E"); // NOI18N
        lblKonv3E.setOpaque(true);
        jPanel3.add(lblKonv3E, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 80, 20));

        txtHarga3E.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga3E.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga3E.setText("0");
        txtHarga3E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga3E.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga3E.setEnabled(false);
        txtHarga3E.setName("txtHarga3E"); // NOI18N
        txtHarga3E.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga3EKeyTyped(evt);
            }
        });
        jPanel3.add(txtHarga3E, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 90, 20));

        txtHarga1E.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga1E.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga1E.setText("0");
        txtHarga1E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga1E.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga1E.setEnabled(false);
        txtHarga1E.setName("txtHarga1E"); // NOI18N
        txtHarga1E.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga1EKeyTyped(evt);
            }
        });
        jPanel3.add(txtHarga1E, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 90, 20));

        txtHarga2E.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga2E.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga2E.setText("0");
        txtHarga2E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga2E.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga2E.setEnabled(false);
        txtHarga2E.setName("txtHarga2E"); // NOI18N
        txtHarga2E.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga2EKeyTyped(evt);
            }
        });
        jPanel3.add(txtHarga2E, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, 20));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 250, 90));

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(153, 153, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Harga");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setOpaque(true);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 90, 20));

        jLabel4.setBackground(new java.awt.Color(153, 153, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Konv");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setOpaque(true);
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 80, 20));

        jLabel5.setBackground(new java.awt.Color(153, 153, 255));
        jLabel5.setText("Satuan");
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setOpaque(true);
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        lblKonv1G.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv1G.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv1G.setText("1");
        lblKonv1G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv1G.setName("lblKonv1G"); // NOI18N
        lblKonv1G.setOpaque(true);
        jPanel4.add(lblKonv1G, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 80, 20));

        lblSat1G.setBackground(new java.awt.Color(153, 255, 255));
        lblSat1G.setText("PCS");
        lblSat1G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat1G.setName("lblSat1G"); // NOI18N
        lblSat1G.setOpaque(true);
        jPanel4.add(lblSat1G, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 80, 20));

        lblKonv2G.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv2G.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv2G.setText("10");
        lblKonv2G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv2G.setName("lblKonv2G"); // NOI18N
        lblKonv2G.setOpaque(true);
        jPanel4.add(lblKonv2G, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 80, 20));

        lblSat2G.setBackground(new java.awt.Color(153, 255, 255));
        lblSat2G.setText("BOX");
        lblSat2G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat2G.setName("lblSat2G"); // NOI18N
        lblSat2G.setOpaque(true);
        jPanel4.add(lblSat2G, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 80, 20));

        lblSat3G.setBackground(new java.awt.Color(153, 255, 255));
        lblSat3G.setText("KARTON");
        lblSat3G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat3G.setName("lblSat3G"); // NOI18N
        lblSat3G.setOpaque(true);
        jPanel4.add(lblSat3G, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 80, 20));

        lblKonv3G.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv3G.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv3G.setText("100");
        lblKonv3G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv3G.setName("lblKonv3G"); // NOI18N
        lblKonv3G.setOpaque(true);
        jPanel4.add(lblKonv3G, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 80, 20));

        txtHarga3G.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga3G.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga3G.setText("0");
        txtHarga3G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga3G.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga3G.setEnabled(false);
        txtHarga3G.setName("txtHarga3G"); // NOI18N
        txtHarga3G.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga3GKeyTyped(evt);
            }
        });
        jPanel4.add(txtHarga3G, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 90, 20));

        txtHarga1G.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga1G.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga1G.setText("0");
        txtHarga1G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga1G.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga1G.setEnabled(false);
        txtHarga1G.setName("txtHarga1G"); // NOI18N
        txtHarga1G.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga1GKeyTyped(evt);
            }
        });
        jPanel4.add(txtHarga1G, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 90, 20));

        txtHarga2G.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga2G.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga2G.setText("0");
        txtHarga2G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHarga2G.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtHarga2G.setEnabled(false);
        txtHarga2G.setName("txtHarga2G"); // NOI18N
        txtHarga2G.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga2GKeyTyped(evt);
            }
        });
        jPanel4.add(txtHarga2G, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 220, 270, 260));

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator2.setName("jSeparator2"); // NOI18N
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 160, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Informasi Stok");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblStok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Gudang", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblStok.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblStok.setName("tblStok"); // NOI18N
        tblStok.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblStok);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 130));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, 270, 170));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-855)/2, (screenSize.height-535)/2, 855, 535);
    }// </editor-fold>//GEN-END:initComponents

    private void txtHarga3EKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga3EKeyTyped
        
}//GEN-LAST:event_txtHarga3EKeyTyped

    private void txtHarga1EKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga1EKeyTyped
        
}//GEN-LAST:event_txtHarga1EKeyTyped

    private void txtHarga2EKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga2EKeyTyped
        
}//GEN-LAST:event_txtHarga2EKeyTyped

    private void txtHarga3GKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga3GKeyTyped
        
}//GEN-LAST:event_txtHarga3GKeyTyped

    private void txtHarga1GKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga1GKeyTyped
        
}//GEN-LAST:event_txtHarga1GKeyTyped

    private void txtHarga2GKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga2GKeyTyped
        
}//GEN-LAST:event_txtHarga2GKeyTyped

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        udfFilter();
    }//GEN-LAST:event_txtCariKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        txtCari.setText("");
        udfFilter();
    }//GEN-LAST:event_formWindowOpened

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_DOWN:{
                tblItem.requestFocus();
                break;
            }
            case KeyEvent.VK_ENTER:{
                setSelected();
                break;
            }
        }
    }//GEN-LAST:event_txtCariKeyPressed

    private void tblItemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblItemKeyPressed
        switch(evt.getKeyCode()){
            case KeyEvent.VK_UP:{
                if(tblItem.getSelectedRow()==0)
                    txtCari.requestFocus();
                break;
            }
            case KeyEvent.VK_ENTER:{
                setSelected();
                break;
            }
        }
    }//GEN-LAST:event_tblItemKeyPressed

    private void tblItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblItemMouseClicked
        if(evt.getClickCount()==2)
            setSelected();
            
    }//GEN-LAST:event_tblItemMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookupItemJual dialog = new DlgLookupItemJual(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblKonv1E;
    private javax.swing.JLabel lblKonv1G;
    private javax.swing.JLabel lblKonv2E;
    private javax.swing.JLabel lblKonv2G;
    private javax.swing.JLabel lblKonv3E;
    private javax.swing.JLabel lblKonv3G;
    private javax.swing.JLabel lblSat1E;
    private javax.swing.JLabel lblSat1G;
    private javax.swing.JLabel lblSat2E;
    private javax.swing.JLabel lblSat2G;
    private javax.swing.JLabel lblSat3E;
    private javax.swing.JLabel lblSat3G;
    private org.jdesktop.swingx.JXTable tblItem;
    private javax.swing.JTable tblStok;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHarga1E;
    private javax.swing.JTextField txtHarga1G;
    private javax.swing.JTextField txtHarga2E;
    private javax.swing.JTextField txtHarga2G;
    private javax.swing.JTextField txtHarga3E;
    private javax.swing.JTextField txtHarga3G;
    // End of variables declaration//GEN-END:variables

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


           }
        }


    } ;

    public void setKeyEvent(KeyEvent evt) {
        this.keyEvent=evt;
    }

    public void setSrcTable(JTable table, int columnIndex) {
        this.srcTable=table;
        this.columnIndex=columnIndex;
    }

    public String getKodeBarang() {
        return sKodeBarang;
    }

    public void clearText() {
        txtCari.setText("");
    }

}
