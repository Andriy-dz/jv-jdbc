package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exeption.DataProcessingException;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {

    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        String insertManufactureRequest =
                "INSERT INTO manufacturers(name, country) value(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement createStatement =
                             connection.prepareStatement(insertManufactureRequest,
                                     Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, manufacturer.getName());
            createStatement.setString(2, manufacturer.getCountry());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufacturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create manufacture " + manufacturer, e);
        }
        return manufacturer;
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        Manufacturer manufacturer = new Manufacturer();
        String getManufactureById = "SELECT * FROM manufacturers WHERE id = ?"
                + " AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement =
                         connection.prepareStatement(getManufactureById)) {
            createStatement.setLong(1, id);
            ResultSet resultSet = createStatement.executeQuery();
            if (resultSet.next()) {
                manufacturer.setId(id);
                manufacturer.setCountry(resultSet.getString("country"));
                manufacturer.setName(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get manufacture by id" + id, e);
        }
        return Optional.ofNullable(manufacturer);
    }

    @Override
    public List<Manufacturer> getAll() {
        List<Manufacturer> allManufacture = new ArrayList<>();
        String getManufactureById = "SELECT * FROM manufacturers WHERE "
                + "is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement =
                         connection.prepareStatement(getManufactureById)) {
            ResultSet resultSet = createStatement.executeQuery();
            while (resultSet.next()) {
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setId(resultSet.getObject("id", Long.class));
                manufacturer.setCountry(resultSet.getString("country"));
                manufacturer.setName(resultSet.getString("name"));
                allManufacture.add(manufacturer);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all manufacture from DB", e);
        }
        return allManufacture;
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String updateManufacturer =
                "UPDATE manufacturers SET name = ?, country = ?"
                        + " WHERE id = ? && is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement
                         = connection.prepareStatement(updateManufacturer)) {
            createStatement.setString(1, manufacturer.getName());
            createStatement.setString(2, manufacturer.getCountry());
            createStatement.setLong(3, manufacturer.getId());
            createStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update manufacture " + manufacturer, e);
        }
        return manufacturer;
    }

    @Override
    public boolean delete(Long id) {
        String deleteManufacturerById =
                "UPDATE manufacturers SET is_deleted = true WHERE id = ? && is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createStatement =
                         connection.prepareStatement(deleteManufacturerById)) {
            createStatement.setLong(1, id);
            return createStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete by id " + id, e);
        }
    }
}