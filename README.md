# ibd-sim

Identity by descendent data dimulation

quick start

java -jar ibdsim.jar -w ibd -p pedigree.txt -c CHRX -S 100000000 -G 100 -C 80 -d 500 -l log.txt

simulate a ibd file according to the pedigree provided by 'pedigree.txt'

the output files will be in './ibd/'

the chromosome name will be 'CHRX'

the physical length of the chromosome will be 100000000bp

the genetic length of the chromosome will be 100cM 

the position of the centramere will at 80cM

the mean distance of the adjacent SNPs will be 500bp, as well as the standard deviation, since the distances follow a Poisson distribution 

the program running process will be logged in file 'log.txt'
