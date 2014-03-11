/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmUserManagement.java
 *
 * Created on Oct 4, 2010, 11:33:23 AM
 */

package retail.main;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author ustadho
 */
public class FrmUserManagement extends javax.swing.JInternalFrame {
    private Connection conn;
    GeneralFunction fn;
    /** Creates new form FrmUserManagement */
    public FrmUserManagement() {
        initComponents();

        tblUser.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btnDelete.setEnabled(tblUser.getSelectedRow()>=0);
                btnUpdate.setEnabled(tblUser.getSelectedRow()>=0);

                if(conn==null || tblUser.getSelectedRow()<0) return;
                TableColumnModel col=tblUser.getColumnModel();

                try{
                    String s="select u.user_id, u.username, " +
                            "photo, ttd_electronic, coalesce(profile, -1) as profile " +
                            "from m_user u " +
                            "where username ='"+tblUser.getValueAt(tblUser.getSelectedRow(), col.getColumnIndex("Username")).toString()+"' ";

                    ResultSet rs=conn.createStatement().executeQuery(s);
                    if(rs.next()){
                        txtUserID.setEnabled(false);
                        txtUserID.setText(rs.getString("user_id"));
                        txtUsername.setText(rs.getString("username"));
                        cmbUserProfile.setSelectedIndex(rs.getInt("profile"));
                        byte[] imgBytes = rs.getBytes("photo");
                        byte[] imgSign = rs.getBytes("ttd_electronic");

                        if(imgBytes!=null){
                            javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(imgBytes);
                            javax.swing.ImageIcon bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
                                               (lblPhoto.getWidth(), lblPhoto.getHeight(), Image.SCALE_REPLICATE));

                            lblPhoto.setIcon(bigImage);
                            imgBytes=null;
                        }else{
                            lblPhoto.setIcon(null);
                        }
                        if(imgSign!=null){
                            javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(imgSign);
                            javax.swing.ImageIcon bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
                                               (lblSign.getWidth(), lblSign.getHeight(), Image.SCALE_REPLICATE));

                            lblSign.setIcon(bigImage);
                            imgSign=null;
                        }else{
                            lblSign.setIcon(null);
                        }
                    }
                    rs.close();
                }catch(SQLException se){
                    JOptionPane.showMessageDialog(FrmUserManagement.this, se.getMessage());
                }

            }
        });

    }

    public void setConn(Connection con){
        this.conn=con;
        fn=new GeneralFunction(con);
    }

    private void udfDelete(){
        int iRow=tblUser.getSelectedRow();
        if(iRow<0) return;
        if(JOptionPane.showConfirmDialog(this, "Anda yakin untuk menghapus user ini?", "Delete user", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                conn.setAutoCommit(false);
                conn.createStatement().executeUpdate("delete from m_user  " +
                            "where username ='"+tblUser.getValueAt(tblUser.getSelectedRow(), tblUser.getColumnModel().getColumnIndex("Username")).toString()+"'");
                conn.setAutoCommit(true);
                ((DefaultTableModel)tblUser.getModel()).removeRow(iRow);
            }catch(SQLException se){
                try {
                    conn.setAutoCommit(true);
                    conn.rollback();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, se.getMessage());
                }
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
    }

    private void udfInitForm(){
        ((DefaultTableModel)tblUser.getModel()).setNumRows(0);
        try{
            ResultSet rs=conn.createStatement().executeQuery("select user_id, username from m_user " +
                    "where user_id||username ilike '%"+txtSearch.getText()+"%'" +
                    "order by username ");
            while(rs.next()){
                ((DefaultTableModel)tblUser.getModel()).addRow(new Object[]{
                    rs.getString("user_id"),
                    rs.getString("username")
                });
            }
            if(tblUser.getRowCount()>0)
                tblUser.setRowSelectionInterval(0, 0);
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

        jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUser = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtUserID = new javax.swing.JTextField();
        lblPhoto = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtPasswordAgain = new javax.swing.JPasswordField();
        txtPassword = new javax.swing.JPasswordField();
        lblSign = new javax.swing.JLabel();
        cmbUserProfile = new javax.swing.JComboBox();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnDelete1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("User Management");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jXTitledPanel1.setTitle("User List");
        jXTitledPanel1.getContentContainer().setLayout(new javax.swing.BoxLayout(jXTitledPanel1.getContentContainer(), javax.swing.BoxLayout.LINE_AXIS));

        tblUser.setAutoCreateRowSorter(true);
        tblUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Username"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUser.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblUserKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblUser);

        jXTitledPanel1.getContentContainer().add(jScrollPane1);

        getContentPane().add(jXTitledPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 240, 370));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("User Detail"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Username :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 55, 70, 20));

        jLabel2.setText("User ID :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 70, 20));
        jPanel1.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 55, 210, -1));
        jPanel1.add(txtUserID, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 110, -1));

        lblPhoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPhoto.setText("Employee Photo"); // NOI18N
        lblPhoto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPhoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPhotoMouseClicked(evt);
            }
        });
        jPanel1.add(lblPhoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 180, 130, 180));

        jLabel7.setText("Password (again) :");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 90, 20));

        jLabel10.setText("User Profile :");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 90, 20));

        jLabel11.setText("Password :");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 90, 20));
        jPanel1.add(txtPasswordAgain, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 155, 160, -1));
        jPanel1.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 160, -1));

        lblSign.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSign.setText("Employee Sign"); // NOI18N
        lblSign.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblSign.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSignMouseClicked(evt);
            }
        });
        jPanel1.add(lblSign, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 280, 190, 80));

        cmbUserProfile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Administrator", "Supervisor", "User" }));
        jPanel1.add(cmbUserProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 105, 170, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 370, 370));

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 390, 70, -1));

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 390, 80, -1));

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 390, 80, -1));

        jLabel3.setText("Search :");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 60, 20));

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 390, 160, -1));

        btnDelete1.setText("New");
        btnDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnDelete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 390, 60, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblPhotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPhotoMouseClicked
        if(evt.getButton()==MouseEvent.BUTTON1){
            PilihFoto("foto");
        }
}//GEN-LAST:event_lblPhotoMouseClicked

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        udfSave();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void lblSignMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSignMouseClicked
        if(evt.getButton()==MouseEvent.BUTTON1){
            PilihFoto("sign");
        }
    }//GEN-LAST:event_lblSignMouseClicked

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        udfInitForm();
    }//GEN-LAST:event_txtSearchKeyReleased

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        udfDelete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblUserKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_DELETE)
            udfDelete();
        
    }//GEN-LAST:event_tblUserKeyPressed

    private void btnDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete1ActionPerformed
        udfNew();
        txtUserID.requestFocus();
    }//GEN-LAST:event_btnDelete1ActionPerformed

    private void PilihFoto(String sFrom) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        FileInputStream in = null;
        try {
            m_chooser.setDialogTitle(sFrom.equalsIgnoreCase("foto")? "Pilih foto": "Tanda tangan");
            m_chooser.setFileFilter(fFilter);
            if (m_chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File fChoosen = m_chooser.getSelectedFile();
            in = new FileInputStream(fChoosen);
            javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(m_chooser.getSelectedFile().toString());
            javax.swing.ImageIcon bigImage ;

            if(sFrom.equalsIgnoreCase("foto")){
                bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
                               (lblPhoto.getWidth(), lblPhoto.getHeight(), Image.SCALE_REPLICATE));
                sFotoFile=m_chooser.getSelectedFile().toString();
                fisFoto = in;
                lblPhoto.setIcon(bigImage);
            }else if(sFrom.equalsIgnoreCase("sign")){
                bigImage = new javax.swing.ImageIcon(myIcon.getImage().getScaledInstance
                               (lblSign.getWidth(), lblSign.getHeight(), Image.SCALE_REPLICATE));

                sSignFile=m_chooser.getSelectedFile().toString();
                fisSginature = in;
                lblSign.setIcon(bigImage);
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            in.close();

        } catch (IOException ex) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } finally {
            try {
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                if (in != null) in.close();
            } catch (IOException ex) {
                Logger.getLogger(FrmUserManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private FileFilter fFilter=new FileFilter() {

        @Override
        public boolean accept(File f) {
            return f.getName ().toLowerCase ().endsWith (".jpg")
          || f.isDirectory ();
        }

        @Override
        public String getDescription() {
            return "Image files (*.jpg)";
        }
    };


    private void udfNew(){
        txtUserID.setText("");
        txtUsername.setText("");
        cmbUserProfile.setSelectedIndex(2);
        txtPassword.setText("");
        txtPasswordAgain.setText("");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDelete1;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox cmbUserProfile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;
    private javax.swing.JLabel lblPhoto;
    private javax.swing.JLabel lblSign;
    private javax.swing.JTable tblUser;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtPasswordAgain;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUserID;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

    private JFileChooser m_chooser = new JFileChooser();
    private String sFotoFile="", sSignFile="";
    FileInputStream fisFoto, fisSginature;

    private void udfSave()  {
        boolean isNew=false;
        {
            FileInputStream fis = null;
            try {
                conn.setAutoCommit(false);
                ResultSet rs = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from m_user where user_id='" + txtUserID.getText() + "'");
                if (!rs.next()) {
                    isNew = true;
                    rs.moveToInsertRow();
                }
                rs.updateString("user_id", txtUserID.getText());
                rs.updateString("username", txtUsername.getText());
                if (sFotoFile.length() > 0) {
                    File file;
                    file = new File(sFotoFile);
                    fis = new FileInputStream(file);
                    rs.updateBinaryStream("photo", fis, (int) file.length());
                }
                if (sSignFile.length() > 0) {
                    File file;
                    file = new File(sSignFile);
                    fis = new FileInputStream(file);
                    rs.updateBinaryStream("ttd_electronic", fis, (int) file.length());
                }
                if (isNew) {
                    rs.insertRow();
                } else {
                    rs.updateRow();
                }
                String pass = "";
                char[] chrPass = txtPassword.getPassword();
                for (int i1 = 0; i1 < chrPass.length; i1++) {
                    pass = pass + chrPass[i1];
                    chrPass[i1] = '0';
                }
                int i=0;
                if(pass.length() >0)
                    i = conn.createStatement().executeUpdate("update m_user set password=md5('" + pass + "') " + "where user_id='" + txtUserID.getText() + "'; ");

                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, "Insert/ Update user sukses!");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FrmUserManagement.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException se) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    JOptionPane.showMessageDialog(this, se.getMessage());
                } catch (SQLException ex) {
                    Logger.getLogger(FrmUserManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
            } finally {
                try {
                    if(fis!=null)   fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(FrmUserManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
