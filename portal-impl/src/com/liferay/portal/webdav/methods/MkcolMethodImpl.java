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

package com.liferay.portal.webdav.methods;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.webdav.Status;
import com.liferay.portal.webdav.WebDAVException;
import com.liferay.portal.webdav.WebDAVRequest;
import com.liferay.portal.webdav.WebDAVStorage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <a href="MkcolMethodImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class MkcolMethodImpl implements Method {

	public int process(WebDAVRequest webDavRequest) throws WebDAVException {
		WebDAVStorage storage = webDavRequest.getWebDAVStorage();
		HttpServletRequest request = webDavRequest.getHttpServletRequest();
		HttpServletResponse response = webDavRequest.getHttpServletResponse();
		long groupId = webDavRequest.getGroupId();

		int statusCode = HttpServletResponse.SC_FORBIDDEN;

		if (groupId != 0) {
			Status status = storage.makeCollection(webDavRequest);

			if (Validator.isNotNull(status.getObject())) {
				response.setHeader(
					HttpHeaders.LOCATION,
					PortalUtil.getPortalURL(request) +
						webDavRequest.getRootPath() + StringPool.SLASH +
							status.getObject());
			}

			statusCode = status.getCode();
		}

		return statusCode;
	}

}