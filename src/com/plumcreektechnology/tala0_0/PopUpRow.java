package com.plumcreektechnology.tala0_0;

/**
 * class for storing data about a aplace displayed in the proximity alert popup
 * an attempt to have a structure that would let us dynamically change the alert display
 * @author Devin Frenze
 * @author Nora Hayes
 *
 */
public class PopUpRow {
	private String id;
	private String name;
	
	public PopUpRow(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
