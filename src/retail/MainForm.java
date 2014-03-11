/*
 * NewJFrame.java
 *
 * Created on April 13, 2008, 10:21 PM
 */

package retail;

import akuntansi.FrmAkunList;
import akuntansi.FrmBuktiKas;
import akuntansi.FrmBukuBank;
import akuntansi.FrmHistoriBB;
import akuntansi.FrmHistoriKasBank;
import akuntansi.FrmJournalEntry;
import akuntansi.FrmJurnalList;
import akuntansi.FrmReportAkun;
import akuntansi.FrmReportKasBank;
import akuntansi.FrmRevCostBudget2;
import retail.sales.FrmSalesHistory;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import petty_cash.FrmCashFlow;
import petty_cash.FrmPettyCash;
import petty_cash.FrmPettyCashList;
import retail.main.About;
import retail.main.FrmSettingMenu;
import retail.main.FrmUserManagement;
import retail.main.MenuAuth;
import retail.main.SysConfig;
import retail.sales.FrmReturPenjualanHistory;
import retail.sales.TrxReturPenjualan;

/**load
 *
 * @author  oestadho
 */
public class MainForm extends javax.swing.JFrame {
    public static String sUserName="";
    public static int iUserProfile=0;
    static String sUserID;
    Connection conn;
    static int iLeft, iTop;
    public static String sKodeGudang="", sNamaGudang="";
    SysConfig sc=new SysConfig();
    private Timer timer;

    /** Creates new form NewJFrame */
    public MainForm() {
        initComponents();
        
        changeUIdefaults();
        setIconImage(new ImageIcon(getClass().getResource("/image/uTorrent.gif")).getImage());
        
//
        //Dimension dm=Toolkit.getDefaultToolkit().getScreenSize();
        //setBounds((dm.width-1024)/2, (dm.height-768)/2, 1024, 768);
        //setBounds(0, 0, dm.width, dm.height);
        this.setExtendedState(MAXIMIZED_BOTH);
    }

    public void setServerLocation(String s){
        lblServer.setText(s);
    }

    public void setUserProfile(int aInt) {
        iUserProfile=aInt;
    }

