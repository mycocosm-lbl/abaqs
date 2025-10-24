package org.mycocosm.famework.collections;

import java.util.Date;
import java.util.Iterator;

import org.mycocosm.famework.text.TextHelper;

public enum AggregationOperations implements AggregationOperation<Object,Object> {


	Sum {
		@Override
		public Object aggregate(Iterable<Object> data) {
			return DOUBLE_SUM.aggregate(data);
		}
	},
	Average{
		@Override
		public Object aggregate(Iterable<Object> data) {
			return DOUBLE_AVERAGE.aggregate(data);
		}
	},
	Count{
		@Override
		public Object aggregate(Iterable<Object> data) {
			return INTEGER_COUNT.aggregate(data);
		}
	},Min{
		@Override
		public Object aggregate(Iterable<Object> data) {
			return DOUBLE_MIN.aggregate(data);
		}
	},Max{
		@Override
		public Object aggregate(Iterable<Object> data) {
			return DOUBLE_MAX.aggregate(data);
		}
	},
	First,
	Last {
		@Override
		public Object aggregate(Iterable<Object> data) {
			Object ret = null;
			for (Object val:data) {
				ret = val;
			}
			return ret;
		}
	},
	Earliest {
		@Override
		public Object aggregate(Iterable<Object> data) {
			return DATE_EARLIEST.aggregate(data);
		}
	},
	Latest {
		@Override
		public Object aggregate(Iterable<Object> data) {
			return DATE_LATEST.aggregate(data);
		}
	},
	FirstNotNull {
		@Override
		public Object aggregate(Iterable<Object> data) {
			for (Object val:data) {
				if (val!=null) {
					return val;
				}
			}
			return null;
		}
	},
	LastNotNull {
		@Override
		public Object aggregate(Iterable<Object> data) {
			Object ret = null;
			for (Object val:data) {
				if (val!=null) {
					ret = val;
				}
			}
			return ret;
		}
	};


	private static final double castToDouble(Object val) {
		if (Number.class.isAssignableFrom(val.getClass())) {
			return ((Number)val).doubleValue();
		} else {
			throw new IllegalArgumentException("Illegal aggregation operand type: "+val.getClass()+", expected numeric");
		}
	}
	public static final AggregationOperation<Double,Object> DOUBLE_SUM = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			double ret = 0.0;
			for (Object val:data) {
				double value = castToDouble(val);
				if (!Double.isNaN(value)) {
					ret+=value;
				}
			}
			return ret;
		}
	};  
	public static final AggregationOperation<Double,Object> DOUBLE_AVERAGE = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			double total = Double.NaN;
			int count=0;
			for (Object val:data) {
				double value = castToDouble(val);
				if (!Double.isNaN(value)) {
					if (!Double.isNaN(total)) {
						total+=value;
						count++;
					} else {
						total=value;
						count=1;
					}
				}
			}
			if (!Double.isNaN(total)) {
				if (count>0) {
					return total/(double)count;
				} else {
					if (total>=0.0) {
						return Double.POSITIVE_INFINITY;
					} else {
						return Double.NEGATIVE_INFINITY;
					}
				}

			} else {
				return Double.NaN;
			}
		}
	};  
	public static final AggregationOperation<Double,Object> DOUBLE_MIN = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			double ret = Double.NaN;
			for (Object val:data) {
				double value = castToDouble(val);
				if (!Double.isNaN(value)) {
					if (!Double.isNaN(ret)) {
						ret=Math.min(ret, value);
					} else {
						ret=value;
					}
				}
			}
			return ret;
		}
	};  
	public static final AggregationOperation<Double,Object> DOUBLE_MAX = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			double ret = Double.NaN;
			for (Object val:data) {
				double value = castToDouble(val);
				if (!Double.isNaN(value)) {
					if (!Double.isNaN(ret)) {
						ret=Math.max(ret, value);
					} else {
						ret=value;
					}
				}
			}
			return ret;
		}
	};  

	public static final AggregationOperation<Double,Object> DOUBLE_COUNT = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			double ret = 0;
			Iterator<Object> it = data.iterator(); 
			while (it.hasNext()) {
				ret+=1.0;
				it.next();
			}
			return ret;
		}
	};

	public static final AggregationOperation<Integer,Object> INTEGER_COUNT = new AggregationOperation<Integer,Object>() {

		@Override
		public Integer aggregate(Iterable<Object> data) {
			int ret = 0;
			Iterator<Object> it = data.iterator(); 
			while (it.hasNext()) {
				ret++;
				it.next();
			}
			return ret;
		}
	};
	public static final AggregationOperation<Double,Object> DOUBLE_FIRST = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			for (Object val:data) {
				double value = castToDouble(val);
				if (!Double.isNaN(value)) {
					return value;
				}
			}
			return Double.NaN;
		}
	};  
	public static final AggregationOperation<Double,Object> DOUBLE_LAST = new AggregationOperation<Double,Object>() {

		@Override
		public Double aggregate(Iterable<Object> data) {
			double ret = Double.NaN;
			for (Object val:data) {
				double value = castToDouble(val);
				if (!Double.isNaN(value)) {
					ret=value;
				}
			}
			return ret;
		}
	};  

	public static final AggregationOperation<Date,Object> DATE_LATEST = new AggregationOperation<Date,Object>() {

		@Override
		public Date aggregate(Iterable<Object> data) {
			Date ret = null;
			for (Object val:data) {
				Date value = (Date)val;
				if (value!=null) {
					if (ret==null) {
						ret = value;
					} else if (ret.before(value)) { 
						ret=value;
					}
				}
			}
			return ret;
		}
	};  

	public static final AggregationOperation<Date,Object> DATE_EARLIEST = new AggregationOperation<Date,Object>() {

		@Override
		public Date aggregate(Iterable<Object> data) {
			Date ret = null;
			for (Object val:data) {
				Date value = (Date)val;
				if (value!=null) {
					if (ret==null) {
						ret = value;
					} else if (ret.after(value)) { 
						ret=value;
					}
				}
			}
			return ret;
		}
	};  
	
	@Override
	public Object aggregate(Iterable<Object> data) {
		Iterator<Object> it = data.iterator(); 
		if (it.hasNext()) {
			return it.next(); 
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final <T,V> AggregationOperation<T,V> parse(String def) {
		for (AggregationOperations v:AggregationOperations.values()) {
			if (TextHelper.nullSafeEqualsIgnoreCase(v.name(), def)) {
				return (AggregationOperation<T,V>)v;
			}
		}
		throw new IllegalAggregationOperationException(def);
	}

}
