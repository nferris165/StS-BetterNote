package betterNote.patches;

import betterNote.events.BetterNoteEvent;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CardSavePatch {

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "nextRoomTransition",
            paramtypez = {
                    SaveFile.class
            }
    )

    public static class SavePatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractDungeon __instance, SaveFile saveFile) {
            if (AbstractDungeon.getCurrRoom() instanceof EventRoom && AbstractDungeon.getCurrRoom().event instanceof BetterNoteEvent) {
                AbstractCard tmpCard = ((BetterNoteEvent)AbstractDungeon.getCurrRoom().event).saveCard;

                if (tmpCard != null) {
                    if(((BetterNoteEvent)AbstractDungeon.getCurrRoom().event).remCard){
                        CardCrawlGame.playerPref.putString("NOTE_CARD", "None");
                    }
                    else {
                        CardCrawlGame.playerPref.putString("NOTE_CARD", tmpCard.cardID);
                        CardCrawlGame.playerPref.putInteger("NOTE_UPGRADE", tmpCard.timesUpgraded);
                        CardCrawlGame.playerPref.putInteger("NOTE_MISC", tmpCard.misc);
                    }
                    CardCrawlGame.playerPref.flush();
                }
            }
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getCurrRoom");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
