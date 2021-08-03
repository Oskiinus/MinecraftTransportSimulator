package minecrafttransportsimulator.baseclasses;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**Basic Orientation class.  Stores an axis for direction, and rotation about that axis as a double.
 * Maintains a Quaternion of these, which allows it to be multiplied with other rotation objects to 
 * produce a final rotation.  Used to store the orientation of objects and
 * render them without the use of Euler Angles.  This allow for multiple axis of rotation to be
 * applied and prevents odd bugs with interpolation due to sign differences.  Note that if
 * this rotation object is to maintain a rotation matrix in the {@link #buffer} variable,
 * then usedInRendering MUST be set in the constructor.  Otherwise said buffer will be null.
 * This is done to prevent the need to update the buffer every update of the rotation if we won't
 * be using it.
 *
 * @author don_bruce
 */
public class Orientation3d{
	public final Point3d axis;
	public double rotation;
	public final FloatBuffer buffer;
	private double x;
	private double y;
	private double z;
	private double w;
	
	public Orientation3d(){
		this(new Point3d(0, 0, 1), 0, false);
	}
	
	public Orientation3d(Point3d axis, double rotation, boolean usedInRendering){
		this.axis = axis;
		this.rotation = rotation;
		this.buffer = usedInRendering ? BufferUtils.createFloatBuffer(16) : null;
		updateQuaternion();
	}
	
	public Orientation3d(double rotX, double rotY, double rotZ){
		// Convert from Euler Angles void Quaternion::FromEuler(float pitch, float yaw, float roll) { // Basically we create 3 Quaternions, one for pitch, one for yaw, one for roll // and multiply those together. // the calculation below does the same, just shorter

		float p = pitch * PIOVER180 / 2.0; float y = yaw * PIOVER180 / 2.0; float r = roll * PIOVER180 / 2.0;

		float sinp = sin(p); float siny = sin(y); float sinr = sin(r); float cosp = cos(p); float cosy = cos(y); float cosr = cos(r);

		x = sinr * cosp * cosy - cosr * sinp * siny; y = cosr * sinp * cosy + sinr * cosp * siny; z = cosr * cosp * siny - sinr * sinp * cosy; w = cosr * cosp * cosy + sinr * sinp * siny;

		normalise(); }
	}


	/**
	 * Updates the Axis and rotation.  This should be done any time the quaternion changes.
	 */
	public void updateAxisRotation(){
		Quaternion to axis-angle
		Given a quaternion {\displaystyle q=w+{\vec {v}}}, the (non-normalized) rotation axis is simply {\displaystyle {\vec {v}}}, provided that an axis exists. For very small rotations, {\displaystyle {\vec {v}}} gets close to the zero vector, so when we compute the normalized rotation axis, the calculation may blow up. In particular, the identity rotation has {\displaystyle {\vec {v}}=0}, so the rotation axis is undefined.
	
		To find the angle of rotation, note that {\displaystyle w=\cos(\theta /2)} and {\displaystyle \|v\|=\sin(\theta /2)}.
	
		// Convert to Axis/Angles void Quaternion::getAxisAngle(Vector3 *axis, float *angle) { float scale = sqrt(x * x + y * y + z * z); axis->x = x / scale; axis->y = y / scale; axis->z = z / scale; *angle = acos(w) * 2.0f; }
	}
	
	/**
	 * Updates the Quaternion.  This should be done any time the axis or rotation changes.
	 */
	public void updateQuaternion(){
		double rotationInRad = Math.toRadians(rotation);
		double sinRad = Math.sin(rotationInRad/2D);
		double cosRad = Math.cos(rotationInRad/2D);
		
		x = axis.x*sinRad;
		y = axis.y*sinRad;
		z = axis.z*sinRad;
		w = cosRad;
		
		if(buffer != null){
			buffer.clear();
	        float f = (float) (x * x);
	        float f1 = (float) (x * y);
	        float f2 = (float) (x * z);
	        float f3 = (float) (x * w);
	        float f4 = (float) (y * y);
	        float f5 = (float) (y * z);
	        float f6 = (float) (y * w);
	        float f7 = (float) (z * z);
	        float f8 = (float) (z * w);
	        buffer.put(1.0F - 2.0F * (f4 + f7));
	        buffer.put(2.0F * (f1 + f8));
	        buffer.put(2.0F * (f2 - f6));
	        buffer.put(0.0F);
	        buffer.put(2.0F * (f1 - f8));
	        buffer.put(1.0F - 2.0F * (f + f7));
	        buffer.put(2.0F * (f5 + f3));
	        buffer.put(0.0F);
	        buffer.put(2.0F * (f2 + f6));
	        buffer.put(2.0F * (f5 - f3));
	        buffer.put(1.0F - 2.0F * (f + f4));
	        buffer.put(0.0F);
	        buffer.put(0.0F);
	        buffer.put(0.0F);
	        buffer.put(0.0F);
	        buffer.put(1.0F);
	        buffer.rewind();
		}
	}
	
	/**
	 * Multiplies this orientation by the first passed-in orientation, storing the result in the
	 * second orientation.  Note that this may be the same object as the one to multiply by.
	 * Returns the storing object for nested operations.
	 */
	public Orientation3d rotateByOrientation(Orientation3d multiplyBy, Orientation3d storeIn){
		double mX = x * multiplyBy.w + w * multiplyBy.x + y * multiplyBy.z - z * multiplyBy.y;
		double mY = y * multiplyBy.w + w * multiplyBy.y + z * multiplyBy.x - x * multiplyBy.z;
		double mZ = z * multiplyBy.w + w * multiplyBy.z + x * multiplyBy.y - y * multiplyBy.x;
		double mW = w * multiplyBy.w - x * multiplyBy.x - y * multiplyBy.y - z * multiplyBy.z;
		storeIn.x = mX;
		storeIn.y = mY;
		storeIn.z = mZ;
		storeIn.w = mW;
		//FIXME make this calculate the axis and set it.
		return storeIn;
	}
	
	/**
	 * Rotates the passed-in point, storing it in the passed-in point object.
	 * Note that this may be the same object as the one to rotate.
	 * Returns the storing object for nested operations.
	 */
	public Point3d rotatePoint(Point3d rotate, Point3d storeIn){
		//First multiply the point by the quaternion.
		double mX = w * rotate.x + y * rotate.z - z * rotate.y;
		double mY = w * rotate.y + z * rotate.x - x * rotate.z;
		double mZ = w * rotate.z + x * rotate.y - y * rotate.x;
		double mW = x * rotate.x - y * rotate.y - z * rotate.z;
		
		//Next, multiply the result by the conjugate of the quaternion.
		//We don't need to calculate the W parameter here as it has to be 0.
		storeIn.x = mX * w + mW * -x + mY * -z - mZ * -y;
		storeIn.y = mY * w + mW * -y + mZ * -x - mX * -z;
		storeIn.z = mZ * w + mW * -z + mX * -y - mY * -x;
		return storeIn;
	}
	
	/**
	 * Short-hand method of {@link #rotatePoint(Point3d, Point3d)}
	 * that has the same point to rotate and store the rotation in.
	 */
	public Point3d rotatePoint(Point3d rotate){
		return rotatePoint(rotate, rotate);
	}
	
	/**
	 * Sets this orientation to the parameters of the passed-in orientation.
	 * Returns this object for nested operations.
	 */
	public Orientation3d setTo(Orientation3d other){
		this.axis.setTo(other.axis);
		this.rotation = other.rotation;
		updateQuaternion();
		return this;
	}
	
	/**
	 * Returns a copy of this rotation as a new object.
	 * This also copies the {@link #axis} object to prevent
	 * an inconsistent state.
	 */
	public Orientation3d copy(){
		return new Orientation3d(axis.copy(), rotation, buffer != null);
	}
}
