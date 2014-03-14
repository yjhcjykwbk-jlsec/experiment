/* 
 *  Created on 28-dec-2004
 */

package nu.fw.jeti.plugins.emoticons;

import java.awt.*;
import java.awt.image.ImageObserver;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JViewport;
import javax.swing.text.*;

/**
 *Prevents animated icons to use CPU even if they are not visible anymore
 */
public class EmoticonsEditorKit extends StyledEditorKit
{

	public ViewFactory getViewFactory()
	{
		return defaultFactory;
	}

	private static final ViewFactory defaultFactory = new StyledViewFactory();

	static class StyledViewFactory implements ViewFactory
	{

		public View create(Element elem)
		{
			String kind = elem.getName();
			if (kind != null)
			{
				if (kind.equals(AbstractDocument.ContentElementName))
				{
					return new LabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName))
				{
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName))
				{
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName))
				{
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) { return new AnimatedIconView(
						elem); }
			}

			// default to text display
			return new LabelView(elem);
		}

		class AnimatedIconView extends IconView implements ImageObserver
		{
			private Container container = null;
			private JViewport viewport;
			private Rectangle bounds = null;
		
			public AnimatedIconView(Element e)
			{
				super(e);
				Icon icon = StyleConstants.getIcon(getAttributes());
				if (icon instanceof ImageIcon)
				{
					((ImageIcon) icon).setImageObserver(this);
				}
			}

			public void setParent(View parent)
			{
				super.setParent(parent);
				container = getContainer();
				if(container!=null) viewport= (JViewport)container.getParent();
			}

			public void paint(Graphics g, Shape s)
			{
				super.paint(g, s);
				bounds = s.getBounds();
			}

			public boolean imageUpdate(Image img, int infoflags, int x, int y,
					int width, int height)
			{
				if (((infoflags & ImageObserver.FRAMEBITS) > 0)
						&& (container != null) && (bounds != null))
				{
					//System.out.println(bounds);
						
					//System.out.println(viewport.getViewRect().contains(bounds));
					//cycles++;
					if(!container.isDisplayable())
					{//clean up when window closed
						//imageIcon.setImageObserver(null);
						container =null;
					}
					else if(viewport.getViewRect().contains(bounds))
					{//only repaint when visible
						container.repaint((int)bounds.getX(),(int)bounds.getY(),(int)bounds.getWidth(),(int)bounds.getHeight());
					}
					//System.out.println(isVisible());
					//System.out.println(container.isDisplayable());
				}
				return true;
			}
		}

	}
}
