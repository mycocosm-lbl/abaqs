# ABAQS
Accuracy-Based Annotation Quality Score (ABAQS)

# Abstract

As fungal genome resources continue to expand, estimating the accuracy and completeness of genome annotations (e.g., gene and protein models) becomes essential to ensure quality of downstream analyses and applications. It becomes critical in comparative genomic analysis to select genomes with comparable qualities of assembly and annotation. Without careful scrutiny, inconsistencies in annotation quality can undermine downstream studies.

A robust quality metric should ideally capture multiple dimensions of genome annotation accuracy. However, in practice, most commonly used quality metrics are single-dimensional or consider only a few aspects of annotation quality, leaving a large space of high-scored but low-quality genome annotations unpenalized.

To address this gap, we introduce the Accuracy-Based Annotation Quality Score (ABAQS), a comprehensive and minimal-data-driven method to summarize genome annotation quality as a single easily understandable numeric value. While developed primarily using fungal genomes from MycoCosm, ABAQS is broadly applicable across eukaryotes. ABAQS evaluates multiple factors, including genome completeness, gene model validity, and protein profile accuracy.

# Build and Installation

## Prerequisites
You need following prerequisites in order to build and run ABAQS:

* git
* Java SDK version >=21
* MAVEN

```
Example how to install them on Ubuntu Linux

sudo apt update

Install git

apt install git -y

Install OpenJDK: Maven requires Java. Install the default JDK

sudo apt install default-jdk -y

Install Maven

sudo apt install maven -y

Verify Installation

mvn -version
```

## Building

```
git clone https://github.com/mycocosm-lbl/abaqs.git
cd abaqs
mvn clean package
java -jar target/abaqs-jar-with-dependencies.jar

```

## Running

You can run ABAQS directly on host computer using Java runtime version >=21

```
java -jar target/abaqs-jar-with-dependencies.jar 
```
```
usage:

org.mycocosm.abaqs.main.ABAQS [options]

options:
 -ig  --input-gff <arg>                            (required) path to the input gff3 or gtf file, type is detected by the file name extention                                                                                                            
 -is  --input-scaffolds-fasta <arg>                (recomended/required) assembly fasta file path, may be ommitted if the gff3 input file has embedded scaffolds fasta                                                                                   
 -ibf --busco-data-file <arg>                      (recomended) path to the busco data file, using native busco output format                                                                                                                            
 -ib  --busco-data <arg>                           (recomended if -ibf is not provided) busco data, like 'C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758', if ommitted "ideal" BUSCO is assumed                                                             
 -md  --domains-protein-id-mapper <arg>            (recomended) mapper for protein id and domains in domains records, default='(?<id>\w+)\s+.*\s+\w*Pfam\s+(?<domain>[a-z]+\d+).*\s+.*'. See --input-domains and --input-proteins-fasta                  
 -id  --input-domains <arg>                        (recomended if -md is not used) input domains data file path, important note: used together with --domains-protein-id-mapper option to parse the input domains file                                   
 -mg  --gff3-protein-id-mapper <arg>               (optional) mapper for protein id in gff3 records, default='attributes:proteinId:.*->{0}' , meaning use proteinId attribute for gene record. Used in connection to --input-proteins-fasta              
 -mp  --protein-fasta-protein-id-mapper <arg>      (optional) mapper for protein id in protein fasta records, default='.+proteinId\s*=\s*(\d+).*->{1}'                                                                                                   
 -ip  --input-proteins-fasta <arg>                 (optional) input proteins fasta file path, if ommitted then ABAQS will translate genes data into aminoacids using provided gene translation table id (--gene-code)                                    
 -g   --gene-code <arg>                            (optional), NCBI gene code id to be used for translation, if needed, default=1                                                                                                                        
 -igc --gene-code-input-file <arg>                 (optional) gene code input file (gc.prt), if missing internal copy will be used, see --gene-code                                                                                                      
 -ilr --reference-protein-lengths-input-file <arg> (optional) reference protein length distribution file, if missing internal reference will be used                                                                                                     
 -io  --isoforms-min-overlap <arg>                 (optional) minimum overlap to detect genes isoforms by coding positions, default=0.25                                                                                                                 
 -ise --suspected-te-input-file <arg>              (optional) suspected transposable elements pfam domains input file, if missing internal list will be used                                                                                             
 -ite --te-input-file <arg>                        (optional) transposable elements pfam domains input file, if missing internal list will be used                                                                                                       
 -mf  --masker-function <arg>                      (optional) masker function used to detect repeatmasled parts of scaffold sequence, used in TE computation, see --no-domain-masked-cutoff and --suspected-domain-masked-cutoff, default='TO_LOWER_CASE'
 -ndc --no-domain-masked-cutoff <arg>              (optional) masked CDS cutoff for TE detection with no Pfam domains, NaN mean not used, default=0.2                                                                                                    
 -o   --output <arg>                               (optional) path for the results file, default will print to the console                                                                                                                               
 -fw  --fasta-width <arg>                          (optional output parameter) fasta output width, default=70. Used ONLY to produce fasta data embedded into the GFF3 output file, see --output-gff                                                      
 -og  --output-gff <arg>                           (optional) gff3 or gtf output path, will produce POST-filtering gff or gtdf output file, type detected by the file extention                                                                          
 -plb --protein-length-binning <arg>               (optional) protein length distribution binning, default=5                                                                                                                                             
 -sdc --suspected-domain-masked-cutoff <arg>       (optional) masked CDS cutoff for TE detection with suspected TE Pfam domains, NaN mean always TE, default=NaN                                                                                         
 -v   --verbose                                    (optional) produce verbose output                                                                                                                                                                     
 -vo  --verbose-output-folder <arg>                (optional) output folder for verbose output, will save supplemental data during computation  in that folder                                                                                           
```

