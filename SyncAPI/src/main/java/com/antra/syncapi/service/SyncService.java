package com.antra.syncapi.service;

import com.antra.syncapi.pojo.CombinedResponse;
import com.antra.syncapi.pojo.ReportRequest;

public interface SyncService {
    CombinedResponse generateSyncFiles(ReportRequest rr);
}
