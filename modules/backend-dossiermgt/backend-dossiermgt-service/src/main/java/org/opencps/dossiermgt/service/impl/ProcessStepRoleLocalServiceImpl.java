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

package org.opencps.dossiermgt.service.impl;

import aQute.bnd.annotation.ProviderType;

import java.util.List;

import org.opencps.dossiermgt.model.ProcessStepRole;
import org.opencps.dossiermgt.service.base.ProcessStepRoleLocalServiceBaseImpl;

/**
 * The implementation of the process step role local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link org.opencps.dossiermgt.service.ProcessStepRoleLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author huymq
 * @see ProcessStepRoleLocalServiceBaseImpl
 * @see org.opencps.dossiermgt.service.ProcessStepRoleLocalServiceUtil
 */
@ProviderType
public class ProcessStepRoleLocalServiceImpl
	extends ProcessStepRoleLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link org.opencps.dossiermgt.service.ProcessStepRoleLocalServiceUtil} to access the process step role local service.
	 */
	
	public List<ProcessStepRole> findByP_S_ID(long processStepId) {
		return processStepRolePersistence.findByP_S_ID(processStepId);
	}
	
}