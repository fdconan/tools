package jm.tools.minijson;

public final class JSONNull {
	private static JSONNull instance = new JSONNull();

	public static JSONNull getInstance() {
		return instance;
	}

	public boolean equals(Object object) {
		return (object == null) || (object == this);
	}

	public int hashCode() {
		return 42 + "null".hashCode();
	}

	public String toString() {
		return "null";
	}
}
