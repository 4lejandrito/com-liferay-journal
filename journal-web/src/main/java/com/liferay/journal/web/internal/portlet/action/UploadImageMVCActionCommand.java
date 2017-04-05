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

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.journal.configuration.JournalFileUploadsConfiguration;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.web.internal.upload.ImageJournalUploadFileEntryHandler;
import com.liferay.journal.web.internal.upload.ImageJournalUploadFileEntrySerializer;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.upload.UploadHandler;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Garcia
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalFileUploadsConfiguration",
	immediate = true,
	property = {
		"javax.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/upload_image"
	},
	service = MVCActionCommand.class
)
public class UploadImageMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_imageJournalUploadFileEntryHandler =
			new ImageJournalUploadFileEntryHandler(
				ConfigurableUtil.createConfigurable(
					JournalFileUploadsConfiguration.class, properties));
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_imageJournalUploadFileEntryHandler,
			_imageJournalUploadFileEntrySerializer, actionRequest,
			actionResponse);
	}

	private volatile ImageJournalUploadFileEntryHandler
		_imageJournalUploadFileEntryHandler;

	@Reference
	private ImageJournalUploadFileEntrySerializer
		_imageJournalUploadFileEntrySerializer;

	@Reference
	private UploadHandler _uploadHandler;

}