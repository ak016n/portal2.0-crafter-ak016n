package com.att.developer.exception;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;

public class ServerSideException extends RuntimeException {

	private static final long serialVersionUID = -633111215638724781L;

	private ServerSideErrors serverSideErrors;
	
	public void setServerSideErrors(ServerSideErrors serverSideErrors) {
		this.serverSideErrors = serverSideErrors;
	}
	
	public ServerSideErrors getServerSideErrors() {
		return serverSideErrors;
	}
	
	public ServerSideException(Throwable e, ServerSideErrors serverSideErrors) {
        super(e);
        this.setServerSideErrors(serverSideErrors);
    }
	
	public ServerSideException(ServerSideErrors serverSideErrors) {
        this.setServerSideErrors(serverSideErrors);
    }

    public ServerSideException(String msg, ServerSideErrors serverSideErrors) {
        super(msg);
        this.setServerSideErrors(serverSideErrors);
    }

    public ServerSideException(String msg, Throwable e, ServerSideErrors serverSideErrors) {
        super(msg, e);
        this.setServerSideErrors(serverSideErrors);
    }
    
	public ServerSideException(Throwable e, ServerSideError serverSideError) {
        super(e);
        ServerSideErrors errorColl = new ServerSideErrors();
        errorColl.add(serverSideError);
        this.setServerSideErrors(errorColl);
    }

	public ServerSideException(ServerSideError error) {
        ServerSideErrors errorColl = new ServerSideErrors();
        errorColl.add(error);
        this.setServerSideErrors(errorColl);
	}

}