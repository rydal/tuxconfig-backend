package jsplink;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class CustomDataSource {
   // JDBC driver name and database URL

   static final String DB_URL = "jdbc:mysql://localhost/linuxconf";
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  

   //  Database credentials
   static final String USER = "arwen";
   static final String PASS = "imleaving";
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
