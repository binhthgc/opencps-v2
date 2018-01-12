package org.opencps.api.controller.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.opencps.api.controller.DeliverablesManagement;
import org.opencps.api.controller.exception.ErrorMsg;
import org.opencps.api.controller.util.DeliverableUtils;
import org.opencps.api.deliverable.model.DeliverableInputModel;
import org.opencps.api.deliverable.model.DeliverableModel;
import org.opencps.api.deliverable.model.DeliverableResultModel;
import org.opencps.api.deliverable.model.DeliverableSearchModel;
import org.opencps.api.deliverable.model.DeliverableUpdateModel;
import org.opencps.auth.api.BackendAuth;
import org.opencps.auth.api.BackendAuthImpl;
import org.opencps.auth.api.exception.UnauthenticationException;
import org.opencps.dossiermgt.action.DeliverableActions;
import org.opencps.dossiermgt.action.DeliverableLogActions;
import org.opencps.dossiermgt.action.impl.DeliverableActionsImpl;
import org.opencps.dossiermgt.action.impl.DeliverableLogActionsImpl;
import org.opencps.dossiermgt.constants.DeliverableTerm;
import org.opencps.dossiermgt.model.Deliverable;
import org.opencps.dossiermgt.model.DeliverableLog;
import org.opencps.dossiermgt.model.Registration;
import org.opencps.dossiermgt.model.impl.DeliverableImpl;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

public class DeliverablesManagementImpl implements DeliverablesManagement {

	@Override
	public Response getDeliverables(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, DeliverableSearchModel search) {
		//TODO
		BackendAuth auth = new BackendAuthImpl();
		
		try {
			
			// Check user is login
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}
			
			if (search.getEnd() == 0) {
				search.setStart(-1);
				search.setEnd(-1);
			}
			
			long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));
			
			// Default sort by modifiedDate
			Sort[] sorts = new Sort[] {
					SortFactoryUtil.create(Field.MODIFIED_DATE + "_sortable", Sort.STRING_TYPE, true) };
			
			if (Validator.isNotNull(search.getSort()) && Validator.isNotNull(search.getOrder())) {
				sorts = new Sort[] { SortFactoryUtil.create(search.getSort() + "_sortable", Sort.STRING_TYPE,
						GetterUtil.getBoolean(search.getOrder())) };
			}

			//
			LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

			params.put(Field.GROUP_ID, String.valueOf(groupId));
			params.put(DeliverableTerm.DELIVERABLE_STATE, search.getState());
			params.put(DeliverableTerm.GOV_AGENCY_CODE, search.getAgency());
			params.put(Field.KEYWORD_SEARCH, search.getKeyword());
			params.put(DeliverableTerm.DELIVERABLE_TYPE, search.getType());
			params.put(DeliverableTerm.APPLICANT_ID_NO, search.getApplicant());
			
			DeliverableActions actions = new DeliverableActionsImpl();
			DeliverableResultModel results = new DeliverableResultModel();
			
			// get JSON data deliverable
			JSONObject jsonData = actions.getListDeliverable(serviceContext.getCompanyId(), params, sorts,
					search.getStart(), search.getEnd(), serviceContext);
