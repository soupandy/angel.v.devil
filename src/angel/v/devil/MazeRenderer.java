package angel.v.devil;

import static android.opengl.GLES10.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;

public class MazeRenderer implements GLSurfaceView.Renderer{
	int ts;
	float angelx,angely;
	angelVdevilActivity parent;
	Tile[][] myMaze;
	int maze_width,maze_height;
	Key key;
	Hammer hammer;
	Door door;
	
	ModelObj glDevil,glRobe,glHalo,glWings,glEyes,glHead,glYEyes;
	
	void render_item(int x,int y,int i,GL10 gl) {

			float xf,yf,zf;
			xf=((float) x)/10f;
			yf=((float) y)/10f;
			zf=0;
			if (myMaze[x][y].floordirection>0) zf-=0.05;
			if (myMaze[x][y].floordirection==5) zf-=0.05;
			if (i==1) {
				
	        	Star star=new Star (xf,yf+0.05f,zf+0.1f,xf+0.1f,yf+0.05f,zf);
	        	star.draw(gl);
			}
			if (i==2) {
				set_glcolor_mode(gl);
				key.x=xf+0.05f;
				key.y=yf+0.05f;
				key.z=zf+0.05f;
				key.draw(gl);
				set_glplain_mode(gl);
			}
			if (i==3) {
				set_glcolor_mode(gl);
				set_glplain_mode(gl);
				hammer.x=xf+0.05f;
				hammer.y=yf+0.05f;
				hammer.z=zf+0.05f;
				gl.glColor4f(1f,0f,0f,1f);
				hammer.draw(gl);
				set_glplain_mode(gl);
			}
			if (i==4) {
				set_glcolor_mode(gl);
				door.x=xf+0.05f+(parent.dooropen?0.04f:0);
				door.y=yf+0.05f;
				door.z=zf+0.05f;
				door.angle=parent.dooropen?-45:0;
				door.draw(gl);
				set_glplain_mode(gl);
			}

	}
	

	void copyMaze() {
		int f;
    	
    	myMaze=parent.myMaze; 	
    	ts=countWalls();
        mWall=new Wall[ts];
        mFloor=new Floor[maze_width*maze_height];
    	
        f=0;
        int x,y;
        for (y=0;y<maze_height;y++)
			for (x=0;x<maze_width;x++)
				{
				float xf,yf;
				xf=((float) x)/10f;
				yf=((float) y)/10f;
				parent.set_floor_raisers(myMaze[x][y].floordirection);
				float p1,p2,p3,p4;
				p1=0.f;p2=0.f;p3=0.f;p4=0.f;
				if (myMaze[x][y].floordirection==5)	{p1=-0.1f;p2=-0.1f;p3=-0.1f;p4=-0.1f;}
				if (myMaze[x][y].floordirection==Tile.directions.NORTH.i)	{p1=-0.1f;p2=-0.1f;p3=-0.f;p4=-0.f;}
				if (myMaze[x][y].floordirection==Tile.directions.SOUTH.i)	{p1=-0.f;p2=-0.f;p3=-0.1f;p4=-0.1f;}
				if (myMaze[x][y].floordirection==Tile.directions.EAST.i)	{p1=-0.f;p2=-0.1f;p3=-0.1f;p4=-0.f;}
				if (myMaze[x][y].floordirection==Tile.directions.WEST.i)	{p1=-0.1f;p2=-0.f;p3=-0.f;p4=-0.1f;}
				mFloor[y*maze_width+x]=new Floor(xf,yf,.1f,xf+.1f,yf+.1f,.01f,myMaze[x][y].color,p1,p2,p3,p4);
				if (myMaze[x][y].NorthDown) { 
					mWall[f]=new Wall(xf,yf,0f,xf+0.1f,yf+0.01f,0.1f,0,0,0,0);
					f++;
					}
				if (myMaze[x][y].SouthDown) { 
					mWall[f]=new Wall(xf,yf+.09f,0,xf+.1f,yf+.1f,0.1f,0,0,0,0);
					f++;
					}
				if (myMaze[x][y].EastDown) { 
					mWall[f]=new Wall(xf+.09f,yf,0,xf+.1f,yf+.1f,.1f,0,0,0,0);
					f++;
					}
				if (myMaze[x][y].WestDown) { 
					mWall[f]=new Wall(xf,yf,0,xf+0.01f,yf+.1f,.1f,0,0,0,0);
					f++;
					}
				if (myMaze[x][y].NorthUp) { 
					mWall[f]=new Wall(xf,yf,0f,xf+0.1f,yf+0.01f,0.11f,p1,p2,p3,p4);
					f++;
					}
				if (myMaze[x][y].SouthUp) { 
					mWall[f]=new Wall(xf,yf+.09f,0f,xf+.1f,yf+.1f,0.11f,p1,p2,p3,p4);
					f++;
					}
				if (myMaze[x][y].EastUp) { 
					mWall[f]=new Wall(xf+.09f,yf,0f,xf+.1f,yf+.1f,0.11f,p1,p2,p3,p4);
					f++;
					}
				if (myMaze[x][y].WestUp) { 
					mWall[f]=new Wall(xf,yf,0f,xf+0.01f,yf+.1f,0.11f,p1,p2,p3,p4);
					f++;
					}
				}
	}
	
	
    public MazeRenderer(angelVdevilActivity context) {
    	parent=context;
    	mContext = context;
        copyMaze();
        glDevil=new ModelObj(context,"devil.obj",8f,0.5f,0f,0f,0f,0f);
        glHalo=new ModelObj(context,"halo.obj",8f,1f,1f,0f,0f,0f);
        glWings=new ModelObj(context,"wings.obj",8f,1f,1f,0f,0f,0f);
        glRobe=new ModelObj(context,"robe.obj",8f,1f,0f,1f,0f,0f);
        glEyes=new ModelObj(context,"eyes.obj",8f,1f,1f,1f,0f,0f);
        glYEyes=new ModelObj(context,"eyes.obj",8f,1f,1f,0f,0f,0f);
        glHead=new ModelObj(context,"head.obj",8f,1f,0.8f,0.8f,0f,0f);
        key=new Key();
        hammer=new Hammer();
        door=new Door();
     }

