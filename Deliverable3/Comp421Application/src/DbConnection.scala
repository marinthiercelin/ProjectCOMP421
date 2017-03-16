
import java.sql.{DriverManager, SQLException, Connection, ResultSet, Statement}
/**
  * Created by tarek on 16/03/17.
  */
object DbConnection{

  //@throws(classOf[SQLException])
  def main(args: Array[String]){

    //Try to load the driver
    try{
      DriverManager.registerDriver(new org.postgresql.Driver())
    } catch {
      case ex : SQLException => println(ex.getMessage)
    }

    //Url for connection
    val url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421"

    //Establish Connection
    val con : Connection = DriverManager.getConnection(url, "cs421g21", "Grp42117;")

    val stat : Statement = con.createStatement()
    /*try{
      stat.executeUpdate("CREATE TABLE test123 (id INTEGER)")
    } catch {
      case ex : SQLException => print(ex.getMessage)
    }*/

    try {
      val res : ResultSet = stat.executeQuery("SELECT * FROM client WHERE cid = 1")
      while (res.next()){
        print(res.getString(2))
      }
    } catch {
      case ex : SQLException => println(ex.getMessage)
    }

    stat.close()
    con.close()



  }
}
