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
  // TODO - Part 3 Step 3a: Display a login form and register form page for the following URL: `/login`.

  @getSession(sessionSvc)
  @cask.get("/login")
  def login()(session: Session) = {
    Layouts.loginPage(None)
  }

  // TODO - Part 3 Step 3b: Process the login information sent by the form with POST to `/login`,
  //      set the user in the provided session (if the user exists) and display a successful or
  //      failed login page.
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

  // TODO - Part 3 Step 3c: Process the register information sent by the form with POST to `/register`,
  //      create the user, set the user in the provided session and display a successful
  //      register page.
  @getSession(sessionSvc)
  @cask.postForm("/register")
  def postRegister(username: String)(session: Session) = {
    accountSvc.isAccountExisting(username) || username.isEmpty() match {
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

  // TODO - Part 3 Step 3d: Reset the current session and display a successful logout page.
  @getSession(sessionSvc)
  @cask.get("/logout")
  def logout()(session: Session) = {
    session.reset()
    Layouts.logoutPage()
  }

  initialize()
end UsersRoutes
