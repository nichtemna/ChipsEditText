package com.example.chips_my2;

import java.io.Serializable;
import java.util.ArrayList;

public class Friend implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean facebook;
	private String name;
	private ArrayList<String> emails;
	private boolean checked = false;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Friend() {
		super();
	}

	public Friend(String name, String email, boolean facebook) {
		super();
		this.name = name;
		this.emails = new ArrayList<String>();
		this.emails.add(email);
		this.facebook = facebook;
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

	public ArrayList<String> getEmail() {
		return emails;
	}

	public void setEmail(ArrayList<String> emails) {
		this.emails = emails;
	}

	public void addEmail(String email) {
		this.emails.add(email);
	}

	public boolean isFacebook() {
		return facebook;
	}

	public void setFacebook(boolean facebook) {
		this.facebook = facebook;
	}
}
