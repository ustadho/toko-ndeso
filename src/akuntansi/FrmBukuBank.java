/*
 * ListAkun.java
 *
 * Created on August 23, 2008, 10:21 AM
 */

package akuntansi;

import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.border.DropShadowBorder;
import retail.MainForm;
import retail.main.GeneralFunction;


/**
 *
 * @author  oestadho
 */
public class FrmBukuBank extends javax.swing.JInternalFrame {
    //private DropShadowBorder dsb = new DropShadowBorder(UIManager.getColor("Control"), 0, 8, .5f, 12, false, true, true, true);
    DefaultTableModel myModel;
    private DropShadowBorder dsb=new DropShadowBorder();
    //private DropShadowBorder dsb = new DropShadowBorder(new Color(255,240,240), 0, 8, .5f, 12, false, true, true, true);
    
    private Connection conn;
    private JDesktopPane jDesktop;
    private ArrayList lstResort=new ArrayList();
    private String sUserName;
    private List lstAkun =new ArrayList();
    private GeneralFunction fn=new GeneralFunction();

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    
    /** Creates new form ListAkun */
    public FrmBukuBank() {
        initComponents();
        masterTable.getColumn("Tanggal").setCellRenderer(new MyRowRenderer()); //Tgl. Pinjam
        
        panelHeader.setBorder(dsb);
//        panelFilter.setBorder(dsb);
        jScrollPane1.setBorder(dsb);
//        masterTable.getColumn(1).setPreferredWidth(230);
    }

    public void setDesktopPane(JDesktopPane jDesktopPane1) {
        jDesktop =jDesktopPane1;
    }

    void setUserName(String sUserName) {
        this.sUserName=sUserName;
    }

    private void udfInitForm(){
        myModel=(DefaultTableModel)masterTable.getModel();
        masterTable.setModel(myModel);
        myModel.setNumRows(0);

        try{
            ResultSet rs=conn.createStatement().executeQuery("select current_date");
            rs.next();
            jDateChooser1.setDate(rs.getDate(1));
            jDateChooser2.setDate(rs.getDate(1));
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }

        udfFilter();
        try{
            ResultSet rs=conn.createStatement().executeQuery("select acc_no, coalesce(acc_name,'') as acc_name " +
                    "from acc_coa where acc_type='01' " +
                    "and acc_no not in(select distinct sub_acc_of " +
                    "from acc_coa where acc_type='01' and sub_acc_of is not null) order by acc_no");
            lstAkun.clear();
            jComboBox1.removeAllItems();

            while(rs.next()){
                jComboBox1.addItem(rs.getString("acc_name"));
                lstAkun.add(rs.getString("acc_no"));
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
//        masterTable.getColumn("Nama Item").setPreferredWidth(200);
       
    }
    
    private void udfFilter(){
        if(myModel==null || jDateChooser1.getDate()==null || jDateChooser2.getDate()==null) return;

        myModel.setNumRows(0);

        String s="select * from fn_acc_rpt_rekap_jurnal_harian(" +
                "'"+new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser1.getDate())+"', " +
                "'"+new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser2.getDate())+"', '"+lstAkun.get(jComboBox1.getSelectedIndex()).toString()+"') " +
                "as (acc_no varchar, acc_name varchar, tanggal date, source_no varchar, description varchar," +
                "saldo_aw double precision, debit double precision, credit double precision)";
        int iRow=1;
        double dSaldo=0;
        try {
            ResultSet rs = conn.createStatement().executeQuery(s);
            while(rs.next()){
                if(iRow==1){
                    myModel.addRow(new Object[]{
                        null, //Tanggal
                        "",    //Sumber
                        "",    //Keterangan
                        null,  //Debet
                        null,  //Credit
                        rs.getDouble("saldo_aw"),
                        ""
                    });
                    dSaldo=rs.getDouble("saldo_aw");
                }
                if(rs.getDouble("debit")>0 ||rs.getDouble("credit")>0 ){
                    dSaldo+=rs.getDouble("debit")-rs.getDouble("credit");

                    myModel.addRow(new Object[]{
                        rs.getDate("tanggal"),
                        rs.getString("source_no"),
                        rs.getString("description"),
                        rs.getDouble("debit"),
                        rs.getDouble("credit"),
                        dSaldo

                    });
                }

                iRow++;
            }
            if(myModel.getRowCount()>0) masterTable.setRowSelectionInterval(0, 0);
            //cmbTipeAkun.setSelectedIndex(-1);
            rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error from udfInitForm", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        SimpleDateFormat dmyFmt=new SimpleDateFormat("dd/MM/yyyy");
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(value instanceof Date ){
                value=dmyFmt.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                value=fn.dFmt.format(value);
            }
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setValue(value);
            return this;
        }
    }