## Docker

We also maintain Docker image on the DockerHub so you can run it with docker

```
$ docker run mycocosm/abaqs:latest
Error parsing command line: Missing required option: ig
 usage:
    docker run mycocosm/abaqs:latest [options]

 options:
  -ig  --input-gff <arg>                            (required) path to the input gff3 or gtf file, type is detected by the file name extention                                                                                                            
  -is  --input-scaffolds-fasta <arg>                (recomended/required) assembly fasta file path, may be ommitted if the gff3 input file has embedded scaffolds fasta                                                                                   
  -ibf --busco-data-file <arg>                      (recomended) path to the busco data file, using native busco output format                                                                                                                            
  -ib  --busco-data <arg>                           (recomended if -ibf is not provided) busco data, like 'C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758', if ommitted "ideal" BUSCO is assumed                                                             
  -md  --domains-protein-id-mapper <arg>            (recomended) mapper for protein id and domains in domains records, default='(?<id>\w+)\s+.*\s+\w*Pfam\s+(?<domain>[a-z]+\d+).*\s+.*'. See --input-domains and --input-proteins-fasta                  
  -id  --input-domains <arg>                        (recomended if -md is not used) input domains data file path, important note: used together with --domains-protein-id-mapper option to parse the input domains file                                   
  -mg  --gff3-protein-id-mapper <arg>               (optional) mapper for protein id in gff3 records, default='attributes:proteinId:.*->{0}' , meaning use proteinId attribute for gene record. Used in connection to --input-proteins-fasta              
  -mp  --protein-fasta-protein-id-mapper <arg>      (optional) mapper for protein id in protein fasta records, default='.+proteinId\s*=\s*(\d+).*->{1}'                                                                                                   
  -ip  --input-proteins-fasta <arg>                 (optional) input proteins fasta file path, if ommitted then ABAQS will translate genes data into aminoacids using provided gene translation table id (--gene-code)                                    
  -g   --gene-code <arg>                            (optional), NCBI gene code id to be used for translation, if needed, default=1                                                                                                                        
  -igc --gene-code-input-file <arg>                 (optional) gene code input file (gc.prt), if missing internal copy will be used, see --gene-code                                                                                                      
  -ilr --reference-protein-lengths-input-file <arg> (optional) reference protein length distribution file, if missing internal reference will be used                                                                                                     
  -io  --isoforms-min-overlap <arg>                 (optional) minimum overlap to detect genes isoforms by coding positions, default=0.25                                                                                                                 
  -ise --suspected-te-input-file <arg>              (optional) suspected transposable elements pfam domains input file, if missing internal list will be used                                                                                             
  -ite --te-input-file <arg>                        (optional) transposable elements pfam domains input file, if missing internal list will be used                                                                                                       
  -mf  --masker-function <arg>                      (optional) masker function used to detect repeatmasled parts of scaffold sequence, used in TE computation, see --no-domain-masked-cutoff and --suspected-domain-masked-cutoff, default='TO_LOWER_CASE'
  -ndc --no-domain-masked-cutoff <arg>              (optional) masked CDS cutoff for TE detection with no Pfam domains, NaN mean not used, default=0.2                                                                                                    
  -o   --output <arg>                               (optional) path for the results file, default will print to the console                                                                                                                               
  -fw  --fasta-width <arg>                          (optional output parameter) fasta output width, default=70. Used ONLY to produce fasta data embedded into the GFF3 output file, see --output-gff                                                      
  -og  --output-gff <arg>                           (optional) gff3 or gtf output path, will produce POST-filtering gff or gtdf output file, type detected by the file extention                                                                          
  -plb --protein-length-binning <arg>               (optional) protein length distribution binning, default=5                                                                                                                                             
  -sdc --suspected-domain-masked-cutoff <arg>       (optional) masked CDS cutoff for TE detection with suspected TE Pfam domains, NaN mean always TE, default=NaN                                                                                         
  -v   --verbose                                    (optional) produce verbose output                                                                                                                                                                     
  -vo  --verbose-output-folder <arg>                (optional) output folder for verbose output, will save supplemental data during computation  in that folder
```
## Basic calculation of ABAQS requires four pieces of data.

