package model;

import database.Database;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Model-Klasse Bike
 */
public class Bike
{
	private final StringProperty rahmennr = new SimpleStringProperty();
	private final StringProperty markeType = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> verfuegbar = new SimpleObjectProperty<>();
	private final StringProperty text = new SimpleStringProperty();
	private final ObjectProperty<Farbe> farbe = new SimpleObjectProperty<>();
	private final ObjectProperty<BigDecimal> preis = new SimpleObjectProperty<>();

	/**
	 * Konstruktor aus Rahmennummer
	 *
	 * @param rahmennr Rahmennummer
	 */
	public Bike(String rahmennr)
	{
		setRahmennr(rahmennr);
	}

	/**
	 * Konstruktor mit allen Attributen.
	 *
	 * @param rahmennr   Rahmennummer
	 * @param markeType  Marke und Type
	 * @param verfuegbar Verfügbarkeitsdatum
	 * @param text       Kommentar
	 * @param farbe      Farbe
	 * @param preis      Preis
	 */
	public Bike(String rahmennr, String markeType, LocalDate verfuegbar, String text, Farbe farbe, BigDecimal preis)
	{
		setRahmennr(rahmennr);
		setMarkeType(markeType);
		setVerfuegbar(verfuegbar);
		setText(text);
		setFarbe(farbe);
		setPreis(preis);
	}

	/**
	 * Selektion eines Bike aus der Datenbank.
	 *
	 * @param rahmennr Rahmennummer
	 *
	 * @return Bike
	 *
	 * @throws SQLException
	 * @throws BikeException
	 */
	public static Bike select(String rahmennr) throws SQLException, BikeException
	{
		PreparedStatement pstmt = Database.getInstance().getPstmtSelect();
		pstmt.setString(1, rahmennr);
		ResultSet resultSet = pstmt.executeQuery();

		Bike bike;
		if (resultSet.next())
		{
			bike = new Bike(resultSet.getString("rahmennr"),
			                resultSet.getString("markeType"),
			                resultSet.getDate("verfuegbar").toLocalDate(),
			                resultSet.getString("text"),
			                Farbe.valueOf(resultSet.getString("farbe")),
			                resultSet.getBigDecimal("preis")
			);
		}
		else
		{
			bike = new Bike(rahmennr);
		}

		return bike;
	}

	public String getRahmennr()
	{
		return rahmennr.get();
	}

	public void setRahmennr(String rahmennr)
	{
		this.rahmennr.set(rahmennr);
	}

	public StringProperty rahmennrProperty()
	{
		return rahmennr;
	}

	public String getMarkeType()
	{
		return markeType.get();
	}

	public void setMarkeType(String markeType)
	{
		this.markeType.set(markeType);
	}

	public StringProperty markeTypeProperty()
	{
		return markeType;
	}

	public LocalDate getVerfuegbar()
	{
		return verfuegbar.get();
	}

	public void setVerfuegbar(LocalDate verfuegbar)
	{
		this.verfuegbar.set(verfuegbar);
	}

	public ObjectProperty<LocalDate> verfuegbarProperty()
	{
		return verfuegbar;
	}

	public String getText()
	{
		return text.get();
	}

	public void setText(String text)
	{
		this.text.set(text);
	}

	public StringProperty textProperty()
	{
		return text;
	}

	public Farbe getFarbe()
	{
		return farbe.get();
	}

	public void setFarbe(Farbe farbe)
	{
		this.farbe.set(farbe);
	}

	public ObjectProperty<Farbe> farbeProperty()
	{
		return farbe;
	}

	public BigDecimal getPreis()
	{
		return preis.get();
	}

	public void setPreis(BigDecimal preis)
	{
		this.preis.set(preis);
	}

	public ObjectProperty<BigDecimal> preisProperty()
	{
		return preis;
	}

	/**
	 * Defaulting und Überprüfung. Wird vor jedem Schreiben auf die Datenbank aufgerufen.
	 *
	 * @throws BikeException
	 */
	private void fillAndKill() throws BikeException
	{
		if (rahmennr.get() == null)
		{
			throw new BikeException("Rahmennummer muss angegeben werden!");
		}

		if (rahmennr.get().length() < 5)
		{
			throw new BikeException("Rahmennummer muss zumindest 5 Stellen haben!");
		}

		if (markeType.get() == null)
		{
			throw new BikeException("Marke und Type muss angegeben werden!");
		}

		if (markeType.get().length() < 3)
		{
			throw new BikeException("Marke und Type muss zumindest 3 Stellen haben!");
		}

		if (verfuegbar.get() == null)
		{
			throw new BikeException("Verfügbarkeitsdatum muss angegeben werden!");
		}

		if (verfuegbar.get() != null && (verfuegbar.get().getYear() < 2000 || verfuegbar.get().getYear() > 2030))
		{
			throw new BikeException("Verfügbarkeitsjahr muss zwischen 2000 und 2030 sein!");
		}

		if (text.get() == null || text.get().length() == 0)
		{
			throw new BikeException("Text muss angegeben werden!");
		}

		if (farbe.get() == null)
		{
			throw new BikeException("Farbe muss angegeben werden!");
		}

		if (preis.get() == null)
		{
			throw new BikeException("Preis muss angegeben werden!");
		}
	}

	/**
	 * Bike in Datenbank speichern.
	 * <p>
	 * Zunächst wird versucgt das Bike einzufügen. Wenn dies wegen einer Primärschlüssel-Violation schief geht, wird
	 * versucht es abzuändern. (insert-default-update)
	 * @throws BikeException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void save() throws BikeException, SQLException, IOException
	{
		fillAndKill();

		try
		{
			// Insert versuchen
			PreparedStatement pstmt = Database.getInstance().getPstmtInsert();
			pstmt.setString(1, getRahmennr());
			pstmt.setString(2, getMarkeType());
			pstmt.setDate(3, java.sql.Date.valueOf(getVerfuegbar()));
			pstmt.setString(4, getText());
			pstmt.setString(5, getFarbe().toString());
			pstmt.setBigDecimal(6, getPreis());
			pstmt.execute();
		}
		catch (SQLException e)
		{
			// Primary Key Violation
			if (e.getSQLState().equals("23505"))
			{
				// Update versuchen
				PreparedStatement pstmt = Database.getInstance().getPstmtUpdate();
				pstmt.setString(1, getMarkeType());
				pstmt.setDate(2, java.sql.Date.valueOf(getVerfuegbar()));
				pstmt.setString(3, getText());
				pstmt.setString(4, getFarbe().toString());
				pstmt.setBigDecimal(5, getPreis());
				pstmt.setString(6, getRahmennr());
				pstmt.execute();
			}
			else
			{
				throw e;
			}
		}
	}

	@Override
	public String toString()
	{
		return "Bike{" +
				"rahmennr=" + rahmennr +
				", markeType=" + markeType +
				", verfuegbar=" + verfuegbar +
				", text=" + text +
				", farbe=" + farbe +
				", preis=" + preis +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		Bike bike = (Bike) o;

		return getRahmennr().equals(bike.getRahmennr());
	}
}