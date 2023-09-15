package com.hp.c4.rsku.rSku.rest.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.bean.request.C4CostData;
import com.hp.c4.rsku.rSku.bean.request.TCosData;
import com.hp.c4.rsku.rSku.bean.request.TPeriodData;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.pojo.Product;
import com.hp.c4.rsku.rSku.rest.repo.C4AggregateCostCalculationRepo;

@Service
public class C4AggregateCostCalculationService {

	@Autowired
	private C4AggregateCostCalculationRepo c4AggregateCostCalculationRepo;

	public Map<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>> getC4CostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product[] prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList) throws Exception {

		return c4AggregateCostCalculationRepo.getC4CostOfSkus(pdesc, prodList, dateList, periodIdMap, _dateList);
	}

	public Set<C4CostData> getC4CosValueBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		return c4AggregateCostCalculationRepo.getC4CosValueBasedCostOfSkus(pdesc, prodList, dateList, periodIdMap,
				_dateList, type);
	}

	public Set<C4CostData> getC4CosVolumeBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		return c4AggregateCostCalculationRepo.getC4CosVolumeBasedCostOfSkus(pdesc, prodList, dateList, periodIdMap,
				_dateList, type);
	}

	public Set<C4CostData> getC4OpexValueBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		return c4AggregateCostCalculationRepo.getC4OpexValueBasedCostOfSkus(pdesc, prodList, dateList, periodIdMap,
				_dateList, type);
	}

	public Set<C4CostData> getC4OpexVolumeBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		return c4AggregateCostCalculationRepo.getC4OpexVolumeBasedCostOfSkus(pdesc, prodList, dateList, periodIdMap,
				_dateList, type);
	}

	public boolean isValueType(Product packId, char geoLevel, String geoCode) throws SQLException {
		return c4AggregateCostCalculationRepo.isValueType(packId, geoLevel, geoCode);
	}
}
