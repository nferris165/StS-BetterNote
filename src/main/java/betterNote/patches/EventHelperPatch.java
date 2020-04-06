package betterNote.patches;

import betterNote.events.BetterNoteEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.helpers.EventHelper;

public class EventHelperPatch {
    @SpirePatch(
            clz = EventHelper.class,
            method = "getEvent"
    )

    public static class NoteSwapPatch {
        public static AbstractEvent Postfix(AbstractEvent __result, String key){

            if (__result instanceof NoteForYourself) {
                /*
                if(AbstractDungeon.actNum == 1){
                    return AbstractDungeon.getEvent(AbstractDungeon.eventRng);
                }
                */
                return new BetterNoteEvent();
            }
            return __result;
        }
    }
}
