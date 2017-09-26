package angel.v.devil;



public class GameMessage implements java.io.Serializable {
	private static final long serialVersionUID = 7852036215208658051L;
	enum MessageType {
		READY_TO_START,
		FULL_MAZE,
		ANGEL_POSITION,
		DEVIL_POSITION,
		HAVE_HAMMER,
		HAVE_SPIKE,
		TRY_TO_MOVE_ANGEL,
		TRY_TO_MOVE_DEVIL,
		ANGEL_IS_AT,
		DEVIL_IS_AT,
		GAME_ON,
		WINNER_ANGEL,
		WINNER_DEVIL,
		WINNER_DRAW,
		TIME_LEFT,
		REPORT_CONTROLS,
		DEVILXDIR,
		DEVILYDIR,
		RESETTILE
	}
	public MessageType message_type;
	public boolean state=false;
	public int value;
	public float x,y,z;
	
	public GameMessage(GameMessage obj) {
	message_type=obj.message_type;
	x=obj.x;
	y=obj.y;
	z=obj.z;
	value=obj.value;
	}
	public GameMessage(MessageType message_type, int value) {
		this.message_type = message_type;
		this.value = value;
	}
	
	
	public GameMessage(MessageType message_type, boolean state) {
		this.message_type = message_type;
		this.state = state;
	}
	
	
	public GameMessage(MessageType message_type, float x, float y, float z) {
		this.message_type = message_type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public GameMessage(MessageType message_type, boolean state, float x, float y, float z,
			int value ) {
		this.message_type = message_type;
		this.state = state;
		this.x = x;
		this.y = y;
		this.value = value;
		this.z = z;
	}
	public GameMessage() {
		message_type=MessageType.READY_TO_START;
		x=0;
		y=0;
		z=0;
		value=0;
	}
	public GameMessage(MessageType message_type) {
		this.message_type = message_type;
	}
}
