package org.mycocosm.sequence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mycocosm.framework.text.TextHelper;

public enum SequenceContent {
	nucleotide {
		@Override
		public SequenceValidationResponse validateSequence(String sequence, SequenceValidateOption option) {
			if (sequence==null) {
				throw new NullPointerException("Sequence parameter cannot be null");
			}
			if (option.needValidation()) {
				if (sequence.length()==0) {
					return new SequenceValidationResponse(SequenceValidationCode.emptySequence, "Empty sequence");
				} else {
					SequenceValidationResponse ret = new SequenceValidationResponse();
					Pattern validationPattern = SequenceHelper.NUCLEOTYDE_SEQUENCE_VALID; 
					Matcher matcher = validationPattern.matcher(sequence);
					if (matcher.find()) {
						int start = matcher.start();
						int end = matcher.end();
						if (start>0 || end<sequence.length()) {
							if (start>0) {
								ret.addError(SequenceValidationCode.illegalChraracter, String.format("Illegal %s sequence: >>%s<<, not matching %s",name(),TextHelper.toHexDump(TextHelper.safeSubstring(sequence, start-3, start+3)), validationPattern.toString()));
							} else {
								ret.addError(SequenceValidationCode.illegalChraracter, String.format("Illegal %s sequence: >>%s<<, not matching %s",name(),TextHelper.toHexDump(TextHelper.safeSubstring(sequence, end-3, end+3)), validationPattern.toString()));
							}
						}
					} else {
						ret.addError(SequenceValidationCode.illegalChraracter, String.format("Illegal %s sequence: >>%s<<, not matching %s",name(),TextHelper.toHexDump(TextHelper.safeSubstring(sequence, 0, 6)), validationPattern.toString()));
					}
					return ret;
				}
			} else {
				return new SequenceValidationResponse();
			}
		}

	},
	aminoacid {
		@Override
		public SequenceValidationResponse validateSequence(String sequence, SequenceValidateOption option) {
			if (sequence==null) {
				throw new NullPointerException("Sequence parameter cannot be null");
			}
			if (option.needValidation()) {
				if (sequence.length()==0) {
					return new SequenceValidationResponse(SequenceValidationCode.emptySequence, "Empty sequence");
				} else {
					SequenceValidationResponse ret = new SequenceValidationResponse();
					if (SequenceHelper.AMINOACID_STOPS.matcher(sequence).matches()) {
						ret.merge(new SequenceValidationResponse(SequenceValidationCode.onlyStopCodons, "Only stop codons"));
					}
					if (SequenceValidateOption.hard.equals(option)) {
						Pattern validationPattern = SequenceHelper.AMINOACID_SEQUENCE_VALID; 
						Matcher matcher = validationPattern.matcher(sequence);
						if (matcher.find()) {
							int start = matcher.start();
							int end = matcher.end();
							if (start>0 || end<sequence.length()) {
								if (start>0) {
									ret.addError(SequenceValidationCode.illegalChraracter, String.format("Illegal %s sequence: >>%s<<, not matching %s",name(),TextHelper.toHexDump(TextHelper.safeSubstring(sequence, start-3, start+3)), validationPattern.toString()));
								} else {
									ret.addError(SequenceValidationCode.illegalChraracter, String.format("Illegal %s sequence: >>%s<<, not matching %s",name(),TextHelper.toHexDump(TextHelper.safeSubstring(sequence, end-3, end+3)), validationPattern.toString()));
								}
							}
						} else {
							ret.addError(SequenceValidationCode.illegalChraracter, String.format("Illegal %s sequence: >>%s<<, not matching %s",name(),TextHelper.toHexDump(TextHelper.safeSubstring(sequence, 0, 6)), validationPattern.toString()));
						}
					}
					return ret;
				}
			} else {
				return new SequenceValidationResponse();
			}
		}	
	};

	public SequenceValidationResponse validateSequence(String sequence, SequenceValidateOption option) {
		return new SequenceValidationResponse();
	}
}
