package MoreThanOneClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientProgram implements WindowListener
{
	private BufferedReader in;
	private PrintWriter out;
	JFrame frame = new JFrame("Chat");
	JTextField text = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);
	Socket socket = null;

	public ClientProgram()
	{
		// Set up GUI
		text.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(text, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		// Add a listener to send the text to the server when the user presses
		// the enter key
		text.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				out.println("MESSAGE " + text.getText());
				text.setText("");
			}
		});
	}

	/**
	 * Prompt for the address of the server
	 * @return the address of the server
	 */
	private String getServerAddress()
	{
		return JOptionPane.showInputDialog(frame,
				"Enter the IP address of the server", "Welcome",
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Prompt for the user name to use in the server
	 * @return the user name to use in the server
	 */
	private String getName()
	{
		return JOptionPane.showInputDialog(frame, "Choose a user name",
				"User Name Selection", JOptionPane.PLAIN_MESSAGE);
	}

	private void run() throws IOException
	{
		String address = getServerAddress();
		messageArea.append("Attempting to connect to the server...\n");
		try
		{
			socket = new Socket(address, 9019);
		}
		catch (ConnectException ce)
		{
			messageArea.append("There was an error connecting to the server\n");
			ce.printStackTrace();
			System.exit(0);
		}
		messageArea.append("Connected to the server, setting up streams\n");

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		messageArea.append("Client streams are set up!\n");

		// Process messages from the server
		while (true)
		{
			try
			{
				String line = in.readLine();
				if (line.startsWith("SUBMITNAME"))
				{
					out.println(getName());
				}
				else if (line.startsWith("NAMEACCECPTED"))
				{
					text.setEnabled(true);
					text.setEditable(true);
				}
				else if (line.startsWith("MESSAGE"))
				{
					messageArea.append(line.substring(8) + "\n");
				}
			}
			catch (Exception e)
			{
				System.out.println("server closed");
				e.printStackTrace();
				socket.close();
				System.exit(0);
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		ClientProgram client = new ClientProgram();
		client.run();

	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		System.out.println("closing");
		try
		{
			socket.close();
			in.close();
		}
		catch (IOException ioe)
		{
			System.err.println("Client sockets did not close properly");
			ioe.printStackTrace();
		}
		out.close();
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

}