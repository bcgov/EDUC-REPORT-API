/* *********************************************************************
 *  Copyright (c) 2016, Ministry of Education, BC.
 *
 *  All rights reserved.
 *    This information contained herein may not be used in whole
 *    or in part without the express written consent of the
 *    Government of British Columbia, Canada.
 *
 *  Revision Control Information
 *  File:                $Id:: StudentExamServiceImpl.java 12336 2019-12-13 19#$
 *  Date of Last Commit: $Date:: 2019-12-13 11:30:20 -0800 (Fri, 13 Dec 2019)  $
 *  Revision Number:     $Rev:: 12336                                          $
 *  Last Commit by:      $Author:: cfunk                                       $
 *
 * ********************************************************************** */
package ca.bc.gov.educ.isd.exam.impl;

import ca.bc.gov.educ.grad.dto.ReportData;
import ca.bc.gov.educ.isd.assessment.AssessmentCourseCode;
import ca.bc.gov.educ.isd.assessment.IncompleteAssessmentCode;
import ca.bc.gov.educ.isd.common.DataException;
import ca.bc.gov.educ.isd.common.DomainServiceException;
import static ca.bc.gov.educ.isd.common.support.impl.Roles.USER;
import ca.bc.gov.educ.isd.eis.EISException;
import ca.bc.gov.educ.isd.eis.trax.db.ExamResult;
import ca.bc.gov.educ.isd.eis.trax.db.ExamStudent;
import ca.bc.gov.educ.isd.eis.trax.db.TRAXAdapter;
import ca.bc.gov.educ.isd.exam.Exam;
import ca.bc.gov.educ.isd.exam.StudentExamResultsReport;
import ca.bc.gov.educ.isd.exam.StudentExamService;
import ca.bc.gov.educ.isd.reports.ProvincialExaminationReport;
import ca.bc.gov.educ.isd.reports.ReportDocument;
import ca.bc.gov.educ.isd.reports.ReportFormat;
import ca.bc.gov.educ.isd.reports.ReportService;
import ca.bc.gov.educ.isd.school.School;
import ca.bc.gov.educ.isd.student.PersonalEducationNumber;
import ca.bc.gov.educ.isd.student.Student;
import ca.bc.gov.educ.isd.student.StudentXRef;
import ca.bc.gov.educ.isd.student.StudentXRefService;
import ca.bc.gov.educ.isd.student.impl.PersonalEducationNumberSimple;
import ca.bc.gov.educ.isd.student.impl.SchoolImpl;
import ca.bc.gov.educ.isd.student.impl.StudentImpl;
import static ca.bc.gov.educ.isd.transcript.impl.constants.Roles.STUDENT_EXAM_REPORT;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import ca.bc.gov.educ.isd.traxadaptor.dao.utils.TRAXThreadDataUtility;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * The Exam Services is an implementation component of the broader Transcript
 * Services group. As part of the mid-tier service layer the buildReport method
 * called by the GUI (browser) to display the exam results. The display is a
 * rendered report generated by the report service populated with data from the
 * TRAX adaptor for the current user as determined by the student demographics
 * service. The report is rendered according to the requested MIME type; HTML,
 * PDF, or XML among others.
 * <p>
 * This service integrates with the Student Demographics, the TRAX adaptor, and
 * the Reporting Service.
 * <p>
 * The security roles required to build a transcript report are:
 * <ol>
 * <li>STUDENT_EXAM_REPORT</li>
 * <li>TRAX_READ</li>
 * <li>SXR_READ</li>
 * <li>SXR_SEARCH</li>
 * <li>USER_REPORTS_EXPORT</li>
 * <li>USER_REPORTS_PEAR</li>
 * <li>USER_PROFILE_SEARCH</li>
 * <li>USER_PROFILE_READ</li>
 * <li>USER</li>
 * </ol>
 * <p>
 * @author CGI Information Management Consultants Inc.
 */
@DeclareRoles({STUDENT_EXAM_REPORT, USER})
public class StudentExamServiceImpl implements StudentExamService, Serializable {
    
    private static final long serialVersionUID = 3L;
    
