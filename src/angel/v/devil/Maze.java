package angel.v.devil;

public class Maze implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Tile myMaze[][];
	int width, height;
	public Maze(Tile[][] myMaze, int width, int height) {
		int x,y;
		this.myMaze=new Tile[width][height];
		for (x=0;x<width;x++) for (y=0;y<height;y++) this.myMaze[x][y]=myMaze[x][y];
		this.width = width;
		this.height = height;
	}
}
