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
    public boolean remCard = false;
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
                this.screen = BetterNoteEvent.CUR_SCREEN.CHOOSE;
                if(this.obtainCard != null) {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                    this.imageEventText.updateDialogOption(0, OPTIONS[5] + this.obtainCard.name + OPTIONS[6], this.obtainCard);
                    this.imageEventText.updateDialogOption(1, OPTIONS[1] + this.obtainCard.name + OPTIONS[2], this.obtainCard);
                    this.imageEventText.updateDialogOption(2, OPTIONS[7] + OPTIONS[8] + this.obtainCard.name + OPTIONS[9], this.obtainCard);
                } else {
                    this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                    this.imageEventText.updateDialogOption(0, OPTIONS[10], true);
                    this.imageEventText.updateDialogOption(1, OPTIONS[10], true);
                    this.imageEventText.updateDialogOption(2, OPTIONS[7] + OPTIONS[9]);
                }
                this.imageEventText.setDialogOption(OPTIONS[3]);
                break;
            case CHOOSE:
                this.screen = BetterNoteEvent.CUR_SCREEN.COMPLETE;
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                switch(buttonPressed) {
                    case 0:
                        for(AbstractRelic r: AbstractDungeon.player.relics){
                            r.onObtainCard(this.obtainCard);
                        }

                        AbstractDungeon.player.masterDeck.addToTop(this.obtainCard);

                        for(AbstractRelic r: AbstractDungeon.player.relics){
                            r.onMasterDeckChange();
                        }
                        this.saveCard = new IronWave();
                        this.remCard = true;
                        break;
                    case 1:
                        for(AbstractRelic r: AbstractDungeon.player.relics){
                            r.onObtainCard(this.obtainCard);
                        }

                        AbstractDungeon.player.masterDeck.addToTop(this.obtainCard);

                        for(AbstractRelic r: AbstractDungeon.player.relics){
                            r.onMasterDeckChange();
                        }

                        this.cardSelect = true;
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(
                                AbstractDungeon.player.masterDeck.getPurgeableCards()), 1,
                                DESCRIPTIONS[2], false, false, false, false);
                        break;
                    case 2:
                        this.cardSelect = true;
                        AbstractDungeon.gridSelectScreen.open(CardGroup.getGroupWithoutBottledCards(
                                AbstractDungeon.player.masterDeck.getPurgeableCards()), 1,
                                DESCRIPTIONS[2], false, false, false, false);
                        break;
                    case 3:
                        //'Leave' Option
                        break;
                }

                this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                this.imageEventText.clearRemainingOptions();
                this.screen = BetterNoteEvent.CUR_SCREEN.COMPLETE;
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                break;
            case COMPLETE:
                this.openMap();
        }
    }

    public void update() {
        super.update();
        if (this.cardSelect && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard storeCard = AbstractDungeon.gridSelectScreen.selectedCards.remove(0);
            this.saveCard = storeCard.makeStatEquivalentCopy();
            this.cardSelect = false;
        }
    }

    private void initializeObtainCard() {
        this.obtainCard = CardLibrary.getCard(CardCrawlGame.playerPref.getString("NOTE_CARD", "None"));
        if (this.obtainCard == null) {
            return;
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
    }

    private enum CUR_SCREEN {
        INTRO,
        CHOOSE,
        COMPLETE;

        CUR_SCREEN() {
        }
    }
}