    private void udfAddActionInventory(){
        taskpane_inventori.removeAll();
        if(menuItem.canRead()){
            taskpane_inventori.add(new AbstractAction() {
            {
              putValue(Action.NAME, "Item");
              putValue(Action.SHORT_DESCRIPTION, "Item/ Barang");
              putValue(Action.SMALL_ICON, Images.CubeClass.getIcon(20, 20));
            }
            public void actionPerformed(ActionEvent e) {
                udfLoadListItem();
            }
          });
        }
      
      taskpane_inventori.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Stock opname");
          putValue(Action.SHORT_DESCRIPTION, "Penyesuaian Persediaan");
          putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            udfLoadSO();
        }
      });
      
      taskpane_inventori.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Transfer Barang");
          putValue(Action.SHORT_DESCRIPTION, "Transfer Item/ Barang");
          putValue(Action.SMALL_ICON, Images.Shopping_Full.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            udfLoadTransfer();
        }
      });
      taskpane_inventori.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Set Harga Jual");
          putValue(Action.SHORT_DESCRIPTION, "Set Harga Jual");
          putValue(Action.SMALL_ICON, Images.CubeClass.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            udfLoadSettingHargaJual();
        }
      });
      taskpane_inventori.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Grouping");
          putValue(Action.SHORT_DESCRIPTION, "Item Group");
          putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            udfLoadItemGrouping();
        }
      });
      taskpane_inventori.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Gudang");
          putValue(Action.SHORT_DESCRIPTION, "Gudang");
          putValue(Action.SMALL_ICON, Images.Molecule.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {

        }
      });

    }
    
    private void udfAddActionDaftar(){
      taskpane_daftar.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Customer");
          putValue(Action.SHORT_DESCRIPTION, "Daftar customer / pelanggan");
          putValue(Action.SMALL_ICON, Images.New_file.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            udfLoadListCustomer();
        }
      });
      if(menuSupplier.canRead()){
          taskpane_daftar.add(new AbstractAction() {
            {
              putValue(Action.NAME, "Supplier");
              putValue(Action.SHORT_DESCRIPTION, "Supplier (Pemasok)");
              putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
            }
            public void actionPerformed(ActionEvent e) {
                udfLoadSupplier();
            }
          });
      }
      
    }
    
    private void udfAddActionReport(){
        taskpane_report.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Persediaan");
          putValue(Action.SHORT_DESCRIPTION, "Persediaan barang");
          putValue(Action.SMALL_ICON, Images.Diagram.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            mnuRptPersediaanjMenuItem1ActionPerformed(e);
        }
      });
      taskpane_report.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Penjualan");
          putValue(Action.SHORT_DESCRIPTION, "Laporan Penjualan barang");
          putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            mnuRptPenjualanjMenuItem1ActionPerformed(e);
        }
      });
      
      taskpane_report.add(new AbstractAction() {
        {
          putValue(Action.NAME, "Pembelian");
          putValue(Action.SHORT_DESCRIPTION, "Laporan pembelian barang");
          putValue(Action.SMALL_ICON, Images.New_file.getIcon(20, 20));
        }
        public void actionPerformed(ActionEvent e) {
            mnuRptPembelianjMenuItem1ActionPerformed(e);
        }
      });
      
      
    }
    
    private void udfAddActionTransaksi(){
        taskPane_trx.add(new AbstractAction() {
            {putValue(Action.NAME, "Penjualan");
              putValue(Action.SHORT_DESCRIPTION, "Penjualan");
              putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
            }
            public void actionPerformed(ActionEvent e) {
                mnuTrxPenjualanjMenuItem1ActionPerformed(e);
            }
        });
        taskPane_trx.add(new AbstractAction() {
            {putValue(Action.NAME, "Pembelian");
              putValue(Action.SHORT_DESCRIPTION, "Pembelian");
              putValue(Action.SMALL_ICON, Images.Order.getIcon(20, 20));
            }
            public void actionPerformed(ActionEvent e) {
                mnuTrxPembelianNonPOjMenuItem1ActionPerformed(e);
            }
        });
    }

    private void changeUIdefaults() {
      // JXTaskPaneContainer settings (developer defaults)
      /* These are all the properties that can be set (may change with new version of SwingX)
        "TaskPaneContainer.useGradient",
        "TaskPaneContainer.background",
        "TaskPaneContainer.backgroundGradientStart",
        "TaskPaneContainer.backgroundGradientEnd",
        etc.
      */

      // setting taskpanecontainer defaults
      UIManager.put("TaskPaneContainer.useGradient", Boolean.FALSE);
      UIManager.put("TaskPaneContainer.background", Colors.LightGray.color(0.5f));

      // setting taskpane defaults
      UIManager.put("TaskPane.font", new FontUIResource(new Font("Verdana", Font.PLAIN, 16)));
      UIManager.put("TaskPane.titleBackgroundGradientStart", Colors.White.color());
      UIManager.put("TaskPane.titleBackgroundGradientEnd", Colors.LightBlue.color());


    }
    
    public void setConn(Connection con){
        conn=con;
    }
    
    public void setUserName(String s){
        sUserName=s;
        lblUserName.setText(s);
    }

    private boolean udfExistForm(JInternalFrame obj){
        JInternalFrame ji[] = jDesktopPane1.getAllFrames();
        for(int i=0;i<ji.length;i++){
            //System.out.println(ji[i].getTitle());

            if(ji[i].getClass().equals(obj.getClass())){
                try{
                    ji[i].setSelected(true);
                    return true;
                } catch(PropertyVetoException PO){
                }
                break;
            }
        }

        return false;
    }

    private boolean udfExistForm(JInternalFrame obj, String sTitle){
        JInternalFrame ji[] = jDesktopPane1.getAllFrames();
        for(int i=0;i<ji.length;i++){
            if(ji[i].getClass().equals(obj.getClass()) && ji[i].getTitle().equalsIgnoreCase(sTitle)){
                try{
                    ji[i].setSelected(true);
                    return true;
                } catch(PropertyVetoException PO){
                }
                break;
            }
        }

        return false;
    }

    private void udfLoadListItem(){
        if(udfExistForm(new FrmItemList())) return;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmItemList f1=new FrmItemList();
        f1.setConn(conn);
        f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
        
    }

    private void udfLoadTransfer(){
        if(udfExistForm(new FrmTransfer())) return;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmTransfer f1=new FrmTransfer();
        f1.setConn(conn);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }

    }

    private void udfLoadListCustomer(){
        if(udfExistForm(new FrmCustomerList())) return;

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmCustomerList f1=new FrmCustomerList();
        f1.setConn(conn);
        f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }

    }

    private void udfLoadPenjualanList() {
        if(udfExistForm(new FrmSalesHistory())) return;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmSalesHistory f1=new FrmSalesHistory();
        f1.setConn(conn);
        //f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }

    }

    private void udfLoadSupplier(){
        if(udfExistForm(new FrmSupplierList())) return;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmSupplierList f1=new FrmSupplierList();
        f1.setConn(conn);
        //f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadReturnPembelian(){
        //TrxReturPembelian//
        if(udfExistForm(new FrmGRReturn())) return;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmGRReturn f1=new FrmGRReturn();
        f1.setConn(conn);
        //f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setKoreksi(false);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
        
    }

    private void udfLoadReport(String s){
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmRptInventory f1=new FrmRptInventory();
        f1.setConn(conn);
        f1.udfSetFlagReport(s);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadReportSales(){
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmRptPenjualan f1=new FrmRptPenjualan();
        f1.setConn(conn);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadReportPembelian(){
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmRptPembelian f1=new FrmRptPembelian();
        f1.setConn(conn);
        f1.setVisible(true);
        //f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }
    
    private void udfLoadLookupGR(){
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DlgLookupPenerimaan f1=new DlgLookupPenerimaan(this, false);
        f1.setConn(conn);
        f1.setIsLookup(false);
        f1.setVisible(true);

       this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollKiri = new javax.swing.JScrollPane();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();
        taskPane_trx = new org.jdesktop.swingx.JXTaskPane();
        taskpane_inventori = new org.jdesktop.swingx.JXTaskPane();
        taskpane_daftar = new org.jdesktop.swingx.JXTaskPane();
        taskpane_report = new org.jdesktop.swingx.JXTaskPane();
        jScrollDesktop = new javax.swing.JScrollPane();
        jDesktopPane1 = new retail.main.JDesktopImage();
        jXStatusBar1 = new org.jdesktop.swingx.JXStatusBar();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        lblTanggal2 = new javax.swing.JLabel();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        lblUserName = new javax.swing.JLabel();
        jXPanel3 = new org.jdesktop.swingx.JXPanel();
        jXPanel4 = new org.jdesktop.swingx.JXPanel();
        lblTanggal = new javax.swing.JLabel();
        jXPanel5 = new org.jdesktop.swingx.JXPanel();
        lblJam = new javax.swing.JLabel();
        lblServer = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        mnuLokasi = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItemAnggota = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMnItemExit = new javax.swing.JMenuItem();
        jMenuSetting = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JSeparator();
        mnuUserSetup = new javax.swing.JMenuItem();
        mnuSettingKategori = new javax.swing.JMenuItem();
        mnuSettingTermin = new javax.swing.JMenuItem();
        mnuMenuAuth = new javax.swing.JMenuItem();
        mnuMasterHargaSupplier = new javax.swing.JMenuItem();
        mnuInventory = new javax.swing.JMenu();
        mnuListItem = new javax.swing.JMenuItem();
        mnuListSupplier = new javax.swing.JMenuItem();
        mnuListCustomer = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuListPenjualan = new javax.swing.JMenuItem();
        mnuListPenjualanRetur = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        mnuListPO = new javax.swing.JMenuItem();
        mnuListPembelian = new javax.swing.JMenuItem();
        mnuListPembelianRetur = new javax.swing.JMenuItem();
        jMenuSetting1 = new javax.swing.JMenu();
        jSeparator7 = new javax.swing.JSeparator();
        mnuInvSO = new javax.swing.JMenuItem();
        mnuInvTransfer = new javax.swing.JMenuItem();
        mnuInvGrouping = new javax.swing.JMenuItem();
        mnuInvReceiptUnplanned = new javax.swing.JMenuItem();
        mnuInvIssueUnplanned = new javax.swing.JMenuItem();
        mnuPembelian = new javax.swing.JMenu();
        mnuTrxPembelianNonPO = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuPO = new javax.swing.JMenuItem();
        mnuPOKoreksi = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuListPO1 = new javax.swing.JMenuItem();
        mnuTrxPembelianPO = new javax.swing.JMenuItem();
        mnuTrxPembelian1 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuTrxBayarSupplier = new javax.swing.JMenuItem();
        mnuAPJatuhTempo = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        mnuTrxPenjualan = new javax.swing.JMenuItem();
        mnuAR1 = new javax.swing.JMenuItem();
        mnuJualLookupItem = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        mnuAR = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnuJualKoreksiTrx = new javax.swing.JMenuItem();
        mnuTrxPenjualanClosing = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnuJournalEntry = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        mnuList = new javax.swing.JMenu();
        mnuListAkun = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        mnuKasBankMasuk = new javax.swing.JMenuItem();
        mnuKasBankKeluar = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        mnuKeuangan = new javax.swing.JMenu();
        mnuKeu_PC = new javax.swing.JMenu();
        mnuKeu_PC_Keluar = new javax.swing.JMenuItem();
        mnuKeu_PC_Masuk = new javax.swing.JMenuItem();
        mnuKeu_PC_list = new javax.swing.JMenuItem();
        mnuKeu_CashFlow = new javax.swing.JMenuItem();
        mnuRpt = new javax.swing.JMenu();
        mnuRptPersediaan = new javax.swing.JMenuItem();
        mnuRptPenjualan = new javax.swing.JMenuItem();
        mnuRptPembelian = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        mnuRptStatistik = new javax.swing.JMenu();
        mnuRptStatistikPenjualan = new javax.swing.JMenuItem();
        mnuRptStatistikPembelian = new javax.swing.JMenuItem();
        mnuRptStatistikPersediaan = new javax.swing.JMenuItem();
        mnuTrx = new javax.swing.JMenu();
        mnuToolsLookupItemJual = new javax.swing.JMenuItem();
        mnuToolsLookupItemBeli = new javax.swing.JMenuItem();
        mnuKalkulator = new javax.swing.JMenuItem();
        mnuToolHpp = new javax.swing.JMenuItem();
        mnuToolHpp1 = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuHelpAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Koperasi Simpan Pinjam "); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jSplitPane1.setDividerSize(10);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jSplitPane1ComponentResized(evt);
            }
        });
        jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane1PropertyChange(evt);
            }
        });
        jSplitPane1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                jSplitPane1AncestorMoved(evt);
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jSplitPane1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        taskPane_trx.setTitle("Transaksi");
        org.jdesktop.swingx.VerticalLayout verticalLayout1 = new org.jdesktop.swingx.VerticalLayout();
        verticalLayout1.setGap(2);
        taskPane_trx.getContentPane().setLayout(verticalLayout1);
        jXTaskPaneContainer1.add(taskPane_trx);

        taskpane_inventori.setScrollOnExpand(true);
        taskpane_inventori.setTitle("Inventory");
        jXTaskPaneContainer1.add(taskpane_inventori);

        taskpane_daftar.setTitle("Daftar");
        jXTaskPaneContainer1.add(taskpane_daftar);

        taskpane_report.setTitle("Report");
        jXTaskPaneContainer1.add(taskpane_report);

        jScrollKiri.setViewportView(jXTaskPaneContainer1);

        jSplitPane1.setLeftComponent(jScrollKiri);

        jScrollDesktop.setViewportView(jDesktopPane1);

        jSplitPane1.setRightComponent(jScrollDesktop);

        lblTanggal2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTanggal2.setText("User login :");

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTanggal2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
            .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTanggal2, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))
        );

        jXStatusBar1.add(jXPanel1);

        jXPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblUserName.setText("XXXXXXXXXXXXXXXXXXXXXXXX");

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
            .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jXPanel2Layout.createSequentialGroup()
                    .addGap(0, 43, Short.MAX_VALUE)
                    .addComponent(lblUserName)
                    .addGap(0, 43, Short.MAX_VALUE)))
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
            .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jXPanel2Layout.createSequentialGroup()
                    .addGap(0, 2, Short.MAX_VALUE)
                    .addComponent(lblUserName)
                    .addGap(0, 2, Short.MAX_VALUE)))
        );

        jXStatusBar1.add(jXPanel2);

        javax.swing.GroupLayout jXPanel3Layout = new javax.swing.GroupLayout(jXPanel3);
        jXPanel3.setLayout(jXPanel3Layout);
        jXPanel3Layout.setHorizontalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jXPanel3Layout.setVerticalGroup(
            jXPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        jXStatusBar1.add(jXPanel3);

        jXPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTanggal.setText("20/01/2011");

        javax.swing.GroupLayout jXPanel4Layout = new javax.swing.GroupLayout(jXPanel4);
        jXPanel4.setLayout(jXPanel4Layout);
        jXPanel4Layout.setHorizontalGroup(
            jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 89, Short.MAX_VALUE)
            .addGroup(jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
        );
        jXPanel4Layout.setVerticalGroup(
            jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
            .addGroup(jXPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel4Layout.createSequentialGroup()
                    .addComponent(lblTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jXStatusBar1.add(jXPanel4);

        jXPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblJam.setText("11:09:01");

        javax.swing.GroupLayout jXPanel5Layout = new javax.swing.GroupLayout(jXPanel5);
        jXPanel5.setLayout(jXPanel5Layout);
        jXPanel5Layout.setHorizontalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblJam, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
        );
        jXPanel5Layout.setVerticalGroup(
            jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
            .addGroup(jXPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel5Layout.createSequentialGroup()
                    .addComponent(lblJam, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jXStatusBar1.add(jXPanel5);

        lblServer.setText("11:09:01");
        jXStatusBar1.add(lblServer);

        jMenuFile.setMnemonic('F');
        jMenuFile.setText(". : File");

        mnuLokasi.setText("Lokasi Barang");
        mnuLokasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLokasiActionPerformed(evt);
            }
        });
        jMenuFile.add(mnuLokasi);

        jMenuItem4.setText("Gudang");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem4);

        jMenuItemAnggota.setText("Login");
        jMenuItemAnggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAnggotaActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemAnggota);
        jMenuFile.add(jSeparator1);

        jMnItemExit.setText("Keluar");
        jMnItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMnItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMnItemExit);

        jMenuBar2.add(jMenuFile);

        jMenuSetting.setMnemonic('S');
        jMenuSetting.setText(". : Setting");
        jMenuSetting.add(jSeparator5);

        mnuUserSetup.setText("User setup");
        mnuUserSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUserSetupActionPerformed1(evt);
            }
        });
        jMenuSetting.add(mnuUserSetup);

        mnuSettingKategori.setText("Kategori Item");
        mnuSettingKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSettingKategoriActionPerformed(evt);
            }
        });
        jMenuSetting.add(mnuSettingKategori);

        mnuSettingTermin.setText("Termin Pembayaran");
        mnuSettingTermin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSettingTerminActionPerformed(evt);
            }
        });
        jMenuSetting.add(mnuSettingTermin);

        mnuMenuAuth.setText("Otorisasi Menu");
        mnuMenuAuth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMenuAuthActionPerformed(evt);
            }
        });
        jMenuSetting.add(mnuMenuAuth);

        mnuMasterHargaSupplier.setText("Master Price by Product");
        mnuMasterHargaSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterHargaSupplierActionPerformed(evt);
            }
        });
        jMenuSetting.add(mnuMasterHargaSupplier);

        jMenuBar2.add(jMenuSetting);

        mnuInventory.setMnemonic('D');
        mnuInventory.setText(". : Daftar");
        mnuInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInventoryActionPerformed(evt);
            }
        });

        mnuListItem.setText("Item");
        mnuListItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListItemActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListItem);

        mnuListSupplier.setText("Supplier");
        mnuListSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListSupplierActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListSupplier);

        mnuListCustomer.setText("Pelanggan");
        mnuListCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListCustomerActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListCustomer);
        mnuInventory.add(jSeparator2);

        mnuListPenjualan.setText("Penjualan");
        mnuListPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPenjualanActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListPenjualan);

        mnuListPenjualanRetur.setText("Retur Penjualan");
        mnuListPenjualanRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPenjualanReturActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListPenjualanRetur);
        mnuInventory.add(jSeparator8);

        mnuListPO.setText("Pesanan Pembelian (PO)");
        mnuListPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPOjMenuItem1ActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListPO);

        mnuListPembelian.setText("Pembelian");
        mnuListPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPembelianjMenuItem1ActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListPembelian);

        mnuListPembelianRetur.setText("Retur Pembelian");
        mnuListPembelianRetur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPembelianReturActionPerformed(evt);
            }
        });
        mnuInventory.add(mnuListPembelianRetur);

        jMenuBar2.add(mnuInventory);

        jMenuSetting1.setMnemonic('P');
        jMenuSetting1.setText(". : Persediaan");
        jMenuSetting1.add(jSeparator7);

        mnuInvSO.setText("Stok Opname");
        mnuInvSO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvSOActionPerformed1(evt);
            }
        });
        jMenuSetting1.add(mnuInvSO);

        mnuInvTransfer.setText("Transfer Barang");
        mnuInvTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvTransferActionPerformed1(evt);
            }
        });
        jMenuSetting1.add(mnuInvTransfer);

        mnuInvGrouping.setText("Pengelompokan (Grouping) Item");
        mnuInvGrouping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInvGroupingActionPerformed1(evt);
            }
        });
        jMenuSetting1.add(mnuInvGrouping);

        mnuInvReceiptUnplanned.setText("Penerimaan Barang - Unplanned");
        jMenuSetting1.add(mnuInvReceiptUnplanned);

        mnuInvIssueUnplanned.setText("Pengeluaran Barang - Unplanned");
        jMenuSetting1.add(mnuInvIssueUnplanned);

        jMenuBar2.add(jMenuSetting1);

        mnuPembelian.setMnemonic('B');
        mnuPembelian.setText(". : Pembelian");

        mnuTrxPembelianNonPO.setText("Faktur Pembelian");
        mnuTrxPembelianNonPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianNonPOjMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuTrxPembelianNonPO);

        jMenu2.setText("Pesanan Pembelian");

        mnuPO.setText("Pesanan Pembelian (PO)");
        mnuPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOjMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(mnuPO);

        mnuPOKoreksi.setText("Koreksi Pesanan Pembelian (PO)");
        mnuPOKoreksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPOKoreksijMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(mnuPOKoreksi);
        jMenu2.add(jSeparator4);

        mnuListPO1.setText("Histori Pesanan Pembelian (PO)");
        mnuListPO1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListPO1jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(mnuListPO1);

        mnuPembelian.add(jMenu2);

        mnuTrxPembelianPO.setText("Penerimaan Barang PO");
        mnuTrxPembelianPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelianPOjMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuTrxPembelianPO);

        mnuTrxPembelian1.setText("Retur Pembelian");
        mnuTrxPembelian1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPembelian1jMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuTrxPembelian1);
        mnuPembelian.add(jSeparator3);

        mnuTrxBayarSupplier.setText("Pembayaran Hutang ke Supplier");
        mnuTrxBayarSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxBayarSupplierjMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuTrxBayarSupplier);

        mnuAPJatuhTempo.setText("Hutang Jatuh Tempo");
        mnuAPJatuhTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAPJatuhTempojMenuItem1ActionPerformed(evt);
            }
        });
        mnuPembelian.add(mnuAPJatuhTempo);

        jMenuBar2.add(mnuPembelian);

        jMenu1.setMnemonic('J');
        jMenu1.setText(". : Penjualan");

        mnuTrxPenjualan.setText("Penjualan Barang");
        mnuTrxPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPenjualanjMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(mnuTrxPenjualan);

        mnuAR1.setText("Retur Penjualan");
        mnuAR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAR1ActionPerformed1(evt);
            }
        });
        jMenu1.add(mnuAR1);

        mnuJualLookupItem.setText("Lookup Harga Jual");
        mnuJualLookupItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJualLookupItemActionPerformed(evt);
            }
        });
        jMenu1.add(mnuJualLookupItem);

        jMenuItem6.setText("Setting Harga Jual");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed1(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        mnuAR.setText("Pembayaran Piutang");
        mnuAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuARActionPerformed1(evt);
            }
        });
        jMenu1.add(mnuAR);

        jMenu3.setText("Koreksi");

        mnuJualKoreksiTrx.setText("Koreksi Penjualan");
        mnuJualKoreksiTrx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJualKoreksiTrxActionPerformed1(evt);
            }
        });
        jMenu3.add(mnuJualKoreksiTrx);

        jMenu1.add(jMenu3);

        mnuTrxPenjualanClosing.setText("Tutup Transaksi");
        mnuTrxPenjualanClosing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrxPenjualanClosingjMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(mnuTrxPenjualanClosing);

        jMenuBar2.add(jMenu1);

        jMenu4.setMnemonic('A');
        jMenu4.setText(". : Akuntansi");

        mnuJournalEntry.setText("Bukti Jurnal Umum");
        mnuJournalEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJournalEntryActionPerformed(evt);
            }
        });
        jMenu4.add(mnuJournalEntry);

        jMenu6.setText("Kas / Bank");

        jMenuItem9.setText("Kas & Bank Keluar");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem9);

        jMenuItem10.setText("Kas & Bank Masuk");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem10);

        jMenu4.add(jMenu6);

        mnuList.setText("List");

        mnuListAkun.setText("Daftar Akun");
        mnuListAkun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListAkunActionPerformed(evt);
            }
        });
        mnuList.add(mnuListAkun);

        jMenu7.setText("Buku Besar");

        jMenuItem15.setText("Bukti Jurnal Umum");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem15);

        jMenuItem8.setText("Histori Buku Besar");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem8);

        jMenuItem11.setText("Budget Pendapatan & Biaya");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem11);

        mnuList.add(jMenu7);

        jMenu8.setText("Kas & Bank");

        mnuKasBankMasuk.setText("Bukti Kas & Bank Masuk");
        mnuKasBankMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKasBankMasukActionPerformed(evt);
            }
        });
        jMenu8.add(mnuKasBankMasuk);

        mnuKasBankKeluar.setText("Bukti Kas & Bank Keluar");
        mnuKasBankKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKasBankKeluarActionPerformed(evt);
            }
        });
        jMenu8.add(mnuKasBankKeluar);

        jMenuItem16.setText("Buku Bank");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem16);

        mnuList.add(jMenu8);

        jMenu4.add(mnuList);

        jMenuBar2.add(jMenu4);

        mnuKeuangan.setText(".: Keuangan");

        mnuKeu_PC.setText("Petty Cash");

        mnuKeu_PC_Keluar.setText("Kas Keluar");
        mnuKeu_PC_Keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeu_PC_KeluarActionPerformed(evt);
            }
        });
        mnuKeu_PC.add(mnuKeu_PC_Keluar);

        mnuKeu_PC_Masuk.setText("Kas Masuk");
        mnuKeu_PC_Masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeu_PC_MasukActionPerformed(evt);
            }
        });
        mnuKeu_PC.add(mnuKeu_PC_Masuk);

        mnuKeu_PC_list.setText("Daftar Petty Cash");
        mnuKeu_PC_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeu_PC_listActionPerformed(evt);
            }
        });
        mnuKeu_PC.add(mnuKeu_PC_list);

        mnuKeuangan.add(mnuKeu_PC);

        mnuKeu_CashFlow.setText("Daftar Aliran Kas");
        mnuKeu_CashFlow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKeu_CashFlowActionPerformed(evt);
            }
        });
        mnuKeuangan.add(mnuKeu_CashFlow);

        jMenuBar2.add(mnuKeuangan);

        mnuRpt.setMnemonic('R');
        mnuRpt.setText(". : Laporan");

        mnuRptPersediaan.setText("Persediaan");
        mnuRptPersediaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPersediaanjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPersediaan);

        mnuRptPenjualan.setText("Laporan Penjualan");
        mnuRptPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPenjualanjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPenjualan);

        mnuRptPembelian.setText("Laporan Pembelian");
        mnuRptPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptPembelianjMenuItem1ActionPerformed(evt);
            }
        });
        mnuRpt.add(mnuRptPembelian);
        mnuRpt.add(jSeparator6);

        jMenu5.setText("Laporan Keuangan");

        jMenuItem12.setText("Buku Besar");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem12);

        jMenuItem13.setText("Kas dan Bank");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        mnuRpt.add(jMenu5);

        mnuRptStatistik.setText("Analisa & Statistik");
        mnuRptStatistik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptStatistikActionPerformed(evt);
            }
        });

        mnuRptStatistikPenjualan.setText("Penjualan");
        mnuRptStatistikPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptStatistikPenjualanActionPerformed(evt);
            }
        });
        mnuRptStatistik.add(mnuRptStatistikPenjualan);

        mnuRptStatistikPembelian.setText("Pembelian");
        mnuRptStatistikPembelian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptStatistikPembelianActionPerformed(evt);
            }
        });
        mnuRptStatistik.add(mnuRptStatistikPembelian);

        mnuRptStatistikPersediaan.setText("Persediaan");
        mnuRptStatistikPersediaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRptStatistikPersediaanActionPerformed(evt);
            }
        });
        mnuRptStatistik.add(mnuRptStatistikPersediaan);

        mnuRpt.add(mnuRptStatistik);

        jMenuBar2.add(mnuRpt);

        mnuTrx.setMnemonic('T');
        mnuTrx.setText(". : Tools");

        mnuToolsLookupItemJual.setText("Lookup Barang by Harga Jual");
        mnuToolsLookupItemJual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolsLookupItemJualjMenuItem1ActionPerformed(evt);
            }
        });
        mnuTrx.add(mnuToolsLookupItemJual);

        mnuToolsLookupItemBeli.setText("Lookup Barang by Beli Supplier");
        mnuToolsLookupItemBeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolsLookupItemBelijMenuItem1ActionPerformed(evt);
            }
        });
        mnuTrx.add(mnuToolsLookupItemBeli);

        mnuKalkulator.setText("Kalkulator");
        mnuKalkulator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKalkulatorActionPerformed(evt);
            }
        });
        mnuTrx.add(mnuKalkulator);

        mnuToolHpp.setText("Setting Harga Pokok");
        mnuToolHpp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolHppActionPerformed(evt);
            }
        });
        mnuTrx.add(mnuToolHpp);

        mnuToolHpp1.setText("Print Price List");
        mnuToolHpp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuToolHpp1ActionPerformed(evt);
            }
        });
        mnuTrx.add(mnuToolHpp1);

        jMenuBar2.add(mnuTrx);

        jMenuHelp.setMnemonic('H');
        jMenuHelp.setText(". : Help");

        jMenuHelpAbout.setMnemonic('A');
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuHelpAboutjMenuItem1ActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuHelpAbout);

        jMenuBar2.add(jMenuHelp);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXStatusBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXStatusBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1016)/2, (screenSize.height-465)/2, 1016, 465);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //this.setExtendedState(MAXIMIZED_BOTH);
        setTitle(sNamaUsaha);
        mnuUserSetup.setVisible(iUserProfile==0);
        sKodeGudang=sc.getSite_Id();

        try{
            ResultSet rs=conn.createStatement().executeQuery("select nama_gudang, now() from r_gudang where kode_gudang='"+sc.getSite_Id()+"'");
            if(rs.next()){
                sNamaGudang=rs.getString("nama_gudang");
                c=Calendar.getInstance();
                c.setTime(rs.getTimestamp(2));
            }
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        timer = new Timer();
        timer.schedule(new DoTick(), 0, 1000);
    }//GEN-LAST:event_formWindowOpened

    private Calendar c;
    private SimpleDateFormat dmy=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat hms=new SimpleDateFormat("hh:mm:ss");

    private void udfLoadSO() {
        StockOpname s1=new StockOpname();
        s1.setKoreksi(false);
        s1.setConn(conn);
        s1.setVisible(true);
    }

    class DoTick extends TimerTask {
        @Override
        public void run() {
            //c = Calendar.getInstance();
            c.add(Calendar.SECOND, 1);
            lblTanggal.setText(dmy.format(c.getTime()));
            lblJam.setText(hms.format(c.getTime()));
        }
    }

    private void jSplitPane1AncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jSplitPane1AncestorMoved
        
    }//GEN-LAST:event_jSplitPane1AncestorMoved

    private void jSplitPane1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jSplitPane1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jSplitPane1AncestorAdded

    private void jSplitPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jSplitPane1ComponentResized
        udfSetTopLeft();
    }//GEN-LAST:event_jSplitPane1ComponentResized

    private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane1PropertyChange
        udfSetTopLeft();
    }//GEN-LAST:event_jSplitPane1PropertyChange

