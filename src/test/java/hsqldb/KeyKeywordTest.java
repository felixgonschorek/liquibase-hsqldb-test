package hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

class KeyKeywordTest {

    @BeforeAll
    static void setupSpec() throws ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
    }

    @Test
    void testFailsForMeWithMessageUnexpectedToken() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb1;sql.syntax_mys=true")) {
            Database database = new HsqlDatabase();
            runLiquibaseUpdate(connection, database);
        }
    }

    private void runLiquibaseUpdate(Connection connection, Database database) throws LiquibaseException, Exception {
        database.setConnection(new JdbcConnection(connection));

        try (ClassLoaderResourceAccessor classLoader = new ClassLoaderResourceAccessor();
             Liquibase liquibase = new liquibase.Liquibase("hsqldb-test.xml", classLoader, database)) {
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }

    @Test
    @Disabled
    void testWorksMeWithKeywordPatch() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb2;sql.syntax_mys=true")) {
            Database database = new HsqlDatabase() {

                @Override
                public boolean isReservedWord(String value) {
                    return super.isReservedWord(value) || "KEY".equals(value.toUpperCase());
                }
            };
            runLiquibaseUpdate(connection, database);
        }
    }

}
