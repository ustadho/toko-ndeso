/*
 * Generated by JasperReports - 4/1/09 8:18 PM
 */
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.fill.*;

import java.util.*;
import java.math.*;
import java.text.*;
import java.io.*;
import java.net.*;

import net.sf.jasperreports.engine.*;
import java.util.*;
import net.sf.jasperreports.engine.data.*;


/**
 *
 */
public class mutasi_item_1238591899437_887293 extends JREvaluator
{


    /**
     *
     */
    private JRFillParameter parameter_REPORT_LOCALE = null;
    private JRFillParameter parameter_REPORT_TIME_ZONE = null;
    private JRFillParameter parameter_REPORT_VIRTUALIZER = null;
    private JRFillParameter parameter_REPORT_FILE_RESOLVER = null;
    private JRFillParameter parameter_REPORT_SCRIPTLET = null;
    private JRFillParameter parameter_REPORT_PARAMETERS_MAP = null;
    private JRFillParameter parameter_REPORT_CONNECTION = null;
    private JRFillParameter parameter_REPORT_CLASS_LOADER = null;
    private JRFillParameter parameter_REPORT_DATA_SOURCE = null;
    private JRFillParameter parameter_REPORT_URL_HANDLER_FACTORY = null;
    private JRFillParameter parameter_IS_IGNORE_PAGINATION = null;
    private JRFillParameter parameter_REPORT_FORMAT_FACTORY = null;
    private JRFillParameter parameter_kode_item = null;
    private JRFillParameter parameter_REPORT_MAX_COUNT = null;
    private JRFillParameter parameter_REPORT_TEMPLATES = null;
    private JRFillParameter parameter_tanggal1 = null;
    private JRFillParameter parameter_kategori = null;
    private JRFillParameter parameter_tanggal2 = null;
    private JRFillParameter parameter_alamat = null;
    private JRFillParameter parameter_nama_koperasi = null;
    private JRFillParameter parameter_telp = null;
    private JRFillParameter parameter_REPORT_RESOURCE_BUNDLE = null;
    private JRFillField field_beli = null;
    private JRFillField field_jenis_barang = null;
    private JRFillField field_jual = null;
    private JRFillField field_item_code = null;
    private JRFillField field_sisa = null;
    private JRFillField field_r_beli = null;
    private JRFillField field_item_name = null;
    private JRFillField field_r_jual = null;
    private JRFillField field_s_awal = null;
    private JRFillField field_opname = null;
    private JRFillVariable variable_PAGE_NUMBER = null;
    private JRFillVariable variable_COLUMN_NUMBER = null;
    private JRFillVariable variable_REPORT_COUNT = null;
    private JRFillVariable variable_PAGE_COUNT = null;
    private JRFillVariable variable_COLUMN_COUNT = null;
    private JRFillVariable variable_jenis_barang_COUNT = null;
    private JRFillVariable variable_no_ = null;


    /**
     *
     */
    public void customizedInit(
        Map pm,
        Map fm,
        Map vm
        )
    {
        initParams(pm);
        initFields(fm);
        initVars(vm);
    }


    /**
     *
     */
    private void initParams(Map pm)
    {
        parameter_REPORT_LOCALE = (JRFillParameter)pm.get("REPORT_LOCALE");
        parameter_REPORT_TIME_ZONE = (JRFillParameter)pm.get("REPORT_TIME_ZONE");
        parameter_REPORT_VIRTUALIZER = (JRFillParameter)pm.get("REPORT_VIRTUALIZER");
        parameter_REPORT_FILE_RESOLVER = (JRFillParameter)pm.get("REPORT_FILE_RESOLVER");
        parameter_REPORT_SCRIPTLET = (JRFillParameter)pm.get("REPORT_SCRIPTLET");
        parameter_REPORT_PARAMETERS_MAP = (JRFillParameter)pm.get("REPORT_PARAMETERS_MAP");
        parameter_REPORT_CONNECTION = (JRFillParameter)pm.get("REPORT_CONNECTION");
        parameter_REPORT_CLASS_LOADER = (JRFillParameter)pm.get("REPORT_CLASS_LOADER");
        parameter_REPORT_DATA_SOURCE = (JRFillParameter)pm.get("REPORT_DATA_SOURCE");
        parameter_REPORT_URL_HANDLER_FACTORY = (JRFillParameter)pm.get("REPORT_URL_HANDLER_FACTORY");
        parameter_IS_IGNORE_PAGINATION = (JRFillParameter)pm.get("IS_IGNORE_PAGINATION");
        parameter_REPORT_FORMAT_FACTORY = (JRFillParameter)pm.get("REPORT_FORMAT_FACTORY");
        parameter_kode_item = (JRFillParameter)pm.get("kode_item");
        parameter_REPORT_MAX_COUNT = (JRFillParameter)pm.get("REPORT_MAX_COUNT");
        parameter_REPORT_TEMPLATES = (JRFillParameter)pm.get("REPORT_TEMPLATES");
        parameter_tanggal1 = (JRFillParameter)pm.get("tanggal1");
        parameter_kategori = (JRFillParameter)pm.get("kategori");
        parameter_tanggal2 = (JRFillParameter)pm.get("tanggal2");
        parameter_alamat = (JRFillParameter)pm.get("alamat");
        parameter_nama_koperasi = (JRFillParameter)pm.get("nama_koperasi");
        parameter_telp = (JRFillParameter)pm.get("telp");
        parameter_REPORT_RESOURCE_BUNDLE = (JRFillParameter)pm.get("REPORT_RESOURCE_BUNDLE");
    }


