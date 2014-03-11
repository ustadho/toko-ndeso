/*
 * ListAkun.java
 *
 * Created on August 23, 2008, 10:21 AM
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.JDesktopPane;
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
import org.jdesktop.swingx.border.DropShadowBorder;
import retail.main.GeneralFunction;


/**
 *
 * @author  oestadho
 */
public class FrmItemList1 extends javax.swing.JInternalFrame {
    //private DropShadowBorder dsb = new DropShadowBorder(UIManager.getColor("Control"), 0, 8, .5f, 12, false, true, true, true);
    DefaultTableModel myModel;
    private DropShadowBorder dsb=new DropShadowBorder();
    private List lstHargaJual=new ArrayList();
    private List lstKonvSat=new ArrayList();
    //private DropShadowBorder dsb = new DropShadowBorder(new Color(255,240,240), 0, 8, .5f, 12, false, true, true, true);
    
    private Connection conn;
    private JDesktopPane jDesktop;
    private ArrayList lstResort=new ArrayList();
    private String sUserName;
    GeneralFunction fn=new GeneralFunction();
    
    public void setConn(Connection conn) {
        this.conn = conn;
        tblHargaJual.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if(e.getType()==TableModelEvent.UPDATE){
                    if(tblHargaJual.getValueAt(tblHargaJual.getSelectedRow(), tblHargaJual.getColumnModel().getColumnIndex("Satuan")).toString().equalsIgnoreCase(cmbSatJual.getSelectedItem().toString())){
                        txtHargaJual.setText(fn.dFmt.format(fn.udfGetDouble(tblHargaJual.getValueAt(tblHargaJual.getSelectedRow(), 1))));
                        lstHargaJual.set(cmbSatJual.getSelectedIndex(), fn.udfGetDouble(tblHargaJual.getValueAt(tblHargaJual.getSelectedRow(), 1)));
                    }
                }
            }
        });
    }
    
    
    /** Creates new form ListAkun */
    public FrmItemList1() {
        initComponents();
        tblHargaJual.getColumnModel().getColumn(1).setCellEditor(new MyTableCellEditor());

        masterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                udfLoadSatuanJual();
            }
        });
        panelHeader.setBorder(dsb);
        panelFilter.setBorder(dsb);
        jScrollPane1.setBorder(dsb);
        masterTable.getColumn(1).setPreferredWidth(230);

    }

    public void setDesktopPane(JDesktopPane jDesktopPane1) {
        jDesktop =jDesktopPane1;
    }

    void setUserName(String sUserName) {
        this.sUserName=sUserName;
    }

    private void udfInitForm(){
        masterTable.getColumn("Nama Item").setPreferredWidth(200);
        tblHargaJual.setRowHeight(20);
        udfFilter();


    }
    
    private void udfFilter(){
        if(conn==null) return;
        
        ((DefaultTableModel)masterTable.getModel()).setNumRows(0);
        String sTipe="";
        sTipe+=(chkInv.isSelected()? (sTipe.length()>0? ",": "")+"'I'":"");
        sTipe+=(chkNonInv.isSelected()? (sTipe.length()>0? ",": "")+"'N'":"");
        sTipe+=(chkService.isSelected()? (sTipe.length()>0? ",": "")+"'S'":"");
        sTipe+=(chkGroup.isSelected()? (sTipe.length()>0? ",": "")+"'G'":"");
        sTipe+=(sTipe.length()>0? ",": "")+"''";

        String s="select kode_item, nama_item, coalesce(stock,0) as qty, coalesce(harga_jual,0) as price," +
                 "  case 	when tipe='I' then 'Inventory' " +
                 "	when tipe='G' then 'Group' " +
                 "	when tipe='S' then 'Service' " +
                 "	else 'Non Inventory' end as type " +
                 "from r_item " +
                 "where (kode_item) ilike '%"+txtKode.getText()+"%' and coalesce(nama_item,'') iLike '%"+txtNama.getText()+"%' " +
                 "and tipe in("+sTipe+") " +
                 (chkViewMoreZeroStock.isSelected()? "and stock>0 ": " ") +
                 (cmbAktif.getSelectedIndex()==0? "": "and active="+(cmbAktif.getSelectedIndex()==1? " true ":" false "))+
                 "Order by kode_item";
        
        //System.out.println(s);
        
        try {
//            ResultSet rs = conn.createStatement().executeQuery("select * from agama where " +
//                    "kdagama Ilike '%"+txtKode.getText()+"%' and desagama iLike '%"+txtNama.getText()+"%'");
            
            ResultSet rs = conn.createStatement().executeQuery(s);
            while(rs.next()){
                ((DefaultTableModel)masterTable.getModel()).addRow(new Object[]{
                    rs.getString("kode_item"),
                    rs.getString("nama_item"),
                    rs.getDouble("qty"),
                    rs.getDouble("price"),
                    rs.getString("type")
                });
            }
            if(masterTable.getRowCount()>0) masterTable.setRowSelectionInterval(0, 0);
            //cmbTipeAkun.setSelectedIndex(-1);
            rs.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error from udfInitForm", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    
//    private void redraw() {
//        DropShadowBorder old = dsb;
//        dsb = new DropShadowBorder(dsb.getLineColor(),
//                dsb.getLineWidth(), 8,
//                dsb.getShadowOpacity(), dsb.getCornerSize(),true, false, false, true);
////                topShadowCB.isSelected(), leftShadowCB.isSelected(),
////                bottomShadowCB.isSelected(), rightShadowCB.isSelected());
//        
//        //iterate down the containment heirarchy, replacing any old dsb's with
//        //the new one
//        replaceBorder(old, this);
//        repaint();
//    }
//    
//    private void replaceBorder(DropShadowBorder old, JComponent c) {
//        if (c.getBorder() == old) {
//            c.setBorder(dsb);
//        }
//        
//        for (Component child : c.getComponents()) {
//            if (child instanceof JComponent) {
//                replaceBorder(old, (JComponent)child);
//            }
//        }
//    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        hiperLinkNew = new org.jdesktop.swingx.JXHyperlink();
        hiperLinkUpd = new org.jdesktop.swingx.JXHyperlink();
        hiperLinkDel = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jPanel2 = new javax.swing.JPanel();
        txtKode = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        chkFilter = new javax.swing.JCheckBox();
        btnPrint = new javax.swing.JButton();
        panelFilter = new org.jdesktop.swingx.JXTitledPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbAktif = new javax.swing.JComboBox();
        chkInv = new javax.swing.JCheckBox();
        chkNonInv = new javax.swing.JCheckBox();
        chkService = new javax.swing.JCheckBox();
        chkGroup = new javax.swing.JCheckBox();
        chkViewMoreZeroStock = new javax.swing.JCheckBox();
        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHargaJual = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtHargaJual = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        cmbSatJual = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        masterTable = new org.jdesktop.swingx.JXTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Item/ Barang"); // NOI18N
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

        panelHeader.setName("panelHeader"); // NOI18N

        hiperLinkNew.setMnemonic('B');
        hiperLinkNew.setText("Baru"); // NOI18N
        hiperLinkNew.setFont(new java.awt.Font("Tahoma 11", 1, 12));
        hiperLinkNew.setName("hiperLinkNew"); // NOI18N
        hiperLinkNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkNewActionPerformed(evt);
            }
        });

        hiperLinkUpd.setMnemonic('U');
        hiperLinkUpd.setText("Ubah"); // NOI18N
        hiperLinkUpd.setFont(new java.awt.Font("Tahoma 11", 1, 12));
        hiperLinkUpd.setName("hiperLinkUpd"); // NOI18N
        hiperLinkUpd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkUpdActionPerformed(evt);
            }
        });

        hiperLinkDel.setMnemonic('H');
        hiperLinkDel.setText("Hapus"); // NOI18N
        hiperLinkDel.setFont(new java.awt.Font("Tahoma", 1, 11));
        hiperLinkDel.setName("hiperLinkDel"); // NOI18N
        hiperLinkDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hiperLinkDelActionPerformed(evt);
            }
        });

        jXHyperlink5.setMnemonic('R');
        jXHyperlink5.setName("jXHyperlink5"); // NOI18N
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink5ActionPerformed(evt);
            }
        });

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKode.setName("txtKode"); // NOI18N
        txtKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKodeKeyReleased(evt);
            }
        });
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 89, -1));

        txtNama.setName("txtNama"); // NOI18N
        txtNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNamaKeyReleased(evt);
            }
        });
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 262, -1));

        jLabel3.setText("Deskripsi Barang"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 0, 260, 20));

        jLabel4.setText("Kode"); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 90, 20));

        chkFilter.setSelected(true);
        chkFilter.setText("Filter"); // NOI18N
        chkFilter.setName("chkFilter"); // NOI18N
        chkFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/retail/image/Icon/print_kecil.png"))); // NOI18N
        btnPrint.setName("btnPrint"); // NOI18N

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(hiperLinkUpd, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hiperLinkDel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(hiperLinkNew, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addComponent(chkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {hiperLinkDel, hiperLinkNew, hiperLinkUpd});

        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hiperLinkDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hiperLinkUpd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hiperLinkNew, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jXHyperlink5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelHeaderLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(btnPrint)
                .addGap(37, 37, 37))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, panelHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(chkFilter)
                .addGap(38, 38, 38))
        );

        panelHeaderLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnPrint, hiperLinkDel, hiperLinkNew, hiperLinkUpd, jXHyperlink5});

        panelFilter.setTitle("Filter"); // NOI18N
        panelFilter.setName("panelFilter"); // NOI18N
        panelFilter.getContentContainer().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Item Type"); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        panelFilter.getContentContainer().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 136, 19));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Status Aktif"); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        panelFilter.getContentContainer().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 136, -1));

        cmbAktif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Ya", "Tidak" }));
        cmbAktif.setName("cmbAktif"); // NOI18N
        cmbAktif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAktifActionPerformed(evt);
            }
        });
        panelFilter.getContentContainer().add(cmbAktif, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 136, -1));

        chkInv.setSelected(true);
        chkInv.setText("Inventory"); // NOI18N
        chkInv.setName("chkInv"); // NOI18N
        chkInv.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkInvItemStateChanged(evt);
            }
        });
        panelFilter.getContentContainer().add(chkInv, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 21, 136, 20));

        chkNonInv.setSelected(true);
        chkNonInv.setText("Non Inventory Part"); // NOI18N
        chkNonInv.setName("chkNonInv"); // NOI18N
        chkNonInv.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkNonInvItemStateChanged(evt);
            }
        });
        panelFilter.getContentContainer().add(chkNonInv, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 44, 136, 20));

        chkService.setSelected(true);
        chkService.setText("Service (Jasa)"); // NOI18N
        chkService.setName("chkService"); // NOI18N
        chkService.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkServiceItemStateChanged(evt);
            }
        });
        panelFilter.getContentContainer().add(chkService, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 67, 136, -1));

        chkGroup.setSelected(true);
        chkGroup.setText("Grouping"); // NOI18N
        chkGroup.setName("chkGroup"); // NOI18N
        chkGroup.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkGroupItemStateChanged(evt);
            }
        });
        panelFilter.getContentContainer().add(chkGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 93, 136, -1));

        chkViewMoreZeroStock.setText("Tampilkan stok > 0"); // NOI18N
        chkViewMoreZeroStock.setName("chkViewMoreZeroStock"); // NOI18N
        panelFilter.getContentContainer().add(chkViewMoreZeroStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 207, 136, -1));

        jXTitledPanel1.setTitle("Informasi Penjualan"); // NOI18N
        jXTitledPanel1.setName("jXTitledPanel1"); // NOI18N
        jXTitledPanel1.getContentContainer().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblHargaJual.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Satuan", "Hrg. Jual"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHargaJual.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblHargaJual.setName("tblHargaJual"); // NOI18N
        tblHargaJual.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblHargaJual);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(FrmItemList1.class);
        tblHargaJual.getColumnModel().getColumn(0).setMinWidth(70);
        tblHargaJual.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblHargaJual.getColumnModel().getColumn(0).setMaxWidth(70);
        tblHargaJual.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblHargaJual.columnModel.title0")); // NOI18N
        tblHargaJual.getColumnModel().getColumn(1).setMinWidth(70);
        tblHargaJual.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblHargaJual.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblHargaJual.columnModel.title1")); // NOI18N

        jXTitledPanel1.getContentContainer().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 150, 100));

        jLabel5.setBackground(new java.awt.Color(204, 204, 204));
        jLabel5.setText(" Satuan Default Penjualan :"); // NOI18N
        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setOpaque(true);
        jXTitledPanel1.getContentContainer().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 145, 20));

        jLabel6.setBackground(new java.awt.Color(204, 204, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Harga Jual"); // NOI18N
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setOpaque(true);
        jXTitledPanel1.getContentContainer().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 75, 20));

        txtHargaJual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHargaJual.setName("txtHargaJual"); // NOI18N
        txtHargaJual.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHargaJualKeyTyped(evt);
            }
        });
        jXTitledPanel1.getContentContainer().add(txtHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 150, 75, -1));

        jLabel7.setBackground(resourceMap.getColor("jLabel7.background")); // NOI18N
        jLabel7.setText(" Satuan"); // NOI18N
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setOpaque(true);
        jXTitledPanel1.getContentContainer().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 70, 20));

        jButton1.setText("Update"); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jXTitledPanel1.getContentContainer().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 180, 80, -1));

        cmbSatJual.setName("cmbSatJual"); // NOI18N
        cmbSatJual.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSatJualItemStateChanged(evt);
            }
        });
        jXTitledPanel1.getContentContainer().add(cmbSatJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 70, 20));

        panelFilter.getContentContainer().add(jXTitledPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 250, 150, 260));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        masterTable.setAutoCreateRowSorter(true);
        masterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "Nama Item", "Qty", "Harga", "Tipe"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
        masterTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                masterTableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(masterTable);
        masterTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("masterTable.columnModel.title0")); // NOI18N
        masterTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("masterTable.columnModel.title1")); // NOI18N
        masterTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("masterTable.columnModel.title2")); // NOI18N
        masterTable.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("masterTable.columnModel.title3")); // NOI18N
        masterTable.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("masterTable.columnModel.title4")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHeader, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
   

    //this.dispose();
}//GEN-LAST:event_formInternalFrameClosed

