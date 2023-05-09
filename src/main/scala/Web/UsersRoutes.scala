package Web

import Data.{AccountService, SessionService, Session}
import Web.Layouts.homePage

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
  @cask.get("/login")
  def index() =
    Layouts.loginPage()
  // TODO - Part 3 Step 3b: Process the login information sent by the form with POST to `/login`,
  //      set the user in the provided session (if the user exists) and display a successful or
  //      failed login page.

  @cask.postForm("/login")
  def postLogin(username: String) = {
    accountSvc.isAccountExisting(username) match {
      case true => {
        sessionSvc.get(username)
        // Layouts.SuccessLoginPage()
      }
      case false =>
      // Layouts.loginPage(error = Some("The specied username does not exist !"))
      /*
        cask.Response(
          s"Login failed !"
        )
       */
    }
  }
  //
  // TODO - Part 3 Step 3c: Process the register information sent by the form with POST to `/register`,
  //      create the user, set the user in the provided session and display a successful
  //      register page.
  //
  // TODO - Part 3 Step 3d: Reset the current session and display a successful logout page.

  initialize()
end UsersRoutes