//    private void udfSetTopLeft() {
//        iLeft=this.getX()+ jSplitPane1.getX()+jSplitPane1.getRightComponent().getX()+ jDesktopPane1.getX()-7;
//        iTop=this.getY()+ jSplitPane1.getY()+ jDesktopPane1.getY();
//    }
    
    private void udfSetTopLeft() {
        iLeft=this.getX()+jSplitPane1.getX()+jSplitPane1.getLeftComponent().getWidth()+ jDesktopPane1.getX()+5;
        iTop=this.getY()+ jSplitPane1.getY()+ jDesktopPane1.getY()+31;
    }
    
    
    
    private void mnuRptPenjualanjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPenjualanjMenuItem1ActionPerformed
        udfLoadReportSales();
       
}//GEN-LAST:event_mnuRptPenjualanjMenuItem1ActionPerformed

    private void jMenuHelpAboutjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuHelpAboutjMenuItem1ActionPerformed
        new About(this, true).setVisible(true);
}//GEN-LAST:event_jMenuHelpAboutjMenuItem1ActionPerformed

    private void mnuRptPersediaanjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPersediaanjMenuItem1ActionPerformed
        udfLoadReport("persediaan");
}//GEN-LAST:event_mnuRptPersediaanjMenuItem1ActionPerformed

    private void mnuRptPembelianjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptPembelianjMenuItem1ActionPerformed
        udfLoadReportPembelian();
       
        
}//GEN-LAST:event_mnuRptPembelianjMenuItem1ActionPerformed

    private void jMenuItemAnggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAnggotaActionPerformed
        
}//GEN-LAST:event_jMenuItemAnggotaActionPerformed

