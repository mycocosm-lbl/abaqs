package org.mycocosm.gff3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.mycocosm.framework.collections.CollectionsHelper;
import org.mycocosm.framework.fasta.FastaFile;
import org.mycocosm.framework.fasta.Scaffold;
import org.mycocosm.framework.fasta.SequenceType;
import org.mycocosm.framework.fasta.SimpleFastaSequenceWithId;

public class GFF3Data {
	public final List<Gff3Record> records;
	public final Map<String,SimpleFastaSequenceWithId> scaffolds;

	private GFF3Data(List<Gff3Record> records, Map<String,SimpleFastaSequenceWithId> scaffolds) {
		this.records = records;
		this.scaffolds = scaffolds;
	}

	public static final GFF3Data parseGff(Logger logger, BufferedReader reader) throws IOException {
		return parse(logger,reader,false,GffParserSupport.gff);
	}
	public static final GFF3Data parseGtf(Logger logger, BufferedReader reader) throws IOException {
		return parse(logger,reader,false,GffParserSupport.gtf);
	}

	public static final GFF3Data parse(Logger logger, BufferedReader reader, boolean acceptMissingId, GffParserSupport gffParserSupport) throws IOException {
		Map<String,Gff3Record> allRecordsByKey = new HashMap<>();
		List<Gff3Record> allRecords = new ArrayList<>();
		Map<String,SimpleFastaSequenceWithId> scaffolds = null;

		// Parsing the input file
		String line = reader.readLine();
		while (line!=null) {
			Gff3Record record = Gff3Record.fromLine(line,acceptMissingId, gffParserSupport);
			if (record!=null) {
				allRecords.add(record);
				String key = gffParserSupport.toKey(record);
				if (key!=null) {
					allRecordsByKey.put(key, record);
				}
				if (record.catergory.equals(Gff3RecordCategory.fasta)) {
					scaffolds = loadFastaFromReader(reader);
				}
			}
			line = reader.readLine();
		}

		// Now set all parents
		List<Gff3Record> ret = new ArrayList<>();
		allRecords.forEach(rec->{
			String parentKey = gffParserSupport.toParentKey(rec);
			if (parentKey!=null) {
				Gff3Record parent = allRecordsByKey.get(parentKey);
				if (parent!=null && !rec.equals(parent)) {
					Gff3Record childRec = rec.setParent(parent);
					String childRecKey = gffParserSupport.toKey(childRec);
					allRecordsByKey.put(childRecKey, childRec); // No need to add it to the results as it will be added together with parent record
				} else {
					ret.add(rec);
				}
			} else {
				ret.add(rec);
			}
		});
		return new GFF3Data(ret, scaffolds);
	}

	// Load fasta from reader current position
	private static Map<String,SimpleFastaSequenceWithId> loadFastaFromReader(BufferedReader reader) throws IOException {
		@SuppressWarnings("resource") // closed upstream
		FastaFile in = new FastaFile(reader);
		Map<String,SimpleFastaSequenceWithId> ret = new HashMap<>();
		while (in.hasMoreScaffolds()) {
			Scaffold s = in.getNextScaffold();
			ret.put(s.name, new SimpleFastaSequenceWithId(s.name, s.getSequence(), SequenceType.nucleotide));
		}
		return ret;
	};

	public void printAll(PrintWriter writer, int fastaWidths, GffParserSupport gffParserSupport) {
		records.stream().filter(gffParserSupport::isAcceptedForOutput).forEach(rec->{
			rec.print(writer, gffParserSupport);
			if (Gff3RecordCategory.fasta.equals(rec.catergory)) {
				scaffolds.forEach((id,scaffold)->{
					writer.print(scaffold.formatAsFastaWithId(fastaWidths));
				});
			}
		});
	}

	public boolean hasScaffolds() {
		return !CollectionsHelper.isNullOrEmpty(scaffolds);
	}

	public List<Gff3Record> getRecordsByPredicate(Predicate<Gff3Record> predicate) {
		final List<Gff3Record> ret = new ArrayList<>();
		records.forEach(rec->{
			ret.addAll(rec.getAllByPredicateInclusive(predicate));
		});		
		return ret;
	}
	
	public GFF3Data replaceScaffolds (Map<String,SimpleFastaSequenceWithId> scaffoldsReplacement) {
		GFF3Data ret = new GFF3Data(this.records, scaffoldsReplacement);
		if (scaffoldsReplacement!=null) {
			List<Gff3Record> fastaRecord = ret.getRecordsByPredicate(r->r.catergory.equals(Gff3RecordCategory.fasta));
			if (fastaRecord.isEmpty()) {
				ret.records.add(Gff3Record.fasta());
			}
		} else {
			List<Gff3Record> fastaRecord = ret.getRecordsByPredicate(r->r.catergory.equals(Gff3RecordCategory.fasta));
			fastaRecord.forEach(rec->ret.records.remove(rec));
		}
		return ret;
	}

	public GFF3Data cloneWithRecordPredicate(Gff3RecordFilter filter) {
		List<Gff3Record> newRecords = new ArrayList<>();
		Map<String,SimpleFastaSequenceWithId> newScaffolds;
		records.forEach(rec->{
			Gff3RecordFilteringResult filterResult = filter.test(rec);
			switch (filterResult) {
			case accepted:
				newRecords.add(rec.cloneWithFilter(null,filter));
				break;
			case acceptedIfNotEmpty:
				Gff3Record newRec = rec.cloneWithFilter(null,filter);
				if (!newRec.children.isEmpty()) {
					newRecords.add(newRec);
				}
				break;
			case rejected: break; 
			}
		});
		if (!CollectionsHelper.isNullOrEmpty(scaffolds)) {
			Set<String> acceptedScaffoldNames = new HashSet<>();
			newRecords.forEach(rec->{
				if (rec.seqid!=null) {
					acceptedScaffoldNames.add(rec.seqid);
				}
			});
			newScaffolds = new HashMap<>();
			scaffolds.forEach((name,scaffold)->{
				if (acceptedScaffoldNames.contains(name)) {
					newScaffolds.put(name, scaffold);
				}
			});
		} else {
			newScaffolds = null;
		}
		return new GFF3Data(newRecords, newScaffolds);
	}
}
