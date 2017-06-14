package tcp.com.hw2;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;



/*
 * note: please configure the chat information before chat to somebody;
 */
public class ChatFrame  extends JFrame
{
	private static final int  MAXLENGTH = 1024;//aximum size of echo datagram
	private static final int TTL = 255; //time to live
	
	private int chatPort;
	private InetAddress chatAddress;
	private int monitorPort;
	private boolean flag = true; // true for unicast ,false for multicast
	private InetAddress chatMultiAddress;
	private DatagramSocket unicastSocket  ;
	private DatagramSocket unicastServerSocket ;
	private MulticastSocket multicastServerSocket;
	private MulticastSocket multicastSocket ;
	private static StringBuilder sendMessage = new StringBuilder();
	
	
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8050213534605487787L;
	
	private JPanel objectPanel = new JPanel();  // ip ,port etc
	
	private JLabel ipLable = new JLabel("target Address :");
	private JTextField ipText = new JTextField(15);
	private JLabel portLabel = new JLabel("Port :");
	private JTextField portText = new JTextField();
	private JLabel monitorPortLabel = new JLabel("Monitor port:");
	private JTextField monitorPortText = new JTextField();
	private JLabel nickName = new JLabel("NickName:");
	private JTextField nickNameText = new JTextField();
	
	
	private JLabel multiLable = new JLabel("Multicast address:");
	private JTextField multiText = new JTextField(15);
	
	
	//private JPanel Panel = new JPanel();
	private ButtonGroup radioButtonGroup = new ButtonGroup();
	private JRadioButton unicastButton = new JRadioButton("Unicast");
	
	private JRadioButton multicastButton = new JRadioButton("Multicast");
	
	
	
	private JTextArea recvMessageArea = new JTextArea(4,40);
	private JScrollPane middlePanel = new JScrollPane(recvMessageArea);

	//private JScrollBar recvMessageScrollBar = new JScrollBar();
	//private JPanel middlePanel = new JPanel();
	
	
	
	private JTextField sendMessageText = new JTextField(50);
	private JButton sendButton = new JButton("Send Message");
	private JPanel rootPanel = new JPanel();
	private Thread unicastServerThread;
	private Thread multicastServerThread;
	
	
	
	
	
