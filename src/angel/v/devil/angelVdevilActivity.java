package angel.v.devil;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import angel.v.devil.GameMessage.MessageType;

public class angelVdevilActivity extends Activity implements SensorEventListener {
	NetCommunicator myComm;
	WaitGameMessage waitGameMessage;
	Thread listener = null;
	MyAdapter adapter;
	ListView listview;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private String[] hiscores;
	MyTimerTask myTimerTask;
	MyWatcherTask myWatcherTask;
	Handler myTimer;
	Handler myAnimTimer;
	Handler mySensorTimer;
	public DrawView myView = null;
	int screenWidth;
	int screenHeight;
	public Sprite angel = new Sprite(0, 0);
	Sprite devil = new Sprite(0, 0);
	Sprite title = new Sprite(0, 0);
	Sprite play = new Sprite(0, 0);
	Sprite item[] = { new Sprite(0, 0), new Sprite(0, 0), new Sprite(0, 0), new Sprite(0, 0) };
	int maze_width = 1;
	int maze_height = 1;
	int exp_3d = 6;
	int level = 1;
	int maxlevel = 1;
	Tile myMaze[][];
	int tile_width = 128;
	int tile_height = 128;
	float playerXdir = 0;
	float playerXgoing = 0;
	float playerYdir = 0;
	float playerYgoing = 0;
	float networkXdir = 0;
	float networkXgoing = 0;
	float networkYdir = 0;
	float networkYgoing = 0;
	int devilGoing = 0;
	int devil_direction = 0;
	Random r = new Random();
	Paint paint = new Paint();
	public boolean game_on = false;
	public boolean network = false;
	public boolean network_devil = false;
	public boolean playing = false;
	boolean havespike = false;
	boolean havehammer = false;
	int devildizzy = 0;
	private boolean level_unraised = true;
	long timeleft = 0;
	long pausetime = 0;
	private boolean paused = false;
	String lastwinner = "none";
	float gestureX;
	float gestureY;
	int animation = 0;
	int animationstep = 0;
	boolean may_start = false;
	private boolean use_accelerometer = false;
	private boolean play_sounds = false;
	private boolean play_music = false;
	private boolean show_3d = false;
	private boolean show_gl = false;
	private boolean random_maze = true;
	private boolean axis_invert = false;
	boolean third_person = false;
	private boolean big_maze = false;
	private boolean cheat = false;
	int playerDir = 0;
	MediaPlayer mpDing, mpAngel, mpDevil, mpDraw, mpBoing, mpSwisss, music;
	Button fameButton, nameOkButton;
	Button clickButton, upButton, downButton;
	TextView levelTextView;
	ImageButton toggleGLButton, togglePauseButton, settingsButton, quitButton, testButton;
	CheckBox view3dCheck;
	CheckBox viewglCheck;
	CheckBox cheatCheck;
	Spinner accuracySpinner;
	CheckBox musicCheck;
	CheckBox randomMazeCheck;
	CheckBox axisInvertCheck;
	CheckBox thirdPersonCheck;
	CheckBox bigMazeCheck;
	TextView aboutTextView;
	int wall_width = (4 / 2) * 4;
	int wall_height = (25 / 2) * 2;
	int score = 0;
	int floor_height;
	MazeRenderer mMazeRenderer = null;
	int accuracy = 1;
	long timestep = 100;
	int angel_speed = 4;
	int devil_speed = angel_speed;

	void set_speed_step() {
		if (accuracy == 0) {
			timestep = 50;
			angel_speed = 8;
		}
		if (accuracy == 1) {
			timestep = 100;
			angel_speed = 4;
		}
		if (accuracy == 2) {
			timestep = 200;
			angel_speed = 2;
		}
		devil_speed = angel_speed;
	}

	float rN = 0, rS = 0, rE = 0, rW = 0;
	float sw, se, nw, ne;

	void set_floor_raisers(int direction) {
		if (direction == 0) {
			rN = 0;
			rS = 0;
			rE = 0;
			rW = 0;
		}
		if (direction == 1) {
			rN = floor_height;
			rS = 0;
			rE = 0;
			rW = 0;
		}
		if (direction == 2) {
			rN = 0;
			rS = 0;
			rE = 0;
			rW = floor_height;
		}
		if (direction == 3) {
			rN = 0;
			rS = floor_height;
			rE = 0;
			rW = 0;
		}
		if (direction == 4) {
			rN = 0;
			rS = 0;
			rE = floor_height;
			rW = 0;
		}
		if (direction == 5) {
			rN = floor_height / 2;
			rS = floor_height / 2;
			rE = floor_height / 2;
			rW = floor_height / 2;
		}
		sw = rS + rW;
		se = rS + rE;
		nw = rN + rW;
		ne = rN + rE;
	}

	void handle_music() {
		if (play_music && !music.isPlaying())
			music.start();
		if (!play_music && music.isPlaying())
			music.pause();
	}

	void add_hiscore(String name, int score) {
		int f, p;
		p = 9;
		for (f = 9; f >= 0; f--) {
			String vals[] = hiscores[f].split(" "); // Split by space
			int os = Integer.parseInt(vals[1]);
			if (score > os)
				p = f;
		}
		for (f = 9; f > p; f--) {
			hiscores[f] = hiscores[f - 1];
		}
		hiscores[p] = name + " " + score;
	}

	void report_hiscores() {
		int f;
		for (f = 0; f < 10; f++)
			System.err.println(hiscores[f]);
	}

