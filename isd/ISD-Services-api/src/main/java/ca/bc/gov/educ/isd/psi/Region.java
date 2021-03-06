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
 *  File:                Region.java
 *  Date of Last Commit: $Date::                                               $
 *  Revision Number:     $Rev::                                                $
 *  Last Commit by:      $Author::                                             $
 *
 * ***********************************************************************
 */
package ca.bc.gov.educ.isd.psi;

import java.io.Serializable;

/**
 *
 * @author CGI Information Management Consultants Inc.
 */
public interface Region extends Serializable {

    public String getCode();

    public void setCode(String code);

    public String getName();

    public void setName(String name);

    public Country getCountry();

    public void setCountry(Country country);

}
