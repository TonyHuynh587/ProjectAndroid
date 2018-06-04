package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubjectsLibraryResponse {

    @SerializedName("subjects")
    @Expose
    private List<LibrarySubjectItem> listSubjecLibrary;

    public List<LibrarySubjectItem> getListSubjecLibrary() {
        return listSubjecLibrary;
    }
}