private void mnuInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInventoryActionPerformed
    
}//GEN-LAST:event_mnuInventoryActionPerformed

private void mnuListItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListItemActionPerformed
    udfLoadListItem();
}//GEN-LAST:event_mnuListItemActionPerformed

private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
    
}//GEN-LAST:event_jMenuItem4ActionPerformed

private void jMnItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMnItemExitActionPerformed
     if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk keluar")==JOptionPane.YES_OPTION)
         System.exit(0);
}//GEN-LAST:event_jMnItemExitActionPerformed

private void mnuUserSetupActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUserSetupActionPerformed1
    udfLoadUserManagement();
}//GEN-LAST:event_mnuUserSetupActionPerformed1

private void mnuInvSOActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvSOActionPerformed1
    udfLoadSO();

}//GEN-LAST:event_mnuInvSOActionPerformed1

private void mnuInvTransferActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvTransferActionPerformed1
    udfLoadTransfer();
}//GEN-LAST:event_mnuInvTransferActionPerformed1

private void jMenuItem6ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed1
    udfLoadSettingHargaJual();
}//GEN-LAST:event_jMenuItem6ActionPerformed1

private void mnuInvGroupingActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInvGroupingActionPerformed1
    udfLoadItemGrouping();
}//GEN-LAST:event_mnuInvGroupingActionPerformed1

