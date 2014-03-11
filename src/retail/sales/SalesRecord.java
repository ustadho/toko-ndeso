/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retail.sales;

/**
 *
 * @author ustadho
 */
public class SalesRecord {
     protected String kode;
     protected String namaBarang;
     protected String satuan;
     protected double qty;
     protected double harga;
     protected double subTotal;
     protected int konv;


     public SalesRecord() {
         kode = "";
         namaBarang = "";
         satuan = "";
         qty=1;
         harga=0;
         subTotal=0;
         konv=1;
     }

     public String getKode() {
         return kode;
     }

     public void setKode(String kode) {
         this.kode = kode;
     }

     public String getNamaBarang() {
         return namaBarang;
     }

     public void setNamaBarang(String s) {
         this.namaBarang = s;
     }

     public String getSatuan() {
         return satuan;
     }

     public void setSatuan(String s) {
         this.satuan = s;
     }

     public double getQty(){
         return qty;
     }

     public void setQty(double d){
         this.qty=d;
     }

     public double getSubtotal(){
         return subTotal;
     }

     public void setSubTotal(){
         
     }

 }


