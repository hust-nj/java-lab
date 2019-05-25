package application;

public class IncomeTableRow {
	private String office_name;
	private String doc_num;
	private String doc_name;
	private String expert;
	private String allpatcount;
	private String allincome;

	public IncomeTableRow(String office_name, String doc_num, String doc_name, String expert, String allpatcount,
			String allincome) {
		this.office_name = office_name;
		this.doc_num = doc_num;
		this.doc_name = doc_name;
		this.expert = expert;
		this.allpatcount = allpatcount;
		this.allincome = allincome;
	}

	public String getOffice_name() {
		return office_name;
	}

	public void setOffice_name(String office_name) {
		this.office_name = office_name;
	}

	public String getDoc_num() {
		return doc_num;
	}

	public void setDoc_num(String doc_num) {
		this.doc_num = doc_num;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public String getExpert() {
		return expert;
	}

	public void setExpert(String expert) {
		this.expert = expert;
	}

	public String getAllpatcount() {
		return allpatcount;
	}

	public void setAllpatcount(String allpatcount) {
		this.allpatcount = allpatcount;
	}

	public String getAllincome() {
		return allincome;
	}

	public void setAllincome(String allincome) {
		this.allincome = allincome;
	}
}