private void hiperLinkNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkNewActionPerformed
    udfNew();
    
    
}//GEN-LAST:event_hiperLinkNewActionPerformed

private void chkFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFilterActionPerformed
    panelFilter.setVisible(chkFilter.isSelected());
}//GEN-LAST:event_chkFilterActionPerformed

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfInitForm();
}//GEN-LAST:event_formInternalFrameOpened

private void hiperLinkDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkDelActionPerformed
    udfDelete();
}//GEN-LAST:event_hiperLinkDelActionPerformed

private void hiperLinkUpdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hiperLinkUpdActionPerformed
    udfUpdate();
            
}//GEN-LAST:event_hiperLinkUpdActionPerformed

private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
    udfInitForm();
}//GEN-LAST:event_jXHyperlink5ActionPerformed

private void txtKodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKodeKeyReleased
    udfFilter();
}//GEN-LAST:event_txtKodeKeyReleased

private void txtNamaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNamaKeyReleased
    udfFilter();
}//GEN-LAST:event_txtNamaKeyReleased

private void cmbAktifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAktifActionPerformed
    if(conn!=null  && myModel!=null) udfFilter();
}//GEN-LAST:event_cmbAktifActionPerformed

private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
    if(evt.getClickCount()==2){
        udfUpdate();
    }
}//GEN-LAST:event_masterTableMouseClicked

