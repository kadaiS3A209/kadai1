package model;

import java.util.Date;

public class XrayOrderBean {
    private int xrayOrderId;
    private int consultationId;
    private String orderStatus;
    private Integer technicianId; // 担当者が未定の場合はnullになるためInteger
    private Date orderedAt;
    private Date completedAt;
    // 以下、必要なGetters and Setters
}
