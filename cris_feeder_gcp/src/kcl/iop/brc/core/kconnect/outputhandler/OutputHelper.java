package kcl.iop.brc.core.kconnect.outputhandler;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.annotation.AnnotationSetImpl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kcl.iop.brc.core.kconnect.outputhandler.YodieOutputHandler.OutputData;
import kcl.iop.brc.core.kconnect.outputhandler.YodieOutputHandler.OutputSetting;
import kcl.iop.brc.core.kconnect.utils.JSONUtils;

public class OutputHelper {
	/**
	 * generate output json string of annotations that need to be saved
	 * @param anns
	 * @param doc
	 * @param keepCUIs
	 * @return
	 */
	public static String getOutputAnnJSON(OutputSetting[] anns, Document doc, String keepCUIs){
		if (anns != null){
			List<AnnotationSet> outputAnns = new LinkedList<AnnotationSet>();
			
			//output based on settings
			for(OutputSetting os : anns){
				AnnotationSet as = doc.getAnnotations(os.getAnnotationSet());
				for (String type : os.getAnnotationType()){
					AnnotationSet annSet = keepAnnotationsByCUIs(as.get(type), keepCUIs);
					outputAnns.add(annSet);
				}
			}
			
			OutputData od = new OutputData();
			if (doc.getFeatures()!=null && doc.getFeatures().get("id")!=null)
					od.setDocId(doc.getFeatures().get("id").toString());
			else
				od.setDocId(doc.getName());
			if (doc.getFeatures()!=null && doc.getFeatures().get("brcid")!=null)
				od.setBrcId(doc.getFeatures().get("brcid").toString());
			od.setAnnotations(outputAnns);
			return JSONUtils.toJSON(od) + "\n";
		}
		return null;
	}
	
	/**
	 * only keep annotations that are in the given CUI list
	 * @param annSet
	 * @param cuis
	 * @return
	 */
	public static AnnotationSet keepAnnotationsByCUIs(AnnotationSet annSet, String cuis){
		if (cuis == null || cuis.length() == 0)
			return annSet;
		AnnotationSetImpl asi = new AnnotationSetImpl(annSet);
		Iterator<Annotation> annIt = asi.iterator();
		List<Annotation> anns2remove = new LinkedList<Annotation>();
		while(annIt.hasNext()){
			Annotation ann = annIt.next();
			FeatureMap fm = ann.getFeatures();
			if (fm.containsKey("inst")){
				if (cuis != null && cuis.indexOf(fm.get("inst").toString()) < 0){
					anns2remove.add(ann);
				}
			}
		}
		for(Annotation ann: anns2remove){
			asi.remove(ann);
		}
		return asi;
	}
}
