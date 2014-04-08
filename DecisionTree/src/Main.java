import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.omg.CORBA.PUBLIC_MEMBER;

import sun.reflect.generics.tree.Tree;


public class Main {

	public static void main(String[] args) {
		//Meta meta = Preprocess.preprocess("trainProdSelection.arff");
		Meta meta = Preprocess.preprocess("trainProdIntro.binary.arff");

		DecisionTree deTree = new DecisionTree(meta.root);
		deTree.buildDecisionTree(meta.root, meta.deData, meta.flags, meta.attrSet, meta.attrMap);
		TreeNode root=deTree.getRoot();
		
		String[][] test=new String[1][1];
		HashMap<String, Integer> attrMap=meta.attrMap;
  ArrayList<String> result=getResult(test, attrMap, root);
		 
		}
	public static ArrayList<String> getResult(String[][] test,HashMap<String, Integer> attrMap,TreeNode root){	
	ArrayList<String> result=new ArrayList<String>();
	for(int i=0;i<test.length;i++){
		while(root.getChilds().size()>0){
		int attrIndex=attrMap.get(root.getElement());
	    String currValue=test[i][attrIndex];
	    LinkedHashSet<TreeNode> childs=root.getChilds();
	   
	    for (TreeNode child : childs) {
			if(child.getValue().equals(currValue)){
			root=child;	
			}
		}
	    String rslt=root.getElement();
	    result.add(rslt);
		}
	}
	return result;
	}}

