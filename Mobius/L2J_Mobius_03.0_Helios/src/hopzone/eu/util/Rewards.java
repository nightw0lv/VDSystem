/*
 * Copyright (c) 2023 DenArt Designs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package hopzone.eu.util;

import hopzone.eu.Configurations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * VDS Stands for: Vote Donation System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.5
 * Pack Support: Mobius 03.0 Helios
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * How to install https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z
 */
public enum Rewards
{
	ITOPZ_INDIVIDUAL_REWARDS(Configurations.ITOPZ_INDIVIDUAL_REWARDS),
	ITOPZ_GLOBAL_REWARDS(Configurations.ITOPZ_GLOBAL_REWARDS),
	HOPZONE_INDIVIDUAL_REWARDS(Configurations.HOPZONE_INDIVIDUAL_REWARDS),
	HOPZONE_GLOBAL_REWARDS(Configurations.HOPZONE_GLOBAL_REWARDS),
	L2TOPGAMESERVER_INDIVIDUAL_REWARDS(Configurations.L2TOPGAMESERVER_INDIVIDUAL_REWARDS),
	L2TOPGAMESERVER_GLOBAL_REWARDS(Configurations.L2TOPGAMESERVER_GLOBAL_REWARDS),
	L2NETWORK_INDIVIDUAL_REWARDS(Configurations.L2NETWORK_INDIVIDUAL_REWARDS),
	L2NETWORK_GLOBAL_REWARDS(Configurations.L2NETWORK_GLOBAL_REWARDS),
	L2JBRASIL_INDIVIDUAL_REWARDS(Configurations.L2JBRASIL_INDIVIDUAL_REWARDS),
	L2JBRASIL_GLOBAL_REWARDS(Configurations.L2JBRASIL_GLOBAL_REWARDS),
	L2TOPSERVERS_INDIVIDUAL_REWARDS(Configurations.L2TOPSERVERS_INDIVIDUAL_REWARDS),
	L2TOPSERVERS_GLOBAL_REWARDS(Configurations.L2TOPSERVERS_GLOBAL_REWARDS),
	L2VOTES_INDIVIDUAL_REWARDS(Configurations.L2VOTES_INDIVIDUAL_REWARDS),
	L2VOTES_GLOBAL_REWARDS(Configurations.L2VOTES_GLOBAL_REWARDS);

	private final Map<Integer, List<Long[]>> _list;
	private static final Rewards[] _rewards = values();

	Rewards(final Map<Integer, List<Long[]>> text)
	{
		_list = text;
	}

	public Set<Integer> keys()
	{
		return _list.keySet();
	}

	public Long[] get(int id)
	{
		return _list.get(id).get(0);
	}

	/**
	 * returns enum from string
	 *
	 * @param TOPSITE string
	 * @return Individual
	 */
	public static Rewards from(String TOPSITE)
	{
		return Arrays.stream(_rewards).filter(s -> s.name().equalsIgnoreCase(TOPSITE)).findFirst().orElse(ITOPZ_INDIVIDUAL_REWARDS);
	}
}
