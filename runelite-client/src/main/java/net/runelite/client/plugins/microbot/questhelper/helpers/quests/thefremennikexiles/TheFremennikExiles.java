/*
 * Copyright (c) 2021, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.thefremennikexiles;

import net.runelite.client.plugins.microbot.questhelper.bank.banktab.BankSlotIcons;
import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.NpcCondition;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemOnTileRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirements;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.InInstanceRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.PrayerRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.ComplexRequirementBuilder;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper.and;
import static net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper.not;

public class TheFremennikExiles extends BasicQuestHelper
{
	//Items Required
	ItemRequirement combatGear, mirrorShield, kegsOfBeer, moltenGlass, astralRunes, petRock, kegs2Or650Coins,
	fishingOrFlyFishingRod, fremennikShield, iceGloves, hammer, glassblowingPipe, pickaxe, sealOfPassage, coins150kOrCharos;

	//Items Recommended
	ItemRequirement food, rellekkaTeleport, coins650, restorePot;

	ItemRequirement letter, fang, venomGland, lunarOre, lunarBar, lunarGlass, moltenGlassI, sigil, sigilE,
	polishedRock, vShield, runeThrowingaxeOrFriend;

	Requirement inYagaHouse, inMine, onIsleOfStone, inTyphorRoom;

	Requirement sealOfPassageOrEliteDiary, killedYoungling, letterNearby, younglingNearby, hasReadLetter,
		askedAboutGlass, askedAboutRock, askedAboutSigil, askedAboutShield, askedAboutAllShieldParts,
		triedToThrowRockIntoGeyser, talkedToPeer, rockInGeyser;

	QuestStep buyKegs, talkToBrundt, talkToFreygerd, searchSandpit, searchSandpitForLetter, killYoungling, pickupLetter,
		readLetter, searchRockslide, searchBoxes, talkToFreygardWithItems, talkToBrundtAgain,
		talkToBrundtSouthEastOfRellekka;

	QuestStep askAboutShield, askAboutGlass, askAboutRock, askAboutSigil;

	QuestStep getFremennikShield, enterYagaHouse, talkToYaga, enterMine, mine3Ores, leaveMine, makeLunarGlass, smeltLunarBars,
		smithSigil, talkToFossegrimen;

	QuestStep inspectGeyser, talkToPeer, inspectGeyserWithIceGloves, retrieveRock, createShield;

	QuestStep talkToBrundtWithShield, talkToBrundtBackInRellekka, talkToBrundtWithBeers, killBasilisks,
		travelToIsleOfStone;

	QuestStep talkToBrundtOutsidePuzzle;

	QuestStep solvePuzzle;

	QuestStep enterCaveToFight, fightTyphor, fightJormungand, watchCutscenes, talkToBrundtToFinish;

	ConditionalStep goMakeSigil, goMakeGlass, goMakeRock, goGetShield;

	//Zones
	Zone lunarMine, yagaHouse, isleOfStone, typhorRoom;
	private ConditionalStep talkToBrundtSouthEastOfRellekkaCond;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep getKegsThenStart = new ConditionalStep(this, buyKegs);
		getKegsThenStart.addStep(kegsOfBeer.quantity(2), talkToBrundt);
		steps.put(0, getKegsThenStart);
		steps.put(5, getKegsThenStart);
		steps.put(10, talkToFreygerd);

		ConditionalStep investigate = new ConditionalStep(this, searchSandpit);
		investigate.addStep(new Conditions(hasReadLetter, fang, venomGland), talkToFreygardWithItems);
		investigate.addStep(new Conditions(hasReadLetter, fang), searchBoxes);
		investigate.addStep(hasReadLetter, searchRockslide);
		investigate.addStep(letter, readLetter);
		investigate.addStep(and(killedYoungling, letterNearby), pickupLetter);
		investigate.addStep(killedYoungling, searchSandpitForLetter);
		investigate.addStep(younglingNearby, killYoungling);
		steps.put(15, investigate);

		steps.put(20, talkToBrundtAgain);
		steps.put(25, talkToBrundtAgain);
		steps.put(30, talkToBrundtAgain);

		steps.put(35, talkToBrundtSouthEastOfRellekkaCond);
		steps.put(40, talkToBrundtSouthEastOfRellekkaCond);

		goMakeGlass = new ConditionalStep(this, enterYagaHouse);
		goMakeGlass.addStep(moltenGlassI, makeLunarGlass);
		goMakeGlass.addStep(inYagaHouse, talkToYaga);
		goMakeGlass.setLockingCondition(lunarGlass);
		goMakeGlass.setBlocker(true);

		goMakeSigil = new ConditionalStep(this, enterMine);
		goMakeSigil.addStep(sigil, talkToFossegrimen);
		goMakeSigil.addStep(lunarBar.quantity(3), smithSigil);
		goMakeSigil.addStep(lunarOre.quantity(3), smeltLunarBars);
		goMakeSigil.addStep(inMine, mine3Ores);
		goMakeSigil.setLockingCondition(sigilE);

		goMakeRock = new ConditionalStep(this, inspectGeyser);
		goMakeRock.addStep(rockInGeyser, retrieveRock);
		goMakeRock.addStep(talkedToPeer, inspectGeyserWithIceGloves);
		goMakeRock.addStep(triedToThrowRockIntoGeyser, talkToPeer);
		goMakeRock.setLockingCondition(polishedRock);

		// TODO: Add more details for shield
		goGetShield = new ConditionalStep(this, getFremennikShield);
		goGetShield.setLockingCondition(fremennikShield);

		ConditionalStep goMakeShield = new ConditionalStep(this, talkToBrundtSouthEastOfRellekkaCond);
		goMakeShield.addStep(vShield, talkToBrundtWithShield);
		goMakeShield.addStep(new Conditions(askedAboutAllShieldParts, fremennikShield, lunarGlass, sigilE, polishedRock), createShield);
		goMakeShield.addStep(new Conditions(askedAboutAllShieldParts, fremennikShield, lunarGlass, sigilE), goMakeRock);
		goMakeShield.addStep(new Conditions(askedAboutAllShieldParts, fremennikShield, lunarGlass), goMakeSigil);
		goMakeShield.addStep(new Conditions(askedAboutAllShieldParts, fremennikShield), goMakeGlass);
		goMakeShield.addStep(askedAboutAllShieldParts, goGetShield);
		steps.put(50, goMakeShield);
		steps.put(55, talkToBrundtWithBeers);
		steps.put(60, talkToBrundtWithBeers);
		steps.put(65, talkToBrundtWithBeers);
		steps.put(70, talkToBrundtBackInRellekka);
		steps.put(75, talkToBrundtBackInRellekka);
		steps.put(76, talkToBrundtBackInRellekka);

		ConditionalStep goDefendRellekka = new ConditionalStep(this, talkToBrundtBackInRellekka);
		goDefendRellekka.addStep(new InInstanceRequirement(), killBasilisks);
		steps.put(77, goDefendRellekka);
		steps.put(80, goDefendRellekka);

		ConditionalStep goToIsle = new ConditionalStep(this, travelToIsleOfStone);
		goToIsle.addStep(onIsleOfStone, talkToBrundtOutsidePuzzle);
		steps.put(85, goToIsle);

		ConditionalStep goDoPuzzle = new ConditionalStep(this, travelToIsleOfStone);
		goDoPuzzle.addStep(onIsleOfStone, solvePuzzle);
		steps.put(90, goDoPuzzle);

		ConditionalStep goFightTyphor = new ConditionalStep(this, travelToIsleOfStone);
		goFightTyphor.addStep(inTyphorRoom, fightTyphor);
		goFightTyphor.addStep(onIsleOfStone, enterCaveToFight);
		steps.put(95, goFightTyphor);
		steps.put(100, goFightTyphor);

		ConditionalStep goFightJorm = new ConditionalStep(this, travelToIsleOfStone);
		goFightJorm.addStep(inTyphorRoom, fightJormungand);
		goFightJorm.addStep(onIsleOfStone, enterCaveToFight);
		steps.put(105, goFightJorm);
		steps.put(110, goFightJorm);

		ConditionalStep goFinish = new ConditionalStep(this, travelToIsleOfStone);
		goFinish.addStep(inTyphorRoom, watchCutscenes);
		goFinish.addStep(onIsleOfStone, enterCaveToFight);
		steps.put(115, goFinish);

		steps.put(120, talkToBrundtToFinish);
		steps.put(125, talkToBrundtToFinish);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		mirrorShield = new ItemRequirement("Mirror shield", ItemID.SLAYER_MIRROR_SHIELD).isNotConsumed();
		kegsOfBeer = new ItemRequirement("Kegs of beer", ItemID.KEG_OF_BEER);
		kegsOfBeer.setTooltip("You can buy some from Rasolo south east of Baxtorian Falls");
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS);
		astralRunes = new ItemRequirement("Astral runes", ItemID.ASTRALRUNE);
		petRock = new ItemRequirement("Pet rock", ItemID.VT_USELESS_ROCK).isNotConsumed();
		petRock.setTooltip("You can get another from Askeladden in Rellekka");
		fishingOrFlyFishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD).isNotConsumed();
		fishingOrFlyFishingRod.addAlternates(ItemID.FLY_FISHING_ROD);
		fremennikShield = new ItemRequirement("Fremennik shield", ItemID.VIKING_SHIELD);
		fremennikShield.setTooltip("Obtainable during the quest for 150k, or free with a Ring of Charos(a)");
		iceGloves = new ItemRequirement("Ice gloves or smiths gloves(i)", ItemID.ICE_GLOVES).isNotConsumed();
		iceGloves.setTooltip("You can get another pair of ice gloves by killing the Ice Queen under White Wolf Mountain");
		iceGloves.addAlternates(ItemID.SMITHING_UNIFORM_GLOVES_ICE);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		glassblowingPipe = new ItemRequirement("Glassblowing pipe", ItemID.GLASSBLOWINGPIPE).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		restorePot = new ItemRequirement("Restore potions", ItemCollections.RESTORE_POTIONS);
		restorePot.setTooltip("Highly recommended to make up for mistakes");

		runeThrowingaxeOrFriend = new ItemRequirement("Rune thrownaxe, or a friend to help enter Waterbirth Isle " +
			"Dungeon",
			ItemID.RUNE_THROWNAXE);

		sealOfPassage = new ItemRequirement("Seal of passage", ItemID.LUNAR_SEAL_OF_PASSAGE).isNotConsumed();
		sealOfPassageOrEliteDiary = ComplexRequirementBuilder.or("Seal of Passage")
			.with(new VarbitRequirement(Varbits.DIARY_FREMENNIK_ELITE, 1))
			.with(sealOfPassage)
			.build();

		coins150kOrCharos = new ItemRequirements(LogicType.OR,
			"Ring of Charos(a) or 150k coins",
			new ItemRequirement("Ring of Charos(a)", ItemID.RING_OF_CHAROS_UNLOCKED),
			new ItemRequirement("Coins", ItemCollections.COINS, 150000));

		coins650 = new ItemRequirement("Coins", ItemCollections.COINS, 650);
		kegs2Or650Coins = new ItemRequirements(LogicType.OR, "2x kegs of beer or 650 coins", kegsOfBeer.quantity(2), coins650);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		rellekkaTeleport = new ItemRequirement("Rellekka teleports", ItemID.NZONE_TELETAB_RELLEKKA, -1);
		rellekkaTeleport.addAlternates(ItemCollections.ENCHANTED_LYRE);
		rellekkaTeleport.addAlternates(ItemCollections.SLAYER_RINGS);


		// Quest items
		letter = new ItemRequirement("Unsealed letter", ItemID.VIKINGEXILE_LETTER);
		letter.addAlternates(ItemID.VIKINGEXILE_LETTERREADONLY);
		fang = new ItemRequirement("Fang", ItemID.VIKINGEXILE_FANG);
		venomGland = new ItemRequirement("Venom gland", ItemID.VIKINGEXILE_GLAND);
		lunarOre = new ItemRequirement("Lunar ore", ItemID.QUEST_LUNAR_MAGIC_ORE);
		lunarOre.addAlternates(ItemID.QUEST_LUNAR_MAGIC_BAR);
		lunarBar = new ItemRequirement("Lunar bar", ItemID.QUEST_LUNAR_MAGIC_BAR);
		lunarGlass = new ItemRequirement("Lunar glass", ItemID.VIKINGEXILE_LUNARGLASS_ENCHANTED);
		moltenGlassI = new ItemRequirement("Molten glass (i)", ItemID.VIKINGEXILE_LUNARGLASS_IMBUED);
		sigil = new ItemRequirement("V sigil", ItemID.VIKINGEXILE_V_SIGIL_UNENCHANTED);
		sigilE = new ItemRequirement("V sigil (e)", ItemID.VIKINGEXILE_V_SIGIL_ENCHANTED);
		polishedRock = new ItemRequirement("Polishing rock", ItemID.VIKINGEXILE_POLISHING_ROCK);
		vShield = new ItemRequirement("V's shield", ItemID.VIKINGEXILE_V_SHIELD);
		vShield.addAlternates(ItemID.V_SHIELD);

		// Quest events
		younglingNearby = new NpcCondition(NpcID.VIKINGEXILE_YOUNGLING);
		letterNearby = new ItemOnTileRequirement(letter);
		killedYoungling = new VarbitRequirement(9460, 1);
		hasReadLetter = new VarbitRequirement(9461, 1);
		// 9468 = 1, youngling popped out first time
		askedAboutShield = new VarbitRequirement(VarbitID.VIKINGEXILE_SHIELD_INFO, 1, Operation.GREATER_EQUAL);
		askedAboutGlass = new VarbitRequirement(VarbitID.VIKINGEXILE_GLASS_INFO, 1, Operation.GREATER_EQUAL);
		askedAboutRock = new VarbitRequirement(VarbitID.VIKINGEXILE_ROCK_INFO, 1, Operation.GREATER_EQUAL);
		askedAboutSigil = new VarbitRequirement(VarbitID.VIKINGEXILE_SIGIL_INFO, 1, Operation.GREATER_EQUAL);
		askedAboutAllShieldParts = new Conditions(askedAboutShield, askedAboutGlass, askedAboutRock, askedAboutSigil);
		triedToThrowRockIntoGeyser = new VarbitRequirement(9464, 2);
		talkedToPeer = new VarbitRequirement(9464, 3);
		rockInGeyser = new VarbitRequirement(9470, 1);
		// been given shield, 9471 = 1
		// Fighting basilisks, 9466 0-30 for 0-100%
		// Zone checks
		inYagaHouse = new ZoneRequirement(yagaHouse);
		inMine = new ZoneRequirement(lunarMine);
		onIsleOfStone = new ZoneRequirement(isleOfStone);
		inTyphorRoom = new ZoneRequirement(typhorRoom);

		// Puzzle:
		// 1=Air, 2=Water, 3=Earth, 4=fire, 5=astral, 6=cosmic
		// 9477, 0 select button
		// 9478, 1 select button
		// 9479, 1 select button
		// 9480, 1 select button

		// 9481 0->1 = rows done
		// 9482 0->1->2, top left tick box (1=green tick, 2=red tick)
		// 9483 0->2, top right?
		// 9484 0->1, bottom left
		// 9485 0->2->1 bottom right
	}

	@Override
	protected void setupZones()
	{
		yagaHouse = new Zone(new WorldPoint(2449, 4645, 0), new WorldPoint(2453, 4649, 0));
		lunarMine = new Zone(new WorldPoint(2300, 10313, 2), new WorldPoint(2370, 10354, 2));
		isleOfStone = new Zone(new WorldPoint(2436, 3986, 0), new WorldPoint(2493, 4033, 0));
		typhorRoom = new Zone(new WorldPoint(2437, 10364, 0), new WorldPoint(2477, 10404, 0));
	}

	public void setupSteps()
	{
		talkToBrundt = new NpcStep(this, NpcID.VIKING_BRUNDT_CHILD, new WorldPoint(2658, 3669, 0),
			"Talk to Brundt in Rellekka's longhall.");
		talkToBrundt.addDialogSteps("Ask for a quest.", "Yes.");
		buyKegs = new NpcStep(this, NpcID.VIKING_LONGHALL_BARKEEP, new WorldPoint(2662, 3673, 0),
			"Buy 2 kegs of beer from Thora in Rellekka.  She will be unavailable to trade later in the quest.", coins650);
		talkToFreygerd = new NpcStep(this, NpcID.VIKING_WOMAN, new WorldPoint(2668, 3703, 0),
			"Talk to Freygerd in north Rellekka.", combatGear, mirrorShield.equipped());
		searchSandpit = new ObjectStep(this, ObjectID.VIKING_SANDPIT, new WorldPoint(2668, 3708, 0),
			"Search the sand pit near Freygerd, ready to fight a basilisk youngling.", combatGear, mirrorShield.equipped());
		searchSandpitForLetter = new ObjectStep(this, ObjectID.VIKING_SANDPIT, new WorldPoint(2668, 3708, 0),
			"Search the sand pit near Freygerd for a letter.", letter.highlighted());
		killYoungling = new NpcStep(this, NpcID.VIKINGEXILE_YOUNGLING, new WorldPoint(2666, 3708, 0),
			"Defeat the Basilisk Youngling.", combatGear, mirrorShield.equipped());
		pickupLetter = new ItemStep(this, "Pick up the letter.", letter);
		pickupLetter.addSubSteps(searchSandpitForLetter);
		readLetter = new DetailedQuestStep(this, "Read the letter.", letter.highlighted());
		searchRockslide = new ObjectStep(this, ObjectID.VIKINGEXILE_ROCKS, new WorldPoint(2659, 3704, 0),
			"Search the rockslide west of the sand pit.");
		searchBoxes = new ObjectStep(this, ObjectID.VIKINGEXILE_BOXES, new WorldPoint(2668, 3699, 0),
			"Search the boxes south of Freygerd.");
		talkToFreygardWithItems = new NpcStep(this, NpcID.VIKING_WOMAN, new WorldPoint(2668, 3703, 0),
			"Return to Freygerd with the items you found.", letter, fang, venomGland);
		talkToBrundtAgain = new NpcStep(this, NpcID.VIKING_BRUNDT_CHILD, new WorldPoint(2658, 3669, 0),
			"Return to Brundt in Rellekka's longhall.");
		talkToBrundtAgain.addDialogStep("Ask about the investigation.");
		talkToBrundtAgain.addDialogStep("Ask about the Jormungand.");

		talkToBrundtSouthEastOfRellekka = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt south east of Rellekka, asking him all available questions.");

		askAboutShield = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt south east of Rellekka, asking him all available questions.");
		askAboutShield.addDialogStep("Where can I find a Fremennik Shield?");
		askAboutGlass = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt south east of Rellekka, asking him all available questions.");
		askAboutGlass.addDialogSteps("Where can I get Lunar Glass?");
		askAboutRock = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt south east of Rellekka, asking him all available questions.");
		askAboutRock.addDialogSteps("How do I make the Polishing Rock?");
		askAboutSigil = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt south east of Rellekka, asking him all available questions.");
		askAboutSigil.addDialogSteps("How do I make V's Sigil?");
		talkToBrundtSouthEastOfRellekka.addSubSteps(askAboutShield, askAboutGlass, askAboutRock, askAboutSigil);

		talkToBrundtSouthEastOfRellekkaCond = new ConditionalStep(this, talkToBrundtSouthEastOfRellekka);
		talkToBrundtSouthEastOfRellekkaCond.addStep(not(askedAboutShield), askAboutShield);
		talkToBrundtSouthEastOfRellekkaCond.addStep(not(askedAboutGlass), askAboutGlass);
		talkToBrundtSouthEastOfRellekkaCond.addStep(not(askedAboutRock), askAboutRock);
		talkToBrundtSouthEastOfRellekkaCond.addStep(not(askedAboutSigil), askAboutSigil);

		enterYagaHouse = new NpcStep(this, NpcID.LUNAR_BABA_YAGA_HOUSE, new WorldPoint(2085, 3931, 0),
			"Talk to Baba Yaga in the chicken-legged house in the north of Lunar Isle's town.", sealOfPassage, moltenGlass);
		talkToYaga = new NpcStep(this, NpcID.LUNAR_MOONCLAN_BABA_YAGA, new WorldPoint(2451, 4646, 0), "Talk to Baba Yaga.",
			sealOfPassage, moltenGlass);
		talkToYaga.addDialogStep("I was wondering if you could help me make Lunar Glass?");
		enterYagaHouse.addSubSteps(talkToYaga);

		enterMine = new ObjectStep(this, ObjectID.LUNAR_MINE_SLANTY_LADDER_DOWN, new WorldPoint(2142, 3944, 0),
			"Enter the mine in the north east of Lunar Isle.", pickaxe);
		mine3Ores = new ObjectStep(this, ObjectID.LUNAR_MINE_STALAGMITE_SMALL,
			"Mine 3 lunar ores from the stalagmites in the area.", pickaxe);
		((ObjectStep) mine3Ores).addAlternateObjects(ObjectID.LUNAR_MINE_STALAGMITE_TWIN);

		leaveMine = new ObjectStep(this, ObjectID.LUNAR_MINE_SLANTY_LADDER_UP, new WorldPoint(2330, 10353, 2),
			"Leave the mine.");

		makeLunarGlass = new ObjectStep(this, ObjectID.ASTRAL_ALTAR, new WorldPoint(2158, 3864, 0), "Use the Lunar " +
			"Altar to make some lunar glass.", moltenGlassI, astralRunes.quantity(100));
		smeltLunarBars = new DetailedQuestStep(this, "Smelt the lunar ore into bars at any furnace.",
			lunarOre.quantity(3));
		smithSigil = new DetailedQuestStep(this, "Make the V sigil on any anvil.", lunarBar.quantity(3));
		smithSigil.addDialogStep("Yes.");
		talkToFossegrimen = new NpcStep(this, NpcID.VIKING_LAKE_SPIRIT, new WorldPoint(2626, 3598, 0),
			"Talk to the Fossegrimen south west of Rellekka to enchant the sigil.", sigil);
		talkToFossegrimen.addDialogStep("Ask about V's Sigil.");

		inspectGeyser = new ObjectStep(this, ObjectID.VIKINGEXILE_GEYSER_LARGE, new WorldPoint(2766, 3675, 0),
			"Inspect the geyser north of the Mountain Camp.", petRock);
		inspectGeyser.addDialogStep("It's for the good of the Province!");
		talkToPeer = new NpcStep(this, NpcID.VIKING_PEER, new WorldPoint(2633, 3667, 0),
			"Talk to Peer the Seer south west of the Rellekka market.");

		inspectGeyserWithIceGloves = new ObjectStep(this, ObjectID.VIKINGEXILE_GEYSER_LARGE, new WorldPoint(2766, 3675, 0),
			"Inspect the geyser again.", petRock, iceGloves.equipped(), fishingOrFlyFishingRod);

		retrieveRock = new ObjectStep(this, ObjectID.VIKINGEXILE_GEYSER_LARGE, new WorldPoint(2766, 3675, 0),
			"Retrieve your rock from the geyser.", iceGloves.equipped(), fishingOrFlyFishingRod);
		createShield = new DetailedQuestStep(this, "Use the shield parts together to make V's Shield.",
			fremennikShield.highlighted(), lunarGlass.highlighted(), polishedRock.highlighted(), sigilE.highlighted()
			, glassblowingPipe);

		talkToBrundtWithShield = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt south east of Rellekka with V's shield.", vShield, kegsOfBeer.quantity(2));
		talkToBrundtWithBeers = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD, new WorldPoint(2705, 3634, 0),
			"Talk to Brundt with 2 kegs of beer.", kegsOfBeer.quantity(2));
		talkToBrundtBackInRellekka = new NpcStep(this, NpcID.VIKING_BRUNDT_CHILD, new WorldPoint(2658, 3669, 0),
			"Return to Brundt in Rellekka's longhall.", combatGear, food);
		talkToBrundtBackInRellekka.addDialogStep("Ask about the Jormungand.");
		getFremennikShield = new DetailedQuestStep(this, "You can get a fremennik shield from dagannoths in " +
			"Waterbirth Dungeon or Bardur in Waterbirth " +
			"Dungeon for 150k, or for free with the Ring of Charos(a). You'll need a friend to get there, or a rune " +
			"thrownaxe and a pet rock.", coins150kOrCharos);
		killBasilisks = new NpcStep(this, NpcID.VIKINGEXILE_BASILISK, new WorldPoint(2644, 3677, 0),
			"Kill basilisks until you're told to stop.", true, vShield.equipped(), combatGear, food);
		((NpcStep) killBasilisks).addAlternateNpcs(NpcID.VIKINGEXILE_BASILISK_HUNTPLAYER, NpcID.VIKINGEXILE_BASILISK_BABY, NpcID.VIKINGEXILE_BASILISK_BABY_HUNTPLAYER,
			NpcID.VIKINGEXILE_BASILISK_SUPERIOR, NpcID.VIKINGEXILE_BASILISK_SUPERIOR_HUNTPLAYER);
		travelToIsleOfStone = new ObjectStep(this, ObjectID.FREMENNIK_BOAT_STONE, new WorldPoint(2623, 3693, 0),
			"Take the boat to the Isle of Stone.");
		talkToBrundtOutsidePuzzle = new NpcStep(this, NpcID.VIKINGEXILE_BRUNDT_CHILD_SHIELD, new WorldPoint(2466, 4010, 0),
		"Talk to Brundt outside the cave entrance.");

		solvePuzzle = new ObjectStep(this, ObjectID.ISLAND_OF_STONE_CAVE, new WorldPoint(2465, 4012, 0),
			"Attempt to open the door and solve the puzzle. It's Mastermind, where a red tick means you have one of " +
				"the correct runes in the wrong position, and a green tick means a correct rune in the correct " +
				"position.");

		enterCaveToFight = new ObjectStep(this, ObjectID.ISLAND_OF_STONE_CAVE, new WorldPoint(2465, 4012, 0),
			"Enter the door, ready to fight.", combatGear, vShield.equipped());
		enterCaveToFight.addDialogStep("Yes.");
		fightTyphor = new NpcStep(this, NpcID.TYPHOR_CUTSCENE, new WorldPoint(2457, 10384, 0), "Fight Typhor, who attacks " +
			"with both Melee and Magic. He is weak to crush.", vShield.equipped());
		((NpcStep) fightTyphor).addAlternateNpcs(NpcID.TYPHOR_CUTSCENE);

		PrayerRequirement protectFromMagic = new PrayerRequirement("Protect from Magic", Prayer.PROTECT_FROM_MAGIC);
		fightJormungand = new NpcStep(this, NpcID.JORMUNGAND_FROZEN, new WorldPoint(2457, 10384, 0), "Defeat the " +
			"Jormungand.", vShield.equipped(), protectFromMagic);
		fightJormungand.addText("When the screen turns red, have your character face away from him.");
		fightJormungand.addText("When frozen in rock, click repeatedly to break out.");
		((NpcStep) fightJormungand).addAlternateNpcs(NpcID.JORMUNGAND_TURN, NpcID.JORMUNGAND, NpcID.JORMUNGAND_DEAD);

		watchCutscenes = new DetailedQuestStep(this, "Watch the cutscenes.");
		talkToBrundtToFinish = new NpcStep(this, NpcID.VIKING_BRUNDT_CHILD, new WorldPoint(2658, 3669, 0),
			"Talk to Brundt in Rellekka's longhall to finish the quest.");
		fightJormungand.addSubSteps(watchCutscenes, talkToBrundtToFinish);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, mirrorShield, kegs2Or650Coins, moltenGlass, astralRunes.quantity(100), petRock,
			runeThrowingaxeOrFriend, fishingOrFlyFishingRod, fremennikShield, iceGloves, hammer, glassblowingPipe, pickaxe, sealOfPassage);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, rellekkaTeleport, restorePot);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList(
			"Basilisk Youngling (level 53)",
			"Basilisk (level 61)",
			"Monstrous Basilisk (level 135)",
			"Typhor (level 218)",
			"The Jormungand (level 363)"
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.CRAFTING, 65));
		req.add(new SkillRequirement(Skill.SLAYER, 60));
		req.add(new SkillRequirement(Skill.SMITHING, 60));
		req.add(new SkillRequirement(Skill.FISHING, 60));
		req.add(new SkillRequirement(Skill.MINING, 60));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 55));
		req.add(new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_ISLES, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.MOUNTAIN_DAUGHTER, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HEROES_QUEST, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.SLAYER, 50000),
				new ExperienceReward(Skill.CRAFTING, 50000),
				new ExperienceReward(Skill.RUNECRAFT, 30000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("V's Shield", ItemID.VIKINGEXILE_V_SHIELD, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Isle of Stone"),
				new UnlockReward("Ability to kill Basilisk Knights as a slayer task"),
				new UnlockReward("Ability to craft and equip the Neitiznot faceguard."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(buyKegs, talkToBrundt, talkToFreygerd,
			searchSandpit, killYoungling, pickupLetter, readLetter, searchRockslide, searchBoxes,
			talkToFreygardWithItems, talkToBrundtAgain, talkToBrundtSouthEastOfRellekka), combatGear, mirrorShield));

		PanelDetails shieldPanel = new PanelDetails("A Fremennik Shield", Collections.singletonList(getFremennikShield),
			coins150kOrCharos, petRock, runeThrowingaxeOrFriend);
		shieldPanel.setLockingStep(goGetShield);
		allSteps.add(shieldPanel);

		PanelDetails glassPanel = new PanelDetails("Making Lunar Glass", Arrays.asList(enterYagaHouse,
			makeLunarGlass), sealOfPassage, moltenGlass, astralRunes.quantity(100));
		glassPanel.setLockingStep(goMakeGlass);
		allSteps.add(glassPanel);

		PanelDetails sigilPanel = new PanelDetails("Making V's Sigil", Arrays.asList(enterMine, mine3Ores,
			smeltLunarBars, smithSigil, talkToFossegrimen), pickaxe, hammer);
		sigilPanel.setLockingStep(goMakeSigil);
		allSteps.add(sigilPanel);

		PanelDetails rockPanel = new PanelDetails("Making a Polishing Rock", Arrays.asList(inspectGeyser, talkToPeer,
			inspectGeyserWithIceGloves, retrieveRock), petRock, iceGloves, fishingOrFlyFishingRod);
		rockPanel.setLockingStep(goMakeRock);
		allSteps.add(rockPanel);

		allSteps.add(new PanelDetails("Forging the shield", Arrays.asList(createShield, talkToBrundtWithShield,
			talkToBrundtWithBeers), fremennikShield, lunarGlass, polishedRock, sigilE, glassblowingPipe,
			kegsOfBeer.quantity(2)));

		allSteps.add(new PanelDetails("Defending Rellekka", Arrays.asList(talkToBrundtBackInRellekka, killBasilisks,
			travelToIsleOfStone, talkToBrundtOutsidePuzzle, solvePuzzle, enterCaveToFight, fightTyphor, fightJormungand),
			combatGear));

		return allSteps;
	}
}
