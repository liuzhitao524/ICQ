package tcp.com.hw2;

import javax.swing.JFrame;

public class SwingConsole 
{
	public static void run(final JFrame f ,final int width,final int height)
	{
		f.setTitle("Tcp/Ip");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(width, height);
		
		f.setVisible(true);
	}
}
