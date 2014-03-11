package akuntansi.Reports;

/*
 * Generated by JasperReports - 8/13/10 4:15 PM
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
public class LabaRugi_1281690932792_555634 extends JREvaluator
{


    /**
     *
     */
    private JRFillParameter parameter_REPORT_LOCALE = null;
    private JRFillParameter parameter_REPORT_TIME_ZONE = null;
    private JRFillParameter parameter_REPORT_VIRTUALIZER = null;
    private JRFillParameter parameter_REPORT_FILE_RESOLVER = null;
    private JRFillParameter parameter_REPORT_SCRIPTLET = null;
    private JRFillParameter parameter_bulan = null;
    private JRFillParameter parameter_REPORT_PARAMETERS_MAP = null;
    private JRFillParameter parameter_REPORT_CONNECTION = null;
    private JRFillParameter parameter_tahun = null;
    private JRFillParameter parameter_REPORT_CLASS_LOADER = null;
    private JRFillParameter parameter_REPORT_DATA_SOURCE = null;
    private JRFillParameter parameter_REPORT_URL_HANDLER_FACTORY = null;
    private JRFillParameter parameter_IS_IGNORE_PAGINATION = null;
    private JRFillParameter parameter_unit = null;
    private JRFillParameter parameter_REPORT_FORMAT_FACTORY = null;
    private JRFillParameter parameter_REPORT_MAX_COUNT = null;
    private JRFillParameter parameter_REPORT_TEMPLATES = null;
    private JRFillParameter parameter_alamat = null;
    private JRFillParameter parameter_nama_koperasi = null;
    private JRFillParameter parameter_telp = null;
    private JRFillParameter parameter_REPORT_RESOURCE_BUNDLE = null;
    private JRFillField field_balance = null;
    private JRFillField field_acc_no = null;
    private JRFillField field_type_name = null;
    private JRFillField field_group_id = null;
    private JRFillField field_lbalance = null;
    private JRFillField field_acc_name = null;
    private JRFillField field_type_id = null;
    private JRFillVariable variable_PAGE_NUMBER = null;
    private JRFillVariable variable_COLUMN_NUMBER = null;
    private JRFillVariable variable_REPORT_COUNT = null;
    private JRFillVariable variable_PAGE_COUNT = null;
    private JRFillVariable variable_COLUMN_COUNT = null;
    private JRFillVariable variable_type_id_COUNT = null;
    private JRFillVariable variable_sub_balance = null;
    private JRFillVariable variable_sub_lbalance = null;
    private JRFillVariable variable_tot_balance = null;
    private JRFillVariable variable_tot_lbalance = null;


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
        parameter_bulan = (JRFillParameter)pm.get("bulan");
        parameter_REPORT_PARAMETERS_MAP = (JRFillParameter)pm.get("REPORT_PARAMETERS_MAP");
        parameter_REPORT_CONNECTION = (JRFillParameter)pm.get("REPORT_CONNECTION");
        parameter_tahun = (JRFillParameter)pm.get("tahun");
        parameter_REPORT_CLASS_LOADER = (JRFillParameter)pm.get("REPORT_CLASS_LOADER");
        parameter_REPORT_DATA_SOURCE = (JRFillParameter)pm.get("REPORT_DATA_SOURCE");
        parameter_REPORT_URL_HANDLER_FACTORY = (JRFillParameter)pm.get("REPORT_URL_HANDLER_FACTORY");
        parameter_IS_IGNORE_PAGINATION = (JRFillParameter)pm.get("IS_IGNORE_PAGINATION");
        parameter_unit = (JRFillParameter)pm.get("unit");
        parameter_REPORT_FORMAT_FACTORY = (JRFillParameter)pm.get("REPORT_FORMAT_FACTORY");
        parameter_REPORT_MAX_COUNT = (JRFillParameter)pm.get("REPORT_MAX_COUNT");
        parameter_REPORT_TEMPLATES = (JRFillParameter)pm.get("REPORT_TEMPLATES");
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
        field_balance = (JRFillField)fm.get("balance");
        field_acc_no = (JRFillField)fm.get("acc_no");
        field_type_name = (JRFillField)fm.get("type_name");
        field_group_id = (JRFillField)fm.get("group_id");
        field_lbalance = (JRFillField)fm.get("lbalance");
        field_acc_name = (JRFillField)fm.get("acc_name");
        field_type_id = (JRFillField)fm.get("type_id");
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
        variable_type_id_COUNT = (JRFillVariable)vm.get("type_id_COUNT");
        variable_sub_balance = (JRFillVariable)vm.get("sub_balance");
        variable_sub_lbalance = (JRFillVariable)vm.get("sub_lbalance");
        variable_tot_balance = (JRFillVariable)vm.get("tot_balance");
        variable_tot_lbalance = (JRFillVariable)vm.get("tot_lbalance");
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
                value = (java.lang.String)("2009");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("09");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
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
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_balance.getValue()));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_lbalance.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.Double)((((java.lang.String)field_group_id.getValue()).equalsIgnoreCase("4")? ((java.lang.Double)field_balance.getValue()): new java.lang.Double((-1*((java.lang.Double)field_balance.getValue()).doubleValue()))));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.Double)((((java.lang.String)field_group_id.getValue()).equalsIgnoreCase("4")? ((java.lang.Double)field_lbalance.getValue()): new java.lang.Double((-1*((java.lang.Double)field_lbalance.getValue()).doubleValue()))));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_type_id.getValue()));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(null);//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)field_type_name.getValue()).toUpperCase());//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(null);//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)("Total "+((java.lang.String)field_type_name.getValue()).toUpperCase());//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_nama_koperasi.getValue()));//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_alamat.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_telp.getValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.String)("Laporan Rugi Laba");//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.String)("TAHUN "+String.valueOf(Integer.parseInt(((java.lang.String)parameter_tahun.getValue()))-1)+"\n(Rp)");//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.String)("TAHUN "+((java.lang.String)parameter_tahun.getValue())+"\n(Rp)");//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.lang.String)(((java.lang.String)field_acc_no.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.String)("~ "+((java.lang.String)field_acc_name.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_balance.getValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_lbalance.getValue()));//$JR_EXPR_ID=34$
                break;
            }
            case 35 : 
            {
                value = (java.lang.String)("Page " + ((java.lang.Integer)variable_PAGE_NUMBER.getValue()) + " of ");//$JR_EXPR_ID=35$
                break;
            }
            case 36 : 
            {
                value = (java.lang.String)("" + ((java.lang.Integer)variable_PAGE_NUMBER.getValue()) + "");//$JR_EXPR_ID=36$
                break;
            }
            case 37 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_tot_balance.getValue()));//$JR_EXPR_ID=37$
                break;
            }
            case 38 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_tot_lbalance.getValue()));//$JR_EXPR_ID=38$
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
                value = (java.lang.String)("2009");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("09");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
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
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_balance.getOldValue()));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_lbalance.getOldValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.Double)((((java.lang.String)field_group_id.getOldValue()).equalsIgnoreCase("4")? ((java.lang.Double)field_balance.getOldValue()): new java.lang.Double((-1*((java.lang.Double)field_balance.getOldValue()).doubleValue()))));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.Double)((((java.lang.String)field_group_id.getOldValue()).equalsIgnoreCase("4")? ((java.lang.Double)field_lbalance.getOldValue()): new java.lang.Double((-1*((java.lang.Double)field_lbalance.getOldValue()).doubleValue()))));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_type_id.getOldValue()));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(null);//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)field_type_name.getOldValue()).toUpperCase());//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(null);//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)("Total "+((java.lang.String)field_type_name.getOldValue()).toUpperCase());//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_nama_koperasi.getValue()));//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_alamat.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_telp.getValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.String)("Laporan Rugi Laba");//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.String)("TAHUN "+String.valueOf(Integer.parseInt(((java.lang.String)parameter_tahun.getValue()))-1)+"\n(Rp)");//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.String)("TAHUN "+((java.lang.String)parameter_tahun.getValue())+"\n(Rp)");//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.lang.String)(((java.lang.String)field_acc_no.getOldValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.String)("~ "+((java.lang.String)field_acc_name.getOldValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_balance.getOldValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_lbalance.getOldValue()));//$JR_EXPR_ID=34$
                break;
            }
            case 35 : 
            {
                value = (java.lang.String)("Page " + ((java.lang.Integer)variable_PAGE_NUMBER.getOldValue()) + " of ");//$JR_EXPR_ID=35$
                break;
            }
            case 36 : 
            {
                value = (java.lang.String)("" + ((java.lang.Integer)variable_PAGE_NUMBER.getOldValue()) + "");//$JR_EXPR_ID=36$
                break;
            }
            case 37 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_tot_balance.getOldValue()));//$JR_EXPR_ID=37$
                break;
            }
            case 38 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_tot_lbalance.getOldValue()));//$JR_EXPR_ID=38$
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
                value = (java.lang.String)("2009");//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.String)("09");//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.String)("");//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
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
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=9$
                break;
            }
            case 10 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_balance.getValue()));//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_lbalance.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.Double)((((java.lang.String)field_group_id.getValue()).equalsIgnoreCase("4")? ((java.lang.Double)field_balance.getValue()): new java.lang.Double((-1*((java.lang.Double)field_balance.getValue()).doubleValue()))));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.Double)((((java.lang.String)field_group_id.getValue()).equalsIgnoreCase("4")? ((java.lang.Double)field_lbalance.getValue()): new java.lang.Double((-1*((java.lang.Double)field_lbalance.getValue()).doubleValue()))));//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.Object)(((java.lang.String)field_type_id.getValue()));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(null);//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)field_type_name.getValue()).toUpperCase());//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(null);//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)("Total "+((java.lang.String)field_type_name.getValue()).toUpperCase());//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_nama_koperasi.getValue()));//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_alamat.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_telp.getValue()));//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.String)("Laporan Rugi Laba");//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.String)("TAHUN "+String.valueOf(Integer.parseInt(((java.lang.String)parameter_tahun.getValue()))-1)+"\n(Rp)");//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.String)("TAHUN "+((java.lang.String)parameter_tahun.getValue())+"\n(Rp)");//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.lang.String)(((java.lang.String)field_acc_no.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.String)("~ "+((java.lang.String)field_acc_name.getValue()));//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_balance.getValue()));//$JR_EXPR_ID=33$
                break;
            }
            case 34 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_lbalance.getValue()));//$JR_EXPR_ID=34$
                break;
            }
            case 35 : 
            {
                value = (java.lang.String)("Page " + ((java.lang.Integer)variable_PAGE_NUMBER.getEstimatedValue()) + " of ");//$JR_EXPR_ID=35$
                break;
            }
            case 36 : 
            {
                value = (java.lang.String)("" + ((java.lang.Integer)variable_PAGE_NUMBER.getEstimatedValue()) + "");//$JR_EXPR_ID=36$
                break;
            }
            case 37 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_tot_balance.getEstimatedValue()));//$JR_EXPR_ID=37$
                break;
            }
            case 38 : 
            {
                value = (java.lang.Double)(((java.lang.Double)variable_tot_lbalance.getEstimatedValue()));//$JR_EXPR_ID=38$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


}