private void cmbSatJualItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSatJualItemStateChanged
    if(cmbSatJual.getSelectedIndex()<0 && lstHargaJual.isEmpty()) return;
        txtHargaJual.setText(fn.dFmt.format(lstHargaJual.get(cmbSatJual.getSelectedIndex())));

}//GEN-LAST:event_cmbSatJualItemStateChanged

private void txtHargaJualKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHargaJualKeyTyped
    GeneralFunction.keyTyped(evt);
}//GEN-LAST:event_txtHargaJualKeyTyped

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    udfUpdateHargaSatuan();
}//GEN-LAST:event_jButton1ActionPerformed

private void chkInvItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkInvItemStateChanged
    udfFilter();
}//GEN-LAST:event_chkInvItemStateChanged

private void chkNonInvItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkNonInvItemStateChanged
    udfFilter();
}//GEN-LAST:event_chkNonInvItemStateChanged

private void chkServiceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkServiceItemStateChanged
    udfFilter();
}//GEN-LAST:event_chkServiceItemStateChanged

private void chkGroupItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkGroupItemStateChanged
    udfFilter();
}//GEN-LAST:event_chkGroupItemStateChanged

private void masterTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_masterTableKeyPressed
    if(masterTable.getSelectedRow()>=0 && evt.getKeyCode()==KeyEvent.VK_DELETE){
        udfDelete();
    }
}//GEN-LAST:event_masterTableKeyPressed

