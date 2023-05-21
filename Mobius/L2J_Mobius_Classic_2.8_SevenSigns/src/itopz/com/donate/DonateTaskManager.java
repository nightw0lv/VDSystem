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
package itopz.com.donate;

import itopz.com.gui.Gui;
import itopz.com.util.Logs;
import itopz.com.util.Utilities;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.data.ItemTable;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

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
 * Pack Support: Mobius Classic 2.8 SevenSigns
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * How to install https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z
 */
public class DonateTaskManager implements Runnable
{
	// logger
	private static final Logs _log = new Logs(DonateTaskManager.class.getSimpleName());

	private final static String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final static String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

	@Override
	public void run()
	{
		start();
	}

	/**
	 * reward player if donation is received
	 */
	private void start()
	{
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement statement = con.prepareStatement(SELECT);
		     ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final int id = rset.getInt("id");
				final Player player = World.getInstance().getPlayer(rset.getString("char_name"));
				final int item_id = rset.getInt("item_id");
				final int count = rset.getInt("item_count");

				Optional.ofNullable(player).ifPresent(s ->
				{
					if (updateDonation(id))
					{
						final ItemTemplate item = ItemTable.getInstance().getTemplate(id);

						if (Objects.nonNull(item))
						{
							Gui.getInstance().ConsoleWrite("Donation: " + player.getName() + " received " + count + "x " + item.getName());
							player.addItem("Donation", item_id, count, player, true);
							player.sendPacket(ActionFailed.STATIC_PACKET);
						}
					}
				});
			}
		}
		catch (final Exception e)
		{
			String error = e.getMessage();
			_log.warn("Donation item delivery failed. " + error);

			if (error.contains("doesn't exist") && error.contains("user_item_delivery"))
			{
				Utilities.deleteTable(Utilities.DELETE_DONATE_TABLE, "user_item_delivery");
				Utilities.createTable(Utilities.CREATE_DONATE_TABLE, "user_item_delivery");
			}
		}
	}

	/**
	 * Remove donation from database
	 *
	 * @param id int
	 * @return boolean
	 */
	private boolean updateDonation(int id)
	{
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement statement = con.prepareStatement(UPDATE))
		{
			statement.setInt(1, id);
			statement.execute();
			return true;
		}
		catch (SQLException e)
		{
			_log.warn("Failed to update the donation on database, id: " + id);
			_log.warn(e.getMessage());
		}

		return false;
	}
}