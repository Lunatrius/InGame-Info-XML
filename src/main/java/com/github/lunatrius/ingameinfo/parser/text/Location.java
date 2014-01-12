package com.github.lunatrius.ingameinfo.parser.text;

import java.util.Locale;

public class Location {
	private int row;
	private int column;

	public Location(int row, int column) {
		this.row = row;
		this.column = column;
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "(%d,%d)", this.row, this.column);
	}

	@Override
	protected Location clone() {
		return new Location(this.row, this.column);
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return this.row;
	}

	public int getColumn() {
		return this.column;
	}
}
