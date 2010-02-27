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

package com.liferay.portal.upgrade.v4_3_0.util;

import java.sql.Types;

/**
 * <a href="DLFileRankTable.java.html"><b><i>View Source</i></b></a>
 *
 * @author	  Brian Wing Shun Chan
 * @generated
 */
public class DLFileRankTable {

	public static String TABLE_NAME = "DLFileRank";

	public static Object[][] TABLE_COLUMNS = {
		{"fileRankId", new Integer(Types.BIGINT)},
		{"companyId", new Integer(Types.BIGINT)},
		{"userId", new Integer(Types.BIGINT)},
		{"createDate", new Integer(Types.TIMESTAMP)},
		{"folderId", new Integer(Types.BIGINT)},
		{"name", new Integer(Types.VARCHAR)}
	};

	public static String TABLE_SQL_CREATE = "create table DLFileRank (fileRankId LONG not null primary key,companyId LONG,userId LONG,createDate DATE null,folderId LONG,name VARCHAR(300) null)";

	public static String TABLE_SQL_DROP = "drop table DLFileRank";

}