//			JSONObject result = action.getListDeliverable(state, agency, type, applicant);
			results.setTotal(jsonData.getInt("total"));
			results.getData()
					.addAll(DeliverableUtils.mappingToDeliverableResultModel((List<Document>) jsonData.get("data")));

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(results)).build();
		} catch (Exception e) {
			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
		}

	}

	@Override
	public Response insertDeliverables(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, DeliverableInputModel input) {
		// TODO Add Deliverable Type
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions action = new DeliverableActionsImpl();
			//
			String deliverableType = input.getDeliverableType();
			String deliverableCode = input.getDeliverableCode();
			String govAgencyCode = input.getGovAgencyCode();
			String applicantIdNo = input.getApplicantIdNo();
			String applicantName = input.getApplicantName();
			String subject = input.getSubject();
			String issueDate = input.getIssueDate();
			String expireDate = input.getExpireDate();
			String revalidate = input.getRevalidate();
			String deliverableState = input.getDeliverableState();
			//
			Deliverable deliverable = action.addDeliverable(groupId, deliverableType, deliverableCode, 
					govAgencyCode, applicantIdNo, applicantName, subject, issueDate, expireDate,
					revalidate, deliverableState, serviceContext);

			DeliverableInputModel result = DeliverableUtils.mappingToDeliverablesModel(deliverable);
//			DeliverableImpl model = new DeliverableImpl();
//			model.setDeliverableType(deliverableType);
//			model.setDeliverableCode(deliverableCode);
//			model.setGovAgencyCode(govAgencyCode);
//			model.setApplicantIdNo(applicantIdNo);
//			model.setApplicantName(applicantName);
//			model.setSubject(subject);
//			model.setIssueDate(issueDate);
//			model.setExpireDate(expireDate);
//			model.setRevalidate(revalidate);
//			model.setDeliverableState(String.valueOf(deliverableState));
//			action.addDeliverable(model);
			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(result)).build();
		} catch (Exception e) {
			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
		}

	}

	@Override
	public Response getDeliverablesDetail(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, Long id) {
		
		// TODO Add Deliverable Type
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions actions = new DeliverableActionsImpl();
			DeliverableModel results = new DeliverableModel();

			Deliverable deliverableInfo = actions.getDetailById(id, groupId);

//			if (Validator.isNull(deliverableInfo)) {
//				serviceInfo = actions.getById(GetterUtil.getLong(id));
//			}

			if (Validator.isNotNull(deliverableInfo)) {
				results = DeliverableUtils.mappingToDeliverableDetailModel(deliverableInfo);
			} else {
				throw new Exception();
			}

			return Response.status(200).entity(results).build();

		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}
	}

	//5
	@Override
	public Response deleteDeliverables(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, Long id) {

		// TODO Add Deliverable Type
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions actions = new DeliverableActionsImpl();
			DeliverableModel results = new DeliverableModel();

			Deliverable deliverableInfo = actions.deleteById(id, groupId);

//			if (Validator.isNull(deliverableInfo)) {
//				serviceInfo = actions.getById(GetterUtil.getLong(id));
//			}

			if (Validator.isNotNull(deliverableInfo)) {
				results = DeliverableUtils.mappingToDeliverableDetailModel(deliverableInfo);
			} else {
				throw new Exception();
			}

			return Response.status(200).entity(results).build();

		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}
		
		//		try {
//			DeliverableActions action = new DeliverableActionsImpl();
//			action.deleteDeliverable(id);
//			return Response.status(200).entity("Success").build();
//		} catch (Exception e) {
//			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
//		}
	}

	@Override
	public Response getFormData(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, Long id) {

		//TODO
		BackendAuth auth = new BackendAuthImpl();
		
		try {
			
			// Check user is login
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

			//
			LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

			params.put(Field.GROUP_ID, String.valueOf(groupId));
			params.put(DeliverableTerm.DELIVERABLE_ID, id);

			DeliverableActions actions = new DeliverableActionsImpl();
			
			// get JSON data deliverable
			JSONObject jsonData = actions.getFormDataById(serviceContext.getCompanyId(), params, null,
					-1, -1, serviceContext);

			JSONObject results = JSONFactoryUtil.createJSONObject(
					DeliverableUtils.mappingToDeliverableFormDataModel((List<Document>) jsonData.get("data")));

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(results)).build();
		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}

//		try {
//			DeliverableActions action = new DeliverableActionsImpl();
//			Deliverable deliverable = action.getListDeliverableDetail(id);
//			return Response.status(200).entity(deliverable.getFormData()).build();
//		} catch (Exception e) {
//			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
//		}
	}

	@Override
	public Response updateFormData(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, Long id, DeliverableInputModel input) {
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions actions = new DeliverableActionsImpl();

			Deliverable deliverable = actions.updateFormData(groupId, id, input.getFormData(), serviceContext);

			String formData = deliverable.getFormData();

			JSONObject result = JSONFactoryUtil.createJSONObject(formData);

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(result)).build();
		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}
	}

	//8
	@Override
	public Response getFormScript(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, Long id) {

		// TODO Add Deliverable Type
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions actions = new DeliverableActionsImpl();
			String results = StringPool.BLANK;

			Deliverable deliverableInfo = actions.getDetailById(id, groupId);

			if (Validator.isNotNull(deliverableInfo)) {
				results = deliverableInfo.getFormScript();
			} else {
				throw new Exception();
			}

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(results)).build();

		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}
		
