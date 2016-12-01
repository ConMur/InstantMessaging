package InstantMessaging;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class IMServer extends JFrame {
	private ServerSocket server = null;
	private Socket client = null;
	private ObjectInputStream streamIn = null;
	private ObjectOutputStream streamOut = null;
	private JTextField text = null;
	private static JTextArea chatWindow = null;
	private int port = 0;

	public IMServer(int portNumber) {
		super("Instant Messages");
		port = portNumber;
		text = new JTextField();
		text.setEditable(false);
		text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				text.setText("");
			}
		});
		add(text, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(500, 600);
		setVisible(true);
	}

	public void start() {
		try {
			server = new ServerSocket(port, 10);
			while (true)
				try {
					waitForConnection();
					openStreams();
					whileChatting();
				} catch (EOFException eof) {
					showMessage("\nServer ended the conection");
				} finally {
					closeStuff();
				}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void waitForConnection() throws IOException
	{
		showMessage("Waiting for someone to connect");
		client = server.accept();
		showMessage("\nNow connected to " + client.getInetAddress());
	}
	public void openStreams() throws IOException {
		streamIn = new ObjectInputStream(
				client.getInputStream());
		streamOut = new ObjectOutputStream(
				client.getOutputStream());
	}

	public void closeStuff() throws IOException {
		client.close();
		streamIn.close();
	}

	public void whileChatting() {
		String message = "\nYou are now connected!\n";
		showMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) streamIn.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException cnf)
			{
				showMessage("\nThere was an error processing the object type");
			} catch (IOException ioe) {
				chatWindow.append("The input was invalid");
				break;
			}
		} while (!message.equals("CLIENT - END"));
	}

	public void sendMessage(String message) {
		try {
			streamOut.writeObject("SERVER- " + message);
			streamOut.flush();
			showMessage("\nServer - " + message);
		} catch (IOException ioe) {
			chatWindow.append("There was an error displaying your message");
		}
	}

	public void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}

	public void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				text.setEditable(tof);
			}
		});
	}

}
