package angel.v.devil;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.Arrays;

public class NetCommunicator {
	boolean UIready = false;
	Context context;
	CustomServerSocket server = null;
	CustomClientSocket client = null;
	Talker talker;
	Handler handler;
	WaitConnect waitConnect;
	WaitMessage waitMessage;
	Dialog dialog;
	Button dialogButton;
	Thread listener;

	public NetCommunicator(Context c) {
		context = c;
		UIready = false;
	}

	public static String toString(Object obj) {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(obj.getClass().getName());
		result.append(" Object {");
		result.append(newLine);
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				Object object = field.get(obj);
				if (field.getType().isArray()) {
					Object[] arr = (Object[]) object;
					result.append(Arrays.deepToString(arr));
				} else
					result.append(object);

			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");
		return result.toString();
	}

	private boolean mResult;

	public boolean getYesNoWithExecutionStop(String title, String message,
			Context context) {
		// make a handler that throws a runtime exception when a message is
		// received
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				throw new RuntimeException();
			}
		};
		// make a text input dialog and show it
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mResult = true;
				handler.sendMessage(handler.obtainMessage());
			}
		});
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mResult = false;
				handler.sendMessage(handler.obtainMessage());
			}
		});
		alert.show();
		// loop till a runtime exception is triggered.
		try {
			Looper.loop();
		} catch (RuntimeException e2) {
		}

		return mResult;
	}

	boolean check_wifi() {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return (mWifi.isConnected());
	}

	void goUI() {
		waitMessage = new WaitMessage();
		listener = new Thread(waitMessage);
		listener.start();
		UIready = true;
		((angelVdevilActivity) context).goUI();
	}

	private class WaitConnect implements Runnable {
		int runs = 0;

		public void run() {
			runs++;
			//
			dialog.setTitle("Waiting for " + runs + " seconds");
			dialogButton = (Button) dialog
					.findViewById(R.id.dialogButtonCancel);
			dialogButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
//					((angelVdevilActivity) context).network=false;
					dialog.dismiss();
				}
			});
			//
			if (!talker.connected)
				handler.postDelayed(waitConnect, 1000);
			else {
				dialog.dismiss();
				goUI();
			}
		}
	}

	void appendInfo(final String message) {
		((angelVdevilActivity) context).appendInfo(message);
	}

	private class WaitMessage implements Runnable {
		@SuppressWarnings("unused")
		public void run() {
			if (talker.ois == null) {
				appendInfo("ois is null\n");
				handler.postDelayed(waitMessage, 3000);
				return;
			}
			if (true)
				return;
			while (true)
				try {
					String message = (String) talker.ois.readObject();
					appendInfo("Them:" + message);
					if (message.equals("s")) {
						appendInfo("They said s");
						Tile[][] c = (Tile[][]) talker.ois.readObject();
						appendInfo(NetCommunicator.toString(c));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					appendInfo("IO exception");
					return;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					appendInfo("Class not found");
					return;
				}
		}
	}

	void serverDialog() {
		dialog = new Dialog(context);
		dialog.setContentView(R.layout.serverconnect);
		dialog.setTitle("Waiting for connection");
		TextView text = (TextView) dialog.findViewById(R.id.prompt);
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo()
				.getIpAddress());
		text.setText("host IP" + ip);
		dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
		dialogButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
		talker = new Talker();
		server = new CustomServerSocket(talker);
		handler = new Handler();
		waitConnect = new WaitConnect();
		handler.postDelayed(waitConnect, 1000);
	}

	void clientDialog() {
		final Context p = context;
		dialog = new Dialog(p);
		dialog.setContentView(R.layout.clientconnect);
		dialog.setTitle("Connecting to server");
		TextView text = (TextView) dialog.findViewById(R.id.prompt);
		text.setText("Please enter the host IP");
		Button dialogOkButton = (Button) dialog
				.findViewById(R.id.dialogButtonOK);
		dialogOkButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				talker = new Talker();
				EditText et = (EditText) dialog
						.findViewById(R.id.serveraddress);
				client = new CustomClientSocket(et.getText().toString(), talker);
				handler = new Handler();
				waitConnect = new WaitConnect();
				dialog = new Dialog(p);
				dialog.setContentView(R.layout.serverconnect);
				dialog.setTitle("Connecting to server");
				dialog.show();
				handler.postDelayed(waitConnect, 1000);
			}
		});
		Button dialogSuggestButton = (Button) dialog
				.findViewById(R.id.dialogButtonSuggest);
		dialogSuggestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				WifiManager wm = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				String ip = Formatter.formatIpAddress(wm.getConnectionInfo()
						.getIpAddress());
				String toks[] = ip.split("\\.");
				String suggestion = "" + toks[0] + "." + toks[1] + "."
						+ toks[2] + ".";
				EditText et = (EditText) dialog
						.findViewById(R.id.serveraddress);
				et.setText(suggestion);
			}
		});
		dialog.show();
	}
}