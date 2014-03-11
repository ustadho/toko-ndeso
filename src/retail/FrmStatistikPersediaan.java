/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmStatistikPenjualan.java
 *
 * Created on 06 Mei 11, 19:21:48
 */
package retail;

import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import retail.main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmStatistikPersediaan extends javax.swing.JInternalFrame {
    private Connection conn;
    ChartPanel chartPanel;
    private GeneralFunction fn=new GeneralFunction();
    
    /** Creates new form FrmStatistikPenjualan */
    public FrmStatistikPersediaan() {
        initComponents();
    }

    private void udfViewItemTerlaris(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sQry="select nama_item as \"Nama Barang\", sat_kecil as \"Satuan\", sum(qty) as \"Total\" "
                    + "from fn_r_rpt_sales_detail_kecil('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty double precision, "
                    + "sat_kecil varchar, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + "group by nama_item, sat_kecil "
                    + "order by sum(qty) desc limit "+fn.udfGetInt(txtLimit.getText());
            
            //System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.addValue(rs.getDouble(3), "Item", rs.getString(1));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Barang terlaris",       // chart title
            "Barang",               // domain axis label
            "Total",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        chartPanel = (ChartPanel) createPanel(chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    private void udfViewKategoriBarangTerlaris(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sQry="select coalesce(i.kategori,'') as \"Kategori\", sat_kecil as \"Satuan\", sum(qty) as \"Total\" "
                    + "from ("
                    + "select * from fn_r_rpt_sales_detail_kecil('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty double precision, "
                    + "sat_kecil varchar, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + ")x inner join r_item i on i.kode_item=x.kode_item "
                    + "group by coalesce(i.kategori,''), sat_kecil "
                    + "order by sum(qty) desc limit "+fn.udfGetInt(txtLimit.getText());
            
            //System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.addValue(rs.getDouble(3), "Kategori", rs.getString(1));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Kategori Barang terlaris",       // chart title
            "Kategori",               // domain axis label
            "Total",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        chartPanel = (ChartPanel) createPanel(chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    private void udfViewOmzetTertinggi(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sQry="select nama_item as \"Nama Barang\", sum(sub_total) as \"Total\" "
                    + "from fn_r_rpt_sales_detail('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty numeric, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + "group by nama_item "
                    + "order by sum(sub_total) desc limit "+fn.udfGetInt(txtLimit.getText());
            
            //System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.addValue(rs.getDouble(2), "Total Penjualan", rs.getString(1));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Omzet penjualan item tertinggi",       // chart title
            "Barang",               // domain axis label
            "Total",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        chartPanel = (ChartPanel) createPanel(chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    private void udfViewJmlNotaTertinggi(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sQry="select nama_item as \"Nama Barang\", count(distinct sales_no) as \"Total Nota\" "
                    + "from fn_r_rpt_sales_detail('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty numeric, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + "group by nama_item "
                    + "order by count(distinct sales_no) desc limit "+fn.udfGetInt(txtLimit.getText());
            
            //System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.addValue(rs.getDouble(2), "Total Nota Penjualan", rs.getString(1));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Barang dengan Transaksi Tertinggi",       // chart title
            "Barang",               // domain axis label
            "Total Nota",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        chartPanel = (ChartPanel) createPanel(chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    private void udfViewPenjualanPerJenisBayar(){
        DefaultPieDataset dataset=new DefaultPieDataset();
        try{
            String sQry="select jenis as \"Jenis Bayar\", sum(sub_total) as \"Total\" "
                    + "from fn_r_rpt_sales_detail('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty numeric, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + "group by jenis ";
            
            //System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.setValue(rs.getString(1), rs.getDouble(2));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart=ChartFactory.createPieChart("Penjualan per jenis pembayaran",dataset,true,true,false);

        
        chartPanel = new ChartPanel(chart);
        
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    public JPanel createPanel(JFreeChart chart) {
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
                0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }
    
    private void udfViewPenjualanPerJenisBayarPerHari(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sQry="select to_char(sales_date,'dd/MM/yy') as \"Tanggal\", "
                    + "sum(case when jenis='Tunai' then sub_total else 0 end) as \"Tunai\", "
                    + "sum(case when jenis='Kredit' then sub_total else 0 end) as \"Kredit\" "
                    + "from fn_r_rpt_sales_detail('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty numeric, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + "group by to_char(sales_date,'dd/MM/yy'), to_char(sales_date,'yy-MM-dd') "
                    + "order by to_char(sales_date,'yy-MM-dd')";
            
            System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.addValue(rs.getDouble(2), "Tunai", rs.getString(1));
                dataset.addValue(rs.getDouble(3), "Kredit", rs.getString(1));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Penjualan per Jenis Bayar per Hari",       // chart title
            "Tanggal",               // domain axis label
            "Total",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        chartPanel = (ChartPanel) createPanel(chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    private void udfViewPenjualanPerJenisBayarPerBulan(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sQry="select to_char(sales_date,'MM/yy') as \"Bulan\", "
                    + "sum(case when jenis='Tunai' then sub_total else 0 end) as \"Tunai\", "
                    + "sum(case when jenis='Kredit' then sub_total else 0 end) as \"Kredit\" "
                    + "from fn_r_rpt_sales_detail('"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker1.getDate())+"', "
                    + "'"+new SimpleDateFormat("yyyy-MM-dd").format(jXDatePicker2.getDate())+"', '', '', '') as (jenis text, sales_date date, kode_cust varchar, "
                    + "nama varchar, sales_no varchar, kode_item varchar, nama_item varchar, qty numeric, unit_price double precision,"
                    + "disc double precision, tax double precision, sub_total double precision) "
                    + "group by to_char(sales_date,'MM/yy'), to_char(sales_date,'yy-MM') "
                    + "order by to_char(sales_date,'yy-MM')";
            
            //System.out.println(sQry);
            ResultSet  rs=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sQry);
            while(rs.next()){
                dataset.addValue(rs.getDouble(2), "Tunai", rs.getString(1));
                dataset.addValue(rs.getDouble(3), "Kredit", rs.getString(1));
            }
            setModel(rs);
            
            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(null, se.getMessage());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Penjualan per Jenis Bayar per Bulan",       // chart title
            "Bulan",               // domain axis label
            "Total",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        
        chartPanel = (ChartPanel) createPanel(chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.validate();
    }
    
    private static JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Legal & General Unit Trust Prices",  // title
            "Date",             // x-axis label
            "Price Per Unit",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

        return chart;

    }
    
    public void setModel(ResultSet rs)  {
         try{
             rs.beforeFirst();
            Object colName[] = new Object[rs.getMetaData().getColumnCount()];
            final Class[] types2 = new Class [rs.getMetaData().getColumnCount()];

            for(int i=0;i < rs.getMetaData().getColumnCount();i++) {
                //myModel.addColumn(rs.getMetaData().getColumnName(i));
                colName[i]=rs.getMetaData().getColumnName(i+1);
                if(rs.getMetaData().getColumnType(i+1)== java.sql.Types.DOUBLE ||
                        rs.getMetaData().getColumnType(i+1)== java.sql.Types.FLOAT||
                        rs.getMetaData().getColumnType(i+1)== java.sql.Types.REAL||
                        rs.getMetaData().getColumnType(i+1)== java.sql.Types.NUMERIC)
                    types2[i]=java.lang.Double.class;
                else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.INTEGER)
                    types2[i]=java.lang.Integer.class;
                else if(rs.getMetaData().getColumnType(i+1)==java.sql.Types.DATE)
                    types2[i]=java.lang.Object.class;
            }

            tblList.setModel(new javax.swing.table.DefaultTableModel(
                null,
                colName
            ) {
                Class[] types = types2;
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }
            });
            DefaultTableModel myModel = (DefaultTableModel)tblList.getModel();

            while (rs.next()) {
                Object arObj[] = new Object[rs.getMetaData().getColumnCount()];
                for(int i=1;i <= rs.getMetaData().getColumnCount();i++) {
                    if(rs.getObject(i) != null){
                        if(rs.getMetaData().getColumnType(i)==java.sql.Types.DOUBLE||
                                rs.getMetaData().getColumnType(i)==java.sql.Types.FLOAT||
                                rs.getMetaData().getColumnType(i)==java.sql.Types.REAL||
                                rs.getMetaData().getColumnType(i)==java.sql.Types.NUMERIC)
                            arObj[i-1]=rs.getDouble(i);
                        if(rs.getMetaData().getColumnType(i)==java.sql.Types.INTEGER)
                            arObj[i-1]=rs.getInt(i);
                        if(rs.getMetaData().getColumnType(i)==java.sql.Types.DATE)
                            arObj[i-1]=rs.getDate(i);
                        else
                            arObj[i-1]=rs.getObject(i);
                    } else {

                        arObj[i-1]=new Object();
                    }
                }
                myModel.addRow(arObj);

            }
            if (tblList.getRowCount()>0) {
                tblList.setRowSelectionInterval(0,0) ;
            } else{
                this.setVisible(false);
            }
            tblList.setModel((DefaultTableModel)fn.autoResizeColWidth(tblList, (DefaultTableModel)tblList.getModel()).getModel());
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

        jLabel1 = new javax.swing.JLabel();
        card = new javax.swing.JPanel();
        panel1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblList = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtLimit = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Statistik Persediaan");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("STATISTIK DAN ANALISA DATA STOK BARANG");
        jLabel1.setName("jLabel1"); // NOI18N

        card.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        card.setName("card"); // NOI18N
        card.setLayout(new java.awt.CardLayout());

        panel1.setName("panel1"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblList.setName("tblList"); // NOI18N
        tblList.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblList);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        card.add(panel1, "card2");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Barang Terlaris", "Kategori Barang Terlaris", "Barang dengan Omzet Tertinggi", "Barang dengan Jumlah Transaksi Tertinggi", "Data Stok Minimum", "Data Stok Tertinggi" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jPanel2.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 310, -1));

        jXDatePicker1.setName("jXDatePicker1"); // NOI18N
        jPanel2.add(jXDatePicker1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, 110, -1));

        jXDatePicker2.setName("jXDatePicker2"); // NOI18N
        jPanel2.add(jXDatePicker2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 120, -1));

        jButton1.setText("Tampilkan");
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 20, 90, -1));

        jLabel2.setForeground(new java.awt.Color(0, 0, 102));
        jLabel2.setText("Kategori :");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 340, 20));

        jLabel3.setForeground(new java.awt.Color(0, 0, 102));
        jLabel3.setText("Dari :");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, 140, 20));

        jLabel4.setForeground(new java.awt.Color(0, 0, 102));
        jLabel4.setText("Limit Item:");
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 70, 20));

        jLabel5.setForeground(new java.awt.Color(0, 0, 102));
        jLabel5.setText("Sampai :");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 0, 120, 20));

        txtLimit.setText("20");
        txtLimit.setName("txtLimit"); // NOI18N
        txtLimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLimitKeyTyped(evt);
            }
        });
        jPanel2.add(txtLimit, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 60, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addComponent(card, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(card, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        switch(jComboBox1.getSelectedIndex()){
            case 0:{
                udfViewItemTerlaris();
                break;
            }
            case 1:{
                udfViewKategoriBarangTerlaris();
                break;
            }
            case 2:{
                udfViewOmzetTertinggi();
                break;
            }
            case 3:{
                udfViewJmlNotaTertinggi();
                break;
            }
            default:{
                break;
            }    
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtLimitKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLimitKeyTyped
        fn.keyTyped(evt);
    }//GEN-LAST:event_txtLimitKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel card;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JPanel panel1;
    private org.jdesktop.swingx.JXTable tblList;
    private javax.swing.JTextField txtLimit;
    // End of variables declaration//GEN-END:variables

    public void setConn(Connection conn) {
        this.conn=conn;
        fn.setConn(conn);
    }
}
