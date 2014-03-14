package nu.fw.jeti.backend.roster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class JIDStatusTree
{
	//synchronized because used in event thread & jabberinput thread
	
	private List root;

	public JIDStatusTree()
	{
		root = new ArrayList(8);
	}


	//get group of maak als hij nog niet bestaat
	/**
	 * Searches group and returns the found group 
	 * or makes a new group if group does not exist
	 * @param group  Group to get
	 * @return JIDStatusGroup 
	 */
	public synchronized JIDStatusGroup getGroup(String group)
	{
		int index = root.indexOf(new JIDStatusGroup(group));
		if(index == -1)
		{
			JIDStatusGroup jidGroup = new JIDStatusGroup(group);
			root.add(jidGroup);
			Collections.sort(root);
			return jidGroup;
		}
		return (JIDStatusGroup)root.get(index);
	}

	
	public synchronized boolean existGroup(String group)
	{
		return (root.indexOf(new JIDStatusGroup(group))!=-1) ;
	}

	public synchronized void removeGroup(JIDStatusGroup jidGroup)
	{
		root.remove(jidGroup);
	}

	public synchronized String[] getGroups()
	{
		String[] groups = new String[root.size()];
		for(int tel =0; tel<root.size();tel++)
		{
			groups[tel] =((JIDStatusGroup)root.get(tel)).getName();
		}
		return groups;
	}

	public synchronized int indexOfGroup(Object group)
	{
	    return root.indexOf(group);
	}

	public synchronized int getSize(){return root.size();}

	public synchronized Object get(int index){return root.get(index);}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
