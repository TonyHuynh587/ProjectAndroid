
package com.skylab.donepaper.donepaper.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormInfoData {

    @SerializedName("services_types")
    @Expose
    private ServicesTypes servicesTypes;
    @SerializedName("default_services_type")
    @Expose
    private String defaultServicesType;
    @SerializedName("paper_types")
    @Expose
    private List<String> paperTypes = null;
    @SerializedName("paper_subjects")
    @Expose
    private List<String> paperSubjects = null;
    @SerializedName("citation_styles")
    @Expose
    private List<String> citationStyles = null;
    @SerializedName("default_citation_style")
    @Expose
    private String defaultCitationStyle;
    @SerializedName("academic_levels")
    @Expose
    private AcademicLevels academicLevels;
    @SerializedName("default_academic_level")
    @Expose
    private String defaultAcademicLevel;
    @SerializedName("spacing_options")
    @Expose
    private SpacingOptions spacingOptions;
    @SerializedName("default_spacing_option")
    @Expose
    private String defaultSpacingOption;
    @SerializedName("spacing_multiplications")
    @Expose
    private SpacingMultiplications spacingMultiplications;
    @SerializedName("words_per_page")
    @Expose
    private int wordsPerPage;
    @SerializedName("default_number_of_pages")
    @Expose
    private int defaultNumberOfPages;
    @SerializedName("pricing")
    @Expose
    private Pricing pricing;
    @SerializedName("deadlines")
    @Expose
    private Deadlines deadlines;
    @SerializedName("allowed_file_extensions")
    @Expose
    private List<String> allowedFileExtensions = null;
    @SerializedName("max_total_file_size")
    @Expose
    private Integer maxTotalFileSize;

    public ServicesTypes getServicesTypes() {
        return servicesTypes;
    }

    public String getDefaultServicesType() {
        return defaultServicesType;
    }

    public void setDefaultServicesType(String defaultServicesType) {
        this.defaultServicesType = defaultServicesType;
    }

    public List<String> getPaperTypes() {
        return paperTypes;
    }

    public List<String> getPaperSubjects() {
        return paperSubjects;
    }

    public List<String> getCitationStyles() {
        return citationStyles;
    }

    public String getDefaultCitationStyle() {
        return defaultCitationStyle;
    }

    public void setDefaultCitationStyle(String defaultCitationStyle) {
        this.defaultCitationStyle = defaultCitationStyle;
    }

    public AcademicLevels getAcademicLevels() {
        return academicLevels;
    }

    public String getDefaultAcademicLevel() {
        return defaultAcademicLevel;
    }

    public void setDefaultAcademicLevel(String defaultAcademicLevel) {
        this.defaultAcademicLevel = defaultAcademicLevel;
    }

    public SpacingOptions getSpacingOptions() {
        return spacingOptions;
    }

    public String getDefaultSpacingOption() {
        return defaultSpacingOption;
    }

    public void setDefaultSpacingOption(String defaultSpacingOption) {
        this.defaultSpacingOption = defaultSpacingOption;
    }

    public SpacingMultiplications getSpacingMultiplications() {
        return spacingMultiplications;
    }

    public int getWordsPerPage() {
        return wordsPerPage;
    }

    public int getDefaultNumberOfPages() {
        return defaultNumberOfPages;
    }

    public void setDefaultNumberOfPages(int defaultNumberOfPages) {
        this.defaultNumberOfPages = defaultNumberOfPages;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public Deadlines getDeadlines() {
        return deadlines;
    }

    public List<String> getAllowedFileExtensions() {
        return allowedFileExtensions;
    }

    public Integer getMaxTotalFileSize() {
        return maxTotalFileSize;
    }
}
