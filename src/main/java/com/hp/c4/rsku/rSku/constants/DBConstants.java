package com.hp.c4.rsku.rSku.constants;

public class DBConstants {

	public static final String C4_DBPOOL_C4PROD_ONSI = "c4.dbPool.OnlineShort";
	public static final String C4_DBPOOL_C4PROD_OFFI = "c4.dbPool.Offline";
	public static final String C4_DBPOOL_GPSNAP_ONSI = "c4.dbPool.Util";
	public static final String C4_DBPOOL_INFOSHU_INFI = "c4.dbPool.c4security";

	public static final String COUNTRY_TO_PRICE_DESCRIPTORS = "SELECT a.CODE, a.DESCRIPTION,a.PARENT,a.SN, b.PRICE_COUNTRY_CODE, b.PRICE_CURRENCY_CODE, b.PRICE_TERM_CODE "
			+ " FROM T_GEO_CODE a, T_DEFAULT_CNTRY_PRC_DESC b "
			+ " where a.CODE=b.COUNTRY_CODE  order by a.DESCRIPTION";

	public static final String DEFAULT_MOT_TRADING_EXPENSE = "select a.tr_ex_3||'#'||a.MOT as TR_MOT, a.element_type AS ELEMENT_TYPE, a.default_tr_ex AS DEF_TREX from t_mot_trading_expense a";
	public static final String DB_PROPERTIES_FILE = "D:/Pervez/C4/RSKU_Project/c4.properties";

	// insert into t_prod_hier
	// values(9609633,'1A4A7A575C0F662DE05400215A951348','RSku1','<null>','<null>','ACTIV','<null>','HP
	// First Dummy Rapid Sku','HPI');
	public static final String INSERT_RSKU_INTO_PROD_HIER = "INSERT INTO T_PROD_HIER (NODE_ID,PROD_LINE_KEY_ID,PROD_ID,OPT,SPN,STATUS,PROD_DESC,SKU_DESC,TENANT_CD) VALUES (?,?,?,'<null>','<null>',?,'<null>',?,?)";

	public static final String SELECT_PL_KEY_ID = "SELECT KEY_ID FROM T_PRODUCT_LINE_KEY WHERE PROD_LINE=? AND PLATFORM!='<null>' AND SUB_PLATFORM!='<null>' AND STATUS='ACTIV' ";

	public static final String SELECT_RSKU_AVLBLE_IN_DB = "SELECT * FROM T_PROD_HIER WHERE TENANT_CD='HPI' AND STATUS='ACTIV' AND PROD_ID=? AND OPT='<null>' AND SPN='<null>'";

	public static final String SELECT_T_PROD_HIER_MAX_NODE_ID = "SELECT MAX(NODE_ID) as MAX_VAL FROM T_PROD_HIER";

	public static final String SELECT_ALL_DELIVERY_METHODS = "SELECT o.element_type, t.description FROM t_cos_element_type_order o, t_cos_element_type t WHERE o.element_type = t.type ";

	public static final String MOT = "select  a.mot as MOT, a.description as DESCRIPTION from t_mot a where status = 'A' order by a.description";

	public static final String FIND_SELECT_T_PERIOD_IDS = "SELECT * FROM T_PERIOD WHERE START_DATE IN(?,?,?)  ORDER BY START_DATE DESC";

	public static final String FIND_COST_FOR_RSKU_AVL = "SELECT COS.ELEMENT_TYPE as ELEMENT_TYPE, COS.COST as COST, COS.ACTION as ACTION FROM T_COS COS, T_COS_KEY CKEY, T_PERIOD PD WHERE "
			+ " (CKEY.PRODUCT_ID=? AND CKEY.MCC='<null>' AND CKEY.OPT='<null>' AND CKEY.SPN='<null>') "
			+ " AND (PD.START_DATE=? AND PD.PERIOD_TYPE='Q') AND COS.STATUS='ACTIV' AND COS.GEO_LEVEL='W'"
			+ " AND CKEY.PACK_ID=COS.PACK_ID AND PD.PERIOD_ID=COS.PERIOD_ID";

	
	// Pricing
	
	public static final String FIND_SELECT_T_PERIOD_ID = "SELECT * FROM T_PERIOD WHERE START_DATE IN(?)";
	public static final String FIND_ALL_SKU_COUNTRY_PERIOD_ID = "select * from t_cos_key where pack_id in (select distinct(pack_id) from t_cos where period_id=? and geo_code=? and status='ACTIV')";
}
