//Created on 14-sep-2003
package nu.fw.jeti.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class TitleTimer implements ActionListener
{
	private Timer timer;
	private String first;
	private String second;
	private int teller;
	private int max = 9;
	private JFrame frame;
	private String title;


	public TitleTimer(JFrame frame, String title)
	{
		this.frame = frame;
		this.title =title;
		timer = new Timer(1200,this);
	}

	public void setTitle(String title)
	{
	    this.title = title;
	}

	public void init(String first, String second)
	{
		frame.setTitle(first);
		timer.stop();
		this.first = first;
		this.second = second;
		teller =0;
		timer.restart();
	}

	public void stop()
	{
		timer.stop();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(teller > max)
		{
			teller =0;
			timer.stop();
			frame.setTitle(title);
		}
		else
		{
			if(teller%2==0)
			{
				frame.setTitle(first);
			}
			else
			{
				frame.setTitle(second);
			}
			teller++;
		}
		//System.out.println("timer");
	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
