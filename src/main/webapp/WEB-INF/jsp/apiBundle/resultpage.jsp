<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<h1>Bulletin Page</h1>

<p>
<span style="font-weight:bold">Current user: </span>${username}<br/>
<span style="font-weight:bold">Current role: </span>${role}
</p>

<hr/>

<p>
${source} <br/><br/>
${result}
</p>

</body>
</html>