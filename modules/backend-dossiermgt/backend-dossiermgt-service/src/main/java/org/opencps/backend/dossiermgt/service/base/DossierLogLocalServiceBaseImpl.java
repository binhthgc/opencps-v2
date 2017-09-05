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

package org.opencps.backend.dossiermgt.service.base;

import aQute.bnd.annotation.ProviderType;

import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import org.opencps.backend.dossiermgt.model.DossierLog;
import org.opencps.backend.dossiermgt.service.DossierLogLocalService;
import org.opencps.backend.dossiermgt.service.persistence.DossierFilePersistence;
import org.opencps.backend.dossiermgt.service.persistence.DossierLogPersistence;
import org.opencps.backend.dossiermgt.service.persistence.DossierPartPersistence;
import org.opencps.backend.dossiermgt.service.persistence.DossierPersistence;
import org.opencps.backend.dossiermgt.service.persistence.DossierTemplatePersistence;
import org.opencps.backend.dossiermgt.service.persistence.FileTemplatePersistence;
import org.opencps.backend.dossiermgt.service.persistence.PaymentConfigPersistence;
import org.opencps.backend.dossiermgt.service.persistence.PaymentFilePersistence;
import org.opencps.backend.dossiermgt.service.persistence.ServiceConfigPersistence;
import org.opencps.backend.dossiermgt.service.persistence.ServiceInfoPersistence;
import org.opencps.backend.dossiermgt.service.persistence.ServiceOptionPersistence;

import java.io.Serializable;

import java.util.List;

import javax.sql.DataSource;

/**
 * Provides the base implementation for the dossier log local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link org.opencps.backend.dossiermgt.service.impl.DossierLogLocalServiceImpl}.
 * </p>
 *
 * @author huymq
 * @see org.opencps.backend.dossiermgt.service.impl.DossierLogLocalServiceImpl
 * @see org.opencps.backend.dossiermgt.service.DossierLogLocalServiceUtil
 * @generated
 */
