
package angel.v.devil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

class Door
{
	float x,y,z;
	float angle=0;
    public Door()
    {
        int one = 0x10000;
        int vertices[] = {
                -one, -one, -one,
                one, -one, -one,
                one,  one, -one,
                -one,  one, -one,
                -one, -one,  one,
                one, -one,  one,
                one,  one,  one,
                -one,  one,  one,
        };

        int colors[] = {
                one,  one/2,    0,  one,
                one,  one/2,    0,  one,
                one,  one/2,    0,  one,
                one,  one/2,    0,  one,
                one,  one/2,  0,  one,
                one,  one/2,  0,  one,
                one,  one/2,  0,  one,
                one,  one/2,  0,  one,
        };

        byte indices[] = {
                0, 4, 5,    0, 5, 1,
                1, 5, 6,    1, 6, 2,
                2, 6, 7,    2, 7, 3,
                3, 7, 4,    3, 4, 0,
                4, 7, 6,    4, 6, 5,
                3, 0, 1,    3, 1, 2
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

    void drawfield(GL10 gl) {
    	/*
    	gl.glPushMatrix();
        gl.glScalef(0.05f, 1f,1f);
        //gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, 18, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        */
        gl.glPushMatrix();
        gl.glRotatef(-45,0, 0, 1);
        gl.glScalef(1f,0.05f,1f);
        //gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
        
    	
    }
    
    public void draw(GL10 gl)
    {
        
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        gl.glPushMatrix();
        gl.glTranslatef(x*2, y*2, z*2);
        gl.glScalef(0.1f,0.1f,0.1f);
        gl.glRotatef(angle,0,0,1);
        drawfield(gl);
        gl.glPopMatrix();
    }

    private IntBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ByteBuffer  mIndexBuffer;
}
