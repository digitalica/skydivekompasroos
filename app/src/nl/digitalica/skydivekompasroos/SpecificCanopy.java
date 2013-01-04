package nl.digitalica.skydivekompasroos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

public class SpecificCanopy extends CanopyBase {

	final public int MAXSPECIFICCANOPIES = 10;

	public UUID typeId;
	public int size;
	// note: the value below is cached, from canopytype
	public int category;
	public String remarks;

	public SpecificCanopy(UUID sTypeId, int sSize, int sCategory, String sRemarks) {
		this.typeId = sTypeId;
		this.size = sSize;
		this.category = sCategory;
		this.remarks = sRemarks;
	}

	static public List<SpecificCanopy> getSpecificCanopiesInList(Context c) {
		List<SpecificCanopy> specificCanopies = new ArrayList<SpecificCanopy>();
		// TODO: add List
		return specificCanopies;
	}

	/***
	 * Determines if a specific canopy is acceptable for a given jumper
	 * 
	 * @param jumperCategory
	 * @param exitWeightInKg
	 * @return
	 * 
	 *         TODO: should return an enum, not an int!!!!!
	 */
	public int acceptablility(int jumperCategory, int exitWeightInKg) {
		if (jumperCategory < this.category)
			return CATEGORYTOOHIGH; // not acceptable
		if (this.size < Calculation.minArea(jumperCategory, exitWeightInKg))
			return NEEDEDSIZENOTAVAILABLE;
		return ACCEPTABLE;
	}

}
