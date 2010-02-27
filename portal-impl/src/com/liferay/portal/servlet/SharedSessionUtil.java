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

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <a href="SharedSessionUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 */
public class SharedSessionUtil {

	public static Map<String, Object> getSharedSessionAttributes(
		HttpServletRequest request) {

		HttpSession session = request.getSession();

		SharedSessionAttributeCache cache =
			SharedSessionAttributeCache.getInstance(session);

		Map<String, Object> values = cache.getValues();

		if (_log.isDebugEnabled()) {
			_log.debug("Shared session attributes " + values);
		}

		return values;
	}

	private static Log _log = LogFactoryUtil.getLog(SharedSessionUtil.class);

}