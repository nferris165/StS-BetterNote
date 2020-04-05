package betterNote.events;

import betterNote.BetterNote;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.blue.GeneticAlgorithm;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.Iterator;

public class BetterNoteEvent extends AbstractImageEvent {


    public static final String ID = BetterNote.makeID("BetterNote");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/selfNote.jpg";

    private AbstractCard obtainCard = null;
    public AbstractCard saveCard = null;
    private boolean cardSelect = false;
    private BetterNoteEvent.CUR_SCREEN screen;

    public BetterNoteEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.screen = BetterNoteEvent.CUR_SCREEN.INTRO;
        this.imageEventText.setDialogOption(OPTIONS[0]);
        this.initializeObtainCard();
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch(this.screen) {
            case INTRO:
                this.imageEventText.updateBodyText(DESCRIPTIONS[1]);// 43
                this.screen = BetterNoteEvent.CUR_SCREEN.CHOOSE;// 44
                this.imageEventText.updateDialogOption(0, OPTIONS[1] + this.obtainCard.name + OPTIONS[2], this.obtainCard);// 45
                this.imageEventText.setDialogOption(OPTIONS[3]);// 46
                break;// 47
            case CHOOSE:
                this.screen = BetterNoteEvent.CUR_SCREEN.COMPLETE;// 49
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;// 50
                switch(buttonPressed) {// 51
                    case 0:
                        Iterator var2 = AbstractDungeon.player.relics.iterator();// 54

                        AbstractRelic r;
                        while(var2.hasNext()) {
                            r = (AbstractRelic)var2.next();
                            r.onObtainCard(this.obtainCard);// 55
                        }

                        AbstractDungeon.player.masterDeck.addToTop(this.obtainCard);// 57
                        var2 = AbstractDungeon.player.relics.iterator();// 59

                        while(var2.hasNext()) {
                            r = (AbstractRelic)var2.next();
                            r.onMasterDeckChange();// 60
                        }

                        this.cardSelect = true;// 63
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), 1, DESCRIPTIONS[2], false, false, false, false);// 64 65 66
                        break;// 73
                    case 1:
                        //logMetricIgnored("NoteForYourself");// 75
                }

                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);// 79
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);// 80
                this.imageEventText.clearRemainingOptions();// 81
                this.screen = BetterNoteEvent.CUR_SCREEN.COMPLETE;// 82
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;// 83
                break;// 84
            case COMPLETE:
                this.openMap();// 86
        }
    }

    public void update() {
        super.update();// 93
        if (this.cardSelect && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {// 95
            AbstractCard storeCard = AbstractDungeon.gridSelectScreen.selectedCards.remove(0);// 96
            AbstractDungeon.player.masterDeck.removeCard(storeCard);// 98
            this.saveCard = storeCard;// 99
            this.cardSelect = false;// 100
        }

    }

    private void initializeObtainCard() {
        this.obtainCard = CardLibrary.getCard(CardCrawlGame.playerPref.getString("NOTE_CARD", "Iron Wave"));
        if (this.obtainCard == null) {
            this.obtainCard = new IronWave();
        }

        this.obtainCard = this.obtainCard.makeCopy();

        for(int i = 0; i < CardCrawlGame.playerPref.getInteger("NOTE_UPGRADE", 0); ++i) {
            this.obtainCard.upgrade();
        }

        this.obtainCard.misc = CardCrawlGame.playerPref.getInteger("NOTE_MISC", 0);
        if(this.obtainCard instanceof RitualDagger){
            this.obtainCard.applyPowers();
            this.obtainCard.baseDamage = this.obtainCard.misc;
            this.obtainCard.isDamageModified = false;
        } else if(this.obtainCard instanceof GeneticAlgorithm){
            this.obtainCard.applyPowers();
            this.obtainCard.isBlockModified = false;
        }


        BetterNote.logger.info(CardCrawlGame.playerPref.getInteger("NOTE_MISC", 0) + "\n\n");

    }

    private enum CUR_SCREEN {
        INTRO,
        CHOOSE,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
