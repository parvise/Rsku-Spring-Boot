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
	
	
}
