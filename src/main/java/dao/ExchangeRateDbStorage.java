package dao;

import model.ExchangeRate;
import utils.ConnectionPool;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDbStorage {
    private final CurrencyDbStorage currencyDbStorage = new CurrencyDbStorage();

    public List<ExchangeRate> getAllRates() throws SQLException {
        List<ExchangeRate> rates = new ArrayList<>();
        String sql = "SELECT * FROM exchange_rates";
        try (Connection connection = ConnectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                ExchangeRate rate = getRateFromResultSet(resultSet);
                rates.add(rate);
            }
        }
        return rates;
    }

    public Optional<ExchangeRate> postRate(String base, String target, BigDecimal rate) throws SQLException {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) " +
                "VALUES (?, ?, ?)";
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, currencyDbStorage.getCurrencyByCode(base).getId());
            statement.setInt(2, currencyDbStorage.getCurrencyByCode(target).getId());
            statement.setBigDecimal(3, rate);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return getRateById(generatedId);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Optional<ExchangeRate> patchRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException {
        String sqlPatch = "UPDATE exchange_rates " +
                "SET rate = ? " +
                "WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?) " +
                "AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)";
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlPatch)) {
            statement.setBigDecimal(1, rate);
            statement.setString(2, baseCode);
            statement.setString(3, targetCode);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }
            return getRateByCodes(baseCode, targetCode);
        }
    }

    public Optional<ExchangeRate> getRateByCodes(String baseCode, String targetCode) throws SQLException {
        String sql = "SELECT e.id, e.base_currency_id, e.target_currency_id, e.rate " +
                "FROM exchange_rates e " +
                "INNER JOIN currencies c on c.id = e.base_currency_id " +
                "INNER JOIN currencies c2 on c2.id = e.target_currency_id " +
                "WHERE c.code = ? AND c2.code = ?";
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    ExchangeRate exchangeRate = getRateFromResultSet(resultSet);
                    return Optional.of(exchangeRate);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    private Optional<ExchangeRate> getRateById(int id) throws SQLException {
        String sql = "SELECT * FROM exchange_rates WHERE id = ?";
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(getRateFromResultSet(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    private ExchangeRate getRateFromResultSet(ResultSet resultSet) throws SQLException {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(resultSet.getInt("id"));
        int baseCurrencyId = resultSet.getInt("base_currency_id");
        exchangeRate.setBaseCurrency(currencyDbStorage.getCurrencyById(baseCurrencyId).get());
        int targetCurrencyId = resultSet.getInt("target_currency_id");
        exchangeRate.setTargetCurrency(currencyDbStorage.getCurrencyById(targetCurrencyId).get());
        exchangeRate.setRate(resultSet.getBigDecimal("rate"));
        return exchangeRate;
    }
}
