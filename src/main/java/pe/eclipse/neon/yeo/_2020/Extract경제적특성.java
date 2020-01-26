package pe.eclipse.neon.yeo._2020;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.eclipse.neon.yeo._2017.y201712.Dictionary;
import pe.eclipse.neon.yeo._2017.y201712.ExcelReport;
import pe.eclipse.neon.yeo._2017.y201712.FileRW;

public class Extract������Ư�� extends FileRW {
	
	Logger log = LoggerFactory.getLogger(getClass().getName());

	File srcPath = null;
	Boolean isScopus = false;
	boolean checkFS = false;
	Map<String, String> totalDatas = new HashMap<String, String>();
	Map<String, String> totalDatasTech = new HashMap<String, String>();

	/**
	 * @param path
	 *            �м� ����� �ִ� ���� �н�
	 */
	public Extract������Ư��(String path) {
		srcPath = new File(path);
		listingFile(srcPath);
		flist.addAll(0, fslist);
		flist.addAll(flist.size(), fplist);
		
		for (File f : flist) {
			if (f.getParent().indexOf("��") != -1) {
				isScopus = true;
			} else if (f.getParent().indexOf("Ư��") != -1) {
				isScopus = false;
			}
//			log.info("q read File " + f.getName() + "\t[SCOPUS doc]:" + isScopus);
			 ana(f);
		}
		// ����� ��ǥ�� �ۼ��Ѵ�.
		 createReport������������();
	}

	List<File> fslist = new LinkedList<File>();
	List<File> fplist = new LinkedList<File>();
	List<File> flist = new LinkedList<File>();

	public int COMPARETYPE_NAME = 0;
	public int COMPARETYPE_DATE = 1;

	public File[] sortFileList(File[] files, final int compareType) {

		Arrays.sort(files, new Comparator<Object>() {
			@Override
			public int compare(Object object1, Object object2) {

				String s1 = "";
				String s2 = "";

				if (compareType == COMPARETYPE_NAME) {
					s1 = ((File) object1).getName();
					s2 = ((File) object2).getName();
				} else if (compareType == COMPARETYPE_DATE) {
					s1 = ((File) object1).lastModified() + "";
					s2 = ((File) object2).lastModified() + "";
				}

				return -s1.compareTo(s2);

			}
		});

		return files;
	}

	private void listingFile(File file) {
		File[] files = file.listFiles();
		files = sortFileList(files,COMPARETYPE_NAME); // Date�� Sort����
		if (file != null) {
			for (File _file : files) {
				// log.info(_file.getName() +"\t" + firstPatent);
				if (_file.isDirectory()) {
					// log.info("Dir " + _file.getName());
					if (_file.getName().indexOf("��") != -1) {
						isScopus = true;
					} else if (_file.getName().indexOf("Ư��") != -1) {
						isScopus = false;
					}
					listingFile(_file);
				} else {
					// �м� ��� ������ �д´�.
					if (_file.isFile() && !_file.getName().endsWith(".txt")) {
						continue;
					}
					if (isScopus) {
						fslist.add(0, _file);
					} else {
						fplist.add(0, _file);
					}
				}
			}
		}

	}

	private String currentFile = null;

	/**
	 * 
	 * @param _file
	 */
	private void ana(File _file) {
		BufferedReader br = createReader(_file, null);
		currentFile = _file.getAbsolutePath();
		readline(br);
		// System.exit(1);
	}

	/**
	 * @param path
	 *            �м� ����� �ִ� ���� �н�
	 */
	public Extract������Ư��() {
		this("");
	}

	SortedMap<Integer, Integer> sm1 = new TreeMap<Integer, Integer>();

	/**
	 * ������ ����
	 */
	public void countPerYear(String py) {
		if (py == null)
			return;
		try {
			int year = Integer.parseInt(py);
			int cnt = 1;
			if (sm1.containsKey(year)) {
				cnt = sm1.get(year) + 1;
			}
			sm1.put(year, cnt);
		} catch (Exception e) {
			// ignore
		}

	}

