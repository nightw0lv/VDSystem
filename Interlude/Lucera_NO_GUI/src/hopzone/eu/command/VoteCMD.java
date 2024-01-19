/*
 * Copyright (c) 2024 DenArt Designs
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
package hopzone.eu.command;

import hopzone.eu.Configurations;
import hopzone.eu.model.IndividualResponse;
import hopzone.eu.util.*;
import hopzone.eu.util.Random;
import hopzone.eu.vote.VDSystem;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.*;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * VDS Stands for: Vote Donation System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.8
 * Pack Support: Lucera NO GUI
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * Quick Guide: https://github.com/nightw0lv/VDSystem/tree/master/Guide
 */
public class VoteCMD extends Functions implements IVoicedCommandHandler
{
	// local variables
	private String _IPAddress;
	
	// 12 hour reuse
	private static final Duration VOTE_REUSE = Duration.ofHours(12);
	
	// vote site list
	public static enum VoteSite
	{
		VOTE,
		HOPZONE,
		ITOPZ,
		HOPZONENET,
		L2TOPGAMESERVER,
		L2NETWORK,
		L2JBRASIL,
		HOTSERVERS,
		L2VOTES,
		L2RANKZONE,
		TOP4TEAMBR,
	}
	
	// flood protector list
	private static final List<FloodProtectorHolder> FLOOD_PROTECTOR = Collections.synchronizedList(new ArrayList<>());
	
	// returns protector holder
	public FloodProtectorHolder getFloodProtector(final Player player, final VoteSite site)
	{
		return FLOOD_PROTECTOR.stream().filter(s -> s.getSite() == site && (s.getIP().equalsIgnoreCase(player.getIP()) || s.getHWID().equalsIgnoreCase(player.getNetConnection().getHwid()))).findFirst().orElseGet(() ->
		{
			final FloodProtectorHolder holder = new FloodProtectorHolder(site, player);
			FLOOD_PROTECTOR.add(holder);
			return holder;
		});
	}
	
	/**
	 * Protector holder class
	 */
	private static class FloodProtectorHolder
	{
		public static final Duration EXTENSION = Duration.ofSeconds(60);
		
		private final VoteSite _site;
		
		private final String _IP;
		private final String _HWID;
		
		private long _lastAction;
		
		public FloodProtectorHolder(final VoteSite site, final Player player)
		{
			_site = site;
			_IP = player.getIP();
			_HWID = player.getNetConnection().getHwid();
		}
		
		public VoteSite getSite()
		{
			return _site;
		}
		
		public String getIP()
		{
			return _IP;
		}
		
		public String getHWID()
		{
			return _HWID;
		}
		
		public long getLastAction()
		{
			return _lastAction;
		}
		
		public void updateLastAction()
		{
			_lastAction = System.currentTimeMillis() + EXTENSION.toMillis();
		}
	}
	
	// commands
	public final static String[] COMMANDS =
	{
		"vote", "hopzone", "itopz", "hopzonenet", "l2jbrasil", "l2network", "l2topgameserver", "hotservers", "l2votes", "l2rankzone", "top4teambr"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String s1)
	{
		openWindow(player);
		player.sendActionFailed();
		return false;
	}
	
