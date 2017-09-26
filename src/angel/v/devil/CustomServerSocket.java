package angel.v.devil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;

public class CustomServerSocket {
	public ServerSocket server;
	private int port = 7777;
	public String data;
	Thread t;
	public void printdata(String s) {
		String news = data + s;
		data = news;
	}

	public void shutdown() {
		t.stop();
	}
	
	public CustomServerSocket(Talker myTalker) {
		data = new String("");
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		ConnectionHandler handler = new ConnectionHandler(this,myTalker);
		t = new Thread(handler);
		t.start();
	}
}

class ConnectionHandler implements Runnable {
	private Socket socket;
	private CustomServerSocket parent;
	Talker talker;
	

	public void handleConnection() {
	}

	public ConnectionHandler(CustomServerSocket parent,Talker talk) {
		this.parent = parent;
		talker=talk;
		talker.ois=null;
		talker.oos=null;
	}

	public void run() {
		parent.printdata("Waiting for client message...\n");

		while (true) {
			try {
				socket = parent.server.accept();
				talker.connected=true;
				talker.ois = new ObjectInputStream(socket.getInputStream());
				talker.oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
