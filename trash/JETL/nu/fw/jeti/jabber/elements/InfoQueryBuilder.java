package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.util.I18N;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class InfoQueryBuilder extends PacketBuilder
{
	private String type;

	public InfoQueryBuilder()
    {
		reset();
    }

	//public void reset(){reset;}

	public void setType(String type){this.type =type;}

	public String getType(){return type;}

	public Packet build() throws InstantiationException
	{
		if(type == null) throw new InstantiationException(I18N.gettext("main.error.type_must_be_specified"));
		if(!(type.equals("get") || type.equals("set") || type.equals("result") || type.equals("error")))
		{
		    throw new InstantiationException(I18N.gettext("main.error.wrong_type") + ": " + type);
		}

		return new InfoQuery(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