	/**
	 * this function called from html
	 * @param params String[]
	 */
	public void check(String[] params)
	{
		int vote_id = 0;
		String TOPSITE = String.valueOf(params[0]).toUpperCase();
		Player player = this.getSelf();
		
		// check if allowed the individual command to run
		if (TOPSITE.equals("HOPZONE") && !Configurations.HOPZONE_EU_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("ITOPZ") && !Configurations.ITOPZ_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("HOPZONENET") && !Configurations.HOPZONE_NET_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("L2TOPGAMESERVER") && !Configurations.L2TOPGAMESERVER_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("L2NETWORK") && !Configurations.L2NETWORK_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("L2JBRASIL") && !Configurations.L2JBRASIL_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("HOTSERVERS") && !Configurations.HOTSERVERS_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("L2VOTES") && !Configurations.L2VOTES_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("L2RANKZONE") && !Configurations.L2RANKZONE_INDIVIDUAL_REWARD)
			return;
		if (TOPSITE.equals("TOP4TEAMBR") && !Configurations.TOP4TEAMBR_INDIVIDUAL_REWARD)
			return;
		
		// vote info
		if (TOPSITE.equalsIgnoreCase("VOTE"))
		{
			openWindow(player);
			player.sendActionFailed();
			return;
		}
		
		// check topsite for flood actions
		final FloodProtectorHolder holder = getFloodProtector(player, VoteSite.valueOf(TOPSITE));
		if (holder.getLastAction() > System.currentTimeMillis())
		{
			int seconds_remain = (int) (holder.getLastAction() - System.currentTimeMillis()) / 1000;
			sendMsg(player, "You can't use ." + TOPSITE + " command so fast!");
			player.sendMessage("Use the command again in " + seconds_remain + " seconds");
			openWindow(player);
			player.sendActionFailed();
			return;
		}
		holder.updateLastAction();
		
		// generate vote id
		if (TOPSITE.equalsIgnoreCase("HOPZONE"))
			vote_id = generateVoteURL(TOPSITE);
		
		// check player eligibility
		if (!playerChecksFail(player, TOPSITE))
		{
			if (vote_id > 0)
			{
				String VoteURL = "https://hopzone.eu/vote/" + Configurations.HOPZONE_EU_SERVER_ID + "/" + vote_id + "";
				player.sendMessage("-----------------------------------");
				player.sendMessage("You have 1 minute to vote in: ");
				player.sendMessage(VoteURL);
				player.sendPacket(new OpenUrl(VoteURL));
				player.sendMessage("-----------------------------------");
				int time = 60000;
				final int vid = vote_id;
				// wait until player votes in the given url
				VDSThreadPool.schedule(() -> Execute(player, TOPSITE, vid), time);
			}
			
			// check normal IP Address vote
			VDSThreadPool.schedule(() -> Execute(player, TOPSITE, 0), Random.get(1000, 5000));
			player.sendActionFailed();
			return;
		}
		openWindow(player);
		player.sendActionFailed();
	}
	
	/**
	 * Show Vote Window
	 * @param player object
	 */
	private void openWindow(Player player)
	{
		String IPAddress = player.getIP();
		StringBuilder sb = new StringBuilder("<html>");
		sb.append("<head>");
		sb.append("<title>Vote for us!</title>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<center>");
		sb.append("<br><font color=\"cc9900\"><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></font><br>");
		sb.append("<img src=\"L2UI.SquareWhite\" width=\"300\" height=\"1\">");
		sb.append("<br>YOUR IP: " + IPAddress + "<br>");
		sb.append("<table width=300 align=center>");
		sb.append("<tr><td align=\"right\">Topsite</td><td align=\"left\">Action</td></tr>");
		if (Configurations.HOPZONE_EU_INDIVIDUAL_REWARD)
			sb.append("<tr><td>Hopzone</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check hopzone\">Reward</a></td></tr>");
		if (Configurations.ITOPZ_INDIVIDUAL_REWARD)
			sb.append("<tr><td>iToPz</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check itopz\">Reward</a></td></tr>");
		if (Configurations.HOPZONE_NET_INDIVIDUAL_REWARD)
			sb.append("<tr><td>Hopzonenet</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check hopzonenet\">Reward</a></td></tr>");
		if (Configurations.L2TOPGAMESERVER_INDIVIDUAL_REWARD)
			sb.append("<tr><td>L2TOPGAMESERVER</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check l2topgameserver\">Reward</a></td></tr>");
		if (Configurations.L2NETWORK_INDIVIDUAL_REWARD)
			sb.append("<tr><td>L2NETWORK</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check l2network\">Reward</a></td></tr>");
		if (Configurations.L2JBRASIL_INDIVIDUAL_REWARD)
			sb.append("<tr><td>L2JBRASIL</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check l2jbrasil\">Reward</a></td></tr>");
		if (Configurations.HOTSERVERS_INDIVIDUAL_REWARD)
			sb.append("<tr><td>HOTSERVERS</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check hotservers\">Reward</a></td></tr>");
		if (Configurations.L2VOTES_INDIVIDUAL_REWARD)
			sb.append("<tr><td>L2VOTES</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check l2votes\">Reward</a></td></tr>");
		if (Configurations.L2RANKZONE_INDIVIDUAL_REWARD)
			sb.append("<tr><td>L2RANKZONE</td><td align=center width=33%><a action=\"bypass -h scripts_hopzone.eu.command.VoteCMD:check l2rankzone\">Reward</a></td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareWhite\" width=300 height=1>");
		sb.append("<img src=\"Sek.cbui371\" width=\"300\" height=\"1\">");
		sb.append("<font color=\"cc9900\"><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></font><br1>");
		sb.append("<img src=l2ui.bbs_lineage2 height=16 width=80>");
		sb.append("</center>");
		sb.append("</body>");
		sb.append("</html>");
		Functions.show(sb.toString(), player, (NpcInstance) null);
	}
	
