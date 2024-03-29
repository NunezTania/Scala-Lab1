package Data

import scala.collection.mutable
import scala.collection.concurrent.TrieMap

trait AccountService:
  /** Retrieve the balance of a given account
    * @param user
    *   the name of the user whose account will be retrieve
    * @return
    *   the current balance of the user
    */
  def getAccountBalance(user: String): Double

  /** Add an account to the existing accounts
    * @param user
    *   the name of the user
    * @param balance
    *   the initial balance value
    */
  def addAccount(user: String, balance: Double = 30): Unit

  /** Indicate is an account exist
    * @param user
    *   the name of the user whose account is checked to exist
    * @return
    *   whether the account exists or not
    */
  def isAccountExisting(user: String): Boolean

  /** Update an account by decreasing its balance.
    * @param user
    *   the name of the user whose account will be updated
    * @param amount
    *   the amount to decrease
    * @return
    *   the new balance
    */
  def purchase(user: String, amount: Double): Double

class AccountImpl extends AccountService:
  private val accounts = TrieMap[String, Double]()

  // TODO - Part 2 Step 2

  def getAccountBalance(user: String): Double =
    accounts.getOrElse(user, 0.0)

  def addAccount(user: String, balance: Double = 30): Unit =
    accounts.put(user, balance)

  def isAccountExisting(user : String) : Boolean = accounts.contains(user)

  def purchase(user: String, amount: Double): Double =
    if isAccountExisting(user) then
      accounts.update(user, accounts.getOrElse(user, 0.0) - amount)
      accounts.getOrElse(user, 0.0)
    else throw new Exception("Account does not exist")

end AccountImpl
