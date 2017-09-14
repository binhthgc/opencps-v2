package org.opencps.usermgt.action.impl;

import java.util.LinkedHashMap;

import org.opencps.auth.api.exception.NotFoundException;
import org.opencps.auth.api.exception.UnauthenticationException;
import org.opencps.auth.api.exception.UnauthorizationException;
import org.opencps.usermgt.action.ApplicantActions;
import org.opencps.usermgt.model.Applicant;
import org.opencps.usermgt.service.ApplicantLocalServiceUtil;

import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringPool;

public class ApplicantActionsImpl implements ApplicantActions {

	@Override
	public Applicant register(ServiceContext context, String applicantName, String applicantIdType,
			String applicantIdNo, String applicantIdDate, String contactEmail, String password)
			throws PortalException, SystemException {

		Applicant applicant = ApplicantLocalServiceUtil.updateApplication(context, 0, applicantName, applicantIdType,
				applicantIdNo, applicantIdDate, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, contactEmail,
				StringPool.BLANK, password);

		return applicant;
	}

	@Override
	public Applicant removeApplicant(ServiceContext context, long applicantId)
			throws NoSuchUserException, NotFoundException, UnauthenticationException, UnauthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getApplicants(ServiceContext context, long userId, long companyId, long groupId,
			LinkedHashMap<String, Object> params, Sort[] sorts, int start, int end, ServiceContext serviceContext) {

		JSONObject result = JSONFactoryUtil.createJSONObject();
		Hits hits = null;
		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(companyId);

		try {

			hits = ApplicantLocalServiceUtil.searchLucene(params, sorts, start, end, searchContext);

			result.put("data", hits.toList());

			long total = ApplicantLocalServiceUtil.countLucene(params, searchContext);

			result.put("total", total);

		} catch (Exception e) {
			_log.error(e);
		}

		return result;

	}

	@Override
	public JSONObject getApplicantDetail(ServiceContext context, long applicantId)
			throws NoSuchUserException, NotFoundException, UnauthenticationException, UnauthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Applicant updateApplicant(ServiceContext context, String address, String cityCode, String cityName,
			String districtCode, String districtName, String wardCode, String wardName, String contactName,
			String contactTelNo, String contactEmail)
			throws NoSuchUserException, NotFoundException, UnauthenticationException, UnauthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Applicant updateProfile(ServiceContext context, long applicantId, String profile)
			throws NoSuchUserException, NotFoundException, UnauthenticationException, UnauthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Applicant removeProfile(ServiceContext context, long applicantId)
			throws NoSuchUserException, NotFoundException, UnauthenticationException, UnauthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Applicant lockApplicant(ServiceContext context, long applicantId)
			throws NoSuchUserException, NotFoundException, UnauthenticationException, UnauthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	Log _log = LogFactoryUtil.getLog(ApplicantActionsImpl.class);
}