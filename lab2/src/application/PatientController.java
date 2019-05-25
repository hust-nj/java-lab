package application;

import java.awt.print.Printable;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.mysql.cj.jdbc.ha.BalanceStrategy;

import java.util.Iterator;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import jdk.internal.dynalink.beans.StaticClass;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import java.io.IOException;
import java.net.Inet4Address;
//import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
//import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;

import javafx.scene.control.TextField;
//import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

public class PatientController implements Initializable {
	@FXML
	private ComboBox<String> office_name, doc_name, reg_type, reg_name;
	@FXML
	private TextField balance;
	@FXML
	private Button btn_1, btn_2, btn_3;
	@FXML
	private TextField pay, payback;
	private boolean[] canshow = new boolean[4];

	public static String patNum;
	private Double restmoney = null;
	// Full 存放一次从数据库中读出的所有的数据，RAW 存放刷选过后的数据
	private ArrayList<String> officeFull = new ArrayList<String>();
	private ArrayList<String> docFull = new ArrayList<String>();
	private ArrayList<String> regnameFull = new ArrayList<String>();
	private ArrayList<String> officeRaw = new ArrayList<String>();
	private ArrayList<String> docRaw = new ArrayList<String>();
	private ArrayList<String> regnameRaw = new ArrayList<String>();
	private ResultSet officers, docrs, regrs;

	public static void setPatNum(String numString) {
		patNum = numString;
	}

	/**
	 * 显示警告信息
	 * 
	 * @param type
	 * @param title
	 * @param info
	 */
	private void popAlert(Alert.AlertType type, String title, String info) {
		Alert infoAlert = new Alert(type, info);
		infoAlert.setTitle("alert!");
		infoAlert.setHeaderText(title);
		infoAlert.showAndWait();
	}

	private void onComboboxChanged(ComboBox<String> comboboxinput, ArrayList<String> Fulllist,
			ArrayList<String> Rawlist, int index) {
		String input = comboboxinput.getEditor().getText();
		System.out.println("input:" + input);
		Iterator<String> it = Fulllist.iterator();
		Rawlist.clear();
		while (it.hasNext()) {
			String target = it.next();
			if (target.toUpperCase().indexOf(input.toUpperCase()) != -1) {
				Rawlist.add(target);
			}
		}
		comboboxinput.getItems().removeAll();
		comboboxinput.getItems().setAll(Rawlist);
		if (comboboxinput.getItems().size() > 0 && !input.isEmpty())
			canshow[index] = true;
		else
			canshow[index] = false;
	}