1. \-is: The assembly fasta file. This should be softmasked using a repeatmasking program. Ideally, low-complexity repeats are ignored such as by running RepeatMasker with the \-nolow option.
1. \-ig: Protein models in gff3 or gtf format. Each gene feature / protein must have a unique name and it is expected that the name is specified in the attributes field as proteinId (shown in red below). If some other field is used to name the protein, you can use the \-mg option to specify it. Here are a few lines from an example file.
```
##gff-version 3
##sequence-region scaffold_1 1 1958655
scaffold_1    fgenesh1_pg    gene    167    2008    0    +    .    ID=gene_2211;feature_name=fgenesh1_pg.1_#_1;Name=gene-jgi|Clapy1|1833732;portal_id=Clapy1;proteinId=1833732;transcriptId=1833838
scaffold_1    fgenesh1_pg    mRNA    167    2008    .    +    .    ID=mRNA_2211;Parent=gene_2211;Name=jgi|Clapy1|1833732;product=expressed protein;proteinId=1833732;track=FilteredModels1;transcriptId=1833838
scaffold_1    fgenesh1_pg    exon    167    169    .    +    .    ID=exon_12561;Parent=mRNA_2211
scaffold_1    fgenesh1_pg    CDS    167    169    .    +    0    ID=CDS_12138;Parent=mRNA_2211
```
1. \-ibf: BUSCO data file. This is usually called “short\_summary.txt” by default when BUSCO is run. Else, you can also provide the BUSCO summary using the \-ib option.  
   Generally, you can run BUSCO using a command like:  
   `busco -i proteins.fasta --auto-lineage-euk -o busco_output -m prot`  
1. \-id: Pfam data for all the proteins. This is a tab separated text file where the proteinId (or whatever other attribute was specified for the gff3 file using the \-mg option) is in column 1 and a pfam domain associated with that protein is in one of the columns such that the immediately preceding column has the text ‘Pfam’.   
   eg: Using InterProScan-5.78-109.0, you can run the command  
   `interproscan.sh -i inout.proteins.fasta -f TSV -appl Pfam`  
   And it will produce an output like
