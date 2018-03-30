package com.github.lunatrius.ingameinfo.value;

import com.github.lunatrius.ingameinfo.value.registry.ValueRegistry;

public abstract class ValueSimple extends Value {
    @Override
    public Value setRawValue(final String value, final boolean isText) {
        this.value = value.replaceAll("\\$(?=[0-9a-fk-or])", "\u00a7");
        if (isText) {
            this.value = this.value.replaceAll("\\\\([<>\\[/\\]\\\\])", "$1");
        }
        return this;
    }

    @Override
    public String getRawValue(final boolean isText) {
        String str = this.value.replace("\u00a7", "$");
        if (isText) {
            str = str.replaceAll("([<>\\[/\\]\\\\])", "\\\\$1");
        }
        return str;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public boolean isValidSize() {
        return this.values.size() == 0;
    }

    public static class ValueString extends ValueSimple {
        @Override
        public String getType() {
            if (this.value.matches("^-?\\d+(\\.\\d+)?$")) {
                return ValueRegistry.INSTANCE.forClass(ValueNumber.class);
            }
            return super.getType();
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }

    public static class ValueNumber extends ValueSimple {
        @Override
        public String getValue() {
            return this.value;
        }
    }

    public static class ValueVariable extends ValueSimple {
        @Override
        public String getValue() {
            return getVariableValue(this.value);
        }
    }

    public static class ValueInvalid extends ValueComplex {
        @Override
        public boolean isValidSize() {
            return true;
        }

        @Override
        public String getValue() {
            return "";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    }

    public static void register() {
        ValueRegistry.INSTANCE.register(new ValueString().setName("str").setAliases("string"));
        ValueRegistry.INSTANCE.register(new ValueNumber().setName("num").setAliases("number", "int", "integer", "float", "double"));
        ValueRegistry.INSTANCE.register(new ValueVariable().setName("var").setAliases("variable"));
        ValueRegistry.INSTANCE.register(new ValueInvalid().setName("invalid"));
    }
}
