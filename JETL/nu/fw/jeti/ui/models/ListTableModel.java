package nu.fw.jeti.ui.models;

import javax.swing.table.*;
import java.util.*;


/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class ListTableModel extends AbstractTableModel
{
	private String[] columnNames;
	private List plugins;

    public ListTableModel(String[] columnNames,List plugins)
    {
		this.columnNames = columnNames;
		this.plugins = plugins;
    }

	public void reload(List list)
	{
		plugins = list;
		fireTableDataChanged();
	}

	public List getPlugins()
	{
		return plugins;
	}

/*
 public synchronized void remove(int row)
 {
	//System.out.println(row);
  data.remove(row);
  fireTableDataChanged();
 }


	public  Object getRow(int row)
	{
			return data.get(row);
	}

	public void delete(Object list)
	{
		data.remove(data.indexOf(list));
		fireTableDataChanged();
	}


 public void sort(int column, boolean asc)
 {
  ascending =asc;
  if (data.isEmpty())return;
  if ((((List)data.get(0)).get(column)).getClass() == String.class)
  {
   sortStrings(column);
  }
  else if ((((List)data.get(0)).get(column)).getClass() == Double.class)
  {
   sortDoubles(column);
  }
  else if ((((List)data.get(0)).get(column)).getClass() == Boolean.class)
  {
   sortBooleans(column);
  }
  else
  {
   sortIntegers(column);
  }
 }

 public void sortIntegers(int column)
 {
  final int col = column;
  Collections.sort(data, new Comparator()
  {
  public int compare(Object o1, Object o2)
  {
   int result = ((Integer)((List)o1).get(col)).compareTo(((Integer)((List)o2).get(col)));
   if (result != 0)
   {
	return ascending ? result : -result;
   }
	return 0;
   }
  });
 }

 public void sortDoubles(int column)
 {
  final int col = column;
  Collections.sort(data, new Comparator()
  {
   public int compare(Object o1, Object o2)
   {
	int result = ((Double)((List)o1).get(col)).compareTo(((Double)((List)o2).get(col)));
	if (result != 0)
	{
	 return ascending ? result : -result;
	}
	return 0;
   }
  });
 }

 public void sortStrings(int column)
 {
  final int col = column;
  Collections.sort(data, new Comparator()
  {
   public int compare(Object o1, Object o2)
   {
	int result = ((String)((List)o1).get(col)).compareTo(((String)((List)o2).get(col)));
	if (result != 0)
	{
	 return ascending ? result : -result;
	}
	return 0;
   }
  });
 }

 public void sortBooleans(int column)
 {
  final int col = column;
  Collections.sort(data, new Comparator()
  {
   public int compare(Object o1, Object o2)
   {
	int result =0;
	if ((((List)o1).get(col)).equals(((List)o2).get(col))) return 0;
	if (((Boolean)((List)o1).get(col)).booleanValue()) result=-1;
	else result=1;
	return ascending ? result : -result;
   }
  });
 }
 */

	public boolean isCellEditable(int row, int col)
	{
	   if (col == 1)  return true;
	   if (col == 3)  return true;
	   return false;
	}

	public void setValueAt(Object value, int row, int col)
	{
		((Object[])plugins.get(row))[col] = value;
		fireTableCellUpdated(row, col);
	}

	public Class getColumnClass(int col)
	{
		return ((Object[])plugins.get(0))[col].getClass();
	}

	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	//required methods by abstractTableModel
	public int getColumnCount()
	{
		return columnNames.length;
	}

	public int getRowCount()
	{
		return plugins.size();
	}

	public Object getValueAt(int row, int col)
	{
		return ((Object[])plugins.get(row))[col];
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