	public ChatFrame()
	{
		
		 //first set mulitcast address
		//multiText.setText("224.0.0.1");
		try {
			unicastSocket = new DatagramSocket(); // send unicast socket
			multicastSocket = new MulticastSocket(); // send multicast socket
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setLayout(new BorderLayout());
		
		/*
		 * the head of chat layout
		 */
		objectPanel.setBorder(new TitledBorder("Configure"));
		
		objectPanel.setLayout(new GridLayout(3,4));
		objectPanel.add(ipLable, 0);
		objectPanel.add(ipText, 1);
		objectPanel.add(portLabel, 2);
		objectPanel.add(portText, 3);
		objectPanel.add(multiLable, 4);
		objectPanel.add(multiText, 5);
		//unicastButton.setSelected(true);
		radioButtonGroup.add(unicastButton);
		radioButtonGroup.add(multicastButton);
		objectPanel.add(unicastButton, 6);
		objectPanel.add(multicastButton,7);
		objectPanel.add(monitorPortLabel,8);
		objectPanel.add(monitorPortText, 9);
		//objectPanel.add(nickName, 10);
		//objectPanel.add(nickNameText, 11);
		
		this.add(objectPanel,BorderLayout.NORTH);
		
		/*
		 * 
		 * the middle of chat layout
		 */
		recvMessageArea.setEditable(false);
		middlePanel.setBorder(new LineBorder(Color.BLUE));
		this.add(middlePanel,BorderLayout.CENTER);
		
		/*
		 * 
		 * root  of chat layout
		 */
		rootPanel.setLayout(new FlowLayout());
		rootPanel.add(sendMessageText);
		rootPanel.add(sendButton);
		this.add(rootPanel, BorderLayout.SOUTH);
		
		/*
		 * 
		 * whether is unicast or multicast,default unicast;
		 *
		 */
		unicastButton.addActionListener(new ActionListener()
				{

					@SuppressWarnings("deprecation")
					@Override
					public void actionPerformed(ActionEvent e)
					{
						// TODO Auto-generated method stub
						if(multicastServerThread!=null)
						{
							multicastServerSocket.close();
							multicastServerThread.stop();
						}
						
						
						if(((JRadioButton)e.getSource()).isSelected())
						{
							flag = true;
							System.out.println("unicast mode");
							
						}
						if(flag) //set unicast server end in a new thread
						{
							
								try {
									unicastServerSocket = new DatagramSocket(monitorPort);
								
									
									
								} catch (SocketException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							
								unicastServerThread = new Thread()
								{
									@Override
									public void run()
									{
										while(true)
										{
											try {
												DatagramPacket recvPacket = new DatagramPacket(new byte[MAXLENGTH],MAXLENGTH);
												unicastServerSocket.receive(recvPacket);
												
												
												
												
												recvMessageArea.append("<"+recvPacket.getAddress().getHostAddress()+":" +recvPacket.getPort() +"> :"+new String(recvPacket.getData()));
												recvMessageArea.append("\n");
											
												
												
												

												
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
											
										}
										
										
									}
									
								};
								unicastServerThread.start();
								
								
						}
						
						
					}
			
				});
		multicastButton.addActionListener(new ActionListener()
				{

					@SuppressWarnings("deprecation")
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						if(unicastServerThread != null)
						{
							unicastServerSocket.close();
							unicastServerThread.stop();
						}
						// TODO Auto-generated method stub
						if(((JRadioButton)e.getSource()).isSelected())
						{
							flag = false;
							System.out.println("multicast mode");
							
						}
						if(!flag)
						{
							
							try {
								multicastServerSocket = new MulticastSocket(monitorPort);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								System.out.println("multicasServerSocket is not create successfully");
							}
							
								try {
									multicastServerSocket.joinGroup(chatMultiAddress);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									System.out.println("Can't join multiaddress group");
									e1.printStackTrace();
								}
							
								multicastServerThread = new Thread()
								{
									public void run()
									{
										while(true)
										{
											DatagramPacket recvPacket = new DatagramPacket(new byte[MAXLENGTH],MAXLENGTH);
											try {
												multicastServerSocket.receive(recvPacket);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											recvMessageArea.append("<"+recvPacket.getAddress().getHostAddress()+":" +recvPacket.getPort() +">  to All:"+new String(recvPacket.getData()));
											recvMessageArea.append("\n");  
											//recvPacket.setLength(MAXLENGTH);
											
										}
									}
								};
								multicastServerThread.start();
								
								
								
						
						}
						
					}
			
				}
				);
		/*
		 * Base flag ,select multicat  or unicast
		 * 
		 */
		ipText.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						// TODO Auto-generated method stub
						JTextField serverAddressField = (JTextField)e.getSource();
						try {
							chatAddress = InetAddress.getByName(serverAddressField.getText().trim());
							System.out.println("chat address : " + serverAddressField.getText().trim());
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
					}
			
				});
		portText.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						// TODO Auto-generated method stub
						JTextField portField = (JTextField)e.getSource();
						chatPort = Integer.parseInt(portField.getText().trim());
						System.out.println("chat port: "+chatPort);
						
					}
			
				});
		monitorPortText.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						// TODO Auto-generated method stub
						
					
							JTextField monitorPortText =(JTextField)e.getSource();
							monitorPort = Integer.parseInt(monitorPortText.getText().trim());
						
							System.out.println("monitor port: "+ monitorPort);
						
					}
			
				});
		multiText.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						// TODO Auto-generated method stub
						JTextField multicastAddressText = (JTextField) e.getSource();
						try {
							chatMultiAddress = InetAddress.getByName(multicastAddressText.getText().trim());
							System.out.println("multicast address: "+ multicastAddressText.getText().trim());
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
						
					}
			
				});
		sendMessageText.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						// TODO Auto-generated method stub
						 sendMessage.setLength(MAXLENGTH);
						 sendMessage.replace(0,MAXLENGTH,sendMessageText.getText().trim());
							byte[] bytesToSend = sendMessage.toString().getBytes();
							// TODO Auto-generated method stub
							
							if(flag) //unicast
							{
								
								try {
									
									DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length,chatAddress,chatPort);
									unicastSocket.send(sendPacket);
									recvMessageArea.append("<Me say>: "+ sendMessage.toString());
									recvMessageArea.append("\n");
									sendMessageText.setText("");
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else //multicast 
							{
								try {
									multicastSocket.setTimeToLive(TTL);
									DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length,chatMultiAddress,monitorPort);
									multicastSocket.send(sendPacket);
									//recvMessageArea.append("<Me say to all>: "+ sendMessage.toString());
									//recvMessageArea.append("\n");
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
							}
							
						}
						
						
						
					
			
				});
		sendButton.addActionListener(new ActionListener()
				{
				
					@Override
					public void actionPerformed(ActionEvent e)
					{
						
						sendMessage.setLength(MAXLENGTH);
						sendMessage.replace(0,MAXLENGTH,sendMessageText.getText().trim());
						byte[] bytesToSend = sendMessage.toString().getBytes();
						// TODO Auto-generated method stub
						
						if(flag) //unicast
						{
							
							try {
								
								DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length,chatAddress,chatPort);
								unicastSocket.send(sendPacket);
								recvMessageArea.append("<Me say>: "+ sendMessage.toString());
								recvMessageArea.append("\n");
								sendMessageText.setText("");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						else //multicast 
						{
							try {
								multicastSocket.setTimeToLive(TTL);
								DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length,chatMultiAddress,monitorPort);
								multicastSocket.send(sendPacket);
								sendMessageText.setText("");
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}
						
					}
			
				});
		
		
	
		
	}
	public static void main(String[] args)
	{
		run(new ChatFrame(),800,1000);
	}

}
