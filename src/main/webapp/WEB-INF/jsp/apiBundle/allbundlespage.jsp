<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>List Bundles</title>
</head>
<body>

<h1>List Bundles Page</h1>

<p>
<span style="font-weight:bold">Current user: </span>${username}<br/>
<span style="font-weight:bold">Current role: </span>${role}
</p>

<hr/>
<div style="width: 1200px" >
	<div>
		<table style="border: 1px solid; width: 1200px; text-align:center" border="1">
			<caption>Public Posts</caption>
			<thead style="background:#fcf">
				<tr>
					<th>id</th>
					<th>name</th>
					<th>comments</th>
					<th>start date</th>
					<th>end date</th>
					<th>last updated</th>
					<th>created on</th>
					<th colspan="4">actions</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${allBundles}" var="bundle">
											
				<c:url var="editUrl" value="/apiBundle/edit?id=${bundle.id}" />
				<c:url var="deleteUrl" value="/apiBundle/delete?id=${bundle.id}" />
				<c:url var="grantPermissionUrl" value="/apiBundle/grantPermission"/>
				<c:url var="removePermissionUrl" value="/apiBundle/removePermission"/>
				<tr>
					<td>${bundle.id}</td>
					<td>${bundle.name}</td>
					<td>${bundle.comments}</td>
					<td>${bundle.startDate}</td>
					<td>${bundle.endDate}</td>
					<td>${bundle.lastUpdated}</td>
					<td>${bundle.createdOn}</td>
					<td><a href="${editUrl}">Edit</a></td>
					<td><a href="${deleteUrl}">Delete</a></td>
					<td>
						<form action="${grantPermissionUrl}" method="post">
							<input type="hidden" name="id" value="${bundle.id}"/>
							orgId: <input type="text" name="orgId" />
							<input type="submit" value="grantPermission">
						</form>
					</td>
					<td>
						<form action="${removePermissionUrl}" method="post">
							<input type="hidden" name="id" value="${bundle.id}"/>
							orgId: <input type="text" name="orgId" />
							<input type="submit" value="removePermissions">
						</form>
					</td>					
					<td>
						<c:forEach items="${bundle.accessControleEntries}" var="ace">
							${ace.sid} : ${ace.permission}
						</c:forEach>
					</td>
				</tr>
			</c:forEach>
			
			</tbody>
		</table>
		
	</div>
	<hr/>
	<c:url var="addUrl" value="/apiBundle/add" />
	<a href="${addUrl}">Add Bundle</a>
</div>

</body>
</html>