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

package com.liferay.content.targeting.rule.role;

import com.liferay.content.targeting.anonymous.users.model.AnonymousUser;
import com.liferay.content.targeting.api.model.BaseRule;
import com.liferay.content.targeting.api.model.Rule;
import com.liferay.content.targeting.model.RuleInstance;
import com.liferay.content.targeting.rule.categories.UserAttributesRuleCategory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.service.RoleLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Eudaldo Alonso
 */
@Component(immediate = true, service = Rule.class)
public class RegularRoleRule extends BaseRule {

	@Activate
	@Override
	public void activate() {
		super.activate();
	}

	@Deactivate
	@Override
	public void deActivate() {
		super.deActivate();
	}

	@Override
	public boolean evaluate(
			HttpServletRequest request, RuleInstance ruleInstance,
			AnonymousUser anonymousUser)
		throws Exception {

		long roleId = GetterUtil.getLong(ruleInstance.getTypeSettings());

		return RoleLocalServiceUtil.hasUserRole(
			anonymousUser.getUserId(), roleId);
	}

	@Override
	public String getIcon() {
		return "icon-group";
	}

	@Override
	public String getRuleCategoryKey() {
		return UserAttributesRuleCategory.KEY;
	}

	@Override
	public String getSummary(RuleInstance ruleInstance, Locale locale) {
		long roleId = GetterUtil.getLong(ruleInstance.getTypeSettings());

		try {
			Role role = RoleLocalServiceUtil.fetchRole(roleId);

			return role.getTitle(locale);
		}
		catch (SystemException e) {
		}

		return StringPool.BLANK;
	}

	@Override
	public String processRule(
		PortletRequest request, PortletResponse response, String id,
		Map<String, String> values) {

		return values.get("roleId");
	}

	@Override
	protected String getFormTemplatePath() {
		return _FORM_TEMPLATE_PATH;
	}

	@Override
	protected void populateContext(
		RuleInstance ruleInstance, Map<String, Object> context,
		Map<String, String> values) {

		long roleId = 0;

		if (!values.isEmpty()) {
			roleId = GetterUtil.getLong(values.get("roleId"));
		}
		else if (ruleInstance != null) {
			roleId = GetterUtil.getLong(ruleInstance.getTypeSettings());
		}

		context.put("roleId", roleId);

		Company company = (Company)context.get("company");

		List<Role> roles = new ArrayList<Role>();

		try {
			roles = RoleLocalServiceUtil.getRoles(
				company.getCompanyId(), new int[] {RoleConstants.TYPE_REGULAR});
		}
		catch (SystemException e) {
		}

		context.put("roles", roles);
	}

	protected static final String _FORM_TEMPLATE_PATH =
		"templates/ct_fields_regular.ftl";

}