package com.github.lunatrius.ingameinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Value {
	public static enum ValueType {
		NONE,
		STR,
		NUM,
		VAR,
		IF(-1, 2, 3),
		NOT(-1, 1),
		AND(2),
		OR(2),
		XOR(2),
		GREATER(2),
		LESSER(2),
		EQUAL(2),
		PCT(-1, 2),
		CONCAT(2),
		OPERATION(2),
		MAX(-1, 2, 4),
		MIN(-1, 2, 4),
		ADD(-1, 2),
		SUB(-1, 2),
		MUL(-1, 2),
		DIV(-1, 2),
		ROUND(-1, 2),
		MOD(-1, 2),
		MODI(-1, 2),
		ITEMQUANTITY(-1, 1, 2),
		TRANS(1),
		ICON(-1, 1, 2, 5, 7, 11);

		private int min = -1;
		private Set<Integer> validSizes;

		ValueType() {}

		ValueType(int min, Integer... validSizes) {
			this.min = min;
			this.validSizes = new HashSet<Integer>(Arrays.asList(validSizes));
		}

		public boolean validSize(int size) {
			return this.validSizes == null || this.validSizes.contains(size) || this.validSizes.size() <= 0 && size >= this.min;
		}

		public static ValueType fromString(String str) {
			if (str.matches("(?i)(str|string)")) {
				return STR;
			} else if (str.matches("(?i)(num|number|int|integer|float|double)")) {
				return NUM;
			} else if (str.matches("(?i)(var|variable)")) {
				return VAR;
			} else if (str.matches("(?i)(if)")) {
				return IF;
			} else if (str.matches("(?i)(not)")) {
				return NOT;
			} else if (str.matches("(?i)(and)")) {
				return AND;
			} else if (str.matches("(?i)(or)")) {
				return OR;
			} else if (str.matches("(?i)(xor)")) {
				return XOR;
			} else if (str.matches("(?i)(greater)")) {
				return GREATER;
			} else if (str.matches("(?i)(less|lesser)")) {
				return LESSER;
			} else if (str.matches("(?i)(equals?)")) {
				return EQUAL;
			} else if (str.matches("(?i)(pct|percent|percentage)")) {
				return PCT;
			} else if (str.matches("(?i)(concat)")) {
				return CONCAT;
			} else if (str.matches("(?i)(op|operation)")) {
				return OPERATION;
			} else if (str.matches("(?i)(max|maximum)")) {
				return MAX;
			} else if (str.matches("(?i)(min|minimum)")) {
				return MIN;
			} else if (str.matches("(?i)(add)")) {
				return ADD;
			} else if (str.matches("(?i)(sub)")) {
				return SUB;
			} else if (str.matches("(?i)(mul)")) {
				return MUL;
			} else if (str.matches("(?i)(div)")) {
				return DIV;
			} else if (str.matches("(?i)(round)")) {
				return ROUND;
			} else if (str.matches("(?i)(mod|modulo)")) {
				return MOD;
			} else if (str.matches("(?i)(imod|intmod|imodulo|intmodulo|modi|modint|moduloi|moduloint)")) {
				return MODI;
			} else if (str.matches("(?i)(itemquantity)")) {
				return ITEMQUANTITY;
			} else if (str.matches("(?i)(trans|translate)")) {
				return TRANS;
			} else if (str.matches("(?i)(icon|img|image)")) {
				return ICON;
			}

			return NONE;
		}
	}

	public ValueType type = ValueType.NONE;
	public String value = "";
	public List<Value> values = new ArrayList<Value>();

	public Value(ValueType type, String value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%s] '%s'", this.type, this.value);
	}
}
