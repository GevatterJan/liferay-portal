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

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Order;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnmodifiableList;
import com.liferay.portal.spring.hibernate.SessionInvocationHandler;

import java.lang.reflect.Proxy;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;

/**
 * <a href="DynamicQueryImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class DynamicQueryImpl implements DynamicQuery {

	public DynamicQueryImpl(DetachedCriteria detachedCriteria) {
		_detachedCriteria = detachedCriteria;
	}

	public DynamicQuery add(Criterion criterion) {
		CriterionImpl criterionImpl = (CriterionImpl)criterion;

		_detachedCriteria.add(criterionImpl.getWrappedCriterion());

		return this;
	}

	public DynamicQuery addOrder(Order order) {
		OrderImpl orderImpl = (OrderImpl)order;

		_detachedCriteria.addOrder(orderImpl.getWrappedOrder());

		return this;
	}

	public void compile(Session session) {
		org.hibernate.Session hibernateSession =
			(org.hibernate.Session)session.getWrappedSession();

		SessionInvocationHandler sessionInvocationHandler =
			(SessionInvocationHandler)Proxy.getInvocationHandler(
				hibernateSession);

		hibernateSession = sessionInvocationHandler.getSession();

		_criteria = _detachedCriteria.getExecutableCriteria(hibernateSession);

		if ((_start != null) && (_end != null)) {
			_criteria = _criteria.setFirstResult(_start.intValue());
			_criteria = _criteria.setMaxResults(
				_end.intValue() - _start.intValue());
		}
	}

	public DetachedCriteria getDetachedCriteria() {
		return _detachedCriteria;
	}

	@SuppressWarnings("unchecked")
	public List list() {
		return list(true);
	}

	@SuppressWarnings("unchecked")
	public List list(boolean unmodifiable) {
		List list = _criteria.list();

		if (unmodifiable) {
			return new UnmodifiableList(list);
		}
		else {
			return ListUtil.copy(list);
		}
	}

	public void setLimit(int start, int end) {
		_start = Integer.valueOf(start);
		_end = Integer.valueOf(end);
	}

	public DynamicQuery setProjection(Projection projection) {
		ProjectionImpl projectionImpl = (ProjectionImpl)projection;

		_detachedCriteria.setProjection(projectionImpl.getWrappedProjection());

		return this;
	}

	private DetachedCriteria _detachedCriteria;
	private Criteria _criteria;
	private Integer _start;
	private Integer _end;

}