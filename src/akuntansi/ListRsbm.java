/*
 * ListRsbm.java
 *
 * Created on May 27, 2005, 8:18 PM
 */

package akuntansi;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.*;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;
/**
 *
 * @author  root
 */
public class ListRsbm extends javax.swing.JFrame {
    static Connection con;
    private ResultSet rs;
    private String sQuery;
    private Object[] oResult;
    private javax.swing.table.DefaultTableModel myModel;
    private javax.swing.JTextField txtCari;
    private javax.swing.JLabel[] lblDes;
    private javax.swing.JComponent[] compDes;
    private javax.swing.JTextField resultString;
    private int iRowCount = 0;
    //private javax.swing.JTextField txtCari;
    private int iPosRow = 0;
    /** Creates new form ListRsbm */
    public ListRsbm() {
        initComponents();
   }

    public ListRsbm(String newQry)  throws SQLException {
        initComponents();
        setSQuery(newQry);
        tblList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        
    }

    public void setConn(Connection conn) {
        this.con=conn;
    }


    public void setLblDes2(JComponent[] component) {
        this.compDes=component;
    }
    
    public void udfRemoveColumn(int col){
        tblList.removeColumn(tblList.getColumnModel().getColumn(col));
    }
    
    public void setRs(ResultSet newRS)  throws SQLException {
        rs = newRS;
        setMyModel(rs);
    }
    
    
    public ResultSet getRs() {
        return rs;
    }
    
    public void setSQuery(String newQry) throws SQLException{
        sQuery = newQry;        
        Statement st = con.createStatement();
        rs = st.executeQuery(sQuery);
        setMyModel(rs);
        rs.close();
        st.close();
    }

    public int getVScrollWidth(){
        return jScrollPane1.getVerticalScrollBar().getWidth();
    }

    public String getSQuery(){
        return sQuery;
    }
    
    public void setLblDes(javax.swing.JLabel[] newlbl){
        lblDes = newlbl;
    }
   
    public void setCompDes(javax.swing.JComponent[] newComp){
	compDes = newComp;
    }
    
    public void setTxtCari(javax.swing.JTextField newTxt){
        txtCari = newTxt;
    }
    
    public void setResultDesc(javax.swing.JTextField newtext){
        resultString=newtext;
    }
    
    public int getIRowCount(){
        return iRowCount;
    }
    
    public int getRowHeight(){
        return tblList.getRowHeight();
    }
    
    
    public void setMyModel(ResultSet newRS)  throws SQLException {
       
        myModel = new javax.swing.table.DefaultTableModel();
        tblList.setModel(myModel);
            
            for(int i=1;i <= rs.getMetaData().getColumnCount();i++) {
                myModel.addColumn(rs.getMetaData().getColumnName(i));
                
            }
            iRowCount = 0;
            while (newRS.next()) {
                Object arObj[] = new Object[newRS.getMetaData().getColumnCount()];
                for(int i=1;i <= newRS.getMetaData().getColumnCount();i++) {
                    if(newRS.getObject(i) != null){
                        arObj[i-1]=newRS.getObject(i);
                    } else {                        
                        arObj[i-1]=new Object();
                    }
                }
                myModel.addRow(arObj);
                iRowCount++;
            }            
            if (tblList.getRowCount()>0) {
                tblList.setRowSelectionInterval(0,0) ;
            } else{
                this.setVisible(false);
            }
            newRS.close();    
            //jScrollPane1.setBounds(jScrollPane1.getX(),jScrollPane1.getY(),jScrollPane1.getWidth(),iRowCount*tblList.getRowHeight());
            setBounds(getX(),getY(), getWidth(),(iRowCount+2)*tblList.getRowHeight());
}
    
    public Object[] getOResult() {
        Object[] oSelected =new Object[myModel.getColumnCount()];
        
        if (myModel.getRowCount()>0) {        
            if (iPosRow == 0) {iPosRow =tblList.getSelectedRow();}
            if (iPosRow>myModel.getRowCount()) {iPosRow=0;}
            for(int i=1;i <= myModel.getColumnCount();i++) {
                oSelected[i-1] = myModel.getValueAt(iPosRow, i-1);
            }
        }
        oResult = oSelected;
        return oResult;
    }
    
    public JTextField getTxtCari(){
        return txtCari;
    }

    void setRemoveCol(int i) {
        tblList.removeColumn(tblList.getColumnModel().getColumn(i));
    }
    
    void udfSetSelectedValue(){
        
        if(iPosRow>=0){
            for (int i=0;i<lblDes.length;i++){
                lblDes[i].setText(myModel.getValueAt(iPosRow, i+1).toString());                
            }
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblList = new javax.swing.JTable();

        setAlwaysOnTop(true);
        setResizable(false);
        setState(2);
        setUndecorated(true);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tblList.setAutoCreateRowSorter(true);
        tblList.setForeground(new java.awt.Color(0, 0, 153));
        tblList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblList.setSelectionBackground(new java.awt.Color(0, 51, 255));
        tblList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblListMouseClicked(evt);
            }
        });
        tblList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblListFocusLost(evt);
            }
        });
        tblList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblListKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblList);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
// TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
      
    }//GEN-LAST:event_formFocusLost

    private void tblListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblListFocusLost
//        this.setVisible(false);
    }//GEN-LAST:event_tblListFocusLost

    private void tblListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblListMouseClicked
        udfSelected();
	    
    }//GEN-LAST:event_tblListMouseClicked

    private void tblListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblListKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {                       
          udfSelected();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP && tblList.getSelectedRow()==0){
            txtCari.requestFocus();
        }         
        
    }//GEN-LAST:event_tblListKeyPressed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        tblList.requestFocus();
    }//GEN-LAST:event_formFocusGained
    
    public void setColWidth(int ColIndex, int ColWidth) {                        
        tblList.getColumnModel().getColumn(ColIndex).setPreferredWidth(ColWidth);
    }
 private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {
//     if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
//            iPosRow =tblList.getSelectedRow();
//            this.setVisible(false);
//            txtCari.setText(tblList.getValueAt(tblList.getSelectedRow(), 0).toString());                            
//            txtCari.requestFocus();            
//            for (int i=0;i<lblDes.length;i++){
//                lblDes[i].setText(tblList.getValueAt(iPosRow, i+1).toString());                
//            }         
//     }
 }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblList;
    // End of variables declaration//GEN-END:variables

    public void udfSelected() {
        iPosRow =tblList.getSelectedRow();
        this.setVisible(false);
        txtCari.setText(myModel.getValueAt(iPosRow, 0).toString());
        txtCari.requestFocus();
//            for (int i=0;i<lblDes.length;i++){
//                lblDes[i].setText(myModel.getValueAt(iPosRow, i+1).toString());
//            }

        for (int i=0;i<compDes.length;i++){
            if(compDes[i].getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD"))
                ((JTextField)compDes[i]).setText(myModel.getValueAt(iPosRow, i+1).toString());
            else if (compDes[i].getClass().getSimpleName().equalsIgnoreCase("JLabel"))
                ((JLabel)compDes[i]).setText(myModel.getValueAt(iPosRow, i+1).toString());
            else if (compDes[i].getClass().getSimpleName().equalsIgnoreCase("JComboBox"))
                ((JComboBox)compDes[i]).setSelectedItem(myModel.getValueAt(iPosRow, i+1).toString());
//            else if (compDes[i].getClass().getSimpleName().equalsIgnoreCase("TableColumnModel"))
//                ((TableColumnModel)compDes[i]).set(myModel.getValueAt(iPosRow, i+1).toString());
        }
    }

    
    
}