    /**
     *
     */
    private void initFields(Map fm)
    {
        field_beli = (JRFillField)fm.get("beli");
        field_jenis_barang = (JRFillField)fm.get("jenis_barang");
        field_jual = (JRFillField)fm.get("jual");
        field_item_code = (JRFillField)fm.get("item_code");
        field_sisa = (JRFillField)fm.get("sisa");
        field_r_beli = (JRFillField)fm.get("r_beli");
        field_item_name = (JRFillField)fm.get("item_name");
        field_r_jual = (JRFillField)fm.get("r_jual");
        field_s_awal = (JRFillField)fm.get("s_awal");
        field_opname = (JRFillField)fm.get("opname");
    }


    /**
     *
     */
    private void initVars(Map vm)
    {
        variable_PAGE_NUMBER = (JRFillVariable)vm.get("PAGE_NUMBER");
        variable_COLUMN_NUMBER = (JRFillVariable)vm.get("COLUMN_NUMBER");
        variable_REPORT_COUNT = (JRFillVariable)vm.get("REPORT_COUNT");
        variable_PAGE_COUNT = (JRFillVariable)vm.get("PAGE_COUNT");
        variable_COLUMN_COUNT = (JRFillVariable)vm.get("COLUMN_COUNT");
        variable_jenis_barang_COUNT = (JRFillVariable)vm.get("jenis_barang_COUNT");
        variable_no_ = (JRFillVariable)vm.get("no_");
    }


