/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSupplierMaster.java
 *
 * Created on Mar 14, 2009, 11:35:18 AM
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.jdesktop.swingx.JXTable;
import retail.main.GeneralFunction;
import retail.main.Termin;

/**
 *
 * @author ustadho
 */
public class FrmSupplierMaster extends javax.swing.JInternalFrame {
    private Connection conn;
    private String sKode;
    private boolean isNew;
    private JXTable srcTable;
    private DefaultTableModel srcModel;
    private String sOldKontak ="";
    private Component aThis;
    private MyKeyListener kListener=new MyKeyListener();
    private Termin[] termin;


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
    private Object objForm;
    /** Creates new form FrmSupplierMaster */
    public FrmSupplierMaster() {
        initComponents();
        
        tblKontak.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if(tblKontak.getSelectedRow()>=0)
                    sOldKontak=tblKontak.getValueAt(tblKontak.getSelectedRow(), 0).toString();
                else
                    sOldKontak="";
            }
        });

        tblKontak.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                int iRow=tblKontak.getSelectedRow();
                if(conn==null || iRow<0) return;
                if(!isNew && e.getType()==TableModelEvent.UPDATE){
                    TableColumnModel col=tblKontak.getColumnModel();
                    try{
                        String sQry="select fn_r_update_supplier_kontak(" +
                                "'"+sOldKontak+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("Nama")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("Jabatan")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("Telepon")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("HP")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("E-Mail")).toString()+"', " +
                                "'"+txtKode.getText()+"')";

                        ResultSet rs=conn.createStatement().executeQuery(sQry);
                        rs.close();
                        sOldKontak=tblKontak.getValueAt(iRow, 0).toString();

                    }catch(SQLException se){
                        JOptionPane.showMessageDialog(aThis, se.getMessage());
                    }
                }
            }
        });

        tblKontak.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "selectNextColumnCell");

        tblKontak.setRowHeight(20);
        aThis=this;
    }

    public void saldoAwal(){
        try{
            ResultSet rs=conn.createStatement().executeQuery("select sum(coalesce(nilai_faktur,0)) as total " +
                    "from r_supp_saldo_awal " +
                    "where kode_supp='"+txtKode.getText()+"'");
            if(rs.next())
                btnSaldoAwal.setText(fn.dFmt.format(rs.getDouble(1)));

            rs.close();

        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    void setConn(Connection con){
        this.conn=con;
    }

    void setIsNew(boolean b) {
        isNew=b;
    }

    void setKodeSupp(String s){
        txtKode.setText(s);
        sKode=s;
    }

    void setSrcModel(DefaultTableModel myModel) {
        srcModel=myModel;
    }

    void setSrcTable(JXTable masterTable) {
        srcTable=masterTable;
    }

    private void udfLoadSupplier(){
        String sQry="select kode_supp, nama_supp, coalesce(alamat_1, '') as alamat, " +
                "coalesce(s.kota,'') as kode_kota, coalesce(k.nama_kota,'') as nama_kota, " +
                "coalesce(telepon,'') as telepon, coalesce(s.fax, '') as fax, coalesce(kontak,'') as kontak , " +
                "coalesce(email,'') as email, coalesce(web,'') as web, coalesce(active, true) as active," +
                "coalesce(termin,'') as termin, coalesce(ket_default, '') as ket_default, " +
                "coalesce(catatan,'') as catatan,  coalesce(s.npwp,'') as npwp, coalesce(s.no_pkp,'') as no_pkp," +
                "coalesce(s.pajak_1,0) as pajak_1, coalesce(s.pajak_2,0) as pajak_2, coalesce(s.tipe_pajak,0) as tipe_pajak " +
                "from r_supplier s  " +
                "left join m_kota k on k.kode_kota=s.kota " +
                "where kode_supp='"+sKode+"'";
        try{
            ResultSet rs=conn.createStatement().executeQuery(sQry);

            if(rs.next()){
                txtKode.setText(rs.getString("kode_supp"));
                txtNama.setText(rs.getString("nama_supp"));
                txtAlamat.setText(rs.getString("alamat"));
                txtKota.setText(rs.getString("kode_kota"));
                lblKota.setText(rs.getString("nama_kota"));
                txtTelepon.setText(rs.getString("telepon"));
                txtFax.setText(rs.getString("fax"));
                txtKontak.setText(rs.getString("kontak"));
                txtEmail.setText(rs.getString("email"));
                txtWeb.setText(rs.getString("web"));
                chkActive.setSelected(rs.getBoolean("active"));
                cmbTermin.setSelectedItem(rs.getString("termin"));
                txtKetDefault.setText(rs.getString("ket_default"));
                txtCatatan.setText(rs.getString("catatan"));
                cmbPajak1.setSelectedIndex(rs.getInt("pajak_1"));
                cmbPajak2.setSelectedIndex(rs.getInt("pajak_2"));
                txtNpwpSupp.setText(rs.getString("npwp"));
                txtNoPKP.setText(rs.getString("no_pkp"));
                cmbTipePajak.setSelectedIndex(rs.getInt("tipe_pajak"));
                isNew=false;

                rs.close();
                rs=conn.createStatement().executeQuery("select nama, coalesce(title,'') as jabatan, coalesce(telpon,'') as telepon, " +
                        "coalesce(hp,'') as hp, coalesce(email,'') as email from r_supp_kontak " +
                        "where kode_supp='"+txtKode.getText()+"' " +
                        "order by nama");
                while(rs.next()){
                    ((DefaultTableModel)tblKontak.getModel()).addRow(new Object[]{
                        rs.getString("nama"),
                        rs.getString("jabatan"),
                        rs.getString("telepon"),
                        rs.getString("hp"),
                        rs.getString("email"),
                    });
                }
                saldoAwal();
                rs.close();
            }
        }catch(SQLException se){
            System.out.println(se.getMessage());
        }
        MyTableCellEditor cEditor=new MyTableCellEditor();
        tblKontak.getColumn("Nama").setCellEditor(cEditor);
        tblKontak.getColumn("Jabatan").setCellEditor(cEditor);
        tblKontak.getColumn("Telepon").setCellEditor(cEditor);
        tblKontak.getColumn("HP").setCellEditor(cEditor);
        tblKontak.getColumn("E-Mail").setCellEditor(cEditor);
        
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        shapePainter1 = new org.jdesktop.swingx.painter.ShapePainter();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtKota = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtWeb = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtKontak = new javax.swing.JTextField();
        lblKota = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTelepon = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        cmbPajak1 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        txtNpwpSupp = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNoPKP = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        cmbPajak2 = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        cmbTipePajak = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        lblDiscTermin = new javax.swing.JLabel();
        cmbTermin = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblHariDisc = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblJatuhTempo = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        btnSaldoAwal = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        txtKetDefault = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        btnAddKontak = new javax.swing.JButton();
        btnDeleteKontak = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtCatatan = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        chkActive = new javax.swing.JCheckBox();

        setClosable(true);
        setTitle("Supplier");
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

        jLabel1.setText("Kode Supplier");

        txtKode.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKode.setEnabled(false);

        jLabel2.setText("Nama Supplier");

        jLabel3.setText("Alamat");

        jLabel4.setText("Kota");

        txtKota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKotaKeyReleased(evt);
            }
        });

        jLabel5.setText("Alamat Email");

        jLabel6.setText("Alamat WEB");

        jLabel7.setText("Kontak");

        lblKota.setBackground(new java.awt.Color(255, 255, 255));
        lblKota.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKota.setOpaque(true);

        jLabel8.setText("Telepon");

        jLabel9.setText("Fax");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAlamat, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKota, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblKota, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKontak, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWeb, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFax, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtKode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtKota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKota, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtKontak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtWeb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Identitas", jPanel1);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Pajak"));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbPajak1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Pajak 10% Standart", "Pajak 10% Sederhana", "PPnBM 15%" }));
        jPanel5.add(cmbPajak1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 250, -1));

        jLabel17.setText("Pajak 1");
        jPanel5.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 20));
        jPanel5.add(txtNpwpSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 320, -1));

        jLabel18.setText("No. PKP");
        jPanel5.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 110, 20));
        jPanel5.add(txtNoPKP, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 105, 320, -1));

        jLabel19.setText("NPWP Supplier");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 80, 20));

        jLabel29.setText("Pajak 2");
        jPanel5.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 55, 80, 20));

        cmbPajak2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Pajak 10% Standart", "Pajak 10% Sederhana", "PPnBM 15%" }));
        jPanel5.add(cmbPajak2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 55, 250, -1));

        jLabel30.setText("Tipe Pajak");
        jPanel5.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 80, 20));

        cmbTipePajak.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Impor BKP", "Impor BKP Tidak Berwujud", "JKP Luar Pabean", "Perolehan Dalam Negeri", "Pajak Masukan yang tidak dapat dikreditkan", "Pembayaran dari Bapeksta Keuangan" }));
        jPanel5.add(cmbTipePajak, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 250, -1));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 470, 160));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Termin"));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDiscTermin.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDiscTermin.setText("0");
        jPanel6.add(lblDiscTermin, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 40, 20));

        cmbTermin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTermin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTerminItemStateChanged(evt);
            }
        });
        jPanel6.add(cmbTermin, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 250, -1));

        jLabel21.setText(")");
        jPanel6.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 50, 10, 20));

        jLabel22.setText("% Disk");
        jPanel6.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 50, 20));

        lblHariDisc.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblHariDisc.setText("0");
        jPanel6.add(lblHariDisc, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 40, 20));

        jLabel23.setText("Hari Disk");
        jPanel6.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 60, 20));

        lblJatuhTempo.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblJatuhTempo.setText("0");
        jPanel6.add(lblJatuhTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, 40, 20));

        jLabel24.setText("Jatuh Tempo");
        jPanel6.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 80, 20));

        jLabel25.setText("(");
        jPanel6.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 10, 20));

        jLabel26.setText("Termin");
        jPanel6.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 20));

        btnSaldoAwal.setFont(new java.awt.Font("Tahoma", 1, 11));
        btnSaldoAwal.setText("0");
        btnSaldoAwal.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSaldoAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaldoAwalActionPerformed(evt);
            }
        });
        jPanel6.add(btnSaldoAwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 250, 22));

        jLabel27.setText("Default Keterangan :");
        jPanel6.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 110, 20));
        jPanel6.add(txtKetDefault, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 105, 320, -1));

        jLabel28.setText("Saldo Awal :");
        jPanel6.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 80, 20));

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 470, 140));

        jTabbedPane1.addTab("Termin Pembayaran dll.", jPanel2);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Jabatan", "Telepon", "HP", "E-Mail"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblKontak.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblKontak);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 471, 270));

        btnAddKontak.setText("Add");
        btnAddKontak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddKontakActionPerformed(evt);
            }
        });
        jPanel3.add(btnAddKontak, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 80, -1));

        btnDeleteKontak.setText("Delete");
        btnDeleteKontak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteKontakActionPerformed(evt);
            }
        });
        jPanel3.add(btnDeleteKontak, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 280, 90, -1));

        jTabbedPane1.addTab("Kontak", jPanel3);

        txtCatatan.setColumns(20);
        txtCatatan.setRows(5);
        jScrollPane1.setViewportView(txtCatatan);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Catatan", jPanel4);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, 338));

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(433, 355, 73, -1));

        btnSave.setMnemonic('S');
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(353, 355, 74, -1));

        chkActive.setSelected(true);
        chkActive.setText("Aktif");
        getContentPane().add(chkActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 73, -1));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-526)/2, (screenSize.height-418)/2, 526, 418);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();

    }//GEN-LAST:event_btnSaveActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnAddKontakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddKontakActionPerformed
        ((DefaultTableModel)tblKontak.getModel()).addRow(new Object[]{
            "", "", "", "", ""
        });
        tblKontak.setRowSelectionInterval(0, 0);
        tblKontak.changeSelection(tblKontak.getRowCount()-1, 0, false, false);
    }//GEN-LAST:event_btnAddKontakActionPerformed

    private void btnDeleteKontakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteKontakActionPerformed
        try {
            int iRow = tblKontak.getSelectedRow();
            if (iRow < 0) {
                return;
            }
            int i = conn.createStatement().executeUpdate("delete from r_supp_kontak where kode_supp='" + txtKode.getText() + "' and " +
                    "nama='" + tblKontak.getValueAt(iRow, 0).toString() + "'; ");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }//GEN-LAST:event_btnDeleteKontakActionPerformed

    private void cmbTerminItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTerminItemStateChanged
        if(cmbTermin.getSelectedIndex()<0) return;
        try{
            ResultSet rs=conn.createStatement().executeQuery("select * from m_termin where kode='"+cmbTermin.getSelectedItem().toString()+"'");
            if(rs.next()){
                lblHariDisc.setText(rs.getString("hari_diskon"));
                lblDiscTermin.setText(rs.getString("diskon"));
                lblJatuhTempo.setText(rs.getString("jatuh_tempo"));
            }else{
                lblHariDisc.setText("0");
                lblDiscTermin.setText("0");
                lblJatuhTempo.setText("0");
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        
    }//GEN-LAST:event_cmbTerminItemStateChanged

    private void btnSaldoAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaldoAwalActionPerformed
        DlgSaldoAwSupplier d1=new DlgSaldoAwSupplier(JOptionPane.getFrameForComponent(this), true);
        d1.setConn(conn);
        d1.setKodeSupplier(txtKode.getText());
        d1.setTitle("Saldo awal supplier : "+txtNama.getText()+" ("+txtKode.getText()+")");
        d1.setVisible(true);
    }//GEN-LAST:event_btnSaldoAwalActionPerformed

    private void txtKotaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKotaKeyReleased
        fn.lookup(evt, new Object[]{lblKota},
                "select kode_kota as kode , coalesce(nama_kota,'') as kota from m_kota where kode_kota||coalesce(nama_kota,'') ilike '%"+txtKota.getText()+"%' order by 2",
                txtKota.getWidth()+lblKota.getWidth()+18, 120);
    }//GEN-LAST:event_txtKotaKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddKontak;
    private javax.swing.JButton btnDeleteKontak;
    private javax.swing.JButton btnSaldoAwal;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JComboBox cmbPajak1;
    private javax.swing.JComboBox cmbPajak2;
    private javax.swing.JComboBox cmbTermin;
    private javax.swing.JComboBox cmbTipePajak;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblDiscTermin;
    private javax.swing.JLabel lblHariDisc;
    private javax.swing.JLabel lblJatuhTempo;
    private javax.swing.JLabel lblKota;
    private org.jdesktop.swingx.painter.ShapePainter shapePainter1;
    private javax.swing.JTable tblKontak;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextArea txtCatatan;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFax;
    private javax.swing.JTextField txtKetDefault;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKontak;
    private javax.swing.JTextField txtKota;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNoPKP;
    private javax.swing.JTextField txtNpwpSupp;
    private javax.swing.JTextField txtTelepon;
    private javax.swing.JTextField txtWeb;
    // End of variables declaration//GEN-END:variables

    private void udfInitForm() {
        fn.setConn(conn);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel6, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel5, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
        try{
            cmbTermin.removeAllItems();
            ResultSet rs = conn.createStatement().executeQuery("select * from m_termin order by kode");
            
            while(rs.next()){
                cmbTermin.addItem(rs.getString("kode"));
                if(rs.getString("kode").equalsIgnoreCase("C.O.D"))
                    cmbTermin.setSelectedItem(rs.getString("kode"));

            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        udfLoadSupplier();
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtNama.requestFocus();
            }
      });
    }

    private void udfSave() {
        try {
            conn.setAutoCommit(false);
            ResultSet rs = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from r_supplier where kode_supp ='" + sKode + "'");

            if(!rs.next()) {
                isNew=true; rs.moveToInsertRow();
                ResultSet r=conn.createStatement().executeQuery("select fn_r_get_kode_supplier()");
                if(r.next())
                    txtKode.setText(r.getString(1));

                r.close();
            }

            rs.updateString("kode_supp", txtKode.getText());
            rs.updateString("nama_supp", txtNama.getText());
            rs.updateString("alamat_1", txtAlamat.getText());
            rs.updateString("kota", txtKota.getText());
            rs.updateString("telepon", txtTelepon.getText());
            rs.updateString("fax", txtFax.getText());
            rs.updateString("kontak", txtKontak.getText());
            rs.updateString("email", txtEmail.getText());
            rs.updateString("web", txtWeb.getText());
            rs.updateBoolean("active", chkActive.isSelected());
            rs.updateString("catatan", txtCatatan.getText());
            rs.updateString("ket_default", txtKetDefault.getText());
            rs.updateString("termin", cmbTermin.getSelectedItem().toString());
            rs.updateInt("pajak_1", cmbPajak1.getSelectedIndex());
            rs.updateInt("pajak_2", cmbTermin.getSelectedIndex());
            rs.updateString("npwp", txtNpwpSupp.getText());
            rs.updateString("no_pkp", txtNoPKP.getText());
            rs.updateInt("tipe_pajak", cmbTipePajak.getSelectedIndex());


            if(isNew) {
                rs.insertRow();
                String sKontak="";
                TableColumnModel col=tblKontak.getColumnModel();
                for(int iRow=0; iRow<tblKontak.getRowCount(); iRow++){
                    sKontak+="select fn_r_update_supplier_kontak(" +
                                "'"+sOldKontak+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("Nama")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("Jabatan")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("Telepon")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("HP")).toString()+"', " +
                                "'"+tblKontak.getValueAt(iRow, col.getColumnIndex("E-Mail")).toString()+"', " +
                                "'"+txtKode.getText()+"')";

                }
                if(sKontak.length()>0){
                    ResultSet rsKontak=conn.createStatement().executeQuery(sKontak);
                    rsKontak.close();
                }
                
                if(srcModel!=null){
                    srcModel.addRow(new Object[]{
                        txtKode.getText(),
                        txtNama.getText(),
                        txtAlamat.getText(),
                        txtTelepon.getText(),
                        txtKontak.getText()
                    });
                    srcTable.setRowSelectionInterval(srcModel.getRowCount()-1, srcModel.getRowCount()-1);
                }
                if(objForm!=null && objForm instanceof FrmGRBeli){
                   this.dispose();
                   ((FrmGRBeli)objForm).requestFocus();
                   ((FrmGRBeli)objForm).requestFocusInWindow();
                   ((FrmGRBeli)objForm).udfSetNewSupplier(txtKode.getText(), txtNama.getText());
                }
            }else {
                rs.updateRow();

                int iRow=srcTable.getSelectedRow();
                srcModel.setValueAt(txtKode.getText(), iRow, srcTable.getColumn("No. Supplier").getModelIndex());
                srcModel.setValueAt(txtNama.getText(), iRow, srcTable.getColumn("Nama Supplier").getModelIndex());
                srcModel.setValueAt(txtAlamat.getText(), iRow, srcTable.getColumn("Alamat").getModelIndex());
                srcModel.setValueAt(txtTelepon.getText(), iRow, srcTable.getColumn("Telepon").getModelIndex());
                srcModel.setValueAt(txtKontak.getText(), iRow, srcTable.getColumn("Kontak").getModelIndex());

            }
            conn.setAutoCommit(true);
            rs.close();
            this.dispose();

        } catch (SQLException ex) {
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Save gagal.\n"+ex.getMessage());
                Logger.getLogger(FrmSupplierMaster.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex1) {

                Logger.getLogger(FrmSupplierMaster.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

   public  void setObjForm(Object aThis) {
        this.objForm=aThis;
    }
    // End of variables declaration

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=ustTextField;

        int col, row;

        //private NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
           //text.setEditable(canEdit);
           col=vColIndex;
           row=rowIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.addKeyListener(kListener);
           text.setFont(table.getFont());
           text.setName("textEditor");

//           text.addKeyListener(new java.awt.event.KeyAdapter() {
//                   public void keyTyped(java.awt.event.KeyEvent evt) {
//                      if (col!=0) {
//                          char c = evt.getKeyChar();
//                          if (!((c >= '0' && c <= '9') || c=='.') &&
//                                (c != KeyEvent.VK_BACK_SPACE) &&
//                                (c != KeyEvent.VK_DELETE) &&
//                                (c != KeyEvent.VK_ENTER)) {
//                                getToolkit().beep();
//                                evt.consume();
//                                return;
//                          }
//                       }
//                    }
//                });
           if (isSelected) {

           }
            text.setText(value==null? "":value.toString());
            return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                return ((JTextField)text).getText();
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
//                if(e.getSource().equals(txtTOP)||e.getSource().equals(txtKurs)||e.getSource().equals(txtDiscPersen)||e.getSource().equals(txtDiscRp)||
//                        (e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                //}
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

//                if(!e.isTemporary() && e.getSource().equals(txtSupplier) && !isKoreksi && !fn.isListVisible() && suppLevel<=1)
//                    udfLoadItemFromPR();
//

           }
        }
    } ;

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
               case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable ||ct instanceof JXTable))                    {
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
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
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
                    if(!(ct instanceof JTable ||ct instanceof JXTable))
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
                case KeyEvent.VK_F2:{
                    //udfSave();
                    break;
                }
                case KeyEvent.VK_INSERT:{


                    break;
                }

                case KeyEvent.VK_DELETE:{
                    if(evt.getSource().equals(tblKontak) && tblKontak.getSelectedRow()>=0){
                        int iRow[]= tblKontak.getSelectedRows();
                        int rowPalingAtas=iRow[0];

                        for (int a=0; a<iRow.length; a++){
                            ((DefaultTableModel)tblKontak.getModel()).removeRow(tblKontak.getSelectedRow());
                        }

                        if(tblKontak.getRowCount()>0 && rowPalingAtas<tblKontak.getRowCount()){
                            //if(tblKontak.getSelectedRow()>0)
                                tblKontak.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
                        }
                        else{
                            if(tblKontak.getRowCount()>0)
                                tblKontak.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);


                        }
                        if(tblKontak.getSelectedRow()>=0)
                            tblKontak.changeSelection(tblKontak.getSelectedRow(), 0, false, false);
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

    GeneralFunction fn=new GeneralFunction();

}
