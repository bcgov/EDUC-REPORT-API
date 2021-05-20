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
 *  File:                TswTxPsiEntity.java
 *  Date of Last Commit: $Date::                                               $
 *  Revision Number:     $Rev::                                                $
 *  Last Commit by:      $Author::                                             $
 *
 * ***********************************************************************
 */
package ca.bc.gov.educ.isd.traxadaptor.dao.tsw.impl;

import static ca.bc.gov.educ.isd.eis.common.DatabaseConstants.*;
import ca.bc.gov.educ.isd.eis.trax.db.TSWTxPSI;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An autogenerated class that represents the TSW_TX_PSI_VW view in the
 * database. An embedded composite primary key has been added since this view
 * does not have a unique primary key column.
 *
 * @author CGI Information Management Consultants Inc.
 */
@Entity
@Table(name = ENTITY_STUDENT_TRANSCRIPT_PSI)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TswTxPsiEntity.findAll", query = "SELECT t FROM TswTxPsiEntity t"),
    @NamedQuery(name = "TswTxPsiEntity.findByTxId", query = "SELECT t FROM TswTxPsiEntity t WHERE t.tswTxPsiId.txId = :txId"),
    @NamedQuery(name = "TswTxPsiEntity.findByStudNo", query = "SELECT t FROM TswTxPsiEntity t WHERE t.tswTxPsiId.studNo = :studNo"),
    @NamedQuery(name = "TswTxPsiEntity.findByPsiCode", query = "SELECT t FROM TswTxPsiEntity t WHERE t.tswTxPsiId.psiCode = :psiCode"),
    @NamedQuery(name = "TswTxPsiEntity.findByPsiStatus", query = "SELECT t FROM TswTxPsiEntity t WHERE t.psiStatus = :psiStatus"),
    @NamedQuery(name = "TswTxPsiEntity.findByProcessDate", query = "SELECT t FROM TswTxPsiEntity t WHERE t.processDate = :processDate")})
public class TswTxPsiEntity implements TSWTxPSI {

    // ------------------ CONSTANT(S) AND FINAL(S)
    private static final long serialVersionUID = 1L;
    private static final String CLASSNAME = TswTxPsiEntity.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASSNAME);

    @EmbeddedId
    protected TswTxPsiId tswTxPsiId;
    @Column(name = COL_PSI_STATUS)
    private Character psiStatus;
    @Column(name = COL_PSI_PROCESS_DATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date processDate;

    // ------------------ CONSTRUCTOR(S)
    /**
     * Empty constructor that assigns the creation data for the object.
     */
    public TswTxPsiEntity() {
    }

    /**
     * Constructor that initializes the object with a given primary key.
     *
     * @param tswTxPsiId object that contains the primary key to be used for the
     * new object.
     */
    public TswTxPsiEntity(TswTxPsiId tswTxPsiId) {
        this.tswTxPsiId = tswTxPsiId;
    }

    /**
     * Constructor that initializes the object with a primary key using the
     * given parameters.
     *
     * @param txId TX id that identify the transaction
     * @param studNo Number that identify the student
     * @param psiCode Code that identify the PSI.
     */
    public TswTxPsiEntity(String txId, String studNo, String psiCode) {
        this.tswTxPsiId = new TswTxPsiId(txId, studNo, psiCode);
    }

    /**
     * Constructor that initializes the object with a status and primary key
     * using the given parameters.
     *
     * @param txId TX id that identify the transaction
     * @param studNo Number that identify the student
     * @param psiCode Code that identify the PSI.
     * @param status Status of the TX PSI.
     */
    public TswTxPsiEntity(String txId, String studNo, String psiCode, Character status) {
        this.tswTxPsiId = new TswTxPsiId(txId, studNo, psiCode);
        this.psiStatus = status;

    }

    // ------------------ GETTER(S) AND SETTER(S)
    /**
     * Gets the primary key.
     *
     * @return an object that contains the primary key.
     */
    public TswTxPsiId getTswTxPsiId() {
        return tswTxPsiId;
    }

    /**
     * Sets the primary key.
     *
     * @param tswTxPsiId primary key to be assigned.
     */
    public void setTswTxPsiId(TswTxPsiId tswTxPsiId) {
        this.tswTxPsiId = tswTxPsiId;
    }

    /**
     * Gets the status of the TX PSI object.
     *
     * @return the status of the TX PSI object.
     */
    @Override
    public Character getPsiStatus() {
        return psiStatus;
    }

    /**
     * Sets the status of the TX PSI object
     *
     * @param psiStatus the new status of the TX PSI object.
     */
    @Override
    public void setPsiStatus(Character psiStatus) {
        this.psiStatus = psiStatus;
    }

    /**
     * Gets the creation/update Date of the object
     *
     * @return the creation date of the object.
     */
    @Override
    public Date getProcessDate() {
        return processDate;
    }

    /**
     * Sets the Creation Date of the Object.
     *
     * @param processDate the new date of the creation/update
     */
    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    @Override
    public String getTxId() {
        return getTswTxPsiId().getTxId();
    }

    @Override
    public String getStudNo() {
        return getTswTxPsiId().getStudNo();
    }

    @Override
    public String getPsiCode() {
        return getTswTxPsiId().getPsiCode();
    }

    // ------------------ METHOD(S)
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tswTxPsiId != null ? tswTxPsiId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof TswTxPsiEntity)) {
            return false;
        }
        final TswTxPsiEntity other = (TswTxPsiEntity) object;
        return this.tswTxPsiId.equals(other.tswTxPsiId);
    }
}