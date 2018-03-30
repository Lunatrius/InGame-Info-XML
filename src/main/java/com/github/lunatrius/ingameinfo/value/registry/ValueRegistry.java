package com.github.lunatrius.ingameinfo.value.registry;

import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.value.Value;
import com.github.lunatrius.ingameinfo.value.ValueComplex;
import com.github.lunatrius.ingameinfo.value.ValueLogic;
import com.github.lunatrius.ingameinfo.value.ValueMath;
import com.github.lunatrius.ingameinfo.value.ValueSimple;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValueRegistry {
    public static final ValueRegistry INSTANCE = new ValueRegistry();

    private Map<String, Value> stringValueMap = new HashMap<>();
    private Map<String, Class<? extends Value>> stringClassMap = new HashMap<>();
    private Map<Class<? extends Value>, String> classStringMap = new HashMap<>();

    private void register(final String name, final Value value, final boolean isAlias) {
        if (this.stringValueMap.containsKey(name)) {
            Reference.logger.error("Duplicate value key '" + name + "'!");
            return;
        }

        if (name == null) {
            Reference.logger.error("Value name cannot be null!");
            return;
        }

        final String nameLowerCase = name.toLowerCase(Locale.ENGLISH);
        this.stringValueMap.put(nameLowerCase, value);
        this.stringClassMap.put(nameLowerCase, value.getClass());
        if (!isAlias) {
            this.classStringMap.put(value.getClass(), nameLowerCase);
        }
    }

    public void register(final Value value) {
        register(value.getName(), value, false);

        for (final String name : value.getAliases()) {
            register(name, value, true);
        }
    }

    public Value forName(String name) {
        name = name.toLowerCase(Locale.ENGLISH);
        try {
            final Class<? extends Value> clazz = this.stringClassMap.get(name);
            if (clazz != null) {
                final Value value = clazz.newInstance();
                if (value != null) {
                    return value;
                }
            }
        } catch (final Exception e) {
            Reference.logger.error(String.format("Failed to create an instance for %s!", name), e);
            return new ValueSimple.ValueInvalid();
        }

        Reference.logger.error(String.format("Failed to create an instance for %s!", name));
        return new ValueSimple.ValueInvalid();
    }

    public String forClass(final Class<? extends Value> clazz) {
        final String str = this.classStringMap.get(clazz);
        return str != null ? str : "invalid";
    }

    public void init() {
        ValueComplex.register();
        ValueLogic.register();
        ValueMath.register();
        ValueSimple.register();
    }
}
