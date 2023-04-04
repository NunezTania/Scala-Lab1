package Chat
import Data.{AccountService, ProductService, Session}
import Chat.ExprTree.Command

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
      case Product(quantity, productType, brand) =>
        productSvc.getPrice(
          productType.toString,
          brand.toString
        ) * quantity.value
      case Command(politeness, products) => products.map(computePrice).sum
      case Price(products)               => products.map(computePrice).sum
      case _                             => 0.0

  /** Return the output text of the current node, in order to write it in
    * console.
    * @return
    *   the output text of the current node
    */
  def reply(session: Session)(t: ExprTree): String =
    // you can use this to avoid having to pass the session when doing recursion
    val inner: ExprTree => String = reply(session)
    t match
      // TODO - Part 2 Step 3
      // Example cases
      case Thirsty =>
        "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
      case Hungry =>
        "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
      case Identification(pseudo) =>
        s"Bonjour ${pseudo.name.toLowerCase().tail} !"

      case And(left, right) => inner(left) + " " + inner(right)

      case Or(left, right) =>
        if (computePrice(left) > computePrice(right)) inner(left)
        else inner(right)

      case Command(politeness, products) =>
        val price = computePrice(t)
        val productText = products.map(inner).mkString(", ")
        s"Vous avez commandé $productText pour un total de $price CHF."

      case CheckBalance =>
        session.getCurrentUser match
          case Some(user) =>
            s"Votre solde est de ${accountSvc.getAccountBalance(user)} CHF."
          case None => "Vous n'êtes pas connecté !"

      case Price(products) =>
        "Le prix total est de " + computePrice(t) + " CHF."

      case _ => ""

end AnalyzerService
