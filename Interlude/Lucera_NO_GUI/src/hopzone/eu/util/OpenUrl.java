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
