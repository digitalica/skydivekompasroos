package nl.digitalica.skydivekompasroos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

public class SpecificCanopy extends CanopyBase {

	final public int MAXSPECIFICCANOPIES = 10;

	public int id;
	public UUID typeId;
	public int size;
	// note: the values below are cached, from canopytype
	public String typeName;
	public int typeCategory;
	public String remarks;

	public SpecificCanopy(int sId, UUID sTypeId, int sSize, String sName,
			int sCategory, String sRemarks) {
		this.id = sId;
		this.typeId = sTypeId;
		this.size = sSize;
		this.typeName = sName;
		this.typeCategory = sCategory;
		this.remarks = sRemarks;
	}

	static public SpecificCanopy getSpecificCanopy(int i, Context c) {
		List<SpecificCanopy> spcList = getSpecificCanopiesInList(c);
		return spcList.get(i - 1);
	}

	static public List<SpecificCanopy> getSpecificCanopiesInList(Context c) {
		List<SpecificCanopy> specificCanopies = new ArrayList<SpecificCanopy>();
		// TODO: add List

		SpecificCanopy canopy1 = new SpecificCanopy(1, UUID.randomUUID(), 230,
				"PD", 1, "eigen");
		SpecificCanopy canopy2 = new SpecificCanopy(2, UUID.randomUUID(), 170,
				"Stiletto", 4, "cool");
		SpecificCanopy canopy3 = new SpecificCanopy(3, UUID.randomUUID(), 120,
				"Katana", 5, "cool");
		specificCanopies.add(canopy1);
		specificCanopies.add(canopy2);
		specificCanopies.add(canopy3);

		return specificCanopies;
	}

	/***
	 * Determines if a specific canopy is acceptable for a given jumper
	 * 
	 * @param jumperCategory
	 * @param exitWeightInKg
	 * @return
	 */
	public AcceptabilityEnum acceptablility(int jumperCategory,
			int exitWeightInKg) {
		if (jumperCategory < this.typeCategory)
			return AcceptabilityEnum.CATEGORYTOOHIGH; // not acceptable
		if (this.size < Calculation.minArea(jumperCategory, exitWeightInKg))
			return AcceptabilityEnum.CATEGORYTOOHIGH;
		return AcceptabilityEnum.ACCEPTABLE;
	}

}