	SortedMap<String, String> sm2 = new TreeMap<String, String>();
	SortedMap<String, String> sm3 = new TreeMap<String, String>();
	SortedMap<String, String> sm4 = new TreeMap<String, String>();
	SortedMap<String, String> sm5 = new TreeMap<String, String>();
	SortedMap<String, String> sm22 = new TreeMap<String, String>();

	Map<String, String> �ѱ��ؿܳ��Ǽ� = new HashMap<String, String>();
	LinkedList<String> ������������ = new LinkedList<String>();
	LinkedList<String> ��������������ü = new LinkedList<String>();

	/**
	 * XX�� ����
	 */
	public void countPerKeyString(Map<String, String> _s, String cn) {
		if (cn == null)
			return;
		try {
			int cnt = 1;
			if (_s.containsKey(cn)) {
				cnt = Integer.parseInt(_s.get(cn)) + 1;
			}
			_s.put(cn.toUpperCase(), String.valueOf(cnt));
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * ����з��� ������
	 */
	public void countPerTech() {

	}

	SortedMap<String, LinkedList<String>> docuList = new TreeMap<String, LinkedList<String>>();

	/**
	 * �� ����Ʈ<br>
	 * ���� : ������ <br>
	 * �÷� : ����, ������, ����, �Ҽӱ��(��������), ������, ASJC�з���, DOI <br>
	 */
	public void listDocument(String py, String datas) {
		if (py == null)
			return;
		if (datas == null)
			return;
		try {
			// log.info(py + "\t" + docuList.keySet());
			LinkedList<String> list = null;
			list = docuList.get(py);
			if (list == null) {
				list = new LinkedList<String>();
			}
			list.add(datas);
			docuList.put(py, list);
		} catch (Exception e) {
			// ignore
		}

	}

	private void init() {
		sm1.clear();
		sm2.clear();
		sm22.clear();
		sm3.clear();
		sm4.clear();
		sm5.clear();
		docuList.clear();
	}

	private String getMapData(Map<Integer, String> clmData, Map<String, Integer> clmIdxData, String fieldName) {
		String result = " ";
//		log.info(clmData +"\t" + clmIdxData + "\t" + fieldName);
		if (fieldName != null) {
			fieldName = fieldName.toUpperCase().trim();
		}
		if (clmIdxData.containsKey(fieldName)) {
			int idx = clmIdxData.get(fieldName);
			if (clmData.containsKey(idx)) {
				result = clmData.get(idx);
			}
		}
		return result;
	}

	@Override
	public void readline(BufferedReader br) {
		String line = null;
		init();
		if (isScopus) {
			sm3.put("������", "0");
			sm3.put("�ؿܳ�", "0");
		} else {
			sm5.put("����Ư��", "0");
			sm5.put("�ؿ�Ư��", "0");
		}
		try {
			StringBuilder buf = new StringBuilder();
			if (isScopus) {
				int rowIdx = 0;
				Map<String, Integer> clmIdxData = new HashMap<String, Integer>();
				Map<Integer, String> clmData = new HashMap<Integer, String>();
				while ((line = br.readLine()) != null) {
//					 log.info("SCOPUS] " + line);
					try {
						String[] st = line.split("\t");  
						int stLength=st.length;
						if (rowIdx == 0) {
							int cellIdx = 0;
							while (cellIdx<stLength) {
								String nt = st[cellIdx].trim();
								clmIdxData.put(nt.toUpperCase(), cellIdx);
								cellIdx++;
							}
//							log.info(clmIdxData);
							rowIdx++;
							continue;
						}

						clmData.clear();
						int idx = 0;
						while (idx<stLength) {
							String nt = st[idx];
							clmData.put(idx++, nt.trim());
						}
						String countryCode = getMapData(clmData, clmIdxData, "FIRST_AUTHOR_COUNTRYCODE").toUpperCase()
								.trim();
//						log.info("FIRST_AUTHOR_COUNTRYCODE : "+countryCode);
						if ("".equals(countryCode)) {
							countPerKeyString(this.sm2, "����");
						} else {
							countPerKeyString(this.sm2, countryCode);
						}

						if ("KOR".equalsIgnoreCase(countryCode)) {
							countPerKeyString(sm3, "������");
						} else {
							if (!"".contentEquals(countryCode)) {
								countPerKeyString(sm3, "�ؿܳ�");
							}
						}

						String py = getMapData(clmData, clmIdxData, "YEAR");
						countPerYear(py);
						buf.setLength(0);
						buf.append(getMapData(clmData, clmIdxData, "EID"));
						buf.append("\t");
						buf.append(getMapData(clmData, clmIdxData, "TITLE"));
						buf.append("\t");
						buf.append(py);
						buf.append(" \t");
						buf.append(getMapData(clmData, clmIdxData, "KEYWORD"));
						buf.append(" \t");
						buf.append(getMapData(clmData, clmIdxData, "INDEX_KEYWORD"));
						buf.append(" \t");
						buf.append(getMapData(clmData, clmIdxData, "NUMBER_CITATION"));
						buf.append(" \t");
						buf.append(getMapData(clmData, clmIdxData, "FIRST_AUTHOR_COUNTRYCODE"));
						buf.append(" \t");
						buf.append(getMapData(clmData, clmIdxData, "FIRST_AFFILIATION_NAME"));
						buf.append(" \t");
						buf.append(getMapData(clmData, clmIdxData, "SOURCE_SOURCETITLE"));
						buf.append(" \n");
						listDocument(py, buf.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					rowIdx++;
				}
			} else {
				// G-PASS
				int rowIdx = 0;
				Map<String, Integer> clmData = new HashMap<String, Integer>();
				int pnoIdx = 0;
				int tiIdx = 0;
				int pnyearIdx = 0;
				int assigneeIdx = 0;
				int ipcIdx = 0;
				int pndateIdx = 0;
				int absIdx = 0;
				int appnoIdx = 0;
				int appYearIdx = 0;
				int authorityIdx = 0;

				LinkedHashSet<String> cnSet = new LinkedHashSet<String>();
				while ((line = br.readLine()) != null) {
					// log.info("SCOPUS] " + line);
					try {
						//StringTokenizer st = new StringTokenizer(line, "\t"); //�̺κп� �ڵ������� ����
						String[] st = line.split("\t");  
						int stLength=st.length;
						if (rowIdx == 0) {
							int cellIdx = 0;							
							while (cellIdx<stLength) {
								String nt = st[cellIdx].trim();
								// log.info(nt + "\t" + cellIdx);
								clmData.put(nt, cellIdx);
								if ("pno".equalsIgnoreCase(nt)) {
									pnoIdx = cellIdx;
								} else if ("ti".equalsIgnoreCase(nt)) {
									tiIdx = cellIdx;
								} else if ("pnyear".equalsIgnoreCase(nt)) {
									pnyearIdx = cellIdx;
								} else if ("assignee".equalsIgnoreCase(nt)) {
									assigneeIdx = cellIdx;
								} else if ("pndate".equalsIgnoreCase(nt)) {
									pndateIdx = cellIdx;
								} else if ("abs".equalsIgnoreCase(nt)) {
									absIdx = cellIdx;
								} else if ("appno".equalsIgnoreCase(nt)) {
									appnoIdx = cellIdx;
								} else if ("ipc".equalsIgnoreCase(nt)) {
									ipcIdx = cellIdx;
									
								} else if ("appyear".equalsIgnoreCase(nt)) {
									appYearIdx = cellIdx;
								} else if ("authority".equalsIgnoreCase(nt)) {
									authorityIdx = cellIdx;
								}
								cellIdx++;
							}
							rowIdx++;
							continue;
						}

						int idx = 0;
						String pno = "";
						String ti = "";
						String pnyear = "";
						String pndate = "";
						String appno = "";
						String abs = "";
						String appyear = "";
						String assignee = "";
						String ipc = "";
						String au = "";
						String firstIPC = "";
						while (idx<stLength) {
							String nt = st[idx];
							if (idx == pnoIdx) {
								pno = nt;
							} else if (idx == tiIdx) {
								ti = nt;
							} else if (idx == pnyearIdx) {
								pnyear = nt;
							} else if (idx == authorityIdx) {
								au = nt;
							} else if (idx == assigneeIdx) {
								assignee = nt;
							} else if (idx == pndateIdx) {
								pndate = nt;
							} else if (idx == absIdx) {
								abs = nt;
							} else if (idx == appnoIdx) {
								appno = nt;
							} else if (idx == appYearIdx) {
								appyear = nt;
							} else if (idx == ipcIdx) {
								ipc = nt;
								String[] ipcs = ipc.split(";");
								cnSet.clear();
								for (String _ipc : ipcs) {
									if (_ipc.length() > 3) {
										String ip = _ipc.substring(0, 4);
//										log.info("aaaaaa:  " + ipcIdx + "   " + ip);
										cnSet.add(ip.trim());
										firstIPC = _ipc.trim().toUpperCase().replaceAll("\\s", "");
										// break;
									}
								}
							}
							idx++;
						}

						// ti application-number application-Year assignee-country

						// GB`LOUGHBOROUGH UNIVERSITY OF TECHNOLOGY;GB`ADCOCK PAUL
						Set<String> assSets = new LinkedHashSet<>();
						String[] asss = assignee.split(";");
						for (String _asss : asss) {
							String[] values = _asss.split("`");
							if (values.length > 1) {
								if (!"".equals(values[0].trim().toUpperCase())) {
									assSets.add(values[0].trim().toUpperCase());
								}
							}
						}

						if ("US".equalsIgnoreCase(au) || "EP".equalsIgnoreCase(au) || "WO".equalsIgnoreCase(au)) {
							for (String _ipc : cnSet) {
								countPerKeyString(sm3, _ipc);
								countPerKeyString(sm4, Dictionary.getInstance().findKSCIIPC(_ipc));
//								log.info(" {}", Dictionary.getInstance().findKSCIIPC(_ipc));
							}
							countPerYear(pnyear);
							countPerKeyString(sm2, au);

							if (assSets.contains("KR")) {
								countPerKeyString(sm5, "����Ư��");
							} else {
								countPerKeyString(sm5, "�ؿ�Ư��");
							}

							for (String cn : assSets) {
								countPerKeyString(sm22, cn);
							}

							buf.setLength(0);
							buf.append(ti);
							buf.append("\t");
							buf.append(appno);
							buf.append(" \t");
							buf.append(appyear);
							buf.append(" \t");
							buf.append(assSets.toString().replaceAll("[\\[\\]]", ""));
							buf.append(" \t");
							totalDatas.put(pno, firstIPC);
							buf.append("\n");
							listDocument(appyear, buf.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					rowIdx++;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		createReportForTech();
	}

	/**
	 * ������ �ۼ��Ѵ�.<br>
	 * ������ ����� ��Ʈ�� �ۼ��Ѵ�.<br>
	 */
	private void createReportForTech() {
		ExcelReport er = new ExcelReport(isScopus);
		Map<String, String> �������������� = null;

		File f = new File(currentFile);
		String fileName = f.getName();
		if(fileName.contains("bulk")) {
			fileName = fileName.replaceAll("\\.bulk.*$", "")+".txt";
		}
		fileName = fileName.substring(0, fileName.lastIndexOf("."));

		if (isScopus) {

			er.createExcelSheetForCount(sm1);
			er.createExcelSheetForCountCustom("��1������ ������ ����", sm2, new String[] { "��1����", "����" });
			// er.createExcelSheetForCountCustom("������ ����", sm2, new String[] { "����", "����"
			// });
			�ѱ��ؿܳ��Ǽ�.put(fileName, sm3.get("������") + ":" + sm3.get("�ؿܳ�"));
			log.info("�ѱ� / �ؿ� �� �� : {}, <= {}", �ѱ��ؿܳ��Ǽ�.get(fileName), fileName);

			// er.createExcelSheetForCountCustom("��1������ ������ ����", sm2, new String[] {
			// "��1����", "����" });
			// er.createExcelSheetForCountCustom("�������� ������ ����\n���� 10��",
			// extractTopCount(sm22, 10), new String[] { "����", "����" });
			// er.createExcelSheetForRateCustom("����з��� ������", extractTopRate(sm3, 5), new
			// String[] { "�з�", "������" });
			er.createExcelSheetForDocumentList("�� ����Ʈ", docuList);

		} else {
			// er.createExcelSheetForCount(sm1);
			// er.createExcelSheetForCountCustom("�������", sm22, new String[] {"����� ����",
			// "Ư���"});
			// er.createExcelSheetForCountCustom("�����(Ư��û)", sm2, new String[] {"�����(Ư��û)",
			// "Ư���"});
			// er.createExcelSheetForRateCustom("����з��� ������", extractTopRate(sm3, 5), new
			// String[] { "IPC", "������" });
			�������������� = extractTopRate(sm4, 1000);
			er.createExcelSheetForRateCustom("KSCI ��� ������", ��������������, new String[] { "�����", "������" }, "");
			er.createExcelSheetForDocumentList("Ư�㸮��Ʈ", docuList);
		}
		// 9.973333333����Ʈ�׸���_�ߺз�08_patent.xlsx

		/** ����� ������� �ӽú��� */

		/** �⺻ �Ҽ��� ���ڸ����� �ݿø� �Ѵ�. */
		if (�������������� != null && ��������������.size() > 0) {
//			log.info("�������������� : " + ��������������);
			double[] ������ = Dictionary.getInstance().get������(��������������);
			StringBuilder buf = new StringBuilder();
			buf.append(fileName);
			buf.append("\t");

			String _���Ǽ� = �ѱ��ؿܳ��Ǽ�.get(fileName);
			log.info("_���Ǽ� : {}", _���Ǽ�);
			if (_���Ǽ� != null) {
				String[] _dd = _���Ǽ�.split(":");
				buf.append(_dd[0]);
				buf.append(" \t ");
				buf.append(_dd[1]);
				buf.append(" \t ");
			} else {
				buf.append("0\t0\t");
			}

			Set<String> sm5Keys = sm5.keySet();
			// log.info(sm5Keys);
			for (String _sk : sm5Keys) {
				/** ����Ư�� �� / �ؿ� Ư�� �� */
				String v = sm5.get(_sk);
				if (v == null)
					v = "_0";
				buf.append(v);
				buf.append("\t");
			}

			sm5.clear();

			for (double _d : ������) {
				// log.info(_d);
				buf.append(String.format("%.3f", _d));
				// buf.append(Math.round(_d * p1) / p2);
				buf.append("\t");
			}
			buf.deleteCharAt(buf.length() - 1);
			������������.add(buf.toString());
			 log.info(buf.toString());

		}
		String path = f.getParent() + File.separator + fileName + ".xlsx";
//		log.info(" writeFile ; " + path);

		er.writeExcel(path);

	}

	private void createReportForTechAll() {
		/* totalDatas���� �ߺ����ŵ� ��ü Ư�� ����� �ִ�. */
		Set<String> tdSet = totalDatas.keySet();
		for (String pno : tdSet) {
			String ipc = totalDatas.get(pno);
			// log.info(pno + "\t" + ipc);
			if ("".equals(ipc.trim())) {
				countPerKeyString(totalDatasTech, "None");
			} else {
				countPerKeyString(totalDatasTech, Dictionary.getInstance().findKSCIIPC(ipc.trim()));
			}
		}

		// log.info(this.totalDatasTech.size());
		Map<String, String> ��������������_��ü = extractTopRate(this.totalDatasTech, 1000);
		/** �⺻ �Ҽ��� ���ڸ����� �ݿø� �Ѵ�. */
		if (��������������_��ü != null && ��������������_��ü.size() > 0) {
//			log.info("��������������_��ü �׸� : " + ��������������_��ü);
			double[] ������ = Dictionary.getInstance().get������(��������������_��ü);
			StringBuilder buf = new StringBuilder();
			buf.append("��ü");
			buf.append("\t");

			for (double _d : ������) {
				// log.info(_d);
				buf.append(String.format("%.3f", _d));
				// buf.append(Math.round(_d * p1) / p2);
				buf.append("\t");
			}
			buf.deleteCharAt(buf.length() - 1);
			��������������ü.add(buf.toString());
//			log.info("��������������ü ====> " + buf.toString());

		}
	}

	/**
	 * ����� ��������� ����Ѵ�.<br>
	 * �̰� Ư�� or �� ���ϵ��� ���� �������ϰ� ���������� �Ѵ�.
	 */
	private void createReport������������() {
		ExcelReport er2 = new ExcelReport(isScopus);
		/**
		 * ���⼭�� currentFile�� ���� �������� �м��� �۾����ϸ��� �ǰ� �� ������ �ٷ� �������� ���� ������� ������ ����.
		 */
		File f = new File(currentFile);
		if (sm4.size() > 0) {
			Map<String, String[]> ����������MAP = new LinkedHashMap<String, String[]>();
			for (String _datas : ������������) {
				String[] result = _datas.split("\t");
				String techName = result[0];
				����������MAP.put(techName, Arrays.copyOfRange(result, 1, result.length));
			}
			er2.createExcelSheetForCountCustom3("�������", ����������MAP, new String[] { "�����(����)", "������", "�ؿܳ�", "����Ư��",
					"�ؿ�Ư��", "������߰��", "�ΰ���ġ���߰��(�ݿø�)", "������(�ݿø�)", "�����(�ݿø�)" });

			createReportForTechAll();
			����������MAP = new LinkedHashMap<String, String[]>();
			for (String _datas : ��������������ü) {
				String[] result = _datas.split("\t");
				String techName = result[0];
				����������MAP.put(techName, Arrays.copyOfRange(result, 1, result.length));
			}
			er2.createExcelSheetForCountCustom3("���������ü", ����������MAP,
					new String[] { "�����(����)", "������߰��", "�ΰ���ġ���߰��(�ݿø�)", "������(�ݿø�)", "�����(�ݿø�)" });

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			String path = f.getParentFile().getParent() + File.separator + format.format(new Date()) + "_����� ��� ����.xlsx";
			er2.writeExcel(path);
			log.info("������������ : " + path);

		}
	}

	@Override
	public void writerline(BufferedWriter bw) {

	}

	public Map<String, String> extractTopRate(Map<String, String> map, int top) {
		double total = 0d;
		Set<String> mks = map.keySet();
		for (String _key : mks) {
			try {
				total += Double.parseDouble(map.get(_key) + "");
			} catch (Exception e) {
				// ignore
			}
		}

		ValueComparator vc = new ValueComparator(map);
		Map<String, String> vsmv = new TreeMap<String, String>(vc);
		vsmv.putAll(map);

		Set<String> ks = vsmv.keySet();
		// log.info(ks);
		int idx = 0;
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		double etcCnt = 0;
		double lastCnt = 0;
		double sum = 0d;
		for (String _key : ks) {
			// log.info(map.get(_key));
			Object obj = map.get(_key);
			if (obj == null)
				continue;
			try {
				double objValue = Double.parseDouble(String.valueOf(obj));
				if (idx < top) {
					result.put(_key.toUpperCase(), String.valueOf((double) ((objValue / total))));
					lastCnt = objValue;
				} else {
					double vcnt = Double.parseDouble(String.valueOf(obj));
					if (lastCnt == vcnt) {
						result.put(_key.toUpperCase(), String.valueOf((double) ((objValue / total))));
					} else {
						etcCnt += vcnt;
					}
				}
				sum += objValue;
			} catch (Exception e) {
				// ignore
				e.printStackTrace();
				log.info("===> " + obj);
			}
			idx++;
		}
		if (etcCnt > 0) {
			result.put("ETC ", String.valueOf((double) ((etcCnt / total))));
		}
		result.put("�հ� ", String.valueOf((double) (sum / total)));
		// log.info(result);
		// System.exit(01);
		return result;
	}

	public Map<String, String> extractTopCount(Map<String, String> map, int top) {
		int total = 0;
		Set<String> mks = map.keySet();
		for (String _key : mks) {
			try {
				total += Double.parseDouble(map.get(_key) + "");
			} catch (Exception e) {
				// ignore
			}
		}

		ValueComparator vc = new ValueComparator(map);
		Map<String, String> vsmv = new TreeMap<String, String>(vc);
		vsmv.putAll(map);

		Set<String> ks = vsmv.keySet();
		// log.info(ks);
		int idx = 0;
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		int etcCnt = 0;
		int lastCnt = 0;
		int sum = 0;
		for (String _key : ks) {
			// log.info(map.get(_key));
			Object obj = map.get(_key);
			if (obj == null)
				continue;
			try {
				int objValue = Integer.parseInt(String.valueOf(obj));
				if (idx < top) {
					result.put(_key.toUpperCase(), String.valueOf(objValue));
					lastCnt = objValue;
				} else {
					int vcnt = Integer.parseInt(String.valueOf(obj));
					if (lastCnt == vcnt) {
						result.put(_key.toUpperCase(), String.valueOf(objValue));
					} else {
						etcCnt += vcnt;
					}
				}
				sum += objValue;
			} catch (Exception e) {
				// ignore
				e.printStackTrace();
				log.info("===> " + obj);
			}
			idx++;
		}
		if (etcCnt > 0) {
			result.put("ETC ", String.valueOf(etcCnt));
		}
		result.put("�հ� ", String.valueOf(sum));
		// log.info(result);
		// System.exit(01);
		return result;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> soryByValues(Map<K, V> map) {
		return map.entrySet().stream().sorted()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static class ValueComparator implements Comparator<String> {
		Map<String, String> map;

		public ValueComparator(Map<String, String> map) {
			this.map = map;
		}

		@Override
		public int compare(String arg0, String arg1) {
			try {
				if (Integer.parseInt(map.get(arg0)) >= Integer.parseInt(this.map.get(arg1))) {
					return -1;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return 1;
		}

	}

	public static void main(String... args) {
		// new Extract������Ư��("F:\\workspace\\2017\\Test\\data\\");
		new Extract������Ư��("c:\\Users\\coreawin\\OneDrive - hansung.ac.kr\\21.KISTI\\21.���\\2020.01.10\\������\\");
		// new Extract������Ư��("F:\\workspace\\2017\\Test\\�������ڵ���_��_Ư��˻�\\");

		// Map<String, Integer> sm113 = new TreeMap<String, Integer>();
		// sm113.put("A", 200);
		// sm113.put("AB", 100);
		// sm113.put("ABQ", 30);
		// sm113.put("ABC", 500);
		//
		// ValueComparator vc = new ValueComparator(sm113);
		// Map<String, Integer> vsmv = new TreeMap<String, Integer>(vc);
		// vsmv.putAll(sm113);
		//
		// log.info(vsmv);
	}

}
