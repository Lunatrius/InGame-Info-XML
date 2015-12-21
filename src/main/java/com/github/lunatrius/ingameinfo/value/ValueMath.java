package com.github.lunatrius.ingameinfo.value;

import com.github.lunatrius.ingameinfo.value.registry.ValueRegistry;

import java.util.Locale;

public abstract class ValueMath extends ValueComplex {
    @Override
    public boolean isValidSize() {
        return this.values.size() == 2;
    }

    public static class ValueAdd extends ValueMath {
        @Override
        public String getValue() {
            try {
                final int arg0 = getIntValue(0);
                final int arg1 = getIntValue(1);
                return String.valueOf(arg0 + arg1);
            } catch (final Exception e1) {
                try {
                    final double arg0 = getDoubleValue(0);
                    final double arg1 = getDoubleValue(1);
                    return String.valueOf(arg0 + arg1);
                } catch (final Exception e2) {
                    return "0";
                }
            }
        }
    }

    public static class ValueSub extends ValueMath {
        @Override
        public String getValue() {
            try {
                final int arg0 = getIntValue(0);
                final int arg1 = getIntValue(1);
                return String.valueOf(arg0 - arg1);
            } catch (final Exception e1) {
                try {
                    final double arg0 = getDoubleValue(0);
                    final double arg1 = getDoubleValue(1);
                    return String.valueOf(arg0 - arg1);
                } catch (final Exception e2) {
                    return "0";
                }
            }
        }
    }

    public static class ValueMul extends ValueMath {
        @Override
        public String getValue() {
            try {
                final int arg0 = getIntValue(0);
                final int arg1 = getIntValue(1);
                return String.valueOf(arg0 * arg1);
            } catch (final Exception e1) {
                try {
                    final double arg0 = getDoubleValue(0);
                    final double arg1 = getDoubleValue(1);
                    return String.valueOf(arg0 * arg1);
                } catch (final Exception e2) {
                    return "0";
                }
            }
        }
    }

    public static class ValueDiv extends ValueMath {
        @Override
        public String getValue() {
            try {
                final double arg0 = getDoubleValue(0);
                final double arg1 = getDoubleValue(1);
                return String.valueOf(arg0 / arg1);
            } catch (final Exception e) {
                return "0";
            }
        }
    }

    public static class ValueRound extends ValueMath {
        @Override
        public String getValue() {
            try {
                final double arg0 = getDoubleValue(0);
                final int arg1 = getIntValue(1);
                final double dec = Math.pow(10, arg1);
                if (arg1 > 0) {
                    return String.format(Locale.ENGLISH, "%." + arg1 + "f", arg0);
                }
                return String.valueOf((int) (Math.round(arg0 * dec) / dec));
            } catch (final Exception e2) {
                return "0";
            }
        }
    }

    public static class ValueMod extends ValueMath {
        @Override
        public String getValue() {
            try {
                final double arg0 = getDoubleValue(0);
                final double arg1 = getDoubleValue(1);
                return String.valueOf(Math.round((arg0 % arg1) * 10e6) / 10e6);
            } catch (final Exception e2) {
                return "0";
            }
        }
    }

    public static class ValueModi extends ValueMath {
        @Override
        public String getValue() {
            try {
                final int arg0 = getIntValue(0);
                final int arg1 = getIntValue(1);
                return String.valueOf(arg0 % arg1);
            } catch (final Exception e2) {
                return "0";
            }
        }
    }

    public static class ValuePercent extends ValueMath {
        @Override
        public String getValue() {
            try {
                final double arg0 = getDoubleValue(0);
                final double arg1 = getDoubleValue(1);
                return String.valueOf(arg0 / arg1 * 100);
            } catch (final Exception e) {
                return "0";
            }
        }
    }

    public static void register() {
        ValueRegistry.INSTANCE.register(new ValueAdd().setName("add"));
        ValueRegistry.INSTANCE.register(new ValueSub().setName("sub"));
        ValueRegistry.INSTANCE.register(new ValueMul().setName("mul"));
        ValueRegistry.INSTANCE.register(new ValueDiv().setName("div"));
        ValueRegistry.INSTANCE.register(new ValueRound().setName("round"));
        ValueRegistry.INSTANCE.register(new ValueMod().setName("mod").setAliases("modulo"));
        ValueRegistry.INSTANCE.register(new ValueModi().setName("modi").setAliases("modint", "moduloi", "moduloint"));
        ValueRegistry.INSTANCE.register(new ValuePercent().setName("pct").setAliases("percent", "percentage"));
    }
}
