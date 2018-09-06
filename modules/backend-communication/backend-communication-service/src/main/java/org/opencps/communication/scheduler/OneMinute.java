package org.opencps.communication.scheduler;

import java.util.Date;
import java.util.List;

import org.opencps.communication.constants.NotificationTemplateTerm;
import org.opencps.communication.model.NotificationQueue;
import org.opencps.communication.model.Notificationtemplate;
import org.opencps.communication.service.NotificationtemplateLocalService;
import org.opencps.communication.utils.NotificationQueueBusinessFactoryUtil;
import org.opencps.communication.utils.NotificationUtil;
import org.opencps.kernel.context.MBServiceContextFactoryUtil;
import org.opencps.kernel.message.MBMessageEntry;
import org.opencps.kernel.message.email.MBEmailSenderFactoryUtil;
import org.opencps.kernel.message.notification.MBNotificationSenderFactoryUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseSchedulerEntryMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.TriggerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.StringPool;

/**
 * @author trungnt
 */
@Component(immediate = true, service = OneMinute.class)
public class OneMinute extends BaseSchedulerEntryMessageListener {

	@Override
	protected void doReceive(Message message) {

		doProcessNotification(message);

	}

	private void doProcessNotification(Message message) {
		
		List<Notificationtemplate> notificationtemplates =
			_notificationTemplateLocalService.findByInterval(
				NotificationTemplateTerm.MINUTELY);

	
		if (notificationtemplates != null) {
			_log.info("notificationtemplates SIZE: "+notificationtemplates.size());
			for (Notificationtemplate notificationtemplate : notificationtemplates) {
				
				List<NotificationQueue> notificationQueues =
					NotificationQueueBusinessFactoryUtil.findByNotificationType_LessThanExpireDate(
						notificationtemplate.getNotificationType(), new Date());
				
				if (notificationQueues != null) {
					_log.info("notificationtemplates SIZE: "+notificationQueues.size());
					for (NotificationQueue notificationQueue : notificationQueues) {
						try {
							ServiceContext serviceContext =
								MBServiceContextFactoryUtil.create(
									notificationQueue.getCompanyId(),
									notificationQueue.getGroupId(),
									notificationQueue.getUserId());

							MBMessageEntry messageEntry =
								NotificationUtil.createMBMessageEntry(
									notificationQueue, notificationtemplate,
									serviceContext);
							_log.info("messageEntry: "+messageEntry);
							if(messageEntry.isSendEmail()){
								_log.info("messageEntry.isSendEmail(): "+messageEntry.isSendEmail());
								MBEmailSenderFactoryUtil.send(messageEntry, StringPool.BLANK);
							}

							if(messageEntry.isSendNotify()){
								_log.info("messageEntry.isSendNotify(): "+messageEntry.isSendNotify());
								MBNotificationSenderFactoryUtil.send(
									messageEntry, messageEntry.getClassName(),
									serviceContext);
							}
							
							NotificationQueueBusinessFactoryUtil.delete(
								notificationQueue.getNotificationQueueId(),
								serviceContext);
						}
						catch (Exception e) {
							_log.warn("Can't send message from queue " + e);
						}
					}
				}
			}
		}
	}

	@Activate
	@Modified
	protected void activate() {

		schedulerEntryImpl.setTrigger(
			TriggerFactoryUtil.createTrigger(
				getEventListenerClass(), getEventListenerClass(), 1,
				TimeUnit.MINUTE));
		_schedulerEngineHelper.register(
			this, schedulerEntryImpl, DestinationNames.SCHEDULER_DISPATCH);
	}

	@Deactivate
	protected void deactivate() {

		_schedulerEngineHelper.unregister(this);
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {

	}

	@Reference(unbind = "-")
	protected void setSchedulerEngineHelper(
		SchedulerEngineHelper schedulerEngineHelper) {

		_schedulerEngineHelper = schedulerEngineHelper;
	}

	@Reference(unbind = "-")
	protected void setTriggerFactory(TriggerFactory triggerFactory) {

	}

	@Reference
	private NotificationtemplateLocalService _notificationTemplateLocalService;

	private SchedulerEngineHelper _schedulerEngineHelper;

	private Log _log = LogFactoryUtil.getLog(OneMinute.class);

}
