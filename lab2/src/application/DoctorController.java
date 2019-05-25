package application;

import java.awt.print.Printable;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DoctorController implements Initializable {
	@FXML
	private DatePicker dateFrom, dateTo;
	@FXML
	private ComboBox<String> hourFrom, hourTo, minFrom, minTo;
	@FXML
	private TabPane docTabPane;
	@FXML
	private TableView<PatTableRow> PatTable;
	@FXML
	private TableView<IncomeTableRow> IncomeTable;
	@FXML
	private Button logoffButton, refreshButton, fiterButton;
	@FXML
	private Label fromdateLabel, fromhourLabel, fromminLabel, todateLabel, tohourLabel, tominLabel, lineLabel;
	@FXML
	private TableColumn<PatTableRow, String> PatTableRegNum, PatTablePatName;
	@FXML
	private TableColumn<PatTableRow, String> PatTableRegDate, PatTableRegType;
	@FXML
	private TableColumn<IncomeTableRow, String> IncTableOffName, IncTableDocNum;
	@FXML
	private TableColumn<IncomeTableRow, String> IncTableDocName, IncTableRegType;
	@FXML
	private TableColumn<IncomeTableRow, String> IncTablePatNum, IncTableIncome;

	private static String docName;
	private static ResultSet rs;

	private static String docNum;
	private boolean table = false;

	public static void setDocNum(String numString) {
		docNum = numString;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			rs = DBUtil.executeQuery("select doc_name from doctor_info where doc_num='" + docNum + "'");
			if (rs.next())
				docName = rs.getString("doc_name");
			else
				throw new SQLException();

			System.out.print(docName);
			initTime();
			hourFrom.getSelectionModel().select(0);
			minFrom.getSelectionModel().select(0);
			hourTo.getSelectionModel().select(0);
			minTo.getSelectionModel().select(0);
			setVisible(false);

			docTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
				@Override
				public void changed(ObservableValue<? extends Tab> observable, Tab t1, Tab t2) {
					if (table == false) {
						table = true;
						setVisible(true);
					} else {
						table = false;
						setVisible(false);
					}
				}
			});

			initPatientTable();
			loadPatientTable(PatTable);
			initIncomeTable();
			loadIncomeTable(IncomeTable);
		} catch (SQLException e) {
			popAlert(AlertType.ERROR, "错误", "数据库连接出错！");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("error!!!!!!!!!!!");
		}
	}

	private void initPatientTable() {
		PatTableRegNum.setCellValueFactory(new PropertyValueFactory<PatTableRow, String>("register_num"));
		PatTablePatName.setCellValueFactory(new PropertyValueFactory<PatTableRow, String>("pat_name"));
		PatTableRegDate.setCellValueFactory(new PropertyValueFactory<PatTableRow, String>("reg_datetime"));
		PatTableRegType.setCellValueFactory(new PropertyValueFactory<PatTableRow, String>("expert"));
	}

	private void initIncomeTable() {
		IncTableOffName.setCellValueFactory(new PropertyValueFactory<IncomeTableRow, String>("office_name"));
		IncTableDocNum.setCellValueFactory(new PropertyValueFactory<IncomeTableRow, String>("doc_num"));
		IncTableDocName.setCellValueFactory(new PropertyValueFactory<IncomeTableRow, String>("doc_name"));
		IncTableRegType.setCellValueFactory(new PropertyValueFactory<IncomeTableRow, String>("expert"));
		IncTablePatNum.setCellValueFactory(new PropertyValueFactory<IncomeTableRow, String>("allpatcount"));
		IncTableIncome.setCellValueFactory(new PropertyValueFactory<IncomeTableRow, String>("allincome"));
	}

	private void loadPatientTable(TableView<PatTableRow> PatTable) {
		ObservableList<PatTableRow> data = FXCollections.observableArrayList();
		try {
			rs = DBUtil.executeQuery("SELECT register_num,	pat_name, reg_datetime, expert "
					+ "FROM register_info A, patient_info B, registration_info C " + "WHERE doc_num = " + docNum
					+ " AND unreg = '0' AND A.pat_num = B.pat_num AND C.reg_num = A.registration_num");
			System.out.println("docnum = " + docNum);
			while (rs.next()) {
				System.out.println(rs.getString("register_num") + rs.getString("pat_name")
						+ rs.getString("reg_datetime") + (rs.getString("expert").equals("1") ? "专家号" : "普通号"));
				data.add(new PatTableRow(rs.getString("register_num"), rs.getString("pat_name"),
						rs.getString("reg_datetime"), rs.getString("expert").equals("1") ? "专家号" : "普通号"));
			}
			PatTable.setItems(data);

		} catch (SQLException e) {
			e.printStackTrace();
			popAlert(AlertType.ERROR, "错误", "从数据库获得表格数据失败");
			return;
		}
	}

	private void loadIncomeTable(TableView<IncomeTableRow> IncTable) {
		ObservableList<IncomeTableRow> data = FXCollections.observableArrayList();
		try {
			String from = dateFrom.getValue().toString() + " " + hourFrom.getValue() + ":" + minFrom.getValue() + ":00";
			String to = dateTo.getValue().toString() + " " + hourTo.getValue() + ":" + minTo.getValue() + ":00";
			System.out.println(from + "----" + to);
			rs = DBUtil.executeQuery(
					"SELECT office_name AS office_name, C.doc_num AS doc_num, doc_name AS doc_name, B.expert AS expert, SUM(1-unreg) AS allpatcount, SUM((1-unreg) * B.reg_cost) AS allincome\r\n"
							+ "FROM register_info A, registration_info B, doctor_info C, office_info D\r\n"
							+ "WHERE A.registration_num = B.reg_num AND C.doc_num = " + docNum
							+ " AND A.doc_num = C.doc_num AND D.office_num = C.office_num AND reg_datetime BETWEEN '"
							+ from + "' AND '" + to + "'\r\n" + "GROUP BY C.doc_num, B.expert");
			while (rs.next()) {
				data.add(new IncomeTableRow(rs.getString("office_name"), rs.getString("doc_num"),
						rs.getString("doc_name"), rs.getString("expert").equals("1") ? "专家号" : "普通号",
						rs.getString("allpatcount"), rs.getString("allincome")));
			}

			IncTable.setItems(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置切换时间标签
	 * 
	 * @param b
	 */
	private void setVisible(boolean b) {
		hourFrom.setVisible(b);
		minFrom.setVisible(b);
		hourTo.setVisible(b);
		minTo.setVisible(b);
		dateFrom.setVisible(b);
		dateTo.setVisible(b);
		fromdateLabel.setVisible(b);
		fromhourLabel.setVisible(b);
		fromminLabel.setVisible(b);
		todateLabel.setVisible(b);
		tohourLabel.setVisible(b);
		tominLabel.setVisible(b);
		lineLabel.setVisible(b);
	}

	private void initTime() {
		dateFrom.setValue(LocalDate.now());
		dateTo.setValue(dateFrom.getValue().plusDays(1));
		for (int i = 0; i < 24; ++i) {
			hourFrom.getItems().add(((i < 10) ? "0" : "") + i);
			hourTo.getItems().add(((i < 10) ? "0" : "") + i);
		}
		hourFrom.getSelectionModel().select(0);
		hourTo.getSelectionModel().select(0);
		for (int i = 0; i < 60; ++i) {
			minFrom.getItems().add(((i < 10) ? "0" : "") + i);
			minTo.getItems().add(((i < 10) ? "0" : "") + i);
		}
		minFrom.getSelectionModel().select(0);
		minTo.getSelectionModel().select(0);
	}

	@FXML
	private void refreshAction(ActionEvent event) {
		if (table) {
			loadIncomeTable(IncomeTable);
		} else {
			loadPatientTable(PatTable);
		}
	}

	@FXML
	private void logoffAction(ActionEvent event) {

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

}