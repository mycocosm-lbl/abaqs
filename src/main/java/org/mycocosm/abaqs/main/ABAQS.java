package org.mycocosm.abaqs.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableInt;
import org.mycocosm.abaqs.compare_tracks.FeaturePair;
import org.mycocosm.abaqs.compare_tracks.FeatureTrack;
import org.mycocosm.abaqs.compare_tracks.FeatureTrackType;
import org.mycocosm.abaqs.compare_tracks.TrackNameAndType;
import org.mycocosm.abaqs.compare_tracks.TrackToTrackMapping;
import org.mycocosm.abaqs.main.MapperDefinitionsFactory.FastaRecordIdMapper;
import org.mycocosm.abaqs.main.MapperDefinitionsFactory.Gff3RecordIdMapper;
import org.mycocosm.framework.biology.MaskerFunction;
import org.mycocosm.framework.cli.BatchHelper;
import org.mycocosm.framework.cli.BatchRunnableCli;
import org.mycocosm.framework.cli.CliOption;
import org.mycocosm.framework.cli.ErrorExitException;
import org.mycocosm.framework.collections.BinnedCollection;
import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.collections.SetsCompareResult;
import org.mycocosm.framework.fasta.FastaFile;
import org.mycocosm.framework.fasta.Scaffold;
import org.mycocosm.framework.fasta.SequenceType;
import org.mycocosm.framework.fasta.SimpleFastaSequenceWithId;
import org.mycocosm.framework.fasta.SimpleFastaSequenceWithIdAndExtra;
import org.mycocosm.framework.io.FilesHelper;
import org.mycocosm.framework.io.IoHelper;
import org.mycocosm.framework.logger.LoggerFactory;
import org.mycocosm.framework.logger.LoggerHelper;
import org.mycocosm.framework.text.PatternHelper;
import org.mycocosm.framework.text.TextHelper;
import org.mycocosm.gff3.GFF3Data;
import org.mycocosm.gff3.Gff3Record;
import org.mycocosm.gff3.Gff3Type;
import org.mycocosm.ncbi.GeneCode;
import org.mycocosm.ncbi.GeneCode.TranslationResult;
import org.mycocosm.ncbi.GeneCodeHelper;
import org.mycocosm.sequence.SequenceHelper;


/*
--input-gff /scratch/abaqs/Aalte1_ExternalModels_2026-03-05.gff3.gz
--input-domains /scratch/abaqs/Aalte1_GeneCatalog_proteins_20190828_IPR.tab.gz
--busco-data C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758
 */
public class ABAQS implements BatchRunnableCli {

	// scaffold_1	Genemark4	gene	14	859	0	-	.	ID=gene_2980;feature_name=gm4.1_g;Name=gene-jgi|Nitpal1|1302658;portal_id=Nitpal1;product=hypothetical protein;proteinId=1302658;transcriptId=1303014
	public static final String DEFAULT_GFF3_TO_PROTEIN_ID_MAPPER = "attributes:proteinId:.*->{0}"; 
	// >jgi|Nitpal1|781|CE780_67 proteinId=781 transcriptId=781
	public static final String DEFAULT_PROTEIN_FASTA_TO_PROTEIN_ID_MAPPER = ".+proteinId\\s*=\\s*(\\d+).*->{1}"; 
	public static final Pattern DEFAULT_DOMAINS_MAPPER = Pattern.compile("(?<id>\\w+)\\t.*\\tHMMPfam\\t(?<domain>\\w+)\\t.*", Pattern.CASE_INSENSITIVE);
	public static final int DEFAULT_GENE_CODE = GeneCode.DEFAULT_GENE_CODE; 
	public static final double DEFAULT_ISOFORMS_MIN_OVERLAP = 0.25;
	public static final MaskerFunction DEFAULT_MASKER_FUNCTION = MaskerFunction.TO_LOWER_CASE;
	public static final double DEFAULT_NO_DOMAINS_CDS_MASKED_CUTOFF = 0.20;
	public static final double DEFAULT_SUSPECTED_DOMAINS_CDS_MASKED_CUTOFF = Double.NaN;
	public static final int DEFAULT_PROTEIN_LENGTCH_BINNING = 5;

