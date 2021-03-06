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
 * A. 작업방법 기술검색식을 이용하여 먼저 SCOPUS, PATENT 다운로드를 시도한다.<br>
 * 1. NEON_ECLIPSE_KISTI_SCOPUS_SEARCHER<br>
 * 2. NEON_ECLIPSE_KISTI_PATENT_SEARCHER<br> 
 * _2021.Rnd package.ScopusSearcher_2021.java <br>
 * 중요 : 특허검색파일명과 논문검색파일명이 같아야 한다. (폴더로 구분만 된다.)
 * </P>
 * 
 * <P>
 * B. 정량분석 지표를 추출한다. <br>
 * pe.neon 프로젝트의 아래 클래스를 이용해 논문/특허 정량지표를 추출한다.<br>
 * pe.neon.여운동._202101.Launcher4Patent  <br>
 * pe.neon.여운동._202101.Launcher4Scopus<br>
 * 결과물 :  <br>
 * 	1. RESULT_SCOPUS_기술명_작업일.txt <br>
 * 	2. RESULT_PATENT_기술명_작업일.txt<br>
 * </P>
 * 
 * <P>
 * C. pe.eclipse.neon 프로젝트를 이용하여 경제적 특성 정보를 최종 취합한다.<br>
 *  pe.eclipse.neon.yeo._2021.Extract경제적특성_2021
 * </P>
 * 
 * 안녕하세요 미소테크 박진현입니다. <br/>
 * 
 * R&D PIE 시스템 데이터 관련 메일 드립니다.<br/>
 * 우선 정리 완료된 4개 분과(전체 16개분과 중) 논문, 특허검색식 전달 해드립니다.<br/>
 * 검색식 부분에 KISTI에서 검색시 결과 건수도 같이 표기 했습니다. 건수가 비슷하게 나오면 될듯합니다.<br/>
 * 이 부분에 대한 작년과 동일한 작업 요청 드리겠습니다.<br/>
 * (분과별 기술군별 논문/특허 결과, 10대 지표값 등)<br/>
 * 일전 말씀드린바와 같이 아래와 같은 필드 추가 요청 드립니다.<br/>
 * 
 * <논문><br/>
 * 
 * - 피인용수: Number of Citation<br/>
 * Check - 저자 국가 식별: Author Country<br/>
 * Check - 저자명 식별: Author ID<br/>
 * Check - 저자명: Author Name<br/>
 * Check - 저자 통합 정보: Author Info<br/>
 * Check - 기관명 국가 식별: Affiliation Country<br/>
 * Check - 기관명 식별: Affiliation IDs<br/>
 * Check - 기관명: Affiliation Name<br/>
 * Check
 * 
 * <특허><br/>
 * 
 * - 출원인 국적: assignee-country<br/>
 * check - 대표출원인(정제된 것?): app_gp<br/>
 * check - 출원인 국적+명: assignee<br/>
 * check - 피인용수; citation-count<br/>
 * check - 등록번호:<br/>
 * ==> KIND 항목이 Grant 인것 check
 * 
 * @author coreawin
 * @date 2021. 1. 9.
 */
public class Extract경제적특성_2021 extends FileRW {

	static Logger log = LoggerFactory.getLogger("Extract경제적특성_2021.class");

	File srcPath = null;
	Boolean isScopus = false;
	boolean checkFS = false;
	Map<String, String> totalDatas = new HashMap<String, String>();
	Map<String, String> totalDatasTech = new HashMap<String, String>();
	String techName = "";

