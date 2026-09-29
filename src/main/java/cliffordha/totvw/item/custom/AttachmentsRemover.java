package cliffordha.totvw.item.custom;

import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.util.VWUtil;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.List;

public class AttachmentsRemover extends Item {
    public AttachmentsRemover(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        List<AttachmentType<?>> PLAYER_ATTACHMENTS = VWAttachments.PLAYER_ATTACHMENTS;

        int count = 0;
        for (AttachmentType<?> attachment : PLAYER_ATTACHMENTS) {
            if (player.hasAttached(attachment)) {
                player.removeAttached(attachment);
                count++;
            }
        }
        String empty = "No attachments were removed";
        String one = "1 attachment was removed";
        String many = count + " attachments were removed";
        VWUtil.sendToChat(player, true, count > 0 ? (count == 1 ? one: many) : empty);

        return super.use(level, player, hand);
    }
}
