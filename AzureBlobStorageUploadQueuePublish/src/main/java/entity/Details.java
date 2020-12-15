package entity;

import java.io.Serializable;
import java.net.URI;

public class Details implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private URI path;

	public Details() {
		super();
	}

	public Details(String id, URI path) {
		super();
		this.id = id;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public URI getPath() {
		return path;
	}

	public void setPath(URI path) {
		this.path = path;
	}
	
	

}
