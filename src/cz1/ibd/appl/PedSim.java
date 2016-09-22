package cz1.ibd.appl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;

import PedigreeSim.Individual;
import PedigreeSim.Locus;
import PedigreeSim.PedigreeSimulate;
import PedigreeSim.PopulationData;
import cz1.ibd.core.Pedigree;
import cz1.ibd.core.Pedigree.Node;
import cz1.ibd.core.SNP;
import cz1.util.Utils;

public class PedSim {

	private static String experiment = null;
	private static String workspace = null;
	private static String output_file = null;
	private static String pedigree_file = null;
	private static BufferedWriter br_log = null;
	private static String log_file = null;
	private static long random_seed = System.currentTimeMillis();
	private static SNP[] snp_global = null;

	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();
		options.addOption( "e", "experiment", true, "experiment name." );
		options.addOption( "w", "workspace", true, "directory contains input files." );
		options.addOption( "o", "output", true, "output file prefix." );
		options.addOption( "p", "pedigree-file",true, "pedigree file." );
		options.addOption( "s", "random-seed", true, "random seed.");
		options.addOption( "c", "chromosome", true, "chromosome.");
		options.addOption( "S", "p-size", true, "chromosome physical size.");
		options.addOption( "G", "g-size", true, "chromosome genetic size.");
		options.addOption( "C", "centramere", true, "chromosome centromere position (genetic).");
		options.addOption( "d", "d-avg", true, "average distance between adjcent SNPs (physical). ");
		options.addOption( "l", "log", true, "log file");
		try {
			// parse the command line arguments
			CommandLine line = parser.parse( options, args );
			if( line.hasOption("e") ) {
				experiment = line.getOptionValue('e');
			}
			if(line.hasOption("w")) {
				workspace = line.getOptionValue("w");
			}
			if(line.hasOption("o")) {
				output_file = line.getOptionValue("o");
			}
			if(line.hasOption("p")) { 
				pedigree_file = line.getOptionValue("p");
			}
			if(line.hasOption("s")) {
				random_seed = Long.parseLong(line.getOptionValue("s"));
				Constants.random = new Random(random_seed);
			}
			if(line.hasOption("c")) {
				Constants.CHROMOSOME = line.getOptionValue("c");
			}
			if(line.hasOption("S")) {
				Constants.CHR_PHYSICAL_SIZE = Double.
						parseDouble(line.getOptionValue("S"));
			}
			if(line.hasOption("G")) {
				Constants.CHR_GENETIC_SIZE = Double.
						parseDouble(line.getOptionValue("G"));
			}
			if(line.hasOption("C")) {
				Constants.CHR_CENTRAMERE = Double.
						parseDouble(line.getOptionValue("C"));
			}
			if(line.hasOption("d")) {
				Constants.SNP_AVG_DISTANCE = Double.
						parseDouble(line.getOptionValue("d"));
			}
			if(line.hasOption("l")) {
				log_file = line.getOptionValue("l");
			}

			br_log = Utils.getBufferedWriter(workspace+Constants.SYSFS+log_file);
			logPars();

			simulate();
			
			br_log.write("SYSTEM TIME:\t"+Utils.getSystemTime()+"\n\n");
			br_log.close();
		}
		catch( ParseException | IOException exp ) {
			System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
	}

