package org.opencps.dossiermgt.service.indexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.opencps.dossiermgt.constants.RegistrationFormTerm;
import org.opencps.dossiermgt.model.RegistrationForm;
import org.opencps.dossiermgt.service.RegistrationFormLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;

public class RegistrationFormIndexer extends BaseIndexer<RegistrationForm> {
	public static final String CLASS_NAME = RegistrationForm.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	protected void doDelete(RegistrationForm object) throws Exception {
		deleteDocument(object.getCompanyId(), object.getPrimaryKey());

	}

	@Override
	protected Document doGetDocument(RegistrationForm object) throws Exception {
		Document document = getBaseModelDocument(CLASS_NAME, object);

		// Indexer of audit fields
		document.addNumberSortable(Field.COMPANY_ID, object.getCompanyId());
		document.addNumberSortable(Field.GROUP_ID, object.getGroupId());
		document.addDateSortable(Field.MODIFIED_DATE, object.getCreateDate());
		document.addDateSortable(Field.CREATE_DATE, object.getModifiedDate());
		document.addNumberSortable(Field.USER_ID, object.getUserId());
		document.addKeywordSortable(Field.ENTRY_CLASS_NAME, CLASS_NAME);
		document.addNumberSortable(Field.ENTRY_CLASS_PK, object.getPrimaryKey());

		// add number fields

		document.addNumberSortable(RegistrationFormTerm.REGISTRATION_ID, object.getRegistrationId());

		// add text fields
		document.addTextSortable(RegistrationFormTerm.REFERENCE_UID, object.getReferenceUid());
		document.addTextSortable(RegistrationFormTerm.FORM_NO, object.getFormNo());
		document.addTextSortable(RegistrationFormTerm.FORM_NAME, object.getFormName());
		document.addTextSortable(RegistrationFormTerm.ISNEW, String.valueOf(object.isIsNew()));
		document.addTextSortable(RegistrationFormTerm.REMOVED, String.valueOf(object.isRemoved()));
		
		// index formData
        try {
            JSONObject jsonObject =
                JSONFactoryUtil.createJSONObject(object.getFormData());
            
            List<Object[]> keyValues = new ArrayList<Object[]>();
            
            parseJSONObject(keyValues, jsonObject);
            
            if (keyValues != null) {
                for (Object[] keyValue : keyValues) {
                    _log.info("=========REGISTRATION_FORM_INDEX_FORM_DATA========:" + keyValue[0] + "_" + keyValue[1]);
                    document.addKeyword(
                        keyValue[0].toString(), keyValue[1].toString());
                }
            }
        }
        catch (Exception e) {
            _log.error(e);
        }

		
		return document;
	}
	
    protected List<Object[]> parseJSONObject(
        List<Object[]> keyValues, JSONObject json) {

        List<Object[]> objects = new ArrayList<Object[]>();
        try {

            Iterator<String> itr = json.keys();
            while (itr.hasNext()) {
                String key = itr.next();
                Object object = json.get(key);
                if (object instanceof JSONObject) {
                    // Tinh chung cho key cha.
                    Object[] keyValue = new Object[2];
                    keyValue[0] = key;
                    keyValue[1] = object.toString();
                    keyValues.add(keyValue);
                    parseJSONObject(keyValues, json.getJSONObject(key));
                }
                else if (object instanceof JSONArray) {
                    JSONArray jsonArray = json.getJSONArray(key);
                    Object[] keyValue = new Object[2];
                    // Tinh chung cho key cha
                    keyValue[0] = key;
                    keyValue[1] = jsonArray.toString();
                    keyValues.add(keyValue);
                    parseJSONObject(keyValues, jsonArray);
                }
                else {
                    Object[] keyValue = new Object[2];
                    keyValue[0] = key;
                    keyValue[1] = object.toString();
                    keyValues.add(keyValue);
                }
            }

        }
        catch (JSONException e) {
            _log.error(e);
        }

        return objects;
    }

    protected List<Object[]> parseJSONObject(
        List<Object[]> keyValues, JSONArray jsonArray)
        throws JSONException {

        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object tempObject = jsonArray.get(i);
                if (tempObject instanceof JSONObject) {
                    parseJSONObject(keyValues, (JSONObject) tempObject);
                }
                else if (tempObject instanceof JSONArray) {
                    parseJSONObject(keyValues, (JSONArray) tempObject);
                }
                else {
                    // Tinh chung cho key cha.
                }
            }
        }

        return keyValues;
    }

	@Override
	protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest,
			PortletResponse portletResponse) throws Exception {
		Summary summary = createSummary(document);

		summary.setMaxContentLength(QueryUtil.ALL_POS);

		return summary;
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		RegistrationForm object = RegistrationFormLocalServiceUtil.getRegistrationForm(classPK);
		doReindex(object);

	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);
		reindex(companyId);

	}

	@Override
	protected void doReindex(RegistrationForm object) throws Exception {
		Document document = getDocument(object);
		IndexWriterHelperUtil.updateDocument(getSearchEngineId(), object.getCompanyId(), document,
				isCommitImmediately());
	}

	protected void reindex(long companyId) throws PortalException {
		final IndexableActionableDynamicQuery indexableActionableDynamicQuery = RegistrationFormLocalServiceUtil
				.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery
				.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<RegistrationForm>() {

					@Override
					public void performAction(RegistrationForm object) {
						try {
							Document document = getDocument(object);

							indexableActionableDynamicQuery.addDocuments(document);
						} catch (PortalException pe) {
							if (_log.isWarnEnabled()) {
								_log.warn("Unable to index DossierLog " + object.getPrimaryKey(), pe);
							}
						}
					}

				});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	private static Log _log = LogFactoryUtil.getLog(RegistrationFormIndexer.class);

}