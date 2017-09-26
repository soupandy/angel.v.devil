package angel.v.devil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.SystemClock;

public class ModelObj {

	 private List<String> verticesList;
	 private List<String> facesList;
	 private IntBuffer verticesBuffer;
	 private IntBuffer colorBuffer;
	 private ShortBuffer facesBuffer;
	 private int faces;
	 private int totalfaces;
	 float x_translate,y_translate,z_translate;
	 float red,green,blue;
	 float dvdr;
	 long lasttime = SystemClock.uptimeMillis();
	 int animspeed=5;
	 
	 class crossproduct {
		 float x,y,z;

		public crossproduct(float bx, float by, float bz,float cx, float cy, float cz) {
			x=by*cz-bz*cy;
			y=bz*cx-bx*cz;
			z=bx*cy-by*cx;
		}
		float size() {
			return (float) Math.sqrt(x*x+y*y+z*z);
		}
		float scalex() {
			return this.x/this.size();
		}
		float scaley() {
			return this.y/this.size();
		}
		float scalez() {
			return this.z/this.size();
		}

	 }
	 
	 void sync_angle() {
		 tangle=angle;
	 }
	 
	 
	 void setdirection(int d) {
		 if (d==1) angle=180;
		 if (d==2) angle=90;
		 if (d==3) angle=0;
		 if (d==4) angle=270;
	 }
	 void setgoing (float xg,float yg) {
		 if (xg>0) angle=270;
		 if (xg<0) angle=90;
		 if (yg>0) angle=0;
		 if (yg<0) angle=180;
	 }
	 
	 int countspaces(String s) {
		 String toks[] = s.split(" +");
		 int b=toks.length;
		 return b-1;
	 }
	 
