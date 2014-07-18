<html>
<head>
<title>Login Page</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link type="text/css" rel="stylesheet" href="/developer/resources/css/ext/bootstrap/bootstrap.min.css?@@TIMESTAMP@@"/>
<link type="text/css" rel="stylesheet" href="/developer/resources/css/ext/bootstrap/bootstrap-theme.min.css?@@TIMESTAMP@@"/>
</head>
<body>
	
	<form class="form-horizontal" role="form" id="sign_in_page" action="/developer/auth/login" method="POST" autocomplete="off">
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-2">
				<h3>Login Page</h3>
			</div>
		</div>
		<c:if test="${error == 'true'}">
			<div class="form-group has-error">
				<div class="col-sm-offset-2 col-sm-2">
					<label class="control-label">${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</label>
				</div>
			</div>	
		</c:if>
		 <div class="form-group">
			<label for="login" class="col-sm-2 control-label">Login</label>
			<div class="col-sm-2">
	      		<input type="text" class="form-control" id="login_txt" name="username" placeholder="enter username or email">
	    	</div>
    	</div>
    	<div class="form-group">
	    	<label for="password" class="col-sm-2 control-label">Password</label>
			<div class="col-sm-2">
	      		<input type="password" class="form-control" id="password_txt" name="password" placeholder="enter password">
	    	</div>
		</div>    	
  		<div class="form-group">
    		<div class="col-sm-offset-2 col-sm-10">
      			<button type="submit" class="btn btn-primary">Sign in</button>
    		</div>
  		</div>    	
	</form>
</body>
</html>