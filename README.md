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
Error parsing command line: Missing required option: ig
usage:
org.mycocosm.abaqs.main.ABAQS [options]
options:
 -fw,--fasta-width <arg>                             fasta output idth, default=70
 -ib,--busco-data <arg>                              Busco data, like 'C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758'
 -ibf,--busco-data-file <arg>                        Busco data file
 -id,--input-domains <arg>                           input domains file path
 -ig,--input-gff <arg>                               input gff3 file path
 -igc,--gene-code-input-file <arg>                   Gene code input file (gc.prt), if missing internal copy will be used
 -ilr,--reference-protein-lengths-input-file <arg>   Reference protein length distribution file, if missing internal reference will be used
 -io,--isoforms-min-overlap <arg>                    Minimum overlap to detect genes isoforms by coding positions, default=0.25
 -ip,--input-proteins-fasta <arg>                    input proteins fasta file path
 -is,--input-scaffolds-fasta <arg>                   input scaffolds fasta file path
 -ise,--suspected-te-input-file <arg>                Suspected transposable elements pfam domains input file, if missing internal list will be used
 -ite,--te-input-file <arg>                          Transposable elements pfam domains input file, if missing internal list will be used
 -md,--domains-protein-id-mapper <arg>               mapper for protein id and domains in domains records, default='(?<id>\w+)\t.*\tHMMPfam\t(?<domain>\w+)\t.*'
 -mf,--masker-function <arg>                         Masker function, default='TO_LOWER_CASE'
 -mg,--gff3-protein-id-mapper <arg>                  mapper for protein id in gff3 recors, default='attributes:proteinId:.*->{0}' , meaning use proteinId attribute for gene record
 -mp,--protein-fasta-protein-id-mapper <arg>         mapper for protein id in protein fasta recors, default='.+proteinId\s*=\s*(\d+).*->{1}'
 -ndc,--no-domain-masked-cutoff <arg>                Masked CDS cutoff for TE detection with no Pfam domains, NaN mean not used, default=0.2
 -o,--output <arg>                                   output path, optional, default to stdout
 -og,--output-gff <arg>                              gff3 output path, optional, default to none
 -plb,--protein-length-binning <arg>                 Protein length distribution binning, default=5
 -sdc,--suspected-domain-masked-cutoff <arg>         Masked CDS cutoff for TE detection with suspected TE Pfam domains, NaN mean always TE, default=NaN
 -v,--verbose                                        produce verbose output
 -vo,--verbose-output-folder <arg>                   output folder for verbose output, optional
```

## Docker

We also maintain Docker image on the DockerHub so you can run it with docker

```
docker run mycocosm/abaqs:latest
Error parsing command line: Missing required option: ig
usage:
org.mycocosm.abaqs.main.ABAQS [options]
options:
 -fw,--fasta-width <arg>                             fasta output idth, default=70
 -ib,--busco-data <arg>                              Busco data, like 'C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758'
 -ibf,--busco-data-file <arg>                        Busco data file
 -id,--input-domains <arg>                           input domains file path
 -ig,--input-gff <arg>                               input gff3 file path
 -igc,--gene-code-input-file <arg>                   Gene code input file (gc.prt), if missing internal copy will be used
 -ilr,--reference-protein-lengths-input-file <arg>   Reference protein length distribution file, if missing internal reference will be used
 -io,--isoforms-min-overlap <arg>                    Minimum overlap to detect genes isoforms by coding positions, default=0.25
 -ip,--input-proteins-fasta <arg>                    input proteins fasta file path
 -is,--input-scaffolds-fasta <arg>                   input scaffolds fasta file path
 -ise,--suspected-te-input-file <arg>                Suspected transposable elements pfam domains input file, if missing internal list will be used
 -ite,--te-input-file <arg>                          Transposable elements pfam domains input file, if missing internal list will be used
 -md,--domains-protein-id-mapper <arg>               mapper for protein id and domains in domains records, default='(?<id>\w+)\t.*\tHMMPfam\t(?<domain>\w+)\t.*'
 -mf,--masker-function <arg>                         Masker function, default='TO_LOWER_CASE'
 -mg,--gff3-protein-id-mapper <arg>                  mapper for protein id in gff3 recors, default='attributes:proteinId:.*->{0}' , meaning use proteinId attribute for gene record
 -mp,--protein-fasta-protein-id-mapper <arg>         mapper for protein id in protein fasta recors, default='.+proteinId\s*=\s*(\d+).*->{1}'
 -ndc,--no-domain-masked-cutoff <arg>                Masked CDS cutoff for TE detection with no Pfam domains, NaN mean not used, default=0.2
 -o,--output <arg>                                   output path, optional, default to stdout
 -og,--output-gff <arg>                              gff3 output path, optional, default to none
 -plb,--protein-length-binning <arg>                 Protein length distribution binning, default=5
 -sdc,--suspected-domain-masked-cutoff <arg>         Masked CDS cutoff for TE detection with suspected TE Pfam domains, NaN mean always TE, default=NaN
 -v,--verbose                                        produce verbose output
 -vo,--verbose-output-folder <arg>                   output folder for verbose output, optional