	private static void logPars() {
		// TODO Auto-generated method stub
		try {
			br_log.write("SYSTEM TIME:\t"+Utils.getSystemTime()+"\n\n");
			br_log.write("EXPERIMENT:\t"+experiment+"\n");
			br_log.write("WORKSPACE:\t"+workspace+"\n");
			br_log.write("OUTPUT:\t"+output_file+"\n");
			br_log.write("PEDIGREE_FILE:\t"+pedigree_file+"\n");
			br_log.write("RANDOM_SEED:\t"+random_seed);
			br_log.write("CHROMOSOME\t"+Constants.CHROMOSOME+"\n");
			br_log.write("CHR_PHYSICAL_SIZE\t"+Constants.CHR_PHYSICAL_SIZE+"\n");
			br_log.write("CHR_GENETIC_SIZE\t"+Constants.CHR_GENETIC_SIZE+"\n");
			br_log.write("CHR_CENTRAMERE\t"+Constants.CHR_CENTRAMERE+"\n");
			br_log.write("SNP_AVG_DISTANCE\t"+Constants.SNP_AVG_DISTANCE+"\n");
			br_log.write("#####\n\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void simulate() {
		// TODO Auto-generated method stub
		File wd = new File(workspace);
		if(!wd.exists() || !wd.isDirectory())
			wd.mkdir();
		genotypeR();
		Pedigree pedigree = new Pedigree(pedigree_file);
		Set<Node> nodes = pedigree.nodes();
		for(Node node : nodes) run(node);
		write(nodes);
	}
	
	private static void write(Set<Node> node_set) {
		// TODO Auto-generated method stub
		try {

			List<Node> nodes = new ArrayList<Node>();
			nodes.addAll(node_set);
			Collections.sort(nodes, new Comparator<Node>() {
				@Override
				public int compare(Node n1, Node n2) {
					// TODO Auto-generated method stub
					return n1.id().compareTo(n2.id());
				}
				
			});
			BufferedReader[] brs_gen = new BufferedReader[nodes.size()],
					brs_hap = new BufferedReader[nodes.size()];
			BufferedWriter bw_vcf = Utils.getBufferedWriter(
					workspace+Constants.SYSFS+"global.vcf"),
					bw_hap = Utils.getBufferedWriter(
							workspace+Constants.SYSFS+"global.hap");
			writeH(bw_vcf);
			writeH(bw_hap);
			bw_vcf.write("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT");
			bw_hap.write("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT");
			
			int i=0, idx=0;
			int haplotype = 0;
			Map<String, Character[]> hapMap = new HashMap<String, Character[]>();
			Map<Node, Integer> idxMap = new HashMap<Node, Integer>();
			for(Node node : nodes) {
				idxMap.put(node, idx++);
				bw_vcf.write("\t"+node.id());
				bw_hap.write("\t"+node.id());
				String f = workspace+Constants.SYSFS+node.id();
				if(new File(f+".hap").exists()) {
					brs_hap[i] = Utils.getBufferedReader(f+".hap");
					brs_hap[i].readLine();
				} else {
					Character[] cs = new Character[2];
					cs[0] = (char)(haplotype>9?'a'+haplotype-10:'0'+haplotype);
					haplotype++;
					cs[1] = (char)(haplotype>9?'a'+haplotype-10:'0'+haplotype);
					haplotype++;
					hapMap.put(node.id(), cs);
				}
				brs_gen[i] = Utils.getBufferedReader(f+".gen");
				brs_gen[i].readLine();
				i++;
			}
			bw_vcf.write("\n");
			bw_hap.write("\n");
			
			String[] s;
			for(i=0; i<snp_global.length; i++) {
				bw_vcf.write(Constants.CHROMOSOME+"\t"+
						snp_global[i].getPosBasePairs()+"\t"+
						snp_global[i].getName()+"\t"+
						snp_global[i].getAlleles()[0]+"\t"+
						snp_global[i].getAlleles()[1]+"\t.\t.\t.\tGT");
				bw_hap.write(Constants.CHROMOSOME+"\t"+
						snp_global[i].getPosBasePairs()+"\t"+
						snp_global[i].getName()+"\t"+
						snp_global[i].getAlleles()[0]+"\t"+
						snp_global[i].getAlleles()[1]+"\t.\t.\t.\tGT");
				
				
				int[][] iv = new int[nodes.size()][2];
				for(int j=0; j<nodes.size(); j++) {
					s = brs_gen[j].readLine().split("\\s+");
					bw_vcf.write("\t"+s[1]+"/"+s[2]);
					
					if(brs_hap[j]!=null) {
						s = brs_hap[j].readLine().split("\\s+");
						iv[j] = new int[]{Integer.parseInt(s[1]),
								Integer.parseInt(s[2])};
					}
				}
				
				for(int j=0; j<iv.length; j++) {
					if(brs_hap[j]==null) {
						Character[] cs = hapMap.get(nodes.get(j).id());
						bw_hap.write("\t"+cs[0]+"|"+cs[1]);
					} else {
						bw_hap.write("\t"+
								searchFounderHap(
										nodes.get(j), 
										hapMap,
										idxMap,
										iv,
										iv[j][0])
								+"|"+
								searchFounderHap(
										nodes.get(j), 
										hapMap,
										idxMap,
										iv,
										iv[j][1]));
					}
				}
				bw_vcf.write("\n");
				bw_hap.write("\n");
			}
			
			for(i=0; i<brs_gen.length; i++) {
				if(brs_hap[i]!=null) 
					brs_hap[i].close();
				brs_gen[i].close();
			}
			bw_vcf.close();
			bw_hap.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static char searchFounderHap(
			Node node,
			Map<String, Character[]> hapMap,
			Map<Node, Integer> idxMap,
			int[][] iv,
			int founder) {
		// TODO Auto-generated method stub
		Node parent = founder<2 ? 
				node.parents()[0] : 
					node.parents()[1];
		if(founder>1) founder-=2;
		if( hapMap.containsKey(parent.id()))
				return hapMap.get(parent.id())[founder];
		
		int f_ancestor = iv[idxMap.get(parent)][founder];
		return searchFounderHap(parent, 
				hapMap, 
				idxMap, 
				iv, 
				f_ancestor);
	}

	private static void writeH(BufferedWriter bw) {
		// TODO Auto-generated method stub
		try {
			bw.write("##SYSTEM TIME:\t"+Utils.getSystemTime()+"\n");
			bw.write("##WORKSPACE:\t"+workspace+"\n");
			bw.write("##PEDIGREE_FILE:\t"+pedigree_file+"\n");
			bw.write("##RANDOM_SEED:\t"+random_seed+"\n");
			bw.write("##CHROMOSOME:\t"+Constants.CHROMOSOME+"\n");
			bw.write("##CHR_PHYSICAL_SIZE\t"+Constants.CHR_PHYSICAL_SIZE+"\n");
			bw.write("##CHR_GENETIC_SIZE\t"+Constants.CHR_GENETIC_SIZE+"\n");
			bw.write("##CHR_CENTRAMERE\t"+Constants.CHR_CENTRAMERE+"\n");
			bw.write("##SNP_AVG_DISTANCE\t"+Constants.SNP_AVG_DISTANCE+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void run(Node node) {
		// TODO Auto-generated method stub
		if(node.f()) return;
		Node[] parents = node.parents();
		if(parents[0]==null || 
				parents[1]==null) {
			genotypeR(node);
			return;
		}
		run(parents[0]);
		run(parents[1]);
		pedigreeR(node);
	}

	private static void pedigreeR(Node node) {
		// TODO Auto-generated method stub
		try {
			Node[] parents = node.parents();
			br_log.write("#####\n");
			br_log.write("Run pedigreeSim for "+node.id()+" @ "
					+parents[0].id()+"x"+parents[1].id()+"\n");
			final String output_dir = workspace+Constants.SYSFS+
					scratch(node.id(),parents[0].id(),parents[1].id());
			File output_file = new File(output_dir);
			br_log.write("Set "+output_dir+" as working directory.\n");
			if(!output_file.exists() || !output_file.isDirectory()) {
				br_log.write(output_dir+" doesn't exist. Create it.\n");
				output_file.mkdir();
			}
			writePedigreeFile(node.id(), parents[0].id(), parents[1].id(), output_dir);
			writeChromosomeFile(output_dir);
			writeGeneFile(parents[0].id(), parents[1].id(), output_dir);
			writeMapFile(output_dir);
			writeParameterFile(output_dir);
			
			br_log.write("Writing file "+node.id()+" @ "
					+parents[0].id()+"x"+parents[1].id()+"...\n");
			PopulationData pop_data = PedigreeSimulate.simulate(output_dir +
					Constants.SYSFS + "tmp.par");
			String parent_dir = new File(output_dir).getParent();
			final BufferedWriter bw_gen = Utils.getBufferedWriter(parent_dir+
					Constants.SYSFS+
					node.id()+".gen"),
					bw_hap = Utils.getBufferedWriter(parent_dir+
							Constants.SYSFS+
							node.id()+".hap");
			bw_gen.write("marker");
			bw_hap.write("marker");
			for(int i=0; i<Constants.ploidy; i++) {
				bw_gen.write("\t"+node.id()+"_"+(i+1));
				bw_hap.write("\t"+node.id()+"_"+(i+1));
			}
			bw_gen.write("\n");
			bw_hap.write("\n");

			Individual invf = pop_data.getIndiv(node.id()); 
			List<Locus> loci = pop_data.getChrom(0).getLocus();
			for(int i=0; i<loci.size(); i++) {
				bw_gen.write(loci.get(i).getLocusName());
				bw_hap.write(loci.get(i).getLocusName());

				String[] geno = invf.getLocusAllele(0, i);
				for(int j=0; j<geno.length; j++)
					bw_gen.write("\t"+geno[j]);
				bw_gen.write("\n");

				for(int j=0; j<Constants.ploidy; j++)
					bw_hap.write("\t"+invf.getHaploStruct(0, j).
							getFounderAt(loci.get(i).getPosition()));
				bw_hap.write("\n");
			}
			bw_hap.close();
			bw_gen.close();
			node.switchf();
			
			br_log.write("done...\n#####\n\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void writeParameterFile(String output_dir) {
		// TODO Auto-generated method stub
		try {
			br_log.write("Writing parameter file...\n");
			BufferedWriter bw = Utils.getBufferedWriter(output_dir+Constants.SYSFS+"tmp.par"); 
			bw.write("; This is a parameter file for PedigreeSim" + Constants.SYSLS +
					"; to run this example:" + Constants.SYSLS +
					"; - open a console window and go to the folder containing these example files" + Constants.SYSLS +
					"; - give the following command (with for <path> the path where PedigreeSim.jar" + Constants.SYSLS +
					";   and folder lib containing jsci-core.jar are located):" + Constants.SYSLS +
					";   java -jar <path>PedigreeSim.jar tmp.par" + Constants.SYSLS + Constants.SYSLS);
			bw.write("PLOIDY = " + Constants.ploidy + Constants.SYSLS +
					"MAPFUNCTION = HALDANE" +Constants.SYSLS +
					"MISSING = NA" + Constants.SYSLS +
					"CHROMFILE = " + output_dir + Constants.SYSFS + "tmp.chrom" + Constants.SYSLS +
					"PEDFILE = " + output_dir + Constants.SYSFS + "tmp.ped" + Constants.SYSLS +
					"MAPFILE = " + output_dir + Constants.SYSFS + "tmp.map" + Constants.SYSLS +
					"FOUNDERFILE = " + output_dir + Constants.SYSFS + "tmp.gen" + Constants.SYSLS +
					"OUTPUT = " + output_dir + Constants.SYSFS + "tmp_out" + Constants.SYSLS + Constants.SYSLS);
			bw.write("; the following parameters are here set to their default values," + Constants.SYSLS +
					"; these lines may therefore be omitted:"  + Constants.SYSLS + Constants.SYSLS +
					"ALLOWNOCHIASMATA = 1" + Constants.SYSLS +
					"NATURALPAIRING = 1 ; Note that this overrules the \"quadrivalent\" column" + Constants.SYSLS +
					"                   ; in the CHROMFILE" + Constants.SYSLS +
					"PARALLELQUADRIVALENTS = 0.0" + Constants.SYSLS +
					"PAIREDCENTROMERES = 0.0");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeMapFile(String output_dir) {
		// TODO Auto-generated method stub
		String parent_dir = new File(output_dir).getParent();
		try {
			br_log.write("Writing map file...\n");
			FileUtils.copyFile(new File(parent_dir+Constants.SYSFS+"global.map"), 
					new File(output_dir+Constants.SYSFS+"tmp.map"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeGeneFile(String p1,
			String p2,
			String output_dir) {
		// TODO Auto-generated method stub
		try {
			br_log.write("Writing gene file...\n");
			BufferedWriter bw = Utils.getBufferedWriter(output_dir+
					Constants.SYSFS+"tmp.gen");
			bw.write("marker\t");
			for(int i=1; i<=Constants.ploidy; i++) 
				bw.write(p1+"_"+i+"\t");
			for(int i=1; i<=Constants.ploidy; i++) 
				bw.write(p2+"_"+i+"\t");
			bw.write(Constants.SYSLS);
			String parent_dir = new File(output_dir).getParent();
			BufferedReader br_p1 = Utils.getBufferedReader(
					parent_dir+Constants.SYSFS+p1+".gen"),
					br_p2 = Utils.getBufferedReader(
							parent_dir+Constants.SYSFS+p2+".gen");
			String line;
			String[] s_p1, s_p2;
			br_p1.readLine(); br_p2.readLine();
			while( (line=br_p1.readLine())!=null ) {
				s_p1 = line.split("\\s+");
				s_p2 = br_p2.readLine().split("\\s+");
				bw.write(s_p1[0]+"\t");
				for(int i=1; i<s_p1.length; i++)
					bw.write(s_p1[i]+"\t");
				for(int i=1; i<s_p2.length; i++)
					bw.write(s_p2[i]+"\t");
				bw.write("\n");
			}
			br_p1.close();
			br_p2.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writePedigreeFile(String f,
			String p1,
			String p2,
			String output_dir) {
		// TODO Auto-generated method stub
		try {
			br_log.write("Writing pedigree file...\n");
			BufferedWriter bw = 
					Utils.getBufferedWriter(output_dir+
							Constants.SYSFS+"tmp.ped");
			bw.write("Name\tParent1\tParent2"+Constants.SYSLS);
			bw.write(p1+"\tNA\tNA"+Constants.SYSLS);
			bw.write(p2+"\tNA\tNA"+Constants.SYSLS);
			bw.write(f+"\t"+p1+"\t"+p2+Constants.SYSLS);
			bw.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private static void writeChromosomeFile(String output_dir) {
		try {
			br_log.write("Writing chromosome file...\n");
			BufferedWriter bw = Utils.getBufferedWriter(output_dir+
					Constants.SYSFS+"tmp.chrom");
			bw.write("chromosome\tlength\tcentromere"+Constants.SYSLS);
			bw.write(Constants.CHROMOSOME+"\t"+
					Constants.CHR_GENETIC_SIZE+"\t"+
					Constants.CHR_CENTRAMERE+Constants.SYSLS);
			bw.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void genotypeR(Node node) {
		try {
			br_log.write("Generate SNPs for " +node.id()+"...\n");
			BufferedWriter bw_gen = Utils.getBufferedWriter(workspace+
					Constants.SYSFS+node.id()+".gen");
			bw_gen.write("marker\t"+node.id()+"_1\t"+node.id()+"_2\n");
			for(int i=0; i<snp_global.length; i++) {
				char[] geno = Constants.phaseR(
						snp_global[i].getAlleles());
				bw_gen.write(snp_global[i].getName()+"\t"
						+geno[0]+"\t"
						+geno[1]+"\n");
			}
			bw_gen.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		node.switchf();
	}

	public static void genotypeR() {
		try {
			br_log.write("Generate global SNPs...\n");
			List<SNP> snp_list = new ArrayList<SNP>();

			int next, t;
			double lambda = Constants.SNP_AVG_DISTANCE;
			t = 0;
			next = Constants.poisson(lambda);
			t += next;
			while(t<Constants.CHR_PHYSICAL_SIZE) {
				snp_list.add(new SNP(Constants.CHROMOSOME+"."+t, 
						Constants.CHROMOSOME, 
						Constants.allelesR(), 
						null, 
						t, 
						t*Constants.CHR_GENETIC_SIZE/Constants.CHR_PHYSICAL_SIZE));
				next = Constants.poisson(lambda);
				t += next;    
			}
			snp_global = new SNP[snp_list.size()];
			snp_list.toArray(snp_global);

			BufferedWriter bw_gen = Utils.getBufferedWriter(workspace+
					Constants.SYSFS+"global.gen"),
					bw_map = Utils.getBufferedWriter(workspace+Constants.SYSFS+"global.map");
			bw_gen.write("marker\tA\tB\n");
			bw_map.write("marker\tchromosome\tposition\n");
			for(int i=0; i<snp_global.length; i++) {
				bw_gen.write(snp_global[i].getName()+"\t"
						+snp_global[i].getAlleles()[0]+"\t"
						+snp_global[i].getAlleles()[1]+"\n");
				bw_map.write(snp_global[i].getName()+"\t"
						+Constants.CHROMOSOME+"\t"
						+snp_global[i].getPosCentiMorgan()+"\n");
			}
			bw_gen.close();
			bw_map.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String scratch(String f, 
			String p1, 
			String p2) {
		return f+"@"+p1+"&"+p2;
	}
}




