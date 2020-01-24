package pe.eclipse.neon.yeo._2017.y201712;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Dictionary {

	enum FileType {
		ASJC, KSCIIPC, ETC
	}

	private Map<String, String> asjcMap = new HashMap<String, String>();
	private Map<String, String> ksciipcMap = new HashMap<String, String>();
	private Map<String, double[]> ���Map = new HashMap<String, double[]>();
	private Set<String> ksciipcExceptSet = new HashSet<String>();
	private static Dictionary instance = new Dictionary();

	private void loadDic(InputStream is, FileType type) {
		BufferedInputStream bis = new BufferedInputStream(is);
		Scanner scan = new Scanner(bis);
		while (scan.hasNext()) {
			String line = scan.nextLine();
			String[] values = null;
			try {
				switch (type) {
				case ASJC:
					values = line.split("\t");
					asjcMap.put(values[0].trim().toUpperCase(), values[1].trim());
					break;
				case KSCIIPC:
					values = line.split("\t");
					if (values.length < 2)
						continue;
					String name = values[0].replaceAll("[;,\"]", " ").replaceAll("\\(.*?\\)", "")
							.replaceAll("\\s{1,}", " ").trim();
					String v1 = values[1].trim().replaceAll("[,\"]", " ").replaceAll("\\(.*?\\)", "")
							.replaceAll("\\s{1,}", " ").trim();
					// System.out.println(v1);
					String[] v1values = v1.split(" ");
					for (String _v1 : v1values) {
						// if(ksciipcMap.containsKey(_v1.trim())) {
						// System.out.println(_v1 +"\t" + name);
						// System.out.println(_v1 +"\t" + ksciipcMap.get(_v1.trim()));
						// System.out.println();
						// }else {
						if ("����".equals(name.trim())) {
							ksciipcExceptSet.add(_v1.trim().toUpperCase());
						} else {
							ksciipcMap.put(_v1.trim().toUpperCase(), name.trim());
						}
						// }

					}
					break;
				case ETC:
					values = line.split("\t");
					/** �̸��� ��ȣ,������� �����Ѵ�. KSCIS-IPC�� KEY�� ����� �ϱ� �����ε� �ڵ�ȭ�� �ȵǾ� �ֱ⶧�� */
					try {
						String ��ǰ�� = values[0].replaceAll("[;,\"]", " ").replaceAll("\\(.*?\\)", "")
								.replaceAll("\\s{1,}", "").trim();
						double ����� = Double.parseDouble(values[1].trim());
						double �ΰ���ġ��� = Double.parseDouble(values[2].trim());
						double ������ = Double.parseDouble(values[3].trim());
						double ���⵵ = Double.parseDouble(values[4].trim());

						���Map.put(��ǰ��, new double[] { �����, �ΰ���ġ���, ������, ���⵵ });
					} catch (Exception e) {
						// ù������ Į�������� �� ������ ���� �����Ѵ�.
						System.err.println("�Ľ̿��� : ù������ �����ص� �� > " + line);
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(line);
				System.exit(-1);
			}
		}
		scan.close();
		System.out.println(���Map);
	}

	private Dictionary() {
		loadDic(getClass().getResourceAsStream("/yeo/resource/asjc.txt"), FileType.ASJC);
		loadDic(getClass().getResourceAsStream("/yeo/resource/KSCI_IPC.txt"), FileType.KSCIIPC);
		loadDic(getClass().getResourceAsStream("/yeo/resource/���_�ΰ���ġ_������_�����.txt"), FileType.ETC);

	}

	public static Dictionary getInstance() {
		return instance;
	}

	public String getASJC(String asjcCode) {
		return asjcMap.get(asjcCode);
	}

	public String getKSCIIPC(String ipc) {
		return ksciipcMap.get(ipc);
	}

	public String getExceptKSCIIPC(String ipc) {
		return ksciipcMap.get(ipc);
	}

	public double[] get������(Map<String, String> ������) {
		/** ������� ������߰�� �ΰ���ġ���߰��(�ݿø�) ������(�ݿø�) �����(�ݿø�)���� ���� ���� */
		
		double[] resultTotals = new double[] { 0d, 0d, 0d, 0d };
		for (String _ipc : ������.keySet()) {
			
		}
		double[] totals = new double[] { 0d, 0d, 0d, 0d };
//		System.out.println("������.keySet() " + ������.keySet());
		for (String _ipc : ������.keySet()) {
			if("none".equalsIgnoreCase(_ipc.trim())) continue;
			char fChat = _ipc.toUpperCase().charAt(0);
			String productName = _ipc;
			if ((int)fChat >= (int)'A' && (int)fChat <= (int)'Z') {
				/* ���ĺ����� �����ϸ� IPC�ڵ尡 ���°Ŵ�. */
				productName = findKSCIIPC(_ipc);
			}

//			System.out.println("��ǰ�� > " + productName);
			if (productName == null || "".equalsIgnoreCase(productName)) {
				continue;
			} else {
				String reProductName = productName.replaceAll("\\s", "").replaceAll("[,\";]", "");
				if (this.���Map.containsKey(reProductName)) {
					double[] _value = get�������(reProductName);
					for (int idx = 0; idx < totals.length; idx++) {
						totals[idx] += (Double.parseDouble(������.get(_ipc)) * _value[idx]);
					}
//					System.out.println("��ǰ�� �׸� ��� �Ϸ� > " + productName);
				}
			}
		}
//		System.out.println("������� ������߰��	�ΰ���ġ���߰��(�ݿø�)	������(�ݿø�)	�����(�ݿø�)");
//		for (double _d : totals) {
//			System.out.println("�� Ȯ�� " + _d);
//		}
		return totals;
	}

	public double[] get�������(String ��ǰ��) {
		return this.���Map.get(��ǰ��);
	}

	public String findKSCIIPC(String ipc) {
		String result = "";
		if (ipc == null) {
			return "";
		}

		int ipcLength = ipc.length();
		while (ipcLength >= 4) {
			// System.out.println(ipc);
			result = Dictionary.getInstance().getKSCIIPC(ipc);
			if (result == null) {
				ipc = ipc.substring(0, ipc.length() - 1);
				ipcLength = ipc.length();
				continue;
			}
			break;
		}

		return result;
	}

	public static void main(String... args) {
		System.out.println(Dictionary.getInstance().findKSCIIPC("H01M8/0228"));
		System.out.println(Dictionary.getInstance().findKSCIIPC("A61K8"));
		System.out.println(Dictionary.getInstance().findKSCIIPC("G03B42"));
		System.out.println(Dictionary.getInstance().findKSCIIPC("A44C23"));
		System.out.println(Dictionary.getInstance().findKSCIIPC("G03B"));
		System.out.println(Dictionary.getInstance().findKSCIIPC("G03B34"));
		System.out.println(Dictionary.getInstance().findKSCIIPC("G03B31"));
		;

		SortedMap<String, String> testSet = new TreeMap<String, String>();
		testSet.put("A01B27/02", "0.6");
		testSet.put("E21D34", "0.3");
		testSet.put("A01G98/23", "0.1");
		System.out.println(Dictionary.getInstance().get������(testSet));
		System.out.println(Dictionary.getInstance().findKSCIIPC("None"));

	}

}
