package pv.dotai.datai.replay;

import pv.dotai.datai.message.datast.DataTable;
import pv.dotai.datai.message.datast.Property;

public class Entity {

	private int index;
	private int classID;
	private int serial;
	private Property property;
	private Property classBaseline;
	private String className;
	private DataTable flatTable;
	
	public Entity(int index, int classID, int serial, pv.dotai.datai.message.datast.Property property, pv.dotai.datai.message.datast.Property classBaseline, String className, DataTable flatTable) {
		this.index = index;
		this.classID = classID;
		this.serial = serial;
		this.property = property;
		this.classBaseline = classBaseline;
		this.className = className;
		this.flatTable = flatTable;
	}

	public int getIndex() {
		return index;
	}

	public int getClassID() {
		return classID;
	}

	public int getSerial() {
		return serial;
	}

	public Property getProperty() {
		return property;
	}

	public Property getClassBaseline() {
		return classBaseline;
	}

	public String getClassName() {
		return className;
	}

	public DataTable getFlatTable() {
		return flatTable;
	}
	
}
