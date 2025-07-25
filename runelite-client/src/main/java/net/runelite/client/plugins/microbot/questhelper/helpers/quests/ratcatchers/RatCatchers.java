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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.ratcatchers;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.collections.NpcCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestVarbits;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.FollowerItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.npc.FollowerRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.widget.WidgetTextRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class RatCatchers extends BasicQuestHelper
{
	// Required
	ItemRequirement ratPoison, cheese, marrentill, unicornHornDust, bucketOfMilk, catspeakAmuletOrDS2,
		potOfWeeds, tinderbox, coins101, snakeCharm, fish8, cat, coin, kwuarm, vial, redEggs;

	Requirement catFollower;

	// Recommended
	ItemRequirement varrockTeleport, sarimTeleport, pollnivneachTeleport, ardougneTeleport, keldagrimTeleport,
		carpetCoins;

	// Quest items
	ItemRequirement directions, poisonedCheese4, poisonedCheese3, poisonedCheese2, poisonedCheese1, smoulderingPot,
		catantipoison, musicScroll;

	Requirement inVarrockSewer, inMansionGrounds, inMansionF1, inMansionF0, inGiantRatArea, inKelgdagrim, inRatPit,
		inPortSarim, caughtRat1, caughtRat2And3, poisonedHole1, poisonedHole2, poisonedHole3, poisonedHole4,
		catSeenFailure, inPlayWidget;

	QuestStep talkToGertrude, enterSewer, talkToPhingspet, catch8Rats, talkToPhingspetAgain;

	QuestStep talkToJimmy, readDirections, climbTrellis, climbTrellisNoPath, climbDownLadderInMansion,
		talkToJimmyAgain;

	NpcStep catchRat1, catchRat2And3, catchRemainingRats;

	QuestStep talkToJack, climbJackLadder, useRatPoisonOnCheese, useCheeseOnHole1, useCheeseOnHole2,
		useCheeseOnHole3, useCheeseOnHole4, goDownToJack, talkToJackAfterCheese, talkToApoth, talkToJackAfterApoth,
		talkToJackAfterCure, climbJackLadderAgain, useCatOnHole, feedCatAsItFights, goDownToJackAfterFight,
		talkToJackAfterFight;

	QuestStep travelToKeldagrim, talkToSmokinJoe, lightWeeds, usePotOnHole, usePotOnHoleAgain, talkToJoeAgain;

	QuestStep enterSarimRatPits, talkToFelkrash, leaveSarimRatPits, talkToTheFaceAgain;

	QuestStep useCoinOnPot, returnToSarim, clickSnakeCharm;

	RatCharming playSnakeCharm;

	QuestStep enterPitsForEnd, talkToFelkrashForEnd;

	Zone varrockSewer, mansionGrounds, mansionF1, mansionF0, giantRatArea, keldagrim, ratPit, portSarim;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGertrude);

		ConditionalStep goTalkToSewerPeople = new ConditionalStep(this, enterSewer);
		goTalkToSewerPeople.addStep(inVarrockSewer, talkToPhingspet);
		steps.put(5, goTalkToSewerPeople);

		ConditionalStep goCatchSewerRats = new ConditionalStep(this, enterSewer);
		goCatchSewerRats.addStep(inVarrockSewer, catch8Rats);
		steps.put(10, goCatchSewerRats);

		ConditionalStep goTalkToSewerPeopleAgain = new ConditionalStep(this, enterSewer);
		goTalkToSewerPeopleAgain.addStep(inVarrockSewer, talkToPhingspetAgain);
		steps.put(15, goTalkToSewerPeopleAgain);

		steps.put(20, talkToJimmy);

		ConditionalStep goEnterMansion = new ConditionalStep(this, readDirections);
		goEnterMansion.addStep(inMansionGrounds, climbTrellis);
		steps.put(25, goEnterMansion);

		ConditionalStep goCatchMansionRats = new ConditionalStep(this, readDirections);
		goCatchMansionRats.addStep(new Conditions(inMansionF0, caughtRat1, caughtRat2And3), catchRemainingRats);
		goCatchMansionRats.addStep(new Conditions(inMansionF1, caughtRat1, caughtRat2And3), climbDownLadderInMansion);
		goCatchMansionRats.addStep(new Conditions(inMansionF1, caughtRat1), catchRat2And3);
		goCatchMansionRats.addStep(inMansionF1, catchRat1);
		goCatchMansionRats.addStep(inMansionGrounds, climbTrellisNoPath);
		steps.put(30, goCatchMansionRats);
		steps.put(35, talkToJimmyAgain);
		steps.put(40, talkToJack);
		steps.put(45, talkToJack);

		ConditionalStep	goPoisonRats = new ConditionalStep(this, climbJackLadder);
		goPoisonRats.addStep(new Conditions(inGiantRatArea, poisonedCheese1, poisonedHole1, poisonedHole2, poisonedHole3),
			useCheeseOnHole4);
		goPoisonRats.addStep(new Conditions(inGiantRatArea, poisonedCheese1, poisonedHole1, poisonedHole2), useCheeseOnHole3);
		goPoisonRats.addStep(new Conditions(inGiantRatArea, poisonedCheese1, poisonedHole1), useCheeseOnHole2);
		goPoisonRats.addStep(new Conditions(inGiantRatArea, poisonedCheese1), useCheeseOnHole1);
		goPoisonRats.addStep(new Conditions(inGiantRatArea), useRatPoisonOnCheese);
		steps.put(50, goPoisonRats);

		ConditionalStep goToJackAfterPoisoning = new ConditionalStep(this, talkToJackAfterCheese);
		goToJackAfterPoisoning.addStep(inGiantRatArea, goDownToJack);
		steps.put(55, goToJackAfterPoisoning);
		steps.put(57, goToJackAfterPoisoning);
		steps.put(60, talkToApoth);
		steps.put(65, talkToApoth);
		steps.put(70, talkToJackAfterApoth);
		steps.put(75, talkToJackAfterCure);

		ConditionalStep goKillBigRat = new ConditionalStep(this, climbJackLadderAgain);
		goKillBigRat.addStep(inGiantRatArea, useCatOnHole);
		steps.put(80, goKillBigRat);

		ConditionalStep goTalkToJackAfterFight = new ConditionalStep(this, talkToJackAfterFight);
		goTalkToJackAfterFight.addStep(inGiantRatArea, goDownToJackAfterFight);
		steps.put(85, goTalkToJackAfterFight);

		ConditionalStep goTalkToJoe = new ConditionalStep(this, travelToKeldagrim);
		goTalkToJoe.addStep(inKelgdagrim, talkToSmokinJoe);
		steps.put(90, goTalkToJoe);

		ConditionalStep goSmokeHole = new ConditionalStep(this, travelToKeldagrim);
		goSmokeHole.addStep(new Conditions(inKelgdagrim, smoulderingPot, catSeenFailure), usePotOnHoleAgain);
		goSmokeHole.addStep(new Conditions(inKelgdagrim, smoulderingPot), usePotOnHole);
		goSmokeHole.addStep(new Conditions(inKelgdagrim), lightWeeds);
		steps.put(95, goSmokeHole);

		ConditionalStep goTalkToJoeAgain = new ConditionalStep(this, travelToKeldagrim);
		goTalkToJoeAgain.addStep(inKelgdagrim, talkToJoeAgain);
		steps.put(100, goTalkToJoeAgain);

		ConditionalStep goTalkToFelk = new ConditionalStep(this, enterSarimRatPits);
		goTalkToFelk.addStep(inRatPit, talkToFelkrash);
		steps.put(105, goTalkToFelk);

		ConditionalStep goTalkToFace = new ConditionalStep(this, talkToTheFaceAgain);
		goTalkToFace.addStep(inRatPit, leaveSarimRatPits);
		steps.put(110, goTalkToFace);

		steps.put(115, useCoinOnPot);

		ConditionalStep goKillRats = new ConditionalStep(this, returnToSarim);
		goKillRats.addStep(new Conditions(inPortSarim, inPlayWidget), playSnakeCharm);
		goKillRats.addStep(inPortSarim, clickSnakeCharm);
		steps.put(120, goKillRats);

		ConditionalStep goFinishQuest = new ConditionalStep(this, enterPitsForEnd);
		goFinishQuest.addStep(inRatPit, talkToFelkrashForEnd);
		steps.put(125, goFinishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		cat = new FollowerItemRequirement("A non-overgrown cat",
			ItemCollections.HUNTING_CATS,
			NpcCollections.getHuntingCats()).isNotConsumed();

		catFollower = new FollowerRequirement("A non-overgrown cat following you", NpcCollections.getHuntingCats());
		ratPoison = new ItemRequirement("Rat poison", ItemID.RAT_POISON);
		ratPoison.canBeObtainedDuringQuest();
		cheese = new ItemRequirement("Cheese", ItemID.CHEESE);
		cheese.addAlternates(ItemID.RATCATCHERS_POISONEDCHEESE);
		marrentill = new ItemRequirement("Marrentill", ItemID.MARENTILL);
		unicornHornDust = new ItemRequirement("Unicorn horn dust", ItemID.UNICORN_HORN_DUST);
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK);
		// TODO: Add DS2 as part of check
		catspeakAmuletOrDS2 = new ItemRequirement("Catspeak amulet", ItemID.ICS_LITTLE_AMULET_OF_CATSPEAK);
		catspeakAmuletOrDS2.addAlternates(ItemID.TWOCATS_AMULETOFCATSPEAK);
		potOfWeeds = new ItemRequirement("Pot of weeds", ItemID.RATCATCHERS_WEEDPOT);
		potOfWeeds.setTooltip("You can make this by using some weeds on a pot");
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		coins101 = new ItemRequirement("Coins", ItemCollections.COINS, 101);
		coin = new ItemRequirement("Coins", ItemCollections.COINS);
		kwuarm = new ItemRequirement("Clean kwuarm", ItemID.KWUARM);
		redEggs = new ItemRequirement("Red spiders' eggs", ItemID.RED_SPIDERS_EGGS);
		vial = new ItemRequirement("Empty vial", ItemID.VIAL_EMPTY);
		snakeCharm = new ItemRequirement("Snake charm", ItemID.SNAKE_FLUTE).isNotConsumed();
		snakeCharm.canBeObtainedDuringQuest();
		fish8 = new ItemRequirement("Fish or more, raw or cooked", ItemCollections.FISH_FOOD, 8);
		fish8.addAlternates(ItemCollections.RAW_FISH);

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		sarimTeleport = new ItemRequirement("Port Sarim teleport", ItemCollections.AMULET_OF_GLORIES);
		sarimTeleport.addAlternates(ItemID.TELETAB_DRAYNOR);
		pollnivneachTeleport = new ItemRequirement("Pollnivneach teleport", ItemID.NZONE_TELETAB_POLLNIVNEACH);
		ardougneTeleport = new ItemRequirement("Ardougne teleport", ItemID.POH_TABLET_ARDOUGNETELEPORT);
		ardougneTeleport.addAlternates(ItemID.ARDY_CAPE_EASY, ItemID.ARDY_CAPE_MEDIUM, ItemID.ARDY_CAPE_HARD,
			ItemID.ARDY_CAPE_ELITE);
		keldagrimTeleport = new ItemRequirement("Mine cart access to Keldagrim from the GE", -1, -1);
		keldagrimTeleport.setDisplayItemId(ItemID.DWARF_MINECART_TICKET_KELDA_ICE);
		carpetCoins = new ItemRequirement("Coins for magic carpet travel", ItemCollections.COINS, 1000);

		directions = new ItemRequirement("Directions", ItemID.RATCATCHERS_PARTY_DIRECTIONS);

		poisonedCheese1 = new ItemRequirement("Poisoned cheese", ItemID.RATCATCHERS_POISONEDCHEESE);
		poisonedCheese2 = new ItemRequirement("Poisoned cheese", ItemID.RATCATCHERS_POISONEDCHEESE, 2);
		poisonedCheese3 = new ItemRequirement("Poisoned cheese", ItemID.RATCATCHERS_POISONEDCHEESE, 3);
		poisonedCheese4 = new ItemRequirement("Poisoned cheese", ItemID.RATCATCHERS_POISONEDCHEESE, 4);
		smoulderingPot = new ItemRequirement("Smouldering pot", ItemID.RATCATCHERS_SMOKEY_WEEDPOT);
		catantipoison = new ItemRequirement("Cat antipoison", ItemID.RATCATCHERS_CAT_ANTIPOISON);
		catantipoison.setTooltip("You can get another from the Apothecary");
		musicScroll = new ItemRequirement("Music scroll", ItemID.RATCATCHERS_MUSIC);
		musicScroll.setTooltip("You can get another from the snake charmer");
	}

	@Override
	protected void setupZones()
	{
		varrockSewer = new Zone(new WorldPoint(3151, 9855, 0), new WorldPoint(3290, 9919, 0));
		mansionGrounds = new Zone(new WorldPoint(2821, 5061, 0), new WorldPoint(2874, 5120, 0));
		mansionF0 = new Zone(new WorldPoint(2831, 5085, 0), new WorldPoint(2864, 5101, 0));
		mansionF1 = new Zone(new WorldPoint(2829, 5083, 1), new WorldPoint(2869, 5108, 2));
		giantRatArea = new Zone(new WorldPoint(3263, 3375, 1), new WorldPoint(3274, 3386, 2));
		keldagrim = new Zone(new WorldPoint(2816, 10112, 0), new WorldPoint(2950, 10239, 3));
		ratPit = new Zone(new WorldPoint(2945, 9622, 0), new WorldPoint(3005, 9680, 0));
		portSarim = new Zone(new WorldPoint(3019, 3232, 0));
	}

	public void setupConditions()
	{
		inVarrockSewer = new ZoneRequirement(varrockSewer);
		inMansionGrounds = new ZoneRequirement(mansionGrounds);
		inMansionF0 = new ZoneRequirement(mansionF0);
		inMansionF1 = new ZoneRequirement(mansionF1);
		inGiantRatArea = new ZoneRequirement(giantRatArea);
		inKelgdagrim = new ZoneRequirement(keldagrim);
		inRatPit = new ZoneRequirement(ratPit);
		inPortSarim = new ZoneRequirement(portSarim);

		// 1423 0->8 for rats caught
		//

		// caught ne,
		// 1423 = 1,
		// 1424 = 1

		caughtRat1 = new VarbitRequirement(1424, 1);
		caughtRat2And3 = new Conditions(
			new VarbitRequirement(1425, 1),
			new VarbitRequirement(1426, 1)
		);

		poisonedHole1 = new VarbitRequirement(1406, 1);
		poisonedHole2 = new VarbitRequirement(1407, 1);
		poisonedHole3 = new VarbitRequirement(1408, 1);
		poisonedHole4 = new VarbitRequirement(1409, 1);

		catSeenFailure = new VarbitRequirement(1410, 1);
		// 1422, cat's told you how to get kelda rats

		inPlayWidget = new WidgetTextRequirement(282, 20, "PLAY");
	}

	public void setupSteps()
	{
		talkToGertrude = new NpcStep(this, NpcID.GERTRUDE_POST, new WorldPoint(3151, 3413, 0),
			"Talk to Gertrude west of Varrock.", catspeakAmuletOrDS2.equipped());
		talkToGertrude.addDialogStep("Yes.");

		enterSewer = new ObjectStep(this, ObjectID.MANHOLEOPEN, new WorldPoint(3237, 3458, 0),
			"Go down into Varrock Sewer via the Manhole south east of Varrock Castle.", cat);
		((ObjectStep) enterSewer).addAlternateObjects(ObjectID.MANHOLECLOSED);

		talkToPhingspet = new NpcStep(this, NpcID.VC_PHINGSPET, new WorldPoint(3243, 9867, 0),
			"Talk to Phingspet in Varrock Sewer.", cat);

		catch8Rats = new NpcStep(this, NpcID.PITRAT_SARIM_DEF, new WorldPoint(3243, 9867, 0),
			"Have your cat catch 8 rats.", true, catFollower);
		((NpcStep) catch8Rats).setHideWorldArrow(true);

		talkToPhingspetAgain = new NpcStep(this, NpcID.VC_PHINGSPET, new WorldPoint(3243, 9867, 0),
			"Talk to Phingspet in Varrock Sewer again.");

		talkToJimmy = new NpcStep(this, NpcID.VC_JIMMY_DAZZLER, new WorldPoint(2563, 3320, 0),
			"Talk to Jimmy Dazzler north of Ardougne Castle.", cat);
		readDirections = new DetailedQuestStep(this, "Read the directions.", cat, directions.highlighted());
		readDirections.addDialogStep("Follow the directions to the house.");
		climbTrellis = new ObjectStep(this, ObjectID.VC_TRELLIS_BASE, new WorldPoint(2844, 5105, 0),
			"Climb the trellis around the back of the mansion, avoiding the guards. You may need to deviate from the " +
				"marked path depending on what the guards are doing.");
		((ObjectStep) climbTrellis).setLinePoints(Arrays.asList(
			new WorldPoint(2847, 5066, 0),
			new WorldPoint(2840, 5066, 0),
			new WorldPoint(2840, 5075, 0),
			new WorldPoint(2826, 5075, 0),
			new WorldPoint(2826, 5082, 0),
			new WorldPoint(2824, 5084, 0),
			new WorldPoint(2824, 5088, 0),
			new WorldPoint(2826, 5090, 0),
			new WorldPoint(2826, 5093, 0),
			new WorldPoint(2824, 5095, 0),
			new WorldPoint(2824, 5099, 0),
			new WorldPoint(2826, 5101, 0),
			new WorldPoint(2826, 5111, 0),
			new WorldPoint(2835, 5112, 0),
			new WorldPoint(2837, 5112, 0),
			new WorldPoint(2837, 5111, 0),
			new WorldPoint(2844, 5105, 0)
		));
		climbTrellisNoPath = new ObjectStep(this, ObjectID.VC_TRELLIS_BASE, new WorldPoint(2844, 5105, 0),
			"Climb the trellis.");
		climbTrellis.addSubSteps(climbTrellisNoPath);
		catchRat1 = new NpcStep(this, NpcID.VC_PARTY_RAT, new WorldPoint(2835, 5098, 1),
			"Catch the rat in the north west room with your cat.", catFollower);
		catchRat1.addTileMarker(new WorldPoint(2841, 5104, 1), SpriteID.EQUIPMENT_SLOT_SHIELD);
		catchRat1.setMaxRoamRange(7);
		catchRat2And3 = new NpcStep(this, NpcID.VC_PARTY_RAT, new WorldPoint(2859, 5091, 1),
			"Hide in the north east room until it's safe to go to the south east room, then catch the rats there.",
			true);
		catchRat2And3.addTileMarker(new WorldPoint(2857, 5098, 1), SpriteID.EQUIPMENT_SLOT_SHIELD);
		climbDownLadderInMansion = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2862, 5092, 1),
			"Climb down the ladder.");
		catchRemainingRats = new NpcStep(this, NpcID.VC_PARTY_RAT, new WorldPoint(2860, 5093, 0),
			"Catch the 3 rats down here.", true, catFollower);
		talkToJimmyAgain = new NpcStep(this, NpcID.VC_JIMMY_DAZZLER, new WorldPoint(2563, 3320, 0),
			"Talk to Jimmy Dazzler again.", cat);

		talkToJack = new NpcStep(this, NpcID.VC_HOOKNOSED_JACK, new WorldPoint(3268, 3401, 0),
			"Talk to Hooknosed Jack in south east Varrock.  You can give Jack your vial, kwuarm, and red spiders' eggs" + 
			" for the rat poison", cat, kwuarm, vial, redEggs);
		climbJackLadder = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER, new WorldPoint(3268, 3379, 0),
			"Climb up the ladder south of Jack.", cheese.quantity(4), ratPoison);
		useRatPoisonOnCheese = new DetailedQuestStep(this, "Add rat poison to your cheese.",
			ratPoison.highlighted(), cheese.highlighted());
		useCheeseOnHole1 = new ObjectStep(this, ObjectID.RATCATCHERS_RATHOLE1, new WorldPoint(3265, 3378, 1),
			"Use a poisoned cheese on the rat holes.", poisonedCheese1.highlighted());
		useCheeseOnHole2 = new ObjectStep(this, ObjectID.RATCATCHERS_RATHOLE2, new WorldPoint(3273, 3377, 1),
			"Use a poisoned cheese on the rat holes.", poisonedCheese1.highlighted());
		useCheeseOnHole3 = new ObjectStep(this, ObjectID.RATCATCHERS_RATHOLE3, new WorldPoint(3273, 3381, 1),
			"Use a poisoned cheese on the rat holes.", poisonedCheese1.highlighted());
		useCheeseOnHole4 = new ObjectStep(this, ObjectID.RATCATCHERS_RATHOLE4, new WorldPoint(3271, 3384, 1),
			"Use a poisoned cheese on the rat holes.", poisonedCheese1.highlighted());
		useCheeseOnHole1.addSubSteps(useCheeseOnHole2, useCheeseOnHole3, useCheeseOnHole4);

		goDownToJack = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDERTOP, new WorldPoint(3268, 3379, 1),
			"Return to Jack.");
		talkToJackAfterCheese = new NpcStep(this, NpcID.VC_HOOKNOSED_JACK, new WorldPoint(3268, 3401, 0),
			"Return to Jack.", cat);
		talkToJackAfterCheese.addSubSteps(goDownToJack);

		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3196, 3404, 0),
			"Talk to the Apothecary in west Varrock.", bucketOfMilk, unicornHornDust, marrentill);
		talkToApoth.addDialogSteps("Talk about something else.", "Talk about the Ratcatchers Quest.");
		talkToJackAfterApoth = new NpcStep(this, NpcID.VC_HOOKNOSED_JACK, new WorldPoint(3268, 3401, 0),
			"Talk to Jack again.", catantipoison, cat);
		talkToJackAfterCure = new NpcStep(this, NpcID.VC_HOOKNOSED_JACK, new WorldPoint(3268, 3401, 0),
			"Talk to Jack again.", catantipoison, cat);
		talkToJackAfterApoth.addSubSteps(talkToJackAfterCure);

		climbJackLadderAgain = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER, new WorldPoint(3268, 3379, 0),
			"Climb up the ladder south of Jack.", cat, fish8);
		useCatOnHole = new ObjectStep(this, ObjectID.VC_BLANK_WALLDECOR, new WorldPoint(3270, 3379, 1),
			"Use your cat on the hole in the wall. You'll need to feed it by using fish ON THE WALL whenever its " +
				"health gets low.", cat.highlighted(), fish8);
		useCatOnHole.addDialogSteps("Yes", "Be careful in there, cat!");
		feedCatAsItFights = new ObjectStep(this, ObjectID.VC_BLANK_WALLDECOR, new WorldPoint(3270, 3379, 1),
			"Use fish on the wall whenever your cat's health gets low.", fish8.highlighted());
		goDownToJackAfterFight = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDERTOP, new WorldPoint(3268, 3379, 1),
			"Return to Jack.");
		talkToJackAfterFight = new NpcStep(this, NpcID.VC_HOOKNOSED_JACK, new WorldPoint(3268, 3401, 0),
			"Return to Jack.");
		talkToJackAfterFight.addSubSteps(goDownToJackAfterFight);

		travelToKeldagrim = new ObjectStep(this, ObjectID.GE_KELDAGRIM_TRAPDOOR, new WorldPoint(3140, 3504, 0),
			"Travel to Keldagrim.", cat, potOfWeeds, tinderbox);
		talkToSmokinJoe = new NpcStep(this, NpcID.VC_SMOKIN_JOE, new WorldPoint(2929, 10213, 0),
			"Talk to Smokin' Joe in the north east of Keldagrim.", cat, potOfWeeds, tinderbox);
		lightWeeds = new DetailedQuestStep(this, "Use a tinderbox on the pot of weeds.", potOfWeeds.highlighted(),
			tinderbox.highlighted());
		usePotOnHole = new ObjectStep(this, ObjectID.RATCATCHERS_RATHOLE5, new WorldPoint(2933, 10212, 0),
			"Use the smouldering pot on the hole east of Joe with your cat following you and your catspeak amulet equipped.",
			smoulderingPot.highlighted(), catFollower, catspeakAmuletOrDS2.equipped());
		usePotOnHole.addIcon(ItemID.RATCATCHERS_SMOKEY_WEEDPOT);
		usePotOnHoleAgain = new ObjectStep(this, ObjectID.RATCATCHERS_RATHOLE5, new WorldPoint(2933, 10212, 0),
			"Use the smouldering pot on the rat hole again.", smoulderingPot.highlighted(), catFollower, catspeakAmuletOrDS2.equipped());
		usePotOnHoleAgain.addIcon(ItemID.RATCATCHERS_SMOKEY_WEEDPOT);
		talkToJoeAgain = new NpcStep(this, NpcID.VC_SMOKIN_JOE, new WorldPoint(2929, 10213, 0),
			"Talk to Smokin' Joe again.");

		enterSarimRatPits = new ObjectStep(this, ObjectID.VC_MANHOLE_OPEN, new WorldPoint(3018, 3232, 0),
			"Go down the manhole near The Face.");
		talkToFelkrash = new NpcStep(this, NpcID.VC_FELKRASH_THE_BARD, new WorldPoint(2978, 9640, 0),
			"Talk to Felkrash in the Port Sarim Rat Pits.");
		leaveSarimRatPits = new ObjectStep(this, ObjectID.VC_LADDER, new WorldPoint(2962, 9651, 0),
			"Leave the rat pits.");
		talkToTheFaceAgain = new NpcStep(this, NpcID.VC_FACE, new WorldPoint(3019, 3232, 0),
			"Talk to The Face in Port Sarim again.", cat, catspeakAmuletOrDS2);
		talkToTheFaceAgain.addDialogStep(3, "I just don't think Felkrash was that impressive.");

		useCoinOnPot = new ObjectStep(this, ObjectID.FEUD_MONEY_BOWL, new WorldPoint(3355, 2953, 0),
		"Use a coin on the pot next to the Snake Charmer in Pollnivneach.", coin.quantity(101).highlighted());
		useCoinOnPot.addDialogSteps("I want to talk to you about animal charming.", "What if I offered you some money?",
			"Forget about it. I don't care that much.", "Walk away slowly", "Stop");
		useCoinOnPot.addIcon(ItemID.COINS);
		returnToSarim = new DetailedQuestStep(this, new WorldPoint(3019, 3232, 0), "Return to just outside the Port " +
			"Sarim Rat Pits.", snakeCharm, musicScroll);
		clickSnakeCharm = new DetailedQuestStep(this, "Click your snake charm and play it.", snakeCharm.highlighted());

		playSnakeCharm = new RatCharming(this);

		enterPitsForEnd = new ObjectStep(this, ObjectID.VC_MANHOLE_OPEN, new WorldPoint(3018, 3232, 0),
			"Return to Felkrash to finish.");
		talkToFelkrashForEnd = new NpcStep(this, NpcID.VC_FELKRASH_THE_BARD, new WorldPoint(2978, 9640, 0),
			"Return to Felkrash to finish.");
		talkToFelkrashForEnd.addSubSteps(enterPitsForEnd);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(cat, cheese.quantity(4), marrentill, unicornHornDust, bucketOfMilk,
			catspeakAmuletOrDS2, vial, kwuarm, redEggs,
			potOfWeeds, tinderbox, coins101, snakeCharm, fish8);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ratPoison, varrockTeleport, sarimTeleport, pollnivneachTeleport, ardougneTeleport, keldagrimTeleport,
			carpetCoins);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER, QuestState.FINISHED));
		req.add(new VarbitRequirement(QuestVarbits.QUEST_THE_GIANT_DWARF.getId(), Operation.GREATER_EQUAL,
			1, "Started The Giant Dwarf"));
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
		return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 4500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Rat Pole", ItemID.VC_RAT_POLE, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Rat Pits"),
				new UnlockReward("Ability to train Overgrown Cats into Wiley and Lazy Cats"));
	}
	
	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("Alternatively, rat poison could be obtained in the basement of the Clocktower south of East" +
				" Ardougne. If you already have the rat poison it follows that you would not need the vial, red spiders' eggs, or kwuarm.");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToGertrude, enterSewer, talkToPhingspet, catch8Rats, talkToPhingspetAgain), cat));
		allSteps.add(new PanelDetails("Helping Jimmy", Arrays.asList(talkToJimmy, readDirections, climbTrellis,
			catchRat1, catchRat2And3, climbDownLadderInMansion, catchRemainingRats, talkToJimmyAgain), cat));
		allSteps.add(new PanelDetails("Helping Jack",
			Arrays.asList(talkToJack, useRatPoisonOnCheese, climbJackLadder, useCheeseOnHole1, talkToJackAfterCheese,
				talkToApoth, talkToJackAfterApoth, climbJackLadderAgain, useCatOnHole, feedCatAsItFights, talkToJackAfterFight),
			cheese.quantity(4), bucketOfMilk, marrentill, unicornHornDust, cat, fish8, vial, kwuarm, redEggs, ratPoison));
		allSteps.add(new PanelDetails("Helping Joe", Arrays.asList(travelToKeldagrim, talkToSmokinJoe, usePotOnHole,
			usePotOnHoleAgain, talkToJoeAgain), cat, catspeakAmuletOrDS2, potOfWeeds, tinderbox));

		allSteps.add(new PanelDetails("Helping Felkrash", Arrays.asList(enterSarimRatPits,
			talkToFelkrash, leaveSarimRatPits, talkToTheFaceAgain, useCoinOnPot, returnToSarim,
			clickSnakeCharm, talkToFelkrashForEnd), snakeCharm, coins101));
		return allSteps;
	}
}