    /**
     *
     */
    public Object evaluate(int id) throws Throwable
    {
        Object value = null;

        switch (id)
        {
            case 0 : 
            {
                value = (java.lang.String)("Koperasi karyawan Siloam Hospitals Surabaya");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("Jl. Raya Gubeng 70");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.String)("031-503 1333");//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.String)("2001-01-01");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("2009-10-01");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=7$
                break;
            }
            case 8 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=8$
                break;
            }
            case 9 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_item_code.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_jenis_barang.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)(((java.lang.String)field_jenis_barang.getValue()));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.String)("Tanggal : " +((java.lang.String)parameter_tanggal1.getValue()).substring(8,10)+ ((java.lang.String)parameter_tanggal1.getValue()).substring(4,7)+'-'+((java.lang.String)parameter_tanggal1.getValue()).substring(0,4)+" S/d "+((java.lang.String)parameter_tanggal2.getValue()).substring(8,10)+ ((java.lang.String)parameter_tanggal2.getValue()).substring(4,7)+'-'+((java.lang.String)parameter_tanggal2.getValue()).substring(0,4));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_nama_koperasi.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_alamat.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_telp.getValue()));//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)(((java.lang.String)field_item_code.getValue()));//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)field_item_name.getValue()));//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_s_awal.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_beli.getValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_jual.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_r_beli.getValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_r_jual.getValue()));//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_opname.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_sisa.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.Integer)(((java.lang.Integer)variable_no_.getValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.String)("Page " + ((java.lang.Integer)variable_PAGE_NUMBER.getValue()) + " of ");//$JR_EXPR_ID=34$
                break;
            }
            case 35 : 
            {
                value = (java.lang.String)("" + ((java.lang.Integer)variable_PAGE_NUMBER.getValue()));//$JR_EXPR_ID=35$
                break;
            }
            case 36 : 
            {
                value = (java.util.Date)(new Date());//$JR_EXPR_ID=36$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


    /**
     *
     */
    public Object evaluateOld(int id) throws Throwable
    {
        Object value = null;

        switch (id)
        {
            case 0 : 
            {
                value = (java.lang.String)("Koperasi karyawan Siloam Hospitals Surabaya");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("Jl. Raya Gubeng 70");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.String)("031-503 1333");//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.String)("2001-01-01");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("2009-10-01");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=7$
                break;
            }
            case 8 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=8$
                break;
            }
            case 9 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_item_code.getOldValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_jenis_barang.getOldValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)(((java.lang.String)field_jenis_barang.getOldValue()));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.String)("Tanggal : " +((java.lang.String)parameter_tanggal1.getValue()).substring(8,10)+ ((java.lang.String)parameter_tanggal1.getValue()).substring(4,7)+'-'+((java.lang.String)parameter_tanggal1.getValue()).substring(0,4)+" S/d "+((java.lang.String)parameter_tanggal2.getValue()).substring(8,10)+ ((java.lang.String)parameter_tanggal2.getValue()).substring(4,7)+'-'+((java.lang.String)parameter_tanggal2.getValue()).substring(0,4));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_nama_koperasi.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_alamat.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_telp.getValue()));//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)(((java.lang.String)field_item_code.getOldValue()));//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)field_item_name.getOldValue()));//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_s_awal.getOldValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_beli.getOldValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_jual.getOldValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_r_beli.getOldValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_r_jual.getOldValue()));//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_opname.getOldValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_sisa.getOldValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.Integer)(((java.lang.Integer)variable_no_.getOldValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.String)("Page " + ((java.lang.Integer)variable_PAGE_NUMBER.getOldValue()) + " of ");//$JR_EXPR_ID=34$
                break;
            }
            case 35 : 
            {
                value = (java.lang.String)("" + ((java.lang.Integer)variable_PAGE_NUMBER.getOldValue()));//$JR_EXPR_ID=35$
                break;
            }
            case 36 : 
            {
                value = (java.util.Date)(new Date());//$JR_EXPR_ID=36$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


    /**
     *
     */
    public Object evaluateEstimated(int id) throws Throwable
    {
        Object value = null;

        switch (id)
        {
            case 0 : 
            {
                value = (java.lang.String)("Koperasi karyawan Siloam Hospitals Surabaya");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("Jl. Raya Gubeng 70");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.String)("031-503 1333");//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.String)("2001-01-01");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("2009-10-01");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=7$
                break;
            }
            case 8 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=8$
                break;
            }
            case 9 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_item_code.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_jenis_barang.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)(((java.lang.String)field_jenis_barang.getValue()));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.String)("Tanggal : " +((java.lang.String)parameter_tanggal1.getValue()).substring(8,10)+ ((java.lang.String)parameter_tanggal1.getValue()).substring(4,7)+'-'+((java.lang.String)parameter_tanggal1.getValue()).substring(0,4)+" S/d "+((java.lang.String)parameter_tanggal2.getValue()).substring(8,10)+ ((java.lang.String)parameter_tanggal2.getValue()).substring(4,7)+'-'+((java.lang.String)parameter_tanggal2.getValue()).substring(0,4));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_nama_koperasi.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_alamat.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_telp.getValue()));//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)(((java.lang.String)field_item_code.getValue()));//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)field_item_name.getValue()));//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_s_awal.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_beli.getValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_jual.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_r_beli.getValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_r_jual.getValue()));//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_opname.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.math.BigDecimal)(((java.math.BigDecimal)field_sisa.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.Integer)(((java.lang.Integer)variable_no_.getEstimatedValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.String)("Page " + ((java.lang.Integer)variable_PAGE_NUMBER.getEstimatedValue()) + " of ");//$JR_EXPR_ID=34$
                break;
            }
            case 35 : 
            {
                value = (java.lang.String)("" + ((java.lang.Integer)variable_PAGE_NUMBER.getEstimatedValue()));//$JR_EXPR_ID=35$
                break;
            }
            case 36 : 
            {
                value = (java.util.Date)(new Date());//$JR_EXPR_ID=36$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


}