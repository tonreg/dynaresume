/*****************************************************************************
* Copyright (c) 2009
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Angelo Zerr <angelo.zerr@gmail.com>
*     Pascal Leclercq <pascal.leclercq@gmail.com>
*******************************************************************************/

package org.akrogen.dynaresume.repository.hibernate;

import java.util.List;

import org.akrogen.dynaresume.domain.ModeleRTF;
import org.akrogen.dynaresume.repository.IModeleRTFDAO;
import org.springframework.stereotype.Repository;

@Repository("modeleRTFDAO")
public class ModeleRTFHibernateDAO<T> extends AbstractDynaResumeDAO<T> implements IModeleRTFDAO<T> {

//	protected String getHQLQuery(BaseCriteria c) {
//		ModeleRTFCriteria criteria = (ModeleRTFCriteria)c;
//		// Prepare HQL Query
//		StringBuffer query = new StringBuffer("");
//		query.append("from ModeleRTF ");
//		if (criteria != null) {
//			// WHERE
//			query.append(" where 1=1 ");	
//		}
//		return query.toString();
//	}
	
	public List<T> findAll() {
		// TODO Auto-generated method stub
		return super.findAll(ModeleRTF.class);
	}
}
