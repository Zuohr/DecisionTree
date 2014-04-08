
public class Main {

	public static void main(String[] args) {
		//Meta meta = Preprocess.preprocess("trainProdSelection.arff");
		Meta meta = Preprocess.preprocess("trainProdIntro.binary.arff");

		DecisionTree deTree = new DecisionTree(meta.root);
		deTree.buildDecisionTree(meta.root, meta.deData, meta.flags, meta.attrSet, meta.attrMap);
		deTree.getRoot();
	}

}
