package cliffordha.totvw.registry.attachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class PlayerPrefs {
    private static final String prefix = "prefs_";
    private static final String benediction = prefix + "benediction_";

    public static final AttachmentType<Boolean> SHOW_ATROCITY_COUNTER = registerBool(prefix + "show_atrocity_counter", true);
    public static final AttachmentType<Boolean> ENABLE_NOTIFIERS = registerBool(prefix + "enable_notifiers", true);

    public static final AttachmentType<Integer> BENEDICTION_HEALTH_THRESHOLD = registerInt(benediction + "health_threshold", true);
    public static final AttachmentType<Boolean> BENEDICTION_SHARE_STACK = registerBool(benediction + "share_stack", true);
    public static final AttachmentType<Boolean> BENEDICTION_ALWAYS_TRIGGER_BLESSING = registerBool(benediction + "always_trigger_blessing", true);
    public static final AttachmentType<Boolean> BENEDICTION_TELEPORT_AFTER_SAVE = registerBool(benediction + "teleport_after_save", true);
    public static final AttachmentType<Integer> BENEDICTION_WOLF_TP_METHOD = registerInt(benediction + "wolf_tp_method", true);
    public static final AttachmentType<Integer> BENEDICTION_PLAYER_TP_METHOD = registerInt(benediction + "player_tp_method", true);
    public static final AttachmentType<Boolean> BENEDICTION_WOLF_TP_ALL = registerBool(benediction + "wolf_tp_all", true);
    public static final AttachmentType<Boolean> BENEDICTION_FORCE_TP = registerBool(benediction + "force_tp", true);
}
