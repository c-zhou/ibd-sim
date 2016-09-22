package cz1.ibd.appl;

import java.util.Random;

public class Constants {
	
	public final static String SYSLS = System.getProperty("line.separator");
    public final static String SYSFS = System.getProperty("file.separator");
    
	public static double CHR_PHYSICAL_SIZE = 0;
	public static double CHR_GENETIC_SIZE = 0;
	public static double CHR_CENTRAMERE = 0;
	public static double SNP_AVG_DISTANCE = 0;
	public static String CHROMOSOME = null;
	
	public static Random random = new Random();
	
	public final static int ploidy = 2;
	public static final char[] Nucleobase = new char[]{'A', 'T', 'C', 'G'};
	
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
}
