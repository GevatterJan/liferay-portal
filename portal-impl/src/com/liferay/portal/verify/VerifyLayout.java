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

package com.liferay.portal.verify;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;

import java.util.List;

/**
 * <a href="VerifyLayout.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class VerifyLayout extends VerifyProcess {

	protected void doVerify() throws Exception {
		List<Layout> layouts =
			LayoutLocalServiceUtil.getNullFriendlyURLLayouts();

		for (Layout layout : layouts) {
			String friendlyURL = StringPool.SLASH + layout.getLayoutId();

			LayoutLocalServiceUtil.updateFriendlyURL(
				layout.getPlid(), friendlyURL);
		}
	}

}