```
# Basic calculation of ABAQS requires four pieces of data.

1. -is: The assembly fasta file. This should be softmasked using a repeatmasking program. Ideally, low-complexity repeats are ignored such as by running RepeatMasker with the `-nolow` option.
1. -ig: Protein models in gff3 format. Each gene feature / protein must have a unique name and it is expected that the name is specified in the attributes field as proteinId. If some other field is used to name the protein, you can use the `-mg` option to specify it. Here are a few lines from an example file.
```
##gff-version 3
##sequence-region scaffold_1 1 1958655
scaffold_1    fgenesh1_pg    gene    167    2008    0    +    .    ID=gene_2211;feature_name=fgenesh1_pg.1_#_1;Name=gene-jgi|Clapy1|1833732;portal_id=Clapy1;proteinId=1833732;transcriptId=1833838
scaffold_1    fgenesh1_pg    mRNA    167    2008    .    +    .    ID=mRNA_2211;Parent=gene_2211;Name=jgi|Clapy1|1833732;product=expressed protein;proteinId=1833732;track=FilteredModels1;transcriptId=1833838
scaffold_1    fgenesh1_pg    exon    167    169    .    +    .    ID=exon_12561;Parent=mRNA_2211
scaffold_1    fgenesh1_pg    CDS    167    169    .    +    0    ID=CDS_12138;Parent=mRNA_2211
```
1. -ibf: BUSCO data file. This is usually called “short_summary.txt” by default when BUSCO is run. Else, you can also provide the BUSCO summary using the `-ib` option.
1. -id: Pfam data for all the proteins. This is a tab separated text file where the proteinId (or whatever other attribute was specified for the gff3 file using the -mg option) is in column 1 and a pfam domain associated with that protein is in one of the columns such that the immediately preceding column has the text ‘HMMPfam’. If any other format is used, it can be specified using the `-md` option. eg: if you use the default tsv output of Interproscan, then, it uses ‘Pfam’ instead of ‘HMMPfam’. Therefore, we can use `-md '(?<id>\w+)\t.*\tPfam\t(?<domain>\w+)\t.*'`
If you use a two column file with the proteinId in column 1 and the pfam data in column 2, use `-md '(?<id>\w+)\t(?<domain>\w+)'`

# Interpreting the results.

After successful completion of the calculation, the software will provide the following data.

1. Total records:  This is the total number of features in the gff file.
1. Total genes:  Count of protein coding genes in the gff file.
1. Total scaffolds:  Number of records in the assembly fasta file.
1. Total proteins: This is the number of proteins. Ideally, it should be the same as “Total genes” in 2 above. See 10 below for why.
1. Total proteins with domains: This is the number of proteins with at least one pfam domain as provided in the pfam data file [-id parameter]. Generally, this number should be >30% of the total number of genes. Else, either the pfam prediction was incomplete, or the file provided is not in the correct format. Use the -md parameter to specify the input format of the pfam data file.
1. Total unique domains:  The number of unique pfams in the pfam data file provided.
1. Protein lengths distribution factor:  This is the PLD score where 1 is ideal and >0.9 is very good. See the manuscript for how this is calculated. A low number here will significantly reduce the final ABAQS score.
1. Incomplete genes factor: The proportion of genes that have both a start and stop codon. A low number here will significantly reduce the final ABAQS.
1. Transposable elements factor: Proportion of TE genes retained in the predicted proteome. See the manuscript for how this is calculated. In short, these are identified using a curated list of known TE PFAM domains. 
1. Isoforms factor:  The percentage of isoforms in the input data (gff file). This should ideally be zero [See the underlying manuscript for why this is preferred]. Coding sequences (CDSs) that overlap another CDS by more than 25% are classified as isoforms.
1. BUSCO duplicated factor: Estimation of genome duplication produced by BUSCO.
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
