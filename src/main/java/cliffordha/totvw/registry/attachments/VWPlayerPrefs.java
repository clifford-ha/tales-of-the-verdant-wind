package cliffordha.totvw.registry.attachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.List;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class VWPlayerPrefs {
    private static final String prefix = "prefs";
    private static final String benediction = prefix + "_benediction";
    public static final AttachmentType<Boolean> SHOW_ATROCITY_COUNTER = registerBool(prefix, "show_atrocity_counter");
    public static final AttachmentType<Boolean> ENABLE_NOTIFIERS = registerBool( prefix, "enable_notifiers");

    public static final AttachmentType<Integer> BENEDICTION_HEALTH_THRESHOLD = registerInt(benediction, "health_threshold");
    public static final AttachmentType<Boolean> BENEDICTION_SHARE_STACK = registerBool(benediction, "share_stack");
    public static final AttachmentType<Boolean> BENEDICTION_ALWAYS_TRIGGER_BLESSING = registerBool(benediction, "always_trigger_blessing");
    public static final AttachmentType<Boolean> BENEDICTION_TELEPORT_AFTER_SAVE = registerBool(benediction, "teleport_after_save");
    public static final AttachmentType<Integer> BENEDICTION_WOLF_TP_METHOD = registerInt(benediction, "wolf_tp_method");
    public static final AttachmentType<Integer> BENEDICTION_PLAYER_TP_METHOD = registerInt(benediction, "player_tp_method");
    public static final AttachmentType<Boolean> BENEDICTION_WOLF_TP_ALL = registerBool(benediction, "wolf_tp_all");
    public static final AttachmentType<Boolean> BENEDICTION_FORCE_TP = registerBool(benediction, "force_tp");
    
    public static int register() {
        final List<AttachmentType<?>> ATTACHMENTS = List.of(
                SHOW_ATROCITY_COUNTER,
                ENABLE_NOTIFIERS,

                BENEDICTION_HEALTH_THRESHOLD,
                BENEDICTION_SHARE_STACK,
                BENEDICTION_ALWAYS_TRIGGER_BLESSING,
                BENEDICTION_TELEPORT_AFTER_SAVE,
                BENEDICTION_WOLF_TP_METHOD,
                BENEDICTION_PLAYER_TP_METHOD,
                BENEDICTION_WOLF_TP_ALL,
                BENEDICTION_FORCE_TP
        );
        return ATTACHMENTS.size();
    }
}