private void mnuTrxPenjualanjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPenjualanjMenuItem1ActionPerformed
//    TrxPenjualan trx=new TrxPenjualan();
    retail.sales.TrxPenjualan trx=new retail.sales.TrxPenjualan();
    trx.setTitle("Penjualan");
    trx.setIconImage(getIconImage());
//    trx.setDesktopPane(jDesktopPane1);
    trx.setConn(conn);
    trx.setState(Frame.MAXIMIZED_BOTH);
    trx.setVisible(true);
}//GEN-LAST:event_mnuTrxPenjualanjMenuItem1ActionPerformed

private void mnuListSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListSupplierActionPerformed
    udfLoadSupplier();
}//GEN-LAST:event_mnuListSupplierActionPerformed

private void mnuTrxPembelianPOjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianPOjMenuItem1ActionPerformed
//    TrxPembelian trx=new TrxPembelian();
//    trx.setConn(conn);
//    trx.setKoreksi(false);
//    trx.setVisible(true);

    FrmGR f1=new FrmGR();
    //FrmGRBeli f1=new FrmGRBeli();
    if(udfExistForm(f1, "Penerimaan/ Pembelian Barang (Plus)")){
        f1.dispose();
        return;
    }
    f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuTrxPembelianPOjMenuItem1ActionPerformed

private void mnuTrxPembelian1jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelian1jMenuItem1ActionPerformed
    udfLoadReturnPembelian();
}//GEN-LAST:event_mnuTrxPembelian1jMenuItem1ActionPerformed

private void mnuListPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPenjualanActionPerformed
    udfLoadPenjualanList();
}//GEN-LAST:event_mnuListPenjualanActionPerformed

private void mnuListPembelianjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPembelianjMenuItem1ActionPerformed
    //udfLoadLookupGR();
    FrmGRHistory f1=new FrmGRHistory();
    if(udfExistForm(f1)){
        f1.dispose();
        return;
    }
    f1.setConn(conn);
    f1.setDesktopPane(jDesktopPane1);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuListPembelianjMenuItem1ActionPerformed

private void mnuToolsLookupItemJualjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolsLookupItemJualjMenuItem1ActionPerformed
    DlgLookupItemJual dlgLookupItem=new DlgLookupItemJual(this, true);
    dlgLookupItem.setConn(conn);
    dlgLookupItem.setVisible(true);
}//GEN-LAST:event_mnuToolsLookupItemJualjMenuItem1ActionPerformed

private void mnuSettingKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSettingKategoriActionPerformed
    udfLoadKategori();
}//GEN-LAST:event_mnuSettingKategoriActionPerformed

private void mnuMenuAuthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMenuAuthActionPerformed
    udfLoadMenuAuth();
}//GEN-LAST:event_mnuMenuAuthActionPerformed

private void mnuSettingTerminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSettingTerminActionPerformed
    udfLoadTermin();
}//GEN-LAST:event_mnuSettingTerminActionPerformed

private void mnuMasterHargaSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterHargaSupplierActionPerformed
    udfLoadMasterPriceByProduct();
}//GEN-LAST:event_mnuMasterHargaSupplierActionPerformed

private void mnuJualLookupItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJualLookupItemActionPerformed
    DlgLookupItemJual d1=new DlgLookupItemJual(this, true);
    d1.setConn(conn);
    d1.setVisible(true);
}//GEN-LAST:event_mnuJualLookupItemActionPerformed

private void mnuListCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListCustomerActionPerformed
    udfLoadListCustomer();
}//GEN-LAST:event_mnuListCustomerActionPerformed

private void mnuARActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuARActionPerformed1
    udfLoadAR();
}//GEN-LAST:event_mnuARActionPerformed1

