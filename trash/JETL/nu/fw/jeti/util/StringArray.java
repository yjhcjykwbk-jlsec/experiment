package nu.fw.jeti.util;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class StringArray implements Cloneable
{
	private String[] stringArray;
	private int size;

	public StringArray()
	{
		stringArray = new String[1];
	}

	public String get(int pos)
	{
		return stringArray[pos];
	}

	public void add(String value)
	{
		checkSize();
		stringArray[size] = value;
		size++;
	}

	public boolean contains(String group)
	{
        for(int i=0;i<size;i++)
		{
			if(stringArray[i].equals(group)) return true;
		}
        return false;
    }

	public void remove(String value)
	{
		for(int i=0;i<size;i++)
		{
			if (stringArray[i].equals(value))
			{
				stringArray[i] =stringArray[size-1];
		        size--;
				return;
			}
		}
	}

	public int getSize()
	{
	    return size;
	}

	public Iterator iterator()
	{
		return new Iterator()
		{
			private int teller=0;

			public Object next()
			{
				if(teller>size) throw new NoSuchElementException();
				return stringArray[teller++];
			}

			public boolean hasNext()
			{
				return teller < size;
			}

		    public void remove()
			{
			    throw new UnsupportedOperationException();
			}
		};
	}

	public boolean isEmpty()
	{
	    return size == 0;
	}

	private void checkSize()
	{
	    if (stringArray.length < size + 1)
		{
			String[] tempArray = new String[size+1];
			for(int i=0;i<stringArray.length;i++)
			{
			    tempArray[i] = stringArray[i];
			}
			stringArray = tempArray;
		}
	}

	public Object clone()
	{
		try
		{
			StringArray clone = (StringArray) super.clone();
			clone.stringArray =(String[]) stringArray.clone();
	        return clone;
		}
		catch (CloneNotSupportedException e)
		{
			 System.err.print(e.getMessage());
		}
		return null;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
