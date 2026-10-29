package cliffordha.totvw.entity.skill;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;

public class WolfSkills {
    public static final SkillDefinition VERDANT_BLESSING =
            new SkillDefinition(
                    WolfAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
                    WolfAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
                    VWColors.VERDANT_WIND,
                    "§nVerdant Wind's Blessing§r"
            );

    public static final SkillDefinition PARALYZER =
            new SkillDefinition(
                    WolfAttachment.CD_BLOODLUST_SKILL_PARALYZE,
                    WolfAttachment.NOTIFY_BLOODLUST_SKILL_PARALYZE,
                    VWColors.BLOODLUST,
                    "§nBloodlust Skill: Paralyzer§r"
            );

    public static final SkillDefinition RUPTURE =
            new SkillDefinition(
                    WolfAttachment.CD_MIGHT_SKILL_RUPTURE,
                    WolfAttachment.NOTIFY_MIGHT_SKILL_RUPTURE,
                    VWColors.MIGHT,
                    "§nMight Skill: Rupture§r"
            );
}
