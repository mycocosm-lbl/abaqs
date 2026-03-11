package org.mycocosm.gff3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

	public static final GFF3Data parse(Logger logger, BufferedReader reader) throws IOException {
		return parse(logger,reader, r->r.id, r->r.attributes.get(Gff3Record.ATTRIBUTE_PARENT),false);
	}

	public static final GFF3Data parse(Logger logger, BufferedReader reader, Function<Gff3Record,String> toKey, Function<Gff3Record,String> toParentKey, boolean acceptMissingId) throws IOException {
		Map<String,Gff3Record> allRecordsByKey = new HashMap<>();
		List<Gff3Record> allRecords = new ArrayList<>();
		Map<String,SimpleFastaSequenceWithId> scaffolds = null;

		// Parsing the input file
		String line = reader.readLine();
		while (line!=null) {
			Gff3Record record = Gff3Record.fromLine(line,acceptMissingId);
			if (record!=null) {
				allRecords.add(record);
				String key = toKey.apply(record);
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
			String parentKey = toParentKey.apply(rec);
			if (parentKey!=null) {
				Gff3Record parent = allRecordsByKey.get(parentKey);
				if (parent!=null && !rec.equals(parent)) {
					Gff3Record childRec = rec.setParent(parent);
					String childRecKey = toKey.apply(childRec);
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

	public void printAll(PrintWriter writer, int fastaWidths) {
		records.forEach(rec->{
			rec.print(writer);
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
		return new GFF3Data(this.records, scaffoldsReplacement);
	}
}
