package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Tree;

import java.io.*;
import java.util.Vector;

public class HDirTreeReader
		extends AbstractTreeReader {
	private Vector fieldNameList;
	private String textFieldName = "label";
	private int levels;
	private double sizes;
	private double isizes;
	private String HREFs;
	private String types;
	private String TEXTs;
	private boolean isdir;
	private int dircnt;
	private double dirtotal;
	private double areimages;
	private double areaudio;
	private double arehtml;
	private String dirfile;
	private int yvals;
	private int dirvals;
	private boolean istext;
	private boolean isdisplayed;
	private boolean isopen;
	private boolean searchHit;
	private StreamTokenizer t = null;
	private double totalsize = 0.0D;
	private String HREFserver;
	private String HREFbase;
	private int index = 0;
	private int count = 0;
	private DefaultTreeNode root;

	public Tree loadTree(InputStream paramInputStream)
			throws IOException {
		this.count = 0;
		this.fieldNameList = new Vector();
		BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
		readDataFile(localBufferedReader);
		System.out.println("Read in tree with " + (this.root.getDescendantCount() + 1) + " nodes.");
		return new DefaultTree(this.root);
	}

	public void readDataFile(BufferedReader paramBufferedReader) {
		int i = 0;
		this.fieldNameList.addElement(this.textFieldName);
		this.index = -1;
		DefaultTreeNode localDefaultTreeNode = null;
		int j = 1;
		try {
			this.t = new StreamTokenizer(paramBufferedReader);
			this.t.resetSyntax();
			this.t.whitespaceChars(0, 32);
			this.t.quoteChar(34);
			this.t.wordChars(97, 122);
			this.t.wordChars(65, 90);
			this.t.wordChars(39, 39);
			this.t.wordChars(92, 92);
			this.t.wordChars(47, 47);
			this.t.wordChars(40, 40);
			this.t.wordChars(41, 41);
			this.t.wordChars(48, 57);
			this.t.wordChars(46, 46);
			this.t.wordChars(43, 43);
			this.t.wordChars(45, 45);
			this.t.wordChars(58, 58);
			this.t.wordChars(59, 59);
			this.t.wordChars(126, 126);
			this.t.wordChars(42, 42);
			this.t.wordChars(35, 35);
			this.t.wordChars(60, 60);
			this.t.ordinaryChars(62, 62);
			this.t.ordinaryChars(44, 44);
			for (; ; ) {
				int k;
				switch (k = this.t.nextToken()) {
					case -1:
						break;
					case -3:
					case 34:
						String str2 = this.t.sval.toLowerCase();
						int m;
						if (str2.equals("<hdir")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							} else {
								if ((m = this.t.nextToken()) == -1) {
									break;
								}
								if (m != 10) {
									if (m == 60) {
										this.t.pushBack();
									} else if (m == -3) {
										if (this.t.sval.startsWith("<")) {
											this.t.pushBack();
										} else {
											this.totalsize = Double.valueOf(this.t.sval).doubleValue();
										}
									} else {
										this.t.pushBack();
									}
								}
							}
						} else if (str2.equals("</hdir")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							}
						} else if (str2.equals("<server")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							} else if (((m = this.t.nextToken()) == -3) || (m == 34)) {
								this.HREFserver = this.t.sval;
							}
						} else if (str2.equals("<base")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							} else if (((m = this.t.nextToken()) == -3) || (m == 34)) {
								this.HREFbase = this.t.sval;
							}
						} else if (str2.equals("<directorytree")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							}
						} else if (str2.equals("<r")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							} else {
								this.index += 1;
								this.levels = i;
								if ((m = this.t.nextToken()) == -1) {
									break;
								}
								if (m != 10) {
									if (m == -3) {
										this.sizes = Double.valueOf(this.t.sval).doubleValue();
									} else {
										this.t.pushBack();
										continue;
									}
									if ((m = this.t.nextToken()) != 44) {
										this.t.pushBack();
									} else {
										if ((m = this.t.nextToken()) == -1) {
											break;
										}
										if (m != 10) {
											if ((m == -3) || (m == 34)) {
												this.HREFs = this.t.sval;
											} else {
												this.t.pushBack();
												continue;
											}
											if ((m = this.t.nextToken()) != 44) {
												this.t.pushBack();
											} else {
												if ((m = this.t.nextToken()) == -1) {
													break;
												}
												if (m != 10) {
													if ((m == -3) || (m == 34)) {
														this.types = this.t.sval;
													} else {
														this.t.pushBack();
														continue;
													}
													String str1 = this.HREFs;
													if ((m = this.t.nextToken()) != 44) {
														this.t.pushBack();
													} else {
														if ((m = this.t.nextToken()) == -1) {
															break;
														}
														if (m == 10) {
															this.t.pushBack();
														} else if ((m == -3) || (m == 34)) {
															if (this.t.sval.startsWith("<")) {
																this.t.pushBack();
															} else if (!this.t.sval.equals("")) {
																str1 = this.t.sval;
															}
														} else {
															this.t.pushBack();
														}
													}
													this.TEXTs = str1;
													localDefaultTreeNode = new DefaultTreeNode();
													localDefaultTreeNode.setAttribute("id", String.valueOf(this.count++));
													localDefaultTreeNode.setAttribute(this.textFieldName, this.TEXTs);
													this.isizes = 0.0D;
													if ((m = this.t.nextToken()) == 44) {
														if ((m = this.t.nextToken()) == -1) {
															break;
														}
														if ((m != 10) && (m == -3)) {
															this.isizes = Double.valueOf(this.t.sval).doubleValue();
														}
													} else {
														this.t.pushBack();
													}
												}
											}
										}
									}
								}
							}
						} else if (str2.equals("<hl")) {
							if ((m = this.t.nextToken()) != 62) {
								this.t.pushBack();
							} else {
								readLevel(paramBufferedReader, i, this.t, localDefaultTreeNode);
								if (j != 0) {
									j = 0;
									this.root = localDefaultTreeNode;
								}
							}
						}
						break;
				}
			}
		} catch (Exception localException) {
			System.err.println("Caught exception in readDataFile " + localException);
			localException.printStackTrace();
		}
	}

	private void readLevel(BufferedReader paramBufferedReader, int paramInt, StreamTokenizer paramStreamTokenizer, DefaultTreeNode paramDefaultTreeNode) {
		String str2 = "";
		paramInt++;
		this.isdir = true;
		try {
			int j;
			if ((j = paramStreamTokenizer.nextToken()) == -1) {
				paramStreamTokenizer.pushBack();
				return;
			}
			if ((j != 10) && ((j == -3) || (j == 34))) {
				str2 = paramStreamTokenizer.sval;
			}
			DefaultTreeNode localDefaultTreeNode = null;
			for (; ; ) {
				int i;
				switch (i = paramStreamTokenizer.nextToken()) {
					case -1:
						break;
					case -3:
					case 34:
						String str3 = paramStreamTokenizer.sval.toLowerCase();
						if (str3.equals("<r")) {
							if ((j = paramStreamTokenizer.nextToken()) != 62) {
								paramStreamTokenizer.pushBack();
							} else {
								this.index += 1;
								this.levels = paramInt;
								if ((j = paramStreamTokenizer.nextToken()) == -1) {
									break;
								}
								if (j != 10) {
									if (j == -3) {
										this.sizes = Double.valueOf(paramStreamTokenizer.sval).doubleValue();
									} else {
										paramStreamTokenizer.pushBack();
										continue;
									}
									if ((j = paramStreamTokenizer.nextToken()) != 44) {
										paramStreamTokenizer.pushBack();
									} else {
										if ((j = paramStreamTokenizer.nextToken()) == -1) {
											break;
										}
										if (j != 10) {
											if ((j == -3) || (j == 34)) {
												this.HREFs = paramStreamTokenizer.sval;
											} else {
												paramStreamTokenizer.pushBack();
												continue;
											}
											if ((j = paramStreamTokenizer.nextToken()) != 44) {
												paramStreamTokenizer.pushBack();
											} else {
												if ((j = paramStreamTokenizer.nextToken()) == -1) {
													break;
												}
												if (j != 10) {
													if ((j == -3) || (j == 34)) {
														this.types = paramStreamTokenizer.sval;
													} else {
														paramStreamTokenizer.pushBack();
														continue;
													}
													String str1 = this.HREFs;
													if ((j = paramStreamTokenizer.nextToken()) != 44) {
														paramStreamTokenizer.pushBack();
													} else {
														if ((j = paramStreamTokenizer.nextToken()) == -1) {
															break;
														}
														if (j == 10) {
															paramStreamTokenizer.pushBack();
														} else if ((j == -3) || (j == 34)) {
															if (paramStreamTokenizer.sval.startsWith("<")) {
																paramStreamTokenizer.pushBack();
															} else if (!paramStreamTokenizer.sval.equals("")) {
																str1 = paramStreamTokenizer.sval;
															}
														} else {
															paramStreamTokenizer.pushBack();
														}
													}
													this.TEXTs = str1;
													localDefaultTreeNode = new DefaultTreeNode();
													localDefaultTreeNode.setAttribute("id", String.valueOf(this.count++));
													localDefaultTreeNode.setAttribute(this.textFieldName, this.TEXTs);
													this.isizes = 0.0D;
													if ((j = paramStreamTokenizer.nextToken()) == 44) {
														if ((j = paramStreamTokenizer.nextToken()) == -1) {
															break;
														}
														if (j == 10) {
															continue;
														}
														if (j == -3) {
															this.isizes = Double.valueOf(paramStreamTokenizer.sval).doubleValue();
														}
													} else {
														paramStreamTokenizer.pushBack();
													}
													paramDefaultTreeNode.addChild(new DefaultEdge(paramDefaultTreeNode, localDefaultTreeNode));
												}
											}
										}
									}
								}
							}
						} else if (str3.equals("<hl")) {
							if ((j = paramStreamTokenizer.nextToken()) != 62) {
								paramStreamTokenizer.pushBack();
							} else {
								readLevel(paramBufferedReader, paramInt, paramStreamTokenizer, localDefaultTreeNode);
							}
						} else if (str3.equals("</hl")) {
							if ((j = paramStreamTokenizer.nextToken()) != 62) {
								paramStreamTokenizer.pushBack();
							} else {
								if ((j = paramStreamTokenizer.nextToken()) == -1) {
									paramStreamTokenizer.pushBack();
									return;
								}
								if ((j != 10) && ((j == -3) || (j == 34))) {
									String str4 = paramStreamTokenizer.sval;
									if (str4.startsWith("<")) {
										paramStreamTokenizer.pushBack();
									} else if (str2.equals(str4)) {
									}
								}
								return;
							}
						}
						break;
				}
			}
		} catch (Exception localException) {
			System.err.println("readLevel ** Exception: " + localException + " " + paramInt);
			localException.printStackTrace();
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/HDirTreeReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */