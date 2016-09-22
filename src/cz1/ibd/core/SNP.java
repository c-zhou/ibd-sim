package cz1.ibd.core;

public class SNP {
	private String name;
    private String chromosome;
    private char[] alleles;
    private char[] phase;
    private int posBasePairs;
    private double posCentiMorgan;

    public SNP(String name, String chromosome, char[] alleles, char[] phase, 
                int posBasePairs, double posCentiMorgan) {
        this.name = name;
        this.chromosome = chromosome;
        this.alleles = alleles;
        this.phase = phase;
        this.posCentiMorgan = posCentiMorgan;
        this.posBasePairs = posBasePairs;
    }
    
    public String getName() {
        return name;
    }

    public String getChromosome() {
        return chromosome;
    }

    public char[] getAlleles() {
        return alleles;
    }

    public char[] getPhase() {
        return phase;
    }

    public int getPosBasePairs() {
        return posBasePairs;
    }

    public double getPosCentiMorgan() {
        return posCentiMorgan;
    }
}
