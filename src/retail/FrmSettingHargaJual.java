/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSettingHargaJual.java
 *
 * Created on Jun 7, 2010, 5:45:25 AM
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.JXTable;
import retail.main.GeneralFunction;
import retail.main.JDesktopImage;

/**
 *
 * @author cak-ust
 */
public class FrmSettingHargaJual extends javax.swing.JInternalFrame {
    private Connection conn;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    MyKeyListener kListener=new MyKeyListener();
    ArrayList lstKov=new ArrayList();
    private JDesktopImage desktop;

    /** Creates new form FrmSettingHargaJual */
    public FrmSettingHargaJual() {
        initComponents();
        aThis=this;
        masterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int iRow=masterTable.getSelectedRow();
                txtHarga2G.setEnabled(false);   txtHarga3G.setEnabled(false);
                txtHarga2E.setEnabled(false);   txtHarga3E.setEnabled(false);
                hypEdit.setEnabled(iRow>=0);     hypDelete.setEnabled(iRow>=0);
                lblKonv2G.setText("1");     lblKonv2E.setText("1");
                lblKonv3G.setText("1");     lblKonv3E.setText("1");
                txtHarga1G.setText("0");    txtHarga2G.setText("0");
                txtHarga3G.setText("0");    txtHarga1E.setText("0");
                txtHarga2E.setText("0");    txtHarga3E.setText("0");

                if(iRow>=0){
                    try{
                        String sQry="select coalesce(unit,'') as unit1," +
                                "coalesce(unit2,'') as unit2," +
                                "coalesce(konv2,1) as konv2," +
                                "coalesce(unit3,'') as unit3," +
                                "coalesce(konv3,1) as konv3," +
                                "coalesce(harga_r_1,0) as harga_r_1," +
                                "coalesce(harga_r_2,0) as harga_r_2, " +
                                "coalesce(harga_r_3,0) as harga_r_3," +
                                "coalesce(harga_g_1,0) as harga_g_1," +
                                "coalesce(harga_g_2,0) as harga_g_2," +
                                "coalesce(harga_g_3,0) as harga_g_3, " +
                                "coalesce(unit_jual,'') as unit_jual," +
                                "coalesce(i.hpp,0) as hpp " +
                                "from r_item i " +
                                "left join r_item_harga_jual h on h.kode_item=i.kode_item " +
                                "where i.kode_item='"+masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString()+"' ";

                        System.out.println(sQry);
                        ResultSet rs=conn.createStatement().executeQuery(sQry                                );


                        if(rs.next()){
                            lstKov.clear(); cmbSatuan.removeAllItems();
                            lstKov.add(1);
                            cmbSatuan.addItem(rs.getString("unit1"));   lblSat1G.setText(rs.getString("unit1"));    lblSat1E.setText(rs.getString("unit1"));
                            if(rs.getString("unit2").length()>0) {cmbSatuan.addItem(rs.getString("unit2"));  lstKov.add(rs.getInt("konv2")); }
                            lblSat2G.setText(rs.getString("unit2"));    lblSat2E.setText(rs.getString("unit2"));

                            if(rs.getString("unit3").length()>0) {cmbSatuan.addItem(rs.getString("unit3"));  lstKov.add(rs.getInt("konv3")); }
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

                            if(cmbSatuan.getItemCount()>0 && rs.getString("unit_jual").length()>0)
                                cmbSatuan.setSelectedItem(rs.getString("unit_jual"));
                            else
                                cmbSatuan.setSelectedIndex(-1);

                            txtHargaPokok.setText(fn.intFmt.format(rs.getDouble("hpp")));
                        }else{
                            udfClear();
                        }
                        rs.close();
                    }catch(SQLException se){
                        JOptionPane.showMessageDialog(aThis, se.getMessage());
                    }
                }
            }
        });

        masterTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                lblItemCount.setText(masterTable.getRowCount()+" Item(s)");
            }
        });
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel4, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jPanel5, kListener, txtFocusListener);
        masterTable.addKeyListener(kListener);

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
        txtHargaPokok.setText("0");
}

    public void setConn(Connection con){
        this.conn=con;
    }

    public void udfFilter(String sKode){
        int iPos=-1;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            final ResultSet rs=conn.createStatement().executeQuery("select i.kode_item, nama_item, coalesce(kategori,'') as kategori, " +
                    "gabung(coalesce(l.nama_lokasi,'')) as lokasi, " +
                    "  case 	when tipe='I' then 'Inventory' " +
                    "	when tipe='G' then 'Group' " +
                    "	when tipe='S' then 'Service' " +
                    "	else 'Non Inventory' end as type " +
                    "from r_item i " +
                    "left join r_item_lokasi il on il.kode_item=i.kode_item " +
                    "left join r_lokasi l on il.kode_lokasi=l.kode_lokasi " +
                    "where i.kode_item||coalesce(nama_item,'')||coalesce(kategori,'')||coalesce(barcode,'')||coalesce(l.nama_lokasi,'') ilike '%"+txtCari.getText()+"%' " +
                    (jCheckBox1.isSelected()? " and i.kode_item not in(select kode_item from r_item_harga_jual) ": "") +
                    "group by i.kode_item, nama_item, coalesce(kategori,''), type " +
                    "order by nama_item");

            ((DefaultTableModel)masterTable.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)masterTable.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("kategori"),
                    rs.getString("lokasi"),
                    rs.getString("type"),
                });
                iPos=sKode.equalsIgnoreCase(rs.getString("kode_item"))? ((DefaultTableModel)masterTable.getModel()).getRowCount()-1: iPos;
            }
            if(masterTable.getRowCount()>0){
                iPos=iPos<0? 0: iPos;
                //masterTable.setRowSelectionInterval(iPos, iPos);
                //masterTable.changeSelection(iPos, 0, false, false);
                masterTable.setModel((DefaultTableModel)fn.autoResizeColWidth(masterTable, (DefaultTableModel)masterTable.getModel()).getModel());
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            rs.close();
            finalize();
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
//        catch(SQLException se){
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            JOptionPane.showMessageDialog(this, se.getMessage());
//        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblKonv1G = new javax.swing.JLabel();
        lblSat1G = new javax.swing.JLabel();
        lblKonv2G = new javax.swing.JLabel();
        lblSat2G = new javax.swing.JLabel();
        lblSat3G = new javax.swing.JLabel();
        lblKonv3G = new javax.swing.JLabel();
        txtHarga3G = new javax.swing.JTextField();
        txtHarga1G = new javax.swing.JTextField();
        txtHarga2G = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        cmbSatuan = new javax.swing.JComboBox();
        lblSat3E1 = new javax.swing.JLabel();
        txtHargaPokok = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnLokasi = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        hypDelete = new org.jdesktop.swingx.JXHyperlink();
        hypNew = new org.jdesktop.swingx.JXHyperlink();
        hypEdit = new org.jdesktop.swingx.JXHyperlink();
        lblItemCount = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Setting Harga Jual");
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

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Harga Grosir");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 18, 170, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Harga Eceran");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 90, 20));
        jPanel1.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 170, -1));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setBackground(new java.awt.Color(153, 153, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Harga");
        jLabel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel10.setOpaque(true);
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 90, 20));

        jLabel11.setBackground(new java.awt.Color(153, 153, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Konv");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel11.setOpaque(true);
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 80, 20));

        jLabel12.setBackground(new java.awt.Color(153, 153, 255));
        jLabel12.setText("Satuan");
        jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel12.setOpaque(true);
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        lblKonv1E.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv1E.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv1E.setText("1");
        lblKonv1E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv1E.setOpaque(true);
        jPanel3.add(lblKonv1E, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 80, 20));

        lblSat1E.setBackground(new java.awt.Color(153, 255, 255));
        lblSat1E.setText("PCS");
        lblSat1E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat1E.setOpaque(true);
        jPanel3.add(lblSat1E, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 80, 20));

        lblKonv2E.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv2E.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv2E.setText("10");
        lblKonv2E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv2E.setOpaque(true);
        jPanel3.add(lblKonv2E, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 80, 20));

        lblSat2E.setBackground(new java.awt.Color(153, 255, 255));
        lblSat2E.setText("BOX");
        lblSat2E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat2E.setOpaque(true);
        jPanel3.add(lblSat2E, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 80, 20));

        lblSat3E.setBackground(new java.awt.Color(153, 255, 255));
        lblSat3E.setText("KARTON");
        lblSat3E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat3E.setOpaque(true);
        jPanel3.add(lblSat3E, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 80, 20));

        lblKonv3E.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv3E.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv3E.setText("100");
        lblKonv3E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv3E.setOpaque(true);
        jPanel3.add(lblKonv3E, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 80, 20));

        txtHarga3E.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga3E.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga3E.setText("0");
        txtHarga3E.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
        txtHarga2E.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga2EKeyTyped(evt);
            }
        });
        jPanel3.add(txtHarga2E, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, 20));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 250, 90));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(153, 153, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Harga");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setOpaque(true);
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 90, 20));

        jLabel3.setBackground(new java.awt.Color(153, 153, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Konv");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel3.setOpaque(true);
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 80, 20));

        jLabel4.setBackground(new java.awt.Color(153, 153, 255));
        jLabel4.setText("Satuan");
        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel4.setOpaque(true);
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 20));

        lblKonv1G.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv1G.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv1G.setText("1");
        lblKonv1G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv1G.setOpaque(true);
        jPanel4.add(lblKonv1G, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 80, 20));

        lblSat1G.setBackground(new java.awt.Color(153, 255, 255));
        lblSat1G.setText("PCS");
        lblSat1G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat1G.setOpaque(true);
        jPanel4.add(lblSat1G, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 80, 20));

        lblKonv2G.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv2G.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv2G.setText("10");
        lblKonv2G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv2G.setOpaque(true);
        jPanel4.add(lblKonv2G, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 80, 20));

        lblSat2G.setBackground(new java.awt.Color(153, 255, 255));
        lblSat2G.setText("BOX");
        lblSat2G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat2G.setOpaque(true);
        jPanel4.add(lblSat2G, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 80, 20));

        lblSat3G.setBackground(new java.awt.Color(153, 255, 255));
        lblSat3G.setText("KARTON");
        lblSat3G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat3G.setOpaque(true);
        jPanel4.add(lblSat3G, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 80, 20));

        lblKonv3G.setBackground(new java.awt.Color(153, 255, 255));
        lblKonv3G.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblKonv3G.setText("100");
        lblKonv3G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKonv3G.setOpaque(true);
        jPanel4.add(lblKonv3G, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 80, 20));

        txtHarga3G.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHarga3G.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHarga3G.setText("0");
        txtHarga3G.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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
        txtHarga2G.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHarga2GKeyTyped(evt);
            }
        });
        jPanel4.add(txtHarga2G, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 90, 20));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, 90));

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/issue.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel1.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 250, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Satuan Default");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 90, 20));
        jPanel1.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 270, 160, -1));

        jPanel1.add(cmbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 240, -1));

        lblSat3E1.setBackground(new java.awt.Color(153, 255, 255));
        lblSat3E1.setText("Harga Pokok");
        lblSat3E1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSat3E1.setOpaque(true);
        jPanel1.add(lblSat3E1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 80, 20));

        txtHargaPokok.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtHargaPokok.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaPokok.setText("0");
        txtHargaPokok.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtHargaPokok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHargaPokokKeyTyped(evt);
            }
        });
        jPanel1.add(txtHargaPokok, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 310, 90, 20));

        jLabel5.setText(" (Harga Sat. Kecil)");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 100, 20));

        jLabel6.setForeground(new java.awt.Color(0, 0, 153));
        jLabel6.setText("<html> <b>F5 &nbsp:</b> Simpan Harga Penjualan<br> <b>F12:</b> Focus ke Harga Penjualan <br> </html>"); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 170, 50));

        btnLokasi.setText("Lokasi Item");
        btnLokasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLokasiActionPerformed(evt);
            }
        });
        jPanel1.add(btnLokasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 450, 140, -1));

        jCheckBox1.setText("Belum diset");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jPanel1.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 400, -1, -1));

        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Kategori", "Lokasi", "Tipe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        masterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        masterTable.getTableHeader().setReorderingAllowed(false);
        masterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masterTableMouseClicked(evt);
            }
        });
        masterTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                masterTableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(masterTable);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setText("Pencarian :");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 80, 20));

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.add(txtCari, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 160, 20));

        hypDelete.setMnemonic('D');
        hypDelete.setText("Hapus");
        hypDelete.setFont(new java.awt.Font("Tahoma", 1, 12));
        hypDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypDeleteActionPerformed(evt);
            }
        });
        jPanel5.add(hypDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 50, -1));

        hypNew.setMnemonic('B');
        hypNew.setText("Baru");
        hypNew.setFont(new java.awt.Font("Tahoma", 1, 12));
        hypNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypNewActionPerformed(evt);
            }
        });
        jPanel5.add(hypNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, -1));

        hypEdit.setMnemonic('U');
        hypEdit.setText("Ubah");
        hypEdit.setFont(new java.awt.Font("Tahoma", 1, 12));
        hypEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypEditActionPerformed(evt);
            }
        });
        jPanel5.add(hypEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 50, -1));

        lblItemCount.setForeground(new java.awt.Color(0, 0, 102));
        lblItemCount.setText("1000 Items");
        jPanel5.add(lblItemCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 90, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    txtCari.requestFocus();
                }
          });
        udfFilter("");
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        udfSave();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtHarga1GKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga1GKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga1GKeyTyped

    private void txtHarga2GKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga2GKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga2GKeyTyped

    private void txtHarga3GKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga3GKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga3GKeyTyped

    private void txtHarga1EKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga1EKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga1EKeyTyped

    private void txtHarga2EKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga2EKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga2EKeyTyped

    private void txtHarga3EKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHarga3EKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtHarga3EKeyTyped

    private void hypNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypNewActionPerformed
        udfNew();
    }//GEN-LAST:event_hypNewActionPerformed

    private void hypDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypDeleteActionPerformed
        udfDelete();
    }//GEN-LAST:event_hypDeleteActionPerformed

    private void hypEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypEditActionPerformed
        udfUpdate();
    }//GEN-LAST:event_hypEditActionPerformed

    private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
        if(evt.getClickCount()==2)
            udfUpdate();
    }//GEN-LAST:event_masterTableMouseClicked

    private void txtHargaPokokKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHargaPokokKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaPokokKeyTyped

    private void masterTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_masterTableKeyPressed
