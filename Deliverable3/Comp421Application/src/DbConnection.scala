
import java.sql._
import scala.io.StdIn._
/**
  * Created by tarek on 16/03/17.
  */
object DbConnection extends App{
  main()

  def main(){

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
    createPayment(con, 1, 1, 1)

    /*val stat : Statement = con.createStatement()
    /*try{
      stat.executeUpdate("CREATE TABLE test123 (id INTEGER)")
    } catch {
      case ex : SQLException => print(ex.getMessage)
    }*/

    /*try {
      val res : ResultSet = stat.executeQuery("SELECT * FROM client WHERE cid = 1")

      while (res.next()){
        print(res.getString(2))
      }
    } catch {
      case ex : SQLException => println(ex.getMessage)
    }

    stat.close()
    con.close()*/*/
  }

  def displayPaymentSet(resultSet: ResultSet, clientId : Int): Unit ={
    val rsmd = resultSet.getMetaData()
    val numCol = rsmd.getColumnCount()

    println("Sorted payments for client " + clientId)


    while (resultSet.next()){
      for (i <- 1 to numCol){

        i match {
          case 1 => print("Payment id : "+resultSet.getInt(i) + " ")
          case 2 => print("Discount : "+resultSet.getInt(i) + "% ")
          case 3 => print("Payment timestamp : " +resultSet.getDate(i).toString + " ")
          case 4 => print("Payment method : " + resultSet.getString(i) + " ")
          case 5 => print("Total Amount : " +resultSet.getFloat(i) + " ")
          case 6 => print("Employee ID : "+ +resultSet.getInt(i) + " ")
          case 7 =>
        }
      }
      println()

    }
  }

  //Returns a result set
  def getPaymentsofClient(clientId : Int, connection: Connection) : Unit ={
    val stat = connection.createStatement()
    val sqlStatement = "SELECT * FROM payment WHERE cid = "+clientId + " ORDER BY pydate"
    try {
      val resultSet = stat.executeQuery(sqlStatement)
      displayPaymentSet(resultSet, clientId)
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }

    stat.close()
  }


  def displayAverageRankings(resultSet: ResultSet, branchid : Int): Unit ={
    val rsmd = resultSet.getMetaData()
    val numCol = rsmd.getColumnCount()

    println("Sorted employee averages for branch " + branchid + "\n")

    while (resultSet.next()){
      for (i<- 1 to numCol){
        i match {
          case 1 => println("Employee id : " + resultSet.getInt(i) +" ")
          case 2 => println("Employee name : " + resultSet.getString(i) + " ")
          case 3 => println("Average rating : " + resultSet.getFloat(i) + " ")
        }
      }
      println()
    }

  }

  def getAverageRankingOfEmployees(branchid : Int, connection: Connection) : Unit = {
    val statement = connection.createStatement()
    val sqlQuery = "SELECT E.eid, E.ename,  A.rating FROM employee E, (" +
      "SELECT eid, AVG(rating) AS rating " +
      "FROM rates " +
      "GROUP By eid) AS A " +
      "WHERE E.eid = A.eid AND E.bid = " + branchid + " ORDER BY A.rating DESC"

    try {
      val resultSet = statement.executeQuery(sqlQuery)
      displayAverageRankings(resultSet, branchid)
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    statement.close()
  }

  def getNextCid(statement: Statement): Int = {
    var cid = 1
    try {
      val resultSet = statement.executeQuery("SELECT MAX(cid) FROM client")
      resultSet.next()
      cid = resultSet.getInt(1) + 1
    } catch {
      case ex: SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    cid
  }

  def getClientIdFromName(cname : String, connection: Connection): Int ={
    val statement = connection.createStatement()

    val sqlQuery = "SELECT cid FROM client WHERE cname = \'" + cname + "\'"

    var cid = 0
    try {
      val resultSet = statement.executeQuery(sqlQuery)
      if (resultSet.next()) {
        resultSet.getInt(1)
      } else {
        throw new SQLException("Client ID not found.")
      }

    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }

    0


  }

  //Creates a new Client
  def newClient(cName : String, streetNum : Int, street: String, city: String, country : String, connection: Connection): Unit ={
    val statement = connection.createStatement()


    val cid: Int = getNextCid(statement)

    val sqlQuery = "INSERT INTO client VALUES("+ cid + ",\'" + cName + "\',\'" + streetNum + "\',\'" + street + "\',\'" + city + "\',\'"+ country + "\')"

    try {
      statement.executeUpdate(sqlQuery)
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }

    println("Client created successfully.")
    statement.close()
  }

  def getNextPaymentId(statement: Statement): Int ={
    var pyid = 1
    try {
      val resultSet = statement.executeQuery("SELECT MAX(cid) FROM payment")
      resultSet.next()
      pyid = resultSet.getInt(1) + 1
    } catch {
      case ex: SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    pyid
  }


  def createPayment(connection: Connection, eid : Int, bid : Int, cid : Int): Unit ={
    val statement = connection.createStatement()

    print("Enter the payment method (cash, debit or credit)")
    val mthd = readLine()

    val pyid = getNextPaymentId(statement)

    var choice = 0

    var amount = 0
    var statements : List[String] = List()

    while (choice != 3) {
      println("What would you like to do now ? : \n 1.Add a for sale product \n 2.Add a for rent product \n 3.Finish payment")
      print("Your choice : ")
      choice = readInt()
      choice match {
        case 1 => {(amount, statements) = addForSaleProduct(bid, statement, pyid, statements, amount)}
        case 2 => amount += addForRentProduct(bid)
        case 3 =>
        case _ => println("Please enter a valid option.")
      }
    }
    statement.close()
  }

  def displayAvailableForSaleProd(statement: Statement, bid : Int): List[(Int, Int)] ={


    val query = "SELECT * FROM product P, forsale F WHERE P.prid = F.prid AND P.available AND P.bid=" + bid
    var productIds : List[(Int, Int)] = List()

    println("List of available forsale products in branch :\n")
    try {
      val resultSet = statement.executeQuery(query)

      while (resultSet.next()){
          productIds = (resultSet.getInt(1), resultSet.getInt(4)) :: productIds
          for (i <- 1 to 9){
            i match {
              case 1 => print("ID : " + resultSet.getInt(i) + " ")
              case 2 => print("Brand : " + resultSet.getString(i) + " ")
              case 4 => print("Type : " + resultSet.getString(i) + "$ ")
              case 9 => print("Price : " + resultSet.getInt(i))
              case _ =>
            }
          }
        println()
      }
    } catch {
      case ex : SQLException => println("Code : " + ex.getErrorCode + " Message : " + ex.getMessage())
    }
    statement.close()
    productIds
  }

  def addForSaleProduct(bid : Int,statement: Statement, pyid : Int, statements : List[String], amount : Int): (Float,List[String]) ={
    println("Select a product ID from the following list")
    val availableIds = displayAvailableForSaleProd(statement, bid)

    print("Product ID : ")

    var productId = readInt()

    while(!availableIds.contains((productId,_))){
      println("Please select a product ID from the list : ")
      productId = readInt()
    }

    val price = availableIds.find(_._1==productId).get._2

    (price, "INSERT INTO buys VALUES("+productId + ',' + pyid +")" :: statements)





  }

  def addForRentProduct(bid : Int) : Float = {

  }

}
