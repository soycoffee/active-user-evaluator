import play.api.inject.Binding
import play.api.{Configuration, Environment}

class Module extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Nil

}