	void new_hiscore() {
		setup_hiscores();
		final LinearLayout nvg = (LinearLayout) findViewById(R.id.entername);
		nvg.setVisibility(View.VISIBLE);
		fameButton = (Button) findViewById(R.id.fameok);
		fameButton.setVisibility(View.INVISIBLE);
		nameOkButton = (Button) findViewById(R.id.nameok);
		nameOkButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				EditText txt = (EditText) findViewById(R.id.yourname);
				String name = txt.getText().toString();
				add_hiscore(name, score);
				save_hiscores();
				score = 0;
				fameButton.setVisibility(View.VISIBLE);
				nvg.setVisibility(View.INVISIBLE);
				setup_hiscores();
			}
		});
	}

	void setup_hiscores() {
		RelativeLayout vg = new RelativeLayout(this);
		ViewGroup.inflate(this, R.layout.fame, vg);
		setChildContentView(vg);
		fameButton = (Button) findViewById(R.id.fameok);
		adapter = new MyAdapter(this);
		listview = (ListView) findViewById(R.id.hiscores);
		listview.setAdapter(adapter);
		fameButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				setChildContentView(myView);
			}
		});
		listview.setOnItemClickListener(new MyListListener());
	}

	public class MyListListener implements OnItemClickListener, OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		}
	}

	void setup_settings() {
		RelativeLayout vg = new RelativeLayout(this);
		ViewGroup.inflate(this, R.layout.main, vg);
		setChildContentView(vg);

		clickButton = (Button) findViewById(R.id.button1);
		upButton = (Button) findViewById(R.id.levelup);
		downButton = (Button) findViewById(R.id.leveldown);
		levelTextView = (TextView) findViewById(R.id.level);
		aboutTextView = (TextView) findViewById(R.id.abouttext);
		musicCheck = (CheckBox) findViewById(R.id.music);
		randomMazeCheck = (CheckBox) findViewById(R.id.randommaze);
		axisInvertCheck = (CheckBox) findViewById(R.id.invertaxis);
		thirdPersonCheck = (CheckBox) findViewById(R.id.thirdperson);
		bigMazeCheck = (CheckBox) findViewById(R.id.bigmaze);
		load_preferences();
		levelTextView.setText("" + level + "/" + maxlevel);
		view3dCheck = (CheckBox) findViewById(R.id.view3d);
		viewglCheck = (CheckBox) findViewById(R.id.viewgl);
		cheatCheck = (CheckBox) findViewById(R.id.cheat);
		accuracySpinner = (Spinner) findViewById(R.id.accuracy);

		clickButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				setChildContentView(myView);
			}
		});
		upButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (level < maxlevel && !playing && !game_on)
					level++;
				levelTextView.setText("" + level + "/" + maxlevel);
				save_preferences();
			}
		});
		downButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (level > 1 && !playing && !game_on)
					level--;
				levelTextView.setText("" + level + "/" + maxlevel);
				save_preferences();
			}
		});
		aboutTextView.setMovementMethod(new ScrollingMovementMethod());
		musicCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				play_music = v;
				save_preferences();
				handle_music();
			}
		});
		view3dCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				show_3d = v;
				save_preferences();
			}
		});
		randomMazeCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				random_maze = v;
				save_preferences();
			}
		});
		axisInvertCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				axis_invert = v;
				save_preferences();
			}
		});
		thirdPersonCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				third_person = v;
				save_preferences();
			}
		});
		bigMazeCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				big_maze = v;
				save_preferences();
			}
		});
		viewglCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				show_gl = v;
				if (!v) {
					third_person = false;
					thirdPersonCheck.setChecked(third_person);
				}
				save_preferences();
			}
		});
		cheatCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton btn, boolean v) {
				cheat = v;
				save_preferences();
			}
		});
		accuracySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				accuracy = pos;
				set_speed_step();
				save_preferences();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		load_preferences();
		musicCheck.setChecked(play_music);
		view3dCheck.setChecked(show_3d);
		randomMazeCheck.setChecked(random_maze);
		axisInvertCheck.setChecked(axis_invert);
		thirdPersonCheck.setChecked(third_person);
		bigMazeCheck.setChecked(big_maze);
		viewglCheck.setChecked(show_gl);
		cheatCheck.setChecked(cheat);
		set_speed_step();
		accuracySpinner.setSelection(accuracy);
	}

	public void playSound(Context context, int soundID) {
		if (this.play_sounds) {
			MediaPlayer mp = MediaPlayer.create(context, soundID);
			mp.start();
		}
	}

	void init_hiscores() {
		hiscores[0] = "Michael 1000";
		hiscores[1] = "Gabriel 900";
		hiscores[2] = "Lucifer 666";
		hiscores[3] = "Beelzebub 606";
		hiscores[4] = "Azazel 590";
		hiscores[5] = "Abraxas 500";
		hiscores[6] = "Leviathan 480";
		hiscores[7] = "Raziel 300";
		hiscores[8] = "Furfur 190";
		hiscores[9] = "Zachariel 100";
	}

	void load_hiscores() {
		SharedPreferences preferences = getSharedPreferences("angel.v.devil", 0);
		hiscores = new String[10];
		int f;
		for (f = 0; f < 10; f++) {
			hiscores[f] = preferences.getString("hiscore" + f, "");
		}
		if (hiscores[0].length() == 0) {
			init_hiscores();
		}
	}

	void load_state() {
		SharedPreferences preferences = getSharedPreferences("angel.v.devil", 0);
		score = preferences.getInt("score", 0);
		playing = preferences.getBoolean("playing", false);
	}

	void load_preferences() {
		SharedPreferences preferences = getSharedPreferences("angel.v.devil", 0);
		use_accelerometer = preferences.getBoolean("use_accelerometer", false);
		play_sounds = preferences.getBoolean("play_sounds", false);
		play_music = preferences.getBoolean("play_music", false);
		show_3d = preferences.getBoolean("show_3d", false);
		show_gl = preferences.getBoolean("show_gl", false);
		cheat = preferences.getBoolean("cheat", false);
		random_maze = preferences.getBoolean("random_maze", true);
		axis_invert = preferences.getBoolean("axis_invert", false);
		third_person = preferences.getBoolean("third_person", false);
		big_maze = preferences.getBoolean("big_maze", false);
		accuracy = preferences.getInt("accuracy", 1);
		level = preferences.getInt("level", 1);
		maxlevel = preferences.getInt("maxlevel", 1);
	}

	void save_hiscores() {
		SharedPreferences preferences = getSharedPreferences("angel.v.devil", 0);
		SharedPreferences.Editor editor = preferences.edit();
		int f;
		for (f = 0; f < 10; f++) {
			editor.putString("hiscore" + f, hiscores[f]);
		}
		editor.commit();
	}

	void save_state() {
		SharedPreferences preferences = getSharedPreferences("angel.v.devil", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("score", score);
		editor.putBoolean("playing", playing);
		editor.commit();
	}

	void save_preferences() {
		SharedPreferences preferences = getSharedPreferences("angel.v.devil", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("use_accelerometer", use_accelerometer);
		editor.putBoolean("play_sounds", play_sounds);
		editor.putBoolean("play_music", play_music);
		editor.putBoolean("show_3d", show_3d);
		editor.putBoolean("show_gl", show_gl);
		editor.putBoolean("cheat", cheat);
		editor.putBoolean("random_maze", random_maze);
		editor.putBoolean("axis_invert", axis_invert);
		editor.putBoolean("third_person", third_person);
		editor.putBoolean("big_maze", big_maze);
		editor.putInt("accuracy", accuracy);
		editor.putInt("level", level);
		editor.putInt("maxlevel", maxlevel);
		editor.commit();

	}

	private class MySensorTask implements Runnable {
		angelVdevilActivity parent;

		public MySensorTask(angelVdevilActivity p) {
			parent = p;
		}

		public void run() {
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(parent, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	private class MyAnimTask implements Runnable {
		angelVdevilActivity parent;
		int step;

		public MyAnimTask(angelVdevilActivity p, int s) {
			parent = p;
			step = s;
		}

		public void onTick() {
			animationstep = step;
			if (animationstep <= 0)
				animation = 0;
			String vals[] = hiscores[9].split(" ");
			int lowesthiscore = Integer.parseInt(vals[1]);
			if (lastwinner.endsWith("DEVIL") && score > lowesthiscore)
				new_hiscore();
			parent.myView.invalidate();
		}

		public void run() {
			if (step > 0) {
				step--;
				parent.myAnimTimer.postDelayed(new MyAnimTask(parent, step), timestep * 2);
			}
			onTick();

		}
	}

	public void test_test_button(Bitmap bb) {
		Bitmap b = Bitmap.createBitmap(16, 16, Config.ARGB_8888);
		Canvas c = new Canvas(b);
		Paint p = new Paint();
		p.setTextSize(16);
		p.setColor(Color.BLACK);
		p.setStyle(Style.FILL);
		c.drawRect(0, 0, 12, 12, p);
		p.setColor(Color.WHITE);
		p.setStyle(Style.FILL_AND_STROKE);
		c.drawText(Character.toString('L'), 2, 14, p);
		int x, y;
		for (x = 0; x < 16; x++)
			for (y = 0; y < 16; y++)
				if (b.getPixel(x, y) == Color.WHITE) {
					b.setPixel(x, y, Color.YELLOW);
				}

		Drawable d = new BitmapDrawable(b);
		testButton.setBackgroundColor(Color.BLUE);
		testButton.setBackgroundDrawable(d);
	}

	private class MyWatcherTask implements Runnable {
		angelVdevilActivity parent;

		public MyWatcherTask(angelVdevilActivity p) {
			parent = p;
		}

		public void onTick() {
			try {
				while (game_on) {
					GameMessage inmessage = (GameMessage) myComm.talker.ois.readObject();
					if (inmessage.message_type == MessageType.GAME_ON && inmessage.state == false)
						game_over();
					if (inmessage.message_type == MessageType.REPORT_CONTROLS) {
						GameMessage outXmessage = new GameMessage(MessageType.DEVILXDIR, (int) networkXdir);
						myComm.talker.sendobj(outXmessage);
						GameMessage outYmessage = new GameMessage(MessageType.DEVILYDIR, (int) networkYdir);
						myComm.talker.sendobj(outYmessage);
					}
					if (inmessage.message_type == MessageType.TIME_LEFT) {
						timeleft = inmessage.value;
						if (!show_gl)
							parent.myView.invalidate();
						if (game_on)
							parent.myTimer.postDelayed(myWatcherTask, timestep);
						return;
					}
					if (inmessage.message_type == MessageType.RESETTILE) {
						int tile_reset_x, tile_reset_y;

						tile_reset_x = (int) (inmessage.x);
						tile_reset_y = (int) (inmessage.y);
						myMaze[tile_reset_x][tile_reset_y].item = 0;
					}
					if (inmessage.message_type == MessageType.ANGEL_IS_AT) {
						int scalex = tile_width * maze_width;
						int scaley = tile_height * maze_height;
						angel.x = (int) (inmessage.x * (float) scalex);
						angel.y = (int) (inmessage.y * (float) scaley);
						angel.z = inmessage.z;
					}
					if (inmessage.message_type == MessageType.DEVIL_IS_AT) {
						int scalex = tile_width * maze_width;
						int scaley = tile_height * maze_height;
						devil.x = (int) (inmessage.x * (float) scalex);
						devil.y = (int) (inmessage.y * (float) scaley);
						devil.z = inmessage.z;
					}
					if (inmessage.message_type == MessageType.WINNER_ANGEL) {
						parent.myTimerTask.set_angel_won();
						parent.game_over();
						parent.myView.invalidate();
					}
					if (inmessage.message_type == MessageType.WINNER_DEVIL) {
						parent.myTimerTask.set_devil_won();
						parent.game_over();
						parent.myView.invalidate();
					}
					if (inmessage.message_type == MessageType.WINNER_DRAW) {
						parent.myTimerTask.set_outoftime();
						parent.game_over();
						parent.myView.invalidate();
					}
				}
			} catch (EOFException e) {
				if (!show_gl)
					parent.myView.invalidate();
				if (game_on)
					parent.myTimer.postDelayed(myWatcherTask, timestep);
				e.printStackTrace();
			} catch (OptionalDataException e) {
				network_failed();
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				network_failed();
				e.printStackTrace();
			}
		}

		public void run() {
			onTick();
		}
	}

	private class MyTimerTask implements Runnable {
		angelVdevilActivity parent;

		public MyTimerTask(angelVdevilActivity p) {
			parent = p;
		}

		void test_item() {
			if (network && !network_devil) {
				int dx = devil.x / tile_width;
				int dy = devil.y / tile_height;
				if (dx > maze_width - 1)
					dx = maze_width - 1;
				if (dy > maze_height - 1)
					dy = maze_height - 1;
				if (dx < 0)
					dx = 0;
				if (dy < 0)
					dy = 0;
				myMaze[dx][dy].item = 0;
				GameMessage outmessage = new GameMessage(MessageType.RESETTILE, (float) dx, (float) dy, (float) 0);
				myComm.talker.sendobj(outmessage);
			}

			int ax = angel.x / tile_width;
			int ay = angel.y / tile_height;
			if (ax > maze_width - 1)
				ax = maze_width - 1;
			if (ay > maze_height - 1)
				ay = maze_height - 1;
			if (ax < 0)
				ax = 0;
			if (ay < 0)
				ay = 0;
			if (myMaze[ax][ay].item == 1) {
				score++;
				myMaze[ax][ay].item = 0;
				if (play_sounds)
					mpDing.start();
			}
			if (myMaze[ax][ay].item == 2) {
				score += 10;
				myMaze[ax][ay].item = 0;
				havespike = true;
				if (play_sounds)
					mpDing.start();
			}
			if (myMaze[ax][ay].item == 3) {
				score += 25;
				myMaze[ax][ay].item = 0;
				havehammer = true;
				if (play_sounds)
					mpDing.start();
			}
			if (myMaze[ax][ay].item == 4) {
				score += 50;
				myMaze[ax][ay].item = 0;
				havespike = false;
				if (play_sounds)
					mpSwisss.start();
			}
			if (network && !network_devil && myMaze[ax][ay].item == 0) {
				GameMessage outmessage = new GameMessage(MessageType.RESETTILE, (float) ax, (float) ay, (float) 0);
				myComm.talker.sendobj(outmessage);
			}

		}

		void set_outoftime() {
			lastwinner = new String("Draw");
			game_on = false;
			animation = 3;
			if (play_sounds)
				mpDraw.start();
			if (network && !network_devil) {
				GameMessage outmessage = new GameMessage(MessageType.WINNER_DRAW);
				myComm.talker.sendobj(outmessage);
			}
		}

		void set_devil_won() {
			lastwinner = new String("DEVIL");
			game_on = false;
			animation = 1;
			if (play_sounds)
				mpDevil.start();
			if (network && !network_devil) {
				GameMessage outmessage = new GameMessage(MessageType.WINNER_DEVIL);
				myComm.talker.sendobj(outmessage);
			}
		}

		void set_angel_won() {
			lastwinner = new String("ANGEL");
			game_on = false;
			animation = 2;
			if (play_sounds)
				mpAngel.start();
			if (network && !network_devil) {
				GameMessage outmessage = new GameMessage(MessageType.WINNER_ANGEL);
				myComm.talker.sendobj(outmessage);
			}
		}

		boolean test_game_end() {
			int dx = devil.x / tile_width;
			int dy = devil.y / tile_height;
			int ax = angel.x / tile_width;
			int ay = angel.y / tile_height;
			if (game_on && timeleft == 0) {
				set_outoftime();
			} else {
				if (dx == ax && dy == ay && devil.z == angel.z) {
					if (havehammer) {
						devildizzy = 100;
						score += 15;
						if (play_sounds)
							mpBoing.start();
						havehammer = false;
					} else if (devildizzy == 0 && !cheat) {
						set_devil_won();
					}
				}
				if (ax == maze_width - 1 && ay == maze_height - 1) {
					set_angel_won();
				}
			}
			return game_on;
		}

		public void onTick() {
			if (!game_on)
				return;
			try_to_move_devil();
			try_to_move_angel();
			test_item();
			if (!test_game_end())
				game_over();
			if (!show_gl)
				parent.myView.invalidate();
		}

		public void run() {
			if (timeleft > 0 && game_on) {
				timeleft -= timestep;

				if (network && !network_devil) {
					GameMessage outmessage = new GameMessage(MessageType.TIME_LEFT, (int) timeleft);
					myComm.talker.sendobj(outmessage);

					if (timeleft / (8 * timestep) * (8 * timestep) == timeleft) {

						GameMessage reqmessage = new GameMessage(MessageType.REPORT_CONTROLS);
						myComm.talker.sendobj(reqmessage);
						try {
							GameMessage inXmessage = (GameMessage) myComm.talker.ois.readObject();

							if (inXmessage.message_type == MessageType.DEVILXDIR) {
								networkXdir = (float) inXmessage.value;
							}

							if (inXmessage.message_type == MessageType.DEVILYDIR) {
								networkYdir = (float) inXmessage.value;
							}
							GameMessage inYmessage = (GameMessage) myComm.talker.ois.readObject();
							if (inYmessage.message_type == MessageType.DEVILXDIR) {
								networkXdir = (float) inXmessage.value;
							}
							if (inYmessage.message_type == MessageType.DEVILYDIR) {
								networkYdir = (float) inYmessage.value;
							}
						} catch (OptionalDataException e) {

							e.printStackTrace();
						} catch (ClassNotFoundException e) {

							e.printStackTrace();
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}

				parent.myTimer.postDelayed(myTimerTask, timestep);
			} else {
				timeleft = 0;
			}
			onTick();
		}
	}

	class Sprite {
		public int x, y;
		public float z;
		Drawable d;
		Drawable id;

		Sprite(int x, int y) {
			this.x = x;
			this.y = y;
			this.z = 0;
		}
	}

	void fill_maze() {
		myMaze = new Tile[maze_width][maze_height];
		int x, y;
		for (y = 0; y < maze_height; y++)
			for (x = 0; x < maze_width; x++) {
				myMaze[x][y] = new Tile();
			}
	}

	void init_rest_values() {
		maze_width = 11;
		maze_height = 8;
		tile_height = screenHeight / (maze_height + 2);
		tile_height = (tile_height / 8) * 8;
		tile_width = ((screenWidth) / (maze_width + maze_height / exp_3d));
		tile_width = (tile_width / 8) * 8;
		wall_width = (wall_height / 8) * 2 + 2;
		floor_height = tile_height;
		fill_maze();
	}

	void init_rest_values(int w, int h) {
		maze_width = w;
		maze_height = h;
		tile_height = screenHeight / (maze_height + 2);
		tile_height = (tile_height / 8) * 8;
		tile_width = ((screenWidth) / (maze_width + maze_height / exp_3d));
		tile_width = (tile_width / 8) * 8;
		wall_width = (wall_height / 8) * 2 + 2;
		floor_height = tile_height;
		fill_maze();
	}

	void horizontal_bridge_road(int x1, int y, int x2) {
		int x, xs, xe;
		xs = x1;
		xe = x2;
		if (xe < xs) {
			xs = x2;
			xe = x1;
		}
		for (x = xs + 1; x < xe; x++) {
			if (myMaze[x][y].color != Color.WHITE && x > xs + 2 && x < xe - 2 && r.nextInt(5) > 2
					&& myMaze[x][y].floordirection == Tile.directions.PLAIN.i
					&& myMaze[x - 1][y].floordirection == Tile.directions.PLAIN.i
					&& myMaze[x + 1][y].floordirection == Tile.directions.PLAIN.i) {
				myMaze[x][y].floordirection = Tile.directions.BRIDGE.i;
				myMaze[x - 1][y].floordirection = Tile.directions.EAST.i;
				myMaze[x + 1][y].floordirection = Tile.directions.WEST.i;
				myMaze[x][y].EastDown = true;
				myMaze[x][y].WestDown = true;
				myMaze[x - 1][y].SouthUp = true;
				myMaze[x - 1][y].NorthUp = true;
				myMaze[x + 1][y].SouthUp = true;
				myMaze[x + 1][y].NorthUp = true;
				myMaze[x - 1][y].SouthDown = true;
				myMaze[x - 1][y].NorthDown = true;
				myMaze[x + 1][y].SouthDown = true;
				myMaze[x + 1][y].NorthDown = true;

			} else
				myMaze[x][y].setHorizontal();
		}
	}

	void vertical_bridge_road(int x, int y1, int y2) {
		int y, ys, ye;
		ys = y1;
		ye = y2;
		if (ye < ys) {
			ys = y2;
			ye = y1;
		}
		for (y = ys + 1; y < ye; y++) {
			if (myMaze[x][y].color != Color.WHITE && y > ys + 2 && y < ye - 2 && r.nextInt(5) > 2
					&& myMaze[x][y].floordirection == Tile.directions.PLAIN.i
					&& myMaze[x][y - 1].floordirection == Tile.directions.PLAIN.i
					&& myMaze[x][y + 1].floordirection == Tile.directions.PLAIN.i) {
				myMaze[x][y].floordirection = Tile.directions.BRIDGE.i;
				myMaze[x][y - 1].floordirection = Tile.directions.SOUTH.i;
				myMaze[x][y + 1].floordirection = Tile.directions.NORTH.i;
				myMaze[x][y].SouthDown = true;
				myMaze[x][y].NorthDown = true;
				myMaze[x][y - 1].EastUp = true;
				myMaze[x][y - 1].WestUp = true;
				myMaze[x][y + 1].EastUp = true;
				myMaze[x][y + 1].WestUp = true;
				myMaze[x][y - 1].EastDown = true;
				myMaze[x][y - 1].WestDown = true;
				myMaze[x][y + 1].EastDown = true;
				myMaze[x][y + 1].WestDown = true;

			} else
				myMaze[x][y].setVertical();
		}
	}

	void horizontal_road(int x1, int y, int x2) {
		int x, xs, xe;
		xs = x1;
		xe = x2;
		if (xe < xs) {
			xs = x2;
			xe = x1;
		}
		for (x = xs + 1; x < xe; x++)
			myMaze[x][y].setHorizontal();
	}

	void vertical_road(int x, int y1, int y2) {
		int y, ys, ye;
		ys = y1;
		ye = y2;
		if (ye < ys) {
			ys = y2;
			ye = y1;
		}
		for (y = ys + 1; y < ye; y++)
			myMaze[x][y].setVertical();
	}

	void node_walls(int xs, int ys, int xe, int ye) { // for the node that
														// exists when we go
														// first horizontally
														// and then vertically
		myMaze[xe][ys].color = Color.CYAN;
		if (ye > ys) {
			myMaze[xe][ys].NorthDown = true;
			myMaze[xe][ys].SouthDown = false;
		} else {
			myMaze[xe][ys].SouthDown = true;
			myMaze[xe][ys].NorthDown = false;
		}
		if (xe > xs) {
			myMaze[xe][ys].EastDown = true;
			myMaze[xe][ys].WestDown = false;
		} else {
			myMaze[xe][ys].WestDown = true;
			myMaze[xe][ys].EastDown = false;
		}
	}

	void start_walls(int xs, int ys, int xe, int ye) { // for the node that
														// exists when we go
														// first vertically and
														// then horizontally
		myMaze[xs][ye].color = Color.CYAN;
		if (ye > ys) {
			myMaze[xs][ye].SouthDown = true;
			myMaze[xs][ye].NorthDown = false;
		} else {
			myMaze[xs][ye].NorthDown = true;
			myMaze[xs][ye].SouthDown = false;
		}
		if (xe > xs) {
			myMaze[xs][ye].WestDown = true;
			myMaze[xs][ye].EastDown = false;
		} else {
			myMaze[xs][ye].EastDown = true;
			myMaze[xs][ye].WestDown = false;
		}
	}

	void fix_bridge_maze() {
		fix_maze();
		int x, y;
		for (y = 0; y < maze_height - 1; y++)
			for (x = 0; x < maze_width; x++) {
				if (myMaze[x][y].EastUp && myMaze[x][y].floordirection != Tile.directions.BRIDGE.i)
					myMaze[x][y].EastDown = true;
				if (myMaze[x][y].WestUp && myMaze[x][y].floordirection != Tile.directions.BRIDGE.i)
					myMaze[x][y].WestDown = true;
				if (myMaze[x][y].SouthUp && myMaze[x][y].floordirection != Tile.directions.BRIDGE.i)
					myMaze[x][y].SouthDown = true;
				if (myMaze[x][y].NorthUp && myMaze[x][y].floordirection != Tile.directions.BRIDGE.i)
					myMaze[x][y].NorthDown = true;
			}
		for (y = 1; y < maze_height - 2; y++)
			for (x = 1; x < maze_width - 1; x++)
				if (myMaze[x][y].color == Color.WHITE) {
					if (myMaze[x + 1][y - 1].floordirection == Tile.directions.SOUTH.i) {
						myMaze[x][y].EastDown = false;
						myMaze[x][y].WestDown = false;
						myMaze[x + 1][y].SouthDown = true;
						myMaze[x + 1][y].NorthDown = true;
					}
					if (myMaze[x - 1][y - 1].floordirection == Tile.directions.SOUTH.i) {
						myMaze[x][y].EastDown = false;
						myMaze[x][y].WestDown = false;
						myMaze[x - 1][y].SouthDown = true;
						myMaze[x - 1][y].NorthDown = true;
					}
					if (myMaze[x - 1][y - 1].floordirection == Tile.directions.EAST.i) {
						myMaze[x][y].SouthDown = false;
						myMaze[x][y].NorthDown = false;
						myMaze[x][y - 1].EastDown = true;
						myMaze[x][y - 1].WestDown = true;
					}
					if (myMaze[x - 1][y + 1].floordirection == Tile.directions.EAST.i) {
						myMaze[x][y].SouthDown = false;
						myMaze[x][y].NorthDown = false;
						myMaze[x][y + 1].EastDown = true;
						myMaze[x][y + 1].WestDown = true;
					}
				}
	}

	void fix_maze() {
		int x, y;

		for (y = 0; y < maze_height - 1; y++)
			for (x = 0; x < maze_width; x++) {
				if (myMaze[x][y + 1].color != Color.WHITE)
					myMaze[x][y].SouthDown = false;
			}
		for (y = 1; y < maze_height; y++)
			for (x = 0; x < maze_width; x++) {
				if (myMaze[x][y - 1].color != Color.WHITE)
					myMaze[x][y].NorthDown = false;
			}
		for (y = 0; y < maze_height; y++)
			for (x = 0; x < maze_width - 1; x++) {
				if (myMaze[x + 1][y].color != Color.WHITE)
					myMaze[x][y].EastDown = false;
			}
		for (y = 0; y < maze_height; y++)
			for (x = 1; x < maze_width; x++) {
				if (myMaze[x - 1][y].color != Color.WHITE)
					myMaze[x][y].WestDown = false;
			}

		for (y = 0; y < maze_height; y++)
			for (x = 0; x < maze_width; x++)
				if (myMaze[x][y].color == Color.WHITE) {
					myMaze[x][y].SouthDown = true;
					myMaze[x][y].NorthDown = true;
					myMaze[x][y].WestDown = true;
					myMaze[x][y].EastDown = true;
					myMaze[x][y].item = 0;
				} else {
					myMaze[x][y].SouthDown = false;
					myMaze[x][y].NorthDown = false;
					myMaze[x][y].WestDown = false;
					myMaze[x][y].EastDown = false;
				}
	}

	void load_maze(String filename) {
		int x, y;

		try {
			File file = new File(getBaseContext().getFilesDir(), filename);
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			for (y = 0; y < maze_height; y++)
				for (x = 0; x < maze_width; x++)
					myMaze[x][y] = (Tile) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("class not found");
			c.printStackTrace();
			return;
		}
	}

	void save_maze(String filename) {
		int x, y;
		try {
			File file = new File(getBaseContext().getFilesDir(), filename);
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			for (y = 0; y < maze_height; y++)
				for (x = 0; x < maze_width; x++)
					out.writeObject(myMaze[x][y]);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	void blank_maze() {
		int x, y;
		for (y = 0; y < maze_height; y++)
			for (x = 0; x < maze_width; x++) {
				myMaze[x][y] = new Tile(true);
			}
	}

	void create_maze(int n) {
		int f, x, y;
		blank_maze();
		if (n == 1) {
			for (f = 0; f < 9; f++)
				myMaze[f][0].SouthDown = true;
			myMaze[3][0].SouthDown = false;
			myMaze[3][7].EastDown = true;
			myMaze[9][7].EastDown = true;
			myMaze[8][6].EastDown = true;
			myMaze[2][3].SouthDown = true;
			myMaze[5][5].SouthDown = true;

			for (f = 0; f < 2; f++) {
				myMaze[f + 1][1].SouthDown = true;
				myMaze[f + 7][1].SouthDown = true;
				myMaze[f][2].SouthDown = true;
				myMaze[f + 7][3].SouthDown = true;
				myMaze[f + 2][4].SouthDown = true;
				myMaze[f + 8][4].SouthDown = true;
				myMaze[f + 9][5].SouthDown = true;
				myMaze[f + 5][6].SouthDown = true;

				myMaze[8][f].EastDown = true;
				myMaze[2][f + 2].EastDown = true;
				myMaze[0][f + 4].EastDown = true;
				myMaze[5][f + 1].EastDown = true;
				myMaze[5][f + 4].EastDown = true;
				myMaze[6][f + 4].EastDown = true;
			}
			for (f = 0; f < 3; f++) {
				myMaze[f + 1][5].SouthDown = true;
				myMaze[f + 7][2].SouthDown = true;

				myMaze[7][f + 5].EastDown = true;
			}
			for (f = 0; f < 4; f++) {
				myMaze[f][6].SouthDown = true;
				myMaze[9][f + 1].EastDown = true;
			}
			for (f = 0; f < 5; f++) {
				myMaze[3][f + 1].EastDown = true;
				myMaze[4][f + 2].EastDown = true;
			}
			myMaze[9][4].EastDown = false;
			for (y = 0; y < maze_height; y++)
				for (x = 0; x < maze_width; x++) {
					int count = 0;
					int clr;
					if (north_available(x, y))
						count++;
					if (south_available(x, y))
						count++;
					if (east_available(x, y))
						count++;
					if (west_available(x, y))
						count++;
					clr = Color.BLUE;
					if (count == 1)
						clr = Color.DKGRAY;
					if (count == 2)
						clr = Color.LTGRAY;
					if (count == 3)
						clr = Color.GREEN;
					if (count == 4)
						clr = Color.RED;
					myMaze[x][y].color = clr;
				}
			for (f = 0; f < 4; f++)
				myMaze[f][7].color = Color.WHITE;
			save_maze("mymaze.1");
			blank_maze();
			load_maze("mymaze.1");
		}
		if (n == 2) {
			for (f = 0; f < 9; f++)
				myMaze[f][0].SouthDown = true;
			myMaze[3][0].floordirection = 4;
			myMaze[4][0].SouthUp = true;
			myMaze[4][0].NorthUp = true;
			myMaze[4][0].floordirection = 5;
			myMaze[5][0].SouthUp = true;
			myMaze[5][0].NorthUp = true;
			myMaze[5][0].floordirection = 5;
			myMaze[6][0].floordirection = 2;

			myMaze[3][2].floordirection = 3;
			myMaze[3][3].floordirection = 5;
			myMaze[3][3].EastUp = true;
			myMaze[3][3].WestUp = true;
			myMaze[3][4].floordirection = 1;

			myMaze[3][0].SouthDown = false;
			myMaze[3][7].EastDown = true;
			myMaze[9][7].EastDown = true;
			myMaze[8][6].EastDown = true;
			myMaze[2][3].SouthDown = true;
			myMaze[5][5].SouthDown = true;

			for (f = 0; f < 2; f++) {
				myMaze[f + 1][1].SouthDown = true;
				myMaze[f + 7][1].SouthDown = true;
				myMaze[f][2].SouthDown = true;
				myMaze[f + 7][3].SouthDown = true;
				myMaze[f + 2][4].SouthDown = true;
				myMaze[f + 8][4].SouthDown = true;
				myMaze[f + 9][5].SouthDown = true;
				myMaze[f + 5][6].SouthDown = true;

				myMaze[8][f].EastDown = true;
				myMaze[2][f + 2].EastDown = true;
				myMaze[0][f + 4].EastDown = true;
				myMaze[5][f + 1].EastDown = true;
				myMaze[5][f + 4].EastDown = true;
				myMaze[6][f + 4].EastDown = true;
			}
			for (f = 0; f < 3; f++) {
				myMaze[f + 1][5].SouthDown = true;
				myMaze[f + 7][2].SouthDown = true;

				myMaze[7][f + 5].EastDown = true;
			}
			for (f = 0; f < 4; f++) {
				myMaze[f][6].SouthDown = true;
				myMaze[9][f + 1].EastDown = true;
			}
			for (f = 0; f < 5; f++) {
				myMaze[3][f + 1].EastDown = true;
				myMaze[4][f + 2].EastDown = true;
			}
			myMaze[9][4].EastDown = false;
			for (y = 0; y < maze_height; y++)
				for (x = 0; x < maze_width; x++) {
					int count = 0;
					int clr;
					if (north_available(x, y))
						count++;
					if (south_available(x, y))
						count++;
					if (east_available(x, y))
						count++;
					if (west_available(x, y))
						count++;
					clr = Color.BLUE;
					if (count == 1)
						clr = Color.DKGRAY;
					if (count == 2)
						clr = Color.LTGRAY;
					if (count == 3)
						clr = Color.GREEN;
					if (count == 4)
						clr = Color.RED;
					myMaze[x][y].color = clr;
				}
			for (f = 0; f < 4; f++)
				myMaze[f][7].color = Color.WHITE;
			save_maze("mymaze.2");
			blank_maze();
			load_maze("mymaze.2");
		}
		if (n == 3) {
			for (f = 0; f < 4; f++)
				myMaze[f][0].SouthDown = true;
			for (f = 0; f < 7; f++)
				myMaze[4][f].EastDown = true;
			for (f = 1; f < 7; f++)
				myMaze[4][f].WestDown = true;
			myMaze[4][2].floordirection = Tile.directions.SOUTH.i;
			myMaze[4][3].floordirection = Tile.directions.BRIDGE.i;
			myMaze[4][2].EastUp = true;
			myMaze[4][2].WestUp = true;
			myMaze[4][3].EastDown = false;
			myMaze[4][3].EastUp = true;
			myMaze[4][3].NorthDown = true;
			myMaze[4][3].SouthDown = true;
			myMaze[4][3].WestDown = false;
			myMaze[4][3].WestUp = true;
			myMaze[4][3].floordirection = Tile.directions.BRIDGE.i;
			myMaze[4][4].floordirection = Tile.directions.NORTH.i;
			myMaze[4][4].EastUp = true;
			myMaze[4][4].WestUp = true;
			for (f = 1; f < 4; f++)
				myMaze[f][4].SouthDown = true;
			for (f = 0; f < 3; f++)
				myMaze[f][4].NorthDown = true;
			for (f = 1; f < 4; f++)
				myMaze[f][2].SouthDown = true;
			for (f = 0; f < 3; f++)
				myMaze[f][2].NorthDown = true;
			myMaze[3][7].WestDown = true;
			myMaze[2][6].EastDown = true;
			myMaze[5][7].EastDown = true;
			myMaze[6][6].EastDown = true;
			for (f = 7; f < 9; f++)
				myMaze[f][6].SouthDown = true;
			for (f = 5; f < 10; f++)
				myMaze[f][5].SouthDown = true;
			for (f = 2; f < 8; f++)
				myMaze[9][f].EastDown = true;
			myMaze[6][2].SouthDown = true;
			myMaze[6][2].NorthDown = true;
			myMaze[6][2].SouthUp = true;
			myMaze[6][2].NorthUp = true;
			myMaze[6][2].floordirection = Tile.directions.EAST.i;
			myMaze[8][2].SouthDown = true;
			myMaze[8][2].NorthDown = true;
			myMaze[8][2].SouthUp = true;
			myMaze[8][2].NorthUp = true;
			myMaze[8][2].floordirection = Tile.directions.WEST.i;
			myMaze[7][2].SouthUp = true;
			myMaze[7][2].NorthUp = true;
			myMaze[7][2].floordirection = Tile.directions.BRIDGE.i;
			myMaze[7][2].EastDown = true;
			myMaze[7][2].WestDown = true;
			myMaze[7][3].WestDown = true;
			myMaze[7][3].SouthDown = true;
			myMaze[8][3].SouthDown = true;
			myMaze[8][4].EastDown = true;
			myMaze[6][4].EastDown = true;
			myMaze[7][5].EastDown = true;
			myMaze[5][3].EastDown = true;
			myMaze[5][4].EastDown = true;
			myMaze[5][5].EastDown = true;
			for (f = 6; f < 11; f++)
				myMaze[f][0].SouthDown = true;
			myMaze[6][1].WestDown = true;
			myMaze[9][1].SouthDown = true;

			for (y = 0; y < maze_height; y++)
				for (x = 0; x < maze_width; x++) {
					int clr;
					clr = Color.BLUE;
					myMaze[x][y].color = clr;
				}

			save_maze("mymaze.3");
			blank_maze();
			load_maze("mymaze.3");
		}
	}

	void create_bonus_items() {
		int x, y, p;
		boolean fieldcreated = false;
		boolean spikecreated = false;
		boolean hammercreated = false;
		int stars = 0;
		myMaze[maze_width - 1][maze_height - 2].item = 4;
		fieldcreated = true;
		for (x = 1; x < maze_width - 1; x++)
			for (y = 1; y < maze_height - 1; y++) {
				if (myMaze[x][y].item == 1)
					stars++;
			}

		for (p = 0; p < 2; p++)
			for (x = 1; x < maze_width - 1; x++)
				for (y = 1; y < maze_height - 1; y++) {
					if (!spikecreated && myMaze[x][y].item == 1 && (r.nextInt(stars) == 0 || p == 1)) {
						spikecreated = true;
						myMaze[x][y].item = 2;
					}
					if (!hammercreated && myMaze[x][y].item == 1 && (r.nextInt(stars) == 0 || p == 1)) {
						hammercreated = true;
						myMaze[x][y].item = 3;
					}
					if (!fieldcreated && myMaze[x][y].item == 1 && (r.nextInt(stars) == 0 || p == 1)) {
						fieldcreated = true;
						myMaze[x][y].item = 4;
					}
				}
	}

	void create_maze() {
		int xs, ys;
		int pys;
		int xe, ye;
		int f;
		xs = 0;
		ys = 0;
		pys = 0;
		fill_maze();
		for (f = 0; f < 10; f++) {
			ye = r.nextInt(maze_height / 2) * 2;
			xe = r.nextInt(maze_width / 2) * 2;
			horizontal_road(xs, ys, xe);
			vertical_road(xe, ys, ye);
			node_walls(xs, ys, xe, ye);
			start_walls(xs, pys, xe, ys);
			pys = ys;
			xs = xe;
			ys = ye;
		}
		xe = maze_width - 1;
		ye = maze_height - 1;
		horizontal_road(xs, ys, xe);
		vertical_road(xe, ys, ye);
		node_walls(xs, ys, xe, ye);
		start_walls(xs, pys, xe, ys);
		myMaze[xe][ye].color = Color.YELLOW;
		fix_maze();
	}

	void create_maze_then_bridge(int n) {
		r.setSeed(n);
		create_maze_then_bridge();
	}

	void create_maze_then_bridge() {
		create_maze();
		int x, y;
		for (y = 0; y < maze_height - 2; y++)
			for (x = 0; x < maze_width - 2; x++)
				if (myMaze[x][y].color == Color.WHITE && myMaze[x + 2][y].color == Color.WHITE
						&& myMaze[x][y + 2].color == Color.WHITE && myMaze[x + 2][y + 2].color == Color.WHITE
						&& myMaze[x + 1][y].color == Color.RED && myMaze[x + 1][y + 2].color == Color.RED
						&& myMaze[x][y + 1].color == Color.BLUE && myMaze[x + 1][y + 1].color == Color.BLUE
						&& myMaze[x + 2][y + 1].color == Color.BLUE && r.nextInt(10) > 5) { // make
																							// a
																							// horizontal
																							// bridge
					myMaze[x + 1][y + 1].floordirection = Tile.directions.BRIDGE.i;
					myMaze[x][y + 1].floordirection = Tile.directions.EAST.i;
					myMaze[x + 2][y + 1].floordirection = Tile.directions.WEST.i;
					myMaze[x + 1][y + 1].SouthUp = true;
					myMaze[x + 1][y + 1].NorthUp = true;
					myMaze[x + 1][y + 1].EastDown = true;
					myMaze[x + 1][y + 1].WestDown = true;
					myMaze[x][y + 1].SouthUp = true;
					myMaze[x][y + 1].NorthUp = true;
					myMaze[x + 2][y + 1].SouthUp = true;
					myMaze[x + 2][y + 1].NorthUp = true;
					myMaze[x][y + 1].SouthDown = true;
					myMaze[x][y + 1].NorthDown = true;
					myMaze[x + 2][y + 1].SouthDown = true;
					myMaze[x + 2][y + 1].NorthDown = true;
					myMaze[x][y + 1].color = Color.GRAY;
					myMaze[x + 1][y + 1].color = Color.GRAY;
					myMaze[x + 2][y + 1].color = Color.GRAY;
				}
		for (y = 0; y < maze_height - 2; y++)
			for (x = 0; x < maze_width - 2; x++)
				if (myMaze[x][y].color == Color.WHITE && myMaze[x + 2][y].color == Color.WHITE
						&& myMaze[x][y + 2].color == Color.WHITE && myMaze[x + 2][y + 2].color == Color.WHITE
						&& myMaze[x + 1][y].color == Color.RED && myMaze[x + 1][y + 2].color == Color.RED
						&& myMaze[x][y + 1].color == Color.BLUE && myMaze[x + 1][y + 1].color == Color.RED
						&& myMaze[x + 2][y + 1].color == Color.BLUE && r.nextInt(10) > 5) { // make
																							// a
																							// vertical
																							// bridge
					myMaze[x + 1][y + 1].floordirection = Tile.directions.BRIDGE.i;
					myMaze[x + 1][y].floordirection = Tile.directions.SOUTH.i;
					myMaze[x + 1][y + 2].floordirection = Tile.directions.NORTH.i;
					myMaze[x + 1][y + 1].EastUp = true;
					myMaze[x + 1][y + 1].WestUp = true;
					myMaze[x + 1][y + 1].SouthDown = true;
					myMaze[x + 1][y + 1].NorthDown = true;
					myMaze[x + 1][y].EastUp = true;
					myMaze[x + 1][y].WestUp = true;
					myMaze[x + 1][y + 2].EastUp = true;
					myMaze[x + 1][y + 2].WestUp = true;
					myMaze[x + 1][y].EastDown = true;
					myMaze[x + 1][y].WestDown = true;
					myMaze[x][y + 2].EastDown = true;
					myMaze[x][y + 2].WestDown = true;
					myMaze[x + 1][y].color = Color.GRAY;
					myMaze[x + 1][y + 1].color = Color.GRAY;
					myMaze[x + 1][y + 2].color = Color.GRAY;
				}
	}

	void create_bridge_maze() {
		int xs, ys;
		int pys;
		int xe, ye;
		int f;
		xs = 0;
		ys = 0;
		pys = 0;
		fill_maze();
		for (f = 0; f < 30; f++) {
			ye = r.nextInt(maze_height / 2) * 2;
			xe = r.nextInt(maze_width / 2) * 2;
			horizontal_bridge_road(xs, ys, xe);
			vertical_bridge_road(xe, ys, ye);
			node_walls(xs, ys, xe, ye);
			start_walls(xs, pys, xe, ys);
			pys = ys;
			xs = xe;
			ys = ye;
		}
		xe = maze_width - 1;
		ye = maze_height - 1;
		horizontal_bridge_road(xs, ys, xe);
		vertical_bridge_road(xe, ys, ye);
		node_walls(xs, ys, xe, ye);
		start_walls(xs, pys, xe, ys);
		myMaze[xe][ye].color = Color.YELLOW;
		fix_bridge_maze();
	}

	void load_sounds() {
		mpDing = MediaPlayer.create(this, R.raw.tick);
		mpAngel = MediaPlayer.create(this, R.raw.angel);
		mpDevil = MediaPlayer.create(this, R.raw.devil);
		mpDraw = MediaPlayer.create(this, R.raw.draw);
		mpBoing = MediaPlayer.create(this, R.raw.boing);
		mpSwisss = MediaPlayer.create(this, R.raw.swisss);
		music = MediaPlayer.create(this, R.raw.andy);
		music.setVolume((float) 0.2, (float) 0.2);
		music.setLooping(true);
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if (bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single
																			// color
																			// bitmap
																			// will
																			// be
																			// created
																			// of
																			// 1x1
																			// pixel
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
					Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	Drawable invertDrawable(Drawable d) {
		Drawable r;
		Matrix matrix = new Matrix();
		matrix.preScale(-1, 1);
		if (d instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
			Bitmap src = bitmapDrawable.getBitmap();
			Bitmap bm = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
			r = new BitmapDrawable(getResources(), bm);
		} else {
			Bitmap src = drawableToBitmap(d);
			Bitmap bm = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
			r = new BitmapDrawable(getResources(), bm);
		}
		return r;
	}

	void load_images() {
		String path = "angel.png";
		try {
			angel.d = Drawable.createFromStream(getAssets().open(path), null);
			angel.id = invertDrawable(angel.d);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "devil.png";
		try {
			devil.d = Drawable.createFromStream(getAssets().open(path), null);
			path = "idevil.png";
			devil.id = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "title.png";
		try {
			title.d = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "play.png";
		try {
			play.d = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "star.png";
		try {
			item[0].d = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "title.png";
		try {
			item[1].d = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "angel.png";
		try {
			item[2].d = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
		path = "devil.png";
		try {
			item[3].d = Drawable.createFromStream(getAssets().open(path), null);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "file " + path + " does not exist", Toast.LENGTH_LONG).show();
		}
	}

	void set_side_buttons() {
		if (game_on && toggleGLButton != null && togglePauseButton != null) {
			toggleGLButton.setVisibility(View.VISIBLE);
			togglePauseButton.setVisibility(View.VISIBLE);
		}
		if (!game_on && toggleGLButton != null && togglePauseButton != null) {
			toggleGLButton.setVisibility(View.INVISIBLE);
			togglePauseButton.setVisibility(View.INVISIBLE);
		}
	}

	void setChildContentView(View newview) {
		ViewGroup parent = (ViewGroup) findViewById(R.id.mainLayout);
		int index = 0;
		View C = parent.getChildAt(index);
		parent.removeView(C);
		C = newview;
		parent.addView(C, index);

	}

	void unpause_game() {
		myTimer.postDelayed(myTimerTask, timestep);
		paused = false;
		Toast.makeText(getBaseContext(), "unpaused", Toast.LENGTH_LONG).show();
	}

	void pause_game() {
		myTimer.removeCallbacks(myTimerTask);
		paused = true;
		Toast.makeText(getBaseContext(), "game paused", Toast.LENGTH_LONG).show();
	}

	void toggle_pause() {
		if (!game_on)
			return;
		if (paused)
			unpause_game();
		else
			pause_game();
	}

	void show_control_buttons() {
		myView.mGLSurfaceView.bdown.bringToFront();
		myView.mGLSurfaceView.bup.bringToFront();
		myView.mGLSurfaceView.bleft.bringToFront();
		myView.mGLSurfaceView.bright.bringToFront();
		myView.mGLSurfaceView.bfire.bringToFront();
	}

	void gl_transparent_background() {
		myView.mGLSurfaceView.child.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		myView.mGLSurfaceView.child.getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}

	void toggle_gl() {
		ViewGroup parent = (ViewGroup) findViewById(R.id.mainLayout);
		int index = 0;
		View C = parent.getChildAt(index);
		if (C == myView.mGLSurfaceView) {
			parent.removeView(C);
			C = myView;
			show_gl = false;
			parent.addView(C, index);
		} else {
			if (game_on) {
				parent.removeView(C);
				myView.mGLSurfaceView = new TouchSurfaceView(myView);
				if (mMazeRenderer == null)
					mMazeRenderer = new MazeRenderer(this);
				gl_transparent_background();
				myView.mGLSurfaceView.setRenderer(mMazeRenderer);
				mMazeRenderer.copyMaze();
				C = myView.mGLSurfaceView;
				show_gl = true;
				parent.addView(C, index);
				myView.mGLSurfaceView.requestFocus();
				myView.mGLSurfaceView.setFocusableInTouchMode(true);
				show_control_buttons();
			}
		}
	}

	void setup_imageButtons() {
		toggleGLButton = (ImageButton) findViewById(R.id.mapButton);
		toggleGLButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				toggle_gl();
			}
		});
		togglePauseButton = (ImageButton) findViewById(R.id.pauseButton);
		togglePauseButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				toggle_pause();
			}
		});
		testButton = (ImageButton) findViewById(R.id.testButton);
		testButton.setEnabled(false);
		testButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				create_maze_then_bridge();
				mMazeRenderer.copyMaze();
			}
		});
		quitButton = (ImageButton) findViewById(R.id.quitButton);
		quitButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (game_on) {
					paused = false;
					lastwinner = new String("DEVIL");
					game_over();
				} else
					save_and_quit();
			}
		});
		settingsButton = (ImageButton) findViewById(R.id.settingsButton);
		settingsButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (game_on && !paused)
					pause_game();
				setup_settings();
			}
		});

	}

	void getscreenWidthFromView() {
		screenWidth = screenWidth * 5 / 6;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Display display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		setContentView(R.layout.parent);
		myComm = new NetCommunicator(this);
		// message=new GameMessage();
		getscreenWidthFromView();
		init_rest_values();
		load_images();
		load_sounds();
		load_preferences();
		load_state();
		load_hiscores();
		save_hiscores();
		handle_music();
		myTimer = new Handler();
		myTimerTask = new MyTimerTask(this);
		myWatcherTask = new MyWatcherTask(this);
		myAnimTimer = new Handler();
		mySensorTimer = new Handler();
		if (use_accelerometer)
			mySensorTimer.postDelayed(new MySensorTask(this), 1);
		new_game(false);
		setup_imageButtons();

	}

	void network_failed() {
		network = false;
		network_devil = false;
		myView.invalidate();
	}

	void new_game(boolean run) {
		playerXgoing = 0;
		playerYgoing = 0;
		playerXdir = 0;
		playerYdir = 0;
		devilGoing = 0;
		playerDir = 0;
		paused = false;
		level_unraised = true;
		havehammer = false;
		havespike = false;
		devildizzy = 0;
		if (!playing) {
			score = 0;
			playing = true;
		}
		if (big_maze)
			init_rest_values(21, 15);
		else
			init_rest_values();

		wall_height = tile_height / 4;

		if (random_maze)
			create_maze_then_bridge(level);
		else
			create_maze(3);
		create_bonus_items();
		if (network) {
			GameMessage outmessage = new GameMessage();
			myComm.talker.sendobj(outmessage);
			Dialog dialog = new Dialog(this);
			dialog.setTitle("Waiting for other player");
			dialog.show();
			try {
				@SuppressWarnings("unused")
				GameMessage message = (GameMessage) myComm.talker.ois.readObject();
			} catch (OptionalDataException e) {
				System.err.println("OPTIONAL DATA EXCEPTION");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				network_failed();
				e.printStackTrace();
			}
			dialog.dismiss();
		}

		if (network) {
			if (network_devil) {
				int x, y;
				try {
					Maze m = (Maze) myComm.talker.ois.readObject();
					maze_width = m.width;
					maze_height = m.height;
					for (x = 0; x < maze_width; x++)
						for (y = 0; y < maze_height; y++)
							myMaze[x][y] = m.myMaze[x][y];
				} catch (OptionalDataException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					network_failed();
					for (x = 0; x < maze_width; x++)
						for (y = 0; y < maze_height; y++)
							myMaze[x][y] = new Tile();
					e.printStackTrace();
				}
			} else {
				Maze m = new Maze(myMaze, maze_width, maze_height);
				myComm.talker.sendGenericObject(m);
			}

		}

		devil.x = tile_width * (maze_width - 1);
		devil.y = tile_height * (maze_height - 1);
		angel.x = 0;
		angel.y = 0;
		if (myView == null) {
			myView = new DrawView(this);
			if (show_gl && mMazeRenderer == null)
				mMazeRenderer = new MazeRenderer(this);
			myView.mGLSurfaceView = new TouchSurfaceView(myView);
			myView.mGLSurfaceView.setRenderer(mMazeRenderer);
			setChildContentView(myView);
		}
		if (run) {
			if (show_gl) {
				if (mMazeRenderer == null)
					mMazeRenderer = new MazeRenderer(this);
				myView.mGLSurfaceView = new TouchSurfaceView(myView);
				gl_transparent_background();
				myView.mGLSurfaceView.setRenderer(mMazeRenderer);
				mMazeRenderer.copyMaze();
				myView.mGLSurfaceView.layout(0, 0, screenWidth, screenHeight);
				setChildContentView(myView.mGLSurfaceView);
				myView.mGLSurfaceView.requestFocus();
				myView.mGLSurfaceView.setFocusableInTouchMode(true);
				show_control_buttons();
			} else
				setChildContentView(myView);
			myView.benchmarkState = 0;
			timeleft = 36000;
			if (cheat)
				timeleft = 3600000;
			if (network) {
				if (network_devil) {
					game_on = true;
					myTimer.postDelayed(myWatcherTask, timestep);
				} else {
					myTimer.postDelayed(myTimerTask, timestep);
					waitGameMessage = new WaitGameMessage();
					/*
					 * if (listener==null || !listener.isAlive()) { listener =
					 * new Thread(waitGameMessage); listener.start(); }
					 */
				}

			} else
				myTimer.postDelayed(myTimerTask, timestep);
			game_on = true;
			set_side_buttons();
		}
	}

	void game_over() {
		may_start = false;
		if (!level_unraised)
			return;
		if (level_unraised && lastwinner.toString().endsWith("ANGEL")) {
			level_unraised = false;
			level++;
			if (maxlevel < level) {
				maxlevel = level;
			}
			save_preferences();
		}
		game_on = false;
		set_side_buttons();
		if (myTimer != null) {
			myTimer.removeCallbacks(myTimerTask);
		}
		if (network && network_devil && myTimer != null) {
			myTimer.removeCallbacks(myWatcherTask);
		}
		if (lastwinner.toString().endsWith("DEVIL"))
			playing = false;
		if (network && !network_devil) {
			if (myTimer != null) {
				myTimer.removeCallbacks(myTimerTask);
			}
		}
		setChildContentView(myView);
		myAnimTimer.postDelayed(new MyAnimTask(this, 20), timestep);
	}

	boolean out_of_bounds(int x, int y) {
		if (x < 0 || x >= maze_width || y < 0 || y >= maze_height)
			return true;
		return false;
	}

	boolean angel_north_available(int x, int y, int z) {
		if (havespike)
			return north_available(x, y, z);
		if (out_of_bounds(x, y))
			return false;
		if (y == 0)
			return false;
		if (myMaze[x][y - 1].item == 4)
			return false;
		return north_available(x, y, z);
	}

	boolean north_available(int x, int y, int z) {
		if (out_of_bounds(x, y))
			return false;
		if (z == 0 || myMaze[x][y].floordirection == 0)
			return north_available(x, y);
		if (myMaze[x][y].NorthUp)
			return false;
		if (y == 0)
			return false;
		if (myMaze[x][y - 1].SouthUp)
			return false;
		return true;
	}

	boolean north_available(int x, int y) {
		if (out_of_bounds(x, y))
			return false;
		if (myMaze[x][y].NorthDown)
			return false;
		if (y == 0)
			return false;
		if (myMaze[x][y - 1].SouthDown)
			return false;
		return true;
	}

	boolean angel_south_available(int x, int y, int z) {
		if (havespike)
			return south_available(x, y, z);
		if (out_of_bounds(x, y))
			return false;
		if (y + 1 == maze_height)
			return false;
		if (myMaze[x][y + 1].item == 4)
			return false;
		return south_available(x, y, z);
	}

	boolean south_available(int x, int y, int z) {
		if (out_of_bounds(x, y))
			return false;
		if (z == 0 || myMaze[x][y].floordirection == 0)
			return south_available(x, y);
		if (myMaze[x][y].SouthUp)
			return false;
		if (y + 1 == maze_height)
			return false;
		if (myMaze[x][y + 1].NorthUp)
			return false;
		return true;
	}

	boolean south_available(int x, int y) {
		if (out_of_bounds(x, y))
			return false;
		if (myMaze[x][y].SouthDown)
			return false;
		if (y + 1 == maze_height)
			return false;
		if (myMaze[x][y + 1].NorthDown)
			return false;
		return true;
	}

	boolean angel_west_available(int x, int y, int z) {
		if (havespike)
			return west_available(x, y, z);
		if (out_of_bounds(x, y))
			return false;
		if (x == 0)
			return false;
		if (myMaze[x - 1][y].item == 4)
			return false;
		return west_available(x, y, z);
	}

	boolean west_available(int x, int y, int z) {
		if (out_of_bounds(x, y))
			return false;
		if (z == 0 || myMaze[x][y].floordirection == 0)
			return west_available(x, y);
		if (myMaze[x][y].WestUp)
			return false;
		if (x == 0)
			return false;
		if (myMaze[x - 1][y].EastUp)
			return false;
		return true;
	}

	boolean west_available(int x, int y) {
		if (out_of_bounds(x, y))
			return false;
		if (myMaze[x][y].WestDown)
			return false;
		if (x == 0)
			return false;
		if (myMaze[x - 1][y].EastDown)
			return false;
		return true;
	}

	boolean angel_east_available(int x, int y, int z) {
		if (havespike)
			return east_available(x, y, z);
		if (out_of_bounds(x, y))
			return false;
		if (x + 1 == maze_width)
			return false;
		if (myMaze[x + 1][y].item == 4)
			return false;
		return east_available(x, y, z);
	}

	boolean east_available(int x, int y, int z) {
		if (out_of_bounds(x, y))
			return false;
		if (z == 0 || myMaze[x][y].floordirection == 0)
			return east_available(x, y);
		if (myMaze[x][y].EastUp)
			return false;
		if (x + 1 == maze_width)
			return false;
		if (myMaze[x + 1][y].WestUp)
			return false;
		return true;
	}

	boolean east_available(int x, int y) {
		if (out_of_bounds(x, y))
			return false;
		if (myMaze[x][y].EastDown)
			return false;
		if (x + 1 == maze_width)
			return false;
		if (myMaze[x + 1][y].WestDown)
			return false;
		return true;
	}

	int select_direction(int x, int y, int s, int z) { // 1: north, 2:west,
														// 3:south, 4:east
		int av[] = new int[4];
		boolean[] available = new boolean[4];
		int av_index;
		av_index = 0;
		int all_index;
		all_index = 0;

		available[0] = north_available(x, y, z);
		available[1] = west_available(x, y, z);
		available[2] = south_available(x, y, z);
		available[3] = east_available(x, y, z);
		for (all_index = 0; all_index < 4; all_index++) {
			if (available[all_index]) {
				av[av_index] = all_index;
				av_index++;
			}
		}
		if (s > av_index) {
			Toast.makeText(getBaseContext(), "SOMETHING IS VERY WRONG", Toast.LENGTH_LONG).show();
			System.err.println("SOMETHING IS VERY WRONG");
			return 0;
		}
		return 1 + av[s];
	}

	void straightenX(Sprite sp, int x) {

		int d = sp.x - x;
		int ad = d;
		if (d < 0)
			ad = -d;
		if (ad < (tile_width / angel_speed))
			sp.x = x;
		else {
			if (x > sp.x)
				sp.x += tile_width / angel_speed;
			else
				sp.x -= tile_width / angel_speed;
		}
	}

	void straightenY(Sprite sp, int y) {

		int d = sp.y - y;
		int ad = d;
		if (d < 0)
			ad = -d;
		if (ad < (tile_height / angel_speed))
			sp.y = y;
		else {
			if (y > sp.y)
				sp.y += tile_height / angel_speed;
			else
				sp.y -= tile_height / angel_speed;
		}
	}

	boolean hor_line_clear(int xs, int y, int xe) {
		int x;
		for (x = xs; x < xe; x++) {
			if (myMaze[x][y].EastDown)
				return false;
			if (myMaze[x + 1][y].WestDown)
				return false;
		}
		return true;
	}

	boolean ver_line_clear(int x, int ys, int ye) {
		int y;
		for (y = ys; y < ye; y++) {
			if (myMaze[x][y].SouthDown)
				return false;
			if (myMaze[x][y + 1].NorthDown)
				return false;
		}
		return true;
	}

	void see_if_visible(int x, int y, int z) {
		int return_direction;
		int tile_x_d;
		int tile_y_d;
		int tile_x_a;
		int tile_y_a;
		return_direction = 0;
		tile_x_d = devil.x / tile_width;
		tile_y_d = devil.y / tile_height;
		tile_x_a = angel.x / tile_width;
		tile_y_a = angel.y / tile_height;
		if (tile_x_d < 0)
			tile_x_d = 0;
		if (tile_y_d < 0)
			tile_y_d = 0;
		if (tile_x_d > maze_width - 1)
			tile_x_d = maze_width - 1;
		if (tile_y_d > maze_height - 1)
			tile_y_d = maze_height - 1;
		if (tile_x_a < 0)
			tile_x_a = 0;
		if (tile_y_a < 0)
			tile_y_a = 0;
		if (tile_x_a > maze_width - 1)
			tile_x_a = maze_width - 1;
		if (tile_y_a > maze_height - 1)
			tile_y_a = maze_height - 1;
		if (tile_y_a == tile_y_d) { // they are on the same level check east and
									// west;
			if (tile_x_a < tile_x_d) { // angel is west
				if (hor_line_clear(tile_x_a, tile_y_a, tile_x_d))
					return_direction = 2;
			}
			if (tile_x_a > tile_x_d) { // angel is east
				if (hor_line_clear(tile_x_d, tile_y_d, tile_x_a))
					return_direction = 4;
			}
		}
		if (tile_x_a == tile_x_d) { // they are on the same column check south
									// and north;
			if (tile_y_a < tile_y_d) { // angel is north
				if (ver_line_clear(tile_x_a, tile_y_a, tile_y_d))
					return_direction = 1;
			}
			if (tile_y_a > tile_y_d) { // angel is south
				if (ver_line_clear(tile_x_d, tile_y_d, tile_y_a))
					return_direction = 3;
			}
		}
		if (return_direction > 0 && devil_direction_available(x, y, return_direction, z)) // make
																							// sure
																							// he
																							// wont
																							// jump
																							// off
																							// a
																							// bridge
			devil_direction = return_direction;
	}

	boolean devil_direction_available(int x, int y, int w, int z) {
		if (w == 1)
			return north_available(x, y, z);
		if (w == 2)
			return west_available(x, y, z);
		if (w == 3)
			return south_available(x, y, z);
		if (w == 4)
			return east_available(x, y, z);
		return false;
	}

	int try_to_turn_devil(int x, int y, int previous_dir, int z) {
		int rvv = previous_dir;

		int p;
		p = r.nextInt(2);
		if (p == 0)
			p = -1;
		rvv += p; // turn one way
		{
			if (rvv < 1)
				rvv += 4;
			if (rvv > 4)
				rvv -= 4;
			if (devil_direction_available(x, y, rvv, z))
				return rvv;
		}
		rvv -= p * 2; // we failed? try the other way
		{
			if (rvv < 1)
				rvv += 4;
			if (rvv > 4)
				rvv -= 4;
			if (devil_direction_available(x, y, rvv, z))
				return rvv;
		}
		rvv -= p; // all right turn back then...
		{
			if (rvv < 1)
				rvv += 4;
			if (rvv > 4)
				rvv -= 4;
			if (devil_direction_available(x, y, rvv, z))
				return rvv;
		}
		rvv += 2 * p; // go back to original direction
		{
			if (rvv < 1)
				rvv += 4;
			if (rvv > 4)
				rvv -= 4;
			if (devil_direction_available(x, y, rvv, z))
				return rvv;
		}
		return 0; // if no direction is available return 0
	}

	void try_to_move_devil() {
		if (devildizzy > 0) {
			devildizzy--;
			return;
		}
		if (!network)
			try_to_move_devil_computer();
		else if (!network_devil)
			try_to_move_devil_network();

		if (network) {
			if (!network_devil) {
				int scalex = tile_width * maze_width;
				int scaley = tile_height * maze_height;
				GameMessage outmessage = new GameMessage(MessageType.DEVIL_IS_AT, devil.x / (float) scalex,
						devil.y / (float) scaley, devil.z);
				myComm.talker.sendobj(outmessage);
			}
		}
	}

	void try_to_move_devil_computer() {
		int tile_x;
		int tile_y;
		tile_x = (this.devil.x) / tile_width;
		tile_y = (this.devil.y) / tile_height;
		if (tile_x < 0)
			tile_x = 0;
		if (tile_y < 0)
			tile_y = 0;
		if (tile_x > maze_width - 1)
			tile_x = maze_width - 1;
		if (tile_y > maze_height - 1)
			tile_y = maze_height - 1;

		set_floor_raisers(myMaze[tile_x][tile_y].floordirection);
		float h = ((sw + se + nw + ne) / 4);
		if (myMaze[tile_x][tile_y].floordirection != 5)
			devil.z = h;
		else if (devil.z > 0) {
			devil.z = floor_height;
		} else {
			devil.z = 0;
		}

		int z = (int) devil.z;
		int directions;
		directions = 0;
		devilGoing = devil_direction;
		int f;
		for (f = 1; f <= 4; f++)
			if (devil_direction_available(tile_x, tile_y, f, z))
				directions++;
		if (tile_x * tile_width + tile_width / 4 > devil.x && tile_y * tile_height + tile_height / 4 > devil.y) {
			if (directions == 2) {
				if (!devil_direction_available(tile_x, tile_y, devilGoing, z))
					devil_direction = try_to_turn_devil(tile_x, tile_y, devilGoing, z);
			}
			if (directions > 2 || directions == 1)
				devil_direction = try_to_turn_devil(tile_x, tile_y, devilGoing, z);
			if (directions == 1)
				devil_direction = try_to_turn_devil(tile_x, tile_y, devilGoing, z);
			if (devil_direction_available(tile_x, tile_y, devilGoing, z) && r.nextInt(2) == 1)
				devil_direction = devilGoing;
			see_if_visible(tile_x, tile_y, z);
			if (directions == 0)
				devil_direction = 0;
		}
		devilGoing = devil_direction;
		if (devil_direction == 1) {
			devil.y -= tile_height / devil_speed;
			straightenX(devil, tile_x * tile_width);
		}
		if (devil_direction == 2) {
			devil.x -= tile_width / devil_speed;
			straightenY(devil, tile_y * tile_height);
		}
		if (devil_direction == 3) {
			devil.y += tile_height / devil_speed;
			straightenX(devil, tile_x * tile_width);
		}
		if (devil_direction == 4) {
			devil.x += tile_width / devil_speed;
			straightenY(devil, tile_y * tile_height);
		}
		if (devil.y < 0)
			devil.y = 0;
		if (devil.x < 0)
			devil.x = 0;
		if (devil.x > (maze_width - 1) * tile_width)
			devil.x = (maze_width - 1) * tile_width;
		if (devil.y > (maze_height - 1) * tile_height)
			devil.y = (maze_height - 1) * tile_height;

	}

	void try_to_move_devil_network() {
		int tile_x;
		int tile_y;

		tile_x = (this.devil.x) / tile_width;
		tile_y = (this.devil.y) / tile_height;

		set_floor_raisers(myMaze[tile_x][tile_y].floordirection);
		float h = ((sw + se + nw + ne) / 4);
		if (myMaze[tile_x][tile_y].floordirection != 5)
			devil.z = h;
		else if (devil.z > 0) {
			devil.z = floor_height;
		} else {
			devil.z = 0;
		}

		int z = (int) devil.z;

		if (networkXdir > 0 && east_available(tile_x, tile_y, z)) {
			networkXgoing = networkXdir;
			networkYgoing = 0;
		}
		if (networkXdir < 0 && west_available(tile_x, tile_y, z)) {
			networkXgoing = networkXdir;
			networkYgoing = 0;
		}
		if (networkYdir > 0 && south_available(tile_x, tile_y, z)) {
			networkYgoing = networkYdir;
			networkXgoing = 0;
		}
		if (networkYdir < 0 && north_available(tile_x, tile_y, z)) {
			networkYgoing = networkYdir;
			networkXgoing = 0;
		}
		if (networkXgoing > 0) { // moving east
			if (east_available(tile_x, tile_y, z)) {
				devil.x += tile_width / devil_speed;
				straightenY(devil, tile_height * tile_y);
			}
		}
		if (networkXgoing < 0) { // moving west
			if (tile_x * tile_width + tile_width / 4 < devil.x) {
				devil.x -= tile_width / devil_speed;
				straightenY(devil, tile_height * tile_y);
			} else if (west_available(tile_x, tile_y, z)) {
				devil.x -= tile_width / devil_speed;
				straightenY(devil, tile_height * tile_y);
			}
		}
		if (networkYgoing > 0) { // moving south
			if (south_available(tile_x, tile_y, z)) {
				devil.y += tile_height / devil_speed;
				straightenX(devil, tile_width * tile_x);
			}
		}
		if (networkYgoing < 0) { // moving north
			if (tile_y * tile_height + tile_height / 4 < devil.y) {
				devil.y -= tile_height / devil_speed;
				straightenX(devil, tile_width * tile_x);
			} else if (north_available(tile_x, tile_y, z)) {
				devil.y -= tile_height / devil_speed;
				straightenX(devil, tile_width * tile_x);
			}
		}
	}

	void try_to_move_angel() {
		int tile_x;
		int tile_y;

		tile_x = (this.angel.x) / tile_width;
		tile_y = (this.angel.y) / tile_height;

		set_floor_raisers(myMaze[tile_x][tile_y].floordirection);
		float h = ((sw + se + nw + ne) / 4);
		if (myMaze[tile_x][tile_y].floordirection != 5)
			angel.z = h;
		else if (angel.z > 0) {
			angel.z = floor_height;
		} else {
			angel.z = 0;
		}

		int z = (int) angel.z;

		if (playerXdir > 0 && angel_east_available(tile_x, tile_y, z)) {
			playerXgoing = playerXdir;
			playerYgoing = 0;
			myView.reset_control_buttons();
		}
		if (playerXdir < 0 && angel_west_available(tile_x, tile_y, z)) {
			playerXgoing = playerXdir;
			playerYgoing = 0;
			myView.reset_control_buttons();
		}
		if (playerYdir > 0 && angel_south_available(tile_x, tile_y, z)) {
			playerYgoing = playerYdir;
			playerXgoing = 0;
			myView.reset_control_buttons();
		}
		if (playerYdir < 0 && angel_north_available(tile_x, tile_y, z)) {
			playerYgoing = playerYdir;
			playerXgoing = 0;
			myView.reset_control_buttons();
		}
		if (playerXgoing > 0) { // moving east
			if (angel_east_available(tile_x, tile_y, z)) {
				angel.x += tile_width / angel_speed;
				straightenY(angel, tile_height * tile_y);
			}
		}
		if (playerXgoing < 0) { // moving west
			if (tile_x * tile_width + tile_width / 4 < angel.x) {
				angel.x -= tile_width / angel_speed;
				straightenY(angel, tile_height * tile_y);
			} else if (angel_west_available(tile_x, tile_y, z)) {
				angel.x -= tile_width / angel_speed;
				straightenY(angel, tile_height * tile_y);
			}
		}
		if (playerYgoing > 0) { // moving south
			if (angel_south_available(tile_x, tile_y, z)) {
				angel.y += tile_height / angel_speed;
				straightenX(angel, tile_width * tile_x);
			}
		}
		if (playerYgoing < 0) { // moving north
			if (tile_y * tile_height + tile_height / 4 < angel.y) {
				angel.y -= tile_height / angel_speed;
				straightenX(angel, tile_width * tile_x);
			} else if (angel_north_available(tile_x, tile_y, z)) {
				angel.y -= tile_height / angel_speed;
				straightenX(angel, tile_width * tile_x);
			}
		}
		if (network) {
			if (!network_devil) {
				int scalex = tile_width * maze_width;
				int scaley = tile_height * maze_height;
				GameMessage outmessage = new GameMessage(MessageType.ANGEL_IS_AT, angel.x / (float) scalex,
						angel.y / (float) scaley, angel.z);
				System.err.println("Angel:" + angel.x + "," + angel.y + "," + angel.z);
				myComm.talker.sendobj(outmessage);
			}
		}
	}

	public class DrawView extends View {
		angelVdevilActivity myparent;
		Context context;
		public TouchSurfaceView mGLSurfaceView;

		int benchmarkState;
		long drawDuration;
		long lastDrawStartTime;
		long lastDrawEndTime;

		public DrawView(Context context) {
			super(context);
			this.context = context;
			myparent = (angelVdevilActivity) context;
			mGLSurfaceView = new TouchSurfaceView(this);
			benchmarkState = 0;
			setDrawingCacheEnabled(true);
		}

		void show_plain_wall(int x1, int y1, int x2, int y2, Canvas c) {

			int f, g;

			paint.setStyle(Paint.Style.STROKE);

			paint.setColor(Color.rgb(128, 64, 0));

			boolean hor = (y1 == y2);
			if (hor) {
				paint.setStrokeWidth(wall_height * 2);
				c.drawLine(x1 * tile_width, y1 * tile_height - wall_height, x2 * tile_width,
						y2 * tile_height - wall_height, paint);
				paint.setStrokeWidth(1);
				paint.setColor(Color.WHITE);
				for (f = 0; f < wall_height * 2; f += wall_height / 2) {
					c.drawLine(x1 * tile_width, y1 * tile_height - f, x2 * tile_width, y2 * tile_height - f, paint);
					for (g = 0; g < wall_height * 2; g += wall_height) {
						c.drawLine(x1 * tile_width, y1 * tile_height - g, x1 * tile_width,
								y2 * tile_height - g - wall_height / 2, paint);
						c.drawLine(x2 * tile_width, y1 * tile_height - g, x2 * tile_width,
								y2 * tile_height - g - wall_height / 2, paint);
						c.drawLine(x1 * tile_width + tile_width / 2, y1 * tile_height - g - wall_height / 2,
								x1 * tile_width + tile_width / 2, y2 * tile_height - g - wall_height, paint);
					}
				}
			}

			else {
				paint.setStrokeWidth(wall_width);
				c.drawLine(x1 * tile_width, y1 * tile_height - wall_height * 2, x2 * tile_width,
						y2 * tile_height - wall_height * 2 + wall_height * 2, paint);
				paint.setStrokeWidth(1);
				paint.setColor(Color.WHITE);
				c.drawLine(x1 * tile_width, y1 * tile_height - wall_height * 2, x2 * tile_width,
						y2 * tile_height - wall_height * 2 + wall_height * 2, paint);
				c.drawLine(x1 * tile_width - wall_width / 2, y1 * tile_height - wall_height * 2,
						x2 * tile_width - wall_width / 2, y2 * tile_height - wall_height * 2 + wall_height * 2, paint);
				c.drawLine(x1 * tile_width + wall_width / 2, y1 * tile_height - wall_height * 2,
						x2 * tile_width + wall_width / 2, y2 * tile_height - wall_height * 2 + wall_height * 2, paint);
				for (f = 0; f < tile_height; f += tile_height / 2) {
					c.drawLine(x1 * tile_width - wall_width / 2, y1 * tile_height + f - wall_height * 2,
							x1 * tile_width, y1 * tile_height + f - wall_height * 2, paint);
					c.drawLine(x1 * tile_width, y1 * tile_height + f + tile_height / 4 - wall_height * 2,
							x1 * tile_width + wall_width / 2, y1 * tile_height + f + tile_height / 4 - wall_height * 2,
							paint);
				}
			}
		}

		void highlight_tile(int x, int y, Canvas c) {
			if (show_3d)
				highlight_3d_tile(x, y, c);
			else
				highlight_plain_tile(x, y, c);
		}

		void highlight_plain_tile(int x, int y, Canvas c) {
			paint.setColor(Color.MAGENTA);
			paint.setStyle(Paint.Style.FILL);
			c.drawCircle(x * tile_width + tile_width / 2, y * tile_height + tile_height / 2,
					(tile_width + tile_height) / 12, paint);
		}

		void highlight_3d_tile(int xset, int y, Canvas c) {
			float x = xset + (float) (maze_height - y) / (float) exp_3d;
			paint.setColor(Color.MAGENTA);
			paint.setStyle(Paint.Style.FILL);
			c.drawCircle(x * tile_width + tile_width / 2, y * tile_height + tile_height / 2,
					(tile_width + tile_height) / 12, paint);
		}

		void show_3d_floor(int xset, int y, int direction, Canvas c) {
			set_floor_raisers(direction);
			float x = xset + (float) (maze_height - y) / (float) exp_3d;
			Path wallpath = new Path();
			wallpath.reset();
			wallpath.moveTo(x * tile_width + wall_width, y * tile_height + wall_width - nw);
			wallpath.lineTo(x * tile_width + tile_width - wall_width, y * tile_height + wall_width - ne);
			wallpath.lineTo(x * tile_width + tile_width - tile_width / exp_3d - wall_width,
					y * tile_height + tile_height - wall_width - se);
			wallpath.lineTo(x * tile_width - tile_width / exp_3d + wall_width,
					y * tile_height + tile_height - wall_width - sw);
			wallpath.moveTo(x * tile_width + wall_width, y * tile_height + wall_width - nw);
			c.drawPath(wallpath, paint);
		}

		void wall_path(int x1, int y1, int d, Canvas c) {
			int y;
			Path wallpath1 = new Path();
			Path wallpath2 = new Path();
			wallpath1.reset();
			wallpath1.moveTo(x1 + wall_width, y1);
			wallpath1.lineTo(x1 - d + wall_width, y1 + tile_height);
			wallpath1.lineTo(x1 - d + wall_width, y1 + tile_height - wall_height);
			wallpath1.lineTo(x1 + wall_width, y1 - wall_height);
			wallpath1.lineTo(x1 + wall_width, y1);
			wallpath2.reset();
			wallpath2.moveTo(x1 + wall_width, y1 - wall_height);
			wallpath2.lineTo(x1 - wall_width, y1 - wall_height);
			wallpath2.lineTo(x1 - d - wall_width, y1 + tile_height - wall_height);
			wallpath2.lineTo(x1 - d + wall_width, y1 + tile_height - wall_height);
			wallpath2.lineTo(x1 + wall_width, y1 - wall_height);
			paint.setColor(Color.rgb(128, 64, 0));
			paint.setStyle(Paint.Style.FILL);
			c.drawPath(wallpath1, paint);
			c.drawPath(wallpath2, paint);
			c.drawRect(x1 - wall_width - d, y1 + tile_height - wall_height, x1 + wall_width - d, y1 + tile_height,
					paint);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			c.drawPath(wallpath1, paint);
			c.drawPath(wallpath2, paint);
			c.drawLine(x1 + wall_width - d / 2, y1 + tile_height / 2 - wall_height, x1 - wall_width - d / 2,
					y1 + tile_height / 2 - wall_height, paint);
			c.drawRect(x1 - wall_width - d, y1 + tile_height - wall_height, x1 + wall_width - d, y1 + tile_height,
					paint);
			for (y = 0; y < wall_height * 2; y += wall_height / 4) {
				c.drawLine(x1 - wall_width - d, y1 + tile_height - y, x1 + wall_width - d, y1 + tile_height - y, paint);
				c.drawLine(x1 + wall_width - d, y1 + tile_height - y, x1 + wall_width,
						y1 + tile_height - y - tile_height, paint);
			}
		}

		void wall_rect(int x1, int y1, int x2, int y2, Canvas c) {
			int xshift, yshift;
			xshift = wall_width / exp_3d;
			yshift = wall_width * tile_height / tile_width;
			Path wallpath = new Path();
			wallpath.moveTo(x1 - xshift, y1 + yshift);
			wallpath.lineTo(x2 - xshift, y1 + yshift);
			wallpath.lineTo(x2 + xshift, y1 - yshift);
			wallpath.lineTo(x1 + xshift, y1 - yshift);
			wallpath.lineTo(x1 - xshift, y1 + yshift);
			wallpath.moveTo((x1 + x2) / 2 - xshift, y1 + yshift);
			wallpath.lineTo((x1 + x2) / 2 + xshift, y1 - yshift);
			paint.setColor(Color.rgb(128, 64, 0));
			paint.setStyle(Paint.Style.FILL);
			c.drawRect(x1 - xshift, y1 + yshift, x2 - xshift, y2 + yshift, paint);
			c.drawPath(wallpath, paint);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			c.drawRect(x1 - xshift, y1 + yshift, x2 - xshift, y2 + yshift, paint);
			c.drawPath(wallpath, paint);

			int y, x;
			int h = (y2 - y1);
			for (y = y1 - h; y < y2; y += (y2 - y1) / 4) {
				c.drawLine(x1 - xshift, y + yshift, x2 - xshift, y + yshift, paint);
			}
			for (y = y1 - h; y < y2; y += (y2 - y1) / 2) {
				for (x = x1; x < x2; x += (x2 - x1) / 2) {
					c.drawLine(x - xshift, y + yshift, x - xshift, y + yshift + (y2 - y1) / 4, paint);
				}
			}
			for (y = y1 - h + (y2 - y1) / 4; y < y2; y += (y2 - y1) / 2) {
				for (x = x1 + (x2 - x1) / 4; x < x2; x += (x2 - x1) / 2) {
					c.drawLine(x - xshift, y + yshift, x - xshift, y + yshift + (y2 - y1) / 4, paint);
				}
			}
		}

		void show_plain_item(int x, int y, Canvas c) {
			int i = myMaze[x][y].item - 1;
			Drawable id = myparent.item[i].d;
			id.setBounds(x * tile_width + wall_width, y * tile_height + wall_width, (x + 1) * tile_width - wall_width,
					(y + 1) * tile_height - wall_width);
			id.draw(c);
		}

		void show_3d_item(int xset, int y, int direction, Canvas c) {
			set_floor_raisers(direction);
			float r = (sw + se + nw + ne) / 4;
			float x = xset + (float) (maze_height - y) / (float) exp_3d;
			int i = myMaze[xset][y].item - 1;
			Drawable id = myparent.item[i].d;
			id.setBounds((int) (x * tile_width + wall_width), (int) (y * tile_height + wall_width - r),
					(int) ((x + 1) * tile_width - wall_width), (int) ((y + 1) * tile_height - wall_width - r));
			id.draw(c);
		}

		void show_3d_wall(int xset, int y, boolean horizontal, int higher, Canvas c) {

			float x = xset + (float) (maze_height - y) / (float) exp_3d;
			paint.setColor(Color.rgb(128, 64, 0));
			if (horizontal) {
				wall_rect((int) (x * tile_width), (int) (y * tile_height - wall_height - higher),
						(int) ((x + 1) * tile_width), (int) (y * tile_height - higher), c);
			} else {
				wall_path((int) (x * tile_width), (int) (y * tile_height - higher), (int) (tile_width / exp_3d), c);
			}
		}

		void show_3d_tile(int x, int y, Canvas c) {
			paint.setColor(myMaze[x][y].color);
			paint.setStyle(Paint.Style.FILL);
			show_3d_floor(x, y, myMaze[x][y].floordirection, c);
			if (myMaze[x][y].NorthDown)
				show_3d_wall(x, y, true, 0, c);
			if (myMaze[x][y].NorthUp)
				show_3d_wall(x, y, true, floor_height, c);
			if (myMaze[x][y].WestDown)
				show_3d_wall(x, y, false, 0, c);
			if (myMaze[x][y].WestUp)
				show_3d_wall(x, y, false, floor_height, c);
			if (myMaze[x][y].EastDown)
				show_3d_wall(x + 1, y, false, 0, c);
			if (myMaze[x][y].EastUp)
				show_3d_wall(x + 1, y, false, floor_height, c);
			if (myMaze[x][y].item > 0)
				show_3d_item(x, y, myMaze[x][y].floordirection, c);

		}

		void show_3d_tile_south(int x, int y, Canvas c) {

			if (myMaze[x][y].SouthDown)
				show_3d_wall(x, y + 1, true, 0, c);
			if (myMaze[x][y].SouthUp)
				show_3d_wall(x, y + 1, true, floor_height, c);
			paint.setColor(myMaze[x][y].color);
			paint.setStyle(Paint.Style.FILL);
			if (myMaze[x][y].floordirection == 5)
				show_3d_floor(x, y, 5, c);
		}

		void show_tile_south(int x, int y, Canvas c) {
			if (show_3d)
				show_3d_tile_south(x, y, c);
			else
				show_plain_tile_south(x, y, c);
		}

		void show_plain_tile_south(int x, int y, Canvas c) {
			if (myMaze[x][y].SouthDown)
				show_plain_wall(x, y + 1, x + 1, y + 1, c);
			if (myMaze[x][y].SouthUp)
				show_plain_wall(x, y + 1, x + 1, y + 1, c);
			paint.setColor(myMaze[x][y].color);
			paint.setStyle(Paint.Style.FILL);
			if (myMaze[x][y].floordirection == 5)
				show_plain_floor(x, y, c);
		}

		void show_tile(int x, int y, Canvas c) {
			if (show_3d)
				show_3d_tile(x, y, c);
			else
				show_plain_tile(x, y, c);
		}

		void draw_hor_lines(int x1, int y1, int x2, int y2, Canvas c) {
			int f;
			for (f = 0; f <= 4; f++) {
				c.drawLine(x1 + f * (x2 - x1) / 4, y1, x1 + f * (x2 - x1) / 4, y2, paint);
			}
		}

		void draw_ver_lines(int x1, int y1, int x2, int y2, Canvas c) {
			int f;
			for (f = 0; f <= 4; f++) {
				c.drawLine(x1, y1 + f * (y2 - y1) / 4, x2, y1 + f * (y2 - y1) / 4, paint);
			}
		}

		void show_plain_floor(int x, int y, Canvas c) {
			paint.setColor(myMaze[x][y].color);
			paint.setStyle(Paint.Style.FILL);
			c.drawRect(x * tile_width + wall_width, y * tile_height + wall_width, (x + 1) * tile_width - wall_width,
					(y + 1) * tile_height - wall_width, paint);
			if (myMaze[x][y].color != Color.WHITE)
				paint.setColor(Color.BLACK);
			c.drawCircle(x * tile_width + tile_width / 2, y * tile_height + tile_height / 2,
					(tile_width + tile_height) / 8, paint);
			paint.setColor(Color.WHITE);
			if (myMaze[x][y].floordirection == Tile.directions.BRIDGE.i
					|| myMaze[x][y].floordirection == Tile.directions.EAST.i
					|| myMaze[x][y].floordirection == Tile.directions.WEST.i) {
				draw_hor_lines(x * tile_width + wall_width, y * tile_height + wall_width,
						(x + 1) * tile_width - wall_width, (y + 1) * tile_height - wall_width, c);
			}
			if (myMaze[x][y].floordirection == Tile.directions.BRIDGE.i
					|| myMaze[x][y].floordirection == Tile.directions.SOUTH.i
					|| myMaze[x][y].floordirection == Tile.directions.NORTH.i) {
				draw_ver_lines(x * tile_width + wall_width, y * tile_height + wall_width,
						(x + 1) * tile_width - wall_width, (y + 1) * tile_height - wall_width, c);
			}
		}

		void show_plain_tile(int x, int y, Canvas c) {
			show_plain_floor(x, y, c);
			if (myMaze[x][y].NorthDown)
				show_plain_wall(x, y, x + 1, y, c);
			if (myMaze[x][y].WestDown)
				show_plain_wall(x, y, x, y + 1, c);
			if (myMaze[x][y].EastDown)
				show_plain_wall(x + 1, y, x + 1, y + 1, c);
			if (myMaze[x][y].item > 0)
				show_plain_item(x, y, c);
			if (myMaze[x][y].NorthUp)
				show_plain_wall(x, y, x + 1, y, c);
			if (myMaze[x][y].WestUp)
				show_plain_wall(x, y, x, y + 1, c);
			if (myMaze[x][y].EastUp)
				show_plain_wall(x + 1, y, x + 1, y + 1, c);
		}

		void display_start_screen(Canvas c) {
			Drawable ad = this.myparent.angel.d;
			Drawable dd = this.myparent.devil.d;
			Drawable td = this.myparent.title.d;
			Drawable pd = this.myparent.play.d;
			ad.setBounds(0, 0, screenWidth / 3, screenWidth / 3 + 0 * screenHeight);
			ad.draw(c);
			dd.setBounds(2 * screenWidth / 3, 0, screenWidth, screenWidth / 3 + 0 * screenHeight);
			dd.draw(c);
			td.setBounds(screenWidth / 3, 0, 2 * screenWidth / 3, screenHeight / 3);
			td.draw(c);
			pd.setBounds(screenWidth / 3, screenHeight / 3, 2 * screenWidth / 3, 2 * screenHeight / 3);
			pd.draw(c);
			paint.setColor(Color.RED);
			paint.setTextSize(tile_height / 2);
			c.drawText("Last Winner:" + lastwinner, screenWidth / 3, screenHeight, paint);

			c.drawText("Network:" + network + ",devil:" + network_devil, screenWidth / 3,
					screenHeight - tile_height / 2, paint);
			if (playing) {
				c.drawText("Current Score:" + score, screenWidth / 3, screenHeight - tile_height, paint);
			} else {
				c.drawText("Ready for new game", screenWidth / 3, screenHeight - tile_height, paint);
			}

			paint.setColor(Color.YELLOW);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(7);
			c.drawRect(screenWidth / 3, screenHeight * 2 / 3, screenWidth / 2, screenHeight * 5 / 6, paint);
			c.drawRect(screenWidth / 2, screenHeight * 2 / 3, screenWidth * 2 / 3, screenHeight * 5 / 6, paint);
			paint.setStyle(Paint.Style.FILL);
			if (play_sounds)
				paint.setColor(Color.GREEN);
			else
				paint.setColor(Color.GRAY);
			c.drawCircle(screenWidth * 5 / 12, screenHeight * 2 / 3 + tile_height / 4, tile_height / 6, paint);
			paint.setTextSize(tile_height / 2);
			c.drawText("Sounds", screenWidth / 3 + 5, screenHeight * 5 / 6 - 5, paint);
			if (use_accelerometer)
				paint.setColor(Color.GREEN);
			else
				paint.setColor(Color.GRAY);
			c.drawCircle(screenWidth * 7 / 12, screenHeight * 2 / 3 + tile_height / 4, tile_height / 6, paint);
			paint.setTextSize(tile_height / 2);
			c.drawText("Gravity", screenWidth / 2 + 5, screenHeight * 5 / 6 - 5, paint);
			paint.setColor(Color.GREEN);
			if (myComm.UIready) {
				if (network) {
					if (network_devil) {
						c.drawText("Connected", screenWidth * 2 / 3, screenHeight / 2, paint);
					} else {
						c.drawText("Connected", 0, screenHeight / 2, paint);
					}

				}
			}
		}

		void display_screen(Canvas c) {
			if (animation > 0) {
				display_game_screen(c);
				if (animation == 1) {
					Drawable ad = this.myparent.angel.d;
					paint.setColor(Color.RED);
					float xdiff = (float) tile_width * (maze_height - myparent.angel.y / (float) tile_height)
							- tile_width;
					int xset = (int) (myparent.angel.x + xdiff / exp_3d);

					ad.setBounds(xset - tile_width * animationstep / 10,
							myparent.angel.y - tile_height * animationstep / 10, xset + tile_width * animationstep / 10,
							myparent.angel.y + tile_height * animationstep / 10);
					ad.draw(c);
				}
				if (animation == 2) {
					Drawable dd = this.myparent.devil.d;
					paint.setColor(Color.GREEN);
					float xdiff = (float) tile_width * (maze_height - myparent.devil.y / (float) tile_height)
							- tile_width;
					int xset = (int) (myparent.devil.x + xdiff / exp_3d);
					dd.setBounds(xset - tile_width * animationstep / 10,
							myparent.devil.y - tile_height * animationstep / 10, xset + tile_width * animationstep / 10,
							myparent.devil.y + tile_height * animationstep / 10);
					dd.draw(c);
				}
				if (animation == 3) {
					Drawable ad = this.myparent.angel.d;
					Drawable dd = this.myparent.devil.d;
					paint.setColor(Color.BLUE);
					float xdiff = (float) tile_width * (maze_height - myparent.devil.y / (float) tile_height)
							- tile_width;
					int xset = (int) (myparent.devil.x + xdiff / exp_3d);
					dd.setBounds(xset - tile_width * animationstep / 10,
							myparent.devil.y - tile_height * animationstep / 10, xset + tile_width * animationstep / 10,
							myparent.devil.y + tile_height * animationstep / 10);
					dd.draw(c);
					xdiff = (float) tile_width * (maze_height - myparent.angel.y / (float) tile_height) - tile_width;
					xset = (int) (myparent.angel.x + xdiff / exp_3d);
					ad.setBounds(xset - tile_width * animationstep / 10,
							myparent.angel.y - tile_height * animationstep / 10, xset + tile_width * animationstep / 10,
							myparent.angel.y + tile_height * animationstep / 10);
					ad.draw(c);
				}

			} else if (game_on)
				display_game_screen(c);
			else
				display_start_screen(c);
		}

		void show_3d_angel(Canvas c) {
			int h = (int) myparent.angel.z;
			Drawable ad = myparent.angel.d;
			if (timeleft / (2 * timestep) * (2 * timestep) == timeleft) {
				ad = myparent.angel.id;
			}
			float xdiff = (float) tile_width * (maze_height - myparent.angel.y / (float) tile_height) - tile_width;
			int xset = (int) (myparent.angel.x + xdiff / exp_3d);
			ad.setBounds(xset - tile_width / 2, -h + myparent.angel.y - tile_height * 3 / 2, xset + 3 * tile_width / 2,
					-h + myparent.angel.y + tile_height / 2);
			ad.draw(c);
		}

		void show_angel(Canvas c) {
			if (show_3d)
				show_3d_angel(c);
			else
				show_plain_angel(c);
		}

		void show_plain_angel(Canvas c) {
			int h = (int) myparent.angel.z;
			Drawable ad = myparent.angel.d;
			if (timeleft / (2 * timestep) * (2 * timestep) == timeleft) {
				ad = myparent.angel.id;
			}
			ad.setBounds(myparent.angel.x - tile_width / 2, -h + myparent.angel.y - tile_height,
					myparent.angel.x + 3 * tile_width / 2, -h + myparent.angel.y + tile_height);
			ad.draw(c);
		}

		void show_devil(Canvas c) {

			if (show_3d)
				show_3d_devil(c);
			else
				show_plain_devil(c);
		}

		void show_3d_devil(Canvas c) {
			int h = (int) myparent.devil.z;
			Drawable dd = myparent.devil.d;
			if (timeleft / (2 * timestep) * (2 * timestep) == timeleft) {
				dd = myparent.devil.id;
			}
			float xdiff = (float) tile_width * (maze_height - myparent.devil.y / (float) tile_height) - tile_width;
			int xset = (int) (myparent.devil.x + xdiff / exp_3d);
			dd.setBounds(xset - tile_width / 2, -h + myparent.devil.y - tile_height * 3 / 2, xset + 3 * tile_width / 2,
					-h + myparent.devil.y + tile_height / 2);
			dd.draw(c);
		}

		void show_plain_devil(Canvas c) {
			int h = (int) myparent.devil.z;
			Drawable dd = myparent.devil.d;
			if (timeleft / (2 * timestep) * (2 * timestep) == timeleft) {
				dd = myparent.devil.id;
			}
			dd.setBounds(myparent.devil.x - tile_width / 2, -h + myparent.devil.y - tile_height,
					myparent.devil.x + 3 * tile_width / 2, -h + myparent.devil.y + tile_height);
			dd.draw(c);
		}

		Bitmap bufferB = null;

		void display_game_screen(Canvas c) {

			if (benchmarkState == 1) {
				return;
			}
			if (benchmarkState == 0) {
				lastDrawStartTime = SystemClock.uptimeMillis();
				benchmarkState = 1;
				setDrawingCacheEnabled(true);
				bufferB = Bitmap.createBitmap(getDrawingCache());
				setDrawingCacheEnabled(false);
				Canvas bufferC = new Canvas(bufferB);
				render_game_screen(bufferC);
				lastDrawEndTime = SystemClock.uptimeMillis();
				drawDuration = lastDrawEndTime - lastDrawStartTime;
				benchmarkState = 2;
				c.drawBitmap(bufferB, 0, 0, paint);
				return;
			}
			long curtime = SystemClock.uptimeMillis();
			if (benchmarkState >= 2) {
				if (lastDrawEndTime + timestep + drawDuration * 2 > curtime) {
					if (bufferB != null) {
						c.drawBitmap(bufferB, 0, 0, paint);
					}
					return;
				}
				if (benchmarkState == 2) {
					lastDrawStartTime = SystemClock.uptimeMillis();
					benchmarkState = 3;
					setDrawingCacheEnabled(true);
					Bitmap bufferBB = Bitmap.createBitmap(getDrawingCache());
					setDrawingCacheEnabled(false);
					Canvas bufferC = new Canvas(bufferBB);
					render_game_screen(bufferC);
					bufferB = bufferBB;
					lastDrawEndTime = SystemClock.uptimeMillis();
					drawDuration = lastDrawEndTime - lastDrawStartTime;
					benchmarkState = 2;
					c.drawBitmap(bufferB, 0, 0, paint);
				}
			}
		}

		void render_game_screen(Canvas c) {
			int x, y;
			int tile_x, tile_y;
			Matrix m = new Matrix();
			m.postTranslate(0, (float) tile_height * 2);
			c.setMatrix(m);
			for (y = 0; y < maze_height; y++) {
				for (x = 0; x < maze_width; x++) {
					show_tile(x, y, c);
					tile_x = (myparent.angel.x) / tile_width;
					tile_y = (myparent.angel.y) / tile_height;
					if (tile_x == x && tile_y == y)
						highlight_tile(tile_x, tile_y, c);
					tile_x = (myparent.devil.x) / tile_width;
					tile_y = (myparent.devil.y) / tile_height;
					if (tile_x == x && tile_y == y)
						highlight_tile(tile_x, tile_y, c);
				}
				tile_y = (myparent.angel.y + tile_height - tile_height / 4) / tile_height;
				tile_x = (myparent.angel.x) / tile_width;
				if (tile_y == y)
					show_angel(c);
				tile_x = (myparent.devil.x) / tile_width;
				tile_y = (myparent.devil.y + tile_height - tile_height / 4) / tile_height;
				if (tile_y == y)
					show_devil(c);
				for (x = 0; x < maze_width; x++) {
					show_tile_south(x, y, c);
				}
				tile_y = (myparent.angel.y + tile_height - tile_height / 4) / tile_height;
				tile_x = (myparent.angel.x) / tile_width;
				if (tile_y == y && angel.z != 0)
					show_angel(c);
				tile_x = (myparent.devil.x) / tile_width;
				tile_y = (myparent.devil.y + tile_height - tile_height / 4) / tile_height;
				if (tile_y == y && devil.z != 0)
					show_devil(c);
			}

			paint.setColor(Color.GREEN);
			paint.setTextSize(tile_height / 2);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setStrokeWidth(1);
			c.drawText("Time:" + timeleft / 1000 + "sec Score:" + score + ",lvl:" + level + "/" + maxlevel, 0,
					-tile_height, paint);
		}

		void disconnect() {
			if (!network)
				return;
			if (myComm.talker != null) {
				if (!myComm.talker.connected)
					return;
			} else
				return;
			if (network_devil) {
				if (myComm.client != null) {
					myComm.client.disconnect();
					try {
						myComm.client.socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				if (myComm.talker.connected)
					try {
						myComm.talker.ois.close();
						myComm.talker.oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (myComm.server != null) {
					try {
						myComm.server.server.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					myComm.server.shutdown();
				}
				
			}

		}

		void toggleConnectAsAngel() {
			if (!network)
				connectAsAngel();
			else {
				disconnect();
				network = false;
			}
			invalidate();
		}

		void toggleConnectAsDevil() {
			if (!network)
				connectAsDevil();
			else {
				disconnect();
				network = false;
			}
			invalidate();
		}

		void connectAsDevil() {
			myComm = new NetCommunicator(myparent);
			if (myComm.check_wifi()) {
				myComm.clientDialog();
				network = true;
				network_devil = true;
			} else {
				if (myComm.getYesNoWithExecutionStop("Wifi is off", "try to connect anyway?", context)) {
					network = true;
					network_devil = true;
					myComm.clientDialog();
				}
			}
		}

		void connectAsAngel() {
			myComm = new NetCommunicator(myparent);
			if (myComm.check_wifi()) {
				network = true;
				network_devil = false;
				myComm.serverDialog();
			} else {
				if (myComm.getYesNoWithExecutionStop("Wifi is off", "try to connect anyway?", context)) {
					network = true;
					network_devil = false;
					myComm.serverDialog();
				}
			}
		}

		public boolean handleStartTouch(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				may_start = true;
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if (x < screenWidth / 3)
					toggleConnectAsAngel();
				if (x > 2 * screenWidth / 3)
					toggleConnectAsDevil();
				if (x > screenWidth / 3 && x < 2 * screenWidth / 3 && y < screenHeight / 3) {
					setup_settings();
				}
				if (x > screenWidth / 3 && x < 2 * screenWidth / 3 && y > 5 * screenHeight / 6) {
					setup_hiscores();
				}
				if (x > screenWidth / 3 && x < 2 * screenWidth / 3 && y > screenHeight / 3 && y < 2 * screenHeight / 3)
					new_game(true);
				if (x > screenWidth / 3 && x < screenWidth / 2 && y > screenHeight * 2 / 3
						&& y < screenHeight * 5 / 6) {
					play_sounds = !play_sounds;
					save_preferences();
					handle_music();
					invalidate();

				}
				if (x > screenWidth / 2 && x < screenWidth * 2 / 3 && y > screenHeight * 2 / 3
						&& y < screenHeight * 5 / 6) {
					use_accelerometer = !use_accelerometer;
					if (use_accelerometer) {
						if (play_sounds)
							mpDing.start();
						myparent.mySensorTimer.postDelayed(new MySensorTask(myparent), 1);
					} else
						mSensorManager.unregisterListener(myparent);
					save_preferences();
					invalidate();
				}
				break;
			}
			return true;

		}

		void reset_control_buttons() {
			myView.mGLSurfaceView.bright.setPressed(false);
			myView.mGLSurfaceView.bleft.setPressed(false);
			myView.mGLSurfaceView.bup.setPressed(false);
			myView.mGLSurfaceView.bdown.setPressed(false);
			myView.mGLSurfaceView.bfire.setPressed(false);
		}

		void handle_action(float x, float y) {
			if (!network || !network_devil)
				handle_angel_action(x, y);
			else
				handle_devil_action(x, y);
		}

		void handle_devil_action(float x, float y) {
			float d = x * x + y * y;

			int dir = 0; // 0: straignt, 1:right : -1(3):left 2: back

			if (d > 1) {
				if (x * x > y * y) {
					if (x > 0)
						dir = 1;
					else
						dir = 3;
				} else {
					if (y > 0)
						dir = 2;
					else
						dir = 0;
				}
			}

			if (dir == 1)
				myView.mGLSurfaceView.bright.setPressed(true);
			else
				myView.mGLSurfaceView.bright.setPressed(false);
			if (dir == 3)
				myView.mGLSurfaceView.bleft.setPressed(true);
			else
				myView.mGLSurfaceView.bleft.setPressed(false);
			if (dir == 0)
				myView.mGLSurfaceView.bup.setPressed(true);
			else
				myView.mGLSurfaceView.bup.setPressed(false);
			if (dir == 2)
				myView.mGLSurfaceView.bdown.setPressed(true);
			else
				myView.mGLSurfaceView.bdown.setPressed(false);

			if (third_person) {
				playerDir += dir;
				dir = playerDir % 4;
			}
			if (dir == 1) {
				networkXdir = 1;
				networkYdir = 0;
			}
			if (dir == 3) {
				networkXdir = -1;
				networkYdir = 0;
			}
			if (dir == 0) {
				networkXdir = 0;
				networkYdir = -1;
			}
			if (dir == 2) {
				networkXdir = 0;
				networkYdir = 1;
			}
			if (third_person) {
				playerDir = dir;
			}
			if (d <= 1) { // tap : stop moving
				networkXdir = networkYdir = networkXgoing = networkYgoing = 0;
				reset_control_buttons();
			}
			devil_direction = 1 + ((4 - playerDir) % 4);
		}

		void handle_angel_action(float x, float y) {
			float d = x * x + y * y;

			int dir = 0; // 0: straignt, 1:right : -1(3):left 2: back

			if (d > 1) {
				if (x * x > y * y) {
					if (x > 0)
						dir = 1;
					else
						dir = 3;
				} else {
					if (y > 0)
						dir = 2;
					else
						dir = 0;
				}
			}

			if (dir == 1)
				myView.mGLSurfaceView.bright.setPressed(true);
			else
				myView.mGLSurfaceView.bright.setPressed(false);
			if (dir == 3)
				myView.mGLSurfaceView.bleft.setPressed(true);
			else
				myView.mGLSurfaceView.bleft.setPressed(false);
			if (dir == 0)
				myView.mGLSurfaceView.bup.setPressed(true);
			else
				myView.mGLSurfaceView.bup.setPressed(false);
			if (dir == 2)
				myView.mGLSurfaceView.bdown.setPressed(true);
			else
				myView.mGLSurfaceView.bdown.setPressed(false);

			if (third_person) {
				playerDir += dir;
				dir = playerDir % 4;
			}
			if (dir == 1) {
				playerXdir = 1;
				playerYdir = 0;
			}
			if (dir == 3) {
				playerXdir = -1;
				playerYdir = 0;
			}
			if (dir == 0) {
				playerXdir = 0;
				playerYdir = -1;
			}
			if (dir == 2) {
				playerXdir = 0;
				playerYdir = 1;
			}
			if (third_person) {
				playerDir = dir;
			}
			if (d <= 1) { // tap : stop moving
				playerXdir = playerYdir = playerXgoing = playerYgoing = 0;
				reset_control_buttons();
			}

		}

		public boolean handleGameTouch(MotionEvent event) {
			float xp = event.getX();
			float yp = event.getY();
			float x, y;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				gestureX = xp;
				gestureY = yp;
				break;
			case MotionEvent.ACTION_MOVE:
				/*
				 * x = xp-gestureX; y = yp-gestureY; handle_action(x,y);
				 * gestureX=(int)xp; gestureY=(int)yp;
				 */
				break;
			case MotionEvent.ACTION_UP:
				x = xp - gestureX;
				y = yp - gestureY;

				handle_action(x, y);
				break;
			}
			return true;

		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (game_on)
				return (handleGameTouch(event));
			else if (animation == 0)
				return (handleStartTouch(event));
			else
				return true;
		}

		protected void onDraw(Canvas canvas) {
			display_screen(canvas);
			super.onDraw(canvas);
		}
	}

	class TouchSurfaceView extends ViewGroup {
		GLSurfaceView child;
		DrawView myView;
		Button bup, bdown, bleft, bright, bfire;

		public TouchSurfaceView(DrawView myView) {
			super(myView.context);
			this.myView = myView;
			Context context = myView.context;
			child = new GLSurfaceView(context);
			addView(child);
			bup = new Button(context);
			bup.setText("^");
			bup.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(bup);
			bdown = new Button(context);
			bdown.setText("v");
			bdown.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(bdown);

			bleft = new Button(context);
			bleft.setText("<");
			bleft.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(bleft);

			bright = new Button(context);
			bright.setText(">");
			bright.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(bright);

			bfire = new Button(context);
			bfire.setText(".");
			bfire.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(bfire);

			final TouchSurfaceView surview = this;

			bleft.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					surview.myView.handle_action(-10, 0);
				}
			});
			bright.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					surview.myView.handle_action(10, 0);
				}
			});
			bup.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					surview.myView.handle_action(0, -10);
				}
			});
			bdown.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					surview.myView.handle_action(0, 10);
				}
			});
			bfire.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					surview.myView.handle_action(0, 0);
				}
			});

		}

		public void setRenderer(MazeRenderer mMazeRenderer) {
			child.setRenderer(mMazeRenderer);

		}

		@Override
		public boolean onTouchEvent(MotionEvent e) {
			boolean r = myView.handleGameTouch(e);
			child.requestRender();
			return r;
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

			child.measure(right - left, bottom - top);
			child.layout(0, 0, right - left, bottom - top);
			bup.measure(right - left, bottom - top);
			int buph = bup.getMeasuredHeight();
			int bupw = bup.getMeasuredWidth();
			bup.layout(0, 0, bupw, buph);
			bdown.measure(right - left, bottom - top);
			int bdownh = bdown.getMeasuredHeight();
			int bdownw = bdown.getMeasuredWidth();
			bdown.layout(0, buph, bdownw, buph + bdownh);

			bright.measure(right - left, bottom - top);
			int brighth = bright.getMeasuredHeight();
			int brightw = bright.getMeasuredWidth();
			bright.layout(right - brightw, 0, right, brighth);

			bleft.measure(right - left, bottom - top);
			int blefth = bleft.getMeasuredHeight();
			int bleftw = bleft.getMeasuredWidth();
			bleft.layout(right - bleftw - brightw, 0, right - brightw, blefth);

			bfire.measure(right - left, bottom - top);
			int bfireh = bfire.getMeasuredHeight();
			int bfirew = bfire.getMeasuredWidth();
			bfire.layout(right - bfirew, bottom - bfireh, right, bottom);
		}
	}

	public class MyAdapter extends BaseAdapter {

		LayoutInflater inflater;

		public MyAdapter(Context c) {
			inflater = ((Activity) c).getLayoutInflater();
		}

		public int getCount() {
			return hiscores.length;
		}

		public Object getItem(int position) {
			return hiscores[position];
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item, null);

			}
			convertView.setVisibility(View.VISIBLE);
			convertView.setBackgroundColor(Color.CYAN);
			TextView entry = (TextView) convertView.findViewById(R.id.nametext);
			TextView entryscore = (TextView) convertView.findViewById(R.id.scoretext);
			entry.setTextColor(Color.BLACK);
			entryscore.setTextColor(Color.BLUE);

			String vals[] = hiscores[position].split(" "); // Split by space
			entry.setText(vals[0]);
			entryscore.setText(vals[1]);
			return convertView;
		}

	}

	private class WaitGameMessage implements Runnable {
		private volatile boolean running = true;

		@SuppressWarnings("unused")
		public void shutdown() {
			running = false;
		}

		public void run() {
			if (myComm.talker.ois == null) {
				return;
			}
			while (running)
				try {
					@SuppressWarnings("unused")
					GameMessage inmessage = (GameMessage) myComm.talker.ois.readObject();
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

	void save_and_quit() {
		save_state();
		System.exit(0);
	}

	protected void onPause() {
		save_and_quit();
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	void handle_sensor_action(float x, float y) {
		if (!network || !network_devil)
			handle_sensor_angel_action(x, y);
		else
			handle_sensor_devil_action(x, y);
	}

	void handle_sensor_devil_action(float x, float y) {
		networkXdir = 0;
		networkYdir = 0;
		float d = x * x + y * y;
		if (d > 1) {
			if (x * x > y * y) { // moving up - down , because screen is
									// oriented landscape
				networkYdir = x;
			} else { // moving left right
				networkXdir = y;
			}
		}
		float nx, ny;
		int f;
		for (f = 0; f < playerDir; f++) {
			nx = -networkYdir;
			ny = networkXdir;
			networkXdir = nx;
			networkYdir = ny;
		}
	}

	void handle_sensor_angel_action(float x, float y) {
		playerXdir = 0;
		playerYdir = 0;
		float d = x * x + y * y;
		if (d > 1) {
			if (x * x > y * y) { // moving up - down , because screen is
									// oriented landscape
				playerYdir = x;
			} else { // moving left right
				playerXdir = y;
			}
		}
		float nx, ny;
		int f;
		for (f = 0; f < playerDir; f++) {
			nx = -playerYdir;
			ny = playerXdir;
			playerXdir = nx;
			playerYdir = ny;
		}

	}

	public void onSensorChanged(SensorEvent event) {
		if (!game_on)
			return;
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;

		float x = event.values[0];
		float y = event.values[1];

		if (axis_invert) {
			x = event.values[1];
			y = -event.values[0];
		}

		handle_sensor_action(x, y);
	}

	public void appendInfo(String message) {

	}

	public void goUI() {
		myView.invalidate();
	}
}
