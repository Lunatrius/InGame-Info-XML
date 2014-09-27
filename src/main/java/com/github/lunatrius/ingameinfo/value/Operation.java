package com.github.lunatrius.ingameinfo.value;

public enum Operation {
    INVALID {
        @Override
        public String getValue(Value value) {
            return "null";
        }
    },
    GT {
        @Override
        public String getValue(Value value) {
            int size = value.values.size();
            int operandCount = (size - 2) / 2;
            double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                double operand = value.getDoubleValue(i);
                if (base > operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    LT {
        @Override
        public String getValue(Value value) {
            int size = value.values.size();
            int operandCount = (size - 2) / 2;
            double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                double operand = value.getDoubleValue(i);
                if (base < operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    GE {
        @Override
        public String getValue(Value value) {
            int size = value.values.size();
            int operandCount = (size - 2) / 2;
            double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                double operand = value.getDoubleValue(i);
                if (base >= operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    LE {
        @Override
        public String getValue(Value value) {
            int size = value.values.size();
            int operandCount = (size - 2) / 2;
            double base = value.getDoubleValue(1);
            for (int i = 2; i < 2 + operandCount; i++) {
                double operand = value.getDoubleValue(i);
                if (base <= operand) {
                    return value.getValue(operandCount + i);
                }
            }
            return size % 2 == 0 ? "" : value.getValue(size - 1);
        }
    },
    EQ {
        @Override
        public String getValue(Value value) {
            int size = value.values.size();
            int operandCount = (size - 2) / 2;
            try {
                double base = value.getDoubleValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    double operand = value.getDoubleValue(i);
                    if (base == operand) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            } catch (NumberFormatException e) {
                String basestr = value.getValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    String operand = value.getValue(i);
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
        public String getValue(Value value) {
            int size = value.values.size();
            int operandCount = (size - 2) / 2;
            try {
                double base = value.getDoubleValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    double operand = value.getDoubleValue(i);
                    if (base != operand) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            } catch (NumberFormatException e) {
                String basestr = value.getValue(1);
                for (int i = 2; i < 2 + operandCount; i++) {
                    String operand = value.getValue(i);
                    if (!basestr.equals(operand)) {
                        return value.getValue(operandCount + i);
                    }
                }
                return size % 2 == 0 ? "" : value.getValue(size - 1);
            }
        }
    };

    public abstract String getValue(Value value);

    public static Operation fromString(String str) {
        for (Operation op : values()) {
            if (String.valueOf(op).equalsIgnoreCase(str)) {
                return op;
            }
        }

        return INVALID;
    }
}
