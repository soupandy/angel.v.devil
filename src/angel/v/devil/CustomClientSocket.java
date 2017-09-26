package angel.v.devil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CustomClientSocket {
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String data;
	Socket socket;

	void disconnect() {

		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void printdata(String s) {
		String news = data + s;
		data = news;
	}

	void talk(String st) {
		try {
			oos.writeObject(st);
			String message = (String) ois.readObject();
			printdata("Server said: " + message);
		} catch (IOException e) {
			printdata("IOException\n");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			printdata("Class not found\n");
			e.printStackTrace();
		}
	}

	CustomClientSocket(String ServerIP, Talker talk) {
		data = new String("");
		try {

			InetAddress host = InetAddress.getByName(ServerIP);
			socket = new Socket(host.getHostName(), 7777);
			talk.connected = true;
			talk.oos = new ObjectOutputStream(socket.getOutputStream());
			talk.ois = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
