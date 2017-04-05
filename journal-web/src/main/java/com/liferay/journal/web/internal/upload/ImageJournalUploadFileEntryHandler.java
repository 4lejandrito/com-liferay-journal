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

import static com.liferay.exportimport.kernel.lar.ExportImportHelper.TEMP_FOLDER_NAME;

import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.journal.configuration.JournalFileUploadsConfiguration;
import com.liferay.journal.service.permission.JournalPermission;
import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourcePermissionCheckerUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.upload.UploadFileEntryHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eduardo Garcia
 */
public class ImageJournalUploadFileEntryHandler
	implements UploadFileEntryHandler {

	public ImageJournalUploadFileEntryHandler(
		JournalFileUploadsConfiguration journalFileUploadsConfiguration) {

		_journalFileUploadsConfiguration = journalFileUploadsConfiguration;
	}

	@Override
	public FileEntry upload(UploadPortletRequest uploadPortletRequest)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)uploadPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String fileName = uploadPortletRequest.getFileName(_PARAMETER_NAME);
		String contentType = uploadPortletRequest.getContentType(
			_PARAMETER_NAME);
		long size = uploadPortletRequest.getSize(_PARAMETER_NAME);

		long userId = themeDisplay.getUserId();
		long groupId = themeDisplay.getScopeGroupId();

		_checkPermission(groupId, themeDisplay.getPermissionChecker());

		_validateFile(fileName, size);

		try (InputStream inputStream =
				uploadPortletRequest.getFileAsStream(_PARAMETER_NAME)) {

			String uniqueFileName = TempFileEntryUtil.getTempFileName(fileName);

			return TempFileEntryUtil.addTempFileEntry(
				groupId, userId, TEMP_FOLDER_NAME, uniqueFileName, inputStream,
				contentType);
		}
	}

	private void _checkPermission(
			long groupId, PermissionChecker permissionChecker)
		throws PortalException {

		boolean containsResourcePermission =
			ResourcePermissionCheckerUtil.containsResourcePermission(
				permissionChecker, JournalPermission.RESOURCE_NAME, groupId,
				ActionKeys.ADD_ARTICLE);

		if (!containsResourcePermission) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, JournalPermission.RESOURCE_NAME, groupId,
				ActionKeys.ADD_ARTICLE);
		}
	}

	private void _validateFile(String fileName, long size)
		throws PortalException {

		long maxSize = PrefsPropsUtil.getLong(PropsKeys.DL_FILE_MAX_SIZE);

		if ((maxSize > 0) && (size > maxSize)) {
			throw new FileSizeException(
				size + " exceeds its maximum permitted size of " + maxSize);
		}

		String extension = FileUtil.getExtension(fileName);

		for (String imageExtension :
				_journalFileUploadsConfiguration.imageExtensions()) {

			if (StringPool.STAR.equals(imageExtension) ||
				imageExtension.equals(StringPool.PERIOD + extension)) {

				return;
			}
		}

		throw new ImageTypeException(
			"Invalid image type for file name " + fileName);
	}

	private static final String _PARAMETER_NAME = "imageSelectorFileName";

	private final JournalFileUploadsConfiguration
		_journalFileUploadsConfiguration;

}