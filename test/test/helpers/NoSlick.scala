package test.helpers

import org.scalatestplus.play.guice.GuiceFakeApplicationFactory
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import slick.basic.BasicProfile

trait NoSlick extends GuiceFakeApplicationFactory {

  val nullDatabaseConfigProvider: DatabaseConfigProvider = new DatabaseConfigProvider {

    override def get[P <: BasicProfile]: Null = null

  }

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      overrides = Seq(
        bind[DatabaseConfigProvider].toInstance(nullDatabaseConfigProvider),
      ),
    )
      .build()

}
