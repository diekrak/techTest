package com.prodigius.error;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorInfo {

	private String url;
	private String message;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
