/*****************************************************************************
 * Copyright (c) 2009
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com>
 *     Jawher Moussa <jawher.moussa@gmail.com>
 *     Nicolas Inchauspe <nicolas.inchauspe@gmail.com>
 *     Pascal Leclercq <pascal.leclercq@gmail.com>
 *******************************************************************************/
package org.dynaresume.hr.service;

import java.util.List;

import org.dynaresume.hr.domain.Competence;
import org.dynaresume.hr.domain.Diploma;
import org.dynaresume.hr.domain.Experience;
import org.dynaresume.hr.domain.Resume;
public interface HRService {

	//public List<Resume> findAll();
	
	public Resume saveResume(Resume resume);
	
	
	public List<Resume> searchByCriteria(List<Competence> competences,List<Diploma> diplomas, List<Experience> experiences);
}
