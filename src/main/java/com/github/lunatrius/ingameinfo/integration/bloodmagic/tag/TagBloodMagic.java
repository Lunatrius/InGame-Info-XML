package com.github.lunatrius.ingameinfo.integration.bloodmagic.tag;

import WayofTime.alchemicalWizardry.api.spell.APISpellHelper;
import com.github.lunatrius.ingameinfo.tag.TagIntegration;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;

public abstract class TagBloodMagic extends TagIntegration {
    @Override
    public String getCategory() {
        return "bloodmagic";
    }

    public static class CurrentLP extends TagBloodMagic {
        @Override
        public String getValue() {
            try {
                return String.valueOf(APISpellHelper.getPlayerLPTag(player));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class MaximumLP extends TagBloodMagic {
        @Override
        public String getValue() {
            try {
                return String.valueOf(APISpellHelper.getPlayerMaxLPTag(player));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new CurrentLP().setName("bmlp").setAliases("bmcurrentlp"));
        TagRegistry.INSTANCE.register(new MaximumLP().setName("bmmaxlp").setAliases("bmmaximumlp"));
    }
}
