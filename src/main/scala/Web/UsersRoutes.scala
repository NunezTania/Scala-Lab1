package Web

import Data.{AccountService, SessionService, Session}
import Web.Layouts.homePage
import Web.Decorators.getSession

/** Assembles the routes dealing with the users:
  *   - One route to display the login form and register form page
  *   - One route to process the login form and display the login success page
  *   - One route to process the register form and display the register success
  *     page
  *   - One route to logout and display the logout success page
  *
  * The username of the current session user is stored inside a cookie called
  * `username`.
  */
class UsersRoutes(accountSvc: AccountService, sessionSvc: SessionService)(
    implicit val log: cask.Logger
) extends cask.Routes:

  @cask.get("/login")
  def login() = {
    Layouts.loginPage(None)
  }

  @getSession(sessionSvc)
  @cask.postForm("/login")
  def postLogin(username: String)(session: Session) = {
    accountSvc.isAccountExisting(username) match {
      case true => {
        session.setCurrentUser(username)
        Layouts.successfulLoginPage(username)
      }
      case false =>
        Layouts.loginPage(error =
          Some(0, "The specified username does not exist !")
        )
    }
  }


  @getSession(sessionSvc)
  @cask.postForm("/register")
  def postRegister(username: String)(session: Session) = {
    accountSvc.isAccountExisting(username) || username.isEmpty() 
    || username.trim().isEmpty() || username == "bot" match {
      case true =>
        Layouts.loginPage(error =
          Some(1, "The specified username isn't valid !")
        )
      case false => {
        accountSvc.addAccount(username)
        session.setCurrentUser(username)
        Layouts.successfulRegisterPage(username)
      }
    }
  }

  @getSession(sessionSvc)
  @cask.get("/logout")
  def logout()(session: Session) = {
    session.reset()
    Layouts.logoutPage()
  }

  initialize()
end UsersRoutes
