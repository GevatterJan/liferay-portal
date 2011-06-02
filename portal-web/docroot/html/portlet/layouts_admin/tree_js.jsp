<%--
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
--%>

<%@ include file="/html/portlet/layouts_admin/init.jsp" %>

<%@ include file="/html/portlet/layouts_admin/init_attributes.jspf" %>

<%
String treeLoadingId = PortalUtil.generateRandomKey(request, "treeLoading");

boolean checkContentDisplayPage = ParamUtil.getBoolean(request, "checkContentDisplayPage", false);
boolean expandFirstNode = ParamUtil.getBoolean(request, "expandFirstNode", true);
boolean saveState = ParamUtil.getBoolean(request, "saveState", true);
boolean selectableTree = ParamUtil.getBoolean(request, "selectableTree");
String treeId = ParamUtil.getString(request, "treeId");
%>

<aui:script use="aui-io-request,aui-tree-view">
	var TreeUtil = {
		CMD_CHECK: 'checkLayout',
		CMD_COLLAPSE: 'collapseLayout',
		CMD_EXPAND: 'expandLayout',
		CMD_UNCHECK: 'uncheckLayout',
		DEFAULT_PARENT_LAYOUT_ID: <%= LayoutConstants.DEFAULT_PARENT_LAYOUT_ID %>,
		PREFIX_LAYOUT_ID: '_layoutId_',
		PREFIX_PLID: '_plid_',

		selectedNode: null,

		afterRenderTree: function(event) {
			var treeInstance = event.target;

			var rootNode = treeInstance.item(0);
			var loadingEl = A.one('#<portlet:namespace />treeLoading<%= treeLoadingId %>');

			loadingEl.hide();

			if (<%= saveState || expandFirstNode %>) {
				TreeUtil.restoreNodeState(rootNode);
			}
		},

		createId: function(layoutId, plid) {
			return '<%= HtmlUtil.escape(treeId) %>' + TreeUtil.PREFIX_LAYOUT_ID + layoutId + TreeUtil.PREFIX_PLID + plid;
		},

		createLink: function(data) {
			var className = 'layout-tree';

			var contentDisplayPage = data.contentDisplayPage;
			var label = data.label;
			var plid = data.plid;
			var uuid = data.uuid;

			if (<%= checkContentDisplayPage %> && !contentDisplayPage) {
				className += ' layout-page-unacceptable';
			}

			return '<a class="' + className + '" data-uuid="' + uuid + '" href="<%= portletURL + StringPool.AMPERSAND + portletDisplay.getNamespace() + "selPlid=" %>'+ plid +'">'+ Liferay.Util.escapeHTML(label) +'</a>';
		},

		createTreeNode: function(node, childrenSet) {
			var newNode = {
				alwaysShowHitArea: node.hasChildren,
				children: childrenSet[node.layoutId],
				expanded: node.expanded,
				id: TreeUtil.createId(node.layoutId, node.plid),
				label: node.name,
				type: '<%= selectableTree ? "task" : "io" %>'
			};

			if (node.layoutRevisionId) {
				newNode.label = [newNode.label, " [", node.layoutSetBranchName, " ", node.layoutRevisionId, "]"].join('');
			}

			if (<%= selectableTree %>) {
				newNode.checked = node.checked;
			}
			else {
				newNode.label = TreeUtil.createLink(
					{
						label: newNode.label,
						plid: node.plid,
						uuid: node.uuid,
						contentDisplayPage: node.contentDisplayPage
					}
				);
			}

			return newNode;
		},

		debouncedCheckedChange: A.debounce(
			function(event) {
				var instance = this;

				var node = event.target;
				var cmd = TreeUtil.CMD_UNCHECK;
				var layoutId = TreeUtil.extractLayoutId(node);

				if (event.newVal) {
					cmd = TreeUtil.CMD_CHECK;
				}

				TreeUtil.updateSessionTreeClick(cmd, layoutId, '<%= HtmlUtil.escape(treeId) %>', true);
			},
			10
		),

		extractLayoutId: function(node) {
			return node.get('id').match(/layoutId_(\d+)/)[1];
		},

		extractPlid: function(node) {
			return node.get('id').match(/plid_(\d+)/)[1];
		},

		formatJSONResults: function(json) {
			var instance = this;

			var childrenSet = {};
			var firstParentLayoutId = 0;
			var results = [];

			A.Array.each(json, function(node, index) {
				var layoutId = node.layoutId;
				var parentLayoutId = node.parentLayoutId;

				if (index === 0) {
					firstParentLayoutId = parentLayoutId;
				}

				childrenSet[layoutId] = [];

				if (!childrenSet[parentLayoutId]) {
					childrenSet[parentLayoutId] = [];
				}

				if (parentLayoutId === firstParentLayoutId) {
					results.push(TreeUtil.createTreeNode(node, childrenSet));
				}
				else {
					childrenSet[parentLayoutId].push(TreeUtil.createTreeNode(node, childrenSet));
				}
			});

			return results;
		},

		getSelectedNodeByPlid: function(plid) {
			var instance = this;

			if (!TreeUtil.selectedNode) {
				var node = A.one('[id$=' + TreeUtil.PREFIX_PLID + plid  + ']');

				TreeUtil.selectedNode = A.Widget.getByNode(node);
			}

			return TreeUtil.selectedNode;
		},

		restoreNodeState: function(node) {
			var instance = this;

			var id = node.get('id');
			var plid = TreeUtil.extractPlid(node);
			var layoutId = TreeUtil.extractLayoutId(node);

			var selectedNode = TreeUtil.getSelectedNodeByPlid('<%= selPlid %>');

			if (selectedNode) {
				selectedNode.select();
			}

			if (!node.get('expanded') && (node.hasChildNodes() || (layoutId == 0))) {
				node.expand();
			}
		},

		updateLayout: function(data) {
			var updateURL = themeDisplay.getPathMain() + '/layouts_admin/update_page';

			A.io.request(
				updateURL,
				{
					data: data
				}
			);
		},

		updateLayoutParent: function(dragPlid, dropPlid) {
			TreeUtil.updateLayout(
				{
					cmd: 'parent_layout_id',
					parentPlid: dropPlid,
					plid: dragPlid
				}
			);
		},

		updateLayoutPriority: function(dragPlid, index) {
			TreeUtil.updateLayout(
				{
					cmd: 'priority',
					plid: dragPlid,
					priority: index
				}
			);
		},

		updateSessionTreeClick: function(cmd, id, treeId, recursive) {
			var sessionClickURL = themeDisplay.getPathMain() + '/portal/session_tree_js_click';

			var data = {
				cmd: cmd,
				groupId: <%= groupId %>,
				layoutId: id,
				privateLayout: <%= privateLayout %>,
				recursive: recursive,
				treeId: treeId
			};

			A.io.request(
				sessionClickURL,
				{
					data: data
				}
			);
		}
	};

	var getLayoutsURL = themeDisplay.getPathMain() + '/layouts_admin/get_layouts';
	var rootId = TreeUtil.createId(TreeUtil.DEFAULT_PARENT_LAYOUT_ID, 0);
	var rootLabel = '<%= HtmlUtil.escapeJS(rootNodeName) %>';
	var treeElId = '<portlet:namespace /><%= HtmlUtil.escape(treeId) %>Output';

	var RootNodeType = A.TreeNodeTask;
	var TreeViewType = A.TreeView;

	var rootConfig = {
		alwaysShowHitArea: true,
		draggable: false,
		id: rootId,
		label: rootLabel,
		leaf: false
	};

	if (!<%= selectableTree %>) {
		RootNodeType = A.TreeNodeIO;
		TreeViewType = A.TreeViewDD;

		rootLabel = TreeUtil.createLink(
			{
				label: rootLabel,
				plid: TreeUtil.DEFAULT_PARENT_LAYOUT_ID
			}
		);
	}

	var rootNode = new RootNodeType(rootConfig);

	rootNode.get('contentBox').addClass('lfr-root-node');

	var treeview = new TreeViewType(
		{
			after: {
				render: TreeUtil.afterRenderTree
			},
			boundingBox: '#' + treeElId,
			children: [rootNode],
			io: {
				cfg: {
					data: function(node) {
						var layoutId = TreeUtil.extractLayoutId(node);

						return {
							groupId: <%= groupId %>,
							layoutId: layoutId,
							privateLayout: <%= privateLayout %>,
							treeId: '<%= HtmlUtil.escape(treeId) %>'
						};
					},
					method: AUI.defaults.io.method
				},
				formatter: TreeUtil.formatJSONResults,
				url: getLayoutsURL
			},
			on: {
				drop: function(event) {
					var tree = event.tree;

					var dragNode = tree.dragNode;

					var index = dragNode.get('parentNode').indexOf(dragNode);

					TreeUtil.updateLayoutPriority(
						TreeUtil.extractPlid(dragNode),
						index
					);
				},
				dropAppend: function(event) {
					var tree = event.tree;

					TreeUtil.updateLayoutParent(
						TreeUtil.extractPlid(tree.dragNode),
						TreeUtil.extractPlid(tree.dropNode)
					);
				},
				dropInsert: function(event) {
					var tree = event.tree;

					TreeUtil.updateLayoutParent(
						TreeUtil.extractPlid(tree.dragNode),
						TreeUtil.extractPlid(tree.dropNode.get('parentNode'))
					);
				}
			},
			paginator: {
				limit: <%= PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN %>,
				offsetParam: 'start'
			},
			type: 'pages'
		}
	).render();

	if (<%= saveState %>) {
		treeview.on('*:checkedChange', TreeUtil.debouncedCheckedChange);

		treeview.on(
			'*:expandedChange',
			function(event) {
				var node = event.target;
				var expanded = event.newVal;
				var layoutId = TreeUtil.extractLayoutId(node);

				if (event.prevVal !== event.newVal) {
					var cmd = TreeUtil.CMD_COLLAPSE;
					var recursive = true;

					if (expanded) {
						cmd = TreeUtil.CMD_EXPAND;
						recursive = false;
					}

					TreeUtil.updateSessionTreeClick(cmd, layoutId, '<%= HtmlUtil.escape(treeId) %>', recursive);
				}
			}
		);

		treeview.on(
			'append',
			function(event) {
				TreeUtil.restoreNodeState(event.tree.node);
			}
		);
	}


	A.one('#' + treeElId).setData('treeInstance', treeview);
</aui:script>

<div class="lfr-tree-loading" id="<portlet:namespace />treeLoading<%= treeLoadingId %>">
	<span class="aui-icon aui-icon-loading lfr-tree-loading-icon"></span>
</div>

<div class="lfr-tree" id="<portlet:namespace /><%= HtmlUtil.escape(treeId) %>Output"></div>