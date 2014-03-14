package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.util.StringArray;
import java.util.List;
import java.util.ArrayList;
import nu.fw.jeti.jabber.*;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQBrowseBuilder
{
	private StringArray namespaces;
	private String name;
	private String type;
	private String category;
	private JID jid;
	private String version;
	private List childItems;

	public void reset()
	{
		name=null;
		type =null;
		category=null;
		jid =null;
		namespaces =null;
		childItems = null;
	}

	public String getName(){return name;}

	public void setName(String name){this.name =name;}

	public String getType(){return type;}

	public void setType(String type){this.type = type;}

	public String getVersion(){return version;}

	public void setVersion(String version){this.version = version;}

	public String getCategory(){return category;}

	public void setCategory(String category){this.category =category;}

	public JID getJID(){return jid;}

	public void setJID(JID jid){this.jid = jid;}

	public List getItems(){return childItems;}

	public void addItem(IQBrowse item)
	{
		if(childItems == null) childItems = new ArrayList();
		childItems.add(item);
	}

	public StringArray getNamespaces()
	{//clone? nullpointers
		//return (StringArray)groups.clone();
		return namespaces;
	}

	public void addNamespace(String namespace)
	{
		if(namespaces  == null) namespaces = new StringArray();
		namespaces.add(namespace);
	}

	public IQBrowse build()
	{
		//if(jid == null) throw new InstantiationException("jid may not be null");
		return new IQBrowse(this);
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
