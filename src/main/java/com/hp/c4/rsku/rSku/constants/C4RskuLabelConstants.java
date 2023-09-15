package com.hp.c4.rsku.rSku.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class C4RskuLabelConstants {

	@Value("${rsku.access.denied.info.prod.not.loaded}")
	public String PRODUCT_NOT_LOADED;

	@Value("${rsku.access.denied.info.product.not.pl}")
	public String PRODUCT_NOT_BELONGS_PL;

	@Value("${rsku.error.key}")
	public String C4_EXCEPTION_KEY;

	@Value("${rsku.error.value}")
	public String C4_EXCEPTION_VALUE;

	@Value("${rsku.importer.notify.email.author.title}")
	public String IMPORTER_NOTIFY_MAIL;

	@Value("${rsku.base.sku.error.key}")
	public String BASE_PRODUCTS_KEY;

	@Value("${rsku.base.sku.error.value}")
	public String BASE_PRODUCTS_VALUE;

	@Value("${rsku.delivery.method.error.key}")
	public String DELIVERY_METHOD_KEY;

	@Value("${rsku.delivery.method.error.value}")
	public String DELIVERY_METHOD_VALUE;

	@Value("${rsku.mot.error.key}")
	public String MOT_KEY;

	@Value("${rsku.mot.error.value}")
	public String MOT_VALUE;

	@Value("${rsku.country.error.key}")
	public String COUNTRY_KEY;

	@Value("${rsku.country.error.value}")
	public String COUNTRY_VALUE;

	@Value("${rsku.invalid.country.error.value}")
	public String INVALID_COUNTRY_VALUE;

	@Value("${rsku.cost.date.error.key}")
	public String COST_DATE_KEY;

	@Value("${rsku.cost.date.missing.error.value}")
	public String COST_DATE_VALUE;

	@Value("${rsku.cost.date.format.error.value}")
	public String COST_DATE_FORMAT_VALUE;

	@Value("${rsku.rapid.sku.error.key}")
	public String RAPID_SKU_KEY;

	@Value("${rsku.rapid.sku.error.value}")
	public String RAPID_SKU_VALUE;

	@Value("${rsku.pl.error.key}")
	public String PL_KEY;

	@Value("${rsku.pl.invalid.error.value}")
	public String PL_INVALID_VALUE;

	@Value("${rsku.pl.invalid.hpi.error.value}")
	public String PL_INVALID_HPI_VALUE;

	@Value("${rsku.connect.error.key}")
	public String CONNECT_KEY;

	@Value("${rsku.connect.error.value}")
	public String CONNECT_VALUE;

	@Value("${rsku.connect.username.password.fail.key}")
	public String AUTH_FAILS_KEY;

	@Value("${c4.rsku.importer.files.from.location}")
	public String IMPORTER_INPUT_FILE_FROM_LOCATION;

	@Value("${c4.rsku.importer.files.to.location}")
	public String IMPORTER_INPUT_FILE_TO_LOCATION;

	@Value("${rsku.transfer.file.authenticate.local.user.allow}")
	public String IS_DB_CONFIG;

	@Value("${rsku.transfer.file.authenticate.host}")
	public String IMPORTER_STAGE_SERVER_HOSTNAME;

	@Value("${rsku.transfer.file.authenticate.local.userName}")
	public String IMPORTER_STAGE_SERVER_USERNAME;

	@Value("${rsku.transfer.file.authenticate.local.password}")
	public String IMPORTER_STAGE_SERVER_PASSWORD;

	@Value("${rsku.staging.importer.sender.not.mailids}")
	public String IMPORTER_NOTIFICATION_AUTHOR_MAILIDS;

	@Value("${c4.rsku.exclude.add.mots.mapped}")
	public String EXCLUDE_ADD_MOTS;
	
	@Value("${c4.valid.skus.with.pipe.error.key}")
	public String REQ_SKU_VALID_PIPE;

	public static final String COS_VALUE_TYPE = "COS_VALUE_TYPE";
	public static final String COS_VOLUME_TYPE = "COS_VOLUME_TYPE";
	public static final String COS_TYPE = "COS_TYPE";

	public static final String OPEXP_TYPE = "OPEXP_TYPE";
	public static final String OPEXP_VALUE_TYPE = "OPEXP_VALUE_TYPE";
	public static final String OPEXP_VOLUME_TYPE = "OPEXP_VOLUME_TYPE";

	// Cost elements used by the formulas
	public final static String OUTPUTFOREX = "OUTPUTFOREX";
	public final static String MATRL = "MATRL";
	public final static String VWRTY = "VWRTY";
	public final static String VTRDX = "VTRDX";
	public final static String VRLTY = "VRLTY";
	public final static String OVBLC = "OVBLC";
	public final static String MALAD = "MALAD";
	public final static String OFXDC = "OFXDC";
	public final static String DEFAULT_DELIVERY = VTRDX;
	public final static String EXWTX = "EXWTX";
	public final static String FCATX = "FCATX";
	public final static String FOBTX = "FOBTX";
	public final static String CPTTX = "CPTTX";
	public final static String CIPTX = "CIPTX";
	public final static String DAFTX = "DAFTX";
	public final static String DDUTX = "DDUTX";

	// Opex Elements used by Formulas
	public final static String LCP = "LCP";
	public final static String FOREX = "FOREX";
	public final static String DSCPC = "DSCPC";

	// Additional MOTS
	public final static String DDUTR = "DDUTR";
	public final static String VTRTR = "VTRTR";

	// MPO R2: MOT : new trading expenses
	public final static String DDURA = "DDURA";
	public final static String DDUSE = "DDUSE";
	public final static String DDUEX = "DDUEX";
	public final static String VTRRA = "VTRRA";
	public final static String VTRSE = "VTRSE";
	public final static String VTREX = "VTREX";

	public final static String DELIVERY = "DELIVERY";

	public final static String BATCH_TYPE_FULL = "Full";
	public final static String BATCH_TYPE_PARTIAL = "Partial";
	public final static String BATCH_FILE_QUARTER = "Quarter";

	public final static String IMPORTER_FILE_XML_EXTENSION = ".xml";
	public final static String IMPORTER_FILE_DAT_EXTENSION = ".dat";

	public final static String DEFAULT_OUTPUT_CURRENCY = "USD";
	public final static char MASK_ELEMENTS_REQUIRED = 'R';
	public final static String PIPE_SYMBOL = "|";
	public final static String ARROW_NULL_CHECK = "<null>";
	public final static char GEO_LEVEL_REGION = 'R';

}