private void udfLoadSatuanJual() {
    if(masterTable.getSelectedRow()<0) return;
    String sKodeItem=masterTable.getValueAt(masterTable.getSelectedRow(), 0).toString();

    String s="select " +
                "coalesce(unit,'') as unit1, coalesce(harga_jual,0) as harga_jual1, " +
                "coalesce(unit2,'') as unit2, coalesce(harga_jual2,0) as harga_jual2, coalesce(konv2,1) as konv2, " +
                "coalesce(unit3,'') as unit3, coalesce(harga_jual3,0) as harga_jual3, coalesce(konv3,1) as konv3," +
                "coalesce(unit_jual,'') as unit_jual, coalesce(harga_jual_default,0) as harga_jual_default, coalesce(konv_jual,1) as konv_jual " +
                "from r_item where kode_item='"+sKodeItem+"'";
    try{
        ResultSet rs=conn.createStatement().executeQuery(s);
        ((DefaultTableModel)tblHargaJual.getModel()).setNumRows(0);
        lstHargaJual.clear();
        cmbSatJual.removeAllItems();
        
        if(rs.next()){
            lstHargaJual.add(rs.getDouble("harga_jual1"));
            lstKonvSat.add(1);
            cmbSatJual.addItem(rs.getString("unit1"));
            ((DefaultTableModel)tblHargaJual.getModel()).addRow(new Object[]{rs.getString("unit1"), rs.getDouble("harga_jual1")});
            if(rs.getString("unit2").length()>0 || rs.getDouble("harga_jual2")>0){
                lstHargaJual.add(rs.getDouble("harga_jual2"));
                lstKonvSat.add(rs.getInt("konv2"));
                cmbSatJual.addItem(rs.getString("unit2"));
                ((DefaultTableModel)tblHargaJual.getModel()).addRow(new Object[]{rs.getString("unit2"), rs.getDouble("harga_jual2")});
            }
            if(rs.getString("unit3").length()>0 || rs.getDouble("harga_jual3")>0){
                lstHargaJual.add(rs.getDouble("harga_jual3"));
                lstKonvSat.add(rs.getInt("konv3"));
                cmbSatJual.addItem(rs.getString("unit3"));
                ((DefaultTableModel)tblHargaJual.getModel()).addRow(new Object[]{rs.getString("unit3"), rs.getDouble("harga_jual3")});
            }

            cmbSatJual.setSelectedItem(rs.getString("unit_jual"));
            txtHargaJual.setText(new DecimalFormat("#,##0").format(rs.getDouble("harga_jual_default")));
        }
    }catch(SQLException se){

    }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JCheckBox chkGroup;
    private javax.swing.JCheckBox chkInv;
    private javax.swing.JCheckBox chkNonInv;
    private javax.swing.JCheckBox chkService;
    private javax.swing.JCheckBox chkViewMoreZeroStock;
    private javax.swing.JComboBox cmbAktif;
    private javax.swing.JComboBox cmbSatJual;
    private org.jdesktop.swingx.JXHyperlink hiperLinkDel;
    private org.jdesktop.swingx.JXHyperlink hiperLinkNew;
    private org.jdesktop.swingx.JXHyperlink hiperLinkUpd;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private org.jdesktop.swingx.JXTable masterTable;
    private org.jdesktop.swingx.JXTitledPanel panelFilter;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JTable tblHargaJual;
    private javax.swing.JTextField txtHargaJual;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JSlider shadowSizeSlider;

    private void udfNew() {
        FrmItemMaster fMaster=new FrmItemMaster();
        fMaster.setTitle("Item baru");
        fMaster.setConn(conn);
        fMaster.setKodeBarang("");
        fMaster.setIsNew(true);
        fMaster.setSrcTable(masterTable);
        fMaster.setSrcModel(myModel);
        fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
        jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fMaster.setVisible(true);
        try{
            //fMaster.setMaximum(true);
            fMaster.setSelected(true);
        } catch(PropertyVetoException PO){

        }
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
            FrmItemMaster fMaster=new FrmItemMaster();
            fMaster.setTitle("Update Item / Barang");
            //fMaster.settDesktopPane(jDesktop);
            fMaster.setConn(conn);
            fMaster.setSrcModel(myModel);
            fMaster.setSrcTable(masterTable);
            fMaster.setIsNew(false);
            fMaster.setKodeBarang(masterTable.getValueAt(iRow, masterTable.getColumnModel().getColumnIndex("Kode")).toString());
            fMaster.setBounds(0, 0, fMaster.getWidth(), fMaster.getHeight());
            jDesktop.add(fMaster, javax.swing.JLayeredPane.DEFAULT_LAYER);
            fMaster.setVisible(true);
            try{
                //fMaster.setMaximum(true);
                fMaster.setSelected(true);
            } catch(PropertyVetoException PO){

            }
            
        }
    }

    private void udfUpdateHargaSatuan() {
        String s1="", s2="", s3="", sUnit="";
        for(int i=0; i<tblHargaJual.getRowCount(); i++){
            if(i==0){
                s1="harga_jual="+GeneralFunction.udfGetDouble(tblHargaJual.getValueAt(i, tblHargaJual.getColumnModel().getColumnIndex("Hrg. Jual")));
                sUnit=s1;
            }   
            else if(i==1){
                s2="harga_jual2="+GeneralFunction.udfGetDouble(tblHargaJual.getValueAt(i, tblHargaJual.getColumnModel().getColumnIndex("Hrg. Jual")));
                sUnit+=(sUnit.length()>0? ",": "")+s2;
            }   
            else if(i==2){
                s3="harga_jual3="+GeneralFunction.udfGetDouble(tblHargaJual.getValueAt(i, tblHargaJual.getColumnModel().getColumnIndex("Hrg. Jual")));
                sUnit+=(sUnit.length()>0? ",": "")+s3;
            }
        }
        String sUpd="update r_item set "+sUnit+", " +
                "unit_jual='"+cmbSatJual.getSelectedItem().toString()+"', " +
                "konv_jual="+lstKonvSat.get(cmbSatJual.getSelectedIndex()).toString()+", " +
                "harga_jual_default="+GeneralFunction.udfGetDouble(txtHargaJual.getText())+" " +
                "where kode_item='"+
                masterTable.getValueAt(masterTable.getSelectedRow(), masterTable.getColumnModel().getColumnIndex("Kode")).toString()+"' ;" ;

        System.out.println(sUpd);
        try{
            int iUPd=conn.createStatement().executeUpdate(sUpd);
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
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

                            if(iRow>=myModel.getRowCount() && ((DefaultTableModel)masterTable.getModel()).getRowCount()>0){
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

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=ustTextField;

        int col, row;

        private NumberFormat  nf=NumberFormat.getNumberInstance(Locale.US);

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           col=vColIndex;
           row=rowIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFoculListener);

           text.addKeyListener(new java.awt.event.KeyAdapter() {
                   public void keyTyped(java.awt.event.KeyEvent evt) {
                      if (col!=0) {
                          char c = evt.getKeyChar();
                          if (!((c >= '0' && c <= '9')) &&
                                (c != KeyEvent.VK_BACK_SPACE) &&
                                (c != KeyEvent.VK_DELETE) &&
                                (c != KeyEvent.VK_ENTER)) {
                                getToolkit().beep();
                                evt.consume();
                                return;
                          }
                       }
                    }
                });
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                try {
                    //Double dVal=Double.parseDouble(value.toString().replace(",",""));
                    Number dVal = nf.parse(value.toString());
                    text.setText(nf.format(dVal));
                } catch (java.text.ParseException ex) {
                    //Logger.getLogger(DlgLookupBarang.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                retVal = GeneralFunction.udfGetDouble(((JTextField)text).getText());
                o=(retVal);
                return o;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }

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

     private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g1);
            ((JTextField)c).setSelectionStart(0);
               ((JTextField)c).setSelectionEnd(((JTextField)c).getText().length());

           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(g2);
            ((JTextField)c).setText(retail.main.GeneralFunction.dFmt.format(retail.main.GeneralFunction.udfGetDouble(((JTextField)c).getText())));

        }
   };
    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

}
