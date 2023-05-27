package hopzone.eu.util;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * @Author !Haber!
 * <p>
 * VDS Stands for: Vote Donation System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.6
 * Pack Support: Lucera NO GUI
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * How to install https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z
 */
public class OpenUrl extends L2GameServerPacket
{
	private String URL;
	
	/**
	 * The url you want to open on players computer
	 * @param URL string
	 */
	public OpenUrl(String URL)
	{
		this.URL = URL;
	}
	
	@Override
	protected void writeImpl()
	{
		if (URL == null)
			URL = "https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z";
		writeC(255);
		writeC(3);
		writeS(URL);
	}
}
