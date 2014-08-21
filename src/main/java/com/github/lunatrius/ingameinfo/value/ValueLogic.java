package com.github.lunatrius.ingameinfo.value;

import com.github.lunatrius.ingameinfo.value.registry.ValueRegistry;

public abstract class ValueLogic extends ValueComplex {
	@Override
	public boolean isValidSize() {
		return this.values.size() > 1;
	}

	public static class ValueIf extends ValueLogic {
		@Override
		public boolean isValidSize() {
			return this.values.size() == 2 || this.values.size() == 3;
		}

		@Override
		public String getValue() {
			try {
				if (getBooleanValue(0)) {
					return getValue(1);
				}
				if (this.values.size() > 2) {
					return getValue(2);
				}
				return "";
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueNot extends ValueLogic {
		@Override
		public boolean isValidSize() {
			return this.values.size() == 1;
		}

		@Override
		public String getValue() {
			try {
				return String.valueOf(!getBooleanValue(0));
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueAnd extends ValueLogic {
		@Override
		public String getValue() {
			try {
				for (Value operand : this.values) {
					if (!operand.getBooleanValue()) {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueOr extends ValueLogic {
		@Override
		public String getValue() {
			try {
				for (Value operand : this.values) {
					if (operand.getBooleanValue()) {
						return String.valueOf(true);
					}
				}
				return String.valueOf(false);
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueXor extends ValueLogic {
		@Override
		public String getValue() {
			try {
				boolean result = false;
				for (Value operand : this.values) {
					result = result ^ operand.getBooleanValue();
				}
				return String.valueOf(result);
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueGreater extends ValueLogic {
		@Override
		public String getValue() {
			try {
				double current = getDoubleValue(0);

				for (Value operand : this.values.subList(1, this.values.size())) {
					double next = operand.getDoubleValue();
					if (current > next) {
						current = next;
					} else {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueLesser extends ValueLogic {
		@Override
		public String getValue() {
			try {
				double current = getDoubleValue(0);

				for (Value operand : this.values.subList(1, this.values.size())) {
					double next = operand.getDoubleValue();
					if (current < next) {
						current = next;
					} else {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				return "?";
			}
		}
	}

	public static class ValueEqual extends ValueLogic {
		@Override
		public String getValue() {
			try {
				double current = getDoubleValue(0);

				for (Value operand : this.values.subList(1, this.values.size())) {
					double next = operand.getDoubleValue();
					if (current != next) {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			} catch (Exception e) {
				String current = replaceVariables(getValue(0));

				for (Value operand : this.values.subList(1, this.values.size())) {
					String next = replaceVariables(operand.getValue());
					if (!current.equals(next)) {
						return String.valueOf(false);
					}
				}
				return String.valueOf(true);
			}
		}
	}

	public static void register() {
		ValueRegistry.INSTANCE.register(new ValueIf().setName("if"));
		ValueRegistry.INSTANCE.register(new ValueNot().setName("not"));
		ValueRegistry.INSTANCE.register(new ValueAnd().setName("and"));
		ValueRegistry.INSTANCE.register(new ValueOr().setName("or"));
		ValueRegistry.INSTANCE.register(new ValueXor().setName("xor"));
		ValueRegistry.INSTANCE.register(new ValueGreater().setName("greater").setAliases("more"));
		ValueRegistry.INSTANCE.register(new ValueLesser().setName("lesser").setAliases("less"));
		ValueRegistry.INSTANCE.register(new ValueEqual().setName("equal").setAliases("equals"));
	}
}