//		try {
//			DeliverableActions action = new DeliverableActionsImpl();
//			Deliverable deliverable = action.getListDeliverableDetail(id);
//			return Response.status(200).entity(deliverable.getFormScript()).build();
//		} catch (Exception e) {
//			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
//		}
	}

	//9
	@Override
	public Response getPreview(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext, Long id) {
		// TODO Add Deliverable Type
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions actions = new DeliverableActionsImpl();
			String results = StringPool.BLANK;

			Deliverable deliverableInfo = actions.getDetailById(id, groupId);

			if (Validator.isNotNull(deliverableInfo)) {
				results = deliverableInfo.getFormReport();
			} else {
				throw new Exception();
			}

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(results)).build();

		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}
	}

	//10
	@Override
	public Response getDeliverableAction(Long id, String deliverableAction) {
		// TODO Auto-generated method stub
		return null;
	}

	//11
	@Override
	public Response getDeliverableLog(Long id) {
		try {
			DeliverableLogActions action = new DeliverableLogActionsImpl();
			DeliverableLog log = action.getDeliverableLog(id);
			return Response.status(200).entity(log).build();
		} catch (Exception e) {
			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
		}
	}

	@Override
	public Response updateDeliverables(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
			User user, ServiceContext serviceContext,Long id, DeliverableUpdateModel input) {

		// TODO Add Deliverable Type
		BackendAuth auth = new BackendAuthImpl();

		long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

		try {
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			DeliverableActions action = new DeliverableActionsImpl();
			//
			String subject = input.getSubject();
			String issueDate = input.getIssueDate();
			String expireDate = input.getExpireDate();
			String revalidate = input.getRevalidate();
			String deliverableState = input.getDeliverableState();
			String deliverableAction = input.getDeliverableAction();
			//
			Deliverable deliverable = action.updateDeliverable(groupId, id, subject, issueDate, expireDate,
					revalidate, deliverableState, deliverableAction, serviceContext);

			DeliverableUpdateModel result = DeliverableUtils.mappingToDeliverablesUpdateModel(deliverable);

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(result)).build();
		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}
//		try {
//			DeliverableActions action = new DeliverableActionsImpl();
//			DeliverableImpl model = new DeliverableImpl();
//			model.setDeliverableId(id);
//			model.setDeliverableType(deliverableType);
//			model.setDeliverableCode(deliverableCode);
//			model.setGovAgencyCode(govAgencyCode);
//			model.setApplicantIdNo(applicantIdNo);
//			model.setApplicantName(applicantName);
//			model.setSubject(subject);
//			model.setIssueDate(issueDate);
//			model.setExpireDate(expireDate);
//			model.setRevalidate(revalidate);
//			model.setDeliverableState(String.valueOf(deliverableState));
//			action.updateDeliverable(model);
//			return Response.status(200).entity("Success").build();
//		} catch (Exception e) {
//			return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(e).build();
//		}

	}

	//18
	@Override
	public Response getDataFormByTypeCode(HttpServletRequest request, HttpHeaders header, Company company,
			Locale locale, User user, ServiceContext serviceContext, String agencyNo, String typeCode,
			String _properties) {

		BackendAuth auth = new BackendAuthImpl();

		try {

			// Check user is login
			if (!auth.isAuth(serviceContext)) {
				throw new UnauthenticationException();
			}

			long groupId = GetterUtil.getLong(header.getHeaderString("groupId"));

			Registration regInfo = null;
			long registrationId = 0;
//			if (Validator.isNotNull("") && Validator.isNotNull(agencyNo)) {
//			regInfo = RegistrationLocalServiceUtil.getByApplicantAndAgency(groupId, "", agencyNo);
//			regInfo = RegistrationLocalServiceUtil.getByAgency(groupId, agencyNo);
//			}
			if (regInfo != null) {
				registrationId = regInfo.getRegistrationId();
			}

//			String _properties = search.getProperties();
//			String _properties = "ten_doanh_nghiep";
			String[] splitProperties = null;
			if (Validator.isNotNull(_properties)) {
				splitProperties = _properties.split(";");
			}
			
			DeliverableActions actions = new DeliverableActionsImpl();
			
			JSONArray jsonDataList = null;
//			jsonDataList = actions.getFormDataByTypecode(groupId, registrationId,
//					typeCode, splitProperties);

			JSONObject results = JSONFactoryUtil.createJSONObject();
			results.put("total", jsonDataList.length());
			results.put("data", jsonDataList);

			return Response.status(200).entity(JSONFactoryUtil.looseSerialize(results)).build();

		} catch (Exception e) {
			ErrorMsg error = new ErrorMsg();

			error.setMessage("not found!");
			error.setCode(404);
			error.setDescription("not found!");

			return Response.status(404).entity(error).build();
		}

	}
}
