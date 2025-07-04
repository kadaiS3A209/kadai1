package model;

import java.util.Date;
import java.util.List;

public class LabTestOrderBean {
    private int labTestOrderId;
    private int consultationId;
    private String orderStatus;
    private Integer technicianId;
    private Date orderedAt;
    private Date completedAt;
    private List<LabTestItemBean> testItems; // この指示に含まれる検査項目のリスト
    // 以下、必要なGetters and Setters
}
