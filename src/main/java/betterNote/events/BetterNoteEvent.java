package betterNote.events;

import betterNote.BetterNote;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class BetterNoteEvent extends AbstractImageEvent {


    public static final String ID = BetterNote.makeID("BetterNote");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = "images/events/selfNote.jpg";

    private AbstractCard c;
    private boolean attack;

    private int screenNum = 0;

    private int goldLoss;

    public BetterNoteEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            goldLoss = 70;
        } else {
            goldLoss = 50;
        }

        this.attack = CardHelper.hasCardWithType(AbstractCard.CardType.ATTACK);

        imageEventText.setDialogOption(OPTIONS[0] + goldLoss + OPTIONS[1]);
        if(this.attack) {
            c = CardHelper.returnCardOfType(AbstractCard.CardType.ATTACK, AbstractDungeon.miscRng);
            imageEventText.setDialogOption(OPTIONS[2] + FontHelper.colorString(this.c.name, "r") + OPTIONS[3]);
        }
        else{
            imageEventText.setDialogOption(OPTIONS[7], true);
        }
        imageEventText.setDialogOption(OPTIONS[4]);
    }

    @Override
    protected void buttonEffect(int i) {
        switch (screenNum) {
            case 0:
                switch (i) {
                    case 0: // button = 0
                        AbstractDungeon.player.loseGold(goldLoss);
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break;
                    case 1:

                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.MED, false);
                        CardCrawlGame.sound.play("BLUNT_FAST");

                        //remove random attack
                        if (this.attack) {
                            AbstractDungeon.effectList.add(new PurgeCardEffect(c));
                            AbstractDungeon.player.masterDeck.removeCard(c);
                        }

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[6]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 2;

                        break;
                    case 2:

                        //SlumberingMod.incSlumberingRelic(-1);

                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break;
                }
                break;
            case 1: //screenNum = 1;
                switch (i) {
                    case 0:
                        openMap();
                        break;
                }
                break;
            case 2:
                switch (i) {
                    case 0:
                        //generate basic cards


                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[5]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break;
                }
                break;
        }
    }
}
