package dao;

import model.Currency;
import utils.ConnectionToDb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDbStorage {

    public Optional<Currency> getCurrencyByCode(String code) throws SQLException {
        String sql = "SELECT * FROM currencies WHERE code = ? LIMIT 1";
        try (Connection connection = ConnectionToDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
             statement.setString(1, code);
             try (ResultSet resultSet = statement.executeQuery()) {
                 if (resultSet.next()){
                     Currency currency = getCurrencyFromResultSet(resultSet);
                     return Optional.of(currency);
                 } else {
                     return Optional.empty();
                 }
             }
        }
    }

    public Optional<Currency> getCurrencyById(int id) throws SQLException {
        String sql = "SELECT * FROM currencies WHERE id = ? LIMIT 1";
        try (Connection connection = ConnectionToDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()){
                    Currency currency = getCurrencyFromResultSet(resultSet);
                    return Optional.of(currency);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        String sql = "SELECT * FROM currencies";
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionToDb.getConnection();
             Statement statement = connection.createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()){
                    Currency currency = getCurrencyFromResultSet(resultSet);
                    currencies.add(currency);
                }
            }
        }
        return currencies;
    }

    public Optional<Currency> postCurrency(String code, String full_name, String sign) throws SQLException {
        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";
        int id;
        Currency currency = new Currency();
        try (Connection connection = ConnectionToDb.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, full_name);
            statement.setString(3, sign);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0){
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                        currency.setId(id);
                        currency.setCode(code);
                        currency.setFull_name(full_name);
                        currency.setSign(sign);
                        return Optional.of(currency);
                    }
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(currency);
    }

    private Currency getCurrencyFromResultSet(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setFull_name(resultSet.getString("full_name"));
        currency.setSign(resultSet.getString("sign"));
        return currency;
    }
}
