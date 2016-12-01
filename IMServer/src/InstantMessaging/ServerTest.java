package InstantMessaging;

import java.io.IOException;

import javax.swing.JFrame;
public class ServerTest {


	public static void main(String[] args) throws IOException {
		IMServer server = new IMServer(5678);
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.start();
		
	}
}
