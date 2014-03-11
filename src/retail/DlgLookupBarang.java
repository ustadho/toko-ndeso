/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgLookupBarang.java
 *
 * Created on Mar 8, 2009, 8:38:29 AM
 */

package retail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import retail.main.ListRsbm;

/**
 *
 * @author ustadho
 */
public class DlgLookupBarang extends javax.swing.JDialog {
    private Connection conn;
    private DefaultTableModel myModel;
    public static String srcKodeBarang="";
    JTextField textEditor=new JTextField("");
    TableRowSorter<TableModel> sorter;

    /** Creates new form DlgLookupBarang */
    public DlgLookupBarang(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1024)/2, (screenSize.height-768)/2, 1024, 768);

        sorter = new TableRowSorter<TableModel>(jTable1.getModel());
        jTable1.setRowSorter(sorter);

        //jTable1.setRowSelectionInterval(0, 0);
        //jTable1.getColumnModel().getColumn(0).setCellEditor(createExampleEditor());
        jTable1.getColumnModel().getColumn(0).setCellEditor(new MyTableCellEditor());
        jTable1.getTableHeader().setSize(jTable1.getWidth(), 33);
        jTable1.setSurrendersFocusOnKeystroke(true);

        jTable1.setRowHeight(25);
        jTable1.setGridColor(new Color(1));
        jTable1.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.QUICKSILVER));
        
        jTable1.changeSelection(0, 0, true, true);
        jTable1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        jTable1.setSelectionForeground(new Color(255, 255, 255));

        MyKeyListener kListener=new MyKeyListener();

        txtCari.addKeyListener(kListener);
        jTable1.addKeyListener(kListener);
        this.addKeyListener(kListener);
        //udfClear();
        //for(int i=1; i<=100; i++){
        
    }

    void setConn(Connection con){
        this.conn=con;
    }

    void setSrcKodeBarang(String s){
        srcKodeBarang=s;
    }

    String getKodeBarang(){
        return srcKodeBarang;
    }

    public void udfClear() {
        myModel=(DefaultTableModel)jTable1.getModel();
        myModel.setNumRows(0);
        jTable1.setModel(myModel);
        
        myModel.addRow(new Object[]{ "", "", "", "" });
        jTable1.setRowSelectionInterval(0, 0);
    }

    void tampilkan(String sKodeBarang) {
        this.srcKodeBarang=sKodeBarang;
        this.setVisible(true);
    }

    private void udfFilter() {
        //if(cmbJenisSimpanan.getSelectedIndex()<0) return;
        String text = "";
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
            sorter.setRowFilter(
                RowFilter.regexFilter(text));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern");
            }
        }
        if(jTable1.getRowCount()>0) jTable1.setRowSelectionInterval(0, 0);

    }

    private void udfLoadBarang(String s){
        try{
            String sQry="Select kode_item,  coalesce(nama_item, '') as nama_item, " +
                    "coalesce(unit_jual,'') as sat_jual, coalesce(harga_jual,0)*coalesce(konv_jual,0) as harga_jual " +
                    "from vw_r_item_trx where (kode_item||coalesce(nama_item,'')) iLike '%"+s+"%' " +
                    "order by nama_item ";

            //where (kode_item||coalesce(nama_item,'')) iLike '%"+s+"%'

            ResultSet rs=conn.createStatement().executeQuery(sQry);

            //System.out.println(sQry);
            //if(jTable1.getSelectedRow() >=0){
                udfClear();
                myModel.setNumRows(0);
                while(rs.next()){
                    myModel.addRow(new Object[]{
                        rs.getString("kode_item"),
                        rs.getString("nama_item"),
                        rs.getString("sat_jual"),
                        rs.getDouble("harga_jual")
                    });
                }
//                if(myModel.getRowCount()==0)
//                    //myModel.addRow(new Object[]{"", "", "", ""});
//                else{
                    if(myModel.getRowCount()>0) jTable1.setRowSelectionInterval(0, 0);

               //}


            //}

        }catch(SQLException se){
            JOptionPane.showMessageDialog(DlgLookupBarang.this, se.getMessage());
        }
    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField component=new JTextField("");
        JLabel label;// =new JLabel("");

        int col, row;

        private NumberFormat  nf=NumberFormat.getInstance();

        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

           col=vColIndex;
           row=rowIndex;
           component.setBackground(new Color(0,255,204));
           component.setFont(new Font("Tahoma",Font.PLAIN,15));
           component.selectAll();

           component.addKeyListener(new java.awt.event.KeyAdapter() {
              public void keyReleased(java.awt.event.KeyEvent evt) {
                    switch(evt.getKeyCode()){
                        case KeyEvent.VK_ENTER:{
                            udfLoadBarang(component.getText());
                            component.requestFocusInWindow();
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                }
           });

           //System.out.println("Value dari editor :"+value);
            //component.setText(value==null? "": value.toString());
            component.setText("");
           return component;
        }

        

        public Object getCellEditorValue() {
            component.setVisible(false);
            Object o="";//=component.getText();
            Object retVal = 0;
                retVal=(Object)component.getText();
                //JOptionPane.showMessageDialog(null, "Kode Barang : "+retVal.toString());
                
                if(retVal.toString().trim().length()>0){
//////                    try{
//////                        String sQry="Select kode_item,  coalesce(nama_item, '') as nama_item, " +
//////                                "coalesce(unit_jual,'') as sat_jual, coalesce(harga_jual,0)*coalesce(konv_jual,0) as harga_jual " +
//////                                "from vw_r_item_trx where (kode_item||coalesce(nama_item,'')) iLike '%"+retVal.toString()+"%'";
//////
//////                        ResultSet rs=conn.createStatement().executeQuery(sQry);
//////
//////                        ///System.out.println(sQry);
//////                        //if(jTable1.getSelectedRow() >=0){
//////                            udfClear();
//////                            myModel.setNumRows(0);
//////                            while(rs.next()){
//////                                myModel.addRow(new Object[]{
//////                                    rs.getString("kode_item"),
//////                                    rs.getString("nama_item"),
//////                                    rs.getString("sat_jual"),
//////                                    rs.getDouble("harga_jual")
//////                                });
//////                            }
//////                            if(myModel.getRowCount()==0)
//////                                myModel.addRow(new Object[]{"", "", "", ""});
//////                            else{
//////                                jTable1.setRowSelectionInterval(0, 0);
//////                                component.setFocusable(false);
//////                            }
//////
//////
//////                        //}
//////
//////                    }catch(SQLException se){
//////                        JOptionPane.showMessageDialog(DlgLookupBarang.this, se.getMessage());
//////                    }
//                }else{
//
                    udfLoadBarang(retVal.toString());
                }
            //return (Object)myModel.getValueAt(row, 0).toString(); //retVal;
            return (Object)retVal;
        }

    }

    private void udfSelectedKodeBarang(){
        if(jTable1.getSelectedRow()<0) return;
        srcKodeBarang=  jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString();
        this.dispose();
    }

    public class MyKeyListener extends KeyAdapter {
        ListRsbm lst;

        public void setListRsbm(ListRsbm l){
            this.lst=l;
        }

        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
            
            case KeyEvent.VK_F3: {  //Edit
                udfClear();
                break;
            }
            case KeyEvent.VK_ENTER: {  //Edit
//                if(evt.getSource().equals(component)){
//
//                }
                break;
            }
            case KeyEvent.VK_F12: {  //Delete
                udfSelectedKodeBarang();
                break;
            }
            case KeyEvent.VK_ESCAPE: {
                srcKodeBarang="";
                dispose();
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
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new org.jdesktop.swingx.JXTable();
        txtCari = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblJumlahStok = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("F12 - Pilih item / barang");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 190, 20));

        jLabel2.setText("F3   - Bersihkan layar");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 190, 20));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Kode", "Keterangan", "Satuan", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        txtCari.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Cari");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Informasi Stok"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblJumlahStok.setText("Stok");
        jPanel2.add(lblJumlahStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 100, 20));

        jLabel6.setText("Stok");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 80, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCari, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-688)/2, (screenSize.height-528)/2, 688, 528);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //udfClear();
        udfLoadBarang("");
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_DOWN && jTable1.getRowCount()>0) {
            jTable1.setRowSelectionInterval(0, 0);
            jTable1.requestFocus();
        }else
            udfLoadBarang(txtCari.getText());
    }//GEN-LAST:event_txtCariKeyReleased

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        if(evt.getKeyCode()==KeyEvent.VK_UP && jTable1.getSelectedRow()==0)
            txtCari.requestFocus();

    }//GEN-LAST:event_jTable1KeyReleased

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgLookupBarang dialog = new DlgLookupBarang(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXTable jTable1;
    private javax.swing.JLabel lblJumlahStok;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables

}
