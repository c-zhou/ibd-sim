package cz1.test;
import java.util.Set;

import cz1.ibd.core.Pedigree;
import cz1.ibd.core.Pedigree.Node;


public class PedigreeTest {

	public static void main(String[] args) {
		Pedigree ped = new Pedigree("C:\\Users\\chenxi.zhou\\"
				+ "Desktop\\ibd\\pedigree.txt");
		
		for(Node node : ped.nodes()) {
			String p1 = node.parents()[0]==null ? "NA" : 
				node.parents()[0].id();
			String p2 = node.parents()[1]==null ? "NA" : 
				node.parents()[1].id();
			System.out.println(node.id()+"\t"+p1+"\t"+p2);
		}
	}
}
