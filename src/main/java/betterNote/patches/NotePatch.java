package betterNote.patches;

import betterNote.BetterNote;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.random.Random;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;


public class NotePatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "generateEvent"
    )

    public static class NotePatchGenerate {
        public static SpireReturn<AbstractEvent> Prefix (Random __rng){

        if (AbstractDungeon.actNum == 1) {
            return SpireReturn.Return(AbstractDungeon.getShrine(__rng));
        }

        return SpireReturn.Continue();

        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getShrine"
    )

    public static class NotePatchChance {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<AbstractEvent> Insert(Random __rng, @ByRef String[] ___tmpKey, ArrayList<String> ___specialOneTimeEventList) {
            BetterNote.logger.info("Forcing Note Event over " + ___tmpKey[0]);
            //BetterNote.logger.info(___specialOneTimeEventList + " got list\n\n");

            if(___specialOneTimeEventList.contains(NoteForYourself.ID)){
                //BetterNote.logger.info("got note\n\n");

                if(AbstractDungeon.actNum == 1){
                    ___tmpKey[0] = NoteForYourself.ID;
                }
            }

            //BetterNote.logger.info(___tmpKey[0] + " got here\n\n");

            return SpireReturn.Continue();

        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "remove");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}

