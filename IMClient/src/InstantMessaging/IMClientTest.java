package InstantMessaging;

import javax.swing.JFrame;
public class IMClientTest {

	public static void main(String[] args) {
		IMClient client = new IMClient("localhost", 5678);
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.start();

	}

}
