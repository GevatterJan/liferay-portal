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

package com.liferay.portlet.enterpriseadmin.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;

import javax.portlet.PortletRequest;

/**
 * <a href="RoleDisplayTerms.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class RoleDisplayTerms extends DisplayTerms {

	public static final String NAME = "name";

	public static final String DESCRIPTION = "description";

	public static final String TYPE = "type";

	public RoleDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		name = ParamUtil.getString(portletRequest, NAME);
		description = ParamUtil.getString(portletRequest, DESCRIPTION);
		type = ParamUtil.getInteger(portletRequest, TYPE);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getType() {
		return type;
	}

	public String getTypeString() {
		if (type != 0) {
			return String.valueOf(type);
		}
		else {
			return StringPool.BLANK;
		}
	}

	protected String name;
	protected String description;
	protected int type;

}