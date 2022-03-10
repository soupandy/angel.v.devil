package angel.v.devil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

class Key
{
	float x,y,z;
	
	public Key() {
		createKey(0,0,0);
	}
	
    public void createKey(float x,float y,float z)
    {
        int one = 0x10000;
        this.x=x;
        this.y=y;
        this.z=z;
        
        int vertices[]=new int[3*2*20];
        byte indices[] = new byte[20*2*3];
        int colors[] = {
                one,    0,    0,  one,
                one,  one,    0,  one,
                0,  one,    0,  one,
                0,    0,  one,  one,
                one,    0,  one,  one,
                one,  one,  one,  one,
        };
        int f;
        
        for (f=0;f<20;f++) {
        	vertices[f*6]=(f>12)?-one:-one/2;
        	vertices[f*6+1]=f*one/10-one;
        	vertices[f*6+2]=0;
        	vertices[f*6+3]=one/4+one/4*(((f+2)/2)%2);
        	if (f>12) vertices[f*6+3]=one;
        	vertices[f*6+4]=f*one/10-one;
        	vertices[f*6+5]=0;
        	indices[f*6]=(byte) (f*2);
        	indices[f*6+1]=(byte) (f*2+1);
        	indices[f*6+2]=(byte) (f*2+2);
        	indices[f*6+3]=(byte) (f*2+2);
        	indices[f*6+4]=(byte) (f*2+1);
        	indices[f*6+5]=(byte) (f*2+3);
        }
        
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
    void drawkey(GL10 gl) {
        gl.glColor4f(1,1,0,1);
        gl.glPushMatrix();
        gl.glScalef(0.25f,0.8f,0.25f);
        gl.glDrawElements(GL10.GL_TRIANGLES, 3*2*19, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glPopMatrix();
    }
   
    public void draw(GL10 gl)
    {
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
        //gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glPushMatrix();
        gl.glTranslatef(x*2, y*2, z*2);
        gl.glRotatef(angle,0,1,0);
        gl.glScalef(0.1f,0.1f,0.1f);
        drawkey(gl);
        gl.glPopMatrix();

    }

    float angle=0;
    private IntBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ByteBuffer  mIndexBuffer;
}