//        if(evt.getKeyCode()==KeyEvent)
//        masterTable.setFocusable(false);
//        txtHarga1G.requestFocusInWindow();
//        txtHarga1G.setFocusable(true);
//        txtHarga1G.requestFocus();
    }//GEN-LAST:event_masterTableKeyPressed

    private void btnLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLokasiActionPerformed
        udfUpdateLokasi();
    }//GEN-LAST:event_btnLokasiActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        udfFilter("");
    }//GEN-LAST:event_jCheckBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLokasi;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cmbSatuan;
    private org.jdesktop.swingx.JXHyperlink hypDelete;
    private org.jdesktop.swingx.JXHyperlink hypEdit;
    private org.jdesktop.swingx.JXHyperlink hypNew;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblItemCount;
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
    private javax.swing.JLabel lblSat3E1;
    private javax.swing.JLabel lblSat3G;
    private org.jdesktop.swingx.JXTable masterTable;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHarga1E;
    private javax.swing.JTextField txtHarga1G;
    private javax.swing.JTextField txtHarga2E;
    private javax.swing.JTextField txtHarga2G;
    private javax.swing.JTextField txtHarga3E;
    private javax.swing.JTextField txtHarga3G;
    private javax.swing.JTextField txtHargaPokok;
    // End of variables declaration//GEN-END:variables

    private void udfSave() {
        int iRow=masterTable.getSelectedRow();
        if(cmbSatuan.getSelectedIndex()<0){
            JOptionPane.showMessageDialog(this, "Silakan pilih satuan jual default terlebih dulu!");
            cmbSatuan.requestFocusInWindow();
            return;
        }
        TableColumnModel col=masterTable.getColumnModel();
        String sItem=masterTable.getValueAt(iRow, col.getColumnIndex("Kode")).toString();
        try{

            ResultSet rs=conn.createStatement().executeQuery("select fn_r_save_harga_jual('"+sItem+"', " +
                    fn.udfGetDouble(txtHarga1E.getText())+", " +
                    fn.udfGetDouble(txtHarga2E.getText())+", " +
                    fn.udfGetDouble(txtHarga3E.getText())+", " +
                    fn.udfGetDouble(txtHarga1G.getText())+", " +
                    fn.udfGetDouble(txtHarga2G.getText())+", " +
                    fn.udfGetDouble(txtHarga3G.getText())+" " +
                    ")");
            if(rs.next()){
                conn.createStatement().executeUpdate("update r_item set unit_jual='"+cmbSatuan.getSelectedItem().toString()+"', " +
                        "konv_jual="+lstKov.get(cmbSatuan.getSelectedIndex()).toString()+", " +
                        "hpp= "+fn.udfGetDouble(txtHargaPokok.getText()) +" "+
                        "where kode_item='"+sItem+"'; ");
                
                JOptionPane.showMessageDialog(this, "Simpan harga penjualan sukses!");
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
                if((e.getSource() instanceof JTextField ||(((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor")))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(!e.getSource().equals(txtCari))
                    ((JTextField)e.getSource()).setText(fn.intFmt.format(fn.udfGetDouble(((JTextField)e.getSource()).getText())));


           }
        }


    } ;

    void setDesktopIcon(JDesktopImage jDesktopPane1) {
        this.desktop=jDesktopPane1;
    }

    public class MyKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getSource().equals(txtCari))
                udfFilter("");
        }


        @Override
        public void keyTyped(java.awt.event.KeyEvent evt) {
          if (evt.getSource() instanceof JTextField &&
              ((JTextField)evt.getSource()).getName()!=null &&
              ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")) {

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
                        if (!fn.isListVisible() && !evt.getSource().equals(txtCari)){
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
                    if(!(evt.getSource() instanceof JXTable)){
                        if(evt.getSource().equals(txtCari)){
                            masterTable.requestFocusInWindow();
                            if(masterTable.getSelectedRow()<0){
                                masterTable.setRowSelectionInterval(0, 0);
                                masterTable.changeSelection(0, 0, false, false);
                            }
                            break;
                        }
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
                    if(!(evt.getSource() instanceof JXTable)){
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:{
                    if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?",
                            "Message",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        dispose();
                    }
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F12:{
                    masterTable.setFocusable(false);
                    txtHarga1G.requestFocusInWindow();
                    txtHarga1G.setFocusable(true);
                    txtHarga1G.requestFocus();
                    break;
                }
                case KeyEvent.VK_F5:{
                    txtCari.setText("");
                    udfFilter("");
                    break;
                }
                case KeyEvent.VK_DELETE:{

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

    private void udfNew() {
        FrmItemMaster fMaster=new FrmItemMaster();
        fMaster.setTitle("Item baru");
        fMaster.setConn(conn);
        fMaster.setKodeBarang("");
        fMaster.setIsNew(true);
        fMaster.setObjForm(this);
        //fMaster.setSrcTable(masterTable);
//        fMaster.setSrcModel(myModel);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        desktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
        } catch(PropertyVetoException PO){

        }
        }
        
    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
            if(masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Tipe")).toString().equalsIgnoreCase("Group")){
                FrmItemGroup fMaster=new FrmItemGroup();
                fMaster.setTitle("Update Item Group");
                fMaster.setConn(conn);
                fMaster.setObjForm(this);
                fMaster.setSrcTable(masterTable);
                fMaster.setIsNew(false);
                fMaster.setKode(masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString());
                fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
                desktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
                fMaster.setVisible(true);
                try{fMaster.setSelected(true);} catch(PropertyVetoException PO){}
            }else{
                FrmItemMaster fMaster=new FrmItemMaster();
                fMaster.setTitle("Update Item / Barang");
                //fMaster.settDesktopPane(jDesktop);
                fMaster.setConn(conn);
                //fMaster.setSrcTable(masterTable);
                fMaster.setObjForm(this);
                fMaster.setIsNew(false);
                fMaster.setKodeBarang(masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString());
                fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
                desktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
                fMaster.setVisible(true);
                try{
                    //fMaster.setMaximum(true);
                    fMaster.setSelected(true);
                } catch(PropertyVetoException PO){

                }
            }
        }
    }

    private void udfUpdateLokasi() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
            FrmItemLocation fLokasi=new FrmItemLocation();
            fLokasi.setConn(conn);
            fLokasi.setObjForm(this);
            fLokasi.setItem(masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString(),
                    masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Nama Barang")).toString());
            fLokasi.setBounds(0, 0, fLokasi.getWidth(), fLokasi.getHeight());
            desktop.add(fLokasi, javax.swing.JLayeredPane.DEFAULT_LAYER);
            fLokasi.setVisible(true);
            try{
                //fMaster.setMaximum(true);
                fLokasi.setSelected(true);
            } catch(PropertyVetoException PO){

            }

        }
    }
    
    private void udfDelete() {
        int iRow = masterTable.getSelectedRow();

        if(iRow>=0){
            if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus data tersebut?", "Confirm", JOptionPane.YES_NO_OPTION)==
                    JOptionPane.YES_OPTION){
                    try {
                        int iDel = conn.createStatement().executeUpdate("Delete from r_item where kode_item='" + masterTable.getValueAt(iRow, 0).toString() + "'");

                        if(iDel>0){
                            JOptionPane.showMessageDialog(this, "Hapus data sukses!!!");
                            ((DefaultTableModel)masterTable.getModel()).removeRow(iRow);

                            if(iRow>=masterTable.getRowCount() && ((DefaultTableModel)masterTable.getModel()).getRowCount()>0){
                                masterTable.setRowSelectionInterval(iRow-1, iRow-1);
    //                        }else if(iRow>myModel.getRowCount()){
    //                            masterTable.setRowSelectionInterval(iRow-1, iRow-1);
                            }else if(((DefaultTableModel)masterTable.getModel()).getRowCount()>0){
                                masterTable.setRowSelectionInterval(iRow, iRow);
                            }
                        }else{
                            JOptionPane.showMessageDialog(this, "Hapus data gagal!!!");
                        }
                    } catch (SQLException ex) {
                        //Logger.getLogger(AgamaList.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(this, "Hapus data gagal");
                    }

            }
        }
    }
}
