package org.mycocosm.abaqs.main;

import java.util.List;
import java.util.function.Function;

import org.mycocosm.framework.fasta.SimpleFastaSequenceWithIdAndExtra;
import org.mycocosm.framework.text.PatternTransformer;
import org.mycocosm.framework.text.TextHelper;
import org.mycocosm.framework.utils.ExceptionsHelper;
import org.mycocosm.gff3.Gff3Record;

public class MapperDefinitionsFactory {

	public static final class Gff3RecordIdMapper implements Function<Gff3Record,String> {
		private final String column;
		private final String attributeName;
		private final PatternTransformer valueMapper;

		protected Gff3RecordIdMapper(String def) {
			List<String> split = TextHelper.parseDelimitedLine(def, ':');
			// "0   :1        :2        "
			// "attribute:proteinId:.*->{0}";
			valueMapper = split.size()>=3?PatternTransformer.of(split.get(2)):PatternTransformer.SAME;
			attributeName = split.size()>=2?split.get(1):"proteinId";
			column = split.size()>=1?split.get(0):"attributes";
			switch (column) {
			case "seqid":
			case "source":
			case "attributes":
				break;
			default:
				throw ExceptionsHelper.newRuntimeException("GFF3 column is invalid:'%s'", split.get(1));
			}
		}

		@Override
		public String apply(Gff3Record rec) {
			String transformerInput;
			if (rec!=null) {
				switch (column) {
				case "seqid":
					transformerInput = rec.seqid;
					break;
				case "source":
					transformerInput = rec.source;
					break;
				case "attributes":
					transformerInput = rec.attributes.get(attributeName);
					break;
				default:
					throw ExceptionsHelper.newRuntimeException("GFF3 column is invalid:'%s'", column);
				}
				if (!TextHelper.isNullOrEmpty(transformerInput)) {
					return valueMapper.transform(transformerInput);
				} else {
					return transformerInput;
				}
			} else {
				return null;
			}
		}
	}

	public static final class FastaRecordIdMapper implements Function<SimpleFastaSequenceWithIdAndExtra,String> {
		private final PatternTransformer valueMapper;

		protected FastaRecordIdMapper(String def) {
			valueMapper = PatternTransformer.of(def);
		}

		@Override
		public String apply(SimpleFastaSequenceWithIdAndExtra fasta) {
			String transformerInput = fasta.id+" "+fasta.extra;
			return valueMapper.transform(transformerInput);
		}
	}

}
