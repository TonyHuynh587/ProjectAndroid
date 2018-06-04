package com.skylab.donepaper.donepaper.model;

import java.util.List;
import com.skylab.donepaper.donepaper.rest.model.OrderData;

public class SectionModelData {
    private String headerTitle;
    private List<OrderData> allItemsInSection;

    public SectionModelData() {
    }

    public SectionModelData(String headerTitle, List<OrderData> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }

    public List<OrderData> getAllItemsInSection() {
        return allItemsInSection;
    }


    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public void setAllItemsInSection(List<OrderData> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}
