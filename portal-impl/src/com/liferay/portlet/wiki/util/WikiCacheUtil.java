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

package com.liferay.portlet.wiki.util;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.wiki.PageContentException;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portlet.wiki.model.WikiPageDisplay;
import com.liferay.portlet.wiki.service.WikiPageLocalServiceUtil;

import java.util.Map;

import javax.portlet.PortletURL;

import org.apache.commons.lang.time.StopWatch;

/**
 * <a href="WikiCacheUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Jorge Ferrer
 */
public class WikiCacheUtil {

	public static final String CACHE_NAME = WikiCacheUtil.class.getName();

	public static void clearCache(long nodeId) {
		_cache.removeAll();
	}

	public static void clearCache(long nodeId, String title) {
		clearCache(nodeId);
	}

	public static WikiPageDisplay getDisplay(
		long nodeId, String title, PortletURL viewPageURL,
		PortletURL editPageURL, String attachmentURLPrefix) {

		StopWatch stopWatch = null;

		if (_log.isDebugEnabled()) {
			stopWatch = new StopWatch();

			stopWatch.start();
		}

		String key = _encodeKey(nodeId, title, viewPageURL.toString());

		WikiPageDisplay pageDisplay = (WikiPageDisplay)_cache.get(key);

		if (pageDisplay == null) {
			pageDisplay = _getPageDisplay(
				nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);

			_cache.put(key, pageDisplay);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"getDisplay for {" + nodeId + ", " + title + ", " +
					viewPageURL + ", " + editPageURL + "} takes " +
						stopWatch.getTime() + " ms");
		}

		return pageDisplay;
	}

	public static Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException {

		String key = _encodeKey(
			page.getNodeId(), page.getTitle(), _OUTGOING_LINKS);

		Map<String, Boolean> links = (Map<String, Boolean>)_cache.get(key);

		if (links == null) {
			links = WikiUtil.getLinks(page);

			_cache.put(key, links);
		}

		return links;
	}

	private static String _encodeKey(
		long nodeId, String title, String postfix) {

		StringBundler sb = new StringBundler(6);

		sb.append(CACHE_NAME);
		sb.append(StringPool.POUND);
		sb.append(nodeId);
		sb.append(title);

		if (postfix != null) {
			sb.append(StringPool.POUND);
			sb.append(postfix);
		}

		return sb.toString();
	}

	private static WikiPageDisplay _getPageDisplay(
		long nodeId, String title, PortletURL viewPageURL,
		PortletURL editPageURL, String attachmentURLPrefix) {

		try {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Get page display for {" + nodeId + ", " + title + ", " +
						viewPageURL + ", " + editPageURL + "}");
			}

			return WikiPageLocalServiceUtil.getPageDisplay(
				nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get page display for {" + nodeId + ", " + title +
						", " + viewPageURL + ", " + editPageURL + "}");
			}

			return null;
		}
	}

	private static final String _OUTGOING_LINKS = "OUTGOING_LINKS";

	private static Log _log = LogFactoryUtil.getLog(WikiUtil.class);

	private static PortalCache _cache = MultiVMPoolUtil.getCache(CACHE_NAME);

}