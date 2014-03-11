/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemPriceList.java
 *
 * Created on 14 Mei 11, 11:55:18
 */
package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmItemPriceList2 extends javax.swing.JInternalFrame {
    private Connection conn;
    private JComboBox cmbTipeHarga=new JComboBox();
    private JComboBox cmbSatuan=new JComboBox();
    private Component aThis;
    
    /** Creates new form FrmItemPriceList */
    public FrmItemPriceList2() {
        initComponents();
        cmbTipeHarga.addItem("Eceran");
        cmbTipeHarga.addItem("Grosir");
        jTable1.getColumn("Satuan").setCellEditor(new ComboBoxCellEditor(cmbSatuan));
        jTable1.getColumn("Tipe Harga").setCellEditor(new ComboBoxCellEditor(cmbTipeHarga));
        jTable1.getColumn("Jml Print").setCellRenderer(new MyRowRenderer());
        AutoCompleteDecorator.decorate(jComboBox1);
        AutoCompleteDecorator.decorate(cmbSatuan);
        AutoCompleteDecorator.decorate(cmbTipeHarga);
        jTable1.setRowHeight(20);
        
        jTable1.getColumn("Jml Print").setCellEditor(new MyTableCellEditor());
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int iRow=jTable1.getSelectedRow();
                udfLoadComboKonv(iRow);
            }
        });
    }

    private void udfLoadComboKonv(int iRow){
        
        if(iRow<0) return;
        if(jTable1.getValueAt(iRow, 0)==null ||jTable1.getValueAt(iRow, 0).toString().equalsIgnoreCase(""))
            return;

        try{
            cmbSatuan.removeAllItems();
            ResultSet rs=conn.createStatement().executeQuery("select coalesce(nama_item,'') as nama_item, " +
                         "coalesce(unit,'') as unit, " +
                         "coalesce(unit2,'') as unit2, coalesce(konv2,1) as konv2, " +
                         "coalesce(unit3,'') as unit3, coalesce(konv3,1) as konv3," +
                         "coalesce(unit_jual,'') as unit_jual, coalesce(konv_jual,1) as konv_jual " +
                         "from r_item where kode_item='"+jTable1.getValueAt(iRow, 0).toString()+"'");
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
    
    public void setConn(Connection c){
        this.conn=c;
    }
    
    private void udfInitForm(){
        aThis=this;
        try{
            ResultSet rs=conn.createStatement().executeQuery("select kategori from r_item_kategori order by 1");
            jComboBox1.removeAllItems();
            while(rs.next()){
                jComboBox1.addItem(rs.getString(1));
            }
            rs.close();
            
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
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
    GeneralFunction fn=new GeneralFunction();
    
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

           
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.setFont(tblDetail.getFont());
           text.setText(value==null? "": value.toString());

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
               text.setText(fn.intFmt.format(value));
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
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
                
           }
        }
    } ;
     
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        btnPreview = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        chkAll = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Print Price List");
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Barang", "Satuan", "Jml Print", "Print"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setName("jTable1"); // NOI18N
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 170, -1));

        jLabel1.setText("Kategori :");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 10, 70, 20));

        btnPreview.setText("Preview");
        btnPreview.setName("btnPreview"); // NOI18N
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });

        btnClose.setText("Close");
        btnClose.setName("btnClose"); // NOI18N

        chkAll.setText("Print Semua");
        chkAll.setName("chkAll"); // NOI18N
        chkAll.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkAllItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(chkAll, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(btnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(chkAll))
                    .addComponent(btnPreview)
                    .addComponent(btnClose))
                .addGap(7, 7, 7))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
        chkAll.setSelected(false);
        if(jComboBox1.getSelectedIndex()<0) return;
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select i.kode_item, i.nama_item, coalesce(r.satuan,'') as satuan, coalesce(r.harga_tunai,0) as harga_tunai, "
                    + "coalesce(i.barcode,'') as barcode "
                    + "from item i "
                    + "left join item_range r on r.kode_item=i.kode_item "
                    + "where coalesce(i.kategori,'')='"+jComboBox1.getSelectedItem().toString()+"'" );
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getString("satuan"),
                    1,
                    false
                });
            }
            if(jTable1.getRowCount()>1){
                jTable1.setRowSelectionInterval(0, 0);
                jTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jTable1, (DefaultTableModel)jTable1.getModel()).getModel());
            }
            rs.close();
            
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        try {                                           
            TableColumnModel col=jTable1.getColumnModel();
            String sTipeHarga="", sSatuan="";
            String sItem="delete from tmp_print_pricelist;\n";
            String sItemRow="";
            
            for(int i=0; i<jTable1.getRowCount(); i++){
                if((Boolean)jTable1.getValueAt(i, col.getColumnIndex("Print"))==true){
                    sTipeHarga  =jTable1.getValueAt(i, col.getColumnIndex("Tipe Harga")).toString();
                    sSatuan     =jTable1.getValueAt(i, col.getColumnIndex("Satuan")).toString();
                    sItemRow="insert into tmp_print_pricelist(kode_item, nama_item, unit, harga) "
                            + "select '"+jTable1.getValueAt(i, col.getColumnIndex("Kode")).toString()+"', "
                            + "'"+jTable1.getValueAt(i, col.getColumnIndex("Nama Barang")).toString()+"', "
                            + "'"+jTable1.getValueAt(i, col.getColumnIndex("Satuan")).toString()+"', "
                            + "case  when '"+sTipeHarga+"'='Grosir' and '"+sSatuan+"'=coalesce(unit,'') then harga_g_1 " +
                             "      when '"+sTipeHarga+"'='Grosir' and '"+sSatuan+"'=coalesce(unit2,'') then harga_g_2 " +
                             "      when '"+sTipeHarga+"'='Grosir' and '"+sSatuan+"'=coalesce(unit3,'') then harga_g_3 " +
                             "      when '"+sTipeHarga+"'='Eceran' and '"+sSatuan+"'=coalesce(unit,'') then harga_r_1 " +
                             "      when '"+sTipeHarga+"'='Eceran' and '"+sSatuan+"'=coalesce(unit2,'') then harga_r_2 " +
                             "      when '"+sTipeHarga+"'='Eceran' and '"+sSatuan+"'=coalesce(unit3,'') then harga_r_3 " +
                             "else (case when '"+sTipeHarga+"'='Grosir' then harga_g_1 else harga_r_1 end) " +
                             "end as harga "
                            + "from r_item i "
                            + "left join r_item_harga_jual h on h.kode_item=i.kode_item "
                            + "where i.kode_item='"+jTable1.getValueAt(i, col.getColumnIndex("Kode")).toString()+"';\n";
                    for(int j=1; j<=fn.udfGetInt(jTable1.getValueAt(i, 4)) ; j++){
                        sItem+=sItemRow;
                    }
                    
                }
            }
            
            //System.out.println(sItem);
            conn.createStatement().executeUpdate(sItem);
            JasperReport jasperReport = null;
            try {
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("Reports/Inv_PriceList.jasper"));
                JasperPrint print = JasperFillManager.fillReport(jasperReport, null, conn);
                print.setOrientation(jasperReport.getOrientation());
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                if (print.getPages().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan");
                    return;
                }
                JasperViewer.viewReport(print, false);

            } catch (JRException ex) {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                System.out.println(ex.getMessage());
            }
        } catch (SQLException ex) {
            Logger.getLogger(FrmItemPriceList2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnPreviewActionPerformed

    private void chkAllItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkAllItemStateChanged
        for(int i=0; i<jTable1.getRowCount(); i++){
            ((DefaultTableModel)jTable1.getModel()).setValueAt(chkAll.isSelected(), i, 5);
        }
    }//GEN-LAST:event_chkAllItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnPreview;
    private javax.swing.JCheckBox chkAll;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    private SimpleDateFormat dmy=new SimpleDateFormat("dd/MM/yyyy");
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());

            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmy.format(value);
            }if(value instanceof Timestamp ){
                value=dmy.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.intFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }
}
