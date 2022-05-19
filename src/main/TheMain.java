package main;

import controllerview.BikeC;
import database.Database;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * @author David Wandratsch, 4BHIT
 * @date 18.05.2022
 * <a href="https://github.com/WandratschDavid/4406_nullableBikeProperties">Github Link zum Repository</a>
 */
public class TheMain extends Application
{
	@Override
	public void init() throws SQLException
	{
		Database.open();
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		BikeC.show(primaryStage);
	}

	@Override
	public void stop()
	{
		Database.close();
	}
}