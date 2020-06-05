package com.summer.demo.bean;

import android.text.SpannableStringBuilder;

public class SpannableInfo {
    SpannableStringBuilder builder;
    SubjectInfo subjectInfo;

    public SpannableStringBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(SpannableStringBuilder builder) {
        this.builder = builder;
    }

    public SubjectInfo getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(SubjectInfo subjectInfo) {
        this.subjectInfo = subjectInfo;
    }
}
