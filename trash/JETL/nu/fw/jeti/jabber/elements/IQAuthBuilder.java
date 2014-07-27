package nu.fw.jeti.jabber.elements;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQAuthBuilder implements ExtensionBuilder
{
	public String username;
	public String password;
	public String digest;
	public String resource;
	public String zeroKHash;
	public String zeroKToken;
	public String zeroKSequence;

    public IQAuthBuilder()
    {
		reset();
    }

	public void reset()
	{
		username = null;
		password = null;
		digest = null;
		resource = null;
		zeroKHash =null;
		zeroKSequence =null;
		zeroKToken =null;
	}

	public Extension build()
	{
	    return new IQAuth(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
