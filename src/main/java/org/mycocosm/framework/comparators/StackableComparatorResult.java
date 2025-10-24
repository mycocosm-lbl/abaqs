package org.mycocosm.framework.comparators;

public enum StackableComparatorResult implements Inversable<StackableComparatorResult>{
	greater {
		@Override
		public StackableComparatorResult inverse() {
			return smaller;
		}
		@Override
		public int toComparatorResult() {
			return 1;
		}
	}, 
	smaller {
		@Override
		public StackableComparatorResult inverse() {
			return greater;
		}
		@Override
		public int toComparatorResult() {
			return -1;
		}
	}, 
	equal {
		@Override
		public StackableComparatorResult inverse() {
			return equal;
		}
		@Override
		public int toComparatorResult() {
			return 0;
		}
		
	}, 
	undecided {
		@Override
		public StackableComparatorResult inverse() {
			return undecided;
		}
		@Override
		public int toComparatorResult() {
			return 0;
		}
	};
	
	public static final StackableComparatorResult fromComparatorResult(int i) {
		if (i>0) {
			return greater;
		} else if (i<0) {
			return smaller;
		} else {
			return equal;
		}
	}

	public static final int toComparatorResult(StackableComparatorResult r) {
		switch (r) {
			case equal: return 0;
			case greater: return 1;
			case smaller: return -1;
			case undecided: 
			default: throw new StackableComparatorException("Open ended stackable comparators stack");
		}
	}

	@Override
	public StackableComparatorResult inverse() {
		return null;
	}
	public int toComparatorResult() {
		return 0;
	}
}
