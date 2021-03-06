/*
 * *********************************************************************
 *  Copyright (c) 2016, Ministry of Education, BC.
 *
 *  All rights reserved.
 *    This information contained herein may not be used in whole
 *    or in part without the express written consent of the
 *    Government of British Columbia, Canada.
 *
 *  Revision Control Information
 *  File:                TswStudPSIEntity.java
 *  Date of Last Commit: $Date::                                               $
 *  Revision Number:     $Rev::                                                $
 *  Last Commit by:      $Author::                                             $
 *
 * ***********************************************************************
 */
package ca.bc.gov.educ.isd.traxadaptor.dao.tsw.impl;

import ca.bc.gov.educ.isd.eis.trax.db.TSWStud;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.logging.Logger;

import static ca.bc.gov.educ.isd.eis.common.DatabaseConstants.*;

/**
 * An autogenerated class that represents the TSW_STUD_PSI_VW entity in the
 * database. An embedded composite primary key has been added since this data
 * does not have a unique primary key column.
 *
 * @author CGI Information Management Consultants Inc.
 */
public class TswStudPSIEntity implements TSWStud {

    // ------------------ CONSTANT(S) AND FINAL(S)
    private static final long serialVersionUID = 1L;
    private static final String CLASSNAME = TswStudPSIEntity.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASSNAME);

    protected TswStudPSIId tswStudPsiId;

    private Date dateEntered;

    private Character psiStatus;

    // ------------------ CONSTRUCTOR(S)
    /**
     * Empty constructor that assigns the creation data for the object.
     */
    public TswStudPSIEntity() {
        this.dateEntered = new Date();
    }

    /**
     * Constructor that initializes the object with a given primary key.
     *
     * @param tswStudPsiId object that contains the primary key to be used for
     * the new object.
     */
    public TswStudPSIEntity(final TswStudPSIId tswStudPsiId) {
        this.tswStudPsiId = tswStudPsiId;
        this.dateEntered = new Date();
    }

    /**
     * Constructor that initializes the object with a primary key using the
     * given parameters.
     *
     * @param studNo Number that identify the student
     * @param psiCode Code that identify the PSI.
     * @param psiYear Year in which the choice was made.
     */
    public TswStudPSIEntity(String studNo, String psiCode, String psiYear) {
        this.tswStudPsiId = new TswStudPSIId(studNo, psiCode, psiYear);
        this.dateEntered = new Date();
    }

    /**
     * Constructor that initializes the object with a status and primary key
     * using the given parameters.
     *
     * @param studNo Number that identify the student
     * @param psiCode Code that identify the PSI.
     * @param psiYear Year in which the choice was made.
     * @param status Status of the PSI choice.
     */
    public TswStudPSIEntity(String studNo, String psiCode, String psiYear, Character status) {
        this.tswStudPsiId = new TswStudPSIId(studNo, psiCode, psiYear);
        this.psiStatus = status;
        this.dateEntered = new Date();
    }

    // ------------------ GETTER(S) AND SETTER(S)
    /**
     * Returns the student number (PEN) associated with a PSI.
     *
     * @return A personal education number.
     */
    @Override
    public String getStudNo() {
        return getTswStudPSIId().getStudNo();
    }

    /**
     * Returns the Post-Secondary Institution code associated with a student.
     *
     * @return A PSI code.
     */
    @Override
    public String getPsiCode() {
        return getTswStudPSIId().getPsiCode();
    }

    /**
     * Returns the Post-Secondary Institution year associated with a student.
     *
     * @return A four digit year.
     */
    @Override
    public String getPsiYear() {
        return getTswStudPSIId().getPsiYear();
    }

    /**
     * Gets the primary key.
     *
     * @return an object that contains the primary key.
     */
    public TswStudPSIId getTswStudPSIId() {
        return tswStudPsiId;
    }

    /**
     * Sets the primary key.
     *
     * @param tswStudPsiId primary key to be assigned.
     */
    public void setTswStudPSIId(TswStudPSIId tswStudPsiId) {
        this.tswStudPsiId = tswStudPsiId;
    }

    /**
     * Gets the creation/update Date of the object
     *
     * @return the creation date of the object.
     */
    @Override
    public Date getDateEntered() {
        return dateEntered;
    }

    /**
     * Sets the Creation Date of the Object.
     *
     * @param dateEntered the new date of the creation/update
     */
    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }

    /**
     * Gets the status of the TWS Stud object.
     *
     * @return the status of the PSI Choice object.
     */
    @Override
    public Character getPsiStatus() {
        return psiStatus;
    }

    /**
     * Sets the status of the TWS Stud object
     *
     * @param psiStatus the new status of the PSI Choice object.
     */
    @Override
    public void setPsiStatus(Character psiStatus) {
        this.psiStatus = psiStatus;
    }

    // ------------------ METHOD(S)
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof TswPSIChoiceEntity)) {
            return false;
        }
        final TswStudPSIEntity other = (TswStudPSIEntity) object;
        return this.tswStudPsiId.equals(other.tswStudPsiId);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tswStudPsiId != null ? tswStudPsiId.hashCode() : 0);
        return hash;
    }

}
