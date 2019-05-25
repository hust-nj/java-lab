package application;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML
	private Button LoginButton, ClearButton;
	@FXML
	private ComboBox<String> SelComboBox;
	@FXML
	private TextField UserNameText;
	@FXML
	private PasswordField PassWordText;
	private boolean type;

	/*
	 * 初始化登陆界面
	 */
	@Override
	public void initialize(URL locUrl, ResourceBundle resource) {
		// 两种模式
		SelComboBox.getItems().addAll("病人", "医生");
		//
		SelComboBox.getSelectionModel().select(0);

		SelComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (type) {
					type = false;
//					System.out.println(type);
				} else {
					type = true;
//					System.out.println(type);
				}
			}
		});
	}

	/**
	 * 显示警告信息
	 */
	private void popAlert(Alert.AlertType type, String title, String info) {
		Alert infoAlert = new Alert(type, info);
		infoAlert.setTitle("alert!");
		infoAlert.setHeaderText(title);
		infoAlert.showAndWait();
//		});
	}

	/**
	 * 按下登陆按钮，连接数据库，进行用户名密码检验与登陆
	 */
	@SuppressWarnings("resource")
	@FXML
	private void loginAct(ActionEvent event) {
		String unString = UserNameText.getText();
		String pwstring = PassWordText.getText();
		if (unString.length() == 0) {
			popAlert(AlertType.INFORMATION, "提示", "用户名不为空");
			return;
		} else if (pwstring.length() == 0) {
			popAlert(AlertType.INFORMATION, "提示", "密码不能为空");
			return;
		}
		ResultSet rs = null;
		try {
			DBUtil.connect();
			if (type) {
				System.out.println("登陆医生数据库");
				rs = DBUtil.executeQuery("select login_cmd from doctor_info where doc_num=" + unString);
			} else {
				System.out.println("登陆病人数据库");
				rs = DBUtil.executeQuery("select login_cmd from patient_info where pat_num=" + unString);
			}
			if (rs.next() && (rs.getString("login_cmd").equals(pwstring))) {
				System.out.println("登陆成功");
				if (type) {
					DBUtil.execute("update doctor_info set last_login=NOW() where doc_num=" + unString);
				} else {
					DBUtil.execute("update patient_info set last_login=NOW() where pat_num=" + unString);
				}
			} else {
				System.out.println("登陆失败");
				popAlert(AlertType.INFORMATION, "提示", "用户名或密码错误");
				return;
			}

			if (type) {
				DoctorController.setDocNum(unString);
				Scene docScene = new Scene(FXMLLoader.load(getClass().getResource("/application/DoctorScene.fxml")));
				Main.primarystage.setScene(docScene);
				Main.primarystage.setTitle("挂号医生管理系统");
			} else {
				PatientController.setPatNum(unString);
				Scene patientScene = new Scene(
						FXMLLoader.load(getClass().getResource("/application/PatientScene.fxml")));
				Main.primarystage.setScene(patientScene);
				Main.primarystage.setTitle("挂号病人管理系统");
			}
		} catch (SQLException e) {
			popAlert(AlertType.ERROR, "错误", "数据库连接错误");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return;
	}

	/**
	 * 清空输入
	 * 
	 * @param event
	 */
	@FXML
	private void clearAct(ActionEvent event) {
		UserNameText.setText("");
		PassWordText.setText("");
	}
}