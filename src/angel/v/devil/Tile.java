package angel.v.devil;

import android.graphics.Color;

@SuppressWarnings("serial")
public class Tile implements java.io.Serializable {
	public enum directions {
		PLAIN (0),NORTH(1),WEST(2),SOUTH(3),EAST(4),BRIDGE(5);
		public final int i;
		directions(int id) {
			this.i=id;
		}
	};	
	
	boolean NorthUp;
	boolean SouthUp;
	boolean WestUp;
	boolean EastUp;
	boolean NorthDown;
	boolean SouthDown;
	boolean WestDown;
	boolean EastDown;
	int color;
	public int item;
	int floordirection; // 0: plain 1: north (south down-north up), 2:west, 3:south, 4:east, 5: bridge
	
	Tile()
	{
		NorthUp=false;
		SouthUp=false;
		WestUp=false;
		EastUp=false;
		NorthDown=true;
		SouthDown=true;
		WestDown=true;
		EastDown=true;
		color=Color.WHITE;
		item=1;
		floordirection=0;
	}
	
	Tile(boolean blank) {
		if (blank) {
			NorthUp=false;
			SouthUp=false;
			WestUp=false;
			EastUp=false;
			NorthDown=false;
			SouthDown=false;
			WestDown=false;
			EastDown=false;
			color=Color.BLACK;
			item=1;
			floordirection=0;
		} else {
			NorthUp=false;
			SouthUp=false;
			WestUp=false;
			EastUp=false;
			NorthDown=true;
			SouthDown=true;
			WestDown=true;
			EastDown=true;
			color=Color.WHITE;
			item=1;
			floordirection=0;
		}
	}

	public void setHorizontal() {
		WestDown=false;
		EastDown=false;
		if (color==Color.WHITE) color=Color.BLUE;
	}
	public void setVertical() {
		NorthDown=false;
		SouthDown=false;
		if (color==Color.WHITE) color=Color.RED;
	}

}
