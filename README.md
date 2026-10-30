# ABAQS User Guide

**Accuracy-Based Annotation Quality Score (ABAQS)**  
Version 1.0 — Developed at the Lawrence Berkeley National Laboratory / Joint Genome Institute

---

## Overview

ABAQS evaluates the quality of eukaryotic genome annotations as a single numeric score between 0 and 1. It was tested primarily using fungal genomes from MycoCosm and algal genomes in PhycoCosm, but is broadly applicable across eukaryotes.

Unlike single-dimensional metrics, ABAQS integrates multiple quality signals:

| Factor | What it measures |
|--------|-----------------|
| Protein length distribution (PLD) | How closely the proteome's size distribution matches a reference |
| Incomplete genes | Proportion of gene models with both a start and stop codon |
| Transposable elements (TE) | Proportion of TE-like gene models retained in the proteome |
| Isoforms | Proportion of overlapping/duplicate gene models |
| BUSCO completeness | Proportion of expected universal single-copy orthologs found |
| BUSCO duplication | Estimated genome duplication from BUSCO |

ABAQS reports scores **before and after filtering**. The post-filtering score reflects the annotation after automatically removing TE models and isoforms (retaining the longest isoform per locus).

---

## Requirements

### Software

| Dependency | Version | Notes |
|-----------|---------|-------|
| Java | >= 11 | Runtime only; Java 21+ needed to build |
| Maven | Any recent | Build only |
| InterProScan | 5.x recommended | For Pfam domain prediction (or provide pre-computed results) |
| BUSCO | 5.x or 6.x | For genome completeness (or provide pre-computed results) |

### Installation on Ubuntu/Debian

```bash
sudo apt update
sudo apt install git default-jdk maven -y
mvn -version   # verify
```

---

## Building from Source

```bash
git clone https://github.com/mycocosm-lbl/abaqs.git
cd abaqs
mvn clean package
```

The executable JAR is produced at:

```
target/abaqs-jar-with-dependencies.jar
```

Quick test:

```bash
java -jar target/abaqs-jar-with-dependencies.jar
```

---

## Docker

A pre-built image is available on Docker Hub:

```bash
docker run mycocosm/abaqs:latest [options]
```

When using Docker, mount your data directory into the container:

```bash
docker run --rm \
  -v /path/to/your/data:/data \
  mycocosm/abaqs:latest \
  -ig /data/input.gff3 \
  -is /data/assembly.fasta \
  -ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758' \
  -o /data/abaqs_results.txt
```

---

## Required and Recommended Inputs

ABAQS needs four pieces of data for a complete score. Two are required; two are strongly recommended.

### 1. Assembly FASTA (`-is`) — required

The genome assembly in FASTA format. **This must be softmasked** (repeat regions in lowercase) using a tool such as RepeatMasker. For best results, run RepeatMasker with `-nolow` to ignore simple/low-complexity repeats, since only transposon-related masking is relevant to the TE detection step.

```
-is input.softmasked.assembly.fasta
```

Gzipped FASTA (`.fasta.gz`) is accepted.

### 2. Gene models (`-ig`) — required

Gene and protein models in **GFF3 or GTF** format. The file type is detected automatically from the file extension (`.gff3`, `.gff`, `.gtf`). Gzipped files are supported.

Each gene model must have a unique protein identifier. By default ABAQS uses the gene record's `ID` attribute. Example GFF3 lines:

```
##gff-version 3
scaffold_1  fgenesh1_pg  gene  167  2008  0  +  .  ID=gene_2211;proteinId=1833732;transcriptId=1833838
scaffold_1  fgenesh1_pg  mRNA  167  2008  .  +  .  ID=mRNA_2211;Parent=gene_2211;proteinId=1833732
scaffold_1  fgenesh1_pg  exon  167  169   .  +  .  ID=exon_12561;Parent=mRNA_2211
scaffold_1  fgenesh1_pg  CDS   167  169   .  +  0  ID=CDS_12138;Parent=mRNA_2211
```

If your GFF3 file uses a different attribute for the protein identifier (e.g. `proteinId`), specify it with `-mg 'attributes:proteinId:.*->{0}'`.

