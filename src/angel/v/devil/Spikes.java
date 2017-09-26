package angel.v.devil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

class Spikes
{
	float x,y,z;
	
	public Spikes() {
		createRhombusStar(0,0,0);
	}
	
    public void createRhombusStar(float x,float y,float z)
    {
        int one = 0x10000;
        this.x=x;
        this.y=y;
        this.z=z;
        int vertices[] = {
        		  0  ,  0 , one ,
        		 one ,  0 ,  0 ,
        		  0  , one, 0 ,
        		-one ,  0 ,  0 ,
        		  0  ,-one,  0 ,
        		  0  ,  0 ,-one
        };
        

        int colors[] = {
                one,    0,    0,  one,
                one,  one,    0,  one,
                0,  one,    0,  one,
                0,    0,  one,  one,
                one,    0,  one,  one,
                one,  one,  one,  one,
        };

        byte indices[] = {
                0, 1, 2,    0, 2, 3,
                0, 3, 4,    0, 4, 1,
                5, 1, 2,    5, 2, 3,
                5, 3, 4,    5, 4, 1,
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asIntBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asIntBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }
    void drawstar4(GL10 gl) {
        gl.glColor4f(1,0,0,1);
        gl.glPushMatrix();
        gl.glScalef(1f,0.25f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(1,1,0,1);
        gl.glPushMatrix();
        gl.glScalef(0.25f,1f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(0,1,0,1);
        gl.glPushMatrix();
        gl.glScalef(0.25f,0.25f,1f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(0,1,1,1);
        gl.glPushMatrix();
        gl.glRotatef(45f,0,0,1);
        gl.glScalef(1f, 0.25f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(0,0,1,1);
        gl.glPushMatrix();
        gl.glRotatef(-45f,0,0,1);
        gl.glScalef(1f, 0.25f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(1,0,1,1);
        gl.glPushMatrix();
        gl.glRotatef(45f,0,1,0);
        gl.glScalef(0.25f, 0.25f,1f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(1,0,0,1);
        gl.glPushMatrix();
        gl.glRotatef(-45f,0,1,0);
        gl.glScalef(0.25f, 0.25f,1f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(1,1,0,1);
        gl.glPushMatrix();
        gl.glRotatef(45f,1,0,0);
        gl.glScalef(0.25f,1f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glColor4f(1,0,1,1);
        gl.glPushMatrix();
        gl.glRotatef(-45f,1,0,0);
        gl.glScalef(0.25f,1f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();

    
    }

    void drawstar3(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(1f,0.25f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glScalef(0.25f,1f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glScalef(0.25f,0.25f,1f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(60f,1,1,1);
        gl.glScalef(1f, 0.25f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(60f,1,1,1);
        gl.glScalef(0.25f,1f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(60f,1,1,1);
        gl.glScalef(0.25f,0.25f,1f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
    }
 
    void drawstar2(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(1f, 0.25f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
    }
    
    void drawstar1(GL10 gl) {
        gl.glPushMatrix();
        gl.glScalef(1f, 0.25f,0.25f);
        gl.glRotatef(80f,1,1,1);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glScalef(0.25f, 1f,0.25f);
        gl.glRotatef(80f,1,1,1);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glScalef(0.25f, 0.25f,1f);
        gl.glRotatef(80f,1,1,1);
        gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
    }
    
    
    public void draw(GL10 gl)
    {
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        gl.glPushMatrix();
        gl.glTranslatef(x*2, y*2, z*2);
        gl.glRotatef(angle,0,1,0);
        gl.glScalef(0.1f,0.1f,0.1f);
        drawstar4(gl);
        gl.glPopMatrix();

    }

    float angle=0;
    private IntBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ByteBuffer  mIndexBuffer;
}
