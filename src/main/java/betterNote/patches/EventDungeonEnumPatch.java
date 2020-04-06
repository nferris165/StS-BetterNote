package betterNote.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = SpirePatch.CLASS
)

public class EventDungeonEnumPatch {

    public static SpireField<Float> noteChance = new SpireField<>(() -> 0.39F);

}
