package org.mycocosm.framework.fasta;

import org.mycocosm.framework.text.TextHelper;
import org.mycocosm.sequence.SequenceHelper;
import org.mycocosm.sequence.SequenceValidateOption;
import org.mycocosm.sequence.SequenceValidationCode;
import org.mycocosm.sequence.SequenceValidationResponse;


public class SimpleFastaSequenceWithId extends AbstractFastaSequence {

	public final String id;
	public final SequenceType type;

	public SimpleFastaSequenceWithId(String id, String sequence, SequenceType type) {
		super(sequence);
		this.id = id;
		this.type = type;
	}

	@Override
	public SequenceKey<?> key() {
		return null;
	}

	@Override
	public String getDefaultIdString() {
		return id;
	}

	@Override
	public SequenceValidationResponse isValid(SequenceValidateOption option) {
		SequenceValidationResponse ret = new SequenceValidationResponse();
		if (TextHelper.isNullOrEmpty(id)) {
			ret.addError(SequenceValidationCode.emptyPortalId, "Fasta sequence id is null or empty");
		}
		return ret.merge(validateSequence(option));
	}
	
	private SequenceValidationResponse validateSequence(SequenceValidateOption option) {
		switch (type) {
		case aminoacid: return SequenceHelper.validateAminoacidSequence(sequence,option);
		case nucleotide: return SequenceHelper.validateNucleotideSequence(sequence,option);
		default: return new SequenceValidationResponse(SequenceValidationCode.genericError, "Illegal sequence type");
		}
	}
	
	public static final FastaIdCreator ID_CREATOR = new FastaIdCreator() {
		
		@Override
		public String getId(AbstractFastaSequence sequence) {
			return ((SimpleFastaSequenceWithId)sequence).id;
		}
	};
	
}
