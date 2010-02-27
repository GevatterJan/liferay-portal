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

package com.liferay.portal.service.persistence;

import com.liferay.portal.NoSuchResourceException;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.model.Resource;
import com.liferay.portal.service.persistence.BasePersistenceTestCase;

import java.util.List;

/**
 * <a href="ResourcePersistenceTest.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class ResourcePersistenceTest extends BasePersistenceTestCase {
	public void setUp() throws Exception {
		super.setUp();

		_persistence = (ResourcePersistence)PortalBeanLocatorUtil.locate(ResourcePersistence.class.getName());
	}

	public void testCreate() throws Exception {
		long pk = nextLong();

		Resource resource = _persistence.create(pk);

		assertNotNull(resource);

		assertEquals(resource.getPrimaryKey(), pk);
	}

	public void testRemove() throws Exception {
		Resource newResource = addResource();

		_persistence.remove(newResource);

		Resource existingResource = _persistence.fetchByPrimaryKey(newResource.getPrimaryKey());

		assertNull(existingResource);
	}

	public void testUpdateNew() throws Exception {
		addResource();
	}

	public void testUpdateExisting() throws Exception {
		long pk = nextLong();

		Resource newResource = _persistence.create(pk);

		newResource.setCodeId(nextLong());
		newResource.setPrimKey(randomString());

		_persistence.update(newResource, false);

		Resource existingResource = _persistence.findByPrimaryKey(newResource.getPrimaryKey());

		assertEquals(existingResource.getResourceId(),
			newResource.getResourceId());
		assertEquals(existingResource.getCodeId(), newResource.getCodeId());
		assertEquals(existingResource.getPrimKey(), newResource.getPrimKey());
	}

	public void testFindByPrimaryKeyExisting() throws Exception {
		Resource newResource = addResource();

		Resource existingResource = _persistence.findByPrimaryKey(newResource.getPrimaryKey());

		assertEquals(existingResource, newResource);
	}

	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			fail("Missing entity did not throw NoSuchResourceException");
		}
		catch (NoSuchResourceException nsee) {
		}
	}

	public void testFetchByPrimaryKeyExisting() throws Exception {
		Resource newResource = addResource();

		Resource existingResource = _persistence.fetchByPrimaryKey(newResource.getPrimaryKey());

		assertEquals(existingResource, newResource);
	}

	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = nextLong();

		Resource missingResource = _persistence.fetchByPrimaryKey(pk);

		assertNull(missingResource);
	}

	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		Resource newResource = addResource();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Resource.class,
				Resource.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("resourceId",
				newResource.getResourceId()));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		assertEquals(1, result.size());

		Resource existingResource = (Resource)result.get(0);

		assertEquals(existingResource, newResource);
	}

	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Resource.class,
				Resource.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("resourceId", nextLong()));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		assertEquals(0, result.size());
	}

	protected Resource addResource() throws Exception {
		long pk = nextLong();

		Resource resource = _persistence.create(pk);

		resource.setCodeId(nextLong());
		resource.setPrimKey(randomString());

		_persistence.update(resource, false);

		return resource;
	}

	private ResourcePersistence _persistence;
}