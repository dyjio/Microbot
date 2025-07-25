/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.runelite.client.plugins.microbot.questhelper.requirements.player;

import net.runelite.client.plugins.microbot.questhelper.QuestHelperConfig;
import net.runelite.client.plugins.microbot.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.microbot.questhelper.requirements.AbstractRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;

/**
 * Requirement that checks if a player meets a certain skill level.
 */
@Getter
@Slf4j
public class SkillRequirement extends AbstractRequirement
{
	private final Skill skill;
	private final int requiredLevel;
	private final Operation operation;
	private boolean canBeBoosted;
	private String displayText;
	private QuestHelperPlugin questHelperPlugin;

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 * @param operation what type of check we're making on the stat
	 */
	public SkillRequirement(Skill skill, int requiredLevel, Operation operation)
	{
		assert(skill != null);
		this.skill = skill;
		this.requiredLevel = requiredLevel;
		this.displayText = getDisplayText();
		this.operation = operation;
		shouldCountForFilter = true;
	}

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 */
	public SkillRequirement(Skill skill, int requiredLevel)
	{
		this(skill, requiredLevel, Operation.GREATER_EQUAL);
	}

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 * @param canBeBoosted if the skill can be boosted to meet this requirement
	 */
	public SkillRequirement(Skill skill, int requiredLevel, boolean canBeBoosted)
	{
		this(skill, requiredLevel);
		this.canBeBoosted = canBeBoosted;
	}

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 * @param canBeBoosted if this skill check can be boosted to meet this requirement
	 * @param displayText the display text
	 */
	public SkillRequirement(Skill skill, int requiredLevel, boolean canBeBoosted, String displayText)
	{
		this(skill, requiredLevel, canBeBoosted);
		this.displayText = displayText;
	}

	@Override
	public boolean check(Client client)
	{
		int skillLevel = canBeBoosted ? Math.max(client.getBoostedSkillLevel(skill), client.getRealSkillLevel(skill)) :
			client.getRealSkillLevel(skill);
		return skillLevel >= requiredLevel;
	}

	/**
	 * Same as check, but takes the highest possible boost into consideration and returns whether
	 * the requirement could be passed with a boost or not
	 */
	BoostStatus checkBoosted(Client client, QuestHelperConfig config)
	{

		if (canBeBoosted)
		{
			var realLevel = client.getRealSkillLevel(skill);
			var boostedLevel = Math.max(client.getBoostedSkillLevel(skill), realLevel);
			if (boostedLevel >= requiredLevel)
			{
				// User passes the requirement either based on their boost or their real skill level
				return BoostStatus.Pass;
			}

			// Find boosts for this skill
			var skillName = skill.getName();
			var oSelectedBoost = Arrays.stream(Boosts.values()).filter(b -> skillName.equals(b.getName())).findAny();
			if (oSelectedBoost.isEmpty())
			{
				log.warn("No boosts found for {}", skillName);
				return BoostStatus.Fail;
			}
			var selectedBoost = oSelectedBoost.get();


			var highestBoost = selectedBoost.getHighestBoost(config.stewBoosts(), realLevel);

			if (realLevel + highestBoost >= requiredLevel)
			{
				return BoostStatus.CanPassWithBoost;
			}
		}
		else
		{
			if (check(client))
			{
				return BoostStatus.Pass;
			}
			else
			{
				return BoostStatus.Fail;
			}
		}

		return BoostStatus.Fail;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		String returnText;
		if (displayText != null)
		{
			returnText = displayText;
		}
		else
		{
			returnText = requiredLevel + " " + skill.getName();
		}

		if (canBeBoosted)
		{
			returnText += " (boostable)";
		}

		return returnText;
	}

	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		switch (checkBoosted(client, config))
		{
			case Pass:
				return config.passColour();
			case CanPassWithBoost:
				return config.boostColour();
			case Fail:
				return config.failColour();
		}
		return config.failColour();
	}

	enum BoostStatus
	{
		Pass,
		CanPassWithBoost,
		Fail,
	}
}
