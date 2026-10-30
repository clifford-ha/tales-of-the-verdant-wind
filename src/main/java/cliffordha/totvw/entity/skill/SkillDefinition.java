package cliffordha.totvw.entity.skill;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public record SkillDefinition(
        AttachmentType<Integer> cooldown,
        AttachmentType<Integer> notifier,
        String skillName
){}
