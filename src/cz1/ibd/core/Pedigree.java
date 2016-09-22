package cz1.ibd.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import cz1.util.Utils;

public class Pedigree {

	private final HashSet<Node> nodes = 
			new HashSet<Node>();
	
	public Pedigree(String pedigreeFile) {
		this.read(pedigreeFile);
	}
		
	private void read(String pedigreeFile) {
		// TODO Auto-generated method stub
		try {
			BufferedReader br_ped = Utils.
					getBufferedReader(pedigreeFile);
			String line;
			String[] s;
			br_ped.readLine();
			while( (line=br_ped.readLine())!=null) {
				System.out.println(line);
				s = line.split("\\s+");
				if(s.length!=3) 
					throw new IOException("Pedigree file "
							+ "may be corrupted.");
				if(!s[0].equals("NA")) nodes.add(new Node(s[0], new Node[]{
						null, null}));
				if(!s[1].equals("NA")) nodes.add(new Node(s[1], new Node[]{
						null, null}));
				if(!s[2].equals("NA")) nodes.add(new Node(s[2], new Node[]{
						null, null}));
			}
			br_ped.close();
			
			br_ped = Utils.getBufferedReader(pedigreeFile);
			br_ped.readLine();
			while( (line=br_ped.readLine())!=null) {
				s = line.split("\\s+");
				this.search(s[0]).parents = new Node[] {
						this.search(s[1]), 
						this.search(s[2])};
			}
			br_ped.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Set<Node> nodes() {
		return this.nodes;
	}

	private Node search(String ns) {
		// TODO Auto-generated method stub
		for(Node node : nodes) 
			if(node.id.equals(ns))
				return node;
		return null;
	}

	public class Node {
		private final String id;
		private Node[] parents;
		private boolean f = false;
		
		public Node(String id, Node[] parents) {
			this.id = id;
			this.parents = parents;
		}
		
		public int hashCode() {
			return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
					// if deriving: appendSuper(super.hashCode()).
					append(id).
					toHashCode();
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Node))
	            return false;
	        if (obj == this)
	            return true;
			return new EqualsBuilder().append(this.id,
					((Node) obj).id).isEquals();
		}
		
		public String id() {
			return this.id;
		}
		
		public Node[] parents() {
			return this.parents;
		}

		public boolean f() {
			// TODO Auto-generated method stub
			return f;
		}
		
		public void switchf() {
			this.f = !f;
		}
	}	
}

