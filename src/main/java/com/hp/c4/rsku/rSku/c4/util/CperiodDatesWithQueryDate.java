package com.hp.c4.rsku.rSku.c4.util;

public abstract class CperiodDatesWithQueryDate {
	public String startOnDates[];
	public String queryDate;

	public CperiodDatesWithQueryDate() {
	}

	public String toString() {
		StringBuffer _ret = new StringBuffer("com.hp.c4.rsku.rSku.c4.util.CperiodDatesWithQueryDate {");
		_ret.append("\n");
		_ret.append("java.lang.String[] startOnDates=");
		_ret.append("{");
		if (startOnDates == null) {
			_ret.append(startOnDates);
		} else {
			for (int $counter55 = 0; $counter55 < startOnDates.length; $counter55++) {
				_ret.append(startOnDates[$counter55] == null ? null : '"' + startOnDates[$counter55] + '"');
				if ($counter55 < startOnDates.length - 1)
					_ret.append(",");
			}

		}
		_ret.append("}");
		_ret.append(",\n");
		_ret.append("java.lang.String queryDate=");
		_ret.append(queryDate == null ? null : '"' + queryDate + '"');
		_ret.append("\n");
		_ret.append("}");
		return _ret.toString();
	}

	public boolean equals(java.lang.Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (o instanceof CperiodDatesWithQueryDate) {
			CperiodDatesWithQueryDate obj = (CperiodDatesWithQueryDate) o;
			boolean res = true;
			if (res = startOnDates.length == obj.startOnDates.length) {
				for (int $counter56 = 0; res && $counter56 < startOnDates.length; $counter56++)
					res = startOnDates[$counter56] == obj.startOnDates[$counter56]
							|| startOnDates[$counter56] != null && obj.startOnDates[$counter56] != null
									&& startOnDates[$counter56].equals(obj.startOnDates[$counter56]);

			}
			if (res)
				res = queryDate == obj.queryDate
						|| queryDate != null && obj.queryDate != null && queryDate.equals(obj.queryDate);
			return res;
		} else {
			return false;
		}
	}

}
