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
package net.runelite.client.plugins.microbot.questhelper.helpers.miniquests.themagearenai;

import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TheMageArenaI extends BasicQuestHelper
{
	ItemRequirement runesForCasts, knife, godCape;

	Requirement inCavern, inStatuesRoom;

	QuestStep enterCavern, talkToKolodion, fightKolodion, enterCavernForPool, enterPool, prayStatue, talkToGuardian;

	Zone cavern, statuesRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goTalkToKolodion = new ConditionalStep(this, enterCavern);
		goTalkToKolodion.addStep(inCavern, talkToKolodion);
		steps.put(0, goTalkToKolodion);

		steps.put(1, fightKolodion);
		steps.put(2, fightKolodion);
		steps.put(3, fightKolodion);
		steps.put(4, fightKolodion);
		steps.put(5, fightKolodion);

		ConditionalStep goGetStaff = new ConditionalStep(this, enterCavernForPool);
		goGetStaff.addStep(new Conditions(inStatuesRoom, godCape), talkToGuardian);
		goGetStaff.addStep(new Conditions(inStatuesRoom), prayStatue);
		goGetStaff.addStep(inCavern, enterPool);
		steps.put(6, goGetStaff);
		steps.put(7, goGetStaff);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		runesForCasts = new ItemRequirement("Runes for fighting Kolodion", -1, -1);
		runesForCasts.setDisplayItemId(ItemID.DEATHRUNE);
		knife = new ItemRequirement("Knife or sharp weapon to cut through a web", ItemID.KNIFE).isNotConsumed();
		godCape = new ItemRequirement("God cape", ItemID.ZAMORAK_CAPE).isNotConsumed();
		godCape.addAlternates(ItemID.GUTHIX_CAPE, ItemID.SARADOMIN_CAPE);
	}

	@Override
	protected void setupZones()
	{
		cavern = new Zone(new WorldPoint(2529, 4709, 0), new WorldPoint(2550, 4725, 0));
		statuesRoom = new Zone(new WorldPoint(2486, 4683, 0), new WorldPoint(2526, 4736, 0));
	}

	public void setupConditions()
	{
		inCavern = new ZoneRequirement(cavern);
		inStatuesRoom = new ZoneRequirement(statuesRoom);
	}

	public void setupSteps()
	{
		enterCavern = new ObjectStep(this, ObjectID.MAGEARENA_LEVER_TO_CELLAR, new WorldPoint(3090, 3956, 0), "Pull the lever in the" +
			" building north of the Mage Arena. This is IN THE WILDERNESS, so don't bring anything you don't want to " +
			"lose.", knife);
		talkToKolodion = new NpcStep(this, NpcID.ARENAMAGE1, new WorldPoint(2539, 4716, 0), "Talk to Kolodion, ready " +
			"for fighting him in the Mage Arena.", runesForCasts);
		talkToKolodion.addDialogSteps("Can I fight here?", "Yes indeedy.", "Okay, let's fight.");

		fightKolodion = new NpcStep(this, NpcID.ARENAMAGE2, new WorldPoint(3105, 3934, 0), "Defeat Kolodion's " +
			"various forms.");
		((NpcStep) fightKolodion).addAlternateNpcs(NpcID.KOLHUMAN, NpcID.KOLOGRE, NpcID.KOLSPIDER,
			NpcID.KOLETHEREAL, NpcID.KOLDEMON);

		enterCavernForPool = new ObjectStep(this, ObjectID.MAGEARENA_LEVER_TO_CELLAR, new WorldPoint(3090, 3956, 0), "Pull the lever in the" +
			" building north of the Mage Arena. This is IN THE WILDERNESS, so don't bring anything you don't want to " +
			"lose.", knife);

		enterPool = new ObjectStep(this, ObjectID.MAGEARENA_WATERPORTAL1, new WorldPoint(2542, 4720, 0), "Enter the sparkling" +
			" pool.");
		enterPool.addSubSteps(enterCavernForPool);

		prayStatue = new DetailedQuestStep(this, new WorldPoint(2507, 4720, 0), "Pray at the statue of the god whose " +
			"cape you want. Pick up the cape which appears.");

		talkToGuardian = new NpcStep(this, NpcID.MAGEARENA_GUARDIAN, new WorldPoint(2508, 4695, 0), "Talk to the " +
			"Chamber Guardian for your staff.", godCape);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(knife, runesForCasts);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kolodion in 5 forms, up to level 112");
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("This miniquest is in deep Wilderness. Don't bring anything you're not " +
			"willing to risk! It's recommended to turn off player attack options to avoid potentially getting " +
			"skulled.");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.MAGIC, 60));
		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("A God Staff", ItemID.ZAMORAK_STAFF, 1),
				new ItemReward("A God Cape", ItemID.ZAMORAK_CAPE, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to unlock 3 new God Spells."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Getting to the Mage Arena", Collections.singletonList(enterCavern), knife));
		allSteps.add(new PanelDetails("Defeating Kolodion", Arrays.asList(talkToKolodion, fightKolodion),
			runesForCasts));

		allSteps.add(new PanelDetails("Getting your rewards",
			Arrays.asList(enterPool, prayStatue, talkToGuardian)));
		return allSteps;
	}
}
