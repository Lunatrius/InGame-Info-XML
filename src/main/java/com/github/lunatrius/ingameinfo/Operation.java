package com.github.lunatrius.ingameinfo;

public enum Operation {
	ERROR,
	GT,
	LT,
	GE,
	LE,
	EQ,
	NE;

	public static Operation fromString(String str) {
		if (str.equalsIgnoreCase(String.valueOf(GT))) {
			return GT;
		} else if (str.equalsIgnoreCase(String.valueOf(LT))) {
			return LT;
		} else if (str.equalsIgnoreCase(String.valueOf(GE))) {
			return GE;
		} else if (str.equalsIgnoreCase(String.valueOf(LE))) {
			return LE;
		} else if (str.equalsIgnoreCase(String.valueOf(EQ))) {
			return EQ;
		} else if (str.equalsIgnoreCase(String.valueOf(NE))) {
			return NE;
		}
		return ERROR;
	}
}
