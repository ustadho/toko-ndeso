/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StockOpname.java
 *
 * Created on Mar 16, 2009, 8:42:49 PM
 */
package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.springframework.mail.MailAuthenticationException;
import retail.main.GeneralFunction;
import retail.main.ListRsbm;

/**
 *
 * @author ustadho
 */
public class StockOpname extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel myModel;
    private ArrayList lstGudang = new ArrayList();
    private ArrayList lstSatuan = new ArrayList();
    private ArrayList lstHarga = new ArrayList();
    private ArrayList lstKonversi = new ArrayList();
    private NumberFormat dFmt = new DecimalFormat("#,##0.00");
    private NumberFormat curFmt = new DecimalFormat("#,##0.00");

    ;
    private boolean isKoreksi = false;
    private retail.main.ListRsbm lst;
    private float fCurrentQty = 0;
    private MyKeyListener kListener = new MyKeyListener();
    private GeneralFunction fn = new GeneralFunction();
    private Date tglSkg;
    private Component aThis;

    /** Creates new form StockOpname */
    public StockOpname() {
        initComponents();
        aThis = this;
        myModel = (DefaultTableModel) opnameTable.getModel();
        opnameTable.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));

        cmbUnit.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (((JComboBox) e.getSource()).getSelectedIndex() >= 0 && lstKonversi.size() > 0) {
                    txtKonv.setText(lstKonversi.get(((JComboBox) e.getSource()).getSelectedIndex()).toString());
                    txtCurrentQty.setText(dFmt.format(fCurrentQty / fn.udfGetInt(lstKonversi.get(cmbUnit.getSelectedIndex()))));
                }
            }
        });
        for (int i = 0; i < panelAddItem.getComponentCount(); i++) {
            Component c = panelAddItem.getComponent(i);
            if (c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD") || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
                    || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox") || c.getClass().getSimpleName().equalsIgnoreCase("JButton")) {
                c.addKeyListener(kListener);
                c.addFocusListener(txtFocusListener);

            }
        }


    }

    public void setConn(Connection con) {
        this.conn = con;
    }

    public void setKoreksi(boolean b) {
        isKoreksi = b;
        txtNoOpname.setEditable(b);
    }

    private void udfClear() {
        txtNoOpname.setText("");
        txtKeterangan.setText("");
        cmbGudang.setSelectedIndex(-1);
        chkValueAdjustment.setSelected(false);
        cmbGudang.setEnabled(true);
        myModel.setNumRows(0);
        opnameTable.setModel(myModel);
    }

    private void udfInitForm() {
        lst = new ListRsbm();
        lst.setConn(conn);
        lst.setVisible(false);
        jXDatePicker1.setFormats("dd/MM/yyyy");

        try {
            ResultSet rs = conn.createStatement().executeQuery("select kode_gudang, coalesce(nama_gudang,'') as nama_gudang, current_date as skg from r_gudang order by kode_gudang");

            lstGudang.clear();
            cmbGudang.removeAllItems();
            while (rs.next()) {
                
                lstGudang.add(rs.getString("kode_gudang"));
                cmbGudang.addItem(rs.getString("nama_gudang"));
                tglSkg = rs.getDate("skg");
            }
            rs.close();
            jXDatePicker1.setDate(tglSkg);
            udfLoadOpname();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        chkValueAdjustment.setSelected(true);
        chkValueAdjustmentActionPerformed(new ActionEvent(chkValueAdjustment, ActionEvent.ACTION_PERFORMED, "checked"));

        opnameTable.getColumn("Kode").setPreferredWidth(txtKode.getWidth());
        opnameTable.getColumn("Nama Barang").setPreferredWidth(txtNamaBarang.getWidth());
    }

    private void udfInsertItem() {
        if (txtKode.getText().trim().equalsIgnoreCase("") || txtNamaBarang.getText().trim().equalsIgnoreCase("")) {
            txtKode.requestFocus();
            return;
        } else if (GeneralFunction.udfGetDouble(txtNewValue.getText()) == 0 && GeneralFunction.udfGetDouble(txtNewQty.getText()) > 0) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan new value terlebih dulu");
            chkValueAdjustment.setSelected(true);
            txtNewValue.requestFocus();
            return;
        }
        if (txtNoOpname.getText().length() == 0) {
            try {
                ResultSet rs = conn.createStatement().executeQuery("select fn_r_ins_opname_header('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "',"
                        + "'', '" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "', '" + txtKeterangan.getText() + "', '" + MainForm.sUserName + "', "
                        + "" + chkValueAdjustment.isSelected() + ")");

                if (rs.next()) {
                    txtNoOpname.setText(rs.getString(1));
                    cmbGudang.setEnabled(false);
                    jXDatePicker1.setEnabled(false);
                }
                rs.close();

            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }

        TableColumnModel column = opnameTable.getColumnModel();
        for (int i = 0; i < opnameTable.getRowCount(); i++) {
            if (myModel.getValueAt(i, column.getColumnIndex("Kode")).toString().equalsIgnoreCase(txtKode.getText())
                    && myModel.getValueAt(i, column.getColumnIndex("Nama Barang")).toString().equalsIgnoreCase(txtNamaBarang.getText())
                    && myModel.getValueAt(i, column.getColumnIndex("Batch No.")).toString().equalsIgnoreCase(txtBatchNo.getText())
                    && myModel.getValueAt(i, column.getColumnIndex("Exp. Date")).toString().equalsIgnoreCase(txtExpDate.getText())) {

                JOptionPane.showMessageDialog(this, "Barang tersebut sudah dimasukkan pada baris ke :" + (i + 1));
                opnameTable.setRowSelectionInterval(i, i);
                txtKode.requestFocus();
                return;
            }
        }

        if (btnInsertItem.getText().equalsIgnoreCase("ADD")) {
            double newValue = 0;
            newValue = (GeneralFunction.udfGetDouble(txtNewValue.getText()) == 0 ? (GeneralFunction.udfGetDouble(txtCurrentValue.getText()) / GeneralFunction.udfGetDouble(txtCurrentQty.getText())) * GeneralFunction.udfGetDouble(txtNewQty.getText())
                    : GeneralFunction.udfGetDouble(txtNewValue.getText()));

            myModel.addRow(new Object[]{
                        txtKode.getText(),
                        txtNamaBarang.getText(),
                        txtBatchNo.getText(),
                        txtExpDate.getText(),
                        cmbUnit.getSelectedItem().toString(),
                        dFmt.format(GeneralFunction.udfGetDouble(txtCurrentQty.getText())),
                        dFmt.format(GeneralFunction.udfGetDouble(txtNewQty.getText())),
                        dFmt.format(GeneralFunction.udfGetDouble(txtCurrentValue.getText())),
                        newValue,
                        txtKonv.getText()
                    });
            opnameTable.setRowSelectionInterval(myModel.getRowCount() - 1, myModel.getRowCount() - 1);
        } else if (btnInsertItem.getText().equalsIgnoreCase("Update")) {
        }
        cmbGudang.setEnabled(opnameTable.getRowCount() < 0);
        udfStartNewItem();
    }

    private void udfInsertItem2() {
        if (txtKode.getText().trim().equalsIgnoreCase("") || txtNamaBarang.getText().trim().equalsIgnoreCase("")) {
            txtKode.requestFocus();
            return;
        } else if (GeneralFunction.udfGetDouble(txtNewValue.getText()) == 0 && GeneralFunction.udfGetDouble(txtNewQty.getText()) > 0) {
            JOptionPane.showMessageDialog(this, "Silakan masukkan new value terlebih dulu");
            chkValueAdjustment.setSelected(true);
            txtNewValue.requestFocus();
            return;
        }
        if (txtNoOpname.getText().length() == 0) {
            try {
                ResultSet rs = conn.createStatement().executeQuery(
                        "select fn_r_ins_opname_header('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "',"
                        + "'', '" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "', '" + txtKeterangan.getText() + "', '" + MainForm.sUserName + "', "
                        + "" + chkValueAdjustment.isSelected() + ")");

                if (rs.next()) {
                    txtNoOpname.setText(rs.getString(1));
                    cmbGudang.setEnabled(false);
                    jXDatePicker1.setEnabled(false);
                }
                rs.close();

            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }

        if (btnInsertItem.getText().equalsIgnoreCase("ADD")) {
            if (isExistsItem(txtKode.getText(), txtExpDate.getText(), txtBatchNo.getText())) {
                if (JOptionPane.showConfirmDialog(this, "Item tersebut sudah dimasukkan apakah anda ingin mengupdate?", "Item sudah masuk", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return;
                } else {
                    udfUpdateItem();
                    udfLoadOpnameDetail();
                    udfStartNewItem();
                    return;
                }
            }
//            double newValue=(GeneralFunction.udfGetDouble(txtNewValue.getText())==0? (GeneralFunction.udfGetDouble(txtCurrentValue.getText())/GeneralFunction.udfGetDouble(txtCurrentQty.getText()))*GeneralFunction.udfGetDouble(txtNewQty.getText()):
//                GeneralFunction.udfGetDouble(txtNewValue.getText()));
            try {
                String sQry = "insert into r_opname_detail(kode_opname, kode_item, exp_date,  batch_no, "
                        + "cur_qty, new_qty, cur_value, new_value, unit, konv) "
                        + "VALUES('" + txtNoOpname.getText() + "', '" + txtKode.getText() + "', "
                        + (txtExpDate.getText().trim().length() == 0 ? "null " : "'" + new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(txtExpDate.getText())) + "'") + ","
                        + "'" + txtBatchNo.getText() + "', "
                        + fn.udfGetDouble(txtCurrentQty.getText()) + ", "
                        + fn.udfGetDouble(txtNewQty.getText()) + ", "
                        + fn.udfGetDouble(txtCurrentValue.getText()) + ", "
                        + fn.udfGetDouble(txtNewValue.getText()) + ", "
                        + "'" + (cmbUnit.getSelectedIndex() >= 0 ? cmbUnit.getSelectedItem().toString() : "") + "',"
                        + fn.udfGetDouble(txtKonv.getText()) + " )";
                System.out.println(sQry);
                conn.setAutoCommit(false);
                conn.createStatement().executeUpdate(sQry);

                conn.setAutoCommit(true);
                udfLoadOpnameDetail();
                udfStartNewItem();
            } catch (SQLException se) {
                try {
                    conn.setAutoCommit(true);
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, se.getMessage());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            } catch (ParseException fe) {
                JOptionPane.showMessageDialog(this, fe.getMessage());

            }

//            myModel.addRow(new Object[]{
//                txtKode.getText(),
//                txtNamaBarang.getText(),
//                txtBatchNo.getText(),
//                txtExpDate.getText(),
//                cmbUnit.getSelectedItem().toString(),
//                dFmt.format(GeneralFunction.udfGetDouble(txtCurrentQty.getText())),
//                dFmt.format(GeneralFunction.udfGetDouble(txtNewQty.getText())),
//                dFmt.format(GeneralFunction.udfGetDouble(txtCurrentValue.getText())),
//                newValue,
//                txtKonv.getText()
//            });
//            opnameTable.setRowSelectionInterval(myModel.getRowCount()-1, myModel.getRowCount()-1);
        } else if (btnInsertItem.getText().equalsIgnoreCase("Update")) {
            udfUpdateItem();
        }
        cmbGudang.setEnabled(opnameTable.getRowCount() < 0);
        udfStartNewItem();
    }

    private void udfUpdateItem() {
        try {
            double newValue = (GeneralFunction.udfGetDouble(txtNewValue.getText()) == 0 ? (GeneralFunction.udfGetDouble(txtCurrentValue.getText()) / GeneralFunction.udfGetDouble(txtCurrentQty.getText())) * GeneralFunction.udfGetDouble(txtNewQty.getText())
                    : GeneralFunction.udfGetDouble(txtNewValue.getText()));
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate("UPDATE r_opname_detail "
                    + "SET  "
                    + "   cur_qty=" + fn.udfGetDouble(txtCurrentQty.getText()) + ", "
                    + "   new_qty=" + fn.udfGetDouble(txtNewQty.getText()) + ", "
                    + "   cur_value=" + fn.udfGetDouble(txtCurrentValue.getText()) + ", "
                    + "   new_value=" + fn.udfGetDouble(txtNewValue.getText()) + ", "
                    + "   unit='" + (cmbUnit.getSelectedIndex() < 0 ? "" : cmbUnit.getSelectedItem().toString()) + "', "
                    + "   konv=" + fn.udfGetDouble(txtKonv.getText()) + " "
                    + "WHERE kode_opname='" + txtNoOpname.getText() + "' and kode_item='" + txtKode.getText() + "' "
                    + "and coalesce(to_char(exp_date, 'dd/MM/yyyy'),'')='" + txtExpDate.getText() + "' "
                    + "and batch_no='" + txtBatchNo.getText() + "'");

            conn.setAutoCommit(true);

        } catch (SQLException se) {
            try {
                conn.setAutoCommit(true);
                conn.rollback();
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private boolean isExistsItem(String sItem, String sExpDate, String sNoBatch) {
        boolean b = false;
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * from r_opname_detail where "
                    + "kode_opname='" + txtNoOpname.getText() + "' and kode_item='" + sItem + "' and "
                    + "coalesce(to_char(exp_date, 'dd/MM/yyyy'),'')='" + sExpDate + "' and batch_no='" + sNoBatch + "'");

            b = rs.next();
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        return b;
    }

    private void udfSetSatuanBarang() {
        lstSatuan.clear();
        lstHarga.clear();
        lstKonversi.clear();
        cmbUnit.removeAllItems();

        try {
            int iRow = opnameTable.getSelectedRow();
            ResultSet rs = conn.createStatement().executeQuery("select * from fn_r_list_satuan_item('" + txtKode.getText() + "') as (harga double precision, satuan varchar, konversi double precision)");
            while (rs.next()) {
                lstHarga.add(rs.getDouble("harga"));
                lstSatuan.add(rs.getString("satuan"));
                cmbUnit.addItem(rs.getString("satuan"));
                lstKonversi.add(rs.getString("konversi"));
            }
            //myModel.setValueAt(cmbSatuan.getModel(), iRow, jXTable1.getColumnModel().getColumnIndex("lstSat"));
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfStartNewItem() {
        txtKode.setText("");
        txtNamaBarang.setText("");
        txtBatchNo.setText("");
        txtExpDate.setText("");
        txtCurrentQty.setText("");
        txtNewQty.setText("");
        cmbUnit.removeAllItems();
        txtCurrentValue.setText("");
        txtNewValue.setText("");
        txtKonv.setText("");
        txtKode.requestFocus();
        btnInsertItem.setText("Add");
    }

    private void udfSaveOpname() {
        if (myModel.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan isi barang yang akan diopname terlebih dulu!");
            txtKode.requestFocus();
            return;
        }
        if (cmbGudang.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih gudang terlebih dulu!");
            cmbGudang.requestFocus();
            return;
        }
        try {
            conn.setAutoCommit(false);

            ResultSet rs = conn.createStatement().executeQuery("select fn_r_ins_opname_header('" + new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) + "',"
                    + "'', '" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "', '" + txtKeterangan.getText() + "', '" + MainForm.sUserName + "', "
                    + "" + chkValueAdjustment.isSelected() + ")");

            if (rs.next()) {
                txtNoOpname.setText(rs.getString(1));

                ResultSet rsDet = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from r_opname_detail limit 0");

                TableColumnModel column = opnameTable.getColumnModel();
                SimpleDateFormat yMd = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat dMy = new SimpleDateFormat("dd/MM/yyyy");

                double newQty = 0, newValue;

                for (int i = 0; i < myModel.getRowCount(); i++) {
                    newQty = GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("New Qty")).toString()) * GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("Konv")).toString());
                    newValue = (!chkValueAdjustment.isSelected() ? 0 : GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("Hpp Baru")).toString()));

                    rsDet.moveToInsertRow();
                    rsDet.updateString("kode_opname", txtNoOpname.getText());
                    rsDet.updateString("kode_item", myModel.getValueAt(i, column.getColumnIndex("Kode")).toString());
                    rsDet.updateDate("exp_date", myModel.getValueAt(i, column.getColumnIndex("Exp. Date")).toString().equalsIgnoreCase("") ? null : java.sql.Date.valueOf(yMd.format(dMy.parse(myModel.getValueAt(i, column.getColumnIndex("Exp. Date")).toString()))));
                    rsDet.updateString("batch_no", myModel.getValueAt(i, column.getColumnIndex("Batch No.")).toString());
                    rsDet.updateDouble("cur_qty", GeneralFunction.udfGetDouble(myModel.getValueAt(i, column.getColumnIndex("Current Qty")).toString()));
                    rsDet.updateDouble("new_qty", newQty);
                    rsDet.updateDouble("cur_value", GeneralFunction.udfGetDouble(myModel.getValueAt(i, 7).toString())); //Current Value
                    rsDet.updateDouble("new_value", newValue); //column.getColumnIndex("Hpp Baru")
                    rsDet.updateString("kode_dep", null);
                    rsDet.updateString("project", null);
                    rsDet.updateString("unit", myModel.getValueAt(i, column.getColumnIndex("Unit")).toString());
                    rsDet.updateInt("konv", GeneralFunction.udfGetInt(myModel.getValueAt(i, column.getColumnIndex("Konv")).toString()));
                    rsDet.insertRow();
                }
                rsDet.close();

            }

            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Opname tersimpan");
        } catch (ParseException ex) {
            Logger.getLogger(StockOpname.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException se) {
            try {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Simpan opname gagal.\nTransaksi di Rollback\n\n" + se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(StockOpname.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void udfLoadOpname() {
        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    "select o.kode_opname, o.tanggal, o.kode_gudang, coalesce(g.nama_gudang,'') as nama_gudang, "
                    + "coalesce(o.catatan,'') as catatan, coalesce(is_value_adj, false) as is_Value_adj "
                    + "from r_opname o "
                    + "inner join r_gudang g on g.kode_gudang=o.kode_gudang "
                    + "where to_char(tanggal, 'yyyy-MM-dd')='" + fn.yyyymmdd_format.format(jXDatePicker1.getDate()) + "' ");
            if (rs.next()) {
                txtNoOpname.setText(rs.getString("kode_opname"));
                jXDatePicker1.setDate(rs.getDate("tanggal"));
                cmbGudang.setSelectedItem(rs.getString("nama_gudang"));
                txtKeterangan.setText(rs.getString("catatan"));
                chkValueAdjustment.setSelected(rs.getBoolean("is_value_adj"));

                rs.close();
                udfLoadOpnameDetail();
            } else {
                txtNoOpname.setText("");
                //jXDatePicker1.setDate(tglSkg);
                cmbGudang.setSelectedItem(MainForm.sNamaGudang);
                txtKeterangan.setText("");
                chkValueAdjustment.setSelected(false);

                ((DefaultTableModel) opnameTable.getModel()).setNumRows(0);

            }
            rs.close();
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfLoadOpnameDetail(){
        try{
            int iRow=0;
            ((DefaultTableModel) opnameTable.getModel()).setNumRows(0);
            ResultSet rs=null;
            rs = conn.createStatement().executeQuery("select d.kode_item, coalesce(i.nama_item,'') as nama_item, coalesce(d.batch_no,'') as batch_no, "
                    + "coalesce(to_char(exp_date,'dd/MM/yyyy'), '') as exp_date, coalesce(d.unit,'') as unit, coalesce(d.konv,1) as konv, "
                    + "coalesce(cur_qty,0) as cur_qty, coalesce(d.new_qty,0) as new_qty, coalesce(cur_value,0) as cur_value, coalesce(new_value,0) as new_value "
                    + "from r_opname_detail d "
                    + "inner join r_item i on i.kode_item=d.kode_item "
                    + "where d.kode_opname='" + txtNoOpname.getText() + "' " +
                    "and d.kode_item||coalesce(i.nama_item,'') ilike '%"+txtCari.getText()+"%' " +
                    "order by d.serial_no desc");
            while (rs.next()) {
                ((DefaultTableModel) opnameTable.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("batch_no"),
                    rs.getString("exp_date"),
                    rs.getString("unit"),
                    rs.getDouble("cur_qty"),
                    rs.getDouble("new_qty"),
                    rs.getDouble("cur_value"),
                    rs.getDouble("new_value"),
                    rs.getInt("konv"),
                });
                if(txtKode.getText().equalsIgnoreCase(rs.getString("kode_item")) &&
                   txtNamaBarang.getText().equalsIgnoreCase(rs.getString("kode_item")) &&
                   txtBatchNo.getText().equalsIgnoreCase(rs.getString("batch_no")))
                    iRow=((DefaultTableModel) opnameTable.getModel()).getRowCount()-1;
            }
            if(iRow>=0){
                opnameTable.setRowSelectionInterval(iRow, iRow);
                opnameTable.changeSelection(iRow, 0, false, false);
            }
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNoOpname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        cmbGudang = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        chkValueAdjustment = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        opnameTable = new org.jdesktop.swingx.JXTable();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jLabel5 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        panelAddItem = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtNamaBarang = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtCurrentQty = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        cmbUnit = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        txtNewQty = new javax.swing.JTextField();
        lblCurrentValue = new javax.swing.JLabel();
        txtCurrentValue = new javax.swing.JTextField();
        lblNewValue = new javax.swing.JLabel();
        txtNewValue = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtKonv = new javax.swing.JTextField();
        btnInsertItem = new javax.swing.JButton();
        txtBatchNo = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtExpDate = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Penyesuaian Persediaan Barang");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Tgl. Opname :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 100, 20));
        jPanel1.add(txtNoOpname, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 140, 23));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Keterangan");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 90, 20));

        jXDatePicker1.setEditable(false);
        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker1ActionPerformed(evt);
            }
        });
        jPanel1.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 120, -1));

        cmbGudang.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cmbGudang, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 130, -1));

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel30.setText("Gudang :"); // NOI18N
        jPanel1.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 80, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("No. Opname");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, 20));

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        jScrollPane1.setViewportView(txtKeterangan);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 670, 50));

        chkValueAdjustment.setSelected(true);
        chkValueAdjustment.setText("Penyesuaian Hpp");
        chkValueAdjustment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkValueAdjustmentActionPerformed(evt);
            }
        });
        jPanel1.add(chkValueAdjustment, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 70, 180, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18));
        jLabel1.setText("Stock Opname");

        opnameTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Batch No.", "Exp. Date", "Unit", "Current Qty", "New Qty", "Hpp Skg", "Hpp Baru", "Konv"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        opnameTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        opnameTable.getTableHeader().setReorderingAllowed(false);
        opnameTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                opnameTableKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(opnameTable);
        opnameTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        opnameTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        opnameTable.getColumnModel().getColumn(9).setPreferredWidth(40);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Pencarian :");

        txtCari.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(744, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtCari, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );

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
        panelAddItem.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 2, 60, -1));

        txtCurrentQty.setEditable(false);
        txtCurrentQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtCurrentQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 60, -1));

        jLabel20.setBackground(new java.awt.Color(255, 255, 204));
        jLabel20.setText("Exp. Date");
        jLabel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel20.setOpaque(true);
        panelAddItem.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 2, 70, -1));

        panelAddItem.add(cmbUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 60, -1));

        jLabel21.setBackground(new java.awt.Color(255, 255, 204));
        jLabel21.setText("Qty Real");
        jLabel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel21.setOpaque(true);
        panelAddItem.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 2, 60, -1));

        txtNewQty.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtNewQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 20, 60, -1));

        lblCurrentValue.setBackground(new java.awt.Color(255, 255, 204));
        lblCurrentValue.setText("Hpp Skg");
        lblCurrentValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblCurrentValue.setOpaque(true);
        panelAddItem.add(lblCurrentValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 2, 85, -1));

        txtCurrentValue.setEditable(false);
        txtCurrentValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtCurrentValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 20, 85, -1));

        lblNewValue.setBackground(new java.awt.Color(255, 255, 204));
        lblNewValue.setText("Hpp  Baru");
        lblNewValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblNewValue.setOpaque(true);
        panelAddItem.add(lblNewValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(785, 2, 85, -1));

        txtNewValue.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelAddItem.add(txtNewValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(785, 20, 85, -1));

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setText("Konv");
        jLabel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel25.setOpaque(true);
        panelAddItem.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 2, 30, -1));

        txtKonv.setEditable(false);
        txtKonv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtKonv.setEnabled(false);
        panelAddItem.add(txtKonv, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 20, 30, -1));

        btnInsertItem.setText("Add");
        btnInsertItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertItemActionPerformed(evt);
            }
        });
        panelAddItem.add(btnInsertItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 19, 79, -1));

        txtBatchNo.setEditable(false);
        txtBatchNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtBatchNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 90, -1));

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setText("Qty Komp");
        jLabel27.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel27.setOpaque(true);
        panelAddItem.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 2, 60, -1));

        jLabel29.setBackground(new java.awt.Color(255, 255, 204));
        jLabel29.setText("Batch No.");
        jLabel29.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel29.setOpaque(true);
        panelAddItem.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 2, 90, -1));

        txtExpDate.setEditable(false);
        txtExpDate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        panelAddItem.add(txtExpDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 70, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 980, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(93, 93, 93))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1024)/2, (screenSize.height-696)/2, 1024, 696);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        udfInitForm();
    }//GEN-LAST:event_formWindowOpened

    private void txtKodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKeyReleased
        try {
            switch (evt.getKeyCode()) {
                case java.awt.event.KeyEvent.VK_ENTER: {
                    if (lst.isVisible()) {
                        Object[] obj = lst.getOResult();
                        if (obj.length > 0) {
                            txtKode.setText(obj[0].toString());
                            lst.udfSelected();
                            lst.setVisible(false);
                        }
                    }
                    if (txtNamaBarang.getText().length() > 0) {
                        udfSetSatuanBarang();
                        fCurrentQty = GeneralFunction.udfGetFloat(txtCurrentQty.getText());
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
                        String sQry = "select distinct i.kode_item, coalesce(nama_item,'') as nama_barang, coalesce(batch_no,''), coalesce(to_char(exp_date,'dd/MM/yyyy'),'') as exp_date,"
                                + "coalesce(saldo,0) as curr_qty, coalesce(unit,'') as unit,  "
                                + "coalesce(hpp,0) as curr_hpp, 1 "
                                + "from vw_r_item_trx i "
                                + "left join r_item_stok stok on i.kode_item=stok.kode_item and "
                                + "stok.kode_gudang='" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "' and saldo<>0  "
                                + "where i.active=true and (i.kode_item||coalesce(nama_item,'')) ilike  '%" + txtKode.getText() + "%'  " +
                                "order by coalesce(nama_item,'') limit 500";

                        //System.out.println(sQry);
                        lst.setSQuery(sQry);

                        lst.setBounds(txtKode.getLocationOnScreen().x,
                                txtKode.getLocationOnScreen().y + txtKode.getHeight(),
                                txtKode.getWidth() + txtNamaBarang.getWidth() + txtExpDate.getWidth() + txtBatchNo.getWidth(),
                                (lst.getIRowCount() > 10 ? 12 * lst.getRowHeight() : (lst.getIRowCount() + 3) * lst.getRowHeight()));


                        lst.setFocusableWindowState(false);
                        lst.setTxtCari(txtKode);
                        //lst.setLblDes(new javax.swing.JLabel[]{lblAnggota, lblNip});
                        lst.setCompDes(new JComponent[]{txtNamaBarang, txtBatchNo, txtExpDate, txtCurrentQty, cmbUnit, txtCurrentValue, txtKonv});

                        lst.setColWidth(0, txtKode.getWidth());
                        lst.setColWidth(1, txtNamaBarang.getWidth() - 10);

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

    private void btnInsertItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertItemActionPerformed
        udfInsertItem2();
}//GEN-LAST:event_btnInsertItemActionPerformed

    private void chkValueAdjustmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkValueAdjustmentActionPerformed
        lblCurrentValue.setVisible(chkValueAdjustment.isSelected());
        lblNewValue.setVisible(chkValueAdjustment.isSelected());
        txtCurrentValue.setVisible(chkValueAdjustment.isSelected());
        txtNewValue.setVisible(chkValueAdjustment.isSelected());

        opnameTable.getColumnExt("Hpp Skg").setVisible(chkValueAdjustment.isSelected());
        opnameTable.getColumnExt("Hpp Baru").setVisible(chkValueAdjustment.isSelected());

        if (chkValueAdjustment.isSelected() == false && txtCurrentValue.isVisible()) {
            JOptionPane.showMessageDialog(this, "Nilai akhir barang ini akan dihitung berdasarkan cost saat ini", "Information", JOptionPane.INFORMATION_MESSAGE);
            for (int i = 0; i < opnameTable.getRowCount(); i++) {
                opnameTable.setValueAt(opnameTable.getValueAt(i, opnameTable.getColumnModel().getColumnIndex("Hpp Skg")),
                        i, opnameTable.getColumnModel().getColumnIndex("Hpp Baru"));
            }
        }

    }//GEN-LAST:event_chkValueAdjustmentActionPerformed

    private void opnameTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_opnameTableKeyPressed
    }//GEN-LAST:event_opnameTableKeyPressed

    private void jXDatePicker1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker1ActionPerformed
        udfLoadOpname();
    }//GEN-LAST:event_jXDatePicker1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new StockOpname().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInsertItem;
    private javax.swing.JCheckBox chkValueAdjustment;
    private javax.swing.JComboBox cmbGudang;
    private javax.swing.JComboBox cmbUnit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private javax.swing.JLabel lblCurrentValue;
    private javax.swing.JLabel lblNewValue;
    private org.jdesktop.swingx.JXTable opnameTable;
    private javax.swing.JPanel panelAddItem;
    private javax.swing.JTextField txtBatchNo;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCurrentQty;
    private javax.swing.JTextField txtCurrentValue;
    private javax.swing.JTextField txtExpDate;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtKonv;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtNewQty;
    private javax.swing.JTextField txtNewValue;
    private javax.swing.JTextField txtNoOpname;
    // End of variables declaration//GEN-END:variables

    public class MyKeyListener extends KeyAdapter {

        double sisa = 0;

        public void keyTyped(KeyEvent e) {
            if (e.getSource().equals(txtCurrentQty) || e.getSource().equals(txtNewQty) || e.getSource().equals(txtCurrentValue) || e.getSource().equals(txtNewValue)) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9')
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_ENTER)
                        || (c == KeyEvent.VK_DELETE))) {
                    getToolkit().beep();
                    e.consume();
                }
                //udfItemSubTotal();
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch (keyKode) {
                case KeyEvent.VK_ENTER: {
                    if (!lst.isVisible()) {
                        Component c = findNextFocus();
                        if (c != null) {
                            c.requestFocus();
                        }
                    } else {
                        lst.requestFocus();
                    }
                    break;
                }

                case KeyEvent.VK_UP: {
                    if (!lst.isVisible()) {
                        Component c = findPrevFocus();
                        if (c != null) {
                            c.requestFocus();
                        }
                    } else {
                        lst.requestFocus();
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    if (!lst.isVisible()) {
                        Component c = findNextFocus();
                        if (c != null) {
                            c.requestFocus();
                        }
                    } else {
                        lst.requestFocus();
                    }
                    break;
                }

                case KeyEvent.VK_F2: {  //Bayar
                    udfStartNewItem();
                    break;
                }
                case KeyEvent.VK_F3: {  //Bayar
                    opnameTableKeyPressed(evt);
                    break;
                }
                case KeyEvent.VK_INSERT: {  //insert item
                    btnInsertItemActionPerformed(new ActionEvent(btnInsertItem, ActionEvent.ACTION_PERFORMED, "Add"));
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
    private FocusListener txtFocusListener = new FocusListener() {

        public void focusGained(FocusEvent e) {
            Component c = (Component) e.getSource();
            c.setBackground(g1);

            if (c.equals(txtNewQty) || c.equals(txtNewValue)) {
                ((JTextField) e.getSource()).setSelectionStart(0);
                ((JTextField) e.getSource()).setSelectionEnd(((JTextField) e.getSource()).getText().length());

            } else if (c.equals(txtKode) && cmbGudang.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(StockOpname.this, "Silakan pilih gudang terlebih dulu");
                //cmbGudang.requestFocus();
                return;
            }

            //c.setForeground(fPutih);
            //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }

        public void focusLost(FocusEvent e) {
            Component c = (Component) e.getSource();
            c.setBackground(g2);

            if (c.equals(txtKode) && !lst.isVisible()) {
                //txtCurrentValue.setText(fn.dFmt.format(fn.udfGetDouble(txtCurrentValue)));
                try {
                    ResultSet rs = conn.createStatement().executeQuery(
                            "select i.kode_item, i.nama_item, coalesce(i.hpp,0) as hpp, coalesce(s.batch_no,'') as batch_no," +
                            " coalesce(saldo,0) as saldo "
                            + "from r_item i "
                            + "left join r_item_stok s on s.kode_item=i.kode_item and s.kode_gudang='" + lstGudang.get(cmbGudang.getSelectedIndex()).toString() + "' "
                            + "and coalesce(to_char(exp_date, 'dd/MM/yyyy'),'')='" + txtExpDate.getText() + "' "
                            + "and coalesce(s.batch_no,'')='" + txtBatchNo.getText() + "' "
                            + "where i.kode_item='" + txtKode.getText() + "'");
                    if (rs.next()) {
                        txtNamaBarang.setText(rs.getString("nama_item"));
                        txtCurrentValue.setText(fn.dFmt.format(rs.getDouble("hpp")));
                        txtNewValue.setText(fn.dFmt.format(rs.getDouble("hpp")));
                        txtCurrentQty.setText(fn.dFmt.format(rs.getDouble("saldo")));
                        txtBatchNo.setText(rs.getString("batch_no"));
                        udfSetSatuanBarang();
                    }

                } catch (SQLException se) {
                    JOptionPane.showMessageDialog(aThis, se.getMessage());
                }
                //udfSetSatuanBarang();
            } else if (c.equals(txtExpDate)) {
                if (txtExpDate.getText().trim().equalsIgnoreCase("")) {
                    return;
                }
                if (!txtExpDate.getText().trim().equalsIgnoreCase("/  /") || !txtExpDate.getText().trim().equalsIgnoreCase("")) {
                    if (txtExpDate.getText().length() == 7) {
                        txtExpDate.setText("01/" + txtExpDate.getText());
                    }
                    if (!GeneralFunction.validateDate(txtExpDate.getText(), true, "dd/MM/yyyy")) {
                        JOptionPane.showMessageDialog(null, "Silakan masukkan tanggal dengan format 'dd/MM/yyyy' !");
                        //txtExpDate.setText("");
                        txtExpDate.requestFocus();

                        return;

                    }
                } else {
                    txtExpDate.setText("");
                }
            } else if (e.getSource().equals(txtNewQty) || e.getSource().equals(txtNewValue)) {
                ((JTextField) e.getSource()).setText(dFmt.format(GeneralFunction.udfGetDouble(((JTextField) e.getSource()).getText())));
            }
            //c.setForeground(fHitam);
        }
    };
    Color g1 = new Color(153, 255, 255);
    Color g2 = new Color(255, 255, 255);
    Color fHitam = new Color(0, 0, 0);
    Color fPutih = new Color(255, 255, 255);
    Color crtHitam = new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
}