    private static final String CLASSNAME = StudentExamServiceImpl.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASSNAME);

    private TRAXAdapter traxAdapter;
    
    private ReportService reportService;
    
    private StudentXRefService studentXRefService;
    
    private List<AssessmentCourseCode> assessmentCodes;

    @Override
    @RolesAllowed({STUDENT_EXAM_REPORT, USER})
    public StudentExamResultsReport buildReport(final ReportFormat format)
            throws DomainServiceException {
        final String _m = "buildReport(ReportFormat)";
        LOG.entering(CLASSNAME, _m);

        // Access TRAX adaptor to obtain required data for PEN.
        final String penId = getStudentPENId();
        final ExamStudent examStudent = getTRAXExamStudent(penId);
        final List<ExamResult> examResults = readExamResults(penId);
        
        LOG.log(Level.FINE, "Retrieved the exam results from TRAX for PEN: {0}", penId);
        
        final PersonalEducationNumber pen = getStudentPEN();

        // Transfer TRAX data to other objects for reporting.
        final String logo = examStudent.getLogo();
        final Student student = adapt(pen, examStudent);
        final School school = adapt(examStudent);
        final Exam exam = adapt(examResults, examStudent.getReportDate());
        final Date updated = examStudent.getReportDate();

        // access reporting services to generate report
        StudentExamResultsReport report = createReport(
                format, student, school, logo, exam, updated
        );
        
        LOG.exiting(CLASSNAME, _m);
        return report;
    }
    
    @Override
    @RolesAllowed({STUDENT_EXAM_REPORT, USER})
    public Exam getExam() throws DomainServiceException {
        final String _m = "getExam()";
        LOG.entering(CLASSNAME, _m);
        
        final String pen = getStudentPENId();
        final Exam exam = getExam(pen);
        
        LOG.exiting(CLASSNAME, _m);
        return exam;
    }
    
    @Override
    @RolesAllowed({STUDENT_EXAM_REPORT, USER})
    public Exam getExam(final String pen) throws DomainServiceException {
        final String _m = "getExam()";
        LOG.entering(CLASSNAME, _m);
        
        final ExamStudent examStudent = getTRAXExamStudent(pen);
        final List<ExamResult> examResults = readExamResults(pen);
        final Exam exam = adapt(examResults, examStudent.getReportDate());
        
        LOG.exiting(CLASSNAME, _m);
        return exam;
    }
    
    private String getStudentPENId() throws DomainServiceException {
        final String _m = "getStudentPENId()";
        LOG.entering(CLASSNAME, _m);
        
        final PersonalEducationNumber pen = getStudentPEN();
        final String result = pen.getValue();
        
        LOG.exiting(CLASSNAME, _m);
        return result;
    }
    
    private PersonalEducationNumber getStudentPEN() throws DomainServiceException {
        final String _m = "getStudentPEN()";
        LOG.entering(CLASSNAME, _m);

        ReportData reportData = TRAXThreadDataUtility.getGenerateReportData();

        if (reportData == null) {
            EntityNotFoundException dse = new EntityNotFoundException(
                    "Report Data not exists for the current report generation");
            LOG.throwing(CLASSNAME, _m, dse);
            throw dse;
        }

        PersonalEducationNumberSimple pen = new PersonalEducationNumberSimple();
        pen.setPen(reportData.getDemographics().getPen());
        
        LOG.log(Level.FINE, "Confirmed the user is a student and retrieved the PEN: {0}.", pen);
        LOG.exiting(CLASSNAME, _m);
        return pen;
    }

    /**
     * Read the static student data from TRAX which is needed for the exam
     * service.
     *
     * @param pen
     * @return
     */
    private ExamStudent getTRAXExamStudent(final String pen)
            throws DomainServiceException {
        final String methodName = "getTRAXExamStudent(String)";
        LOG.entering(CLASSNAME, methodName, pen);
        ExamStudent studentInfo;
        try {
            studentInfo = traxAdapter.readStudent_Exam(pen);
            
            LOG.log(Level.FINER,
                    "Returned from TRAX to retrieve student info for PEN: {0}",
                    pen);
            
            if (studentInfo == null) {
                final String msg = "No student exam results in TRAX for PEN: ".concat(pen);
                final DomainServiceException de = new DomainServiceException(msg);
                LOG.throwing(CLASSNAME, methodName, de);
                throw de;
            } else {
                LOG.log(Level.FINEST, "Retrieved student info:");
                LOG.log(Level.FINEST, "{0} {1} {2}", new Object[]{studentInfo.getPen(),
                    studentInfo.getFirstName(), studentInfo.getMiddleName(), studentInfo.getLastName()});
            }
            
        } catch (EISException ex) {
            DataException dex = new DataException(
                    null,
                    null,
                    "Failed to access TRAX student info for PEN: " + pen,
                    ex);
            LOG.throwing(CLASSNAME, methodName, dex);
            throw dex;
        }
        
        LOG.log(Level.FINE, "Completed call to TRAX.");
        LOG.exiting(CLASSNAME, methodName);
        return studentInfo;
    }

    /**
     * Read the collection of exam results from the TRAX Adaptor which is
     * required for the exam service.
     *
     * @param pen Student PEN to use for retrieving examination results.
     * @return A list of exam results for the given student PEN.
     */
    private List<ExamResult> readExamResults(String pen) throws DataException {
        final String methodName = "readExamResults(String)";
        LOG.entering(CLASSNAME, methodName, pen);
        final List<ExamResult> results;
        
        try {
            results = traxAdapter.readCourses_Exam(pen);
            
            LOG.log(Level.FINER,
                    "Retrieved the collection of exam results from TRAX for PEN: {0}",
                    pen);
            
            if (results != null && !results.isEmpty()) {
                LOG.log(Level.FINEST, "Retrieved student exam results:");
                for (final ExamResult result : results) {
                    LOG.log(Level.FINEST, "{0} {1} {2}", new Object[]{result.getCourseName(), result.getFinalPercent(), result.getFinalLetterGrade()});
                }
            }
            
        } catch (EISException ex) {
            String msg = "Failed to access TRAX exam results data for student with PEN: ".concat(pen);
            DataException dex = new DataException(null, null, msg, ex);
            LOG.throwing(CLASSNAME, methodName, dex);
            throw dex;
        }
        
        LOG.log(Level.FINE, "Completed call to TRAX.");
        LOG.exiting(CLASSNAME, methodName);
        return results;
    }

    /**
     * Transfer the TRAX data from the data value object into a Student object.
     *
     * @param penObj PersonalEducationNumber student identifier PEN object
     * @param traxStudent
     *
     * @return student represents student business entity object
     */
    private Student adapt(
            final PersonalEducationNumber penObj,
            final ExamStudent traxStudent) {
        final String methodName = "adapt(PersonalEducationNumber, ExamStudent)";
        LOG.entering(CLASSNAME, methodName, new Object[]{penObj, traxStudent});

        //FIXME - coupling error components may not use the inner workings of other components.
        final StudentImpl student = new StudentImpl();
        student.setPen(penObj);
        student.setFirstName(traxStudent.getFirstName());
        student.setLastName(traxStudent.getLastName());
        student.setMiddleName(traxStudent.getMiddleName());
        student.setBirthdate(traxStudent.getBirthdate());
        
        LOG.logp(Level.FINE, CLASSNAME, methodName, "Populated the Student object with TRAX data.");
        LOG.exiting(CLASSNAME, methodName, student);
        return student;
    }

    /**
     * Transfer the TRAX data from the data value object into a School object.
     * <p>
     * @param traxStudent
     */
    private School adapt(final ExamStudent traxStudent) {
        final String methodName = "adapt(ExamStudent)";
        LOG.entering(CLASSNAME, methodName, traxStudent);
        
        final SchoolImpl school = new SchoolImpl();
        school.setMincode(traxStudent.getMincode());
        school.setName(traxStudent.getSchoolName());
        
        LOG.exiting(CLASSNAME, methodName);
        return school;
    }

    /**
     * Transfer the TRAX data from the collection of data value objects into a
     * collection of ExamResult objects.
     *
     * @param traxExamResults The results to adapt
     */
    private List<ca.bc.gov.educ.isd.exam.ExamResult> adapt(final List<ExamResult> traxExamResults) {
        final String methodName = "adapt(List<ExamResult>)";
        LOG.entering(CLASSNAME, methodName);
        
        final List<ca.bc.gov.educ.isd.exam.ExamResult> examResults = new ArrayList<>();
        
        if (traxExamResults != null) {
            for (final ExamResult traxResult : traxExamResults) {
                final ExamResultImpl eResult = new ExamResultImpl();
                eResult.setSessionDate(traxResult.getCourseSession());
                eResult.setTitle(traxResult.getCourseName());
                eResult.setCourseCode(traxResult.getCourseCode());
                
                final MarkImpl eMark = new MarkImpl();
                eMark.setSchoolPercent(traxResult.getSchoolPercent());
                eMark.setBestSchoolPercent(traxResult.getBestSchoolPercent());
                eMark.setExamPercent(traxResult.getExamPercent());
                eMark.setBestExamPercent(traxResult.getBestExamPercent());
                eMark.setFinalPercent(traxResult.getFinalPercent());
                eMark.setFinalLetterGrade(traxResult.getFinalLetterGrade());
                
                eResult.setExamMark(eMark);
                examResults.add(eResult);
            }
        }
        
        Collections.sort(examResults, createComparator());
        
        LOG.exiting(CLASSNAME, methodName);
        return examResults;
    }

    /**
     * Transfer the TRAX data from the collection of data value objects into a
     * collection of ExamResult objects.
     *
     * @param traxExamResults
     * @param issueDate
     */
    private Exam adapt(
            final List<ExamResult> traxExamResults,
            final Date issueDate) {
        final String methodName = "adapt(List<ExamResult>, Date)";
        LOG.entering(CLASSNAME, methodName);
        
        final List<ca.bc.gov.educ.isd.exam.ExamResult> examResults = adapt(traxExamResults);
        final ExamImpl exam = new ExamImpl();
        exam.setIssueDate(issueDate);
        exam.setResults(examResults);
        
        LOG.exiting(CLASSNAME, methodName);
        return exam;
    }
    
    private Comparator<ca.bc.gov.educ.isd.exam.ExamResult> createComparator() {
        return new Comparator<ca.bc.gov.educ.isd.exam.ExamResult>() {
            @Override
            public int compare(
                    final ca.bc.gov.educ.isd.exam.ExamResult e1,
                    final ca.bc.gov.educ.isd.exam.ExamResult e2) {
                return new CompareToBuilder()
                        .append(e2.getSessionDate(), e1.getSessionDate())
                        .toComparison();
            }
        };
    }

    /**
     * Generate the ProvincialExaminationReport.
     *
     * @param reportFormat
     * @param student
     * @param school
     * @param logo
     * @param exam
     * @return
     * @throws DomainServiceException
     */
    private StudentExamResultsReport createReport(
            final ReportFormat reportFormat,
            final Student student,
            final School school,
            final String logo,
            final Exam exam,
            final Date updateDt) throws DomainServiceException {
        final String _m = "createReport(ReportFormat, Student, School, String, Exam, Date)";
        LOG.entering(CLASSNAME, _m);
        
        final ProvincialExaminationReport examReport = reportService.createProvincialExaminationReport();
        examReport.setStudent(student);
        examReport.setSchool(school, logo);
        examReport.setExamination(exam);
        examReport.setReportDate(updateDt);
        examReport.setFormat(reportFormat);
        examReport.setAppendUniqueSuffix(true);
        
        StudentExamResultsReport report = null;
        
        try {
            final ReportDocument document = reportService.export(examReport);
            final String filename = examReport.getFilename();
            final byte[] content = document.asBytes();
            
            if (isEmpty(content)) {
                final String msg = "The provincial examination report is empty.";
                final DomainServiceException dse = new DomainServiceException(msg);
                LOG.throwing(CLASSNAME, _m, dse);
                throw dse;
            }
            
            report = new StudentExamResultsReportImpl(
                    content, reportFormat, filename, "PEAR"
            );
        } catch (final IOException ex) {
            LOG.log(Level.SEVERE,
                    "Failed to generate the provincial examination report.", ex);
        }
        
        LOG.exiting(CLASSNAME, _m);
        return report;
    }
    
    @Override
    @RolesAllowed({STUDENT_EXAM_REPORT, USER})
    public Boolean hasNumeracyAssessments() throws DomainServiceException {
        assessmentCodes = AssessmentCourseCode.getNumeracyCodes();
        return hasAssessments();
    }
    
    @Override
    @RolesAllowed({STUDENT_EXAM_REPORT, USER})
    public Boolean hasLiteracyAssessments() throws DomainServiceException {
        assessmentCodes = AssessmentCourseCode.getLiteracyCodes();
        return hasAssessments();
    }
    
    private Boolean hasAssessments() throws DomainServiceException {
        Boolean retVal = false;
        Exam exam = getExam();
        if (exam != null && assessmentCodes != null) {
            List<ca.bc.gov.educ.isd.exam.ExamResult> results = exam.getResults();
            for (ca.bc.gov.educ.isd.exam.ExamResult result : results) {
                String finalPercent = result.getMark().getFinalPercent();
                String courseCode = result.getCourseCode();
                if (courseCode != null) {
                    finalPercent = finalPercent.trim();
                    courseCode = courseCode.trim();
                    if (!isAssessmentComplete(finalPercent)) {
                        continue;
                    }
                    if (assessmentCodes.contains(AssessmentCourseCode.fromValue(courseCode))) {
                        retVal = true;
                        break;
                    }
                }
            }
        } else {
            LOG.log(Level.WARNING, "Could not check assessments.");
        }
        if (retVal == false) { 
            LOG.log(Level.INFO, "No assessments found for this student.");
        }
        return retVal;
    }
    
    
    private boolean isAssessmentComplete(final String finalPercent) {
        for (IncompleteAssessmentCode i : IncompleteAssessmentCode.values()) {
            if (finalPercent.equals(i.toString())) {
                return false;
            }
        }
        return true;
    }
}
