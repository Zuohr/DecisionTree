

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Iterator;

class TreeNode {
	String element;  
	String value;    
	LinkedHashSet<TreeNode> childs;

	public TreeNode() {
		this.element = null;
		this.value = null;
		this.childs = null;
	}
	
	public TreeNode(String value) {
		this.element = null;
		this.value = value;
		this.childs = null;
	}
	
	public String getElement() {
		return this.element;
	}
	
	public void setElement(String e) {
		this.element = e;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String v) {
		this.value = v;
	}
	
	public LinkedHashSet<TreeNode> getChilds() {
		return this.childs;
	}
	
	public void setChilds(LinkedHashSet<TreeNode> childs) {
		this.childs = childs;
	}
}

public class DecisionTree {
	TreeNode root;
	
	public DecisionTree() {
		root = new TreeNode();
	}
	
	public DecisionTree(TreeNode root) {
		this.root = root;
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	
	public String selectAtrribute(TreeNode node,String[][] deData, boolean flags[],
			LinkedHashSet<String> atrributes, HashMap<String,Integer> attrIndexMap) {
		double Gain[] = new double[atrributes.size()];
		int class_index = deData[0].length - 1;
		String return_atrribute = null;
		
		int count = 0;
		for(String atrribute:atrributes) {
			int values_count, class_count;
			int index = attrIndexMap.get(atrribute);
			LinkedHashSet<String> values = new LinkedHashSet<String>();
			LinkedHashSet<String> classes = new LinkedHashSet<String>();
			
			for(int i = 0; i < deData.length; i++) {
				if(flags[i] == true) {
					values.add(deData[i][index]);
					classes.add(deData[i][class_index]);
				}
			}
			values_count = values.size();
			class_count = classes.size();
			
			int values_vector[] = new int[values_count * class_count];
			int class_vector[] = new int[class_count];
			
			for(int i = 0; i < deData.length; i++) {
				if(flags[i] == true) {
					int j = 0;
					for(String v:values) {
						if(deData[i][index].equals(v)) {
							break;
						} else {
							j++;
						}
					}
										
					int k = 0;
					for(String c:classes) {
						if(deData[i][class_index].equals(c)) {
							break;
						} else {
							k++;
						}
					}
					
					values_vector[j*class_count+k]++;
					class_vector[k]++;
				}
			}
			
			/*  
			for(int i = 0; i < values_count * class_count; i++) {
				System.out.print(values_vector[i] + " ");
			}
			System.out.println();
			for(int i = 0; i < class_count; i++) {
				System.out.print(class_vector[i] + " ");
			}
			System.out.println();
			*/		
			
			double InfoD = 0.0;
			double class_total = 0.0;
			for(int i = 0; i < class_vector.length; i++){
				class_total += class_vector[i];
			}
			for(int i = 0; i < class_vector.length; i++){
				if(class_vector[i] == 0) {
					continue;
				} else {
					double d = Math.log(class_vector[i]/class_total) / Math.log(2.0) * class_vector[i] / class_total;
					InfoD = InfoD - d;
				}
			}
			
			double InfoA = 0.0;
			for(int i = 0; i < values_count; i++) {
				double middle = 0.0;
				double attr_count = 0.0;
				
				for(int j = 0; j < class_count; j++) {
					attr_count +=  values_vector[i*class_count+j];
				}
				
				for(int j = 0; j < class_count; j++) {
					if(values_vector[i*class_count+j] != 0) {
						double k = values_vector[i*class_count+j];
						middle = middle - Math.log(k/attr_count) / Math.log(2.0) * k / attr_count;
					}
				}
				
				InfoA += middle * attr_count / class_total;
			}

			Gain[count] = InfoD - InfoA;
			count++;
		}
		
		double max = 0.0;
		int i = 0;
		for(String atrribute:atrributes) {
			if(Gain[i] > max) {
				max = Gain[i];
				return_atrribute = atrribute;
			}
			i++;
		}
		
		return return_atrribute;
	}
	
	public void buildDecisionTree(TreeNode node, String[][] deData, boolean flags[],
			LinkedHashSet<String> attributes, HashMap<String,Integer> attrIndexMap) {
		if(attributes.isEmpty() == true) {
			HashMap<String,Integer> classMap = new HashMap<String,Integer>();
			int classIndex = deData[0].length - 1;
			for(int i = 0; i < deData.length; i++) {
				if(flags[i] == true) {
					if(classMap.containsKey(deData[i][classIndex])) {
						int count = classMap.get(deData[i][classIndex]);
						classMap.put(deData[i][classIndex], count+1);
					} else {
						classMap.put(deData[i][classIndex], 1);
					}
				}
			}
			String mostClass = null;
			int mostCount = 0;
			Iterator<String> it = classMap.keySet().iterator();
			while(it.hasNext()) {
				String strClass = (String)it.next();
				if(classMap.get(strClass) > mostCount) {
					mostClass = strClass;
					mostCount = classMap.get(strClass);
				}
			}
			node.setElement(mostClass);
			node.setChilds(null);
			System.out.println("yezhi:" + node.getElement() + ":" + node.getValue());
			return;
		}
		
		int class_index = deData[0].length - 1;
		String class_name = null;
		HashSet<String> classSet = new HashSet<String>();
		for(int i = 0; i < deData.length; i++) {
			if(flags[i] == true) {
				class_name = deData[i][class_index];
				classSet.add(class_name);
			}
		}
		if(classSet.size() == 1) {
			node.setElement(class_name);
			node.setChilds(null);
			System.out.println("leaf:" + node.getElement() + ":" + node.getValue());
			return;
		}
		
						
		String attribute = selectAtrribute(node, deData, flags, attributes, attrIndexMap);
		node.setElement(attribute);
		//System.out.println(attribute);
		if(node == root) {
			System.out.println("root:" + node.getElement() + ":" + node.getValue());
		} else {
			System.out.println("branch:" + node.getElement() + ":" + node.getValue());
		}
		
		int attrIndex = attrIndexMap.get(attribute);
		LinkedHashSet<String> attrValues = new LinkedHashSet<String>();
		for(int i = 0; i < deData.length; i++) {
			if(flags[i] == true) {
				attrValues.add(deData[i][attrIndex]);
			}
		}
		LinkedHashSet<TreeNode> childs = new LinkedHashSet<TreeNode>();
		for(String attrValue:attrValues) {
			TreeNode tn = new TreeNode(attrValue);
			childs.add(tn);
		}
		node.setChilds(childs);
		
		attributes.remove(attribute);
		
		if(childs.isEmpty() != true) {
			for(TreeNode child:childs) {
				boolean newFlags[] = new boolean[deData.length] ;
				for(int i = 0; i < deData.length; i++) {
					newFlags[i] = flags[i];
					if(!deData[i][attrIndex].equals(child.getValue())) {
						newFlags[i] = false;
					}
				}
				LinkedHashSet<String> newAttributes = new LinkedHashSet<String>();
				for(String attr:attributes) {
					newAttributes.add(attr);
				}
				buildDecisionTree(child, deData, newFlags, newAttributes, attrIndexMap);
			}
		}
	}
	
	public void printDecisionTree() {
	
	}
}



