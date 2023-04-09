package Chat
import Data.{AccountService, ProductService, Session}
import Chat.ExprTree.Order

class AnalyzerService(productSvc: ProductService, accountSvc: AccountService):
  import ExprTree._

  /** Compute the price of the current node, then returns it. If the node is not
    * a computational node, the method returns 0.0. For example if we had a "+"
    * node, we would add the values of its two children, then return the result.
    * @return
    *   the result of the computation
    */
  // TODO - Part 2 Step 3
  def computePrice(t: ExprTree): Double =
    t match
      case And(left, right) => computePrice(left) + computePrice(right)
      case Or(left, right) =>
        if computePrice(left) < computePrice(right) then computePrice(left)
        else computePrice(right)
      case Product(quantity, productType, brand) =>
        productSvc.getPrice(
          productType.toString,
          brand.toString
        ) * quantity
      case Order(products) => computePrice(products)
      case Price(products) => computePrice(products)
      case _               => 0.0

  /** Return the output text of the current node, in order to write it in
    * console.
    * @return
    *   the output text of the current node
    */
  def reply(session: Session)(t: ExprTree): String =
    val inner: ExprTree => String = reply(session)
    t match
      // TODO - Part 2 Step 3
      case Thirsty =>
        "Eh bien, la chance est de votre cote, car nous offrons les meilleures bieres de la region !"
      case Hungry =>
        "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
      case Pseudo(name) =>
        session.setCurrentUser(name)
        s"Bonjour, ${name.toLowerCase().tail} !"

      case Product(quantity, productType, brand) =>
        val price = computePrice(t)
        s" $quantity $productType $brand"

      case And(left, right) => inner(left) + " et " + inner(right)

      case Or(left, right) =>
        if (computePrice(left) > computePrice(right)) then inner(left)
        else inner(right)

      case Order(products) =>
        if session.getCurrentUser.isEmpty then
          "Veuillez d'abord vous identifier !"
        else
          val price = computePrice(t)
          val currentUser = session.getCurrentUser.get

          if price > accountSvc.getAccountBalance(currentUser) then
            "Vous n'avez pas assez d'argent !"
          else
            s"Vous avez commande ${inner(products)} pour un total de $price CHF. Votre nouveau solde est de ${accountSvc
                .purchase(currentUser, price)} CHF."

      case CheckBalance =>
        session.getCurrentUser match
          case Some(user) =>
            s"Votre solde est de ${accountSvc.getAccountBalance(user)} CHF."
          case None => "Vous n'etes pas connecte !"

      case Price(products) =>
        "Cela coute " + computePrice(t) + " CHF."

end AnalyzerService
