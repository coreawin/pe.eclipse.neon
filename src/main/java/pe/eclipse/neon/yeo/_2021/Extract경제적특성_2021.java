package pe.eclipse.neon.yeo._2021;

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
import java.util.zip.DataFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.eclipse.neon.yeo._2017.y201712.Dictionary;
import pe.eclipse.neon.yeo._2017.y201712.ExcelReport;
import pe.eclipse.neon.yeo._2017.y201712.FileRW;

/**
 * <P>
 * A. �۾���� ����˻����� �̿��Ͽ� ���� SCOPUS, PATENT �ٿ�ε带 �õ��Ѵ�.<br>
 * 1. NEON_ECLIPSE_KISTI_SCOPUS_SEARCHER<br>
 * 2. NEON_ECLIPSE_KISTI_PATENT_SEARCHER<br> 
 * _2021.Rnd package.ScopusSearcher_2021.java <br>
 * �߿� : Ư��˻����ϸ�� ���˻����ϸ��� ���ƾ� �Ѵ�. (������ ���и� �ȴ�.)
 * </P>
 * 
 * <P>
 * B. �����м� ��ǥ�� �����Ѵ�. <br>
 * pe.neon ������Ʈ�� �Ʒ� Ŭ������ �̿��� ��/Ư�� ������ǥ�� �����Ѵ�.<br>
 * pe.neon.���._202101.Launcher4Patent  <br>
 * pe.neon.���._202101.Launcher4Scopus<br>
 * ����� :  <br>
 * 	1. RESULT_SCOPUS_�����_�۾���.txt <br>
 * 	2. RESULT_PATENT_�����_�۾���.txt<br>
 * </P>
 * 
 * <P>
 * C. pe.eclipse.neon ������Ʈ�� �̿��Ͽ� ������ Ư�� ������ ���� �����Ѵ�.<br>
 *  pe.eclipse.neon.yeo._2021.Extract������Ư��_2021
 * </P>
 * 
 * �ȳ��ϼ��� �̼���ũ �������Դϴ�. <br/>
 * 
 * R&D PIE �ý��� ������ ���� ���� �帳�ϴ�.<br/>
 * �켱 ���� �Ϸ�� 4�� �а�(��ü 16���а� ��) ��, Ư��˻��� ���� �ص帳�ϴ�.<br/>
 * �˻��� �κп� KISTI���� �˻��� ��� �Ǽ��� ���� ǥ�� �߽��ϴ�. �Ǽ��� ����ϰ� ������ �ɵ��մϴ�.<br/>
 * �� �κп� ���� �۳�� ������ �۾� ��û �帮�ڽ��ϴ�.<br/>
 * (�а��� ������� ��/Ư�� ���, 10�� ��ǥ�� ��)<br/>
 * ���� �����帰�ٿ� ���� �Ʒ��� ���� �ʵ� �߰� ��û �帳�ϴ�.<br/>
 * 
 * <��><br/>
 * 
 * - ���ο��: Number of Citation<br/>
 * Check - ���� ���� �ĺ�: Author Country<br/>
 * Check - ���ڸ� �ĺ�: Author ID<br/>
 * Check - ���ڸ�: Author Name<br/>
 * Check - ���� ���� ����: Author Info<br/>
 * Check - ����� ���� �ĺ�: Affiliation Country<br/>
 * Check - ����� �ĺ�: Affiliation IDs<br/>
 * Check - �����: Affiliation Name<br/>
 * Check
 * 
 * <Ư��><br/>
 * 
 * - ����� ����: assignee-country<br/>
 * check - ��ǥ�����(������ ��?): app_gp<br/>
 * check - ����� ����+��: assignee<br/>
 * check - ���ο��; citation-count<br/>
 * check - ��Ϲ�ȣ:<br/>
 * ==> KIND �׸��� Grant �ΰ� check
 * 
 * @author coreawin
 * @date 2021. 1. 9.
 */
