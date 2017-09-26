package angel.v.devil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Talker {
	ObjectInputStream ois;
	ObjectOutputStream oos;
	boolean connected;

	public Talker() {
		ois = null;
		oos = null;
		connected = false;
	}

	boolean sendobj(GameMessage obj) {
		if (oos==null) return false;
		try {
			GameMessage mts=new GameMessage(obj);
			oos.writeObject(mts);
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	
	
	boolean sendGenericObject(Object obj) {
		if (oos==null) return false;
		try {
			oos.writeObject(obj);
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	boolean send(String data) {
		if (oos==null) return false;
		try {
			oos.writeObject(data);
			if (data.equals("s")) {
			}
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}

}
