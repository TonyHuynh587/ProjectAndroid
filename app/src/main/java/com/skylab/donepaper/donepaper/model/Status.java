package com.skylab.donepaper.donepaper.model;

public enum Status {
    ORDER_UNPAID,
    ORDER_PAID,
    ORDER_INQUIRED,
    ORDER_IN_PROGRESS,
    ORDER_ON_HOLD,
    ORDER_COMPLETE,
    ORDER_DELIVERED,
    ORDER_REVISION_REQUESTED,
    ORDER_IN_REVISION,
    ORDER_CLOSED,
    ORDER_CANCELED;

    @Override
    public String toString() {
        switch (this) {
            case ORDER_UNPAID:
                return "Unpaid";
            case ORDER_PAID:
                return "Paid";
            case ORDER_INQUIRED:
                return "Inquired";
            case ORDER_IN_PROGRESS:
                return "In progress";
            case ORDER_ON_HOLD:
                return "On hold";
            case ORDER_COMPLETE:
                return "Complete";
            case ORDER_DELIVERED:
                return "Delivered";
            case ORDER_REVISION_REQUESTED:
                return "Revision requested";
            case ORDER_IN_REVISION:
                return "In revision";
            case ORDER_CLOSED:
                return "Closed";
            case ORDER_CANCELED:
                return "Canceled";
            default:
                return "";
        }
    }
}