public class Extract������Ư��_2021 extends FileRW {

	static Logger log = LoggerFactory.getLogger("Extract������Ư��_2021.class");

	File srcPath = null;
	Boolean isScopus = false;
	boolean checkFS = false;
	Map<String, String> totalDatas = new HashMap<String, String>();
	Map<String, String> totalDatasTech = new HashMap<String, String>();
	String techName = "";

	/**
	 * �ش� path���� �а����� �����Ͱ� ����ִ�.
	 * 
	 * @param techName
	 *            ����а���
	 * @param path
	 *            ��/Ư�� �м� ����� �ִ� ���� �н�
	 * @throws Exception 
	 */
	public Extract������Ư��_2021(String techName, String path) throws Exception {
		this.techName = techName;
		srcPath = new File(path);
		listingFile(srcPath);
		flist.addAll(0, fslist);
		flist.addAll(flist.size(), fplist);

		for (File f : flist) {
			log.info("read File " + f.getName() + "\t[SCOPUS doc]:" +  isScopus);
			if (f.getParent().indexOf("��") != -1 || f.getParent().indexOf("scopus") != -1) {
				isScopus = true;
			} else if (f.getParent().indexOf("Ư��") != -1 || f.getParent().indexOf("patent") != -1) {
				isScopus = false;
			}
			ana(f);
		}
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		// ����� ��ǥ�� �ۼ��Ѵ�.
//		try {
//			this.wait(10000);
		createReport������������();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
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

				if (s1.toLowerCase().startsWith("scopus") | s1.toLowerCase().startsWith("patent")) {
					return -s2.compareTo(s1);
				} else {
					return -s1.compareTo(s2);
				}

			}
		});

		return files;
	}

	private void listingFile(File file) {
		File[] files = file.listFiles();

		files = sortFileList(files, COMPARETYPE_NAME); // Date�� Sort����
		if (file != null) {
			for (File _file : files) {
//				 log.info(_file.getAbsolutePath());
				if (_file.isDirectory()) {
					// log.info("Dir " + _file.getName());
					if (_file.getParent().indexOf("��") != -1 || _file.getParent().indexOf("scopus") != -1) {
						isScopus = true;
					} else if (_file.getParent().indexOf("Ư��") != -1 || _file.getParent().indexOf("patent") != -1) {
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
	 * @throws Exception 
	 */
	private void ana(File _file) throws Exception {
		BufferedReader br = createReader(_file, null);
		currentFile = _file.getAbsolutePath();
		readline(br);
		// System.exit(1);
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
	LinkedHashMap<String, String> sm5 = new LinkedHashMap<String, String>();
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
		// log.info("�ڷᱸ�� �ʱ�ȭ");
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
		// log.info(clmData +"\t" + clmIdxData + "\t" + fieldName);
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
	public void readline(BufferedReader br) throws Exception {
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
						int stLength = st.length;
						if (rowIdx == 0) {
							int cellIdx = 0;
							while (cellIdx < stLength) {
								String nt = st[cellIdx].trim();
								clmIdxData.put(nt.toUpperCase(), cellIdx);
								cellIdx++;
							}
//							 log.info("SCOPUS] " +clmIdxData.toString());
							rowIdx++;
							continue;
						}

						clmData.clear();
						int idx = 0;
						while (idx < stLength) {
							String nt = st[idx];
							clmData.put(idx++, nt.trim());
						}
						String countryCode = getMapData(clmData, clmIdxData, "FIRST_AUTHOR_COUNTRYCODE").toUpperCase().trim();
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
				log.info("patent read file {}", currentFile);
				while ((line = br.readLine()) != null) {
					
					try {
						// StringTokenizer st = new StringTokenizer(line, "\t");
						// //�̺κп� �ڵ������� ����
						String[] st = line.split("\t");
						int stLength = st.length;
						if (rowIdx == 0) {
							int cellIdx = 0;
							while (cellIdx < stLength) {
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
						while (idx < stLength) {
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
										// log.info("aaaaaa:  " + ipcIdx + "   "
										// + ip);
										cnSet.add(ip.trim());
										firstIPC = _ipc.trim().toUpperCase().replaceAll("\\s", "");
										// break;
									}
								}
							}
							idx++;
						}
						if(currentFile.endsWith("LNG���ڿ�_cargo_pumpT15.txt")){
							 log.info("assignee ] {}" + assignee);
						}

						// ti application-number application-Year
						// assignee-country

						// GB`LOUGHBOROUGH UNIVERSITY OF TECHNOLOGY;GB`ADCOCK
						// PAUL
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

//						if ("US".equalsIgnoreCase(au) || "EP".equalsIgnoreCase(au) || "WO".equalsIgnoreCase(au)) {
//						if ("US".equalsIgnoreCase(au)) {
							//@coreawin 2020-01 ��� �������� US Ư�㸦 ������θ� �����ش޶�� ��û��.
							for (String _ipc : cnSet) {
								countPerKeyString(sm3, _ipc);
								countPerKeyString(sm4, Dictionary.getInstance().findKSCIIPC(_ipc));
								// log.info("Dictionary.getInstance().findKSCIIPC {} /  {}",
								// _ipc,
								// Dictionary.getInstance().findKSCIIPC(_ipc));
							}
							countPerYear(pnyear);
							countPerKeyString(sm2, au);

							if(currentFile.endsWith("LNG���ڿ�_cargo_pumpT15.txt")){
								log.info("assSet {}", assSets);
							}
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
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					rowIdx++;
				}
				log.info("Ư��� {}", sm5);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		createReportForTech();
	}

	/**
	 * ������ �ۼ��Ѵ�.<br>
	 * ������ ����� ��Ʈ�� �ۼ��Ѵ�.<br>
	 * @throws Exception 
	 */
	private void createReportForTech() throws Exception {
		ExcelReport er = new ExcelReport(isScopus);
		Map<String, String> �������������� = null;

		File f = new File(currentFile);
		String fileName = f.getName();
		if (fileName.contains("bulk")) {
			fileName = fileName.replaceAll("\\.bulk.*$", "") + ".txt";
		}
		fileName = fileName.replaceAll("[\\-]", "");
//		fileName = fileName.replaceAll("[\\-_]", "");
		fileName = fileName.substring(0, fileName.lastIndexOf("."));

//		log.info("isScopus : {}", isScopus);
		if (isScopus) {
			er.createExcelSheetForCount(sm1);
			er.createExcelSheetForCountCustom("��1������ ������ ����", sm2, new String[] { "��1����", "����" });
			// er.createExcelSheetForCountCustom("������ ����", sm2, new String[] {
			// "����", "����"
			// });
			�ѱ��ؿܳ��Ǽ�.put(fileName, sm3.get("������") + ":" + sm3.get("�ؿܳ�"));
			 log.info("�ѱ� / �ؿ� �� �� : {} {}, <= {}", fileName,  �ѱ��ؿܳ��Ǽ�.get(fileName), fileName);
			 
			 if(�ѱ��ؿܳ��Ǽ�.get(fileName)==null){
				 log.error(f.getAbsolutePath());
				 log.error("�Ǽ��� ���������� ������ �ִ�. @@@={}=@@@", fileName);
				 System.exit(-1);
			 }

			// er.createExcelSheetForCountCustom("��1������ ������ ����", sm2, new
			// String[] {
			// "��1����", "����" });
			// er.createExcelSheetForCountCustom("�������� ������ ����\n���� 10��",
			// extractTopCount(sm22, 10), new String[] { "����", "����" });
			// er.createExcelSheetForRateCustom("����з��� ������", extractTopRate(sm3,
			// 5), new
			// String[] { "�з�", "������" });
			er.createExcelSheetForDocumentList("�� ����Ʈ", docuList);

		} else {
			// er.createExcelSheetForCount(sm1);
			// er.createExcelSheetForCountCustom("�������", sm22, new String[]
			// {"����� ����",
			// "Ư���"});
			// er.createExcelSheetForCountCustom("�����(Ư��û)", sm2, new String[]
			// {"�����(Ư��û)",
			// "Ư���"});
			// er.createExcelSheetForRateCustom("����з��� ������", extractTopRate(sm3,
			// 5), new
			// String[] { "IPC", "������" });
			�������������� = extractTopRate(sm4, 1000);
			er.createExcelSheetForRateCustom("KSCI ��� ������", ��������������, new String[] { "�����", "������" }, "");
			er.createExcelSheetForDocumentList("Ư�㸮��Ʈ", docuList);
		}
		// 9.973333333����Ʈ�׸���_�ߺз�08_patent.xlsx

		/** ����� ������� �ӽú��� */

		/** �⺻ �Ҽ��� ���ڸ����� �ݿø� �Ѵ�. */
		if (�������������� != null && ��������������.size() > 0) {
			 log.info("�������������� : " + ��������������);
			double[] ������ = Dictionary.getInstance().get������(��������������);
			StringBuilder buf = new StringBuilder();
			buf.append(fileName);
			buf.append("\t");

			String _���Ǽ� = �ѱ��ؿܳ��Ǽ�.get(fileName);
			 log.info("ISSCOPUS {} /  {}, _���Ǽ� : {}", fileName, isScopus, _���Ǽ�);
			 
			 if(_���Ǽ�==null){
				 log.error(f.getAbsolutePath());
				 log.error("�Ǽ��� ���������� ������ �ִ�. @@@={}=@@@", fileName);
				 throw new Exception("�� �Ǽ��� null�� ����Ǿ���.");
			 }
			 
			 char[] abc = fileName.toCharArray();
			 for(char c : abc){
				 log.info("{} / {}", c, (int)c);
			 }
			 
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
//			 log.info("����Ư���, / �ؿ� Ư�� �� {} / {} ", sm5Keys);
			for (String _sk : sm5Keys) {
				/** ����Ư�� �� / �ؿ� Ư�� �� */
				String v = sm5.get(_sk);
				log.info("����Ư���, / �ؿ� Ư�� �� {} / {} ", _sk, v);
				
				
				
				 if(_sk==null ||  v==null){
					 
					 log.error(f.getAbsolutePath());
					 log.error("�Ǽ��� ���������� ������ �ִ�. @@@={}=@@@", fileName);
					 throw new Exception("Ư�� �Ǽ��� null�� ����Ǿ���.");
				 }
				
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
			// log.info("������������ : " + buf.toString());

		}
		String path = f.getParent() + File.separator + fileName + ".xlsx";
		// log.info(" writeFile ; " + path);

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
			 log.info("��������������_��ü �׸� : " + ��������������_��ü);
			double[] ������ = Dictionary.getInstance().get������(��������������_��ü);
			StringBuilder buf = new StringBuilder();
			buf.append("��ü");
			buf.append("\t");

			for (double _d : ������) {
				log.info("������ : {}", _d);
				buf.append(String.format("%.3f", _d));
				// buf.append(Math.round(_d * p1) / p2);
				buf.append("\t");
			}
			buf.deleteCharAt(buf.length() - 1);
			��������������ü.add(buf.toString());
			 log.info("��������������ü ====> " + buf.toString());

		}
	}

	final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * ����� ��������� ����Ѵ�.<br>
	 * �̰� Ư�� or �� ���ϵ��� ���� �������ϰ� ���������� �Ѵ�.
	 */
	private void createReport������������() {
		ExcelReport er2 = new ExcelReport(isScopus);
		/**
		 * ���⼭�� currentFile�� ���� �������� �м��� �۾����ϸ��� �ǰ� �� ������ �ٷ� �������� ���� ������� ������
		 * ����.
		 */
		log.info("������ �������� : {} ", currentFile);
		File f = new File(currentFile);
		String reportingFile = f.getParentFile().getParent()+ File.separator + this.techName + "_" + format.format(new Date())
				+ "_����� ��� ����.xlsx";
		log.info("������ ���� ���� : {} ",reportingFile );
		log.info("��� ������ ũ�� {} ", sm4.size());
		// if (sm4.size() > 0) {
		Map<String, String[]> ����������MAP = new LinkedHashMap<String, String[]>();
		for (String _datas : ������������) {
			String[] result = _datas.split("\t");
			String techName = result[0];
			 for(String _r : result){
			 log.info(_r);
			 }
			����������MAP.put(techName, Arrays.copyOfRange(result, 1, result.length));
		}
		er2.createExcelSheetForCountCustom3("�������", ����������MAP, new String[] { "�����(����)", "������", "�ؿܳ�", "����Ư��", "�ؿ�Ư��",
				"������߰��", "�ΰ���ġ���߰��(�ݿø�)", "������(�ݿø�)", "�����(�ݿø�)" });

		createReportForTechAll();
		����������MAP = new LinkedHashMap<String, String[]>();
		for (String _datas : ��������������ü) {
			String[] result = _datas.split("\t");
			String techName = result[0];
			����������MAP.put(techName, Arrays.copyOfRange(result, 1, result.length));
		}
		er2.createExcelSheetForCountCustom3("���������ü", ����������MAP, new String[] { "�����(����)", "������߰��", "�ΰ���ġ���߰��(�ݿø�)",
				"������(�ݿø�)", "�����(�ݿø�)" });
		er2.writeExcel(reportingFile);
		log.info("������������ : " + reportingFile);
		// }
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

	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static void main(String... args) throws Exception {
//		String �а��� = "�����������";
//		�а��� = "�Һ���";
//		�а��� = "���ż���";
//		�а��� = "��������";
		String �۾��� = "20210126";
//		final String path = "d:\\data\\2020\\yeo\\"+�۾���+"\\"+�а���+"\\";
		
		
		String[] �а������� = new String[] { "���ɹ��α�", "���ҿ�����", "����Ʈ�׸���", "����Ʈ��", "�ý��۹ݵ�ü", "��������", "�������κ�", "ģȯ����", "�¾籤" };
//		�а������� = new String[] {  "�¾籤" };
		�а������� = new String[] { "���ɹ��α�", "���ҿ�����", "����Ʈ�׸���","����Ʈ��Ƽ",  "����Ʈ��", "�ý��۹ݵ�ü", "��������", "�ΰ�����", "�������κ�", "ģȯ����", "�¾籤" };
		�а������� = new String[] {  "�����Ƿ�","���̿�������" };
		�а������� = new String[] {  "ǳ��" };
		�а������� = new String[] {  "���ɹ��α�","���̿�������","���ҿ�����","����Ʈ�׸���","����Ʈ��Ƽ","����Ʈ��","�ý��۹ݵ�ü","��������","�ΰ�����","�����Ƿ�","�������κ�","ģȯ����","�¾籤","ǳ��" };
		
		
		
		for(String �а��� : �а�������){
			String downloadPath = "d:/data/2021/������-�̼�/download/"+�۾��� +"/����" ;
			final File dir = new File(downloadPath);
			
			if (dir.isDirectory()) {
				File[] dirs = dir.listFiles();
				for (File _dir : dirs) {
					if (_dir.isDirectory()) {
						String ����а��� = _dir.getName();
					for(String �а� : �а�������){
						if(�а�.equalsIgnoreCase(����а���)){
							/* Ư���� �ݵ�� Ư����� �о�� �Ѵ�. */
							System.out.println(_dir.getAbsolutePath());
							new Extract������Ư��_2021(����а���, _dir.getAbsolutePath());
						}
					}
//					log.info("read path {}", _dir.getAbsolutePath());
//					if(!_dir.getName().contains("�������κ�")){
//						continue;
//					}
					
					}
				}
			}
		}
	}

}
