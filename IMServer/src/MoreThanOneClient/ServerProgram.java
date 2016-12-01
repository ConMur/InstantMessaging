package MoreThanOneClient;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerProgram
{
	// GUI
	static JFrame frame = new JFrame("ADMIN");
	static JTextArea messageArea = new JTextArea(8, 40);

	// The port the server runs on
	private static final int PORT = 9019;

	// Keep track of all the people on the server
	private static ArrayList<String> names = new ArrayList<String>();

	// The set of all the print writers for all the clients. This
	// set is kept so we can easily broadcast messages.
	private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();

	private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private static Date date = new Date();

	public static void main(String[] args) throws Exception
	{
		// Show GUI
		messageArea.setEditable(false);
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.setSize((int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		messageArea.append("[" + dateFormat.format(date)
				+ "] The chat server is running.\n");
		ServerSocket listener = new ServerSocket(PORT, 10);
		try
		{
			while (true)
			{
				new ServerProgram().new Handler(listener.accept()).start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			listener.close();
			System.exit(0);
		}
	}

	public class Handler extends Thread
	{
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket)
		{
			this.socket = socket;
		}

		public void run()
		{
			messageArea.append("[" + dateFormat.format(date)
					+ "] Client connected: " + socket.getInetAddress() + "\n");
			// Create new streams for the socket
			try
			{
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
				out.println("MESSAGE Your streams from the server are ready!");
				out.flush();

				// Loop until the given name is an unique one
				while (true)
				{
					out.println("SUBMITNAME");
					out.flush();
					name = in.readLine();
					if (name == null)
						return;
					synchronized (names)
					{
						if (!names.contains(name))
						{
							names.add(name);
							break;
						}
					}
				}

				// Notify that the name was accepted
				out.println("NAMEACCECPTED");
				out.flush();

				writers.add(out);

				// Take input from this writer and broadcast it to the other
				// valid clients
				while (true)
				{
					if (!socket.isConnected())
					{
						messageArea.append("[" + dateFormat.format(date) + "] "
								+ socket.getInetAddress() + " was disconnected" + "\n");
						socket.close();
						in.close();
						out.close();
					}
					String input = in.readLine();
					if (input == null)
					{
						return;
					}
					String display = name + ": " + input.substring(8);
					for (PrintWriter writer : writers)
					{
						writer.println("MESSAGE " + display);
						writer.flush();
					}
					messageArea.append("[" + dateFormat.format(date) + "] "
							+ display + "\n");
				}
			}
			catch (IOException ioe)
			{
				System.err.println(ioe);
			}
			finally
			{
				// Client is crashing, remove everything of it from the server
				if (name != null)
					names.remove(name);
				if (out != null)
					writers.remove(out);

				try
				{
					socket.close();
					in.close();
					out.close();
				}
				catch (IOException ioe)
				{
					System.err.println(ioe);
				}
			}

		}
	}

}
