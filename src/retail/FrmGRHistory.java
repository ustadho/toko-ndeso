/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmSalesHistory.java
 *
 * Created on 09 Jan 11, 6:34:37
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
import java.awt.print.PrinterJob;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MaskFormatter;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.GeneralFunction;
import retail.main.JDesktopImage;
import retail.main.SysConfig;

/**
 *
 * @author cak-ust
 */
public class FrmGRHistory extends javax.swing.JInternalFrame {
    private Connection conn;
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private GeneralFunction fn;
    MyKeyListener kListener=new MyKeyListener();
    private JFormattedTextField jFDate1;
    private Component aThis;
    private JDesktopImage desktop;

    /** Creates new form FrmSalesHistory */
    public FrmGRHistory() {
        initComponents();
        aThis=this;
        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                int i=jXTable1.getSelectedRow();
                btnKoreksi.setEnabled(i>=0 && (Boolean)jXTable1.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Koreksi"))==false);
                btnKoreksiHeader.setEnabled(i>=0 && (Boolean)jXTable1.getValueAt(i, jXTable1.getColumnModel().getColumnIndex("Koreksi"))==false);
                btnPrintUlang.setEnabled(i>=0);
                
                if(i<0 ||conn==null) return;
                try{
                   ResultSet rs=conn.createStatement().executeQuery("select d.kode_item, coalesce(i.nama_item,'') as item_name, coalesce(d.qty,0) as qty, coalesce(d.unit,'') as unit, " +
                           "coalesce(d.unit_price,0) as harga,  coalesce(d.disc,0) as disc, coalesce(d.tax,0) as ppn, " +
                           "case when d.is_disc_rp=true then ((d.qty*d.unit_price)-coalesce(disc,0)) else ((d.qty*d.unit_price)*(1-coalesce(disc,0)/100)) end  as sub_total, " +
                           "coalesce(d.tax,0) as tax, coalesce(is_disc_rp, false) as is_disc_rp, coalesce(is_tax_rp, false) as is_tax_rp, " +
                           "coalesce(g.freight,0) as biaya_lain " +
                           "from r_gr_detail d  " +
                           "inner join r_gr g on g.no_gr=d.no_gr " +
                           "inner join r_item i on i.kode_item=d.kode_item " +
                           "where d.no_gr='"+jXTable1.getValueAt(i, 0).toString()+"' ");

                   //((d.qty*d.unit_price)*(1-coalesce(disc,0)/100))*(1+coalesce(d.tax,0)/100)
                    ((DefaultTableModel)tblItem.getModel()).setNumRows(0);
                    double extPrice=0,totLine=0, totVat=0 ;
                    while(rs.next()){
                        ((DefaultTableModel)tblItem.getModel()).addRow(new Object[]{
                            rs.getString("kode_item"),
                            rs.getString("item_name"),
                            rs.getDouble("qty"),
                            rs.getString("unit"),
                            rs.getDouble("harga"),
                            rs.getDouble("disc"),
                            rs.getDouble("ppn"),
                            rs.getDouble("sub_total")
                        });
                        txtBiayaLain.setText(fn.dFmt.format(rs.getDouble("biaya_lain")));
                        extPrice=fn.udfGetDouble(rs.getDouble("sub_total"));
                        totLine+=extPrice;
                        //totVat+=extPrice/100*fn.udfGetDouble(table.getValueAt(i, col.getColumnIndex("PPN")));
                        totVat+= rs.getBoolean("is_tax_rp")==true ? rs.getDouble("ppn"): extPrice/100*rs.getDouble("ppn");
                    }
                    txtTotalLine.setText(fn.dFmt.format(Math.floor(totLine)));
                    txtTotVat.setText(fn.dFmt.format(Math.floor(totVat)));
                    txtNetto.setText(fn.dFmt.format(Math.floor(totVat)+Math.floor(totLine)));

                    if(tblItem.getRowCount()>0){
                        tblItem.setRowSelectionInterval(0, 0);
                        tblItem.setModel((DefaultTableModel)fn.autoResizeColWidth(tblItem, (DefaultTableModel)tblItem.getModel()).getModel());
                    }

                }catch(SQLException se){
                    JOptionPane.showMessageDialog(aThis, se.getMessage());
                }
            }
        });
    }

    public void setConn(Connection con){
        this.conn=con;
    }

    private void udfInitForm(){
        fn=new GeneralFunction(conn);
        fn.addKeyListenerInContainer(jPanel3, kListener, txtFocusListener);
        jXTable1.getColumnModel().getColumn(1).setCellRenderer(new MyRowRenderer());
        jXTable1.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));

        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        jFDate1 = new JFormattedTextField(fmttgl);
        jFTgl.setFormatterFactory(jFDate1.getFormatterFactory());
        jFTglAkhir.setFormatterFactory(jFDate1.getFormatterFactory());

        
        try{
            ResultSet rs = conn.createStatement().executeQuery("select '01/'||to_char(current_date, 'MM/yyyy') as tgl1,  to_char(current_date, 'dd/MM/yyyy') as tgl2 ");
            if(rs.next()){
                jFTgl.setText(rs.getString(2));
                jFTgl.setValue(rs.getString(2));
                jFTglAkhir.setText(rs.getString(2));
                jFTglAkhir.setValue(rs.getString(2));
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        udfFilter();
    }

    private void udfFilter(){
        try{
            String sQry="select gr.no_gr, tanggal, coalesce(no_receipt,'') as no_receipt, coalesce(s.nama_supp,'') as nama_supp, " +
                    "coalesce(gr.no_po,'') as no_po, sum(" +
                    "(case when d.is_disc_rp then d.qty*d.unit_price -coalesce(disc,0) else ((d.qty*d.unit_price)*(1-coalesce(disc,0)/100)) end) " +
                    "+(case when d.is_tax_rp=true then coalesce(d.tax,0) else " +
                    "(case when d.is_disc_rp then d.qty*d.unit_price -coalesce(disc,0) else ((d.qty*d.unit_price)*(1-coalesce(disc,0)/100)) end)*(coalesce(d.tax,0)/100) end))+coalesce(gr.freight,0)   as total," +
                    "coalesce(gr.koreksi, false) as koreksi " +
                    "from r_gr gr " +
                    "inner join r_gr_detail d on d.no_gr=gr.no_gr " +
                    "inner join r_item i on i.kode_item=d.kode_item " +
                    "left join r_supplier s on s.kode_supp=gr.kode_supp " +
                    "where (gr.no_gr||coalesce(no_receipt,'')||coalesce(s.nama_supp,'')||coalesce(gr.no_po,'') ilike '%"+txtItem.getText()+"%' or  " +
                    " d.kode_item||coalesce(i.nama_item,'') ilike '%"+txtItem.getText()+"%') " +
                    "and to_char(gr.tanggal, 'yyyy-MM-dd')>='" + ymd.format(dmy.parse(jFTgl.getText())) + "' " +
                    "and to_char(gr.tanggal, 'yyyy-MM-dd')<='"+ymd.format(dmy.parse(jFTglAkhir.getText())) + "' "+
                    "group by gr.no_gr, tanggal, coalesce(no_receipt,''), coalesce(s.nama_supp,''),coalesce(gr.no_po,''), coalesce(gr.freight,0), gr.koreksi " +
                    "order by tanggal, coalesce(s.nama_supp,'')";
            
            ResultSet rs=conn.createStatement().executeQuery(sQry);

            System.out.println(sQry);
            ((DefaultTableModel)jXTable1.getModel()).setNumRows(0);
            while(rs.next()){
                ((DefaultTableModel)jXTable1.getModel()).addRow(new Object[]{
                    rs.getString("no_gr"),
                    rs.getDate("tanggal"),
                    rs.getString("no_receipt"),
                    rs.getString("nama_supp"),
                    rs.getString("no_po"),
                    rs.getDouble("total"),
                    rs.getBoolean("koreksi")
                });
            }
            if(jXTable1.getRowCount()>0){
                jXTable1.setRowSelectionInterval(0, 0);
                jXTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jXTable1, (DefaultTableModel)jXTable1.getModel()).getModel());
            }
            
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }catch(ParseException pe){
            JOptionPane.showMessageDialog(this, pe.getMessage());
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jFTgl.requestFocus();
            }
        });
    }

    private void printKwitansi(){
//        int iRow=jXTable1.getSelectedRow();
//        if(iRow < 0) return;
//        String sNo=jXTable1.getValueAt(iRow, 0).toString();
//
//        PrinterJob job = PrinterJob.getPrinterJob();
//        SysConfig sy=new SysConfig();
//
//        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        PrintServiceAttributeSet pset = new HashPrintServiceAttributeSet();
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
//        int i=0;
//        for(i=0;i<services.length;i++){
//            if(services[i].getName().equalsIgnoreCase(sy.getPrintKwtName())){
//                break;
//            }
//        }
//        //if (JOptionPane.showConfirmDialog(null,"Cetak Invoice?","Message",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
//        try{
//            PrintPenjualan pn = new PrintPenjualan(conn, sNo, MainForm.sUserName,services[i]);
//        }catch(java.lang.ArrayIndexOutOfBoundsException ie){
//            JOptionPane.showMessageDialog(this, "Printer '"+sy.getPrintKwtName()+"' tidak ditemukan!");
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

        jPanel3 = new javax.swing.JPanel();
        btnFilter1 = new javax.swing.JButton();
        jFTgl = new javax.swing.JFormattedTextField();
        txtItem = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jFTglAkhir = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        lblTotal2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnPrintUlang = new javax.swing.JButton();
        btnKoreksi = new javax.swing.JButton();
        btnKoreksiHeader = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        txtTotalLine = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtTotVat = new javax.swing.JLabel();
        txtNetto = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtBiayaLain = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("History Penerimaan/ Pembelian");
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

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnFilter1.setText("Cari");
        btnFilter1.setToolTipText("Fiter");
        btnFilter1.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter1.setName("btnFilter1"); // NOI18N
        btnFilter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter1ActionPerformed(evt);
            }
        });
        jPanel3.add(btnFilter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 20, 70, 25));

        jFTgl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTgl.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFTgl.setName("jFTgl"); // NOI18N
        jPanel3.add(jFTgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 80, 20));

        txtItem.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        txtItem.setName("txtItem"); // NOI18N
        jPanel3.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 25, 130, 20));

        jLabel2.setBackground(new java.awt.Color(204, 204, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Pencarian");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setOpaque(true);
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 130, -1));

        jFTglAkhir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 153), 1, true));
        jFTglAkhir.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFTglAkhir.setName("jFTglAkhir"); // NOI18N
        jPanel3.add(jFTglAkhir, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 25, 80, 20));

        jLabel16.setBackground(new java.awt.Color(204, 204, 255));
        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Dari Tgl.");
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setOpaque(true);
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 80, -1));

        jLabel20.setBackground(new java.awt.Color(204, 204, 255));
        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Sampai Tgl.");
        jLabel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setOpaque(true);
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 80, -1));

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Trans.", "Tanggal", "No. Inv", "Supplier", "No. PO", "Total", "Koreksi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Boolean.class
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
        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jXTable1.setName("jXTable1"); // NOI18N
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jXTable1);

        lblTotal2.setBackground(new java.awt.Color(0, 0, 102));
        lblTotal2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTotal2.setForeground(new java.awt.Color(255, 255, 255));
        lblTotal2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal2.setText("Detail Barang");
        lblTotal2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal2.setName("lblTotal2"); // NOI18N
        lblTotal2.setOpaque(true);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Qty", "Satuan", "Harga", "Disc", "PPN", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItem.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblItem.setName("tblItem"); // NOI18N
        tblItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblItem);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPrintUlang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/print.png"))); // NOI18N
        btnPrintUlang.setText("Print");
        btnPrintUlang.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrintUlang.setName("btnPrintUlang"); // NOI18N
        btnPrintUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintUlangActionPerformed(evt);
            }
        });
        jPanel4.add(btnPrintUlang, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 100, 40));

        btnKoreksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Edit.png"))); // NOI18N
        btnKoreksi.setText("Koreksi");
        btnKoreksi.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnKoreksi.setName("btnKoreksi"); // NOI18N
        btnKoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKoreksiActionPerformed(evt);
            }
        });
        jPanel4.add(btnKoreksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, 40));

        btnKoreksiHeader.setText("Koreksi Header");
        btnKoreksiHeader.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnKoreksiHeader.setName("btnKoreksiHeader"); // NOI18N
        btnKoreksiHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKoreksiHeaderActionPerformed(evt);
            }
        });
        jPanel4.add(btnKoreksiHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, 40));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Netto");
        jLabel31.setName("jLabel31"); // NOI18N
        jPanel5.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 90, 20));

        txtTotalLine.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotalLine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotalLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotalLine.setName("txtTotalLine"); // NOI18N
        txtTotalLine.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotalLinePropertyChange(evt);
            }
        });
        jPanel5.add(txtTotalLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 120, 20));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Line Total :");
        jLabel26.setName("jLabel26"); // NOI18N
        jPanel5.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 90, 20));

        txtTotVat.setFont(new java.awt.Font("Dialog", 1, 12));
        txtTotVat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtTotVat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtTotVat.setName("txtTotVat"); // NOI18N
        txtTotVat.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtTotVatPropertyChange(evt);
            }
        });
        jPanel5.add(txtTotVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 120, 20));

        txtNetto.setFont(new java.awt.Font("Dialog", 1, 12));
        txtNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtNetto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtNetto.setName("txtNetto"); // NOI18N
        txtNetto.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtNettoPropertyChange(evt);
            }
        });
        jPanel5.add(txtNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 70, 120, 20));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("V.A.T");
        jLabel30.setName("jLabel30"); // NOI18N
        jPanel5.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 90, 20));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Biaya Lain");
        jLabel32.setName("jLabel32"); // NOI18N
        jPanel5.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 80, 20));

        txtBiayaLain.setFont(new java.awt.Font("Dialog", 1, 12));
        txtBiayaLain.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBiayaLain.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        txtBiayaLain.setDisabledTextColor(new java.awt.Color(153, 153, 153));
        txtBiayaLain.setName("txtBiayaLain"); // NOI18N
        txtBiayaLain.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBiayaLainFocusLost(evt);
            }
        });
        txtBiayaLain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBiayaLainKeyReleased(evt);
            }
        });
        jPanel5.add(txtBiayaLain, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 120, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblTotal2, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addGap(9, 9, 9))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)))
                .addGap(8, 8, 8))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1055)/2, (screenSize.height-499)/2, 1055, 499);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFilter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter1ActionPerformed
        udfFilter();
}//GEN-LAST:event_btnFilter1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnPrintUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintUlangActionPerformed
        printKwitansi();
    }//GEN-LAST:event_btnPrintUlangActionPerformed

    private void txtTotalLinePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotalLinePropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotalLinePropertyChange

    private void txtTotVatPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtTotVatPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtTotVatPropertyChange

    private void txtNettoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtNettoPropertyChange
        // TODO add your handling code here:
}//GEN-LAST:event_txtNettoPropertyChange

    private void txtBiayaLainFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBiayaLainFocusLost
        // TODO add your handling code here:
}//GEN-LAST:event_txtBiayaLainFocusLost

    private void txtBiayaLainKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBiayaLainKeyReleased
        // TODO add your handling code here:
}//GEN-LAST:event_txtBiayaLainKeyReleased

    private void btnKoreksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKoreksiActionPerformed
        udfKoreksi();
    }//GEN-LAST:event_btnKoreksiActionPerformed

    private void btnKoreksiHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKoreksiHeaderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKoreksiHeaderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFilter1;
    private javax.swing.JButton btnKoreksi;
    private javax.swing.JButton btnKoreksiHeader;
    private javax.swing.JButton btnPrintUlang;
    private javax.swing.JFormattedTextField jFTgl;
    private javax.swing.JFormattedTextField jFTglAkhir;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JTable tblItem;
    private javax.swing.JTextField txtBiayaLain;
    private javax.swing.JTextField txtItem;
    private javax.swing.JLabel txtNetto;
    private javax.swing.JLabel txtTotVat;
    private javax.swing.JLabel txtTotalLine;
    // End of variables declaration//GEN-END:variables

    private void udfKoreksi() {
        int iRow=jXTable1.getSelectedRow();
        if(iRow<0) return;
        String sNoKoreksi=jXTable1.getValueAt(iRow, 0).toString();
        FrmGRBeli f1=new FrmGRBeli();
        f1.setConn(conn);
        f1.setKoreksi(true);
        f1.setNoTrx(sNoKoreksi);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        desktop.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    public void setDesktopPane(JDesktopImage jDesktopPane1) {
        this.desktop=jDesktopPane1;
    }
    // End of variables declaration

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

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);

            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);


           }
        }
    } ;

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
                value=fn.dFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }

}