private void mnuPOjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOjMenuItem1ActionPerformed
    udfLoadPO();
}//GEN-LAST:event_mnuPOjMenuItem1ActionPerformed

private void mnuTrxPembelianNonPOjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPembelianNonPOjMenuItem1ActionPerformed
    //FrmGR f1=new FrmGR();
    FrmGRBeli f1=new FrmGRBeli();
    if(udfExistForm(f1, "Penerimaan Barang - Tanpa PO")){
        f1.dispose();
        return;
    }
    f1.setFlagPO(false);
    f1.setConn(conn);
    f1.setDesktop(jDesktopPane1);
    f1.setTitle("Penerimaan Barang - Tanpa PO");
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuTrxPembelianNonPOjMenuItem1ActionPerformed

private void mnuAR1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAR1ActionPerformed1
    TrxReturPenjualan trx=new TrxReturPenjualan();
    trx.setConn(conn);
    trx.setState(Frame.MAXIMIZED_BOTH);
    trx.setVisible(true);
}//GEN-LAST:event_mnuAR1ActionPerformed1

private void mnuListPOjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPOjMenuItem1ActionPerformed
    FrmPOHistory f1=new FrmPOHistory(conn);
    if(udfExistForm(f1)){
        f1.dispose();
        return;
    }
    //f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    f1.setMainForm(this);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuListPOjMenuItem1ActionPerformed

private void mnuJualKoreksiTrxActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJualKoreksiTrxActionPerformed1
    retail.sales.FrmTrxPenjualan trx=new retail.sales.FrmTrxPenjualan();
    trx.setConn(conn);
    trx.setFlagKoreksi(true);
    trx.setState(Frame.MAXIMIZED_BOTH);
    trx.setVisible(true);
}//GEN-LAST:event_mnuJualKoreksiTrxActionPerformed1

private void mnuPOKoreksijMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPOKoreksijMenuItem1ActionPerformed
    udfLoadPOKoreksi("");
}//GEN-LAST:event_mnuPOKoreksijMenuItem1ActionPerformed

private void mnuToolsLookupItemBelijMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolsLookupItemBelijMenuItem1ActionPerformed
    DlgLookupItemBeli dlgLookupItem=new DlgLookupItemBeli(this, true);
    dlgLookupItem.setConn(conn);
    dlgLookupItem.setVisible(true);
}//GEN-LAST:event_mnuToolsLookupItemBelijMenuItem1ActionPerformed

private void mnuListPembelianReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPembelianReturActionPerformed
    FrmGRReturnHistory f1=new FrmGRReturnHistory(conn);
    if(udfExistForm(f1)){
        f1.dispose();
        return;
    }
    //f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    f1.setMainForm(this);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuListPembelianReturActionPerformed

private void mnuListPenjualanReturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPenjualanReturActionPerformed
    FrmReturPenjualanHistory f1=new FrmReturPenjualanHistory(conn);
    if(udfExistForm(f1)){
        f1.dispose();
        return;
    }
    //f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    f1.setMainForm(this);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuListPenjualanReturActionPerformed

private void mnuKalkulatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKalkulatorActionPerformed
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    Runtime rt = Runtime.getRuntime();
    try {

        rt.exec(new String[]{"cmd","/c","start calc"});
    } catch(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
        e.printStackTrace();
    }
}//GEN-LAST:event_mnuKalkulatorActionPerformed

private void mnuLokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLokasiActionPerformed
    FrmLokasi f1=new FrmLokasi();
    if(udfExistForm(f1)){
        f1.dispose();
        return;
    }
    f1.setConn(conn);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    f1.setVisible(true);
    //f1.setMainForm(this);
    try{
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuLokasiActionPerformed

private void mnuTrxBayarSupplierjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxBayarSupplierjMenuItem1ActionPerformed
    udfLoadAP();
}//GEN-LAST:event_mnuTrxBayarSupplierjMenuItem1ActionPerformed

private void mnuListPO1jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListPO1jMenuItem1ActionPerformed
    mnuListPOjMenuItem1ActionPerformed(evt);
}//GEN-LAST:event_mnuListPO1jMenuItem1ActionPerformed

private void mnuAPJatuhTempojMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAPJatuhTempojMenuItem1ActionPerformed
    udfLoadAPJtTempo();
}//GEN-LAST:event_mnuAPJatuhTempojMenuItem1ActionPerformed

private void mnuToolHppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolHppActionPerformed
    udfLoadSettingHpp();
}//GEN-LAST:event_mnuToolHppActionPerformed

private void mnuListAkunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListAkunActionPerformed
    udfLoadListAkun();
}//GEN-LAST:event_mnuListAkunActionPerformed

private void mnuJournalEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJournalEntryActionPerformed
    udfLoadJournalEntry();
}//GEN-LAST:event_mnuJournalEntryActionPerformed

private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
    udfLoadKas("K");
}//GEN-LAST:event_jMenuItem9ActionPerformed

private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
    udfLoadKas("M");
}//GEN-LAST:event_jMenuItem10ActionPerformed

private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
    udfLoadListJurnalUst();
}//GEN-LAST:event_jMenuItem15ActionPerformed

private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
    udfLoadListHistoriBB();
}//GEN-LAST:event_jMenuItem8ActionPerformed

private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
    udfLoadRevCostBudget();
}//GEN-LAST:event_jMenuItem11ActionPerformed

private void mnuKasBankMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKasBankMasukActionPerformed
    udfLoadListBuktiKas("M");
}//GEN-LAST:event_mnuKasBankMasukActionPerformed

private void mnuKasBankKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKasBankKeluarActionPerformed
    udfLoadListBuktiKas("K");
}//GEN-LAST:event_mnuKasBankKeluarActionPerformed

private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
    udfLoadBukuBank();
}//GEN-LAST:event_jMenuItem16ActionPerformed

private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
    udfLoadReportGL();
}//GEN-LAST:event_jMenuItem12ActionPerformed

private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
    udfLoadReportKasBank();
}//GEN-LAST:event_jMenuItem13ActionPerformed

private void mnuRptStatistikPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptStatistikPenjualanActionPerformed
    FrmStatistikPenjualan fRpt=new FrmStatistikPenjualan();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    //fAkun.setDesktopPane(jDesktopPane1);

    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);

    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuRptStatistikPenjualanActionPerformed

private void mnuRptStatistikPembelianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptStatistikPembelianActionPerformed
    
}//GEN-LAST:event_mnuRptStatistikPembelianActionPerformed

private void mnuRptStatistikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptStatistikActionPerformed
    
}//GEN-LAST:event_mnuRptStatistikActionPerformed

private void mnuRptStatistikPersediaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRptStatistikPersediaanActionPerformed
    FrmStatistikPersediaan fRpt=new FrmStatistikPersediaan();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    //fAkun.setDesktopPane(jDesktopPane1);

    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);

    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){

    }
}//GEN-LAST:event_mnuRptStatistikPersediaanActionPerformed

private void mnuKeu_PC_KeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeu_PC_KeluarActionPerformed
    FrmPettyCash fRpt=new FrmPettyCash();
    if(udfExistForm(fRpt, "Petty Kas Keluar")){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    fRpt.setTitle("Petty Kas Keluar");
    fRpt.setKeluarMasuk("K");
    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){}
}//GEN-LAST:event_mnuKeu_PC_KeluarActionPerformed

private void mnuKeu_PC_MasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeu_PC_MasukActionPerformed
    FrmPettyCash fRpt=new FrmPettyCash();
    if(udfExistForm(fRpt, "Petty Kas Masuk")){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    fRpt.setTitle("Petty Kas Masuk");
    fRpt.setKeluarMasuk("M");
    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){}
}//GEN-LAST:event_mnuKeu_PC_MasukActionPerformed

private void mnuKeu_PC_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeu_PC_listActionPerformed
    FrmPettyCashList fRpt=new FrmPettyCashList();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    fRpt.setDesktopPane(jDesktopPane1);
    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){}
}//GEN-LAST:event_mnuKeu_PC_listActionPerformed

private void mnuTrxPenjualanClosingjMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrxPenjualanClosingjMenuItem1ActionPerformed
    DlgClosingTrx d1=new DlgClosingTrx(this, true);
    d1.setConn(conn);
    d1.setVisible(true);
    
}//GEN-LAST:event_mnuTrxPenjualanClosingjMenuItem1ActionPerformed

private void mnuKeu_CashFlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKeu_CashFlowActionPerformed
    FrmCashFlow fRpt=new FrmCashFlow();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){}
}//GEN-LAST:event_mnuKeu_CashFlowActionPerformed

private void mnuToolHpp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuToolHpp1ActionPerformed
    FrmItemPriceList2 fRpt=new FrmItemPriceList2();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){}
}//GEN-LAST:event_mnuToolHpp1ActionPerformed

private void udfLoadReportKasBank(){
    FrmReportKasBank fRpt=new FrmReportKasBank();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    //fAkun.setDesktopPane(jDesktopPane1);

    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);

    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){

    }

}

private void udfLoadReportGL(){
    FrmReportAkun fRpt=new FrmReportAkun();
    if(udfExistForm(fRpt)){
        fRpt.dispose();
        return;
    }
    fRpt.setConn(conn);
    fRpt.setVisible(true);
    fRpt.setBounds(0, 0, fRpt.getWidth(), fRpt.getHeight());
    jDesktopPane1.add(fRpt, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        fRpt.setSelected(true);
    } catch(PropertyVetoException PO){

    }

}

private void udfLoadListBuktiKas(String string) {
    String sTitle="Bukti Kas/ Bank "+(string.equalsIgnoreCase("M")? "Masuk": "Keluar");
    FrmHistoriKasBank f1=new FrmHistoriKasBank();

    if(udfExistForm(f1 , sTitle)){
        f1.dispose();
        return;
    }
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    f1.setConn(conn);
    f1.setFlag(string);
    f1.setVisible(true);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        f1.setMaximum(true);
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){
    }
}

private void udfLoadBukuBank() {
    if(udfExistForm(new FrmBukuBank ())) return;
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    FrmBukuBank f1=new FrmBukuBank();
    f1.setConn(conn);
    f1.setVisible(true);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        f1.setMaximum(true);
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){
    }
}

private void udfLoadJournalEntry(){
        FrmJournalEntry fJournal=new FrmJournalEntry();
        fJournal.setConn(conn);
        //fJournal.setBounds(0, 0, fJournal.getWidth(), fJournal.getHeight());
        //jDesktopPane1.add(fJournal, javax.swing.JLayeredPane.DEFAULT_LAYER);
        fJournal.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fJournal.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
    }

private void udfLoadRevCostBudget() {
    if(udfExistForm(new FrmRevCostBudget2())) return;

    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    FrmRevCostBudget2 f1=new FrmRevCostBudget2();
    f1.setConn(conn);
    f1.setVisible(true);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

    try{
        f1.setMaximum(true);
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }
}

