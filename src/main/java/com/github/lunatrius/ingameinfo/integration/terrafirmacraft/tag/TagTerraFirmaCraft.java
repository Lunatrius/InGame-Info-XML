package com.github.lunatrius.ingameinfo.integration.terrafirmacraft.tag;

import com.bioxx.tfc.Core.Player.BodyTempStats;
import com.bioxx.tfc.Core.Player.SkillStats;
import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.api.SkillsManager;
import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.TagIntegration;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import net.minecraft.client.resources.I18n;

import java.util.Locale;

public abstract class TagTerraFirmaCraft extends TagIntegration {
    @Override
    public String getCategory() {
        return "terrafirmacraft";
    }

    public static class Rainfall extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf((int) TFC_Climate.getRainfall(world, playerPosition.x, playerPosition.y, playerPosition.z));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class Temperature extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.format(Locale.ENGLISH, "%.2f", TFC_Climate.getHeightAdjustedTemp(world, playerPosition.x, playerPosition.y, playerPosition.z));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class AverageTemperature extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.format(Locale.ENGLISH, "%.2f", TFC_Climate.getBioTemperatureHeight(world, playerPosition.x, playerPosition.y, playerPosition.z));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class TemperatureWithHeatSources extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.format(Locale.ENGLISH, "%.2f", TFC_Climate.getHeightAdjustedTemp(world, playerPosition.x, playerPosition.y, playerPosition.z) + BodyTempStats.applyTemperatureFromHeatSources(player));
            } catch (Throwable e) {
                log(this, e);
            }
            return "-1";
        }
    }

    public static class Season extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return TFC_Time.getSeason();
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }
    }

    public static class Date extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return TFC_Time.getDateStringFromHours((int) TFC_Time.getTotalHours());
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }
    }

    public static class Day extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(TFC_Time.getDayOfMonth());
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }
    }

    public static class Month extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(TFC_Time.getMonth());
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }
    }

    public static class Year extends TagTerraFirmaCraft {
        @Override
        public String getValue() {
            try {
                return String.valueOf(1000 + TFC_Time.getYear());
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }
    }

    public static class Skill extends TagTerraFirmaCraft {
        private final String skillName;

        public Skill(final String skillName) {
            this.skillName = skillName;
        }

        @Override
        public String getValue() {
            try {
                final SkillStats skillStats = TFC_Core.getSkillStats(player);
                return String.format(Locale.ENGLISH, "%s (%s), %.1f%%", I18n.format(this.skillName), skillStats.getSkillRank(this.skillName).getLocalizedName(), skillStats.getPercToNextRank(this.skillName) * 100);
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }

        @Override
        public String getLocalizedDescription() {
            return I18n.format(Reference.MODID.toLowerCase() + ".tag.fmtskill.desc", I18n.format(this.skillName));
        }
    }

    public static class SkillRank extends TagTerraFirmaCraft {
        private final String skillName;

        public SkillRank(final String skillName) {
            this.skillName = skillName;
        }

        @Override
        public String getValue() {
            try {
                return TFC_Core.getSkillStats(player).getSkillRank(this.skillName).getLocalizedName();
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }

        @Override
        public String getLocalizedDescription() {
            return I18n.format(Reference.MODID.toLowerCase() + ".tag.fmtskillrank.desc", I18n.format(this.skillName));
        }
    }

    public static class SkillProgress extends TagTerraFirmaCraft {
        private final String skillName;

        public SkillProgress(final String skillName) {
            this.skillName = skillName;
        }

        @Override
        public String getValue() {
            try {
                return String.format(Locale.ENGLISH, "%.1f", TFC_Core.getSkillStats(player).getPercToNextRank(this.skillName) * 100);
            } catch (Throwable e) {
                log(this, e);
            }
            return "";
        }

        @Override
        public String getLocalizedDescription() {
            return I18n.format(Reference.MODID.toLowerCase() + ".tag.fmtskillprogress.desc", I18n.format(this.skillName));
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new Rainfall().setName("tfcrainfall"));
        TagRegistry.INSTANCE.register(new Temperature().setName("tfctemperature"));
        TagRegistry.INSTANCE.register(new AverageTemperature().setName("tfcaveragetemperature"));
        TagRegistry.INSTANCE.register(new TemperatureWithHeatSources().setName("tfctemperatureheatsources"));
        TagRegistry.INSTANCE.register(new Season().setName("tfcseason"));
        TagRegistry.INSTANCE.register(new Date().setName("tfcdate"));
        TagRegistry.INSTANCE.register(new Day().setName("tfcday"));
        TagRegistry.INSTANCE.register(new Month().setName("tfcmonth"));
        TagRegistry.INSTANCE.register(new Year().setName("tfcyear"));

        for (SkillsManager.Skill skill : SkillsManager.instance.getSkillsArray()) {
            String skillName = skill.skillName.toLowerCase();
            if (skillName.startsWith("skill.")) {
                skillName = skillName.substring(6);
            }

            TagRegistry.INSTANCE.register(new Skill(skill.skillName).setName("tfcskill" + skillName));
            TagRegistry.INSTANCE.register(new SkillRank(skill.skillName).setName("tfcskillrank" + skillName));
            TagRegistry.INSTANCE.register(new SkillProgress(skill.skillName).setName("tfcskillprogress" + skillName));
        }
    }
}
