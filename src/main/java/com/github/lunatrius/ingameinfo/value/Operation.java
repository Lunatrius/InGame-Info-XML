package com.github.lunatrius.ingameinfo.value;

public enum Operation {
    INVALID {
        @Override
        public String getValue(final Value value) {
            return "null";
        }
    },
    GT {
        @Override
        public String getValue(final Value value) {
            final int size = value.values.size();
            final int operandCount = (size - 2) / 2;
            final double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                final double operand = value.getDoubleValue(i);
                if (base > operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    LT {
        @Override
        public String getValue(final Value value) {
            final int size = value.values.size();
            final int operandCount = (size - 2) / 2;
            final double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                final double operand = value.getDoubleValue(i);
                if (base < operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    GE {
        @Override
        public String getValue(final Value value) {
            final int size = value.values.size();
            final int operandCount = (size - 2) / 2;
            final double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                final double operand = value.getDoubleValue(i);
                if (base >= operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    LE {
        @Override
        public String getValue(final Value value) {
            final int size = value.values.size();
            final int operandCount = (size - 2) / 2;
            final double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                final double operand = value.getDoubleValue(i);
                if (base <= operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    EQ {
        @Override
        public String getValue(final Value value) {
            final int size = value.values.size();
            final int operandCount = (size - 2) / 2;
            try {
                final double base = value.getDoubleValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    final double operand = value.getDoubleValue(i);
                    if (base == operand) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            } catch (final NumberFormatException e) {
                final String basestr = value.getValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    final String operand = value.getValue(i);
                    if (basestr.equals(operand)) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            }
        }
    },
    NE {
        @Override
        public String getValue(final Value value) {
            final int size = value.values.size();
            final int operandCount = (size - 2) / 2;
            try {
                final double base = value.getDoubleValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    final double operand = value.getDoubleValue(i);
                    if (base != operand) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            } catch (final NumberFormatException e) {
                final String basestr = value.getValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    final String operand = value.getValue(i);
                    if (!basestr.equals(operand)) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            }
        }
    };

    public abstract String getValue(Value value);

    public static Operation fromString(final String str) {
        for (final Operation op : values()) {
            if (String.valueOf(op).equalsIgnoreCase(str)) {
                return op;
            }
        }

        return INVALID;
    }
}
