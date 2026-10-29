package cliffordha.totvw.entity.skill;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;

public class PlayerSkills {
    public static final SkillDefinition VERDANT_BLESSING = new SkillDefinition(
            PlayerAttachment.CD_BLESSING_OF_THE_VERDANT_WIND,
            PlayerAttachment.NOTIFY_BLESSING_OF_THE_VERDANT_WIND,
            VWColors.VERDANT_WIND_MUTED,
            "§nVerdant Wind's Blessing§r"
    );
}
