/*
 * *********************************************************************
 *  Copyright (c) 2017, Ministry of Education, BC.
 *
 *  All rights reserved.
 *    This information contained herein may not be used in whole
 *    or in part without the express written consent of the
 *    Government of British Columbia, Canada.
 *
 *  Revision Control Information
 *  File:                $Id::                                                 $
 *  Date of Last Commit: $Date::                                               $
 *  Revision Number:     $Rev::                                                $
 *  Last Commit by:      $Author::                                             $
 *
 * ***********************************************************************
 */
package ca.bc.gov.educ.isd.common.support.search;

import ca.bc.gov.educ.isd.common.SearchObjectFieldValue;
import java.io.Serializable;

/**
 * For searching using CriteriaBuilder.
 *
 * @author CGI Information Management Consultants Inc.
 */
public class Equal implements SearchObjectFieldValue, Serializable {

    private static final long serialVersionUID = 1L;
    private final String fieldName;
    private final Object value;

    public Equal(final String field, final Object value) {
        this.fieldName = field;
        this.value = value;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return fieldName + " = " + value;
    }
}
