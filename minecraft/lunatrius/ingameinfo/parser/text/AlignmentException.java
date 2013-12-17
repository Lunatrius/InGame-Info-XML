package lunatrius.ingameinfo.parser.text;

public class AlignmentException extends Exception {
	private final String position;
	private final boolean valid;

	public AlignmentException(String position, boolean valid) {
		this.position = position;
		this.valid = valid;
	}

	public String getPosition() {
		return this.position;
	}

	public boolean isValid() {
		return this.valid;
	}
}
