package com.hp.c4.rsku.rSku.dbio.persistent.cache;

import com.hp.c4.rsku.rSku.pojo.Product;

public class PlMappingData implements Comparable {

	private final static String PL_KEY_DELIMITER = "|";
	private static long _thePlCacheHits = 0;

	private String _pl = null;
	private String _platform = null;
	private com.hp.c4.rsku.rSku.pojo.Product _prod = null;
	int _count = 0;

	public PlMappingData(com.hp.c4.rsku.rSku.pojo.Product pProd, String pPl, String pPlatform) {
		_pl = pPl;
		_platform = pPlatform;
		_prod = pProd;
	}

	private void resetCounter() {
		_count = 0;
	}

	private synchronized void count() {
		try {
			_thePlCacheHits++;
			_count++;
		} catch (Throwable ignore) {
		} // in case we reach the int limit
	}

	public int compareTo(Object o) {
		PlMappingData that = (PlMappingData) o;
		if (_count > that._count)
			return -1;
		if (_count < that._count)
			return 1;

		return getPlKey(_prod).compareTo(getPlKey(that._prod));
	}

	public String get_pl() {
		return _pl;
	}

	public void set_pl(String _pl) {
		this._pl = _pl;
	}

	public String get_platform() {
		return _platform;
	}

	public void set_platform(String _platform) {
		this._platform = _platform;
	}

	public Product get_prod() {
		return _prod;
	}

	public void set_prod(Product _prod) {
		this._prod = _prod;
	}

	public String toString() {
		StringBuffer theBuffer = new StringBuffer();
		theBuffer.append("[");
		theBuffer.append(_pl);
		theBuffer.append(",");
		theBuffer.append(_platform);
		theBuffer.append(",");
		theBuffer.append(_count);
		theBuffer.append("]");
		return theBuffer.toString();
	}

	static private String getPlKey(Product pProd) {
		return getPlKey(pProd.getProdId(), pProd.getOpt(), pProd.getSpn(), pProd.getMcc());
	}

	static private String getPlKey(String pId, String pOpt, String pSpn, String pMcc) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(pId);
		buffer.append(PL_KEY_DELIMITER);
		buffer.append((pOpt == null || pOpt.trim().length() == 0) ? "" : pOpt);
		buffer.append(PL_KEY_DELIMITER);
		buffer.append((pSpn == null || pSpn.trim().length() == 0) ? "" : pSpn);
		buffer.append(PL_KEY_DELIMITER);
		buffer.append((pMcc == null || pMcc.trim().length() == 0) ? "" : pMcc);
		return buffer.toString();
	}

}