	/**
	 * 进行科室信息的筛选
	 */
	private void officeChs() {
		office_name.getItems().addAll(officeFull);
		office_name.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				office_name.getSelectionModel().select(0);
				doc_name.getEditor().setText("");
			}
		});
		office_name.getEditor().textProperty().addListener((obs, oldval, newval) -> {
			Platform.runLater(() -> {
				onComboboxChanged(office_name, officeFull, officeRaw, 0);
			});
		});
		office_name.getEditor().setOnKeyReleased(e -> {
			office_name.getItems().removeAll();
			office_name.getItems().setAll(officeRaw);
			doc_name.getEditor().setText("");
			if (office_name.getItems().size() == 0 || !canshow[0])
				office_name.hide();
			else {
				office_name.hide();
				office_name.show();
			}
		});
		// 依据科室名称，筛选医生名称
		office_name.getSelectionModel().selectedIndexProperty().addListener(e -> onDocSelect());
	}

	private void onDocSelect() {
		Iterator<String> it = officeRaw.iterator();
		if (office_name.getEditor().getText().isEmpty()) {
			return;// 如果没有输入科室，则不对医生进行筛选
		} else {
			docFull.clear();
			docRaw.clear();
		}
		while (it.hasNext()) {
			String office = getOfficeNum(it.next());
			System.out.println("office = " + office);
			String sql = "SELECT * FROM doctor_info where office_num ='" + office + "'";

			try {
				ResultSet rs = DBUtil.executeQuery(sql);// 查询科室
				while (rs.next()) {
					docFull.add(rs.getString("office_num") + " " + rs.getString("doc_name") + " "
							+ rs.getString("doc_pinyin"));
					docRaw.add(rs.getString("office_num") + " " + rs.getString("doc_name") + " "
							+ rs.getString("doc_pinyin"));
					System.out.println(rs.getString("doc_name"));
				}
				doc_name.getItems().removeAll();
				doc_name.getItems().setAll(docRaw);
			} catch (SQLException e) {
				popAlert(AlertType.ERROR, "错误", "数据库连接出错");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 取出第一个字段：科室编号
	 * 
	 * @param docinfo
	 * @return
	 */
	private String getOfficeNum(String docinfo) {
		if (docinfo == null || docinfo.isEmpty())
			return "";
		int i = docinfo.indexOf(' ');
		return docinfo.substring(0, i);
	}

	private void docChs() {
		doc_name.getItems().addAll(docFull);
		doc_name.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ENTER)
				doc_name.getSelectionModel().select(0);
		});
		doc_name.getEditor().textProperty().addListener((obs, oldval, newval) -> {
			Platform.runLater(() -> {
				// onDocSelect();
				onComboboxChanged(doc_name, docFull, docRaw, 1);
			});
		});
		doc_name.getEditor().setOnKeyReleased(e -> {
			doc_name.getItems().removeAll();
			doc_name.getItems().setAll(docRaw);
			if (doc_name.getItems().size() == 0 || !canshow[1])
				doc_name.hide();
			else {
				doc_name.hide();
				doc_name.show();
			}
		});
		doc_name.getSelectionModel().selectedIndexProperty().addListener(e -> onRegTypeSelect());// 监听医生名称，进行号种类型文本过滤
		doc_name.getSelectionModel().selectedIndexProperty().addListener(e -> onRegNameSelect());// 监听医生名称，进行号名类型文本过滤
	}

	private void onRegNameSelect() {
		// TODO Auto-generated method stub
		System.out.println("onRegNameSelect");
		Iterator<String> it = officeRaw.iterator();
		if (office_name.getEditor().getText().isEmpty()) {
			return;
		} else {
			regnameFull.clear();
			regnameRaw.clear();
		}
		while (it.hasNext()) {
			String officenum = getOfficeNum(it.next());
			int expert = 0;
			String sql = "SELECT * FROM registration_info where office_num ='" + officenum + "'";

			try {
				ResultSet rs = DBUtil.executeQuery(sql);
				while (rs.next()) {
					System.out.println();
					System.out.println("expert = " + expert);
					if (rs.getString("office_num").equals(officenum) && rs.getString("expert").equals("0")) {
						System.out.println("fuck!");
						regnameFull.add(rs.getString("reg_name") + " " + rs.getString("reg_pinyin"));
						regnameRaw.add(rs.getString("reg_name") + " " + rs.getString("reg_pinyin"));
					}
				}
				reg_name.getItems().removeAll();
				reg_name.getItems().setAll(regnameRaw);
				reg_type.getItems().removeAll();
				reg_type.getItems().setAll("普通号");
			} catch (SQLException e1) {
				// 数据库连接失败异常处理
				e1.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	private void onRegTypeSelect() {
		// TODO Auto-generated method stub

	}

	private void regChs() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		canshow[0] = canshow[1] = canshow[2] = canshow[3] = false;
		if (patNum == null)
			System.out.println("ERROR");
		try {
			office_name.setEditable(true);
			doc_name.setEditable(true);
			reg_type.setEditable(true);
			reg_name.setEditable(true);
			balance.setEditable(false);

			ResultSet rs = DBUtil.executeQuery("SELECT * FROM patient_info where Pat_num ='" + patNum + "'");
			rs.next();
			restmoney = rs.getDouble("prestore_amount");
			balance.setText(Double.toString(restmoney));
			// 科室、医生、号种
			// 科室数据读入内存
			officers = DBUtil.executeQuery("SELECT * FROM office_info");
			while (officers.next()) {
				officeFull.add(officers.getString("office_num") + " " + officers.getString("office_name") + " "
						+ officers.getString("office_pinyin"));
				officeRaw.add(officers.getString("office_num") + " " + officers.getString("office_name") + " "
						+ officers.getString("office_pinyin"));
			}
			// 医生数据读入内存
			docrs = DBUtil.executeQuery("SELECT * FROM doctor_info");
			while (docrs.next()) {
				docFull.add(docrs.getString("office_num") + " " + docrs.getString("doc_name") + " "
						+ docrs.getString("doc_pinyin"));
				docRaw.add(docrs.getString("office_num") + " " + docrs.getString("doc_name") + " "
						+ docrs.getString("doc_pinyin"));
			}
			// 号种信息读入内存
			regrs = DBUtil.executeQuery("SELECT * FROM registration_info");
			while (regrs.next()) {
				regnameFull.add(regrs.getString("reg_name") + " " + regrs.getString("reg_pinyin"));
				regnameRaw.add(regrs.getString("reg_name") + " " + regrs.getString("reg_pinyin"));
			}

			// 科室选择
			officeChs();
			// 医生选择
			docChs();
			// 号种选择
			regChs();

			// 确认按钮
			btn_1.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					if (pay.getText().isEmpty() || pay.getText().isEmpty()) {
						popAlert(AlertType.WARNING, "警告", "请将信息填写完整");
						return;
					} else if (payback.getText().equals("余额不足！")) {
						popAlert(AlertType.WARNING, "警告", "余额不足");
						return;
					}
					String docnum = getOfficeNum(doc_name.getEditor().getText());
					ResultSet docrs;

					String office_num = getOfficeNum(office_name.getEditor().getText());

					ResultSet guahaors = null, guahaors1 = null;
					Integer regtotal = 0;
					try {
						guahaors = DBUtil.executeQuery("SELECT * FROM register_info");
					} catch (SQLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					// 计算该号种挂号人数有多少个并且获取本次挂号编号
					try {
						while (guahaors.next()) {
							String string = guahaors.getString("pat_num").replaceAll("^(0+)", "");
							int b = 0;
							try {
								b = Integer.valueOf(string).intValue();
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
							if (regtotal < b)
								regtotal = b;// 获取挂号编号
						}
					} catch (SQLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					String limitString = null, reg_id = null, get_fee = null;
					// 获取当前挂号的号种的ID
					Iterator<String> it = officeRaw.iterator();
					if (office_name.getEditor().getText().isEmpty()) {
						return;
					}

					while (it.hasNext()) {
						String departmentID = getOfficeNum(it.next());
						String expert = "0";
						System.out.println(departmentID);
						String sql = "SELECT * FROM registration_info where office_num ='" + office_num + "'";

						ResultSet rs = null;
						try {
							rs = DBUtil.executeQuery(sql);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							while (rs.next()) {
								try {
									if (rs.getString("office_num").equals(departmentID)
											&& rs.getString("expert").equals(expert)) {
										reg_id = rs.getString("reg_num");
										get_fee = rs.getString("reg_cost");
										limitString = rs.getString("max_patient");
									}
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					int count = 0;
					try {
						guahaors1 = DBUtil.executeQuery("SELECT * FROM register_info");
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						while (guahaors1.next()) {
							if (reg_id.equals(guahaors1.getString("registration_num"))) {
								count = count + 1;
							}
						}
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					int guahaonum = count + 1;
					System.out.println(limitString);
					if (guahaonum > Integer.valueOf(limitString).intValue()) {
						popAlert(AlertType.WARNING, "警告", "挂号人数已满");
						return;
					}
					int bianhao = regtotal + 1;
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if (get_fee == null || get_fee.isEmpty())
						System.out.println("ERROR");
					String sqlString = "insert into register_info values ('" + bianhao + "','" + reg_id + "','" + docnum
							+ "','" + patNum + "','" + guahaonum + "', 1, '" + get_fee + "','" + df.format(new Date())
							+ "')";
					System.out.println(sqlString);
					try {
						DBUtil.execute(sqlString);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					JOptionPane.showMessageDialog(new JFrame().getContentPane(),
							"挂号成功！\n挂号编号为" + String.format("%06d", bianhao), "提示", JOptionPane.INFORMATION_MESSAGE);
					String moneString = payback.getText() + "0";
					String string = "update patient_info set prestore_amount = " + "'" + moneString
							+ "' where pat_num= '" + patNum + "'";
					try {
						DBUtil.execute(string);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					balance.setText(payback.getText());
				}
			});
		} catch (SQLException e1) {
			// 数据库连接失败异常处理
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}