    private int countWalls() {
		int x,y;
		int t;
		maze_height=parent.maze_height;
		maze_width=parent.maze_width;
		t=0;
		for (y=0;y<maze_height;y++)
			for (x=0;x<maze_width;x++)
				{
				if (myMaze[x][y].NorthDown) t++;
				if (myMaze[x][y].SouthDown) t++;	
				if (myMaze[x][y].EastDown) t++;
				if (myMaze[x][y].WestDown) t++;
				if (myMaze[x][y].NorthUp) t++;
				if (myMaze[x][y].SouthUp) t++;	
				if (myMaze[x][y].EastUp) t++;
				if (myMaze[x][y].WestUp) t++;
				}
		return t;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glDisable(GL_DITHER);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT,GL_FASTEST);
        glClearColor(0f, 0.85f, 0.85f, 0f);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        int[] textures = new int[2];
        glGenTextures(2, textures, 0);
        
        mTextureIDfloor = textures[0];
        mTextureIDwall = textures[1];
        
        glBindTexture(GL_TEXTURE_2D, mTextureIDfloor);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,GL_REPLACE);
        glBindTexture(GL_TEXTURE_2D, mTextureIDwall);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        
        InputStream is1 = mContext.getResources().openRawResource(R.drawable.floor);
        InputStream is2 = mContext.getResources().openRawResource(R.drawable.wall);
        
        Bitmap bitmap1,bitmap2;
        
        try {
            bitmap1 = BitmapFactory.decodeStream(is1);
            bitmap2 = BitmapFactory.decodeStream(is2);
        } finally {
            try {
                is1.close();
                is2.close();
            } catch(IOException e) {
                // Ignore.
            }
        }
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap1, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap2, 0);
        bitmap1.recycle();
        bitmap2.recycle();
    }

    public void onDrawFrame(GL10 gl) {
        glDisable(GL_DITHER);
        glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,GL_MODULATE);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureIDfloor);
        glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        glBindTexture(GL_TEXTURE_2D, mTextureIDwall);
        glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        float angelx=parent.angel.x;
        float angely=parent.angel.y;
        float angelz=parent.angel.z;
        float devilx=parent.devil.x;
        float devily=parent.devil.y;
        float devilz=parent.devil.z;
        
        gl.glPushMatrix();
        
        set_glplain_mode(gl); // commenting out this creates randomly a nice gold letters effect
        
        
        gl.glScalef(0.1f, 0.1f, 0.1f);
        gl.glTranslatef(-4f, 0, -20f);
        gl.glRotatef(90f,0,0,1);
        gl.glRotatef(1f,1f,0,0);
        gl.glDisable(GL_CULL_FACE);
        
        gl.glTranslatef(2f, 0, 0f);
        
        Letter mL=new Letter(parent.score);
        Letter mT=new Letter((int) (parent.timeleft/1000));
        Letter mLvl=new Letter(parent.level,parent);
        
        gl.glColor4f(0,.8f,1,1);
        Letter.drawtriangle(gl, 0, 0, 0.1f, 2, 7f, 0.1f, 0, 7.5f, 0.1f);
        Letter.drawtriangle(gl, 0, 0, 0.1f, 2, 7f, 0.1f, 2, 0, 0.1f);
        
        mL.draw(gl);
        Letter.line_width=1;
        gl.glColor4f(0,0,1,1);
        Letter.drawtriangle(gl, 0, 8, 0.1f, 1, 9, 0.1f, 2, 8, 0.1f);
        Letter.drawtriangle(gl, 0, 8, 0.1f, 1, 7, 0.1f, 2, 8, 0.1f);
        
        gl.glColor4f(1,1,0,1);
        mL.moveto(7.75f, 1);
        mL.draw_star(gl);
        glTranslatef(3,0,0);
        
        
        gl.glColor4f(0,.8f,1,1);
        gl.glColor4f(0,0,1,1);
        Letter.drawtriangle(gl, 0, 0, 0.1f, 2, 7f, 0.1f, 0, 7.5f, 0.1f);
        Letter.drawtriangle(gl, 0, 0, 0.1f, 2, 7f, 0.1f, 2, 0, 0.1f);
        
        mT.draw(gl);
        Letter.line_width=1;
        gl.glColor4f(0,.8f,1,1);
        Letter.drawtriangle(gl, 0, 8, 0.1f, 1, 9, 0.1f, 2, 8, 0.1f);
        Letter.drawtriangle(gl, 0, 8, 0.1f, 1, 7, 0.1f, 2, 8, 0.1f);
        gl.glColor4f(1,0,1,1);
        mT.moveto(8, 1);
        mT.draw_clock(gl);
        
        glTranslatef(-6,0,0);
        gl.glColor4f(0,.8f,1,1);
        Letter.drawtriangle(gl, 0, 0, 0.1f, 2, 10f, 0.1f, 0, 9.5f, 0.1f);
        Letter.drawtriangle(gl, 0, 0, 0.1f, 2, 10f, 0.1f, 2, 0, 0.1f);

        mLvl.moveto(3.5f, 0);
        mLvl.draw(gl,4);
        Letter.line_width=7;
        gl.glColor4f(0,0,1,1);
        
        mLvl.moveto(7.75f, 1);
        gl.glColor4f(1,0,0,0);
        mLvl.moveto(1.5f, 0);
        mLvl.drawtext(gl,"Lvl:".toCharArray());
        
        gl.glColor4f(1,1,1,1);
        Letter.line_width=1;
        gl.glPopMatrix();
        
        set_glcolor_mode(gl);
        set_glplain_mode(gl);
        if (parent.havehammer) {
        	hammer.x=hammer.y=hammer.z=0;
        	gl.glPushMatrix();
        	gl.glRotatef(180f, 1, 0, 0);
        	gl.glTranslatef(-0.3f, 0, 0);
        	hammer.draw(gl);
        	gl.glPopMatrix();
        }
        if (parent.havekey) {
        	key.x=key.y=key.z=0;
        	gl.glPushMatrix();
        	gl.glRotatef(180f, 1, 0, 0);
        	gl.glTranslatef(-0.3f, -0.2f, 0f);
        	key.draw(gl);
        	gl.glPopMatrix();
        }
        
        set_gl_texture_mode(gl); 
        
        glRotatef(35f,1f,0,0);
        if (parent.third_person) {
        	if (!parent.network || !parent.network_devil) glRotatef(180-glRobe.tangle,0,0,1); // use this to make it third person
        	else glRotatef(180-glDevil.tangle,0,0,1); 
        }
        
        float centerx,centery;
        centerx=angelx/10.0f;
        centery=angely/10.0f;
        if (parent.network && parent.network_devil) {
            centerx=devilx/10.0f;
            centery=devily/10.0f;
        }
        centerx/=(float) (parent.tile_width);
        centery/=(float) (parent.tile_height);
        glScalef(3.0f,3.0f,3.0f);
        if (parent.third_person) glScalef(2.0f,2.0f,2.0f);
        glTranslatef(centerx*2,centery*2,0.3f);
        
        glRotatef(180f,0,0,1f);
        glBindTexture(GL_TEXTURE_2D, mTextureIDwall);
        int f;
        gl.glColor4f(1,1,1,1f);
        for (f=0;f<ts;f++) mWall[f].draw(gl);
        glBindTexture(GL_TEXTURE_2D, mTextureIDfloor);
        for (f=0;f<maze_width*maze_height;f++) mFloor[f].draw(gl);
        
        set_glplain_mode(gl);
        int x,y;
        for (y=0;y<maze_height;y++)
        for (x=0;x<maze_width;x++)
        if (myMaze[x][y].item>0)  {
        	render_item(x,y,myMaze[x][y].item,gl);
        }
        {
        	float xf,yf,zf;
			xf=(angelx/(float)parent.tile_width)/10f;
			yf=(angely/(float) parent.tile_height)/10f;
			zf=0;
			if (angelz!=0) {
				int tile_x,tile_y;
				tile_x=(int) (angelx)/parent.tile_width;
				tile_y=(int) (angely)/parent.tile_height;
				if (tile_x<0) tile_x=0;
				if (tile_y<0) tile_y=0;
				if (tile_x>maze_width-1) tile_x=maze_width-1;
				if (tile_y>maze_height-1) tile_y=maze_height-1;
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.BRIDGE.i) zf=-0.1f;
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.SOUTH.i)
					zf=-0.1f/(float)(parent.tile_height)*(angely-(tile_y*parent.tile_height));
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.NORTH.i)
					zf=-0.1f/(float)(parent.tile_height)*(1-angely+(tile_y*parent.tile_height));	

				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.EAST.i)
					zf=-0.1f/(float)(parent.tile_width)*(angelx-(tile_x*parent.tile_width));
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.WEST.i)
					zf=-0.1f/(float)(parent.tile_width)*(1-angelx+(tile_x*parent.tile_width));
			}
			else {zf=0.05f;}
			glRobe.x_translate=xf+0.05f;
			glRobe.y_translate=yf+0.05f;
			glRobe.z_translate=zf;
			
			
			
				if (parent.havekey) {
			  key.x=xf+0.05f;
			  key.y=yf+0.05f;
			  key.z=zf;
			  gl.glPushMatrix();
			  gl.glTranslatef(2*key.x, 2*key.y, -0.01f+2*key.z);
			  
			  gl.glRotatef(glRobe.tangle,0,0,1);
			  gl.glRotatef(90f,1, 0, 0);
			  gl.glRotatef(-60f,0, 1, 0);
		 	 gl.glTranslatef(-2*key.x-0.1f, -2*key.y, -2*key.z);
		 	 
			  key.draw(gl);
			  gl.glPopMatrix();
}
			
			
			if (parent.havehammer) {
			  hammer.x=xf+0.05f;
			  hammer.y=yf+0.05f;
			  hammer.z=zf;
			  gl.glPushMatrix();
			  gl.glTranslatef(2*hammer.x, 2*hammer.y, -0.01f+2*hammer.z);
			  
			  gl.glRotatef(glRobe.tangle,0,0,1);
			  gl.glRotatef(90f,1, 0, 0);
			  gl.glRotatef(60f,0, 1, 0);
		 	 gl.glTranslatef(-2*hammer.x+0.1f, -2*hammer.y, -2*hammer.z);
		 	 
			  hammer.draw(gl);
			  gl.glPopMatrix();
}
			glRobe.setdirection(1+((4-parent.playerDir)%4));
			glHalo.x_translate=xf+0.05f;
			glHalo.y_translate=yf+0.05f;
			glHalo.z_translate=zf;
			glWings.x_translate=xf+0.05f;
			glWings.y_translate=yf+0.05f;
			glWings.z_translate=zf;
			glEyes.x_translate=xf+0.05f;
			glEyes.y_translate=yf+0.05f;
			glEyes.z_translate=zf;
			glEyes.setdirection(1+((4-parent.playerDir)%4));
			glWings.setdirection(1+((4-parent.playerDir)%4));
			glEyes.angle=glRobe.tangle;
			glWings.angle=glRobe.tangle;
			glEyes.sync_angle();
			glWings.sync_angle();
			glHead.x_translate=xf+0.05f;
			glHead.y_translate=yf+0.05f;
			glHead.z_translate=zf;
        }
        {
        	float xf,yf,zf;
			xf=(devilx/(float)parent.tile_width)/10f;
			yf=(devily/(float) parent.tile_height)/10f;
			zf=0;
			if (devilz!=0) {
				int tile_x,tile_y;
				tile_x=(int) (devilx)/parent.tile_width;
				tile_y=(int) (devily)/parent.tile_height;
				if (tile_x<0) tile_x=0;
				if (tile_y<0) tile_y=0;
				if (tile_x>maze_width-1) tile_x=maze_width-1;
				if (tile_y>maze_height-1) tile_y=maze_height-1;
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.BRIDGE.i) zf=-0.1f;
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.SOUTH.i)
					zf=-0.1f/(float)(parent.tile_height)*(devily-(tile_y*parent.tile_height));
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.NORTH.i)
					zf=-0.1f/(float)(parent.tile_height)*(1-devily+(tile_y*parent.tile_height));	

				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.EAST.i)
					zf=-0.1f/(float)(parent.tile_width)*(devilx-(tile_x*parent.tile_width));
				if (myMaze[tile_x][tile_y].floordirection==Tile.directions.WEST.i)
					zf=-0.1f/(float)(parent.tile_width)*(1-devilx+(tile_x*parent.tile_width));
			}
			else {zf=0.05f;}
			glDevil.x_translate=xf+0.05f;
			glDevil.y_translate=yf+0.05f;
			glDevil.z_translate=zf;
			
			
			
				if (parent.devildizzy>0) {
			  hammer.x=xf+0.05f;
			  hammer.y=yf+0.05f;
			  hammer.z=zf;
			  gl.glPushMatrix();
			  gl.glTranslatef(2*hammer.x, 2*hammer.y, -0.01f+2*hammer.z);
			  
			  gl.glRotatef(glDevil.tangle,0,0,1);
			  gl.glRotatef(90f,1, 0, 0);
			  gl.glRotatef(60f,0, 1, 0);
		 	 gl.glTranslatef(-2*hammer.x+0.1f, -2*hammer.y, -2*hammer.z);
		 	 
			  hammer.draw(gl);
			  gl.glPopMatrix();
}
			
			
        }
        glBindTexture(GL_TEXTURE_2D, mTextureIDfloor);
        
        
        set_glcolor_mode(gl);
        glRobe.draw(gl);
        glHalo.draw(gl);
        glWings.draw(gl);
        glEyes.draw(gl);
        glHead.draw(gl);
        glDevil.draw(gl);
        glDevil.setdirection(parent.devil_direction);
        glYEyes.x_translate=glDevil.x_translate;
        glYEyes.y_translate=glDevil.y_translate;
        glYEyes.z_translate=glDevil.z_translate;
        glYEyes.setdirection(parent.devil_direction);
        glYEyes.angle=glDevil.tangle;
        glYEyes.sync_angle();
        glYEyes.draw(gl);
        
        
    }
    void set_glcolor_mode(GL10 gl) {
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        }
    void set_glplain_mode(GL10 gl) {
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }

    void set_gl_texture_mode(GL10 gl) {
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        glViewport(0, 0, w, h);
        float ratio = (float) w / h;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustumf(-ratio, ratio, -1, 1, 3, 17); // is this the limit of view?
    }

    private Context mContext;
    private Wall mWall[];
    private Floor mFloor[];
    private int mTextureIDfloor,mTextureIDwall;

    static class Floor {
    	int color;
        public Floor(float x1,float y1,float z,float x2,float y2,float zw,int c,float p1,float p2,float p3,float p4) {
        	color=c;
            ByteBuffer vbb = ByteBuffer.allocateDirect(36 * 3 * 4);
            vbb.order(ByteOrder.nativeOrder());
            mFVertexBuffer = vbb.asFloatBuffer();

            ByteBuffer tbb = ByteBuffer.allocateDirect(36 * 2 * 4);
            tbb.order(ByteOrder.nativeOrder());
            mTexBuffer = tbb.asFloatBuffer();

            ByteBuffer ibb = ByteBuffer.allocateDirect(36 * 2);
            ibb.order(ByteOrder.nativeOrder());
            mIndexBuffer = ibb.asShortBuffer();

            float coords[] = {
                    x1, y1, z+p1,
                    x2, y1, z+p2,
                    x2, y2, z+p3,
                    x1, y2, z+p4,
                    x1, y1, z+zw+p1,
                    x2, y1, z+zw+p2,
                    x2, y2, z+zw+p3,
                    x1, y2, z+zw+p4,
            };
            for (int i = 0; i < VERTS; i++) {
                for(int j = 0; j < 3; j++) {
                    mFVertexBuffer.put(coords[i*3+j] * 2.0f);
                }
            }
            for (int i = 0; i < VERTS; i++) {
            	
            	mTexBuffer.put(coords[i*3] * 32f );
            	mTexBuffer.put(coords[i*3+1] * 32f );
            }

            byte indices[] = {
                    0, 4, 5,    0, 5, 1,
                    1, 5, 6,    1, 6, 2,
                    2, 6, 7,    2, 7, 3,
                    3, 7, 4,    3, 4, 0,
                    4, 7, 6,    4, 6, 5,
                    3, 0, 1,    3, 1, 2
            };
            for(int i = 0; i < 36 ; i++) {
                mIndexBuffer.put((short) indices[i]);
            }

            mFVertexBuffer.position(0);
            mTexBuffer.position(0);
            mIndexBuffer.position(0);
        }

        public void draw(GL10 gl) {
            glDisable(GL_CULL_FACE);
            glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
            glEnable(GL_TEXTURE_2D);
            glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color >> 0) & 0xFF;
            float red=(float)r/256f;
            float green=(float)g/256f;
            float blue=(float)b/256f;
            gl.glColor4f(red, green, blue, 1f);
            glDrawElements(GL_TRIANGLE_STRIP, 36, GL_UNSIGNED_SHORT, mIndexBuffer);
            gl.glColor4f(1f, 1f, 1f, 1f);
        }
        private final static int VERTS = 8;
        private FloatBuffer mFVertexBuffer;
        private FloatBuffer mTexBuffer;
        private ShortBuffer mIndexBuffer;
        
    }

    
    static class Wall {
        public Wall(float x1,float y1,float z,float x2,float y2,float zw,float p1,float p2,float p3,float p4) {
            ByteBuffer vbb = ByteBuffer.allocateDirect(36 * 3 * 4);
            vbb.order(ByteOrder.nativeOrder());
            mFVertexBuffer = vbb.asFloatBuffer();

            ByteBuffer tbb = ByteBuffer.allocateDirect(36 * 2 * 4);
            tbb.order(ByteOrder.nativeOrder());
            mTexBuffer = tbb.asFloatBuffer();

            ByteBuffer ibb = ByteBuffer.allocateDirect(36 * 2);
            ibb.order(ByteOrder.nativeOrder());
            mIndexBuffer = ibb.asShortBuffer();

            float coords[] = {
                    x1, y1, z+p1,
                    x2, y1, z+p2,
                    x2, y2, z+p3,
                    x1, y2, z+p4,
                    x1, y1, z+zw+p1,
                    x2, y1, z+zw+p2,
                    x2, y2, z+zw+p3,
                    x1, y2, z+zw+p4
            };
            for (int i = 0; i < VERTS; i++) {
                for(int j = 0; j < 3; j++) {
                    mFVertexBuffer.put(coords[i*3+j] * 2.0f);
                }
            }
            for (int i = 0; i < VERTS; i++) {
            	
            	mTexBuffer.put(coords[i*3] * 16f );
            	mTexBuffer.put(coords[i*3+2] * 16f );
            }

            byte indices[] = {
                    0, 4, 5,    0, 5, 1,
                    1, 5, 6,    1, 6, 2,
                    2, 6, 7,    2, 7, 3,
                    3, 7, 4,    3, 4, 0,
                    4, 7, 6,    4, 6, 5,
                    3, 0, 1,    3, 1, 2
            };
            for(int i = 0; i < 36 ; i++) {
                mIndexBuffer.put((short) indices[i]);
            }

            mFVertexBuffer.position(0);
            mTexBuffer.position(0);
            mIndexBuffer.position(0);
        }

        public void draw(GL10 gl) {
            glDisable(GL_CULL_FACE);
            glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
            glEnable(GL_TEXTURE_2D);
            glTexCoordPointer(2, GL_FLOAT, 0, mTexBuffer);
            glDrawElements(GL_TRIANGLE_STRIP, 36, GL_UNSIGNED_SHORT, mIndexBuffer);
        }
        private final static int VERTS = 8;
        private FloatBuffer mFVertexBuffer;
        private FloatBuffer mTexBuffer;
        private ShortBuffer mIndexBuffer;
        
    }


	static class Star {
		float x,y,z;
	    public Star(float x1,float y1,float z1,float x2,float y2,float z2) {
	    	x=2*(x2+x1)/2;
	    	y=2*(y2+y1)/2;
	    	z=2*(z2+z1)/2;

	        ByteBuffer vbb = ByteBuffer.allocateDirect(36 * 3 * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        mFVertexBuffer = vbb.asFloatBuffer();
	
	        ByteBuffer tbb = ByteBuffer.allocateDirect(36 * 2 * 4);
	        tbb.order(ByteOrder.nativeOrder());
	        mTexBuffer = tbb.asFloatBuffer();
	
	        ByteBuffer ibb = ByteBuffer.allocateDirect(36 * 2);
	        ibb.order(ByteOrder.nativeOrder());
	        mIndexBuffer = ibb.asShortBuffer();
	        float xb=(x1+x2)/2;
	        float yb=(y1+y2)/2;
	        float zb=(z1+z2)/2;
	        
	        
	        long time = SystemClock.uptimeMillis() % 40000L;
	        angle = 0.09f * ((int) time);
	        
	        float coords[] = new float [30];
	        int p=0;
	        for (double a=0; a<Math.PI*2;a+=Math.PI*2/5) 
	        {
	                coords[p]  = (float) (xb + 0.01*Math.sin(a));
	                coords[p+1]= (float) (yb );
	                coords[p+2]= (float) (zb+ 0.01*Math.cos(a));
	                p+=3;
	                coords[p]  = (float) (xb + 0.03*Math.sin(a+Math.PI*2/10));
	                coords[p+1]= (float) (yb );
	                coords[p+2]= (float) (zb+ 0.03*Math.cos(a+Math.PI*2/10));
	                p+=3;
	        };
	        for (int i = 0; i < VERTS; i++) {
	            for(int j = 0; j < 3; j++) {
	                mFVertexBuffer.put(coords[i*3+j] * 2.0f);
	            }
	        }
	
	        for(int i = 0; i < 5 ; i++)
	        	for(int j = 0; j < 3; j++){
	            mIndexBuffer.put((short) (i*2+j));
	        }
	        mIndexBuffer.put(14,(short) 0);
	
	        mFVertexBuffer.position(0);
	        mTexBuffer.position(0);
	        mIndexBuffer.position(0);
	    }
	
	    public void draw(GL10 gl) {
	        glDisable(GL_CULL_FACE);
	        glVertexPointer(3, GL_FLOAT, 0, mFVertexBuffer);
	        glDisable(GL_TEXTURE_2D);
	        gl.glColor4f(1f,1f,0f,1f);
	        gl.glPushMatrix();
	        gl.glTranslatef(x, y, z);
	        long time = SystemClock.uptimeMillis() % 40000L;
	        angle = 0.09f * ((int) time);
	        gl.glRotatef(angle, 0, 0, 1f);
	        gl.glTranslatef(-x, -y, -z);
	        glDrawElements(GL_TRIANGLES, 15, GL_UNSIGNED_SHORT, mIndexBuffer);
	        gl.glColor4f(1f,1f,1f,1f);
	        gl.glPopMatrix();
	    }
	    private final static int VERTS = 10;
	    private FloatBuffer mFVertexBuffer;
	    private FloatBuffer mTexBuffer;
	    private ShortBuffer mIndexBuffer;
	    float angle;
	}

}