private void udfLoadListHistoriBB(){
    if(udfExistForm(new FrmHistoriBB())) return;

    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    FrmHistoriBB f1=new FrmHistoriBB();
    f1.setConn(conn);
    f1.setVisible(true);
    f1.setDesktopPane(jDesktopPane1);
    f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
    jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
    try{
        f1.setMaximum(true);
        f1.setSelected(true);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch(PropertyVetoException PO){

    }

}

private void udfLoadKas(String sFlag){
    FrmBuktiKas fBuktiKas=new FrmBuktiKas();
    fBuktiKas.setConn(conn);
    fBuktiKas.setFlag(sFlag);
    //fJournal.setBounds(0, 0, fJournal.getWidth(), fJournal.getHeight());
    //jDesktopPane1.add(fJournal, javax.swing.JLayeredPane.DEFAULT_LAYER);
    fBuktiKas.setVisible(true);
//        try{
//            //fMaster.setMaximum(true);
//            fJournal.setSelected(true);
//        } catch(PropertyVetoException PO){
//
//        }
}

private void udfLoadListJurnalUst(){
        if(udfExistForm(new FrmJurnalList())) return;

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FrmJurnalList f1=new FrmJurnalList();
        f1.setConn(conn);
//        f1.setUserName(sUserName);
        f1.setVisible(true);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try{
            f1.setMaximum(true);
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }

    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private retail.main.JDesktopImage jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuHelpAbout;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItemAnggota;
    private javax.swing.JMenu jMenuSetting;
    private javax.swing.JMenu jMenuSetting1;
    private javax.swing.JMenuItem jMnItemExit;
    private javax.swing.JScrollPane jScrollDesktop;
    private javax.swing.JScrollPane jScrollKiri;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JSplitPane jSplitPane1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXPanel jXPanel3;
    private org.jdesktop.swingx.JXPanel jXPanel4;
    private org.jdesktop.swingx.JXPanel jXPanel5;
    private org.jdesktop.swingx.JXStatusBar jXStatusBar1;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    private javax.swing.JLabel lblJam;
    private javax.swing.JLabel lblServer;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JLabel lblTanggal2;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JMenuItem mnuAPJatuhTempo;
    private javax.swing.JMenuItem mnuAR;
    private javax.swing.JMenuItem mnuAR1;
    private javax.swing.JMenuItem mnuInvGrouping;
    private javax.swing.JMenuItem mnuInvIssueUnplanned;
    private javax.swing.JMenuItem mnuInvReceiptUnplanned;
    private javax.swing.JMenuItem mnuInvSO;
    private javax.swing.JMenuItem mnuInvTransfer;
    private javax.swing.JMenu mnuInventory;
    private javax.swing.JMenuItem mnuJournalEntry;
    private javax.swing.JMenuItem mnuJualKoreksiTrx;
    private javax.swing.JMenuItem mnuJualLookupItem;
    private javax.swing.JMenuItem mnuKalkulator;
    private javax.swing.JMenuItem mnuKasBankKeluar;
    private javax.swing.JMenuItem mnuKasBankMasuk;
    private javax.swing.JMenuItem mnuKeu_CashFlow;
    private javax.swing.JMenu mnuKeu_PC;
    private javax.swing.JMenuItem mnuKeu_PC_Keluar;
    private javax.swing.JMenuItem mnuKeu_PC_Masuk;
    private javax.swing.JMenuItem mnuKeu_PC_list;
    private javax.swing.JMenu mnuKeuangan;
    private javax.swing.JMenu mnuList;
    private javax.swing.JMenuItem mnuListAkun;
    private javax.swing.JMenuItem mnuListCustomer;
    private javax.swing.JMenuItem mnuListItem;
    private javax.swing.JMenuItem mnuListPO;
    private javax.swing.JMenuItem mnuListPO1;
    private javax.swing.JMenuItem mnuListPembelian;
    private javax.swing.JMenuItem mnuListPembelianRetur;
    private javax.swing.JMenuItem mnuListPenjualan;
    private javax.swing.JMenuItem mnuListPenjualanRetur;
    private javax.swing.JMenuItem mnuListSupplier;
    private javax.swing.JMenuItem mnuLokasi;
    private javax.swing.JMenuItem mnuMasterHargaSupplier;
    private javax.swing.JMenuItem mnuMenuAuth;
    private javax.swing.JMenuItem mnuPO;
    private javax.swing.JMenuItem mnuPOKoreksi;
    private javax.swing.JMenu mnuPembelian;
    private javax.swing.JMenu mnuRpt;
    private javax.swing.JMenuItem mnuRptPembelian;
    private javax.swing.JMenuItem mnuRptPenjualan;
    private javax.swing.JMenuItem mnuRptPersediaan;
    private javax.swing.JMenu mnuRptStatistik;
    private javax.swing.JMenuItem mnuRptStatistikPembelian;
    private javax.swing.JMenuItem mnuRptStatistikPenjualan;
    private javax.swing.JMenuItem mnuRptStatistikPersediaan;
    private javax.swing.JMenuItem mnuSettingKategori;
    private javax.swing.JMenuItem mnuSettingTermin;
    private javax.swing.JMenuItem mnuToolHpp;
    private javax.swing.JMenuItem mnuToolHpp1;
    private javax.swing.JMenuItem mnuToolsLookupItemBeli;
    private javax.swing.JMenuItem mnuToolsLookupItemJual;
    private javax.swing.JMenu mnuTrx;
    private javax.swing.JMenuItem mnuTrxBayarSupplier;
    private javax.swing.JMenuItem mnuTrxPembelian1;
    private javax.swing.JMenuItem mnuTrxPembelianNonPO;
    private javax.swing.JMenuItem mnuTrxPembelianPO;
    private javax.swing.JMenuItem mnuTrxPenjualan;
    private javax.swing.JMenuItem mnuTrxPenjualanClosing;
    private javax.swing.JMenuItem mnuUserSetup;
    private org.jdesktop.swingx.JXTaskPane taskPane_trx;
    private org.jdesktop.swingx.JXTaskPane taskpane_daftar;
    private org.jdesktop.swingx.JXTaskPane taskpane_inventori;
    private org.jdesktop.swingx.JXTaskPane taskpane_report;
    // End of variables declaration//GEN-END:variables

    public static String sNamaUsaha="UD. Nabila Artha Abadi";
    public static String sAlamat="Muka Pasar Pucangro Lamongan";
    public static String sTelp="Telp. 0322-390652";

    private void udfLoadUserManagement() {
        FrmUserManagement f1=new FrmUserManagement();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadKategori() {
        FrmItemCategory f1=new FrmItemCategory();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadItemGrouping(){
        FrmItemGroup f1=new FrmItemGroup();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadMenuAuth() {
        FrmSettingMenu f1=new FrmSettingMenu();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    public void udfSetUserMenu(){
//        lblUser.setText("<html>Username : <b>"+sID+" - "+ sUserName+"</b></html>");
//        lblServer.setText(sIPServer);
//        lblShift.setText(shift);
//        lblUser1.setText(sUserName);
//        lblSite.setText("<html>Site :&nbsp <b>"+sSiteID+" - "+sSiteName+"</b></html>");
//        lblDepo.setText("<html>Workstation :&nbsp <b>"+sDepo+"</b></html>");

        try{
            String sQry="select menu_description, " +
                    "coalesce(can_insert,false) as can_insert, " +
                    "coalesce(can_update, false) as can_update, " +
                    "coalesce(can_delete, false) as can_delete, " +
                    "coalesce(can_read, false) as can_read, " +
                    "coalesce(can_print, false) as can_print, " +
                    "coalesce(can_correction, false) as can_correction " +
                    "from m_menu_authorization auth " +
                    "inner join m_menu_list list on list.id=auth.menu_id " +
                    "where user_name='"+sUserName+"' and module_name='RTL'";

            ResultSet rs=conn.createStatement().executeQuery(sQry);
            logOff();

            while(rs.next()){
                if(rs.getString("menu_description").equalsIgnoreCase("Master Item")){
                    setMenu(menuItem, mnuListItem, rs.getBoolean("can_read"), rs.getBoolean("can_insert"), rs.getBoolean("can_update"), rs.getBoolean("can_delete"), rs.getBoolean("can_print"), rs.getBoolean("can_correction"));
                    
                }
                if(rs.getString("menu_description").equalsIgnoreCase("Master Supplier")){
                    setMenu(menuSupplier, mnuListSupplier, rs.getBoolean("can_read"), rs.getBoolean("can_insert"), rs.getBoolean("can_update"), rs.getBoolean("can_delete"), rs.getBoolean("can_print"), rs.getBoolean("can_correction"));
                }
                if(rs.getString("menu_description").equalsIgnoreCase("Master Kategori")){
                    setMenu(menuSettingKategori, mnuSettingKategori, rs.getBoolean("can_read"), rs.getBoolean("can_insert"), rs.getBoolean("can_update"), rs.getBoolean("can_delete"), rs.getBoolean("can_print"), rs.getBoolean("can_correction"));
                }

            }
            udfAddActionTransaksi();
            udfAddActionInventory();
            udfAddActionDaftar();
            udfAddActionReport();
            
            rs.close();
//            rs=conn.createStatement().executeQuery("select photo from m_user where username='"+sUserName+"'");
//            if(rs.next()){
//                byte[] imgBytes = rs.getBytes("photo");
//
//                if(imgBytes!=null){
//                    javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(imgBytes);
//                    javax.swing.ImageIcon bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
//                                       (lblPhoto.getWidth(), lblPhoto.getHeight(), Image.SCALE_REPLICATE));
//
//                    lblPhoto.setIcon(bigImage);
//                    imgBytes=null;
//                }else{
//                    lblPhoto.setIcon(null);
//                }
//            }

        }catch(SQLException se){
            System.out.println(se.getMessage());
        }
    }

    public void logOff(){
        mnuListItem.setVisible(false);
        mnuListSupplier.setVisible(false);
        mnuSettingKategori.setVisible(false);

    }

    private void setMenu(MenuAuth mau, JMenuItem mnuItem, boolean can_read, boolean can_insert, boolean can_update, boolean can_delete,
        boolean can_print, boolean can_correction){
        mau.setRead(can_read);
        mau.setInsert(can_insert);
        mau.setUpdate(can_update);
        mau.setDelete(can_delete);
        mau.setPrint(can_print);
        mau.setKoreksi(can_correction);
        if(mnuItem!=null) mnuItem.setVisible(can_read);
    }


    public static MenuAuth menuItem=new MenuAuth();
    public static MenuAuth menuSupplier=new MenuAuth();
    public static MenuAuth menuSettingKategori=new MenuAuth();

    private void udfLoadTermin() {
        DlgTermin d1=new DlgTermin(this, true);
        d1.setConn(conn);
        d1.setVisible(true);
    }

    private void udfLoadMasterPriceByProduct() {
        FrmSupplierPriceByItem f1=new FrmSupplierPriceByItem();
        f1.setConn(conn);
        f1.setVisible(true);
    }

    private void udfLoadSettingHargaJual() {
        FrmSettingHargaJual f1=new FrmSettingHargaJual();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setDesktopIcon(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadAR() {
        FrmAR f1=new FrmAR();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadAP() {
        FrmAP f1=new FrmAP();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadAPJtTempo() {
        FrmAPJtTempo f1=new FrmAPJtTempo();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setDesktopPane(jDesktopPane1);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadSettingHpp() {
        FrmSettingHpp f1=new FrmSettingHpp();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadListAkun(){
        FrmAkunList f1=new FrmAkunList();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        f1.setDesktop(jDesktopPane1);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    private void udfLoadPO() {
        FrmPO f1=new FrmPO();
        if(udfExistForm(f1)){
            f1.dispose();
            return;
        }
        //f1.setMainForm(this);
        f1.setConn(conn);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    public void udfLoadPOKoreksi(String sNoPo) {
        FrmPO f1=new FrmPO();
        if(udfExistForm(f1, "Koreksi PO - '"+sNoPo+"'")){
            f1.dispose();
            return;
        }
        f1.setNoPO(sNoPo);
        f1.setTitle("Koreksi PO - '"+sNoPo+"'");
        f1.setConn(conn);
        f1.setFlagKoreksi(true);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
        try{
            f1.setSelected(true);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch(PropertyVetoException PO){

        }
    }

    public void udfLoadKoreksiReturJual(String sNoRetur, Object objForm) {
        TrxReturPenjualan f1=new TrxReturPenjualan();
//        if(udfExistForm(f1, "Koreksi Retur Jual - '"+sNoPo+"'")){
//            f1.dispose();
//            return;
//        }
        f1.setObjForm(objForm);
        f1.setNoRetur(sNoRetur);
        f1.setTitle("Koreksi Retur Jual - '"+sNoRetur+"'");
        f1.setConn(conn);
        f1.setFlagKoreksi(true);
//        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
//        jDesktopPane1.add(f1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        f1.setVisible(true);
//        try{
//            f1.setSelected(true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//        } catch(PropertyVetoException PO){
//
//        }
    }
}
