package InstantMessaging;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class IMClient extends JFrame
{
	private JTextField text = null;
	private JTextArea chatWindow = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private String message = "";
	private String hostName;
	private int port;
	private Socket client = null;
	
	public IMClient(String host, int portNumber)
	{
		super("Instant Messaging Client");
		hostName = host;
		port = portNumber;
		text = new JTextField();
		text.setEditable(false);
		text.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						sendMessage(e.getActionCommand());
						text.setText("");
					}
				}
				);
		add(text, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(500,600);
		setVisible(true);
	}
	
	public void start()
	{
		try
		{
			connectToServer();
			setUpStreams();
			whileChatting();
		}catch(EOFException eof)
		{
			showMessage("\nThe client terminated the connection");
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}finally{
			closeStuff();
		}
	}
	private void connectToServer() throws IOException
	{
		showMessage("Attempting to connect to the server\n");
		client = new Socket(InetAddress.getByName(hostName), port);
		showMessage("Connected to: " + client.getInetAddress().getHostName());
	}
	private void setUpStreams() throws IOException
	{
		out = new ObjectOutputStream(client.getOutputStream());
		out.flush();
		in = new ObjectInputStream(client.getInputStream());
		showMessage("\nStreams set up\n");
	}
	private void whileChatting() throws IOException
	{
		ableToType(true);
		do
		{
			try {
				message = (String) in.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException cnf) {
				showMessage("There was an error processing the object type");
			}
		}while(!message.equals("SERVER - END"));
	}
	private void closeStuff()
	{
		showMessage("\nClosing down");
		ableToType(false);
		try
		{
			in.close();
			out.close();
			client.close();
		}catch (IOException ioe)
		{
		ioe.printStackTrace();
		}
	}
	private void sendMessage(String message)
	{
		try
		{
			out.writeObject("CLIENT - " + message);
			out.flush();
			showMessage("\nCLIENT - " + message);
		}catch (IOException ioe)
		{
			chatWindow.append("There was an error sending the message");
		}
	}
	private void showMessage(final String m)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						chatWindow.append(m);
					}
				}
			);
	}
	private void ableToType(final boolean tof)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						text.setEditable(tof);
					}
				}
			);
	}
}
