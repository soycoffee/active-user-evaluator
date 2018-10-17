import play.api.inject.Binding
import play.api.{Configuration, Environment, Mode}
import services.ApiDefinitionInitializer

class Module extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      if (environment.mode == Mode.Dev)
        bind[ApiDefinitionInitializer].to[ApiDefinitionInitializer.Dev]
      else
        bind[ApiDefinitionInitializer].to(ApiDefinitionInitializer.Empty),
    )
  }

}
