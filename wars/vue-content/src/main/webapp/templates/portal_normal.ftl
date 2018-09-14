<!DOCTYPE html>

<#include init />

<html class="${root_css_class}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>Hệ thống Dịch vụ công trực tuyến</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<base href="/">
	<link href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,900|Material+Icons&amp;subset=vietnamese" rel="stylesheet">
	<link type="text/css" href="/o/vue-content/css/main_public.7aad116d33cf0f763e67beb48fed4e98.css" rel="stylesheet">
	<link type="text/css" href="/o/vue-content//css/base-style.css?t=1" rel="stylesheet">
	<link type="text/css" href="/o/vue-content//css/app_custom.css?t=1" rel="stylesheet">
	
</head>

<body style="min-width: unset !important;">
	
	<@liferay_util["include"] page=body_top_include />
	
	<#if selectable>
		<@liferay_util["include"] page=content_include />
	<#else>
		${portletDisplay.recycle()}

		${portletDisplay.setTitle(the_title)}
		
		<@liferay_theme["wrap-portlet"] page="portlet.ftl">
			<@liferay_util["include"] page=content_include />
		</@>
	</#if>
</body>

</html>