@ProviderType
public abstract class DossierLogLocalServiceBaseImpl
	extends BaseLocalServiceImpl implements DossierLogLocalService,
		IdentifiableOSGiService {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link org.opencps.backend.dossiermgt.service.DossierLogLocalServiceUtil} to access the dossier log local service.
	 */

	/**
	 * Adds the dossier log to the database. Also notifies the appropriate model listeners.
	 *
	 * @param dossierLog the dossier log
	 * @return the dossier log that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DossierLog addDossierLog(DossierLog dossierLog) {
		dossierLog.setNew(true);

		return dossierLogPersistence.update(dossierLog);
	}

	/**
	 * Creates a new dossier log with the primary key. Does not add the dossier log to the database.
	 *
	 * @param dossierLogId the primary key for the new dossier log
	 * @return the new dossier log
	 */
	@Override
	public DossierLog createDossierLog(long dossierLogId) {
		return dossierLogPersistence.create(dossierLogId);
	}

	/**
	 * Deletes the dossier log with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dossierLogId the primary key of the dossier log
	 * @return the dossier log that was removed
	 * @throws PortalException if a dossier log with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public DossierLog deleteDossierLog(long dossierLogId)
		throws PortalException {
		return dossierLogPersistence.remove(dossierLogId);
	}

	/**
	 * Deletes the dossier log from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dossierLog the dossier log
	 * @return the dossier log that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public DossierLog deleteDossierLog(DossierLog dossierLog) {
		return dossierLogPersistence.remove(dossierLog);
	}

	@Override
	public DynamicQuery dynamicQuery() {
		Class<?> clazz = getClass();

		return DynamicQueryFactoryUtil.forClass(DossierLog.class,
			clazz.getClassLoader());
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return dossierLogPersistence.findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.opencps.backend.dossiermgt.model.impl.DossierLogModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery, int start,
		int end) {
		return dossierLogPersistence.findWithDynamicQuery(dynamicQuery, start,
			end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.opencps.backend.dossiermgt.model.impl.DossierLogModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery, int start,
		int end, OrderByComparator<T> orderByComparator) {
		return dossierLogPersistence.findWithDynamicQuery(dynamicQuery, start,
			end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return dossierLogPersistence.countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery,
		Projection projection) {
		return dossierLogPersistence.countWithDynamicQuery(dynamicQuery,
			projection);
	}

	@Override
	public DossierLog fetchDossierLog(long dossierLogId) {
		return dossierLogPersistence.fetchByPrimaryKey(dossierLogId);
	}

	/**
	 * Returns the dossier log matching the UUID and group.
	 *
	 * @param uuid the dossier log's UUID
	 * @param groupId the primary key of the group
	 * @return the matching dossier log, or <code>null</code> if a matching dossier log could not be found
	 */
	@Override
	public DossierLog fetchDossierLogByUuidAndGroupId(String uuid, long groupId) {
		return dossierLogPersistence.fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the dossier log with the primary key.
	 *
	 * @param dossierLogId the primary key of the dossier log
	 * @return the dossier log
	 * @throws PortalException if a dossier log with the primary key could not be found
	 */
	@Override
	public DossierLog getDossierLog(long dossierLogId)
		throws PortalException {
		return dossierLogPersistence.findByPrimaryKey(dossierLogId);
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery = new DefaultActionableDynamicQuery();

		actionableDynamicQuery.setBaseLocalService(dossierLogLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(DossierLog.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("dossierLogId");

		return actionableDynamicQuery;
	}

	@Override
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery = new IndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setBaseLocalService(dossierLogLocalService);
		indexableActionableDynamicQuery.setClassLoader(getClassLoader());
		indexableActionableDynamicQuery.setModelClass(DossierLog.class);

		indexableActionableDynamicQuery.setPrimaryKeyPropertyName(
			"dossierLogId");

		return indexableActionableDynamicQuery;
	}

	protected void initActionableDynamicQuery(
		ActionableDynamicQuery actionableDynamicQuery) {
		actionableDynamicQuery.setBaseLocalService(dossierLogLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(DossierLog.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("dossierLogId");
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		final PortletDataContext portletDataContext) {
		final ExportActionableDynamicQuery exportActionableDynamicQuery = new ExportActionableDynamicQuery() {
				@Override
				public long performCount() throws PortalException {
					ManifestSummary manifestSummary = portletDataContext.getManifestSummary();

					StagedModelType stagedModelType = getStagedModelType();

					long modelAdditionCount = super.performCount();

					manifestSummary.addModelAdditionCount(stagedModelType,
						modelAdditionCount);

					long modelDeletionCount = ExportImportHelperUtil.getModelDeletionCount(portletDataContext,
							stagedModelType);

					manifestSummary.addModelDeletionCount(stagedModelType,
						modelDeletionCount);

					return modelAdditionCount;
				}
			};

		initActionableDynamicQuery(exportActionableDynamicQuery);

		exportActionableDynamicQuery.setAddCriteriaMethod(new ActionableDynamicQuery.AddCriteriaMethod() {
				@Override
				public void addCriteria(DynamicQuery dynamicQuery) {
					portletDataContext.addDateRangeCriteria(dynamicQuery,
						"modifiedDate");
				}
			});

		exportActionableDynamicQuery.setCompanyId(portletDataContext.getCompanyId());

		exportActionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<DossierLog>() {
				@Override
				public void performAction(DossierLog dossierLog)
					throws PortalException {
					StagedModelDataHandlerUtil.exportStagedModel(portletDataContext,
						dossierLog);
				}
			});
		exportActionableDynamicQuery.setStagedModelType(new StagedModelType(
				PortalUtil.getClassNameId(DossierLog.class.getName())));

		return exportActionableDynamicQuery;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {
		return dossierLogLocalService.deleteDossierLog((DossierLog)persistedModel);
	}

	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {
		return dossierLogPersistence.findByPrimaryKey(primaryKeyObj);
	}

	/**
	 * Returns all the dossier logs matching the UUID and company.
	 *
	 * @param uuid the UUID of the dossier logs
	 * @param companyId the primary key of the company
	 * @return the matching dossier logs, or an empty list if no matches were found
	 */
	@Override
	public List<DossierLog> getDossierLogsByUuidAndCompanyId(String uuid,
		long companyId) {
		return dossierLogPersistence.findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of dossier logs matching the UUID and company.
	 *
	 * @param uuid the UUID of the dossier logs
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of dossier logs
	 * @param end the upper bound of the range of dossier logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching dossier logs, or an empty list if no matches were found
	 */
	@Override
	public List<DossierLog> getDossierLogsByUuidAndCompanyId(String uuid,
		long companyId, int start, int end,
		OrderByComparator<DossierLog> orderByComparator) {
		return dossierLogPersistence.findByUuid_C(uuid, companyId, start, end,
			orderByComparator);
	}

	/**
	 * Returns the dossier log matching the UUID and group.
	 *
	 * @param uuid the dossier log's UUID
	 * @param groupId the primary key of the group
	 * @return the matching dossier log
	 * @throws PortalException if a matching dossier log could not be found
	 */
	@Override
	public DossierLog getDossierLogByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {
		return dossierLogPersistence.findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns a range of all the dossier logs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link org.opencps.backend.dossiermgt.model.impl.DossierLogModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of dossier logs
	 * @param end the upper bound of the range of dossier logs (not inclusive)
	 * @return the range of dossier logs
	 */
	@Override
	public List<DossierLog> getDossierLogs(int start, int end) {
		return dossierLogPersistence.findAll(start, end);
	}

	/**
	 * Returns the number of dossier logs.
	 *
	 * @return the number of dossier logs
	 */
	@Override
	public int getDossierLogsCount() {
		return dossierLogPersistence.countAll();
	}

	/**
	 * Updates the dossier log in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param dossierLog the dossier log
	 * @return the dossier log that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public DossierLog updateDossierLog(DossierLog dossierLog) {
		return dossierLogPersistence.update(dossierLog);
	}

	/**
	 * Returns the dossier local service.
	 *
	 * @return the dossier local service
	 */
	public org.opencps.backend.dossiermgt.service.DossierLocalService getDossierLocalService() {
		return dossierLocalService;
	}

	/**
	 * Sets the dossier local service.
	 *
	 * @param dossierLocalService the dossier local service
	 */
	public void setDossierLocalService(
		org.opencps.backend.dossiermgt.service.DossierLocalService dossierLocalService) {
		this.dossierLocalService = dossierLocalService;
	}

	/**
	 * Returns the dossier persistence.
	 *
	 * @return the dossier persistence
	 */
	public DossierPersistence getDossierPersistence() {
		return dossierPersistence;
	}

	/**
	 * Sets the dossier persistence.
	 *
	 * @param dossierPersistence the dossier persistence
	 */
	public void setDossierPersistence(DossierPersistence dossierPersistence) {
		this.dossierPersistence = dossierPersistence;
	}

	/**
	 * Returns the dossier file local service.
	 *
	 * @return the dossier file local service
	 */
	public org.opencps.backend.dossiermgt.service.DossierFileLocalService getDossierFileLocalService() {
		return dossierFileLocalService;
	}

	/**
	 * Sets the dossier file local service.
	 *
	 * @param dossierFileLocalService the dossier file local service
	 */
	public void setDossierFileLocalService(
		org.opencps.backend.dossiermgt.service.DossierFileLocalService dossierFileLocalService) {
		this.dossierFileLocalService = dossierFileLocalService;
	}

	/**
	 * Returns the dossier file persistence.
	 *
	 * @return the dossier file persistence
	 */
	public DossierFilePersistence getDossierFilePersistence() {
		return dossierFilePersistence;
	}

	/**
	 * Sets the dossier file persistence.
	 *
	 * @param dossierFilePersistence the dossier file persistence
	 */
	public void setDossierFilePersistence(
		DossierFilePersistence dossierFilePersistence) {
		this.dossierFilePersistence = dossierFilePersistence;
	}

	/**
	 * Returns the dossier log local service.
	 *
	 * @return the dossier log local service
	 */
	public DossierLogLocalService getDossierLogLocalService() {
		return dossierLogLocalService;
	}

	/**
	 * Sets the dossier log local service.
	 *
	 * @param dossierLogLocalService the dossier log local service
	 */
	public void setDossierLogLocalService(
		DossierLogLocalService dossierLogLocalService) {
		this.dossierLogLocalService = dossierLogLocalService;
	}

	/**
	 * Returns the dossier log persistence.
	 *
	 * @return the dossier log persistence
	 */
	public DossierLogPersistence getDossierLogPersistence() {
		return dossierLogPersistence;
	}

	/**
	 * Sets the dossier log persistence.
	 *
	 * @param dossierLogPersistence the dossier log persistence
	 */
	public void setDossierLogPersistence(
		DossierLogPersistence dossierLogPersistence) {
		this.dossierLogPersistence = dossierLogPersistence;
	}

	/**
	 * Returns the dossier part local service.
	 *
	 * @return the dossier part local service
	 */
	public org.opencps.backend.dossiermgt.service.DossierPartLocalService getDossierPartLocalService() {
		return dossierPartLocalService;
	}

	/**
	 * Sets the dossier part local service.
	 *
	 * @param dossierPartLocalService the dossier part local service
	 */
	public void setDossierPartLocalService(
		org.opencps.backend.dossiermgt.service.DossierPartLocalService dossierPartLocalService) {
		this.dossierPartLocalService = dossierPartLocalService;
	}

	/**
	 * Returns the dossier part persistence.
	 *
	 * @return the dossier part persistence
	 */
	public DossierPartPersistence getDossierPartPersistence() {
		return dossierPartPersistence;
	}

	/**
	 * Sets the dossier part persistence.
	 *
	 * @param dossierPartPersistence the dossier part persistence
	 */
	public void setDossierPartPersistence(
		DossierPartPersistence dossierPartPersistence) {
		this.dossierPartPersistence = dossierPartPersistence;
	}

	/**
	 * Returns the dossier template local service.
	 *
	 * @return the dossier template local service
	 */
	public org.opencps.backend.dossiermgt.service.DossierTemplateLocalService getDossierTemplateLocalService() {
		return dossierTemplateLocalService;
	}

	/**
	 * Sets the dossier template local service.
	 *
	 * @param dossierTemplateLocalService the dossier template local service
	 */
	public void setDossierTemplateLocalService(
		org.opencps.backend.dossiermgt.service.DossierTemplateLocalService dossierTemplateLocalService) {
		this.dossierTemplateLocalService = dossierTemplateLocalService;
	}

	/**
	 * Returns the dossier template persistence.
	 *
	 * @return the dossier template persistence
	 */
	public DossierTemplatePersistence getDossierTemplatePersistence() {
		return dossierTemplatePersistence;
	}

	/**
	 * Sets the dossier template persistence.
	 *
	 * @param dossierTemplatePersistence the dossier template persistence
	 */
	public void setDossierTemplatePersistence(
		DossierTemplatePersistence dossierTemplatePersistence) {
		this.dossierTemplatePersistence = dossierTemplatePersistence;
	}

	/**
	 * Returns the file template local service.
	 *
	 * @return the file template local service
	 */
	public org.opencps.backend.dossiermgt.service.FileTemplateLocalService getFileTemplateLocalService() {
		return fileTemplateLocalService;
	}

	/**
	 * Sets the file template local service.
	 *
	 * @param fileTemplateLocalService the file template local service
	 */
	public void setFileTemplateLocalService(
		org.opencps.backend.dossiermgt.service.FileTemplateLocalService fileTemplateLocalService) {
		this.fileTemplateLocalService = fileTemplateLocalService;
	}

	/**
	 * Returns the file template persistence.
	 *
	 * @return the file template persistence
	 */
	public FileTemplatePersistence getFileTemplatePersistence() {
		return fileTemplatePersistence;
	}

	/**
	 * Sets the file template persistence.
	 *
	 * @param fileTemplatePersistence the file template persistence
	 */
	public void setFileTemplatePersistence(
		FileTemplatePersistence fileTemplatePersistence) {
		this.fileTemplatePersistence = fileTemplatePersistence;
	}

	/**
	 * Returns the payment config local service.
	 *
	 * @return the payment config local service
	 */
	public org.opencps.backend.dossiermgt.service.PaymentConfigLocalService getPaymentConfigLocalService() {
		return paymentConfigLocalService;
	}

	/**
	 * Sets the payment config local service.
	 *
	 * @param paymentConfigLocalService the payment config local service
	 */
	public void setPaymentConfigLocalService(
		org.opencps.backend.dossiermgt.service.PaymentConfigLocalService paymentConfigLocalService) {
		this.paymentConfigLocalService = paymentConfigLocalService;
	}

	/**
	 * Returns the payment config persistence.
	 *
	 * @return the payment config persistence
	 */
	public PaymentConfigPersistence getPaymentConfigPersistence() {
		return paymentConfigPersistence;
	}

	/**
	 * Sets the payment config persistence.
	 *
	 * @param paymentConfigPersistence the payment config persistence
	 */
	public void setPaymentConfigPersistence(
		PaymentConfigPersistence paymentConfigPersistence) {
		this.paymentConfigPersistence = paymentConfigPersistence;
	}

	/**
	 * Returns the payment file local service.
	 *
	 * @return the payment file local service
	 */
	public org.opencps.backend.dossiermgt.service.PaymentFileLocalService getPaymentFileLocalService() {
		return paymentFileLocalService;
	}

	/**
	 * Sets the payment file local service.
	 *
	 * @param paymentFileLocalService the payment file local service
	 */
	public void setPaymentFileLocalService(
		org.opencps.backend.dossiermgt.service.PaymentFileLocalService paymentFileLocalService) {
		this.paymentFileLocalService = paymentFileLocalService;
	}

	/**
	 * Returns the payment file persistence.
	 *
	 * @return the payment file persistence
	 */
	public PaymentFilePersistence getPaymentFilePersistence() {
		return paymentFilePersistence;
	}

	/**
	 * Sets the payment file persistence.
	 *
	 * @param paymentFilePersistence the payment file persistence
	 */
	public void setPaymentFilePersistence(
		PaymentFilePersistence paymentFilePersistence) {
		this.paymentFilePersistence = paymentFilePersistence;
	}

	/**
	 * Returns the service config local service.
	 *
	 * @return the service config local service
	 */
	public org.opencps.backend.dossiermgt.service.ServiceConfigLocalService getServiceConfigLocalService() {
		return serviceConfigLocalService;
	}

	/**
	 * Sets the service config local service.
	 *
	 * @param serviceConfigLocalService the service config local service
	 */
	public void setServiceConfigLocalService(
		org.opencps.backend.dossiermgt.service.ServiceConfigLocalService serviceConfigLocalService) {
		this.serviceConfigLocalService = serviceConfigLocalService;
	}

	/**
	 * Returns the service config persistence.
	 *
	 * @return the service config persistence
	 */
	public ServiceConfigPersistence getServiceConfigPersistence() {
		return serviceConfigPersistence;
	}

	/**
	 * Sets the service config persistence.
	 *
	 * @param serviceConfigPersistence the service config persistence
	 */
	public void setServiceConfigPersistence(
		ServiceConfigPersistence serviceConfigPersistence) {
		this.serviceConfigPersistence = serviceConfigPersistence;
	}

	/**
	 * Returns the service info local service.
	 *
	 * @return the service info local service
	 */
	public org.opencps.backend.dossiermgt.service.ServiceInfoLocalService getServiceInfoLocalService() {
		return serviceInfoLocalService;
	}

	/**
	 * Sets the service info local service.
	 *
	 * @param serviceInfoLocalService the service info local service
	 */
	public void setServiceInfoLocalService(
		org.opencps.backend.dossiermgt.service.ServiceInfoLocalService serviceInfoLocalService) {
		this.serviceInfoLocalService = serviceInfoLocalService;
	}

	/**
	 * Returns the service info persistence.
	 *
	 * @return the service info persistence
	 */
	public ServiceInfoPersistence getServiceInfoPersistence() {
		return serviceInfoPersistence;
	}

	/**
	 * Sets the service info persistence.
	 *
	 * @param serviceInfoPersistence the service info persistence
	 */
	public void setServiceInfoPersistence(
		ServiceInfoPersistence serviceInfoPersistence) {
		this.serviceInfoPersistence = serviceInfoPersistence;
	}

	/**
	 * Returns the service option local service.
	 *
	 * @return the service option local service
	 */
	public org.opencps.backend.dossiermgt.service.ServiceOptionLocalService getServiceOptionLocalService() {
		return serviceOptionLocalService;
	}

	/**
	 * Sets the service option local service.
	 *
	 * @param serviceOptionLocalService the service option local service
	 */
	public void setServiceOptionLocalService(
		org.opencps.backend.dossiermgt.service.ServiceOptionLocalService serviceOptionLocalService) {
		this.serviceOptionLocalService = serviceOptionLocalService;
	}

	/**
	 * Returns the service option persistence.
	 *
	 * @return the service option persistence
	 */
	public ServiceOptionPersistence getServiceOptionPersistence() {
		return serviceOptionPersistence;
	}

	/**
	 * Sets the service option persistence.
	 *
	 * @param serviceOptionPersistence the service option persistence
	 */
	public void setServiceOptionPersistence(
		ServiceOptionPersistence serviceOptionPersistence) {
		this.serviceOptionPersistence = serviceOptionPersistence;
	}

	/**
	 * Returns the counter local service.
	 *
	 * @return the counter local service
	 */
	public com.liferay.counter.kernel.service.CounterLocalService getCounterLocalService() {
		return counterLocalService;
	}

	/**
	 * Sets the counter local service.
	 *
	 * @param counterLocalService the counter local service
	 */
	public void setCounterLocalService(
		com.liferay.counter.kernel.service.CounterLocalService counterLocalService) {
		this.counterLocalService = counterLocalService;
	}

	/**
	 * Returns the class name local service.
	 *
	 * @return the class name local service
	 */
	public com.liferay.portal.kernel.service.ClassNameLocalService getClassNameLocalService() {
		return classNameLocalService;
	}

	/**
	 * Sets the class name local service.
	 *
	 * @param classNameLocalService the class name local service
	 */
	public void setClassNameLocalService(
		com.liferay.portal.kernel.service.ClassNameLocalService classNameLocalService) {
		this.classNameLocalService = classNameLocalService;
	}

	/**
	 * Returns the class name persistence.
	 *
	 * @return the class name persistence
	 */
	public ClassNamePersistence getClassNamePersistence() {
		return classNamePersistence;
	}

	/**
	 * Sets the class name persistence.
	 *
	 * @param classNamePersistence the class name persistence
	 */
	public void setClassNamePersistence(
		ClassNamePersistence classNamePersistence) {
		this.classNamePersistence = classNamePersistence;
	}

	/**
	 * Returns the resource local service.
	 *
	 * @return the resource local service
	 */
	public com.liferay.portal.kernel.service.ResourceLocalService getResourceLocalService() {
		return resourceLocalService;
	}

	/**
	 * Sets the resource local service.
	 *
	 * @param resourceLocalService the resource local service
	 */
	public void setResourceLocalService(
		com.liferay.portal.kernel.service.ResourceLocalService resourceLocalService) {
		this.resourceLocalService = resourceLocalService;
	}

	/**
	 * Returns the user local service.
	 *
	 * @return the user local service
	 */
	public com.liferay.portal.kernel.service.UserLocalService getUserLocalService() {
		return userLocalService;
	}

	/**
	 * Sets the user local service.
	 *
	 * @param userLocalService the user local service
	 */
	public void setUserLocalService(
		com.liferay.portal.kernel.service.UserLocalService userLocalService) {
		this.userLocalService = userLocalService;
	}

	/**
	 * Returns the user persistence.
	 *
	 * @return the user persistence
	 */
	public UserPersistence getUserPersistence() {
		return userPersistence;
	}

	/**
	 * Sets the user persistence.
	 *
	 * @param userPersistence the user persistence
	 */
	public void setUserPersistence(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	public void afterPropertiesSet() {
		persistedModelLocalServiceRegistry.register("org.opencps.backend.dossiermgt.model.DossierLog",
			dossierLogLocalService);
	}

	public void destroy() {
		persistedModelLocalServiceRegistry.unregister(
			"org.opencps.backend.dossiermgt.model.DossierLog");
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return DossierLogLocalService.class.getName();
	}

	protected Class<?> getModelClass() {
		return DossierLog.class;
	}

	protected String getModelClassName() {
		return DossierLog.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = dossierLogPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(dataSource,
					sql);

			sqlUpdate.update();
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@BeanReference(type = org.opencps.backend.dossiermgt.service.DossierLocalService.class)
	protected org.opencps.backend.dossiermgt.service.DossierLocalService dossierLocalService;
	@BeanReference(type = DossierPersistence.class)
	protected DossierPersistence dossierPersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.DossierFileLocalService.class)
	protected org.opencps.backend.dossiermgt.service.DossierFileLocalService dossierFileLocalService;
	@BeanReference(type = DossierFilePersistence.class)
	protected DossierFilePersistence dossierFilePersistence;
	@BeanReference(type = DossierLogLocalService.class)
	protected DossierLogLocalService dossierLogLocalService;
	@BeanReference(type = DossierLogPersistence.class)
	protected DossierLogPersistence dossierLogPersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.DossierPartLocalService.class)
	protected org.opencps.backend.dossiermgt.service.DossierPartLocalService dossierPartLocalService;
	@BeanReference(type = DossierPartPersistence.class)
	protected DossierPartPersistence dossierPartPersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.DossierTemplateLocalService.class)
	protected org.opencps.backend.dossiermgt.service.DossierTemplateLocalService dossierTemplateLocalService;
	@BeanReference(type = DossierTemplatePersistence.class)
	protected DossierTemplatePersistence dossierTemplatePersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.FileTemplateLocalService.class)
	protected org.opencps.backend.dossiermgt.service.FileTemplateLocalService fileTemplateLocalService;
	@BeanReference(type = FileTemplatePersistence.class)
	protected FileTemplatePersistence fileTemplatePersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.PaymentConfigLocalService.class)
	protected org.opencps.backend.dossiermgt.service.PaymentConfigLocalService paymentConfigLocalService;
	@BeanReference(type = PaymentConfigPersistence.class)
	protected PaymentConfigPersistence paymentConfigPersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.PaymentFileLocalService.class)
	protected org.opencps.backend.dossiermgt.service.PaymentFileLocalService paymentFileLocalService;
	@BeanReference(type = PaymentFilePersistence.class)
	protected PaymentFilePersistence paymentFilePersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.ServiceConfigLocalService.class)
	protected org.opencps.backend.dossiermgt.service.ServiceConfigLocalService serviceConfigLocalService;
	@BeanReference(type = ServiceConfigPersistence.class)
	protected ServiceConfigPersistence serviceConfigPersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.ServiceInfoLocalService.class)
	protected org.opencps.backend.dossiermgt.service.ServiceInfoLocalService serviceInfoLocalService;
	@BeanReference(type = ServiceInfoPersistence.class)
	protected ServiceInfoPersistence serviceInfoPersistence;
	@BeanReference(type = org.opencps.backend.dossiermgt.service.ServiceOptionLocalService.class)
	protected org.opencps.backend.dossiermgt.service.ServiceOptionLocalService serviceOptionLocalService;
	@BeanReference(type = ServiceOptionPersistence.class)
	protected ServiceOptionPersistence serviceOptionPersistence;
	@ServiceReference(type = com.liferay.counter.kernel.service.CounterLocalService.class)
	protected com.liferay.counter.kernel.service.CounterLocalService counterLocalService;
	@ServiceReference(type = com.liferay.portal.kernel.service.ClassNameLocalService.class)
	protected com.liferay.portal.kernel.service.ClassNameLocalService classNameLocalService;
	@ServiceReference(type = ClassNamePersistence.class)
	protected ClassNamePersistence classNamePersistence;
	@ServiceReference(type = com.liferay.portal.kernel.service.ResourceLocalService.class)
	protected com.liferay.portal.kernel.service.ResourceLocalService resourceLocalService;
	@ServiceReference(type = com.liferay.portal.kernel.service.UserLocalService.class)
	protected com.liferay.portal.kernel.service.UserLocalService userLocalService;
	@ServiceReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;
	@ServiceReference(type = PersistedModelLocalServiceRegistry.class)
	protected PersistedModelLocalServiceRegistry persistedModelLocalServiceRegistry;
}