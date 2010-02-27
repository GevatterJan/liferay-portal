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

package com.liferay.portal.servlet.filters.strip;

import com.liferay.portal.kernel.concurrent.ConcurrentLRUCache;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.KMPSearch;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.servlet.filters.etag.ETagUtil;
import com.liferay.portal.util.MinifierUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.util.servlet.ServletResponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <a href="StripFilter.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class StripFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		StripFilter.class.getName() + "SKIP_FILTER";

	protected int countContinuousWhiteSpace(byte[] oldByteArray, int offset) {
		int count = 0;

		for (int i = offset ; i < oldByteArray.length ; i++) {
			char c = (char)oldByteArray[i];

			if ((c == CharPool.SPACE) || (c == CharPool.TAB) ||
				(c == CharPool.RETURN) || (c == CharPool.NEW_LINE)) {

				count++;
			}
			else{
				return count;
			}
		}

		return count;
	}

	protected boolean hasMarker(byte[] oldByteArray, int pos, byte[] marker) {
		if ((pos + marker.length) >= oldByteArray.length) {
			return false;
		}

		for (int i = 0; i < marker.length; i++) {
			byte c = marker[i];

			byte oldC = oldByteArray[pos + i + 1];

			if ((c != oldC) && (Character.toUpperCase(c) != oldC)) {
				return false;
			}
		}

		return true;
	}

	protected boolean isAlreadyFiltered(HttpServletRequest request) {
		if (request.getAttribute(SKIP_FILTER) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	protected boolean isInclude(HttpServletRequest request) {
		String uri = (String)request.getAttribute(
			JavaConstants.JAVAX_SERVLET_INCLUDE_REQUEST_URI);

		if (uri == null) {
			return false;
		}
		else {
			return true;
		}
	}

	protected boolean isStrip(HttpServletRequest request) {
		if (!ParamUtil.getBoolean(request, _STRIP, true)) {
			return false;
		}
		else {

			// Modifying binary content through a servlet filter under certain
			// conditions is bad on performance the user will not start
			// downloading the content until the entire content is modified.

			String lifecycle = ParamUtil.getString(request, "p_p_lifecycle");

			if ((lifecycle.equals("1") &&
				 LiferayWindowState.isExclusive(request)) ||
				lifecycle.equals("2")) {

				return false;
			}
			else {
				return true;
			}
		}
	}

	protected int processCSS(
		byte[] oldByteArray, UnsyncByteArrayOutputStream newBytes,
		int currentIndex) {

		int beginIndex = currentIndex + _MARKER_STYLE_OPEN.length + 1;

		int endIndex = KMPSearch.search(
			oldByteArray, beginIndex, _MARKER_STYLE_CLOSE,
			_MARKER_STYLE_CLOSE_NEXTS);

		if (endIndex == -1) {
			_log.error("Missing </style>");

			return currentIndex + 1;
		}

		int newBeginIndex = endIndex + _MARKER_STYLE_CLOSE.length;

		newBeginIndex += countContinuousWhiteSpace(oldByteArray, newBeginIndex);

		String content = new String(
			oldByteArray, beginIndex, endIndex - beginIndex);

		if (Validator.isNull(content)) {
			return newBeginIndex;
		}

		String minifiedContent = content;

		if (PropsValues.MINIFIER_INLINE_CONTENT_CACHE_SIZE > 0) {
			String key = String.valueOf(content.hashCode());

			minifiedContent = _minifierCache.get(key);

			if (minifiedContent == null) {
				minifiedContent = MinifierUtil.minifyCss(content);

				_minifierCache.put(key, minifiedContent);
			}
		}

		if (Validator.isNull(minifiedContent)) {
			return newBeginIndex;
		}

		newBytes.write(_STYLE_TYPE_CSS);
		newBytes.write(minifiedContent.getBytes());
		newBytes.write(_MARKER_STYLE_CLOSE);

		return newBeginIndex;
	}

	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		if (isStrip(request) && !isInclude(request) &&
			!isAlreadyFiltered(request)) {

			if (_log.isDebugEnabled()) {
				String completeURL = HttpUtil.getCompleteURL(request);

				_log.debug("Stripping " + completeURL);
			}

			request.setAttribute(SKIP_FILTER, Boolean.TRUE);

			StripResponse stripResponse = new StripResponse(response);

			processFilter(
				StripFilter.class, request, stripResponse, filterChain);

			String contentType = GetterUtil.getString(
				stripResponse.getContentType()).toLowerCase();

			byte[] oldByteArray = stripResponse.getData();

			if ((oldByteArray != null) && (oldByteArray.length > 0)) {
				byte[] newByteArray = null;

				if (_log.isDebugEnabled()) {
					_log.debug("Stripping content of type " + contentType);
				}

				if (contentType.indexOf("text/") != -1) {
					newByteArray = strip(oldByteArray);
				}
				else {
					newByteArray = oldByteArray;
				}

				if (!ETagUtil.processETag(request, response, newByteArray)) {
					response.setContentType(contentType);

					ServletResponseUtil.write(response, newByteArray);
				}
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				String completeURL = HttpUtil.getCompleteURL(request);

				_log.debug("Not stripping " + completeURL);
			}

			processFilter(StripFilter.class, request, response, filterChain);
		}
	}

	protected int processJavaScript(
		byte[] oldByteArray, UnsyncByteArrayOutputStream newBytes,
		int currentIndex, byte[] openTag) {

		int beginIndex = currentIndex + openTag.length + 1;

		int endIndex = KMPSearch.search(
			oldByteArray, beginIndex, _MARKER_SCRIPT_CLOSE,
			_MARKER_SCRIPT_CLOSE_NEXTS);

		if (endIndex == -1) {
			_log.error("Missing </script>");

			return currentIndex + 1;
		}

		int newBeginIndex = endIndex + _MARKER_SCRIPT_CLOSE.length;

		newBeginIndex += countContinuousWhiteSpace(oldByteArray, newBeginIndex);

		String content = new String(
			oldByteArray, beginIndex, endIndex - beginIndex);

		if (Validator.isNull(content)) {
			return newBeginIndex;
		}

		String minifiedContent = content;

		if (PropsValues.MINIFIER_INLINE_CONTENT_CACHE_SIZE > 0) {
			String key = String.valueOf(content.hashCode());

			minifiedContent = _minifierCache.get(key);

			if (minifiedContent == null) {
				minifiedContent = MinifierUtil.minifyJavaScript(content);

				_minifierCache.put(key, minifiedContent);
			}
		}

		if (Validator.isNull(minifiedContent)) {
			return newBeginIndex;
		}

		newBytes.write(_SCRIPT_TYPE_JAVASCRIPT);
		newBytes.write(_CDATA_OPEN);
		newBytes.write(minifiedContent.getBytes());
		newBytes.write(_CDATA_CLOSE);
		newBytes.write(_MARKER_SCRIPT_CLOSE);

		return newBeginIndex;
	}

	protected int processPre(
		byte[] oldByteArray, UnsyncByteArrayOutputStream newBytes,
		int currentIndex) {

		int beginIndex = currentIndex + _MARKER_PRE_OPEN.length + 1;

		int endIndex = KMPSearch.search(
			oldByteArray, beginIndex, _MARKER_PRE_CLOSE,
			_MARKER_PRE_CLOSE_NEXTS);

		if (endIndex == -1) {
			_log.error("Missing </pre>");

			return currentIndex + 1;
		}

		int newBeginIndex = endIndex + _MARKER_PRE_CLOSE.length;

		newBytes.write(
			oldByteArray, currentIndex, newBeginIndex - currentIndex);

		newBeginIndex += countContinuousWhiteSpace(oldByteArray, newBeginIndex);

		return newBeginIndex;
	}

	protected int processTextArea(
		byte[] oldByteArray, UnsyncByteArrayOutputStream newBytes,
		int currentIndex) {

		int beginIndex = currentIndex + _MARKER_TEXTAREA_OPEN.length + 1;

		int endIndex = KMPSearch.search(
			oldByteArray, beginIndex, _MARKER_TEXTAREA_CLOSE,
			_MARKER_TEXTAREA_CLOSE_NEXTS);

		if (endIndex == -1) {
			_log.error("Missing </textArea>");

			return currentIndex + 1;
		}

		int newBeginIndex = endIndex + _MARKER_TEXTAREA_CLOSE.length;

		newBytes.write(
			oldByteArray, currentIndex, newBeginIndex - currentIndex);

		newBeginIndex += countContinuousWhiteSpace(oldByteArray, newBeginIndex);

		return newBeginIndex;
	}

	protected byte[] strip(byte[] oldByteArray) {
		UnsyncByteArrayOutputStream newBytes = new UnsyncByteArrayOutputStream(
			(int)(oldByteArray.length * _COMPRESSION_RATE));

		int count = countContinuousWhiteSpace(oldByteArray, 0);

		for (int i = count; i < oldByteArray.length; i++) {
			byte b = oldByteArray[i];

			if (b == CharPool.LESS_THAN) {
				if (hasMarker(oldByteArray, i, _MARKER_PRE_OPEN)) {
					i = processPre(oldByteArray, newBytes, i) - 1;

					continue;
				}
				else if (hasMarker(oldByteArray, i, _MARKER_TEXTAREA_OPEN)) {
					i = processTextArea(oldByteArray, newBytes, i) - 1;

					continue;
				}
				else if (hasMarker(oldByteArray, i, _MARKER_JS_OPEN)) {
					i = processJavaScript(
							oldByteArray, newBytes, i, _MARKER_JS_OPEN) - 1;

					continue;
				}
				else if (hasMarker(oldByteArray, i, _MARKER_SCRIPT_OPEN)) {
					i = processJavaScript(
							oldByteArray, newBytes, i, _MARKER_SCRIPT_OPEN) - 1;

					continue;
				}
				else if (hasMarker(oldByteArray, i, _MARKER_STYLE_OPEN)) {
					i = processCSS(oldByteArray, newBytes, i) - 1;

					continue;
				}
			}
			else if (b == CharPool.GREATER_THAN) {
				newBytes.write(b);

				int spaceCount = countContinuousWhiteSpace(oldByteArray, i + 1);

				if (spaceCount > 0) {
					i = i + spaceCount;

					newBytes.write(CharPool.SPACE);
				}

				continue;
			}

			int spaceCount = countContinuousWhiteSpace(oldByteArray, i);

			if (spaceCount > 0) {
				newBytes.write(CharPool.SPACE);

				i = i + spaceCount - 1;
			}
			else {
				newBytes.write(b);
			}
		}

		return newBytes.toByteArray();
	}

	private static final byte[] _CDATA_CLOSE = "/*]]>*/".getBytes();

	private static final byte[] _CDATA_OPEN = "/*<![CDATA[*/".getBytes();

	private static final double _COMPRESSION_RATE = 0.7;

	private static final byte[] _MARKER_JS_OPEN =
		"script type=\"text/javascript\">".getBytes();

	private static final byte[] _MARKER_PRE_CLOSE = "/pre>".getBytes();

	private static final int[] _MARKER_PRE_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_PRE_CLOSE);

	private static final byte[] _MARKER_PRE_OPEN = "pre>".getBytes();

	private static final byte[] _MARKER_SCRIPT_CLOSE = "</script>".getBytes();

	private static final int[] _MARKER_SCRIPT_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_SCRIPT_CLOSE);

	private static final byte[] _MARKER_SCRIPT_OPEN = "script>".getBytes();

	private static final byte[] _MARKER_STYLE_CLOSE = "</style>".getBytes();

	private static final int[] _MARKER_STYLE_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_STYLE_CLOSE);

	private static final byte[] _MARKER_STYLE_OPEN =
		"style type=\"text/css\">".getBytes();

	private static final byte[] _MARKER_TEXTAREA_CLOSE =
		"/textarea>".getBytes();

	private static final int[] _MARKER_TEXTAREA_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_TEXTAREA_CLOSE);

	private static final byte[] _MARKER_TEXTAREA_OPEN =
		"textarea ".getBytes();

	private static final byte[] _SCRIPT_TYPE_JAVASCRIPT =
		"<script type=\"text/javascript\">".getBytes();

	private static final String _STRIP = "strip";

	private static final byte[] _STYLE_TYPE_CSS =
		"<style type=\"text/css\">".getBytes();

	private static Log _log = LogFactoryUtil.getLog(StripFilter.class);

	private ConcurrentLRUCache<String, String> _minifierCache =
		new ConcurrentLRUCache<String, String>(
			PropsValues.MINIFIER_INLINE_CONTENT_CACHE_SIZE);

}