	/**
	 * 해당 path에는 분과별로 데이터가 들어있다.
	 * 
	 * @param techName
	 *            기술분과명
	 * @param path
	 *            논문/특허 분석 대상이 있는 파일 패스
	 * @throws Exception 
	 */
	public Extract경제적특성_2021(String techName, String path) throws Exception {
		this.techName = techName;
		srcPath = new File(path);
		listingFile(srcPath);
		flist.addAll(0, fslist);
		flist.addAll(flist.size(), fplist);

		for (File f : flist) {
			log.info("read File " + f.getName() + "\t[SCOPUS doc]:" +  isScopus);
			if (f.getParent().indexOf("논문") != -1 || f.getParent().indexOf("scopus") != -1) {
				isScopus = true;
			} else if (f.getParent().indexOf("특허") != -1 || f.getParent().indexOf("patent") != -1) {
				isScopus = false;
			}
			ana(f);
		}
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		// 기술별 지표를 작성한다.
//		try {
//			this.wait(10000);
		createReport기술별계수정보();
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

		files = sortFileList(files, COMPARETYPE_NAME); // Date로 Sort실행
		if (file != null) {
			for (File _file : files) {
//				 log.info(_file.getAbsolutePath());
				if (_file.isDirectory()) {
					// log.info("Dir " + _file.getName());
					if (_file.getParent().indexOf("논문") != -1 || _file.getParent().indexOf("scopus") != -1) {
						isScopus = true;
					} else if (_file.getParent().indexOf("특허") != -1 || _file.getParent().indexOf("patent") != -1) {
						isScopus = false;
					}
					listingFile(_file);
				} else {
					// 분석 대상 파일을 읽는다.
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
	 * 연도별 논문수
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

	Map<String, String> 한국해외논문건수 = new HashMap<String, String>();
	LinkedList<String> 기술별계수정보 = new LinkedList<String>();
	LinkedList<String> 기술별계수정보전체 = new LinkedList<String>();

	/**
	 * XX별 논문수
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
	 * 기술분류별 점유율
	 */
	public void countPerTech() {

	}

	SortedMap<String, LinkedList<String>> docuList = new TreeMap<String, LinkedList<String>>();

	/**
	 * 논문 리스트<br>
	 * 정렬 : 발행일 <br>
	 * 컬럼 : 제목, 발행일, 저자, 소속기관(국가포함), 저널지, ASJC분류명, DOI <br>
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
		// log.info("자료구조 초기화");
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
			sm3.put("국내논문", "0");
			sm3.put("해외논문", "0");
		} else {
			sm5.put("국내특허", "0");
			sm5.put("해외특허", "0");
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
							countPerKeyString(this.sm2, "없음");
						} else {
							countPerKeyString(this.sm2, countryCode);
						}

						if ("KOR".equalsIgnoreCase(countryCode)) {
							countPerKeyString(sm3, "국내논문");
						} else {
							if (!"".contentEquals(countryCode)) {
								countPerKeyString(sm3, "해외논문");
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
						// //이부분에 코딩에러가 있음
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
						if(currentFile.endsWith("LNG선박용_cargo_pumpT15.txt")){
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
							//@coreawin 2020-01 여운동 연구원이 US 특허를 대상으로만 추출해달라고 요청함.
							for (String _ipc : cnSet) {
								countPerKeyString(sm3, _ipc);
								countPerKeyString(sm4, Dictionary.getInstance().findKSCIIPC(_ipc));
								// log.info("Dictionary.getInstance().findKSCIIPC {} /  {}",
								// _ipc,
								// Dictionary.getInstance().findKSCIIPC(_ipc));
							}
							countPerYear(pnyear);
							countPerKeyString(sm2, au);

							if(currentFile.endsWith("LNG선박용_cargo_pumpT15.txt")){
								log.info("assSet {}", assSets);
							}
							if (assSets.contains("KR")) {
								countPerKeyString(sm5, "국내특허");
							} else {
								countPerKeyString(sm5, "해외특허");
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
				log.info("특허수 {}", sm5);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		createReportForTech();
	}

	/**
	 * 보고서를 작성한다.<br>
	 * 엑셀로 기술별 시트를 작성한다.<br>
	 * @throws Exception 
	 */
	private void createReportForTech() throws Exception {
		ExcelReport er = new ExcelReport(isScopus);
		Map<String, String> 고용산업계수분포율 = null;

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
			er.createExcelSheetForCountCustom("제1저자의 국가별 논문수", sm2, new String[] { "제1저자", "논문수" });
			// er.createExcelSheetForCountCustom("국가별 논문수", sm2, new String[] {
			// "국가", "논문수"
			// });
			한국해외논문건수.put(fileName, sm3.get("국내논문") + ":" + sm3.get("해외논문"));
			 log.info("한국 / 해외 논문 수 : {} {}, <= {}", fileName,  한국해외논문건수.get(fileName), fileName);
			 
			 if(한국해외논문건수.get(fileName)==null){
				 log.error(f.getAbsolutePath());
				 log.error("건수를 못가져오면 문제가 있다. @@@={}=@@@", fileName);
				 System.exit(-1);
			 }

			// er.createExcelSheetForCountCustom("제1저자의 국가별 논문수", sm2, new
			// String[] {
			// "제1저자", "논문수" });
			// er.createExcelSheetForCountCustom("주저자의 국가별 논문수\n상위 10위",
			// extractTopCount(sm22, 10), new String[] { "국가", "논문수" });
			// er.createExcelSheetForRateCustom("기술분류별 점유율", extractTopRate(sm3,
			// 5), new
			// String[] { "분류", "점유율" });
			er.createExcelSheetForDocumentList("논문 리스트", docuList);

		} else {
			// er.createExcelSheetForCount(sm1);
			// er.createExcelSheetForCountCustom("국가통계", sm22, new String[]
			// {"출원인 국가",
			// "특허수"});
			// er.createExcelSheetForCountCustom("출원국(특허청)", sm2, new String[]
			// {"출원국(특허청)",
			// "특허수"});
			// er.createExcelSheetForRateCustom("기술분류별 점유율", extractTopRate(sm3,
			// 5), new
			// String[] { "IPC", "점유율" });
			고용산업계수분포율 = extractTopRate(sm4, 1000);
			er.createExcelSheetForRateCustom("KSCI 산업 분포율", 고용산업계수분포율, new String[] { "산업명", "분포율" }, "");
			er.createExcelSheetForDocumentList("특허리스트", docuList);
		}
		// 9.973333333스마트그리드_중분류08_patent.xlsx

		/** 기술별 계수정보 임시보관 */

		/** 기본 소수점 세자리에서 반올림 한다. */
		if (고용산업계수분포율 != null && 고용산업계수분포율.size() > 0) {
			 log.info("고용산업계수분포율 : " + 고용산업계수분포율);
			double[] 계수결과 = Dictionary.getInstance().get계수계산(고용산업계수분포율);
			StringBuilder buf = new StringBuilder();
			buf.append(fileName);
			buf.append("\t");

			String _논문건수 = 한국해외논문건수.get(fileName);
			 log.info("ISSCOPUS {} /  {}, _논문건수 : {}", fileName, isScopus, _논문건수);
			 
			 if(_논문건수==null){
				 log.error(f.getAbsolutePath());
				 log.error("건수를 못가져오면 문제가 있다. @@@={}=@@@", fileName);
				 throw new Exception("논문 건수가 null로 추출되었다.");
			 }
			 
			 char[] abc = fileName.toCharArray();
			 for(char c : abc){
				 log.info("{} / {}", c, (int)c);
			 }
			 
			if (_논문건수 != null) {
				String[] _dd = _논문건수.split(":");
				buf.append(_dd[0]);
				buf.append(" \t ");
				buf.append(_dd[1]);
				buf.append(" \t ");
			} else {
				buf.append("0\t0\t");
			}

			Set<String> sm5Keys = sm5.keySet();
//			 log.info("국내특허수, / 해외 특허 수 {} / {} ", sm5Keys);
			for (String _sk : sm5Keys) {
				/** 국내특허 수 / 해외 특허 수 */
				String v = sm5.get(_sk);
				log.info("국내특허수, / 해외 특허 수 {} / {} ", _sk, v);
				
				
				
				 if(_sk==null ||  v==null){
					 
					 log.error(f.getAbsolutePath());
					 log.error("건수를 못가져오면 문제가 있다. @@@={}=@@@", fileName);
					 throw new Exception("특허 건수가 null로 추출되었다.");
				 }
				
				if (v == null)
					v = "_0";
				buf.append(v);
				buf.append("\t");
			}

			sm5.clear();

			for (double _d : 계수결과) {
				// log.info(_d);
				buf.append(String.format("%.3f", _d));
				// buf.append(Math.round(_d * p1) / p2);
				buf.append("\t");
			}
			buf.deleteCharAt(buf.length() - 1);
			기술별계수정보.add(buf.toString());
			// log.info("기술별계수정보 : " + buf.toString());

		}
		String path = f.getParent() + File.separator + fileName + ".xlsx";
		// log.info(" writeFile ; " + path);

		er.writeExcel(path);

	}

	private void createReportForTechAll() {
		/* totalDatas에는 중복제거된 전체 특허 목록이 있다. */
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
		Map<String, String> 고용산업계수분포율_전체 = extractTopRate(this.totalDatasTech, 1000);
		/** 기본 소수점 세자리에서 반올림 한다. */
		if (고용산업계수분포율_전체 != null && 고용산업계수분포율_전체.size() > 0) {
			 log.info("고용산업계수분포율_전체 항목 : " + 고용산업계수분포율_전체);
			double[] 계수결과 = Dictionary.getInstance().get계수계산(고용산업계수분포율_전체);
			StringBuilder buf = new StringBuilder();
			buf.append("전체");
			buf.append("\t");

			for (double _d : 계수결과) {
				log.info("계수결과 : {}", _d);
				buf.append(String.format("%.3f", _d));
				// buf.append(Math.round(_d * p1) / p2);
				buf.append("\t");
			}
			buf.deleteCharAt(buf.length() - 1);
			기술별계수정보전체.add(buf.toString());
			 log.info("기술별계수정보전체 ====> " + buf.toString());

		}
	}

	final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 기술별 계수정보를 출력한다.<br>
	 * 이건 특허 or 논문 파일들을 전부 리포팅하고 마지막으로 한다.
	 */
	private void createReport기술별계수정보() {
		ExcelReport er2 = new ExcelReport(isScopus);
		/**
		 * 여기서의 currentFile은 가장 마지막에 분석한 작업파일명이 되고 그 파일의 바로 윗폴더에 최종 계수정보 보고서가
		 * 생성.
		 */
		log.info("리포팅 생성파일 : {} ", currentFile);
		File f = new File(currentFile);
		String reportingFile = f.getParentFile().getParent()+ File.separator + this.techName + "_" + format.format(new Date())
				+ "_기술별 계수 정보.xlsx";
		log.info("리포팅 생성 파일 : {} ",reportingFile );
		log.info("출력 데이터 크기 {} ", sm4.size());
		// if (sm4.size() > 0) {
		Map<String, String[]> 기술계수정보MAP = new LinkedHashMap<String, String[]>();
		for (String _datas : 기술별계수정보) {
			String[] result = _datas.split("\t");
			String techName = result[0];
			 for(String _r : result){
			 log.info(_r);
			 }
			기술계수정보MAP.put(techName, Arrays.copyOfRange(result, 1, result.length));
		}
		er2.createExcelSheetForCountCustom3("계수정보", 기술계수정보MAP, new String[] { "기술명(파일)", "국내논문", "해외논문", "국내특허", "해외특허",
				"고용유발계수", "부가가치유발계수(반올림)", "감응도(반올림)", "영향력(반올림)" });

		createReportForTechAll();
		기술계수정보MAP = new LinkedHashMap<String, String[]>();
		for (String _datas : 기술별계수정보전체) {
			String[] result = _datas.split("\t");
			String techName = result[0];
			기술계수정보MAP.put(techName, Arrays.copyOfRange(result, 1, result.length));
		}
		er2.createExcelSheetForCountCustom3("계수정보전체", 기술계수정보MAP, new String[] { "기술명(파일)", "고용유발계수", "부가가치유발계수(반올림)",
				"감응도(반올림)", "영향력(반올림)" });
		er2.writeExcel(reportingFile);
		log.info("기술별계수정보 : " + reportingFile);
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
		result.put("합계 ", String.valueOf((double) (sum / total)));
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
		result.put("합계 ", String.valueOf(sum));
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
//		String 분과명 = "신재생에너지";
//		분과명 = "소부장";
//		분과명 = "혁신성장";
//		분과명 = "연료전지";
		String 작업일 = "20210126";
//		final String path = "d:\\data\\2020\\yeo\\"+작업일+"\\"+분과명+"\\";
		
		
		String[] 분과명정보 = new String[] { "고기능무인기", "수소에너지", "스마트그리드", "스마트팜", "시스템반도체", "연료전지", "지능형로봇", "친환경차", "태양광" };
//		분과명정보 = new String[] {  "태양광" };
		분과명정보 = new String[] { "고기능무인기", "수소에너지", "스마트그리드","스마트시티",  "스마트팜", "시스템반도체", "연료전지", "인공지능", "지능형로봇", "친환경차", "태양광" };
		분과명정보 = new String[] {  "정밀의료","바이오에너지" };
		분과명정보 = new String[] {  "풍력" };
		분과명정보 = new String[] {  "고기능무인기","바이오에너지","수소에너지","스마트그리드","스마트시티","스마트팜","시스템반도체","연료전지","인공지능","정밀의료","지능형로봇","친환경차","태양광","풍력" };
		
		
		
		for(String 분과명 : 분과명정보){
			String downloadPath = "d:/data/2021/박진현-미소/download/"+작업일 +"/점검" ;
			final File dir = new File(downloadPath);
			
			if (dir.isDirectory()) {
				File[] dirs = dir.listFiles();
				for (File _dir : dirs) {
					if (_dir.isDirectory()) {
						String 기술분과명 = _dir.getName();
					for(String 분과 : 분과명정보){
						if(분과.equalsIgnoreCase(기술분과명)){
							/* 특성상 반드시 특허부터 읽어야 한다. */
							System.out.println(_dir.getAbsolutePath());
							new Extract경제적특성_2021(기술분과명, _dir.getAbsolutePath());
						}
					}
//					log.info("read path {}", _dir.getAbsolutePath());
//					if(!_dir.getName().contains("지능형로봇")){
//						continue;
//					}
					
					}
				}
			}
		}
	}

}
