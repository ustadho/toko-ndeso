/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgClosingTrx.java
 *
 * Created on 08 Mei 11, 14:00:20
 */
package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import retail.main.GeneralFunction;
import tableRender.ColumnGroup;
import tableRender.GroupableTableHeader;

/**
 *
 * @author cak-ust
 */
public class DlgClosingTrx extends javax.swing.JDialog {
    private Connection conn;
    
    
    /** Creates new form DlgClosingTrx */
    public DlgClosingTrx(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jXDatePicker1.setFormats("dd/MM/yyyy");
        jTable1.getColumn("No").setPreferredWidth(40);
        jTable1.getColumn("Keterangan").setPreferredWidth(140);
        UIManager.put(GroupableTableHeader.uiClassID, "tableRender.GroupableTableHeaderUI");
        GroupableTableHeader header = new GroupableTableHeader(jTable1.getColumnModel());
        TableColumnModel columns = jTable1.getColumnModel();
        
        ColumnGroup tunai = new ColumnGroup("Tunai");
        tunai.add(columns.getColumn(2)); //Total
        tunai.add(columns.getColumn(3)); //Aktual
        
        ColumnGroup kredit = new ColumnGroup("Kredit");
        kredit.add(columns.getColumn(4)); //Total
        kredit.add(columns.getColumn(5)); //Aktual
        
        header.addGroup(tunai);
        header.addGroup(kredit);
        jTable1.setTableHeader(header);
        
        MyTableCellEditor cEditor=new MyTableCellEditor();
        jTable1.getColumnModel().getColumn(3).setCellEditor(cEditor);
        jTable1.getColumnModel().getColumn(5).setCellEditor(cEditor);
        txtKasir.setText(MainForm.sUserName);
    }

    public void setConn(Connection con){
        this.conn=con;
    }
    
    private void udfLoadClosing(){
        try {
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(
                    "select * from fn_r_rpt_closing_trx('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate()) +"', "
                    + "'"+MainForm.sUserName+"') as (urut smallint, cara_bayar varchar, tunai double precision, kredit double precision)");
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getInt("urut"),
                    rs.getString("cara_bayar"),
                    rs.getDouble("tunai"),
                    rs.getDouble("tunai"),
                    rs.getDouble("kredit"),
                    rs.getDouble("kredit")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(DlgClosingTrx.class.getName()).log(Level.SEVERE, null, ex);
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
    private GeneralFunction fn=new GeneralFunction();
    
    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text=ustTextField;

        int col, row;

        private NumberFormat  nf=NumberFormat.getNumberInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           //text.addKeyListener(kListener);
           //text.setEditable(!sudahTerbayar);
           col=vColIndex;
           row=rowIndex;
           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFoculListener);
           
           if(col==jTable1.getColumnModel().getColumnIndex("Selisih")){
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
               }
           if (isSelected) {

           }
           //System.out.println("Value dari editor :"+value);
            text.setText(value==null? "": value.toString());
            //component.setText(df.format(value));

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
                text.setText(fn.intFmt.format(fn.udfGetDouble(value)));
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                retVal = GeneralFunction.udfGetDouble(((JTextField)text).getText());
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

    }
    Color g1 = new Color(153,255,255);
    Color g2 = new Color(255,255,255);

    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255);

    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255);
    
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
            ((JTextField)c).setText(GeneralFunction.dFmt.format(GeneralFunction.udfGetDouble(((JTextField)c).getText())));

        }
   };
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        txtKasir = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnProses = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Keterangan", "Aktual", "Selisih", "Aktual", "Selisih"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 67, 520, 150));

        jLabel1.setText("Catatan");
        jLabel1.setName("jLabel1"); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 50, 20));

        jLabel2.setText("Kasir");
        jLabel2.setName("jLabel2"); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 20));

        txtCatatan.setName("txtCatatan"); // NOI18N
        getContentPane().add(txtCatatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 35, 470, -1));

        txtKasir.setEnabled(false);
        txtKasir.setName("txtKasir"); // NOI18N
        getContentPane().add(txtKasir, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 80, -1));

        btnCancel.setText("Batal");
        btnCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 220, 80, -1));

        btnProses.setText("Proses");
        btnProses.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnProses.setName("btnProses"); // NOI18N
        getContentPane().add(btnProses, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 80, -1));

        btnPrint.setText("Cetak");
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setName("btnPrint"); // NOI18N
        getContentPane().add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 80, -1));

        jLabel3.setText("Tanggal");
        jLabel3.setName("jLabel3"); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 50, 20));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePicker1ActionPerformed(evt);
            }
        });
        getContentPane().add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 130, -1));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-559)/2, (screenSize.height-291)/2, 559, 291);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try{
            ResultSet rs=conn.createStatement().executeQuery("select current_date");
            if(rs.next())
                jXDatePicker1.setDate(rs.getDate(1));
            
            rs.close();
        }catch(SQLException s){
            JOptionPane.showMessageDialog(this, s.getMessage());
        }
        udfLoadClosing();
    }//GEN-LAST:event_formWindowOpened

    private void jXDatePicker1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePicker1ActionPerformed
        udfLoadClosing();
    }//GEN-LAST:event_jXDatePicker1ActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                DlgClosingTrx dialog = new DlgClosingTrx(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnProses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtKasir;
    // End of variables declaration//GEN-END:variables
}
