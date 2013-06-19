package com.example.chips_my2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Friend implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private HashMap<String, Boolean> emails;
	private boolean checked = false;

	public Friend() {
		super();
	}

	public Friend(String name, String email) {
		super();
		this.name = name;
		this.emails = new HashMap<String, Boolean>();
		this.emails.put(email, true);
	}

	public ArrayList<String> getArrayOfEmails() {
		ArrayList<String> array = new ArrayList<String>();
		for (String string : emails.keySet()) {
			array.add(string);
		}
		return array;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friend other = (Friend) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Boolean> getEmail() {
		return emails;
	}

	public void setEmail(HashMap<String, Boolean> emails) {
		this.emails = emails;
	}

	public void addEmail(String email, boolean chosen) {
		this.emails.put(email, chosen);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
