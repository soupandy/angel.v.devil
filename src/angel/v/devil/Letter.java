
package angel.v.devil;


import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES10.GL_LINES;
import static android.opengl.GLES10.GL_LINE_SMOOTH;
import static android.opengl.GLES10.glEnable;

import static android.opengl.GLES10.glVertexPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

class Letter
{
	int num;
	static int line_width=3;
	angelVdevilActivity activity;
    public Letter(int n)
    {
    	this.num=n;
    }
    public Letter(int n,angelVdevilActivity act)
    {
    	this.num=n;
    	activity=act;
    }

    
    static void drawtriangle(GL10 gl,float x1,float y1,float z1,float x2,float y2,float z2,float x3,float y3,float z3) {
    	ByteBuffer vbb = ByteBuffer.allocateDirect(3*3*4);
    	vbb.order(ByteOrder.nativeOrder());
    	FloatBuffer vertexBuffer = vbb.asFloatBuffer();
    	ByteBuffer indexBuffer = ByteBuffer.allocateDirect(6);
        float vertices[]={x1,y1,z1,x2,y2,z2,x3,y3,z3};
        byte indices[] = {0,1,2};
        vertexBuffer.put(vertices);
        indexBuffer.put(indices);
        vertexBuffer.position(0);
        indexBuffer.position(0);
        glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 3);
    }
    
    static void drawline(GL10 gl,float x1,float y1,float z1,float x2,float y2,float z2) {
    	ByteBuffer vbb = ByteBuffer.allocateDirect(3*3*4);
    	vbb.order(ByteOrder.nativeOrder());
    	FloatBuffer vertexBuffer = vbb.asFloatBuffer();
    	ByteBuffer indexBuffer = ByteBuffer.allocateDirect(6);
        float vertices[]={x1,y1,z1,x2,y2,z2};
        byte indices[] = {0,1,0};
        vertexBuffer.put(vertices);
        indexBuffer.put(indices);
        vertexBuffer.position(0);
        indexBuffer.position(0);
        
        gl.glLineWidth(line_width);
        glEnable(GL_LINE_SMOOTH);
        glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL_LINES, 0, 2);
    }
    
    void drawtext(GL10 gl,char[] text) {
    	int c;
    	for (c=0;c<text.length;c++)
    		drawchar(gl,text[c]);
    }
    
    void drawchar(GL10 gl,char letter) {
    	  Bitmap b=Bitmap.createBitmap(16,16,Config.ARGB_8888);
    	  Canvas c=new Canvas(b);
    	  Paint p=new Paint();
    	  p.setTextSize(16);
    	  p.setColor(Color.BLACK);
    	  p.setStyle(Style.FILL);
    	  c.drawRect(0, 0,12,12,p);
    	  p.setColor(Color.WHITE);
    	  p.setStyle(Style.FILL_AND_STROKE);
    	  c.drawText(Character.toString(letter),2, 14,p);
    	  
    	  int x,y;
    	  line_width=3;
    	  for (y=0;y<16;y++) {
    		  moverel(-1.6f,0.1f);
    		  for (x=0;x<16;x++) {
    			  moverel(0.1f,0);
    			  if (b.getPixel(x, 15-y)==Color.WHITE) {
    				 moveLine(gl,0.1f,0);
    				 moveLine(gl,0,0.1f);
    				 moveLine(gl,-0.1f,0);
    				 moveLine(gl,0,-0.1f);
    			  }
    		  }
    	  }
    	  b.recycle();
    	  moverel(0.8f,-1.6f);
    }
    
    void moveto(float x,float y) {
    	lastx=x;
    	lasty=y;
    }
    
    void moverel(float x,float y) {
    	lastx+=x;
    	lasty+=y;
    }
    
    
    void draw_clock(GL10 gl) {
    	float a=0;
    	int f;
    	float c; 
		float s; 
    	moveLine(gl,0.5f,0.5f);
		moverel(-0.5f,-0.5f);
		moveLine(gl,-0.5f,0.5f);
		moverel(0.5f,-0.5f);
    	for (f=0;f<12;f++) {
    		c=(float) Math.cos(a*Math.PI/180.0f)/2;
    		s=(float) Math.sin(a*Math.PI/180.0f)/2;
    		moverel(s,c);
    		moveLine(gl,s*0.5f,c*0.5f);
    		moverel(-1.5f*s,-1.5f*c);
    		a+=30;
    	}
    }

    void draw_star(GL10 gl) {
    	float a=0;
    	int f;
    	float c; 
		float s; 
		moverel(0,-0.75f);
    	for (f=0;f<10;f++) {
    		c=(float) Math.cos(a*Math.PI/180.0f)/2;
    		s=(float) Math.sin(a*Math.PI/180.0f)/2;
    		moveLine(gl,3*s,3*c);
    		a+=180-36;
    	}
    }
    
    void draw_one (GL10 gl) {
    	moveLine(gl,1,0);
    	moverel(-0.5f,0);
    	moveLine(gl,0,2);
    	moveLine(gl,-0.5f,-0.5f);
    	moverel(1.5f,-1.5f);
    }
    
    void draw_two(GL10 gl) {
    	moveLine(gl,1,0);
    	moverel(-1,0);
    	moveLine(gl,1,1.5f);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,-0.5f,-0.5f);
    	moverel(1.5f,-1.5f);
    }
    
    void draw_three(GL10 gl) {
    	moveLine(gl,0.5f,0);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,-0.5f,0);
    	moverel(1.5f,-2f);
    }
    
    void draw_four(GL10 gl) {
    	moverel(0.5f,0);
    	moveLine(gl,0,2f);
    	moveLine(gl,-0.5f,-1f);
    	moveLine(gl,1,0);
    	moverel(0.5f,-1);
    }

    void draw_five(GL10 gl) {
    	moveLine(gl,0.5f,0);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,-0.5f,0);
    	moveLine(gl,0,1);
    	moveLine(gl,1,0);
    	moverel(0.5f,-2f);
    }
    
    void draw_six(GL10 gl) {
    	moverel(0.5f,0);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,-0.5f,0);
    	moveLine(gl,0,0.5f);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,0.5f,0);
    	moverel(-1,-1);
    	moveLine(gl,0,-0.5f);
    	moveLine(gl,0.5f,-0.5f);
    	moverel(1,0);
    }

    void draw_seven(GL10 gl) {
    	moverel(0.5f,0);
    	moveLine(gl,0,1);
    	moveLine(gl,0.5f,1);
    	moveLine(gl,-1,0);
    	moverel(1.5f,-2f);
    }
    
    void draw_eight(GL10 gl) {
    	moverel(0.5f,0);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,-1f,1f);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,0.5f,-0.5f);
    	moveLine(gl,-1f,-1f);
    	moveLine(gl,0.5f,-0.5f);
    	moverel(1,0);
    }

    void draw_nine(GL10 gl) {
    	moveLine(gl,0.5f,0);
    	moveLine(gl,0.5f,1);
    	moveLine(gl,0,0.5f);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,-0.5f,-0.5f);
    	moveLine(gl,0.5f,-0.5f);
    	moveLine(gl,0.5f,0);
    	moverel(0.5f,-1);
    }

    void draw_zero(GL10 gl) {
    	moverel(0.5f,0);
    	moveLine(gl,0.5f,0.5f);
    	moveLine(gl,0,1);
    	moveLine(gl,-0.5f,0.5f);
    	moveLine(gl,-0.5f,-0.5f);
    	moveLine(gl,0,-1);
    	moveLine(gl,0.5f,-0.5f);
    	moverel(1,0);
    }
    
    void moveLine(GL10 gl,float x,float y) {
    	drawline(gl,lasty+y,lastx+x,0,lasty,lastx,0);
    	lastx+=x;
    	lasty+=y;
    }
    
    
    void draw_shapes(GL10 gl,int limit) {
    	int d;
    	int f=num;
    	int l;
    	if (num>99999) f=99999;
    	l=10000;
    	int s=7;
    	while (l>0 && s>0) {
    		d=f/l; if (s<limit+3) {
	    		if (d==0) draw_zero(gl);
	    		if (d==1) draw_one(gl);
	    		if (d==2) draw_two(gl);
	    		if (d==3) draw_three(gl);
	    		if (d==4) draw_four(gl);
	    		if (d==5) draw_five(gl);
	    		if (d==6) draw_six(gl);
	    		if (d==7) draw_seven(gl);
	    		if (d==8) draw_eight(gl);
	    		if (d==9) draw_nine(gl); 
    		}
    		f=f-d*l;
    		l=l/10;
    		s--;
    	}
    }

    public void draw(GL10 gl,int limit)
    {
    	float savex,savey;
    	savex=lastx;savey=lasty;
    	gl.glColor4f(1f,1f,1f,1f);
    	line_width=7;
    	draw_shapes(gl,limit);
    	gl.glColor4f(0.5f, 0f, .9f, 1f);
    	line_width=3;
    	lastx=savex;lasty=savey;
        draw_shapes(gl,limit);
    	gl.glColor4f(1f,1f,1f,1f);
    }

    
    public void draw(GL10 gl)
    {
    	gl.glColor4f(1f,1f,1f,1f);
    	line_width=7;
    	moveto(0,0);
    	draw_shapes(gl,5);
    	gl.glColor4f(0.5f, 0f, .9f, 1f);
    	line_width=3;
    	moveto(0,0);
    	draw_shapes(gl,5);
    	gl.glColor4f(1f,1f,1f,1f);
    }

    private float lastx,lasty;
}
