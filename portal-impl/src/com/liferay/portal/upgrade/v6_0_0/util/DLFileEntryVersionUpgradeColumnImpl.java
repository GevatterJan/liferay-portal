/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade.v6_0_0.util;

import com.liferay.portal.kernel.upgrade.util.BaseUpgradeColumnImpl;

import java.text.NumberFormat;

/**
 * <a href="DLFileEntryVersionUpgradeColumnImpl.java.html"><b><i>View Source</i>
 * </b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class DLFileEntryVersionUpgradeColumnImpl extends BaseUpgradeColumnImpl {

	public static String getNewVersion(double version) {
		NumberFormat numberFormat = NumberFormat.getInstance();

		numberFormat.setMaximumFractionDigits(1);
		numberFormat.setMinimumFractionDigits(1);

		return numberFormat.format(version);
	}

	public DLFileEntryVersionUpgradeColumnImpl(String name) {
		super(name);
	}

	public Object getNewValue(Object oldValue) throws Exception {
		Double version = (Double)oldValue;

		return getNewVersion(version.doubleValue());
	}

}