	/**
	 * Validate user
	 *
	 * @param player  object
	 * @param TOPSITE string
	 * @return boolean
	 */
	private boolean playerChecksFail(final Player player, final String TOPSITE)
	{
		// check for private network (website will not accept it)
		if (!Configurations.DEBUG && Utilities.localIp(player.getIP()) && !TOPSITE.equals("HOPZONE"))
		{
			sendMsg(player, "Private networks are not allowed.");
			return true;
		}
		
		// check if 12 hours has pass from last vote
		final long voteTimer = Utilities.selectIndividualVar(TOPSITE, "can_vote", Configurations.DEBUG ? Utilities.getMyIP() : player.getIP());
		if (voteTimer > System.currentTimeMillis())
		{
			String dateFormatted = Utilities.formatMillisecond(voteTimer);
			sendMsg(player, "You already voted on " + TOPSITE + " try again after " + dateFormatted + ".");
			return true;
		}
		
		// restrict players from same IP to vote again
		final boolean ipVoted = Utilities.selectIndividualIP(TOPSITE, "can_vote", Configurations.DEBUG ? Utilities.getMyIP() : player.getIP());
		if (ipVoted)
		{
			sendMsg(player, "Someone already voted on " + TOPSITE + " from your IP.");
			return true;
		}
		
		// ignore failures for debug
		if (Configurations.DEBUG)
		{
			_IPAddress = Utilities.getMyIP();
			return false;
		}
		
		_IPAddress = player.getIP();
		return false;
	}
	
	/**
	 * Execute individual response and reward player on success
	 * response url depends on a vote id is given or not
	 *
	 * @param player  object
	 * @param TOPSITE string
	 * @param vote_id int
	 */
	private void Execute(final Player player, final String TOPSITE, final int vote_id)
	{
		if (TOPSITE.equals("VOTE"))
			return;
		IndividualResponse iResponse;
		if (vote_id == 0)
		{
			iResponse = IndividualResponse.OPEN(Url.from(TOPSITE + "_INDIVIDUAL_URL_IP").toString(), _IPAddress);
		}
		else
		{
			iResponse = IndividualResponse.OPEN(Url.from(TOPSITE + "_INDIVIDUAL_URL_VOTE_ID").toString(), vote_id + "");
		}
		// get response from topsite about this ip address
		Optional.ofNullable(iResponse.connect(TOPSITE, VDSystem.VoteType.INDIVIDUAL)).ifPresent(response ->
		{
			// set variables
			final StatsSet set = new StatsSet();
			set.set("response_code", response.getResponseCode());
			set.set("has_voted", response.hasVoted());
			set.set("vote_time", response.getVoteTime());
			set.set("server_time", response.getServerTime());
			set.set("response_error", response.getError());
			
			// player can get reward?
			if (isEligible(player, TOPSITE, set, vote_id))
			{
				sendMsg(player, "Successfully voted in " + TOPSITE + "!" + (Configurations.DEBUG ? "(DEBUG ON)" : ""));
				reward(player, TOPSITE);
				// set can vote: 12 hours (in ms).
				Utilities.saveIndividualVar(TOPSITE, "can_vote", System.currentTimeMillis() + VOTE_REUSE.toMillis(), _IPAddress);
				player.sendActionFailed();
			}
		});
	}
	