    private boolean udfCekNumeric(String str){
        boolean flag = true;
        char[] stringArray;
        stringArray = str.toCharArray();
        for(int index=0; index < stringArray.length; index++){
//            System.out.print(stringArray[index]);
            if (!((stringArray[index] >= '0' && stringArray[index] <= '9')) &&
                  stringArray[index] != '-' && stringArray[index] != ' '){
                flag = false;
          }
        }
        return flag;
    }

    

    private void udfLoadJournalEntry(){
        FrmJournalEntry fJournal=new FrmJournalEntry();
        fJournal.setConn(conn);
        fJournal.setKoreksi(true);
        fJournal.setKode(masterTable.getValueAt(masterTable.getSelectedRow(), 2).toString());
        fJournal.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        lblUbah = new org.jdesktop.swingx.JXHyperlink();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Buku Bank"); // NOI18N
        setName("Form"); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        panelHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelHeader.setName("panelHeader"); // NOI18N

        jXHyperlink5.setMnemonic('R');
        jXHyperlink5.setName("jXHyperlink5"); // NOI18N
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink5ActionPerformed(evt);
            }
        });

        lblUbah.setMnemonic('U');
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(FrmBukuBank.class);
        lblUbah.setText(resourceMap.getString("lblUbah.text")); // NOI18N
        lblUbah.setFont(resourceMap.getFont("lblUbah.font")); // NOI18N
        lblUbah.setName("lblUbah"); // NOI18N
        lblUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lblUbahActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setName("jComboBox1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jDateChooser1.setDateFormatString(resourceMap.getString("jDateChooser1.dateFormatString")); // NOI18N
        jDateChooser1.setName("jDateChooser1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jDateChooser2.setDateFormatString(resourceMap.getString("jDateChooser2.dateFormatString")); // NOI18N
        jDateChooser2.setName("jDateChooser2"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(736, 736, 736)
                        .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblUbah, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUbah, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jDateChooser2, lblUbah});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "No. Sumber", "Keterangan", "Debet", "Kredit", "Saldo", "JrType"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        masterTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        masterTable.setName("masterTable"); // NOI18N
        masterTable.getTableHeader().setReorderingAllowed(false);
        masterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masterTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(masterTable);
        masterTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        masterTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("masterTable.columnModel.title1")); // NOI18N
        masterTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        masterTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("masterTable.columnModel.title0")); // NOI18N
        masterTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        masterTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("masterTable.columnModel.title2")); // NOI18N
        masterTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        masterTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("masterTable.columnModel.title4")); // NOI18N
        masterTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        masterTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("masterTable.columnModel.title5")); // NOI18N
        masterTable.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("masterTable.columnModel.title8")); // NOI18N
        masterTable.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("masterTable.columnModel.title7")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                    .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
    
}//GEN-LAST:event_formInternalFrameClosed

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
    udfInitForm();
}//GEN-LAST:event_jXHyperlink5ActionPerformed

private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
    if(evt.getClickCount()==2){
        udfUpdate();
    }
}//GEN-LAST:event_masterTableMouseClicked

private void lblUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lblUbahActionPerformed
    udfFilter();
}//GEN-LAST:event_lblUbahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXHyperlink lblUbah;
    private org.jdesktop.swingx.JXTable masterTable;
    private javax.swing.JPanel panelHeader;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JSlider shadowSizeSlider;

    private void udfNew() {
//        FrmItemMaster fMaster=new FrmItemMaster();
//        fMaster.setTitle("Item baru");
//        fMaster.setConn(conn);
//        fMaster.setKodeBarang("XXXXXXXXXXXXXXXXXXXXXXXXXX");
//        fMaster.setIsNew(true);
//        fMaster.setSrcTable(masterTable);
//        fMaster.setSrcModel(myModel);
//        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
//        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        fMaster.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fMaster.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
        }

    private void udfSetSaldoAwal() {
        int i=masterTable.getSelectedRow();
        if(i>=0){
//            DlgSaldoAwal d1=new DlgSaldoAwal(JOptionPane.getFrameForComponent(this), false);
//            d1.setConn(conn);
//            d1.setNoAnggota(masterTable.getValueAt(i, 0).toString());
//            d1.setTitle("Saldo awal anggota : "+masterTable.getValueAt(i, 0).toString()+" - "+masterTable.getValueAt(i, 1).toString());
//            d1.setVisible(true);
        }
    }

    private void udfUpdate() {
        int iRow= masterTable.getSelectedRow();
        if(iRow>=0){
//            FrmItemMaster fMaster=new FrmItemMaster();
//            fMaster.setTitle("Update Item / Barang");
//            //fMaster.settDesktopPane(jDesktop);
//            fMaster.setConn(conn);
//            fMaster.setSrcModel(myModel);
//            fMaster.setSrcTable(masterTable);
//            fMaster.setKodeBarang(masterTable.getValueAt(iRow, 0).toString());
//            fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
//            jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
//            fMaster.setVisible(true);
//            try{
//                //fMaster.setMaximum(true);
//                fMaster.setSelected(true);
//            } catch(PropertyVetoException PO){
//
//            }
            
        }
    }
}
