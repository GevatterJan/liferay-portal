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

package com.liferay.portlet.journalarticles.action;

import com.liferay.portal.NoSuchGroupException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portlet.journalcontent.action.WebContentAction;

import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <a href="ViewAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class ViewAction extends WebContentAction {

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		try {
			PortletPreferences preferences = renderRequest.getPreferences();

			long groupId = GetterUtil.getLong(
				preferences.getValue("group-id", StringPool.BLANK));

			GroupLocalServiceUtil.getGroup(groupId);

			return mapping.findForward("portlet.journal_articles.view");
		}
		catch (NoSuchGroupException nsge) {
			return mapping.findForward("/portal/portlet_not_setup");
		}
	}

}