	/**
	 * generate unique vote id for player, so he can vote on it
	 * this will prevent players with IPv6 to lose their reward
	 * @implNote not all topsites support this feature
	 *
	 * @param TOPSITE string
	 * @return vote_id int
	 */
	private int generateVoteURL(final String TOPSITE)
	{
		int vote_id = 0;
		try
		{
			// Specify the URL of the remote JSON resource
			String urlString = Url.from(TOPSITE + "_INDIVIDUAL_GENERATE_VOTE").toString();
			
			// Create a URL object from the specified URL string
			URL url = new URL(urlString);
			
			// Open a connection to the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			// Set the request method
			connection.setRequestMethod("GET");
			
			// Get the response code
			int responseCode = connection.getResponseCode();
			
			// Check if the request was successful (HTTP 200)
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				// Create a BufferedReader to read the response
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				// Read the response line by line
				String line;
				StringBuilder response = new StringBuilder();
				while ((line = reader.readLine()) != null)
				{
					response.append(line);
				}
				reader.close();
				
				// Convert the response to a JSON string
				String jsonString = response.toString();
				
				String[] split;
				for (String s : jsonString.replaceAll("[{}\"]", "").replace("result:", "").split(","))
				{
					if (s == null)
						continue;
					
					split = s.split(":");
					if (split[0].contains("vote_id"))
					{
						vote_id = Integer.parseInt(split[1].trim());
					}
				}
			}
			// Disconnect the connection
			connection.disconnect();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return vote_id;
	}
	
	/**
	 * Return true if player is eligible to get a reward
	 *
	 * @param player object
	 * @return boolean
	 */
	private boolean isEligible(final Player player, final String TOPSITE, final StatsSet set, final int vote_id)
	{
		final int _responseCode = set.getInteger("response_code");
		final boolean _hasVoted = set.getBool("has_voted");
		final long _voteTime = set.getLong("vote_time");
		final long _serverTime = set.getLong("server_time");
		final String _responseError = set.getString("response_error");
		
		// check if response was not ok
		if (_responseCode != 200)
		{
			sendMsg(player, TOPSITE + " server is not responding try again later.");
			return false;
		}
		
		// server returned error
		if (!_responseError.equals("NONE"))
		{
			sendMsg(player, "Response error:" + _responseError + ".");
			return false;
		}
		
		// player has not voted
		if (!_hasVoted)
		{
			if (vote_id == 0)
			{
				sendMsg(player, "You didn't vote at " + TOPSITE + ".");
			}
			else
			{
				sendMsg(player, "You didn't vote at " + TOPSITE + " generated URL try again.");
			}
			return false;
		}
		
		// check 12hours on server time pass
		if ((_serverTime > 0 && _voteTime > 0) && (_voteTime + VOTE_REUSE.toMillis() < _serverTime))
		{
			if (Configurations.DEBUG)
			{
				sendMsg(player, "Dates " + (_voteTime + VOTE_REUSE.toMillis()) + "<" + _serverTime);
			}
			sendMsg(player, "The reward has expired, vote again.");
			return false;
		}
		
		// the player is eligible to receive reward
		return true;
	}
	
	/**
	 * reward player
	 *
	 * @param player object
	 */
	private void reward(final Player player, final String TOPSITE)
	{
		// iterate on item values
		for (final int itemId : Rewards.from(TOPSITE + "_INDIVIDUAL_REWARDS").keys())
		{
			// check if the item id exists
			final ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
			if (Objects.nonNull(item))
			{
				// get config values
				final Integer[] values = Rewards.from(TOPSITE + "_INDIVIDUAL_REWARDS").get(itemId);
				// set min count value of received item
				int min = values[0];
				// set max count value of received item
				int max = values[1];
				// set chances of getting the item
				int chance = values[2];
				// set count of each item
				long count = Random.get(min, max);
				// chance for each item
				if (Random.get(100) < chance || chance >= 100)
				{
					// reward item
					Functions.addItem(player, itemId, count);
				}
			}
		}
	}
	
	/**
	 * Send message to player
	 *
	 * @param player object
	 * @param s      string
	 */
	private void sendMsg(final Player player, final String s)
	{
		player.sendPacket(new ExShowScreenMessage(s, 3000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
		player.sendMessage(s);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}