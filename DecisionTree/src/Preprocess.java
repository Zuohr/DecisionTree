import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Preprocess {
	public static ArrayList<Boolean> realCols;
	public static int labelPos;

	public static Meta preprocess(String fileName) {
		Meta result = new Meta();
		TreeNode root = new TreeNode();
		result.root = root;

		result.attrSet = new LinkedHashSet<String>();
		result.attrMap = new HashMap<String, Integer>();

		String[][] deData = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					fileName)));
			labelPos = 0;
			int idx = 0;
			realCols = new ArrayList<Boolean>();
			int rowCnt = 0;

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("@attribute")) {
					String[] attrs = line.trim().split("\\s+");
					if (2 < attrs.length
							&& "real".equals(attrs[2].toLowerCase())) {
						realCols.add(true);
					} else {
						realCols.add(false);
					}

					if (1 < attrs.length
							&& "label".equals(attrs[1].toLowerCase())) {
						labelPos = idx;
					} else if (1 < attrs.length
							&& !"label".equals(attrs[1].toLowerCase())) {
						result.attrSet.add(attrs[1]);
						result.attrMap.put(attrs[1], idx);
					}
					idx++;
				} else if (line.startsWith("@") || line.isEmpty()) {
					continue;
				} else {
					rowCnt++;
				}
			}
			br.close();

			// reads data
			br = new BufferedReader(new FileReader(new File(fileName)));
			deData = new String[rowCnt][realCols.size()];
			line = null;
			int row = 0;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("@") && !line.isEmpty()) {
					String[] cols = line.trim().split(",");
					for (int col = 0; col < cols.length; col++) {
						deData[row][col] = cols[col];
					}
					row++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < deData.length; i++) {
			System.out.println(Arrays.toString(deData[i]));
		}

		result.flags = new boolean[deData.length];
		Arrays.fill(result.flags, true);
		result.deData = deData;
		return result;
	}

	public static String[][] transform(String[][] table) {
		String[][] result = new String[table.length][table[0].length];
		for (int row = 0; row < table.length; row++) {
			for (int col = 0; col < table[0].length; col++) {
				result[row][col] = table[row][col];
			}
		}
		for (int i = 0; i < realCols.size(); i++) {
			if (realCols.get(i)) {
				transfer(result, i, labelPos);
			}
		}

		return result;
	}

	// private static double getTotalEntropy(String[][] data, int labelPos) {
	// Map<String, Double> freq = new HashMap<String, Double>();
	// int len = data.length;
	// for (int i = 0; i < len; i++) {
	// String clazz = data[i][labelPos];
	// Double cnt = freq.get(clazz);
	// if (cnt == null) {
	// freq.put(clazz, 1d);
	// } else {
	// freq.put(clazz, cnt + 1);
	// }
	// }
	//
	// double entropy = 0;
	// for (String key : freq.keySet()) {
	// double cnt = freq.get(key);
	// entropy += cnt / len * Math.log(cnt / len) / Math.log(2);
	// }
	//
	// return -entropy;
	// }

	private static void transfer(String[][] result, int col, int labelPos) {
		int bestPos = 0;
		double minEntropy = Double.MAX_VALUE;
		double[] vals = new double[result.length];
		for (int i = 0; i < result.length; i++) {
			vals[i] = Double.parseDouble(result[i][col]);
		}

		for (int i = 0; i < vals.length; i++) {
			double threshold = vals[i];
			Map<String, Double> lemap = new HashMap<String, Double>();
			Map<String, Double> gtmap = new HashMap<String, Double>();
			int leCnt = 0, gtCnt = 0;
			for (int j = 0; j < vals.length; j++) {
				String label = result[j][labelPos];
				if (vals[j] <= threshold) {
					Double cnt = lemap.get(label);
					if (cnt == null) {
						lemap.put(label, 1.0);
					} else {
						lemap.put(label, cnt + 1);
					}
					leCnt++;
				} else {
					Double cnt = gtmap.get(label);
					if (cnt == null) {
						gtmap.put(label, 1.0);
					} else {
						gtmap.put(label, cnt + 1);
					}
					gtCnt++;
				}
			}

			double entropy = 0;
			for (String key : lemap.keySet()) {
				Double cnt = lemap.get(key);
				entropy += cnt / leCnt * Math.log(cnt / leCnt) / Math.log(2);
			}
			entropy *= (double) leCnt / (leCnt + gtCnt);
			double temp = entropy;
			entropy = 0;

			for (String key : gtmap.keySet()) {
				Double cnt = gtmap.get(key);
				entropy += cnt / gtCnt * Math.log(cnt / gtCnt) / Math.log(2);
			}
			entropy *= (double) gtCnt / (leCnt + gtCnt);
			entropy += temp;

			entropy = -entropy;
			if (entropy < minEntropy) {
				minEntropy = entropy;
				bestPos = i;
			}
		}

		double threshold = vals[bestPos];
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] <= threshold) {
				result[i][col] = "le*" + String.valueOf(threshold);
			} else {
				result[i][col] = "gt*" + String.valueOf(threshold);
			}
		}
	}

}
