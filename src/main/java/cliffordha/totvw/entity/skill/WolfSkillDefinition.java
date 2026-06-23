package cliffordha.totvw.entity.skill;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public record WolfSkillDefinition(
        AttachmentType<Integer> cooldown,
        AttachmentType<Integer> notifier,
        int notifierColor,
        String skillName
){}
