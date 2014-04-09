import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		String fileName = "trainProdIntro.binary.arff";
		int rowNum = countRow(fileName);
		int testNum = 10, testRow = rowNum / testNum;

		int errCnt = 0;
		for (int i = 1; i <= testNum; i++) {
			String[] fileNames = splitFile(fileName, i, testRow);
			Meta trainMeta = Preprocess.preprocess(fileNames[0]);
			Meta testMeta = Preprocess.preprocess(fileNames[1]);
			DecisionTree deTree = new DecisionTree(trainMeta.root);
			deTree.buildDecisionTree(trainMeta.root, trainMeta.deData,
					trainMeta.flags, trainMeta.attrSet, trainMeta.attrMap,
					trainMeta.mostFreq);
			int err = validate(deTree, testMeta);
			if (err != -1) {
				errCnt += err;
			} else {
				rowNum -= testRow;
			}
		}

		System.out.println((double) errCnt / rowNum);
		System.out.println(errCnt);
		System.out.println(rowNum);
	}

	private static int validate(DecisionTree deTree, Meta testMeta) {
		int errCnt = 0;
		ArrayList<String> prediction = predict(deTree, testMeta);
		if (prediction == null) {
			return -1;
		}
		ArrayList<String> answer = new ArrayList<String>();
		for (int i = 0; i < testMeta.deData.length; i++) {
			answer.add(testMeta.deData[i][testMeta.labelPos]);
		}
		for (int i = 0; i < prediction.size(); i++) {
			if (!prediction.get(i).equals(answer.get(i))) {
				errCnt++;
			}
		}

		return errCnt;
	}

	private static ArrayList<String> predict(DecisionTree deTree, Meta testMeta) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < testMeta.deData.length; i++) {
			TreeNode ptr = deTree.getRoot();
			while (ptr.children != null) {
				TreeNode next = ptr;
				int currCol = testMeta.attrMap.get(ptr.feature);
				for (TreeNode child : ptr.children) {
					if (testMeta.realCols.get(currCol)) {
						double threshold = Double.parseDouble(child.value
								.substring(3));
						if (Double.parseDouble(testMeta.deData[i][currCol]) <= threshold
								&& child.value.startsWith("le")
								|| Double
										.parseDouble(testMeta.deData[i][currCol]) > threshold
								&& child.value.startsWith("gt")) {
							next = child;
							break;
						}
					} else {
						if (testMeta.deData[i][currCol].equals(child.value)) {
							next = child;
							break;
						}
					}
				}
				if (next == ptr) {
					return null;
				} else {
					ptr = next;
				}
			}
			result.add(ptr.feature);
		}
		return result;
	}

	private static String[] splitFile(String fileName, int i, int testRow) {
		String[] fileNames = { "train_" + i, "test_" + i };
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					fileName)));
			PrintWriter train = new PrintWriter(new FileWriter(new File(
					fileNames[0])));
			PrintWriter test = new PrintWriter(new FileWriter(new File(
					fileNames[1])));

			String line;
			int row = 0;
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty() && !line.startsWith("@")) {
					if (row >= (i - 1) * testRow && row < i * testRow) {
						test.println(line);
					} else {
						train.println(line);
					}
					row++;
				} else {
					test.println(line);
					train.println(line);
				}
			}

			test.close();
			train.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileNames;
	}

	private static int countRow(String fileName) {
		int cnt = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					fileName)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty() && !line.startsWith("@")) {
					cnt++;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cnt;
	}

}
