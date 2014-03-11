package akuntansi.Reports;

/*
 * Generated by JasperReports - 13/05/10 13:22
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
public class BuktiKas_1273731754871_507446 extends JREvaluator
{


    /**
     *
     */
    private JRFillParameter parameter_REPORT_LOCALE = null;
    private JRFillParameter parameter_logo = null;
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
    private JRFillParameter parameter_SUBREPORT_DIR = null;
    private JRFillParameter parameter_REPORT_FORMAT_FACTORY = null;
    private JRFillParameter parameter_REPORT_MAX_COUNT = null;
    private JRFillParameter parameter_REPORT_TEMPLATES = null;
    private JRFillParameter parameter_no_bukti = null;
    private JRFillParameter parameter_REPORT_RESOURCE_BUNDLE = null;
    private JRFillField field_amount = null;
    private JRFillField field_diketahui_oleh = null;
    private JRFillField field_flag = null;
    private JRFillField field_terbilang = null;
    private JRFillField field_description = null;
    private JRFillField field_no_voucher = null;
    private JRFillField field_diterima_oleh = null;
    private JRFillField field_no_bukti = null;
    private JRFillField field_tanggal = null;
    private JRFillField field_dibayar_oleh = null;
    private JRFillVariable variable_PAGE_NUMBER = null;
    private JRFillVariable variable_COLUMN_NUMBER = null;
    private JRFillVariable variable_REPORT_COUNT = null;
    private JRFillVariable variable_PAGE_COUNT = null;
    private JRFillVariable variable_COLUMN_COUNT = null;


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
        parameter_logo = (JRFillParameter)pm.get("logo");
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
        parameter_SUBREPORT_DIR = (JRFillParameter)pm.get("SUBREPORT_DIR");
        parameter_REPORT_FORMAT_FACTORY = (JRFillParameter)pm.get("REPORT_FORMAT_FACTORY");
        parameter_REPORT_MAX_COUNT = (JRFillParameter)pm.get("REPORT_MAX_COUNT");
        parameter_REPORT_TEMPLATES = (JRFillParameter)pm.get("REPORT_TEMPLATES");
        parameter_no_bukti = (JRFillParameter)pm.get("no_bukti");
        parameter_REPORT_RESOURCE_BUNDLE = (JRFillParameter)pm.get("REPORT_RESOURCE_BUNDLE");
    }


    /**
     *
     */
    private void initFields(Map fm)
    {
        field_amount = (JRFillField)fm.get("amount");
        field_diketahui_oleh = (JRFillField)fm.get("diketahui_oleh");
        field_flag = (JRFillField)fm.get("flag");
        field_terbilang = (JRFillField)fm.get("terbilang");
        field_description = (JRFillField)fm.get("description");
        field_no_voucher = (JRFillField)fm.get("no_voucher");
        field_diterima_oleh = (JRFillField)fm.get("diterima_oleh");
        field_no_bukti = (JRFillField)fm.get("no_bukti");
        field_tanggal = (JRFillField)fm.get("tanggal");
        field_dibayar_oleh = (JRFillField)fm.get("dibayar_oleh");
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
                value = (java.lang.String)("E:\\Project\\akuntasi\\src\\akuntansi\\resources\\LogoKopegtel.JPG");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("E:\\Project\\akuntasi\\src\\akuntansi\\Reports\\");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=7$
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
                value = (java.lang.String)("BUKTI KAS "+ (((java.lang.String)field_flag.getValue()).equalsIgnoreCase("M")? "MASUK" :"KELUAR"));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "Telah terima dari : ": "Dibayarkan kepada");//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase("M")? "No. BKM" : "No. BKK");//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.String)(((java.lang.String)field_no_bukti.getValue()));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.String)(((java.lang.String)field_no_voucher.getValue()));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.String)("Reff.");//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.String)("Tanggal");//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.util.Date)(((java.sql.Date)field_tanggal.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? ((java.lang.String)field_dibayar_oleh.getValue()): ((java.lang.String)field_diterima_oleh.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)("# "+((java.lang.String)field_terbilang.getValue())+ " Rupiah");//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_amount.getValue()));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.lang.String)field_description.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)field_diketahui_oleh.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? ((java.lang.String)field_diterima_oleh.getValue()): ((java.lang.String)field_dibayar_oleh.getValue()));//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "Diterima oleh": "Dibayar Oleh");//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "Disetor oleh": "Diterima Oleh");//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "K" )? ((java.lang.String)field_diterima_oleh.getValue()): ((java.lang.String)field_dibayar_oleh.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "": "Terbilang");//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_logo.getValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_no_bukti.getValue()));//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.sql.Connection)(((java.sql.Connection)parameter_REPORT_CONNECTION.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()) + "BuktiKas_subreport0.jasper");//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_logo.getValue()));//$JR_EXPR_ID=33$
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
                value = (java.lang.String)("E:\\Project\\akuntasi\\src\\akuntansi\\resources\\LogoKopegtel.JPG");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("E:\\Project\\akuntasi\\src\\akuntansi\\Reports\\");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=7$
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
                value = (java.lang.String)("BUKTI KAS "+ (((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase("M")? "MASUK" :"KELUAR"));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "M" )? "Telah terima dari : ": "Dibayarkan kepada");//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase("M")? "No. BKM" : "No. BKK");//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.String)(((java.lang.String)field_no_bukti.getOldValue()));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.String)(((java.lang.String)field_no_voucher.getOldValue()));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.String)("Reff.");//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.String)("Tanggal");//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.util.Date)(((java.sql.Date)field_tanggal.getOldValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "M" )? ((java.lang.String)field_dibayar_oleh.getOldValue()): ((java.lang.String)field_diterima_oleh.getOldValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)("# "+((java.lang.String)field_terbilang.getOldValue())+ " Rupiah");//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_amount.getOldValue()));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.lang.String)field_description.getOldValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)field_diketahui_oleh.getOldValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "M" )? ((java.lang.String)field_diterima_oleh.getOldValue()): ((java.lang.String)field_dibayar_oleh.getOldValue()));//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "M" )? "Diterima oleh": "Dibayar Oleh");//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "M" )? "Disetor oleh": "Diterima Oleh");//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "K" )? ((java.lang.String)field_diterima_oleh.getOldValue()): ((java.lang.String)field_dibayar_oleh.getOldValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getOldValue()).equalsIgnoreCase( "M" )? "": "Terbilang");//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_logo.getValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_no_bukti.getValue()));//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.sql.Connection)(((java.sql.Connection)parameter_REPORT_CONNECTION.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()) + "BuktiKas_subreport0.jasper");//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_logo.getValue()));//$JR_EXPR_ID=33$
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
                value = (java.lang.String)("E:\\Project\\akuntasi\\src\\akuntansi\\resources\\LogoKopegtel.JPG");//$JR_EXPR_ID=0$
                break;
            }
            case 1 : 
            {
                value = (java.lang.String)("E:\\Project\\akuntasi\\src\\akuntansi\\Reports\\");//$JR_EXPR_ID=1$
                break;
            }
            case 2 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=2$
                break;
            }
            case 3 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=3$
                break;
            }
            case 4 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=4$
                break;
            }
            case 5 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=5$
                break;
            }
            case 6 : 
            {
                value = (java.lang.Integer)(new Integer(1));//$JR_EXPR_ID=6$
                break;
            }
            case 7 : 
            {
                value = (java.lang.Integer)(new Integer(0));//$JR_EXPR_ID=7$
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
                value = (java.lang.String)("BUKTI KAS "+ (((java.lang.String)field_flag.getValue()).equalsIgnoreCase("M")? "MASUK" :"KELUAR"));//$JR_EXPR_ID=10$
                break;
            }
            case 11 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "Telah terima dari : ": "Dibayarkan kepada");//$JR_EXPR_ID=11$
                break;
            }
            case 12 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase("M")? "No. BKM" : "No. BKK");//$JR_EXPR_ID=12$
                break;
            }
            case 13 : 
            {
                value = (java.lang.String)(((java.lang.String)field_no_bukti.getValue()));//$JR_EXPR_ID=13$
                break;
            }
            case 14 : 
            {
                value = (java.lang.String)(((java.lang.String)field_no_voucher.getValue()));//$JR_EXPR_ID=14$
                break;
            }
            case 15 : 
            {
                value = (java.lang.String)("Reff.");//$JR_EXPR_ID=15$
                break;
            }
            case 16 : 
            {
                value = (java.lang.String)("Tanggal");//$JR_EXPR_ID=16$
                break;
            }
            case 17 : 
            {
                value = (java.util.Date)(((java.sql.Date)field_tanggal.getValue()));//$JR_EXPR_ID=17$
                break;
            }
            case 18 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? ((java.lang.String)field_dibayar_oleh.getValue()): ((java.lang.String)field_diterima_oleh.getValue()));//$JR_EXPR_ID=18$
                break;
            }
            case 19 : 
            {
                value = (java.lang.String)("# "+((java.lang.String)field_terbilang.getValue())+ " Rupiah");//$JR_EXPR_ID=19$
                break;
            }
            case 20 : 
            {
                value = (java.lang.Double)(((java.lang.Double)field_amount.getValue()));//$JR_EXPR_ID=20$
                break;
            }
            case 21 : 
            {
                value = (java.lang.String)(((java.lang.String)field_description.getValue()));//$JR_EXPR_ID=21$
                break;
            }
            case 22 : 
            {
                value = (java.lang.String)(((java.lang.String)field_diketahui_oleh.getValue()));//$JR_EXPR_ID=22$
                break;
            }
            case 23 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? ((java.lang.String)field_diterima_oleh.getValue()): ((java.lang.String)field_dibayar_oleh.getValue()));//$JR_EXPR_ID=23$
                break;
            }
            case 24 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "Diterima oleh": "Dibayar Oleh");//$JR_EXPR_ID=24$
                break;
            }
            case 25 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "Disetor oleh": "Diterima Oleh");//$JR_EXPR_ID=25$
                break;
            }
            case 26 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "K" )? ((java.lang.String)field_diterima_oleh.getValue()): ((java.lang.String)field_dibayar_oleh.getValue()));//$JR_EXPR_ID=26$
                break;
            }
            case 27 : 
            {
                value = (java.lang.String)(((java.lang.String)field_flag.getValue()).equalsIgnoreCase( "M" )? "": "Terbilang");//$JR_EXPR_ID=27$
                break;
            }
            case 28 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()));//$JR_EXPR_ID=28$
                break;
            }
            case 29 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_logo.getValue()));//$JR_EXPR_ID=29$
                break;
            }
            case 30 : 
            {
                value = (java.lang.Object)(((java.lang.String)parameter_no_bukti.getValue()));//$JR_EXPR_ID=30$
                break;
            }
            case 31 : 
            {
                value = (java.sql.Connection)(((java.sql.Connection)parameter_REPORT_CONNECTION.getValue()));//$JR_EXPR_ID=31$
                break;
            }
            case 32 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_SUBREPORT_DIR.getValue()) + "BuktiKas_subreport0.jasper");//$JR_EXPR_ID=32$
                break;
            }
            case 33 : 
            {
                value = (java.lang.String)(((java.lang.String)parameter_logo.getValue()));//$JR_EXPR_ID=33$
                break;
            }
           default :
           {
           }
        }
        
        return value;
    }


}
