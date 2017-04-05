/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.journal.web.internal.upload;

import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.upload.DefaultUploadFileEntrySerializer;
import com.liferay.upload.UploadFileEntrySerializer;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Garcia
 */
@Component(service = ImageJournalUploadFileEntrySerializer.class)
public class ImageJournalUploadFileEntrySerializer
	implements UploadFileEntrySerializer {

	@Override
	public JSONObject serialize(PortalException pe) throws PortalException {
		if (pe instanceof ImageTypeException) {
			JSONObject errorJSONObject = JSONFactoryUtil.createJSONObject();

			errorJSONObject.put(
				"errorType",
				ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION);
			errorJSONObject.put("message", StringPool.BLANK);

			return errorJSONObject;
		}
		else {
			return _defaultUploadFileEntrySerializer.serialize(pe);
		}
	}

	@Override
	public JSONObject serialize(
			PortletRequest portletRequest, FileEntry fileEntry)
		throws PortalException {

		JSONObject jsonObject = _defaultUploadFileEntrySerializer.serialize(
			portletRequest, fileEntry);

		jsonObject.put("type", "journal");

		return jsonObject;
	}

	@Reference
	private DefaultUploadFileEntrySerializer _defaultUploadFileEntrySerializer;

}