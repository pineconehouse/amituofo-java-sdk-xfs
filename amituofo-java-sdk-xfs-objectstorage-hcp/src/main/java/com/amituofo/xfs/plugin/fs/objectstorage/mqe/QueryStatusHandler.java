package com.amituofo.xfs.plugin.fs.objectstorage.mqe;

import com.amituofo.common.define.HandleFeedback;
import com.hitachivantara.hcp.query.model.QueryStatus;

public interface QueryStatusHandler {
	HandleFeedback queryStatusChanged(QueryStatus status);
}
