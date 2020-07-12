package betterNote;

import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.interfaces.*;
import betterNote.patches.customMetrics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;

import basemod.BaseMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import betterNote.util.TextureLoader;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings("unused")

@SpireInitializer
public class BetterNote implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostDeathSubscriber,
        PostInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(BetterNote.class.getName());

    //mod settings
    public static Properties defaultSettings = new Properties();
    public static final String ascension_limit_settings = "ascensionLimit";
    public static boolean ascLimit = false;
    public static final String dupe_limit_settings = "dupeLimit";
    public static boolean dupeLimit = true;

    private static final String MODNAME = "Better Note";
    private static final String AUTHOR = "Nichilas";
    private static final String DESCRIPTION = "A mod to make the Note for Yourself event better";

    private static final String BADGE_IMAGE = "betterNoteResources/images/Badge.png";

    private static final String AUDIO_PATH = "betterNoteResources/audio/";

    private static final String modID = "betterNote";


    //Image Directories
    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

    public static String makeMonsterPath(String resourcePath) {
        return modID + "Resources/images/monsters/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return modID + "Resources/images/orbs/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makeRelicOutlinePath(String resourcePath) {
        return modID + "Resources/images/relics/outline/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static String makeVfxPath(String resourcePath) {
        return modID + "Resources/images/vfx/" + resourcePath;
    }


    public BetterNote() {
        BaseMod.subscribe(this);

        logger.info("Adding mod settings");
        defaultSettings.setProperty(ascension_limit_settings, "FALSE");
        defaultSettings.setProperty(dupe_limit_settings, "TRUE");
        try {
            SpireConfig config = new SpireConfig("betterNote", "betterNoteConfig", defaultSettings);
            config.load();
            ascLimit = config.getBool(ascension_limit_settings);
            dupeLimit = config.getBool(dupe_limit_settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        BetterNote betterNote = new BetterNote();
    }

    public void receiveEditPotions() {
        //BaseMod.addPotion(NewPotion.class, SLUMBERING_POTION_RUST, SLUMBERING_TEAL, SLUMBERING_POTION_RUST, NewPotion.POTION_ID, TheSlumbering.Enums.THE_SLUMBERING);
    }

    @Override
    public void receiveEditCards() {

    }

    @Override
    public void receiveEditCharacters() {
        receiveEditPotions();
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(modID + "Resources/localization/eng/Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID.toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditRelics() {

    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                modID + "Resources/localization/eng/Card-Strings.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                modID + "Resources/localization/eng/Character-Strings.json");
        BaseMod.loadCustomStringsFile(EventStrings.class,
                modID + "Resources/localization/eng/Event-Strings.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                modID + "Resources/localization/eng/Monster-Strings.json");
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                modID + "Resources/localization/eng/Orb-Strings.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                modID + "Resources/localization/eng/Potion-Strings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                modID + "Resources/localization/eng/Power-Strings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                modID + "Resources/localization/eng/Relic-Strings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class,
                modID + "Resources/localization/eng/UI-Strings.json");
    }

    private void loadAudio() {
        HashMap<String, Sfx> map = (HashMap<String, Sfx>) ReflectionHacks.getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");
        //map.put("Pop", new Sfx(AUDIO_PATH + "pop.ogg", false));
    }

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        ModPanel settingsPanel = new ModPanel();

        ModLabeledToggleButton enableEventsButton = new ModLabeledToggleButton("Enables Better Note event for all ascension levels.",
                350.0f, 750.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                ascLimit,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    ascLimit = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterNote", "betterNoteConfig", defaultSettings);
                        config.setBool(ascension_limit_settings, ascLimit);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        ModLabeledToggleButton enabledDupesButton = new ModLabeledToggleButton("Limits ability to 'Take & Give' the card just received.",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                dupeLimit,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    dupeLimit = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterNote", "betterNoteConfig", defaultSettings);
                        config.setBool(dupe_limit_settings, dupeLimit);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        settingsPanel.addUIElement(enableEventsButton);
        settingsPanel.addUIElement(enabledDupesButton);
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        //events
        //BaseMod.addEvent(MysteriousOrb.ID, MysteriousOrb.class, Exordium.ID);

        //monsters
        //BaseMod.addMonster(NewSlaver.ID, NewSlaver.ID, () -> new NewSlaver(0.0F, 0.0F));

        //encounters
        //BaseMod.addMonsterEncounter(Exordium.ID, new MonsterInfo(NewCultist.ID, 3.0F));  //normal weight 2

        //audio
        loadAudio();
    }

    @Override
    public void receivePostDeath() {
        customMetrics metrics = new customMetrics();

        Thread t = new Thread(metrics);
        t.setName("Metrics");
        t.start();
    }
}