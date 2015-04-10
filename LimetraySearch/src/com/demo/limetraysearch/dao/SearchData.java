package com.demo.limetraysearch.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SEARCH_DATA.
 */
public class SearchData {

    private String DateCreated;
    private String IdStr;
    private String Text;
    private String Source;
    private Boolean IsTruncated;
    private Long InReplyToStatusId;
    private String InReplyToStatusIdStr;
    private Long InReplyToUserId;
    private String InReplyToUserIdStr;
    private String InReplyToScreenName;
    private Long id;

    public SearchData() {
    }

    public SearchData(Long id) {
        this.id = id;
    }

    public SearchData(String DateCreated, String IdStr, String Text, String Source, Boolean IsTruncated, Long InReplyToStatusId, String InReplyToStatusIdStr, Long InReplyToUserId, String InReplyToUserIdStr, String InReplyToScreenName, Long id) {
        this.DateCreated = DateCreated;
        this.IdStr = IdStr;
        this.Text = Text;
        this.Source = Source;
        this.IsTruncated = IsTruncated;
        this.InReplyToStatusId = InReplyToStatusId;
        this.InReplyToStatusIdStr = InReplyToStatusIdStr;
        this.InReplyToUserId = InReplyToUserId;
        this.InReplyToUserIdStr = InReplyToUserIdStr;
        this.InReplyToScreenName = InReplyToScreenName;
        this.id = id;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String DateCreated) {
        this.DateCreated = DateCreated;
    }

    public String getIdStr() {
        return IdStr;
    }

    public void setIdStr(String IdStr) {
        this.IdStr = IdStr;
    }

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public Boolean getIsTruncated() {
        return IsTruncated;
    }

    public void setIsTruncated(Boolean IsTruncated) {
        this.IsTruncated = IsTruncated;
    }

    public Long getInReplyToStatusId() {
        return InReplyToStatusId;
    }

    public void setInReplyToStatusId(Long InReplyToStatusId) {
        this.InReplyToStatusId = InReplyToStatusId;
    }

    public String getInReplyToStatusIdStr() {
        return InReplyToStatusIdStr;
    }

    public void setInReplyToStatusIdStr(String InReplyToStatusIdStr) {
        this.InReplyToStatusIdStr = InReplyToStatusIdStr;
    }

    public Long getInReplyToUserId() {
        return InReplyToUserId;
    }

    public void setInReplyToUserId(Long InReplyToUserId) {
        this.InReplyToUserId = InReplyToUserId;
    }

    public String getInReplyToUserIdStr() {
        return InReplyToUserIdStr;
    }

    public void setInReplyToUserIdStr(String InReplyToUserIdStr) {
        this.InReplyToUserIdStr = InReplyToUserIdStr;
    }

    public String getInReplyToScreenName() {
        return InReplyToScreenName;
    }

    public void setInReplyToScreenName(String InReplyToScreenName) {
        this.InReplyToScreenName = InReplyToScreenName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
