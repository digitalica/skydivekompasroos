package nl.digitalica.skydivekompasroos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpecificCanopy extends CanopyBase {

	final static String SETTING_SPECIFIC_CANOPY_SIZE = "Specific_Size_";
	final static String SETTING_SPECIFIC_CANOPY_TYPEID = "Specific_TypeId_";
	final static String SETTING_SPECIFIC_CANOPY_TYPENAME = "Specific_TypeName_";
	final static String SETTING_SPECIFIC_CANOPY_TYPECATEGORY = "Specific_TypeCategory_";
	final static String SETTING_SPECIFIC_CANOPY_REMARKS = "Specific_Remarks_";

	final public static int MAXSPECIFICCANOPIES = 10;

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

	/**
	 * Returns the specific canopy at specified position in list (first is 1)
	 * 
	 * @param i
	 * @param c
	 * @return
	 */
	static public SpecificCanopy getSpecificCanopy(Context c, int i) {
		List<SpecificCanopy> spcList = getSpecificCanopiesInList(c);
		return spcList.get(i - 1);
	}

	/**
	 * Returns the saved specific canopies in a list
	 * 
	 * @param c
	 * @return
	 */
	static public List<SpecificCanopy> getSpecificCanopiesInList(Context c) {
		List<SpecificCanopy> specificCanopies = new ArrayList<SpecificCanopy>();
		int index = 1;
		int size;
		String typeId;
		String typeName;
		int typeCategory;
		String remarks;
		do {
			SharedPreferences prefs = c.getSharedPreferences(
					KompasroosBaseActivity.KOMPASROOSPREFS,
					Context.MODE_PRIVATE);
			String nr = Integer.toString(index);
			size = prefs.getInt(SETTING_SPECIFIC_CANOPY_SIZE + nr, 0);
			typeId = prefs.getString(SETTING_SPECIFIC_CANOPY_TYPEID + nr, "");
			typeName = prefs.getString(SETTING_SPECIFIC_CANOPY_TYPENAME + nr,
					"");
			typeCategory = prefs.getInt(SETTING_SPECIFIC_CANOPY_TYPECATEGORY
					+ nr, 0);
			remarks = prefs.getString(SETTING_SPECIFIC_CANOPY_REMARKS + nr, "");
			if (!typeId.equals("")) {
				SpecificCanopy spc = new SpecificCanopy(index,
						UUID.fromString(typeId), size, typeName, typeCategory,
						remarks);
				specificCanopies.add(spc);
			}
			index++;
		} while (!typeId.equals(""));
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

	/**
	 * Saves one (potentially new) specific canopy
	 * 
	 * @param index
	 * @param size
	 * @param typeId
	 * @param remarks
	 */
	public void save(Context c) {
		String nr = Integer.toString(this.id);
		SharedPreferences prefs = c.getSharedPreferences(
				KompasroosBaseActivity.KOMPASROOSPREFS, Context.MODE_PRIVATE);
		CanopyType ct = CanopyType.getCanopy(this.typeId, c);
		Editor e = prefs.edit();
		e.putInt(SETTING_SPECIFIC_CANOPY_SIZE + nr, this.size);
		e.putString(SETTING_SPECIFIC_CANOPY_TYPEID + nr, this.typeId.toString());
		e.putString(SETTING_SPECIFIC_CANOPY_TYPENAME + nr, ct.name);
		e.putInt(SETTING_SPECIFIC_CANOPY_TYPECATEGORY + nr, ct.category);
		e.putString(SETTING_SPECIFIC_CANOPY_REMARKS + nr, this.remarks);
		e.commit();
	}

	/**
	 * Deletes a specific canopy (and moves te ones after that)
	 * 
	 * @param index
	 */
	public void Delete(Context c, int indexToDelete) {
		SharedPreferences prefs = c.getSharedPreferences(
				KompasroosBaseActivity.KOMPASROOSPREFS, Context.MODE_PRIVATE);
		for (int index = 1; index < MAXSPECIFICCANOPIES - 1; index++) {
			String nr = Integer.toString(index);
			String next = Integer.toString(index + 1);
			int size = prefs.getInt(SETTING_SPECIFIC_CANOPY_SIZE + next, 0);
			String type = prefs.getString(SETTING_SPECIFIC_CANOPY_SIZE + next,
					"");
			String remarks = prefs.getString(SETTING_SPECIFIC_CANOPY_REMARKS
					+ next, "");
			if (!type.equals("")) {
				Editor e = prefs.edit();
				e.putInt(SETTING_SPECIFIC_CANOPY_SIZE + nr, size);
				e.putString(SETTING_SPECIFIC_CANOPY_TYPEID + nr, type);
				e.putString(SETTING_SPECIFIC_CANOPY_REMARKS + nr, remarks);
				e.commit();
			}
		}
	}

	/**
	 * Deletes all specific canopies (only used in test)
	 */
	public static void DeleteAll(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(
				KompasroosBaseActivity.KOMPASROOSPREFS, Context.MODE_PRIVATE);
		for (int index = 1; index < MAXSPECIFICCANOPIES; index++) {
			String nr = Integer.toString(index);
			Editor e = prefs.edit();
			e.remove(SETTING_SPECIFIC_CANOPY_SIZE + nr);
			e.remove(SETTING_SPECIFIC_CANOPY_TYPEID + nr);
			e.remove(SETTING_SPECIFIC_CANOPY_REMARKS + nr);
			e.commit();
		}
	}
}
