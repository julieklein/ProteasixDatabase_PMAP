import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main_PDB_Pmap {

	public Main_PDB_Pmap() throws MalformedURLException, IOException {
		PrintStream csvWriter = null;
		LinkedList<CsDatabaseEntry> PmapnotcuratedProteasixDB = new LinkedList<CsDatabaseEntry>();
		java.util.Calendar calendar = java.util.Calendar.getInstance();

		String version = "JUNE2012";

		File f = new File(
				"//Users/julieklein/Dropbox/ProteasiX/ProteasiX/ProteasixVersionJune2012/PMAPJUIN2012_5");
		File[] files = f.listFiles();
		for (File file : files) {
			String filepath = "file://" + file.getPath() + "/";
			String htmlcontentmultipleentries = getHtmlcontent(
					new URL(filepath)).toString();

			Matcher splithtml = getPatternmatcher(
					"(<input\\s+id=\"ballot.*?>Detail</a></td>)",
					htmlcontentmultipleentries);
			String htmlsplitted = putSplittedhtmlintostringbuilder(splithtml);
			Matcher retrievepmapentryid = getPatternmatcher("<td><a\\s+href=\""
					+ "([^\"]+)" + "\"[^>]*>", htmlsplitted);

			while (retrievepmapentryid.find()) {
				String url = retrievepmapentryid.group(1);
				if (url.equalsIgnoreCase("/relation/show/16398")
						|| url.equalsIgnoreCase("/relation/show/17178")
						|| url.equalsIgnoreCase("/relation/show/17177")
						|| url.equalsIgnoreCase("/relation/show/17074")
						|| url.equalsIgnoreCase("/relation/show/17458")
						|| url.equalsIgnoreCase("/relation/show/17467")
						|| url.equalsIgnoreCase("/relation/show/16083")
						|| url.equalsIgnoreCase("/relation/show/16082")
						|| url.equalsIgnoreCase("/relation/show/16081")
						|| url.equalsIgnoreCase("/relation/show/16080")
						|| url.equalsIgnoreCase("/relation/show/16398")
						|| url.equalsIgnoreCase("/relation/show/16271")) {
				} else {
					url = "http://cutdb.burnham.org" + url;
					String entry = getHtmlcontent(new URL(url)).toString();
					Matcher patternProteaseTaxon = getPatternmatcher(
							"<div\\s+id=\"protdata\">[^<]+<table>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Definition:</th>"
									+ "([^<]+<td><b>[^<]+</b></td>)?"
									+ "[^<]+</tr>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Organism:</th>[^<]+<td><a\\s+href\\s+=\\s+\"[^\"]+\"\\s+target=\"[^\"]+\">"
									+ "([^<]+)", entry);
					String proteaseTaxon = getProteaseTaxon(patternProteaseTaxon);
					System.out.println(proteaseTaxon);

					Matcher patternSubstrateTaxon = getPatternmatcher(
							"<div\\s+id=\"sbstdata\">[^<]+<table>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Organism:</th>[^<]+<td><a\\s+href\\s+=\\s+\"[^\"]+\"\\s+target=\"[^\"]+\">"
									+ "([^<]+)", entry);
					String substrateTaxon = getSubstrateTaxon(patternSubstrateTaxon);
					System.out.println(substrateTaxon);

					System.out.println("\n" + "******************************"
							+ url);

					Matcher patternSubstrateName = getPatternmatcher(
							"Substrate[^<]+</th>[^<]+<td>[^<]+<table>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Definition:</th>[^<]+<td><b>"
									+ "([^<]+)", entry);
					SubstrateDatabaseEntry substratedatabase = new SubstrateDatabaseEntry();
					CsDatabaseEntry csdatabase = new CsDatabaseEntry();
					ProteaseDatabaseEntry proteasedatabase = new ProteaseDatabaseEntry();

					substratedatabase.setS_Taxon(substrateTaxon);

					String commentS = getSubstrateNameSymbolId(
							patternSubstrateName, substratedatabase,
							csdatabase, entry, substrateTaxon);
					System.out.println("out");

					proteasedatabase.setP_Taxon(proteaseTaxon);
					// csdatabase.setSubstrate(substratedatabase);

					Matcher patternProteaseName = getPatternmatcher(
							"<div\\s+id=\"protdata\">[^<]+<table>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Definition:</th>[^<]+<td><b>"
									+ "([^<]+)", entry);
					String commentP = getProteaseNameSymbolId(
							patternProteaseName, csdatabase, proteasedatabase,
							proteaseTaxon, commentS);

					csdatabase.setComment(commentP);
					System.out.println(commentP);
					csdatabase.setExternal_Link(url);

					Matcher patternCleavagesitePosition = getPatternmatcher(
							"<div\\s+id=\"cleav2\">[^<]+<table>[^<]+<th\\s+class=\"th3\">Position:</th>[^<]+<td>"
									+ "([^<]+)?", entry);
					getCleavagesitePosition(patternCleavagesitePosition,
							csdatabase);

					Matcher patternCleavagesiteSequence = getPatternmatcher(
							"<div\\s+id=\"cleav\">[^<]+<table>[^<]+<th\\s+class=\"th3\">Sequence:</td>[^<]+<td>"
									+ "([^<]+)?", entry);
					String csSequence = getCleavagesiteSequence(
							patternCleavagesiteSequence, csdatabase);
					String csSequencenodash = csSequence.replaceAll("-", "");
					csSequencenodash = csSequencenodash.trim();

					Matcher patternPmid = getPatternmatcher(
							"<div\\s+id=\"pubmed\">[^<]+<table>[^<]+<td>[^<]+<a\\s+href=\"[^\"]+\"\\s+target=\"[^\"]+\"\\s+>"
									+ "([^<]+)", entry);
					getPmid(patternPmid, csdatabase);

					Matcher patternErrorUnmatched = getPatternmatcher(
							"<td><font\\s+color=\"#FF0000\">"
									+ "(\\*Unmatched)", entry);
					getErrorUnmatched(patternErrorUnmatched, csdatabase);

					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
					Calendar originalDate = Calendar.getInstance();
					String dateString = format.format(originalDate.getTime());
					System.out.println(dateString);
					csdatabase.setCreation_Date(dateString);

					if (!substratedatabase.getS_UniprotID().contains(
							"n.d")
							&& !substratedatabase.getS_UniprotID()
									.equalsIgnoreCase("to check")
							&& !csdatabase.getP1_Sequence().equalsIgnoreCase(
									"?")
							&& !csdatabase.getP1prime_Sequence()
									.equalsIgnoreCase("?")
							&& !(csdatabase.getP1_Position() == 0)
							&& !(csdatabase.getP1prime_Position() == 0)
							&& !csdatabase.getCuration_Status().contains(
									"discarded")) {
						String motif = csdatabase.getP1_Sequence() + "-"
								+ csdatabase.getP1prime_Sequence();
						int motifposition = csSequence.indexOf(motif) + 1;

						NodeList entries = getEntries(
								"/uniprot/entry/sequence/text()",
								parseUniprot("http://www.uniprot.org/uniprot/"
										+ substratedatabase.getS_UniprotID()
										+ ".xml"));
						for (int i = 0; i < entries.getLength(); i++) {
							String sequence = getUniSubstratesequence(entries,
									i, substratedatabase);

							int startcs = 0;

							if (csSequence.startsWith("---")) {
								startcs = csdatabase.getP1_Position() - 1;
							} else if (csSequence.startsWith("--")) {
								startcs = csdatabase.getP1_Position() - 2;
							} else if (csSequence.startsWith("-")) {
								startcs = csdatabase.getP1_Position() - 3;
							} else {
								startcs = csdatabase.getP1_Position() - 4;
							}

							int length = csSequencenodash.length();
							
							if ((startcs + length) < sequence.length() || (startcs + length) == sequence.length()) {
								
							String csonsequence = sequence.substring(startcs,
									startcs + length);
							System.out
									.println("CS ON SEQUENCE " + csonsequence);

							if (!csonsequence.equals(csSequencenodash)) {
								if (sequence.contains(csSequencenodash)) {
									int cleavagesiteposition = sequence
											.indexOf(csSequencenodash);
									int newp1 = motifposition
											+ cleavagesiteposition;
									int newp1prime = newp1 + 1;
									csdatabase.setP1_Position(newp1);
									csdatabase.setP1prime_Position(newp1prime);
									System.out.println(newp1);
									System.out.println(newp1prime);
									csdatabase
											.setCuration_Status("Cleavage site curated based on Uniprot protein sequence");
									System.out
											.println("Cleavage site curated based on Uniprot protein sequence");
								} else {
									csdatabase
											.setCuration_Status("Unmatched cleavage site; Cleavage site discarded");
									System.out
											.println("Unmatched cleavage site; Cleavage site discarded");
								}
							}
							} else {
							
								if (sequence.contains(csSequencenodash)) {
	                                int cleavagesiteposition = sequence.indexOf(csSequencenodash);
	                                int newp1 = motifposition + cleavagesiteposition;
	                                int newp1prime = newp1 + 1;
	                                if (newp1 == csdatabase.getP1_Position() && newp1prime == csdatabase.getP1prime_Position()) {
	                                    continue;
	                                } else {
	                                    csdatabase.setP1_Position(newp1);
	                                    csdatabase.setP1prime_Position(newp1prime);
	                                    System.out.println(newp1);
	                                    System.out.println(newp1prime);
	                                    csdatabase.setCuration_Status("Cleavage site curated based on Uniprot protein sequence");
	                                    System.out.println("Cleavage site curated based on Uniprot protein sequence");
	                                }
							}
							}
						}

						PmapnotcuratedProteasixDB.add(csdatabase);
						
					} else if (!substratedatabase.getS_UniprotID().contains(
							"n.d")
							&& !substratedatabase.getS_UniprotID()
									.equalsIgnoreCase("to check")
							&& !csdatabase.getP1_Sequence().equalsIgnoreCase(
									"?")
							&& !csdatabase.getP1prime_Sequence()
									.equalsIgnoreCase("?")
							&& csdatabase.getP1_Position() == 0
							&& csdatabase.getP1prime_Position() == 0
							&& !csdatabase.getCuration_Status().contains(
									"discarded")){	
						
						String motif = csdatabase.getP1_Sequence() + "-"
								+ csdatabase.getP1prime_Sequence();
						int motifposition = csSequence.indexOf(motif) + 1;

						NodeList entries = getEntries(
								"/uniprot/entry/sequence/text()",
								parseUniprot("http://www.uniprot.org/uniprot/"
										+ substratedatabase.getS_UniprotID()
										+ ".xml"));
						for (int i = 0; i < entries.getLength(); i++) {
							String sequence = getUniSubstratesequence(entries,
									i, substratedatabase);
							if (sequence.contains(csSequencenodash)) {
                                int cleavagesiteposition = sequence.indexOf(csSequencenodash);
                                int newp1 = motifposition + cleavagesiteposition;
                                int newp1prime = newp1 + 1;
                                if (newp1 == csdatabase.getP1_Position() && newp1prime == csdatabase.getP1prime_Position()) {
                                    continue;
                                } else {
                                    csdatabase.setP1_Position(newp1);
                                    csdatabase.setP1prime_Position(newp1prime);
                                    System.out.println(newp1);
                                    System.out.println(newp1prime);
                                    csdatabase.setCuration_Status("Cleavage site curated based on Uniprot protein sequence");
                                    System.out.println("Cleavage site curated based on Uniprot protein sequence");
                                }

                            } else {
                                csdatabase.setCuration_Status("Unmatched cleavage site; Cleavage site discarded");
                                System.out.println("Unmatched cleavage site; Cleavage site discarded");
                            }
                        }
						PmapnotcuratedProteasixDB.add(csdatabase);

					} else {
						PmapnotcuratedProteasixDB.add(csdatabase);
					}

				}
			}
		}
		try {
			System.out.println("-----------------");
			csvWriter = new PrintStream("Pmap5notcuratedProteasixDB" + "_"
					+ version + ".csv");
			// populateHeaders(csvWriter);
			for (CsDatabaseEntry csDatabaseEntry : PmapnotcuratedProteasixDB) {
				System.out.println(csDatabaseEntry.getExternal_Link());
				populateData(csvWriter, csDatabaseEntry);
				System.out.println("OK");
			}

		} catch (FileNotFoundException ex) {
			Logger.getLogger(Main_PDB_Pmap.class.getName()).log(Level.SEVERE,
					null, ex);
		} finally {
			csvWriter.close();
		}
	}

	public static void main(String[] args) throws MalformedURLException,
			IOException {
		// TODO code application logic here
		Main_PDB_Pmap Main_PDB_Pmap = new Main_PDB_Pmap();
	}

	private StringBuilder getHtmlcontent(URL u) throws IOException {
		InputStream is = null;
		DataInputStream dis;
		String s = null;
		StringBuilder htmlcontent = new StringBuilder();

		is = u.openStream();
		dis = new DataInputStream(new BufferedInputStream(is));
		while ((s = dis.readLine()) != null) {
			s = s.replaceAll("\\?", "");
			htmlcontent.append(s);
		}
		is.close();
		return htmlcontent;
	}

	private Matcher getPatternmatcher(String expression, String string) {
		Pattern p = Pattern.compile(expression, Pattern.DOTALL
				| Pattern.UNIX_LINES | Pattern.MULTILINE);
		Matcher matcher = p.matcher(string);
		return matcher;
	}

	private String putSplittedhtmlintostringbuilder(Matcher splithtml) {
		StringBuilder sbd = new StringBuilder();
		while (splithtml.find()) {
			String entry = splithtml.group(1);
			entry = entry + "\n" + "******************************" + "\n";
			// System.out.println(entry);
			sbd.append(entry);
		}
		String splittedentry = sbd.toString();
		return splittedentry;
	}

	private String getProteaseTaxon(Matcher patternProteaseTaxon) {
		String proteaseTaxon = null;
		if (patternProteaseTaxon.find()) {
			proteaseTaxon = patternProteaseTaxon.group(2);
			proteaseTaxon = proteaseTaxon.trim();
		} else {
			proteaseTaxon = "n.d.";
		}
		return proteaseTaxon;
	}

	private String getSubstrateTaxon(Matcher patternSubstrateTaxon) {
		String substrateTaxon = null;
		if (patternSubstrateTaxon.find()) {
			substrateTaxon = patternSubstrateTaxon.group(1);
			substrateTaxon = substrateTaxon.trim();
		} else {
			substrateTaxon = "n.d.";
		}
		return substrateTaxon;
	}

	private String getSubstrateNameSymbolId(Matcher patternSubstrateName,
			SubstrateDatabaseEntry substratedatabase,
			CsDatabaseEntry csdatabase, String entry, String substrateTaxon)
			throws IOException {
		String commentS = null;
		Matcher patternSubstrateAccession = getPatternmatcher(
				"UniProt\\s+Accession:</th>[^<]+<td><a\\s+href\\s+=\\s+\"[^\"]+\"\\s+target=\"[^\"]+\">"
						+ "([^<]+)", entry);
		String accession = getSubstrateAccession(patternSubstrateAccession,
				substratedatabase);

		Matcher patternSubstrateSymbol = getPatternmatcher(
				"Substrate[^<]+</th>[^<]+<td>[^<]+<table>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Definition:</th>[^<]+<td><b>"
						+ "[^<]+"
						+ "</b></td>[^<]+<tr>[^<]+<th\\s+class=\"th3\">Symbol:</th>"
						+ "([^<]+<td><b>)?([^<]+)", entry);
		String symbol = getSubstrateSymbol(patternSubstrateSymbol,
				substratedatabase);

		if (patternSubstrateName.find()) {
			String Substratename = "to check";
			String Substratesymbol = "to check";
			String Substrateaccession = "to check";
			substratedatabase.setS_NL_Name(Substratename);
			substratedatabase.setS_Name(Substratename);
			substratedatabase.setS_Symbol(Substratesymbol);
			substratedatabase.setS_UniprotID(Substrateaccession);

			Substratename = patternSubstrateName.group(1);
			Substratename = Substratename.trim();
			Substratename = Substratename.replaceAll(",", "");
			Substratename = Substratename.replaceAll(";", "");
			substratedatabase.setS_NL_Name(Substratename);
			commentS = "Check Substrate Symbol and Accession; add to Substrate Librairy";

			BufferedReader bReader = null;
			if (substrateTaxon.contains("Homo")) {
				bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/SubstrateHSALibrairy.txt");
			} else if (substrateTaxon.contains("Mus")) {
				bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/SubstrateMMULibrairy.txt");
			} else if (substrateTaxon.contains("Rattus")) {
				bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/SubstrateRNOLibrairy.txt");
			}
			String line;
			while ((line = bReader.readLine()) != null) {
				String splitarray[] = line.split("\t");
				String naturallanguage = splitarray[1];
				naturallanguage = naturallanguage.replaceAll("\"", "");
				naturallanguage = naturallanguage.replaceAll(",", "");
				naturallanguage = naturallanguage.replaceAll(";", "");
				if (naturallanguage.equalsIgnoreCase(Substratename)) {
					Substratesymbol = splitarray[0];
					Substratesymbol = Substratesymbol.replaceAll("sept-0",
							"SEPT");
					Substrateaccession = splitarray[2];

					if (Substrateaccession.contains("n.d.")) {
						substratedatabase.setS_Name("n.d.");
						substratedatabase.setS_UniprotID(Substrateaccession);
						substratedatabase.setS_Symbol(Substratesymbol);

					} else {
						String UniprotURL = "http://www.uniprot.org/uniprot/"
								+ Substrateaccession + ".xml";
						NodeList entries = getEntries("/uniprot/entry",
								parseUniprot(UniprotURL));
						for (int i = 0; i < entries.getLength(); i++) {
							getUniSubstratepproteinname(entries, i,
									substratedatabase);
							String genename = getUniSubstrategenename(entries,
									i, substratedatabase);
						}
						// System.out.println(Substrateaccession);
						substratedatabase.setS_UniprotID(Substrateaccession);
						commentS = "-";
						System.out.println(commentS);
					}
				}
				csdatabase.setSubstrate(substratedatabase);
			}

		} else if (!symbol.contains("n.d.")) {
			String Substratename = "to check";
			String Substratesymbol = "to check";
			String Substrateaccession = "to check";
			substratedatabase.setS_NL_Name(Substratename);
			substratedatabase.setS_Name(Substratename);
			substratedatabase.setS_Symbol(Substratesymbol);
			substratedatabase.setS_UniprotID(Substrateaccession);

			BufferedReader bReader = null;
			if (substrateTaxon.contains("Homo")) {
				bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/SubstrateHSALibrairy.txt");
			} else if (substrateTaxon.contains("Mus")) {
				bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/SubstrateMMULibrairy.txt");
			} else if (substrateTaxon.contains("Rattus")) {
				bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/SubstrateRNOLibrairy.txt");
			}
			String line;
			while ((line = bReader.readLine()) != null) {
				String splitarray[] = line.split("\t");
				String librairisymbol = splitarray[0];
				librairisymbol = librairisymbol.replaceAll("\"", "");
				librairisymbol = librairisymbol.replaceAll("sept-0", "SEPT");
				if (librairisymbol.equals(symbol)) {
					Substrateaccession = splitarray[2];

					if (Substrateaccession.contains("n.d.")) {
						substratedatabase.setS_Name("n.d.");
						substratedatabase.setS_UniprotID(Substrateaccession);
						substratedatabase.setS_Symbol(symbol);

					} else {
						String UniprotURL = "http://www.uniprot.org/uniprot/"
								+ Substrateaccession + ".xml";
						NodeList entries = getEntries("/uniprot/entry",
								parseUniprot(UniprotURL));
						for (int i = 0; i < entries.getLength(); i++) {
							getUniSubstratepproteinname(entries, i,
									substratedatabase);
							String genename = getUniSubstrategenename(entries,
									i, substratedatabase);
						}
						// System.out.println(Substrateaccession);
						substratedatabase.setS_UniprotID(Substrateaccession);
						commentS = "-";
						System.out.println(commentS);
					}
				}
				csdatabase.setSubstrate(substratedatabase);
			}

		} else {
			String Substratename = "n.d.";
			String Substratesymbol = "n.d.";
			String Substrateaccession = "n.d";
			substratedatabase.setS_NL_Name(Substratename);
			substratedatabase.setS_Name(Substratename);
			substratedatabase.setS_Symbol(Substratesymbol);
			substratedatabase.setS_UniprotID(Substrateaccession);
			System.out.println(Substratename);
			System.out.println(Substratesymbol);
			System.out.println(Substrateaccession);
			commentS = "-";
			System.out.println(commentS);
			csdatabase.setSubstrate(substratedatabase);
		}
		return commentS;
	}

	private String getSubstrateSymbol(Matcher patternSubstrateSymbol,
			SubstrateDatabaseEntry substratedatabase) {
		String Substratesymbol = null;
		if (patternSubstrateSymbol.find()) {
			Substratesymbol = patternSubstrateSymbol.group(2);
			substratedatabase.setS_Symbol(Substratesymbol);
			// System.out.println(Substratesymbol);
		} else {
			Substratesymbol = "n.d.";
			substratedatabase.setS_Symbol(Substratesymbol);
			// System.out.println(Substratesymbol);
		}
		return Substratesymbol;
	}

	private String getSubstrateAccession(Matcher patternSubstrateAccession,
			SubstrateDatabaseEntry subtratedatabase) {
		String accession = null;
		if (patternSubstrateAccession.find()) {
			accession = patternSubstrateAccession.group(1);
			accession = accession.trim();
			subtratedatabase.setS_UniprotID(accession);
			// System.out.println(accession);
		} else {
			accession = "n.d.";
			subtratedatabase.setS_UniprotID(accession);
			// System.out.println(accession);
		}
		return accession;
	}

	private BufferedReader createBufferedreader(String datafilename)
			throws FileNotFoundException {
		BufferedReader bReader = new BufferedReader(
				new FileReader(datafilename));
		return bReader;

	}

	private Document parseUniprot(String url) {
		ParseUniprot parser = new ParseUniprot();
		Document xml = parser.getXML(url);
		xml.getXmlVersion();
		return xml;
	}

	private NodeList getEntries(String query, Document xml) {
		XPathUniprot XPather = new XPathUniprot();
		NodeList entrylist = XPather.getNodeListByXPath(query, xml);
		return entrylist;
	}

	private void getUniSubstratepproteinname(NodeList entries, int i,
			SubstrateDatabaseEntry substratedatabase) {
		// GET SUBSTRATE PROTEIN NAME using getInformation method
		LinkedList<String> protnamelist = getInformation(
				"./protein/recommendedName/fullName/text()", entries.item(i));
		String protname = null;
		if (!protnamelist.isEmpty()) {
			protname = protnamelist.getFirst();
			protname = protname.replaceAll(",", "");
			System.out.println(protname);
			substratedatabase.setS_Name(protname);
		}
	}

	private LinkedList<String> getInformation(String query, Node i) {
		XPathNodeUniprot XPathNoder = new XPathNodeUniprot();
		NodeList entrynodelist = XPathNoder.getNodeListByXPath(query, i);
		Loop l1 = new Loop();
		LinkedList<String> information = l1
				.getStringfromNodelist(entrynodelist);
		return information;
	}

	private String getUniSubstrategenename(NodeList entries, int i,
			SubstrateDatabaseEntry substratedatabase) {
		// GET SUBSTRATE GENE NAME using getInformation method
		LinkedList<String> genenamelist = getInformation(
				"./gene/name[@type][1]/text()", entries.item(i));
		String genename = null;
		if (!genenamelist.isEmpty()) {
			genename = genenamelist.getFirst();
			System.out.println(genename);
			substratedatabase.setS_Symbol(genename);
		}
		return genename;
	}

	private String getProteaseNameSymbolId(Matcher patternProteaseName,
			CsDatabaseEntry csdatabase, ProteaseDatabaseEntry proteasedatabase,
			String proteaseTaxon, String commentS) throws IOException {
		String commentP = null;
		if (patternProteaseName.find()) {
			String proteaseName = patternProteaseName.group(1);
			proteaseName = proteaseName.trim();
			proteaseName = proteaseName.replaceAll(",", "");
			proteaseName = proteaseName.replaceAll(";", "");
			commentP = mapProteasetoLibrairy(commentS, proteaseTaxon,
					proteaseName, csdatabase, proteasedatabase);

		} else {
			String proteaseName = "n.d.";
			String proteaseSymbol = "n.d.";
			String proteaseUniprot = "n.d";
			String proteaseBrenda = "n.d.";
			commentP = commentS + ";-";
			proteasedatabase.setP_NL_Name(proteaseName);
			proteasedatabase.setP_Symbol(proteaseSymbol);
			proteasedatabase.setP_UniprotID(proteaseUniprot);
			proteasedatabase.setP_EC_Number(proteaseBrenda);
			csdatabase.setProtease(proteasedatabase);
			System.out.println(proteaseName);
			System.out.println(proteaseSymbol);
			System.out.println(proteaseUniprot);
			System.out.println(proteaseBrenda);
		}
		return commentP;
	}

	private String mapProteasetoLibrairy(String commentS, String proteaseTaxon,
			String proteaseName, CsDatabaseEntry csdatabase,
			ProteaseDatabaseEntry proteasedatabase) throws IOException {
		String commentP;
		commentP = commentS
				+ "; Check Protease Symbol and Accession; add to Substrate Librairy";
		if (proteaseTaxon.equalsIgnoreCase("Homo Sapiens")) {
			BufferedReader bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/ProteaseHSALibrairy.txt");
			commentP = getProteaseInformation(bReader, proteaseName,
					csdatabase, proteasedatabase, commentS);
		} else {
			if (proteaseTaxon.equalsIgnoreCase("Rattus Norvegicus")) {
				BufferedReader bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/ProteaseRNOLibrairy.txt");
				commentP = getProteaseInformation(bReader, proteaseName,
						csdatabase, proteasedatabase, commentS);
			} else {
				if (proteaseTaxon.equalsIgnoreCase("Mus Musculus")) {
					BufferedReader bReader = createBufferedreader("/Users/julieklein/Dropbox/ProteasiX/LIBRAIRIES/ProteaseMMULibrairy.txt");
					commentP = getProteaseInformation(bReader, proteaseName,
							csdatabase, proteasedatabase, commentS);
				}
			}
		}
		return commentP;
	}

	private String getProteaseInformation(BufferedReader bReader,
			String proteaseName, CsDatabaseEntry csdatabase,
			ProteaseDatabaseEntry proteasedatabase, String commentS)
			throws IOException {
		String line;
		String commentP = null;
		proteasedatabase.setP_NL_Name(proteaseName);
		proteasedatabase.setP_Name("to check");
		proteasedatabase.setP_EC_Number("to check");
		proteasedatabase.setP_UniprotID("to check");
		while ((line = bReader.readLine()) != null) {
			String splitarray[] = line.split("\t");
			String naturallanguage = splitarray[0];
			naturallanguage = naturallanguage.replaceAll("\"", "");
			naturallanguage = naturallanguage.replaceAll(",", "");
			naturallanguage = naturallanguage.replaceAll(";", "");
			if (naturallanguage.equalsIgnoreCase(proteaseName)) {
				String proteaseSymbol = splitarray[1];
				proteaseSymbol = proteaseSymbol.replaceAll("sept-0", "SEPT");
				String proteaseUniprot = splitarray[2];
				String proteaseBrenda = splitarray[3];

				if (proteaseUniprot.contains("n.d")) {
					proteasedatabase.setP_Name("n.d.");
					proteasedatabase.setP_UniprotID(proteaseUniprot);
					proteasedatabase.setP_EC_Number(proteaseBrenda);
					csdatabase.setProtease(proteasedatabase);
				} else {
					String UniprotURL = "http://www.uniprot.org/uniprot/"
							+ proteaseUniprot + ".xml";
					NodeList entries = getEntries("/uniprot/entry",
							parseUniprot(UniprotURL));
					for (int i = 0; i < entries.getLength(); i++) {
						getUniProteasepproteinname(entries, i, proteasedatabase);
						String genename = getUniProteasegenename(entries, i,
								proteasedatabase);
					}
					commentP = commentS + ";-";
					proteasedatabase.setP_UniprotID(proteaseUniprot);
					proteasedatabase.setP_EC_Number(proteaseBrenda);
					csdatabase.setProtease(proteasedatabase);
					System.out.println(proteaseUniprot);
					System.out.println(proteaseBrenda);
				}
			}

		}
		return commentP;
	}

	private String getUniProteasegenename(NodeList entries, int i,
			ProteaseDatabaseEntry proteasedatabase) {
		// GET SUBSTRATE GENE NAME using getInformation method
		LinkedList<String> genenamelist = getInformation(
				"./gene/name[@type][1]/text()", entries.item(i));
		String genename = null;
		if (!genenamelist.isEmpty()) {
			genename = genenamelist.getFirst();
			System.out.println(genename);
			proteasedatabase.setP_Symbol(genename);
		}
		return genename;
	}

	private void getUniProteasepproteinname(NodeList entries, int i,
			ProteaseDatabaseEntry proteasedatabase) {
		// GET SUBSTRATE PROTEIN NAME using getInformation method
		LinkedList<String> protnamelist = getInformation(
				"./protein/recommendedName/fullName/text()", entries.item(i));
		String protname = null;
		if (!protnamelist.isEmpty()) {
			protname = protnamelist.getFirst();
			protname = protname.replaceAll(",", "");
			System.out.println(protname);
			proteasedatabase.setP_Name(protname);
		}
	}

	private void getCleavagesitePosition(Matcher patternCleavagesitePosition,
			CsDatabaseEntry csdatabase) throws NumberFormatException {
		while (patternCleavagesitePosition.find()) {
			String position = patternCleavagesitePosition.group(1);
			position = position.trim();
			if (position.equalsIgnoreCase("No_information")) {
				int intP1 = 0;
				csdatabase.setP1_Position(intP1);
				int intP1prime = 0;
				csdatabase.setP1prime_Position(intP1prime);
				System.out.println(intP1);
				System.out.println(intP1prime);
			} else {
				// System.out.println(position);
				String positionSplit[] = position.split("-");
				String P1 = positionSplit[0];
				// String P1prime = positionSplit[1];
				int intP1 = Integer.parseInt(P1);
				csdatabase.setP1_Position(intP1);
				// int intP1prime = Integer.parseInt(P1prime);
				int intP1prime = intP1 + 1;
				csdatabase.setP1prime_Position(intP1prime);
				System.out.println(intP1);
				System.out.println(intP1prime);
			}
		}
	}

	private String getCleavagesiteSequence(Matcher patternCleavagesiteSequence,
			CsDatabaseEntry csdatabase) {
		String csSequence = null;
		while (patternCleavagesiteSequence.find()) {
			csSequence = patternCleavagesiteSequence.group(1);
			csSequence = csSequence.trim();
			if (csSequence.equalsIgnoreCase("No_information")) {
				String aaP1 = "?";
				String aaP1prime = "?";
				csdatabase.setP1_Sequence(aaP1);
				csdatabase.setP1prime_Sequence(aaP1prime);
				System.out.println(aaP1);
				System.out.println(aaP1prime);
				csdatabase.setCleavagesiteseaquence("no information");
				System.out.println("no information");

			} else {
				if (csSequence.length() < 9) {
					if (csSequence.substring(4, 5).equalsIgnoreCase("-")) {
						int difference = 9 - csSequence.length();
						for (int i = 0; i < difference; i++) {
							csSequence = csSequence + "-";
						}

					} else if (csSequence.substring(2, 3).equalsIgnoreCase("-")
							|| csSequence.substring(3, 4).equalsIgnoreCase("-")) {
						int difference = 9 - csSequence.length();
						for (int i = 0; i < difference; i++) {
							csSequence = "-" + csSequence;
						}
					}
				}
				System.out.println(csSequence.length());
				String positionSplit[] = csSequence.split("");
				String aaP1 = positionSplit[4];
				String aaP1prime = positionSplit[6];
				csdatabase.setP1_Sequence(aaP1);
				csdatabase.setP1prime_Sequence(aaP1prime);
				System.out.println(aaP1);
				System.out.println(aaP1prime);
				String csSequencenodash = csSequence.substring(1, 4)
						+ csSequence.substring(5, 8);

				csdatabase.setCleavagesiteseaquence(csSequencenodash);
				System.out.println(csSequence);
				System.out.println(csSequencenodash);
			}
		}
		return csSequence;
	}

	private void getPmid(Matcher patternPmid, CsDatabaseEntry csdatabase) {
		if (patternPmid.find()) {
			String pmid = patternPmid.group(1);
			pmid = pmid.trim();
			pmid = pmid.replaceAll(",", ";");
			csdatabase.setPMID(pmid);
			System.out.println(pmid);
		} else {
			String pmid = "-";
			csdatabase.setPMID(pmid);
			System.out.println(pmid);
		}
	}

	private void getErrorUnmatched(Matcher patternErrorUnmatched,
			CsDatabaseEntry csdatabase) {
		if (patternErrorUnmatched.find()) {
			String errormunmatched = patternErrorUnmatched.group(1);
			String error = "Unmatched cleavage site; Cleavage site discarded";
			csdatabase.setCuration_Status(error);
			System.out.println(error);

		} else {
			String error = "-";
			csdatabase.setCuration_Status(error);
			System.out.println(error);
		}

	}

	private String getUniSubstratesequence(NodeList entries, int i,
			SubstrateDatabaseEntry substratedatabase) {
		// GET PROTSEQUENCE using getInformation method
		String sequence = getInformation("/uniprot/entry/sequence/text()",
				entries.item(i)).getFirst();
		sequence = sequence.replaceAll("\n", "");
		return sequence;
	}

	private void populateData(PrintStream csvWriter, CsDatabaseEntry csdatabase) {
		// System.out.println(cleavageSiteDBEntry);

		csvWriter.print(csdatabase.protease.getP_Name());
		csvWriter.print(",");
		csvWriter.print(csdatabase.protease.getP_Symbol());
		csvWriter.print(",");
		csvWriter.print(csdatabase.protease.getP_UniprotID());
		csvWriter.print(",");
		csvWriter.print(csdatabase.protease.getP_EC_Number());
		csvWriter.print(",");
		csvWriter.print(csdatabase.protease.getP_Taxon());
		csvWriter.print(",");
		csvWriter.print(csdatabase.substrate.getS_Name());
		csvWriter.print(",");
		csvWriter.print(csdatabase.substrate.getS_Symbol());
		csvWriter.print(",");
		csvWriter.print(csdatabase.substrate.getS_UniprotID());
		// csvWriter.print(",");
		// csvWriter.print(csdatabase.substrate.getSubstratesequence());
		csvWriter.print(",");
		csvWriter.print(csdatabase.substrate.getS_Taxon());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getCleavagesiteseaquence());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getP1_Position());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getP1prime_Position());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getP1_Sequence());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getP1prime_Sequence());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getExternal_Link());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getPMID());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getComment());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getCuration_Status());
		csvWriter.print(",");
		csvWriter.print(csdatabase.getCreation_Date());
		csvWriter.print("\n");
	}

}