	public static final CliOption<Boolean> VERBOSE = CliOption.optionalBooleanNoArgument("v", "verbose", "produce verbose output");
	public static final CliOption<Path> VERBOSE_OUTPUT_FOLDER = CliOption.optionalPathWithArgument("vo", "verbose-output-folder", "output folder for verbose output, optional");
	public static final CliOption<Path> OUTPUT = CliOption.optionalPathWithArgument("o", "output", "output path, optional, default to stdout");
	public static final CliOption<Path> INPUT_GFF3 = CliOption.requiredPathWithArgument("ig", "input-gff", "input gff3 file path");
	public static final CliOption<Path> INPUT_SCAFFOLFS_FASTA = CliOption.optionalPathWithArgument("is", "input-scaffolds-fasta", "input scaffolds fasta file path");
	public static final CliOption<Path> INPUT_PROTEINS_FASTA = CliOption.optionalPathWithArgument("ip", "input-proteins-fasta", "input proteins fasta file path");
	public static final CliOption<Path> INPUT_DOMAINS = CliOption.optionalPathWithArgument("id", "input-domains", "input domains file path");
	public static final CliOption<Path> OUTPUT_RESULTS = CliOption.optionalPathWithArgument("o", "output", "output path, optional, default to stdout");
	public static final CliOption<Path> OUTPUT_GFF3 = CliOption.optionalPathWithArgument("og", "output-gff", "gff3 output path, optional, default to none");
	public static final CliOption<Integer> FASTA_WIDTH = CliOption.optionalIntegerWithArgument("fw", "fasta-width", "fasta output idth, default="+SequenceHelper.DEFAULT_FASTA_WIDTH,SequenceHelper.DEFAULT_FASTA_WIDTH);
	public static final CliOption<String> GFF3_PROTEIN_ID_MAPPER = CliOption.optionalStringWithArgument("mg", "gff3-protein-id-mapper", "mapper for protein id in gff3 recors, default='"+DEFAULT_GFF3_TO_PROTEIN_ID_MAPPER+"' , meaning use proteinId attribute for gene record",DEFAULT_GFF3_TO_PROTEIN_ID_MAPPER);
	public static final CliOption<String> PROTEIN_FASTA_PROTEIN_ID_MAPPER = CliOption.optionalStringWithArgument("mp", "protein-fasta-protein-id-mapper", "mapper for protein id in protein fasta recors, default='"+DEFAULT_PROTEIN_FASTA_TO_PROTEIN_ID_MAPPER+"'",DEFAULT_PROTEIN_FASTA_TO_PROTEIN_ID_MAPPER);
	public static final CliOption<Pattern> DOMAINS_MAPPER = CliOption.optionalPatternWithArgument("md", "domains-protein-id-mapper", "mapper for protein id and domains in domains records, default='"+DEFAULT_DOMAINS_MAPPER+"'",DEFAULT_DOMAINS_MAPPER);
	public static final CliOption<Integer> GENE_CODE = CliOption.optionalIntegerWithArgument("g", "gene-code", "fasta output idth, default="+DEFAULT_GENE_CODE,DEFAULT_GENE_CODE);
	public static final CliOption<Double> ISOFORMS_MIN_OVERLAP = CliOption.optionalDoubleWithArgument("io", "isoforms-min-overlap", "Minimum overlap to detect genes isoforms by coding positions, default="+DEFAULT_ISOFORMS_MIN_OVERLAP,DEFAULT_ISOFORMS_MIN_OVERLAP);
	public static final CliOption<String> BUSCO_DATA = CliOption.optionalStringWithArgument("ib", "busco-data", "Busco data, like 'C:99.3%[S:98.9%,D:0.4%],F:0.3%,M:0.4%,n:758'");
	public static final CliOption<Path> BUSCO_DATA_FILE = CliOption.optionalPathWithArgument("ibf", "busco-data-file", "Busco data file");
	public static final CliOption<String> MASKER_FUNCTION = CliOption.optionalStringWithArgument("mf", "masker-function", "Masker function, default='"+DEFAULT_MASKER_FUNCTION.id()+"'",DEFAULT_MASKER_FUNCTION.id());
	public static final CliOption<Path> TE_INPUT_FILE = CliOption.optionalPathWithArgument("ite", "te-input-file", "Transposable elements pfam domains input file, if missing internal list will be used");
	public static final CliOption<Path> SUSPECTED_TE_INPUT_FILE = CliOption.optionalPathWithArgument("ise", "suspected-te-input-file", "Suspected transposable elements pfam domains input file, if missing internal list will be used");
	public static final CliOption<Path> REFERENCE_PROTEINS_LENGTH_INPUT_FILE = CliOption.optionalPathWithArgument("ilr", "reference-protein-lengths-input-file", "Reference protein length distribution file, if missing internal reference will be used");
	public static final CliOption<Double> NO_DOMAINS_CDS_MASKED_CUTOFF = CliOption.optionalDoubleWithArgument("ndc", "no-domain-masked-cutoff", "Masked CDS cutoff for TE detection with no Pfam domains, NaN mean not used, default="+DEFAULT_NO_DOMAINS_CDS_MASKED_CUTOFF,DEFAULT_NO_DOMAINS_CDS_MASKED_CUTOFF);
	public static final CliOption<Double> SUSPECTED_DOMAINS_CDS_MASKED_CUTOFF = CliOption.optionalDoubleWithArgument("sdc", "suspected-domain-masked-cutoff", "Masked CDS cutoff for TE detection with suspected TE Pfam domains, NaN mean always TE, default="+DEFAULT_SUSPECTED_DOMAINS_CDS_MASKED_CUTOFF,DEFAULT_SUSPECTED_DOMAINS_CDS_MASKED_CUTOFF);
	public static final CliOption<Integer> PROTEIN_LENGTCH_BINNING = CliOption.optionalIntegerWithArgument("plb", "protein-length-binning", "Protein length distribution binning, default="+DEFAULT_PROTEIN_LENGTCH_BINNING,DEFAULT_PROTEIN_LENGTCH_BINNING);
	public static final CliOption<Path> GENE_CODE_FILE = CliOption.optionalPathWithArgument("igc", "gene-code-input-file", "Gene code input file (gc.prt), if missing internal copy will be used");

	private final Logger logger;

	public static final String VERSION = "1.0"; 


	private ABAQS(Logger logger) {
		this.logger = logger;
	}

	public static void main(String[] args) throws ParseException {
		BatchHelper.runCli(new ABAQS(LoggerFactory.getLogger(ABAQS.class)), args);
	}

	@Override
	public Options buildOptions() {
		return CliOption.buildCliOptions(VERBOSE, VERBOSE_OUTPUT_FOLDER, OUTPUT,INPUT_GFF3,INPUT_SCAFFOLFS_FASTA,INPUT_PROTEINS_FASTA,INPUT_DOMAINS,OUTPUT_RESULTS,OUTPUT_GFF3,FASTA_WIDTH,GFF3_PROTEIN_ID_MAPPER,PROTEIN_FASTA_PROTEIN_ID_MAPPER,DOMAINS_MAPPER,
				ISOFORMS_MIN_OVERLAP,BUSCO_DATA,BUSCO_DATA_FILE,MASKER_FUNCTION,TE_INPUT_FILE,SUSPECTED_TE_INPUT_FILE,REFERENCE_PROTEINS_LENGTH_INPUT_FILE,
				NO_DOMAINS_CDS_MASKED_CUTOFF,SUSPECTED_DOMAINS_CDS_MASKED_CUTOFF,PROTEIN_LENGTCH_BINNING,GENE_CODE_FILE);
	}

	@Override
	public void runBatch(CommandLine options) {
		boolean verbose = VERBOSE.getOptionValue(options);
		Path verboseOutput = VERBOSE_OUTPUT_FOLDER.getOptionValue(options);
		Path inputGff3 = INPUT_GFF3.getOptionValue(options);
		Path inputScaffoldsFasta = INPUT_SCAFFOLFS_FASTA.getOptionValue(options);
		Path inputProteinsFasta = INPUT_PROTEINS_FASTA.getOptionValue(options);
		Path inputDomains = INPUT_DOMAINS.getOptionValue(options);
		Path outputResults = OUTPUT_RESULTS.getOptionValue(options);
		Path outputGff3 = OUTPUT_GFF3.getOptionValue(options);
		int fastaWidth = FASTA_WIDTH.getOptionValue(options);
		int geneCode = GENE_CODE.getOptionValue(options);
		Path geneCodeFile = GENE_CODE_FILE.getOptionValue(options);
		Gff3RecordIdMapper gff3ProteinIdMapper = new MapperDefinitionsFactory.Gff3RecordIdMapper(GFF3_PROTEIN_ID_MAPPER.getOptionValue(options));
		FastaRecordIdMapper proteinFastaProteinIdMapper = new MapperDefinitionsFactory.FastaRecordIdMapper(PROTEIN_FASTA_PROTEIN_ID_MAPPER.getOptionValue(options));
		Pattern domainsProteinMapper = DOMAINS_MAPPER.getOptionValue(options);
		double isoformsMinimumOverlap = ISOFORMS_MIN_OVERLAP.getOptionValue(options);
		String buscoDataString = BUSCO_DATA.getOptionValue(options); 
		Path buscoDataFile = BUSCO_DATA_FILE.getOptionValue(options); 
		MaskerFunction maskerFunction = MaskerFunction.of(MASKER_FUNCTION.getOptionValue(options));
		Path teInputFile = TE_INPUT_FILE.getOptionValue(options); 
		Path suspectedTeInputFile = SUSPECTED_TE_INPUT_FILE.getOptionValue(options); 
		Path referenceProteinLengthDistributionFile = REFERENCE_PROTEINS_LENGTH_INPUT_FILE.getOptionValue(options);
		double noDomainCDSMaskedCutoff = NO_DOMAINS_CDS_MASKED_CUTOFF.getOptionValue(options);
		double suspectedDomainCDSMaskedCutoff = SUSPECTED_DOMAINS_CDS_MASKED_CUTOFF.getOptionValue(options);
		int proteinLengthBinning = PROTEIN_LENGTCH_BINNING.getOptionValue(options);

		try {
			processInput(
					logger, 
					inputGff3,
					outputResults,
					outputGff3, 
					verbose,
					verboseOutput,
					inputScaffoldsFasta, 
					inputProteinsFasta, 
					inputDomains, 
					gff3ProteinIdMapper, 
					proteinFastaProteinIdMapper, 
					domainsProteinMapper, 
					fastaWidth, 
					geneCode,
					geneCodeFile,
					isoformsMinimumOverlap,
					buscoDataString,
					buscoDataFile,
					maskerFunction,
					teInputFile,
					suspectedTeInputFile,
					referenceProteinLengthDistributionFile,
					noDomainCDSMaskedCutoff,
					suspectedDomainCDSMaskedCutoff,
					proteinLengthBinning
					);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final Path TE_RESOURCE_LOCATION = Path.of("abaqs/transposable-elements-pfams.txt");
	private static final Path SUSPECTED_TE_RESOURCE_LOCATION = Path.of("abaqs/suspected-transposable-elements-pfams.txt");

	private void processInput(
			Logger logger, 
			Path inputGff3,
			Path outputResults,
			Path outputGff3, 
			boolean verbose, 
			Path verboseOutputFolder,
			Path inputScaffoldsFasta, 
			Path inputProteinsFasta, 
			Path inputDomains, 
			Gff3RecordIdMapper 
			gff3ProteinIdMapper, 
			FastaRecordIdMapper proteinFastaProteinIdMapper, 
			Pattern domainsProteinMapper, 
			int fastaWidth, 
			int geneCode,
			Path geneCodeFile,
			double isoformsMinimumOverlap, 
			String buscoDataString, 
			Path buscoDataFile, 
			MaskerFunction maskerFunction, 
			Path transposableElementsFile, 
			Path suspectedTransposableElementsFile, 
			Path referenceProteinLengthDistributionFile, 
			double noDomainCDSMaskedCutoff, 
			double suspectedDomainCDSMaskedCutoff,
			int proteinLengthBinningSize
			) throws IOException {

		LoggerHelper.log(logger, Level.INFO, "Starting ABAQS version '%s'",VERSION);

		if (verbose) {
			showJava(logger);
		}

		GeneCode ncbiGeneCode = loadGeneCode(logger,geneCode, geneCodeFile);

		GFF3Data gffData = null;
		LoggerHelper.log(logger, Level.INFO, "Loading input GFF3 from:'%s'",inputGff3);
		try (BufferedReader inputReader = FilesHelper.newBufferedReaderOptionallyGzipped(inputGff3)) {
			gffData = GFF3Data.parse(logger, inputReader);
		}
		LoggerHelper.log(logger, Level.INFO, "Data loaded, total %,d records (%,d genes)",gffData.records.size(),gffData.records.stream().filter(Gff3Record::filterForTopLevelRegularRecords).count());
		if (outputGff3!=null) {
			LoggerHelper.log(logger, Level.INFO, "Dumping GFF3 records to:'%s'",outputGff3);
			try (PrintWriter outputGff3Writer = new PrintWriter(FilesHelper.newOutputStreamOptionallyGzipped(outputGff3)) ) {
				gffData.printAll(outputGff3Writer, fastaWidth);
			}
		}
		if (inputScaffoldsFasta!=null) {
			Map<String, SimpleFastaSequenceWithId> scaffolds = loadScaffoldsFasta(logger, inputScaffoldsFasta);
			gffData = gffData.replaceScaffolds(scaffolds);
		} else if (!gffData.hasScaffolds()) {
			throw ErrorExitException.ofMessage("You need to provide ether path to scaffolds fasta or GFF file with #FASTA record");
		}

		Map<String, SimpleFastaSequenceWithIdAndExtra> proteins;
		if (inputProteinsFasta!=null) { // have proteins to load
			proteins = loadProteinsFastaWithIdMapper(logger, inputProteinsFasta, proteinFastaProteinIdMapper);
		} else { // will translate proteins
			proteins = translateProteinsFastaWithIdMapper(logger, gffData, gff3ProteinIdMapper, ncbiGeneCode, verbose);
		}
		final Map<String, Set<PfamDomain>> domains = loadDomainsWithIdMapper(logger, inputDomains, domainsProteinMapper, verbose);
		final Set<PfamDomain> transposableElements = loadTEDomains(logger, "transposable elements", transposableElementsFile, TE_RESOURCE_LOCATION);
		final Set<PfamDomain> suspectedTransposableElements = loadTEDomains(logger, "suspected transposable elements", suspectedTransposableElementsFile, SUSPECTED_TE_RESOURCE_LOCATION);

		final Map<String,GeneRecord> geneRecords = createGeneRecords(logger, gffData, maskerFunction, proteins, domains, gff3ProteinIdMapper, transposableElements, suspectedTransposableElements, noDomainCDSMaskedCutoff, suspectedDomainCDSMaskedCutoff);

		LoggerHelper.log(logger, Level.INFO, "Total collected %,d mRNA records for analisys",geneRecords.size());

		double isoformsFactor = computeIsoformsFactor(logger, gffData, geneRecords, isoformsMinimumOverlap);
		LoggerHelper.log(logger, Level.INFO, "Isoforms factor: %.4f",isoformsFactor);

		BuscoData buscoData = loadBuscoData(logger, buscoDataString, buscoDataFile);
		double buscoCompleteFactor = Double.NaN;
		double buscoDuplicatedFactor = Double.NaN;
		if (buscoData!=null) {
			buscoCompleteFactor = buscoData.complete;
			buscoDuplicatedFactor = buscoData.duplicated;
		} else {
			buscoCompleteFactor = 1.0;
			buscoDuplicatedFactor = 0.0;
		}
		LoggerHelper.log(logger, Level.INFO, "BUSCO complete factor: %.4f",buscoCompleteFactor);
		LoggerHelper.log(logger, Level.INFO, "BUSCO duplicated factor: %.4f",buscoDuplicatedFactor);

		double incompleteGenesFactor = computeIncompleteGenesFactor(logger,geneRecords);
		LoggerHelper.log(logger, Level.INFO, "Incomplete genes factor: %.4f",incompleteGenesFactor);

		double transposableElementsFactor = computeTransposableElementsFactor(logger,geneRecords,transposableElements,suspectedTransposableElements);
		LoggerHelper.log(logger, Level.INFO, "Transposable elements factor: %.4f",transposableElementsFactor);

		Map<Integer,Double> referenceProteinLengthDitribution = loadReferenceProteinLengthDistribution(logger,referenceProteinLengthDistributionFile);
		Map<Integer,Double> organismProteinLengthDitribution = loadOrganismProteinLengthDistribution(logger,geneRecords.values().stream().filter(r->!r.detectedTtransposableElement).map(r->r.protein).collect(Collectors.toList()),proteinLengthBinningSize,verbose,verboseOutputFolder);
		double proteinLengthsDistributionFactor = computeProteinLengthDistributionFactor(logger, organismProteinLengthDitribution, referenceProteinLengthDitribution,verbose,verboseOutputFolder);
		LoggerHelper.log(logger, Level.INFO, "Protein lengths distribution factor: %.4f",proteinLengthsDistributionFactor);

		// Final computations
		double upper = Math.sqrt(proteinLengthsDistributionFactor * incompleteGenesFactor);
		double lower = 1.0 + 0.5 * (transposableElementsFactor + isoformsFactor + buscoDuplicatedFactor + 1.0 - buscoCompleteFactor);

		double abaqsScore = upper / lower;

		LoggerHelper.log(logger, Level.INFO, "Final ABAQS for '%s':%.4f",inputGff3,abaqsScore);
		try (PrintWriter resultsOutputWriter = FilesHelper.newPrintWriterOrStdOutput(outputResults)) {
			resultsOutputWriter.format("Computing ABAQS from input:\t'%s'\n",inputGff3);
			resultsOutputWriter.format("Total records:\t%d\n",gffData.records.size());
			resultsOutputWriter.format("Total genes:\t%d\n",gffData.records.stream().filter(Gff3Record::filterForTopLevelRegularRecords).count());
			resultsOutputWriter.format("Total scaffolds:\t%d\n",gffData.scaffolds.size());
			resultsOutputWriter.format("Total proteins:\t%d\n",proteins.size());
			final Set<String> uniqueProteinsWithDomains = new HashSet<>();
			final Set<PfamDomain> uniqueDomains = new HashSet<>();
			domains.forEach((protein,proteinDomains)->{
				if (proteins.containsKey(protein)) {
					uniqueProteinsWithDomains.add(protein);
					uniqueDomains.addAll(proteinDomains);
				}
			});
			resultsOutputWriter.format("Total proteins with domains:\t%d\n",uniqueProteinsWithDomains.size());
			resultsOutputWriter.format("Total unique domains:\t%d\n",uniqueDomains.size());

			resultsOutputWriter.format("Protein lengths distribution factor:\t%.4f\n",proteinLengthsDistributionFactor);
			resultsOutputWriter.format("Incomplete genes factor:\t%.4f\n",incompleteGenesFactor);
			resultsOutputWriter.format("Transposable elements factor:\t%.4f\n",transposableElementsFactor);
			resultsOutputWriter.format("Isoforms factor:\t%.4f\n",isoformsFactor);
			resultsOutputWriter.format("BUSCO duplicated factor:\t%.4f\n",buscoDuplicatedFactor);
			resultsOutputWriter.format("BUSCO complete factor:\t%.4f\n",buscoCompleteFactor);
			resultsOutputWriter.format("ABAQS:\t%.4f\n",abaqsScore);
		}
		LoggerHelper.log(logger, Level.INFO, "All done");
	}


	private static final Path GENE_CODE_LOCATION = Path.of("ncbi/gc.prt");
	private GeneCode loadGeneCode(Logger logger, int geneCode, Path geneCodeFile) throws SocketException, IOException {
		if (geneCodeFile!=null) {
			try (InputStream in = FilesHelper.newInputStreamOptionallyGzipped(geneCodeFile)) {
				return GeneCodeHelper.loadAndParseGeneCodes(logger, in).get(geneCode);
			}
		} else {
			try (InputStream in = IoHelper.openResourceStream(GENE_CODE_LOCATION)) {
				return GeneCodeHelper.loadAndParseGeneCodes(logger, in).get(geneCode);
			}
		}
	}

	private static final double limits(double d, double min, double max) {
		if (d<min) {
			return min;
		} else if (d>max) {
			return max;
		} else {
			return d;
		}
	}

	private static final class DistributionComparisonPair {
		private double organismValue=0.0;
		private double referenceValue=0.0;
	}

	private static final Path COMPUTATIONS_VERBOSE_FILE = Path.of("computations.tsv");
	private double computeProteinLengthDistributionFactor(Logger logger, Map<Integer, Double> organismProteinLengthDitribution, Map<Integer, Double> referenceProteinLengthDitribution, boolean verbose, Path verboseOutputFolder) throws IOException {
		final Map<Integer,DistributionComparisonPair> comparisonPairs = new HashMap<>();
		organismProteinLengthDitribution.forEach((length,value)->comparisonPairs.computeIfAbsent(length, k->new DistributionComparisonPair()).organismValue=value);
		referenceProteinLengthDitribution.forEach((length,value)->comparisonPairs.computeIfAbsent(length, k->new DistributionComparisonPair()).referenceValue=value);
		final MutableDouble sumDiff = new MutableDouble();
		final MutableDouble sumBase = new MutableDouble();
		if (verbose || verboseOutputFolder!=null) {
			Path verboseOutput = verboseOutputFolder!=null?verboseOutputFolder.resolve(COMPUTATIONS_VERBOSE_FILE):null;
			try (PrintWriter out = new PrintWriter(FilesHelper.newPrintWriterOrStdOutput(verboseOutput))) {
				out.format("%s\t%s\t%s\t%s\t%s\n", "length","referenceValue","organismValue","diff","base");
				comparisonPairs.forEach((length,pair)->{
					if (length>0) { // Ignore zero length
						double diff = (((double)length) + 0.5) * (pair.referenceValue - pair.organismValue);
						double base = (((double)length) + 0.5) * pair.referenceValue;
						sumDiff.add(diff);
						sumBase.add(base);
						out.format("%d\t%.12f\t%.12f\t%.12f\t%.12f\n", length,pair.referenceValue,pair.organismValue,diff,base);
					}
				});
				out.format("\n\nTotal diff:\t%.12f\n", sumDiff.doubleValue());
				out.format("Total base:\t%.12f\n", sumBase.doubleValue());
				out.format("Raw PLD factor:\t%.12f\n", 1.0 - sumDiff.doubleValue() / sumBase.doubleValue());
			};
		} else {
			comparisonPairs.forEach((length,pair)->{
				if (length>0) { // Ignore zero length
					double diff = (((double)length) + 0.5) * (pair.referenceValue - pair.organismValue);
					double base = (((double)length) + 0.5) * pair.referenceValue;
					sumDiff.add(diff);
					sumBase.add(base);
				}
			});
		}

		return limits(1.0 - sumDiff.doubleValue() / sumBase.doubleValue(),0.0,1.0);
	}

	private static final Path BINS_VERBOSE_FILE = Path.of("bins.tsv");
	private Map<Integer, Double> loadOrganismProteinLengthDistribution(Logger logger, Collection<SimpleFastaSequenceWithIdAndExtra> proteins, int binSize, boolean verbose, Path verboseOutputFolder) throws IOException {
		BinnedCollection<SimpleFastaSequenceWithIdAndExtra> binnedRecords[] = CollectionsHelper.binCollectionWithFixedBinSize(proteins, r->r.sequence.length(), binSize, 0.0, Double.NaN);
		final double totalCount = (double) proteins.size();
		Map<Integer,Double> ret = new HashMap<>(binnedRecords.length);
		if (verbose || verboseOutputFolder!=null) {
			Path verboseOutput = verboseOutputFolder!=null?verboseOutputFolder.resolve(BINS_VERBOSE_FILE):null;
			try (PrintWriter out = new PrintWriter(FilesHelper.newPrintWriterOrStdOutput(verboseOutput))) {
				out.format("%s\t%s\t%s\t%s\t%s\t%s\n", "bin","start","end","count","nosmalized count","list");
				for (int index=0;index<binnedRecords.length;index++) {
					double normilizedValue = ((double)binnedRecords[index].entries.size())/totalCount; 
					ret.put(index, normilizedValue);
					out.format("%d\t%f\t%f\t%d\t%.12f\t%s\n", index,binnedRecords[index].fromBinValue,binnedRecords[index].toBinValue,binnedRecords[index].entries.size(),normilizedValue,binnedRecords[index].entries.stream().map(s->String.format("%s:%d", s.id, s.sequence.length())).collect(Collectors.joining(",")));
				}
			}
		} else {
			for (int index=0;index<binnedRecords.length;index++) {
				double normilizedValue = ((double)binnedRecords[index].entries.size())/totalCount; 
				ret.put(index, normilizedValue);
			}
		}
		LoggerHelper.log(logger, Level.INFO, "Binned %,d proteins to %,d bins",proteins.size(),ret.size());
		return ret;
	}

	private static final Pattern PROTEIN_LENGTH_RECORD = Pattern.compile("(\\d+)\\W+([\\d\\.\\-+e]+)");
	private static final Path REFERENCE_PROTEIN_LENGTH_RESOURCE = Path.of("abaqs/reference-proteins-length-distribution.tsv.gz");
	private Map<Integer, Double> loadReferenceProteinLengthDistribution(Logger logger, Path referenceProteinLengthDistributionFile) throws IOException {
		final Map<Integer,Double> distribution = new HashMap<>();
		try (BufferedReader reader=new BufferedReader(new InputStreamReader(openFileOrStream(logger, "reference protein lengths", referenceProteinLengthDistributionFile, REFERENCE_PROTEIN_LENGTH_RESOURCE)))) {
			reader.lines().forEach(line->{
				Matcher matcher = PROTEIN_LENGTH_RECORD.matcher(line);
				if (matcher.matches()) {
					Integer length = Integer.valueOf(matcher.group(1));
					Double value = Double.valueOf(matcher.group(2));
					distribution.put(length,value);
				}
			});
		}
		final MutableDouble totalSum = new MutableDouble();
		// Normalize distribution to total sum = 1.0
		distribution.forEach((length,value)->totalSum.add(value));
		final Map<Integer,Double> ret = new HashMap<>(distribution);
		distribution.forEach((length,value)->ret.put(length, value/totalSum.doubleValue()));
		LoggerHelper.log(logger, Level.INFO, "Loaded total %,d record of the reference distribution",ret.size());
		return ret;

	}

	private double computeTransposableElementsFactor(Logger logger, Map<String, GeneRecord> geneRecords, Set<PfamDomain> transposableElements, Set<PfamDomain> suspectedTransposableElements) {
		final MutableInt teCount = new MutableInt();
		geneRecords.forEach((id,record)->{
			if (record.detectedTtransposableElement) {
				teCount.increment();
			}
		});
		LoggerHelper.log(logger, Level.INFO, "Found %,d TE or suspected TE",teCount.intValue());
		return teCount.doubleValue() / (double) geneRecords.size();
	}

	/*
	Calculate the proportion of TE t by (# of TE gene models/total # of gene models)
	TEs are identified from the Interpro data file as
	a. Has any domain from the known TE list, regardless of other domain content.
	b. Any model without domains that has CDS masking > 20%. The location of masking is obtained from the softmasked assembly file.
	c. If suspicious domain alone, consider as TE if CDS masking > 90%
	 */
	private boolean isTransposableElement(Set<PfamDomain> domains, double portionOfCDSMasked, Set<PfamDomain> transposableElements, Set<PfamDomain> suspectedTransposableElements, double noDomainCDSMaskedCutoff, double suspectedDomainCDSMaskedCutoff) {
		// a. Has any domain from the known TE list, regardless of other domain content.
		if (domains!=null && domains.stream().filter(transposableElements::contains).count()>0) {
			return true;
		};
		// b. Any model without domains that has CDS masking > 20%. The location of masking is obtained from the softmasked assembly file.
		//		if (CollectionsHelper.isNullOrEmpty(domains) && (Double.isNaN(noDomainCDSMaskedCutoff) || portionOfCDSMasked>noDomainCDSMaskedCutoff)) {
		//			return true;
		//		};
		// c. If suspicious domain alone, consider as TE if CDS masking > 90%
		if (domains!=null && domains.stream().filter(suspectedTransposableElements::contains).count()>0 && (Double.isNaN(suspectedDomainCDSMaskedCutoff) || portionOfCDSMasked>suspectedDomainCDSMaskedCutoff)) {
			return true;
		};
		return false;
	}

	private static final Pattern PFAM_PATTERN = Pattern.compile("pf\\d+", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
	private Set<PfamDomain> loadTEDomains(Logger logger, String prefix, Path elementsFile, Path resourceLocation) throws IOException {
		Set<PfamDomain> ret = new HashSet<>();
		try (BufferedReader reader=new BufferedReader(new InputStreamReader(openFileOrStream(logger, prefix, elementsFile, resourceLocation)))) {
			reader.lines().forEach(line->{
				if (!PatternHelper.COMMENT_LINE.matcher(line).matches()) {
					Matcher matcher = PFAM_PATTERN.matcher(line);
					while (matcher.find()) {
						ret.add(PfamDomain.of(matcher.group(0)));
					}
				}
			});
		}
		LoggerHelper.log(logger, Level.INFO, "Loaded total %,d of %s",ret.size(),prefix);
		return ret;
	}

	private InputStream openFileOrStream(Logger logger, String prefix, Path file, Path resource) throws IOException {
		if (file!=null) {
			LoggerHelper.log(logger, Level.INFO, "Using %s file: '%s'",prefix,file);
			return FilesHelper.newInputStreamOptionallyGzipped(file);
		} else {
			LoggerHelper.log(logger, Level.INFO, "Using internal %s",prefix);
			if (FilesHelper.isGzip(resource)) {
				return new GZIPInputStream(IoHelper.openResourceStream(resource));
			} else {
				return IoHelper.openResourceStream(resource);
			}
		}
	}

	private double computeIncompleteGenesFactor(Logger logger, Map<String, GeneRecord> geneRecords) {
		final MutableInt incompleteGenesCount = new MutableInt();

		geneRecords.forEach((id,record)->{
			String proteinSeq = record.protein.sequence;
			if (proteinSeq.length()>0) {
				if (proteinSeq.charAt(0)!='M' || proteinSeq.charAt(proteinSeq.length()-1)!='*') {
					incompleteGenesCount.increment();
				}
			}
		});
		LoggerHelper.log(logger, Level.INFO, "Found %,d incomplete genes",incompleteGenesCount.intValue());
		return 1.0 - incompleteGenesCount.doubleValue() / (double) geneRecords.size();
	}

	private BuscoData loadBuscoData(Logger logger, String buscoDataString, Path buscoDataFile) throws IOException {
		if (!TextHelper.isNullOrEmpty(buscoDataString)) {
			LoggerHelper.log(logger, Level.INFO, "Using provided BUSCO summary:'%s'",buscoDataString);
			return BuscoData.of(buscoDataString);
		} else if (buscoDataFile!=null) {
			LoggerHelper.log(logger, Level.INFO, "Loading BUSCO from file:'%s'",buscoDataFile);
			return BuscoData.ofFile(buscoDataFile);
		} else {
			LoggerHelper.log(logger, Level.INFO, "No BUSCO data present");
			return null;
		}
	}

	private double computeIsoformsFactor(Logger logger, GFF3Data gff3Data, Map<String,GeneRecord> geneRecords, double isoformsMinimumOverlap) {
		FeatureTrack track = FeatureTrack.createTrackFromGff3Data(gff3Data, new TrackNameAndType(null, "gff3Data", FeatureTrackType.model), null, null);
		TrackToTrackMapping selfMapping = FeatureTrack.mapTwoTracksByPositionOverlap(logger,track,track, (p1,p2)->FeaturePair.compareByOverlapDesc(p1,p2));
		Set<String> gff3IsoformsmRNAIds = new HashSet<>();
		final MutableInt isoformsCount = new MutableInt();
		final MutableInt overlappedCount = new MutableInt();
		gff3Data.getRecordsByPredicate(r->Gff3Type.gene.equals(r.type)).forEach(gene->{
			List<Gff3Record> isoformsmRNA = gene.getAllByPredicateInclusive(r->Gff3Type.mRNA.equals(r.type));
			if (isoformsmRNA.size()>1) {
				isoformsCount.increment();
				isoformsmRNA.forEach(mRNA->gff3IsoformsmRNAIds.add(mRNA.id));
			}
		});
		LoggerHelper.log(logger, Level.INFO, "Found %,d GFF3 isoforms",isoformsCount.intValue());
		selfMapping.mappedPairs.forEach(pair->{
			if (pair.overlap>isoformsMinimumOverlap) {
				String mRNAIdA = pair.a.name;
				String mRNAIdB = pair.b.name;
				if (!gff3IsoformsmRNAIds.contains(mRNAIdA) || !gff3IsoformsmRNAIds.contains(mRNAIdB)) {
					overlappedCount.increment();
					gff3IsoformsmRNAIds.add(mRNAIdA);
					gff3IsoformsmRNAIds.add(mRNAIdB);
				}
			}
		});
		LoggerHelper.log(logger, Level.INFO, "Found %,d genes overlapped by > %.2f%%",overlappedCount.intValue(),isoformsMinimumOverlap*100.0);
		double ret = (isoformsCount.doubleValue()+overlappedCount.doubleValue())/ (double)geneRecords.size();
		return ret;
	}

	private Map<String,GeneRecord> createGeneRecords(Logger logger, GFF3Data gffData, MaskerFunction maskerFunction,  Map<String, SimpleFastaSequenceWithIdAndExtra> effectiveProteins, Map<String, Set<PfamDomain>> domains, Gff3RecordIdMapper gff3ProteinIdMapper, Set<PfamDomain> transposableElements, Set<PfamDomain> suspectedTransposableElements, double noDomainCDSMaskedCutoff, double suspectedDomainCDSMaskedCutoff) {
		Map<String,GeneRecord> ret = new HashMap<>();

		gffData.records.forEach(geneRecord->{
			List<Gff3Record> mRNAs = geneRecord.getAllByPredicateInclusive(r->Gff3Type.mRNA.equals(r.type));
			mRNAs.stream().forEach(mRNA->{
				MutableInt cdsStart = new MutableInt(Integer.MAX_VALUE);
				MutableInt cdsEnd = new MutableInt(Integer.MIN_VALUE);
				geneRecord.getAllByPredicateInclusive(r->Gff3Type.CDS.equals(r.type)).forEach(cds->{
					if (cds.start<cdsStart.intValue()) {
						cdsStart.setValue(cds.start);
					}
					if (cds.end>cdsEnd.intValue()) {
						cdsEnd.setValue(cds.end);
					}
				});
				String proteinId = gff3ProteinIdMapper.apply(mRNA);
				double scaffoldCDSMasked = computeScaffoldMaskedFactor(gffData.scaffolds.get(mRNA.seqid),mRNA, maskerFunction);
				boolean detectedTtransposableElement = isTransposableElement(domains.get(proteinId), scaffoldCDSMasked, transposableElements, suspectedTransposableElements, noDomainCDSMaskedCutoff, suspectedDomainCDSMaskedCutoff);
				GeneRecord record = new GeneRecord(mRNA, mRNAs.size(), cdsStart.intValue(), cdsEnd.intValue(), effectiveProteins.get(proteinId), domains.get(proteinId), scaffoldCDSMasked, detectedTtransposableElement);
				ret.put(mRNA.id,record);
			});
		});
		return ret;
	}

	private double computeScaffoldMaskedFactor(SimpleFastaSequenceWithId simpleFastaSequenceWithId, Gff3Record mRNA, MaskerFunction maskerFunction) {
		final MutableInt totalCount = new MutableInt();
		final MutableInt maskedCount = new MutableInt();
		mRNA.getAllByPredicateInclusive(r->Gff3Type.CDS.equals(r.type)).forEach(cds->{
			for (int pos=cds.start; pos<=cds.end; pos++) {
				totalCount.increment();
				if (maskerFunction.isMasked(simpleFastaSequenceWithId.sequence.charAt(pos-1))) {
					maskedCount.increment();
				}
			}
		});
		return maskedCount.doubleValue() / totalCount.doubleValue();
	}

	@SuppressWarnings("unused")
	private void validateProteinTranslation(Logger logger, Map<String, SimpleFastaSequenceWithIdAndExtra> loadedProteins, Map<String, SimpleFastaSequenceWithIdAndExtra> translatedProteins) {

		SetsCompareResult<String> compareResult = CollectionsHelper.compareTwoSets(loadedProteins.keySet(), translatedProteins.keySet());
		if (compareResult.uniqueToA.size()>0) {
			LoggerHelper.log(logger,Level.WARNING,"Found %,d protein ids missing in translated from gff file",compareResult.uniqueToA.size());
			compareResult.uniqueToA.stream().sorted().forEach(id->{
				SimpleFastaSequenceWithIdAndExtra loaded = loadedProteins.get(id);
				LoggerHelper.log(logger,Level.WARNING,"Loaded id:'%s'",id);
				LoggerHelper.log(logger,Level.WARNING,"Loaded:'%s'",loaded.sequence);
			});
		} else {
			LoggerHelper.log(logger,Level.INFO,"No protein ids missing in translated from gff file");
		}
		if (compareResult.uniqueToB.size()>0) {
			LoggerHelper.log(logger,Level.WARNING,"Found %,d protein ids missing in loaded fasta file",compareResult.uniqueToB.size());
			compareResult.uniqueToB.stream().sorted().forEach(id->{
				SimpleFastaSequenceWithIdAndExtra translated = translatedProteins.get(id);
				LoggerHelper.log(logger,Level.WARNING,"Translated id:'%s'",id);
				LoggerHelper.log(logger,Level.WARNING,"Translated:'%s'",translated.sequence);
			});
		} else {
			LoggerHelper.log(logger,Level.INFO,"No protein ids missing in loaded fasta file");
		}
		if (compareResult.common.size()>0) {
			LoggerHelper.log(logger,Level.INFO,"Found %,d matched protein ids between loaded from fasta and translated from gff file",compareResult.common.size());
			compareResult.common.stream().sorted().forEach(id->{
				SimpleFastaSequenceWithIdAndExtra loaded = loadedProteins.get(id);
				SimpleFastaSequenceWithIdAndExtra translated = translatedProteins.get(id);
				if (!loaded.sequence.equals(translated.sequence)) {
					LoggerHelper.log(logger,Level.WARNING,"Loaded protein sequence do not match translated from GFF data for protein id:'%s' gff id:'%s'",id,translated.extra);
					LoggerHelper.log(logger,Level.WARNING,"    Loaded:'%s'",loaded.sequence);
					LoggerHelper.log(logger,Level.WARNING,"Translated:'%s'",translated.sequence);
				}
			});
		} else {
			LoggerHelper.log(logger,Level.WARNING,"No matched protein ids between loaded from fasta and translated from gff file");
		}
	}

	private Map<String, Set<PfamDomain>> loadDomainsWithIdMapper(Logger logger, Path inputDomains, Pattern domainsProteinMapper, boolean verbose) throws IOException {
		Map<String, Set<PfamDomain>> ret = new HashMap<>();
		final MutableInt totalSkippedLines = new MutableInt();
		final MutableInt totalAddedDomains = new MutableInt();
		if (inputDomains!=null) {
			LoggerHelper.log(logger, Level.INFO, "Loading domains from input file:'%s'",inputDomains);
			try (BufferedReader reader = FilesHelper.newBufferedReaderOptionallyGzipped(inputDomains)) {
				reader.lines().forEach(line->{
					Matcher m = domainsProteinMapper.matcher(line);
					if (m.matches()) {
						String proteinId = m.group("id");
						String pfam = m.group("domain");
						ret.computeIfAbsent(proteinId, id->new HashSet<>()).add(PfamDomain.of(pfam));
						totalAddedDomains.increment();
						if (verbose) {
							LoggerHelper.log(logger, Level.INFO, "Added domain '%s' to protein '%s'",pfam,proteinId);
						}
					} else {
						totalSkippedLines.increment();
						if (verbose) {
							LoggerHelper.log(logger, Level.WARNING, "Skipped line in domains file:'%s'",line);
						}
					}
				});
			}
		} else {
			LoggerHelper.log(logger, Level.WARNING, "No domains file specified");
		}
		LoggerHelper.log(logger, Level.INFO, "Total added %,d domains to %,d unique proteins",totalAddedDomains.intValue(), ret.size());
		if (totalSkippedLines.intValue()>0) {
			LoggerHelper.log(logger, Level.WARNING, "Total skipped %,d lines in domains file",totalSkippedLines.intValue());
		}
		return ret;
	}

	private Map<String, SimpleFastaSequenceWithIdAndExtra> translateProteinsFastaWithIdMapper(Logger logger, GFF3Data gffData, Gff3RecordIdMapper gff3ProteinIdMapper, GeneCode geneCode, boolean verbose) {
		LoggerHelper.log(logger, Level.INFO,"Translating proteins from gff3 data using gcode '%s(%d)'",geneCode.names[0],geneCode.id);
		Map<String, SimpleFastaSequenceWithIdAndExtra> ret = new HashMap<>();
		gffData.getRecordsByPredicate(r->Gff3Type.mRNA.equals(r.type)).forEach(rec->{
			SimpleFastaSequenceWithId scaffold = gffData.scaffolds.get(rec.seqid);
			TranslationResult aminoacidSequence = rec.getTranslatedAminoacidSequence(scaffold.sequence,geneCode);
			String proteinId = gff3ProteinIdMapper.apply(rec);
			if (verbose && aminoacidSequence.hasOverhang()) {
				LoggerHelper.log(logger, Level.WARNING,"GFF record '%s' have overhang after translation:'%s'",rec.id,aminoacidSequence.overhang);
			}
			ret.put(proteinId, new SimpleFastaSequenceWithIdAndExtra(proteinId, rec.id, aminoacidSequence.output, SequenceType.aminoacid));
		});
		return ret;
	}

	private Map<String, SimpleFastaSequenceWithIdAndExtra> loadProteinsFastaWithIdMapper(Logger logger, Path inputProteinsFasta, FastaRecordIdMapper proteinFastaProteinIdMapper) throws IOException {
		LoggerHelper.log(logger, Level.INFO, "Loading proteins fasta from file:'%s'",inputProteinsFasta);
		Map<String, SimpleFastaSequenceWithIdAndExtra> ret = new HashMap<>();
		try (FastaFile in=new FastaFile(FilesHelper.newBufferedReaderOptionallyGzipped(inputProteinsFasta))) {
			while(in.hasMoreScaffolds()) {
				Scaffold s = in.getNextScaffold();
				SimpleFastaSequenceWithIdAndExtra protein = new SimpleFastaSequenceWithIdAndExtra(s.name, s.extra, s.getSequence(), SequenceType.aminoacid);
				String mappedId = proteinFastaProteinIdMapper.apply(protein);
				ret.put(mappedId, protein);
			}
		}
		LoggerHelper.log(logger, Level.INFO, "Loading proteins fasta completed, total %,d proteins loaded",ret.size());
		return ret;
	}

	private Map<String, SimpleFastaSequenceWithId> loadScaffoldsFasta(Logger logger, Path inputScaffoldsFasta) throws IOException {
		Map<String, SimpleFastaSequenceWithId> ret = new HashMap<>();
		LoggerHelper.log(logger, Level.INFO, "Loading scaffolds fasta from file:'%s'",inputScaffoldsFasta);
		try (FastaFile in=new FastaFile(FilesHelper.newBufferedReaderOptionallyGzipped(inputScaffoldsFasta))) {
			while(in.hasMoreScaffolds()) {
				Scaffold s = in.getNextScaffold();
				ret.put(s.name, new SimpleFastaSequenceWithId(s.name, s.getSequence(), SequenceType.nucleotide));
			}
		}
		LoggerHelper.log(logger, Level.INFO, "Loading scaffolds fasta completed, total %,d scaffolds loaded",ret.size());
		return ret;
	}

	private static void showJava(Logger logger) {
		String runtimeName = System.getProperty("java.runtime.name","N/S");
		String runtimeVersion = System.getProperty("java.runtime.version","N/S");
		String vmName = System.getProperty("java.vm.name","N/S");
		String vmVersion = System.getProperty("java.version","N/S");
		String vmInfo = System.getProperty("java.vm.info","N/S");
		String javaVendor = System.getProperty("java.vendor","N/S");
		LoggerHelper.log(logger, Level.INFO,"Java Runtime: %s %s", runtimeName, runtimeVersion);
		LoggerHelper.log(logger, Level.INFO,"Java VM: %s %s %s", vmName, vmVersion, vmInfo);
		LoggerHelper.log(logger, Level.INFO,"Java Vendor: %s", javaVendor);
		long maxMemory = Runtime.getRuntime().maxMemory();
		if (Long.MAX_VALUE>=maxMemory) {
			LoggerHelper.log(logger, Level.INFO,"Java Memory: %,dM",maxMemory/(1024*1024));
		} else {
			LoggerHelper.log(logger, Level.INFO,"Java Memory: UNLIMITED");
		}
	}
}
