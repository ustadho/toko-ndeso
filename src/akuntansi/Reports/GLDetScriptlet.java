import net.sf.jasperreports.engine.*;
import java.util.*;
import java.lang.*;

public class GLDetScriptlet extends it.businesslogic.ireport.IReportScriptlet {
private HashMap saldo;
private Double lastSaldo;
/** Creates a new instance of JRIreportDefaultScriptlet */
public GLDetScriptlet() {
	saldo=new HashMap();
	lastSaldo=new Double(0);
}

public Double getLastSaldo(){
	return lastSaldo;
}










/** Begin EVENT_AFTER_COLUMN_INIT This line is generated by iReport. Don't modify or move please! */
public void afterColumnInit() throws JRScriptletException
{
	super.beforeColumnInit();
}
/** End EVENT_AFTER_COLUMN_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_AFTER_DETAIL_EVAL This line is generated by iReport. Don't modify or move please! */
public void afterDetailEval() throws JRScriptletException
{
	super.afterDetailEval();
}
/** End EVENT_AFTER_DETAIL_EVAL This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_AFTER_GROUP_INIT This line is generated by iReport. Don't modify or move please! */
public void afterGroupInit(String groupName) throws JRScriptletException
{
	super.afterGroupInit(groupName);
	Double saldoAwal = (java.lang.Double)getFieldValue("saldo_aw");
	lastSaldo=saldoAwal;
}
/** End EVENT_AFTER_GROUP_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_AFTER_PAGE_INIT This line is generated by iReport. Don't modify or move please! */
public void afterPageInit() throws JRScriptletException
{
	super.afterPageInit();
}
/** End EVENT_AFTER_PAGE_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_AFTER_REPORT_INIT This line is generated by iReport. Don't modify or move please! */
public void afterReportInit() throws JRScriptletException
{
	
}
/** End EVENT_AFTER_REPORT_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_BEFORE_COLUMN_INIT This line is generated by iReport. Don't modify or move please! */
public void beforeColumnInit() throws JRScriptletException
{
		
}
/** End EVENT_BEFORE_COLUMN_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_BEFORE_DETAIL_EVAL This line is generated by iReport. Don't modify or move please! */
public void beforeDetailEval() throws JRScriptletException
{
	/**
	BigDecimal prd = (java.math.BigDecimal)getFieldValue("kode_produk");
	BigDecimal stok = (java.math.BigDecimal)getFieldValue("stok");
	BigDecimal trans = (java.math.BigDecimal)getFieldValue("trans");
	if(!saldo.containsKey(prd))//Berarti blm ada stok
	saldo.put(prd,stok);
	//
	BigDecimal tmpStok = (BigDecimal) saldo.get(prd);
	stok = tmpStok.add(trans);
	saldo.remove(prd);
	saldo.put(prd,stok);

	lastSaldo = (BigDecimal) saldo.get(prd);
*/
	Double debit = (java.lang.Double)getFieldValue("debit");
	Double credit = (java.lang.Double)getFieldValue("credit");
	Double tmpSaldo = (Double) lastSaldo+(debit-credit);
	
	lastSaldo = tmpSaldo;
}
/** end EVENT_BEFORE_DETAIL_EVAL Please don't touch or move this comment*/

/** End EVENT_BEFORE_DETAIL_EVAL This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_BEFORE_GROUP_INIT This line is generated by iReport. Don't modify or move please! */
public void beforeGroupInit(String groupName) throws JRScriptletException
{
	
}
/** End EVENT_BEFORE_GROUP_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_BEFORE_PAGE_INIT This line is generated by iReport. Don't modify or move please! */
public void beforePageInit() throws JRScriptletException
{
	
}
/** End EVENT_BEFORE_PAGE_INIT This line is generated by iReport. Don't modify or move please! */
/** Begin EVENT_BEFORE_REPORT_INIT This line is generated by iReport. Don't modify or move please! */
public void beforeReportInit() throws JRScriptletException
{
	
}

/** End EVENT_BEFORE_REPORT_INIT This line is generated by iReport. Don't modify or move please! */

}