	 public ModelObj(Context context,String file,float divider,float r,float g,float b,float xo,float yo) {
		 	x_translate=xo;
		 	y_translate=yo;
		 	z_translate=0f;
		 	dvdr=divider;
		 	red=r;
		 	green=g;
		 	blue=b;
	        verticesList = new ArrayList<String>();
	        facesList = new ArrayList<String>();
	        faces=0;
	        int one = 30000;
	        Scanner scanner;
			try {
				scanner = new Scanner(context.getAssets().open(file));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
	         totalfaces=0;
	        while(scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            if(line.startsWith("v ")) {
	                verticesList.add(line);
	            } else if(line.startsWith("f ")) {
	            	totalfaces+=countspaces(line);
	                facesList.add(line);
	            }
	        }
	        scanner.close();
	     // Create buffer for vertices
	        ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
	        buffer1.order(ByteOrder.nativeOrder());
	        verticesBuffer = buffer1.asIntBuffer();
		 // Create buffer for colors
	        ByteBuffer buffer3 = ByteBuffer.allocateDirect(verticesList.size() * 4 * 4);
	        buffer3.order(ByteOrder.nativeOrder());
	        colorBuffer = buffer3.asIntBuffer();
	     // Create buffer for faces
	        ByteBuffer buffer2 = ByteBuffer.allocateDirect(totalfaces *3* 2);
	        buffer2.order(ByteOrder.nativeOrder());
	        facesBuffer = buffer2.asShortBuffer();
	        for(String vertex: verticesList) {
	            String coords[] = vertex.split(" +"); // Split by space
	            float x = Float.parseFloat(coords[1]);
	            float y = Float.parseFloat(coords[2]);
	            float z = Float.parseFloat(coords[3]);
	            x=x*(float)one/divider;
	            y=y*(float)one/divider;
	            z=z*(float)one/divider;
	            verticesBuffer.put((int)x);
	            verticesBuffer.put((int)y);
	            verticesBuffer.put((int)z);
	            colorBuffer.put(one/2);
	            colorBuffer.put(one);
	            colorBuffer.put(one/4);
	            colorBuffer.put(0x10000);
	        }
	        verticesBuffer.position(0);
	        colorBuffer.position(0);
	        for(String face: facesList) {
	            String vertexIndices[] = face.split(" +");
	            short vertex1=1;
	            short vertex2=1; 
	            short vertex3=1; 
	            int c;
	            c=0;
	            for (String vi :vertexIndices) {
	            	if (c==1) {
	            		String ix[]=vi.split("/");
	            		vertex1=Short.parseShort(ix[0]);
	            	}
	            	if (c>1) {
	            		vertex2=vertex3;
	            		String ix[]=vi.split("/");
	            		vertex3=Short.parseShort(ix[0]);
	            	}
	            	if (c>2) {
	            		facesBuffer.put((short)(vertex1 - 1));
	    	            facesBuffer.put((short)(vertex2 - 1));
	    	            facesBuffer.put((short)(vertex3 - 1));
	    	            {
	    	            	float x1=(float) verticesBuffer.get(3*(vertex1-1));
	    	            	float y1=(float) verticesBuffer.get(1+3*(vertex1-1));
	    	            	float z1=(float) verticesBuffer.get(2+3*(vertex1-1));
	    	            	float x2=(float) verticesBuffer.get(3*(vertex2-1));
	    	            	float y2=(float) verticesBuffer.get(1+3*(vertex2-1));
	    	            	float z2=(float) verticesBuffer.get(2+3*(vertex2-1));
	    	            	float x3=(float) verticesBuffer.get(3*(vertex3-1));
	    	            	float y3=(float) verticesBuffer.get(1+3*(vertex3-1));
	    	            	float z3=(float) verticesBuffer.get(2+3*(vertex3-1));
	    	            	float xd1=x2-x1;
	    	            	float yd1=y2-y1;
	    	            	float zd1=z2-z1;
	    	            	float xd2=x3-x1;
	    	            	float yd2=y3-y1;
	    	            	float zd2=z3-z1;
	    	            	crossproduct cp=new crossproduct(xd1,yd1,zd1,xd2,yd2,zd2);
	    	            	colorBuffer.put(4*(vertex1-1),(int)(red*one)+(int)(red*one*cp.scaley()));
	    	            	colorBuffer.put(1+4*(vertex1-1),(int)(green*one)+(int)(green*one*cp.scaley()));
	    	            	colorBuffer.put(2+4*(vertex1-1),(int)(blue*one)+(int)(blue*one*cp.scaley()));
	    	            	colorBuffer.put(4*(vertex2-1),(int)(red*one)+(int)(red*one*cp.scaley()));
	    	            	colorBuffer.put(1+4*(vertex2-1),(int)(green*one)+(int)(green*one*cp.scaley()));
	    	            	colorBuffer.put(2+4*(vertex2-1),(int)(blue*one)+(int)(blue*one*cp.scaley()));
	    	            	colorBuffer.put(4*(vertex3-1),(int)(red*one)+(int)(red*one*cp.scaley()));
	    	            	colorBuffer.put(1+4*(vertex3-1),(int)(green*one)+(int)(green*one*cp.scaley()));
	    	            	colorBuffer.put(2+4*(vertex3-1),(int)(blue*one)+(int)(blue*one*cp.scaley()));
	    	            }
	    	            faces++;
	            	}
	            	c++;
	            }
	        }
	        facesBuffer.position(0);
	    }

	void animate_angle() {
		long time = SystemClock.uptimeMillis();
		long timepassed=time-lasttime;
		lasttime=time;
		timepassed=timepassed/10;
		if (timepassed>30) timepassed=30;
		if (timepassed<0) timepassed=0;
		timepassed+=5;
		animspeed=(int) timepassed;
		
		int t=0;
		int a1=(int) tangle;
		int a2=(int) angle;
		a1=(a1+360)%360;
		a2=(a2+360)%360;
		tangle=a1;
		angle=a2;
		int d=(360+a2-a1)%360;
		if (d>0 && d<180) t=1;
		if (d>=180) t=-1;
		if (t==1) {
			tangle+=animspeed;
			if (tangle>angle && (tangle-animspeed)<angle) tangle=angle;
		} 
		if (t==-1) {
			tangle-=animspeed;
			if (tangle<angle && (tangle+animspeed)>angle) tangle=angle;
		}
		if (t==0) tangle=angle;
	}
	 
	public void draw(GL10 gl)
	{
		long time = SystemClock.uptimeMillis() % 300L;
		float ad=(float)(time/100)-1f;
		ad=ad*10;
		gl.glPushMatrix();
		gl.glTranslatef(this.x_translate*2, this.y_translate*2, this.z_translate*2);
		gl.glRotatef(tangle+ad, 0f, 0f, 1f);
		gl.glRotatef(-90, 1f, 0f, 0f);
	    gl.glVertexPointer(3, GL10.GL_FIXED, 0, verticesBuffer);
	    gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
	    gl.glDrawElements(GL10.GL_TRIANGLES, faces*3, GL10.GL_UNSIGNED_SHORT, facesBuffer);
	    gl.glPopMatrix();
	    animate_angle();
	}
	float angle;
	float tangle=0;
}
