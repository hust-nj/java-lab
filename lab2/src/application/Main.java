package application;
	
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	public static Stage primarystage = null;
	public static Scene login = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,400,400);
//			scene.getStylesheets().add(getClass().getResource("application/MyScene.fxml").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
			primarystage = primaryStage;
			login = new Scene(FXMLLoader.load(getClass().getResource("/application/MyScene.fxml")));
			primaryStage.setScene(login);
			primaryStage.setTitle("挂号登陆");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showstage(){
		start(new Stage());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
