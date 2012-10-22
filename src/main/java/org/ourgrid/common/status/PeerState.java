package org.ourgrid.common.status;

public enum PeerState {
	
	DOWN {
	    public String toString() {
	        return "DOWN";
	    }
	},
	NOT_LOGGED {
	    public String toString() {
	        return "NOT LOGGED IN";
	    }
	},
	LOGGED {
	    public String toString() {
	        return "LOGGED";
	    }
	}
}
