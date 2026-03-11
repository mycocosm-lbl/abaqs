# ABAQS
Accuracy-Based Annotation Quality Score (ABAQS)

# Abstract
As fungal genome resources continue to expand, estimating the accuracy and completeness of genome annotations (e.g., gene and protein models) becomes essential to ensure quality of downstream analyses and applications. It becomes critical in comparative genomic analysis to select genomes with comparable qualities of assembly and annotation. Without careful scrutiny, inconsistencies in annotation quality can undermine downstream studies.

A robust quality metric should ideally capture multiple dimensions of genome annotation accuracy. However, in practice, most commonly used quality metrics are single-dimensional or consider only a few aspects of annotation quality,  leaving a large space of high-scored but low-quality genome annotations unpenalized. For example, tools like BUSCO (Manni et al. 2021) and CEGMA (Parra, Bradnam, and Korf 2007) evaluate assembly and annotation qualities by detecting conserved gene content in assemblies or predicted proteomes. Such methods solely focus on genome and proteome completeness which is insufficient to generate a full picture of assembly and annotation quality.

To address this gap, we introduce the Accuracy-Based Annotation Quality Score (ABAQS), a comprehensive and minimal-data-driven method to summarize genome annotation quality as a single easily understandable numeric value. While developed primarily using fungal genomes from [MycoCosm](https://mycocosm.jgi.doe.gov), ABAQS is broadly applicable across eukaryotes. ABAQS evaluates multiple factors, including genome completeness, gene model validity, and protein profile accuracy, outperforming metrics like BUSCO and PSAURON. We applied ABAQS to 1700+ eukaryotic genomes and showed its robustness and effectiveness in evaluating genome annotation quality, making it a valuable tool for researchers working with genomic data.

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
