package betterNote.patches;

import betterNote.BetterNote;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RequirementPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "isNoteForYourselfAvailable"
    )

    public static class ReqPatch {

        public static boolean Postfix(boolean __result) {
            if (BetterNote.ascLimit) {
                if (Settings.isDailyRun) {
                    return __result;
                } else if (AbstractDungeon.ascensionLevel < AbstractDungeon.player.getPrefs().getInteger("ASCENSION_LEVEL")) {
                    BetterNote.logger.info("Lifting Ascension limit for Note For Yourself");
                    return true;
                } else{
                    BetterNote.logger.info("Lifting Ascension limit for Note For Yourself");
                    return true;
                }
            }
            return __result;
        }
    }
}
