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
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;

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
 * Pack Support: L2Scripts rev20720(2268)
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

	private final String DELETE = "DELETE FROM donate_holder WHERE no=? LIMIT 1";
	private final String SELECT = "SELECT no, id, count, playername FROM donate_holder";

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
		try (Connection con = DatabaseFactory.getInstance().getConnection();
		     PreparedStatement statement = con.prepareStatement(SELECT);
		     ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final Player player = GameObjectsStorage.getPlayer(rset.getString("playername"));
				final int no = rset.getInt("no");
				final int id = rset.getInt("id");
				final int count = rset.getInt("count");

				Optional.ofNullable(player).ifPresent(s ->
				{
					if (removeDonation(no))
					{
						final ItemTemplate item = ItemHolder.getInstance().getTemplate(id);

						if (Objects.nonNull(item))
						{
							Gui.getInstance().ConsoleWrite("Donation: " + player.getName() + " received " + count + "x " + item.getName());
							ItemFunctions.addItem(player, id, count, true, "Donation");
							player.sendActionFailed();
						}
					}
				});
			}
		} catch (final Exception e)
		{
			String error = e.getMessage();
			_log.warn("Check donate items failed. " + error);

			if (error.contains("doesn't exist") && error.contains("donate_holder"))
			{
				Utilities.deleteTable(Utilities.DELETE_DONATE_TABLE, "Donate");
				Utilities.createTable(Utilities.CREATE_DONATE_TABLE, "Donate");
			}
		}
	}

	/**
	 * Remove donation from database
	 *
	 * @param id int
	 * @return boolean
	 */
	private boolean removeDonation(int id)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
		     PreparedStatement statement = con.prepareStatement(DELETE))
		{
			statement.setInt(1, id);
			statement.execute();
			return true;
		} catch (SQLException e)
		{
			_log.warn("Failed to remove donation from database of donation id: " + id);
			_log.warn(e.getMessage());
		}

		return false;
	}
}