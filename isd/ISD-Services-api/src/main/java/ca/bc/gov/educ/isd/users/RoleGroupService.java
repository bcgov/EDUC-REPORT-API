/* *********************************************************************
 *  Copyright (c) 2016, Ministry of Education, BC.
 * 
 *  All rights reserved.
 *    This information contained herein may not be used in whole 
 *    or in part without the express written consent of the 
 *    Government of British Columbia, Canada. 
 * 
 *  Revision Control Information 
 *  File:                        RoleGroupService.java 
 *  Date of Last Commit: $Date:: 2016-08-25 10:55:55 -0700 (Thu, 25 Aug 2016)  $  
 *  Revision Number:     $Rev:: 3100                                           $  
 *  Last Commit by:      $Author:: cprince                                     $ 
 *  
 * ********************************************************************** */

package ca.bc.gov.educ.isd.users;

import ca.bc.gov.educ.isd.common.CommonEntityService;
import ca.bc.gov.educ.isd.common.DataException;

/**
 * Definition of a service that facilitates management of role groups.
 *
 * @author CGI Information Management Consultants Inc.
 */
public interface RoleGroupService extends CommonEntityService<RoleGroup, RoleGroupSearchResult> {

    RoleGroup create(final String name, final String description) throws DataException;

}
