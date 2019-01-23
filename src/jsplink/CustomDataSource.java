package jsplink;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class CustomDataSource {
   // JDBC driver name and database URL
   private static DataSource datasource;
   private static final BasicDataSource basicDataSource;

   static {
      basicDataSource = new BasicDataSource();
      basicDataSource.setDriverClassName(JDBC_DRIVER);
      basicDataSource.setUsername(USER);
      basicDataSource.setPassword(PASS);
      basicDataSource.setUrl(DB_URL);
   }

   public static DataSource getInstance() {
      return basicDataSource;
   }
}
