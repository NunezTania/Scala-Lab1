package Chat

/** This sealed trait represents a node of the tree.
  */
sealed trait ExprTree

/** Declarations of the nodes' types.
  */
object ExprTree:
  // TODO - Part 2 Step 3

  // Leaf nodes
  // case object Thirsty extends ExprTree
  // case object Hungry extends ExprTree
  /*
  case class Number(value : Int) extends ExprTree
  case class Identifier(pseudo : String) extends ExprTree

  case object Pseudo extends ExprTree
  case object ProductType extends ExprTree
  case object ProductBrand extends ExprTree



  // Tree nodes
  case object Command extends ExprTree
  case object Balance extends ExprTree
  case object Mood extends ExprTree
  case object Price extends ExprTree

  case class Product(brand: ExprTree, kind : ExprTree) extends ExprTree

  case class And(left: ExprTree, right: ExprTree) extends ExprTree
  case class Or(left: ExprTree, right: ExprTree) extends ExprTree

   */
  case class Pseudo(name: String) extends ExprTree
  case class Number(value: Int) extends ExprTree

  sealed trait ProductType extends ExprTree
  case object Croissant extends ProductType
  case object Biere extends ProductType

  sealed trait Brand extends ExprTree
  case object Maison extends Brand
  case object Cailler extends Brand
  case object Farmer extends Brand
  case object Boxer extends Brand
  case object Wittekop extends Brand
  case object PunkIPA extends Brand
  case object JackHammer extends Brand
  case object Tenebreuse extends Brand

  sealed trait Politeness extends ExprTree
  case class Want(politeness: String) extends Politeness

  sealed trait StateOfMind extends ExprTree
  case object Thirsty extends StateOfMind
  case object Hungry extends StateOfMind

  case class Identification(name: Pseudo) extends ExprTree

  case class Product(
      quantity: Number,
      productType: ProductType,
      brand: Brand
  ) extends ExprTree

  case class And(left: ExprTree, right: ExprTree) extends ExprTree
  case class Or(left: ExprTree, right: ExprTree) extends ExprTree

  case class Command(politeness: Politeness, products: Seq[Product])
      extends ExprTree

  case object CheckBalance extends ExprTree

  case class Price(products: Seq[Product]) extends ExprTree