```
402418	2b03b8b3595baefa2f3a895a67a8f2a9	89	Pfam	PF11034	Glucose-repressible protein Grg1	19	87	5.0E-21	T	18-09-2026	IPR020100	Glucose-repressible protein Grg1	-	-`  
441626	d5e71191dbd9b5da093aa60e64e17393	717	Pfam	PF00083	Sugar (and other) transporter	174	613	7.5E-104	T	18-09-2026	IPR005828	Major facilitator, sugar transporter-like	-	-`  
429463	47008df6a8758c4a622f27615b9ed5be	814	Pfam	PF13374	Tetratricopeptide repeat	525	552	27.0	T	18-09-2026	-	-	-	-`  
429463	47008df6a8758c4a622f27615b9ed5be	814	Pfam	PF13374	Tetratricopeptide repeat	721	751	0.013	T	18-09-2026	-	-	-	-`  
429463	47008df6a8758c4a622f27615b9ed5be	814	Pfam	PF13374	Tetratricopeptide repeat	754	791	0.91	T	18-09-2026	-	-	-	-`  
429463	47008df6a8758c4a622f27615b9ed5be	814	Pfam	PF13424	Tetratricopeptide repeat	638	710	3.9E-14	T	18-09-2026	-	-	-	-`  
429463	47008df6a8758c4a622f27615b9ed5be	814	Pfam	PF13424	Tetratricopeptide repeat	560	627	2.0E-10	T	18-09-2026	-	-	-	-`  
466084	97cde85c0dd806bd6fb831f06d6e528e	133	Pfam	PF04828	Glutathione-dependent formaldehyde-activating enzyme	4	116	1.0E-19	T	18-09-2026	IPR006913	CENP-V/GFA domain	-	-`
```
   If any other format is used, it can be specified using the \-md option. Eg: If you use a two column file with the proteinId in column 1 and the pfam data in column 2, use \-md '(?\<id\>\\w+)\\t(?\<domain\>\\w+)'

**Interpreting the results.**  
After successful completion of the calculation, the software will provide the following data.

1. Total records:  This is the total number of features in the gff file.  
1. Total genes:  Count of protein coding genes in the gff file.  
1. Total scaffolds:  Number of records in the assembly fasta file.  
1. Total proteins: This is the number of proteins. Ideally, it should be the same as “Total genes” in 2 above. See 10 below for why.  
1. Total proteins with domains: This is the number of proteins with at least one pfam domain as provided in the pfam data file \[-id parameter\]. Generally, this number should be \>30% of the total number of genes. Else, either the pfam prediction was incomplete, or the file provided is not in the correct format. Use the \-md parameter to specify the input format of the pfam data file.  
1. Total unique domains:  The number of unique pfams in the pfam data file provided.  
1. Protein lengths distribution factor:  This is the PLD score where 1 is ideal and \>0.9 is very good. See the manuscript for how this is calculated. A low number here will significantly reduce the final ABAQS score.  
1. Incomplete genes factor: The proportion of genes that have both a start and stop codon. A low number here will significantly reduce the final ABAQS.  
1. Transposable elements factor: Proportion of TE genes retained in the predicted proteome. See the manuscript for how this is calculated. In short, these are identified using a curated list of known TE PFAM domains.   
1. Isoforms factor:  The percentage of isoforms in the input data (gff file). This should ideally be zero \[See the underlying manuscript for why this is preferred\]. Coding sequences (CDSs) that overlap another CDS by more than 25% are classified as isoforms.  
1. BUSCO duplicated factor: Estimation of genome duplication produced by BUSCO \[D/(1+D)\].  
1. BUSCO complete factor:  Estimation of genome completeness estimated by BUSCO.  
1. ABAQS score: Final ABAQS on a 0-1 scale.



# Copyright Notice

Accuracy-Based Annotation Quality Score (**ABAQS**) Copyright (c) 2025, The Regents of the University of California, 
through Lawrence Berkeley National Laboratory (subject to receipt of any required approvals from the U.S. Dept. of Energy). All rights reserved.

If you have questions about your rights to use or distribute this software,
please contact Berkeley Lab's Intellectual Property Office at
IPO@lbl.gov.

*NOTICE*.  This Software was developed under funding from the U.S. Department
of Energy and the U.S. Government consequently retains certain rights.  As
such, the U.S. Government has been granted for itself and others acting on
its behalf a paid-up, nonexclusive, irrevocable, worldwide license in the
Software to reproduce, distribute copies to the public, prepare derivative 
works, and perform publicly and display publicly, and to permit others to do so.
