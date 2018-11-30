//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.util;

import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URL;
import java.util.Hashtable;

public class StringAbbreviator {
	private static final String SUFFIX = "suffix";
	private static final String PREFIX = "prefix";
	public static final int NAME = 0;
	public static final int PHONE = 1;
	public static final int EMAIL = 2;
	public static final int TRUNCATE = 3;
	public static final int FILE = 4;
	public static final String STR_NAME = "name";
	public static final String STR_PHONE = "phone";
	public static final String STR_EMAIL = "email";
	public static final String STR_TRUNCATE = "truncate";
	public static final String STR_FILE = "file";
	protected URL datadir;
	protected String datafile;
	protected Hashtable abbrevT;
	protected static Hashtable prefixSuffixT = new Hashtable();
	private static StringAbbreviator s_abbrev;

	public StringAbbreviator(URL var1, String var2) {
		this.datadir = var1;
		this.datafile = var2;
	}

	public static StringAbbreviator getInstance() {
		return s_abbrev;
	}

	public String abbreviate(String var1, int var2, FontMetrics var3, int var4) {
		switch(var2) {
			case 0:
				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviateName(var1, false);
				}

				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviateName(var1, true);
				}
				break;
			case 1:
				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviatePhone(var1, 8);
				}

				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviatePhone(var1, 4);
				}
				break;
			case 2:
				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviateEmail(var1);
				}
				break;
			case 3:
			default:
				int var5 = 0;
				int var6 = 0;

				for(int var7 = 0; var7 < var4 && var6 < var1.length(); ++var6) {
					if (Character.isWhitespace(var1.charAt(var6))) {
						var5 = var6;
					}

					var7 += var3.charWidth(var1.charAt(var6));
				}

				if (var6 < var1.length() && var5 > 0) {
					var6 = var5;
				}

				var1 = var6 > 0 ? var1.substring(0, var6) : var1;
				break;
			case 4:
				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviate(var1, false);
				}

				if (var3.stringWidth(var1) > var4) {
					var1 = this.abbreviate(var1, true);
				}
		}

		return var1;
	}

	public String abbreviateName(String var1, FontMetrics var2, int var3) {
		return this.abbreviate(var1, 0, var2, var3);
	}

	public String abbreviateEmail(String var1, FontMetrics var2, int var3) {
		return this.abbreviate(var1, 2, var2, var3);
	}

	protected String abbreviate(String var1, boolean var2) {
		if (this.abbrevT == null) {
			this.readAbbrFile();
		}

		String var3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringReader var4 = new StringReader(var1);
		StreamTokenizer var5 = new StreamTokenizer(var4);
		var5.wordChars(38, 38);
		var5.wordChars(64, 64);
		var5.ordinaryChar(44);
		var5.ordinaryChar(46);
		var5.ordinaryChar(45);
		var5.ordinaryChar(58);
		String var7 = null;
		StringBuffer var8 = new StringBuffer();
		boolean var9 = true;

		try {
			while(true) {
				int var6 = var5.nextToken();
				switch(var6) {
					case -3:
						if (var9) {
							var9 = false;
						} else if (!var2) {
							var8.append(" ");
						}

						if (var2) {
							String var10 = var5.sval.substring(0, 1);
							if (var3.indexOf(var10) >= 0) {
								var8.append(var10);
							}
						} else if ((var7 = (String)this.abbrevT.get(var5.sval.toLowerCase())) != null) {
							var8.append(var7);
						} else {
							var8.append(var5.sval);
						}
						break;
					case -2:
						if (var9) {
							var9 = false;
						} else if (!var2) {
							var8.append(" ");
						}

						var8.append(new Integer((int)var5.nval));
						break;
					case -1:
						return var8.toString();
					case 10:
						System.err.println("warning: unexpected EOL token");
						break;
					case 44:
						var8.append((char)var6);
						break;
					case 45:
						if (!var2) {
							var8.append((char)var6);
						}
						break;
					case 46:
						if (!var2) {
							var8.append((char)var6);
						}
						break;
					case 58:
						var8.append((char)var6);
						break;
					default:
						if (!var2) {
							var8.append((char)var6);
						}
				}
			}
		} catch (IOException var11) {
			var11.printStackTrace();
			return var8.toString();
		}
	}

	protected String abbreviatePhone(String var1, int var2) {
		return var1 != null && var1.length() > var2 ? var1.substring(var1.length() - var2) : var1;
	}

	protected String abbreviateEmail(String var1) {
		return var1 != null && var1.indexOf(64) > 0 ? var1.substring(0, var1.indexOf(64)) : var1;
	}

	protected String abbreviateName(String var1, boolean var2) {
		StringReader var3 = new StringReader(var1);
		StreamTokenizer var4 = new StreamTokenizer(var3);
		var4.wordChars(38, 38);
		var4.wordChars(64, 64);
		var4.wordChars(58, 58);
		var4.ordinaryChar(44);
		var4.ordinaryChar(45);
		Object var6 = null;
		String var7 = null;
		String var8 = null;
		StringBuffer var9 = new StringBuffer();

		try {
			while(true) {
				int var5 = var4.nextToken();
				switch(var5) {
					case -3:
						if (var4.sval.endsWith(":")) {
							var9.append(var4.sval + " ");
						} else if (prefixSuffixT.get(var4.sval.toLowerCase()) == null) {
							if (!var2) {
								if (var8 != null) {
									var9.append(var8);
								}

								var8 = var4.sval.substring(0, 1) + ". ";
							}

							var7 = var4.sval;
						}
					case -2:
					default:
						break;
					case -1:
					case 44:
						var9.append(var7);
						return var9.toString();
					case 10:
						System.err.println("warning: unexpected EOL token");
				}
			}
		} catch (IOException var11) {
			var11.printStackTrace();
			return var9.toString();
		}
	}

	public void readAbbrFile() {
		this.abbrevT = new Hashtable();

		try {
			URL var1 = new URL(this.datadir, this.datafile);
			BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.openStream()));
			parseAbbrFile(var2, this.abbrevT);
		} catch (Exception var3) {
			;
		}

	}

	public static void parseAbbrFile(BufferedReader var0, Hashtable var1) {
		StreamTokenizer var2 = new StreamTokenizer(var0);
		var2.whitespaceChars(61, 61);
		var2.wordChars(38, 38);
		var2.wordChars(47, 47);
		var2.slashStarComments(true);
		var2.slashSlashComments(true);
		var2.commentChar(35);
		String var4 = null;
		boolean var5 = false;
		boolean var6 = false;

		try {
			label30:
			while(true) {
				int var3 = var2.nextToken();
				switch(var3) {
					case -3:
						if (var6) {
							var1.put(var4, var2.sval);
							var6 = false;
						} else {
							var6 = true;
							var4 = var2.sval.toLowerCase();
						}
						break;
					case -2:
						if (var6) {
							var1.put(var4, new Double(var2.nval));
							var6 = false;
							break;
						} else {
							var5 = true;
						}
					case -1:
						break label30;
					case 10:
						System.err.println("warning: unexpected EOL token");
						break;
					default:
						var5 = true;
						break label30;
				}
			}

			if (var5) {
				System.out.println("Error encountered around '" + var4 + "'");
			}
		} catch (IOException var8) {
			var8.printStackTrace();
		}

	}

	public void setAbbrFile(String var1) {
		this.datafile = var1;
		this.readAbbrFile();
	}

	static {
		prefixSuffixT.put("mr", "prefix");
		prefixSuffixT.put("mr.", "prefix");
		prefixSuffixT.put("dr", "prefix");
		prefixSuffixT.put("dr.", "prefix");
		prefixSuffixT.put("lt", "prefix");
		prefixSuffixT.put("lt.", "prefix");
		prefixSuffixT.put("gen", "prefix");
		prefixSuffixT.put("gen.", "prefix");
		prefixSuffixT.put("sgt", "prefix");
		prefixSuffixT.put("sgt.", "prefix");
		prefixSuffixT.put("cmdr", "prefix");
		prefixSuffixT.put("cmdr.", "prefix");
		prefixSuffixT.put("cpt", "prefix");
		prefixSuffixT.put("cpt.", "prefix");
		prefixSuffixT.put("ii", "suffix");
		prefixSuffixT.put("iii", "suffix");
		prefixSuffixT.put("iv", "suffix");
		prefixSuffixT.put("jr", "suffix");
		prefixSuffixT.put("jr.", "suffix");
		prefixSuffixT.put("sr", "suffix");
		prefixSuffixT.put("sr.", "suffix");
		s_abbrev = new StringAbbreviator((URL)null, (String)null);
	}
}
