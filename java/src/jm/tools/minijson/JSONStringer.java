package jm.tools.minijson;

import java.io.StringWriter;

public class JSONStringer extends JSONBuilder {
	public JSONStringer() {
		super(new StringWriter());
	}

	public String toString() {
		return this.mode == 'd' ? this.writer.toString() : null;
	}
}
