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

package com.liferay.portlet.messageboards.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;

import java.rmi.RemoteException;

/**
 * <a href="MBMessageServiceSoap.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This class provides a SOAP utility for the
 * {@link com.liferay.portlet.messageboards.service.MBMessageServiceUtil} service utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it is difficult for SOAP to
 * support certain types.
 * </p>
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a {@link java.util.List}, that
 * is translated to an array of {@link com.liferay.portlet.messageboards.model.MBMessageSoap}.
 * If the method in the service utility returns a
 * {@link com.liferay.portlet.messageboards.model.MBMessage}, that is translated to a
 * {@link com.liferay.portlet.messageboards.model.MBMessageSoap}. Methods that SOAP cannot
 * safely wire are skipped.
 * </p>
 *
 * <p>
 * The benefits of using the SOAP utility is that it is cross platform
 * compatible. SOAP allows different languages like Java, .NET, C++, PHP, and
 * even Perl, to call the generated services. One drawback of SOAP is that it is
 * slow because it needs to serialize all calls into a text format (XML).
 * </p>
 *
 * <p>
 * You can see a list of services at
 * http://localhost:8080/tunnel-web/secure/axis. Set the property
 * <b>tunnel.servlet.hosts.allowed</b> in portal.properties to configure
 * security.
 * </p>
 *
 * <p>
 * The SOAP utility is only generated for remote services.
 * </p>
 *
 * @author    Brian Wing Shun Chan
 * @see       MBMessageServiceHttp
 * @see       com.liferay.portlet.messageboards.model.MBMessageSoap
 * @see       com.liferay.portlet.messageboards.service.MBMessageServiceUtil
 * @generated
 */
public class MBMessageServiceSoap {
	public static com.liferay.portlet.messageboards.model.MBMessageSoap addDiscussionMessage(
		java.lang.String className, long classPK, long threadId,
		long parentMessageId, java.lang.String subject, java.lang.String body,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.portlet.messageboards.model.MBMessage returnValue = MBMessageServiceUtil.addDiscussionMessage(className,
					classPK, threadId, parentMessageId, subject, body,
					serviceContext);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.messageboards.model.MBMessageSoap addMessage(
		long groupId, long categoryId, java.lang.String subject,
		java.lang.String body,
		java.util.List<com.liferay.portal.kernel.util.ObjectValuePair<String, byte[]>> files,
		boolean anonymous, double priority,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.portlet.messageboards.model.MBMessage returnValue = MBMessageServiceUtil.addMessage(groupId,
					categoryId, subject, body, files, anonymous, priority,
					serviceContext);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.messageboards.model.MBMessageSoap addMessage(
		long groupId, long categoryId, long threadId, long parentMessageId,
		java.lang.String subject, java.lang.String body,
		java.util.List<com.liferay.portal.kernel.util.ObjectValuePair<String, byte[]>> files,
		boolean anonymous, double priority,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.portlet.messageboards.model.MBMessage returnValue = MBMessageServiceUtil.addMessage(groupId,
					categoryId, threadId, parentMessageId, subject, body,
					files, anonymous, priority, serviceContext);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static void deleteDiscussionMessage(long groupId,
		java.lang.String className, long classPK, long messageId)
		throws RemoteException {
		try {
			MBMessageServiceUtil.deleteDiscussionMessage(groupId, className,
				classPK, messageId);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static void deleteMessage(long messageId) throws RemoteException {
		try {
			MBMessageServiceUtil.deleteMessage(messageId);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.messageboards.model.MBMessageSoap[] getCategoryMessages(
		long groupId, long categoryId, int status, int start, int end)
		throws RemoteException {
		try {
			java.util.List<com.liferay.portlet.messageboards.model.MBMessage> returnValue =
				MBMessageServiceUtil.getCategoryMessages(groupId, categoryId,
					status, start, end);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModels(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static int getCategoryMessagesCount(long groupId, long categoryId,
		int status) throws RemoteException {
		try {
			int returnValue = MBMessageServiceUtil.getCategoryMessagesCount(groupId,
					categoryId, status);

			return returnValue;
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.messageboards.model.MBMessageSoap getMessage(
		long messageId) throws RemoteException {
		try {
			com.liferay.portlet.messageboards.model.MBMessage returnValue = MBMessageServiceUtil.getMessage(messageId);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static void subscribeMessage(long messageId)
		throws RemoteException {
		try {
			MBMessageServiceUtil.subscribeMessage(messageId);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static void unsubscribeMessage(long messageId)
		throws RemoteException {
		try {
			MBMessageServiceUtil.unsubscribeMessage(messageId);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.messageboards.model.MBMessageSoap updateDiscussionMessage(
		java.lang.String className, long classPK, long messageId,
		java.lang.String subject, java.lang.String body,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.portlet.messageboards.model.MBMessage returnValue = MBMessageServiceUtil.updateDiscussionMessage(className,
					classPK, messageId, subject, body, serviceContext);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	public static com.liferay.portlet.messageboards.model.MBMessageSoap updateMessage(
		long messageId, java.lang.String subject, java.lang.String body,
		java.util.List<com.liferay.portal.kernel.util.ObjectValuePair<String, byte[]>> files,
		java.util.List<String> existingFiles, double priority,
		com.liferay.portal.service.ServiceContext serviceContext)
		throws RemoteException {
		try {
			com.liferay.portlet.messageboards.model.MBMessage returnValue = MBMessageServiceUtil.updateMessage(messageId,
					subject, body, files, existingFiles, priority,
					serviceContext);

			return com.liferay.portlet.messageboards.model.MBMessageSoap.toSoapModel(returnValue);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new RemoteException(e.getMessage());
		}
	}

	private static Log _log = LogFactoryUtil.getLog(MBMessageServiceSoap.class);
}