package lunatrius.ingameinfo;

import java.util.ArrayList;
import java.util.List;

public class Value {
	public String type = "";
	public String value = "";
	public List<Value> values = new ArrayList<Value>();

	public Value(String type, String value) {
		this.type = type;
		this.value = value;
	}
}
