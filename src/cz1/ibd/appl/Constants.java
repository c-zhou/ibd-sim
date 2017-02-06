package cz1.ibd.appl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Constants {
	
	public final static String SYSLS = System.getProperty("line.separator");
    public final static String SYSFS = System.getProperty("file.separator");
    
	public static double CHR_PHYSICAL_SIZE = 0;
	public static double CHR_GENETIC_SIZE = 0;
	public static double CHR_CENTRAMERE = 0;
	public static double SNP_AVG_DISTANCE = 0;
	public static String CHROMOSOME = null;

	public static Map<Integer, Double> SNP_AVG_DISTANCE_MIXTURE =
            new HashMap<Integer, Double>();
    public static double PROB_PRECISION = 1e-6;
    public static int PROB_PRECISION_INV = (int) (1/PROB_PRECISION);
    static {
            double[] avg = new double[]{100, 200, 500, 1000, 2000, 5000, 10000};
            double[] probs = new double[]{0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05};
            double p = 0.0;
            for(int i=0; i<avg.length; i++) {
                    int start = (int) (p/PROB_PRECISION),
                            end = (int) ((p+probs[i])/PROB_PRECISION);
                    for(int j=start; j<end; j++)
                            SNP_AVG_DISTANCE_MIXTURE.put(j, avg[i]);
                    p = p+probs[i];
            }

    }

	public static Random random = new Random();
	
	public final static int ploidy = 2;
	public static final char[] Nucleobase = new char[]{'A', 'T', 'C', 'G'};
    public static final char[][] NucleobaseT = new char[][]{
        {'A', 'G', 'C', 'T'},
        {'G', 'A', 'C', 'T'},
        {'C', 'T', 'A', 'G'},
        {'T', 'C', 'A', 'G'}};

	public static char[] allelesR() {
		int i1 = (int)Math.floor(random.nextDouble()*4),
				i2 = (int)Math.floor(random.nextDouble()*3);
		if(i1<=i2) i2++;
		return new char[]{Nucleobase[i1], Nucleobase[i2]};
	}
	
	public static char[] phaseR(char[] alleles) {
		return new char[] {
				alleles[(int)Math.floor(random.nextDouble()*2)],
				alleles[(int)Math.floor(random.nextDouble()*2)]
		};
	}
	
    public static int poisson(double lambda) {
        //double L = Math.exp(-lambda);
        //double p = 1.0;
        double p = 0;
    	int k = 0;
        do {
            k++;
            //p *= random.nextDouble();
            p += Math.log(random.nextDouble());
        } //while (p > L);
        while(p>-lambda);
        return k - 1;
    }
    
    public static int poisson() {
        //double L = Math.exp(-lambda);
        //double p = 1.0;
        double avg = SNP_AVG_DISTANCE_MIXTURE.get(
                random.nextInt(PROB_PRECISION_INV));
        double p = 0;
        int k = 0;
        do {
            k++;
            //p *= random.nextDouble();
            p += Math.log(random.nextDouble());
        } //while (p > L);
        while(p>-avg);
        return k - 1;
    }
    
    public static void main(String[] args) {
    	for(int i=0; i<1000; i++)
    		System.err.print(poisson()+",");
    }
}