### 3. BUSCO data (`-ib` or `-ibf`) — recommended

BUSCO measures how many expected universal single-copy orthologs are present and complete in the proteome.

**Option A — pass the summary string directly:**

```
-ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758'
```

Copy this string from your BUSCO `short_summary*.txt` output file.

**Option B — pass the BUSCO output file:**

```
-ibf /path/to/short_summary.specific.fungi_odb10.txt
```

**Option C — let ABAQS run BUSCO automatically** (see [Automated Workflows](##Workflows) below).

If BUSCO data is omitted, ABAQS assumes a perfect BUSCO score, which will overestimate the final ABAQS for incomplete assemblies.

### 4. Pfam domain data (`-id`) — recommended

InterProScan Pfam predictions for all proteins. This is a tab-separated file produced by InterProScan with the `-appl Pfam` option.

Run InterProScan:

```bash
interproscan.sh -i proteins.fasta -f TSV -appl Pfam
```

Example output (first few columns matter):

```
402418  2b03b8b...  89  Pfam  PF11034  Glucose-repressible protein Grg1  ...
441626  d5e711...  717  Pfam  PF00083  Sugar transporter  ...
```

Pass to ABAQS:

```
-id proteins_interproscan.tsv
```

If omitted, ABAQS will skip Pfam-based TE detection.

> **Tip:** At least 30% of proteins should have at least one Pfam domain. If fewer do, check that InterProScan completed successfully and that the correct format was used.

**Option — let ABAQS run InterProScan automatically** (see [Automated Workflows](##Workflows) below).

---

## Workflows

### Workflow 1 — Minimal run (BUSCO string, no InterProScan)

Use when you have pre-computed BUSCO but do not have Pfam data. TE detection will be limited to masking-based inference.

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758'
```

Results are printed to the console. Add `-o results.txt` to save them.

---

### Workflow 2 — Standard run with Pfam data and BUSCO string

This is the recommended workflow when you have pre-computed InterProScan output.

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -id proteins_interproscan.tsv \
  -ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758' \
  -o abaqs_results.txt
```

---

### Workflow 3 — Standard run with output proteins FASTA

Adds `-op` to save the translated protein sequences for downstream use (e.g. running BUSCO or InterProScan yourself).

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758' \
  --output-proteins-fasta abaqs_output/output.proteins.fasta
```

---

### Workflow 4 — Automated run: ABAQS calls InterProScan and BUSCO

ABAQS can run InterProScan and/or BUSCO automatically. Pass the command line (including any extra flags such as `--cpu`) using `-icl` and `-bcl`.

**With automatic lineage detection:**

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -icl '/path/to/interproscan.sh --cpu 8' \
  -bcl '/path/to/busco --cpu 8 --auto-lineage-euk' \
  --output-proteins-fasta abaqs.output_proteins.fasta \
  --working-dir /path/to/work_dir
```

**With a specific BUSCO lineage dataset (e.g. fungi):**

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -icl '/path/to/interproscan.sh --cpu 8' \
  -bcl '/path/to/busco --cpu 8 --lineage_dataset fungi_odb10' \
  --output-proteins-fasta abaqs.output_proteins.fasta \
  --working-dir /path/to/work_dir
```

ABAQS creates a `working-dir/interpro/` and `working-dir/busco/` subdirectory for intermediate files. The working directory defaults to the current directory if `--working-dir` is not specified.

---

### Workflow 5 — Post-filtering GFF3 output

To export a cleaned GFF3 file with TE models and isoforms removed, use `--output-gff`. The output type (GFF3 or GTF) is inferred from the file extension.

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -id proteins_interproscan.tsv \
  -ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758' \
  --output-gff filtered_output.gff3 \
  -o abaqs_results.txt
```

---

### Workflow 6 — Verbose/diagnostic output

Use `-v` to see detailed logging (isoform pairs, TE calls, protein translations). Add `-vo` to save supplemental TSV files (protein length bins, computation details) to a folder.

```bash
java -jar /path/to/abaqs-jar-with-dependencies.jar \
  -ig input.gff3 \
  -is input.softmasked.assembly.fasta \
  -id proteins_interproscan.tsv \
  -ib 'C:99.74%[S:91.16%,D:8.58%],F:0.26%,M:0.0%,n:758' \
  -v \
  -vo verbose_output/
```

---

## All Options Reference

### Required

| Short | Long | Description |
|-------|------|-------------|
| `-ig` | `--input-gff` | Path to GFF3 or GTF file. Type detected by extension. Gzip supported. |

### Recommended

| Short | Long | Description |
|-------|------|-------------|
| `-is` | `--input-scaffolds-fasta` | Softmasked assembly FASTA. Required unless the GFF3 file embeds a `##FASTA` section. |
| `-ib` | `--busco-data` | BUSCO summary string, e.g. `'C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758'` |
| `-ibf` | `--busco-data-file` | Path to BUSCO `short_summary*.txt` file |
| `-id` | `--input-domains` | Path to InterProScan TSV file with Pfam domains |

### Automated execution

| Short | Long | Description |
|-------|------|-------------|
| `-icl` | `--interpro-command-line` | Command to run InterProScan. ABAQS appends `--applications Pfam --formats TSV` automatically. |
| `-bcl` | `--busco-command-line` | Command to run BUSCO. ABAQS appends `--mode prot --in <proteins.fasta> --out <output> --force`. |
| `-wd` | `--working-dir` | Working directory for intermediate files. Defaults to `.` |

### Protein handling

| Short | Long | Default | Description |
|-------|------|---------|-------------|
| `-ip` | `--input-proteins-fasta` | — | Pre-computed protein FASTA. If omitted, proteins are translated from the GFF3/assembly. |
| `-op` | `--output-proteins-fasta` | — | Save the (translated or loaded) proteins to a FASTA file. |
| `-g` | `--gene-code` | 1 | NCBI genetic code ID for translation. |
| `-igc` | `--gene-code-input-file` | internal | Path to NCBI `gc.prt` file. |

### ID mapping

| Short | Long | Default | Description |
|-------|------|---------|-------------|
| `-mg` | `--gff3-protein-id-mapper` | `id:` | How to extract protein ID from GFF3 records. |
| `-mp` | `--protein-fasta-protein-id-mapper` | `.+proteinId\s*=\s*(\d+).*->{1}` | How to extract protein ID from FASTA headers. |
| `-md` | `--domains-protein-id-mapper` | Pfam TSV pattern | Regex (named groups `id` and `domain`) for parsing domain files. |

### Scoring parameters

| Short | Long | Default | Description |
|-------|------|---------|-------------|
| `-io` | `--isoforms-min-overlap` | 0.25 | Minimum CDS overlap fraction to classify two models as isoforms. |
| `-ilr` | `--reference-protein-lengths-input-file` | internal | Reference protein length distribution for PLD factor. |
| `-plb` | `--protein-length-binning` | 5 | Bin size for protein length distribution (in amino acids × 5 = aa bins). |

### Transposable element detection

| Short | Long | Default | Description |
|-------|------|---------|-------------|
| `-ite` | `--te-input-file` | internal | File of known TE Pfam domain IDs. |
| `-ise` | `--suspected-te-input-file` | internal | File of suspected TE Pfam domain IDs. |
| `-mf` | `--masker-function` | `TO_LOWER_CASE` | How repeat-masked positions are detected in the FASTA. |
| `-ndc` | `--no-domain-masked-cutoff` | 0.2 | CDS masking fraction above which a domain-free gene is called a TE. |
| `-sdc` | `--suspected-domain-masked-cutoff` | NaN (always TE) | CDS masking fraction above which a suspected-TE-domain gene is called a TE. |

### Output

| Short | Long | Default | Description |
|-------|------|---------|-------------|
| `-o` | `--output` | console | Path for the results file. |
| `-og` | `--output-gff` | — | Output filtered GFF3 or GTF file. |
| `-fw` | `--fasta-width` | 70 | Line width for FASTA sequences embedded in GFF3 output. |
| `-v` | `--verbose` | false | Enable verbose logging. |
| `-vo` | `--verbose-output-folder` | — | Folder to save supplemental TSV files. |

---

## Interpreting the Output

ABAQS prints two blocks: **before filtering** and **after filtering** (TEs and non-longest isoforms removed).

```
ABAQS from input: 'input.gff3'
before filtering

Total records:                   <all lines in GFF file>
Total genes:                     <protein-coding gene count>
Total scaffolds:                 <scaffold count in assembly FASTA>
Total proteins:                  <unique proteins analyzed>
Total proteins with domains:     <proteins with ≥1 Pfam domain>
Total unique domains:            <unique Pfam IDs observed>
Protein lengths distribution factor:  0.9xyz
Incomplete genes factor:              0.9xyz
Transposable elements factor:         0.0xyz
Isoforms count:                  N
Isoforms factor:                      0.0xyz
BUSCO duplicated factor:              0.0xyz
BUSCO complete factor:                0.9xyz
ABAQS score:                          0.9xyz

ABAQS after filtering
...
```

### Score interpretation

| Metric | Ideal value | Notes |
|--------|------------|-------|
| Protein lengths distribution factor | ~1.0 (≥0.9 is very good) | Low values strongly penalize the final score |
| Incomplete genes factor | ~1.0 | Fraction of genes with both start and stop codons |
| Transposable elements factor | ~0.0 | Fraction of gene models classified as TEs |
| Isoforms factor | ~0.0 | Fraction of gene models that are non-primary isoforms |
| BUSCO duplicated factor | ~0.0 | D/(1+D); penalizes duplicated BUSCOs |
| BUSCO complete factor | ~1.0 | Fraction of expected BUSCOs that are complete |
| **ABAQS score** | **~1.0** | Final composite score; ≥0.9 is considered high quality |

> If **Total proteins with domains** is less than 30% of Total proteins, the Pfam data may be incomplete or in an unexpected format. Verify your InterProScan output and use the `-md` option if necessary.

> If **Total proteins** differs significantly from **Total genes**, this usually indicates isoforms in the input or a mismatch in protein ID mapping. Use `-v` to investigate.

---

## Tips and Troubleshooting

**My GFF3 uses a non-standard protein ID attribute.**  
Use `-mg 'attributes:proteinId:.*->{0}'` to pull from the `proteinId` attribute, or adapt the pattern to your attribute name.

**My domain file uses a custom format.**  
Use `-md` with a Java regex that captures named groups `id` (protein ID) and `domain` (Pfam accession). For a two-column file:  
`-md '(?<id>\w+)\t(?<domain>\w+)'`

**ABAQS reports many incomplete genes.**  
This is common when gene models are predicted ab initio and may be truncated at scaffold boundaries. It will lower the incomplete-genes factor and the final ABAQS score, which is expected.

**BUSCO duplication factor is high.**  
This often indicates a partially collapsed assembly or retained heterozygosity. The BUSCO duplicated factor is computed as `D / (1 + D)`, so even moderate duplication has a noticeable effect.

**I want to use a custom reference protein length distribution.**  
Provide a two-column TSV (length, frequency) with `-ilr`. The internal reference was built from a large set of validated RefSeq proteins.

**Running on an HPC cluster.**  
When calling InterProScan or BUSCO via `-icl` / `-bcl`, make sure the commands are available in your `PATH` or specify full paths. The `--working-dir` flag controls where intermediate files are written, which should be on a writable filesystem.

---

## Copyright

Accuracy-Based Annotation Quality Score (ABAQS) Copyright (c) 2025, The Regents of the University of California, through Lawrence Berkeley National Laboratory (subject to receipt of any required approvals from the U.S. Dept. of Energy). All rights reserved.

Questions about rights to use or distribute this software: IPO@lbl.gov

*NOTICE.* This Software was developed under funding from the U.S. Department of Energy and the U.S. Government consequently retains certain rights. As such, the U.S. Government has been granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable, worldwide license in the Software to reproduce, distribute copies to the public, prepare derivative works, and perform publicly and display publicly, and to permit others to do so.
