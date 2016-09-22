package cz1.ibd.core;

public class Chromosome {
	private int N;
    private String[] name;
    private int[] LBasePairs;
    private double[] LCentiMorgan;
    private double[] centromere;
    private double[] prefPairing;
    private double[] quadrivalent;

    public Chromosome(int N, String[] name, int[] LBasePairs,
            double[] LCentiMorgan, double[] centromere,
            double[] prefPairing, double[] quadrivalent) {
            this.N = N;
            this.name = name;
            this.LBasePairs = LBasePairs;
            this.LCentiMorgan = LCentiMorgan;
            this.centromere = centromere;
            this.prefPairing = prefPairing;
            this.quadrivalent = quadrivalent;
    }
    
    public int getNumber() {
        return N;
    }
    
    public String[] getName() {
        return name;
    }

    public int[] getLBasePairs() {
        return LBasePairs;
    }

    public double[] getLCentiMorgan() {
        return LCentiMorgan;
    }

    public double[] getCentromere() {
        return centromere;
    }

    public double[] getPrefPairing() {
        return prefPairing;
    }

    public double[] getQuadrivalent() {
        return quadrivalent;
    }
}
