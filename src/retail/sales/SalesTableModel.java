/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retail.sales;

/**
 *
 * @author ustadho
 */
import java.util.Collection;
 import java.util.Vector;
 import javax.swing.table.AbstractTableModel;

 public class SalesTableModel extends AbstractTableModel {
     public static final int TITLE_INDEX = 0;
     public static final int ARTIST_INDEX = 1;
     public static final int ALBUM_INDEX = 2;
     public static final int HIDDEN_INDEX = 3;

     protected String[] columnNames;
     protected Vector dataVector;

     public SalesTableModel(String[] columnNames) {
         this.columnNames = columnNames;
         dataVector = new Vector();
     }

     public String getColumnName(int column) {
         return columnNames[column];
     }

     public boolean isCellEditable(int row, int column) {
         if (column == HIDDEN_INDEX) return false;
         else return true;
     }

     public Class getColumnClass(int column) {
         switch (column) {
             case TITLE_INDEX:
             case ARTIST_INDEX:
             case ALBUM_INDEX:
                return String.class;
             default:
                return Object.class;
         }
     }

     public Object getValueAt(int row, int column) {
         SalesRecord record = (SalesRecord)dataVector.get(row);
         switch (column) {
             case TITLE_INDEX:
                return record.getKode();
             case ARTIST_INDEX:
                return record.getNamaBarang();
             case ALBUM_INDEX:
                return record.getSatuan();
             default:
                return new Object();
         }
     }

     public void setValueAt(Object value, int row, int column) {
         SalesRecord record = (SalesRecord)dataVector.get(row);
         switch (column) {
             case TITLE_INDEX:
                record.setKode((String)value);
                break;
             case ARTIST_INDEX:
                record.setNamaBarang((String)value);
                break;
             case ALBUM_INDEX:
                record.setSatuan((String)value);
                break;
             default:
                System.out.println("invalid index");
         }
         fireTableCellUpdated(row, column);
     }

     public int getRowCount() {
         return dataVector.size();
     }

     public int getColumnCount() {
         return columnNames.length;
     }

     public boolean hasEmptyRow() {
         if (dataVector.size() == 0) return false;
         SalesRecord audioRecord = (SalesRecord)dataVector.get(dataVector.size() - 1);
         if (audioRecord.getKode().trim().equals("") &&
            audioRecord.getNamaBarang().trim().equals("") &&
            audioRecord.getSatuan().trim().equals(""))
         {
            return true;
         }
         else return false;
     }

     public void addEmptyRow() {
         dataVector.add(new SalesRecord());
         fireTableRowsInserted(
            dataVector.size() - 1,
            dataVector.size() - 1);
     }
 }
