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
package hopzone.eu.task;

import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.templates.L2Item;
import com.l2jfrozen.util.database.L2DatabaseFactory;
import hopzone.eu.gui.Gui;
import hopzone.eu.util.Logs;
import hopzone.eu.util.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * VDS Stands for: Vote Delivery System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.6
 * Pack Support: Frozen 1132 Last beta branch https://app.assembla.com/spaces/L2jFrozenInterlude/subversion/source
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * How to install https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z
 */
public class ItemDeliveryManager implements Runnable
{
	// logger
	private static final Logs _log = new Logs(ItemDeliveryManager.class.getSimpleName());

	private final static String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final static String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

	@Override
	public void run()
	{
		start();
	}

	/**
	 * Deliver item on player
	 */
	private void start()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			 PreparedStatement statement = con.prepareStatement(SELECT);
			 ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(rset.getString("char_name"));
				final int id = rset.getInt("id");
				final int item_id = rset.getInt("item_id");
				final int item_count = rset.getInt("item_count");

				if (player != null)
				{
					if (updateItemStatus(id))
					{
						final L2Item item = ItemTable.getInstance().getTemplate(item_id);

						if (item != null)
						{
							Gui.getInstance().ConsoleWrite("Delivery: " + player.getName() + " received " + item_count + "x " + item.getName());
							player.addItem("", item_id, item_count, player, true);
							player.sendPacket(new ActionFailed());
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			String error = e.getMessage();
			_log.warn("Item delivery failed. " + error);

			if (error.contains("doesn't exist") && error.contains("user_item_delivery"))
			{
				Utilities.deleteTable(Utilities.DELETE_DELIVERY_TABLE, "user_item_delivery");
				Utilities.createTable(Utilities.CREATE_DELIVERY_TABLE, "user_item_delivery");
			}
		}
	}

	/**
	 * Update Item Status from Delivery on database
	 *
	 * @param id int
	 * @return boolean
	 */
	private boolean updateItemStatus(int id)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			 PreparedStatement statement = con.prepareStatement(UPDATE))
		{
			statement.setInt(1, id);
			statement.execute();
			return true;
		}
		catch (SQLException e)
		{
			_log.warn("Failed to update the Delivery on database, id: " + id);
			_log.warn(e.getMessage());
		}

		return false;
	}
}