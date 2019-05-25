package application;

public class PatTableRow {
	private String register_num; // in register_info
	private String pat_name; // in patient_info
	private String reg_datetime; // in register_info
	private String expert; // in registration_info

	public PatTableRow(String register_num, String pat_name, String reg_datetime, String expert) {
		this.register_num = register_num;
		this.pat_name = pat_name;
		this.reg_datetime = reg_datetime;
		this.expert = expert;
	}

	public String getRegister_num() {
		return register_num;
	}

	public void setRegister_num(String register_num) {
		this.register_num = register_num;
	}

	public String getPat_name() {
		return pat_name;
	}

	public void setPat_name(String pat_name) {
		this.pat_name = pat_name;
	}

	public String getReg_datetime() {
		return reg_datetime;
	}

	public void setReg_datetime(String reg_datetime) {
		this.reg_datetime = reg_datetime;
	}

	public String getExpert() {
		return expert;
	}

	public void setExpert(String expert) {
		this.expert = expert;
	}
}
